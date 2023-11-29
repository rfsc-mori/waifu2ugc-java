package waifu2ugc.template;

public class Face
{
	private final FaceIndex faceIndex;

	private final int x2D;
	private final int y2D;

	Face(FaceIndex index, int x, int y) {
		assert (index != FaceIndex.INVALID) : "index == INVALID";

		this.faceIndex = index;

		x2D = x;
		y2D = y;
	}

	public FaceIndex getIndex() { return faceIndex; }

	public int getX2D() { return x2D; }
	public int getY2D() { return y2D; }
}
