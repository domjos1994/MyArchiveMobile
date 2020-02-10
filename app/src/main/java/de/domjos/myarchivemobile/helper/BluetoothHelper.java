package de.domjos.myarchivemobile.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class BluetoothHelper {
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 568;
    private DataOutputStream os;

    public BluetoothHelper(Activity activity) {
        this.context = activity;
        this.bluetoothAdapter = this.getBluetoothAdapter();
    }

    public void getDevices(ArrayAdapter<Device> devices) {
        if(this.bluetoothAdapter != null) {
            if(!this.bluetoothAdapter.isEnabled()) {
                List<Device> bondedDevices = this.getBondedDevices();
                this.bluetoothAdapter.startDiscovery();
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if(bluetoothDevice != null) {
                                Device device = new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress());
                                for(Device tmp : bondedDevices) {
                                    if(tmp.getAddress().equals(device.getAddress())) {
                                        device.setBonded(true);
                                        break;
                                    }
                                }
                                devices.add(device);
                            }
                        }
                    }
                };

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                this.context.registerReceiver(broadcastReceiver, filter);
            }
        }
    }

    public void pair(Device device) throws Exception {
        if(this.bluetoothAdapter != null) {
            if(!this.bluetoothAdapter.isEnabled()) {
                BluetoothDevice bluetoothDevice = this.bluetoothAdapter.getRemoteDevice(device.getAddress());

                if(device.isBonded()) {
                    this.removeBond(bluetoothDevice);
                } else {
                    this.createBond(bluetoothDevice);
                }
            }
        }
    }

    public void enableBluetooth() {
        if(this.bluetoothAdapter != null) {
            if(!this.bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity)this.context).startActivityForResult(enableBtIntent, BluetoothHelper.REQUEST_ENABLE_BT);
            }
        }
    }

    public boolean isBluetoothAvailable() {
        return this.bluetoothAdapter!=null;
    }

    public void sendData() {
        if(this.bluetoothAdapter != null) {
            if(!this.bluetoothAdapter.isEnabled()) {
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        try{
                            if(remoteDevice != null) {
                                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(remoteDevice.getAddress());

                                Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                                BluetoothSocket clientSocket =  (BluetoothSocket) m.invoke(device, 1);

                                Objects.requireNonNull(clientSocket).connect();

                                os = new DataOutputStream(clientSocket.getOutputStream());

                                new clientSock().start();
                            }
                        } catch (Exception e) {
                            MessageHelper.printException(e, R.mipmap.ic_launcher_round, context);
                        }
                    }
                };
                this.context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean createBond(BluetoothDevice btDevice) throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return Objects.requireNonNull(returnValue);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions", "JavaReflectionMemberAccess"})
    private boolean removeBond(BluetoothDevice btDevice) throws Exception {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        if(removeBondMethod.invoke(btDevice) != null) {
            return (Boolean) removeBondMethod.invoke(btDevice);
        }
        return false;
    }


    private BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private List<Device> getBondedDevices() {
        List<Device> devices = new LinkedList<>();
        if(this.bluetoothAdapter != null) {
            if(!this.bluetoothAdapter.isEnabled()) {
                for(BluetoothDevice bluetoothDevice : this.bluetoothAdapter.getBondedDevices()) {
                    devices.add(new Device(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                }
            }
        }
        return devices;
    }

    public class Device {
        private String name;
        private String address;
        private boolean bonded;

        public Device(String name, String address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isBonded() {
            return this.bonded;
        }

        public void setBonded(boolean bonded) {
            this.bonded = bonded;
        }

        @NonNull
        @Override
        public String toString() {
            return this.name;
        }
    }

    public class clientSock extends Thread {
        public void run () {
            try {
                os.write(MainActivity.GLOBALS.getDatabase().getBytes());
                os.flush();
            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }
}
