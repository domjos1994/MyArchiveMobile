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

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class MediaCustomFieldFragment extends AbstractFragment<BaseMediaObject> {
    private LinearLayout container;
    private Map<CustomField, String> values;
    private List<CustomField> customFields;
    private List<View> views;
    private boolean editMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_custom_fields, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.container = view.findViewById(R.id.customFieldsContainer);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.values = baseMediaObject.getCustomFieldValues();
        if(this.values == null) {
            this.values = new LinkedHashMap<>();
        }
        this.reload(((DatabaseObject) baseMediaObject).getTable());
        this.changeMode(this.editMode);
    }

    @Override
    public BaseMediaObject getMediaObject() {
        this.values.clear();
        for(View view : this.views) {
            String value = "";
            if(view instanceof EditText) {
                EditText editText = (EditText) view;
                if(!editText.getText().toString().isEmpty()) {
                    value = editText.getText().toString();
                }
            } else if(view instanceof Spinner) {
                Spinner sp = (Spinner) view;
                if(sp.getSelectedItemPosition() != -1) {
                    value = sp.getSelectedItem().toString();
                }
            }

            if(!value.isEmpty()) {
                for(CustomField customField : this.customFields) {
                    if(customField.getId()==Long.parseLong(String.valueOf(view.getTag()))) {
                        this.values.put(customField, value);
                        break;
                    }
                }
            }
        }
        BaseMediaObject baseMediaObject = new BaseMediaObject();
        baseMediaObject.setCustomFieldValues(this.values);
        return baseMediaObject;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.editMode = editMode;
        if(this.views != null) {
            for(View view : this.views) {
                view.setEnabled(this.editMode);
            }
        }
    }

    private void reload(String table) {
        this.views = new LinkedList<>();
        this.container.removeAllViews();
        this.customFields = MainActivity.GLOBALS.getDatabase().getCustomFields(String.format("%s=1", table));
        if(!this.customFields.isEmpty()) {
            for(CustomField customField : this.customFields) {
                if(customField.getType().equals(this.getString(R.string.customFields_type_values_text))) {
                    if(customField.getAllowedValues().isEmpty()) {
                        this.addTextField(customField, InputType.TYPE_CLASS_TEXT);
                    } else {
                        this.addAutoCompleteTextView(customField);
                    }
                } else if(customField.getType().equals(this.getString(R.string.customFields_type_values_number))) {
                    this.addTextField(customField, InputType.TYPE_CLASS_NUMBER);
                } else if(customField.getType().equals(this.getString(R.string.customFields_type_values_date))) {
                    this.addTextField(customField, InputType.TYPE_CLASS_DATETIME);
                } else {
                    this.addSpinner(customField);
                }
            }
        } else {
            this.addLabel(this.getString(de.domjos.customwidgets.R.string.main_noEntry));
        }
    }

    private void addTextField(CustomField customField, int type) {
        EditText txt = new EditText(this.getContext());
        txt.setTag(customField.getId());
        txt.setInputType(type);
        txt.setHint(customField.getTitle());
        this.views.add(txt);
        this.container.addView(txt);

        String value = this.getValue(txt);
        if(!value.isEmpty()) {
            txt.setText(value);
        }
    }

    private void addAutoCompleteTextView(CustomField customField) {
        AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(this.getContext());
        ArrayAdapter<String> values = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), android.R.layout.simple_list_item_1);
        for(String item : customField.getAllowedValues().split(";")) {
            values.add(item.trim());
        }
        autoCompleteTextView.setAdapter(values);
        values.notifyDataSetChanged();
        autoCompleteTextView.setTag(customField.getId());
        autoCompleteTextView.setHint(customField.getTitle());
        autoCompleteTextView.setInputType(InputType.TYPE_CLASS_TEXT);
        this.views.add(autoCompleteTextView);
        this.container.addView(autoCompleteTextView);

        String value = this.getValue(autoCompleteTextView);
        if(!value.isEmpty()) {
            autoCompleteTextView.setText(value);
        }
    }

    private void addLabel(String text) {
        TextView label = new TextView(this.getContext());
        label.setTextSize(16f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int px = ConvertHelper.convertDPToPixels(2, Objects.requireNonNull(this.getContext()));
        layoutParams.setMargins(px, px, px, px);
        label.setLayoutParams(layoutParams);
        label.setTypeface(null, Typeface.BOLD);
        label.setText(text);
        this.container.addView(label);
    }

    private void addSpinner(CustomField customField) {
        this.addLabel(customField.getTitle());

        Spinner sp = new Spinner(this.getContext());
        sp.setTag(customField.getId());
        sp.setContentDescription(customField.getTitle());
        ArrayAdapter<String> values = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), R.layout.spinner_item);
        for(String item : customField.getAllowedValues().split(";")) {
            values.add(item.trim());
        }
        sp.setAdapter(values);
        values.notifyDataSetChanged();
        this.views.add(sp);
        this.container.addView(sp);

        String value = this.getValue(sp);
        if(!value.isEmpty()) {
            sp.setSelection(values.getPosition(value));
        }
    }

    private String getValue(View view) {
        for(Map.Entry<CustomField, String> entry : this.values.entrySet()) {
            if(entry.getKey().getId()==Long.parseLong(String.valueOf(view.getTag()))) {
                return entry.getValue();
            }
        }
        return "";
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
