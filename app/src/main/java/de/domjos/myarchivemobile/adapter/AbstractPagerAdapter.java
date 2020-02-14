/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.AbstractFragment;
import de.domjos.myarchivemobile.fragments.MediaGeneralFragment;

public abstract class AbstractPagerAdapter<T> extends FragmentStatePagerAdapter {
    Context context;
    private FragmentManager fragmentManager;

    AbstractPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fm;
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

    private String getFragmentTag(int pos){
        return "android:switcher:"+ R.id.viewPager+":"+pos;
    }

    Fragment getFragment(int position, AbstractFragment abstractFragment) {
        Fragment fragment = this.fragmentManager.findFragmentByTag(this.getFragmentTag(position));
        if(fragment != null) {
            if(fragment.getClass().isInstance(abstractFragment)) {
                abstractFragment = (AbstractFragment) fragment;
                this.fragmentManager.beginTransaction().detach(abstractFragment).attach(abstractFragment).commit();
            } else {
                FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
                abstractFragment = (AbstractFragment) this.fragmentManager.getFragmentFactory().instantiate(ClassLoader.getSystemClassLoader(), abstractFragment.getClass().getName());
                fragmentTransaction.add(abstractFragment, this.getFragmentTag(position));
                fragmentTransaction.detach(abstractFragment).attach(abstractFragment).commit();
            }
        }
        return abstractFragment;
    }
}
