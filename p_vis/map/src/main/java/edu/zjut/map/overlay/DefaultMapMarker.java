package edu.zjut.map.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

/**
 * 
 * @author yulewei
 * 
 */
public class DefaultMapMarker extends MapMarker {

	private static BufferedImage img = null;
	private static BufferedImage highlightImg = null;

	static {
		try {
			img = ImageIO.read(DefaultMapMarker.class
					.getResource("resources/marker_h.png"));
			highlightImg = ImageIO.read(DefaultMapMarker.class
					.getResource("resources/marker_s.png"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ê��. Ĭ������£�ê������Ϊͼ��ĵײ��м�λ�� (������/2, �߶�)
	 */
	protected int[] anchor;

	public DefaultMapMarker(GeoPosition coord) {
		this(coord, "");
	}

	public DefaultMapMarker(Point coord, String title) {
		super(coord, title);
		anchor = new int[] { img.getWidth() / 2, img.getHeight() };
	}

	public DefaultMapMarker(GeoPosition coord, String title) {
		super(coord, title);
		anchor = new int[] { img.getWidth() / 2, img.getHeight() };
	}

	@Override
	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.drawImage(img, (int) pt.getX() - anchor[0], (int) pt.getY()
				- anchor[1], null);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		g.drawImage(highlightImg, (int) pt.getX() - anchor[0], (int) pt.getY()
				- anchor[1], null);

		int r = 5;
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2.0f));
		g.translate(pt.getX(), pt.getY());
		g.drawLine(-r, -r, +r, +r);
		g.drawLine(-r, +r, +r, -r);
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		Point2D pt = GeoUtils.getScreenCoord(map, point);

		return Math.abs(pt.getX() - x) < 10 && (pt.getY() - y) > 0
				&& (pt.getY() - y) < 34;
	}
}
