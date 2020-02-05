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
import de.domjos.customwidgets.utils.Converter;
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

public class MainMusicFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvAlbums;
    private AlbumPagerAdapter albumPagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private String search;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;

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
            this.albumPagerAdapter.setMediaObject((Album) this.currentObject.getObject());
            this.changeMode(false, true);
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.albumPagerAdapter.setMediaObject(new Album());
                    this.currentObject = null;
                    break;
                case R.id.cmdEdit:
                    if(this.currentObject != null) {
                        this.changeMode(true, true);
                        this.albumPagerAdapter.setMediaObject((Album) this.currentObject.getObject());
                    }
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    currentObject = null;
                    this.reload();
                    break;
                case R.id.cmdSave:
                    if(this.validator.getState()) {
                        Album album = this.albumPagerAdapter.getMediaObject();
                        if(this.validator.checkDuplicatedEntry(album.getTitle(), this.lvAlbums.getAdapter().getList())) {
                            if(this.currentObject!=null) {
                                album.setId(((Album) this.currentObject.getObject()).getId());
                            }
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateAlbum(album);
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
        this.albumPagerAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        if(this.lvAlbums.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            this.lvAlbums.setLayoutParams(editMode ? MainActivity.CLOSE_LIST : MainActivity.OPEN_LIST);
            this.viewPager.setLayoutParams(editMode ? MainActivity.OPEN_PAGER : MainActivity.CLOSE_PAGER);
        }

        this.albumPagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvAlbums = view.findViewById(R.id.lvMediaAlbum);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        this.viewPager = view.findViewById(R.id.viewPager);
        this.viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(this.viewPager);

        this.albumPagerAdapter = new AlbumPagerAdapter(Objects.requireNonNull(this.getParentFragmentManager()), this.getContext(), () -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, albumPagerAdapter, currentObject, lvAlbums, new Album()));
        this.validator = this.albumPagerAdapter.initValidator();
        this.viewPager.setAdapter(this.albumPagerAdapter);

        ControlsHelper.initTabs(tabLayout, viewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_general_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_music_note_black_24dp);

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

            this.lvAlbums.getAdapter().clear();
            for(Album album : MainActivity.GLOBALS.getDatabase().getAlbums(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(album.getTitle());
                baseDescriptionObject.setDescription(Converter.convertDateToString(album.getReleaseDate(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(album.getCover());
                baseDescriptionObject.setObject(album);
                this.lvAlbums.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String parent) {
        try {
            if(parent.equals(this.getString(R.string.main_navigation_media_music))) {
                String[] code = codes.split("\n");
                EANDataAlbumTask eanDataService = new EANDataAlbumTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, MainActivity.GLOBALS.getSettings().getEANDataKey());
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
        long id = Objects.requireNonNull(this.getArguments()).getLong("id");
        if( id != 0) {
            for(int i = 0; i<=this.lvAlbums.getAdapter().getItemCount()-1; i++) {
                BaseDescriptionObject baseDescriptionObject = this.lvAlbums.getAdapter().getItem(i);
                BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                if(baseMediaObject.getId() == id) {
                    currentObject = baseDescriptionObject;
                    albumPagerAdapter.setMediaObject((Album) currentObject.getObject());
                    changeMode(false, true);
                    return;
                }
            }
        }
    }
}