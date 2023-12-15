package de.domjos.myarchivedatabase.model.general.tag;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivedatabase.model.media.song.Song;

public final class TagWithSongs {
    @Embedded
    private Tag tag;
    @Relation(
            entity = Song.class,
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = TagSongCrossRef.class, parentColumn = "tagId", entityColumn = "songId")
    )
    private List<Song> songs;

    public TagWithSongs() {
        this.songs = new LinkedList<>();
        this.tag = null;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}