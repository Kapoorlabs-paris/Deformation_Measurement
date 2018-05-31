package curvatureUtils;

import java.util.List;

public class Node <RealLocalizable> {
	
	public final RealLocalizable nodePoint;
	public final int depth;
	public final List<RealLocalizable> leftTree;
	public final List<RealLocalizable> rightTree;
	public final List<RealLocalizable> parent;
	
	/**
	 * Convert a list of points into a tree structure, the constructor contains the split point, the left and right tree and the
	 * depth of the tree
	 * 
	 * 
	 * @param nodePoint
	 * @param leftTree
	 * @param rightTree
	 * @param depth
	 */
	
	public Node (final RealLocalizable nodePoint, final List<RealLocalizable> parent, final List<RealLocalizable> leftTree,final List<RealLocalizable> rightTree, int depth ){
		
		this.nodePoint = nodePoint;
		this.leftTree = leftTree;
		this.rightTree = rightTree;
		this.parent = parent;
		this.depth = depth;
		
	}
	
	
	

}
