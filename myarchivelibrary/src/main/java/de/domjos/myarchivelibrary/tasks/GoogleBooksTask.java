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

package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.tasks.AbstractTask;
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
            } catch (InterruptedIOException ignored) {
            } catch (UnknownHostException ex) {
                this.printMessage(getContext().getString(R.string.sys_no_internet));
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return books;
    }
}
