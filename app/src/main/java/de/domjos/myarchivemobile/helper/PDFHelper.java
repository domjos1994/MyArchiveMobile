package de.domjos.myarchivemobile.helper;

import android.content.Context;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    private final int headerColor, rowColor;

    public PDFHelper(String file, Context context) throws Exception {
        this.context = context;
        this.headerColor = context.getResources().getColor(R.color.colorPrimaryDark);
        this.rowColor = context.getResources().getColor(R.color.colorPrimary);

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
        if(baseMediaObject.getCover() != null) {
            this.pdfService.addImage(baseMediaObject.getCover(), PDFService.CENTER, 10f);
        }
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
        StringBuilder stringBuilder = new StringBuilder();
        if(book.getType() != null) {
            stringBuilder.append(this.context.getString(R.string.book_type)).append(": ").append(book.getType().toString()).append("\n");
        }
        stringBuilder.append(this.context.getString(R.string.book_edition)).append(": ").append(book.getEdition()).append("\n");
        stringBuilder.append(this.context.getString(R.string.book_topics)).append(": ").append(TextUtils.join(",", book.getTopics())).append("\n");
        stringBuilder.append(this.context.getString(R.string.book_numberOfPages)).append(": ").append(book.getNumberOfPages()).append("\n");
        stringBuilder.append(this.context.getString(R.string.book_path)).append(": ").append(book.getPath());
        this.pdfService.addParagraph(stringBuilder.toString(), PDFService.P, PDFService.LEFT, 10f);
    }

    private void addMovie(Movie movie) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if(movie.getType() != null) {
            stringBuilder.append(this.context.getString(R.string.movie_type)).append(": ").append(movie.getType().toString()).append("\n");
        }
        stringBuilder.append(this.context.getString(R.string.movie_length)).append(": ").append(movie.getLength()).append("\n");
        stringBuilder.append(this.context.getString(R.string.movie_path)).append(": ").append(movie.getPath());
        this.pdfService.addParagraph(stringBuilder.toString(), PDFService.P, PDFService.LEFT, 10f);
    }

    private void addGame(Game game) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if(game.getType() != null) {
            stringBuilder.append(this.context.getString(R.string.game_type)).append(": ").append(game.getType().toString()).append("\n");
        }
        stringBuilder.append(this.context.getString(R.string.game_length)).append(": ").append(game.getLength()).append("\n");
        this.pdfService.addParagraph(stringBuilder.toString(), PDFService.P, PDFService.LEFT, 10f);
    }

    private void addAlbum(Album album) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        if(album.getType() != null) {
            stringBuilder.append(this.context.getString(R.string.album_type)).append(": ").append(album.getType().toString()).append("\n");
        }
        stringBuilder.append(this.context.getString(R.string.album_number)).append(": ").append(album.getNumberOfDisks()).append("\n");
        this.pdfService.addParagraph(stringBuilder.toString(), PDFService.P, PDFService.LEFT, 10f);
    }

    private void addPersons(List<Person> people) throws Exception {
        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.media_persons_firstName), 150.0f);
        columns.put(this.context.getString(R.string.media_persons_lastName), 150.0f);
        columns.put(this.context.getString(R.string.media_persons_birthDate), 150.0f);

        List<List<String>> rows = new LinkedList<>();
        for(Person person : people) {
            String birthDate = "";
            if(person.getBirthDate() != null) {
                birthDate = Converter.convertDateToString(person.getBirthDate(), this.context.getString(R.string.sys_date_format));
            }

            rows.add(Arrays.asList(person.getFirstName(), person.getLastName(), birthDate));
        }

        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor);
    }

    private void addCompanies(List<Company> companies) throws Exception {
        Map<String, Float> columns = new LinkedHashMap<>();
        columns.put(this.context.getString(R.string.sys_title), 150.0f);
        columns.put(this.context.getString(R.string.media_companies_foundation), 150.0f);

        List<List<String>> rows = new LinkedList<>();
        for(Company company : companies) {
            String foundation = "";
            if(company.getFoundation() != null) {
                foundation = Converter.convertDateToString(company.getFoundation(), this.context.getString(R.string.sys_date_format));
            }

            rows.add(Arrays.asList(company.getTitle(), foundation));
        }

        this.pdfService.addTable(columns, rows, this.headerColor, this.rowColor);
    }
}
