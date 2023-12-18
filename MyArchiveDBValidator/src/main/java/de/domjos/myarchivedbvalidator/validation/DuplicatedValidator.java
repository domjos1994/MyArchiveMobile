package de.domjos.myarchivedbvalidator.validation;

import android.content.Context;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;
import de.domjos.myarchivedatabase.model.base.BaseTitleObject;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedbvalidator.validation.Validator;

public class DuplicatedValidator extends Validator {
    private final AppDatabase appDatabase;

    public DuplicatedValidator(Object object, AppDatabase appDatabase) {
        super(object);

        this.appDatabase = appDatabase;
    }

    @Override
    public boolean validate() {
        BaseTitleObject bto = ((BaseTitleObject) super.object);
        if(super.object instanceof Album) {
            return this.appDatabase.albumDAO().getAlbum(bto.getTitle())==null;
        }
        if(super.object instanceof Book) {
            return this.appDatabase.bookDAO().getBook(bto.getTitle())==null;
        }
        if(super.object instanceof Game) {
            return this.appDatabase.gameDAO().getGame(bto.getTitle())==null;
        }
        if(super.object instanceof Movie) {
            return this.appDatabase.movieDAO().getMovie(bto.getTitle())==null;
        }

        return false;
    }
}
