package edu.zjut.vis.time;

import java.util.ArrayList;
import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import edu.zjut.chart.plot.TimeSeriesPlot;
import edu.zjut.color.Legend;
import edu.zjut.color.LegendListener;
import edu.zjut.common.data.time.TimePeriod;
import edu.zjut.common.data.time.TimeSeriesCollection;
import edu.zjut.common.data.time.TimeType;

public class PTimeSeries extends PApplet implements LegendListener {

	String title;
	Legend legend;
	int[] colorArr;
	ColourTable cTable;

	float titleHeight = 50;
	float legendHeight = 50;
	private boolean isShowTitle = false;
	private boolean isShowLegend = true;

	DraggableRect cover;
	TimeSeriesPlot overviewPlot;
	List<TimeSeriesPlot> detailPlotList;
	List<Integer> weightList;

	float overviewWeight = 0.6f;

	public PTimeSeries() {
		this.detailPlotList = new ArrayList<TimeSeriesPlot>();
		this.weightList = new ArrayList<Integer>();
	}

	public void setSeries(List<TimeSeriesPlot> plots) {
		setOverviewPlot(plots.get(0));

		detailPlotList.clear();
		weightList.clear();

		setDetailPlots(plots.subList(1, plots.size()));
	}

	public void setOverviewPlot(TimeSeriesPlot subplot) {
		this.overviewPlot = subplot;
		initColor(overviewPlot.getSeries());

		int range = overviewPlot.getSeries().getTimeRange();
		this.cover = new DraggableRect(this, 0, range - 1);
	}

	public TimeSeriesPlot getOverviewPlot() {
		return overviewPlot;
	}

	public void setDetailPlots(List<TimeSeriesPlot> detailPlotList) {
		this.detailPlotList = detailPlotList;
		weightList.clear();
		for (int i = 0; i < detailPlotList.size(); i++) {
			weightList.add(1);
		}

		for (TimeSeriesPlot plot : detailPlotList)
			plot.setColors(colorArr);
	}

	public List<TimeSeriesPlot> getDetailPlots() {
		return detailPlotList;
	}

	public void setDetailPlot(int index, TimeSeriesPlot detailPlot) {
		detailPlot.setColors(colorArr);
		detailPlotList.set(index, detailPlot);
	}

	public void setOverviewRange(int left, int right) {
		cover.setLeftIndex(left);
		cover.setRightIndex(right);
	}

	public int[] getOverviewRange() {
		return new int[] { cover.getLeftIndex(), cover.getRightIndex() };
	}

	/**
	 * ��Ӷ��detailPlot, ��С(�߶�)��Ȩ�ؼ���
	 * 
	 * @param subplot
	 * @param weight
	 */
	public void addDetailPlot(TimeSeriesPlot subplot, int weight) {
		detailPlotList.add(subplot);
		weightList.add(weight);
	}

	public void removeDetailPlot(int index) {
		detailPlotList.remove(index);
		weightList.remove(index);
	}

	/**
	 * TODO legend��θĽ�, ��ɫ��ο���
	 * 
	 * @param series
	 */
	protected void initColor(TimeSeriesCollection series) {
		cTable = ColourTable.getPresetColourTable(ColourTable.SET3_8);
		legend = new Legend(this, series.getNames(), cTable);
		legend.addLegendListener(this);

		int size = series.seriesSize();
		colorArr = new int[size];
		for (int i = 0; i < size; i++) {
			colorArr[i] = cTable.findColour(i + 1);
		}
	}

	/**
	 * ��ɫ����
	 */
	public void setColors(int[] colorArr) {
		overviewPlot.setColors(colorArr);
		for (TimeSeriesPlot plot : detailPlotList)
			plot.setColors(colorArr);
	}

	public void setDrawGrid(boolean isDrawGrid) {
		overviewPlot.setDrawGrid(isDrawGrid);
		for (TimeSeriesPlot plot : detailPlotList)
			plot.setDrawGrid(isDrawGrid);

	}

	public void setup() {
		PFont font = createFont("FFScala", 12);
		textFont(font);
		smooth();
	}

	public void draw() {
		if (overviewPlot == null)
			return;

		background(255);

		float titleHeight = isShowTitle ? this.titleHeight : 0;
		float legendHeight = isShowLegend ? this.legendHeight : 0;
		float gap = 5;
		float plotHeight = height - titleHeight - legendHeight - 2 * gap;
		plotHeight = isShowLegend ? plotHeight - gap : plotHeight;

		// title
		if (isShowTitle) {
			fill(100);
			textAlign(PConstants.CENTER, PConstants.CENTER);
			textSize(18);
			text(title, width / 2, titleHeight / 2);
		}

		textSize(12);

		setColors(colorArr);
		drawPlot(gap, titleHeight + gap, width - 2 * gap, plotHeight);

		// legend
		if (isShowLegend) {
			legend.size(width / 2 - 150, height - legendHeight - gap, 300,
					legendHeight);
			legend.draw();
		}
	}

	public void drawPlot(float x, float y, float width, float height) {
		float sum = overviewWeight;
		for (int w : weightList) {
			sum += w;
		}

		float oh = height * (overviewWeight / sum);

		overviewPlot.size(x, y + height - oh, width, oh);
		overviewPlot.draw();

		float[] rect = overviewPlot.getPlotRect();
		cover.rangeSize(rect[0], rect[2], rect[1], rect[3]);
		cover.draw();

		// ���ƶ��detail plot
		TimePeriod baseTime = overviewPlot.getSeries().getTimeMin();
		TimeType type = overviewPlot.getSeries().getTimeType();
		TimePeriod minTime = baseTime.rollDate(type, cover.getLeftIndex());
		TimePeriod maxTime = baseTime.rollDate(type, cover.getRightIndex());

		float dy = y;
		for (int i = 0; i < detailPlotList.size(); i++) {
			TimeSeriesPlot detailPlot = detailPlotList.get(i);

			float dh = height * (weightList.get(i) / sum);
			detailPlot.size(x, dy, width, dh);
			dy += dh;

			detailPlot.setVisualRange(minTime, maxTime);
			detailPlot.draw();
		}
	}

	@Override
	public void colorChanged() {
		cTable = legend.getColorTable();
		int numColours = cTable.getColourRules().size() - 1;

		colorArr = new int[numColours];
		for (int i = 0; i < numColours; i++) {
			colorArr[i] = cTable.findColour(i + 1);
		}
	}
}
