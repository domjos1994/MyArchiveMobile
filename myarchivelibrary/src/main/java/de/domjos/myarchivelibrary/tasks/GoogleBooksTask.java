package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.services.GoogleBooksWebservice;

public class GoogleBooksTask extends AbstractTask<String, Void, List<Book>> {
    private String id, key;

    public GoogleBooksTask(Activity activity, boolean showNotifications, int icon, String id, String key) {
        super(activity, R.string.service_google_search, R.string.service_google_search_content, showNotifications, icon);
        this.id = id;
        this.key = key;
    }

    @Override
    protected void before() {

    }

    @Override
    protected List<Book> doInBackground(String... strings) {
        LinkedList<Book> books = new LinkedList<>();

        for(String code : strings) {
            try {
                GoogleBooksWebservice googleBooksWebservice;
                if(this.id.isEmpty()) {
                    googleBooksWebservice = new GoogleBooksWebservice(super.getContext(), code, "", this.key);
                } else {
                    googleBooksWebservice = new GoogleBooksWebservice(super.getContext(), "", this.id, this.key);
                }
                Book book = googleBooksWebservice.execute();
                if(book != null) {
                    book.setCode(code.trim());
                    books.add(book);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return books;
    }
}
