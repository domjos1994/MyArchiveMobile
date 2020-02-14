package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.tasks.WikiDataCompanyTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class CompanyFragment extends AbstractFragment<Company> {
    private EditText txtCompanyTitle, txtCompanyFoundation, txtCompanyDescription;
    private ImageButton cmdCompanySearch;
    private Company company;
    private Validator validator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.company_fragment, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtCompanyTitle = view.findViewById(R.id.txtCompanyTitle);
        this.txtCompanyFoundation = view.findViewById(R.id.txtCompanyFoundation);
        this.txtCompanyDescription = view.findViewById(R.id.txtCompanyDescription);
        this.cmdCompanySearch = view.findViewById(R.id.cmdCompanySearch);

        this.cmdCompanySearch.setOnClickListener(event -> {
            try {
                this.company = this.getMediaObject();
                WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);
                this.abstractPagerAdapter.setMediaObject(wikiDataCompanyTask.execute(this.company).get().get(0));
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
            }
        });

        this.validator = this.initValidation(this.validator);
        this.changeMode(false);
    }

    @Override
    public void setMediaObject(Company company) {
        this.company = company;

        this.txtCompanyTitle.setText(this.company.getTitle());
        if(this.company.getFoundation() != null) {
            this.txtCompanyFoundation.setText(ConvertHelper.convertDateToString(this.company.getFoundation(), this.getString(R.string.sys_date_format)));
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
                this.company.setFoundation(ConvertHelper.convertStringToDate(this.txtCompanyFoundation.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ignored) {}

        return this.company;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtCompanyTitle.setEnabled(editMode);
        this.txtCompanyFoundation.setEnabled(editMode);
        this.txtCompanyDescription.setEnabled(editMode);
        this.cmdCompanySearch.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        this.validator = validator;
        if(this.txtCompanyTitle != null  && this.validator != null) {
            this.validator.addEmptyValidator(this.txtCompanyTitle);
        }
        return this.validator;
    }
}
