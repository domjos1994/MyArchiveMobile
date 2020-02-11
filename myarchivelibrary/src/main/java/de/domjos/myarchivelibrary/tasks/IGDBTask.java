package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.services.IGDBWebservice;

public class IGDBTask extends AbstractTask<Long, Void, List<Game>> {
    private String key;

    public IGDBTask(Activity activity, boolean showNotifications, int icon, String key) {
        super(activity, R.string.service_igdb_search, R.string.service_igdb_search_content, showNotifications, icon);
        this.key = key;
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Game> doInBackground(Long... ids) {
        LinkedList<Game> movies = new LinkedList<>();

        for(Long id : ids) {
            try {
                IGDBWebservice movieDBWebService = new IGDBWebservice(super.getContext(), id, this.key);
                Game game = movieDBWebService.execute();

                if(game != null) {
                    movies.add(game);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return movies;
    }
}
