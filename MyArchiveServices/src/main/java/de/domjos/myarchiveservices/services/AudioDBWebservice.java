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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.media.AbstractMedia;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchiveservices.R;

/** @noinspection CharsetObjectCanBeUsed*/
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

    public List<AbstractMedia> getMedia(String search) throws IOException, JSONException {
        List<AbstractMedia> baseMediaObjects = new LinkedList<>();
        JSONObject jsonObject = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            jsonObject = new JSONObject(readUrl(new URL(AudioDBWebservice.BASE_URL + URLEncoder.encode(search, StandardCharsets.UTF_8))));
        } else {
            jsonObject = new JSONObject(readUrl(new URL(AudioDBWebservice.BASE_URL + URLEncoder.encode(search, "Utf-8"))));
        }
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
    public String getType() {
        return super.CONTEXT.getString(R.string.service_type_music);
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
                Category baseDescriptionObject = new Category();
                baseDescriptionObject.setTitle(genre);
                album.setCategoryItem(baseDescriptionObject);
            }
        } catch (Exception ignored) {}
    }

    private Drawable setCover(JSONObject albumObject, String key) {
        try {
            if(albumObject.has(key) && !albumObject.isNull(key)) {
                byte[] data = ConvertHelper.convertStringToByteArray(albumObject.getString(key));
                if(data == null) {
                    return null;
                }
                ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(data);
                BitmapDrawable drawable = new BitmapDrawable(CONTEXT.getResources(), byteArrayOutputStream);
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return drawable;
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
