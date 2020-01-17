package de.domjos.myarchivemobile.helper;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class ControlsHelper {


    public static List<BaseDescriptionObject> getAllMediaItems(Context context, String search) throws Exception {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getBooks(search)) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.book));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getMovies(search)) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.movie));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getAlbums(search)) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.album));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getGames(search)) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.game));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        return baseDescriptionObjects;
    }

    public static BaseDescriptionObject convertMediaToDescriptionObject(BaseMediaObject baseMediaObject, Context context) {
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setTitle(baseMediaObject.getTitle());
        baseDescriptionObject.setCover(baseMediaObject.getCover());
        if(baseMediaObject instanceof Book) {
            baseDescriptionObject.setDescription(context.getString(R.string.book));
        }
        if(baseMediaObject instanceof Movie) {
            baseDescriptionObject.setDescription(context.getString(R.string.movie));
        }
        if(baseMediaObject instanceof Album) {
            baseDescriptionObject.setDescription(context.getString(R.string.album));
        }
        if(baseMediaObject instanceof Game) {
            baseDescriptionObject.setDescription(context.getString(R.string.game));
        }
        baseDescriptionObject.setObject(baseMediaObject);
        return baseDescriptionObject;
    }
}
