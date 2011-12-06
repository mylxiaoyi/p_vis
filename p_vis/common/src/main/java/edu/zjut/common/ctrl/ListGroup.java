package edu.zjut.common.ctrl;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * ��ͬJList֮���ѡ�񻥳�, ����ButtonGroup�Ĺ���
 * 
 * @author yulewei
 * 
 * @see javax.swing.ButtonGroup
 */
public class ListGroup implements ListSelectionListener {
	ArrayList<JList> lists = new ArrayList<JList>();

	public void add(JList l) {
		lists.add(l);
		l.addListSelectionListener(this);
	}

	public void valueChanged(ListSelectionEvent e) {
		JList src = (JList) e.getSource();
		if (src.isSelectionEmpty())
			return;

		for (JList l : lists) {
			if (l != src)
				l.clearSelection();
		}
	}
}