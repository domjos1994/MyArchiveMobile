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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.tasks.GoogleBooksTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.BookPagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;


public class MainBooksFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvBooks;
    private BookPagerAdapter bookPagerAdapter;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private String search;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_books, container, false);
        this.initControls(root);

        this.lvBooks.setOnReloadListener(MainBooksFragment.this::reload);
        this.lvBooks.setOnDeleteListener(listObject -> {
            Book book = (Book) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(book);
            this.changeMode(false, false);
            this.bookPagerAdapter.setMediaObject(new Book());
        });
        this.lvBooks.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.currentObject = listObject;
            this.bookPagerAdapter.setMediaObject((Book) this.currentObject.getObject());
            this.changeMode(false, true);
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.bookPagerAdapter.setMediaObject(new Book());
                    this.currentObject = null;
                    break;
                case R.id.cmdEdit:
                    if(this.currentObject != null) {
                        this.changeMode(true, true);
                        this.bookPagerAdapter.setMediaObject((Book) this.currentObject.getObject());
                    }
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    currentObject = null;
                    this.reload();
                    break;
                case R.id.cmdSave:
                    if(this.validator.getState()) {
                        Book book = this.bookPagerAdapter.getMediaObject();
                        if(this.currentObject!=null) {
                            book.setId(((Book) this.currentObject.getObject()).getId());
                        }
                        if(this.validator.checkDuplicatedEntry(book.getTitle(), book.getId(), this.lvBooks.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateBook(book);
                            this.changeMode(false, false);
                            this.currentObject = null;
                            this.reload();
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, this.getActivity());
                    }
                    break;
            }
            return true;
        });

        this.reload();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.bookPagerAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        if(this.lvBooks.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.lvBooks.setLayoutParams(editMode ? MainActivity.CLOSE_LIST : MainActivity.OPEN_LIST);
            this.viewPager.setLayoutParams(editMode ? MainActivity.OPEN_PAGER : MainActivity.CLOSE_PAGER);
        }

        this.bookPagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvBooks = view.findViewById(R.id.lvMediaBooks);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        this.viewPager = view.findViewById(R.id.viewPager);
        this.viewPager.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(this.viewPager);

        this.bookPagerAdapter = new BookPagerAdapter(Objects.requireNonNull(this.getParentFragmentManager()), this.getContext(), () -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, bookPagerAdapter, currentObject, lvBooks, new Book()));
        this.validator = this.bookPagerAdapter.initValidator();
        this.viewPager.setAdapter(this.bookPagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_general_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_book_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(R.drawable.ic_stars_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(R.drawable.ic_text_fields_black_24dp);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);
    }

    private void reload() {
        try {
            String searchQuery = "";
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    searchQuery = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                if(!MainActivity.query.isEmpty()) {
                    searchQuery = "title like '%" + MainActivity.query + "%' or originalTitle like '%" + MainActivity.query + "%'";
                } else {
                    searchQuery = "";
                }
            }

            this.lvBooks.getAdapter().clear();
            for(Book book : MainActivity.GLOBALS.getDatabase().getBooks(searchQuery)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(book.getTitle());
                baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(book.getReleaseDate(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(book.getCover());
                baseDescriptionObject.setId(book.getId());
                baseDescriptionObject.setState(book.getLastRead()!=null);
                baseDescriptionObject.setObject(book);
                this.lvBooks.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String parent) {
        try {
            if(parent.equals(this.getString(R.string.main_navigation_media_books))) {
                String[] code = codes.split("\n");
                GoogleBooksTask googleBooksTask = new GoogleBooksTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, "", MainActivity.GLOBALS.getSettings().getGoogleBooksKey());
                List<Book> books = googleBooksTask.execute(code).get();
                for(Book book : books) {
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateBook(book);
                }
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
        long id = Objects.requireNonNull(this.getArguments()).getLong("id");
        if(id != 0) {
            for(int i = 0; i<=this.lvBooks.getAdapter().getItemCount()-1; i++) {
                BaseDescriptionObject baseDescriptionObject = this.lvBooks.getAdapter().getItem(i);
                BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                if(baseMediaObject.getId() == id) {
                    currentObject = baseDescriptionObject;
                    bookPagerAdapter.setMediaObject((Book) currentObject.getObject());
                    changeMode(false, true);
                    return;
                }
            }
        }
    }
}