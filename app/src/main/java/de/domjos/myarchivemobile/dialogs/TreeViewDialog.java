package de.domjos.myarchivemobile.dialogs;

import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TableRow;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeFile;
import de.domjos.myarchivelibrary.model.media.fileTree.TreeNode;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class TreeViewDialog extends DialogFragment {
    private final static String ID = "id";
    private final static String TYPE = "type";
    private final static String SYSTEM = "system";
    private final static String PARENT = "parent";

    public final static String NODE = "node";
    public final static String FILE = "file";

    private Runnable preExecution;
    private String path;

    private LinearLayout controls;
    private TableRow rowFileIV, rowFileButtons, rowNodeGallery;

    private EditText txtTitle, txtDescription;
    private AutoCompleteTextView txtCategory;
    private MultiAutoCompleteTextView txtTags;
    private ImageView ivImage;
    private ImageButton cmdImageAdd;
    private CheckBox chkImageEmbed, chkNodeGallery;

    private ImageButton cmdDelete, cmdSave, cmdCancel;

    private TreeNode node;
    private TreeFile file;

    public static TreeViewDialog newInstance(BaseDescriptionObject item, boolean system) {

        Bundle args = new Bundle();

        args.putLong(TreeViewDialog.ID, item.getId());
        args.putBoolean(TreeViewDialog.SYSTEM, system);
        if(item instanceof TreeNode) {
            args.putLong(TreeViewDialog.PARENT, ((TreeNode) item).getParent() == null ? 0 : ((TreeNode) item).getParent().getId());
            args.putString(TreeViewDialog.TYPE, TreeViewDialog.NODE);
        } else {
            args.putLong(TreeViewDialog.PARENT, ((TreeFile) item).getParent() == null ? 0 : ((TreeFile) item).getParent().getId());
            args.putString(TreeViewDialog.TYPE, TreeViewDialog.FILE);
        }

        TreeViewDialog fragment = new TreeViewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static TreeViewDialog newInstance(String type, long parent) {

        Bundle args = new Bundle();

        args.putLong(TreeViewDialog.ID, 0);
        args.putString(TreeViewDialog.TYPE, type);
        args.putBoolean(TreeViewDialog.SYSTEM, false);
        args.putLong(TreeViewDialog.PARENT, parent);

        TreeViewDialog fragment = new TreeViewDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void addPreExecute(Runnable runnable) {
        this.preExecution = runnable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment_file_tree_dialog, container, false);
        Dialog dialog = Objects.requireNonNull(this.getDialog());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.initControls(v);
        this.controlView();
        this.load();

        this.cmdImageAdd.setOnClickListener(event -> {
            List<String> extensions = Arrays.asList("jpg", "JPG", "jpeg", "JPEG", "png", "PNG", "bmp", "BMP");
            FilePickerDialog filePickerDialog = ControlsHelper.openFilePicker(false, false, extensions, this.getActivity());
            filePickerDialog.setDialogSelectionListener(files -> {
                if(files != null) {
                    if(files.length != 0) {
                        this.path = files[0];
                    } else {
                        this.path = "";
                    }
                } else {
                    this.path = "";
                }

                if(this.path.isEmpty()) {
                    this.ivImage.setImageDrawable(null);
                } else {
                    this.ivImage.setImageBitmap(BitmapFactory.decodeFile(this.path));
                }
            });
            filePickerDialog.show();
        });

        this.cmdCancel.setOnClickListener(event -> this.dismiss());
        this.cmdDelete.setOnClickListener(event -> {
            if(this.node != null) {
                MainActivity.GLOBALS.getDatabase().deleteItem(this.node);
            }
            if(this.file != null) {
                MainActivity.GLOBALS.getDatabase().deleteItem(this.file);
            }
            this.dismiss();
        });
        this.cmdSave.setOnClickListener(event -> {
            if(this.node != null) {
                this.node.setTitle(this.txtTitle.getText().toString());
                this.node.setDescription(this.txtDescription.getText().toString());

                String category = this.txtCategory.getText().toString();
                if(!category.trim().isEmpty()) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(category);
                    this.node.setCategory(baseDescriptionObject);
                }

                String tags = this.txtTags.getText().toString();
                if(!tags.trim().isEmpty()) {
                    for(String tag : tags.split(", ")) {
                        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                        baseDescriptionObject.setTitle(tag);
                        this.node.getTags().add(baseDescriptionObject);
                    }
                }

                this.node.setGallery(this.chkNodeGallery.isChecked());
                MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNode(this.node);
            }
            if(this.file != null) {
                this.file.setTitle(this.txtTitle.getText().toString());
                this.file.setDescription(this.txtDescription.getText().toString());

                String category = this.txtCategory.getText().toString();
                if(!category.trim().isEmpty()) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(category);
                    this.file.setCategory(baseDescriptionObject);
                }

                String tags = this.txtTags.getText().toString();
                if(!tags.trim().isEmpty()) {
                    for(String tag : tags.split(", ")) {
                        if(!tag.trim().isEmpty()) {
                            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                            baseDescriptionObject.setTitle(tag);
                            this.file.getTags().add(baseDescriptionObject);
                        }
                    }
                }

                if(this.path != null) {
                    if(!this.path.isEmpty()) {
                        if(this.chkImageEmbed.isChecked()) {
                            try {
                                File file = new File(this.path);
                                int size = (int) file.length();
                                byte[] bytes = new byte[size];
                                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                                buf.read(bytes, 0, bytes.length);
                                buf.close();
                                this.file.setEmbeddedContent(bytes);
                            } catch (Exception ex) {
                                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
                            }
                        } else {
                            this.file.setPathToFile(this.path);
                        }
                    }
                }
                MainActivity.GLOBALS.getDatabase().insertOrUpdateTreeNodeFiles(this.file);
            }
            this.dismiss();
        });

        return v;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(this.preExecution != null) {
            this.preExecution.run();
        }
    }

    public void show(FragmentActivity activity) {
        super.show((FragmentManager) activity.getSupportFragmentManager(), "dialog");
    }

    private void initControls(View view) {
        this.rowFileIV = view.findViewById(R.id.rowFileIV);
        this.rowFileButtons = view.findViewById(R.id.rowFileButtons);
        this.rowNodeGallery = view.findViewById(R.id.rowNodeGallery);

        this.txtTitle = view.findViewById(R.id.txtTitle);
        this.txtDescription = view.findViewById(R.id.txtDescription);
        this.txtTags = view.findViewById(R.id.txtTags);
        this.txtCategory = view.findViewById(R.id.txtCategory);

        this.ivImage = view.findViewById(R.id.ivImage);
        this.cmdImageAdd = view.findViewById(R.id.cmdImageAdd);
        this.chkImageEmbed = view.findViewById(R.id.chkImageEmbed);

        this.chkNodeGallery = view.findViewById(R.id.chkNodeGallery);

        this.controls = view.findViewById(R.id.controls);
        this.cmdDelete = view.findViewById(R.id.cmdDelete);
        this.cmdSave = view.findViewById(R.id.cmdSave);
        this.cmdCancel = view.findViewById(R.id.cmdCancel);
    }

    private void controlView() {
        boolean system = false;
        long id = 0;
        boolean node = true;
        if(this.getArguments() != null) {
            system = this.getArguments().getBoolean(TreeViewDialog.SYSTEM);
            id = this.getArguments().getLong(TreeViewDialog.ID);
            node = Objects.equals(this.requireArguments().getString(TreeViewDialog.TYPE), TreeViewDialog.NODE);
        }

        this.controls.setVisibility(system ? View.GONE : View.VISIBLE);
        this.rowNodeGallery.setVisibility(node ? View.VISIBLE : View.GONE);
        this.rowFileIV.setVisibility(!node ? View.VISIBLE : View.GONE);
        this.rowFileButtons.setVisibility(!node ? View.VISIBLE : View.GONE);

        long parent = this.getArguments().getLong(TreeViewDialog.PARENT);
        if(id != 0) {
            if(node) {
                this.node = MainActivity.GLOBALS.getDatabase().getNodeById(id);
                this.node.setParent(MainActivity.GLOBALS.getDatabase().getNodeById(parent));
            } else {
                this.file = MainActivity.GLOBALS.getDatabase().getTreeNodeFiles("id=" + id).get(0);
                this.file.setParent(MainActivity.GLOBALS.getDatabase().getNodeById(parent));
            }
        } else {
            if(node) {
                this.node = new TreeNode();
                this.node.setParent(MainActivity.GLOBALS.getDatabase().getNodeById(parent));
            } else {
                this.file = new TreeFile();
                this.file.setParent(MainActivity.GLOBALS.getDatabase().getNodeById(parent));
            }
        }

        if(parent == id || parent == 0) {
            this.cmdDelete.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams)this.cmdSave.getLayoutParams()).weight = 6;
            ((LinearLayout.LayoutParams)this.cmdCancel.getLayoutParams()).weight = 6;
        } else {
            this.cmdDelete.setVisibility(View.VISIBLE);
            ((LinearLayout.LayoutParams)this.cmdSave.getLayoutParams()).weight = 4;
            ((LinearLayout.LayoutParams)this.cmdCancel.getLayoutParams()).weight = 4;
        }
    }

    private void load() {
        if(this.node != null) {
            this.txtTitle.setText(this.node.getTitle());
            this.txtDescription.setText(this.node.getDescription());
            this.txtCategory.setText(this.node.getCategory() == null ? "" : this.node.getCategory().getTitle());
            if(this.node.getTags() != null) {
                for(BaseDescriptionObject baseDescriptionObject : this.node.getTags()) {
                    this.txtTags.getText().append(baseDescriptionObject.getTitle()).append(", ");
                }
            }
            this.chkNodeGallery.setChecked(this.node.isGallery());
        }

        if(this.file != null) {
            this.txtTitle.setText(this.file.getTitle());
            this.txtDescription.setText(this.file.getDescription());
            this.txtCategory.setText(this.file.getCategory() == null ? "" : this.file.getCategory().getTitle());
            if(this.file.getTags() != null) {
                for(BaseDescriptionObject baseDescriptionObject : this.file.getTags()) {
                    this.txtTags.getText().append(baseDescriptionObject.getTitle()).append(", ");
                }
            }

            if(this.file.getInternalId() != 0) {
                byte[] content = MainActivity.GLOBALS.getDatabase().loadImage(this.file.getInternalId(), this.file.getInternalTable(), this.file.getInternalColumn());
                if(content != null) {
                    this.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(content, 0, content.length));
                }
                this.cmdImageAdd.setVisibility(View.GONE);
                this.chkImageEmbed.setVisibility(View.GONE);
            } else if(!this.file.getPathToFile().trim().isEmpty()) {
                this.ivImage.setImageBitmap(BitmapFactory.decodeFile(this.file.getPathToFile()));
            } else {
                if(this.file.getEmbeddedContent() != null) {
                    this.chkImageEmbed.setChecked(true);
                    this.ivImage.setImageBitmap(BitmapFactory.decodeByteArray(this.file.getEmbeddedContent(), 0, this.file.getEmbeddedContent().length));
                }
            }
        }
    }
}
