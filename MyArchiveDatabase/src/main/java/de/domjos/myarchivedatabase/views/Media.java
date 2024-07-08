package de.domjos.myarchivedatabase.views;

import androidx.room.DatabaseView;

import java.util.Date;

@DatabaseView(
        "SELECT books.id as id, books.title as title, originalTitle, books.description as description, " +
            "'Book' as type, cover, categories.title as category, group_concat(tags.title) as tags, " +
            "group_concat(DISTINCT customFields.title || ':' || books_customFields.value) as customFields, " +
            "books.releaseDate as releaseDate " +
        "FROM books " +
            "LEFT JOIN categories ON categories.ID=books.category " +
            "LEFT JOIN TagBookCrossRef books_tags ON books_tags.bookId=books.id " +
            "LEFT JOIN tags ON tags.id=books_tags.tagId " +
            "LEFT JOIN CustomFieldValueBookCrossRef ON CustomFieldValueBookCrossRef.bookId=books.id " +
            "LEFT JOIN customFieldValues books_customFields ON books_customFields.id=CustomFieldValueBookCrossRef.customFieldValueId " +
            "LEFT JOIN customFields ON customFields.id=books_customFields.customField " +
        "GROUP BY books.title " +
        "UNION " +
            "SELECT movies.id as id, movies.title as title, originalTitle, movies.description as description, " +
                "'Movie' as type, cover, categories.title as category, group_concat(tags.title) as tags, " +
                "group_concat(DISTINCT customFields.title || ':' || movies_customFields.value) as customFields, " +
                "movies.releaseDate as releaseDate " +
            "FROM movies " +
                "LEFT JOIN categories ON categories.ID=movies.category " +
                "LEFT JOIN TagMovieCrossRef movies_tags ON movies_tags.movieId=movies.id " +
                "LEFT JOIN tags ON tags.id=movies_tags.tagId " +
                "LEFT JOIN CustomFieldValueMovieCrossRef ON CustomFieldValueMovieCrossRef.movieId=movies.id " +
                "LEFT JOIN customFieldValues movies_customFields ON movies_customFields.id=CustomFieldValueMovieCrossRef.customFieldValueId " +
                "LEFT JOIN customFields ON customFields.id=movies_customFields.customField " +
            "GROUP BY movies.title " +
        "UNION " +
            "SELECT albums.id as id, albums.title as title, originalTitle, albums.description as description, " +
                "'Album' as type, cover, categories.title as category, group_concat(tags.title) as tags, " +
                "group_concat(DISTINCT customFields.title || ':' || albums_customFields.value) as customFields, " +
                "albums.releaseDate as releaseDate " +
            "FROM albums " +
                "LEFT JOIN categories ON categories.ID=albums.category " +
                "LEFT JOIN TagAlbumCrossRef albums_tags ON albums_tags.albumId=albums.id " +
                "LEFT JOIN tags ON tags.id=albums_tags.tagId " +
                "LEFT JOIN CustomFieldValueAlbumCrossRef ON CustomFieldValueAlbumCrossRef.albumId=albums.id " +
                "LEFT JOIN customFieldValues albums_customFields ON albums_customFields.id=CustomFieldValueAlbumCrossRef.customFieldValueId " +
                "LEFT JOIN customFields ON customFields.id=albums_customFields.customField " +
            "GROUP BY albums.title " +
        "UNION " +
            "SELECT games.id as id, games.title as title, originalTitle, games.description as description," +
                "'Game' as type, cover, categories.title as category, group_concat(tags.title) as tags, " +
                "group_concat(DISTINCT customFields.title || ':' || games_customFields.value) as customFields, " +
                "games.releaseDate as releaseDate " +
            "FROM games " +
                "LEFT JOIN categories ON categories.ID=games.category " +
                "LEFT JOIN TagGameCrossRef games_tags ON games_tags.gameId=games.id " +
                "LEFT JOIN tags ON tags.id=games_tags.tagId " +
                "LEFT JOIN CustomFieldValueGameCrossRef ON CustomFieldValueGameCrossRef.gameId=games.id " +
                "LEFT JOIN customFieldValues games_customFields ON games_customFields.id=CustomFieldValueGameCrossRef.customFieldValueId " +
                "LEFT JOIN customFields ON customFields.id=games_customFields.customField " +
            "GROUP BY games.title " +
        "ORDER BY title")
public final class Media {
    private long id;
    private String title;
    private String description;
    private String type;
    private String category;
    private String tags;
    private String customFields;
    private Date releaseDate;

    public Media() {
        this.id = 0L;
        this.title = "";
        this.description = "";
        this.type = "";
        this.category = "";
        this.tags = "";
        this.customFields = "";
        this.releaseDate = null;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCustomFields() {
        return this.customFields;
    }

    public void setCustomFields(String customFields) {
        this.customFields = customFields;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
