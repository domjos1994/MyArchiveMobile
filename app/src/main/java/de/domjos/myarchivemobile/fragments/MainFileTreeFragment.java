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

package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unnamed.b.atv.view.AndroidTreeView;

import de.domjos.myarchivelibrary.custom.AbstractTask;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeFile;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeNode;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.custom.CustomTreeNode;
import de.domjos.myarchivemobile.custom.CustomTreeNodeHolder;
import de.domjos.myarchivemobile.dialogs.TreeViewDialog;
import de.domjos.myarchivemobile.tasks.TreeViewTask;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainFileTreeFragment extends ParentFragment {
    private RelativeLayout treeContainer;
    private AndroidTreeView androidTreeView;

    private ProgressBar pbProgress;

    private BottomNavigationView navigationView;

    private com.unnamed.b.atv.model.TreeNode.TreeNodeClickListener treeNodeClickListener;

    private com.unnamed.b.atv.model.TreeNode lastNode;
    private TreeNode node, cpNode;
    private TreeFile file, cpFile;
    private String search;

    private String path;
    private boolean data = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_file_tree, container, false);
        this.initControls(root);
        this.initTreeView(true, false, "");

        this.navigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.cmdAddFile) {
                TreeViewDialog fileDialog = TreeViewDialog.newInstance(TreeViewDialog.FILE, (this.node!=null?this.node.getId():0));
                fileDialog.addPreExecute(this::reset);
                fileDialog.show(this.requireActivity());
            } else if(menuItem.getItemId() == R.id.cmdAddNode) {
                TreeViewDialog nodeDialog = TreeViewDialog.newInstance(TreeViewDialog.NODE, (this.node!=null?this.node.getId():0));
                nodeDialog.addPreExecute(this::reset);
                nodeDialog.show(this.requireActivity());
            } else if(menuItem.getItemId() == R.id.cmdReload) {
                this.initTreeView(true, true, "");
            } else if(menuItem.getItemId() == R.id.cmdView) {
                this.viewItem();
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

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderTitle("Context Menu");

        MenuInflater inflater = super.requireActivity().getMenuInflater();
        inflater.inflate(R.menu.context_tree, menu);
        menu.findItem(R.id.ctxPaste).setEnabled(this.cpNode != null || this.cpFile != null);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.ctxCopy) {
            this.selectItem(false);
        } else if(item.getItemId() == R.id.ctxCut) {
            this.selectItem(true);
        } else if(item.getItemId() == R.id.ctxPaste) {
            this.pasteItem();
        }
        return true;
    }

    private void selectItem(boolean move) {
        if(this.node != null) {
            if(this.node.getId() != 0) {
                if(!move) {
                    this.node.setId(0);
                }
                this.cpNode = this.node;
            }
        }
        if(this.file != null) {
            if(this.file.getId() != 0) {
                if(!move) {
                    this.file.setId(0);
                }
                this.cpFile = this.file;
            }
        }
    }

    private void pasteItem() {
        if(this.node != null && (this.cpNode != null || this.cpFile != null)) {
            if (this.node.getId() != 0) {
                if(this.cpNode != null) {
                    this.cpNode.setParent(this.node);
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(this.cpNode);
                }
                if(this.cpFile != null) {
                    this.cpFile.setParent(this.node);
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(this.cpFile);
                }
                this.reset("");
            }
        }
    }

    private void initControls(View view) {
        this.treeContainer = view.findViewById(R.id.treeContainer);
        this.navigationView = view.findViewById(R.id.navigationView);
        this.pbProgress = view.findViewById(R.id.pbProgress);
        this.registerForContextMenu(this.treeContainer);

        this.treeNodeClickListener = (node, value) -> {
            if(this.lastNode != null) {
                ((CustomTreeNodeHolder) this.lastNode.getViewHolder()).unSelect();
            }
            this.lastNode = node;
            if(((CustomTreeNode)value).getTreeItem() instanceof TreeNode) {
                this.file = null;
                this.node = (TreeNode) ((CustomTreeNode)value).getTreeItem();
            }
            if(((CustomTreeNode)value).getTreeItem() instanceof TreeFile) {
                this.node = null;
                this.file = (TreeFile) ((CustomTreeNode)value).getTreeItem();
            }

            ((CustomTreeNodeHolder) this.lastNode.getViewHolder()).select();
            boolean system = ((CustomTreeNodeHolder) this.lastNode.getViewHolder()).isSystem();
            this.navigationView.setVisibility(system || this.data ? View.GONE : View.VISIBLE);
            if(((CustomTreeNode) value).getTreeItem() instanceof TreeNode) {
                try {
                    TreeViewTask treeViewTask = new TreeViewTask(this.requireActivity(), this.pbProgress, false, false, true, system, node, this.node, this.search);
                    treeViewTask.execute().get();
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.requireActivity());
                }
            }
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

    private void viewItem() {
        boolean system = false;
        BaseDescriptionObject tmp;
        if(this.file != null) {
            tmp = this.file;
            if(this.file.getParent() != null) {
                system = this.file.getParent().isSystem();
            }
        } else if(this.node != null) {
            tmp = this.node;
            system =  this.node.isSystem();
        } else {
            return;
        }

        TreeViewDialog treeViewDialog;
        if(this.data && !system) {
            treeViewDialog = TreeViewDialog.newInstance(TreeViewDialog.FILE, this.path, tmp.getId());
            treeViewDialog.addPreExecute(()->System.exit(0));
        } else {
            treeViewDialog = TreeViewDialog.newInstance(tmp, system);
            treeViewDialog.addPreExecute(this::reset);
        }
        treeViewDialog.show(this.requireActivity());
    }

    private void initTreeView(boolean firstStart, boolean checkDatabase, String search) {
        com.unnamed.b.atv.model.TreeNode node = com.unnamed.b.atv.model.TreeNode.root();

        TreeViewTask treeViewTask = new TreeViewTask(this.requireActivity(), this.pbProgress, firstStart, checkDatabase, false, false, node, null, search);
        treeViewTask.after((AbstractTask.PostExecuteListener<com.unnamed.b.atv.model.TreeNode>) o -> {
            this.androidTreeView = new AndroidTreeView(this.requireActivity(), o);
            this.androidTreeView.setDefaultNodeClickListener(this.treeNodeClickListener);
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
