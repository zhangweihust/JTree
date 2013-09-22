package com.zhangwei.ui.jlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
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
import com.zhangwei.ui.jtree.TreePane;

public class ListPane extends JPanel implements ActionListener, SmaliEntryChanged, ListSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5157608841700640203L;

    private static final String ACTION_KEY = "theJListAction";
	
	private JList<CommonEntry> jlist;
    private SmaliListDataModel listmodel;
    
    private SmaliEntryChanged JTextDataNotify;
    private SmaliMemberChanged JSmaliMemberNotify;

	private ListSelectionModel listSelectionModel;
	
	private CommonEntry selectedCommonEntry;
    
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
        selectedCommonEntry = null;
        
        Action actionListener = new AbstractAction() {
            /**
			 * 
			 */
			private static final long serialVersionUID = -2993268334156314079L;

			public void actionPerformed(ActionEvent actionEvent) {

				int i=0;
				i++;
				JList<CommonEntry> source = (JList<CommonEntry>)actionEvent.getSource();
			  
			  
			  if(selectedCommonEntry!=null){
				  String s = null;
				  if(selectedCommonEntry.type==1){ //head
		              s = (String)JOptionPane.showInputDialog(
		                      ListPane.this,
		                      "Rename class head: " + selectedCommonEntry.id,
		                      "Rename",
		                      JOptionPane.PLAIN_MESSAGE,
		                      null,
		                      null,
		                      selectedCommonEntry.id);
				  }else if(selectedCommonEntry.type==2){ //field
		              s = (String)JOptionPane.showInputDialog(
		                      ListPane.this,
		                      "Rename class field" + selectedCommonEntry.id,
		                      "Rename",
		                      JOptionPane.PLAIN_MESSAGE,
		                      null,
		                      null,
		                      selectedCommonEntry.id);
				  }else if(selectedCommonEntry.type==3){ //method
		              s = (String)JOptionPane.showInputDialog(
		                      ListPane.this,
		                      "Rename class method" + selectedCommonEntry.id,
		                      "Rename",
		                      JOptionPane.PLAIN_MESSAGE,
		                      null,
		                      null,
		                      selectedCommonEntry.id);
				  }
				  

	              System.out.println("Activated: after: " + s);
			  }
/*              SmaliEntry item = (SmaliEntry)source.getLeadSelectionPath().getLastPathComponent();
              String rename_item_str = item.file.getName();
              if(item.isFile()){
            	  rename_item_str = rename_item_str.replace(".smali", "");
              }
              
              String s = (String)JOptionPane.showInputDialog(
                      ListPane.this,
                      item.isFile()?"Rename class":"Rename package" + rename_item_str,
                      "Rename",
                      JOptionPane.PLAIN_MESSAGE,
                      null,
                      null,
                      rename_item_str);
              System.out.println("Activated: after: " + s);*/
            }
          };
        
        KeyStroke F2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0 , true);
        ActionMap actionMap = jlist.getActionMap();
        actionMap.put(ACTION_KEY, actionListener);
        
        InputMap inputMap = jlist.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(F2, ACTION_KEY);
        jlist.setActionMap(actionMap);
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
            			selectedCommonEntry = item;
            			System.out.println("id:" + item.id + ", offset:" + item.offset);
            			if(JSmaliMemberNotify!=null){
            				JSmaliMemberNotify.MemberChanged(item.offset);
            			}
                    }
                }
            }else{
            	selectedCommonEntry = null;
            }
			

		}

	}
}
