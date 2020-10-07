package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unnamed.b.atv.view.AndroidTreeView;

import de.domjos.customwidgets.model.tasks.AbstractTask;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeFile;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeNode;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.custom.CustomTreeNode;
import de.domjos.myarchivemobile.custom.CustomTreeNodeHolder;
import de.domjos.myarchivemobile.dialogs.TreeViewDialog;
import de.domjos.myarchivemobile.tasks.TreeViewTask;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainFileTreeFragment extends ParentFragment {
    private RelativeLayout treeContainer;
    private AndroidTreeView androidTreeView;

    private ProgressBar pbProgress;
    private TextView lblMessage;

    private BottomNavigationView navigationView;

    private com.unnamed.b.atv.model.TreeNode.TreeNodeLongClickListener treeNodeLongClickListener;
    private com.unnamed.b.atv.model.TreeNode.TreeNodeClickListener treeNodeClickListener;

    private com.unnamed.b.atv.model.TreeNode lastNode;
    private TreeNode node;
    private String search;

    private String path;
    private boolean data = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_file_tree, container, false);
        this.initControls(root);
        this.initTreeView(true, false, "");

        this.navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAddFile:
                    TreeViewDialog fileDialog = TreeViewDialog.newInstance(TreeViewDialog.FILE, this.node.getId());
                    fileDialog.addPreExecute(this::reset);
                    fileDialog.show(this.requireActivity());
                    break;
                case R.id.cmdAddNode:
                    TreeViewDialog nodeDialog = TreeViewDialog.newInstance(TreeViewDialog.NODE, this.node.getId());
                    nodeDialog.addPreExecute(this::reset);
                    nodeDialog.show(this.requireActivity());
                    break;
                case R.id.cmdReload:
                    this.initTreeView(true, true, "");
                    break;
            }

            return true;
        });

        return root;
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    @Override
    public void reload(String search, boolean reload) {
        this.search = search;

        if(reload) {
            this.reset(search);
        }
    }

    @Override
    public void select() {

    }

    private void initControls(View view) {
        this.treeContainer = view.findViewById(R.id.treeContainer);
        this.navigationView = view.findViewById(R.id.navigationView);
        this.pbProgress = view.findViewById(R.id.pbProgress);
        this.lblMessage = view.findViewById(R.id.lblMessage);

        this.treeNodeClickListener = (node, value) -> {
            if(this.lastNode != null) {
                ((CustomTreeNodeHolder) this.lastNode.getViewHolder()).unSelect();
            }
            this.lastNode = node;
            if(((CustomTreeNode)value).getTreeItem() instanceof TreeNode) {
                this.node = (TreeNode) ((CustomTreeNode)value).getTreeItem();
            }

            ((CustomTreeNodeHolder) this.lastNode.getViewHolder()).select();
            boolean system = ((CustomTreeNodeHolder) this.lastNode.getViewHolder()).isSystem();
            this.navigationView.setVisibility(system || this.data ? View.GONE : View.VISIBLE);
            if(((CustomTreeNode) value).getTreeItem() instanceof TreeNode) {
                try {
                    TreeViewTask treeViewTask = new TreeViewTask(this.requireActivity(), this.pbProgress, this.lblMessage, false, false, true, system, node, this.node, this.search);
                    treeViewTask.execute().get();
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.requireActivity());
                }
            }
        };

        this.treeNodeLongClickListener = (node, value) -> {
            boolean system = false;
            CustomTreeNode customTreeNode = ((CustomTreeNode) value);
            if(customTreeNode.getTreeItem() instanceof TreeFile) {
                TreeFile treeFile = (TreeFile) customTreeNode.getTreeItem();
                if(treeFile.getParent() != null) {
                    system = treeFile.getParent().isSystem();
                }
            } else {
                TreeNode treeNode = (TreeNode) customTreeNode.getTreeItem();
                system = treeNode.isSystem();
            }

            if(this.data && !system) {
                TreeViewDialog treeViewDialog = TreeViewDialog.newInstance(TreeViewDialog.FILE, this.path, ((BaseDescriptionObject) ((CustomTreeNode) value).getTreeItem()).getId());
                treeViewDialog.addPreExecute(()->System.exit(0));
                treeViewDialog.show(this.requireActivity());
            } else {
                TreeViewDialog treeViewDialog = TreeViewDialog.newInstance((BaseDescriptionObject) ((CustomTreeNode) value).getTreeItem(), system);
                treeViewDialog.addPreExecute(this::reset);
                treeViewDialog.show(this.requireActivity());
            }
            return true;
        };

        Bundle bundle = this.getArguments();
        if(bundle!=null) {
            if(bundle.containsKey("uri")) {
                this.path = bundle.getString("uri");
                this.data = true;
                this.navigationView.setVisibility(View.GONE);
            }
        }
    }

    private void initTreeView(boolean firstStart, boolean checkDatabase, String search) {
        com.unnamed.b.atv.model.TreeNode node = com.unnamed.b.atv.model.TreeNode.root();

        TreeViewTask treeViewTask = new TreeViewTask(this.requireActivity(), this.pbProgress, this.lblMessage, firstStart, checkDatabase, false, false, node, null, search);
        treeViewTask.after((AbstractTask.PostExecuteListener<com.unnamed.b.atv.model.TreeNode>) o -> {
            this.androidTreeView = new AndroidTreeView(this.requireActivity(), o);
            this.androidTreeView.setDefaultNodeClickListener(this.treeNodeClickListener);
            this.androidTreeView.setDefaultNodeLongClickListener(this.treeNodeLongClickListener);
            this.androidTreeView.setDefaultAnimation(true);
            this.androidTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleDivided);
            this.treeContainer.addView(this.androidTreeView.getView());
            this.treeContainer.getChildAt(0).getLayoutParams().width = MATCH_PARENT;
        });
        treeViewTask.execute();
    }

    private void reset() {
        this.androidTreeView = null;
        this.treeContainer.removeAllViews();
        this.initTreeView(false, false, "");
    }

    private void reset(String search) {
        this.androidTreeView = null;
        this.treeContainer.removeAllViews();
        this.initTreeView(false, false, search);
    }
}
