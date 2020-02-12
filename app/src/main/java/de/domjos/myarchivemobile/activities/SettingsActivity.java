package de.domjos.myarchivemobile.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.settings.Settings;

public final class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            EditTextPreference exportDatabase = this.getPreferenceManager().findPreference("swtExportDatabase");
            EditTextPreference importDatabase = this.getPreferenceManager().findPreference("swtImportDatabase");

            Objects.requireNonNull(exportDatabase).setOnPreferenceClickListener(preference -> {
                try {
                    String pwd = MainActivity.GLOBALS.getDatabase().copyDatabase();
                    exportDatabase.setText(MainActivity.GLOBALS.getSettings().getSetting(Settings.DB_PASSWORD, pwd, true));
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                }
                return true;
            });

            Objects.requireNonNull(importDatabase).setOnPreferenceChangeListener((obs, newVal) -> {
                try {
                    MainActivity.GLOBALS.getDatabase().copyDatabaseFromDownload();
                    MainActivity.GLOBALS.getSettings().setSetting(Settings.DB_PASSWORD, newVal, true);
                    SettingsActivity.triggerRebirth(Objects.requireNonNull(this.getContext()));
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                }
                return true;
            });
        }
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = Objects.requireNonNull(intent).getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}