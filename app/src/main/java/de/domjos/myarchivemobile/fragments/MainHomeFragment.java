package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class MainHomeFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvMedia;
    private String search;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_home, container, false);
        this.lvMedia = root.findViewById(R.id.lvMedia);

        this.lvMedia.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reload();
            }
        });

        this.reload();
        return root;
    }

    private void reload() {
        try {
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    this.search = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                this.search = "";
            }

            this.lvMedia.getAdapter().clear();
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getBooks(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(this.getString(R.string.book));
                baseDescriptionObject.setObject(baseMediaObject);
                this.lvMedia.getAdapter().add(baseDescriptionObject);
            }
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getMovies(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(this.getString(R.string.movie));
                baseDescriptionObject.setObject(baseMediaObject);
                this.lvMedia.getAdapter().add(baseDescriptionObject);
            }
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getAlbums(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(this.getString(R.string.album));
                baseDescriptionObject.setObject(baseMediaObject);
                this.lvMedia.getAdapter().add(baseDescriptionObject);
            }
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getGames(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(this.getString(R.string.game));
                baseDescriptionObject.setObject(baseMediaObject);
                this.lvMedia.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String parent) {

    }

    @Override
    public void reload(String search, boolean reload) {
        this.search = search;

        if(reload) {
            this.reload();
        }
    }
}