package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Tangentobject;
import net.imglib2.KDTree;
import net.imglib2.RealPoint;

public class NearestNeighbourSearch implements IntersectionTracker {

	private final HashMap<String, ArrayList<Tangentobject>> ALLIntersections;
	private SimpleWeightedGraph<Tangentobject, DefaultWeightedEdge> graph;
	protected String errorMessage;
	private final int z;
	private final int fourthDimSize;

	public NearestNeighbourSearch(final HashMap<String, ArrayList<Tangentobject>> ALLIntersections, final int z,
			final int fourthDimSize) {

		this.ALLIntersections = ALLIntersections;
		this.z = z;
		this.fourthDimSize = fourthDimSize;

	}

	@Override
	public SimpleWeightedGraph<Tangentobject, DefaultWeightedEdge> getResult() {

		return graph;
	}

	@Override
	public boolean checkInput() {

		return true;
	}

	@Override
	public boolean process() {

		for (int t = 1; t < fourthDimSize - 1; ++t) {

			String uniqueID = Integer.toString(z) + Integer.toString(t);
			String uniqueIDnext = Integer.toString(z) + Integer.toString(t + 1);

			ArrayList<Tangentobject> baseobject = ALLIntersections.get(uniqueID);
			ArrayList<Tangentobject> targetobject = ALLIntersections.get(uniqueIDnext);

			Iterator<Tangentobject> baseobjectiterator = baseobject.iterator();

			final int Targetintersections = targetobject.size();

			final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Targetintersections);

			final List<FlagNode<Tangentobject>> targetNodes = new ArrayList<FlagNode<Tangentobject>>(
					Targetintersections);

			for (int index = 0; index < baseobject.size(); ++index) {

				ArrayList<double[]> cordlist = baseobject.get(index).Intersections;

				for (int i = 0; i < cordlist.size(); ++i) {
					targetCoords.add(new RealPoint(cordlist.get(i)));
					targetNodes.add(new FlagNode<Tangentobject>(targetobject.get(i)));

				}

			}

			if (targetNodes.size() > 0 && targetCoords.size() > 0) {

				final KDTree<FlagNode<Tangentobject>> Tree = new KDTree<FlagNode<Tangentobject>>(targetNodes,
						targetCoords);

				final NNFlagsearchKDtree<Tangentobject> Search = new NNFlagsearchKDtree<Tangentobject>(Tree);

				while (baseobjectiterator.hasNext()) {

					final Tangentobject source = baseobjectiterator.next();

					ArrayList<double[]> cordlist = source.Intersections;

					for (int i = 0; i < cordlist.size(); ++i) {
						final RealPoint sourceCoords = new RealPoint(cordlist.get(i));
						Search.search(sourceCoords);
						final double squareDist = Search.getSquareDistance();
						final FlagNode<Tangentobject> targetNode = Search.getSampler().get();

						targetNode.setVisited(true);

						synchronized (graph) {

							graph.addVertex(source);
							graph.addVertex(targetNode.getValue());
							final DefaultWeightedEdge edge = graph.addEdge(source, targetNode.getValue());
							graph.setEdgeWeight(edge, squareDist);

						}

					}

				}

			}

		}

		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

}
