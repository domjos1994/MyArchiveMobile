package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.helper.PDFHelper;

public class MainExportFragment extends ParentFragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_export, container, false);

        EditText txtExportPath = root.findViewById(R.id.txtExportPath);
        EditText txtExportName = root.findViewById(R.id.txtExportName);
        Button cmdExportPath = root.findViewById(R.id.cmdExportPath);
        cmdExportPath.setOnClickListener(event -> {
            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, true, Arrays.asList("pdf", "PDF"), MainExportFragment.this.getActivity());
            dialog.setDialogSelectionListener(files -> {
                if(files != null) {
                    if(files.length != 0) {
                        txtExportPath.setText(files[0]);
                    }
                }
            });
            dialog.show();
        });

        CheckBox chkExportBooks = root.findViewById(R.id.chkExportBooks);
        CheckBox chkExportMovies = root.findViewById(R.id.chkExportMovies);
        CheckBox chkExportMusic = root.findViewById(R.id.chkExportMusic);
        CheckBox chkExportGames = root.findViewById(R.id.chkExportGames);

        ImageButton cmdExport = root.findViewById(R.id.cmdExport);
        cmdExport.setOnClickListener(event -> {
            try {
                String path = txtExportPath.getText().toString() + File.separatorChar  + txtExportName.getText().toString() + ".pdf";
                List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
                if(chkExportBooks.isChecked()) {
                    baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getBooks(""));
                }
                if(chkExportMovies.isChecked()) {
                    baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getMovies(""));
                }
                if(chkExportMusic.isChecked()) {
                    baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getAlbums(""));
                }
                if(chkExportGames.isChecked()) {
                    baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase().getGames(""));
                }
                PDFHelper pdfHelper = new PDFHelper(path, Objects.requireNonNull(this.getActivity()));
                pdfHelper.execute(baseMediaObjects);
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
            }
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
}
