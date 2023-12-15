package de.domjos.myarchivedatabase.model.general.person;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.game.Game;

public final class PersonWithGames {
    @Embedded
    private Person person;

    @Relation(
            entity = Game.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = PersonGameCrossRef.class, parentColumn = "personId", entityColumn = "gameId")
    )
    private List<Game> games;

    public PersonWithGames() {
        this.person = null;
        this.games = new LinkedList<>();
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
