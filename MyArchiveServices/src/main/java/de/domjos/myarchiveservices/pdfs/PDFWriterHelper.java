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

package de.domjos.myarchiveservices.pdfs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivedatabase.converter.BitmapConverter;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.media.AbstractMedia;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchiveservices.services.PDFService;
import de.domjos.myarchiveservices.R;

public class PDFWriterHelper {
    private final PDFService pdfService;
    private final Context context;

    private final int headerColor, rowColor;

    public PDFWriterHelper(String file, Context context, int icon_id) {
        this.context = context;
        this.headerColor = context.getColor(R.color.colorPrimaryDark);
        this.rowColor = context.getColor(R.color.colorPrimary);

        this.pdfService = new PDFService(file, icon_id, context);

        byte[] icon = ConvertHelper.convertDrawableToByteArray(this.context, icon_id);
        this.pdfService.addHeadPage(icon, this.context.getString(R.string.main_navigation_media), this.context.getString(R.string.app_name));
    }

    public void addRow(AbstractMedia baseMediaObject) {
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

    private void addMediaObject(AbstractMedia baseMediaObject) {
        this.pdfService.addParagraph(baseMediaObject.getTitle(), PDFService.H4, PDFService.CENTER, 20f);

        if(baseMediaObject.getCover() != null) {
            Drawable drawable = baseMediaObject.getCover();
            Bitmap bitmap;

            if(drawable == null) {
                return;
            }

            if (drawable instanceof BitmapDrawable bitmapDrawable) {
                if(bitmapDrawable.getBitmap() != null) {
                    this.pdfService.addImage(BitmapConverter.toByte(bitmapDrawable.getBitmap()), PDFService.CENTER, 256f, 256f, 10f);
                }
            } else {
                if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                    bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                }

                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                this.pdfService.addImage(BitmapConverter.toByte(bitmap), PDFService.CENTER, 256f, 256f, 10f);
            }
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
        if(baseMediaObject.getCategoryItem() != null) {
            rows.add(Arrays.asList(this.context.getString(R.string.media_general_category), baseMediaObject.getCategoryItem().getTitle()));
        }
        if(baseMediaObject.getTags() != null && !baseMediaObject.getTags().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for(Tag tag : baseMediaObject.getTags()) {
                stringBuilder.append(tag.getTitle()).append(", ");
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
            rows.add(Arrays.asList(this.context.getString(R.string.book_type), book.getType()));
        }
        if(!book.getEdition().trim().isEmpty()) {
            rows.add(Arrays.asList(this.context.getString(R.string.book_edition), book.getEdition()));
        }
        if(!book.getTopics().isEmpty()) {
            rows.add(Arrays.asList(this.context.getString(R.string.book_topics), TextUtils.join(",", Collections.singleton(book.getTopics()))));
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
            rows.add(Arrays.asList(this.context.getString(R.string.movie_type), movie.getType()));
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
            rows.add(Arrays.asList(this.context.getString(R.string.movie_type), game.getType()));
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
            rows.add(Arrays.asList(this.context.getString(R.string.movie_type), album.getType()));
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
