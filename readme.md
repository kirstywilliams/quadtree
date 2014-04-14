#QuadTree
####The QuadTree herein was implemented as part of a graph drawing project back in August, 2012. A description can be found on my [Blog: Quadtrees for Space Decomposition](http://www.kirstywilliams.co.uk/blog/2012/08/quadtrees-java-implementation "Blog post").


##1. Usage

The QuadTree is generic so has a wide range of possible usage. Although, it was developed to be used as a component of the [FADE](http://aquigley.host.cs.st-andrews.ac.uk/?Research:Past_Projects:FADE) drawing algorithm for the deconstruction of a graph on a 2D plane.

**Example:**

```java
//initialises the QuadTree with the graph bounds, and sets as root node
QuadTree tree = new Quadtree(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());

//assigns each vertex to the node containing its coordinates
//continually increasing the size of the assigned node
//if node exceeds a size of 4, node is recursively split.
for(Vertex v : V)
	 tree.put(v.getX(), v.getY(), v);
```

##2. Authors

* [Kirsty Williams](http://www.github.com/kirstywilliams "Kirsty Williams Github")