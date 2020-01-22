package de.domjos.myarchivelibrary.services;

import android.content.Context;
import de.domjos.myarchivelibrary.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.books.Book;

public class GoogleBooksService extends JSONService {
    private final static String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s&key=%s";
    private final String KEY;
    private String code;

    public GoogleBooksService(String code, Context context) {
        this.code = code;
        this.KEY = context.getString(R.string.service_google_key);
    }

    public Book execute() throws Exception {
        String content = this.readUrl(new URL(String.format(GoogleBooksService.BASE_URL, this.code, this.KEY)));
        return this.getBookFromJsonString(content);
    }

    private Book getBookFromJsonString(String content) throws Exception {
        JSONObject jsonObject = new JSONObject(content);

        if(jsonObject.has("items")) {
            JSONObject item = jsonObject.getJSONArray("items").getJSONObject(0);
            if(item.has("volumeInfo")) {
                Book book = new Book();
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                book.setTitle(getString(volumeInfo, "title"));
                book.setOriginalTitle(getString(volumeInfo, "subtitle"));
                book.setNumberOfPages(getInt(volumeInfo, "pageCount"));
                book.setDescription(getString(volumeInfo, "description"));

                try {
                    String date = getString(volumeInfo, "publishedDate");
                    if(!date.isEmpty()) {
                        if(date.contains("-")) {
                            book.setReleaseDate(Converter.convertStringToDate(date, "yyyy-MM-dd"));
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.set(Calendar.YEAR, Integer.parseInt(date));
                            book.setReleaseDate(calendar.getTime());
                        }
                    }
                } catch (Exception ignored) {}

                if(volumeInfo.has("authors")) {
                    JSONArray jsonArray = volumeInfo.getJSONArray("authors");
                    for(int i = 0; i<=jsonArray.length()-1; i++) {
                        Person person = new Person();
                        person.setFirstName(jsonArray.getString(i).split(" ")[0]);
                        person.setLastName(jsonArray.getString(i).replace(jsonArray.getString(i).split(" ")[0], ""));
                        book.getPersons().add(person);
                    }
                }

                if(volumeInfo.has("publisher")) {
                    String publisher = volumeInfo.getString("publisher");
                    Company company = new Company();
                    company.setTitle(publisher);
                    book.setCompanies(Collections.singletonList(company));
                }

                if(volumeInfo.has("categories")) {
                    JSONArray jsonArray = volumeInfo.getJSONArray("categories");
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(jsonArray.getString(0));
                    book.setCategory(baseDescriptionObject);
                }

                if(volumeInfo.has("imageLinks")) {
                    JSONObject imageObject = volumeInfo.getJSONObject("imageLinks");
                    if(imageObject.has("thumbnail")) {
                        book.setCover(Converter.convertStringToByteArray(imageObject.getString("thumbnail")));
                    }
                }
                return book;
            }
        }

        return null;
    }
}

