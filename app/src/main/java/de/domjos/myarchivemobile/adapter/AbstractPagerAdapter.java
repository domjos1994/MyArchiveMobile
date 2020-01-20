package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;

public abstract class AbstractPagerAdapter<T> extends FragmentStatePagerAdapter {
    Context context;

    AbstractPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    public abstract void changeMode(boolean editMode);

    public abstract void setMediaObject(T mediaObject);
    public abstract T getMediaObject();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract Validator initValidator();
}
