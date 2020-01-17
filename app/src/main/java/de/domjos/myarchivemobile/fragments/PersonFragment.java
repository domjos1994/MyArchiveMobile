package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivemobile.R;

public class PersonFragment extends AbstractFragment<Person> {
    private EditText txtPersonFirstName, txtPersonLastName, txtPersonBirthDate, txtPersonDescription;
    private Person person;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtPersonFirstName = view.findViewById(R.id.txtPersonFirstName);
        this.txtPersonLastName = view.findViewById(R.id.txtPersonLastName);
        this.txtPersonBirthDate = view.findViewById(R.id.txtPersonBirthDate);
        this.txtPersonDescription = view.findViewById(R.id.txtPersonDescription);

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(Person person) {
        this.person = person;

        this.txtPersonFirstName.setText(this.person.getFirstName());
        this.txtPersonLastName.setText(this.person.getLastName());
        if(this.person.getBirthDate() != null) {
            this.txtPersonBirthDate.setText(Converter.convertDateToString(this.person.getBirthDate(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtPersonBirthDate.setText("");
        }
        this.txtPersonDescription.setText(this.person.getDescription());
    }

    @Override
    public Person getMediaObject() {
        try {
            this.person.setFirstName(this.txtPersonFirstName.getText().toString());
            this.person.setLastName(this.txtPersonLastName.getText().toString());
            this.person.setDescription(this.txtPersonDescription.getText().toString());
            if(!this.txtPersonBirthDate.getText().toString().isEmpty()) {
                this.person.setBirthDate(Converter.convertStringToDate(this.txtPersonBirthDate.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ignored) {}

        return this.person;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtPersonFirstName.setEnabled(editMode);
        this.txtPersonLastName.setEnabled(editMode);
        this.txtPersonBirthDate.setEnabled(editMode);
        this.txtPersonDescription.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }
}
