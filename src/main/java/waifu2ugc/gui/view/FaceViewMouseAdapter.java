package waifu2ugc.gui.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

// Future revisions:
// Use change listeners instead.
class FaceViewMouseAdapter extends MouseAdapter
{
	private Consumer<Rectangle> dragStarted;
	private BiConsumer<Point, Rectangle> contentDragged;
	private Consumer<Rectangle> commitPlacement;

	private Point origin;

	private boolean enabled;

	private Rectangle container;
	private Rectangle draggable;

	public void setDragStarted(Consumer<Rectangle> dragStarted) { this.dragStarted = dragStarted; }
	public void setContentDragged(BiConsumer<Point, Rectangle> contentDragged) { this.contentDragged = contentDragged; }
	public void setCommitPlacement(Consumer<Rectangle> commitPlacement) { this.commitPlacement = commitPlacement; }

	boolean isEnabled() {
		return enabled;
	}

	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	Rectangle getContainer() {
		return new Rectangle(container);
	}

	Optional<Rectangle> getContainerOptional() {
		return (container != null) ? Optional.of(getContainer()) : Optional.empty();
	}

	void setContainer(Rectangle container) {
		this.container = new Rectangle(container);
	}

	Rectangle getDraggable() {
		return new Rectangle(draggable);
	}

	Optional<Rectangle> getDraggableOptional() {
		return (draggable != null) ? Optional.of(getDraggable()) : Optional.empty();
	}

	void setDraggable(Rectangle draggable) {
		this.draggable = new Rectangle(draggable);
	}

	boolean isDraggable() {
		return (container != null) && (draggable != null) && enabled;
	}

	boolean isDragging() {
		return (origin != null);
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		if (isDraggable())
		{
			Point mouse = mouseEvent.getPoint();

			if (draggable.contains(mouse))
			{
				origin = mouse;
				dragStarted.accept(new Rectangle(draggable));
			}
		}
		else
		{
			origin = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isDragging())
		{
			origin = null;
			commitPlacement.accept(new Rectangle(draggable));
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		if (isDraggable() && isDragging())
		{
			int deltaX = (mouseEvent.getX() - origin.x);
			int deltaY = (mouseEvent.getY() - origin.y);

			origin = mouseEvent.getPoint();

			Point previous = draggable.getLocation();

			draggable.x += deltaX;
			draggable.y += deltaY;

			if (draggable.x < container.x)
			{
				draggable.x = container.x;
			}
			else if ((draggable.x + draggable.width) > (container.x + container.width))
			{
				draggable.x = (container.x + container.width) - draggable.width;
			}

			if (draggable.y < container.y)
			{
				draggable.y = container.y;
			}
			else if ((draggable.y + draggable.height) > (container.y + container.height))
			{
				draggable.y = (container.y + container.height) - draggable.height;
			}

			if ((draggable.x != previous.x) || (draggable.y != previous.y))
			{
				contentDragged.accept(previous, new Rectangle(draggable));
			}
		}
	}
}
