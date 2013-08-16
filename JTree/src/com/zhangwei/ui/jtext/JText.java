package com.zhangwei.ui.jtext;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.Document;


import com.zhangwei.smali.api.SmaliEntry;
import com.zhangwei.ui.jlist.SmaliEntryChanged;

public class JText extends JPanel implements SmaliEntryChanged{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8123960294134045973L;
	
	private JTextField jtextField;
	private SmaliDocument doc;
	
	public JText() {
    	super(new BorderLayout());
		jtextField = new JTextField();
		doc = new SmaliDocument();
		
		jtextField.setDocument(doc);
		
        add(new JScrollPane(jtextField));
	}

	@Override
	public void EntryChanged(SmaliEntry newEntry) {
		// TODO Auto-generated method stub
		jtextField.setText(newEntry.getFileContent());
	}
	
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

}
