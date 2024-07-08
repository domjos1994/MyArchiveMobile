package de.domjos.myarchivedatabase.model.media.game;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.customField.CustomField;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldValueGameCrossRef;
import de.domjos.myarchivedatabase.model.general.customField.CustomFieldWithValues;

public final class GameWithCustomFieldValues {
    @Embedded
    private Game game;

    @Relation(
            entity = CustomField.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = CustomFieldValueGameCrossRef.class, parentColumn = "gameId", entityColumn = "customFieldValueId")
    )
    private List<CustomFieldWithValues> customFieldWithValues;

    public GameWithCustomFieldValues() {
        this.game = null;
        this.customFieldWithValues = new LinkedList<>();
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<CustomFieldWithValues> getCustomFieldWithValues() {
        return this.customFieldWithValues;
    }

    public void setCustomFieldWithValues(List<CustomFieldWithValues> customFieldWithValues) {
        this.customFieldWithValues = customFieldWithValues;
    }
}
