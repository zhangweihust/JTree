package com.zhangwei.ui.jlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListSelectionModel;
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
import com.zhangwei.ui.jtext.SmaliMemberChanged;
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
    private SmaliMemberChanged JSmaliMemberNotify;

	private ListSelectionModel listSelectionModel;
    
    public ListPane() {
    	super(new BorderLayout());
    	jlist = new JList<CommonEntry>();
    	listmodel = new SmaliListDataModel();
    	
    	listSelectionModel = jlist.getSelectionModel();
    	listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	listSelectionModel.addListSelectionListener(this);
    	
/*    	jlist.setSelectionModel(listSelectionModel);
    	jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);*/
    	jlist.setModel(listmodel);
    	
        add(new JScrollPane(jlist));
    }
    
    public void setSmaliEntryChangedListener(SmaliEntryChanged listener){
    	JTextDataNotify = listener;
    }
    
    public void setSmaliMemberChangedListener(SmaliMemberChanged listener){
    	JSmaliMemberNotify = listener;
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
			
		}else{
			listmodel.ChangeSmaliEntry(null);
		}

		if(JTextDataNotify!=null){
			JTextDataNotify.EntryChanged(newEntry);
		}
		
	}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		if(listmodel!=null && listmodel.getSize()>0){
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
            			CommonEntry item = listmodel.getElementAt(i);
            			System.out.println("id:" + item.id + ", offset:" + item.offset);
            			if(JSmaliMemberNotify!=null){
            				JSmaliMemberNotify.MemberChanged(item.offset);
            			}
                    }
                }
            }
			

		}

	}
}
