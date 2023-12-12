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

package de.domjos.myarchivelibrary.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.R;

/**
 * This sample performs continuous scanning, displaying the barcode and source image whenever
 * a barcode is scanned.
 */
public class ScanActivity extends AbstractActivity {
    private DecoratedBarcodeView barcodeView;
    private EditText txtScannerCodes;
    private BeepManager beepManager;
    private String lastText;
    private ImageButton cmdScannerSave;
    private String parent;
    private boolean single;

    public ScanActivity() {
        super(R.layout.scan_activity);
    }

    @Override
    protected void initActions() {
        this.cmdScannerSave.setOnClickListener(event -> this.finishAction());
    }

    @Override
    protected void initControls() {
        this.barcodeView = this.findViewById(R.id.barcode_scanner);
        this.txtScannerCodes = this.findViewById(R.id.txtScannerCodes);
        this.cmdScannerSave = this.findViewById(R.id.cmdScannerSave);
        BarcodeCallback callback = new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() == null || result.getText().equals(lastText)) {
                    return;
                }

                lastText = result.getText();
                barcodeView.setStatusText(result.getText());
                beepManager.playBeepSoundAndVibrate();

                if(single) {
                    txtScannerCodes.setText(result.getText());
                    finishAction();
                } else {
                    if(!txtScannerCodes.getText().toString().trim().isEmpty()) {
                        txtScannerCodes.setText(String.format("%s%n%s", txtScannerCodes.getText().toString(), result.getText()));
                    } else {
                        txtScannerCodes.setText(result.getText());
                    }

                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints){}
        };

        this.barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory());
        this.barcodeView.initializeFromIntent(getIntent());

        if(this.single) {
            this.barcodeView.decodeSingle(callback);
        } else {
            this.barcodeView.decodeContinuous(callback);
        }

        this.beepManager = new BeepManager(this);

        this.parent = this.getIntent().getStringExtra("parent");
        this.single = this.getIntent().getBooleanExtra("single", false);
        boolean qr = this.getIntent().getBooleanExtra("qr", false);
        if(qr) {
            Collection<BarcodeFormat> formats = new LinkedHashSet<>();
            formats.add(BarcodeFormat.QR_CODE);
            this.barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats, null, "UTF-8", false));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.barcodeView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(!this.txtScannerCodes.getText().toString().trim().isEmpty()) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        super.onBackPressed();
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.scanner_dialog_message);
            builder.setPositiveButton(R.string.scanner_dialog_positive, dialogClickListener);
            builder.setNegativeButton(R.string.scanner_dialog_negative, dialogClickListener);
            builder.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.controls, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String name = "codes.txt";
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File apiFile = new File(folder + File.separatorChar + name);

        int itemId = item.getItemId();
        if (itemId == R.id.menGetFromFile) {
            if(!apiFile.exists()) {
                MessageHelper.printMessage(this.getString(R.string.export_not_exists), R.drawable.icon_export, this);
            } else {
                try {
                    FileInputStream fileInputStream = new FileInputStream(apiFile);
                    String content = ConvertHelper.convertStreamToString(fileInputStream);
                    fileInputStream.close();
                    this.txtScannerCodes.setText(content);
                    MessageHelper.printMessage(this.getString(R.string.export_success), R.drawable.icon_export, this);
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.drawable.icon_export, this);
                }

            }
        } else if (itemId == R.id.menSaveToFile) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(apiFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(this.txtScannerCodes.getText().toString());
                outputStreamWriter.close();
                fileOutputStream.flush();
                fileOutputStream.close();
                MessageHelper.printMessage(this.getString(R.string.export_success), R.drawable.icon_export, this);
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.drawable.icon_export, this);
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void finishAction() {
        Intent intent = new Intent();
        intent.putExtra("parent", this.parent);
        intent.putExtra("codes", this.txtScannerCodes.getText().toString());
        this.setResult(RESULT_OK, intent);
        this.finish();
    }
}