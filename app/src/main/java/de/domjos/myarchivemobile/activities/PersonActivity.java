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

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.tasks.AbstractTask;
import de.domjos.myarchivelibrary.tasks.WikiDataPersonTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.PersonPagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public final class PersonActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvPersons;
    private PersonPagerAdapter personPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;

    private Person person = null;
    private Validator validator;
    private boolean firstReload = true;

    public PersonActivity() {
        super(R.layout.person_activity);
    }


    @Override
    protected void initActions() {
        this.lvPersons.setOnReloadListener(PersonActivity.this::reload);
        this.lvPersons.setOnDeleteListener(listObject -> {
            Person person = (Person) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(person);
            this.changeMode(false, false);
            this.personPagerAdapter.setMediaObject(new Person());
        });
        this.lvPersons.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.person = (Person) listObject.getObject();
            this.personPagerAdapter.setMediaObject(this.person);
            this.changeMode(false, true);
        });

        this.lvPersons.addButtonClick(R.drawable.icon_search, this.getString(R.string.sys_search), list -> {
            for(BaseDescriptionObject baseDescriptionObject : list) {
                try {
                    Person person = (Person) baseDescriptionObject.getObject();
                    if(person != null) {
                        WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(PersonActivity.this, MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);
                        wikiDataPersonTask.after(new AbstractTask.PostExecuteListener<List<Person>>() {
                            @Override
                            public void onPostExecute(List<Person> o) {
                                MainActivity.GLOBALS.getDatabase().insertOrUpdatePerson(o.get(0), "", 0);
                                reload();
                            }
                        });
                        wikiDataPersonTask.execute(person);
                    }
                } catch (Exception ignored) {}
            }
        });


        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.personPagerAdapter.setMediaObject(new Person());
                    this.person = null;
                    break;
                case R.id.cmdEdit:
                    if(this.person != null) {
                        this.changeMode(true, true);
                        this.personPagerAdapter.setMediaObject(this.person);
                    }
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    this.person = null;
                    this.reload();
                    break;
                case R.id.cmdSave:
                    if(this.validator.getState()) {
                        Person person = this.personPagerAdapter.getMediaObject();
                        if(this.person!=null) {
                            person.setId(this.person.getId());
                        }
                        if(this.validator.checkDuplicatedEntry(String.format("%s %s", person.getFirstName(), person.getLastName()), person.getId(), this.lvPersons.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdatePerson(person, "", 0);
                            this.changeMode(false, false);
                            this.person = null;
                            this.reload();
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, PersonActivity.this);
                    }
                    break;
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        this.lvPersons = this.findViewById(R.id.lvPersons);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);

        TabLayout tabLayout = this.findViewById(R.id.tabLayout);
        this.viewPager = this.findViewById(R.id.viewPager);
        this.viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(this.viewPager);

        this.personPagerAdapter = new PersonPagerAdapter(this.getSupportFragmentManager(), getApplicationContext());
        this.validator = this.personPagerAdapter.initValidator();
        this.viewPager.setAdapter(this.personPagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            this.viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        this.viewPager.setCurrentItem(0);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_person);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icon_image);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.icon_list);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);
    }

    private void select() {
        if(this.firstReload) {
            long id = this.getIntent().getLongExtra("id", 0L);

            if(id != 0) {
                for(int i = 0; i<=this.lvPersons.getAdapter().getItemCount() - 1; i++) {
                    if(this.lvPersons.getAdapter().getItem(i).getId() == id) {
                        this.lvPersons.select(this.lvPersons.getAdapter().getItem(i));
                        break;
                    }
                }
            }
            this.firstReload = false;
        }
    }

    @Override
    protected void reload() {
        try {
            this.lvPersons.getAdapter().clear();
            for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("", 0)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()).trim());
                baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(person.getBirthDate(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(person.getImage());
                baseDescriptionObject.setId(person.getId());
                baseDescriptionObject.setObject(person);
                this.lvPersons.getAdapter().add(baseDescriptionObject);
            }
            this.select();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, PersonActivity.this);
        }
    }

    protected void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);
        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvPersons, 4);
        ControlsHelper.changeScreenIfEditMode(mp, this.viewPager, PersonActivity.this, editMode);

        this.personPagerAdapter.changeMode(editMode);
    }
}
