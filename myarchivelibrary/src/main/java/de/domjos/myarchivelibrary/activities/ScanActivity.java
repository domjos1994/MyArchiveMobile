package de.domjos.myarchivelibrary.activities;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

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
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
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
        intent.putExtra("codes", txtScannerCodes.getText().toString());
        this.setResult(RESULT_OK, intent);
        this.finish();
    }
}