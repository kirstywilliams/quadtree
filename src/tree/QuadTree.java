package tree;
/**
 * @author Kirsty Williams
 * Email: me@kirstywilliams.co.uk
 * Date Created: August, 2012
 * @version 1.0
 */

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;

import util.AbstractDouble;
import util.Box;

public class QuadTree<T> {

	protected QuadNode<T> root = null;
	private int size = 0;
	private AbstractCollection<T> values = null;

	/**
	 * Creates an empty QuadTree with the bounds
	 */
	public QuadTree(double minX, double minY, double maxX, double maxY) {
		this.root = new QuadNode<T>(minX, minY, maxX, maxY);
	}

	/**
	 * Associates the specified value with the specified coords in this
	 * QuadTree.
	 */
	public boolean put(double x, double y, T value) {
		if (this.root.put(x, y, value)) {
			increaseSize();
			return true;
		}
		return false;
	}

	public boolean remove(double x, double y, T value) {
		if (this.root.remove(x, y, value)) {
			decreaseSize();
			return true;
		}
		return false;
	}

	public void clear() {
		this.root.clear();
		this.size = 0;
	}
	
	private void increaseSize() { 
		this.size++; this.values = null;
	}
	
	private void decreaseSize() { 
		this.size--; this.values = null; 
	}

	/**
	 * Gets the object closest to (x,y)
	 */
	public T get(double x, double y) {
		return this.root.get(x, y, new AbstractDouble(Double.POSITIVE_INFINITY));
	}

	/**
	 * Gets all objects within a certain distance
	 */
	public ArrayList<T> get(double x, double y, double distance) {
		return this.root.get(x, y, distance, new ArrayList<T>());
	}

	/**
	 * Gets all objects inside the specified boundary.
	 */
	public ArrayList<T> get(Box bounds, ArrayList<T> values) {
		return this.root.get(bounds, values);
	}

	/**
	 * Gets all objects inside the specified area.
	 */
	public ArrayList<T> get(double minX, double minY, double maxX, double maxY, ArrayList<T> values) {
		return get(new Box(minX, minY, maxX, maxY), values);
	}

	public int execute(Box bounds, Executor<T> executor) {
		if (bounds == null) {
			return this.root.execute(this.root.getBounds(), executor);
		}
		return this.root.execute(bounds, executor);
	}

	public int execute(double minX, double minY, double maxX, double maxY, Executor<T> executor) {
		return execute(new Box(minX, minY, maxX, maxY), executor);
	}

	public int size() {
		return this.size;
	}

	public double getMinX() {
		return this.root.getBounds().minX;
	}

	public double getMaxX() {
		return this.root.getBounds().maxX;
	}

	public double getMinY() {
		return this.root.getBounds().minY;
	}

	public double getMaxY() {
		return this.root.getBounds().maxY;
	}

	public AbstractCollection<T> values() {
		if (this.values == null) {
			this.values = new AbstractCollection<T>() {
				@Override
				public Iterator<T> iterator() {
					Iterator<T> iterator = new Iterator<T>() {
						private QuadLeaf<T> currentLeaf = firstLeaf();
						private int nextIndex = 0;
						private T next = first();

						private T first() {
							if (this.currentLeaf == null) {
								return null;
							}
							this.nextIndex = 0;
							loadNext();
							return this.next;
						}

						@Override
						public boolean hasNext() {
							return this.next != null;
						}

						@Override
						public T next() {
							if (this.next == null) {
								return null;
							}
							T current = this.next;
							loadNext();
							return current;
						}

						private void loadNext() {
							boolean searching = true;
							while (searching) {
								if (this.nextIndex < this.currentLeaf.values.size()) {
									this.nextIndex++;
									this.next = this.currentLeaf.values.get(this.nextIndex - 1);
									searching = false;
								} else {
									this.currentLeaf = nextLeaf(this.currentLeaf);
									if (this.currentLeaf == null) {
										this.next = null;
										searching = false;
									} else {
										this.nextIndex = 0;
									}
								}
							}
						}
						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
					return iterator;
				}
				@Override
				public int size() {
					return QuadTree.this.size;
				}
			};
		}
		return this.values;
	}

	private QuadLeaf<T> firstLeaf() {
		return this.root.firstLeaf();
	}

	private QuadLeaf<T> nextLeaf(QuadLeaf<T> currentLeaf) {
		return this.root.nextLeaf(currentLeaf);
	}

	interface Executor<T>{	
		public void execute(double x, double y, T object); 
	}
}