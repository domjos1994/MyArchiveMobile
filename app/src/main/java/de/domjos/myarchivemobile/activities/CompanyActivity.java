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

package de.domjos.myarchivemobile.activities;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.model.tasks.AbstractTask;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.tasks.WikiDataCompanyTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CompanyPagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public final class CompanyActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvCompanies;
    private SearchView searchView;
    private ViewPager viewPager;
    private CompanyPagerAdapter companyPagerAdapter;
    private BottomNavigationView bottomNavigationView;

    private Company company = null;
    private Validator validator;
    private boolean firstReload = true;

    public CompanyActivity() {
        super(R.layout.company_activity);
    }


    @Override
    protected void initActions() {
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                reload(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        this.lvCompanies.setOnReloadListener(CompanyActivity.this::reload);
        this.lvCompanies.setOnDeleteListener(listObject -> {
            Company company = (Company) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(company);
            this.changeMode(false, false);
            this.companyPagerAdapter.setMediaObject(new Company());
        });
        this.lvCompanies.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.company = (Company) listObject.getObject();
            this.companyPagerAdapter.setMediaObject(this.company);
            this.changeMode(false, true);
        });

        this.lvCompanies.addButtonClick(R.drawable.icon_search, this.getString(R.string.sys_search), list -> {
            for(BaseDescriptionObject baseDescriptionObject : list) {
                try {
                    Company company = (Company) baseDescriptionObject.getObject();
                    if(company != null) {
                        WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(CompanyActivity.this, MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification);
                        wikiDataCompanyTask.after((AbstractTask.PostExecuteListener<List<Company>>) o -> {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateCompany(o.get(0), "", 0);
                            reload();
                        });
                        wikiDataCompanyTask.execute(company);
                    }
                } catch (Exception ignored) {}
            }
        });


        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    if(menuItem.getTitle().equals(this.getString(R.string.sys_add))) {
                        this.changeMode(true, false);
                        this.companyPagerAdapter.setMediaObject(new Company());
                        this.company = null;
                    } else {
                        this.changeMode(false, false);
                        this.company = null;
                        this.reload();
                    }
                    break;
                case R.id.cmdEdit:
                    if(menuItem.getTitle().equals(this.getString(R.string.sys_edit))) {
                        if(this.company != null) {
                            this.changeMode(true, true);
                            this.companyPagerAdapter.setMediaObject(this.company);
                        }
                    } else {
                        if(this.validator.getState()) {
                            Company company = this.companyPagerAdapter.getMediaObject();
                            if(this.company !=null) {
                                company.setId(this.company.getId());
                            }
                            if(this.validator.checkDuplicatedEntry(company.getTitle(), company.getId(), this.lvCompanies.getAdapter().getList())) {
                                MainActivity.GLOBALS.getDatabase().insertOrUpdateCompany(company, "", 0);
                                this.changeMode(false, false);
                                this.company = null;
                                this.reload();
                            }
                        } else {
                            MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, CompanyActivity.this);
                        }
                    }
                    break;
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        ControlsHelper.addToolbar(this);

        this.lvCompanies = this.findViewById(R.id.lvCompanies);
        this.searchView = this.findViewById(R.id.cmdSearch);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdPrevious).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdNext).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);

        TabLayout tabLayout = this.findViewById(R.id.tabLayout);
        this.viewPager = this.findViewById(R.id.viewPager);
        this.viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(this.viewPager);

        this.companyPagerAdapter = new CompanyPagerAdapter(this.getSupportFragmentManager(), getApplicationContext());
        this.validator = this.companyPagerAdapter.initValidator();
        this.viewPager.setAdapter(this.companyPagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            this.viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        this.viewPager.setCurrentItem(0);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_people);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icon_image);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.icon_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings().isDebugMode());
        menu.findItem(R.id.menMainScanner).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int requestCode = 0;
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menMainPersons:
                intent = new Intent(this, PersonActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainCompanies:
                intent = new Intent(this, CompanyActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainCategoriesAndTags:
                intent = new Intent(this, CategoriesTagsActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainSettings:
                intent = new Intent(this, SettingsActivity.class);
                requestCode = MainActivity.SETTINGS_REQUEST;
                break;
            case R.id.menMainLog:
                intent = new Intent(this, LogActivity.class);
                break;
        }
        if(intent != null) {
            this.startActivityForResult(intent, requestCode);
        }
        return super.onOptionsItemSelected(item);
    }

    private void select() {
        if(this.firstReload) {
            long id = this.getIntent().getLongExtra("id", 0L);

            if(id != 0) {
                for(int i = 0; i<=this.lvCompanies.getAdapter().getItemCount() - 1; i++) {
                    if(this.lvCompanies.getAdapter().getItem(i).getId() == id) {
                        this.lvCompanies.select(this.lvCompanies.getAdapter().getItem(i));
                        break;
                    }
                }
            }
            this.firstReload = false;
        }
    }

    @Override
    protected void reload() {
        this.reload("");
    }

    private void reload(String search) {
        if(!search.trim().isEmpty()) {
            search = "title like '%" + search + "%'";
        }
        try {
            this.lvCompanies.getAdapter().clear();
            LoadingTask<Company> loadingTask = new LoadingTask<>(CompanyActivity.this, new Company(), null, search, this.lvCompanies, "companies");
            loadingTask.after((AbstractTask.PostExecuteListener<List<Company>>) companies -> {
                for(Company company : companies) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(company.getTitle());
                    baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(company.getFoundation(), this.getString(R.string.sys_date_format)));
                    baseDescriptionObject.setCover(company.getCover());
                    baseDescriptionObject.setId(company.getId());
                    baseDescriptionObject.setObject(company);
                    this.lvCompanies.getAdapter().add(baseDescriptionObject);
                }
                this.select();
            });
            loadingTask.execute();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, CompanyActivity.this);
        }
    }

    protected void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);
        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvCompanies, 4);
        ControlsHelper.changeScreenIfEditMode(mp, this.viewPager, CompanyActivity.this, editMode);

        this.companyPagerAdapter.changeMode(editMode);
    }
}
