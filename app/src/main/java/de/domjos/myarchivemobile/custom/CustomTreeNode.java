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

package de.domjos.myarchivemobile.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.unnamed.b.atv.model.TreeNode;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivemobile.R;

public class CustomTreeNode extends TreeNode {
    private final Drawable drawable;
    private final Object treeItem;

    public CustomTreeNode(de.domjos.myarchivelibrary.model.media.fileTree.TreeNode node, Context context) {
        super((node == null ? "" : node.getTitle() == null ? "" : node.getTitle()));

        if(node != null) {
            if(node.isGallery()) {
                this.drawable = ConvertHelper.convertResourcesToDrawable(context, R.drawable.icon_image);
            } else {
                this.drawable = ConvertHelper.convertResourcesToDrawable(context, R.drawable.icon_file);
            }
        } else {
            this.drawable = ConvertHelper.convertResourcesToDrawable(context, R.mipmap.ic_launcher_round);
        }
        this.treeItem = node;
    }

    public CustomTreeNode(de.domjos.myarchivelibrary.model.media.fileTree.TreeNode node, Context context, int resId) {
        super(node.getTitle());

        this.drawable = ConvertHelper.convertResourcesToDrawable(context, resId);
        this.treeItem = node;
    }

    public CustomTreeNode(de.domjos.myarchivelibrary.model.media.fileTree.TreeFile file, Context context) {
        super(file.getTitle());

        this.drawable = ConvertHelper.convertResourcesToDrawable(context, R.drawable.icon_file);
        this.treeItem = file;
    }

    public Drawable getDrawable() {
        return this.drawable;
    }


    public Object getTreeItem() {
        return this.treeItem;
    }
}
