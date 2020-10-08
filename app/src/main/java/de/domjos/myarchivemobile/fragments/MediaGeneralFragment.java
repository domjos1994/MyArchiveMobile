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

package de.domjos.myarchivemobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.*;

import de.domjos.customwidgets.utils.ConvertHelper;
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
import de.domjos.myarchivelibrary.services.AudioDBWebservice;
import de.domjos.myarchivelibrary.services.GoogleBooksWebservice;
import de.domjos.myarchivelibrary.services.IGDBWebservice;
import de.domjos.myarchivelibrary.services.MovieDBWebservice;
import de.domjos.myarchivelibrary.services.TitleWebservice;
import de.domjos.myarchivelibrary.tasks.EANDataAlbumTask;
import de.domjos.myarchivelibrary.tasks.EANDataGameTask;
import de.domjos.myarchivelibrary.tasks.EANDataMovieTask;
import de.domjos.myarchivelibrary.tasks.GoogleBooksTask;
import de.domjos.myarchivelibrary.tasks.IGDBTask;
import de.domjos.myarchivelibrary.tasks.TheAudioDBTask;
import de.domjos.myarchivelibrary.tasks.TheMovieDBTask;
import de.domjos.myarchivelibrary.tasks.WikiDataCompanyTask;
import de.domjos.myarchivelibrary.tasks.WikiDataPersonTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomAutoCompleteAdapter;
import de.domjos.myarchivemobile.custom.CustomDatePickerField;
import de.domjos.myarchivemobile.dialogs.MediaDialog;
import de.domjos.myarchivemobile.settings.Settings;

import static android.app.Activity.RESULT_OK;

public class MediaGeneralFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaGeneralTitle, txtMediaGeneralOriginalTitle;
    private EditText txtMediaGeneralCode, txtMediaGeneralPrice, txtMediaGeneralDescription;
    private CustomDatePickerField txtMediaGeneralReleaseDate;
    private AutoCompleteTextView txtMediaGeneralCategory;
    private MultiAutoCompleteTextView txtMediaGeneralTags;
    private ImageButton cmdMediaGeneralScan, cmdMediaGeneralSearch, cmdMediaGeneralTitleSearch;

    private BaseMediaObject baseMediaObject;
    private Validator validator;

    private final static int SUGGESTIONS_REQUEST = 345;

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

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.txtMediaGeneralTitle.getLayoutParams();
        if(this.abstractPagerAdapter != null) {
            if(MainActivity.GLOBALS.isNetwork()) {
                layoutParams.weight = 9;
                this.cmdMediaGeneralTitleSearch.setVisibility(View.VISIBLE);
            } else {
                layoutParams.weight = 10;
                this.cmdMediaGeneralTitleSearch.setVisibility(View.GONE);
            }
        }

        this.txtMediaGeneralCategory = view.findViewById(R.id.txtMediaGeneralCategory);
        CustomAutoCompleteAdapter<String> categoryAdapter = new CustomAutoCompleteAdapter<>(this.requireActivity(), this.txtMediaGeneralCategory);
        for(BaseDescriptionObject baseDescriptionObject : MainActivity.GLOBALS.getDatabase().getBaseObjects("categories", "", 0, "")) {
            categoryAdapter.add(baseDescriptionObject.getTitle());
        }
        this.txtMediaGeneralCategory.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();

        this.txtMediaGeneralTags = view.findViewById(R.id.txtMediaGeneralTags);
        this.txtMediaGeneralTags.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        CustomAutoCompleteAdapter<String> tagAdapter = new CustomAutoCompleteAdapter<>(this.requireActivity(), this.txtMediaGeneralTags);
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
            Context ctx = this.requireActivity();
            try {
                String type;
                String search = this.txtMediaGeneralTitle.getText().toString();
                TitleWebservice<? extends BaseMediaObject> titleWebservice;
                Settings settings = MainActivity.GLOBALS.getSettings();

                if(search.trim().isEmpty()) {
                    throw new Exception(ctx.getString(R.string.media_general_empty));
                }

                if(this.abstractPagerAdapter.getItem(3) instanceof MediaMovieFragment) {
                    type = this.getString(R.string.movie);
                    titleWebservice = new MovieDBWebservice(ctx, 0L, "", settings.getMovieDBKey());
                } else if(this.abstractPagerAdapter.getItem(3) instanceof MediaAlbumFragment) {
                    type = this.getString(R.string.album);
                    titleWebservice = new AudioDBWebservice(ctx, 0L);
                } else if(this.abstractPagerAdapter.getItem(3) instanceof MediaBookFragment) {
                    type = this.getString(R.string.book);
                    titleWebservice = new GoogleBooksWebservice(ctx, "", "", settings.getGoogleBooksKey());
                } else {
                    type = this.getString(R.string.game);
                    titleWebservice = new IGDBWebservice(ctx, 0, settings.getIGDBKey());
                }
                MediaDialog mediaDialog = MediaDialog.newInstance(search, type, Collections.singletonList(titleWebservice));
                mediaDialog.setTargetFragment(this, MediaGeneralFragment.SUGGESTIONS_REQUEST);
                mediaDialog.show(this.getParentFragmentManager(), "dialog");
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ctx);
            }
        });

        this.cmdMediaGeneralSearch.setOnClickListener(view1 -> {
            Activity ctx = this.requireActivity();
            try {
                int icon = R.drawable.icon_notification;
                boolean notifications = MainActivity.GLOBALS.getSettings().isNotifications();
                String search = this.txtMediaGeneralCode.getText().toString();

                if(search.trim().isEmpty()) {
                    MessageHelper.printMessage(this.getString(R.string.media_general_empty), R.mipmap.ic_launcher_round, this.requireActivity());
                } else {
                    if((this.abstractPagerAdapter.getItem(3) instanceof MediaBookFragment)) {
                        GoogleBooksTask googleBooksTask = new GoogleBooksTask(ctx, notifications, icon, "", MainActivity.GLOBALS.getSettings().getGoogleBooksKey());
                        List<Book> books = googleBooksTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                        if (books != null && !books.isEmpty()) {
                            Book book = books.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(ctx, notifications, icon);
                            book.setPersons(wikiDataPersonTask.execute(book.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(ctx, notifications, icon);
                            book.setCompanies(wikiDataCompanyTask.execute(book.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(book);
                        }
                    } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaMovieFragment)) {
                        EANDataMovieTask eanDataTask = new EANDataMovieTask(ctx, notifications, icon, MainActivity.GLOBALS.getSettings().getEANDataKey());
                        List<Movie> movies = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                        if(movies != null && !movies.isEmpty()) {
                            Movie movie = movies.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(ctx, notifications, icon);
                            movie.setPersons(wikiDataPersonTask.execute(movie.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(ctx, notifications, icon);
                            movie.setCompanies(wikiDataCompanyTask.execute(movie.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(movie);
                        }
                    } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaAlbumFragment)) {
                        EANDataAlbumTask eanDataTask = new EANDataAlbumTask(ctx, notifications, icon, MainActivity.GLOBALS.getSettings().getEANDataKey());
                        List<Album> albums = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                        if(albums != null && !albums.isEmpty()) {
                            Album album = albums.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(ctx, notifications, icon);
                            album.setPersons(wikiDataPersonTask.execute(album.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(ctx, notifications, icon);
                            album.setCompanies(wikiDataCompanyTask.execute(album.getCompanies().toArray(new Company[]{})).get());

                            this.abstractPagerAdapter.setMediaObject(album);
                        }
                    } else if((this.abstractPagerAdapter.getItem(3) instanceof MediaGameFragment)) {
                        EANDataGameTask eanDataTask = new EANDataGameTask(ctx, notifications, icon, MainActivity.GLOBALS.getSettings().getEANDataKey());
                        List<Game> games = eanDataTask.execute(this.txtMediaGeneralCode.getText().toString()).get();
                        if(games != null && !games.isEmpty()) {
                            Game game = games.get(0);
                            WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(ctx, notifications, icon);
                            game.setPersons(wikiDataPersonTask.execute(game.getPersons().toArray(new Person[]{})).get());
                            WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(ctx,notifications, icon);
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
        this.hideSearchButton(MainActivity.GLOBALS.isNetwork());
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
            this.txtMediaGeneralReleaseDate.setDate(this.baseMediaObject.getReleaseDate());
        } else {
            this.txtMediaGeneralReleaseDate.setText("");
        }
        this.txtMediaGeneralCode.setText(this.baseMediaObject.getCode());
        this.txtMediaGeneralPrice.setText(ConvertHelper.convertDoubleToString(this.baseMediaObject.getPrice()));
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
            this.baseMediaObject.setReleaseDate(this.txtMediaGeneralReleaseDate.getDate());
            this.baseMediaObject.setCode(this.txtMediaGeneralCode.getText().toString());
            this.baseMediaObject.setPrice(ConvertHelper.convertStringToDouble(this.txtMediaGeneralPrice.getText().toString()));
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
    @SuppressWarnings("unchecked")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if(resultCode == RESULT_OK && requestCode == 234) {
                this.txtMediaGeneralCode.setText(intent.getStringExtra("codes"));
            }

            if(resultCode == RESULT_OK && requestCode == SUGGESTIONS_REQUEST) {
                String type = intent.getStringExtra("type");
                String description = intent.getStringExtra("description");
                long id = intent.getLongExtra("id", 0);
                if(Objects.requireNonNull(type).equals(this.getString(R.string.movie))) {
                    TheMovieDBTask theMovieDBTask = new TheMovieDBTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, description, MainActivity.GLOBALS.getSettings().getMovieDBKey());
                    List<Movie> movies = theMovieDBTask.execute(id).get();
                    if(movies != null && !movies.isEmpty()) {
                        this.abstractPagerAdapter.setMediaObject(movies.get(0));
                    }
                } else if(Objects.requireNonNull(type).equals(this.getString(R.string.album))) {
                    TheAudioDBTask theMovieDBTask = new TheAudioDBTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification);
                    List<Album> movies = theMovieDBTask.execute(id).get();
                    if(movies != null && !movies.isEmpty()) {
                        this.abstractPagerAdapter.setMediaObject(movies.get(0));
                    }
                } else if(Objects.requireNonNull(type).equals(this.getString(R.string.book))) {
                    GoogleBooksTask googleBooksTask = new GoogleBooksTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, description, MainActivity.GLOBALS.getSettings().getGoogleBooksKey());
                    List<Book> books = googleBooksTask.execute("").get();
                    if(books != null && !books.isEmpty()) {
                        this.abstractPagerAdapter.setMediaObject(books.get(0));
                    }
                } else if(Objects.requireNonNull(type).equals(this.getString(R.string.game))) {
                    IGDBTask igdbTask = new IGDBTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, MainActivity.GLOBALS.getSettings().getIGDBKey());
                    List<Game> games = igdbTask.execute(id).get();
                    if(games != null && !games.isEmpty()) {
                        this.abstractPagerAdapter.setMediaObject(games.get(0));
                    }
                }

            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }
}
