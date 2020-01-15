package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.domjos.customwidgets.tokenizer.CommaTokenizer;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class MediaPersonsCompaniesFragment extends AbstractFragment {
    private MultiAutoCompleteTextView txtMediaPersons, txtMediaCompanies;

    private BaseMediaObject baseMediaObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_persons_companies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaPersons = view.findViewById(R.id.txtMediaPersons);
        this.txtMediaCompanies = view.findViewById(R.id.txtMediaCompanies);

        try {
            this.txtMediaPersons.setTokenizer(new CommaTokenizer());
            ArrayAdapter<String> personsAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_expandable_list_item_1);
            for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("", 0)) {
                personsAdapter.add(person.getFirstName() + " " + person.getLastName());
            }
            this.txtMediaPersons.setAdapter(personsAdapter);
            personsAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        try {
            this.txtMediaCompanies.setTokenizer(new CommaTokenizer());
            ArrayAdapter<String> companiesAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_expandable_list_item_1);
            for(Company company : MainActivity.GLOBALS.getDatabase().getCompanies("", 0)) {
                companiesAdapter.add(company.getTitle());
            }
            this.txtMediaPersons.setAdapter(companiesAdapter);
            companiesAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.baseMediaObject = baseMediaObject;

        StringBuilder persons = new StringBuilder();
        for(Person person : this.baseMediaObject.getPersons()) {
            persons.append(person.getFirstName()).append(" ").append(person.getLastName()).append(", ");
        }
        this.txtMediaPersons.setText(persons.toString());

        StringBuilder companies = new StringBuilder();
        for(Company company : this.baseMediaObject.getCompanies()) {
            companies.append(company.getTitle()).append(", ");
        }
        this.txtMediaCompanies.setText(companies.toString());
    }

    @Override
    public BaseMediaObject getMediaObject() {
        for(String person : this.txtMediaPersons.getText().toString().split(",")) {
            if(!person.trim().isEmpty()) {
                String[] spl = person.trim().split(" ");
                Person tmp = new Person();
                tmp.setFirstName(spl[0].trim());
                tmp.setLastName(person.replace(spl[0], "").trim());
                this.baseMediaObject.getPersons().add(tmp);
            }
        }

        for(String company : this.txtMediaCompanies.getText().toString().split(",")) {
            if(!company.trim().isEmpty()) {
                Company tmp = new Company();
                tmp.setTitle(company.trim());
                this.baseMediaObject.getCompanies().add(tmp);
            }
        }

        return this.baseMediaObject;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaCompanies.setEnabled(editMode);
        this.txtMediaPersons.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
