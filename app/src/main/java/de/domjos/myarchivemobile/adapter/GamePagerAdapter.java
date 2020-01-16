package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivemobile.fragments.AbstractFragment;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaGameFragment;
import de.domjos.myarchivemobile.fragments.MediaGeneralFragment;
import de.domjos.myarchivemobile.fragments.MediaPersonsCompaniesFragment;

public class GamePagerAdapter extends AbstractPagerAdapter<Game> {
    private AbstractFragment mediaCoverFragment;
    private AbstractFragment mediaGeneralFragment;
    private AbstractFragment mediaGameFragment;
    private AbstractFragment mediaPersonsCompaniesFragment;

    public GamePagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, context);

        this.mediaCoverFragment = new MediaCoverFragment();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaGameFragment = new MediaGameFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaGameFragment.setAbstractPagerAdapter(this);
        this.mediaPersonsCompaniesFragment.setAbstractPagerAdapter(this);
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
                return this.mediaPersonsCompaniesFragment;
            case 3:
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
}
