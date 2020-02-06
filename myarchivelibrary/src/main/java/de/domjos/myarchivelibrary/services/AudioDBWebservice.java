package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;

public class AudioDBWebservice extends TitleWebservice<Album> {
    private final static String BASE_URL = "https://theaudiodb.com/api/v1/json/1/searchalbum.php?a=";

    public AudioDBWebservice(Context context, String search) {
        super(context, search);
    }

    @Override
    public Album execute() throws JSONException, IOException {
        return this.getAlbumFromJson();
    }

    private Album getAlbumFromJson() throws JSONException, IOException {
        List<BaseMediaObject> albums = AudioDBWebservice.getMedia(this.SEARCH);
        if(albums != null) {
            if (albums.size() != 0) {
                BaseMediaObject baseMediaObject = albums.get(0);
                Album album = (Album) baseMediaObject;
                JSONObject jsonObject = new JSONObject(readUrl(new URL("https://theaudiodb.com/api/v1/json/1/album.php?m=" + album.getId())));
                JSONObject albumObject = jsonObject.getJSONArray("album").getJSONObject(0);
                album.setTitle(albumObject.getString("strAlbum"));
                album.setOriginalTitle(albumObject.getString("strAlbumStripped"));
                album.setDescription(this.getDescription(albumObject, "strDescription"));
                album.setReleaseDate(this.setReleaseYear(albumObject, "intYearReleased"));
                this.setCategory(albumObject, album);
                album.setCover(this.setCover(albumObject, "strAlbumThumb"));
                this.setArtist(albumObject, album);
                album.setId(0);
                return album;
            }
        }
        return null;
    }

    public static List<BaseMediaObject> getMedia(String search) throws IOException, JSONException {
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        JSONObject jsonObject = new JSONObject(readUrl(new URL(AudioDBWebservice.BASE_URL + search)));
        if(!jsonObject.isNull("album")) {
            JSONArray albumArray = jsonObject.getJSONArray("album");
            for(int i = 0; i<=albumArray.length()-1; i++) {
                JSONObject albumObject = albumArray.getJSONObject(i);
                Album baseMediaObject = new Album();
                baseMediaObject.setId(albumObject.getLong("idAlbum"));
                baseMediaObject.setTitle(albumObject.getString("strAlbum"));
                baseMediaObjects.add(baseMediaObject);
            }
        }
        return baseMediaObjects;
    }

    private String getDescription(JSONObject albumObject, String key) {
        String description = "";
        try {
            if(albumObject.has(key + "DE")) {
                if(!albumObject.isNull(key + "DE")) {
                    description = albumObject.getString(key + "DE");
                }
            }
            if(description.isEmpty()) {
                if(albumObject.has(key + "EN")) {
                    if(!albumObject.isNull(key + "EN")) {
                        description = albumObject.getString(key + "EN");
                    }
                }
            }
        } catch (Exception ignored) {}
        return description;
    }

    private Date setReleaseYear(JSONObject albumObject, String key) {
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
            if(albumObject.has("strGenre")) {
                if(!albumObject.isNull("strGenre")) {
                    String genre = albumObject.getString("strGenre");
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(genre);
                    album.setCategory(baseDescriptionObject);
                }
            }
        } catch (Exception ignored) {}
    }

    private byte[] setCover(JSONObject albumObject, String key) {
        try {
            if(albumObject.has(key)) {
                if(!albumObject.isNull(key)) {
                    return Converter.convertStringToByteArray(albumObject.getString(key));
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void setArtist(JSONObject albumObject, Album album) {
        try {
            if(albumObject.has("idArtist")) {
                if(!albumObject.isNull("idArtist")) {
                    JSONObject jsonObject = new JSONObject(readUrl(new URL("https://theaudiodb.com/api/v1/json/1/artist.php?i=" + albumObject.getLong("idArtist"))));
                    JSONObject artistObject = jsonObject.getJSONArray("artists").getJSONObject(0);
                    Company company = new Company();
                    company.setTitle(artistObject.getString("strArtist"));
                    company.setFoundation(this.setReleaseYear(artistObject, "intFormedYear"));
                    company.setDescription(this.getDescription(artistObject, "strBiography"));
                    company.setCover(this.setCover(artistObject, "strArtistThumb"));
                    album.getCompanies().add(company);
                }
            }
        } catch (Exception ignored) {}
    }
}
