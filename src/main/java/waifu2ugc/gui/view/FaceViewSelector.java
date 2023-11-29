package waifu2ugc.gui.view;

import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

class FaceViewSelector
{
	private float alpha;
	private Color color;
	private float thickness;

	private float decorationThickness;
	private Color decorationColor;

	private int animationCap;
	private int animationJoin;
	private float animationMiterLimit;
	private float animationIncrement;

	private float[] animationDash;

	private float dashPhase;

	FaceViewSelector() {
		loadDefaults();
	}

	float getAlpha() { return alpha; }
	void setAlpha(float alpha) { this.alpha = alpha; }

	Color getColor() { return color; }
	void setColor(Color color) { this.color = color; }

	float getThickness() { return thickness; }
	void setThickness(float thickness) { this.thickness = thickness; }

	float getDecorationThickness() { return decorationThickness; }
	void setDecorationThickness(float decorationThickness) { this.decorationThickness = decorationThickness; }

	Color getDecorationColor() { return decorationColor; }
	void setDecorationColor(Color decoration) { this.decorationColor = decoration; }

	void incrementAnimation() { dashPhase += animationIncrement; }
	void resetAnimation() { dashPhase = 0.0f; }

	void draw(Graphics2D g2d, Rectangle selectorRect, boolean decorate) {
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		if (decorate)
		{
			drawDecorated(g2d, selectorRect);
		}
		else
		{
			drawAnimated(g2d, selectorRect);
		}
	}

	private void drawDecorated(Graphics2D g2d, Rectangle selectorRect) {
		g2d.setStroke(new BasicStroke(decorationThickness));

		Rectangle outer = new Rectangle(selectorRect);
		outer.x += decorationThickness / 2;
		outer.y += decorationThickness / 2;
		outer.width -= decorationThickness;
		outer.height -= decorationThickness;

		g2d.setColor(color);
		g2d.drawRect(outer.x, outer.y, outer.width, outer.height);

		Rectangle decoration = new Rectangle(outer);
		decoration.x += decorationThickness;
		decoration.y += decorationThickness;
		decoration.width -= decorationThickness * 2;
		decoration.height -= decorationThickness * 2;

		g2d.setColor(decorationColor);
		g2d.drawRect(decoration.x, decoration.y, decoration.width, decoration.height);

		Rectangle inner = new Rectangle(decoration);
		inner.x += decorationThickness;
		inner.y += decorationThickness;
		inner.width -= decorationThickness * 2;
		inner.height -= decorationThickness * 2;

		g2d.setColor(color);
		g2d.drawRect(inner.x, inner.y, inner.width, inner.height);
	}

	private void drawAnimated(Graphics2D g2d, Rectangle selectorRect) {
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(thickness,
		                              animationCap, animationJoin, animationMiterLimit,
		                              animationDash, dashPhase));
		g2d.drawRect(selectorRect.x, selectorRect.y, selectorRect.width, selectorRect.height);
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		alpha = options.getFloat("alpha").orElse(0.90f);
		color = options.getColor("color").orElse(Color.BLACK);
		thickness = options.getFloat("thickness").orElse(2.00f);

		decorationColor = options.getColor("decorationColor").orElse(Color.DARK_GRAY);
		decorationThickness = options.getFloat("decorationThickness").orElse(1.00f);

		animationCap = options.getInt("animationCap").orElse(BasicStroke.CAP_SQUARE);
		animationJoin = options.getInt("animationJoin").orElse(BasicStroke.JOIN_MITER);
		animationMiterLimit = options.getFloat("animationMiterLimit").orElse(1.00f);
		animationIncrement = options.getFloat("animationIncrement").orElse(2.00f);

		String[] dashes = options.getString("animationDash").orElse("4.00f,8.00f").split(",");

		animationDash = new float[dashes.length];

		for (int i = 0; i < dashes.length; ++i)
		{
			animationDash[i] = Float.parseFloat(dashes[i]);
		}
	}
}
