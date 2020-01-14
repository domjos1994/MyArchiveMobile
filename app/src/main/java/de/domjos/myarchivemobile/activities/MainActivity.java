package de.domjos.myarchivemobile.activities;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.UUID;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.settings.Settings;

public final class MainActivity extends AbstractActivity {
    private AppBarConfiguration appBarConfiguration;
    public static Globals GLOBALS = new Globals();

    public MainActivity() {
        super(R.layout.main_activity);
    }

    @Override
    public void initActions() {

    }

    @Override
    public void initControls() {
        // init toolbar
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // init menu
        DrawerLayout drawerLayout = this.findViewById(R.id.drawer_layout);
        NavigationView navigationView = this.findViewById(R.id.nav_view);

        this.appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navMainHome, R.id.navMainMediaMusic, R.id.navMainMediaMovies, R.id.navMainMediaBooks, R.id.navMainMediaGames
        ).setDrawerLayout(drawerLayout).build();

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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, this.appBarConfiguration) || super.onSupportNavigateUp();
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
}
