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

package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchiveservices.mediaTasks.GoogleBooksTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.BookPagerAdapter;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchiveservices.tasks.LoadingBaseDescriptionObjects;
import de.domjos.myarchivemobile.helper.ControlsHelper;


public class MainBooksFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvBooks;
    private BookPagerAdapter bookPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private TextView txtStatistics;
    private String search;
    private ViewGroup spl;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;
    private boolean changePage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_books, container, false);
        this.initControls(root);

        this.lvBooks.setOnReloadListener(MainBooksFragment.this::reload);
        this.lvBooks.setOnDeleteListener(listObject -> {
            Book book = (Book) listObject.getObject();
            MainActivity.GLOBALS.getDatabase(this.getActivity()).deleteItem(book);
            this.changeMode(false, false);
            this.bookPagerAdapter.setMediaObject(new Book());
        });
        this.lvBooks.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.currentObject = listObject;
            this.currentObject.setObject(ControlsHelper.getObject(listObject, this.requireContext()));
            this.bookPagerAdapter.setMediaObject((Book) this.currentObject.getObject());
            this.changeMode(false, true);
        });
        this.lvBooks.addButtonClick(R.drawable.icon_book, this.getString(R.string.book_last_read), list -> {
            for(BaseDescriptionObject baseDescriptionObject : list) {
                Book book = (Book) baseDescriptionObject.getObject();
                book.setLastRead(new Date());
                MainActivity.GLOBALS.getDatabase(this.getActivity()).insertOrUpdateBook(book);
            }
        });

        ControlsHelper.onItemSelectedListener(this.bottomNavigationView, (item) -> {
            this.changePage = true;
            MainActivity.GLOBALS.setPage(requireContext(), MainActivity.GLOBALS.getPage(requireContext(), Globals.BOOKS) + 1, Globals.BOOKS);
            this.reload();
        }, (item) -> {
            this.changePage = true;
            MainActivity.GLOBALS.setPage(requireContext(), MainActivity.GLOBALS.getPage(requireContext(), Globals.BOOKS) - 1, Globals.BOOKS);
            this.reload();
        }, (item) -> {
            if(Objects.equals(item.getTitle(), this.getString(R.string.sys_add))) {
                this.changeMode(true, false);
                this.bookPagerAdapter.setMediaObject(new Book());
                this.currentObject = null;
            } else {
                this.changeMode(false, false);
                this.bookPagerAdapter.setMediaObject(new Book());
                this.currentObject = null;
                this.reload();
            }
        }, (item) -> {
            if(Objects.equals(item.getTitle(), this.getString(R.string.sys_edit))) {
                if(this.currentObject != null) {
                    this.changeMode(true, true);
                    this.bookPagerAdapter.setMediaObject((Book) this.currentObject.getObject());
                }
            } else {
                if(this.validator.getState()) {
                    Book book = this.bookPagerAdapter.getMediaObject();
                    if(this.currentObject!=null) {
                        book.setId(((Book) this.currentObject.getObject()).getId());
                    }
                    if(this.validator.checkDuplicatedEntry(book.getTitle(), book.getId(), this.lvBooks.getAdapter().getList())) {
                        MainActivity.GLOBALS.getDatabase(this.getActivity()).insertOrUpdateBook(book);
                        this.changeMode(false, false);
                        this.bookPagerAdapter.setMediaObject(new Book());
                        this.currentObject = null;
                        this.reload();
                    }
                } else {
                    MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, this.getActivity());
                }
            }
        });

        this.reload();
        return root;
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);
        ControlsHelper.splitPaneEditMode(this.spl, editMode);
        this.bookPagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvBooks = view.findViewById(R.id.lvMediaBooks);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);

        this.txtStatistics = view.findViewById(R.id.lblNumber);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(6);
        tabLayout.setupWithViewPager(viewPager);

        this.spl = view.findViewById(R.id.spl);

        this.bookPagerAdapter = new BookPagerAdapter(Objects.requireNonNull(this.getParentFragmentManager()), this.getContext(), () -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, bookPagerAdapter, currentObject, lvBooks, new Book()));
        this.validator = this.bookPagerAdapter.initValidator();
        viewPager.setAdapter(this.bookPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_general);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icon_image);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.icon_person);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.icon_book);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(R.drawable.icon_pdf);
        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(R.drawable.icon_stars);
        Objects.requireNonNull(tabLayout.getTabAt(6)).setIcon(R.drawable.icon_field);
    }

    private void reload() {
        try {


            String searchQuery = "";
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    searchQuery = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                if(!MainActivity.getQuery().isEmpty()) {
                    searchQuery = "title like '%" + MainActivity.getQuery() + "%' or originalTitle like '%" + MainActivity.getQuery() + "%'";
                } else {
                    searchQuery = "";
                }
            }

            this.lvBooks.getAdapter().clear();
            String key = this.returnKey();
            key = ControlsHelper.setThePage(this, "books", key);
            LoadingBaseDescriptionObjects<BaseDescriptionObject> loadingTask = new LoadingBaseDescriptionObjects<>(
                    this.getActivity(), new BaseDescriptionObject(), null, searchQuery, this.lvBooks, key,
                    MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications(),
                    R.drawable.icon_notification, MainActivity.GLOBALS.getDatabase(this.requireContext()),
                    MainActivity.GLOBALS.getSettings(this.requireContext()).getMediaCount(),
                    MainActivity.GLOBALS.getOffset("companies"),
                    MainActivity.GLOBALS.getSettings(this.requireContext()).getOrderBy()
            );
            String finalKey = key;
            loadingTask.after(books -> {
                for(BaseDescriptionObject baseDescriptionObject : books) {
                    lvBooks.getAdapter().add(baseDescriptionObject);
                }
                this.select();
                ControlsHelper.setMediaStatistics(this.txtStatistics, finalKey);
            });
            loadingTask.execute();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private String returnKey() {
        String key = Globals.BOOKS;

        if(this.search != null) {
            if(!this.search.isEmpty()) {
                key += Globals.SEARCH;
            }
        } else {
            if(!MainActivity.getQuery().isEmpty()) {
                key += Globals.SEARCH;
            }
        }

        if(!this.changePage) {
            key += Globals.RESET;
        } else {
            this.changePage = false;
        }
        return key;
    }

    @Override
    public void setCodes(String codes, String parent) {
        try {
            if(parent.equals(this.getString(R.string.main_navigation_media_books))) {
                String[] code = codes.split("\n");
                GoogleBooksTask googleBooksTask = new GoogleBooksTask(this.getActivity(), MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications(), R.drawable.icon_notification, "", MainActivity.GLOBALS.getSettings(this.requireContext()).getGoogleBooksKey());
                googleBooksTask.after(books -> {
                    for(Book book : books) {
                        MainActivity.GLOBALS.getDatabase(this.getActivity()).insertOrUpdateBook(book);
                    }
                });
                googleBooksTask.execute(code);
                this.reload();
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void reload(String search, boolean reload) {
        this.search = search;

        if(reload) {
            this.reload();
        }
    }

    @Override
    public void select() {
        if(this.getArguments() != null) {
            long id = this.getArguments().getLong("id");
            if(id != 0) {
                for(int i = 0; i<=this.lvBooks.getAdapter().getItemCount()-1; i++) {
                    BaseDescriptionObject baseDescriptionObject = this.lvBooks.getAdapter().getItem(i);
                    BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                    if(baseMediaObject.getId() == id) {
                        currentObject = baseDescriptionObject;
                        this.currentObject.setObject(ControlsHelper.getObject(baseDescriptionObject, this.requireContext()));
                        bookPagerAdapter.setMediaObject((Book) currentObject.getObject());
                        lvBooks.select(currentObject);
                        changeMode(false, true);
                        return;
                    }
                }
            } else {
                changeMode(true, false);
            }
        }
    }


}