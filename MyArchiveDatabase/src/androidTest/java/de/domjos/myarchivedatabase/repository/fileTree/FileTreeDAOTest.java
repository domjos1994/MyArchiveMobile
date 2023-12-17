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
import de.domjos.myarchivedatabase.model.fileTree.FileTreeTagCrossRef;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeWithParent;
import de.domjos.myarchivedatabase.model.fileTree.FileTreeWithTags;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.repository.general.TagDAO;

@RunWith(AndroidJUnit4.class)
public class FileTreeDAOTest {
    private FileTreeDAO fileTreeDAO;
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

        FileTree rootTree = this.fileTreeDAO.getRootFileTreeElement();
        Assert.assertEquals(rootTree.getTitle(), fileTree.getTitle());

        fileTree.setTitle(this.test + "1");
        this.fileTreeDAO.updateFileTreeElements(fileTree);
        FileTree tmp = this.fileTreeDAO.getFileTreeElementById(fileTree.getId());
        Assert.assertEquals(fileTree.getTitle(), tmp.getTitle());


        this.fileTreeDAO.deleteFileTreeElements(fileTree);
        Assert.assertEquals(0, this.fileTreeDAO.getAllFileTreeElements().size());
    }

    @Test
    public void testFileTreeWithParent() {
        FileTree fileTree = new FileTree();
        fileTree.setTitle(this.test);
        fileTree.setId(this.fileTreeDAO.insertFileTreeElements(fileTree)[0]);

        FileTree childTree = new FileTree();
        childTree.setTitle(this.test);
        childTree.setParent(fileTree.getId());
        childTree.setId(this.fileTreeDAO.insertFileTreeElements(childTree)[0]);

        List<FileTreeWithParent> fileTreeWithParent = this.fileTreeDAO.getChildTreeElementsWithParents();
        Assert.assertEquals(1, fileTreeWithParent.get(0).getFileTrees().size());
        Assert.assertEquals(childTree.getId(), fileTreeWithParent.get(0).getFileTrees().get(0).getId());

        this.fileTreeDAO.deleteFileTreeElements(childTree);
        this.fileTreeDAO.deleteFileTreeElements(fileTree);
    }

    @Test
    public void testFileTreeWithTags() {
        FileTree fileTree = new FileTree();
        fileTree.setTitle(this.test);
        fileTree.setId(this.fileTreeDAO.insertFileTreeElements(fileTree)[0]);

        Tag tag = new Tag();
        tag.setTitle(this.test);
        tag.setId(this.tagDAO.insertTags(tag)[0]);

        FileTreeTagCrossRef fileTreeTagCrossRef = new FileTreeTagCrossRef();
        fileTreeTagCrossRef.setTagId(tag.getId());
        fileTreeTagCrossRef.setFileTreeId(fileTree.getId());
        this.fileTreeDAO.insertFileTreeTagsElements(fileTreeTagCrossRef);

        List<FileTreeWithTags> fileTreeWithTags = this.fileTreeDAO.getChildTreeElementsWithTags();
        Assert.assertEquals(1, fileTreeWithTags.get(0).getTags().size());

        this.fileTreeDAO.deleteFileTreeTagsElements(fileTreeTagCrossRef);
        this.tagDAO.deleteTags(tag);
        this.fileTreeDAO.deleteFileTreeElements(fileTree);
    }
}
