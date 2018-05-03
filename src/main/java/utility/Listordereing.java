package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ellipsoidDetector.Distance;
import mpicbg.models.Point;
import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;

public class Listordereing {

	
	//@VKapoor
	
	/**
	 * Return an ordered list of XY coordinates starting from the min X position to
	 * the end of the list
	 * 
	 * 
	 * @param truths
	 * @return
	 */

	public static <T extends RealType<T> & NativeType<T>> List<RealLocalizable> getOrderedList(
			List<Pair<RealLocalizable, T>> truths, double deltasep) {

		List<Pair<RealLocalizable, T>> copytruths = copyList(truths);
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		Pair<RealLocalizable, T> minCord = getMinCord(copytruths);

             orderedtruths.add(minCord.getA());
             copytruths.remove(minCord);
		do {
		
			
			Pair<RealLocalizable, T> nextCord = getNextNearest(minCord.getA(), copytruths);
			if (Distance.DistanceSqrt(minCord.getA(), nextCord.getA()) > deltasep) {
			minCord = nextCord;
			orderedtruths.add(minCord.getA());
			}
			copytruths.remove(nextCord);
			
		} while (copytruths.size() > 0);

		
		
		return orderedtruths;
	}
	


	public static <T extends RealType<T> & NativeType<T>> List<Pair<RealLocalizable, T>> copyList(
			List<Pair<RealLocalizable, T>> truths) {

		List<Pair<RealLocalizable, T>> copytruths = new ArrayList<Pair<RealLocalizable, T>>();

		Iterator<Pair<RealLocalizable, T>> iterator = truths.iterator();

		while (iterator.hasNext()) {

			Pair<RealLocalizable, T> nextvalue = iterator.next();

			copytruths.add(nextvalue);

		}

		return copytruths;
	}

	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list
	 * 
	 * @param truths
	 * @return
	 */

	public static <T extends RealType<T> & NativeType<T>> Pair<RealLocalizable, T> getMinCord(
			List<Pair<RealLocalizable, T>> truths) {

		RealPoint minCord = new RealPoint(new double[] { Double.MAX_VALUE, Double.MAX_VALUE });
		Pair<RealLocalizable, T> minobject = null;
		Iterator<Pair<RealLocalizable, T>> iter = truths.iterator();

		while (iter.hasNext()) {

			Pair<RealLocalizable, T> currentpair = iter.next();

			RealLocalizable currentpoint = currentpair.getA();

			if (currentpoint.getDoublePosition(0) <= minCord.getDoublePosition(0)
					&& currentpoint.getDoublePosition(1) <= minCord.getDoublePosition(1)) {

				minCord = new RealPoint(currentpoint);
				minobject = currentpair;

			}

		}

		return minobject;
	}

	/**
	 * 
	 * 
	 * Get the Next nearest point in the list
	 * 
	 * @param minCord
	 * @param truths
	 * @return
	 */

	public static <T extends RealType<T> & NativeType<T>> Pair<RealLocalizable, T> getNextNearest(
			RealLocalizable minCord, List<Pair<RealLocalizable, T>> truths) {

		Pair<RealLocalizable, T> nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<Pair<RealLocalizable, T>>> targetNodes = new ArrayList<FlagNode<Pair<RealLocalizable, T>>>(
				truths.size());

		for (Pair<RealLocalizable, T> localcord : truths) {

			targetCoords.add(new RealPoint(localcord.getA()));
			targetNodes.add(new FlagNode<Pair<RealLocalizable, T>>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Pair<RealLocalizable, T>>> Tree = new KDTree<FlagNode<Pair<RealLocalizable, T>>>(
					targetNodes, targetCoords);

			final NNFlagsearchKDtree<Pair<RealLocalizable, T>> Search = new NNFlagsearchKDtree<Pair<RealLocalizable, T>>(
					Tree);

			Search.search(minCord);

			final FlagNode<Pair<RealLocalizable, T>> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}
	
	/**
	 * 
	 * 
	 * Get the Next nearest point in the list
	 * 
	 * @param minCord
	 * @param truths
	 * @return
	 */

	public static Pair<RealLocalizable, Double> getNextNearestPoint(
			RealLocalizable minCord, List<Pair<RealLocalizable, Double>> truths) {

		Pair<RealLocalizable, Double> nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<Pair<RealLocalizable, Double>>> targetNodes = new ArrayList<FlagNode<Pair<RealLocalizable, Double>>>(
				truths.size());

		for (Pair<RealLocalizable, Double> localcord : truths) {

			targetCoords.add(new RealPoint(localcord.getA()));
			targetNodes.add(new FlagNode<Pair<RealLocalizable, Double>>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Pair<RealLocalizable, Double>>> Tree = new KDTree<FlagNode<Pair<RealLocalizable, Double>>>(
					targetNodes, targetCoords);

			final NNFlagsearchKDtree<Pair<RealLocalizable, Double>> Search = new NNFlagsearchKDtree<Pair<RealLocalizable, Double>>(
					Tree);

			Search.search(minCord);

			final FlagNode<Pair<RealLocalizable, Double>> targetNode = Search.getSampler().get();

			nextobject = targetNode.getValue();
		}

		return nextobject;

	}

}
