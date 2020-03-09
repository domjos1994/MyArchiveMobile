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

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.services.WikiDataWebservice;

public class WikiDataPersonTask extends AbstractTask<Person, Void, List<Person>> {

    public WikiDataPersonTask(Activity activity, boolean showNotifications, int icon) {
        super(activity, R.string.service_wiki_data_search, R.string.service_wiki_data_search_content, showNotifications, icon);
    }

    @Override
    protected void before() {

    }

    @Override
    protected List<Person> doInBackground(Person... people) {
        List<Person> persons = new LinkedList<>();

        try {
            for(Person person : people) {
                WikiDataWebservice wikiDataWebService = new WikiDataWebservice(person.getFirstName(), person.getLastName());
                Person tmp = wikiDataWebService.getPerson();
                if(tmp.getImage() == null) {
                    tmp.setImage(person.getImage());
                }
                persons.add(tmp);
            }
        } catch (Exception ex) {
            super.printException(ex);
        }

        return persons;
    }
}
