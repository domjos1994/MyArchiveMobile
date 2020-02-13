package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public abstract class TitleWebservice<T extends BaseMediaObject> extends JSONService {
    final Context CONTEXT;
    final long SEARCH;

    TitleWebservice(Context context, long id) {
        super();
        this.CONTEXT = context;
        this.SEARCH = id;
    }

    public abstract T execute() throws JSONException, IOException;

    public abstract List<BaseMediaObject> getMedia(String search) throws IOException, JSONException;

    public abstract String getTitle();

    public abstract String getUrl();

    @Override
    public String toString() {
        return this.getTitle();
    }
}
