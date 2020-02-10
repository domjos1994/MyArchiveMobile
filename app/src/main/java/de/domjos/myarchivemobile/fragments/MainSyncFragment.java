package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.DeviceAdapter;
import de.domjos.myarchivemobile.helper.BluetoothHelper;

public class MainSyncFragment extends ParentFragment {
    private ListView lvDevices;
    private ImageButton cmdBluetoothSync;

    private DeviceAdapter deviceAdapter;

    private BluetoothHelper bluetoothHelper;

    private BluetoothHelper.Device device;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_sync, container, false);
        this.initControls(root);

        this.lvDevices.setOnItemClickListener((adapterView, view, i, l) -> {
            this.device = this.deviceAdapter.getItem(i);
            if(this.device != null) {
                this.cmdBluetoothSync.setEnabled(this.device.isBonded());
            } else {
                this.cmdBluetoothSync.setEnabled(false);
            }
        });

        this.cmdBluetoothSync.setOnClickListener(view -> this.bluetoothHelper.sendData());

        return root;
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    @Override
    public void reload(String search, boolean reload) {
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        Objects.requireNonNull(this.getActivity()).getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public void select() {

    }

    private void initControls(View view) {
        TextView lblNoBluetooth = view.findViewById(R.id.lblNoBluetooth);

        this.lvDevices = view.findViewById(R.id.lvBluetoothDevices);
        this.deviceAdapter = new DeviceAdapter(Objects.requireNonNull(this.getContext()), (device, state) -> {
            try {
                this.bluetoothHelper.pair(device);
                this.deviceAdapter.clear();
                this.bluetoothHelper.getDevices(this.deviceAdapter);
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
            }
        });
        this.lvDevices.setAdapter(this.deviceAdapter);
        this.deviceAdapter.notifyDataSetChanged();

        this.cmdBluetoothSync = view.findViewById(R.id.cmdBluetoothSync);
        this.cmdBluetoothSync.setEnabled(false);

        this.bluetoothHelper = new BluetoothHelper(this.getActivity());
        this.bluetoothHelper.enableBluetooth();

        lblNoBluetooth.setVisibility(this.bluetoothHelper.isBluetoothAvailable() ? View.GONE : View.VISIBLE);
        this.bluetoothHelper.getDevices(this.deviceAdapter);
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {

    }
}
