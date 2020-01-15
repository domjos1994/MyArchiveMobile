package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.services.GoogleBooksService;

public class GoogleBooksTask extends AbstractTask<String, Void, List<Book>> {

    public GoogleBooksTask(Activity activity, int title, int content, int icon) {
        super(activity, title, content, true, icon);
    }

    @Override
    protected void before() {

    }

    @Override
    protected List<Book> doInBackground(String... strings) {
        LinkedList<Book> books = new LinkedList<>();

        for(String code : strings) {
            try {
                GoogleBooksService googleBooksService = new GoogleBooksService(code);
                Book book = googleBooksService.execute();
                book.setCode(code.trim());
                books.add(book);
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return books;
    }
}
