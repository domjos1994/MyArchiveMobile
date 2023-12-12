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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchiveservices.services.TextService;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.settings.Settings;
import de.domjos.myarchiveservices.customTasks.CustomAbstractTask;
import de.domjos.myarchiveservices.tasks.ExportTask;
import de.domjos.myarchiveservices.tasks.ImportTask;

import static android.app.Activity.RESULT_OK;

public class MainApiFragment extends ParentFragment {
    private final static String BOOKS = "books";
    private final static String MOVIES = "movies";
    private final static String GAMES = "games";
    private final static String MUSIC = "music";
    private final static String WEBSERVICE= "webService";
    private final static String NAME = "name";
    private final static String PATH = "path";
    private final static String TYPE = "type";
    private final static String FORMAT = "format";

    private TableLayout tblCells;
    private TableRow rowList, rowListNew;
    private CheckBox chkApiBooks, chkApiGames, chkApiMusic, chkApiMovies, chkApiWebService, chkApiDelete;
    private EditText txtApiName, txtApiPath, txtApiListNew;
    private Spinner spApiList;
    private String[] typeArray, formatArray;
    private final Map<String, Spinner> mpCells = new LinkedHashMap<>();
    private ProgressBar pbProgress;
    private TextView lblState, lblMessage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_api, container, false);

        Activity activity = this.requireActivity();

        this.typeArray = activity.getResources().getStringArray(R.array.api_type);
        this.formatArray = new String[4];
        this.formatArray[0] = "pdf";
        this.formatArray[1] = "txt";
        this.formatArray[2] = "csv";
        this.formatArray[3] = "db";


        Spinner spApiType = root.findViewById(R.id.spApiType);
        spApiType.setAdapter(new CustomSpinnerAdapter<>(this.requireActivity(), this.typeArray));
        Spinner spApiFormat = root.findViewById(R.id.spApiFormat);
        CustomSpinnerAdapter<String> formatAdapter = new CustomSpinnerAdapter<>(this.requireActivity());
        spApiFormat.setAdapter(formatAdapter);
        formatAdapter.notifyDataSetChanged();

        formatAdapter.add(this.formatArray[0]);
        formatAdapter.add(this.formatArray[1]);
        formatAdapter.add(this.formatArray[2]);
        formatAdapter.add(this.formatArray[3]);

        this.tblCells = root.findViewById(R.id.tblCells);

        this.pbProgress = root.findViewById(R.id.pbProgress);
        this.lblState = root.findViewById(R.id.lblState);
        this.lblMessage = root.findViewById(R.id.lblMessage);

        this.txtApiPath = root.findViewById(R.id.txtApiPath);
        this.txtApiName = root.findViewById(R.id.txtApiName);
        Button cmdApiPath = root.findViewById(R.id.cmdApiPath);

        this.chkApiBooks = root.findViewById(R.id.chkApiBooks);
        this.chkApiMovies = root.findViewById(R.id.chkApiMovies);
        this.chkApiMusic = root.findViewById(R.id.chkApiMusic);
        this.chkApiGames = root.findViewById(R.id.chkApiGames);
        this.chkApiWebService = root.findViewById(R.id.chkApiWebService);
        this.chkApiDelete = root.findViewById(R.id.chkApiDelete);

        this.rowList = root.findViewById(R.id.listRow);
        this.rowListNew = root.findViewById(R.id.listRowNew);
        this.txtApiListNew = root.findViewById(R.id.txtApiListNew);
        this.spApiList = root.findViewById(R.id.spApiList);
        this.rowList.setVisibility(View.GONE);
        this.rowListNew.setVisibility(View.GONE);

        try {
            this.loadFromSettings(spApiFormat, spApiType);
            this.reloadLists();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        ImageButton cmdApi = root.findViewById(R.id.cmdApi);

        spApiType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                formatAdapter.clear();
                if(spApiType.getSelectedItem().toString().equals(typeArray[0])) { // export
                    formatAdapter.add("pdf");
                    rowList.setVisibility(View.GONE);
                    rowListNew.setVisibility(View.GONE);
                    txtApiName.setVisibility(View.VISIBLE);
                } else {
                    rowList.setVisibility(View.VISIBLE);
                    rowListNew.setVisibility(View.VISIBLE);
                    txtApiName.setVisibility(View.GONE);
                }

                formatAdapter.add("txt");
                formatAdapter.add("csv");
                formatAdapter.add("db");

                hidePnlCells(spApiType.getSelectedItem().toString(), spApiFormat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spApiFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean first = spApiFormat.getSelectedItem().toString().equals(formatArray[0]);
                boolean csvOrTxt = spApiType.getSelectedItemPosition() == 0 || spApiType.getSelectedItemPosition() == 1;
                boolean imp = spApiFormat.getSelectedItemPosition() == 1;

                chkApiBooks.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiMovies.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiMusic.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiGames.setVisibility(first || csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiWebService.setVisibility(imp && csvOrTxt ? View.VISIBLE : View.GONE);
                chkApiDelete.setVisibility(imp && csvOrTxt ? View.VISIBLE : View.GONE);

                hidePnlCells(spApiType.getSelectedItem().toString(), spApiFormat.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        cmdApiPath.setOnClickListener(event -> {
            List<String> filter = new LinkedList<>();
            filter.add(spApiFormat.getSelectedItem().toString());
            boolean export = spApiType.getSelectedItem().toString().equals(this.typeArray[0]);

            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, export, filter, activity);
            dialog.setDialogSelectionListener(files -> {
                try {
                    if(files != null && files.length != 0) {
                        this.txtApiPath.setText(files[0]);

                        if(spApiFormat.getSelectedItem().toString().equals("csv") || spApiFormat.getSelectedItem().toString().equals("txt")) {
                            TextService textService = new TextService(files[0]);
                            this.fillPnlCells(textService.getHeader());
                        }
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                }
            });
            dialog.show();
        });

        cmdApi.setOnClickListener(event -> {
            try {
                if(spApiType.getSelectedItem().toString().equals(this.typeArray[0])) { // export
                    switch (spApiFormat.getSelectedItemPosition()) {
                        case 0, 1, 2 -> {
                            String path = this.txtApiPath.getText().toString() + File.separatorChar + this.txtApiName.getText().toString() + "." + spApiFormat.getSelectedItem().toString();
                            List<BaseMediaObject> baseMediaObjects = new LinkedList<>();
                            if (chkApiBooks.isChecked()) {
                                baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase(this.getActivity()).getBooks("", MainActivity.GLOBALS.getSettings(this.requireContext()).getMediaCount(), MainActivity.GLOBALS.getOffset("api")));
                            }
                            if (chkApiMovies.isChecked()) {
                                baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase(this.getActivity()).getMovies("", MainActivity.GLOBALS.getSettings(this.requireContext()).getMediaCount(), MainActivity.GLOBALS.getOffset("api")));
                            }
                            if (chkApiMusic.isChecked()) {
                                baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase(this.getActivity()).getAlbums("", MainActivity.GLOBALS.getSettings(this.requireContext()).getMediaCount(), MainActivity.GLOBALS.getOffset("api")));
                            }
                            if (chkApiGames.isChecked()) {
                                baseMediaObjects.addAll(MainActivity.GLOBALS.getDatabase(this.getActivity()).getGames("", MainActivity.GLOBALS.getSettings(this.requireContext()).getMediaCount(), MainActivity.GLOBALS.getOffset("api")));
                            }
                            ExportTask exportTask = new ExportTask(
                                    this.getActivity(), path, this.pbProgress, this.lblState,
                                    this.lblMessage, baseMediaObjects,
                                    MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications(),
                                    R.drawable.icon_notification
                            );
                            exportTask.after(o -> {
                                MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.api)), R.mipmap.ic_launcher_round, getActivity());
                                saveSettings(spApiFormat, spApiType);
                            });
                            exportTask.execute();
                        }
                        case 3 -> {
                            this.exportToDatabase();
                            MessageHelper.printMessage(String.format(this.getString(R.string.sys_success), this.getString(R.string.api)), R.mipmap.ic_launcher_round, this.getActivity());
                            this.saveSettings(spApiFormat, spApiType);
                        }
                    }
                } else {
                    switch (spApiFormat.getSelectedItemPosition()) {
                        case 0, 1 -> {
                            int selected = 0;
                            if (chkApiBooks.isChecked()) {
                                selected += 1;
                            }
                            if (chkApiMovies.isChecked()) {
                                selected += 1;
                            }
                            if (chkApiMusic.isChecked()) {
                                selected += 1;
                            }
                            if (chkApiGames.isChecked()) {
                                selected += 1;
                            }
                            if (selected == 1) {
                                if (chkApiWebService.isChecked()) {
                                    if (!MainActivity.GLOBALS.isNetwork()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
                                        builder.setTitle(R.string.api_webservice_no_network_title);
                                        builder.setMessage(R.string.api_webservice_no_network_content);
                                        builder.setIcon(R.mipmap.ic_launcher_round);
                                        builder.setNegativeButton(R.string.api_webservice_no_network_no, (dialogInterface, i) -> {
                                        });
                                        builder.setPositiveButton(R.string.api_webservice_no_network_yes, (dialogInterface, i) -> fillAndExecuteImportTask(spApiFormat, spApiType));
                                        builder.show();
                                    } else {
                                        fillAndExecuteImportTask(spApiFormat, spApiType);
                                    }
                                } else {
                                    fillAndExecuteImportTask(spApiFormat, spApiType);
                                }
                            }
                        }
                        case 2 -> {
                            this.importToDatabase();
                            MessageHelper.printMessage(String.format(this.getString(R.string.sys_success), this.getString(R.string.api)), R.mipmap.ic_launcher_round, this.getActivity());
                            this.saveSettings(spApiFormat, spApiType);
                        }
                    }
                }
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

    private void exportToDatabase() throws Exception {
        String path = this.txtApiPath.getText().toString() + File.separatorChar  + this.txtApiName.getText().toString() + ".db";
        MainActivity.GLOBALS.getDatabase(this.getActivity()).copyDatabase(path);
    }

    private void importToDatabase() throws Exception {
        String path = this.txtApiPath.getText().toString();
        MainActivity.GLOBALS.getDatabase(this.getActivity()).getDatabase(path);
        MainApiFragment.triggerRebirth(this.requireContext());
    }

    private void fillAndExecuteImportTask(Spinner spApiFormat, Spinner spApiType) {
        if(this.chkApiDelete.isChecked()) {
            this.lblMessage.setText(R.string.api_delete);
            MainActivity.GLOBALS.getDatabase(this.getActivity()).deleteAll();
        }

        ImportTask importTask = new ImportTask(
                MainApiFragment.this.getActivity(),
                this.txtApiPath.getText().toString(),
                this.pbProgress, this.lblState, this.lblMessage, this.chkApiBooks.isChecked(),
                this.chkApiMovies.isChecked(), this.chkApiMusic.isChecked(), this.chkApiWebService.isChecked(),
                this.mpCells, this.createList(),
                MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications(),
                R.drawable.icon_notification, MainActivity.GLOBALS.getDatabase(this.requireContext()));
        importTask.after((CustomAbstractTask.PostExecuteListener<List<String>>) o -> {
            String logPath = "";
            try {
                File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String path = downloadFolder.getAbsolutePath() + File.separatorChar + "myArchiveMobile_import.log";
                TextService textService = new TextService(path);
                textService.openWriter();
                for(String line : o) {
                    textService.writeLine(line);
                }
                textService.closeWriter();
                logPath = path;
            } catch (Exception ignored) {}
            MessageHelper.printMessage(String.format(getString(R.string.sys_success), getString(R.string.api)) + (logPath.trim().isEmpty()? "": ":" + logPath.trim()), R.mipmap.ic_launcher_round, getActivity());
            saveSettings(spApiFormat, spApiType);
        });
        importTask.execute();
    }

    private long createList() {
        try {
            if(this.txtApiListNew.getText().toString().trim().isEmpty()) {
                Object object = this.spApiList.getSelectedItem();
                if(object != null) {
                    if(object instanceof MediaList mediaList) {
                        return mediaList.getId();
                    }
                }
            } else {
                MediaList mediaList = new MediaList();
                mediaList.setTitle(this.txtApiListNew.getText().toString());
                return MainActivity.GLOBALS.getDatabase(this.getActivity()).insertOrUpdateMediaList(mediaList);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.requireActivity());
        }
        return 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String path = this.txtApiPath.getText().toString();
            if(resultCode == RESULT_OK && requestCode == 965) {
                MainActivity.GLOBALS.getDatabase(this.getActivity()).getDatabase(path);
                MainApiFragment.triggerRebirth(this.requireContext());
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

    @SuppressWarnings("unchecked")
    private void loadFromSettings(Spinner spFormat, Spinner spType) {
        Settings settings = MainActivity.GLOBALS.getSettings(this.requireContext());
        this.chkApiBooks.setChecked(settings.getSetting(MainApiFragment.BOOKS, false));
        this.chkApiMovies.setChecked(settings.getSetting(MainApiFragment.MOVIES, false));
        this.chkApiMusic.setChecked(settings.getSetting(MainApiFragment.MUSIC, false));
        this.chkApiGames.setChecked(settings.getSetting(MainApiFragment.GAMES, false));
        this.chkApiWebService.setChecked(settings.getSetting(MainApiFragment.WEBSERVICE, true));

        this.txtApiName.setText(settings.getSetting(MainApiFragment.NAME, "export"));
        this.txtApiPath.setText(settings.getSetting(MainApiFragment.PATH, "export"));

        Adapter formatAdapter = spFormat.getAdapter();
        if(formatAdapter instanceof CustomSpinnerAdapter) {
            CustomSpinnerAdapter<String> spinnerAdapter = (CustomSpinnerAdapter<String>) formatAdapter;
            String format = settings.getSetting(MainApiFragment.FORMAT, this.formatArray[0]);
            spFormat.setSelection(spinnerAdapter.getPosition(format));
        }

        Adapter typeAdapter = spType.getAdapter();
        if(typeAdapter instanceof CustomSpinnerAdapter) {
            CustomSpinnerAdapter<String> spinnerAdapter = (CustomSpinnerAdapter<String>) typeAdapter;
            String type = settings.getSetting(MainApiFragment.TYPE, this.typeArray[0]);
            spType.setSelection(spinnerAdapter.getPosition(type));
        }
    }

    private void saveSettings(Spinner spFormat, Spinner spType) {
        Settings settings = MainActivity.GLOBALS.getSettings(this.requireContext());
        settings.setSetting(MainApiFragment.BOOKS, this.chkApiBooks.isChecked());
        settings.setSetting(MainApiFragment.MOVIES, this.chkApiMovies.isChecked());
        settings.setSetting(MainApiFragment.MUSIC, this.chkApiMusic.isChecked());
        settings.setSetting(MainApiFragment.GAMES, this.chkApiGames.isChecked());
        settings.setSetting(MainApiFragment.WEBSERVICE, this.chkApiWebService.isChecked());

        settings.setSetting(MainApiFragment.PATH, this.txtApiPath.getText().toString());
        settings.setSetting(MainApiFragment.NAME, this.txtApiName.getText().toString());

        settings.setSetting(MainApiFragment.FORMAT, spFormat.getSelectedItem().toString());
        settings.setSetting(MainApiFragment.TYPE, spType.getSelectedItem().toString());
    }

    private void hidePnlCells(String type, String format) {
        if(type.equals(typeArray[0])) {
            this.tblCells.setVisibility(View.GONE);
        } else {
            if(format.equals("pdf") || format.equals("db")) {
                this.tblCells.setVisibility(View.GONE);
            } else {
                this.tblCells.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("InflateParams")
    private void fillPnlCells(List<String> cells) {
        LayoutInflater inflater = (LayoutInflater) this.requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tblCells.removeViews(1, this.tblCells.getChildCount() - 1);
        this.mpCells.clear();
        for(String cell : cells) {
            TableRow tableRow = (TableRow) Objects.requireNonNull(inflater).inflate(R.layout.row_import, null);
            ((TextView) tableRow.getChildAt(0)).setText(cell);
            Spinner spinner = ((Spinner) tableRow.getChildAt(1));
            CustomSpinnerAdapter<String> adapter = this.reloadColumns();
            spinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            this.mpCells.put(cell, spinner);
            this.tblCells.addView(tableRow);
            this.selectColumn(cell, spinner, adapter);
        }
    }

    private CustomSpinnerAdapter<String> reloadColumns() {
        CustomSpinnerAdapter<String> columns = new CustomSpinnerAdapter<>(this.requireActivity());
        columns.clear();

        String[] cellArray = this.getResources().getStringArray(R.array.api_cells);
        columns.add("");
        for(int i = 0; i<=cellArray.length - 1 ; i++) {
            columns.add(cellArray[i]);
        }
        return columns;
    }

    private void selectColumn(String cell, Spinner spinner, CustomSpinnerAdapter<String> adapter) {
        String lower = cell.trim().toLowerCase();
        if(lower.startsWith("original")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.media_general_originalTitle)));
        } else if(lower.startsWith("title") || lower.startsWith("titel")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.sys_title)));
        } else if(lower.startsWith("descr") || lower.startsWith("beschr")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.sys_description)));
        } else if(lower.startsWith("isbn") || lower.startsWith("ean")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.media_general_code)));
        } else if(lower.startsWith("genre")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.media_general_category)));
        } else if(lower.startsWith("year") || lower.startsWith("jahr") || lower.contains("release") || lower.contains("erschein")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.media_general_releaseDate)));
        } else if(lower.contains("pages") || lower.contains("seiten")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.book_numberOfPages)));
        } else if(lower.startsWith("author") || lower.startsWith("autor") || lower.startsWith("actor") || lower.startsWith("interpret")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.media_persons)));
        }  else if(lower.startsWith("publisher") || lower.startsWith("heraus") || lower.startsWith("verlaog")) {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.media_companies)));
        } else {
            spinner.setSelection(adapter.getPosition(this.getString(R.string.customFields)));
        }
    }

    private void reloadLists() {
        try {
            ArrayAdapter<MediaList> mediaListAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.spinner_item);
            mediaListAdapter.setDropDownViewResource(R.layout.spinner_item);
            List<MediaList> mediaLists = MainActivity.GLOBALS.getDatabase(this.getActivity()).getMediaLists("", 1, 0);
            mediaListAdapter.addAll(mediaLists);
            MediaList mediaList = new MediaList();
            mediaList.setTitle("");
            mediaList.setId(0);
            mediaListAdapter.insert(mediaList, 0);
            this.spApiList.setAdapter(mediaListAdapter);
            mediaListAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.drawable.icon_notification, this.requireActivity());
        }
    }
}
