package waifu2ugc.template;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum FaceIndex
{
	INVALID(0, "Invalid"),
	FRONT(1, "Front"),
	TOP(2, "Top"),
	RIGHT(3, "Right"),
	BACK(4, "Back"),
	BOTTOM(5, "Bottom"),
	LEFT(6, "Left");

	private final int index;
	private final String alias;

	FaceIndex(int index, String alias) {
		this.index = index;
		this.alias = alias;
	}

	public int asInt() { return index; }

	public String getAlias() { return alias; }

	public static List<FaceIndex> faces() { return Arrays.asList(FRONT, TOP, RIGHT, BACK, BOTTOM, LEFT); }
	public static Stream<FaceIndex> stream() { return Stream.of(FRONT, TOP, RIGHT, BACK, BOTTOM, LEFT); }

	public boolean isValid() { return !this.equals(INVALID); }

	public boolean affectsXHorizontally() { return Arrays.asList(FRONT, TOP, BACK, BOTTOM).contains(this); }
	public boolean affectsXVertically() { return false; } // Never affects.

	public boolean affectsYHorizontally() { return false; } // Never affects.
	public boolean affectsYVertically() { return Arrays.asList(FRONT, RIGHT, BACK, LEFT).contains(this); }

	public boolean affectsZHorizontally() { return Arrays.asList(RIGHT, LEFT).contains(this); }
	public boolean affectsZVertically() { return Arrays.asList(TOP, BOTTOM).contains(this); }

	public FaceIndex getOpposite() {
		assert (this != INVALID) : "this(caller) == INVALID";

		FaceIndex face;

		switch (this)
		{
			case FRONT:
				face = BACK;
				break;

			case TOP:
				face = BOTTOM;
				break;

			case RIGHT:
				face = LEFT;
				break;

			case BACK:
				face = FRONT;
				break;

			case BOTTOM:
				face = TOP;
				break;

			case LEFT:
				face = RIGHT;
				break;

			default:
				throw new IllegalArgumentException(String.format("Caller this[%s] must be a valid face.", this));
		}

		return face;
	}

	@Override
	public String toString() {
		return getAlias();
	}
}
