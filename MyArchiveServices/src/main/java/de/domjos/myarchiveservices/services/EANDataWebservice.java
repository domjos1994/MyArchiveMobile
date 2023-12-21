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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.media.AbstractMedia;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchiveservices.R;

public class EANDataWebservice extends JSONService {
    private final static String BASE_URL = "https://eandata.com/feed/?v=3&keycode=%s&mode=json&find=%s";
    private final static String KEY_CODE = "code";
    private final static String PRODUCT = "product";

    private final String key;
    private final String code;
    private final Context context;

    public EANDataWebservice(String code, String key, Context context) {
        this.code = code;
        this.context = context;

        if(!key.isEmpty()) {
            this.key = key;
        } else {
            this.key = context.getString(R.string.service_ean_data_key);
        }
    }

    public Movie executeMovie() throws JSONException, IOException, ParseException {
        String content = readUrl(new URL(String.format(EANDataWebservice.BASE_URL, this.key, this.code.trim())));
        return this.getMovieFromJsonString(content);
    }

    public Album executeAlbum() throws JSONException, IOException, ParseException {
        String content = readUrl(new URL(String.format(EANDataWebservice.BASE_URL, this.key, this.code.trim())));
        return this.getAlbumFromJsonString(content);
    }

    public Game executeGame() throws JSONException, IOException, ParseException {
        String content = readUrl(new URL(String.format(EANDataWebservice.BASE_URL, this.key, this.code.trim())));
        return this.getGameFromJsonString(content);
    }

    private Movie getMovieFromJsonString(String content) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(content);

        JSONObject statusObject = jsonObject.getJSONObject("status");
        if(statusObject.has(EANDataWebservice.KEY_CODE) && statusObject.getString(EANDataWebservice.KEY_CODE).startsWith("4")) {
            return null;
        }

        JSONObject productObject = jsonObject.getJSONObject(EANDataWebservice.PRODUCT);
        JSONObject attributesObject = productObject.getJSONObject("attributes");
        Movie movieObject = new Movie();
        this.setBaseParams(movieObject, productObject);
        this.setCompany(movieObject, jsonObject);

        String director = this.getString(attributesObject, "movie_director");
        if(!director.isEmpty()) {
            String[] spl = director.split(" ");

            Person person = new Person();
            person.setFirstName(spl[0].trim());
            person.setLastName(director.replace(spl[0], "").trim());
            movieObject.getPersons().add(person);
        }

        String runtime = this.getString(attributesObject, "movie_runtime");
        if(!runtime.isEmpty()) {
            movieObject.setLength(Double.parseDouble(runtime));
        }

        return movieObject;
    }

    private Album getAlbumFromJsonString(String content) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(content);

        JSONObject statusObject = jsonObject.getJSONObject("status");
        if(statusObject.has(EANDataWebservice.KEY_CODE) && statusObject.getString(EANDataWebservice.KEY_CODE).startsWith("4")) {
            return null;
        }

        JSONObject productObject = jsonObject.getJSONObject(EANDataWebservice.PRODUCT);
        Album albumObject = new Album();
        this.setBaseParams(albumObject, productObject);
        this.setCompany(albumObject, jsonObject);
        return albumObject;
    }

    private Game getGameFromJsonString(String content) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(content);

        JSONObject statusObject = jsonObject.getJSONObject("status");
        if(statusObject.has(EANDataWebservice.KEY_CODE) && statusObject.getString(EANDataWebservice.KEY_CODE).startsWith("4")) {
            return null;
        }

        JSONObject productObject = jsonObject.getJSONObject(EANDataWebservice.PRODUCT);
        Game gameObject = new Game();
        this.setBaseParams(gameObject, productObject);
        this.setCompany(gameObject, jsonObject);
        return gameObject;
    }


    private void setBaseParams(AbstractMedia baseMediaObject, JSONObject productObject) throws JSONException, ParseException {
        JSONObject attributesObject = productObject.getJSONObject("attributes");
        baseMediaObject.setTitle(this.getString(attributesObject, EANDataWebservice.PRODUCT));
        baseMediaObject.setPrice(this.getDouble(attributesObject, "price_new"));
        Category category = new Category();
        category.setTitle(this.getString(attributesObject, "category_text"));
        baseMediaObject.setCategoryItem(category);
        baseMediaObject.setDescription(this.getString(attributesObject, "description"));

        String published = this.getString(attributesObject, "published");
        if(!published.isEmpty()) {
            baseMediaObject.setReleaseDate(ConvertHelper.convertStringToDate(published, "yyyy-MM-dd"));
        } else {
            String releaseYear = this.getString(attributesObject, "release_year");
            if(!releaseYear.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, 1);
                calendar.set(Calendar.YEAR, Integer.parseInt(releaseYear.split("\\.")[0].trim()));
                baseMediaObject.setReleaseDate(calendar.getTime());
            }
        }

        String img = this.getString(productObject, "image");
        if(!img.isEmpty()) {
            baseMediaObject.setCover(setCover(img, context));
        }
    }

    private void setCompany(AbstractMedia baseMediaObject, JSONObject baseObject) throws JSONException {
        if(baseObject.has("company") && !baseObject.isNull("company")) {
            JSONObject companyObject = baseObject.getJSONObject("company");
            Company company = new Company();
            company.setTitle(this.getString(companyObject, "name"));
            String url = this.getString(companyObject, "url");
            if(!url.isEmpty()) {
                company.setCover(setCover(url, context));
            }
            baseMediaObject.getCompanies().add(company);
        }
    }
}
