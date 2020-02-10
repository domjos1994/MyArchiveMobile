package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.services.MovieDBWebService;

public class TheMovieDBTask extends AbstractTask<Long, Void, List<Movie>> {
    private String type, key;

    public TheMovieDBTask(Activity activity, boolean showNotifications, int icon, String type, String key) {
        super(activity, R.string.service_movie_db_search, R.string.service_movie_db_search_content, showNotifications, icon);
        this.type = type;
        this.key = key;
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Movie> doInBackground(Long... ids) {
        LinkedList<Movie> movies = new LinkedList<>();

        for(Long id : ids) {
            try {
                MovieDBWebService movieDBWebService = new MovieDBWebService(super.getContext(), id, this.type, this.key);
                Movie movie = movieDBWebService.execute();

                if(movie != null) {
                    movies.add(movie);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return movies;
    }
}
