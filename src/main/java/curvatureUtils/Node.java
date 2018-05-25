package curvatureUtils;

import java.util.List;

public class Node <RealLocalizable> {
	
	public final RealLocalizable nodePoint;
	public final int depth;
	public final List<RealLocalizable> leftTree;
	public final List<RealLocalizable> rightTree;
	
	
	public Node (final RealLocalizable nodePoint,final List<RealLocalizable> leftTree,final List<RealLocalizable> rightTree, int depth ){
		
		this.nodePoint = nodePoint;
		this.leftTree = leftTree;
		this.rightTree = rightTree;
		this.depth = depth;
		
	}
	
	
	

}
