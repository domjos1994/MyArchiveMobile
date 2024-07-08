package de.domjos.myarchivedatabase.repository.fileTree;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.domjos.myarchivedatabase.AppDatabase;
import de.domjos.myarchivedatabase.converter.DrawableConverter;
import de.domjos.myarchivedatabase.model.fileTree.FileTree;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFile;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileTagCrossRef;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileWithParent;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeFileWithTags;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)
public class FileTreeFileDAOTest {
    private FileTreeDAO fileTreeDAO;
    private FileTreeFileDAO fileTreeFileDAO;
    private TagDAO tagDAO;
    private AppDatabase appDatabase;
    private final String test = "Test";

    @Before
    public void createDB() {
        Context context = ApplicationProvider.getApplicationContext();
        DrawableConverter drawableConverter = new DrawableConverter(context);
        this.appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .addTypeConverter(drawableConverter).build();
        this.fileTreeDAO = this.appDatabase.fileTreeDAO();
        this.fileTreeFileDAO = this.appDatabase.fileTreeFileDAO();
        this.tagDAO = this.appDatabase.tagDAO();
    }

    @After
    public void closeDb() {
        this.appDatabase.close();
    }

    @Test
    public void testInsertAndDelete() {
        FileTree fileTree = new FileTree();
        fileTree.setTitle(this.test);
        fileTree.setId(this.fileTreeDAO.insertFileTreeElements(fileTree)[0]);

        FileTreeFile fileTreeFile = new FileTreeFile();
        fileTreeFile.setTitle(this.test);
        fileTreeFile.setId(this.fileTreeFileDAO.insertFileTreeFileElements(fileTreeFile)[0]);

        List<FileTreeFile> fileTreeFiles = this.fileTreeFileDAO.getAllFileTreeFileElements();
        Assert.assertEquals(1, fileTreeFiles.size());

        fileTreeFile.setTitle(this.test + "1");
        this.fileTreeFileDAO.updateFileTreeFileElements(fileTreeFile);
        FileTreeFile tmp = this.fileTreeFileDAO.getFileTreeFileElementById(fileTreeFile.getId());
        Assert.assertEquals(fileTreeFile.getTitle(), tmp.getTitle());

        List<FileTreeFileWithParent> fileTreeFileWithParents = this.fileTreeFileDAO.getChildTreeFileElementsWithParents();
        Assert.assertEquals(1, fileTreeFileWithParents.size());


        this.fileTreeFileDAO.deleteFileTreeFileElements(fileTreeFile);
        Assert.assertEquals(0, fileTreeFileWithParents.get(0).getFileTrees().size());
    }

    @Test
    public void testFileTreeWithTags() {
        FileTreeFile fileTreeFile = new FileTreeFile();
        fileTreeFile.setTitle(this.test);
        fileTreeFile.setId(this.fileTreeFileDAO.insertFileTreeFileElements(fileTreeFile)[0]);

        Tag tag = new Tag();
        tag.setTitle(this.test);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        FileTreeFileTagCrossRef fileTreeTagCrossRef = new FileTreeFileTagCrossRef();
        fileTreeTagCrossRef.setTagId(tag.getId());
        fileTreeTagCrossRef.setFileTreeFileId(fileTreeFile.getId());
        this.fileTreeFileDAO.insertFileTreeFileTagsElements(fileTreeTagCrossRef);

        List<FileTreeFileWithTags> fileTreeWithTags = this.fileTreeFileDAO.getChildTreeFileElementsWithTags();
        Assert.assertEquals(1, fileTreeWithTags.get(0).getTags().size());

        this.fileTreeFileDAO.deleteFileTreeFileTagsElements(fileTreeTagCrossRef);
        this.tagDAO.deleteTags(tag);
        this.fileTreeFileDAO.deleteFileTreeFileElements(fileTreeFile);
    }
}
