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

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchiveservices.services.AudioDBWebservice;
import de.domjos.myarchiveservices.services.GoogleBooksWebservice;
import de.domjos.myarchiveservices.services.IGDBWebservice;
import de.domjos.myarchiveservices.services.MovieDBWebservice;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.dialogs.MediaDialog;
import de.domjos.myarchivemobile.fragments.ParentFragment;
import de.domjos.myarchivemobile.helper.CheckNetwork;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.services.LibraryService;
import de.domjos.myarchivemobile.services.ListService;
import de.domjos.myarchivemobile.settings.Globals;

public final class MainActivity extends AbstractActivity {
    private final static boolean INIT_WITH_EXAMPLE_DATA = false;

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private NavHostFragment navHostFragment;
    private ImageButton cmdSearchWeb;
    public final static Globals GLOBALS = new Globals();
    private SearchView cmdSearch;
    private Menu menu;
    private String label;
    private boolean onlyOrientationChanged = false;
    private static String query = "";

    private ActivityResultLauncher<Intent> scannerCallback;
    private ActivityResultLauncher<Intent> settingsCallback;
    private ActivityResultLauncher<Intent> categoriesCallback;
    private ActivityResultLauncher<Intent> emptyCallback;

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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //initSearch(newText);
                return false;
            }
        });

        this.cmdSearch.setOnCloseListener(() -> {
            initSearch("");
            return false;
        });

        this.cmdSearchWeb.setOnClickListener(view -> {
            GoogleBooksWebservice googleBooksWebservice = new GoogleBooksWebservice(MainActivity.this, "", "", "");
            MovieDBWebservice movieDBWebservice = new MovieDBWebservice(MainActivity.this, 0L, "", "");
            AudioDBWebservice audioDBWebservice = new AudioDBWebservice(MainActivity.this, 0L);
            IGDBWebservice igdbWebservice = new IGDBWebservice(MainActivity.this, 0L, "");

            MediaDialog mediaDialog = MediaDialog.newInstance("", this.getString(R.string.book), Arrays.asList(googleBooksWebservice, movieDBWebservice, audioDBWebservice, igdbWebservice), (result) -> {});
            mediaDialog.show(this.getSupportFragmentManager(), "dialog");
        });

        this.navHostFragment.getNavController().addOnDestinationChangedListener((controller, destination, arguments) -> {
            this.label = Objects.requireNonNull(destination.getLabel()).toString();
            if(this.menu != null) {
                boolean isMediaFragment =
                        this.label.equals(this.getString(R.string.main_navigation_media_books)) ||
                        this.label.equals(this.getString(R.string.main_navigation_media_movies)) ||
                        this.label.equals(this.getString(R.string.main_navigation_media_music)) ||
                        this.label.equals(this.getString(R.string.main_navigation_media_games));

                boolean isHomeFragment = this.label.equals(this.getString(R.string.app_name));
                boolean isApiFragment = this.label.equals(this.getString(R.string.api));

                this.menu.findItem(R.id.menMainScanner).setVisible(isMediaFragment && MainActivity.GLOBALS.isNetwork());
                this.cmdSearch.setVisibility(isApiFragment ? View.GONE : View.VISIBLE);
                this.cmdSearchWeb.setVisibility(!(isMediaFragment || isHomeFragment) ? View.GONE : View.VISIBLE);
                ControlsHelper.checkNetwork(this);
            }
        });
    }

    private void initSearch(String query) {
        MainActivity.query = query;
        List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
        if(fragments.size() == 1) {
            ParentFragment parentFragment = ((ParentFragment) fragments.get(0));
            parentFragment.reload(query, true);
        } else {
            for(Fragment fragment : fragments) {
                if(fragment instanceof ParentFragment parentFragment) {
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
        this.cmdSearchWeb = this.findViewById(R.id.cmdSearchWeb);

        // init menu
        DrawerLayout drawerLayout = this.findViewById(R.id.drawer_layout);
        NavigationView navigationView = this.findViewById(R.id.nav_view);
        this.setTextColorForMenuItem(navigationView.getMenu().findItem(R.id.navMainMedia));
        this.setTextColorForMenuItem(navigationView.getMenu().findItem(R.id.navMainGeneral));

        this.appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navMainHome, R.id.navMainMediaMusic, R.id.navMainMediaMovies, R.id.navMainMediaBooks, R.id.navMainMediaGames
        ).setOpenableLayout(drawerLayout).build();

        this.navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, this.navController, this.appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, this.navController);

        this.label = Objects.requireNonNull(Objects.requireNonNull(this.navHostFragment.getNavController().getCurrentDestination()).getLabel()).toString();

        // init globals
        try {
            this.initGlobals();

            if(MainActivity.INIT_WITH_EXAMPLE_DATA) {
                MainActivity.GLOBALS.getDatabase(this.getApplicationContext()).insertExampleData();
            }

            this.selectFileTreeFragment();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
        }

        ControlsHelper.checkNetwork(this);
        this.initOnResultCallBacks();
    }

    private void setTextColorForMenuItem(MenuItem menuItem) {
        SpannableString spanString = new SpannableString(Objects.requireNonNull(menuItem.getTitle()).toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.textColorPrimary)), 0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings(this.getApplicationContext()).isDebugMode());
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
        Intent intent;

        if(item.getItemId() == R.id.menMainScanner) {
            intent = new Intent(MainActivity.this, ScanActivity.class);
            intent.putExtra("parent", this.label);
            this.scannerCallback.launch(intent);
        }
        ControlsHelper.onOptionsItemsSelected(item, this,
                this.emptyCallback, this.emptyCallback, this.categoriesCallback,
                this.settingsCallback, this.emptyCallback
        );
        return super.onOptionsItemSelected(item);
    }

    private void initOnResultCallBacks() {
        this.scannerCallback = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
                        for(Fragment fragment : fragments) {
                            if(fragment instanceof ParentFragment) {
                                if(result.getData() != null) {
                                    ((ParentFragment) fragment).setCodes(result.getData().getStringExtra("codes"), result.getData().getStringExtra("parent"));
                                }
                            }
                        }
                    }
                }
        );
        this.settingsCallback = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> this.menu.findItem(R.id.menMainLog)
                        .setVisible(MainActivity.GLOBALS.getSettings(this.getApplicationContext())
                                .isDebugMode()
                        )
        );
        this.categoriesCallback = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    assert result.getData() != null;
                    if(result.getData().hasExtra("type") && result.getData().hasExtra("id")) {
                        String type = result.getData().getStringExtra("type");
                        if(type != null) {
                            this.selectTab(type, result.getData().getLongExtra("id", 0));
                        }
                    }
                }
        );
        this.emptyCallback = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {}
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, this.appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.navController.navigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.initPermissions();
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
        if(title.trim().equals(this.getString(R.string.library))) {
            this.navController.navigate(R.id.navMainLibrary, args);
        }
    }

    private void selectFileTreeFragment() {
        Uri uri = ControlsHelper.getDataFromOtherApp(this);
        if(uri != null) {
            Bundle args = new Bundle();
            args.putString("uri", MainActivity.getFilePath(this, uri));
            this.navController.navigate(R.id.navMainMediaFileTree, args);
        }
    }

    private void initGlobals() {
        CheckNetwork checkNetwork = new CheckNetwork(this.getApplicationContext());
        checkNetwork.registerNetworkCallback(availableNetwork -> {
            if(menu != null && label != null) {
                boolean book = label.equals(getString(R.string.main_navigation_media_books));
                boolean movie = label.equals(getString(R.string.main_navigation_media_movies));
                boolean music = label.equals(getString(R.string.main_navigation_media_music));
                boolean game = label.equals(getString(R.string.main_navigation_media_games));

                runOnUiThread(() -> menu.findItem(R.id.menMainScanner).setVisible((book || movie || music || game) && MainActivity.GLOBALS.isNetwork()));
            }
        });

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

    private static String getFilePath(Context context, Uri uri) {
        try {
            String selection = null;
            String[] selectionArgs = null;
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{
                            split[1]
                    };
                }
            }
            if ("content".equalsIgnoreCase(uri.getScheme())) {


                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                String[] projection = {
                        MediaStore.Images.Media.DATA
                };
                Cursor cursor;
                try {
                    String path = uri.getPath();
                    if(path != null) {
                        if(path.startsWith("/media")) {
                            path = path.replace("/media", Environment.getExternalStorageDirectory().getAbsolutePath());
                            return path;
                        }
                    }
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                    int column_index = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
