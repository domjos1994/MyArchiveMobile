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