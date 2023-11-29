package waifu2ugc.gui.view;

import waifu2ugc.image.ImageWrapper;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

class FaceViewBackground
{
	private ImageWrapper pattern = new ImageWrapper();
	private TexturePaint paint;

	private float alpha;
	private Color color;

	FaceViewBackground() {
		loadDefaults();
	}

	ImageWrapper getPattern() { return new ImageWrapper(pattern); }

	void setPattern(ImageWrapper pattern) {
		this.pattern.setImage(pattern);
		updatePaint();
	}

	void updatePaint() { paint = new TexturePaint(pattern.getImage(), pattern.getRect()); }

	float getAlpha() { return alpha; }
	void setAlpha(float alpha) { this.alpha = alpha; }

	Color getColor() { return color; }
	void setColor(Color color) { this.color = color; }

	void draw(Graphics2D g2d, Rectangle where) {
		draw(g2d, Collections.singletonList(where));
	}

	void draw(Graphics2D g2d, List<Rectangle> where) {
		if (pattern.hasImage())
		{
			g2d.setPaint(paint);
		}
		else
		{
			g2d.setColor(color);
		}

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

		where.forEach(rect -> {
			if ((rect.width > 0) && (rect.height > 0))
			{
				g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
		});
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		alpha = options.getFloat("alpha").orElse(0.75f);
		color = options.getColor("color").orElse(Color.DARK_GRAY);

		String pattern = options.getString("pattern").orElse("/images/background.png");

		try (InputStream resource = FaceViewBackground.class.getResourceAsStream(pattern))
		{
			assert (resource != null) : "resource == null.";
			setPattern(new ImageWrapper(resource));
		}
		catch (IOException exception)
		{
			// Handled: Ignore and continue.
			exception.printStackTrace();
		}
	}
}
