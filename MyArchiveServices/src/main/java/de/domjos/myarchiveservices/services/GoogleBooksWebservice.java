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

package de.domjos.myarchiveservices.services;

import android.content.Context;
import android.util.Log;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.media.AbstractMedia;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchiveservices.R;

public class GoogleBooksWebservice extends TitleWebservice<Book> {
    private final static String HTTP = "http://", HTTPS = "https://";

    private final String code;
    private final String id;
    private final String key;

    public GoogleBooksWebservice(Context context, String code, String id, String key) {
        super(context, 0L);
        this.code = code;
        this.id = id;
        this.key = key;
    }

    public Book execute() throws IOException {
        Books books = new Books.Builder(new NetHttpTransport(), new GsonFactory(), null).setApplicationName("MyArchive").build();
        Books.Volumes.List list;
        if(!this.code.isEmpty()) {
            list = books.volumes().list("isbn:" + this.code).setProjection("full");
            if(!this.key.isEmpty()) {
                list = list.setKey(this.key);
            }
            if(list != null) {
                List<Volume> volumes = list.execute().getItems();
                if(volumes != null && !volumes.isEmpty()) {
                    return this.getBookFromList(volumes.get(0));
                }
            }
        } else {
            return this.getBookFromList(books.volumes().get(this.id).execute());
        }
        return null;
    }

    public List<AbstractMedia> getMedia(String search) throws IOException {
        List<AbstractMedia> baseMediaObjects = new LinkedList<>();
        Books books = new Books.Builder(new NetHttpTransport(), new GsonFactory(), null).setApplicationName("MyArchive").build();
        Books.Volumes.List list = books.volumes().list("intitle:" + search).setProjection("full");
        if(!this.key.isEmpty()) {
            list.setKey(this.key);
        }
        if(list != null) {
            List<Volume> volumes = list.execute().getItems();
            if(volumes != null && !volumes.isEmpty()) {
                for(Volume volume : volumes) {
                    Book book = new Book();
                    book.setDescription(volume.getId());
                    if(volume.getVolumeInfo() != null) {
                        book.setTitle(volume.getVolumeInfo().getTitle());
                        try {
                            book.setReleaseDate(getReleaseDate(volume.getVolumeInfo().getPublishedDate()));
                        } catch (ParseException e) {
                            Log.e("Error", e.toString());
                        }
                        setCover(volume.getVolumeInfo(), book);
                    }
                    baseMediaObjects.add(book);
                }
            }
        }
        return baseMediaObjects;
    }

    @Override
    public String getTitle() {
        return super.CONTEXT.getString(R.string.service_google_search);
    }

    @Override
    public String getType() {
        return super.CONTEXT.getString(R.string.service_type_books);
    }

    @Override
    public String getUrl() {
        return "https://books.google.com";
    }

    private Book getBookFromList(Volume volume) {
        Book book = new Book();
        if(volume.getVolumeInfo()!=null) {
            Volume.VolumeInfo info = volume.getVolumeInfo();
            book.setTitle(info.getTitle());
            book.setOriginalTitle(info.getSubtitle());
            if (info.getPageCount() != null) {
                book.setNumberOfPages(info.getPageCount());
            }
            book.setDescription(info.getDescription());
            try {
                book.setReleaseDate(getReleaseDate(info.getPublishedDate()));
            } catch (ParseException ex) {
                Log.e("Error", ex.toString());
            }
            if(info.getAverageRating() != null) {
                if(info.getAverageRating() != 0) {
                    book.setRatingWeb(info.getAverageRating() * 2);
                } else {
                    book.setRatingWeb(0.0);
                }
            } else {
                book.setRatingWeb(0.0);
            }

            if (info.getAuthors() != null && !info.getAuthors().isEmpty()) {
                for (String author : info.getAuthors()) {
                    Person person = new Person();
                    String firstName = author.split(" ")[0];
                    person.setFirstName(firstName);
                    person.setLastName(author.replace(firstName, "").trim());
                    book.getPersons().add(person);
                }
            }

            if (info.getPublisher() != null && !info.getPublisher().isEmpty()) {
                Company company = new Company();
                company.setTitle(info.getPublisher());
                book.getCompanies().add(company);
            }

            if (info.getCategories() != null && !info.getCategories().isEmpty()) {
                for (String category : info.getCategories()) {
                    Tag baseDescriptionObject = new Tag();
                    baseDescriptionObject.setTitle(category);
                    book.getTags().add(baseDescriptionObject);
                }
            }
            setCover(info, book);
        }
        return book;
    }

    private void setCover(Volume.VolumeInfo info, Book book) {
        if(info.getImageLinks() != null) {
            Volume.VolumeInfo.ImageLinks imageLinks = info.getImageLinks();
            addCover(book, imageLinks.getSmall());
            addCover(book, imageLinks.getThumbnail());
            addCover(book, imageLinks.getSmallThumbnail());
            addCover(book, imageLinks.getMedium());
            addCover(book, imageLinks.getLarge());
            addCover(book, imageLinks.getExtraLarge());
        }
    }

    private void addCover(Book book, String link) {
        if(book.getCover() == null && link != null && !link.isEmpty()) {
            book.setCover(setCover(link.replace(GoogleBooksWebservice.HTTP, GoogleBooksWebservice.HTTPS), CONTEXT));
        }
    }

    private static Date getReleaseDate(String date) throws ParseException {
        Date dt = null;
        if(date != null && !date.isEmpty()) {
            if(date.contains("-")) {
                String[] spl = date.split("-");
                if(spl.length == 2) {
                    dt = ConvertHelper.convertStringToDate(date + "-01", "yyyy-MM-dd");
                } else {
                    dt = ConvertHelper.convertStringToDate(date, "yyyy-MM-dd");
                }
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.YEAR, Integer.parseInt(date));
                dt = calendar.getTime();
            }
        }
        return dt;
    }
}

