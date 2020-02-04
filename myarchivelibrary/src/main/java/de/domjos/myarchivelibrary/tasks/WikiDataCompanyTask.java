package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.services.WikiDataWebService;

public class WikiDataCompanyTask extends AbstractTask<Company, Void, List<Company>> {

    public WikiDataCompanyTask(Activity activity, boolean showNotifications, int icon) {
        super(activity, R.string.service_wiki_data_search, R.string.service_wiki_data_search_content, showNotifications, icon);
    }

    @Override
    protected void before() {

    }

    @Override
    protected List<Company> doInBackground(Company... companies) {
        List<Company> companyList = new LinkedList<>();

        try {
            for(Company company : companies) {
                WikiDataWebService wikiDataWebService = new WikiDataWebService(company.getTitle());
                companyList.add(wikiDataWebService.getCompany());
            }
        } catch (Exception ex) {
            super.printException(ex);
        }

        return companyList;
    }
}
