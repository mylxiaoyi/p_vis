package edu.zjut.chart.axis;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import processing.core.PApplet;
import processing.core.PConstants;

public class NumberAxis extends Axis {

	private NumberFormat numberFormat = new DecimalFormat("0");

	float dataMin, dataMax;

	public NumberAxis(PApplet p) {
		super(p);
	}

	public void setNumberFormat(NumberFormat formatter) {
		this.numberFormat = formatter;
	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public void axisData(float dataMin, float dataMax) {
		this.dataMax = dataMax;
		this.dataMin = dataMin;
	}

	/**
	 * ��������Ӧ��������
	 */
	@Override
	protected void adjustIntervalHor() {
		// ��С���10����
		interMin = intervalMinor;
		while (true) {
			float w = PApplet.map(interMin + dataMin, dataMin, dataMax, plotX1,
					plotX2) - plotX1;
			if (w < 10)
				interMin *= 2;
			else
				break;
		}

		// ��ֹ�����ı��໥����
		inter = interval;
		float wmin = p.textWidth(numberFormat.format(dataMin));
		float wmax = p.textWidth(numberFormat.format(dataMax));
		wmax = wmax > wmin ? wmax : wmin;
		wmax += 3;
		for (int i = 1;; i++) {
			float w = PApplet.map(inter + dataMin, dataMin, dataMax, plotX1,
					plotX2) - plotX1;
			if (w < wmax)
				inter = interMin * i;
			else
				break;
		}
	}

	@Override
	protected void adjustIntervalVer() {
		// ��С���10����
		interMin = intervalMinor;
		while (true) {
			float w = plotY2
					- PApplet.map(interMin + dataMin, dataMin, dataMax, plotY2,
							plotY1);
			if (w < 10)
				interMin *= 2;
			else
				break;
		}

		// ��ֹ�����ı��໥����
		inter = interval;
		float wmax = p.textAscent() + 5;
		for (int i = 1;; i++) {
			float w = plotY2
					- PApplet.map(inter + dataMin, dataMin, dataMax, plotY2,
							plotY1);
			if (w < wmax)
				inter = interMin * i;
			else
				break;
		}
	}

	/**
	 * ����ˮƽ��
	 */
	protected void drawHor() {
		for (float v = dataMin; v <= dataMax; v += interMin) {
			// If a tick mark
			float x = PApplet.map(v, dataMin, dataMax, plotX1, plotX2);
			if (v % inter == 0) {
				// Draw major tick
				p.textAlign(PConstants.CENTER);
				p.stroke(axisColor);
				p.text(numberFormat.format(v), x, plotY2 + p.textAscent() + 10);
				p.line(x, plotY2 + 4, x, plotY2);

			} else {
				// Draw minor tick
				p.stroke(128);
				p.line(x, plotY2 + 2, x, plotY2);
			}
		}
	}

	/**
	 * ���ƴ�ֱ��
	 */
	protected void drawVer() {
		for (float v = dataMin; v <= dataMax; v += interMin) {
			// If a tick mark
			float y = PApplet.map(v, dataMin, dataMax, plotY2, plotY1);
			if (v % inter == 0) {
				float textOffset = p.textAscent() / 2;
				if (v == dataMin) {
					textOffset = 0;
				} else if (v == dataMax) {
					textOffset = p.textAscent();
				}

				// Draw major tick
				p.textAlign(PConstants.RIGHT);
				p.stroke(axisColor);
				p.text(numberFormat.format(v), plotX1 - 6, y + textOffset);
				p.line(plotX1 - 4, y, plotX1, y);

			} else {
				// Draw minor tick
				p.stroke(128);
				p.line(plotX1 - 2, y, plotX1, y);
			}
		}
	}

	/**
	 * ������
	 */
	protected void drawGrid() {
		p.stroke(gridColor);
		for (float v = dataMin + interMin; v <= dataMax; v += interMin) {
			if (v % interMin == 0) {
				if (orient == Orientation.HORIZONTAL) {
					float x = PApplet.map(v, dataMin, dataMax, plotX1, plotX2);
					p.line(x, plotY1, x, plotY2);
				}
				if (orient == Orientation.VERTICAL) {
					float y = PApplet.map(v, dataMin, dataMax, plotY2, plotY1);
					p.line(plotX1, y, plotX2, y);
				}
			}
		}
	}
}
