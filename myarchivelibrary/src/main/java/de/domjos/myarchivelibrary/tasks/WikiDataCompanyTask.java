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

import de.domjos.customwidgets.model.tasks.AbstractTask;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.services.WikiDataWebservice;

public class WikiDataCompanyTask extends AbstractTask<Company, Void, List<Company>> {

    public WikiDataCompanyTask(Activity activity, boolean showNotifications, int icon) {
        super(activity, R.string.service_wiki_data_search, R.string.service_wiki_data_search_content, showNotifications, icon);
    }

    @Override
    protected List<Company> doInBackground(Company... companies) {
        List<Company> companyList = new LinkedList<>();

        try {
            for(Company company : companies) {
                WikiDataWebservice wikiDataWebService = new WikiDataWebservice(company.getTitle());
                Company tmp = wikiDataWebService.getCompany();
                if(tmp.getCover() == null) {
                    tmp.setCover(company.getCover());
                }
                companyList.add(tmp);
            }
        } catch (Exception ex) {
            super.printException(ex);
        }

        return companyList;
    }
}
