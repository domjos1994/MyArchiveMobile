package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.games.Game;

public class IGDBWebservice extends TitleWebservice<Game>  {
    private final static String BASE_URL = "https://api-v3.igdb.com";
    private final String key;

    public IGDBWebservice(Context context, long id, String key) {
        super(context, id);
        if(key.isEmpty()) {
            this.key = context.getString(R.string.service_igdb_key);
        } else {
            this.key = key;
        }
    }

    @Override
    public Game execute() throws JSONException, IOException {
        Game game = new Game();
        String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/games/" + super.SEARCH + "?fields=name,alternative_names,cover,genres,first_release_date,keywords,summary,involved_companies"), Collections.singletonList("user-key:" + this.key));
        JSONObject jsonObject = new JSONArray(content).getJSONObject(0);

        game.setTitle(jsonObject.getString("name"));
        game.setOriginalTitle(this.getOriginalTitle(jsonObject));
        game.setCover(this.getCover(jsonObject,"cover","covers"));
        game.setCategory(this.getCategory(jsonObject));
        game.setReleaseDate(this.getDate(jsonObject, "first_release_date"));
        game.setTags(this.getTags(jsonObject));
        game.setDescription(jsonObject.getString("summary"));
        game.setCompanies(this.getCompanies(jsonObject));

        return game;
    }

    public List<BaseMediaObject> getMedia(String search) throws IOException, JSONException {
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/games?search=" + search + "&fields=name,first_release_date,cover"), Collections.singletonList("user-key:" + this.key));
        JSONArray jsonArray = new JSONArray(content);
        if(jsonArray.length() != 0) {
            for(int i = 0; i<=jsonArray.length()-1; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                BaseMediaObject baseMediaObject = new BaseMediaObject();
                baseMediaObject.setId(jsonObject.getLong("id"));
                baseMediaObject.setTitle(jsonObject.getString("name"));
                baseMediaObject.setReleaseDate(this.getDate(jsonObject, "first_release_date"));
                baseMediaObject.setCover(this.getCover(jsonObject, "cover", "covers"));
                baseMediaObjects.add(baseMediaObject);
            }
        }
        return baseMediaObjects;
    }

    @Override
    public String getTitle() {
        return super.CONTEXT.getString(R.string.service_igdb_search);
    }

    @Override
    public String getUrl() {
        return "https://www.igdb.com";
    }

    private Date getDate(JSONObject jsonObject, String key) {
        Date dt = null;
        try {
            if(jsonObject.has(key)) {
                if(!jsonObject.isNull(key)) {
                    dt = new Date();
                    dt.setTime(jsonObject.getLong(key) * 1000);
                }
            }
        } catch (Exception ignored) {}
        return dt;
    }

    private byte[] getCover(JSONObject obj, String key, String url_part) throws IOException, JSONException {
        if(obj.has(key)) {
            if(!obj.isNull(key)) {
                String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/" + url_part + "/" + obj.getLong(key) + "?fields=url"), Collections.singletonList("user-key:" + this.key));
                JSONObject jsonObject = new JSONArray(content).getJSONObject(0);
                return Converter.convertStringToByteArray(jsonObject.getString("url").replace("//", "https://"));
            }
        }
        return null;
    }

    private String getOriginalTitle(JSONObject jsonObject) {
        StringBuilder name = new StringBuilder();
        try {
            if(!jsonObject.isNull("alternative_names")) {
                JSONArray jsonArray = jsonObject.getJSONArray("alternative_names");
                if(jsonArray.length() != 0) {
                    for(int i = 0; i<=jsonArray.length()-1; i++) {
                        long id = jsonArray.getLong(i);
                        String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/alternative_names/" + id + "?fields=name"), Collections.singletonList("user-key:" + this.key));
                        JSONObject obj = new JSONArray(content).getJSONObject(0);
                        name.append(obj.getString("name")).append(",");
                    }
                }
            }
        } catch (Exception ignored) {}
        return name.toString();
    }

    private BaseDescriptionObject getCategory(JSONObject jsonObject) {
        BaseDescriptionObject baseDescriptionObject = null;
        try {
            if(!jsonObject.isNull("genres")) {
                JSONArray jsonArray = jsonObject.getJSONArray("genres");
                if(jsonArray.length() != 0) {
                    long id = jsonArray.getLong(0);
                    String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/genres/" + id + "?fields=name"), Collections.singletonList("user-key:" + this.key));
                    JSONObject obj = new JSONArray(content).getJSONObject(0);
                    baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(obj.getString("name"));
                }
            }
        } catch (Exception ignored) {}
        return baseDescriptionObject;
    }

    private List<BaseDescriptionObject> getTags(JSONObject jsonObject) {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        try {
            if(!jsonObject.isNull("keywords")) {
                JSONArray jsonArray = jsonObject.getJSONArray("keywords");
                if(jsonArray.length() != 0) {
                    int max = jsonArray.length();
                    if(jsonArray.length() > 10) {
                        max = 10;
                    }
                    for(int i = 0; i<=max-1; i++) {
                        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                        long id = jsonArray.getLong(i);
                        String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/keywords/" + id + "?fields=name"), Collections.singletonList("user-key:" + this.key));
                        JSONObject obj = new JSONArray(content).getJSONObject(0);
                        baseDescriptionObject.setTitle(obj.getString("name"));
                        baseDescriptionObjects.add(baseDescriptionObject);
                    }
                }
            }
        } catch (Exception ignored) {}
        return baseDescriptionObjects;
    }

    private List<Company> getCompanies(JSONObject jsonObject) {
        List<Company> baseDescriptionObjects = new LinkedList<>();
        try {
            if(!jsonObject.isNull("involved_companies")) {
                JSONArray jsonArray = jsonObject.getJSONArray("involved_companies");
                if(jsonArray.length() != 0) {
                    for(int i = 0; i<=jsonArray.length()-1; i++) {
                        Company company = new Company();
                        long id = jsonArray.getLong(i);
                        String content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/involved_companies/" + id + "?fields=company"), Collections.singletonList("user-key:" + this.key));
                        JSONObject obj = new JSONArray(content).getJSONObject(0);
                        content = JSONService.readUrl(new URL(IGDBWebservice.BASE_URL + "/companies/" + obj.getLong("company") + "?fields=name,description,logo,start_date"), Collections.singletonList("user-key:" + this.key));
                        obj = new JSONArray(content).getJSONObject(0);
                        company.setTitle(obj.getString("name"));
                        company.setDescription(obj.getString("description"));
                        company.setFoundation(this.getDate(obj, "start_date"));
                        company.setCover(this.getCover(obj, "logo", "company_logos"));
                        baseDescriptionObjects.add(company);
                    }
                }
            }
        } catch (Exception ignored) {}
        return baseDescriptionObjects;
    }
}
