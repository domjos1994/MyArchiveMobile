package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
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
    private BottomNavigationView bottomNavigationView;
    private String search;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_books, container, false);
        this.initControls(root);

        this.lvBooks.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                MainBooksFragment.this.reload();
            }
        });
        this.lvBooks.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                Book book = (Book) listObject.getObject();
                MainActivity.GLOBALS.getDatabase().deleteItem(book);
                changeMode(false, false);
                bookPagerAdapter.setMediaObject(new Book());
            }
        });
        this.lvBooks.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                currentObject = listObject;
                bookPagerAdapter.setMediaObject((Book) currentObject.getObject());
                changeMode(false, true);
            }
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
                        MainActivity.GLOBALS.getDatabase().insertOrUpdateBook(book);
                        this.changeMode(false, false);
                        this.currentObject = null;
                        this.reload();
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

        this.bookPagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvBooks = view.findViewById(R.id.lvMediaBooks);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager  viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(viewPager);

        this.bookPagerAdapter = new BookPagerAdapter(Objects.requireNonNull(this.getFragmentManager()), this.getContext(), () -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, bookPagerAdapter, currentObject, lvBooks, new Book()));
        this.validator = this.bookPagerAdapter.initValidator();
        viewPager.setAdapter(this.bookPagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        viewPager.setCurrentItem(0);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_general_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_book_black_24dp);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);
    }

    private void reload() {
        try {
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    this.search = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                this.search = "";
            }

            this.lvBooks.getAdapter().clear();
            for(Book book : MainActivity.GLOBALS.getDatabase().getBooks(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(book.getTitle());
                baseDescriptionObject.setDescription(Converter.convertDateToString(book.getReleaseDate(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(book.getCover());
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
                GoogleBooksTask googleBooksTask = new GoogleBooksTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);
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