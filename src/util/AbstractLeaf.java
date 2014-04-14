package util;

import tree.QuadLeaf;

/**
 * @author Kirsty Williams
 * Email: me@kirstywilliams.co.uk
 * Date Created: August, 2012
 * @version 1.0
 */

public class AbstractLeaf<T> {
	public QuadLeaf<T> value;

	public AbstractLeaf(QuadLeaf<T> value) {
		this.value = value;
	}
}