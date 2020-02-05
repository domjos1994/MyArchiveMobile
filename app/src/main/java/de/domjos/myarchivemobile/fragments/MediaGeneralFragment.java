package de.domjos.myarchivemobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.Timer;
import java.util.TimerTask;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.activities.ScanActivity;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.MovieDBWebService;
import de.domjos.myarchivelibrary.tasks.EANDataAlbumTask;
import de.domjos.myarchivelibrary.tasks.EANDataGameTask;
import de.domjos.myarchivelibrary.tasks.EANDataMovieTask;
import de.domjos.myarchivelibrary.tasks.GoogleBooksTask;
import de.domjos.myarchivelibrary.tasks.TheMovieDBTask;
import de.domjos.myarchivelibrary.tasks.WikiDataCompanyTask;
import de.domjos.myarchivelibrary.tasks.WikiDataPersonTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

import static android.app.Activity.RESULT_OK;

public class MediaGeneralFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaGeneralOriginalTitle, txtMediaGeneralReleaseDate;
    private EditText txtMediaGeneralCode, txtMediaGeneralPrice, txtMediaGeneralDescription;
    private AutoCompleteTextView txtMediaGeneralTitle, txtMediaGeneralCategory;
    private MultiAutoCompleteTextView txtMediaGeneralTags;
    private ImageButton cmdMediaGeneralScan, cmdMediaGeneralSearch, cmdMediaGeneralTitleSearch;

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
        this.cmdMediaGeneralTitleSearch = view.findViewById(R.id.cmdMediaGeneralTitleSearch);

        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_list_item_1);
        this.txtMediaGeneralTitle.setAdapter(titleAdapter);
        titleAdapter.notifyDataSetChanged();

        final Timer[] timer = {new Timer()};
        this.txtMediaGeneralTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(ControlsHelper.hasNetwork(Objects.requireNonNull(getActivity()))) {
                    timer[0].cancel();
                    timer[0] = new Timer();
                    timer[0].schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                getActivity().runOnUiThread(titleAdapter::clear);
                                List<Movie> movies = MovieDBWebService.getMovies(Objects.requireNonNull(getActivity()), editable.toString());
                                for(Movie movie : movies) {
                                    getActivity().runOnUiThread(()->titleAdapter.add(movie.getTitle()));
                                }
                                getActivity().runOnUiThread(()->txtMediaGeneralTitle.showDropDown());
                            } catch (Exception ex) {
                                getActivity().runOnUiThread(()->MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getContext()));
                            }
                        }
                    }, 500);
                } else {
                    titleAdapter.clear();
                }
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.txtMediaGeneralTitle.getLayoutParams();
        if((this.abstractPagerAdapter.getItem(3) instanceof MediaMovieFragment)) {
            layoutParams.weight = 9;
            this.cmdMediaGeneralTitleSearch.setVisibility(View.VISIBLE);
        } else {
            layoutParams.weight = 10;
            this.cmdMediaGeneralTitleSearch.setVisibility(View.GONE);
        }

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

        this.cmdMediaGeneralTitleSearch.setOnClickListener(view1 -> {
            try {
                TheMovieDBTask theMovieDBTask = new TheMovieDBTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);
                List<Movie> movies = theMovieDBTask.execute(this.txtMediaGeneralTitle.getText().toString()).get();
                if(movies != null) {
                    if(!movies.isEmpty()) {
                        this.abstractPagerAdapter.setMediaObject(movies.get(0));
                    }
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
            }
        });

        this.cmdMediaGeneralSearch.setOnClickListener(view1 -> {
            try {
                int icon = R.mipmap.ic_launcher_round;
                boolean notifications = MainActivity.GLOBALS.getSettings().isNotifications();

                if((this.abstractPagerAdapter.getItem(3) instanceof MediaBookFragment)) {
                    GoogleBooksTask googleBooksTask = new GoogleBooksTask(this.getActivity(), notifications, icon);
                    List<Book> books = googleBooksTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if (books != null) {
                        if (!books.isEmpty()) {
                            Book book = books.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(this.getActivity(), notifications, icon);
                            book.setPersons(wikiDataPersonTask.execute(book.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(this.getActivity(), notifications, icon);
                            book.setCompanies(wikiDataCompanyTask.execute(book.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(book);
                        }
                    }
                } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaMovieFragment)) {
                    EANDataMovieTask eanDataTask = new EANDataMovieTask(this.getActivity(), notifications, icon, MainActivity.GLOBALS.getSettings().getEANDataKey());
                    List<Movie> movies = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if(movies != null) {
                        if(!movies.isEmpty()) {
                            Movie movie = movies.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(this.getActivity(), notifications, icon);
                            movie.setPersons(wikiDataPersonTask.execute(movie.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(this.getActivity(), notifications, icon);
                            movie.setCompanies(wikiDataCompanyTask.execute(movie.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(movie);
                        }
                    }
                } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaAlbumFragment)) {
                    EANDataAlbumTask eanDataTask = new EANDataAlbumTask(this.getActivity(), notifications, icon, MainActivity.GLOBALS.getSettings().getEANDataKey());
                    List<Album> albums = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if(albums != null) {
                        if(!albums.isEmpty()) {
                            Album album = albums.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(this.getActivity(), notifications, icon);
                            album.setPersons(wikiDataPersonTask.execute(album.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(this.getActivity(), notifications, icon);
                            album.setCompanies(wikiDataCompanyTask.execute(album.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(album);
                        }
                    }
                } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaGameFragment)) {
                    EANDataGameTask eanDataTask = new EANDataGameTask(this.getActivity(), notifications, icon, MainActivity.GLOBALS.getSettings().getEANDataKey());
                    List<Game> games = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                    if(games != null) {
                        if(!games.isEmpty()) {
                            Game game = games.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(this.getActivity(), notifications, icon);
                            game.setPersons(wikiDataPersonTask.execute(game.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(this.getActivity(),notifications, icon);
                            game.setCompanies(wikiDataCompanyTask.execute(game.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(game);
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
        this.cmdMediaGeneralTitleSearch.setEnabled(editMode);
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
