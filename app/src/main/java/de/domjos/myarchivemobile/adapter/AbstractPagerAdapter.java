package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public abstract class AbstractPagerAdapter<T extends BaseMediaObject> extends FragmentStatePagerAdapter {
    protected Context context;

    public AbstractPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    public abstract void changeMode(boolean editMode);

    public abstract void setMediaObject(T mediaObject);
    public abstract T getMediaObject();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
