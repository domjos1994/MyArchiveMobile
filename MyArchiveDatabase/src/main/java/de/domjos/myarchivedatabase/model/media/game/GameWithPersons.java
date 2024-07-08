package de.domjos.myarchivedatabase.model.media.game;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.general.person.PersonGameCrossRef;

public final class GameWithPersons {
    @Embedded
    private Game game;

    @Relation(
            entity = Person.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonGameCrossRef.class, parentColumn = "gameId", entityColumn = "personId")
    )
    private List<Person> persons;

    public GameWithPersons() {
        this.game = null;
        this.persons = new LinkedList<>();
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<Person> getPersons() {
        return this.persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
