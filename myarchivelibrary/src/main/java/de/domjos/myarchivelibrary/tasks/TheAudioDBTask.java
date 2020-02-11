package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.AudioDBWebservice;

public class TheAudioDBTask extends AbstractTask<Long, Void, List<Album>> {

    public TheAudioDBTask(Activity activity, boolean showNotifications, int icon) {
        super(activity, R.string.service_audio_db_search, R.string.service_audio_db_search_content, showNotifications, icon);
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Album> doInBackground(Long... ids) {
        LinkedList<Album> movies = new LinkedList<>();

        for(Long id : ids) {
            try {
                AudioDBWebservice movieDBWebService = new AudioDBWebservice(super.getContext(), id);
                Album album = movieDBWebService.execute();

                if(album != null) {
                    movies.add(album);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return movies;
    }
}
