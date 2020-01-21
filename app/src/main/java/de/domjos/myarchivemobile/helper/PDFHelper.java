package de.domjos.myarchivemobile.helper;

import android.content.Context;

import java.util.List;

import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.services.PDFService;
import de.domjos.myarchivemobile.R;

public class PDFHelper {
    private PDFService pdfService;
    private Context context;

    public PDFHelper(String file, Context context) throws Exception {
        this.context = context;
        this.pdfService = new PDFService(file);

        byte[] icon = Converter.convertDrawableToByteArray(this.context, R.mipmap.ic_launcher);
        this.pdfService.addHeadPage(icon, this.context.getString(R.string.main_navigation_media), this.context.getString(R.string.app_name));
    }

    public void execute(List<BaseMediaObject> objects) throws Exception {
        for(BaseMediaObject baseMediaObject : objects) {
            this.addMediaObject(baseMediaObject);

            if(baseMediaObject instanceof Book) {
                this.addBook((Book) baseMediaObject);
            }
            if(baseMediaObject instanceof Movie) {
                this.addMovie((Movie) baseMediaObject);
            }
            if(baseMediaObject instanceof Game) {
                this.addGame((Game) baseMediaObject);
            }
            if(baseMediaObject instanceof Album) {
                this.addAlbum((Album) baseMediaObject);
            }

            this.addPersons(baseMediaObject.getPersons());
            this.addCompanies(baseMediaObject.getCompanies());
            this.pdfService.newPage();
        }
        this.pdfService.close();
    }

    private void addMediaObject(BaseMediaObject baseMediaObject) throws Exception {
        this.pdfService.addParagraph(baseMediaObject.getTitle(), PDFService.H1, PDFService.CENTER, 20f);
        this.pdfService.addParagraph(this.context.getString(R.string.media_general), PDFService.H3, PDFService.LEFT, 10f);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.context.getString(R.string.media_general_originalTitle)).append(": ").append(baseMediaObject.getOriginalTitle()).append("\n");
        if(baseMediaObject.getReleaseDate() != null) {
            stringBuilder.append(this.context.getString(R.string.media_general_releaseDate)).append(": ").append(Converter.convertDateToString(baseMediaObject.getReleaseDate(), this.context.getString(R.string.sys_date_format))).append("\n");
        }
        stringBuilder.append(this.context.getString(R.string.media_general_price)).append(": ").append(baseMediaObject.getPrice()).append("\n");
        stringBuilder.append(this.context.getString(R.string.media_general_code)).append(": ").append(baseMediaObject.getCode()).append("\n");
        if(baseMediaObject.getCategory() != null) {
            stringBuilder.append(this.context.getString(R.string.media_general_category)).append(": ").append(baseMediaObject.getCategory().getTitle()).append("\n");
        }
        if(baseMediaObject.getTags() != null) {
            if(!baseMediaObject.getTags().isEmpty()) {
                stringBuilder.append(this.context.getString(R.string.media_general_tags)).append(": ");
                for(BaseDescriptionObject baseDescriptionObject : baseMediaObject.getTags()) {
                    stringBuilder.append(baseDescriptionObject.getTitle()).append(", ");
                }
            }
        }
        this.pdfService.addParagraph(stringBuilder.toString(), PDFService.P, PDFService.LEFT, 10f);
    }

    private void addBook(Book book) throws Exception {

    }

    private void addMovie(Movie movie) throws Exception {

    }

    private void addGame(Game game) throws Exception {

    }

    private void addAlbum(Album album) throws Exception {

    }

    private void addPersons(List<Person> people) throws Exception {

    }

    private void addCompanies(List<Company> companies) {

    }
}
