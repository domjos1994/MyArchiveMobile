package de.domjos.myarchivelibrary.model.base;

public class BaseTitleObject extends BaseObject {
    private String title;

    public BaseTitleObject() {
        super();

        this.title = "";
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
