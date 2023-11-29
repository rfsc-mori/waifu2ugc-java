package waifu2ugc.gui.view;

import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class FaceViewGrid
{
	private float alpha;
	private Color color;
	private float thickness;

	private int margin;

	FaceViewGrid() {
		loadDefaults();
	}

	float getAlpha() { return alpha; }
	void setAlpha(float alpha) { this.alpha = alpha; }

	Color getColor() { return color; }
	void setColor(Color color) { this.color = color; }

	float getThickness() { return thickness; }
	void setThickness(float thickness) { this.thickness = thickness; }

	int getMargin() { return margin; }
	void setMargin(int margin) { this.margin = margin; }

	void draw(Graphics2D g2d, Rectangle gridRect, List<Point> hlines, List<Point> vlines) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.setStroke(new BasicStroke(thickness));
		g2d.setColor(color);

		hlines.forEach(line -> g2d.drawLine(line.x + margin, line.y, line.x + gridRect.width - margin, line.y));
		vlines.forEach(line -> g2d.drawLine(line.x, line.y + margin, line.x, line.y + gridRect.height - margin));
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		alpha = options.getFloat("alpha").orElse(0.40f);
		color = options.getColor("color").orElse(Color.BLACK);
		thickness = options.getFloat("thickness").orElse(1.00f);
		margin = options.getInt("margin").orElse(3);
	}
}
