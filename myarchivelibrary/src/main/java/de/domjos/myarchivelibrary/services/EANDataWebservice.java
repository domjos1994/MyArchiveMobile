package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;

public class EANDataWebservice extends JSONService {
    private final static String BASE_URL = "https://eandata.com/feed/?v=3&keycode=%s&mode=json&find=%s";
    private final static String KEY_CODE = "code";
    private final static String PRODUCT = "product";

    private String key;
    private String code;

    public EANDataWebservice(String code, String key, Context context) {
        this.code = code;

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


    private void setBaseParams(BaseMediaObject baseMediaObject, JSONObject productObject) throws JSONException, ParseException {
        JSONObject attributesObject = productObject.getJSONObject("attributes");
        baseMediaObject.setTitle(this.getString(attributesObject, EANDataWebservice.PRODUCT));
        baseMediaObject.setPrice(this.getDouble(attributesObject, "price_new"));
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setTitle(this.getString(attributesObject, "category_text"));
        baseMediaObject.setCategory(baseDescriptionObject);
        baseMediaObject.setDescription(this.getString(attributesObject, "description"));

        String published = this.getString(attributesObject, "published");
        if(!published.isEmpty()) {
            baseMediaObject.setReleaseDate(Converter.convertStringToDate(published, "yyyy-MM-dd"));
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
            baseMediaObject.setCover(Converter.convertStringToByteArray(img));
        }
    }

    private void setCompany(BaseMediaObject baseMediaObject, JSONObject baseObject) throws JSONException {
        if(baseObject.has("company") && !baseObject.isNull("company")) {
            JSONObject companyObject = baseObject.getJSONObject("company");
            Company company = new Company();
            company.setTitle(this.getString(companyObject, "name"));
            String url = this.getString(companyObject, "url");
            if(!url.isEmpty()) {
                company.setCover(Converter.convertStringToByteArray(url));
            }
            baseMediaObject.getCompanies().add(company);
        }
    }
}
