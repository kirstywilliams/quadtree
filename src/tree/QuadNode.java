package tree;
/**
 * @author Kirsty Williams
 * Email: me@kirstywilliams.co.uk
 * Date Created: August, 2012
 * @version 1.0
 */

import java.util.ArrayList;

import util.AbstractDouble;
import util.AbstractLeaf;
import util.Box;

public class QuadNode<T> {

	private QuadLeaf<T> leaf = null;

	private boolean hasChildren = false;
	private QuadNode<T> NW = null;
	private QuadNode<T> NE = null;
	private QuadNode<T> SE = null;
	private QuadNode<T> SW = null;
	private final Box bounds;

	public QuadNode(double minX, double minY, double maxX, double maxY) {
		this.bounds = new Box(minX, minY, maxX, maxY);
	}

	public boolean put(QuadLeaf<T> leaf) {
		if (this.hasChildren) return getChild(leaf.x, leaf.y).put(leaf);
		if (this.leaf == null) {
			this.leaf = leaf;
			return true;
		}
		if (this.leaf.x == leaf.x && this.leaf.y == leaf.y) {
			boolean changed = false;
			for (T value : leaf.values) {
				if (!this.leaf.values.contains(value)) {
					changed = this.leaf.values.add(value) || changed;
				}
			}
			return changed;
		}
		this.divide();
		return getChild(leaf.x, leaf.y).put(leaf);
	}

	public boolean put(double x, double y, T value) {
		return put(new QuadLeaf<T>(x, y, value));
	}

	public boolean remove(double x, double y, T value) {
		if (this.hasChildren) return getChild(x, y).remove(x, y, value);
		if (this.leaf != null && this.leaf.x == x && this.leaf.y == y) {
			if (this.leaf.values.remove(value)) {
				if (this.leaf.values.size() == 0) {
					this.leaf = null;
				}
				return true;
			}
		}
		return false;
	}

	public Box getBounds() {
		return this.bounds;
	}

	public void clear() {
		if (this.hasChildren) {
			this.NW.clear();
			this.NE.clear();
			this.SE.clear();
			this.SW.clear();
			this.NW = null;
			this.NE = null;
			this.SE = null;
			this.SW = null;
			this.hasChildren = false;
		} else {
			if (this.leaf != null) {
				this.leaf.values.clear();
				this.leaf = null;
			}
		}
	}

	public T get(double x, double y, AbstractDouble bestDistance) {
		if (this.hasChildren) {
			T closest = null;
			QuadNode<T> bestChild = this.getChild(x, y);
			if (bestChild != null) {
				closest = bestChild.get(x, y, bestDistance);
			}
			if (bestChild != this.NW && this.NW.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.NW.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			if (bestChild != this.NE && this.NE.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.NE.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			if (bestChild != this.SE && this.SE.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.SE.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			if (bestChild != this.SW && this.SW.bounds.calcDist(x, y) < bestDistance.value) {
				T value = this.SW.get(x, y, bestDistance);
				if (value != null) { closest = value; }
			}
			return closest;
		}
		if (this.leaf != null && this.leaf.values.size() > 0) {
			T value = this.leaf.values.get(0);
			double distance = Math.sqrt(
					(this.leaf.x - x) * (this.leaf.x - x)
					+ (this.leaf.y - y) * (this.leaf.y - y));
			if (distance < bestDistance.value) {
				bestDistance.value = distance;
				return value;
			}
		}
		return null;
	}

	public ArrayList<T> get(double x, double y, double maxDistance, ArrayList<T> values) {
		if (this.hasChildren) {
			if (this.NW.bounds.calcDist(x, y) <= maxDistance) {
				this.NW.get(x, y, maxDistance, values);
			}
			if (this.NE.bounds.calcDist(x, y) <= maxDistance) {
				this.NE.get(x, y, maxDistance, values);
			}
			if (this.SE.bounds.calcDist(x, y) <= maxDistance) {
				this.SE.get(x, y, maxDistance, values);
			}
			if (this.SW.bounds.calcDist(x, y) <= maxDistance) {
				this.SW.get(x, y, maxDistance, values);
			}
			return values;
		}
		if (this.leaf != null && this.leaf.values.size() > 0) {
			double distance = Math.sqrt(
					(this.leaf.x - x) * (this.leaf.x - x)
					+ (this.leaf.y - y) * (this.leaf.y - y));
			if (distance <= maxDistance) {
				values.addAll(this.leaf.values);
			}
		}
		return values;
	}

	public ArrayList<T> get(Box bounds, ArrayList<T> values) {
		if (this.hasChildren) {
			if (this.NW.bounds.intersects(bounds)) {
				this.NW.get(bounds, values);
			}
			if (this.NE.bounds.intersects(bounds)) {
				this.NE.get(bounds, values);
			}
			if (this.SE.bounds.intersects(bounds)) {
				this.SE.get(bounds, values);
			}
			if (this.SW.bounds.intersects(bounds)) {
				this.SW.get(bounds, values);
			}
			return values;
		}
		if (this.leaf != null && this.leaf.values.size() > 0 && bounds.contains(this.leaf.x, this.leaf.y)) {
			values.addAll(this.leaf.values);
		}
		return values;
	}

	public int execute(Box globalBounds, QuadTree.Executor<T> executor) {
		int count = 0;
		if (this.hasChildren) {
			if (this.NW.bounds.intersects(globalBounds)) {
				count += this.NW.execute(globalBounds, executor);
			}
			if (this.NE.bounds.intersects(globalBounds)) {
				count += this.NE.execute(globalBounds, executor);
			}
			if (this.SE.bounds.intersects(globalBounds)) {
				count += this.SE.execute(globalBounds, executor);
			}
			if (this.SW.bounds.intersects(globalBounds)) {
				count += this.SW.execute(globalBounds, executor);
			}
			return count;
		}
		if (this.leaf != null && this.leaf.values.size() > 0 && globalBounds.contains(this.leaf.x, this.leaf.y)) {
			count += this.leaf.values.size();
			for (T object : this.leaf.values) executor.execute(this.leaf.x, this.leaf.y, object);
		}
		return count;
	}

	private void divide() {
		this.NW = new QuadNode<T>(this.bounds.minX, this.bounds.centreY, this.bounds.centreX, this.bounds.maxY);
		this.NE = new QuadNode<T>(this.bounds.centreX, this.bounds.centreY, this.bounds.maxX, this.bounds.maxY);
		this.SE = new QuadNode<T>(this.bounds.centreX, this.bounds.minY, this.bounds.maxX, this.bounds.centreY);
		this.SW = new QuadNode<T>(this.bounds.minX, this.bounds.minY, this.bounds.centreX, this.bounds.centreY);
		this.hasChildren = true;
		if (this.leaf != null) {
			getChild(this.leaf.x, this.leaf.y).put(this.leaf);
			this.leaf = null;
		}
	}

	private QuadNode<T> getChild(double x, double y) {
		if (this.hasChildren) {
			if (x < this.bounds.centreX) {
				if (y < this.bounds.centreY)
					return this.SW;
				return this.NW;
			}
			if (y < this.bounds.centreY)
				return this.SE;
			return this.NE;
		}
		return null;
	}

	public QuadLeaf<T> firstLeaf() {
		if (this.hasChildren) {
			QuadLeaf<T> leaf = this.SW.firstLeaf();
			if (leaf == null) { leaf = this.NW.firstLeaf(); }
			if (leaf == null) { leaf = this.SE.firstLeaf(); }
			if (leaf == null) { leaf = this.NE.firstLeaf(); }
			return leaf;
		}
		return this.leaf;
	}

	public boolean nextLeaf(QuadLeaf<T> currentLeaf, AbstractLeaf<T> nextLeaf) {
		if (this.hasChildren) {
			boolean found = false;
			if (currentLeaf.x <= this.bounds.centreX && currentLeaf.y <= this.bounds.centreY) {
				found = this.SW.nextLeaf(currentLeaf, nextLeaf);
				if (found) {
					if (nextLeaf.value == null) { nextLeaf.value = this.NW.firstLeaf(); }
					if (nextLeaf.value == null) { nextLeaf.value = this.SE.firstLeaf(); }
					if (nextLeaf.value == null) { nextLeaf.value = this.NE.firstLeaf(); }
					return true;
				}
			}
			if (currentLeaf.x <= this.bounds.centreX && currentLeaf.y >= this.bounds.centreY) {
				found = this.NW.nextLeaf(currentLeaf, nextLeaf);
				if (found) {
					if (nextLeaf.value == null) { nextLeaf.value = this.SE.firstLeaf(); }
					if (nextLeaf.value == null) { nextLeaf.value = this.NE.firstLeaf(); }
					return true;
				}
			}
			if (currentLeaf.x >= this.bounds.centreX && currentLeaf.y <= this.bounds.centreY) {
				found = this.SE.nextLeaf(currentLeaf, nextLeaf);
				if (found) {
					if (nextLeaf.value == null) { nextLeaf.value = this.NE.firstLeaf(); }
					return true;
				}
			}
			if (currentLeaf.x >= this.bounds.centreX && currentLeaf.y >= this.bounds.centreY) {
				return this.NE.nextLeaf(currentLeaf, nextLeaf);
			}
			return false;
		}
		return currentLeaf == this.leaf;
	}

	public QuadLeaf<T> nextLeaf(QuadLeaf<T> currentLeaf) {
		AbstractLeaf<T> nextLeaf = new AbstractLeaf<T>(null);
		nextLeaf(currentLeaf, nextLeaf);
		return nextLeaf.value;
	}
}