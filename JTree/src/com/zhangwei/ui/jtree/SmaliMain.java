package com.zhangwei.ui.jtree;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.zhangwei.ui.JavaFileView;
import com.zhangwei.ui.jlist.ListPane;
import com.zhangwei.ui.jtext.JText;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class SmaliMain {
	public static final String AUTO_RENAME = "Auto-rename (R)";
	public static final String AUTO_PUBLIC = "Auto-public (U)";
	public static final String CLOSE = "Close (C)";
	public static final String OPEN = "Open (O)";
	public static final String Load = "Load (L)";
	public static final String SAVE = "Save (S)";
	public static final String Add = "Add (A)";
	public static final String Del = "Del (D)";
	public static final String Command = "Command (M)";
	public static final String Shrink = "Shrink (K)";
	
	static JFrame frame;

	public static void main(final String args[]) {

		// MyParser.Paser_dir(args[0]);
		Runnable runner = new Runnable() {
			public void run() {
				try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
				
				String title = ("JTree Sample");
				frame = new JFrame(title);

				TreePane treePane = new TreePane();

				JMenuBar menuBar = new JMenuBar();
				JMenu fileMenu = new JMenu("File");
				fileMenu.setLayout(new GridLayout(3, 3));
				fileMenu.setMnemonic(KeyEvent.VK_F);
				menuBar.add(fileMenu);

				// File->Open, O - Mnemonic
				JMenuItem openMenuItem = new JMenuItem(OPEN, KeyEvent.VK_O);
				openMenuItem.addActionListener(treePane);
				fileMenu.add(openMenuItem);
				
				// File->Save, S - Mnemonic
				JMenuItem saveMenuItem = new JMenuItem(SAVE, KeyEvent.VK_S);
				saveMenuItem.addActionListener(treePane);
				fileMenu.add(saveMenuItem);
				
				// File->Load, L - Mnemonic
				JMenuItem loadMenuItem = new JMenuItem(Load, KeyEvent.VK_L);
				loadMenuItem.addActionListener(treePane);
				fileMenu.add(loadMenuItem);
				
		        // Separator
		        fileMenu.addSeparator();
		        
				// File->Open, O - Mnemonic
				JMenuItem autoMenuItem = new JMenuItem( AUTO_RENAME, KeyEvent.VK_R);
				autoMenuItem.addActionListener(treePane);
				fileMenu.add(autoMenuItem);
				
		        // Separator
		        fileMenu.addSeparator();
		        
				// File->Open, O - Mnemonic
				JMenuItem publicMenuItem = new JMenuItem( AUTO_PUBLIC, KeyEvent.VK_U);
				publicMenuItem.addActionListener(treePane);
				fileMenu.add(publicMenuItem);
				
		        // Separator
		        fileMenu.addSeparator();
		        
				// File->Add, A - Mnemonic
				JMenuItem addMenuItem = new JMenuItem(Add, KeyEvent.VK_A);
				addMenuItem.addActionListener(treePane);
				fileMenu.add(addMenuItem);
				
				// File->Del, D - Mnemonic
				JMenuItem delMenuItem = new JMenuItem(Del, KeyEvent.VK_D);
				delMenuItem.addActionListener(treePane);
				fileMenu.add(delMenuItem);
				
		        // Separator
		        fileMenu.addSeparator();
		        
				// File->Command, M - Mnemonic
				JMenuItem commandMenuItem = new JMenuItem(Command, KeyEvent.VK_M);
				commandMenuItem.addActionListener(treePane);
				fileMenu.add(commandMenuItem);
				
				// File->Shrink, K - Mnemonic
				JMenuItem shrinkMenuItem = new JMenuItem(Shrink, KeyEvent.VK_M);
				shrinkMenuItem.addActionListener(treePane);
				fileMenu.add(shrinkMenuItem);
				
		        // Separator
		        fileMenu.addSeparator();
				
		        // File->Close, C - Mnemonic
		        JMenuItem closeMenuItem = new JMenuItem(CLOSE, KeyEvent.VK_C);
		        closeMenuItem.addActionListener(treePane);
		        fileMenu.add(closeMenuItem);

				// UIManager.put("Tree.leafIcon", new
				// ImageIcon("leafIcon.png"));
				// UIManager.put("Tree.closedIcon", new
				// ImageIcon("package.png"));
				// UIManager.put("Tree.openIcon", new ImageIcon("package.png"));
				UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
				UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JPanel jp = new JPanel(new GridBagLayout());
		        addComponent(jp, treePane, 0, 0, 1, 0,
		                GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		        
		        ListPane listPane = new ListPane();
		        listPane.setVisible(true);
		        treePane.addSmaliEntryChangedListener(listPane);
		        
		        JText textPane = new JText();
		        textPane.setVisible(true);
		        
		        treePane.addSmaliEntryChangedListener(textPane);
		        listPane.setSmaliMemberChangedListener(textPane);

		        addComponent(jp, listPane, 1, 0, 1, 0,
		                GridBagConstraints.CENTER, GridBagConstraints.BOTH);
		        
		        addComponent(jp, textPane, 2, 0, 2, 0,
		                GridBagConstraints.CENTER, GridBagConstraints.BOTH);
				
				frame.add(jp, BorderLayout.CENTER);
//
		        frame.add(menuBar, BorderLayout.NORTH);

				frame.setSize(600, 600);
				frame.setLocation(650, 20);
				frame.setVisible(true);
			}
		};
		
		
		EventQueue.invokeLater(runner);
	}
	
	private static final Insets insets = new Insets(0,0,0,0);
	
	  private static void addComponent(Container container, Component component,
		      int gridx, int gridy, int gridwidth, int gridheight, int anchor,
		      int fill) {
		    GridBagConstraints gbc = new GridBagConstraints(gridx, gridy,
		      gridwidth, gridheight, 1.0, 1.0, anchor, fill, insets, 0, 0);
		    container.add(component, gbc);
		  }

}
