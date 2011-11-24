package edu.zjut.common.ctrl;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.ListModel;

public class FieldList<E> extends JList<E> {

	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 3;

	private FieldListCell<E> cellRenderer;
	private boolean isHorizontal = false;
	private int listHeight;

	public FieldList() {
		init();
	}

	public FieldList(ListModel<E> dataModel) {
		super(dataModel);
		init();
	}

	public FieldList(final E[] listData) {
		super(listData);
		init();
	}

	private void init() {
		this.cellRenderer = new FieldListCell<E>();
		this.setCellRenderer(cellRenderer);
		this.setPreferredSize(new Dimension(100, 25));
	}

	public void setLayoutOrientation(int layoutOrientation) {
		if (layoutOrientation == HORIZONTAL) {
			this.setVisibleRowCount(1);
			this.setFixedCellWidth(120);
			this.setFixedCellHeight(listHeight);
			isHorizontal = true;

			super.setLayoutOrientation(HORIZONTAL_WRAP);
		} else {
			this.setVisibleRowCount(10);
			this.setFixedCellHeight(25);
			isHorizontal = false;
			super.setLayoutOrientation(layoutOrientation);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (listHeight != this.getHeight()) {
			listHeight = this.getHeight();

			if (listHeight < 35)
				this.setLayoutOrientation(HORIZONTAL);
			else if (listHeight > 50)
				this.setLayoutOrientation(VERTICAL);

			if (isHorizontal)
				this.setFixedCellHeight(listHeight);
		}

		super.paintComponent(g);
	}
}
