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


package de.domjos.myarchivemobile.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.domjos.customwidgets.model.tasks.TaskStatus;
import de.domjos.customwidgets.utils.ConvertHelper;
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
import de.domjos.customwidgets.model.tasks.StatusTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class ImportTask extends StatusTask<Void, Void> {
    private String path;
    private boolean books, movies, music, webservice;
    private Map<String, Spinner> cells;
    private WeakReference<TextView> lblState;

    public ImportTask(Activity activity, String path, ProgressBar pbProgress, TextView lblState, TextView lblMessage, boolean books, boolean movies, boolean music, boolean webservice, Map<String, Spinner> cells) {
        super(
                activity,
                R.string.api_task_import,
                R.string.api_task_import_content,
                MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, pbProgress, lblMessage);

        this.path = path;
        this.books = books;
        this.movies = movies;
        this.music = music;
        this.webservice = webservice;
        this.cells = cells;
        this.lblState = new WeakReference<>(lblState);
    }

    @Override
    protected final void onProgressUpdate(TaskStatus... values) {
        super.onProgressUpdate(values);
        this.lblState.get().setText(String.format("%s / %s", this.max, values[0].getStatus()));
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
            publishProgress(new TaskStatus(i, this.getContext().getString(R.string.api_task_import_data)));
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

            publishProgress(new TaskStatus(i, this.getContext().getString(R.string.api_task_import_webservice)));
            if(this.books) {
                if(mediaObject instanceof Book) {
                    Book book = (Book) mediaObject;

                    try {
                        if(this.webservice) {
                            Book webServiceBook;

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
                                webServiceBook = this.searchBookByTitle(book, null);
                            } else {
                                GoogleBooksWebservice googleBooksWebservice = new GoogleBooksWebservice(this.getContext(), code, "", "");
                                webServiceBook = googleBooksWebservice.execute();
                                if(webServiceBook == null) {
                                    webServiceBook = this.searchBookByTitle(book, null);
                                } else {
                                    if(webServiceBook.getCover() != null) {
                                        webServiceBook = this.searchBookByTitle(book, webServiceBook);
                                    }
                                }
                            }

                            if(webServiceBook != null) {
                                this.mergeDataFromWebservice(book, webServiceBook);
                            }
                        }
                    } catch (Exception ignored) {}

                    MainActivity.GLOBALS.getDatabase().insertOrUpdateBook(book);
                }
            } else if(this.movies) {
                if(mediaObject instanceof Movie) {
                    Movie movie = (Movie) mediaObject;

                    try {
                        if(this.webservice) {
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
                        }
                    } catch (Exception ignored) {}

                    MainActivity.GLOBALS.getDatabase().insertOrUpdateMovie(movie);
                }
            } else if(this.music) {
                if(mediaObject instanceof Album) {
                    Album album = (Album) mediaObject;

                    try {
                        if(this.webservice) {
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
                        }
                    } catch (Exception ignored) {}

                    MainActivity.GLOBALS.getDatabase().insertOrUpdateAlbum(album);
                }
            } else {
                if(mediaObject instanceof Game) {
                    Game game = (Game) mediaObject;

                    try {
                        if(this.webservice) {
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
                        }
                    } catch (Exception ignored) {}

                    MainActivity.GLOBALS.getDatabase().insertOrUpdateGame(game);
                }
            }
            i++;
        }
    }

    private Book searchBookByTitle(Book book, Book webServiceBook) throws Exception {
        GoogleBooksWebservice googleBooksWebservice = new GoogleBooksWebservice(this.getContext(), "", "", "");
        List<BaseMediaObject> baseMediaObjects = googleBooksWebservice.getMedia(book.getTitle());
        if(!baseMediaObjects.isEmpty()) {
            googleBooksWebservice = new GoogleBooksWebservice(this.getContext(), "", baseMediaObjects.get(0).getDescription(), "");
            webServiceBook = googleBooksWebservice.execute();
        }
        return webServiceBook;
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

    private String validateCode(String content) {
        if(content == null) {
            return "";
        }

        if(content.trim().isEmpty()) {
            return "";
        }

        content = content.trim();
        if(content.contains(",")) {
            String[] codes = content.split(",");
            if(codes.length >= 1) {
                return codes[0];
            }
        }

        if(content.contains(";")) {
            String[] codes = content.split(";");
            if(codes.length >= 1) {
                return codes[0];
            }
        }
        if(!content.trim().isEmpty()) {
            return content.trim();
        }

        return "";
    }

    private Date validateReleaseDate(String  content) {
        try {
            if(content == null) {
                return null;
            }

            if(content.trim().isEmpty()) {
                return null;
            }

            content = content.trim();
            if(content.contains(".")) {
                int number = this.countChar('.', content);
                if(number == 2) {
                    int index = content.indexOf(".");
                    if(index == 2) {
                        return ConvertHelper.convertStringToDate(content, "dd.MM.yyyy");
                    } else if(index == 4) {
                        return ConvertHelper.convertStringToDate(content, "yyyy.MM.dd");
                    } else {
                        return null;
                    }
                } else if(number == 1) {
                    int index = content.indexOf(".");
                    if(index == 2) {
                        return ConvertHelper.convertStringToDate(content, "MM.yyyy");
                    } else if(index == 4) {
                        return ConvertHelper.convertStringToDate(content, "yyyy.MM");
                    } else {
                        return null;
                    }
                } else if(this.isInt(content)) {
                    if(content.length() == 4) {
                        return ConvertHelper.convertStringToDate(content, "yyyy");
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            content = content.trim();
            if(content.contains("-")) {
                int number = this.countChar('-', content);
                if(number == 2) {
                    int index = content.indexOf("-");
                    if(index == 2) {
                        return ConvertHelper.convertStringToDate(content, "dd-MM-yyyy");
                    } else if(index == 4) {
                        return ConvertHelper.convertStringToDate(content, "yyyy-MM-dd");
                    } else {
                        return null;
                    }
                } else if(number == 1) {
                    int index = content.indexOf(".");
                    if(index == 2) {
                        return ConvertHelper.convertStringToDate(content, "MM-yyyy");
                    } else if(index == 4) {
                        return ConvertHelper.convertStringToDate(content, "yyyy-MM");
                    } else {
                        return null;
                    }
                } else if(this.isInt(content)) {
                    if(content.length() == 4) {
                        return ConvertHelper.convertStringToDate(content, "yyyy");
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if(this.isInt(content)) {
                if(content.length() == 4) {
                    return ConvertHelper.convertStringToDate(content, "yyyy");
                } else {
                    return null;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private int countChar(char ch, String content) {
        Pattern pattern = Pattern.compile("[^" + ch + "]*" + ch);
        Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private boolean isInt(String content) {
        try {
            Integer.parseInt(content);
            return true;
        } catch (Exception ignored) {}
        return false;
    }

    private void setValueToObject(BaseMediaObject mediaObject, String column, String value, String cell) {
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
                mediaObject.setReleaseDate(this.validateReleaseDate(value));
                break;
            case 3:
                mediaObject.setPrice(Double.parseDouble(value));
                break;
            case 4:
                mediaObject.setCode(this.validateCode(value));
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
