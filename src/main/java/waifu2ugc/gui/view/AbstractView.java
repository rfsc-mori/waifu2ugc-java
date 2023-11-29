package waifu2ugc.gui.view;

import waifu2ugc.image.ImageDimension;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

abstract class AbstractView extends JPanel
{
	protected int paddingTop;
	protected int paddingRight;
	protected int paddingBottom;
	protected int paddingLeft;

	public int getPaddingTop() { return paddingTop; }
	public void setPaddingTop(int paddingTop) { this.paddingTop = paddingTop; }

	public int getPaddingRight() { return paddingRight; }
	public void setPaddingRight(int paddingRight) { this.paddingRight = paddingRight; }

	public int getPaddingBottom() { return paddingBottom; }
	public void setPaddingBottom(int paddingBottom) { this.paddingBottom = paddingBottom; }

	public int getPaddingLeft() { return paddingLeft; }
	public void setPaddingLeft(int paddingLeft) { this.paddingLeft = paddingLeft; }

	public ImageDimension getViewSize() {
		return new ImageDimension(getSize());
	}

	public Rectangle getViewRect() {
		return new Rectangle(getViewSize());
	}

	public ImageDimension getContentSize() {
		ImageDimension contentSize = getViewSize();

		contentSize.width -= (paddingLeft + paddingRight);
		contentSize.height -= (paddingTop + paddingBottom);

		return contentSize;
	}

	public Rectangle getContentRect() {
		Rectangle contentRect = getViewRect();

		contentRect.x += paddingLeft;
		contentRect.y += paddingTop;

		contentRect.setSize(getContentSize());

		return contentRect;
	}

	public void repaintView() {
		repaint(getViewRect());
	}

	public void repaintContent() {
		repaint(getContentRect());
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Graphics2D g2d = (Graphics2D) graphics;

		Rectangle clip = g2d.getClipBounds();
		draw(g2d, clip);
	}

	abstract protected void draw(Graphics2D g2d, Rectangle clip);
}
