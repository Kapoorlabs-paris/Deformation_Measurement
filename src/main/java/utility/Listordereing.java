package utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
	
	
	public static List<RealLocalizable> getCopyList(List<RealLocalizable> copytruths) {
		
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		Iterator<RealLocalizable> iter = copytruths.iterator();
		
		while(iter.hasNext()) {
			
			orderedtruths.add(iter.next());
			
			
			
		}
		
		return orderedtruths;
	}
	
	
	public static List<RealLocalizable> getList(
			List<RealLocalizable> truths, int index) {
		
		
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		
		for (int i = index; i < truths.size() ; ++i) {
			
			
			orderedtruths.add(truths.get(i));
			
		}
	     for (int i = 0; i < index ; ++i) {
			
			
			orderedtruths.add(truths.get(i));
			
		}
		
		
		return orderedtruths;
		
	}
	
	/**
	 * Return an ordered list of XY coordinates starting from the min X position to
	 * the end of the list
	 * 
	 * 
	 * @param truths
	 * @return
	 */

	public static List<RealLocalizable> getOrderedList(
			List<RealLocalizable> truths) {

		
		
		List<RealLocalizable> copytruths = getCopyList(truths);
		List<RealLocalizable> orderedtruths = new ArrayList<RealLocalizable>();
		// Get the starting minX and minY co-ordinates
		RealLocalizable minCord = getMinCord(copytruths);

		
		
		RealLocalizable meanCord = getMeanCord(copytruths);
             orderedtruths.add(minCord);
             copytruths.remove(minCord);
		do {
		
			
			RealLocalizable nextCord = getNextNearest(minCord, copytruths);
			copytruths.remove(nextCord);
			if(copytruths.size()!=0) {
			RealLocalizable secondnextCord = getNextNearest(minCord, copytruths);
			copytruths.add(nextCord);
			
			double nextangle = Distance.AngleVectors(minCord, nextCord, meanCord);
			double secondnextangle = Distance.AngleVectors(minCord, secondnextCord, meanCord);
			RealLocalizable chosenCord = null;
			
			if(nextangle >= 0 && secondnextangle >= 0 && nextangle <= secondnextangle)
				chosenCord = nextCord;
			if(nextangle >= 0 && secondnextangle >= 0 && nextangle > secondnextangle)
				chosenCord = secondnextCord;
			
			if(nextangle < 0 && secondnextangle > 0)
				chosenCord = nextCord;
			if(nextangle > 0 && secondnextangle < 0)
				chosenCord = secondnextCord;
			
			else if (nextangle < 0 || secondnextangle < 0)
			
			chosenCord = (nextangle >= secondnextangle) ? nextCord: secondnextCord; 
			

			minCord = chosenCord;
			orderedtruths.add(minCord);
			
		
			copytruths.remove(chosenCord);
			}
			else break;
		} while (copytruths.size() > 1);

		
		return orderedtruths;
	}
	
	/**
	 * Return an ordered list of XY coordinates starting from the min X position to
	 * the end of the list
	 * 
	 * 
	 * @param truths
	 * @return
	 */



	public static  List<RealLocalizable> copyList(
			List<RealLocalizable> truths) {

		List<RealLocalizable> copytruths = new ArrayList<RealLocalizable>();

		Iterator<RealLocalizable> iterator = truths.iterator();

		while (iterator.hasNext()) {

			RealLocalizable nextvalue = iterator.next();

			copytruths.add(nextvalue);

		}

		return copytruths;
	}

	/**
	 * 
	 * Get the mean XY co-ordinates from the list
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getMeanCord(
			List<RealLocalizable> truths) {

		Iterator<RealLocalizable> iter = truths.iterator();
        double Xmean = 0, Ymean = 0;
		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			RealLocalizable currentpoint = currentpair;

			Xmean+= currentpoint.getDoublePosition(0);
			Ymean+= currentpoint.getDoublePosition(1);
			
			

		}
		RealPoint meanCord = new RealPoint(new double[] {Xmean / truths.size(), Ymean / truths.size()});
		
		

		return meanCord;
	}
	
	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list, start from minX and minY
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getRandomCord(
			List<RealLocalizable> truths, int index) {


	
			
		RealLocalizable minobject = truths.get(index);
			
			
		
	

		return minobject;
	}
	/**
	 * 
	 * Get the starting XY co-ordinates to create an ordered list, start from minX and minY
	 * 
	 * @param truths
	 * @return
	 */

	public static RealLocalizable getMinCord(
			List<RealLocalizable> truths) {

		RealPoint minCord = new RealPoint(new double[] { Double.MAX_VALUE, Double.MAX_VALUE });
		RealLocalizable minobject = null;
		Iterator<RealLocalizable> iter = truths.iterator();

		while (iter.hasNext()) {

			RealLocalizable currentpair = iter.next();

			RealLocalizable currentpoint = currentpair;

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

	public static  RealLocalizable getNextNearest(
			RealLocalizable minCord, List<RealLocalizable> truths) {

		RealLocalizable nextobject = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(truths.size());
		final List<FlagNode<RealLocalizable>> targetNodes = new ArrayList<FlagNode<RealLocalizable>>(
				truths.size());

		for (RealLocalizable localcord : truths) {

			targetCoords.add(new RealPoint(localcord));
			targetNodes.add(new FlagNode<RealLocalizable>(localcord));
		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<RealLocalizable>> Tree = new KDTree<FlagNode<RealLocalizable>>(
					targetNodes, targetCoords);

			final NNFlagsearchKDtree<RealLocalizable> Search = new NNFlagsearchKDtree<RealLocalizable>(
					Tree);

			Search.search(minCord);

			final FlagNode<RealLocalizable> targetNode = Search.getSampler().get();

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
