package edu.zjut.map.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.vividsolutions.jts.geom.Point;

/**
 * ͼ��ο����ѷ���
 * 
 * @author yulewei
 * 
 */
public class IconMarker extends DefaultMapMarker {

	public static final int PURPLE = 0; // z, ��ɫ
	public static final int PINK = 1; // f, ��ɫ
	public static final int BLUE = 2;// l, ��ɫ
	public static final int GREEN = 3; // n, ��ɫ
	public static final int YELLOW = 4; // h, ��ɫ
	public static final int BROWN = 5; // y, ��ɫ
	private static String[] colors = { "z", "f", "l", "n", "h", "y" };
	private static BufferedImage[] left;
	private static BufferedImage[] middle;
	private static BufferedImage[] right;
	private static BufferedImage[] jt;

	static {
		try {
			left = new BufferedImage[colors.length];
			middle = new BufferedImage[colors.length];
			right = new BufferedImage[colors.length];
			jt = new BufferedImage[colors.length];

			for (int i = 0; i < colors.length; i++) {
				left[i] = ImageIO.read(IconMarker.class.getResource(String
						.format("resources/%s_left.png", colors[i])));
				middle[i] = ImageIO.read(IconMarker.class.getResource(String
						.format("resources/%s_middle.png", colors[i])));
				right[i] = ImageIO.read(IconMarker.class.getResource(String
						.format("resources/%s_right.png", colors[i])));
				jt[i] = ImageIO.read(IconMarker.class.getResource(String
						.format("resources/%s_jt.png", colors[i])));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int iconColor = BLUE;
	private int textWidth;

	public IconMarker(Point coord, String title) {
		super(coord, title);
	}

	public IconMarker(GeoPosition coord, String title) {
		super(coord, title);
	}

	public void paintOverlay(Graphics2D g, JXMapViewer map) {
		if (isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		drawIcon(g, pt, iconColor);
	}

	@Override
	public void paintHighlightOverlay(Graphics2D g, JXMapViewer map) {
		if (!isHighlighted)
			return;

		Point2D pt = GeoUtils.getScreenCoord(map, point);
		drawIcon(g, pt, YELLOW);
	}

	protected void drawIcon(Graphics2D g, Point2D pt, int iconColor) {
		g.translate(pt.getX(), pt.getY());

		Font f = g.getFont();
		FontRenderContext frc = g.getFontRenderContext();
		Rectangle2D rec = f.getStringBounds(title, frc);

		int gap = 8;
		textWidth = (int) rec.getWidth() + gap * 2;

		int h = left[iconColor].getHeight() + jt[iconColor].getHeight() - 5;
		int w = left[iconColor].getWidth();

		g.drawImage(left[iconColor], 0, -h, null);
		g.drawImage(right[iconColor], w + textWidth, -h, null);

		int iw = middle[iconColor].getWidth();
		int n = textWidth / iw + 1;
		for (int i = 0; i < n; i++)
			g.drawImage(middle[iconColor], w + iw * i, -h, null);

		g.drawImage(jt[iconColor], 1, -jt[iconColor].getHeight(), null);

		g.setPaint(Color.WHITE);

		g.drawString(title, w + gap, -jt[iconColor].getHeight() - 5);
	}

	@Override
	public boolean contains(JXMapViewer map, int x, int y) {
		Point2D pt = GeoUtils.getScreenCoord(map, point);

		return (x - pt.getX()) > 10 && (x - pt.getX()) < 10 + textWidth
				&& (pt.getY() - y) > 15 && (pt.getY() - y) < 40;
	}
}
