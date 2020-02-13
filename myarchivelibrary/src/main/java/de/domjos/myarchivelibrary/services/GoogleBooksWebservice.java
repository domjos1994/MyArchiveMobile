package de.domjos.myarchivelibrary.services;

import android.content.Context;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;

public class GoogleBooksWebservice extends TitleWebservice<Book> {
    private String code;
    private String id;
    private String key;

    public GoogleBooksWebservice(Context context, String code, String id, String key) {
        super(context, 0L);
        this.code = code;
        this.id = id;
        this.key = key;
    }

    public Book execute() throws IOException {
        Books books = new Books.Builder(new NetHttpTransport(), AndroidJsonFactory.getDefaultInstance(), null).setApplicationName("MyArchive").build();
        Books.Volumes.List list;
        if(!this.code.isEmpty()) {
            list = books.volumes().list("isbn:" + this.code).setProjection("full");
            if(!this.key.isEmpty()) {
                list = list.setKey(this.key);
            }
            if(list != null) {
                List<Volume> volumes = list.execute().getItems();
                if(volumes != null) {
                    if(!volumes.isEmpty()) {
                        return this.getBookFromList(volumes.get(0));
                    }
                }
            }
        } else {
            return this.getBookFromList(books.volumes().get(this.id).execute());
        }
        return null;
    }

    public List<BaseMediaObject> getMedia(String search) throws IOException {
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        Books books = new Books.Builder(new NetHttpTransport(), AndroidJsonFactory.getDefaultInstance(), null).setApplicationName("MyArchive").build();
        Books.Volumes.List list = books.volumes().list("intitle:" + search).setProjection("full");
        if(!this.key.isEmpty()) {
            list.setKey(this.key);
        }
        if(list != null) {
            List<Volume> volumes = list.execute().getItems();
            if(volumes != null) {
                if(!volumes.isEmpty()) {
                    for(Volume volume : volumes) {
                        Book book = new Book();
                        book.setDescription(volume.getId());
                        if(volume.getVolumeInfo() != null) {
                            book.setTitle(volume.getVolumeInfo().getTitle());
                            book.setReleaseDate(getReleaseDate(volume.getVolumeInfo().getPublishedDate()));
                            setCover(volume.getVolumeInfo(), book);
                        }
                        baseMediaObjects.add(book);
                    }
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
            book.setReleaseDate(getReleaseDate(info.getPublishedDate()));
            if(info.getAverageRating() != null) {
                if(info.getAverageRating() != 0) {
                    book.setRatingWeb(info.getAverageRating() * 2);
                } else {
                    book.setRatingWeb(0.0);
                }
            } else {
                book.setRatingWeb(0.0);
            }

            if (info.getAuthors() != null) {
                if (!info.getAuthors().isEmpty()) {
                    for (String author : info.getAuthors()) {
                        Person person = new Person();
                        String firstName = author.split(" ")[0];
                        person.setFirstName(firstName);
                        person.setLastName(author.replace(firstName, "").trim());
                        book.getPersons().add(person);
                    }
                }
            }

            if (info.getPublisher() != null) {
                if (!info.getPublisher().isEmpty()) {
                    Company company = new Company();
                    company.setTitle(info.getPublisher());
                    book.getCompanies().add(company);
                }
            }

            if (info.getCategories() != null) {
                if (!info.getCategories().isEmpty()) {
                    for (String category : info.getCategories()) {
                        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                        baseDescriptionObject.setTitle(category);
                        book.getTags().add(baseDescriptionObject);
                    }
                }
            }
            setCover(info, book);
        }
        return book;
    }

    private static void setCover(Volume.VolumeInfo info, Book book) {
        if(info.getImageLinks() != null) {
            Volume.VolumeInfo.ImageLinks imageLinks = info.getImageLinks();

            if(imageLinks.getLarge() != null) {
                if(!imageLinks.getLarge().isEmpty()) {
                    book.setCover(Converter.convertStringToByteArray(imageLinks.getLarge().replace("http://", "https://")));
                }
            }

            if(book.getCover() == null) {
                if(imageLinks.getMedium() != null) {
                    if(!imageLinks.getMedium().isEmpty()) {
                        book.setCover(Converter.convertStringToByteArray(imageLinks.getMedium().replace("http://", "https://")));
                    }
                }
            }

            if(book.getCover() == null) {
                if(imageLinks.getSmall() != null) {
                    if(!imageLinks.getSmall().isEmpty()) {
                        book.setCover(Converter.convertStringToByteArray(imageLinks.getSmall().replace("http://", "https://")));
                    }
                }
            }

            if(book.getCover() == null) {
                if(imageLinks.getThumbnail() != null) {
                    if(!imageLinks.getThumbnail().isEmpty()) {
                        book.setCover(Converter.convertStringToByteArray(imageLinks.getThumbnail().replace("http://", "https://")));
                    }
                }
            }
        }
    }

    private static Date getReleaseDate(String date) {
        Date dt = null;
        try {
            if(!date.isEmpty()) {
                if(date.contains("-")) {
                    String[] spl = date.split("-");
                    if(spl.length == 2) {
                        dt = Converter.convertStringToDate(date + "-01", "yyyy-MM-dd");
                    } else {
                        dt = Converter.convertStringToDate(date, "yyyy-MM-dd");
                    }
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.YEAR, Integer.parseInt(date));
                    dt = calendar.getTime();
                }
            }
        } catch (Exception ignored) {}
        return dt;
    }
}

