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

package de.domjos.myarchivelibrary.services;

import android.app.Activity;
import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.custom.AsyncTaskExecutorService;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.activities.MainActivity;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category(ServiceTest.class)
public class MovieDBWebserviceTest {
    private final static String SEARCH = "Hair";
    private MovieDBWebservice movieDBWebservice;
    private Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void before() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.movieDBWebservice = new MovieDBWebservice(this.context, 0L, "", "");
    }

    @Test
    public void testWebservice() {
        activityRule.getScenario().onActivity(activity -> {
            List<BaseMediaObject> results = new LinkedList<>();
            long end = 0L;
            try {
                WebServiceTask webServiceTask = new WebServiceTask(activity, this.movieDBWebservice);
                results = webServiceTask.execute(new String[]{MovieDBWebserviceTest.SEARCH}).get();
                end = System.currentTimeMillis();
            } catch (Exception ignored) {}
            assertNotEquals(0L, end);
            assertNotNull(results);
            assertNotEquals(0, results.size());

            this.movieDBWebservice = new MovieDBWebservice(this.context, results.get(0).getId(), results.get(0).getDescription(), "");
            try {
                WebServiceTask webServiceTask = new WebServiceTask(activity, this.movieDBWebservice);
                results = webServiceTask.execute(new String[]{""}).get();
                end = System.currentTimeMillis();
            } catch (Exception ignored) {}
            assertNotEquals(0L, end);
            assertNotNull(results);
            assertEquals(1, results.size());
        });
    }

    public static class WebServiceTask extends AsyncTaskExecutorService<String[],Void, List<BaseMediaObject>> {
        private final MovieDBWebservice movieDBWebservice;

        WebServiceTask(Activity activity, MovieDBWebservice movieDBWebservice) {
            super(activity);
            this.movieDBWebservice = movieDBWebservice;
        }

        @Override
        protected List<BaseMediaObject> doInBackground(String[] search) {
            try {
                if(!search[0].isEmpty()) {
                    return this.movieDBWebservice.getMedia(search[0]);
                } else {
                    return Collections.singletonList(this.movieDBWebservice.execute());
                }
            } catch (Exception ignored) {}
            return null;
        }

        @Override
        protected void onPostExecute(List<BaseMediaObject> baseMediaObjects) {

        }
    }
}
