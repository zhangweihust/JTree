package com.zhangwei.ui.jlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.zhangwei.smali.api.CommonEntry;
import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.ui.jtree.SmaliTree;
import com.zhangwei.ui.jtree.SmaliTreeModel;

public class ListPane extends JPanel implements ActionListener, SmaliEntryChanged, ListSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5157608841700640203L;
	
	private JList<CommonEntry> jlist;
    private SmaliListDataModel listmodel;
    
    private SmaliEntryChanged JTextDataNotify;
    
    public ListPane() {
    	super(new BorderLayout());
    	jlist = new JList<CommonEntry>();
    	listmodel = new SmaliListDataModel();
    	jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	jlist.setModel(listmodel);
    	
        add(new JScrollPane(jlist));
    }
    
    public void setSmaliEntryChangedListener(SmaliEntryChanged listener){
    	JTextDataNotify = listener;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void EntryChanged(SmaliEntry newEntry) {
		// TODO Auto-generated method stub
		if(newEntry.isFile){
			listmodel.ChangeSmaliEntry(newEntry);
			
			if(JTextDataNotify!=null){
				JTextDataNotify.EntryChanged(newEntry);
			}
			
		}

	}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

	@Override
	public void valueChanged(ListSelectionEvent event) {
		// TODO Auto-generated method stub
		System.out.print(event.toString());
	}
}
