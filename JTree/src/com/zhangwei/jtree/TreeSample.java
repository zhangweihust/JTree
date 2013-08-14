package com.zhangwei.jtree;
import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.*;

public class TreeSample {
  public static void main(final String args[]) {
	  char a = '-';
	  System.out.print("-:" + (int)a);
	//  MyParser.Paser_dir(args[0]);
/*    Runnable runner = new Runnable() {
      public void run() {
        String title = ("JTree Sample");
        JFrame frame = new JFrame(title);

        //UIManager.put("Tree.leafIcon", new ImageIcon("leafIcon.png"));
        //UIManager.put("Tree.closedIcon", new ImageIcon("package.png"));
        //UIManager.put("Tree.openIcon", new ImageIcon("package.png"));
        UIManager.put("Tree.collapsedIcon", new ImageIcon("collapsedIcon.png"));
        UIManager.put("Tree.expandedIcon", new ImageIcon("expandedIcon.png"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        EntryVector<String, Object> entryVector = DirLoader.getInstance().Load(args[0]);

        JTree tree = new JTree(entryVector);
        //JTree tree = new JTree();
        tree.setRootVisible(true);
        
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)tree.getCellRenderer();
        
        renderer.setLeafIcon(new ImageIcon("leafIcon.png"));
        renderer.setOpenIcon(new ImageIcon("package.png"));
        renderer.setClosedIcon(new ImageIcon("package.png"));


        JScrollPane scrollPane = new JScrollPane(tree);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(300, 150);
        frame.setVisible(true);
      }
    };
    EventQueue.invokeLater(runner);*/
  }
}

