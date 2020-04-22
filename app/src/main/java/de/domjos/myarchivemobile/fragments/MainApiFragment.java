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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.AudioDBWebservice;
import de.domjos.myarchivelibrary.services.GoogleBooksWebservice;
import de.domjos.myarchivelibrary.services.IGDBWebservice;
import de.domjos.myarchivelibrary.services.MovieDBWebservice;
import de.domjos.myarchivelibrary.services.TextService;
import de.domjos.myarchivelibrary.tasks.AbstractTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.helper.PDFWriterHelper;
import de.domjos.myarchivemobile.settings.Settings;

import static android.app.Activity.RESULT_OK;

public class MainApiFragment extends ParentFragment {
    private final static String BOOKS = "books";
    private final static String MOVIES = "movies";
    private final static String GAMES = "games";
    private final static String MUSIC = "music";
    private final static String NAME = "name";
    private final static String PATH = "path";
    private final static String TYPE = "type";
    private final static String FORMAT = "format";

    private TableLayout tblCells;
    private CheckBox chkApiBooks, chkApiGames, chkApiMusic, chkApiMovies;
    private EditText txtApiName, txtApiPath;
    private String[] typeArray, formatArray;
    private Map<String, Spinner> mpCells = new LinkedHashMap<>();
    private ProgressBar pbProgress;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_api, container, false);

        Activity activity = Objects.requireNonNull(this.getActivity());

        this.typeArray = activity.getResources().getStringArray(R.array.api_type);
        this.formatArray = new String[4];
        this.formatArray[0] = "pdf";
        this.formatArray[1] = "txt";
        this.formatArray[2] = "csv";
        this.formatArray[3] = "db";


        Spinner spApiType = root.findViewById(R.id.spApiType);
        spApiType.setAdapter(new CustomSpinnerAdapter<>(this.getActivity(), this.typeArray));
        Spinner spApiFormat = root.findViewById(R.id.spApiFormat);
        CustomSpinnerAdapter<String> formatAdapter = new CustomSpinnerAdapter<>(this.getActivity());
        spApiFormat.setAdapter(formatAdapter);
        formatAdapter.notifyDataSetChanged();

        formatAdapter.add(this.formatArray[0]);
        formatAdapter.add(this.formatArray[1]);
        formatAdapter.add(this.formatArray[2]);
        formatAdapter.add(this.formatArray[3]);

        this.tblCells = root.findViewById(R.id.tblCells);
        this.pbProgress = root.findViewById(R.id.pbProgress);
        this.txtApiPath = root.findViewById(R.id.txtApiPath);
        this.txtApiName = root.findViewById(R.id.txtApiName);
        Button cmdApiPath = root.findViewById(R.id.cmdApiPath);

        this.chkApiBooks = root.findViewById(R.id.chkApiBooks);
        this.chkApiMovies = root.findViewById(R.id.chkApiMovies);
        this.chkApiMusic = root.findViewById(R.id.chkApiMusic);
        this.chkApiGames = root.findViewById(R.id.chkApiGames);

        try {
            this.loadFromSettings(spApiFormat, spApiType);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        ImageButton cmdApi = root.findViewById(R.id.cmdApi);

        spApiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                formatAdapter.clear();
                if(spApiType.getSelectedItem().toString().equals(typeArray[0])) { // export
                    formatAdapter.add("pdf");
                }

                formatAdapter.add("txt");
                formatAdapter.add("csv");
                formatAdapter.add("db");

                hidePnlCells(spApiType.getSelectedItem().toString(), spApiFormat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spApiFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean first = spApiFormat.getSelectedItem().toString().equals(formatArray[0]);
                boolean csvOrTxt = spApiType.getSelectedItemPosition() == 0 || spApiType.getSelectedItemPosition() == 1;

                chkApiBooks.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiMovies.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiMusic.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiGames.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);

                hidePnlCells(spApiType.getSelectedItem().toString(), spApiFormat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        cmdApiPath.setOnClickListener(event -> {
            List<String> filter = new LinkedList<>();
            filter.add(spApiFormat.getSelectedItem().toString());
            boolean export = spApiType.getSelectedItem().toString().equals(this.typeArray[0]);

            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, export, filter, activity);
            dialog.setDialogSelectionListener(files -> {
                try {
                    if(files != null && files.length != 0) {
                        this.txtApiPath.setText(files[0]);

                        if(spApiFormat.getSelectedItem().toString().equals("csv") || spApiFormat.getSelectedItem().toString().equals("txt")) {
                            TextService textService = new TextService(files[0]);
                            this.fillPnlCells(textService.getHeader());
                        }
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                }
            });
            dialog.show();
        });

        cmdApi.setOnClickListener(event -> {
            try {
                if(spApiType.getSelectedItem().toString().equals(this.typeArray[0])) { // export
                    switch (spApiFormat.getSelectedItemPosition()) {
                        case 0:
                            this.exportToPDF();
                            break;
                        case 1:
                        case 2:

                            break;
                        case 3:
                            this.exportToDatabase();
                            break;
                    }
                    MessageHelper.printMessage(String.format(this.getString(R.string.sys_success), this.getString(R.string.api)), R.mipmap.ic_launcher_round, this.getActivity());
                    this.saveSettings(spApiFormat, spApiType);
                } else {
                    switch (spApiFormat.getSelectedItemPosition()) {
                        case 0:
                        case 1:
                            int selected = 0;
                            if (chkApiBooks.isChecked()) {
                                selected += 1;
                            }
                            if (chkApiMovies.isChecked()) {
                                selected += 1;
                            }
                            if (chkApiMusic.isChecked()) {
                                selected += 1;
                            }
                            if (chkApiGames.isChecked()) {
                                selected += 1;
                            }
                            if(selected == 1) {
                                ImportTask importTask = new ImportTask(
                                    MainApiFragment.this.getActivity(),
                                    this.txtApiPath.getText().toString(),
                                    this.pbProgress, this.chkApiBooks.isChecked(),
                                    this.chkApiMovies.isChecked(), this.chkApiMusic.isChecked(), this.mpCells);
                                importTask.after(new AbstractTask.PostExecuteListener() {
                                    @Override
                                    public void onPostExecute(Object o) {
                                        MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.api)), R.mipmap.ic_launcher_round, getActivity());
                                        saveSettings(spApiFormat, spApiType);
                                    }
                                });
                                importTask.execute();
                            }
                            break;
                        case 2:
                            this.importToDatabase();
                            MessageHelper.printMessage(String.format(this.getString(R.string.sys_success), this.getString(R.string.api)), R.mipmap.ic_launcher_round, this.getActivity());
                            this.saveSettings(spApiFormat, spApiType);
                            break;
                    }
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
            }
        });

        return root;
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    @Override
    public void reload(String search, boolean reload) {

    }

    @Override
    public void select() {

    }

    private void exportToPDF() throws ParseException {
        String path = this.txtApiPath.getText().toString() + File.separatorChar  + this.txtApiName.getText().toString() + ".pdf";
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        if(chkApiBooks.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getBooks(""));
        }
        if(chkApiMovies.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getMovies(""));
        }
        if(chkApiMusic.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getAlbums(""));
        }
        if(chkApiGames.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getGames(""));
        }
        PDFWriterHelper pdfWriterHelper = new PDFWriterHelper(path, Objects.requireNonNull(this.getActivity()));
        pdfWriterHelper.execute(baseMediaObjects);
    }

    private void exportToDatabase() throws Exception {
        String path = this.txtApiPath.getText().toString() + File.separatorChar  + this.txtApiName.getText().toString() + ".db";
        MainActivity.GLOBALS.getDatabase().copyDatabase(path);
    }

    private void importToDatabase() throws Exception {
        String path = this.txtApiPath.getText().toString();
        MainActivity.GLOBALS.getDatabase().getDatabase(path);
        MainApiFragment.triggerRebirth(Objects.requireNonNull(this.getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String path = this.txtApiPath.getText().toString();
            if(resultCode == RESULT_OK && requestCode == 965) {
                MainActivity.GLOBALS.getDatabase().getDatabase(path);
                MainApiFragment.triggerRebirth(Objects.requireNonNull(this.getContext()));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = Objects.requireNonNull(intent).getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @SuppressWarnings("unchecked")
    private void loadFromSettings(Spinner spFormat, Spinner spType) {
        Settings settings = MainActivity.GLOBALS.getSettings();
        this.chkApiBooks.setChecked(settings.getSetting(MainApiFragment.BOOKS, false));
        this.chkApiMovies.setChecked(settings.getSetting(MainApiFragment.MOVIES, false));
        this.chkApiMusic.setChecked(settings.getSetting(MainApiFragment.MUSIC, false));
        this.chkApiGames.setChecked(settings.getSetting(MainApiFragment.GAMES, false));

        this.txtApiName.setText(settings.getSetting(MainApiFragment.NAME, "export"));
        this.txtApiPath.setText(settings.getSetting(MainApiFragment.PATH, "export"));

        Adapter formatAdapter = spFormat.getAdapter();
        if(formatAdapter instanceof CustomSpinnerAdapter) {
            CustomSpinnerAdapter<String> spinnerAdapter = (CustomSpinnerAdapter<String>) formatAdapter;
            String format = settings.getSetting(MainApiFragment.FORMAT, this.formatArray[0]);
            spFormat.setSelection(spinnerAdapter.getPosition(format));
        }

        Adapter typeAdapter = spType.getAdapter();
        if(typeAdapter instanceof CustomSpinnerAdapter) {
            CustomSpinnerAdapter<String> spinnerAdapter = (CustomSpinnerAdapter<String>) typeAdapter;
            String type = settings.getSetting(MainApiFragment.TYPE, this.typeArray[0]);
            spType.setSelection(spinnerAdapter.getPosition(type));
        }
    }

    private void saveSettings(Spinner spFormat, Spinner spType) {
        Settings settings = MainActivity.GLOBALS.getSettings();
        settings.setSetting(MainApiFragment.BOOKS, this.chkApiBooks.isChecked());
        settings.setSetting(MainApiFragment.MOVIES, this.chkApiMovies.isChecked());
        settings.setSetting(MainApiFragment.MUSIC, this.chkApiMusic.isChecked());
        settings.setSetting(MainApiFragment.GAMES, this.chkApiGames.isChecked());

        settings.setSetting(MainApiFragment.PATH, this.txtApiPath.getText().toString());
        settings.setSetting(MainApiFragment.NAME, this.txtApiName.getText().toString());

        settings.setSetting(MainApiFragment.FORMAT, spFormat.getSelectedItem().toString());
        settings.setSetting(MainApiFragment.TYPE, spType.getSelectedItem().toString());
    }

    private void hidePnlCells(String type, String format) {
        if(type.equals(typeArray[0])) {
            this.tblCells.setVisibility(View.GONE);
        } else {
            if(format.equals("pdf") || format.equals("db")) {
                this.tblCells.setVisibility(View.GONE);
            } else {
                this.tblCells.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fillPnlCells(List<String> cells) {
        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(this.getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tblCells.removeViews(1, this.tblCells.getChildCount() - 1);
        this.mpCells.clear();
        for(String cell : cells) {
            TableRow tableRow = (TableRow) Objects.requireNonNull(inflater).inflate(R.layout.row_import, null);
            ((TextView) tableRow.getChildAt(0)).setText(cell);
            Spinner spinner = ((Spinner) tableRow.getChildAt(1));
            CustomSpinnerAdapter<String> adapter = this.reloadColumns();
            spinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            this.mpCells.put(cell, spinner);
            this.tblCells.addView(tableRow);
        }
    }

    private CustomSpinnerAdapter<String> reloadColumns() {
        CustomSpinnerAdapter<String> columns = new CustomSpinnerAdapter<>(Objects.requireNonNull(this.getActivity()));
        columns.clear();

        String[] cellArray = this.getResources().getStringArray(R.array.api_cells);
        columns.add("");
        for(int i = 0; i<=cellArray.length - 1 ; i++) {
            columns.add(cellArray[i]);
        }
        return columns;
    }

    private static class ImportTask extends AbstractTask<Void, Integer, Void> {
        private String path;
        private int max;
        private boolean books, movies, music;
        private WeakReference<ProgressBar> pbProgress;
        private Map<String, Spinner> cells;

        ImportTask(Activity activity, String path, ProgressBar pbProgress, boolean books, boolean movies, boolean music, Map<String, Spinner> cells) {
            super(
                activity,
                Objects.requireNonNull(activity).getString(R.string.settings_general_database_import),
                R.string.settings_general_database_import_summary,
                MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);

            this.path = path;
            this.pbProgress = new WeakReference<>(pbProgress);
            this.books = books;
            this.movies = movies;
            this.music = music;
            this.cells = cells;
        }

        @Override
        protected void before() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            this.pbProgress.get().setProgress((int) (values[0] / (this.max / 100.0)));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                this.importToTXTOrCSV();
            } catch (Exception ex) {
                this.printException(ex);
            }
            return null;
        }

        private void importToTXTOrCSV() throws Exception {
            TextService textService = new TextService(this.path);
            List<Map<String, String>> rows = textService.readFile();
            this.max = rows.size();

            int i = 0;
            for(Map<String, String> row : rows) {
                BaseMediaObject mediaObject;
                if(this.books) {
                    mediaObject = new Book();
                } else if(this.movies) {
                    mediaObject = new Movie();
                } else if(this.music) {
                    mediaObject = new Album();
                } else {
                    mediaObject = new Game();
                }

                for(Map.Entry<String, Spinner> entry : this.cells.entrySet()) {
                    if(entry.getValue() != null) {
                        if(entry.getValue().getSelectedItem() != null) {
                            if(!entry.getValue().getSelectedItem().toString().trim().isEmpty()) {
                                String dbColumn = entry.getValue().getSelectedItem().toString().trim();
                                String value = Objects.requireNonNull(row.get(entry.getKey())).trim();
                                this.setValueToObject(mediaObject, dbColumn, value, entry.getKey());
                            }
                        }
                    }
                }

                if(this.books) {
                    if(mediaObject instanceof Book) {
                        Book book = (Book) mediaObject;

                        try {
                            Book webServiceBook = null;

                            String code = book.getCode();
                            if(code.trim().isEmpty()) {
                                code = "";
                            } else if(code.contains(",")) {
                                code = code.split(",")[0].trim();
                            } else if(code.contains(";")) {
                                code = code.split(";")[0].trim();
                            } else {
                                code = code.trim();
                            }

                            if(code.isEmpty()) {
                                GoogleBooksWebservice googleBooksWebservice = new GoogleBooksWebservice(this.getContext(), "", "", "");
                                List<BaseMediaObject> baseMediaObjects = googleBooksWebservice.getMedia(book.getTitle());
                                if(!baseMediaObjects.isEmpty()) {
                                    googleBooksWebservice = new GoogleBooksWebservice(this.getContext(), "", baseMediaObjects.get(0).getDescription(), "");
                                    webServiceBook = googleBooksWebservice.execute();
                                }
                            } else {
                                GoogleBooksWebservice googleBooksWebservice = new GoogleBooksWebservice(this.getContext(), code, "", "");
                                webServiceBook = googleBooksWebservice.execute();
                            }

                            if(webServiceBook != null) {
                                this.mergeDataFromWebservice(book, webServiceBook);
                            }
                        } catch (Exception ignored) {}

                        MainActivity.GLOBALS.getDatabase().insertOrUpdateBook(book);
                    }
                } else if(this.movies) {
                    if(mediaObject instanceof Movie) {
                        Movie movie = (Movie) mediaObject;

                        try {
                            Movie webServiceMovie = null;

                            MovieDBWebservice webservice = new MovieDBWebservice(this.getContext(), 0, "", "");
                            List<BaseMediaObject> baseMediaObjects = webservice.getMedia(movie.getTitle());
                            if(!baseMediaObjects.isEmpty()) {
                                webservice =  new MovieDBWebservice(this.getContext(),  baseMediaObjects.get(0).getId(), baseMediaObjects.get(0).getDescription(), "");
                                webServiceMovie = webservice.execute();
                            }

                            if(webServiceMovie != null) {
                                this.mergeDataFromWebservice(movie, webServiceMovie);
                            }
                        } catch (Exception ignored) {}

                        MainActivity.GLOBALS.getDatabase().insertOrUpdateMovie(movie);
                    }
                } else if(this.music) {
                    if(mediaObject instanceof Album) {
                        Album album = (Album) mediaObject;

                        try {
                            Album webServiceAlbum = null;

                            AudioDBWebservice webservice = new AudioDBWebservice(this.getContext(), 0);
                            List<BaseMediaObject> baseMediaObjects = webservice.getMedia(album.getTitle());
                            if(!baseMediaObjects.isEmpty()) {
                                webservice =  new AudioDBWebservice(this.getContext(),  baseMediaObjects.get(0).getId());
                                webServiceAlbum = webservice.execute();
                            }

                            if(webServiceAlbum != null) {
                                this.mergeDataFromWebservice(album, webServiceAlbum);
                            }
                        } catch (Exception ignored) {}

                        MainActivity.GLOBALS.getDatabase().insertOrUpdateAlbum(album);
                    }
                } else {
                    if(mediaObject instanceof Game) {
                        Game game = (Game) mediaObject;

                        try {
                            Game webServiceGame = null;

                            IGDBWebservice webservice = new IGDBWebservice(this.getContext(), 0, "");
                            List<BaseMediaObject> baseMediaObjects = webservice.getMedia(game.getTitle());
                            if(!baseMediaObjects.isEmpty()) {
                                webservice =  new IGDBWebservice(this.getContext(),  baseMediaObjects.get(0).getId(), "");
                                webServiceGame = webservice.execute();
                            }

                            if(webServiceGame != null) {
                                this.mergeDataFromWebservice(game, webServiceGame);
                            }
                        } catch (Exception ignored) {}

                        MainActivity.GLOBALS.getDatabase().insertOrUpdateGame(game);
                    }
                }
                publishProgress(++i);
            }
        }

        private void mergeDataFromWebservice(BaseMediaObject importedObject, BaseMediaObject webServiceObject) {
            if(importedObject.getTitle().trim().isEmpty()) {
                importedObject.setTitle(webServiceObject.getTitle().trim());
            }
            if(importedObject.getOriginalTitle().trim().isEmpty()) {
                importedObject.setOriginalTitle(webServiceObject.getOriginalTitle().trim());
            }
            if(importedObject.getReleaseDate() == null) {
                importedObject.setReleaseDate(webServiceObject.getReleaseDate());
            }
            if(importedObject.getPrice() == 0.0) {
                importedObject.setPrice(webServiceObject.getPrice());
            }
            if(importedObject.getCategory() == null) {
                importedObject.setCategory(webServiceObject.getCategory());
            }
            if(importedObject.getRatingOwn() == 0.0) {
                importedObject.setRatingOwn(webServiceObject.getRatingOwn());
            }
            if(importedObject.getRatingWeb() == 0.0) {
                importedObject.setRatingWeb(webServiceObject.getRatingWeb());
            }
            if(importedObject.getDescription().trim().isEmpty()) {
                if(webServiceObject.getDescription() != null) {
                    importedObject.setDescription(webServiceObject.getDescription().trim());
                }
            }
            importedObject.setCover(webServiceObject.getCover());

            if(importedObject instanceof Book) {
                Book importedBook = (Book) importedObject;
                Book webServiceBook = (Book) webServiceObject;

                if(importedBook.getNumberOfPages() == 0) {
                    importedBook.setNumberOfPages(webServiceBook.getNumberOfPages());
                }
                if(importedBook.getTopics().isEmpty()) {
                    importedBook.setTopics(webServiceBook.getTopics());
                }
            }
            if(importedObject instanceof Movie) {
                Movie importedMovie = (Movie) importedObject;
                Movie webServiceMovie = (Movie) webServiceObject;

                if(importedMovie.getLength() == 0.0) {
                    importedMovie.setLength(webServiceMovie.getLength());
                }
            }
            if(importedObject instanceof Album) {
                Album importedAlbum = (Album) importedObject;
                Album webServiceAlbum = (Album) webServiceObject;

                if(importedAlbum.getNumberOfDisks() == 0) {
                    importedAlbum.setNumberOfDisks(webServiceAlbum.getNumberOfDisks());
                }
            }
            if(importedObject instanceof Game) {
                Game importedGame = (Game) importedObject;
                Game webServiceGame = (Game) webServiceObject;

                if(importedGame.getLength() == 0.0) {
                    importedGame.setLength(webServiceGame.getLength());
                }
            }
        }

        private void setValueToObject(BaseMediaObject mediaObject, String column, String value, String cell) throws Exception {
            Map<String, Integer> columns = new LinkedHashMap<>();
            int i = 0;
            for(String item : this.getContext().getResources().getStringArray(R.array.api_cells)) {
                columns.put(item, i);
                i++;
            }

            switch (Objects.requireNonNull(columns.get(column))) {
                case 0:
                    mediaObject.setTitle(value);
                    break;
                case 1:
                    mediaObject.setOriginalTitle(value);
                    break;
                case 2:
                    Date release = ConvertHelper.convertStringToDate(value, this.getContext().getString(R.string.sys_date_format));
                    mediaObject.setReleaseDate(release);
                    break;
                case 3:
                    mediaObject.setPrice(Double.parseDouble(value));
                    break;
                case 4:
                    mediaObject.setCode(value);
                    break;
                case 5:
                    BaseDescriptionObject categoryObject = new BaseDescriptionObject();
                    categoryObject.setTitle(value);
                    mediaObject.setCategory(categoryObject);
                    break;
                case 6:
                    for(String tag : this.convertStringToList(value)) {
                        BaseDescriptionObject tagObject = new BaseDescriptionObject();
                        tagObject.setTitle(tag);
                        mediaObject.getTags().add(tagObject);
                    }
                    break;
                case 7:
                    for(String item : this.convertStringToList(value)) {
                        Person person = new Person();
                        String[] spl = item.split(" ");
                        if(spl.length == 1) {
                            person.setLastName(spl[0].trim());
                        } else {
                            person.setFirstName(spl[0].trim());
                            person.setLastName(item.replace(person.getFirstName(), "").trim());
                        }
                        mediaObject.getPersons().add(person);
                    }
                    break;
                case 8:
                    for(String item : this.convertStringToList(value)) {
                        Company company = new Company();
                        company.setTitle(item);
                        mediaObject.getCompanies().add(company);
                    }
                    break;
                case 9:
                    mediaObject.setRatingOwn(Double.parseDouble(value));
                    break;
                case 10:
                    mediaObject.setRatingWeb(Double.parseDouble(value));
                    break;
                case 11:
                    mediaObject.setDescription(value);
                    break;
                case 12:
                    if(mediaObject instanceof Book) {
                        ((Book) mediaObject).setEdition(value);
                    }
                    break;
                case 13:
                    if(mediaObject instanceof Book) {
                        ((Book) mediaObject).setNumberOfPages(Integer.parseInt(value));
                    }
                    break;
                case 14:
                    if(mediaObject instanceof Book) {
                        for(String row : value.split("\n")) {
                            ((Book) mediaObject).getTopics().add(row.trim());
                        }
                    }
                    break;
                case 15:
                    if(mediaObject instanceof Movie) {
                        ((Movie) mediaObject).setLength(Double.parseDouble(value));
                    }
                    if(mediaObject instanceof Game) {
                        ((Game) mediaObject).setLength(Double.parseDouble(value));
                    }
                    break;
                case 16:
                    if(mediaObject instanceof Album) {
                        ((Album) mediaObject).setNumberOfDisks(Integer.parseInt(value));
                    }
                    break;
                case 17:
                    List<CustomField> customFields = MainActivity.GLOBALS.getDatabase().getCustomFields("title='" + cell + "'");
                    CustomField customField = null;
                    if(customFields != null) {
                        if(!customFields.isEmpty()) {
                            customField = customFields.get(0);
                        }
                    }

                    if(customField == null) {
                        customField = new CustomField();
                        customField.setTitle(cell);
                        customField.setAlbums(true);
                        customField.setBooks(true);
                        customField.setMovies(true);
                        customField.setGames(true);
                        customField.setType(this.getContext().getString(R.string.customFields_type_values_text));
                        customField.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateCustomField(customField));
                    }
                    mediaObject.getCustomFieldValues().put(customField, value);
                    break;
            }
        }

        private List<String> convertStringToList(String value) {
            List<String> list = new LinkedList<>();
            if(value.contains(",")) {
                list.addAll(Arrays.asList(value.split(",")));
            } else if(value.contains(";")) {
                list.addAll(Arrays.asList(value.split(";")));
            }
            return list;
        }
    }
}
