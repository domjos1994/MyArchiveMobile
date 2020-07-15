package de.domjos.myarchivemobile.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.unnamed.b.atv.model.TreeNode;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivemobile.R;

public class CustomTreeNode extends TreeNode {
    private final Drawable drawable;
    private Object treeItem;

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
