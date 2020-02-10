package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.helper.BluetoothHelper;

public class DeviceAdapter extends ArrayAdapter<BluetoothHelper.Device> {
    private Context context;
    private OnCheckedListener onCheckedListener;

    public DeviceAdapter(@NonNull Context context, @NonNull OnCheckedListener onCheckedListener) {
        super(context, R.layout.device_item);
        this.context = context;
        this.onCheckedListener = onCheckedListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = this.getRowView(this.context, parent);
        BluetoothHelper.Device device = this.getItem(position);

        TextView lblTitle = view.findViewById(R.id.lblTitle);
        TextView lblSubTitle = view.findViewById(R.id.lblSubTitle);
        ToggleButton btn = view.findViewById(R.id.btnToggled);

        if(device != null) {
            if(lblTitle != null) {
                lblTitle.setText(device.getName());
            }

            if(lblSubTitle != null) {
                lblSubTitle.setText(device.getAddress());
            }

            if(btn != null) {
                btn.setChecked(device.isBonded());
                btn.setOnCheckedChangeListener((compoundButton, b) -> this.onCheckedListener.setCheckedChange(device, b));
            }
        }

        return view;
    }


    private View getRowView(Context context, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater!=null) {
            return inflater.inflate(R.layout.device_item, parent, false);
        }
        return new View(context);
    }

    @FunctionalInterface
    public interface OnCheckedListener {
        void setCheckedChange(BluetoothHelper.Device device, boolean state);
    }
}
