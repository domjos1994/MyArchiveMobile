package de.domjos.myarchivelibrary.model.general;

import java.util.Arrays;
import java.util.Date;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;

public final class Company extends BaseDescriptionObject implements DatabaseObject {
    private Date foundation;
    private byte[] cover;

    public Company() {
        super();

        this.foundation = null;
        this.cover = null;
    }

    public Date getFoundation() {
        if(this.foundation != null) {
            return (Date) this.foundation.clone();
        } else {
            return null;
        }
    }

    public void setFoundation(Date foundation) {
        if(foundation != null) {
            this.foundation = (Date) foundation.clone();
        } else {
            this.foundation = null;
        }
    }

    public byte[] getCover() {
        if(this.cover != null) {
            return this.cover.clone();
        } else {
            return null;
        }
    }

    public void setCover(byte[] cover) {
        if(cover == null) {
            this.cover = null;
        } else {
            this.cover = Arrays.copyOf(cover, cover.length);
        }
    }

    @Override
    public String getTable() {
        return "companies";
    }
}
