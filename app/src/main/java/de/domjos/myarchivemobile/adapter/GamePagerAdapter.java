package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.*;

public class GamePagerAdapter extends AbstractPagerAdapter<Game> {
    private AbstractFragment<BaseMediaObject> mediaCoverFragment;
    private AbstractFragment<BaseMediaObject> mediaGeneralFragment;
    private AbstractFragment<BaseMediaObject> mediaGameFragment;
    private AbstractFragment<BaseMediaObject> mediaPersonsCompaniesFragment;
    private Runnable runnable;
    private boolean first = true;

    public GamePagerAdapter(@NonNull FragmentManager fm, Context context, Runnable runnable) {
        super(fm, context);

        this.runnable = runnable;

        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaGameFragment = new MediaGameFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaGameFragment.setAbstractPagerAdapter(this);
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
        switch (position) {
            case 0:
                this.mediaGeneralFragment = (MediaGeneralFragment) super.getFragment(position, this.mediaGeneralFragment);
                return this.mediaGeneralFragment;
            case 1:
                this.mediaCoverFragment = (MediaCoverFragment) super.getFragment(position, this.mediaCoverFragment);
                return this.mediaCoverFragment;
            case 2:
                this.mediaPersonsCompaniesFragment = (MediaPersonsCompaniesFragment) super.getFragment(position, this.mediaPersonsCompaniesFragment);
                return this.mediaPersonsCompaniesFragment;
            case 3:
                this.mediaGameFragment = (MediaGameFragment) super.getFragment(position, this.mediaGameFragment);
                return this.mediaGameFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public void changeMode(boolean editMode) {
        this.mediaGeneralFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaGameFragment.changeMode(editMode);
        this.mediaPersonsCompaniesFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Game game) {
        this.mediaGeneralFragment.setMediaObject(game);
        this.mediaCoverFragment.setMediaObject(game);
        this.mediaPersonsCompaniesFragment.setMediaObject(game);
        this.mediaGameFragment.setMediaObject(game);
    }

    @Override
    public Game getMediaObject() {
        BaseMediaObject baseMediaObject = this.mediaGeneralFragment.getMediaObject();
        baseMediaObject.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        BaseMediaObject tmp = this.mediaPersonsCompaniesFragment.getMediaObject();
        baseMediaObject.setCompanies(tmp.getCompanies());
        baseMediaObject.setPersons(tmp.getPersons());
        Game game = (Game) baseMediaObject;
        Game tmpGame = (Game) this.mediaGameFragment.getMediaObject();
        game.setType(tmpGame.getType());
        game.setLength(tmpGame.getLength());
        return game;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mediaGeneralFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaCoverFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaPersonsCompaniesFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaGameFragment.onActivityResult(requestCode, resultCode, data);
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
        return this.mediaGameFragment.initValidation(validator);
    }
}
