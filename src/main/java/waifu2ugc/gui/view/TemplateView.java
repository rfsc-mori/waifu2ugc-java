package waifu2ugc.gui.view;

import com.twelvemonkeys.image.ResampleOp;

import waifu2ugc.image.ImageDimension;
import waifu2ugc.image.ImageTooBigException;
import waifu2ugc.image.ImageWrapper;
import waifu2ugc.image.ScaledImagePair;
import waifu2ugc.settings.DefaultsReader;
import waifu2ugc.settings.PropertyReader;
import waifu2ugc.template.FaceIndex;
import waifu2ugc.template.TemplateCube;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.EnumMap;
import java.util.Map;

public class TemplateView extends AbstractView
{
	private TemplateCube cube;
	private FaceIndex currentFace = FaceIndex.INVALID;

	private final ScaledImagePair templateCache = new ScaledImagePair();
	private final Map<FaceIndex, TemplateViewFace> views = new EnumMap<>(FaceIndex.class);

	public TemplateView() {
		loadDefaults();
		createViews();
	}

	private void createViews() {
		FaceIndex.stream().forEach(index -> views.put(index, new TemplateViewFace(this)));
	}

	public void setTemplateCube(TemplateCube cube) {
		this.cube = cube;
		setTemplateFaces();
	}

	private void setTemplateFaces() {
		views.forEach((index, view) -> view.setFace(cube.getFace(index)));
	}

	public FaceIndex getCurrentFace() { return currentFace; }
	public void setCurrentFace(FaceIndex face) { currentFace = face; }

	public void repaintTemplate() {
		repaint(getTemplateRect());
	}

	public void repaintFace(FaceIndex index) {
		Rectangle faceRect = views.get(index).getFaceRect();
		repaint(faceRect);
	}

	ImageDimension getTemplateSize() {
		return cube.getImageSize().resizeToFit(getContentSize());
	}

	Rectangle getTemplateRect() {
		return ImageDimension.getCenteredRect(getContentRect(), getTemplateSize());
	}

	Point scaleToView(Point input) {
		return transformPoint(input, scaleTemplateFromCube());
	}

	Rectangle scaleToView(Rectangle input) {
		return transformRect(input, scaleTemplateFromCube());
	}

	Point scaleToTemplate(Point input) {
		return transformPoint(input, scaleTemplateToCube());
	}

	Rectangle scaleToTemplate(Rectangle input) {
		return transformRect(input, scaleTemplateToCube());
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

	private AffineTransform scaleTemplateFromCube() {
		return AffineTransform.getScaleInstance(getTemplateSize().getWidth() / cube.getImageSize().getWidth(),
		                                        getTemplateSize().getHeight() / cube.getImageSize().getHeight());
	}

	private AffineTransform scaleTemplateToCube() {
		return AffineTransform.getScaleInstance(cube.getImageSize().getWidth() / getTemplateSize().getWidth(),
		                                        cube.getImageSize().getHeight() / getTemplateSize().getHeight());
	}

	@Override
	protected void draw(Graphics2D g2d, Rectangle clip) {
		if ((cube != null) && cube.hasImage() && getTemplateSize().hasValidAspectRatio())
		{
			Rectangle templateRect = getTemplateRect();
			g2d.clip(templateRect);

			g2d.setComposite(AlphaComposite.SrcOver);

			try
			{
				ImageWrapper template = templateCache.getScaled(cube.getImage(), templateRect.getSize());
				g2d.drawImage(template.getImage(), templateRect.x, templateRect.y, null);
			}
			catch (ImageTooBigException exception)
			{
				g2d.drawImage(cube.getImage().getImage(), templateRect.x, templateRect.y, null);

				// TODO: Notify user!
				exception.printStackTrace();
			}

			views.values().forEach(view -> view.draw(g2d, clip.intersection(templateRect)));
		}
	}

	private void loadDefaults() {
		PropertyReader options = new DefaultsReader(this.getClass().getName());

		paddingTop = options.getInt("paddingTop").orElse(0);
		paddingRight = options.getInt("paddingRight").orElse(0);
		paddingBottom = options.getInt("paddingBottom").orElse(0);
		paddingLeft = options.getInt("paddingLeft").orElse(0);

		templateCache.setQuality(options.getInt("templateCache.quality").orElse(ResampleOp.FILTER_TRIANGLE));
	}
}
