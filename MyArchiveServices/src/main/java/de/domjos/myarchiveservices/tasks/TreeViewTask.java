package de.domjos.myarchiveservices.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.model.tasks.TaskStatus;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivedatabase.model.fileTree.FileTree;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedatabase.model.general.person.Person;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedbvalidator.Database;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomStatusTask;
import de.domjos.myarchiveservices.treeNode.CustomTreeNode;
import de.domjos.myarchiveservices.treeNode.CustomTreeNodeHolder;

public class TreeViewTask {//extends CustomStatusTask<Void, TreeNode> {
//    private final boolean initDatabase;
//    private final boolean checkDatabase;
//    private final boolean loadFiles;
//    private final boolean system;
//    private final de.domjos.myarchivelibrary.model.media.fileTree.TreeNode parent;
//    private final TreeNode node;
//    private final String search;
//    private final Database database;
//    private final int icon_notification;
//
//    public TreeViewTask(
//            Activity activity, ProgressBar progressBar, TextView status, boolean initDatabase,
//            boolean checkDatabase, boolean loadFiles, boolean system, TreeNode root,
//            de.domjos.myarchivelibrary.model.media.fileTree.TreeNode parent,
//            String search, boolean notification, int icon_notification, Database database) {
//        super(
//                activity, R.string.file_tree_load, R.string.file_tree_load, notification,
//                icon_notification, progressBar, status
//        );
//
//        this.initDatabase = initDatabase;
//        this.icon_notification = icon_notification;
//        this.database = database;
//        this.checkDatabase = checkDatabase;
//        this.loadFiles = loadFiles;
//        this.system = system;
//        this.parent = parent;
//        this.node = root;
//        this.search = search;
//        super.max = 100;
//    }
//
//    @Override
//    protected TreeNode doInBackground(Void voids) {
//        if(this.initDatabase) {
//            this.initDatabase();
//        }
//
//        String where = "";
//
//        if(!this.loadFiles) {
//            this.publishProgress(new TaskStatus(0, this.getContext().getString(R.string.file_tree_load_tree)));
//            de.domjos.myarchivelibrary.model.media.fileTree.TreeNode root = this.database.getRoot(where);
//            CustomTreeNode customTreeNode = new CustomTreeNode(root, this.getContext(), icon_notification);
//            TreeNode treeNode = new TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()));
//            this.addChildren(root, treeNode);
//
//            this.node.addChild(treeNode);
//        } else {
//            if(this.search != null) {
//                if(!this.search.trim().isEmpty()) {
//                    where = "title like '%" + this.search.trim() + "%' or description like '%" + this.search.trim() + "%'";
//                }
//            }
//            this.publishProgress(new TaskStatus(0, this.getContext().getString(R.string.file_tree_load_files)));
//            List<TreeFile> treeFiles = this.database.getTreeNodeFiles("parent=" + this.parent.getId() + (where.trim().isEmpty() ? "" : " and " + where.trim()));
//            for(TreeFile treeFile : treeFiles) {
//                treeFile.setParent(this.parent);
//                CustomTreeNode customTreeNode = new CustomTreeNode(treeFile, this.getContext());
//                TreeNode node = new TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()).system(this.system));
//
//                for(TreeNode tmp : this.node.getChildren()) {
//                    if(tmp.getValue() instanceof CustomTreeNode tmpNode && node.getValue() instanceof CustomTreeNode nodeNode) {
//                        if(tmpNode.getTreeItem().toString().equals(nodeNode.getTreeItem().toString())) {
//                            this.node.deleteChild(tmp);
//                            break;
//                        }
//                    }
//                }
//                this.node.addChild(node);
//            }
//        }
//        this.publishProgress(new TaskStatus(0, ""));
//
//        return this.node;
//    }
//
//    private void initDatabase() {
//        try {
//            int status = 0;
//            this.publishProgress(new TaskStatus(status, this.getContext().getString(R.string.file_tree_load_db)));
//
//            de.domjos.myarchivelibrary.model.media.fileTree.TreeNode treeNode = this.database.getRoot();
//            BaseDescriptionObject systemCategory = new BaseDescriptionObject();
//            systemCategory.setTitle(this.getContext().getString(R.string.file_tree_system));
//
//            if(treeNode == null) {
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode root =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                root.setTitle(this.getContext().getString(R.string.file_tree));
//                root.setId(this.database.insertOrUpdateTreeNode(root));
//
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode app =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                app.setTitle(this.getContext().getString(R.string.app_name));
//                app.setSystem(true);
//                app.setParent(root);
//                app.setCategory(systemCategory);
//                app.setId(this.database.insertOrUpdateTreeNode(app));
//
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode persons =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                persons.setTitle(this.getContext().getString(R.string.media_persons));
//                persons.setSystem(true);
//                persons.setGallery(true);
//                persons.setParent(app);
//                persons.setCategory(systemCategory);
//                persons.setId(this.database.insertOrUpdateTreeNode(persons));
//
//                for(Person person : this.database.getPersons("")) {
//                    FileTree treeFile = new FileTree();
//                    treeFile.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()));
//                    treeFile.setDescription(person.getDescription());
//                    treeFile.setParent(persons);
//                    treeFile.setInternalId(person.getId());
//                    treeFile.setInternalTable("persons");
//                    treeFile.setInternalColumn("image");
//                    this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                    this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Persons")));
//                }
//
//                status = 0;
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode companies =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                companies.setTitle(this.getContext().getString(R.string.media_companies));
//                companies.setSystem(true);
//                companies.setGallery(true);
//                companies.setParent(app);
//                companies.setCategory(systemCategory);
//                companies.setId(this.database.insertOrUpdateTreeNode(companies));
//
//                for(Company company : this.database.getCompanies("")) {
//                    FileTree treeFile = new FileTree();
//                    treeFile.setTitle(company.getTitle());
//                    treeFile.setDescription(company.getDescription());
//                    treeFile.setParent(companies);
//                    treeFile.setInternalId(company.getId());
//                    treeFile.setInternalTable("companies");
//                    treeFile.setInternalColumn("cover");
//                    this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                    this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Companies")));
//                }
//
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode media =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                media.setTitle(this.getContext().getString(R.string.main_navigation_media));
//                media.setSystem(true);
//                media.setGallery(true);
//                media.setParent(app);
//                media.setCategory(systemCategory);
//                media.setId(this.database.insertOrUpdateTreeNode(media));
//
//                status = 0;
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode books =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                books.setTitle(this.getContext().getString(R.string.main_navigation_media_books));
//                books.setSystem(true);
//                books.setGallery(true);
//                books.setParent(media);
//                books.setCategory(systemCategory);
//                books.setId(this.database.insertOrUpdateTreeNode(books));
//
//                for(Book book : this.database.getBooks("", -1, 0)) {
//                    FileTree treeFile = new FileTree();
//                    treeFile.setTitle(book.getTitle());
//                    treeFile.setDescription(book.getDescription());
//                    treeFile.setParent(books);
//                    treeFile.setInternalId(book.getId());
//                    treeFile.setInternalTable("books");
//                    treeFile.setInternalColumn("cover");
//                    this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                    this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Books")));
//                }
//
//                status = 0;
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode movies =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                movies.setTitle(this.getContext().getString(R.string.main_navigation_media_movies));
//                movies.setSystem(true);
//                movies.setGallery(true);
//                movies.setParent(media);
//                movies.setCategory(systemCategory);
//                movies.setId(this.database.insertOrUpdateTreeNode(movies));
//
//                for(Movie movie : this.database.getMovies("", -1, 0)) {
//                    FileTree treeFile = new FileTree();
//                    treeFile.setTitle(movie.getTitle());
//                    treeFile.setDescription(movie.getDescription());
//                    treeFile.setParent(movies);
//                    treeFile.setInternalId(movie.getId());
//                    treeFile.setInternalTable("movies");
//                    treeFile.setInternalColumn("cover");
//                    this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                    this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Movies")));
//                }
//
//                status = 0;
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode albums =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                albums.setTitle(this.getContext().getString(R.string.main_navigation_media_music));
//                albums.setSystem(true);
//                albums.setGallery(true);
//                albums.setParent(media);
//                albums.setCategory(systemCategory);
//                albums.setId(this.database.insertOrUpdateTreeNode(albums));
//
//                for(Album album : this.database.getAlbums("", -1, 0)) {
//                    FileTree treeFile = new FileTree();
//                    treeFile.setTitle(album.getTitle());
//                    treeFile.setDescription(album.getDescription());
//                    treeFile.setParent(albums);
//                    treeFile.setInternalId(album.getId());
//                    treeFile.setInternalTable("albums");
//                    treeFile.setInternalColumn("cover");
//                    this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                    this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Music")));
//                }
//
//                status = 0;
//                de.domjos.myarchivelibrary.model.media.fileTree.TreeNode games =
//                        new de.domjos.myarchivelibrary.model.media.fileTree.TreeNode();
//                games.setTitle(this.getContext().getString(R.string.main_navigation_media_games));
//                games.setSystem(true);
//                games.setGallery(true);
//                games.setParent(media);
//                games.setCategory(systemCategory);
//                games.setId(this.database.insertOrUpdateTreeNode(games));
//
//                for(Game game : this.database.getGames("", -1, 0)) {
//                    FileTree treeFile = new FileTree();
//                    treeFile.setTitle(game.getTitle());
//                    treeFile.setDescription(game.getDescription());
//                    treeFile.setParent(games);
//                    treeFile.setInternalId(game.getId());
//                    treeFile.setInternalTable("games");
//                    treeFile.setInternalColumn("cover");
//                    this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                    this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Games")));
//                }
//            } else {
//                if(this.checkDatabase) {
//                    for(Person person : this.database.getPersons("")) {
//                        List<FileTree> treeFiles = this.database.getTreeNodeFiles("internalId=" + person.getId() + " AND internalTable='persons' AND internalColumn='image'");
//                        if(treeFiles.isEmpty()) {
//                            FileTree treeFile = new FileTree();
//                            treeFile.setTitle(String.format("%s %s", person.getFirstName(), person.getLastName()));
//                            treeFile.setDescription(person.getDescription());
//                            treeFile.setParent(this.database.getNodeByName(this.getContext().getString(R.string.media_persons)));
//                            treeFile.setInternalId(person.getId());
//                            treeFile.setInternalTable("persons");
//                            treeFile.setInternalColumn("image");
//                            this.database.insertOrUpdateTreeNodeFiles(treeFile);
//                            this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Persons")));
//                        }
//                    }
//
//                    status = 0;
//                    for(Company company : this.database.getCompanies("")) {
//                        List<FileTree> treeFiles = this.database.getTreeNodeFiles("internalId=" + company.getId() + " AND internalTable='companies' AND internalColumn='cover'");
//                        if(treeFiles.isEmpty()) {
//                            this.addFileIfNotExists(company, "companies", this.getContext().getString(R.string.media_companies));
//                        }
//                        this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Companies")));
//                    }
//                    status = 0;
//                    for(Book book : this.database.getBooks("", -1, 0)) {
//                        List<FileTree> treeFiles = this.database.getTreeNodeFiles("internalId=" + book.getId() + " AND internalTable='books' AND internalColumn='cover'");
//                        if(treeFiles.isEmpty()) {
//                            this.addFileIfNotExists(book, "books", this.getContext().getString(R.string.main_navigation_media_books));
//                        }
//                        this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Books")));
//                    }
//                    status = 0;
//                    for(Movie movie : this.database.getMovies("", -1, 0)) {
//                        List<FileTree> treeFiles = this.database.getTreeNodeFiles("internalId=" + movie.getId() + " AND internalTable='movies' AND internalColumn='cover'");
//                        if(treeFiles.isEmpty()) {
//                            this.addFileIfNotExists(movie, "movies", this.getContext().getString(R.string.main_navigation_media_movies));
//                        }
//                        this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Movies")));
//                    }
//                    status = 0;
//                    for(Album album : this.database.getAlbums("", -1, 0)) {
//                        List<FileTree> treeFiles = this.database.getTreeNodeFiles("internalId=" + album.getId() + " AND internalTable='albums' AND internalColumn='cover'");
//                        if(treeFiles.isEmpty()) {
//                            this.addFileIfNotExists(album, "albums", this.getContext().getString(R.string.main_navigation_media_music));
//                        }
//                        this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Music")));
//                    }
//                    status = 0;
//                    for(Game game : this.database.getGames("", -1, 0)) {
//                        List<FileTree> treeFiles = this.database.getTreeNodeFiles("internalId=" + game.getId() + " AND internalTable='games' AND internalColumn='cover'");
//                        if(treeFiles.isEmpty()) {
//                            this.addFileIfNotExists(game, "games", this.getContext().getString(R.string.main_navigation_media_games));
//                        }
//                        this.publishProgress(new TaskStatus(++status, String.format(this.getContext().getString(R.string.file_tree_load_db), "Games")));
//                    }
//                }
//            }
//            this.publishProgress(new TaskStatus(0, ""));
//        } catch (Exception ex) {
//            MessageHelper.printException(ex, this.icon_notification, this.getContext());
//        }
//    }
//
//    private void addFileIfNotExists(BaseDescriptionObject obj, String table, String name) {
//        FileTree treeFile = new FileTree();
//        treeFile.setTitle(obj.getTitle());
//        treeFile.setDescription(obj.getDescription());
//        treeFile.setParent(this.database.getNodeByName(name));
//        treeFile.setInternalId(obj.getId());
//        treeFile.setInternalTable(table);
//        treeFile.setInternalColumn("cover");
//        if(obj instanceof BaseMediaObject baseMediaObject) {
//            treeFile.setCategory(baseMediaObject.getCategory());
//            treeFile.setTags(baseMediaObject.getTags());
//        }
//        this.database.insertOrUpdateTreeNodeFiles(treeFile);
//    }
//
//    private void addChildren(FileTree dbRoot, TreeNode viewRoot) {
//        for(FileTree dbChild : dbRoot.getChildren()) {
//            CustomTreeNode customTreeNode = new CustomTreeNode(dbChild, this.getContext(), this.icon_notification);
//            TreeNode viewChild = new TreeNode(customTreeNode).setViewHolder(new CustomTreeNodeHolder(this.getContext()));
//            this.addChildren(dbChild, viewChild);
//            viewRoot.addChild(viewChild);
//        }
//    }
}
