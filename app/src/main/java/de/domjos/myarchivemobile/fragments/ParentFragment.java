package de.domjos.myarchivemobile.fragments;

import androidx.fragment.app.Fragment;

public abstract class ParentFragment extends Fragment {

    public abstract void setCodes(String codes, String label);

    public abstract void reload(String search, boolean reload);

    public void changeMode(boolean editMode, boolean selected) {

    }

    public abstract void select();
}
