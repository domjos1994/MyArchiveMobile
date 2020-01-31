package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.AbstractFragment;
import de.domjos.myarchivemobile.fragments.MediaAlbumFragment;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaGeneralFragment;
import de.domjos.myarchivemobile.fragments.MediaPersonsCompaniesFragment;

public class AlbumPagerAdapter extends AbstractPagerAdapter<Album> {
    private FragmentManager fragmentManager;
    private AbstractFragment<BaseMediaObject> mediaCoverFragment;
    private AbstractFragment<BaseMediaObject> mediaGeneralFragment;
    private AbstractFragment<BaseMediaObject> mediaAlbumFragment;
    private AbstractFragment<BaseMediaObject> mediaPersonsCompaniesFragment;
    private Runnable runnable;
    private boolean first = true;

    public AlbumPagerAdapter(@NonNull FragmentManager fm, Context context, Runnable runnable) {
        super(fm, context);

        this.fragmentManager = fm;
        this.runnable = runnable;

        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaAlbumFragment = new MediaAlbumFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaAlbumFragment.setAbstractPagerAdapter(this);
        this.mediaPersonsCompaniesFragment.setAbstractPagerAdapter(this);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        super.finishUpdate(container);

        if(this.first) {
            this.runnable.run();
            this.first = false;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = this.fragmentManager.findFragmentByTag(this.getFragmentTag(position));
                if(fragment!=null) {
                    if(fragment instanceof MediaGeneralFragment) {
                        this.mediaGeneralFragment = (MediaGeneralFragment) fragment;
                        this.fragmentManager.beginTransaction().detach(this.mediaGeneralFragment).attach(this.mediaGeneralFragment).commit();
                    }
                }
                return this.mediaGeneralFragment;
            case 1:
                fragment = this.fragmentManager.findFragmentByTag(this.getFragmentTag(position));
                if(fragment!=null) {
                    if(fragment instanceof MediaCoverFragment) {
                        this.mediaCoverFragment = (MediaCoverFragment) fragment;
                        this.fragmentManager.beginTransaction().detach(this.mediaCoverFragment).attach(this.mediaCoverFragment).commit();
                    }
                }
                return this.mediaCoverFragment;
            case 2:
                fragment = this.fragmentManager.findFragmentByTag(this.getFragmentTag(position));
                if(fragment!=null) {
                    if(fragment instanceof  MediaPersonsCompaniesFragment) {
                        this.mediaPersonsCompaniesFragment = (MediaPersonsCompaniesFragment) fragment;
                        this.fragmentManager.beginTransaction().detach(this.mediaPersonsCompaniesFragment).attach(this.mediaPersonsCompaniesFragment).commit();
                    }
                }
                return this.mediaPersonsCompaniesFragment;
            case 3:
                fragment = this.fragmentManager.findFragmentByTag(this.getFragmentTag(position));
                if(fragment!=null) {
                    if(fragment instanceof MediaAlbumFragment) {
                        this.mediaAlbumFragment = (MediaAlbumFragment) fragment;
                        this.fragmentManager.beginTransaction().detach(this.mediaAlbumFragment).attach(this.mediaAlbumFragment).commit();
                    }
                }
                return this.mediaAlbumFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public void changeMode(boolean editMode) {
        this.mediaGeneralFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaAlbumFragment.changeMode(editMode);
        this.mediaPersonsCompaniesFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Album album) {
        this.mediaGeneralFragment.setMediaObject(album);
        this.mediaCoverFragment.setMediaObject(album);
        this.mediaPersonsCompaniesFragment.setMediaObject(album);
        this.mediaAlbumFragment.setMediaObject(album);
    }

    @Override
    public Album getMediaObject() {
        BaseMediaObject baseMediaObject = this.mediaGeneralFragment.getMediaObject();
        baseMediaObject.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        BaseMediaObject tmp = this.mediaPersonsCompaniesFragment.getMediaObject();
        baseMediaObject.setCompanies(tmp.getCompanies());
        baseMediaObject.setPersons(tmp.getPersons());
        Album album = (Album) baseMediaObject;
        Album tmpAlbum = (Album) this.mediaAlbumFragment.getMediaObject();
        album.setType(tmpAlbum.getType());
        album.setNumberOfDisks(tmpAlbum.getNumberOfDisks());
        return album;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mediaGeneralFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaCoverFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaPersonsCompaniesFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaAlbumFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return " ";
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Validator initValidator() {
        Validator validator = new Validator(super.context, R.mipmap.ic_launcher_round);
        validator = this.mediaGeneralFragment.initValidation(validator);
        validator = this.mediaCoverFragment.initValidation(validator);
        validator = this.mediaPersonsCompaniesFragment.initValidation(validator);
        return this.mediaAlbumFragment.initValidation(validator);
    }
}
