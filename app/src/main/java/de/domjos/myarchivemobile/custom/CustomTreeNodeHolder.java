package de.domjos.myarchivemobile.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import de.domjos.myarchivemobile.R;

public class CustomTreeNodeHolder extends TreeNode.BaseNodeViewHolder<CustomTreeNode> {
    private int level = 0;
    private TextView tvValue;
    private int unselectedColor;
    private int selectedColor;
    private boolean system = false;

    public CustomTreeNodeHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, CustomTreeNode value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.main_fragment_file_tree_node, null, false);

        this.getLevel(node);
        view.setPadding(this.level * 20, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());

        this.tvValue = view.findViewById(R.id.txt);
        this.tvValue.setText(value.getValue().toString());

        this.selectedColor = this.context.getResources().getColor(R.color.selected_colorPrimary, context.getTheme());
        this.unselectedColor = this.context.getResources().getColor(R.color.textColorPrimary, context.getTheme());
        if(this.system) {
            this.unselectedColor = this.context.getResources().getColor(R.color.hintColor, context.getTheme());
        } else {
            if(value.getTreeItem() instanceof de.domjos.myarchivelibrary.model.media.fileTree.TreeNode) {
                if(((de.domjos.myarchivelibrary.model.media.fileTree.TreeNode)value.getTreeItem()).isSystem()) {
                    this.system = true;
                    this.unselectedColor = this.context.getResources().getColor(R.color.hintColor, context.getTheme());
                }
            }
        }
        this.tvValue.setTextColor(this.unselectedColor);

        ImageView iv = view.findViewById(R.id.iv);
        iv.setMinimumHeight(32);
        iv.setMinimumWidth(32);
        iv.setMaxHeight(32);
        iv.setMaxWidth(32);
        iv.setImageDrawable(value.getDrawable());

        return view;
    }

    private void getLevel(TreeNode node) {
        if(node.getParent() != null) {
            this.level++;
            this.getLevel(node.getParent());
        }
    }

    public CustomTreeNodeHolder system(boolean system) {
        this.system = system;
        return this;
    }

    public void select() {
        this.tvValue.setTextColor(this.selectedColor);
    }

    public void unSelect() {
        this.tvValue.setTextColor(this.unselectedColor);
    }

    public boolean isSystem() {
        return this.system;
    }
}
