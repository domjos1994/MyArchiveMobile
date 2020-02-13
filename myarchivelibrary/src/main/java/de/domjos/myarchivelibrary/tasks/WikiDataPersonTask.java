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
                persons.add(wikiDataWebService.getPerson());
            }
        } catch (Exception ex) {
            super.printException(ex);
        }

        return persons;
    }
}
