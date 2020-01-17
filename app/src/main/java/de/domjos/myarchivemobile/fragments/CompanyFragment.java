package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivemobile.R;

public class CompanyFragment extends AbstractFragment<Company> {
    private EditText txtCompanyTitle, txtCompanyFoundation, txtCompanyDescription;
    private Company company;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.company_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtCompanyTitle = view.findViewById(R.id.txtCompanyTitle);
        this.txtCompanyFoundation = view.findViewById(R.id.txtCompanyFoundation);
        this.txtCompanyDescription = view.findViewById(R.id.txtCompanyDescription);

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(Company company) {
        this.company = company;

        this.txtCompanyTitle.setText(this.company.getTitle());
        if(this.company.getFoundation() != null) {
            this.txtCompanyFoundation.setText(Converter.convertDateToString(this.company.getFoundation(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtCompanyFoundation.setText("");
        }
        this.txtCompanyDescription.setText(this.company.getDescription());
    }

    @Override
    public Company getMediaObject() {
        try {
            this.company.setTitle(this.txtCompanyTitle.getText().toString());
            this.company.setDescription(this.txtCompanyDescription.getText().toString());
            if(!this.txtCompanyFoundation.getText().toString().isEmpty()) {
                this.company.setFoundation(Converter.convertStringToDate(this.txtCompanyFoundation.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ignored) {}

        return this.company;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtCompanyTitle.setEnabled(editMode);
        this.txtCompanyFoundation.setEnabled(editMode);
        this.txtCompanyDescription.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }
}
