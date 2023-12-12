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

package de.domjos.myarchiveservices.services;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.movies.Movie;

public class MovieDBWebservice extends TitleWebservice<Movie> {
    private final static String NAME = "name";
    private final static String BASE_URL = "https://api.themoviedb.org/3";
    private final static String IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    private final String type;
    private String key;

    public MovieDBWebservice(Context context, long id, String type, String key) {
        super(context, id);
        this.type = type;
        this.key = key;
        if(this.key.isEmpty()) {
            this.key = this.CONTEXT.getString(R.string.service_movie_db_key);
        }
    }

    @Override
    public Movie execute() throws IOException, JSONException {
        return this.getMovieFromJson();
    }

    private Movie getMovieFromJson() throws JSONException, IOException {
        JSONObject resultObject = new JSONObject(readUrl(new URL(MovieDBWebservice.BASE_URL + "/" + this.type +"/" + this.SEARCH + "?api_key=" + this.key + "&language=" + Locale.getDefault().getLanguage())));

        Movie movie = new Movie();
        movie.setTitle(setString(resultObject, Arrays.asList(MovieDBWebservice.NAME, "title")));
        movie.setOriginalTitle(setString(resultObject, Arrays.asList("original_name", "original_title")));
        if(resultObject.has("overview") && !resultObject.isNull("overview")) {
            movie.setDescription(resultObject.getString("overview"));
        }
        movie.setRatingWeb(this.getRating(resultObject));

        this.getLength(resultObject, movie);
        getReleaseDate(resultObject, movie);
        getPoster(resultObject, movie);
        this.getGenres(resultObject, movie);
        this.getCompanies(resultObject, movie);
        this.getPersons(this.SEARCH, movie);

        return movie;
    }

    private static String setString(JSONObject jsonObject, List<String> names) {
        try {
            for(String name : names) {
                if(jsonObject.has(name)) {
                    return jsonObject.getString(name);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }

    private double getRating(JSONObject jsonObject) {
        try {
            if(jsonObject.has("vote_average") && !jsonObject.isNull("vote_average")) {
                return jsonObject.getDouble("vote_average");
            }
        } catch (Exception ignored) {}
        return 0.0;
    }

    public List<BaseMediaObject> getMedia(String search) throws IOException, JSONException {
        List<BaseMediaObject> movies = new LinkedList<>();
        String url = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            url = String.format("%s/search/multi?api_key=%s&language=%s&query=%s", MovieDBWebservice.BASE_URL, key, Locale.getDefault().getLanguage(), URLEncoder.encode(search, StandardCharsets.UTF_8));
        } else {
            url = String.format("%s/search/multi?api_key=%s&language=%s&query=%s", MovieDBWebservice.BASE_URL, key, Locale.getDefault().getLanguage(), "UTF-8");
        }
        JSONObject jsonObject = new JSONObject(readUrl(new URL(url)));
        if(!jsonObject.isNull("results")) {
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for(int i = 0; i<=jsonArray.length()-1; i++) {
                if(!jsonArray.isNull(i)) {
                    JSONObject resultObject = jsonArray.getJSONObject(i);
                    Movie movie = new Movie();
                    if(resultObject.has(MovieDBWebservice.NAME)) {
                        movie.setTitle(resultObject.getString(MovieDBWebservice.NAME));
                    } else if(resultObject.has("title")) {
                        movie.setTitle(resultObject.getString("title"));
                    }
                    movie.setId(resultObject.getLong("id"));
                    movie.setDescription(resultObject.getString("media_type"));
                    getPoster(resultObject, movie);
                    getReleaseDate(resultObject, movie);
                    movies.add(movie);
                }
            }
        }
        return movies;
    }

    @Override
    public String getTitle() {
        return super.CONTEXT.getString(R.string.service_movie_db_search);
    }

    @Override
    public String getType() {
        return super.CONTEXT.getString(R.string.service_type_movies);
    }

    @Override
    public String getUrl() {
        return "https://imdb.org";
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

    private static void getReleaseDate(JSONObject resultObject, Movie movie) {
        try {
            String date = setString(resultObject, Arrays.asList("last_air_date", "release_date"));
            if(!date.trim().isEmpty()) {
                movie.setReleaseDate(ConvertHelper.convertStringToDate(date, "yyyy-MM-dd"));
            }
        } catch (Exception ignored) {}
    }

    private static void getPoster(JSONObject resultObject, Movie movie) {
        try {
            if(!resultObject.isNull("poster_path")) {
                movie.setCover(getImage(resultObject.getString("poster_path")));
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
                    baseDescriptionObject.setTitle(genreObject.getString(MovieDBWebservice.NAME));
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
                    company.setTitle(companyObject.getString(MovieDBWebservice.NAME));
                    company.setCover(getImage(companyObject.getString("logo_path")));
                    movie.getCompanies().add(company);
                }
            }
        } catch (Exception ignored) {}
    }

    private static byte[] getImage(String path) {
        try {
            return ConvertHelper.convertStringToByteArray(MovieDBWebservice.IMAGE_URL + path);
        } catch (Exception ignored) {}
        return null;
    }

    private void getPersons(long id, Movie movie) {
        try {
            JSONObject resultObject = new JSONObject(readUrl(new URL(MovieDBWebservice.BASE_URL + "/" + this.type + "/" + id + "/credits?api_key=" + this.key)));
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
                    String name = castObject.getString(MovieDBWebservice.NAME);
                    String[] spl = name.split(" ");
                    String firstName = spl[0].trim();
                    String lastName = name.replace(spl[0].trim(), "").trim();
                    if(lastName.isEmpty()) {
                        person.setLastName(firstName);
                    } else {
                        person.setLastName(lastName);
                        person.setFirstName(firstName);
                    }
                    person.setImage(getImage(castObject.getString("profile_path")));
                    movie.getPersons().add(person);
                }
            }
        } catch (JSONException | IOException ex) {
            Log.e("Error", ex.toString());
        }
    }
}
