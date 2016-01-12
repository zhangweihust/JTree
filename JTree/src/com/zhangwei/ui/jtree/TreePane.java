package com.zhangwei.ui.jtree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.google.gson.Gson;
import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.smali.json.SaveState;
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
    
    private String last_dir_for_chose = "D:\\android\\crack\\AndroidKiller_v1.3.1\\projects" ; //"D:\\android\\crack\\test1\\guosen3.6";//"D:\\android\\crack\\guosen_dir\\examples";
    private String last_file_for_chose = "D:\\android\\crack\\AndroidKiller_v1.3.1\\projects\\test\\saveState.conf";//"D:\\android\\crack\\guosen_dir\\examples";
    
    private static final String ACTION_KEY = "theJTreeAction";
    
    private ProgressMonitor monitor;
    
    public TreePane() {
    	super(new BorderLayout());
		UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));
		
		SmaliEntry root = new SmaliEntry(null, "defalut",  false);
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
              TreePath tp = source.getLeadSelectionPath();
              SmaliEntry item = (SmaliEntry)tp.getLastPathComponent();
              String rename_item_str = item.toString();
              if(item.isFile()){ //rename class
            	  rename_item_str = rename_item_str.replace(".smali", "");
                  
                  String s = "NULL";
                  Pattern pattern = Pattern.compile("^L.*;$");
                  Matcher matcher = pattern.matcher(s);
                  while(!matcher.matches()){
                      s = (String)JOptionPane.showInputDialog(
                              TreePane.this,
                              "Rename class: "+ rename_item_str,
                              "Rename",
                              JOptionPane.PLAIN_MESSAGE,
                              null,
                              null,
                              item.classHeader.classNameSelf);
                      
                      if(s==null){
                    	  break;
                      }else if(!s.equals("")){
                          matcher = pattern.matcher(s);
                      }
                  }

                  
                  System.out.println("Rename class after input: " + s);
                  
                  if(s!=null && !s.equals(item.classHeader.classNameSelf)){
                      if(item.isFile()){
                    	  SmaliLoader.getInstance().renameClass(item, item.classHeader.classNameSelf, s, true);
                    	  model.Refresh();
                      }
                  }
              }else{ //rename package
                  String s = "$";
                  Pattern pattern = Pattern.compile("L[a-z|A-Z|0-9|/]*");
                  Matcher matcher = pattern.matcher(s);
                  while(!matcher.matches()){
                      s = (String)JOptionPane.showInputDialog(
                              TreePane.this,
                              "Rename package: " + rename_item_str,
                              "Rename",
                              JOptionPane.PLAIN_MESSAGE,
                              null,
                              null,
                              rename_item_str);
                      if(s==null){
                    	  break;
                      }else if(!s.equals("")){
                          matcher = pattern.matcher(s);
                      }

                  }

                  if(s!=null){
                      String packageName = item.packageName; //Lcom/b/d/a
                     //String packageName = SmaliLoader.getPackageName(item.file); //com.b.d.a
                      
//                      String smaliPackagePrefix = "L" + s.replace(".", "/") + "/"; //Lcom/b/d/a/
                      String smaliPackagePrefix = s + "/"; //Lcom/b/d/a/
                      
                      System.out.println("Rename package after input: " + s + ", packageName:" + packageName);

                      if(item.leafChildren!=null && item.leafChildren.size()>0 && !s.equals(packageName)){
                    	  ArrayList<SmaliEntry> tmp = new ArrayList<SmaliEntry>();
                    	  Iterator<SmaliEntry> iterator = item.leafChildren.iterator();
                    	  while(iterator.hasNext()){
                    		  SmaliEntry it = iterator.next();
                    		  tmp.add(it);
                    	  }
                    	  
                    	  for(SmaliEntry a : tmp){
                    		  String subStr = a.toString().replace(".smali", ""); //c.smali -> c
                    		  String newClassName = smaliPackagePrefix + subStr + ";"; //Lcom/b/d/a/c;
                              if(newClassName!=null && !newClassName.equals(a.classHeader.classNameSelf)){
                                  if(a.isFile()){
                                	  SmaliLoader.getInstance().renameClass(a, a.classHeader.classNameSelf, newClassName, true);
                                  }
                              }
                    	  }
                    	  
                    	  model.Refresh();
                      }
                  }

              }

              


              
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
		String action = actionEvent.getActionCommand();
		System.out.println("Selected: " + action);
        Component parent = (Component)actionEvent.getSource();
        
        if(action.equals(SmaliMain.OPEN)){
            JFileChooser fileChooser = new JFileChooser(last_dir_for_chose);
            //fileChooser.setAccessory(new LabelAccessory(fileChooser));
            FileView view = new JavaFileView();
            fileChooser.setFileView (view);
            fileChooser.setDialogTitle("选择smali根目录");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(true);
            int status = fileChooser.showOpenDialog(parent);
            if (status == JFileChooser.APPROVE_OPTION) {
//                File selectedFile = fileChooser.getSelectedFile();
            	File[] selectedFiles = fileChooser.getSelectedFiles();
                LoadThread t = new LoadThread();
                t.startThread(selectedFiles);
            } 
        }if(action.equals(SmaliMain.Load)){
            JFileChooser fileChooser = new JFileChooser(last_file_for_chose);
            //fileChooser.setAccessory(new LabelAccessory(fileChooser));
            FileView view = new JavaFileView();
            fileChooser.setFileView (view);
            fileChooser.setDialogTitle("选择saveState.conf");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int status = fileChooser.showOpenDialog(parent);
            if (status == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                LoadConfThread t = new LoadConfThread();
                t.startThread(selectedFile);
            } 
        }else if(action.equals(SmaliMain.SAVE)){
        	SaveConfThread t = new SaveConfThread();
        	t.startThread();
        }else if(action.equals(SmaliMain.AUTO_RENAME)){
        	RenameThread t = new RenameThread();
        	t.startThread();
        }else if(action.equals(SmaliMain.AUTO_PUBLIC)){
        	UpPublicThread t = new UpPublicThread();
        	t.startThread();
        }
        

    
	}
	
	class LoadThread extends Thread {
		
		private File[] selectedFiles;

		void startThread(File[] selectedFiles ){
			this.selectedFiles = selectedFiles;
			this.start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
            
			if(selectedFiles.length>0){
	            last_dir_for_chose = selectedFiles[0].getParentFile().getAbsolutePath();
	            SmaliEntry root = new SmaliEntry(selectedFiles[0].getParentFile(), "defalut", false);
	           
				
	            SmaliLoader.getInstance().loadRoot(TreePane.this, root, selectedFiles);
	            
	            tree.changeRoot(root);
	            
	            tree.expandPath(new TreePath(root));
			}


		}
	}
	
	class SaveConfThread extends Thread {
		

		void startThread(){
			this.start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

            SmaliLoader.getInstance().saveState(TreePane.this);

		}
	}
	
	class LoadConfThread extends Thread {
		
		private File selectedFile;

		void startThread(File selectedFile ){
			this.selectedFile = selectedFile;
			this.start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
            
			last_file_for_chose = selectedFile.getAbsolutePath();

            Gson gson = new Gson();
            Reader reader = null;
            SaveState state = null;
			try {
				reader = new FileReader(last_file_for_chose);
				state = gson.fromJson(reader, SaveState.class);

	            
	            SmaliEntry root = state.root;
	            SmaliLoader.getInstance().loadState(TreePane.this, state);

	            tree.changeRoot(root);
	            
	            tree.expandPath(new TreePath(root));
	            
	            reader.close();
	            
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

		}
	}
	
	class RenameThread extends Thread {
		

		void startThread(){
			this.start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
            SmaliLoader.getInstance().autoRename(TreePane.this);

		}
	}
	
	class UpPublicThread extends Thread {
		

		void startThread(){
			this.start();
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
            SmaliLoader.getInstance().autoPublic(TreePane.this);

		}
	}
}

