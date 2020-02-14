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

package de.domjos.myarchivemobile.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.domjos.myarchivemobile.activities.MainActivity;

public class CheckNetwork {
    private Context context;

    public CheckNetwork(Context context) {
        this.context = context;
    }

    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Objects.requireNonNull(connectivityManager).registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            MainActivity.GLOBALS.setNetwork(true);
                        }
                        @Override
                        public void onLost(@NonNull Network network) {
                            MainActivity.GLOBALS.setNetwork(false);
                        }
                    });
                } else {
                    Objects.requireNonNull(connectivityManager).registerNetworkCallback(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback(){
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            MainActivity.GLOBALS.setNetwork(true);
                        }
                        @Override
                        public void onLost(@NonNull Network network) {
                            MainActivity.GLOBALS.setNetwork(false);
                        }
                    });
                }
            } else {
                MainActivity.GLOBALS.setNetwork(true);
            }
        }catch (Exception e){
            MainActivity.GLOBALS.setNetwork(true);
        }
    }
}
