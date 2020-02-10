package de.domjos.myarchivemobile.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.DialogFragment;

import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.services.AudioDBWebservice;
import de.domjos.myarchivelibrary.services.GoogleBooksService;
import de.domjos.myarchivelibrary.services.MovieDBWebService;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

import static android.app.Activity.RESULT_OK;

public class MediaDialog extends DialogFragment {
    private BaseDescriptionObject currentObject;

    public static MediaDialog newInstance(String search, String type) {
        MediaDialog mediaDialog = new MediaDialog();
        Bundle args = new Bundle();
        args.putString("search", search);
        args.putString("type", type);
        mediaDialog.setArguments(args);

        return mediaDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.media_dialog, container, true);

        Activity activity = Objects.requireNonNull(this.getActivity());
        Bundle arguments = Objects.requireNonNull(this.getArguments());
        String type = arguments.getString("type");
        String search = arguments.getString("search");

        EditText txtSearch = v.findViewById(R.id.txtSearch);
        ImageButton cmdSearch = v.findViewById(R.id.cmdSearch);
        SwipeRefreshDeleteList lvMedia = v.findViewById(R.id.lvSuggestions);
        ImageButton cmdSave = v.findViewById(R.id.cmdSave);
        txtSearch.setText(search);

        this.reload(activity, type, search, lvMedia);

        cmdSearch.setOnClickListener(view -> this.reload(activity, type, txtSearch.getText().toString(), lvMedia));

        lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> this.currentObject = listObject);

        cmdSave.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("id", ((BaseMediaObject)this.currentObject.getObject()).getId());
            intent.putExtra("type", type);
            intent.putExtra("description", ((BaseMediaObject) this.currentObject.getObject()).getDescription());
            Objects.requireNonNull(this.getTargetFragment()).onActivityResult(this.getTargetRequestCode(), RESULT_OK, intent);
            this.dismiss();
        });

        return v;
    }

    private void reload(Activity activity, String type, String search, SwipeRefreshDeleteList lvMedia) {
        if(type != null) {
            new Thread(()->{
                try {
                    List<BaseMediaObject> objects;
                    if(type.equals(this.getString(R.string.movie))) {
                        objects = MovieDBWebService.getMedia(activity, search, MainActivity.GLOBALS.getSettings().getMovieDBKey());
                    } else if(type.equals(this.getString(R.string.album))) {
                        objects = AudioDBWebservice.getMedia(search);
                    } else if(type.equals(this.getString(R.string.book))) {
                        objects = GoogleBooksService.getMedia(search, MainActivity.GLOBALS.getSettings().getGoogleBooksKey());
                    } else {
                        objects = null;
                    }

                    activity.runOnUiThread(()->lvMedia.getAdapter().clear());
                    if(objects != null) {
                        for(BaseMediaObject baseMediaObject : objects) {
                            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
                            baseDescriptionObject.setCover(baseMediaObject.getCover());
                            baseDescriptionObject.setDescription(Converter.convertDateToString(baseMediaObject.getReleaseDate(), this.getString(R.string.sys_date_format)));
                            baseDescriptionObject.setObject(baseMediaObject);
                            activity.runOnUiThread(()->lvMedia.getAdapter().add(baseDescriptionObject));
                        }
                    }
                } catch (Exception ex) {
                    activity.runOnUiThread(()->MessageHelper.printException(ex, R.mipmap.ic_launcher_round, activity));
                }
            }).start();
        }
    }
}
