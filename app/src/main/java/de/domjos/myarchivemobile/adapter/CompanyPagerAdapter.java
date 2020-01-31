package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.fragments.CompanyFragment;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaListFragment;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class CompanyPagerAdapter extends AbstractPagerAdapter<Company> {
    private FragmentManager fragmentManager;
    private CompanyFragment companyFragment;
    private MediaCoverFragment<Company> mediaCoverFragment;
    private MediaListFragment mediaListFragment;
    private Context context;

    public CompanyPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, context);

        this.context = context;
        this.fragmentManager = fm;

        this.companyFragment = new CompanyFragment();
        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaListFragment = new MediaListFragment();

        this.companyFragment.setAbstractPagerAdapter(this);
        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaListFragment.setAbstractPagerAdapter(this);
    }

    @Override
    public void changeMode(boolean editMode) {
        this.companyFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaListFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Company mediaObject) {
        try {
            this.companyFragment.setMediaObject(mediaObject);
            this.mediaCoverFragment.setMediaObject(mediaObject);

            List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getObjects(mediaObject.getTable(), mediaObject.getId())) {
                baseDescriptionObjects.add(ControlsHelper.convertMediaToDescriptionObject(baseMediaObject, this.context));
            }
            this.mediaListFragment.setMediaObject(baseDescriptionObjects);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    @Override
    public Company getMediaObject() {
        Company company = this.companyFragment.getMediaObject();
        company.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        return company;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.companyFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaCoverFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaListFragment.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = this.fragmentManager.findFragmentByTag(this.getFragmentTag(position));
                if(fragment!=null) {
                    if(fragment instanceof CompanyFragment) {
                        this.companyFragment = (CompanyFragment) fragment;
                        this.fragmentManager.beginTransaction().detach(this.companyFragment).attach(this.companyFragment).commit();
                    }
                }
                return this.companyFragment;
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
                    if(fragment instanceof MediaListFragment) {
                        this.mediaListFragment = (MediaListFragment) fragment;
                        this.fragmentManager.beginTransaction().detach(this.mediaListFragment).attach(this.mediaListFragment).commit();
                    }
                }
                return this.mediaListFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Validator initValidator() {
        Validator validator = new Validator(super.context, R.mipmap.ic_launcher_round);
        validator = this.companyFragment.initValidation(validator);
        validator = this.mediaCoverFragment.initValidation(validator);
        return this.mediaListFragment.initValidation(validator);
    }
}
