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

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.domjos.myarchivelibrary.services.AudioDBWebservice;
import de.domjos.myarchivelibrary.services.GoogleBooksWebservice;
import de.domjos.myarchivelibrary.services.IGDBWebservice;
import de.domjos.myarchivelibrary.services.MovieDBWebservice;
import de.domjos.myarchivemobile.dialogs.MediaDialog;
import net.sqlcipher.database.SQLiteDatabase;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.activities.ScanActivity;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.ParentFragment;
import de.domjos.myarchivemobile.helper.CheckNetwork;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.services.LibraryService;
import de.domjos.myarchivemobile.services.ListService;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.settings.Settings;

public final class MainActivity extends AbstractActivity {
    private final static boolean INIT_WITH_EXAMPLE_DATA = false;
    private final static int SETTINGS_REQUEST = 51;
    private final static int PER_COMP_TAG_CAT_REQUEST = 52;
    private final static int SCANNER_REQUEST = 53;
    public final static LinearLayout.LayoutParams OPEN_LIST = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 4);
    public final static LinearLayout.LayoutParams CLOSE_LIST = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0);
    public final static LinearLayout.LayoutParams CLOSE_PAGER = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 6);
    public final static LinearLayout.LayoutParams OPEN_PAGER = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10);

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private NavHostFragment navHostFragment;
    public final static Globals GLOBALS = new Globals();
    private SearchView cmdSearch;
    private Menu menu;
    private String label;
    private boolean onlyOrientationChanged = false;
    private static String query = "";

    public MainActivity() {
        super(R.layout.main_activity);
    }

    public static String getQuery() {
        return MainActivity.query;
    }

    @Override
    public void initActions() {
        this.cmdSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                initSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                initSearch(newText);
                return false;
            }
        });

        this.cmdSearch.setOnCloseListener(() -> {
            initSearch("");
            return false;
        });

        this.navHostFragment.getNavController().addOnDestinationChangedListener((controller, destination, arguments) -> {
            this.label = Objects.requireNonNull(destination.getLabel()).toString();
            if(this.menu != null) {
                menu.findItem(R.id.menMainScanner).setVisible(
                        (
                                this.label.equals(this.getString(R.string.main_navigation_media_books)) ||
                                        this.label.equals(this.getString(R.string.main_navigation_media_movies)) ||
                                        this.label.equals(this.getString(R.string.main_navigation_media_music)) ||
                                        this.label.equals(this.getString(R.string.main_navigation_media_games))
                        )
                );
            }
        });

        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        if(manager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.registerNetworkCallback(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    if(menu != null && label != null) {
                        boolean book = label.equals(getString(R.string.main_navigation_media_books));
                        boolean movie = label.equals(getString(R.string.main_navigation_media_movies));
                        boolean music = label.equals(getString(R.string.main_navigation_media_music));
                        boolean game = label.equals(getString(R.string.main_navigation_media_games));

                        runOnUiThread(() -> menu.findItem(R.id.menMainScanner).setVisible((book || movie || music || game) && MainActivity.GLOBALS.isNetwork()));
                    }
                }

                @Override
                public void onLosing(@NonNull Network network, int maxMsToLive) {
                    if(menu != null && label != null) {
                        boolean book = label.equals(getString(R.string.main_navigation_media_books));
                        boolean movie = label.equals(getString(R.string.main_navigation_media_movies));
                        boolean music = label.equals(getString(R.string.main_navigation_media_music));
                        boolean game = label.equals(getString(R.string.main_navigation_media_games));

                        runOnUiThread(() -> menu.findItem(R.id.menMainScanner).setVisible((book || movie || music || game) && MainActivity.GLOBALS.isNetwork()));
                    }
                }

                @Override
                public void onLost(@NonNull Network network) {
                    if(menu != null && label != null) {
                        boolean book = label.equals(getString(R.string.main_navigation_media_books));
                        boolean movie = label.equals(getString(R.string.main_navigation_media_movies));
                        boolean music = label.equals(getString(R.string.main_navigation_media_music));
                        boolean game = label.equals(getString(R.string.main_navigation_media_games));

                        runOnUiThread(() -> menu.findItem(R.id.menMainScanner).setVisible((book || movie || music || game) && MainActivity.GLOBALS.isNetwork()));
                    }
                }

                @Override
                public void onUnavailable() {
                    if(menu != null && label != null) {
                        boolean book = label.equals(getString(R.string.main_navigation_media_books));
                        boolean movie = label.equals(getString(R.string.main_navigation_media_movies));
                        boolean music = label.equals(getString(R.string.main_navigation_media_music));
                        boolean game = label.equals(getString(R.string.main_navigation_media_games));

                        runOnUiThread(() -> menu.findItem(R.id.menMainScanner).setVisible((book || movie || music || game) && MainActivity.GLOBALS.isNetwork()));
                    }
                }
            });
        }
    }

    private void initSearch(String query) {
        MainActivity.query = query;
        List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
        if(fragments.size() == 1) {
            ParentFragment parentFragment = ((ParentFragment) fragments.get(0));
            parentFragment.reload(query, true);
        } else {
            for(Fragment fragment : fragments) {
                if(fragment instanceof ParentFragment) {
                    ParentFragment parentFragment = ((ParentFragment) fragment);
                    parentFragment.reload(query, true);
                    return;
                }
            }
        }
    }

    @Override
    public void initControls() {
        this.initPermissions();

        // init toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.cmdSearch = this.findViewById(R.id.cmdSearch);

        // init menu
        DrawerLayout drawerLayout = this.findViewById(R.id.drawer_layout);
        NavigationView navigationView = this.findViewById(R.id.nav_view);
        this.setTextColorForMenuItem(navigationView.getMenu().findItem(R.id.navMainMedia), R.color.textColorPrimary);
        this.setTextColorForMenuItem(navigationView.getMenu().findItem(R.id.navMainGeneral), R.color.textColorPrimary);

        this.appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navMainHome, R.id.navMainMediaMusic, R.id.navMainMediaMovies, R.id.navMainMediaBooks, R.id.navMainMediaGames
        ).setDrawerLayout(drawerLayout).build();

        this.navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, this.navController, this.appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, this.navController);

        this.label = Objects.requireNonNull(Objects.requireNonNull(this.navHostFragment.getNavController().getCurrentDestination()).getLabel()).toString();

        // init globals
        try {
            this.initGlobals();

            if(MainActivity.INIT_WITH_EXAMPLE_DATA) {
                MainActivity.GLOBALS.getDatabase().insertExampleData();
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
        }
    }

    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings().isDebugMode());
        menu.findItem(R.id.menMainScanner).setVisible(
                (
                        this.label.equals(this.getString(R.string.main_navigation_media_books)) ||
                        this.label.equals(this.getString(R.string.main_navigation_media_movies)) ||
                        this.label.equals(this.getString(R.string.main_navigation_media_music)) ||
                        this.label.equals(this.getString(R.string.main_navigation_media_games))
                )
                && MainActivity.GLOBALS.isNetwork()
        );
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int requestCode = 0;
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menMainScanner:
                intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("parent", this.label);
                requestCode = MainActivity.SCANNER_REQUEST;
                break;
            case R.id.menMainPersons:
                intent = new Intent(MainActivity.this, PersonActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainCompanies:
                intent = new Intent(MainActivity.this, CompanyActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainCategoriesAndTags:
                intent = new Intent(MainActivity.this, CategoriesTagsActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainSearch:
                GoogleBooksWebservice googleBooksWebservice = new GoogleBooksWebservice(MainActivity.this, "", "", "");
                MovieDBWebservice movieDBWebservice = new MovieDBWebservice(MainActivity.this, 0L, "", "");
                AudioDBWebservice audioDBWebservice = new AudioDBWebservice(MainActivity.this, 0L);
                IGDBWebservice igdbWebservice = new IGDBWebservice(MainActivity.this, 0L, "");

                MediaDialog mediaDialog = MediaDialog.newInstance("", this.getString(R.string.book), Arrays.asList(googleBooksWebservice, movieDBWebservice, audioDBWebservice, igdbWebservice));
                mediaDialog.show(this.getSupportFragmentManager(), "dialog");
                break;
            case R.id.menMainSettings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                requestCode = MainActivity.SETTINGS_REQUEST;
                break;
            case R.id.menMainLog:
                intent = new Intent(MainActivity.this, LogActivity.class);
                break;
        }
        if(intent != null) {
            this.startActivityForResult(intent, requestCode);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, this.appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull  int[] grantResults) {
        this.initPermissions();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == MainActivity.SCANNER_REQUEST) {
                List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
                for(Fragment fragment : fragments) {
                    if(fragment instanceof ParentFragment) {
                        ((ParentFragment) fragment).setCodes(data.getStringExtra("codes"), data.getStringExtra("parent"));
                    }
                }
            }
            if(requestCode == MainActivity.SETTINGS_REQUEST) {
                this.menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings().isDebugMode());
            }
            if(requestCode == MainActivity.PER_COMP_TAG_CAT_REQUEST && data.hasExtra("type") && data.hasExtra("id")) {
                String type = data.getStringExtra("type");
                if(type != null) {
                    this.selectTab(type, data.getLongExtra("id", 0));
                }
            }

            List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
            for(Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        int result = newConfig.orientation;
        int land = Configuration.ORIENTATION_LANDSCAPE;
        int portrait = Configuration.ORIENTATION_PORTRAIT;

        this.onlyOrientationChanged = result == land || result == portrait;

        super.onConfigurationChanged(newConfig);
    }

    public void selectTab(String title, long id) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putString("search", MainActivity.query);
        if(title.trim().equals(this.getString(R.string.book))) {
            this.navController.navigate(R.id.navMainMediaBooks, args);
        }
        if(title.trim().equals(this.getString(R.string.movie))) {
            this.navController.navigate(R.id.navMainMediaMovies, args);
        }
        if(title.trim().equals(this.getString(R.string.game))) {
            this.navController.navigate(R.id.navMainMediaGames, args);
        }
        if(title.trim().equals(this.getString(R.string.album))) {
            this.navController.navigate(R.id.navMainMediaMusic, args);
        }
    }

    private void initGlobals() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        MainActivity.GLOBALS.setSettings(new Settings(this.getApplicationContext()));
        String pwd = MainActivity.GLOBALS.getSettings().getSetting(Settings.DB_PASSWORD, "", true);
        if (pwd != null && pwd.trim().isEmpty()) {
            pwd = UUID.randomUUID().toString();
            MainActivity.GLOBALS.getSettings().setSetting(Settings.DB_PASSWORD, pwd, true);
        }

        CheckNetwork checkNetwork = new CheckNetwork(this.getApplicationContext());
        checkNetwork.registerNetworkCallback();

        SQLiteDatabase.loadLibs(this.getApplicationContext());
        Database database = new Database(this.getApplicationContext(), pwd);
        MainActivity.GLOBALS.setDatabase(database);

        if(!onlyOrientationChanged) {
            try {
                ControlsHelper.scheduleJob(MainActivity.this, Arrays.asList(LibraryService.class, ListService.class));
            } catch (Exception ignored) {}
        }
    }

    private void initPermissions() {
        Activity act = MainActivity.this;
        try {
            int grant = PackageManager.PERMISSION_GRANTED;

            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            if(info.requestedPermissions != null) {

                List<String> permissions = new LinkedList<>();
                boolean grantState = true;
                for(String perm : info.requestedPermissions) {
                    permissions.add(perm);
                    if(ContextCompat.checkSelfPermission(act, perm) != grant) {
                        grantState = false;
                    }
                }
                if(!grantState) {
                    ActivityCompat.requestPermissions(act, permissions.toArray(new String[]{}), 99);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, act);
        }
    }
}
