package waifu2ugc.template;

import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class Cube
{
	private final int x3D;
	private final int y3D;
	private final int z3D;

	private final FaceIndex outerFace;

	private final Map<FaceIndex, Face> faces = new EnumMap<>(FaceIndex.class);

	public Cube(int x, int y, int z, int xCount, int yCount, int zCount) {
		x3D = x;
		y3D = y;
		z3D = z;

		outerFace = detectOuterFace(xCount, yCount, zCount);
		assert (outerFace != FaceIndex.INVALID) : "Invisible cube created.";

		FaceIndex.stream().forEach(faceIndex -> addFace(faceIndex, xCount, yCount, zCount));
	}

	public int getX3D() { return x3D; }
	public int getY3D() { return y3D; }
	public int getZ3D() { return z3D; }

	public int getX2D() { return faces.get(outerFace).getX2D(); }
	public int getY2D() { return faces.get(outerFace).getY2D(); }

	public FaceIndex getOuterFace() { return outerFace; }

	public boolean hasFace(FaceIndex faceIndex) { return faces.containsKey(faceIndex); }
	public Face getFace(FaceIndex faceIndex) { return faces.get(faceIndex); }
	public Stream<Face> getFaces() { return faces.values().stream(); }

	private void addFace(FaceIndex faceIndex, int xCount, int yCount, int zCount) {
		boolean visible = isFaceVisible(faceIndex, xCount, yCount, zCount);

		if (visible)
		{
			Point pos2d = translatePoint(faceIndex, xCount, yCount, zCount);
			faces.put(faceIndex, new Face(faceIndex, pos2d.x, pos2d.y));
		}
	}

	private FaceIndex detectOuterFace(int xCount, int yCount, int zCount) {
		return FaceIndex.stream()
		                .filter(index -> isFaceVisible(index, xCount, yCount, zCount))
		                .findFirst()
		                .orElse(FaceIndex.INVALID);
	}

	private boolean isFaceVisible(FaceIndex faceIndex, int xCount, int yCount, int zCount) {
		assert (faceIndex != FaceIndex.INVALID) : "faceIndex == INVALID";

		boolean visible;

		switch (faceIndex)
		{
			case FRONT:
				visible = (z3D == 0);
				break;

			case TOP:
				visible = (y3D == 0);
				break;

			case RIGHT:
				visible = (x3D == (xCount - 1));
				break;

			case BACK:
				visible = (z3D == (zCount - 1));
				break;

			case BOTTOM:
				visible = (y3D == (yCount - 1));
				break;

			case LEFT:
				visible = (x3D == 0);
				break;

			default:
				throw new IllegalArgumentException(String.format("Argument @faceIndex[%s] must be valid.", faceIndex));
		}

		return visible;
	}

	private Point translatePoint(FaceIndex faceIndex, int xCount, int yCount, int zCount) {
		assert (faceIndex != FaceIndex.INVALID) : "faceIndex == INVALID";

		Point translated;

		switch (faceIndex)
		{
			case FRONT: // FRONT {x = x; y = y}
				translated = new Point(x3D, y3D);
				break;

			case TOP: // TOP {x = x; y = invert(z)}
				translated = new Point(x3D, (zCount - 1) - z3D);
				break;

			case RIGHT: // RIGHT {x = z; y = y}
				translated = new Point(z3D, y3D);
				break;

			case BACK: // BACK {x = invert(x); y = y}
				translated = new Point((xCount - 1) - x3D, y3D);
				break;

			case BOTTOM: // BOTTOM {x = invert(x); y = invert(z)}
				translated = new Point((xCount - 1) - x3D, (zCount - 1) - z3D);
				break;

			case LEFT: // LEFT {x = invert(z); y = y}
				translated = new Point((zCount - 1) - z3D, y3D);
				break;

			default:
				throw new IllegalArgumentException(String.format("Argument @faceIndex[%s] must be valid.", faceIndex));
		}

		return translated;
	}
}
