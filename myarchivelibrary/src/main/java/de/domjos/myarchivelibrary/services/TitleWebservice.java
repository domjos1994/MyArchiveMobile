package de.domjos.myarchivelibrary.services;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public abstract class TitleWebservice<T extends BaseMediaObject> extends JSONService {
    final Context CONTEXT;
    final String SEARCH;

    TitleWebservice(Context context, String search) {
        super();
        this.CONTEXT = context;
        this.SEARCH = search;
    }

    public abstract T execute() throws JSONException, IOException;
}
