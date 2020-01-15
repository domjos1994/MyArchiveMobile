package de.domjos.myarchivemobile.fragments;

import androidx.fragment.app.Fragment;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.adapter.AbstractPagerAdapter;

public abstract class AbstractFragment extends Fragment {
    protected AbstractPagerAdapter abstractPagerAdapter;

    public abstract void setMediaObject(BaseMediaObject baseMediaObject);
    public abstract BaseMediaObject getMediaObject();

    public void setAbstractPagerAdapter(AbstractPagerAdapter abstractPagerAdapter) {
        this.abstractPagerAdapter = abstractPagerAdapter;
    }

    public abstract void changeMode(boolean editMode);

    public abstract void initValidation();
}
