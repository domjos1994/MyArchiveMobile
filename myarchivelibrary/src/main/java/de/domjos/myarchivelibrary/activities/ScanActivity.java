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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
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

    public static Bitmap generateQRCode(String content) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 400, 400);
        return ScanActivity.matrixToBitmap(bitMatrix);
    }

    public static String readQRCode(Bitmap bitmap) throws Exception {
        QRCodeReader qrCodeReader = new QRCodeReader();
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        LuminanceSource luminanceSource = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        HybridBinarizer hybridBinarizer = new HybridBinarizer(luminanceSource);
        BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
        Result result = qrCodeReader.decode(binaryBitmap);
        return result.getText();
    }

    private static Bitmap matrixToBitmap(BitMatrix bitMatrix) {
        final int w = bitMatrix.getWidth();
        final int h = bitMatrix.getHeight();
        final int[] pixels = new int[w * h];

        for (int y = 0; y < h; y++) {
            final int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        final Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 400, 0, 0, w, h);

        return bitmap;
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

    private void finishAction() {
        Intent intent = new Intent();
        intent.putExtra("parent", this.parent);
        intent.putExtra("codes", this.txtScannerCodes.getText().toString());
        this.setResult(RESULT_OK, intent);
        this.finish();
    }
}