package de.domjos.myarchivemobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.activities.ScanActivity;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.tasks.EANDataAlbumTask;
import de.domjos.myarchivelibrary.tasks.EANDataGameTask;
import de.domjos.myarchivelibrary.tasks.EANDataMovieTask;
import de.domjos.myarchivelibrary.tasks.GoogleBooksTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CONNECTIVITY_SERVICE;

public class MediaGeneralFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaGeneralTitle, txtMediaGeneralOriginalTitle, txtMediaGeneralReleaseDate;
    private EditText txtMediaGeneralCode, txtMediaGeneralPrice, txtMediaGeneralDescription;
    private AutoCompleteTextView txtMediaGeneralCategory;
    private MultiAutoCompleteTextView txtMediaGeneralTags;
    private ImageButton cmdMediaGeneralScan, cmdMediaGeneralSearch;

    private BaseMediaObject baseMediaObject;
    private Validator validator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_general, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaGeneralTitle = view.findViewById(R.id.txtMediaGeneralTitle);
        this.txtMediaGeneralOriginalTitle = view.findViewById(R.id.txtMediaGeneralOriginalTitle);
        this.txtMediaGeneralReleaseDate = view.findViewById(R.id.txtMediaGeneralReleaseDate);
        this.txtMediaGeneralCode = view.findViewById(R.id.txtMediaGeneralCode);
        this.txtMediaGeneralPrice = view.findViewById(R.id.txtMediaGeneralPrice);
        this.txtMediaGeneralDescription = view.findViewById(R.id.txtMediaGeneralDescription);

        this.txtMediaGeneralCategory = view.findViewById(R.id.txtMediaGeneralCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_expandable_list_item_1);
        for(BaseDescriptionObject baseDescriptionObject : MainActivity.GLOBALS.getDatabase().getBaseObjects("categories", "", 0, "")) {
            categoryAdapter.add(baseDescriptionObject.getTitle());
        }
        this.txtMediaGeneralCategory.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        this.txtMediaGeneralTags = view.findViewById(R.id.txtMediaGeneralTags);
        this.txtMediaGeneralTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_expandable_list_item_1);
        for(BaseDescriptionObject baseDescriptionObject : MainActivity.GLOBALS.getDatabase().getBaseObjects("tags", "", 0, "")) {
            tagAdapter.add(baseDescriptionObject.getTitle());
        }
        this.txtMediaGeneralTags.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

        this.cmdMediaGeneralSearch = view.findViewById(R.id.cmdMediaGeneralCodeSearch);
        this.cmdMediaGeneralScan = view.findViewById(R.id.cmdMediaGeneralCodeScan);

        this.cmdMediaGeneralScan.setOnClickListener(view1 -> {
            Intent intent = new Intent(this.getActivity(), ScanActivity.class);
            intent.putExtra("parent", "");
            intent.putExtra("single", true);
            startActivityForResult(intent, 234);
        });

        this.cmdMediaGeneralSearch.setOnClickListener(view1 -> {
            try {
                if((this.abstractPagerAdapter.getItem(3) instanceof MediaBookFragment)) {
                    GoogleBooksTask googleBooksTask = new GoogleBooksTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);
                    List<Book> books = googleBooksTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if (books != null) {
                        if (!books.isEmpty()) {
                            this.abstractPagerAdapter.setMediaObject(books.get(0));
                        }
                    }
                } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaMovieFragment)) {
                    EANDataMovieTask eanDataTask = new EANDataMovieTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, MainActivity.GLOBALS.getSettings().getEANDataKey());
                    List<Movie> movies = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if(movies != null) {
                        if(!movies.isEmpty()) {
                            this.abstractPagerAdapter.setMediaObject(movies.get(0));
                        }
                    }
                } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaAlbumFragment)) {
                    EANDataAlbumTask eanDataTask = new EANDataAlbumTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, MainActivity.GLOBALS.getSettings().getEANDataKey());
                    List<Album> albums = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if(albums != null) {
                        if(!albums.isEmpty()) {
                            this.abstractPagerAdapter.setMediaObject(albums.get(0));
                        }
                    }
                } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaGameFragment)) {
                    EANDataGameTask eanDataTask = new EANDataGameTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, MainActivity.GLOBALS.getSettings().getEANDataKey());
                    List<Game> games = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if(games != null) {
                        if(!games.isEmpty()) {
                            this.abstractPagerAdapter.setMediaObject(games.get(0));
                        }
                    }
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
            }
        });

        this.changeMode(false);
        this.validator = this.initValidation(this.validator);
        this.testConnection();
    }

    private void testConnection() {
        this.hideSearchButton(ControlsHelper.hasNetwork(Objects.requireNonNull(this.getContext())));

        ConnectivityManager manager = (ConnectivityManager) Objects.requireNonNull(this.getContext()).getSystemService(CONNECTIVITY_SERVICE);
        Activity activity = this.getActivity();
        if(manager != null && activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                manager.registerNetworkCallback(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        hideSearchButton(ControlsHelper.hasNetwork(activity));
                    }

                    @Override
                    public void onLosing(@NonNull Network network, int maxMsToLive) {
                        hideSearchButton(ControlsHelper.hasNetwork(activity));
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        hideSearchButton(ControlsHelper.hasNetwork(activity));
                    }

                    @Override
                    public void onUnavailable() {
                        hideSearchButton(ControlsHelper.hasNetwork(activity));
                    }
                });
            }
        }
    }

    private void hideSearchButton(boolean network) {
        Activity activity = this.getActivity();
        if(activity != null) {
            activity.runOnUiThread(()->this.cmdMediaGeneralSearch.setVisibility(network ? View.VISIBLE : View.GONE));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.txtMediaGeneralCode.getLayoutParams();
            layoutParams.weight = network ? 8 : 9;
            activity.runOnUiThread(()->this.txtMediaGeneralCode.setLayoutParams(layoutParams));
        }
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.baseMediaObject = baseMediaObject;

        this.txtMediaGeneralTitle.setText(this.baseMediaObject.getTitle());
        this.txtMediaGeneralOriginalTitle.setText(this.baseMediaObject.getOriginalTitle());
        if(this.baseMediaObject.getReleaseDate() != null) {
            this.txtMediaGeneralReleaseDate.setText(Converter.convertDateToString(this.baseMediaObject.getReleaseDate(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtMediaGeneralReleaseDate.setText("");
        }
        this.txtMediaGeneralCode.setText(this.baseMediaObject.getCode());
        this.txtMediaGeneralPrice.setText(String.valueOf(this.baseMediaObject.getPrice()));
        if(this.baseMediaObject.getCategory() != null) {
            this.txtMediaGeneralCategory.setText(this.baseMediaObject.getCategory().getTitle());
        } else {
            this.txtMediaGeneralCategory.setText("");
        }
        String tags = TextUtils.join(", ", this.baseMediaObject.getTags());
        if(tags != null) {
            this.txtMediaGeneralTags.setText(tags);
        } else {
            this.txtMediaGeneralTags.setText("");
        }
        this.txtMediaGeneralDescription.setText(this.baseMediaObject.getDescription());
    }

    @Override
    public BaseMediaObject getMediaObject() {
        try {
            this.baseMediaObject.setTitle(this.txtMediaGeneralTitle.getText().toString());
            this.baseMediaObject.setOriginalTitle(this.txtMediaGeneralOriginalTitle.getText().toString());
            this.baseMediaObject.setReleaseDate(Converter.convertStringToDate(this.txtMediaGeneralReleaseDate.getText().toString(), this.getString(R.string.sys_date_format)));
            this.baseMediaObject.setCode(this.txtMediaGeneralCode.getText().toString());
            this.baseMediaObject.setPrice(Double.parseDouble(this.txtMediaGeneralPrice.getText().toString()));
            this.baseMediaObject.setDescription(this.txtMediaGeneralDescription.getText().toString());

            if(!this.txtMediaGeneralCategory.getText().toString().isEmpty()) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(this.txtMediaGeneralCategory.getText().toString());
                this.baseMediaObject.setCategory(baseDescriptionObject);
            }

            if(!this.txtMediaGeneralTags.getText().toString().isEmpty()) {
                String[] tags = this.txtMediaGeneralTags.getText().toString().split(",");
                for(String tag : tags) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(tag.trim());
                    this.baseMediaObject.getTags().add(baseDescriptionObject);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        return this.baseMediaObject;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaGeneralTitle.setEnabled(editMode);
        this.txtMediaGeneralOriginalTitle.setEnabled(editMode);
        this.txtMediaGeneralReleaseDate.setEnabled(editMode);
        this.txtMediaGeneralCode.setEnabled(editMode);
        this.txtMediaGeneralPrice.setEnabled(editMode);
        this.txtMediaGeneralDescription.setEnabled(editMode);
        this.txtMediaGeneralCategory.setEnabled(editMode);
        this.txtMediaGeneralTags.setEnabled(editMode);
        this.cmdMediaGeneralSearch.setEnabled(editMode);
        this.cmdMediaGeneralScan.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        this.validator = validator;
        if(this.txtMediaGeneralTitle != null  && this.validator != null) {
            this.validator.addEmptyValidator(this.txtMediaGeneralTitle);
            this.validator.addLengthValidator(this.txtMediaGeneralCode, 0, 13);
        }
        return this.validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if(resultCode == RESULT_OK && requestCode == 234) {
                this.txtMediaGeneralCode.setText(intent.getStringExtra("codes"));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }
}
