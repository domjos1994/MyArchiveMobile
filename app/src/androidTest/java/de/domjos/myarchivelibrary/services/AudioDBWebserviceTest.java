package de.domjos.myarchivelibrary.services;

import android.content.Context;
import android.os.AsyncTask;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

@Category(ServiceTest.class)
public class AudioDBWebserviceTest {
    private final static String SEARCH = "Let it be";
    private static long start;
    private AudioDBWebservice audioDBWebservice;
    private Context context;

    @Before
    public void before() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        AudioDBWebserviceTest.start = System.currentTimeMillis();
        this.audioDBWebservice = new AudioDBWebservice(this.context, 0L);
    }

    @Test
    public void testWebservice() {
        List<BaseMediaObject> results = new LinkedList<>();
        long end = 0L;
        try {
            WebServiceTask webServiceTask = new WebServiceTask(this.audioDBWebservice);
            results = webServiceTask.execute(AudioDBWebserviceTest.SEARCH).get();
            end = System.currentTimeMillis();
        } catch (Exception ignored) {}
        assertNotEquals(0L, end);
        assertNotNull(results);
        assertNotEquals(0, results.size());
        assertThat("Time", end, lessThan(AudioDBWebserviceTest.start + (60*1000)));

        this.audioDBWebservice = new AudioDBWebservice(this.context, results.get(0).getId());
        AudioDBWebserviceTest.start = System.currentTimeMillis();
        try {
            WebServiceTask webServiceTask = new WebServiceTask(this.audioDBWebservice);
            results = webServiceTask.execute("").get();
            end = System.currentTimeMillis();
        } catch (Exception ignored) {}
        assertNotEquals(0L, end);
        assertNotNull(results);
        assertEquals(1, results.size());
    }

    public static class WebServiceTask extends AsyncTask<String,Void,List<BaseMediaObject>> {
        private AudioDBWebservice audioDBWebservice;

        WebServiceTask(AudioDBWebservice audioDBWebservice) {
            this.audioDBWebservice = audioDBWebservice;
        }

        @Override
        protected List<BaseMediaObject> doInBackground(String... search) {
            try {
                if(!search[0].equals("")) {
                    return this.audioDBWebservice.getMedia(search[0]);
                } else {
                    return Collections.singletonList(this.audioDBWebservice.execute());
                }
            } catch (Exception ignored) {}
            return null;
        }
    }
}
