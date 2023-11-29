package waifu2ugc.gui.view;

import com.twelvemonkeys.image.ResampleOp;

import waifu2ugc.image.ImageDimension;
import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.image.ScaledImagePair;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

class TemplateViewFace
{
	private final TemplateView parent;

	private TemplateFace face;
	private final ScaledImagePair faceCache = new ScaledImagePair();

	private float alpha;
	private float highlightThickness;

	private Color textColor;
	private Color faceColor;
	private Color highlightColor;

	TemplateViewFace(TemplateView parent) {
		this.parent = parent;
	}

	TemplateFace getFace() { return face; }
	FaceIndex getIndex() { return (face != null) ? face.getIndex() : FaceIndex.INVALID; }

	void setFace(TemplateFace face) {
		assert (face.getIndex() != FaceIndex.INVALID) : "An INVALID face should not be created.";

		loadDefaults(face.getIndex());
		this.face = face;
	}

	float getAlpha() { return alpha; }
	void setAlpha(float alpha) { this.alpha = alpha; }

	float getHighlightThickness() { return highlightThickness; }
	void setHighlightThickness(float thickness) { this.highlightThickness = thickness; }

	Color getTextColor() { return textColor; }
	void setTextColor(Color color) { this.textColor = color; }

	Color getFaceColor() { return faceColor; }
	void setFaceColor(Color color) { this.faceColor = color; }

	Color getHighlightColor() { return highlightColor; }
	void setHighlightColor(Color color) { this.highlightColor = color; }

	void setQuality(int quality) { faceCache.setQuality(quality); }

	Rectangle getFaceRect() {
		Rectangle templateRect = parent.getTemplateRect();

		Rectangle faceRect = parent.scaleToView(face.getRect());
		faceRect.translate(templateRect.x, templateRect.y);

		return faceRect;
	}

	ImageDimension getFaceSize() {
		return new ImageDimension(getFaceRect());
	}

	void draw(Graphics2D g2d, Rectangle clip) {
		if ((face != null) && face.isEnabled() && getFaceSize().hasValidAspectRatio())
		{
			g2d.setClip(clip);

			Rectangle faceRect = getFaceRect();
			g2d.clip(faceRect);

			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

			if (face.hasImage())
			{
				try
				{
					drawFaceImage(g2d, faceRect);
				}
				catch (ImageTooBigException exception)
				{
					drawFacePlaceholder(g2d, faceRect);

					// TODO: Notify user!
					exception.printStackTrace();
				}
			}
			else
			{
				drawFacePlaceholder(g2d, faceRect);
			}

			if (face.getIndex() == parent.getCurrentFace())
			{
				drawFaceHighlight(g2d, faceRect);
			}

			drawFaceText(g2d, faceRect);
		}
	}

	private void drawFaceImage(Graphics2D g2d, Rectangle faceRect) throws ImageTooBigException {
		ImageWrapper image = faceCache.getScaled(face.getImage(), faceRect.getSize());
		g2d.drawImage(image.getImage(), faceRect.x, faceRect.y, null);
	}

	private void drawFacePlaceholder(Graphics2D g2d, Rectangle faceRect) {
		g2d.setColor(faceColor);
		g2d.fillRect(faceRect.x, faceRect.y, faceRect.width, faceRect.height);
	}

	private void drawFaceHighlight(Graphics2D g2d, Rectangle faceRect) {
		Rectangle highlight = new Rectangle(faceRect);
		highlight.x += highlightThickness / 2;
		highlight.y += highlightThickness / 2;
		highlight.width -= highlightThickness;
		highlight.height -= highlightThickness;

		g2d.setStroke(new BasicStroke(highlightThickness));
		g2d.setColor(highlightColor);
		g2d.drawRect(highlight.x, highlight.y, highlight.width, highlight.height);
	}

	private void drawFaceText(Graphics2D g2d, Rectangle faceRect) {
		String faceText = String.format("%s", face.getIndex());

		Font current = g2d.getFont();

		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.FAMILY, current.getFamily());
		attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		attributes.put(TextAttribute.SIZE, current.getSize());

		g2d.setFont(Font.getFont(attributes));

		FontMetrics fontMetrics = g2d.getFontMetrics();
		int textHeight = fontMetrics.getHeight();

		if (textHeight <= faceRect.height)
		{
			int textWidth = fontMetrics.stringWidth(faceText);

			if (textWidth >= faceRect.width)
			{
				faceText = String.format("%d", face.getIndex().asInt());
				textWidth = fontMetrics.stringWidth(faceText);
			}

			if (textWidth < faceRect.width)
			{
				Rectangle textRect = fontMetrics.getStringBounds(faceText, g2d).getBounds();
				textRect.translate(faceRect.x + (faceRect.width / 2), faceRect.y + (faceRect.height / 2));
				textRect.translate(-textRect.width / 2, 0);

				g2d.clip(textRect);

				g2d.setColor(textColor);
				g2d.drawString(faceText, textRect.x, textRect.y + fontMetrics.getAscent());
			}
		}

		g2d.setFont(current);
	}

	private void loadDefaults(FaceIndex index) {
		PropertyReader options = new DefaultsReader(this.getClass().getName().concat(".").concat(index.name()));

		alpha = options.getFloat("alpha").orElse(0.75f);
		highlightThickness = options.getFloat("highlightThickness").orElse(3.00f);

		textColor = options.getColor("textColor").orElse(Color.BLACK);
		faceColor = options.getColor("faceColor").orElse(Color.PINK);
		highlightColor = options.getColor("highlightColor").orElse(Color.CYAN);

		faceCache.setQuality(options.getInt("faceCache.quality").orElse(ResampleOp.FILTER_TRIANGLE));
	}
}
