package de.domjos.myarchivemobile.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.services.TitleWebservice;
import de.domjos.myarchivelibrary.tasks.AbstractTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

import static android.app.Activity.RESULT_OK;

public class MediaDialog extends DialogFragment {
    private BaseDescriptionObject currentObject;
    private TextView lblTitle;
    private TitleWebservice<? extends BaseMediaObject> titleWebservice;

    public static MediaDialog newInstance(String search, String type, TitleWebservice<? extends BaseMediaObject> titleWebservice) {
        MediaDialog mediaDialog = new MediaDialog();
        mediaDialog.setTitleWebservice(titleWebservice);
        Bundle args = new Bundle();
        args.putString("search", search);
        args.putString("type", type);
        mediaDialog.setArguments(args);

        return mediaDialog;
    }

    private void setTitleWebservice(TitleWebservice<? extends BaseMediaObject> titleWebservice) {
        this.titleWebservice = titleWebservice;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.media_dialog, container, true);

        Activity activity = Objects.requireNonNull(this.getActivity());
        Bundle arguments = Objects.requireNonNull(this.getArguments());
        String type = arguments.getString("type");
        String search = arguments.getString("search");

        EditText txtSearch = v.findViewById(R.id.txtSearch);
        ImageButton cmdSearch = v.findViewById(R.id.cmdSearch);
        SwipeRefreshDeleteList lvMedia = v.findViewById(R.id.lvSuggestions);
        ImageButton cmdSave = v.findViewById(R.id.cmdSave);
        this.lblTitle = v.findViewById(R.id.lblTitle);
        this.lblTitle.setMovementMethod(LinkMovementMethod.getInstance());

        txtSearch.setText(search);

        this.reload(activity, type, search, lvMedia);

        cmdSearch.setOnClickListener(view -> this.reload(activity, type, txtSearch.getText().toString(), lvMedia));

        lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> this.currentObject = listObject);

        cmdSave.setOnClickListener(view -> {
            Intent intent = new Intent();
            if(this.currentObject.getObject() != null) {
                intent.putExtra("id", ((BaseMediaObject) this.currentObject.getObject()).getId());
                intent.putExtra("type", type);
                intent.putExtra("description", ((BaseMediaObject) this.currentObject.getObject()).getDescription());
                Objects.requireNonNull(this.getTargetFragment()).onActivityResult(this.getTargetRequestCode(), RESULT_OK, intent);
                this.dismiss();
            }
        });

        return v;
    }

    private void reload(Activity activity, String type, String search, SwipeRefreshDeleteList lvMedia) {
        try {
            if(type != null) {
                if(this.titleWebservice != null) {
                    Spanned text = Html.fromHtml("<a href='" + this.titleWebservice.getUrl() + "'>" +this. titleWebservice.getTitle() + "</a>");
                    this.lblTitle.setText(text);

                    SearchTask searchTask = new SearchTask(activity, MainActivity.GLOBALS.getSettings().isNotifications(), this.titleWebservice);
                    List<BaseDescriptionObject> baseDescriptionObjects = searchTask.execute(search).get();
                    lvMedia.getAdapter().clear();
                    for(BaseDescriptionObject baseDescriptionObject : baseDescriptionObjects) {
                        lvMedia.getAdapter().add(baseDescriptionObject);
                    }
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity);
        }
    }

    public static class SearchTask extends AbstractTask<String, Void, List<BaseDescriptionObject>> {
        private TitleWebservice<? extends  BaseMediaObject> titleWebservice;

        SearchTask(Activity activity, boolean showNotifications, TitleWebservice<? extends BaseMediaObject> titleWebservice) {
            super(activity, titleWebservice.getTitle(), R.string.sys_search, showNotifications, R.mipmap.ic_launcher_round);
            this.titleWebservice = titleWebservice;
        }


        @Override
        protected void before() {

        }

        @Override
        protected List<BaseDescriptionObject> doInBackground(String... strings) {
            List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
            try {
                List<BaseMediaObject> baseMediaObjects = titleWebservice.getMedia(strings[0]);
                if(baseMediaObjects != null) {
                    for(BaseMediaObject baseMediaObject : baseMediaObjects) {
                        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                        baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                        baseDescriptionObject.setCover(baseMediaObject.getCover());
                        baseDescriptionObject.setDescription(Converter.convertDateToString(baseMediaObject.getReleaseDate(), super.getContext().getString(R.string.sys_date_format)));
                        baseDescriptionObject.setObject(baseMediaObject);
                        baseDescriptionObjects.add(baseDescriptionObject);
                    }
                }
            } catch (Exception ex) {
                this.printException(ex);
            }
            return baseDescriptionObjects;
        }
    }
}
