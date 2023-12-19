package de.domjos.myarchivedbvalidator;


import android.content.Context;
import android.text.TextUtils;

import androidx.room.Room;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.base.BaseDescriptionObject;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedbvalidator.exceptions.LengthMinMaxException;
import de.domjos.myarchivedbvalidator.exceptions.TitleDuplicatedException;
import de.domjos.myarchivedbvalidator.exceptions.ValidationException;
import de.domjos.myarchivedbvalidator.validation.DuplicatedValidator;
import de.domjos.myarchivedbvalidator.validation.LengthValidator;

/**
 * Class to connect App with SQLite-Database
 * @author Dominic Joas
 * @version 1.0
 */
public final class Database {
    public final static String MEDIA_ALBUM = "album";
    public final static String MEDIA_MOVIE = "movie";
    public final static String MEDIA_BOOK = "book";
    public final static String MEDIA_GAME = "game";
    public final static String MEDIA_SONG = "song";
    public final static String FILTER = "filter";
    public final static String FILE_TREE = "file_tree";
    public final static String FILE_TREE_FILE = "file_tree_file";

    private final static String TYPE = "type";
    private final static String PATH = "path";
    private final static String LENGTH = "length";
    private final static String DEAD_LINE = "deadLine";
    private final static String DATE_FORMAT = "yyyy-MM-dd";
    private final static String DESCRIPTION = "description";
    private final static String TITLE = "title";
    private final static String COVER = "cover";
    private final static String TAGS = "tags";
    private final static String CATEGORIES = "categories";
    private final static String ID_FILTER = "id=";

    private final Context context;
    private final AppDatabase appDatabase;

    public Database(Context context) {
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();

        this.context = context;
    }

    public List<BaseMediaObject> getObjectList(Map<DatabaseObject, String> content, int number, int offset, String orderBy) {
        List<BaseMediaObject> baseMediaObjects = new LinkedList<>();

        StringBuilder builder = new StringBuilder();
        for(Map.Entry<DatabaseObject, String> entry : content.entrySet()) {
            builder.append("(type='").append(entry.getKey().getTable()).append("'");
            if (!entry.getValue().trim().isEmpty()) {
                builder.append(" AND ").append(entry.getValue());
            }
            builder.append(") OR ");
        }
        builder.replace(builder.length() - 3, builder.length(), "");

        if(orderBy.trim().isEmpty()) {
            orderBy = "";
        }

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT id, title, cover, type FROM media" + where(builder.toString()) + orderBy + " LIMIT " + number + " OFFSET " + offset, new String[]{});
        while (cursor.moveToNext()) {
            BaseMediaObject baseMediaObject = null;
            switch (cursor.getString(cursor.getColumnIndex("type"))) {
                case Database.BOOKS:
                    baseMediaObject = new Book();
                    break;
                case Database.MOVIES:
                    baseMediaObject = new Movie();
                    break;
                case Database.GAMES:
                    baseMediaObject = new Game();
                    break;
                case Database.ALBUMS:
                    baseMediaObject = new Album();
                    break;
            }

            if(baseMediaObject != null) {
                baseMediaObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
                baseMediaObject.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                baseMediaObject.setCover(cursor.getBlob(cursor.getColumnIndex("cover")));
                baseMediaObjects.add(baseMediaObject);
            }
        }
        cursor.close();
        return baseMediaObjects;
    }

    public int getPageOfItem(String table, long id, int number) {
        int offset = 0;
        int currentPage = 0;

        String paramQuery = "SELECT %s FROM %s";
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format(paramQuery, "count(id)", table), new String[]{});
        cursor.moveToNext();
        int max = cursor.getInt(0);
        cursor.close();

        for(;offset<=max; offset += number, currentPage++) {
            int page = -1;
            cursor = this.getReadableDatabase().rawQuery(String.format(paramQuery, "id", table) + " LIMIT " + number + " OFFSET " + offset, new String[]{});
            while (cursor.moveToNext()) {
                if(cursor.getInt(0) == id) {
                    page = currentPage;
                }
            }
            cursor.close();
            if(page != -1) {
                return page;
            }
        }
        return -1;
    }

    public void insertOrUpdateAlbum(Album album) throws ValidationException {
        DuplicatedValidator duplicatedValidator = new DuplicatedValidator(album, this.appDatabase);
        if(!duplicatedValidator.validate()) {
            throw new TitleDuplicatedException(this.context, album);
        }
        LengthValidator lengthValidator = new LengthValidator(album.getPrice(), 0, 10000000);
        if(!lengthValidator.validate()) {
            throw new LengthMinMaxException(this.context, 0, 10000000);
        }

        Category category = album.getCategoryItem();
        if(category == null) {
            album.setCategory(0L);
        } else {
            if(category.getId() == 0) {
                category.setId(this.appDatabase.categoryDAO().insertCategories(category)[0]);
                album.setCategoryItem(category);
                album.setCategory(category.getId());
            }
        }
        album.setId(this.appDatabase.albumDAO().insertAlbums(album)[0]);



        int position = this.insertOrUpdateBaseMediaObject(statement, album);
        if(album.getType() != null) {
            statement.bindString(++position, album.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindLong(++position, album.getNumberOfDisks());
        if(album.getLastHeard() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(album.getLastHeard(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(album.getId() == 0) {
            album.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        for(Song song : album.getSongs()) {
            this.insertOrUpdateSong(song, album.getId());
        }
        this.saveForeignTables(album, album.getTable());
    }

    public List<Album> getAlbums(String where, int number, int offset) throws ParseException {
        List<Album> albums = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM albums" + this.where(where) + " LIMIT " + number + " OFFSET " + offset, null);
        while (cursor.moveToNext()) {
            Album album = new Album();
            this.getMediaObjectFromCursor(cursor, album, Database.ALBUMS);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                album.setType(!type.trim().isEmpty() ? Album.Type.valueOf(type) : Album.Type.AudioCD);
            }
            album.setNumberOfDisks(cursor.getInt(cursor.getColumnIndex("numberOfDisks")));
            String dt = cursor.getString(cursor.getColumnIndex("last_heard"));
            if(dt != null && !dt.isEmpty()) {
                album.setLastHeard(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            album.setSongs(this.getSongs("album=" + album.getId()));
            albums.add(album);
        }
        cursor.close();
        return albums;
    }

    public void insertOrUpdateMovie(Movie movie) {
        SQLiteStatement statement = this.getStatement(movie, Arrays.asList(Database.TYPE, Database.LENGTH, Database.PATH, "last_seen"));
        int position = this.insertOrUpdateBaseMediaObject(statement, movie);
        if(movie.getType() != null) {
            statement.bindString(++position, movie.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindDouble(++position, movie.getLength());
        statement.bindString(++position, movie.getPath());
        if(movie.getLastSeen() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(movie.getLastSeen(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(movie.getId()==0) {
            movie.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(movie, movie.getTable());
    }

    public List<Movie> getMovies(String where, int number, int offset) throws ParseException {
        List<Movie> movies = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM movies" + this.where(where) + " LIMIT " + number + " OFFSET " + offset, null);
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            this.getMediaObjectFromCursor(cursor, movie, Database.MOVIES);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                movie.setType(!type.trim().isEmpty() ? Movie.Type.valueOf(type) : Movie.Type.DVD);
            }
            movie.setLength(cursor.getDouble(cursor.getColumnIndex(Database.LENGTH)));
            movie.setPath(cursor.getString(cursor.getColumnIndex(Database.PATH)));
            String dt = cursor.getString(cursor.getColumnIndex("last_seen"));
            if(dt != null && !dt.isEmpty()) {
                movie.setLastSeen(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            movies.add(movie);
        }
        cursor.close();
        return movies;
    }

    public void insertOrUpdateBook(Book book) {
        SQLiteStatement statement = this.getStatement(book, Arrays.asList(Database.TYPE, "numberOfPages", Database.PATH, "edition", "topics", "last_read"));
        int position = this.insertOrUpdateBaseMediaObject(statement, book);
        if(book.getType()!=null) {
            statement.bindString(++position, book.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindDouble(++position, book.getNumberOfPages());
        statement.bindString(++position, book.getPath());
        statement.bindString(++position, book.getEdition());
        statement.bindString(++position, TextUtils.join("\n", book.getTopics()));
        if(book.getLastRead() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(book.getLastRead(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(book.getId() == 0) {
            book.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(book, book.getTable());
    }

    public List<Book> getBooks(String where, int number, int offset) throws ParseException {
        List<Book> books = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM books" + this.where(where) + " LIMIT " + number + " OFFSET " + offset, null);
        while (cursor.moveToNext()) {
            Book book = new Book();
            this.getMediaObjectFromCursor(cursor, book, Database.BOOKS);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                book.setType(!type.trim().isEmpty() ? Book.Type.valueOf(type) : Book.Type.book);
            }
            book.setNumberOfPages(cursor.getInt(cursor.getColumnIndex("numberOfPages")));
            book.setPath(cursor.getString(cursor.getColumnIndex(Database.PATH)));
            book.setEdition(cursor.getString(cursor.getColumnIndex("edition")));
            book.setTopics(Arrays.asList(cursor.getString(cursor.getColumnIndex("topics")).split("\n")));
            String dt = cursor.getString(cursor.getColumnIndex("last_read"));
            if(dt != null && !dt.isEmpty()) {
                book.setLastRead(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            books.add(book);
        }
        cursor.close();
        return books;
    }

    public void insertOrUpdateGame(Game game) {
        SQLiteStatement statement = this.getStatement(game, Arrays.asList(Database.TYPE, Database.LENGTH, "last_played"));
        int position = this.insertOrUpdateBaseMediaObject(statement, game);
        if(game.getType() != null) {
            statement.bindString(++position, game.getType().name());
        } else {
            statement.bindNull(++position);
        }
        statement.bindDouble(++position, game.getLength());
        if(game.getLastPlayed() != null) {
            statement.bindString(++position, Objects.requireNonNull(ConvertHelper.convertDateToString(game.getLastPlayed(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(++position);
        }

        if(game.getId() == 0) {
            game.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(game, game.getTable());
    }

    public List<Game> getGames(String where, int number, int offset) throws ParseException {
        List<Game> games = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM games" + this.where(where) + " LIMIT " + number + " OFFSET " + offset, null);
        while (cursor.moveToNext()) {
            Game game = new Game();
            this.getMediaObjectFromCursor(cursor, game, Database.GAMES);
            String type = cursor.getString(cursor.getColumnIndex(Database.TYPE));
            if(type != null) {
                game.setType(!type.trim().isEmpty() ? Game.Type.valueOf(type) : Game.Type.computer);
            }
            game.setLength(cursor.getDouble(cursor.getColumnIndex(Database.LENGTH)));
            String dt = cursor.getString(cursor.getColumnIndex("last_played"));
            if(dt != null && !dt.isEmpty()) {
                game.setLastPlayed(ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT));
            }
            games.add(game);
        }
        cursor.close();
        return games;
    }

    public long insertOrUpdateTreeNode(TreeNode treeNode) {
        SQLiteStatement statement = this.getBaseStatement(treeNode, Arrays.asList("title", "description", "category", "gallery", "system", "parent"));
        statement.bindString(1, treeNode.getTitle());
        statement.bindString(2, treeNode.getDescription());
        if(treeNode.getCategory() != null) {
            if(treeNode.getCategory().getId() == 0) {
                treeNode.getCategory().setId(this.insertOrUpdateBaseObject(treeNode.getCategory(), "categories", "", 0));
            }
            statement.bindLong(3, treeNode.getCategory().getId());
        } else {
            statement.bindNull(3);
        }
        statement.bindLong(4, treeNode.isGallery() ? 1 : 0);
        statement.bindLong(5, treeNode.isSystem() ? 1 : 0);
        if(treeNode.getParent() != null) {
            statement.bindLong(6, treeNode.getParent().getId());
        } else {
            statement.bindNull(6);
        }

        if(treeNode.getId() == 0) {
            treeNode.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        for(BaseDescriptionObject tag : treeNode.getTags()) {
            tag.setId(this.insertOrUpdateBaseObject(tag, "tags", "file_tree", treeNode.getId()));
        }
        return treeNode.getId();
    }

    public TreeNode getRoot() {
        return this.getRoot("");
    }

    public TreeNode getRoot(String where) {
        TreeNode root = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM file_tree WHERE parent is null" + (where.trim().isEmpty() ? "" : " and " + where.trim()), new String[]{});
        while (cursor.moveToNext()) {
            root = this.getTreeNodeFromCursor(cursor);
        }
        cursor.close();
        if(root != null) {
            this.addChildren(root);
        }
        return root;
    }

    public TreeNode getNodeByName(String name) {
        TreeNode treeNode = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM file_tree WHERE title=?", new String[]{name});
        while (cursor.moveToNext()) {
            treeNode = this.getTreeNodeFromCursor(cursor);
        }
        cursor.close();
        return treeNode;
    }

    public TreeNode getNodeById(long id) {
        TreeNode treeNode = null;
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM file_tree WHERE id=?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            treeNode = this.getTreeNodeFromCursor(cursor);
        }
        cursor.close();
        return treeNode;
    }

    public byte[] loadImage(long id, String table, String column) {
        byte[] content = null;
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s WHERE id=?", column, table), new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            content = cursor.getBlob(cursor.getColumnIndex(column));
        }
        cursor.close();
        return content;
    }

    private void addChildren(TreeNode root) {
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM file_tree WHERE parent=?", new String[]{String.valueOf(root.getId())});
        while (cursor.moveToNext()) {
            TreeNode child = this.getTreeNodeFromCursor(cursor);
            child.setParent(root);
            this.addChildren(child);
            root.getChildren().add(child);
        }
        cursor.close();
    }

    private TreeNode getTreeNodeFromCursor(Cursor cursor) {
        TreeNode treeNode = new TreeNode();
        treeNode.setId(cursor.getLong(cursor.getColumnIndex("id")));
        treeNode.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
        treeNode.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
        treeNode.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
        long category = cursor.getLong(cursor.getColumnIndex("category"));
        if(category != 0) {
            treeNode.setCategory(this.getBaseObjects(Database.CATEGORIES, "", category, "").get(0));
        }
        treeNode.setGallery(cursor.getInt(cursor.getColumnIndex("gallery")) == 1);
        treeNode.setSystem(cursor.getInt(cursor.getColumnIndex("system")) == 1);
        treeNode.setTags(this.getBaseObjects(Database.TAGS, "file_tree", 0, ""));
        for(TreeFile file : this.getTreeNodeFiles("parent=" + treeNode.getId())) {
            file.setParent(treeNode);
            treeNode.getFiles().add(file);
        }
        return treeNode;
    }

    public void insertOrUpdateTreeNodeFiles(TreeFile treeFile) {
        SQLiteStatement statement = this.getBaseStatement(treeFile, Arrays.asList("title", "description", "category", "parent", "internalId", "internalTable", "internalColumn", "pathToFile", "embeddedContent"));
        statement.bindString(1, treeFile.getTitle());
        statement.bindString(2, treeFile.getDescription());
        if(treeFile.getCategory() != null) {
            if(treeFile.getCategory().getId() == 0) {
                treeFile.getCategory().setId(this.insertOrUpdateBaseObject(treeFile.getCategory(), "categories", "", 0));
            }
            statement.bindLong(3, treeFile.getCategory().getId());
        } else {
            statement.bindNull(3);
        }
        if(treeFile.getParent() != null) {
            statement.bindLong(4, treeFile.getParent().getId());
        } else {
            statement.bindNull(4);
        }
        statement.bindLong(5, treeFile.getInternalId());
        statement.bindString(6, treeFile.getInternalTable());
        statement.bindString(7, treeFile.getInternalColumn());
        statement.bindString(8, treeFile.getPathToFile());
        if(treeFile.getEmbeddedContent() != null) {
            statement.bindBlob(9, treeFile.getEmbeddedContent());
        } else {
            statement.bindNull(9);
        }

        if(treeFile.getId() == 0) {
            treeFile.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        for(BaseDescriptionObject tag : treeFile.getTags()) {
            tag.setId(this.insertOrUpdateBaseObject(tag, "tags", "file_tree_file", treeFile.getId()));
        }
    }

    public List<TreeFile> getTreeNodeFiles(String where) {
        List<TreeFile> treeFiles = new LinkedList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM file_tree_file" + this.where(where), new String[]{});
        while (cursor.moveToNext()) {
            TreeFile treeFile = new TreeFile();
            treeFile.setId(cursor.getLong(cursor.getColumnIndex("id")));
            treeFile.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            treeFile.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
            long category = cursor.getLong(cursor.getColumnIndex("category"));
            if(category != 0) {
                treeFile.setCategory(this.getBaseObjects(Database.CATEGORIES, "", category, "").get(0));
            }
            treeFile.setInternalId(cursor.getLong(cursor.getColumnIndex("internalId")));
            treeFile.setInternalTable(cursor.getString(cursor.getColumnIndex("internalTable")));
            treeFile.setInternalColumn(cursor.getString(cursor.getColumnIndex("internalColumn")));
            treeFile.setPathToFile(cursor.getString(cursor.getColumnIndex("pathToFile")));
            treeFile.setEmbeddedContent(cursor.getBlob(cursor.getColumnIndex("embeddedContent")));
            treeFile.setTags(this.getBaseObjects(Database.TAGS, "file_tree_file", 0, ""));
            treeFiles.add(treeFile);
        }
        cursor.close();

        return treeFiles;
    }

    public void insertOrUpdateLibraryObject(LibraryObject libraryObject, BaseMediaObject baseMediaObject) {
        SQLiteStatement sqLiteStatement = this.getBaseStatement(libraryObject, Arrays.asList("media", Database.TYPE, "person", "numberOfDays", "numberOfWeeks", Database.DEAD_LINE, "returnedAt"));
        sqLiteStatement.bindLong(1, baseMediaObject.getId());
        if(baseMediaObject instanceof Book) {
            sqLiteStatement.bindString(2, Database.BOOKS);
        }
        if(baseMediaObject instanceof Movie) {
            sqLiteStatement.bindString(2, Database.MOVIES);
        }
        if(baseMediaObject instanceof Album) {
            sqLiteStatement.bindString(2, Database.ALBUMS);
        }
        if(baseMediaObject instanceof Game) {
            sqLiteStatement.bindString(2, Database.GAMES);
        }
        sqLiteStatement.bindLong(3, this.insertOrUpdatePerson(libraryObject.getPerson(), "", 0));
        sqLiteStatement.bindLong(4, libraryObject.getNumberOfDays());
        sqLiteStatement.bindLong(5, libraryObject.getNumberOfWeeks());
        if(libraryObject.getDeadLine() != null) {
            sqLiteStatement.bindString(6, Objects.requireNonNull(ConvertHelper.convertDateToString(libraryObject.getDeadLine(), Database.DATE_FORMAT)));
        } else {
            sqLiteStatement.bindNull(6);
        }
        if(libraryObject.getReturned() != null) {
            sqLiteStatement.bindString(7, Objects.requireNonNull(ConvertHelper.convertDateToString(libraryObject.getReturned(), Database.DATE_FORMAT)));
        } else {
            sqLiteStatement.bindNull(7);
        }
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    public List<LibraryObject> getLibraryObjects(String where) throws ParseException {
        List<LibraryObject> libraryObjects = new LinkedList<>();
        List<Person> persons = this.getPersons("", 0);
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM library" + this.where(where), null);
        while (cursor.moveToNext()) {
            LibraryObject libraryObject = new LibraryObject();
            for(Person person : persons) {
                if(cursor.getLong(cursor.getColumnIndex("person"))==person.getId()) {
                    libraryObject.setPerson(person);
                    break;
                }
            }
            libraryObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
            libraryObject.setNumberOfDays(cursor.getInt(cursor.getColumnIndex("numberOfDays")));
            libraryObject.setNumberOfWeeks(cursor.getInt(cursor.getColumnIndex("numberOfWeeks")));
            String deadline = cursor.getString(cursor.getColumnIndex(Database.DEAD_LINE));
            if(deadline!=null) {
                libraryObject.setDeadLine(!deadline.isEmpty() ? ConvertHelper.convertStringToDate(deadline, Database.DATE_FORMAT) : null);
            }
            String returnedAt = cursor.getString(cursor.getColumnIndex("returnedAt"));
            if(returnedAt!=null) {
                libraryObject.setReturned(!returnedAt.isEmpty() ? ConvertHelper.convertStringToDate(returnedAt, Database.DATE_FORMAT) : null);
            }
            libraryObjects.add(libraryObject);
        }
        cursor.close();
        return libraryObjects;
    }

    public Map<BaseMediaObject, LibraryObject> getLendOutObjects(Person person, int number, int offset) throws ParseException {
        Map<BaseMediaObject, LibraryObject> mp = new LinkedHashMap<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT media, type, id FROM library WHERE person=" + person.getId(), new String[]{});
        while (cursor.moveToNext()) {
            LibraryObject libraryObject = this.getLibraryObjects(Database.ID_FILTER + cursor.getLong(cursor.getColumnIndex("id"))).get(0);
            BaseMediaObject baseMediaObject = null;
            long id = cursor.getLong(cursor.getColumnIndex("media"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            switch (type.trim().toLowerCase()) {
                case Database.BOOKS:
                    baseMediaObject = this.getBooks(Database.ID_FILTER + id, number, offset).get(0);
                    break;
                case Database.MOVIES:
                    baseMediaObject = this.getMovies(Database.ID_FILTER + id, number, offset).get(0);
                    break;
                case "albums":
                    baseMediaObject = this.getAlbums(Database.ID_FILTER + id, number, offset).get(0);
                    break;
                case Database.GAMES:
                    baseMediaObject = this.getGames(Database.ID_FILTER + id, number, offset).get(0);
                    break;
            }
            mp.put(baseMediaObject, libraryObject);
        }
        cursor.close();
        return mp;
    }

    public void addMediaToList(long list, long media, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM media_lists WHERE list=" + list + " AND media=" + media + " AND type='" + type + "'");
        SQLiteStatement statement = db.compileStatement("INSERT INTO media_lists(list, media, type) VALUES(?, ?, ?)");
        statement.bindLong(1, list);
        statement.bindLong(2, media);
        statement.bindString(3, type);
        statement.executeInsert();
        statement.close();
    }

    public long insertOrUpdateMediaList(MediaList mediaList) {
        SQLiteStatement sqLiteStatement = this.getBaseStatement(mediaList, Arrays.asList(Database.TITLE, Database.DEAD_LINE, Database.DESCRIPTION));
        sqLiteStatement.bindString(1, mediaList.getTitle());
        if(mediaList.getDeadLine() != null) {
            String content = ConvertHelper.convertDateToString(mediaList.getDeadLine(), Database.DATE_FORMAT);
            if(content == null) {
                sqLiteStatement.bindNull(2);
            } else {
                sqLiteStatement.bindString(2, content);
            }
        } else {
            sqLiteStatement.bindNull(2);
        }
        sqLiteStatement.bindString(3, mediaList.getDescription());

        if(mediaList.getId() != 0) {
            sqLiteStatement.execute();
        } else {
            mediaList.setId(sqLiteStatement.executeInsert());
        }
        sqLiteStatement.close();

        this.getWritableDatabase().execSQL("DELETE FROM media_lists WHERE list=?", new String[]{String.valueOf(mediaList.getId())});

        for(BaseMediaObject baseMediaObject : mediaList.getBaseMediaObjects()) {
            sqLiteStatement = this.getWritableDatabase().compileStatement("INSERT INTO media_lists(list, media, type) VALUES(?, ?, ?)");
            sqLiteStatement.bindLong(1, mediaList.getId());
            sqLiteStatement.bindLong(2, baseMediaObject.getId());
            if(baseMediaObject instanceof Book) {
                sqLiteStatement.bindString(3, Database.BOOKS);
            }
            if(baseMediaObject instanceof Movie) {
                sqLiteStatement.bindString(3, Database.MOVIES);
            }
            if(baseMediaObject instanceof Album) {
                sqLiteStatement.bindString(3, Database.ALBUMS);
            }
            if(baseMediaObject instanceof Game) {
                sqLiteStatement.bindString(3, Database.GAMES);
            }
            sqLiteStatement.executeInsert();
            sqLiteStatement.close();
        }
        return mediaList.getId();
    }

    public List<MediaList> getMediaLists(String where, int number, int offset) throws ParseException {
        List<MediaList> mediaLists = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM lists" + this.where(where), null);
        while (cursor.moveToNext()) {
            MediaList mediaList = new MediaList();
            mediaList.setId(cursor.getLong(cursor.getColumnIndex("id")));
            mediaList.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            mediaList.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
            String deadLine = cursor.getString(cursor.getColumnIndex(Database.DEAD_LINE));
            if(deadLine != null) {
                mediaList.setDeadLine(!deadLine.isEmpty() ? ConvertHelper.convertStringToDate(deadLine, Database.DATE_FORMAT) : null);
            }
            mediaList.setBaseMediaObjects(this.getObjects("lists", mediaList.getId(), number, offset));
            mediaLists.add(mediaList);
        }
        cursor.close();
        return mediaLists;
    }

    public void insertOrUpdateFilter(MediaFilter mediaFilter) {
        List<MediaFilter> mediaFilters = this.getFilters("title='" + mediaFilter.getTitle() + "'");
        if(mediaFilters!=null && !mediaFilters.isEmpty()) {
            mediaFilter.setId(mediaFilters.get(0).getId());
        }
        SQLiteStatement sqLiteStatement = this.getBaseStatement(mediaFilter, Arrays.asList(Database.TITLE, "search", Database.CATEGORIES, Database.TAGS, Database.BOOKS, Database.MOVIES, "music", Database.GAMES, "customFields", "list"));
        sqLiteStatement.bindString(1, mediaFilter.getTitle());
        sqLiteStatement.bindString(2, mediaFilter.getSearch());
        sqLiteStatement.bindString(3, mediaFilter.getCategories());
        sqLiteStatement.bindString(4, mediaFilter.getTags());
        sqLiteStatement.bindLong(5, mediaFilter.isBooks() ? 1 : 0);
        sqLiteStatement.bindLong(6, mediaFilter.isMovies() ? 1 : 0);
        sqLiteStatement.bindLong(7, mediaFilter.isMusic() ? 1 : 0);
        sqLiteStatement.bindLong(8, mediaFilter.isGames() ? 1 : 0);
        sqLiteStatement.bindString(9, mediaFilter.getCustomFields());
        if(mediaFilter.getMediaList() != null) {
            sqLiteStatement.bindLong(10, mediaFilter.getMediaList().getId());
        } else {
            sqLiteStatement.bindNull(10);
        }
        sqLiteStatement.execute();
        sqLiteStatement.close();
    }

    public List<MediaFilter> getFilters(String where) {
        List<MediaFilter> mediaFilters = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM filters" + this.where(where), null);
        while (cursor.moveToNext()) {
            MediaFilter mediaFilter = new MediaFilter();
            mediaFilter.setId(cursor.getLong(cursor.getColumnIndex("id")));
            mediaFilter.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            mediaFilter.setSearch(cursor.getString(cursor.getColumnIndex("search")));
            mediaFilter.setCategories(cursor.getString(cursor.getColumnIndex(Database.CATEGORIES)));
            mediaFilter.setTags(cursor.getString(cursor.getColumnIndex(Database.TAGS)));
            mediaFilter.setBooks(cursor.getInt(cursor.getColumnIndex(Database.BOOKS)) == 1);
            mediaFilter.setMovies(cursor.getInt(cursor.getColumnIndex(Database.MOVIES)) == 1);
            mediaFilter.setMusic(cursor.getInt(cursor.getColumnIndex("music")) == 1);
            mediaFilter.setGames(cursor.getInt(cursor.getColumnIndex(Database.GAMES)) == 1);
            mediaFilter.setCustomFields(cursor.getString(cursor.getColumnIndex("customFields")));
            try {
                int id = cursor.getInt(cursor.getColumnIndex("list"));
                if(id != 0) {
                    List<MediaList> lists = this.getMediaLists("ID=" + id, -1, 0);
                    if(lists != null) {
                        if(!lists.isEmpty()) {
                            mediaFilter.setMediaList(lists.get(0));
                        }
                    }
                }
            } catch (Exception ignored) {}
            mediaFilters.add(mediaFilter);
        }
        cursor.close();
        return mediaFilters;
    }

    public long insertOrUpdateCustomField(CustomField customField) {
        SQLiteStatement statement = this.getBaseStatement(customField, Arrays.asList(Database.TITLE, Database.DESCRIPTION, Database.TYPE, "allowedValues", Database.BOOKS, Database.MOVIES, Database.ALBUMS, Database.GAMES));
        statement.bindString(1, customField.getTitle());
        statement.bindString(2, customField.getDescription());
        statement.bindString(3, customField.getType());
        statement.bindString(4, customField.getAllowedValues());
        statement.bindLong(5, customField.isBooks() ? 1 : 0);
        statement.bindLong(6, customField.isMovies() ? 1 : 0);
        statement.bindLong(7, customField.isAlbums() ? 1 : 0);
        statement.bindLong(8, customField.isGames() ? 1 : 0);
        if(customField.getId() != 0) {
            statement.execute();
            statement.close();
            return customField.getId();
        } else {
            long id = statement.executeInsert();
            statement.close();
            return id;
        }
    }

    public List<CustomField> getCustomFields(String where) {
        List<CustomField> customFields = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM customFields" + this.where(where), null);
        while (cursor.moveToNext()) {
            CustomField customField = new CustomField();
            customField.setId(cursor.getLong(cursor.getColumnIndex("id")));
            customField.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            customField.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
            customField.setType(cursor.getString(cursor.getColumnIndex(Database.TYPE)));
            customField.setAllowedValues(cursor.getString(cursor.getColumnIndex("allowedValues")));
            customField.setBooks(cursor.getInt(cursor.getColumnIndex(Database.BOOKS)) == 1);
            customField.setMovies(cursor.getInt(cursor.getColumnIndex(Database.MOVIES)) == 1);
            customField.setAlbums(cursor.getInt(cursor.getColumnIndex(Database.ALBUMS)) == 1);
            customField.setGames(cursor.getInt(cursor.getColumnIndex(Database.GAMES)) == 1);
            customFields.add(customField);
        }
        cursor.close();
        return customFields;
    }

    public List<BaseMediaObject> getObjects(String table, long id, int number, int offset) {
        String start = "id IN (";
        String booksWhere = start, moviesWhere = start, gamesWhere = start, albumsWhere = start;

        switch (table) {
            case Database.TAGS:
            case "persons":
            case "companies":
                booksWhere += this.getWhere(Database.BOOKS, table, id);
                moviesWhere += this.getWhere(Database.MOVIES, table, id);
                gamesWhere += this.getWhere(Database.GAMES, table, id);
                albumsWhere += this.getWhere(Database.ALBUMS, table, id);
                break;
            case Database.CATEGORIES:
                booksWhere += this.getCategoryWhere(Database.BOOKS, id);
                moviesWhere += this.getCategoryWhere(Database.MOVIES, id);
                gamesWhere += this.getCategoryWhere(Database.GAMES, id);
                albumsWhere += this.getCategoryWhere(Database.ALBUMS, id);
                break;
            case "lists":
                booksWhere += this.getListWhere(Database.BOOKS, id);
                moviesWhere += this.getListWhere(Database.MOVIES, id);
                gamesWhere += this.getListWhere(Database.GAMES, id);
                albumsWhere += this.getListWhere(Database.ALBUMS, id);
                break;
        }

        Map<DatabaseObject, String> mp = new LinkedHashMap<>();
        mp.put(new Book(), booksWhere);
        mp.put(new Movie(), moviesWhere);
        mp.put(new Game(), gamesWhere);
        mp.put(new Album(), albumsWhere);
        return this.getObjectList(mp, number, offset, "");
    }

    public void deleteItem(DatabaseObject databaseObject) {
        this.getWritableDatabase().execSQL(String.format("DELETE FROM %s WHERE id=%s", databaseObject.getTable(), databaseObject.getId()));
    }

    public void deleteItem(BaseDescriptionObject baseDescriptionObject, String table) {
        this.getWritableDatabase().execSQL(String.format("DELETE FROM %s WHERE id=%s", table, baseDescriptionObject.getId()));
    }

    public long insertOrUpdateBaseObject(BaseDescriptionObject baseDescriptionObject, String table, String foreignTable, long id) {
        // return if object is empty
        if(baseDescriptionObject.getTitle().trim().isEmpty()) {
            return 0L;
        }

        // get id if object already exists
        for(BaseDescriptionObject tmp : this.getBaseObjects(table, "", id, "")) {
            if(tmp.getTitle().equals(baseDescriptionObject.getTitle())) {
                baseDescriptionObject.setId(tmp.getId());
                break;
            }
        }

        SQLiteStatement statement;
        if(baseDescriptionObject.getId() == 0) {
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s(title, description) VALUES(?, ?)", table));
        } else {
            statement = this.getWritableDatabase().compileStatement(String.format("UPDATE %s SET title=?, description=? WHERE id=?", table));
            statement.bindLong(3, baseDescriptionObject.getId());
        }
        statement.bindString(1, baseDescriptionObject.getTitle());
        statement.bindString(2, baseDescriptionObject.getDescription());

        if(baseDescriptionObject.getId() == 0) {
            baseDescriptionObject.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        if(!foreignTable.trim().isEmpty()) {
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_%s WHERE %s=%s AND %s=%s", foreignTable, table, foreignTable, id, table, baseDescriptionObject.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_%s(%s, %s) VALUES(?, ?)", foreignTable, table, table, foreignTable));
            statement.bindLong(1, baseDescriptionObject.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
        return baseDescriptionObject.getId();
    }

    public List<BaseDescriptionObject> getBaseObjects(String table, String foreignTable, long id, String where) {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s" + this.where(where), table), null);
            while (cursor.moveToNext()) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
                baseDescriptionObject.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
                baseDescriptionObject.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            cursor.close();
        } else {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s_%s WHERE %s=%s", foreignTable, table, foreignTable, id), null);
            while (cursor.moveToNext()) {
                Cursor tmp = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s WHERE id=%s", table, cursor.getLong(cursor.getColumnIndex(table))), null);
                while (tmp.moveToNext()) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setId(tmp.getLong(tmp.getColumnIndex("id")));
                    baseDescriptionObject.setTitle(tmp.getString(tmp.getColumnIndex(Database.TITLE)));
                    baseDescriptionObject.setDescription(tmp.getString(tmp.getColumnIndex(Database.DESCRIPTION)));
                    baseDescriptionObjects.add(baseDescriptionObject);
                }
                tmp.close();
            }
            cursor.close();
        }

        return baseDescriptionObjects;
    }

    public long insertOrUpdatePerson(Person person, String foreignTable, long id) {
        try {
            if(person.getFirstName().trim().equals("") && person.getLastName().trim().equals("")) {
                return 0L;
            }

            for(Person tmp : this.getPersons("", 0)) {
                if(tmp.getFirstName().equals(person.getFirstName()) && tmp.getLastName().equals(person.getLastName())) {
                    person.setId(tmp.getId());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SQLiteStatement statement;
        if(person.getId() == 0) {
            statement = this.getWritableDatabase().compileStatement("INSERT INTO persons(firstName, lastName, birthDate, image, description) VALUES(?, ?, ?, ?, ?)");
        } else {
            statement = this.getWritableDatabase().compileStatement("UPDATE persons SET firstName=?, lastName=?, birthDate=?, image=?, description=? WHERE id=?");
            statement.bindLong(6, person.getId());
        }
        statement.bindString(1, person.getFirstName());
        statement.bindString(2, person.getLastName());
        if(person.getBirthDate() != null) {
            String dt = ConvertHelper.convertDateToString(person.getBirthDate(), Database.DATE_FORMAT);
            if(dt != null) {
                statement.bindString(3, dt);
            } else {
                statement.bindNull(3);
            }
        } else {
            statement.bindNull(3);
        }
        if(person.getImage()!=null) {
            statement.bindBlob(4, person.getImage());
        } else {
            statement.bindNull(4);
        }
        statement.bindString(5, person.getDescription());

        if(person.getId() == 0) {
            person.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        if(!foreignTable.isEmpty()) {
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_persons WHERE %s=%s AND persons=%s", foreignTable, foreignTable, id, person.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_persons(persons, %s) VALUES(?, ?)", foreignTable, foreignTable));
            statement.bindLong(1, person.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
        return person.getId();
    }

    public List<Person> getPersons(String where) throws ParseException {
        List<Person> people = new LinkedList<>();

        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM persons" + this.where(where), null);
        while (cursor.moveToNext()) {
            Person person = new Person();
            person.setId(cursor.getLong(cursor.getColumnIndex("id")));
            person.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
            person.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
            int index = cursor.getColumnIndex("birthDate");
            String dt = cursor.getString(index);
            if(dt!=null) {
                person.setBirthDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
            }
            try {
                person.setImage(cursor.getBlob(cursor.getColumnIndex("image")));
            } catch (Exception ignored) {}
            person.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
            people.add(person);
        }
        cursor.close();
        return people;
    }

    public List<Person> getPersons(String foreignTable, long id) throws ParseException {
        List<Person> people = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM persons", null);
            while (cursor.moveToNext()) {
                Person person = new Person();
                person.setId(cursor.getLong(cursor.getColumnIndex("id")));
                person.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
                person.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
                int index = cursor.getColumnIndex("birthDate");
                String dt = cursor.getString(index);
                if(dt!=null) {
                    person.setBirthDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                }
                try {
                    person.setImage(cursor.getBlob(cursor.getColumnIndex("image")));
                } catch (Exception ignored) {}
                person.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
                people.add(person);
            }
            cursor.close();
        } else {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s_persons WHERE %s=%s", foreignTable, foreignTable, id), null);
            while (cursor.moveToNext()) {
                Cursor tmp = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM persons WHERE id=%s", cursor.getLong(cursor.getColumnIndex("persons"))), null);
                while (tmp.moveToNext()) {
                    Person person = new Person();
                    person.setId(tmp.getLong(tmp.getColumnIndex("id")));
                    person.setFirstName(tmp.getString(tmp.getColumnIndex("firstName")));
                    person.setLastName(tmp.getString(tmp.getColumnIndex("lastName")));
                    String dt = tmp.getString(tmp.getColumnIndex("birthDate"));
                    if(dt!=null) {
                        person.setBirthDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                    }
                    try {
                        person.setImage(tmp.getBlob(tmp.getColumnIndex("image")));
                    } catch (Exception ignored) {}
                    person.setDescription(tmp.getString(tmp.getColumnIndex(Database.DESCRIPTION)));
                    people.add(person);
                }
                tmp.close();
            }
            cursor.close();
        }

        return people;
    }

    public void insertOrUpdateCompany(Company company, String foreignTable, long id) {
        try {
            if(company.getTitle().trim().equals("")) {
                return;
            }

            for(Company tmp : this.getCompanies("", 0)) {
                if(tmp.getTitle().equals(company.getTitle())) {
                    company.setId(tmp.getId());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SQLiteStatement statement;
        if(company.getId() == 0) {
            statement = this.getWritableDatabase().compileStatement("INSERT INTO companies(title, foundation, cover, description) VALUES(?, ?, ?, ?)");
        } else {
            statement = this.getWritableDatabase().compileStatement("UPDATE companies SET title=?, foundation=?, cover=?, description=? WHERE id=?");
            statement.bindLong(5, company.getId());
        }
        statement.bindString(1, company.getTitle());
        if(company.getFoundation() != null) {
            statement.bindString(2, Objects.requireNonNull(ConvertHelper.convertDateToString(company.getFoundation(), Database.DATE_FORMAT)));
        } else {
            statement.bindNull(2);
        }
        if(company.getCover() != null) {
            statement.bindBlob(3, company.getCover());
        } else {
            statement.bindNull(3);
        }
        statement.bindString(4, company.getDescription());

        if(company.getId() == 0) {
            company.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        if(!foreignTable.isEmpty()) {
            this.getWritableDatabase().execSQL(String.format("DELETE FROM %s_companies WHERE %s=%s AND companies=%s", foreignTable, foreignTable, id, company.getId()));
            statement = this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s_companies(companies, %s) VALUES(?, ?)", foreignTable, foreignTable));
            statement.bindLong(1, company.getId());
            statement.bindLong(2, id);
            statement.executeInsert();
            statement.close();
        }
    }

    public List<Company> getCompanies(String where) throws ParseException {
        List<Company> companies = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM companies" + this.where(where), null);
        while (cursor.moveToNext()) {
            Company company = new Company();
            company.setId(cursor.getLong(cursor.getColumnIndex("id")));
            company.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
            String dt = cursor.getString(cursor.getColumnIndex("foundation"));
            if(dt!=null) {
                company.setFoundation(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
            }
            company.setCover(cursor.getBlob(cursor.getColumnIndex(Database.COVER)));
            company.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
            companies.add(company);
        }
        cursor.close();
        return companies;
    }

    public List<Company> getCompanies(String foreignTable, long id) throws ParseException {
        List<Company> companies = new LinkedList<>();
        if(foreignTable.trim().isEmpty()) {
            Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM companies", null);
            while (cursor.moveToNext()) {
                Company company = new Company();
                company.setId(cursor.getLong(cursor.getColumnIndex("id")));
                company.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
                String dt = cursor.getString(cursor.getColumnIndex("foundation"));
                if(dt!=null) {
                    company.setFoundation(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                }
                company.setCover(cursor.getBlob(cursor.getColumnIndex(Database.COVER)));
                company.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
                companies.add(company);
            }
            cursor.close();
        } else {
            Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM %s_companies WHERE %s=%s", foreignTable, foreignTable, id), null);
            while (cursor.moveToNext()) {
                Cursor tmp = this.getReadableDatabase().rawQuery(String.format("SELECT * FROM companies WHERE id=%s", cursor.getLong(cursor.getColumnIndex("companies"))), null);
                while (tmp.moveToNext()) {
                    Company company = new Company();
                    company.setId(tmp.getLong(tmp.getColumnIndex("id")));
                    company.setTitle(tmp.getString(tmp.getColumnIndex(Database.TITLE)));
                    String dt = tmp.getString(tmp.getColumnIndex("foundation"));
                    if(dt!=null) {
                        company.setFoundation(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
                    }
                    company.setCover(tmp.getBlob(tmp.getColumnIndex(Database.COVER)));
                    company.setDescription(tmp.getString(tmp.getColumnIndex(Database.DESCRIPTION)));
                    companies.add(company);
                }
                tmp.close();
            }
            cursor.close();
        }

        return companies;
    }

    public void insertExampleData() {
        for(int i = 0; i<=10; i++) {
            Book book = new Book();
            book.setTitle("book " + i);
            this.insertOrUpdateBook((Book) this.setFields(book));
        }
        for(int i = 0; i<=10; i++) {
            Movie movie = new Movie();
            movie.setTitle("movie " + i);
            this.insertOrUpdateMovie((Movie) this.setFields(movie));
        }
        for(int i = 0; i<=10; i++) {
            Album album = new Album();
            album.setTitle("movie " + i);
            this.insertOrUpdateAlbum((Album) this.setFields(album));
        }
        for(int i = 0; i<=10; i++) {
            Game game = new Game();
            game.setTitle("game " + i);
            this.insertOrUpdateGame((Game) this.setFields(game));
        }
    }

    private BaseMediaObject setFields(BaseMediaObject baseMediaObject) {
        for(int j = 0; j<=3; j++) {
            Random generator = new Random();
            Company company = new Company();
            company.setTitle("Company "  + generator.nextInt(3));
            baseMediaObject.getCompanies().add(company);
        }

        for(int j = 0; j<=3; j++) {
            Random generator = new Random();
            Person person = new Person();
            person.setFirstName("Person");
            person.setLastName(String.valueOf(generator.nextInt(3)));
            baseMediaObject.getPersons().add(person);
        }

        for(int j = 0; j<=3; j++) {
            Random generator = new Random();
            BaseDescriptionObject tag = new BaseDescriptionObject();
            tag.setTitle("Tag "  + generator.nextInt(3));
            baseMediaObject.getTags().add(tag);
        }

        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setTitle("Category");
        baseMediaObject.setCategory(baseDescriptionObject);
        return baseMediaObject;
    }

    private void insertOrUpdateSong(Song song, long id) {
        SQLiteStatement statement = this.getStatement(song, Arrays.asList(Database.LENGTH, Database.PATH, "album"));
        int position = this.insertOrUpdateBaseMediaObject(statement, song);
        statement.bindDouble(++position, song.getLength());
        statement.bindString(++position, song.getPath());
        statement.bindLong(++position, id);

        if(song.getId() == 0) {
            song.setId(statement.executeInsert());
        } else {
            statement.execute();
        }
        statement.close();

        this.saveForeignTables(song, song.getTable());
    }

    private List<Song> getSongs(String where) throws ParseException {
        List<Song> songs = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM songs" + this.where(where), null);
        while (cursor.moveToNext()) {
            Song song = new Song();
            this.getMediaObjectFromCursor(cursor, song, "songs");
            song.setLength(cursor.getDouble(cursor.getColumnIndex(Database.LENGTH)));
            song.setPath(cursor.getString(cursor.getColumnIndex(Database.PATH)));
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    private SQLiteStatement getStatement(DatabaseObject databaseObject, List<String> columns) {
        List<String> baseColumns = Arrays.asList(Database.TITLE, "originalTitle", "releaseDate", "code", "price", "category", Database.COVER, Database.DESCRIPTION, "rating_web", "rating_own", "rating_note");
        String[] allColumns = new String[baseColumns.size() + columns.size()];

        int i = 0;
        for(String column : baseColumns) {
            allColumns[i] = column;
            i++;
        }
        for(String column : columns) {
            allColumns[i] = column;
            i++;
        }

        return this.getBaseStatement(databaseObject, Arrays.asList(allColumns));
    }

    private SQLiteStatement getBaseStatement(DatabaseObject databaseObject, List<String> columns) {
        String[] allColumns = new String[columns.size()];
        String[] allQuestionMarks = new String[columns.size()];

        int i = 0;
        for(String column : columns) {
            allColumns[i] = column;
            allQuestionMarks[i] = "?";
            i++;
        }

        if(databaseObject.getId() == 0) {
            String columnString = TextUtils.join(", ", allColumns);
            String questionString = TextUtils.join(", ", allQuestionMarks);

            return this.getWritableDatabase().compileStatement(String.format("INSERT INTO %s(%s) VALUES(%s)", databaseObject.getTable(), columnString, questionString));
        } else {
            String columnString = (TextUtils.join("=?, ", allColumns)) + "=?";
            return this.getWritableDatabase().compileStatement(String.format("UPDATE %s SET %s WHERE id=%s", databaseObject.getTable(), columnString, databaseObject.getId()));
        }
    }

    private int insertOrUpdateBaseMediaObject(SQLiteStatement sqLiteStatement, BaseMediaObject baseMediaObject) {
        sqLiteStatement.bindString(1, baseMediaObject.getTitle());
        if(baseMediaObject.getOriginalTitle() != null) {
            sqLiteStatement.bindString(2, baseMediaObject.getOriginalTitle());
        } else {
            sqLiteStatement.bindString(2, "");
        }
        if(baseMediaObject.getReleaseDate()!=null) {
            sqLiteStatement.bindString(3, Objects.requireNonNull(ConvertHelper.convertDateToString(baseMediaObject.getReleaseDate(), Database.DATE_FORMAT)));
        } else {
            sqLiteStatement.bindNull(3);
        }
        sqLiteStatement.bindString(4, baseMediaObject.getCode());
        sqLiteStatement.bindDouble(5, baseMediaObject.getPrice());
        if(baseMediaObject.getCategory()!=null) {
            sqLiteStatement.bindLong(6, this.insertOrUpdateBaseObject(baseMediaObject.getCategory(), Database.CATEGORIES, "", 0));
        } else {
            sqLiteStatement.bindLong(6, 0);
        }
        if(baseMediaObject.getCover() != null) {
            sqLiteStatement.bindBlob(7, baseMediaObject.getCover());
        } else {
            sqLiteStatement.bindNull(7);
        }
        if(baseMediaObject.getDescription() != null) {
            sqLiteStatement.bindString(8, baseMediaObject.getDescription());
        } else {
            sqLiteStatement.bindNull(8);
        }
        sqLiteStatement.bindDouble(9, baseMediaObject.getRatingWeb());
        sqLiteStatement.bindDouble(10, baseMediaObject.getRatingOwn());
        if(baseMediaObject.getRatingNote() != null) {
            sqLiteStatement.bindString(11, baseMediaObject.getRatingNote());
        } else {
            sqLiteStatement.bindNull(11);
        }
        return 11;
    }

    private void saveForeignTables(BaseMediaObject baseMediaObject, String table) {
        for(BaseDescriptionObject tag : baseMediaObject.getTags()) {
            this.insertOrUpdateBaseObject(tag, Database.TAGS, table, baseMediaObject.getId());
        }
        for(Person person : baseMediaObject.getPersons()) {
            this.insertOrUpdatePerson(person, table, baseMediaObject.getId());
        }
        for(Company company : baseMediaObject.getCompanies()) {
            this.insertOrUpdateCompany(company, table, baseMediaObject.getId());
        }
        for(LibraryObject libraryObject : baseMediaObject.getLibraryObjects()) {
            this.insertOrUpdateLibraryObject(libraryObject, baseMediaObject);
        }
        if(baseMediaObject.getCustomFieldValues() != null) {
            for(Map.Entry<CustomField, String> entry : baseMediaObject.getCustomFieldValues().entrySet()) {
                this.insertCustomFieldValues(baseMediaObject, entry.getKey(), entry.getValue(), table);
            }
        }
    }

    private void getMediaObjectFromCursor(Cursor cursor, BaseMediaObject baseMediaObject, String table) throws ParseException {
        baseMediaObject.setId(cursor.getLong(cursor.getColumnIndex("id")));
        baseMediaObject.setTitle(cursor.getString(cursor.getColumnIndex(Database.TITLE)));
        baseMediaObject.setOriginalTitle(cursor.getString(cursor.getColumnIndex("originalTitle")));
        String dt = cursor.getString(cursor.getColumnIndex("releaseDate"));
        if(dt != null) {
            baseMediaObject.setReleaseDate(!dt.isEmpty() ? ConvertHelper.convertStringToDate(dt, Database.DATE_FORMAT) : null);
        }
        baseMediaObject.setCode(cursor.getString(cursor.getColumnIndex("code")));
        baseMediaObject.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
        int category = cursor.getInt(cursor.getColumnIndex("category"));
        if(category != 0) {
            baseMediaObject.setCategory(this.getBaseObjects(Database.CATEGORIES, "", 0, Database.ID_FILTER + category).get(0));
        }
        baseMediaObject.setCover(cursor.getBlob(cursor.getColumnIndex(Database.COVER)));
        baseMediaObject.setDescription(cursor.getString(cursor.getColumnIndex(Database.DESCRIPTION)));
        baseMediaObject.setRatingOwn(cursor.getDouble(cursor.getColumnIndex("rating_own")));
        baseMediaObject.setRatingWeb(cursor.getDouble(cursor.getColumnIndex("rating_web")));
        baseMediaObject.setRatingNote(cursor.getString(cursor.getColumnIndex("rating_note")));

        baseMediaObject.setTags(this.getBaseObjects(Database.TAGS, table, baseMediaObject.getId(), ""));
        baseMediaObject.setPersons(this.getPersons(table, baseMediaObject.getId()));
        baseMediaObject.setCompanies(this.getCompanies(table, baseMediaObject.getId()));
        baseMediaObject.setCustomFieldValues(this.getCustomFieldValues(baseMediaObject, table));

        baseMediaObject.setLendOut(false);
        List<LibraryObject> libraryObjects = this.getLibraryObjects("media=" + baseMediaObject.getId() + " AND type='" + table + "'");
        for(LibraryObject libraryObject : libraryObjects) {
            if(libraryObject.getReturned() == null) {
                baseMediaObject.setLendOut(true);
                break;
            }
        }
        baseMediaObject.setLibraryObjects(libraryObjects);
    }

    private void insertCustomFieldValues(BaseMediaObject baseMediaObject, CustomField customField, String value, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(String.format("DELETE FROM %s_customFields WHERE %s=? AND customFields=?", table, table), new String[]{String.valueOf(baseMediaObject.getId()), String.valueOf(customField.getId())});
        SQLiteStatement statement = db.compileStatement(String.format("INSERT INTO %s_customFields(%s, customFields, value) VALUES(?, ?, ?);", table, table));
        statement.bindLong(1, baseMediaObject.getId());
        statement.bindLong(2, customField.getId());
        statement.bindString(3, value);
        statement.execute();
        statement.close();
    }

    private Map<CustomField, String> getCustomFieldValues(BaseMediaObject baseMediaObject, String table) {
        Map<CustomField, String> customFieldValues = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(String.format("SELECT * FROM %s_customFields WHERE %s=?", table, table), new String[]{String.valueOf(baseMediaObject.getId())});
        while (cursor.moveToNext()) {
            long customFieldId = cursor.getLong(cursor.getColumnIndex("customFields"));
            String value = cursor.getString(cursor.getColumnIndex("value"));
            List<CustomField> customFields = this.getCustomFields("id=" + customFieldId);
            if(customFields!=null && !customFields.isEmpty()) {
                customFieldValues.put(customFields.get(0), value);
            }
        }
        cursor.close();
        return customFieldValues;
    }

    private String getListWhere(String table, long id) {
        List<Long> idList = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT media FROM media_lists WHERE list=? AND type=?", new String[]{String.valueOf(id), table});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex("media")));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    private String getCategoryWhere(String table, long id) {
        List<Long> idList = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT id FROM %s WHERE category=?", table), new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex("id")));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    private String getWhere(String table, String foreignTable, long id) {
        List<Long> idList = new LinkedList<>();
        Cursor cursor = this.getReadableDatabase().rawQuery(String.format("SELECT %s FROM %s_%s WHERE %s=?", table, table, foreignTable, foreignTable), new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            idList.add(cursor.getLong(cursor.getColumnIndex(table)));
        }
        cursor.close();
        return TextUtils.join(", ", idList) + ")";
    }

    public void copyDatabase(String path) throws IOException {
        String currentDBPath = this.getWritableDatabase().getPath();
        File currentDB = new File(currentDBPath);
        File backupDB = new File(path);

        if (currentDB.exists()) {
            try (FileChannel src = new FileInputStream(currentDB).getChannel();
                 FileChannel dst = new FileOutputStream(backupDB).getChannel()) {
                dst.transferFrom(src, 0, src.size());
            }
        }
    }

    public void getDatabase(String path) throws IOException {
        this.close();

        String currentDBPath = this.getWritableDatabase().getPath();
        File currentDB = new File(currentDBPath);
        File backupDB = new File(path);

        if (currentDB.exists()) {
            try (FileChannel src = new FileOutputStream(currentDB).getChannel();
                 FileChannel dst = new FileInputStream(backupDB).getChannel()) {

                src.transferFrom(dst, 0, dst.size());
            }
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        for(String media : new String[]{"albums", "songs", "movies", "games", "books"}) {
            for(String sub : new String[]{"", "_tags", "_persons", "_companies", "_customfields"}) {
                db.execSQL(String.format("DELETE FROM %s%s", media, sub), new Object[]{});
            }
        }
        db.execSQL("DELETE FROM filters");
        db.execSQL("DELETE FROM media_lists");
        db.execSQL("DELETE FROM lists");
        db.execSQL("DELETE FROM library");
        db.execSQL("DELETE FROM companies");
        db.execSQL("DELETE FROM persons");
        db.execSQL("DELETE FROM categories");
        db.execSQL("DELETE FROM tags");
    }

    private String where(String where) {
        if(!where.trim().isEmpty()) {
            return " WHERE " + where;
        }
        return "";
    }

    private void insertTag(long id, String type, Tag... tags) {
        if(id != 0) {
            switch (type.toLowerCase()) {
                case Database.MEDIA_ALBUM ->
                        this.appDatabase.albumDAO().getAlbumWithTags(id).getTags().forEach(tag -> {
                            this.appDatabase.tagDAO().deleteTags(tag);
                        });
                case Database.MEDIA_BOOK ->
                        this.appDatabase.bookDAO().getBookWithTags(id).getTags().forEach(tag -> {
                            this.appDatabase.tagDAO().deleteTags(tag);
                        });
                case Database.MEDIA_GAME ->
                        this.appDatabase.gameDAO().getGameWithTags(id).getTags().forEach(tag -> {
                            this.appDatabase.tagDAO().deleteTags(tag);
                        });
                case Database.MEDIA_MOVIE ->
                        this.appDatabase.movieDAO().getMovieWithTags(id).getTags().forEach(tag -> {
                            this.appDatabase.tagDAO().deleteTags(tags);
                        });
                case Database.MEDIA_SONG ->
                        this.appDatabase.songDAO().getSongWithTags(id).getTags().forEach(tag -> {
                            this.appDatabase.tagDAO().deleteTags(tags);
                        });
                case Database.FILTER ->
                        this.appDatabase.filterDAO().getFilterWithTags(id).getTags().forEach(tag -> {
                            this.appDatabase.tagDAO().deleteTags(tags);
                        });
            }
        }

        for(Tag tag : tags) {
            if(tag.getId() == 0) {
                tag.setId(this.appDatabase.tagDAO().insertTags(tag)[0]);
            } else {
                this.appDatabase.tagDAO().updateTags(tag);
            }
        }
    }
}