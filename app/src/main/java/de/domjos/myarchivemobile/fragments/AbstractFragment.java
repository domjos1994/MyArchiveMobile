package de.domjos.myarchivemobile.fragments;

import androidx.fragment.app.Fragment;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.adapter.AbstractPagerAdapter;

public abstract class AbstractFragment<T> extends Fragment {
    AbstractPagerAdapter abstractPagerAdapter;

    public abstract void setMediaObject(T baseMediaObject);
    public abstract T getMediaObject();

    public void setAbstractPagerAdapter(AbstractPagerAdapter abstractPagerAdapter) {
        this.abstractPagerAdapter = abstractPagerAdapter;
    }

    public abstract void changeMode(boolean editMode);

    public abstract Validator initValidation(Validator validator);
}
