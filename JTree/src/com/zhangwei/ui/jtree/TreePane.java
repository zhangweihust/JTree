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

public class TreePane extends JPanel implements ActionListener,
		TreeSelectionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8375461163958980443L;

	private SmaliTree tree;
	private SmaliTreeModel model;
	private DefaultTreeSelectionModel dtm;

	private ArrayList<SmaliEntryChanged> JListDataNotify;

	private String last_dir_for_chose = "D:\\android\\crack\\AndroidKiller_v1.3.1\\projects\\weixin637android660_20151231\\Project"; // "D:\\android\\crack\\test1\\guosen3.6";//"D:\\android\\crack\\guosen_dir\\examples";
	private String last_file_for_chose = "D:\\android\\crack\\AndroidKiller_v1.3.1\\projects\\weixin637android660_20151231\\Project\\saveState.conf";// "D:\\android\\crack\\guosen_dir\\examples";

	private static final String ACTION_F2_KEY = "theJTreeAction_F2";
	private static final String ACTION_F3_KEY = "theJTreeAction_F3";

	private ProgressMonitor monitor;

	public TreePane() {
		super(new BorderLayout());
		UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
		UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));

		SmaliEntry root = new SmaliEntry(null, "defalut", false);
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

		Action actionF2Listener = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -2993268334156314079L;

			public void actionPerformed(ActionEvent actionEvent) {

				SmaliTree source = (SmaliTree) actionEvent.getSource();
				TreePath tp = source.getLeadSelectionPath();
				SmaliEntry item = (SmaliEntry) tp.getLastPathComponent();
				String rename_item_str = item.toString();
				if (item.isFile()) { // rename class

					String s = "NULL";
					Pattern pattern = Pattern.compile("^L.*;$");
					Matcher matcher = pattern.matcher(s);
					while (!matcher.matches()) {
						s = (String) JOptionPane.showInputDialog(TreePane.this,
								"Rename class: " + rename_item_str, "Rename",
								JOptionPane.PLAIN_MESSAGE, null, null,
								item.classHeader.classNameSelf);

						if (s == null) {
							break;
						} else if (!s.equals("")) {
							matcher = pattern.matcher(s);
						}
					}

					System.out.println("Rename class after input: " + s);

					if (s != null && !s.equals(item.classHeader.classNameSelf)) {
						if (item.isFile()) {
							SmaliLoader.getInstance().renameClass(item,
									item.classHeader.classNameSelf, s, true);
							model.Refresh();
						}
					}
				} else { // rename package
					String s = "$";
					Pattern pattern = Pattern.compile("L[a-z|A-Z|0-9|/]*");
					Matcher matcher = pattern.matcher(s);
					while (!matcher.matches()) {
						s = (String) JOptionPane.showInputDialog(TreePane.this,
								"Rename package: " + rename_item_str, "Rename",
								JOptionPane.PLAIN_MESSAGE, null, null,
								rename_item_str);
						if (s == null) {
							break;
						} else if (!s.equals("")) {
							matcher = pattern.matcher(s);
						}

					}

					if (s != null) {
						String packageName = item.packageName; // Lcom/b/d/a
						// String packageName =
						// SmaliLoader.getPackageName(item.file); //com.b.d.a

						// String smaliPackagePrefix = "L" + s.replace(".", "/")
						// + "/"; //Lcom/b/d/a/
						String smaliPackagePrefix = s + "/"; // Lcom/b/d/a/

						System.out.println("Rename package after input: " + s
								+ ", packageName:" + packageName);

						if (item.leafChildren != null
								&& item.leafChildren.size() > 0
								&& !s.equals(packageName)) {
							ArrayList<SmaliEntry> tmp = new ArrayList<SmaliEntry>();
							Iterator<SmaliEntry> iterator = item.leafChildren
									.iterator();
							while (iterator.hasNext()) {
								SmaliEntry it = iterator.next();
								tmp.add(it);
							}

							for (SmaliEntry a : tmp) {
								String subStr = a.toString().replace(".smali",
										""); // c.smali -> c
								String newClassName = smaliPackagePrefix
										+ subStr + ";"; // Lcom/b/d/a/c;
								if (newClassName != null
										&& !newClassName
												.equals(a.classHeader.classNameSelf)) {
									if (a.isFile()) {
										SmaliLoader.getInstance().renameClass(
												a, a.classHeader.classNameSelf,
												newClassName, true);
									}
								}
							}

							model.Refresh();
						}
					}

				}

			}
		};

		Action actionF3Listener = new AbstractAction() {


			/**
			 * 
			 */
			private static final long serialVersionUID = 7493278812177464115L;

			public void actionPerformed(ActionEvent actionEvent) {

				SmaliTree source = (SmaliTree) actionEvent.getSource();
				TreePath tp = source.getLeadSelectionPath();
				SmaliEntry item = (SmaliEntry) tp.getLastPathComponent();
				String rename_item_str = item.toString();
				if (item.isFile()) { // rename class
					
					int ret = JOptionPane.showConfirmDialog(TreePane.this, "AutoRename", rename_item_str, JOptionPane.OK_CANCEL_OPTION);
					if(ret==JOptionPane.OK_OPTION){
						SmaliLoader.getInstance().autoRename(TreePane.this, item.classHeader.classNameSelf);
						
						model.Refresh();
					}

				} else { // rename package
					int ret = JOptionPane.showConfirmDialog(TreePane.this, "AutoRename", rename_item_str, JOptionPane.OK_CANCEL_OPTION);
					if(ret==JOptionPane.OK_OPTION){
						SmaliLoader.getInstance().autoRename(TreePane.this, item.packageName + "/");
						
						model.Refresh();
					}

				}

			}
		};

		KeyStroke F2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true);
		KeyStroke F3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, true);

		ActionMap actionMap = tree.getActionMap();
		actionMap.put(ACTION_F2_KEY, actionF2Listener);
		actionMap.put(ACTION_F3_KEY, actionF3Listener);

		InputMap inputMap = tree
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(F2, ACTION_F2_KEY);
		inputMap.put(F3, ACTION_F3_KEY);
		tree.setActionMap(actionMap);

		add(new JScrollPane(tree));

	}

	public void addSmaliEntryChangedListener(SmaliEntryChanged listener) {
		JListDataNotify.add(listener);
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		// TODO Auto-generated method stub
		System.err.println(event.getNewLeadSelectionPath());
		TreePath tp = event.getNewLeadSelectionPath();
		if (tp != null) {
			SmaliEntry newEntry = (SmaliEntry) tp.getLastPathComponent();
			if (JListDataNotify != null && JListDataNotify.size() > 0) {
				for (SmaliEntryChanged listener : JListDataNotify) {
					listener.EntryChanged(newEntry);
				}

			}
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}

	protected void addFiles(File rootFile, DefaultTreeModel model,
			DefaultMutableTreeNode root) {

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
		Component parent = (Component) actionEvent.getSource();

		if (action.equals(SmaliMain.OPEN)) {
			JFileChooser fileChooser = new JFileChooser(last_dir_for_chose);
			// fileChooser.setAccessory(new LabelAccessory(fileChooser));
			FileView view = new JavaFileView();
			fileChooser.setFileView(view);
			fileChooser.setDialogTitle("选择smali根目录");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setMultiSelectionEnabled(true);
			int status = fileChooser.showOpenDialog(parent);
			if (status == JFileChooser.APPROVE_OPTION) {
				// File selectedFile = fileChooser.getSelectedFile();
				File[] selectedFiles = fileChooser.getSelectedFiles();
				OpenThread t = new OpenThread();
				t.startThread(selectedFiles);
			}
		} else if (action.equals(SmaliMain.Load)) {
			JFileChooser fileChooser = new JFileChooser(last_file_for_chose);
			// fileChooser.setAccessory(new LabelAccessory(fileChooser));
			FileView view = new JavaFileView();
			fileChooser.setFileView(view);
			fileChooser.setDialogTitle("选择saveState.conf");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int status = fileChooser.showOpenDialog(parent);
			if (status == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				LoadConfThread t = new LoadConfThread();
				t.startThread(selectedFile);
			}
		} else if (action.equals(SmaliMain.Shrink)) {
			JFileChooser fileChooser = new JFileChooser(last_file_for_chose);
			// fileChooser.setAccessory(new LabelAccessory(fileChooser));
			FileView view = new JavaFileView();
			fileChooser.setFileView(view);
			fileChooser.setDialogTitle("选择shrink json");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int status = fileChooser.showOpenDialog(parent);
			if (status == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				ShrinkThread t = new ShrinkThread();
				t.startThread(selectedFile);
			}
		} else if (action.equals(SmaliMain.Command)) {
			JFileChooser fileChooser = new JFileChooser(last_file_for_chose);
			// fileChooser.setAccessory(new LabelAccessory(fileChooser));
			FileView view = new JavaFileView();
			fileChooser.setFileView(view);
			fileChooser.setDialogTitle("选择command json");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int status = fileChooser.showOpenDialog(parent);
			if (status == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				CommandThread t = new CommandThread();
				t.startThread(selectedFile);
			}
		} else if (action.equals(SmaliMain.SAVE)) {
			SaveConfThread t = new SaveConfThread();
			t.startThread();
		} else if (action.equals(SmaliMain.AUTO_RENAME)) {
			RenameThread t = new RenameThread();
			t.startThread();
		} else if (action.equals(SmaliMain.AUTO_PUBLIC)) {
			UpPublicThread t = new UpPublicThread();
			t.startThread();
		} else if (action.equals(SmaliMain.Add)) {
			JFileChooser fileChooser = new JFileChooser(last_dir_for_chose);
			// fileChooser.setAccessory(new LabelAccessory(fileChooser));
			FileView view = new JavaFileView();
			fileChooser.setFileView(view);
			fileChooser.setDialogTitle("add - 选择smali根目录");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setMultiSelectionEnabled(true);
			int status = fileChooser.showOpenDialog(parent);
			if (status == JFileChooser.APPROVE_OPTION) {
				// File selectedFile = fileChooser.getSelectedFile();
				File[] selectedFiles = fileChooser.getSelectedFiles();
				AddThread t = new AddThread();
				t.startThread(selectedFiles);
			}
		} else if (action.equals(SmaliMain.Del)) {
			JFileChooser fileChooser = new JFileChooser(last_dir_for_chose);
			// fileChooser.setAccessory(new LabelAccessory(fileChooser));
			FileView view = new JavaFileView();
			fileChooser.setFileView(view);
			fileChooser.setDialogTitle("del - 选择smali根目录");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fileChooser.setMultiSelectionEnabled(true);
			int status = fileChooser.showOpenDialog(parent);
			if (status == JFileChooser.APPROVE_OPTION) {
				// File selectedFile = fileChooser.getSelectedFile();
				File[] selectedFiles = fileChooser.getSelectedFiles();
				DelThread t = new DelThread();
				t.startThread(selectedFiles);
			}
		}

	}

	class DelThread extends Thread {

		private File[] selectedFiles;

		void startThread(File[] selectedFiles) {
			this.selectedFiles = selectedFiles;
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if (selectedFiles.length > 0) {

				SmaliEntry root = tree.getRoot();

				if (root == null) {
					System.err.println("root null");
					return;
				}

				last_dir_for_chose = selectedFiles[0].getParentFile()
						.getAbsolutePath();

				SmaliLoader.getInstance().delRoot(TreePane.this, root,
						selectedFiles);
				SmaliLoader.getInstance().sortTree();
				tree.Refresh();

			}

		}
	}

	class AddThread extends Thread {

		private File[] selectedFiles;

		void startThread(File[] selectedFiles) {
			this.selectedFiles = selectedFiles;
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if (selectedFiles.length > 0) {

				SmaliEntry root = tree.getRoot();

				if (root == null) {
					System.err.println("root null");
					return;
				}

				last_dir_for_chose = selectedFiles[0].getParentFile()
						.getAbsolutePath();

				SmaliLoader.getInstance().addRoot(TreePane.this, root,
						selectedFiles);
				SmaliLoader.getInstance().sortTree();

				tree.Refresh();

			}

		}
	}

	class ShrinkThread extends Thread {

		private File selectedFile;

		void startThread(File selectedFile) {
			this.selectedFile = selectedFile;
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if (selectedFile != null && selectedFile.isFile()) {
				last_file_for_chose = selectedFile.getParentFile()
						.getAbsolutePath();

				SmaliLoader.getInstance().execShrink(TreePane.this,
						selectedFile);

			}

		}
	}

	class CommandThread extends Thread {

		private File selectedFile;

		void startThread(File selectedFile) {
			this.selectedFile = selectedFile;
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if (selectedFile != null && selectedFile.isFile()) {
				last_file_for_chose = selectedFile.getParentFile()
						.getAbsolutePath();

				SmaliLoader.getInstance().execCommand(TreePane.this,
						selectedFile);

			}

		}
	}

	class OpenThread extends Thread {

		private File[] selectedFiles;

		void startThread(File[] selectedFiles) {
			this.selectedFiles = selectedFiles;
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			if (selectedFiles.length > 0) {
				last_dir_for_chose = selectedFiles[0].getParentFile()
						.getAbsolutePath();
				SmaliEntry root = new SmaliEntry(
						selectedFiles[0].getParentFile(), "defalut", false);

				SmaliLoader.getInstance().loadRoot(TreePane.this, root,
						selectedFiles);
				SmaliLoader.getInstance().sortTree();

				tree.changeRoot(root);

				tree.expandPath(new TreePath(root));
			}

		}
	}

	class SaveConfThread extends Thread {

		void startThread() {
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

		void startThread(File selectedFile) {
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

				SmaliLoader.getInstance().sortTree();

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

		void startThread() {
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			SmaliLoader.getInstance().autoRename(TreePane.this);
			SmaliLoader.getInstance().sortTree();
			tree.Refresh();
		}
	}

	class UpPublicThread extends Thread {

		void startThread() {
			this.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			SmaliLoader.getInstance().autoPublic(TreePane.this);
			tree.Refresh();
		}
	}
}
