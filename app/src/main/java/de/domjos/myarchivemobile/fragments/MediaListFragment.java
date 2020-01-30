package de.domjos.myarchivemobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;

public class MediaListFragment extends AbstractFragment<List<BaseDescriptionObject>> {
    private SwipeRefreshDeleteList lvMedia;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.media_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.lvMedia = view.findViewById(R.id.lvMedia);

        this.lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            Intent intent = new Intent();
            intent.putExtra("type", listObject.getDescription());
            intent.putExtra("id", ((BaseMediaObject) listObject.getObject()).getId());
            Activity activity = MediaListFragment.this.getActivity();
            if(activity != null) {
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    @Override
    public void setMediaObject(List<BaseDescriptionObject> baseMediaObject) {
        this.lvMedia.getAdapter().clear();
        for(BaseDescriptionObject baseDescriptionObject : baseMediaObject) {
            this.lvMedia.getAdapter().add(baseDescriptionObject);
        }
    }

    @Override
    public List<BaseDescriptionObject> getMediaObject() {
        return new LinkedList<>();
    }

    @Override
    public void changeMode(boolean editMode) {

    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
