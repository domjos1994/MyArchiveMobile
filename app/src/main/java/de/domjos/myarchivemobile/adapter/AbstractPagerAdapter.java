package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivemobile.R;

public abstract class AbstractPagerAdapter<T> extends FragmentPagerAdapter {
    Context context;

    AbstractPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        super.finishUpdate(container);

        for(int i = 0; i<=this.getCount()-1; i++) {
            this.getItem(i);
        }
    }

    public abstract void changeMode(boolean editMode);

    public abstract void setMediaObject(T mediaObject);
    public abstract T getMediaObject();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract Validator initValidator();

    protected String getFragmentTag(int pos){
        return "android:switcher:"+ R.id.viewPager+":"+pos;
    }
}
