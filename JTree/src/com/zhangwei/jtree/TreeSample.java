package com.zhangwei.jtree;

import javax.swing.*;
import javax.swing.filechooser.FileView;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.zhangwei.ui.JavaFileView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

public class TreeSample {
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

				TreePane testpane = new TreePane();

				JMenuBar menuBar = new JMenuBar();
				JMenu fileMenu = new JMenu("File");
				fileMenu.setLayout(new GridLayout(3, 3));
				fileMenu.setMnemonic(KeyEvent.VK_F);
				menuBar.add(fileMenu);

				// File->Open, O - Mnemonic
				JMenuItem openMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
				openMenuItem.addActionListener(testpane);
				fileMenu.add(openMenuItem);
				
		        // Separator
		        fileMenu.addSeparator();
				
		        // File->Close, C - Mnemonic
		        JMenuItem closeMenuItem = new JMenuItem("Close", KeyEvent.VK_C);
		        closeMenuItem.addActionListener(testpane);
		        fileMenu.add(closeMenuItem);

				// UIManager.put("Tree.leafIcon", new
				// ImageIcon("leafIcon.png"));
				// UIManager.put("Tree.closedIcon", new
				// ImageIcon("package.png"));
				// UIManager.put("Tree.openIcon", new ImageIcon("package.png"));
				UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
				UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
				//tree = new JTree(entryVector);
				//tree.setRootVisible(true);


				//DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();

				//renderer.setLeafIcon(new ImageIcon("leafIcon.png"));
				//renderer.setOpenIcon(new ImageIcon("package.png"));
				//renderer.setClosedIcon(new ImageIcon("package.png"));

				//JScrollPane scrollPane = new JScrollPane(tree);

				//frame.add(scrollPane, BorderLayout.CENTER);
				frame.add(testpane, BorderLayout.CENTER);
//
		        frame.add(menuBar, BorderLayout.NORTH);

				frame.setSize(600, 600);
				frame.setLocation(650, 20);
				frame.setVisible(true);
			}
		};
		
		
		EventQueue.invokeLater(runner);
	}

}
