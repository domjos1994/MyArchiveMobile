package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.services.EANDataService;

public class EANDataMovieTask extends AbstractTask<String, Void, List<Movie>> {
    private String key;

    public EANDataMovieTask(Activity activity, boolean showNotifications, int icon, String key) {
        super(activity, R.string.service_ean_data_search, R.string.service_ean_data_search_content, showNotifications, icon);
        this.key = key;
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Movie> doInBackground(String... strings) {
        LinkedList<Movie> movies = new LinkedList<>();

        for(String code : strings) {
            try {
                EANDataService eanDataService = new EANDataService(code, this.key);
                Movie movie = eanDataService.executeMovie();

                if(movie != null) {
                    movie.setCode(code);
                    movies.add(movie);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return movies;
    }
}
