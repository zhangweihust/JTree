package com.zhangwei.jtree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.ui.JavaFileView;

public class TreePane extends JPanel implements ActionListener {
    //private DefaultTreeModel model;
    //private DefaultTreeCellRenderer renderer;
    //private JTree tree;
	private SmaliTree tree;
    private SmaliTreeModel model;
    
    private String last_dir_for_chose = ".";
    
    public TreePane() {
    	super(new BorderLayout());
		UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));
		
		SmaliEntry root = new SmaliEntry(new File("."), false, "root/.");
        tree = new SmaliTree();
        model = new SmaliTreeModel(root);

        tree.setModel(model);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);

        add(new JScrollPane(tree));

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    protected void addFiles(File rootFile, DefaultTreeModel model, DefaultMutableTreeNode root) {

        for (File file : rootFile.listFiles()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(file);
            model.insertNodeInto(child, root, root.getChildCount());
            if (file.isDirectory()) {
                addFiles(file, model, child);
            }
        }

    }

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		// TODO Auto-generated method stub
		
		System.out.println("Selected: " + actionEvent.getActionCommand());
        Component parent = (Component)actionEvent.getSource();
        JFileChooser fileChooser = new JFileChooser(last_dir_for_chose);
        //fileChooser.setAccessory(new LabelAccessory(fileChooser));
        FileView view = new JavaFileView();
        fileChooser.setFileView (view);
        fileChooser.setDialogTitle("选择smali根目录");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int status = fileChooser.showOpenDialog(parent);
        if (status == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
          
            last_dir_for_chose = selectedFile.getAbsolutePath();
			//DirLoader.getInstance().Load(entryVector, selectedFile.getAbsolutePath());
            SmaliEntry root = new SmaliEntry(selectedFile, false, "root");
            SmaliLoader.getInstance().loadRoot(root);

            tree.changeRoot(root);
            
            tree.expandPath(new TreePath(root));

         } 



    
	}
}

