package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.services.GoogleBooksService;

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
                GoogleBooksService googleBooksService;
                if(this.id.isEmpty()) {
                    googleBooksService = new GoogleBooksService(super.getContext(), code, "", this.key);
                } else {
                    googleBooksService = new GoogleBooksService(super.getContext(), "", this.id, this.key);
                }
                Book book = googleBooksService.execute();
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
