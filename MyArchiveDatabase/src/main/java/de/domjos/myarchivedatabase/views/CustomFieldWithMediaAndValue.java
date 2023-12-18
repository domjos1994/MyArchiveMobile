package de.domjos.myarchivedatabase.views;

import androidx.room.DatabaseView;

@DatabaseView(
        "SELECT " +
            "c.id as customFieldId, c.title as customFieldTitle, c.description as customFieldDescription, " +
            "cv.value as customFieldValue,  'Album' as mediaType, a.id as mediaId " +
                "FROM customFields c " +
                "INNER JOIN customFieldValues cv ON c.id=cv.customField " +
                "INNER JOIN CustomFieldValueAlbumCrossRef cva ON cv.id=cva.customFieldValueId " +
                "INNER JOIN albums a ON cva.albumId=a.id " +
        "UNION " +
            "SELECT " +
                "c.id as customFieldId, c.title as customFieldTitle, c.description as customFieldDescription, " +
                "cv.value as customFieldValue,  'Book' as mediaType, b.id as mediaId " +
                "FROM customFields c " +
                "INNER JOIN customFieldValues cv ON c.id=cv.customField " +
                "INNER JOIN CustomFieldValueBookCrossRef cvb ON cv.id=cvb.customFieldValueId " +
                "INNER JOIN books b ON cvb.bookId=b.id " +
        "UNION " +
            "SELECT " +
                "c.id as customFieldId, c.title as customFieldTitle, c.description as customFieldDescription, " +
                "cv.value as customFieldValue,  'Game' as mediaType, g.id as mediaId " +
                "FROM customFields c " +
                "INNER JOIN customFieldValues cv ON c.id=cv.customField " +
                "INNER JOIN CustomFieldValueGameCrossRef cvg ON cv.id=cvg.customFieldValueId " +
                "INNER JOIN games g ON cvg.gameId=g.id " +
        "UNION " +
            "SELECT " +
                "c.id as customFieldId, c.title as customFieldTitle, c.description as customFieldDescription, " +
                "cv.value as customFieldValue,  'Movie' as mediaType, m.id as mediaId " +
                "FROM customFields c " +
                "INNER JOIN customFieldValues cv ON c.id=cv.customField " +
                "INNER JOIN CustomFieldValueMovieCrossRef cvm ON cv.id=cvm.customFieldValueId " +
                "INNER JOIN movies m ON cvm.movieId=m.id "
)
public final class CustomFieldWithMediaAndValue {
    private long customFieldId;
    private String customFieldTitle;
    private String customFieldDescription;
    private String customFieldValue;
    private String mediaType;
    private long mediaId;

    public CustomFieldWithMediaAndValue() {
        this.customFieldId = 0L;
        this.customFieldTitle = "";
        this.customFieldDescription = "";
        this.customFieldValue = "";
        this.mediaType = "";
        this.mediaId = 0L;
    }

    public long getCustomFieldId() {
        return this.customFieldId;
    }

    public void setCustomFieldId(long customFieldId) {
        this.customFieldId = customFieldId;
    }

    public String getCustomFieldTitle() {
        return this.customFieldTitle;
    }

    public void setCustomFieldTitle(String customFieldTitle) {
        this.customFieldTitle = customFieldTitle;
    }

    public String getCustomFieldDescription() {
        return this.customFieldDescription;
    }

    public void setCustomFieldDescription(String customFieldDescription) {
        this.customFieldDescription = customFieldDescription;
    }

    public String getCustomFieldValue() {
        return this.customFieldValue;
    }

    public void setCustomFieldValue(String customFieldValue) {
        this.customFieldValue = customFieldValue;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public long getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }
}
