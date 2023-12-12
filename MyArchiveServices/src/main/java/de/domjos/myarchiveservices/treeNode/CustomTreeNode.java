package de.domjos.myarchiveservices.treeNode;

import android.content.Context;
import android.graphics.drawable.Drawable;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeFile;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeNode;
import de.domjos.myarchiveservices.R;

public class CustomTreeNode extends com.unnamed.b.atv.model.TreeNode {
    private final Drawable drawable;
    private final Object treeItem;

    public CustomTreeNode(TreeNode node, Context context, int icon) {
        super((node == null ? "" : node.getTitle() == null ? "" : node.getTitle()));

        if(node != null) {
            if(node.isGallery()) {
                this.drawable = ConvertHelper.convertResourcesToDrawable(context, R.drawable.icon_image);
            } else {
                this.drawable = ConvertHelper.convertResourcesToDrawable(context, R.drawable.icon_file);
            }
        } else {
            this.drawable = ConvertHelper.convertResourcesToDrawable(context, icon);
        }
        this.treeItem = node;
    }

    public CustomTreeNode(TreeFile file, Context context) {
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
