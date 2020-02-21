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

package de.domjos.myarchivemobile.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.activities.ScanActivity;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.helper.PDFHelper;
import de.domjos.myarchivemobile.settings.Settings;

import static android.app.Activity.RESULT_OK;

public class MainApiFragment extends ParentFragment {
    private CheckBox chkApiBooks, chkApiGames, chkApiMusic, chkApiMovies;
    private EditText txtApiName, txtApiPath;
    private String[] typeArray, formatArray;
    private ImageView ivApiQr;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_api, container, false);

        Activity activity = Objects.requireNonNull(this.getActivity());

        this.typeArray = activity.getResources().getStringArray(R.array.api_type);
        this.formatArray = activity.getResources().getStringArray(R.array.api_format);

        Spinner spApiType = root.findViewById(R.id.spApiType);
        Spinner spApiFormat = root.findViewById(R.id.spApiFormat);

        this.txtApiPath = root.findViewById(R.id.txtApiPath);
        this.txtApiName = root.findViewById(R.id.txtApiName);
        Button cmdApiPath = root.findViewById(R.id.cmdApiPath);

        this.chkApiBooks = root.findViewById(R.id.chkApiBooks);
        this.chkApiMovies = root.findViewById(R.id.chkApiMovies);
        this.chkApiMusic = root.findViewById(R.id.chkApiMusic);
        this.chkApiGames = root.findViewById(R.id.chkApiGames);

        this.ivApiQr = root.findViewById(R.id.ivApiQR);

        ImageButton cmdApi = root.findViewById(R.id.cmdApi);
        Button cmdApiQRCode = root.findViewById(R.id.cmdApiQR);

        spApiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ivApiQr.setImageBitmap(null);
                if(spApiType.getSelectedItem().toString().equals(typeArray[1])) {
                    spApiFormat.setSelection(1);
                    spApiFormat.setVisibility(View.INVISIBLE);
                    txtApiName.setText(typeArray[1].toLowerCase());
                    cmdApiQRCode.setText(R.string.api_qr_code_open);
                } else {
                    spApiFormat.setVisibility(View.VISIBLE);
                    txtApiName.setText(typeArray[0].toLowerCase());
                    cmdApiQRCode.setText(R.string.api_qr_code_save);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spApiFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ivApiQr.setImageBitmap(null);
                boolean first = spApiFormat.getSelectedItem().toString().equals(formatArray[0]);

                chkApiBooks.setVisibility(first ? View.VISIBLE : View.GONE);
                chkApiMovies.setVisibility(first ? View.VISIBLE : View.GONE);
                chkApiMusic.setVisibility(first ? View.VISIBLE : View.GONE);
                chkApiGames.setVisibility(first ? View.VISIBLE : View.GONE);
                cmdApiQRCode.setVisibility(first ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        cmdApiPath.setOnClickListener(event -> {
            ivApiQr.setImageBitmap(null);
            List<String> filter = new LinkedList<>();
            if(spApiFormat.getSelectedItem().toString().equals(this.formatArray[0])) {
                filter.add("pdf");
                filter.add("PDF");
            } else {
                filter.add("db");
                filter.add(this.formatArray[1]);
            }

            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, spApiType.getSelectedItem().toString().equals(this.typeArray[0]), filter, activity);
            dialog.setDialogSelectionListener(files -> {
                if(files != null && files.length != 0) {
                    this.txtApiPath.setText(files[0]);
                }
            });
            dialog.show();
        });

        cmdApi.setOnClickListener(event -> {
            try {
                if(spApiType.getSelectedItem().toString().equals(this.typeArray[0])) {
                    if(spApiFormat.getSelectedItem().toString().equals(this.formatArray[0])) {
                        this.exportToPDF();
                    } else {
                        this.exportToDatabase();
                    }
                } else {
                    this.importToDatabase();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
            }
        });

        cmdApiQRCode.setOnClickListener(view -> {
            boolean save = cmdApiQRCode.getText().toString().equals(this.getString(R.string.api_qr_code_save));
            List<String> filter = Arrays.asList("jpg", "JPG");

            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, save, filter, activity);
            dialog.setDialogSelectionListener(files -> {
                if(files != null && files.length != 0) {
                    String path = files[0];
                    if (save) {
                        byte[] content = ConvertHelper.convertDrawableToByteArray(this.ivApiQr.getDrawable());
                        try (FileOutputStream fOut = new FileOutputStream(path + File.separatorChar + "qr.jpg")) {
                            fOut.write(content);
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                        }
                    } else {
                        byte[] content = new byte[(int) new File(path).length()];
                        try (BufferedInputStream fIn = new BufferedInputStream(new FileInputStream(path))) {
                            fIn.read(content, 0, content.length);
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                        }
                        ivApiQr.setImageBitmap(ConvertHelper.convertByteArrayToBitmap(content));
                    }
                }
            });
            dialog.show();
        });

        return root;
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    @Override
    public void reload(String search, boolean reload) {

    }

    @Override
    public void select() {

    }

    private void exportToPDF() throws ParseException {
        String path = this.txtApiPath.getText().toString() + File.separatorChar  + this.txtApiName.getText().toString() + ".pdf";
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
        if(chkApiBooks.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getBooks(""));
        }
        if(chkApiMovies.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getMovies(""));
        }
        if(chkApiMusic.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getAlbums(""));
        }
        if(chkApiGames.isChecked()) {
            baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getGames(""));
        }
        PDFHelper pdfHelper = new PDFHelper(path, Objects.requireNonNull(this.getActivity()));
        pdfHelper.execute(baseMediaObjects);
    }

    private void exportToDatabase() throws Exception {
        String path = this.txtApiPath.getText().toString() + File.separatorChar  + this.txtApiName.getText().toString() + ".db";
        String pwd = MainActivity.GLOBALS.getDatabase().copyDatabase(path);
        this.ivApiQr.setImageBitmap(ScanActivity.generateQRCode(MainActivity.GLOBALS.getSettings().getSetting(Settings.DB_PASSWORD, pwd, true)));
    }

    private void importToDatabase() throws Exception {
        if(this.ivApiQr.getDrawable() != null) {
            String path = this.txtApiPath.getText().toString();
            byte[] bytes = ConvertHelper.convertDrawableToByteArray(this.ivApiQr.getDrawable());
            String codes = ScanActivity.readQRCode(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            MainActivity.GLOBALS.getSettings().setSetting(Settings.DB_PASSWORD, codes, true);
            MainActivity.GLOBALS.getDatabase().getDatabase(path);
            MainApiFragment.triggerRebirth(Objects.requireNonNull(this.getContext()));
        } else {
            Intent intent = new Intent(this.getContext(), ScanActivity.class);
            intent.putExtra("parent", "");
            intent.putExtra("single", true);
            intent.putExtra("qr", true);
            startActivityForResult(intent, 965);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String path = this.txtApiPath.getText().toString();
            if(resultCode == RESULT_OK && requestCode == 965) {
                String codes = data.getStringExtra("codes");
                MainActivity.GLOBALS.getSettings().setSetting(Settings.DB_PASSWORD, codes, true);
                MainActivity.GLOBALS.getDatabase().getDatabase(path);
                MainApiFragment.triggerRebirth(Objects.requireNonNull(this.getContext()));
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = Objects.requireNonNull(intent).getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}
