package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.services.EANDataService;
import de.domjos.myarchivelibrary.services.MovieDBWebService;

public class TheMovieDBTask extends AbstractTask<String, Void, List<Movie>> {

    public TheMovieDBTask(Activity activity, boolean showNotifications, int icon) {
        super(activity, R.string.service_movie_db_search, R.string.service_movie_db_search_content, showNotifications, icon);
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Movie> doInBackground(String... strings) {
        LinkedList<Movie> movies = new LinkedList<>();

        for(String search : strings) {
            try {
                MovieDBWebService movieDBWebService = new MovieDBWebService(super.getContext(), search);
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
