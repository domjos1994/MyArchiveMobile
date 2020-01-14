package de.domjos.myarchivelibrary.model.base;

public class BaseDescriptionObject extends BaseTitleObject {
    private String description;

    public BaseDescriptionObject() {
        super();

        this.description = "";
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
