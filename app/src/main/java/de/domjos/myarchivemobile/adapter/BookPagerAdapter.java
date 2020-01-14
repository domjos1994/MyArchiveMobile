package de.domjos.myarchivemobile.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.MediaBookFragment;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaGeneralFragment;
import de.domjos.myarchivemobile.fragments.MediaPersonsCompaniesFragment;
import de.domjos.myarchivemobile.fragments.MediaTagsFragment;

public class BookPagerAdapter extends FragmentStatePagerAdapter {
    private MediaCoverFragment mediaCoverFragment;
    private MediaGeneralFragment mediaGeneralFragment;
    private MediaBookFragment mediaBookFragment;
    private MediaTagsFragment mediaTagsFragment;
    private MediaPersonsCompaniesFragment mediaPersonsCompaniesFragment;
    private Context context;

    public BookPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.context = context;
        this.mediaCoverFragment = new MediaCoverFragment();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaBookFragment = new MediaBookFragment();
        this.mediaTagsFragment = new MediaTagsFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return this.mediaGeneralFragment;
            case 1:
                return this.mediaCoverFragment;
            case 2:
                return this.mediaTagsFragment;
            case 3:
                return this.mediaPersonsCompaniesFragment;
            case 4:
                return this.mediaBookFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return " ";
    }

    @Override
    public int getCount() {
        return 5;
    }
}
