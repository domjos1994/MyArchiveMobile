package de.domjos.myarchivemobile.tasks;

import android.app.Activity;
import android.widget.ProgressBar;

import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.custom.ProgressBarTask;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeFile;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeNode;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.custom.CustomTreeNode;
import de.domjos.myarchivemobile.custom.CustomTreeNodeHolder;

public class TreeViewTask extends ProgressBarTask<Void, com.unnamed.b.atv.model.TreeNode> {
    private final boolean initDatabase, checkDatabase, loadFiles, system;
    private final TreeNode parent;
    private final com.unnamed.b.atv.model.TreeNode node;
    private final String search;

    public TreeViewTask(Activity activity, ProgressBar progressBar, boolean initDatabase, boolean checkDatabase, boolean loadFiles, boolean system, com.unnamed.b.atv.model.TreeNode root, TreeNode parent, String search) {
        super(activity, R.string.file_tree_load, R.string.file_tree_load, MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, progressBar);

        this.initDatabase = initDatabase;
        this.checkDatabase = checkDatabase;
        this.loadFiles = loadFiles;
        this.system = system;
        this.parent = parent;
        this.node = root;
        this.search = search;
        super.max = 100;
    }

    @Override
    protected com.unnamed.b.atv.model.TreeNode doInBackground(Void... voids) {
        if(this.initDatabase) {
            this.initDatabase();
        }

        String where = "";

        if(!this.loadFiles) {
            this.publishProgress(0);
            TreeNode root = MainActivity.GLOBALS.getDatabase().getRoot(where);
            CustomTreeNode customTreeNode = new CustomTreeNode(root, this.getContext());
            com.unnamed.b.atv.model.TreeNode treeNode = new com.unnamed.b.atv.model.TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()));
            this.addChildren(root, treeNode);

            this.node.addChild(treeNode);
        } else {
            if(this.search != null) {
                if(!this.search.trim().isEmpty()) {
                    where = "title like '%" + this.search.trim() + "%' or description like '%" + this.search.trim() + "%'";
                }
            }
            this.publishProgress(0);
            List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("parent=" + this.parent.getId() + (where.trim().isEmpty() ? "" : " and " + where.trim()));
            for(TreeFile treeFile : treeFiles) {
                treeFile.setParent(this.parent);
                CustomTreeNode customTreeNode = new CustomTreeNode(treeFile, this.getContext());
                com.unnamed.b.atv.model.TreeNode node = new com.unnamed.b.atv.model.TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()).system(this.system));

                for(com.unnamed.b.atv.model.TreeNode tmp : this.node.getChildren()) {
                    if(tmp.getValue() instanceof CustomTreeNode tmpNode && node.getValue() instanceof CustomTreeNode nodeNode) {
                        if(tmpNode.getTreeItem().toString().equals(nodeNode.getTreeItem().toString())) {
                            this.node.deleteChild(tmp);
                            break;
                        }
                    }
                }
                this.node.addChild(node);
            }
        }
        this.publishProgress(0);

        return this.node;
    }

    private void initDatabase() {
        try {
            int status = 0;
            this.publishProgress(status);

            TreeNode treeNode = MainActivity.GLOBALS.getDatabase().getRoot();
            BaseDescriptionObject systemCategory = new BaseDescriptionObject();
            systemCategory.setTitle(this.getContext().getString(R.string.file_tree_system));

            if(treeNode == null) {
                TreeNode root = new TreeNode();
                root.setTitle(this.getContext().getString(R.string.file_tree));
                root.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(root));

                TreeNode app = new TreeNode();
                app.setTitle(this.getContext().getString(R.string.app_name));
                app.setSystem(true);
                app.setParent(root);
                app.setCategory(systemCategory);
                app.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(app));

                TreeNode persons = new TreeNode();
                persons.setTitle(this.getContext().getString(R.string.media_persons));
                persons.setSystem(true);
                persons.setGallery(true);
                persons.setParent(app);
                persons.setCategory(systemCategory);
                persons.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(persons));

                for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("")) {
                    TreeFile treeFile = new TreeFile();
                    treeFile.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()));
                    treeFile.setDescription(person.getDescription());
                    treeFile.setParent(persons);
                    treeFile.setInternalId(person.getId());
                    treeFile.setInternalTable("persons");
                    treeFile.setInternalColumn("image");
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                    this.publishProgress(++status);
                }

                status = 0;
                TreeNode companies = new TreeNode();
                companies.setTitle(this.getContext().getString(R.string.media_companies));
                companies.setSystem(true);
                companies.setGallery(true);
                companies.setParent(app);
                companies.setCategory(systemCategory);
                companies.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(companies));

                for(Company company : MainActivity.GLOBALS.getDatabase().getCompanies("")) {
                    TreeFile treeFile = new TreeFile();
                    treeFile.setTitle(company.getTitle());
                    treeFile.setDescription(company.getDescription());
                    treeFile.setParent(companies);
                    treeFile.setInternalId(company.getId());
                    treeFile.setInternalTable("companies");
                    treeFile.setInternalColumn("cover");
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                    this.publishProgress(++status);
                }

                TreeNode media = new TreeNode();
                media.setTitle(this.getContext().getString(R.string.main_navigation_media));
                media.setSystem(true);
                media.setGallery(true);
                media.setParent(app);
                media.setCategory(systemCategory);
                media.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(media));

                status = 0;
                TreeNode books = new TreeNode();
                books.setTitle(this.getContext().getString(R.string.main_navigation_media_books));
                books.setSystem(true);
                books.setGallery(true);
                books.setParent(media);
                books.setCategory(systemCategory);
                books.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(books));

                for(Book book : MainActivity.GLOBALS.getDatabase().getBooks("", -1, 0)) {
                    TreeFile treeFile = new TreeFile();
                    treeFile.setTitle(book.getTitle());
                    treeFile.setDescription(book.getDescription());
                    treeFile.setParent(books);
                    treeFile.setInternalId(book.getId());
                    treeFile.setInternalTable("books");
                    treeFile.setInternalColumn("cover");
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                    this.publishProgress(++status);
                }

                status = 0;
                TreeNode movies = new TreeNode();
                movies.setTitle(this.getContext().getString(R.string.main_navigation_media_movies));
                movies.setSystem(true);
                movies.setGallery(true);
                movies.setParent(media);
                movies.setCategory(systemCategory);
                movies.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(movies));

                for(Movie movie : MainActivity.GLOBALS.getDatabase().getMovies("", -1, 0)) {
                    TreeFile treeFile = new TreeFile();
                    treeFile.setTitle(movie.getTitle());
                    treeFile.setDescription(movie.getDescription());
                    treeFile.setParent(movies);
                    treeFile.setInternalId(movie.getId());
                    treeFile.setInternalTable("movies");
                    treeFile.setInternalColumn("cover");
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                    this.publishProgress(++status);
                }

                status = 0;
                TreeNode albums = new TreeNode();
                albums.setTitle(this.getContext().getString(R.string.main_navigation_media_music));
                albums.setSystem(true);
                albums.setGallery(true);
                albums.setParent(media);
                albums.setCategory(systemCategory);
                albums.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(albums));

                for(Album album : MainActivity.GLOBALS.getDatabase().getAlbums("", -1, 0)) {
                    TreeFile treeFile = new TreeFile();
                    treeFile.setTitle(album.getTitle());
                    treeFile.setDescription(album.getDescription());
                    treeFile.setParent(albums);
                    treeFile.setInternalId(album.getId());
                    treeFile.setInternalTable("albums");
                    treeFile.setInternalColumn("cover");
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                    this.publishProgress(++status);
                }

                status = 0;
                TreeNode games = new TreeNode();
                games.setTitle(this.getContext().getString(R.string.main_navigation_media_games));
                games.setSystem(true);
                games.setGallery(true);
                games.setParent(media);
                games.setCategory(systemCategory);
                games.setId(MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(games));

                for(Game game : MainActivity.GLOBALS.getDatabase().getGames("", -1, 0)) {
                    TreeFile treeFile = new TreeFile();
                    treeFile.setTitle(game.getTitle());
                    treeFile.setDescription(game.getDescription());
                    treeFile.setParent(games);
                    treeFile.setInternalId(game.getId());
                    treeFile.setInternalTable("games");
                    treeFile.setInternalColumn("cover");
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                    this.publishProgress(++status);
                }
            } else {
                if(this.checkDatabase) {
                    for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("")) {
                        List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("internalId=" + person.getId() + " AND internalTable='persons' AND internalColumn='image'");
                        if(treeFiles.isEmpty()) {
                            TreeFile treeFile = new TreeFile();
                            treeFile.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()));
                            treeFile.setDescription(person.getDescription());
                            treeFile.setParent(MainActivity.GLOBALS.getDatabase().getNodeByName(this.getContext().getString(R.string.media_persons)));
                            treeFile.setInternalId(person.getId());
                            treeFile.setInternalTable("persons");
                            treeFile.setInternalColumn("image");
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
                            this.publishProgress(++status);
                        }
                    }

                    status = 0;
                    for(Company company : MainActivity.GLOBALS.getDatabase().getCompanies("")) {
                        List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("internalId=" + company.getId() + " AND internalTable='companies' AND internalColumn='cover'");
                        if(treeFiles.isEmpty()) {
                            this.addFileIfNotExists(company, "companies", this.getContext().getString(R.string.media_companies));
                        }
                        this.publishProgress(++status);
                    }
                    status = 0;
                    for(Book book : MainActivity.GLOBALS.getDatabase().getBooks("", -1, 0)) {
                        List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("internalId=" + book.getId() + " AND internalTable='books' AND internalColumn='cover'");
                        if(treeFiles.isEmpty()) {
                            this.addFileIfNotExists(book, "books", this.getContext().getString(R.string.main_navigation_media_books));
                        }
                        this.publishProgress(++status);
                    }
                    status = 0;
                    for(Movie movie : MainActivity.GLOBALS.getDatabase().getMovies("", -1, 0)) {
                        List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("internalId=" + movie.getId() + " AND internalTable='movies' AND internalColumn='cover'");
                        if(treeFiles.isEmpty()) {
                            this.addFileIfNotExists(movie, "movies", this.getContext().getString(R.string.main_navigation_media_movies));
                        }
                        this.publishProgress(++status);
                    }
                    status = 0;
                    for(Album album : MainActivity.GLOBALS.getDatabase().getAlbums("", -1, 0)) {
                        List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("internalId=" + album.getId() + " AND internalTable='albums' AND internalColumn='cover'");
                        if(treeFiles.isEmpty()) {
                            this.addFileIfNotExists(album, "albums", this.getContext().getString(R.string.main_navigation_media_music));
                        }
                        this.publishProgress(++status);
                    }
                    status = 0;
                    for(Game game : MainActivity.GLOBALS.getDatabase().getGames("", -1, 0)) {
                        List<TreeFile> treeFiles = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("internalId=" + game.getId() + " AND internalTable='games' AND internalColumn='cover'");
                        if(treeFiles.isEmpty()) {
                            this.addFileIfNotExists(game, "games", this.getContext().getString(R.string.main_navigation_media_games));
                        }
                        this.publishProgress(++status);
                    }
                }
            }
            this.publishProgress(0);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
        }
    }

    private void addFileIfNotExists(BaseDescriptionObject obj, String table, String name) {
        TreeFile treeFile = new TreeFile();
        treeFile.setTitle(obj.getTitle());
        treeFile.setDescription(obj.getDescription());
        treeFile.setParent(MainActivity.GLOBALS.getDatabase().getNodeByName(name));
        treeFile.setInternalId(obj.getId());
        treeFile.setInternalTable(table);
        treeFile.setInternalColumn("cover");
        if(obj instanceof BaseMediaObject baseMediaObject) {
            treeFile.setCategory(baseMediaObject.getCategory());
            treeFile.setTags(baseMediaObject.getTags());
        }
        MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(treeFile);
    }

    private void addChildren(TreeNode dbRoot, com.unnamed.b.atv.model.TreeNode viewRoot) {
        for(TreeNode dbChild : dbRoot.getChildren()) {
            if(dbChild.getTitle().equals(this.getContext().getString(R.string.app_name))) {
                CustomTreeNode customTreeNode = new CustomTreeNode(dbChild, this.getContext(), R.mipmap.ic_launcher_round);
                com.unnamed.b.atv.model.TreeNode viewChild = new com.unnamed.b.atv.model.TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()));
                this.addChildren(dbChild, viewChild);
                viewRoot.addChild(viewChild);
            } else {
                CustomTreeNode customTreeNode = new CustomTreeNode(dbChild, this.getContext());
                com.unnamed.b.atv.model.TreeNode viewChild = new com.unnamed.b.atv.model.TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()));
                this.addChildren(dbChild, viewChild);
                viewRoot.addChild(viewChild);
            }
        }
    }
}
