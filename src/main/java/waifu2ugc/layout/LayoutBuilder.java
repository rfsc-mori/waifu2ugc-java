package waifu2ugc.layout;

import waifu2ugc.template.FaceIndex;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.EnumMap;
import java.util.Map;

class LayoutBuilder
{
	private final String name;

	private boolean isCustom;
	private boolean isDefault;

	private final Map<FaceIndex, Rectangle> faces = new EnumMap<>(FaceIndex.class);

	private String imagePath;

	LayoutBuilder(String name) {
		this.name = name;
	}

	LayoutBuilder setCustom() {
		return setCustom(true);
	}

	LayoutBuilder setCustom(boolean isCustom) {
		this.isCustom = isCustom;
		return this;
	}

	LayoutBuilder setDefault() {
		return setDefault(true);
	}

	LayoutBuilder setDefault(boolean isDefault) {
		this.isDefault = isDefault;
		return this;
	}

	LayoutBuilder addFace(FaceIndex index, Point location, int width, int height) {
		return addFace(index, location.x, location.y, width, height);
	}

	LayoutBuilder addFace(FaceIndex index, int x, int y, Dimension size) {
		return addFace(index, x, y, size.width, size.height);
	}

	LayoutBuilder addFace(FaceIndex index, Point location, Dimension size) {
		return addFace(index, location.x, location.y, size.width, size.height);
	}

	LayoutBuilder addFace(FaceIndex index, int x, int y, int width, int height) {
		return addFace(index, new Rectangle(x, y, width, height));
	}

	LayoutBuilder addFace(FaceIndex index, Rectangle rect) {
		faces.put(index, rect);
		return this;
	}

	LayoutBuilder setImagePath(String path) {
		imagePath = path;
		return this;
	}

	Layout getLayout() {
		return new Layout(name, isCustom, isDefault, faces, imagePath);
	}
}
