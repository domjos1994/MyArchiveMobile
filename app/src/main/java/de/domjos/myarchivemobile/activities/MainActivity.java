package de.domjos.myarchivemobile.activities;

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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.activities.ScanActivity;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.ParentFragment;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.settings.Settings;

public final class MainActivity extends AbstractActivity {
    private AppBarConfiguration appBarConfiguration;
    private NavHostFragment navHostFragment;
    public static Globals GLOBALS = new Globals();
    private SearchView cmdSearch;

    public MainActivity() {
        super(R.layout.main_activity);
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
    }

    private void initSearch(String query) {

        List<Fragment> fragments = navHostFragment.getChildFragmentManager().getFragments();
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

        this.appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navMainHome, R.id.navMainMediaMusic, R.id.navMainMediaMovies, R.id.navMainMediaBooks, R.id.navMainMediaGames
        ).setDrawerLayout(drawerLayout).build();

        this.navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // init globals
        try {
            this.initGlobals();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, MainActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menMainScanner:
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("parent", Objects.requireNonNull(this.navHostFragment.getNavController().getCurrentDestination()).getLabel());
                this.startActivityForResult(intent, 99);
                return true;
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
        if(resultCode == RESULT_OK && requestCode==99) {
            List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
            for(Fragment fragment : fragments) {
                if(fragment instanceof ParentFragment) {
                    ((ParentFragment) fragment).setCodes(data.getStringExtra("codes"), data.getStringExtra("parent"));
                }
            }
        }

        List<Fragment> fragments = this.navHostFragment.getChildFragmentManager().getFragments();
        for(Fragment fragment : fragments) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initGlobals() throws Exception {
        MainActivity.GLOBALS.setSettings(new Settings(this.getApplicationContext()));
        String pwd = MainActivity.GLOBALS.getSettings().getSetting(Settings.DB_PASSWORD, "", true);
        if(pwd.trim().isEmpty()) {
            pwd = UUID.randomUUID().toString();
            MainActivity.GLOBALS.getSettings().setSetting(Settings.DB_PASSWORD, pwd, true);
        }

        SQLiteDatabase.loadLibs(this.getApplicationContext());
        Database database = new Database(this.getApplicationContext(), pwd);
        MainActivity.GLOBALS.setDatabase(database);
    }

    private void initPermissions() {
        String extStore = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String camera = Manifest.permission.CAMERA;
        String internet = Manifest.permission.INTERNET;
        Activity act = MainActivity.this;
        int grant = PackageManager.PERMISSION_GRANTED;

        if(
            ContextCompat.checkSelfPermission(act, extStore) != grant ||
            ContextCompat.checkSelfPermission(act, camera) != grant ||
            ContextCompat.checkSelfPermission(act, internet) != grant) {

            ActivityCompat.requestPermissions(act, new String[]{extStore, camera, internet}, 99);
        }
    }
}
