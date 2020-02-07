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
