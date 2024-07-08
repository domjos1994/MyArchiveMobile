package de.domjos.myarchivedatabase.model.media.movie;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.general.tag.TagMovieCrossRef;

public final class MovieWithTags {
    @Embedded
    private Movie movie;
    @Relation(
            entity = Tag.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagMovieCrossRef.class, parentColumn = "movieId", entityColumn = "tagId")
    )
    private List<Tag> tags;

    public MovieWithTags() {
        this.movie = null;
        this.tags = new LinkedList<>();
    }

    public Movie getMovie() {
        return this.movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}