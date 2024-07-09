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

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivelibrary.custom.AbstractTask;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.tasks.EANDataAlbumTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.AlbumPagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public class MainMusicFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvAlbums;
    private AlbumPagerAdapter albumPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private TextView txtStatistics;
    private String search;
    private ViewGroup spl;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;
    private boolean changePage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_music, container, false);
        this.initControls(root);

        this.lvAlbums.setOnReloadListener(MainMusicFragment.this::reload);
        this.lvAlbums.setOnDeleteListener(listObject -> {
            Album album = (Album) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(album);
            this.changeMode(false, false);
            this.albumPagerAdapter.setMediaObject(new Album());
        });
        this.lvAlbums.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.currentObject = listObject;
            this.currentObject.setObject(ControlsHelper.getObject(listObject, this.requireContext()));
            this.albumPagerAdapter.setMediaObject((Album) this.currentObject.getObject());
            this.changeMode(false, true);
        });
        this.lvAlbums.addButtonClick(R.drawable.icon_music, this.getString(R.string.album_last_heard), list -> {
            for(BaseDescriptionObject baseDescriptionObject : list) {
                Album album = (Album) baseDescriptionObject.getObject();
                album.setLastHeard(new Date());
                MainActivity.GLOBALS.getDatabase().insertOrUpdateAlbum(album);
            }
        });

        this.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.cmdNext) {
                this.changePage = true;
                MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.MUSIC) + 1, Globals.MUSIC);
                this.reload();
            } else if(menuItem.getItemId() == R.id.cmdPrevious) {
                this.changePage = true;
                MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.MUSIC) - 1, Globals.MUSIC);
                this.reload();
            } else if(menuItem.getItemId() == R.id.cmdAdd) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_add))) {
                    this.changeMode(true, false);
                    this.albumPagerAdapter.setMediaObject(new Album());
                    this.currentObject = null;
                } else {
                    this.changeMode(false, false);
                    this.albumPagerAdapter.setMediaObject(new Album());
                    currentObject = null;
                    this.reload();
                }
            } else if(menuItem.getItemId() == R.id.cmdEdit) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_edit))) {
                    if(this.currentObject != null) {
                        this.changeMode(true, true);
                        this.albumPagerAdapter.setMediaObject((Album) this.currentObject.getObject());
                    }
                } else {
                    if(this.validator.getState()) {
                        Album album = this.albumPagerAdapter.getMediaObject();
                        if(this.currentObject!=null) {
                            album.setId(((Album) this.currentObject.getObject()).getId());
                        }
                        if(this.validator.checkDuplicatedEntry(album.getTitle(), album.getId(), this.lvAlbums.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateAlbum(album);
                            this.changeMode(false, false);
                            this.albumPagerAdapter.setMediaObject(new Album());
                            this.currentObject = null;
                            this.reload();
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, this.getActivity());
                    }
                }
            }
            return true;
        });

        this.reload();
        return root;
    }

    @Override
    public void onResult(ActivityResult result) {
        this.albumPagerAdapter.onResult(result);
    }

    @Override
    public void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);
        ControlsHelper.splitPaneEditMode(this.spl, editMode);
        this.albumPagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvAlbums = view.findViewById(R.id.lvMediaAlbum);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);

        this.txtStatistics = view.findViewById(R.id.lblNumber);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);

        this.spl = view.findViewById(R.id.spl);

        this.albumPagerAdapter = new AlbumPagerAdapter(this.requireActivity(), this.getContext(), () -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, albumPagerAdapter, currentObject, lvAlbums, new Album()));
        this.validator = this.albumPagerAdapter.initValidator();
        viewPager.setAdapter(this.albumPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(" ")).attach();

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_general);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icon_image);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.icon_person);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.icon_music);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(R.drawable.icon_stars);
        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(R.drawable.icon_field);
    }



    private void reload() {
        try {
            String searchQuery = getString();

            this.lvAlbums.getAdapter().clear();
            String key = this.returnKey();
            key = ControlsHelper.setThePage(this, "albums", key);
            LoadingTask<Album> loadingTask = new LoadingTask<>(this.getActivity(), new Album(), null, searchQuery, this.lvAlbums, key);
            String finalKey = key;
            loadingTask.after((AbstractTask.PostExecuteListener<List<BaseDescriptionObject>>) albums -> {
                for(BaseDescriptionObject baseDescriptionObject : albums) {
                    lvAlbums.getAdapter().add(baseDescriptionObject);
                }
                this.select();
                ControlsHelper.setMediaStatistics(this.txtStatistics, finalKey);
            });
            loadingTask.execute(null);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private @NonNull String getString() {
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
        return searchQuery;
    }

    private String returnKey() {
        String key = Globals.MUSIC;

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
            if(parent.equals(this.getString(R.string.main_navigation_media_music))) {
                String[] code = codes.split("\n");
                EANDataAlbumTask eanDataService = new EANDataAlbumTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, MainActivity.GLOBALS.getSettings().getEANDataKey());
                List<Album> albums = eanDataService.execute(code).get();
                for(Album album : albums) {
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateAlbum(album);
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
        if(this.getArguments() != null) {
            long id = this.getArguments().getLong("id");
            if( id != 0) {
                for(int i = 0; i<=this.lvAlbums.getAdapter().getItemCount()-1; i++) {
                    BaseDescriptionObject baseDescriptionObject = this.lvAlbums.getAdapter().getItem(i);
                    BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                    if(baseMediaObject.getId() == id) {
                        currentObject = baseDescriptionObject;
                        this.currentObject.setObject(ControlsHelper.getObject(baseDescriptionObject, this.requireContext()));
                        albumPagerAdapter.setMediaObject((Album) currentObject.getObject());
                        lvAlbums.select(currentObject);
                        changeMode(false, true);
                        return;
                    }
                }
            }
        }
    }
}