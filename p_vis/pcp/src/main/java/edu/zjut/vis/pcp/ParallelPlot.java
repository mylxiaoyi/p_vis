package edu.zjut.vis.pcp;

import java.util.List;

import javax.swing.TransferHandler;

import org.mediavirus.parvis.gui.ParallelDisplay;

import edu.zjut.common.ctrl.FieldImporter;
import edu.zjut.common.ctrl.FieldComponent.ColorEnum;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.attr.AttributeData;
import edu.zjut.common.data.attr.DataField;
import edu.zjut.common.data.attr.MeasureField;
import edu.zjut.common.event.DataSetEvent;
import edu.zjut.common.event.DataSetListener;
import edu.zjut.common.event.IndicationEvent;
import edu.zjut.common.event.IndicationListener;
import edu.zjut.common.event.SelectionEvent;
import edu.zjut.common.event.SelectionListener;
import edu.zjut.common.event.SubspaceEvent;
import edu.zjut.common.event.SubspaceListener;

/**
 * ��ParvisPlot������¼�Эͬ��װ
 * 
 * @author yulewei
 */
public class ParallelPlot extends ParvisPlot implements DataSetListener,
		SubspaceListener, IndicationListener, SelectionListener {

	private final int MAX_AXES = 6;

	int[] savedSelection;
	private DataSetForApps dataSet;
	private AttributeData attrData;

	public ParallelPlot() {
		parallelDisplay.addIndicationListener(this);
		parallelDisplay.addSelectionListener(this);

		parallelDisplay.setTransferHandler(new PCPFieldImporter());
	}

	@Override
	public void dataSetChanged(DataSetEvent e) {
		dataSet = e.getDataSetForApps();
		attrData = dataSet.getAttrData();

		int nVars = attrData.getNumMeasures();
		int min = nVars < MAX_AXES ? nVars : MAX_AXES;
		int[] vars = new int[min];
		for (int i = 0; i < min; i++) {
			vars[i] = i;
		}
		setSubspace(vars);
	}

	@Override
	public void subspaceChanged(SubspaceEvent e) {
		int[] selectedVars = e.getSubspace();
		setSubspace(selectedVars);
	}

	private void setSubspace(int[] vars) {
		MeasureField[] measureFeilds = attrData.getMeasureFields();

		int nNumeric = measureFeilds.length;
		int nVars = Math.min(vars.length, Math.min(nNumeric, MAX_AXES));

		// �����Ӽ�
		DataField[] feilds = new DataField[nVars + 1];
		feilds[0] = attrData.getObservationField();
		for (int i = 0; i < nVars; i++) {
			feilds[i + 1] = measureFeilds[vars[i]];
		}

		AttributeData subAttrData = new AttributeData(feilds,
				attrData.getColumnArrays());
		this.setDataSet(new DataSetForApps(subAttrData, null, null));

		// �õ�һ��ά����ɫ
		colorField = subAttrData.getMeasureFields()[0];
		double[] values = colorField.getColumnAsDouble();
		colorFieldComp.setValue(colorField);
		colorFieldComp.setColor(ColorEnum.WHITE);
		setColor(values, colorField.getColorTable());
	}

	public void setDataSet(DataSetForApps dataSet) {
		DataSetSpaceModel model = new DataSetSpaceModel(dataSet);
		model.addProgressListener(this);
		model.readContents();
		setModel(model);
	}

	@Override
	public void indicationChanged(IndicationEvent e) {
		parallelDisplay.indicationChanged(e);
	}

	@Override
	public void selectionChanged(SelectionEvent e) {
		savedSelection = e.getSelection();

		if (getWidth() * getHeight() <= 0) {
			logger.info("ParallelPlot got selection when size was zero");
			return;
		}
		parallelDisplay.selectionChanged(e);
		parallelDisplay.setEditMode(ParallelDisplay.BRUSH);
	}

	@Override
	public SelectionEvent getSelectionEvent() {
		return new SelectionEvent(this, parallelDisplay.savedSelection);
	}

	public void addIndicationListener(IndicationListener l) {
		parallelDisplay.addIndicationListener(l);
	}

	public void removeIndicationListener(IndicationListener l) {
		parallelDisplay.removeIndicationListener(l);
	}

	public void addSelectionListener(SelectionListener l) {
		parallelDisplay.addSelectionListener(l);
	}

	public void removeSelectionListener(SelectionListener l) {
		parallelDisplay.removeSelectionListener(l);
	}

	public void fireIndicationChanged(int newIndication) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		IndicationEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IndicationListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new IndicationEvent(this, newIndication);
				}
				((IndicationListener) listeners[i + 1]).indicationChanged(e);
			}
		}// next i
	}

	class PCPFieldImporter extends FieldImporter<MeasureField> {

		public PCPFieldImporter() {
			super(MeasureField.class);
		}

		public boolean importData(TransferHandler.TransferSupport support) {
			getTransferData(support);

			List<MeasureField> values = data.getValues();
			int[] varsIndex = new int[values.size()];

			MeasureField[] measureFeilds = attrData.getMeasureFields();
			for (int i = 0; i < values.size(); i++) {
				int index = -1;
				for (int k = 0; k < measureFeilds.length; k++) {
					if (measureFeilds[k].getName().equals(
							values.get(i).getName())) {
						index = k;
						break;
					}
				}
				varsIndex[i] = index;
			}

			setSubspace(varsIndex);

			return true;
		}
	}
}
