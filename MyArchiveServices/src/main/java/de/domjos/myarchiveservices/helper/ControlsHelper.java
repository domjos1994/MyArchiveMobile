package de.domjos.myarchiveservices.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivedatabase.model.filter.Filter;
import de.domjos.myarchivedatabase.model.media.AbstractMedia;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedatabase.model.mediaList.MediaList;
import de.domjos.myarchivedbvalidator.Database;
import de.domjos.myarchiveservices.R;

public final class ControlsHelper {

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, String search, Database database, int mediaCount, int offset, String orderBy) {
//        Map<AbstractMedia, String> mp = new LinkedHashMap<>();
//        mp.put(new Book(), search);
//        mp.put(new Movie(), search);
//        mp.put(new Album(), search);
//        mp.put(new Game(), search);
//        return ControlsHelper.getAllMediaItems(context, mp, database, mediaCount, offset, orderBy);
        return new LinkedList<>();
    }

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, Filter mediaFilter, String extendedWhere, String key, Database database, int mediaCount, int offset, String orderBy) {
//        String where = "";
//
//        if(mediaFilter.isMediaLists()) {
//            try {
//                List<MediaList> mediaLists = database.getMediaLists("id=" + mediaFilter.getMediaLists().getId(), mediaCount, offset);
//                mediaFilter.setMediaList(mediaLists==null ? mediaFilter.getMediaList() : mediaLists.get(0));
//            } catch (Exception ignored) {}
//            if(mediaFilter.getMediaList() != null) {
//                List<Long> bookIds = new LinkedList<>(), movieIds = new LinkedList<>(), musicIds = new LinkedList<>(), gameIds = new LinkedList<>();
//                for(AbstractMedia baseMediaObject : mediaFilter.getMediaList().getBaseMediaObjects()) {
//                    if(baseMediaObject instanceof Book) {
//                        bookIds.add(baseMediaObject.getId());
//                    } else if(baseMediaObject instanceof Movie) {
//                        movieIds.add(baseMediaObject.getId());
//                    } else if(baseMediaObject instanceof Album) {
//                        musicIds.add(baseMediaObject.getId());
//                    } else {
//                        gameIds.add(baseMediaObject.getId());
//                    }
//                }
//
//                Map<AbstractMedia, String> mp = new LinkedHashMap<>();
//                final String s = extendedWhere.trim().isEmpty() ? "" : " AND (" + extendedWhere + ")";
//                mp.put(new Book(), "id in (" + TextUtils.join(", ", bookIds) + ")" + s);
//                mp.put(new Movie(), "id in (" + TextUtils.join(", ", movieIds) + ")" + s);
//                mp.put(new Album(), "id in (" + TextUtils.join(", ", musicIds) + ")" + s);
//                mp.put(new Game(), "id in (" + TextUtils.join(", ", gameIds) + ")" + s);
//                return ControlsHelper.getAllMediaItems(context, mp, database, mediaCount, offset, orderBy);
//            }
//        } else {
//            List<String> categories = Arrays.asList(mediaFilter.getCategories().split("\\|"));
//            String categoryWhere = "category like '%" + TextUtils.join("%' or category like '%", categories) + "%'";
//            if(!categories.isEmpty() && !categories.get(0).isEmpty()) {
//                where = categoryWhere;
//            }
//
//            List<String> tags = Arrays.asList(mediaFilter.getTags().split("\\|"));
//            String tagsWhere = "tags like '%" + TextUtils.join("%' or tags like '%", tags) + "%'";
//            if(!tags.isEmpty() && !tags.get(0).isEmpty()) {
//                if(where.isEmpty()) {
//                    where = tagsWhere;
//                } else {
//                    where += " AND " + tagsWhere;
//                }
//            }
//
//            List<String> customFields = Arrays.asList(mediaFilter.getCustomFields().split("\\|"));
//            String customFieldsWhere = "customFields like '%" + TextUtils.join("%' or customFields like '%", customFields) + "%'";
//            if(!customFields.isEmpty() && !customFields.get(0).isEmpty()) {
//                if(where.isEmpty()) {
//                    where = customFieldsWhere;
//                } else {
//                    where += " AND " + customFieldsWhere;
//                }
//            }
//        }
//
//        Map<AbstractMedia, String> mp = new LinkedHashMap<>();
//        String fullWhere = where.trim().isEmpty() ? extendedWhere : (extendedWhere.trim().isEmpty() ? where : where + " AND (" + extendedWhere + ")");
//
//        String bookWhere = "", movieWhere = "", musicWhere = "", gameWhere = "";
//        if(mediaFilter.getMediaList() != null) {
//            List<Long> bookIds = new LinkedList<>(), movieIds = new LinkedList<>(), musicIds = new LinkedList<>(), gameIds = new LinkedList<>();
//            for (AbstractMedia baseMediaObject : mediaFilter.getMediaList().getBaseMediaObjects()) {
//                if (baseMediaObject instanceof Book) {
//                    bookIds.add(baseMediaObject.getId());
//                } else if (baseMediaObject instanceof Movie) {
//                    movieIds.add(baseMediaObject.getId());
//                } else if (baseMediaObject instanceof Album) {
//                    musicIds.add(baseMediaObject.getId());
//                } else {
//                    gameIds.add(baseMediaObject.getId());
//                }
//            }
//
//            bookWhere =  "id in (" + TextUtils.join(", ", bookIds) + ")";
//            movieWhere =  "id in (" + TextUtils.join(", ", movieIds) + ")";
//            musicWhere =  "id in (" + TextUtils.join(", ", musicIds) + ")";
//            gameWhere =  "id in (" + TextUtils.join(", ", gameIds) + ")";
//        }
//
//        if(mediaFilter.isBooks()) {
//            mp.put(new Book(), bookWhere.trim().isEmpty() ? fullWhere : (fullWhere.trim().isEmpty() ? bookWhere : fullWhere + " AND " + bookWhere));
//        }
//        if(mediaFilter.isMovies()) {
//            mp.put(new Movie(), movieWhere.trim().isEmpty() ? fullWhere : (fullWhere.trim().isEmpty() ? movieWhere : fullWhere + " AND " + movieWhere));
//        }
//        if(mediaFilter.isAlbums()) {
//            mp.put(new Album(), musicWhere.trim().isEmpty() ? fullWhere : (fullWhere.trim().isEmpty() ? musicWhere : fullWhere + " AND " + musicWhere));
//        }
//        if(mediaFilter.isGames()) {
//            mp.put(new Game(), gameWhere.trim().isEmpty() ? fullWhere : (fullWhere.trim().isEmpty() ? gameWhere : fullWhere + " AND " + gameWhere));
//        }
//        return ControlsHelper.getAllMediaItems(context, mp, database, mediaCount, offset, orderBy);
        return new LinkedList<>();
    }

    private static List<BaseDescriptionObject> getAllMediaItems(Context context, Map<AbstractMedia, String> mp, Database database, int mediaCount, int offset, String orderBy) {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
//        for(AbstractMedia baseMediaObject : database.getObjectList(mp, mediaCount, offset, orderBy)) {
//            if(baseMediaObject instanceof Book) {
//                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
//                try {
//                    if(baseMediaObject.getCover() != null) {
//                        baseDescriptionObject.setCover(baseMediaObject.getCover());
//                    } else {
//                        baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_book));
//                    }
//                } catch (OutOfMemoryError error) {
//                    System.gc();
//                }
//                baseDescriptionObject.setDescription(context.getString(R.string.book));
//                baseDescriptionObject.setObject(baseMediaObject);
//                baseDescriptionObjects.add(baseDescriptionObject);
//            }
//            if(baseMediaObject instanceof Movie) {
//                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
//                if(baseMediaObject.getCover() != null) {
//                    baseDescriptionObject.setCover(baseMediaObject.getCover());
//                } else {
//                    baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_movie));
//                }
//                baseDescriptionObject.setDescription(context.getString(R.string.movie));
//                baseDescriptionObject.setObject(baseMediaObject);
//                baseDescriptionObjects.add(baseDescriptionObject);
//            }
//            if(baseMediaObject instanceof Album) {
//                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
//                if(baseMediaObject.getCover() != null) {
//                    baseDescriptionObject.setCover(baseMediaObject.getCover());
//                } else {
//                    baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_music));
//                }
//                baseDescriptionObject.setDescription(context.getString(R.string.album));
//                baseDescriptionObject.setObject(baseMediaObject);
//                baseDescriptionObjects.add(baseDescriptionObject);
//            }
//            if(baseMediaObject instanceof Game) {
//                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
//                if(baseMediaObject.getCover() != null) {
//                    baseDescriptionObject.setCover(baseMediaObject.getCover());
//                } else {
//                    baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_game));
//                }
//                baseDescriptionObject.setDescription(context.getString(R.string.game));
//                baseDescriptionObject.setObject(baseMediaObject);
//                baseDescriptionObjects.add(baseDescriptionObject);
//            }
//        }

        return baseDescriptionObjects;
    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);

            Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(drawable).getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } catch (Exception ex) {
            return null;
        }
    }

    private static BaseDescriptionObject setItem(Context context, AbstractMedia baseMediaObject) {
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
//        if(baseMediaObject.isLendOut()) {
//            baseDescriptionObject.setTitle(baseMediaObject.getTitle() + " (" + context.getString(R.string.library_lendOut) + ")");
//        } else {
//            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
//        }
        return baseDescriptionObject;
    }
}
