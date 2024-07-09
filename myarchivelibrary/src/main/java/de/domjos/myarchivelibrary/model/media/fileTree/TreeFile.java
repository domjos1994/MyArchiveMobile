/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivelibrary.model.media.fileTree;

import java.util.LinkedList;
import java.util.List;

import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;

public class TreeFile extends BaseDescriptionObject implements DatabaseObject {
    private BaseDescriptionObject category;
    private List<BaseDescriptionObject> tags;
    private TreeNode parent;
    private long internalId;
    private String internalTable;
    private String internalColumn;
    private String pathToFile;
    private byte[] embeddedContent;

    public TreeFile() {
        super();

        this.category = null;
        this.tags = new LinkedList<>();
        this.parent = null;
        this.internalId = 0;
        this.internalTable = "";
        this.internalColumn = "";
        this.pathToFile = "";
        this.embeddedContent = null;
    }

    public BaseDescriptionObject getCategory() {
        return this.category;
    }

    public void setCategory(BaseDescriptionObject category) {
        this.category = category;
    }

    public List<BaseDescriptionObject> getTags() {
        return this.tags;
    }

    public void setTags(List<BaseDescriptionObject> tags) {
        this.tags = tags;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public long getInternalId() {
        return this.internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getInternalTable() {
        return this.internalTable;
    }

    public void setInternalTable(String internalTable) {
        this.internalTable = internalTable;
    }

    public String getInternalColumn() {
        return this.internalColumn;
    }

    public void setInternalColumn(String internalColumn) {
        this.internalColumn = internalColumn;
    }

    public String getPathToFile() {
        return this.pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public byte[] getEmbeddedContent() {
        return this.embeddedContent;
    }

    public void setEmbeddedContent(byte[] embeddedContent) {
        this.embeddedContent = embeddedContent;
    }

    @Override
    public String getTable() {
        return "file_tree_file";
    }
}
