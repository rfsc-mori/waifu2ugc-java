package waifu2ugc.template;

import waifu2ugc.image.ImageDimension;
import waifu2ugc.image.ImageWrapper;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateCube
{
	private ImageWrapper image = new ImageWrapper();
	private final Map<FaceIndex, TemplateFace> faces = new EnumMap<>(FaceIndex.class);

	public TemplateCube() {
		FaceIndex.stream().forEach(this::createTemplateFace);
	}

	private void createTemplateFace(FaceIndex index) {
		faces.put(index, new TemplateFace(index));
	}

	public TemplateFace getFace(FaceIndex index) { return faces.get(index); }

	public Stream<TemplateFace> getFaces() { return faces.values().stream(); }
	public Stream<TemplateFace> getEnabledFaces() { return getFaces().filter(TemplateFace::isEnabled); }
	public Stream<TemplateFace> getDisabledFaces() { return getFaces().filter(TemplateFace::isDisabled); }

	public boolean hasImage() { return image.hasImage(); }
	public boolean hasNoImage() { return image.hasNoImage(); }

	public ImageDimension getImageSize() { return image.getSizeOptional().orElse(new ImageDimension()); }
	public Rectangle getImageRect() { return image.getRectOptional().orElse(new Rectangle()); }

	public ImageWrapper getImage() { return new ImageWrapper(image); }
	public void setImage(ImageWrapper image) { this.image.setImage(image); }

	public int getXAxisSize() {
		return getEnabledFaces().filter(TemplateFace::affectsXHorizontally)
		                        .mapToInt(TemplateFace::getHorizontalCount)
		                        .reduce(1, Integer::max);
	}

	public int getYAxisSize() {
		return getEnabledFaces().filter(TemplateFace::affectsYVertically)
		                        .mapToInt(TemplateFace::getVerticalCount)
		                        .reduce(1, Integer::max);
	}

	public int getZAxisSize() {
		return getEnabledFaces().filter(face -> (face.affectsZHorizontally() || face.affectsZVertically()))
		                        .mapToInt(face -> (face.affectsZHorizontally() ?
		                                           face.getHorizontalCount() :
		                                           face.getVerticalCount()))
		                        .reduce(1, Integer::max);
	}

	public int getTotalBlockCount() {
		return traverseVisible(do_nothing -> {});
	}

	public int getTotalFaceCount() {
		return explode().map(Cube::getFaces)
		                .map(list -> list.collect(Collectors.toList()))
		                .mapToInt(Collection::size)
		                .sum();
	}

	public Stream<Cube> explode() {
		Stream.Builder<Cube> cubes = Stream.builder();
		traverseVisible(cubes::add);
		return cubes.build();
	}

	private int traverseVisible(Consumer<Cube> consumer) {
		int xCount = getXAxisSize();
		int yCount = getYAxisSize();
		int zCount = getZAxisSize();

		boolean frontEnabled = getFace(FaceIndex.FRONT).isEnabled();
		boolean topEnabled = getFace(FaceIndex.TOP).isEnabled();
		boolean rightEnabled = getFace(FaceIndex.RIGHT).isEnabled();
		boolean backEnabled = getFace(FaceIndex.BACK).isEnabled();
		boolean bottomEnabled = getFace(FaceIndex.BOTTOM).isEnabled();
		boolean leftEnabled = getFace(FaceIndex.LEFT).isEnabled();

		int count = 0;

		for (int x = 0; x < xCount; ++x)
		{
			for (int y = 0; y < yCount; ++y)
			{
				for (int z = 0; z < zCount; ++z)
				{
					boolean front = z == 0;
					boolean top = y == 0;
					boolean right = x == (xCount - 1);
					boolean back = z == (zCount - 1);
					boolean bottom = y == (yCount - 1);
					boolean left = x == 0;

					if ((front && frontEnabled) || (top && topEnabled) ||
					    (right && rightEnabled) || (back && backEnabled) ||
					    (bottom && bottomEnabled) || (left && leftEnabled))
					{
						Cube cube = new Cube(x, y, z, xCount, yCount, zCount);
						TemplateFace face = getFace(cube.getOuterFace());

						if (cube.getX2D() < face.getHorizontalCount() &&
						    cube.getY2D() < face.getVerticalCount())
						{
							consumer.accept(cube);
							count++;
						}
					}
				}
			}
		}

		return count;
	}
}
