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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.LinkedList;
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
    private NavController navController;
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
        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, this.navController, this.appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, this.navController);

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
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings().isDebugMode());
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menMainScanner:
                intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra("parent", Objects.requireNonNull(this.navHostFragment.getNavController().getCurrentDestination()).getLabel());
                break;
            case R.id.menMainPersons:
                intent = new Intent(MainActivity.this, PersonActivity.class);
                break;
            case R.id.menMainCompanies:
                intent = new Intent(MainActivity.this, CompanyActivity.class);
                break;
            case R.id.menMainCategoriesAndTags:
                intent = new Intent(MainActivity.this, CategoriesTagsActivity.class);
                break;
            case R.id.menMainSettings:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                break;
            case R.id.menMainLog:
                intent = new Intent(MainActivity.this, LogActivity.class);
                break;
        }
        if(intent != null) {
            this.startActivityForResult(intent, 99);
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

    public void selectTab(String title, long id) {
        Bundle args = new Bundle();
        args.putLong("id", id);
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
