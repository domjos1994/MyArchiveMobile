/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivemobile.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.fragment.app.DialogFragment;

import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchiveservices.customTasks.CustomAbstractTask;
import de.domjos.myarchiveservices.services.AudioDBWebservice;
import de.domjos.myarchiveservices.services.GoogleBooksWebservice;
import de.domjos.myarchiveservices.services.IGDBWebservice;
import de.domjos.myarchiveservices.services.MovieDBWebservice;
import de.domjos.myarchiveservices.services.TitleWebservice;
import de.domjos.myarchiveservices.mediaTasks.GoogleBooksTask;
import de.domjos.myarchiveservices.mediaTasks.IGDBTask;
import de.domjos.myarchiveservices.mediaTasks.TheAudioDBTask;
import de.domjos.myarchiveservices.mediaTasks.TheMovieDBTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;

import static android.app.Activity.RESULT_OK;

public class MediaDialog extends DialogFragment {
    private Activity activity;
    private BaseDescriptionObject currentObject;
    private TextView lblTitle;
    private String type, search;
    private boolean multiple = false;
    private List<TitleWebservice<? extends BaseMediaObject>> titleWebservices;

    private SwipeRefreshDeleteList lvMedia;
    private EditText txtSearch;
    private ImageButton cmdSearch, cmdSave;
    private Spinner spWebservices;
    private CustomSpinnerAdapter<? extends TitleWebservice<? extends BaseMediaObject>> webServiceAdapter;

    private TheMovieDBTask theMovieDBTask = null;
    private TheAudioDBTask theAudioDBTask = null;
    private GoogleBooksTask googleBooksTask = null;
    private IGDBTask igdbTask = null;
    private SearchTask searchTask = null;

    public static MediaDialog newInstance(String search, String type, List<TitleWebservice<? extends BaseMediaObject>> titleWebservices) {
        MediaDialog mediaDialog = new MediaDialog();
        mediaDialog.setTitleWebservices(titleWebservices);
        Bundle args = new Bundle();
        args.putString("search", search);
        args.putString("type", type);
        mediaDialog.setArguments(args);

        return mediaDialog;
    }

    private void setTitleWebservices(List<TitleWebservice<? extends BaseMediaObject>> titleWebservices) {
        this.titleWebservices = titleWebservices;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.media_dialog, container, true);
        Objects.requireNonNull(this.getDialog()).requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.initControls(v);
        this.changeView();
        this.setLabel(0);

        this.txtSearch.setText(this.search);

        if(!this.search.trim().isEmpty()) {
            this.reload(0);
        }

        this.txtSearch.setOnEditorActionListener((textView, i, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                this.cmdSearch.callOnClick();
            }
            return false;
        });

        this.cmdSearch.setOnClickListener(view -> {
            this.reload(this.spWebservices.getSelectedItemPosition());
            currentObject = null;
        });

        this.spWebservices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLabel(position);
                lvMedia.getAdapter().clear();
                currentObject = null;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        this.lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> this.currentObject = listObject);

        this.cmdSave.setOnClickListener(view -> {
            if(this.cmdSave.getTag().toString().equals(this.getString(R.string.sys_save))) {
                this.cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_cancel));
                this.cmdSave.setTag(this.getString(R.string.sys_cancel));
                this.setCancelable(false);
                if(this.currentObject != null) {
                    if(this.multiple) {
                        int icon = R.drawable.icon_notification;
                        boolean notification = MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications();

                        try {
                            TitleWebservice<? extends BaseMediaObject> currentWebService = this.webServiceAdapter.getItem(this.spWebservices.getSelectedItemPosition());
                            long id = ((BaseMediaObject) this.currentObject.getObject()).getId();
                            String description = ((BaseMediaObject) this.currentObject.getObject()).getDescription();
                            Validator validator = new Validator(this.activity, icon);
                            List<BaseDescriptionObject> baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.activity, "", "media");

                            if(validator.checkDuplicatedEntry(this.currentObject.getTitle(), 0, baseDescriptionObjects) && currentWebService != null) {
                                if(currentWebService instanceof MovieDBWebservice) {
                                    String key = MainActivity.GLOBALS.getSettings(this.requireContext()).getMovieDBKey();
                                    theMovieDBTask = new TheMovieDBTask(this.activity, notification, icon, description, key);
                                    theMovieDBTask.after(o -> {
                                        if(o != null && !o.isEmpty()) {
                                            MainActivity.GLOBALS.getDatabase(this.activity).insertOrUpdateMovie(o.get(0));
                                        }
                                        cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_save));
                                        cmdSave.setTag(getString(R.string.sys_save));
                                        MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.sys_save)), icon, activity);
                                        MediaDialog.this.setCancelable(true);
                                    });
                                    theMovieDBTask.execute(new Long[] {id});
                                } else if(currentWebService instanceof AudioDBWebservice) {
                                    theAudioDBTask = new TheAudioDBTask(this.activity, notification, icon);
                                    theAudioDBTask.after(o -> {
                                        if(o != null && !o.isEmpty()) {
                                            MainActivity.GLOBALS.getDatabase(this.activity).insertOrUpdateAlbum(o.get(0));
                                        }
                                        cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_save));
                                        cmdSave.setTag(getString(R.string.sys_save));
                                        MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.sys_save)), icon, activity);
                                        MediaDialog.this.setCancelable(true);
                                    });
                                    theAudioDBTask.execute(new Long[] {id});
                                } else if(currentWebService instanceof GoogleBooksWebservice) {
                                    String key = MainActivity.GLOBALS.getSettings(this.requireContext()).getGoogleBooksKey();
                                    googleBooksTask = new GoogleBooksTask(this.activity, notification, icon, description, key);
                                    googleBooksTask.after(o -> {
                                        if(o != null && !o.isEmpty()) {
                                            MainActivity.GLOBALS.getDatabase(this.activity).insertOrUpdateBook(o.get(0));
                                        }
                                        cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_save));
                                        cmdSave.setTag(getString(R.string.sys_save));
                                        MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.sys_save)), icon, activity);
                                        MediaDialog.this.setCancelable(true);
                                    });
                                    googleBooksTask.execute(new String[] {""});
                                } else if(currentWebService instanceof IGDBWebservice) {
                                    String key = MainActivity.GLOBALS.getSettings(this.requireContext()).getIGDBKey();
                                    igdbTask = new IGDBTask(this.activity, notification, icon, key);
                                    igdbTask.after(o -> {
                                        if(o != null && !o.isEmpty()) {
                                            MainActivity.GLOBALS.getDatabase(this.activity).insertOrUpdateGame(o.get(0));
                                        }
                                        cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_save));
                                        cmdSave.setTag(getString(R.string.sys_save));
                                        MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.sys_save)), icon, activity);
                                        MediaDialog.this.setCancelable(true);
                                    });
                                    igdbTask.execute(new Long[] {id});
                                }
                            } else {
                                MessageHelper.printMessage(validator.getResult(), R.mipmap.ic_launcher_round, this.activity);
                                this.cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_save));
                                this.cmdSave.setTag(getString(R.string.sys_save));
                                MediaDialog.this.setCancelable(true);
                            }
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, icon, this.activity);
                            MediaDialog.this.setCancelable(true);
                        }
                    } else {
                        Intent intent = new Intent();
                        if(this.currentObject.getObject() != null) {
                            intent.putExtra("id", ((BaseMediaObject) this.currentObject.getObject()).getId());
                            intent.putExtra("type", this.type);
                            intent.putExtra("description", ((BaseMediaObject) this.currentObject.getObject()).getDescription());
                            Objects.requireNonNull(this.getTargetFragment()).onActivityResult(this.getTargetRequestCode(), RESULT_OK, intent);
                            this.dismiss();
                        }
                    }
                }
            } else {
                if(this.theMovieDBTask != null) {
                    this.theMovieDBTask.shutDown();
                }
                if(this.theAudioDBTask != null) {
                    this.theAudioDBTask.shutDown();
                }
                if(this.googleBooksTask != null) {
                    this.googleBooksTask.shutDown();
                }
                if(this.igdbTask != null) {
                    this.igdbTask.shutDown();
                }

                this.cmdSave.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_save));
                this.cmdSave.setTag(this.getString(R.string.sys_save));
            }
        });

        this.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                search = editable.toString();
            }
        });

        return v;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void reload(int position) {
        try {
            if(this.cmdSearch.getTag().toString().equals(this.getString(R.string.sys_search))) {
                this.cmdSearch.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_cancel));
                this.cmdSearch.setTag(this.getString(R.string.sys_cancel));
                if(this.type != null && this.titleWebservices != null) {
                    TitleWebservice currentService = this.webServiceAdapter.getItem(position);

                    if(currentService != null) {
                        this.searchTask = new SearchTask(this.activity, MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications(), currentService);
                        this.searchTask.after(o -> {
                            lvMedia.getAdapter().clear();
                            for(BaseDescriptionObject baseDescriptionObject : o) {
                                lvMedia.getAdapter().add(baseDescriptionObject);
                            }
                            cmdSearch.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_search));
                            cmdSearch.setTag(getString(R.string.sys_search));
                        });
                        this.searchTask.execute(new String[]{this.search});
                    }
                }
            } else {
                if(this.searchTask != null) {
                    this.searchTask.shutDown();
                }

                this.cmdSearch.setImageDrawable(WidgetUtils.getDrawable(this.requireContext(), R.drawable.icon_search));
                this.cmdSearch.setTag(this.getString(R.string.sys_search));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity);
        }
    }

    private void initControls(View view) {
        this.activity = this.requireActivity();
        Bundle arguments = this.requireArguments();
        this.type = arguments.getString("type");
        this.search = arguments.getString("search");

        this.txtSearch = view.findViewById(R.id.txtSearch);
        this.cmdSearch = view.findViewById(R.id.cmdSearch);
        this.lvMedia = view.findViewById(R.id.lvSuggestions);
        this.cmdSave = view.findViewById(R.id.cmdSave);
        this.lblTitle = view.findViewById(R.id.lblTitle);
        this.lblTitle.setMovementMethod(LinkMovementMethod.getInstance());

        this.spWebservices = view.findViewById(R.id.spWebServices);
        this.webServiceAdapter = new CustomSpinnerAdapter<>(this.activity, this.titleWebservices);
        this.spWebservices.setAdapter(this.webServiceAdapter);
        this.webServiceAdapter.notifyDataSetChanged();
    }

    private void changeView() {
        if(this.titleWebservices != null) {
            if(this.titleWebservices.size() != 1) {
                this.multiple = true;
                this.spWebservices.setVisibility(View.VISIBLE);
            } else {
                this.multiple = false;
                this.spWebservices.setVisibility(View.GONE);
            }
        }
    }

    private void setLabel(int position) {
        TitleWebservice<? extends BaseMediaObject> currentService = this.webServiceAdapter.getItem(position);

        if(currentService != null) {
            this.lblTitle.setText(currentService.getTitle());
        }
    }

    public static class SearchTask extends CustomAbstractTask<String[], Void, List<BaseDescriptionObject>> {
        private final TitleWebservice<? extends  BaseMediaObject> titleWebservice;

        SearchTask(Activity activity, boolean showNotifications, TitleWebservice<? extends BaseMediaObject> titleWebservice) {
            super(activity, R.string.sys_search, R.string.sys_search, showNotifications, R.drawable.icon_notification);
            this.titleWebservice = titleWebservice;
        }

        @Override
        protected List<BaseDescriptionObject> doInBackground(String... strings) {
            List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
            try {
                List<BaseMediaObject> baseMediaObjects = titleWebservice.getMedia(strings[0]);
                if (baseMediaObjects != null) {
                    for (BaseMediaObject baseMediaObject : baseMediaObjects) {
                        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                        baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                        baseDescriptionObject.setCover(baseMediaObject.getCover());
                        baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(baseMediaObject.getReleaseDate(), super.getContext().getString(R.string.sys_date_format)));
                        baseDescriptionObject.setObject(baseMediaObject);
                        baseDescriptionObjects.add(baseDescriptionObject);
                    }
                }
            } catch (InterruptedIOException ignored) {
            } catch (UnknownHostException ex) {
                this.printMessage(getContext().getString(R.string.sys_no_internet));
            } catch (Exception ex) {
                this.printException(ex);
            }
            return baseDescriptionObjects;
        }
    }
}
