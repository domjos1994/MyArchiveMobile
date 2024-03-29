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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchiveservices.mediaTasks.WikiDataPersonTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.PersonPagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchiveservices.tasks.LoadingPersons;

public final class PersonActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvPersons;
    private SearchView searchView;
    private PersonPagerAdapter personPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;

    private Person person = null;
    private Validator validator;
    private boolean firstReload = true;
    private ActivityResultLauncher<Intent> emptyCallback;

    public PersonActivity() {
        super(R.layout.person_activity);
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

        this.lvPersons.setOnReloadListener(PersonActivity.this::reload);
        this.lvPersons.setOnDeleteListener(listObject -> {
            Person person = (Person) listObject.getObject();
            MainActivity.GLOBALS.getDatabase(this.getApplicationContext()).deleteItem(person);
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
                        WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(PersonActivity.this, MainActivity.GLOBALS.getSettings(this.getApplicationContext()).isNotifications(), R.drawable.icon_notification);
                        wikiDataPersonTask.after(o -> {
                            MainActivity.GLOBALS.getDatabase(this.getApplicationContext()).insertOrUpdatePerson(o.get(0), "", 0);
                            reload();
                        });
                        wikiDataPersonTask.execute(new Person[]{person});
                    }
                } catch (Exception ignored) {}
            }
        });


        this.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.cmdAdd) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_add))) {
                    this.changeMode(true, false);
                    this.personPagerAdapter.setMediaObject(new Person());
                    this.person = null;
                } else {
                    this.changeMode(false, false);
                    this.person = null;
                    this.reload();
                }
            }
            if(menuItem.getItemId() == R.id.cmdEdit) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_edit))) {
                    if(this.person != null) {
                        this.changeMode(true, true);
                        this.personPagerAdapter.setMediaObject(this.person);
                    }
                } else {
                    if(this.validator.getState()) {
                        Person person = this.personPagerAdapter.getMediaObject();
                        if(this.person!=null) {
                            person.setId(this.person.getId());
                        }
                        if(this.validator.checkDuplicatedEntry(String.format("%s %s", person.getFirstName(), person.getLastName()), person.getId(), this.lvPersons.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase(this.getApplicationContext()).insertOrUpdatePerson(person, "", 0);
                            this.changeMode(false, false);
                            this.person = null;
                            this.reload();
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, PersonActivity.this);
                    }
                }
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        ControlsHelper.addToolbar(this);

        this.lvPersons = this.findViewById(R.id.lvPersons);
        this.searchView = this.findViewById(R.id.cmdSearch);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdNext).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdPrevious).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);

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
        ControlsHelper.checkNetwork(this);
        this.initCallBacks();
    }

    private void initCallBacks() {
        this.emptyCallback = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings(this.getApplicationContext()).isDebugMode());
        menu.findItem(R.id.menMainScanner).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ControlsHelper.onOptionsItemsSelected(item, this,
                emptyCallback, emptyCallback, emptyCallback, emptyCallback, emptyCallback
        );
        return super.onOptionsItemSelected(item);
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
        this.reload("");
    }

    private void reload(String search) {
        if(!search.trim().isEmpty()) {
            search = "lastName like '%" + search + "%'";
        }
        try {
            this.lvPersons.getAdapter().clear();
            LoadingPersons loadingTask = new LoadingPersons(
                PersonActivity.this, this.lvPersons, search,
                MainActivity.GLOBALS.getSettings(this.getApplicationContext()).isNotifications(),
                R.drawable.icon_notification, MainActivity.GLOBALS.getDatabase(this.getApplicationContext())
            );
            loadingTask.after(persons -> {
                for(Person person : persons) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()).trim());
                    baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(person.getBirthDate(), this.getString(R.string.sys_date_format)));
                    baseDescriptionObject.setCover(person.getImage());
                    baseDescriptionObject.setId(person.getId());
                    baseDescriptionObject.setObject(person);
                    this.lvPersons.getAdapter().add(baseDescriptionObject);
                }
                this.select();
            });
            loadingTask.execute();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, PersonActivity.this);
        }
    }

    private void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        ControlsHelper.navViewEditMode(editMode, selected, bottomNavigationView);
        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvPersons, 4);
        ControlsHelper.changeScreenIfEditMode(mp, this.viewPager, PersonActivity.this, editMode);

        this.personPagerAdapter.changeMode(editMode);
    }
}
