/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
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
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.tasks.EANDataGameTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.GamePagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public class MainGamesFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvGames;
    private GamePagerAdapter gamePagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private TextView txtStatistics;
    private String search;
    private ViewGroup spl;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;
    private boolean changePage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_games, container, false);
        this.initControls(root);

        this.lvGames.setOnReloadListener(MainGamesFragment.this::reload);
        this.lvGames.setOnDeleteListener(listObject -> {
            Game game = (Game) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(game);
            this.changeMode(false, false);
            this.gamePagerAdapter.setMediaObject(new Game());
        });
        this.lvGames.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.currentObject = listObject;
            this.currentObject.setObject(ControlsHelper.getObject(listObject, this.requireContext()));
            this.gamePagerAdapter.setMediaObject((Game) this.currentObject.getObject());
            this.changeMode(false, true);
        });
        this.lvGames.addButtonClick(R.drawable.icon_game, this.getString(R.string.game_last_played), list -> {
            for(BaseDescriptionObject baseDescriptionObject : list) {
                Game game = (Game) baseDescriptionObject.getObject();
                game.setLastPlayed(new Date());
                MainActivity.GLOBALS.getDatabase().insertOrUpdateGame(game);
            }
        });

        this.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.cmdNext) {
                this.changePage = true;
                MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.GAMES) + 1, Globals.GAMES);
                this.reload();
            } else if(menuItem.getItemId() == R.id.cmdPrevious) {
                this.changePage = true;
                MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.GAMES) - 1, Globals.GAMES);
                this.reload();
            } else if(menuItem.getItemId() == R.id.cmdAdd) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_add))) {
                    this.changeMode(true, false);
                    this.gamePagerAdapter.setMediaObject(new Game());
                    this.currentObject = null;
                } else {
                    this.changeMode(false, false);
                    this.gamePagerAdapter.setMediaObject(new Game());
                    currentObject = null;
                    this.reload();
                }
            } else if(menuItem.getItemId() == R.id.cmdEdit) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_edit))) {
                    if(this.currentObject != null) {
                        this.changeMode(true, true);
                        this.gamePagerAdapter.setMediaObject((Game) this.currentObject.getObject());
                    }
                } else {
                    if(this.validator.getState()) {
                        Game game = this.gamePagerAdapter.getMediaObject();
                        if(this.currentObject!=null) {
                            game.setId(((Game) this.currentObject.getObject()).getId());
                        }
                        if(this.validator.checkDuplicatedEntry(game.getTitle(), game.getId(), this.lvGames.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateGame(game);
                            this.changeMode(false, false);
                            this.gamePagerAdapter.setMediaObject(new Game());
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
        this.gamePagerAdapter.onResult(result);
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);
        ControlsHelper.splitPaneEditMode(this.spl, editMode);
        this.gamePagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvGames = view.findViewById(R.id.lvMediaGames);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdNext).setVisible(false);

        this.txtStatistics = view.findViewById(R.id.lblNumber);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(5);

        this.spl = view.findViewById(R.id.spl);

        this.gamePagerAdapter = new GamePagerAdapter(this.requireActivity(), this.getContext(), () -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, gamePagerAdapter, currentObject, lvGames, new Game()));
        this.validator = this.gamePagerAdapter.initValidator();
        viewPager.setAdapter(this.gamePagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(" ")).attach();

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_general);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icon_image);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.icon_person);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.icon_game);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(R.drawable.icon_stars);
        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(R.drawable.icon_field);
    }

    private void reload() {
        try {
            String searchQuery = getString();

            this.lvGames.getAdapter().clear();
            String key = this.returnKey();
            key = ControlsHelper.setThePage(this, "games", key);
            LoadingTask<Game> loadingTask = new LoadingTask<>(this.getActivity(), new Game(), null, searchQuery, this.lvGames, key);
            String finalKey = key;
            loadingTask.after((AbstractTask.PostExecuteListener<List<BaseDescriptionObject>>) games -> {
                for(BaseDescriptionObject baseDescriptionObject : games) {
                    lvGames.getAdapter().add(baseDescriptionObject);
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
        String key = Globals.GAMES;

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
            if(parent.equals(this.getString(R.string.main_navigation_media_games))) {
                String[] code = codes.split("\n");
                EANDataGameTask eanDataService = new EANDataGameTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, MainActivity.GLOBALS.getSettings().getEANDataKey());
                List<Game> games = eanDataService.execute(code).get();
                for(Game game : games) {
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateGame(game);
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
            if (id != 0) {
                for (int i = 0; i <= this.lvGames.getAdapter().getItemCount() - 1; i++) {
                    BaseDescriptionObject baseDescriptionObject = this.lvGames.getAdapter().getItem(i);
                    BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                    if (baseMediaObject.getId() == id) {
                        currentObject = baseDescriptionObject;
                        this.currentObject.setObject(ControlsHelper.getObject(baseDescriptionObject, this.requireContext()));
                        gamePagerAdapter.setMediaObject((Game) currentObject.getObject());
                        lvGames.select(currentObject);
                        changeMode(false, true);
                        return;
                    }
                }
            }
        }
    }
}