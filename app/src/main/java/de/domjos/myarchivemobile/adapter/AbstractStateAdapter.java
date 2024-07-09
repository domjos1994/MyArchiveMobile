package de.domjos.myarchivemobile.adapter;

import android.content.Context;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.domjos.customwidgets.utils.Validator;

public abstract class AbstractStateAdapter<T> extends FragmentStateAdapter {
    protected Context context;

    public AbstractStateAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity);

        this.context = context;
    }

    public abstract void changeMode(boolean editMode);

    public abstract void setMediaObject(T mediaObject);
    public abstract T getMediaObject();

    public abstract void onResult(ActivityResult result);

    public abstract Validator initValidator();
}
