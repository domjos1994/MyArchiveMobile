package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.EANDataService;

public class EANDataAlbumTask extends AbstractTask<String, Void, List<Album>> {
    private String key;

    public EANDataAlbumTask(Activity activity, boolean showNotifications, int icon, String key) {
        super(activity, R.string.service_ean_data_search, R.string.service_ean_data_search_content, showNotifications, icon);
        this.key = key;
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Album> doInBackground(String... strings) {
        LinkedList<Album> albums = new LinkedList<>();

        for(String code : strings) {
            try {
                EANDataService eanDataService = new EANDataService(code, this.key, super.getContext());
                Album album = eanDataService.executeAlbum();

                if(album != null) {
                    album.setCode(code);
                    albums.add(album);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return albums;
    }
}
