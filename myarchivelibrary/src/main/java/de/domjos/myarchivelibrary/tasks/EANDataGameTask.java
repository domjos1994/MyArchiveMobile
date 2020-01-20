package de.domjos.myarchivelibrary.tasks;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.R;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.services.EANDataService;

public class EANDataGameTask extends AbstractTask<String, Void, List<Game>> {
    private String key;

    public EANDataGameTask(Activity activity, boolean showNotifications, int icon, String key) {
        super(activity, R.string.service_ean_data_search, R.string.service_ean_data_search_content, showNotifications, icon);
        this.key = key;
    }


    @Override
    protected void before() {

    }

    @Override
    protected List<Game> doInBackground(String... strings) {
        LinkedList<Game> games = new LinkedList<>();

        for(String code : strings) {
            try {
                EANDataService eanDataService = new EANDataService(code, this.key);
                Game game = eanDataService.executeGame();

                if(game != null) {
                    game.setCode(code);
                    games.add(game);
                }
            } catch (Exception ex) {
                super.printException(ex);
            }
        }

        return games;
    }
}
