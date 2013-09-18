package com.zhangwei.ui.jtree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.ui.JavaFileView;
import com.zhangwei.ui.jlist.SmaliEntryChanged;

public class TreePane extends JPanel implements ActionListener, TreeSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8375461163958980443L;

	private SmaliTree tree;
    private SmaliTreeModel model;
    private DefaultTreeSelectionModel dtm;
    
    private ArrayList<SmaliEntryChanged> JListDataNotify;
    
    private String last_dir_for_chose = "D:\\android\\crack\\guosen_dir\\examples";
    
    private static final String ACTION_KEY = "theAction";
    
    public TreePane() {
    	super(new BorderLayout());
		UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));
		
		SmaliEntry root = new SmaliEntry(new File("."), false, "root");
        tree = new SmaliTree();
        model = new SmaliTreeModel(root);
        JListDataNotify = new ArrayList<SmaliEntryChanged>();

        dtm = new DefaultTreeSelectionModel() {

			private static final long serialVersionUID = 5208506471560059640L;


			@Override
			public boolean isPathSelected(TreePath path) {
				// TODO Auto-generated method stub
				return false;
			}
			
			
			@Override
			public TreePath getSelectionPath() {
				// TODO Auto-generated method stub
				return null;
			}
		

		};
        
		dtm.addTreeSelectionListener(this);
		
		dtm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
        tree.setSelectionModel(dtm);
        tree.setModel(model);
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        
        Action actionListener = new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -2993268334156314079L;

			public void actionPerformed(ActionEvent actionEvent) {
              SmaliTree source = (SmaliTree)actionEvent.getSource();
              SmaliEntry item = (SmaliEntry)source.getLeadSelectionPath().getLastPathComponent();
              System.out.println("Activated: " + item.file.getAbsolutePath());
            }
          };
        
        KeyStroke F2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0 , true);
        ActionMap actionMap = tree.getActionMap();
        actionMap.put(ACTION_KEY, actionListener);
        
        InputMap inputMap = tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(F2, ACTION_KEY);
        tree.setActionMap(actionMap);
        
        add(new JScrollPane(tree));

    }
    
    public void addSmaliEntryChangedListener(SmaliEntryChanged listener){
    	JListDataNotify.add(listener);
    }
    
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		// TODO Auto-generated method stub
		System.err.println(event.getNewLeadSelectionPath());
		TreePath tp = event.getNewLeadSelectionPath();
		if(tp!=null){
			SmaliEntry newEntry = (SmaliEntry) tp.getLastPathComponent();
			if(JListDataNotify!=null && JListDataNotify.size()>0){
				for(SmaliEntryChanged listener : JListDataNotify){
					listener.EntryChanged(newEntry);
				}

			}
		}

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

