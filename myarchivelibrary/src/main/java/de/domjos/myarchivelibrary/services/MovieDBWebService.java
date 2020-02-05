package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.movies.Movie;

public class MovieDBWebService extends JSONService {
    private final static String BASE_URL = "https://api.themoviedb.org/3";
    private final static String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private final String SEARCH;
    private final Context CONTEXT;
    private String type;

    public MovieDBWebService(Context context, String search) {
        this.SEARCH = search;
        this.CONTEXT = context;
    }

    public Movie execute() throws IOException, JSONException {
        return this.getMovieFromJson();
    }

    private Movie getMovieFromJson() throws JSONException, IOException {
        List<Movie> movies = MovieDBWebService.getMovies(this.CONTEXT, this.SEARCH);
        if(movies.size() != 0) {
            Movie movie = movies.get(0);
            long movieId = movie.getId();
            this.type = movie.getDescription();
            JSONObject resultObject = new JSONObject(readUrl(new URL(MovieDBWebService.BASE_URL + "/" + this.type +"/" + movieId + "?api_key=" + this.CONTEXT.getString(R.string.service_movie_db_key) + "&language=" + Locale.getDefault().getLanguage())));

            movie = new Movie();
            movie.setTitle(this.setString(resultObject, Arrays.asList("name", "title")));
            movie.setOriginalTitle(this.setString(resultObject, Arrays.asList("original_name", "original_title")));
            movie.setDescription(resultObject.getString("overview"));

            this.getLength(resultObject, movie);
            this.getReleaseDate(resultObject, movie);
            this.getPoster(resultObject, movie);
            this.getGenres(resultObject, movie);
            this.getCompanies(resultObject, movie);
            this.getPersons(movieId, movie);

            return movie;
        }
        return null;
    }

    private String setString(JSONObject jsonObject, List<String> names) {
        try {
            for(String name : names) {
                if(jsonObject.has(name)) {
                    return jsonObject.getString(name);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    public static List<Movie> getMovies(Context context, String search) throws IOException, JSONException {
        List<Movie> movies = new LinkedList<>();
        String key = context.getString(R.string.service_movie_db_key);
        String url = String.format("%s/search/multi?api_key=%s&language=%s&query=%s", MovieDBWebService.BASE_URL, key, Locale.getDefault().getLanguage(), search);
        JSONObject jsonObject = new JSONObject(readUrl(new URL(url)));
        if(!jsonObject.isNull("results")) {
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for(int i = 0; i<=jsonArray.length()-1; i++) {
                JSONObject resultObject = jsonArray.getJSONObject(i);
                Movie movie = new Movie();
                if(resultObject.has("name")) {
                    movie.setTitle(resultObject.getString("name"));
                } else if(resultObject.has("title")) {
                    movie.setTitle(resultObject.getString("title"));
                }
                movie.setId(resultObject.getLong("id"));
                movie.setDescription(resultObject.getString("media_type"));
                movies.add(movie);
            }
        }
        return movies;
    }

    private void getLength(JSONObject resultObject, Movie movie) {
        try {
            if(!resultObject.isNull("runtime")) {
                double runtime = resultObject.getDouble("runtime");
                if(runtime != 0.0) {
                    movie.setLength(runtime / 60.0);
                } else {
                    movie.setLength(runtime);
                }
            }
        } catch (Exception ignored) {}
    }

    private void getReleaseDate(JSONObject resultObject, Movie movie) {
        try {
            String date = this.setString(resultObject, Arrays.asList("last_air_date", "release_date"));
            if(!date.trim().isEmpty()) {
                movie.setReleaseDate(Converter.convertStringToDate(date, "yyyy-MM-dd"));
            }
        } catch (Exception ignored) {}
    }

    private void getPoster(JSONObject resultObject, Movie movie) {
        try {
            if(!resultObject.isNull("poster_path")) {
                movie.setCover(this.getImage(resultObject.getString("poster_path")));
            }
        }catch (Exception ignored) {}
    }

    private void getGenres(JSONObject resultObject, Movie movie) {
        try {
            if(!resultObject.isNull("genres")) {
                JSONArray genreArray = resultObject.getJSONArray("genres");
                for(int i = 0; i<=genreArray.length()-1; i++) {
                    JSONObject genreObject = genreArray.getJSONObject(i);
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(genreObject.getString("name"));
                    movie.getTags().add(baseDescriptionObject);
                }
            }
        } catch (Exception ignored) {}
    }

    private void getCompanies(JSONObject resultObject, Movie movie) {
        try {
            if(!resultObject.isNull("production_companies")) {
                JSONArray companyArray = resultObject.getJSONArray("production_companies");
                for(int i = 0; i<=companyArray.length()-1; i++) {
                    JSONObject companyObject = companyArray.getJSONObject(i);
                    Company company = new Company();
                    company.setTitle(companyObject.getString("name"));
                    company.setCover(this.getImage(companyObject.getString("logo_path")));
                    movie.getCompanies().add(company);
                }
            }
        } catch (Exception ignored) {}
    }

    private byte[] getImage(String path) {
        try {
            return Converter.convertStringToByteArray(MovieDBWebService.IMAGE_URL + path);
        } catch (Exception ignored) {}
        return null;
    }

    private void getPersons(long id, Movie movie) {
        try {
            String key = this.CONTEXT.getString(R.string.service_movie_db_key);
            JSONObject resultObject = new JSONObject(readUrl(new URL(MovieDBWebService.BASE_URL + "/" + this.type + "/" + id + "/credits?api_key=" + key)));
            if(!resultObject.isNull("cast")) {
                JSONArray jsonArray = resultObject.getJSONArray("cast");
                int max;
                if(jsonArray.length() > 5) {
                    max = 5;
                } else {
                    max = jsonArray.length() - 1;
                }

                for(int i = 0; i<=max; i++) {
                    JSONObject castObject = jsonArray.getJSONObject(i);
                    Person person = new Person();
                    String name = castObject.getString("name");
                    String[] spl = name.split(" ");
                    String firstName = spl[0].trim();
                    String lastName = name.replace(spl[0].trim(), "").trim();
                    if(lastName.isEmpty()) {
                        person.setLastName(firstName);
                    } else {
                        person.setLastName(lastName);
                        person.setFirstName(firstName);
                    }
                    person.setImage(this.getImage(castObject.getString("profile_path")));
                    movie.getPersons().add(person);
                }
            }
        } catch (Exception ignored) {}
    }
}
