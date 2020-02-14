package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;

public class AudioDBWebservice extends TitleWebservice<Album> {
    private final static String BASE_URL = "https://theaudiodb.com/api/v1/json/1/searchalbum.php?a=";

    public AudioDBWebservice(Context context, long id) {
        super(context, id);
    }

    @Override
    public Album execute() throws JSONException, IOException {
        return this.getAlbumFromJson();
    }

    private Album getAlbumFromJson() throws JSONException, IOException {
        Album album = new Album();
        JSONObject jsonObject = new JSONObject(readUrl(new URL("https://theaudiodb.com/api/v1/json/1/album.php?m=" + this.SEARCH)));
        JSONObject albumObject = jsonObject.getJSONArray("album").getJSONObject(0);
        album.setTitle(albumObject.getString("strAlbum"));
        album.setOriginalTitle(albumObject.getString("strAlbumStripped"));
        album.setDescription(this.getDescription(albumObject, "strDescription"));
        album.setReleaseDate(setReleaseYear(albumObject, "intYearReleased"));
        if(albumObject.has("intScore") && !albumObject.isNull("intScore")) {
            album.setRatingWeb(albumObject.getDouble("intScore"));
        }
        this.setCategory(albumObject, album);
        album.setCover(setCover(albumObject, "strAlbumThumb"));
        this.setArtist(albumObject, album);
        album.setId(0);
        return album;
    }

    public List<BaseMediaObject> getMedia(String search) throws IOException, JSONException {
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        JSONObject jsonObject = new JSONObject(readUrl(new URL(AudioDBWebservice.BASE_URL + URLEncoder.encode(search, "UTF-8"))));
        if(!jsonObject.isNull("album")) {
            JSONArray albumArray = jsonObject.getJSONArray("album");
            for(int i = 0; i<=albumArray.length()-1; i++) {
                JSONObject albumObject = albumArray.getJSONObject(i);
                Album baseMediaObject = new Album();
                baseMediaObject.setId(albumObject.getLong("idAlbum"));
                baseMediaObject.setTitle(albumObject.getString("strAlbum"));
                baseMediaObject.setReleaseDate(setReleaseYear(albumObject, "intYearReleased"));
                baseMediaObject.setCover(setCover(albumObject, "strAlbumThumb"));
                baseMediaObjects.add(baseMediaObject);
            }
        }
        return baseMediaObjects;
    }

    @Override
    public String getTitle() {
        return super.CONTEXT.getString(R.string.service_audio_db_search);
    }

    @Override
    public String getUrl() {
        return "https://theaudiodb.com";
    }

    private String getDescription(JSONObject albumObject, String key) {
        String description = "";
        try {
            if(albumObject.has(key + "DE") && !albumObject.isNull(key + "DE")) {
                description = albumObject.getString(key + "DE");
            }
            if(description.isEmpty() && albumObject.has(key + "EN") && !albumObject.isNull(key + "EN")) {
                description = albumObject.getString(key + "EN");
            }
        } catch (Exception ignored) {}
        return description;
    }

    private static Date setReleaseYear(JSONObject albumObject, String key) {
        try {
            int year = albumObject.getInt(key);
            if(year != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                return calendar.getTime();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void setCategory(JSONObject albumObject, Album album) {
        try {
            if(albumObject.has("strGenre") && !albumObject.isNull("strGenre")) {
                String genre = albumObject.getString("strGenre");
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(genre);
                album.setCategory(baseDescriptionObject);
            }
        } catch (Exception ignored) {}
    }

    private static byte[] setCover(JSONObject albumObject, String key) {
        try {
            if(albumObject.has(key) && !albumObject.isNull(key)) {
                return ConvertHelper.convertStringToByteArray(albumObject.getString(key));
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void setArtist(JSONObject albumObject, Album album) throws JSONException, IOException {
        if(albumObject.has("idArtist") && !albumObject.isNull("idArtist")) {
            JSONObject jsonObject = new JSONObject(readUrl(new URL("https://theaudiodb.com/api/v1/json/1/artist.php?i=" + albumObject.getLong("idArtist"))));
            JSONObject artistObject = jsonObject.getJSONArray("artists").getJSONObject(0);
            Company company = new Company();
            company.setTitle(artistObject.getString("strArtist"));
            company.setFoundation(setReleaseYear(artistObject, "intFormedYear"));
            company.setDescription(this.getDescription(artistObject, "strBiography"));
            company.setCover(setCover(artistObject, "strArtistThumb"));
            album.getCompanies().add(company);
        }
    }
}
