package waifu2ugc.gui.view;

import com.twelvemonkeys.image.ResampleOp;

import waifu2ugc.image.AspectHint;
import waifu2ugc.image.ImageDimension;
import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.image.ScaledImagePair;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.TemplateFace;
import waifu2ugc.template.FaceIndex;

import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

// Future revisions:
// Use change listeners instead.
// Change selector on change event.
public class FaceView extends AbstractView
{
	private TemplateFace face;

	private Consumer<Point> offsetUpdated;

	private final Map<FaceIndex, ScaledImagePair> cache = new EnumMap<>(FaceIndex.class);

	private final FaceViewBackground background = new FaceViewBackground();
	private final FaceViewSelector selector = new FaceViewSelector();
	private final FaceViewGrid grid = new FaceViewGrid();

	private final FaceViewMouseAdapter mouseAdapter = new FaceViewMouseAdapter();

	private final Timer animation = new Timer(50, this::selectorAnimation);

	public FaceView() {
		loadDefaults();

		createCache();

		mouseAdapter.setDragStarted(this::dragStarted);
		mouseAdapter.setContentDragged(this::contentDragged);
		mouseAdapter.setCommitPlacement(this::commitPlacement);

		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}

	private void createCache() {
		FaceIndex.stream().forEach(index -> cache.put(index, new ScaledImagePair()));
	}

	public TemplateFace getFace() { return face; }
	public void setFace(TemplateFace face) { this.face = face; }

	public boolean isDragging() {
		return mouseAdapter.isDragging();
	}

	public void setOffsetUpdated(Consumer<Point> offsetUpdated) {
		this.offsetUpdated = offsetUpdated;
	}

	private ImageDimension getFrameSize() {
		ImageDimension frameSize;

		if (face.hasBlocks())
		{
			frameSize = face.getFrameSize().resizeToFit(getContentSize());
		}
		else
		{
			frameSize = getContentSize();
		}

		return frameSize;
	}

	private Rectangle getFrameRect() {
		return ImageDimension.getCenteredRect(getContentRect(), getFrameSize());
	}

	private List<Rectangle> getBackgroundRects() {
		Rectangle v = getViewRect();
		Rectangle s = getSelectorRect();

		return Arrays.asList(new Rectangle(v.x, v.y, v.width, s.y - v.y),
		                     new Rectangle(v.x, s.y, s.x - v.x, s.height),
		                     new Rectangle(s.x + s.width, s.y, v.width - s.x - s.width, s.height),
		                     new Rectangle(v.x, s.y + s.height, v.width, v.height - s.y - s.height));
	}

	private ImageDimension getFaceSize() {
		ImageDimension faceSize;

		if (face.isResamplingAllowed())
		{
			if (face.isAspectRatioPreserved() && face.hasBlocks())
			{
				if (face.getAspectHint() == AspectHint.FIT)
				{
					faceSize = face.getFinalSize().resizeToFit(getFrameSize());
				}
				else
				{
					faceSize = face.getFinalSize().resizeToFit(getContentSize());
				}
			}
			else
			{
				faceSize = getFrameSize();
			}
		}
		else
		{
			faceSize = face.getFinalSize().resizeToFit(getFrameSize());
		}

		return faceSize;
	}

	private Rectangle getFaceRect() {
		Rectangle faceRect;

		if (face.isResamplingAllowed() && face.isAspectRatioPreserved() && face.hasBlocks())
		{
			if (face.getAspectHint() == AspectHint.FIT)
			{
				ImageDimension faceSize = getFaceSize();

				Point origin = transformPoint(face.getFitOffset(), scaleFromFaceFinalSize());

				faceRect = new Rectangle(getFrameRect().getLocation(), faceSize);
				faceRect.translate(origin.x, origin.y);
			}
			else
			{
				faceRect = ImageDimension.getCenteredRect(getContentRect(), getFaceSize());
			}
		}
		else
		{
			faceRect = ImageDimension.getCenteredRect(getFrameRect(), getFaceSize());
		}

		return faceRect;
	}

	private Rectangle getFaceBackgroundRect() {
		Rectangle faceBackground;

		if (!face.isResamplingAllowed() || face.getAspectHint() == AspectHint.FIT)
		{
			faceBackground = getFrameRect().intersection(getSelectorRect());
		}
		else
		{
			faceBackground = getFaceRect().intersection(getSelectorRect());
		}

		return faceBackground;
	}

	private ImageDimension getSelectorSize() {
		ImageDimension selectorSize;

		if (face.isResamplingAllowed() && face.isAspectRatioPreserved() && face.hasBlocks())
		{
			if (face.getAspectHint() == AspectHint.FIT)
			{
				selectorSize = getFrameSize();
			}
			else
			{
				selectorSize = face.getFrameSize().resizeToFit(getFaceSize());
			}
		}
		else
		{
			selectorSize = getFrameSize();
		}

		return selectorSize;
	}

	private Rectangle getSelectorRect() {
		Rectangle selectorRect;

		if (face.isResamplingAllowed() && face.isAspectRatioPreserved() && face.hasBlocks())
		{
			if (face.getAspectHint() == AspectHint.FIT)
			{
				selectorRect = ImageDimension.getCenteredRect(getFrameRect(), getSelectorSize());
			}
			else
			{
				ImageDimension selectorSize = getSelectorSize();

				Point origin = transformPoint(face.getCropOffset(), scaleFromFaceFinalSize());

				selectorRect = new Rectangle(getFaceRect().getLocation(), selectorSize);
				selectorRect.translate(origin.x, origin.y);
			}
		}
		else
		{
			selectorRect = ImageDimension.getCenteredRect(getFrameRect(), getSelectorSize());
		}

		return selectorRect;
	}

	private ImageDimension getGridSize() {
		return getSelectorSize();
	}

	private Rectangle getGridRect() {
		return getSelectorRect();
	}

	private List<Point> getHorizontalLines() {
		List<Point> lines;

		if (face.getVerticalCount() > 1)
		{
			Rectangle grid = getGridRect();
			ImageDimension faceSize = face.getSize();

			lines = new ArrayList<>(face.getVerticalCount() - 1);

			AffineTransform faceToView = scaleSelectorFromFacePortrait();

			for (int y = 1; y < face.getVerticalCount(); ++y)
			{
				Point origin = transformPoint(new Point(0, y * faceSize.height), faceToView);
				origin.translate(grid.x, grid.y);

				lines.add(origin);
			}
		}
		else
		{
			lines = Collections.emptyList();
		}

		return lines;
	}

	private List<Point> getVerticalLines() {
		List<Point> lines;

		if (face.getHorizontalCount() > 1)
		{
			Rectangle grid = getGridRect();
			ImageDimension faceSize = face.getSize();

			lines = new ArrayList<>(face.getHorizontalCount() - 1);

			AffineTransform faceToView = scaleSelectorFromFacePortrait();

			for (int x = 1; x < face.getHorizontalCount(); ++x)
			{
				Point origin = transformPoint(new Point(x * faceSize.width, 0), faceToView);
				origin.translate(grid.x, grid.y);

				lines.add(origin);
			}
		}
		else
		{
			lines = Collections.emptyList();
		}

		return lines;
	}

	private Point transformPoint(Point input, AffineTransform scale) {
		Point scaled = new Point(input);
		scale.transform(scaled, scaled);

		return scaled;
	}

	private Rectangle transformRect(Rectangle input, AffineTransform scale) {
		Point topLeft = new Point(input.x, input.y);
		Point bottomRight = new Point(input.x + input.width, input.y + input.height);

		scale.transform(topLeft, topLeft);
		scale.transform(bottomRight, bottomRight);

		return new Rectangle(topLeft.x, topLeft.y, bottomRight.x - topLeft.x, bottomRight.y - topLeft.y);
	}

	private AffineTransform scaleFromFaceFinalSize() {
		return AffineTransform.getScaleInstance(getFaceSize().getWidth() / face.getFinalSize().getWidth(),
		                                        getFaceSize().getHeight() / face.getFinalSize().getHeight());
	}

	private AffineTransform scaleToFaceFinalSize() {
		return AffineTransform.getScaleInstance(face.getFinalSize().getWidth() / getFaceSize().getWidth(),
		                                        face.getFinalSize().getHeight() / getFaceSize().getHeight());
	}

	private AffineTransform scaleSelectorFromFacePortrait() {
		return AffineTransform.getScaleInstance(getSelectorSize().getWidth() / face.getFrameSize().getWidth(),
		                                        getSelectorSize().getHeight() / face.getFrameSize().getHeight());
	}

	private AffineTransform scaleSelectorToFacePortrait() {
		return AffineTransform.getScaleInstance(face.getFrameSize().getWidth() / getSelectorSize().getWidth(),
		                                        face.getFrameSize().getHeight() / getSelectorSize().getHeight());
	}

	private void updateDraggableRects() {
		if ((face != null) && face.isResamplingAllowed() && face.isAspectRatioPreserved() && face.hasBlocks())
		{
			if (face.getAspectHint() == AspectHint.FIT)
			{
				mouseAdapter.setContainer(getFrameRect());
				mouseAdapter.setDraggable(getFaceRect());
			}
			else
			{
				mouseAdapter.setContainer(getFaceRect());
				mouseAdapter.setDraggable(getSelectorRect());
			}

			mouseAdapter.setEnabled(face.hasAspectRatioMismatch());
		}
		else
		{
			mouseAdapter.setEnabled(false);
		}
	}

	private void repaintDraggedRect(Rectangle dirty) {
		int thickness = Math.round(selector.getThickness());

		Rectangle rect = new Rectangle(dirty);
		rect.grow(thickness, thickness);

		repaint(rect);
	}

	private void dragStarted(Rectangle where) {
		this.setCursor(new Cursor(Cursor.MOVE_CURSOR));

		if ((face != null) && face.hasAspectRatioMismatch())
		{
			Rectangle dirty;

			if (face.getAspectHint() == AspectHint.CROP)
			{
				animation.start();
				dirty = where;
			}
			else
			{
				dirty = mouseAdapter.getContainer();
			}

			repaint(dirty);
		}
	}

	private void contentDragged(Point previous, Rectangle current) {
		if ((face != null) && face.hasAspectRatioMismatch())
		{
			Point destination = transformPoint(new Point(current.x - mouseAdapter.getContainer().x,
			                                             current.y - mouseAdapter.getContainer().y),
			                                   scaleToFaceFinalSize());

			if (face.getAspectHint() == AspectHint.FIT)
			{
				face.updateFitOffset(destination);
			}
			else
			{
				face.updateCropOffset(destination);
			}

			Rectangle dirty = new Rectangle(previous, current.getSize());
			repaintDraggedRect(dirty);

			dirty.translate((current.x - previous.x), (current.y - previous.y));
			repaintDraggedRect(dirty);

			if (offsetUpdated != null)
			{
				offsetUpdated.accept(face.getCropOffset());
			}
		}
	}

	private void commitPlacement(Rectangle destination) {
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		if ((face != null) && face.hasAspectRatioMismatch())
		{
			Rectangle dirty;

			if (face.getAspectHint() == AspectHint.FIT)
			{
				dirty = mouseAdapter.getContainer();

				if (offsetUpdated != null)
				{
					offsetUpdated.accept(face.getFitOffset());
				}
			}
			else
			{
				dirty = destination;

				animation.stop();
				selector.resetAnimation();

				if (offsetUpdated != null)
				{
					offsetUpdated.accept(face.getCropOffset());
				}
			}

			repaintDraggedRect(dirty);
		}
	}

	private void selectorAnimation(ActionEvent e) {
		if ((face != null) && face.hasAspectRatioMismatch())
		{
			selector.incrementAnimation();
			repaintDraggedRect(getSelectorRect());
		}
	}

	@Override
	protected void draw(Graphics2D g2d, Rectangle clip) {
		if ((face != null) && face.hasImage() && getFaceSize().hasValidAspectRatio())
		{
			if (face.hasAspectRatioMismatch())
			{
				updateDraggableRects();
			}

			Rectangle faceBackground = getFaceBackgroundRect();
			g2d.clip(faceBackground);
			background.draw(g2d, faceBackground);

			Rectangle faceRect = getFaceRect();
			g2d.setClip(clip.intersection(faceRect));

			try
			{
				this.drawFace(g2d, faceRect);
			}
			catch (ImageTooBigException exception)
			{
				// TODO: Notify user!
				exception.printStackTrace();
			}

			Rectangle selectorRect = getSelectorRect();
			g2d.setClip(clip.intersection(selectorRect));
			selector.draw(g2d, selectorRect, !mouseAdapter.isDragging());

			Rectangle gridRect = getGridRect();
			g2d.clip(gridRect);
			grid.draw(g2d, gridRect, getHorizontalLines(), getVerticalLines());

			g2d.setClip(clip);
			background.draw(g2d, getBackgroundRects());
		}
		else
		{
			g2d.clip(getViewRect());

			g2d.setComposite(AlphaComposite.Src);
			background.draw(g2d, getViewRect());
		}
	}

	private void drawFace(Graphics2D g2d, Rectangle faceRect) throws ImageTooBigException {
		ImageWrapper faceImage = cache.get(face.getIndex()).getScaled(face.getImage(), faceRect.getSize());
		g2d.setComposite(AlphaComposite.SrcOver);
		g2d.drawImage(faceImage.getImage(), faceRect.x, faceRect.y, null);
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		paddingTop = options.getInt("paddingTop").orElse(10);
		paddingRight = options.getInt("paddingRight").orElse(10);
		paddingBottom = options.getInt("paddingBottom").orElse(10);
		paddingLeft = options.getInt("paddingLeft").orElse(10);

		cache.forEach((index, cache) -> {
			cache.setQuality(options.getInt(index.name().concat(".quality")).orElse(ResampleOp.FILTER_TRIANGLE));
		});

		animation.setDelay(options.getInt("animation.delay").orElse(50));
	}
}
