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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import de.domjos.myarchivemobile.R;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings().isDebugMode());
        menu.findItem(R.id.menMainScanner).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int requestCode = 0;
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menMainPersons:
                intent = new Intent(this, PersonActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainCompanies:
                intent = new Intent(this, CompanyActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainCategoriesAndTags:
                intent = new Intent(this, CategoriesTagsActivity.class);
                requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
                break;
            case R.id.menMainSettings:
                intent = new Intent(this, SettingsActivity.class);
                requestCode = MainActivity.SETTINGS_REQUEST;
                break;
            case R.id.menMainLog:
                intent = new Intent(this, LogActivity.class);
                break;
        }
        if(intent != null) {
            this.startActivityForResult(intent, requestCode);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
    }


}