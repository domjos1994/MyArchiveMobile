package de.domjos.myarchivelibrary.services;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.Volume;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.books.Book;

public class GoogleBooksService extends JSONService {
    private String code;

    public GoogleBooksService(String code) {
        this.code = code;
    }

    public Book execute() throws IOException {
        Books books = new Books.Builder(new NetHttpTransport(), AndroidJsonFactory.getDefaultInstance(), null).setApplicationName("MyArchive").build();
        Books.Volumes.List list = books.volumes().list("isbn:" + this.code).setProjection("full");
        return this.getBookFromList(list);
    }

    private Book getBookFromList(Books.Volumes.List list) throws IOException {
        if(list != null) {
            List<Volume> volumes = list.execute().getItems();
            if(volumes!=null) {
                if(!volumes.isEmpty()) {
                    Volume volume = volumes.get(0);
                    Book book = new Book();
                    if(volume.getVolumeInfo()!=null) {
                        Volume.VolumeInfo info = volume.getVolumeInfo();
                        book.setTitle(info.getTitle());
                        book.setOriginalTitle(info.getSubtitle());
                        if(info.getPageCount() != null) {
                            book.setNumberOfPages(info.getPageCount());
                        }
                        book.setDescription(info.getDescription());
                        book.setReleaseDate(this.getReleaseDate(info.getPublishedDate()));

                        if(info.getAuthors() != null) {
                            if(!info.getAuthors().isEmpty()) {
                                for(String author : info.getAuthors()) {
                                    Person person = new Person();
                                    String firstName = author.split(" ")[0];
                                    person.setFirstName(firstName);
                                    person.setLastName(author.replace(firstName, "").trim());
                                    book.getPersons().add(person);
                                }
                            }
                        }

                        if(info.getPublisher() != null) {
                            if(!info.getPublisher().isEmpty()) {
                                Company company = new Company();
                                company.setTitle(info.getPublisher());
                                book.getCompanies().add(company);
                            }
                        }

                        if(info.getCategories() != null) {
                            if(!info.getCategories().isEmpty()) {
                                for(String category : info.getCategories()) {
                                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                                    baseDescriptionObject.setTitle(category);
                                    book.getTags().add(baseDescriptionObject);
                                }
                            }
                        }

                        if(info.getImageLinks() != null) {
                            Volume.VolumeInfo.ImageLinks imageLinks = info.getImageLinks();

                            if(imageLinks.getLarge() != null) {
                                if(!imageLinks.getLarge().isEmpty()) {
                                    book.setCover(Converter.convertStringToByteArray(imageLinks.getLarge()));
                                }
                            }

                            if(book.getCover() == null) {
                                if(imageLinks.getMedium() != null) {
                                    if(!imageLinks.getMedium().isEmpty()) {
                                        book.setCover(Converter.convertStringToByteArray(imageLinks.getMedium()));
                                    }
                                }
                            }

                            if(book.getCover() == null) {
                                if(imageLinks.getSmall() != null) {
                                    if(!imageLinks.getSmall().isEmpty()) {
                                        book.setCover(Converter.convertStringToByteArray(imageLinks.getSmall()));
                                    }
                                }
                            }

                            if(book.getCover() == null) {
                                if(imageLinks.getThumbnail() != null) {
                                    if(!imageLinks.getThumbnail().isEmpty()) {
                                        book.setCover(Converter.convertStringToByteArray(imageLinks.getThumbnail()));
                                    }
                                }
                            }
                        }
                        return book;
                    }
                }
            }
        }
        return null;
    }

    private Date getReleaseDate(String date) {
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

