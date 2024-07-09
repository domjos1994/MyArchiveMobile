/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
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

package de.domjos.myarchivemobile.helper;

import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.PDFService;
import de.domjos.myarchivemobile.R;

public class PDFWriterHelper {
    private final PDFService pdfService;
    private final Context context;

    private final int headerColor, rowColor;

    public PDFWriterHelper(String file, Context context) {
        this.context = context;
        this.headerColor = context.getColor(R.color.colorPrimaryDark);
        this.rowColor = context.getColor(R.color.colorPrimary);

        this.pdfService = new PDFService(file, R.mipmap.ic_launcher_round, context);

        byte[] icon = ConvertHelper.convertDrawableToByteArray(this.context, R.mipmap.ic_launcher);
        this.pdfService.addHeadPage(icon, this.context.getString(R.string.main_navigation_media), this.context.getString(R.string.app_name));
    }

    public void addRow(BaseMediaObject baseMediaObject) {
        this.addMediaObject(baseMediaObject);

        if(baseMediaObject instanceof Book) {
            this.addBook((Book) baseMediaObject);
        }
        if(baseMediaObject instanceof Movie) {
            this.addMovie((Movie) baseMediaObject);
        }
        if(baseMediaObject instanceof Game) {
            this.addGame((Game) baseMediaObject);
        }
        if(baseMediaObject instanceof Album) {
            this.addAlbum((Album) baseMediaObject);
        }

        this.addPersons(baseMediaObject.getPersons());
        this.addCompanies(baseMediaObject.getCompanies());
        this.pdfService.newPage();
    }

    public void close() {
        this.pdfService.close();
    }

    private void addMediaObject(BaseMediaObject baseMediaObject) {
        this.pdfService.addParagraph(baseMediaObject.getTitle(), PDFService.H4, PDFService.CENTER, 20f);

        if(baseMediaObject.getCover() != null) {
            this.pdfService.addImage(baseMediaObject.getCover(), PDFService.CENTER, 256f, 256f, 10f);
        }

        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.media_general), 200.0f);
        columns.put("", 400.0f);

        List<List<String>> rows = new LinkedList<>();
        if(!baseMediaObject.getOriginalTitle().trim().isEmpty()) {
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_originalTitle), baseMediaObject.getOriginalTitle()));
        }
        if(baseMediaObject.getReleaseDate() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_releaseDate), ConvertHelper.convertDateToString(baseMediaObject.getReleaseDate(), this.context.getString(R.string.sys_date_format))));
        }
        if(baseMediaObject.getPrice() != 0.0) {
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_price), String.valueOf(baseMediaObject.getPrice())));
        }
        if(!baseMediaObject.getCode().trim().isEmpty()) {
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_code), baseMediaObject.getCode()));
        }
        if(baseMediaObject.getCategory() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_category), baseMediaObject.getCategory().getTitle()));
        }
        if(baseMediaObject.getTags() != null && !baseMediaObject.getTags().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for(BaseDescriptionObject baseDescriptionObject : baseMediaObject.getTags()) {
                stringBuilder.append(baseDescriptionObject.getTitle()).append(", ");
            }
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_tags), stringBuilder.toString()));
        }
        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10);
    }

    private void addBook(Book book) {
        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.book), 200.0f);
        columns.put("", 400.0f);

        List<List<String>> rows = new LinkedList<>();
        if(book.getType() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.book_type), book.getType().toString()));
        }
        if(!book.getEdition().trim().isEmpty()) {
            rows.add(Arrays.asList(this.context.getString(R.string.book_edition), book.getEdition()));
        }
        if(!book.getTopics().isEmpty()) {
            rows.add(Arrays.asList(this.context.getString(R.string.book_topics), TextUtils.join(",", book.getTopics())));
        }
        if(book.getNumberOfPages() != 0) {
            rows.add(Arrays.asList(this.context.getString(R.string.book_numberOfPages), String.valueOf(book.getNumberOfPages())));
        }
        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10);
    }

    private void addMovie(Movie movie) {
        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.movie), 200.0f);
        columns.put("", 400.0f);

        List<List<String>> rows = new LinkedList<>();
        if(movie.getType() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.movie_type), movie.getType().toString()));
        }
        if(movie.getLength() != 0.0) {
            rows.add(Arrays.asList(this.context.getString(R.string.movie_length), String.valueOf(movie.getLength())));
        }
        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10);
    }

    private void addGame(Game game) {
        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.game), 200.0f);
        columns.put("", 400.0f);

        List<List<String>> rows = new LinkedList<>();
        if(game.getType() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.movie_type), game.getType().toString()));
        }
        if(game.getLength() != 0.0) {
            rows.add(Arrays.asList(this.context.getString(R.string.movie_length), String.valueOf(game.getLength())));
        }
        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10);
    }

    private void addAlbum(Album album) {
        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.album), 200.0f);
        columns.put("", 400.0f);

        List<List<String>> rows = new LinkedList<>();
        if(album.getType() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.movie_type), album.getType().toString()));
        }
        if(album.getNumberOfDisks() != 0.0) {
            rows.add(Arrays.asList(this.context.getString(R.string.movie_length), String.valueOf(album.getNumberOfDisks())));
        }
        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10);
    }

    private void addPersons(List<Person> people) {
        if(!people.isEmpty()) {
            Map<String, Float> columns = new LinkedHashMap<>();
            columns.put(this.context.getString(R.string.media_persons_firstName), 150.0f);
            columns.put(this.context.getString(R.string.media_persons_lastName), 150.0f);
            columns.put(this.context.getString(R.string.media_persons_birthDate), 150.0f);

            List<List<String>> rows = new LinkedList<>();
            for(Person person : people) {
                String birthDate = "";
                if(person.getBirthDate() != null) {
                    birthDate = ConvertHelper.convertDateToString(person.getBirthDate(), this.context.getString(R.string.sys_date_format));
                }

                rows.add(Arrays.asList(person.getFirstName(), person.getLastName(), birthDate));
            }

            this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10f);
        }
    }

    private void addCompanies(List<Company> companies) {
        if(!companies.isEmpty()) {
            Map<String, Float> columns = new LinkedHashMap<>();
            columns.put(this.context.getString(R.string.sys_title), 150.0f);
            columns.put(this.context.getString(R.string.media_companies_foundation), 150.0f);

            List<List<String>> rows = new LinkedList<>();
            for(Company company : companies) {
                String foundation = "";
                if(company.getFoundation() != null) {
                    foundation = ConvertHelper.convertDateToString(company.getFoundation(), this.context.getString(R.string.sys_date_format));
                }

                rows.add(Arrays.asList(company.getTitle(), foundation));
            }

            this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor, 10f);
        }
    }
}
