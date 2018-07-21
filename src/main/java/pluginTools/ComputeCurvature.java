package pluginTools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import costMatrix.PixelratiowDistCostFunction;
import curvatureUtils.DisplaySelected;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import hashMapSorter.SortTimeorZ;
import ij.ImageStack;
import ij.gui.Line;
import kalmanForSegments.Segmentobject;
import kalmanForSegments.TrackSegmentModel;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.NearestNeighbourSearch;
import kalmanTracker.NearestNeighbourSearch2D;
import kalmanTracker.TrackModel;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import track.TrackingFunctions;
import utility.CreateTable;
import utility.Curvatureobject;
import utility.Listordereing;
import utility.Roiobject;
import utility.ThreeDRoiobject;

public class ComputeCurvature extends SwingWorker<Void, Void> {

	final InteractiveSimpleEllipseFit parent;
	final JProgressBar jpb;

	public ComputeCurvature(final InteractiveSimpleEllipseFit parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {

		HashMap<String, Integer> map = SortTimeorZ.sortByValues(parent.Accountedframes);
		parent.Accountedframes = map;

		HashMap<String, Integer> mapZ = SortTimeorZ.sortByValues(parent.AccountedZ);
		parent.AccountedZ = mapZ;

		EllipseTrack newtrack = new EllipseTrack(parent, jpb);
		newtrack.ComputeCurvature();

		parent.inputField.setEnabled(false);
		parent.inputtrackField.setEnabled(false);
		parent.Savebutton.setEnabled(false);
		parent.SaveAllbutton.setEnabled(false);
		parent.ChooseDirectory.setEnabled(false);

		return null;

	}

	public void MakeKymo(HashMap<String, ArrayList<Segmentobject>> sortedMappair, long[] size, int CellLabel) {

		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

		RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

		while (itZ.hasNext()) {

			Map.Entry<String, Integer> entry = itZ.next();
			String currentID = entry.getKey();

			int time = entry.getValue();

			ArrayList<Segmentobject> currentlist = sortedMappair.get(currentID);

			ranac.setPosition(time, 0);

			int count = 0;

			for (Segmentobject currentobject : currentlist) {

				ranac.setPosition(count, 1);
				ranac.get().setReal(currentobject.Curvature);
				count++;
				System.out.println(currentobject.z + "time unit" + " " + currentobject.centralpoint.getDoublePosition(0)
						+ " " + currentobject.centralpoint.getDoublePosition(1) + "Check if arranging points correct");
			}

		}

		ImageJFunctions.show(CurvatureKymo).setTitle("Curvature Kymo for Cell Label: " + CellLabel);

	}

	public void MakeInterKymo(HashMap<String, ArrayList<Intersectionobject>> sortedMappair, long[] size,
			int CellLabel) {

		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

		RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

		while (itZ.hasNext()) {

			Map.Entry<String, Integer> entry = itZ.next();
			String currentID = entry.getKey();

			int time = entry.getValue();

			ArrayList<Intersectionobject> currentlist = sortedMappair.get(currentID);

			ranac.setPosition(time, 0);

			for (Intersectionobject currentobject : currentlist) {
				int count = 0;
				ArrayList<double[]> linelist = currentobject.linelist;
				System.out.println(linelist.size() + " Should be number of points");
				for (int i = 0; i < linelist.size(); ++i) {

					ranac.setPosition(count, 1);
					ranac.get().set((float) currentobject.linelist.get(i)[2]);

					// System.out.println(currentobject.z + "time unit" + " " +
					// currentobject.linelist.get(i)[0]
					// + " " + currentobject.linelist.get(i)[1] + "Check if arranging points
					// correct");
					count++;
				}

			}

		}

		ImageJFunctions.show(CurvatureKymo).setTitle("Curvature Kymo for Cell Label: " + CellLabel);

	}

	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		parent.resultDraw.clear();
		parent.Tracklist.clear();
		parent.SegmentTracklist.clear();
		parent.table.removeAll();

		TrackingFunctions track = new TrackingFunctions(parent);
		if (parent.ndims > 3) {

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

			while (itZ.hasNext()) {

				int z = itZ.next().getValue();

				if (parent.celltrackcirclefits) {

					SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

					parent.parentgraphZ.put(Integer.toString(z), simplegraph);

					CurvedLineage();

				}
				if (parent.circlefits) {

					SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> simpleSegmentgraph = track
							.TrackSegmentfunction();

					parent.parentgraphSegZ.put(Integer.toString(z), simpleSegmentgraph);

					CurvedSegmentLineage();
				}
			}

		}

		else {

			if (parent.celltrackcirclefits) {

				SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

				parent.parentgraphZ.put(Integer.toString(1), simplegraph);

				CurvedLineage();

				Pair<HashMap<Integer, Integer>, HashMap<String, ArrayList<Intersectionobject>>> sortedMappair = GetZTTrackList(
						parent);
				int TimedimensionKymo = parent.AccountedZ.size() + 1;
				HashMap<Integer, Integer> idmap = sortedMappair.getA();

				Iterator<Map.Entry<Integer, Integer>> it = idmap.entrySet().iterator();
				while (it.hasNext()) {

					Map.Entry<Integer, Integer> mapentry = it.next();
					int id = mapentry.getKey();

					int Xkymodimension = mapentry.getValue();

					long[] size = new long[] { TimedimensionKymo, Xkymodimension };
					System.out.println(Xkymodimension + " X dimension");
					MakeInterKymo(sortedMappair.getB(), size, id);

				}
			}

		}

		if (parent.circlefits) {

			SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> simpleSegmentgraph = track.TrackSegmentfunction();

			parent.parentgraphSegZ.put(Integer.toString(1), simpleSegmentgraph);
			CurvedSegmentLineage();
			Pair<HashMap<Integer, Integer>, HashMap<String, ArrayList<Segmentobject>>> sortedMappair = GetZTSegTrackList(
					parent);
			int TimedimensionKymo = parent.AccountedZ.size() + 1;
			HashMap<Integer, Integer> idmap = sortedMappair.getA();

			Iterator<Map.Entry<Integer, Integer>> it = idmap.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry<Integer, Integer> mapentry = it.next();
				int id = mapentry.getKey();

				int Xkymodimension = mapentry.getValue();

				long[] size = new long[] { TimedimensionKymo, Xkymodimension };

				MakeKymo(sortedMappair.getB(), size, id);

			}
		}

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public void CurvedLineage() {

		if (parent.ndims < 3) {
			if (parent.circlefits) {

				DisplaySelected.mark(parent);
				DisplaySelected.select(parent);
			}

			if (parent.celltrackcirclefits || parent.pixelcelltrackcirclefits) {
				DisplaySelected.markAll(parent);
				DisplaySelected.selectAll(parent);

			}

			for (ArrayList<Curvatureobject> local : parent.AlllocalCurvature) {
				Iterator<Curvatureobject> iterator = local.iterator();

				while (iterator.hasNext()) {

					Curvatureobject currentcurvature = iterator.next();

					if (parent.originalimg.numDimensions() > 3) {
						if (currentcurvature.t == parent.fourthDimension) {
							parent.Finalcurvatureresult.put(currentcurvature.Label, currentcurvature);
						}
					} else if (parent.originalimg.numDimensions() <= 3) {
						if (currentcurvature.z == parent.thirdDimension) {
							parent.Finalcurvatureresult.put(currentcurvature.Label, currentcurvature);

						}

					}

				}
			}
			curvatureUtils.CurvatureTable.CreateTableView(parent);

		}

		else {

			for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parent.parentgraphZ
					.entrySet()) {

				TrackModel model = new TrackModel(entryZ.getValue());

				int minid = Integer.MAX_VALUE;
				int maxid = Integer.MIN_VALUE;

				for (final Integer id : model.trackIDs(true)) {

					if (id > maxid)
						maxid = id;

					if (id < minid)
						minid = id;

				}

				if (minid != Integer.MAX_VALUE) {

					for (final Integer id : model.trackIDs(true)) {

						Comparator<Pair<String, Intersectionobject>> ThirdDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

							@Override
							public int compare(final Pair<String, Intersectionobject> A,
									final Pair<String, Intersectionobject> B) {

								return A.getB().z - B.getB().z;

							}

						};

						Comparator<Pair<String, Intersectionobject>> FourthDimcomparison = new Comparator<Pair<String, Intersectionobject>>() {

							@Override
							public int compare(final Pair<String, Intersectionobject> A,
									final Pair<String, Intersectionobject> B) {

								return A.getB().t - B.getB().t;

							}

						};

						model.setName(id, "Track" + id + entryZ.getKey());

						final HashSet<Intersectionobject> Angleset = model.trackIntersectionobjects(id);

						Iterator<Intersectionobject> Angleiter = Angleset.iterator();

						while (Angleiter.hasNext()) {

							Intersectionobject currentangle = Angleiter.next();
							parent.Tracklist.add(new ValuePair<String, Intersectionobject>(
									Integer.toString(id) + entryZ.getKey(), currentangle));
						}
						Collections.sort(parent.Tracklist, ThirdDimcomparison);
						if (parent.fourthDimensionSize > 1)
							Collections.sort(parent.Tracklist, FourthDimcomparison);
						parent.Tracklist = Listordereing.getOrderedIntersectionList(parent.Tracklist);
					}

					for (int id = minid; id <= maxid; ++id) {
						Intersectionobject bestangle = null;
						if (model.trackIntersectionobjects(id) != null) {

							List<Intersectionobject> sortedList = new ArrayList<Intersectionobject>(
									model.trackIntersectionobjects(id));

							Collections.sort(sortedList, new Comparator<Intersectionobject>() {

								@Override
								public int compare(Intersectionobject o1, Intersectionobject o2) {

									return o1.t - o2.t;
								}

							});

							Collections.sort(sortedList, new Comparator<Intersectionobject>() {

								@Override
								public int compare(Intersectionobject o1, Intersectionobject o2) {

									return o1.z - o2.z;
								}

							});

							Iterator<Intersectionobject> iterator = sortedList.iterator();

							int count = 0;
							while (iterator.hasNext()) {

								Intersectionobject currentangle = iterator.next();
								if (count == 0)
									bestangle = currentangle;
								if (parent.originalimg.numDimensions() > 3) {
									if (currentangle.t == parent.fourthDimension) {
										bestangle = currentangle;
										count++;
										break;
									}
								} else if (parent.originalimg.numDimensions() <= 3) {
									if (currentangle.z == parent.thirdDimension) {
										bestangle = currentangle;
										count++;
										break;

									}

								}

							}
							parent.Finalresult.put(Integer.toString(id) + entryZ.getKey(), bestangle);

						}

					}
				}
			}
			curvatureUtils.CurvatureTable.CreateTableTrackView(parent);

		}

	}

	public void CurvedSegmentLineage() {
		if (parent.circlefits) {
			DisplaySelected.mark(parent);
			DisplaySelected.select(parent);
		}

		if (parent.celltrackcirclefits || parent.pixelcelltrackcirclefits) {

			DisplaySelected.markAll(parent);
			DisplaySelected.selectAll(parent);

		}
		if (parent.ndims >= 3) {

			for (Map.Entry<String, SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge>> entryZ : parent.parentgraphSegZ
					.entrySet()) {

				TrackSegmentModel model = new TrackSegmentModel(entryZ.getValue());

				int minid = Integer.MAX_VALUE;
				int maxid = Integer.MIN_VALUE;

				for (final Integer id : model.trackIDs(true)) {

					if (id > maxid)
						maxid = id;

					if (id < minid)
						minid = id;

				}

				if (minid != Integer.MAX_VALUE) {

					for (final Integer id : model.trackIDs(true)) {

						Comparator<Pair<String, Segmentobject>> ThirdDimcomparison = new Comparator<Pair<String, Segmentobject>>() {

							@Override
							public int compare(final Pair<String, Segmentobject> A,
									final Pair<String, Segmentobject> B) {

								return A.getB().z - B.getB().z;

							}

						};

						Comparator<Pair<String, Segmentobject>> FourthDimcomparison = new Comparator<Pair<String, Segmentobject>>() {

							@Override
							public int compare(final Pair<String, Segmentobject> A,
									final Pair<String, Segmentobject> B) {

								return A.getB().t - B.getB().t;

							}

						};

						model.setName(id, "Track" + id + entryZ.getKey());

						final HashSet<Segmentobject> Angleset = model.trackSegmentobjects(id);

						Iterator<Segmentobject> Angleiter = Angleset.iterator();

						while (Angleiter.hasNext()) {

							Segmentobject currentangle = Angleiter.next();
							parent.SegmentTracklist.add(new ValuePair<String, Segmentobject>(
									Integer.toString(id) + entryZ.getKey(), currentangle));
						}
						Collections.sort(parent.SegmentTracklist, ThirdDimcomparison);
						parent.SegmentTracklist = Listordereing.getOrderedSegList(parent.SegmentTracklist);
					}

					for (int id = minid; id <= maxid; ++id) {
						Segmentobject bestangle = null;

						if (model.trackSegmentobjects(id) != null) {
							List<Segmentobject> sortedList = new ArrayList<Segmentobject>(
									model.trackSegmentobjects(id));

							Collections.sort(sortedList, new Comparator<Segmentobject>() {

								@Override
								public int compare(Segmentobject o1, Segmentobject o2) {

									return o1.t - o2.t;
								}

							});
							Collections.sort(sortedList, new Comparator<Segmentobject>() {

								@Override
								public int compare(Segmentobject o1, Segmentobject o2) {

									return o1.z - o2.z;
								}

							});

							Iterator<Segmentobject> iterator = sortedList.iterator();

							int count = 0;
							while (iterator.hasNext()) {

								Segmentobject currentangle = iterator.next();
								if (count == 0)
									bestangle = currentangle;
								if (parent.originalimg.numDimensions() > 3) {
									if (currentangle.t == parent.fourthDimension) {
										bestangle = currentangle;
										count++;
										break;
									}
								} else if (parent.originalimg.numDimensions() <= 3) {
									if (currentangle.z == parent.thirdDimension) {
										bestangle = currentangle;
										count++;
										break;

									}

								}

							}
							parent.SegmentFinalresult.put(Integer.toString(id) + entryZ.getKey(), bestangle);

						}

					}
				}
			}

			curvatureUtils.CurvatureTable.CreateSegTableTrackView(parent);

		}

	}

	public static Pair<HashMap<Integer, Integer>, HashMap<String, ArrayList<Segmentobject>>> GetZTSegTrackList(
			final InteractiveSimpleEllipseFit parent) {

		int maxCurveDim = 0;

		HashMap<Integer, Integer> maxidcurve = new HashMap<Integer, Integer>();
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
		HashMap<String, ArrayList<Segmentobject>> sortedMap = new HashMap<String, ArrayList<Segmentobject>>();
		while (itZ.hasNext()) {
			ArrayList<Segmentobject> currentframeobject = new ArrayList<Segmentobject>();
			Map.Entry<String, Integer> entry = itZ.next();

			
			int z = entry.getValue();
			
			int minid = Integer.MAX_VALUE;
			int maxid = Integer.MIN_VALUE;

			for (Pair<String, Segmentobject> currentangle : parent.SegmentTracklist) {

				if (currentangle.getB().z == z) {

					currentframeobject.add(currentangle.getB());

				}

				for (final Segmentobject Allsegments : currentframeobject) {

					if (Allsegments.cellLabel > maxid)
						maxid = Allsegments.cellLabel;

					if (Allsegments.cellLabel < minid)
						minid = Allsegments.cellLabel;

				}

			}

			for (int id = minid; id <= maxid; ++id) {

				if (currentframeobject.size() > maxCurveDim) {

					maxCurveDim = currentframeobject.size();

				}

				String UniqueID = entry.getKey();

				sortedMap.put(UniqueID, currentframeobject);
				maxidcurve.put(id, maxCurveDim);
			}
		}
		return new ValuePair<HashMap<Integer, Integer>, HashMap<String, ArrayList<Segmentobject>>>(maxidcurve,
				sortedMap);

	}

	public static Pair<HashMap<Integer, Integer>, HashMap<String, ArrayList<Intersectionobject>>> GetZTTrackList(
			final InteractiveSimpleEllipseFit parent) {

		int maxCurveDim = 0;

		HashMap<Integer, Integer> maxidcurve = new HashMap<Integer, Integer>();
		
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
		
		
		HashMap<String, ArrayList<Intersectionobject>> sortedMap = new HashMap<String, ArrayList<Intersectionobject>>();
		
		
		while (itZ.hasNext()) {
			ArrayList<Intersectionobject> currentframeobject = new ArrayList<Intersectionobject>();
			Map.Entry<String, Integer> entry = itZ.next();

			int z = entry.getValue();
			
			int minid = Integer.MAX_VALUE;
			int maxid = Integer.MIN_VALUE;

			for (Pair<String, Intersectionobject> currentangle : parent.Tracklist) {

				if (currentangle.getB().z == z) {

					currentframeobject.add(currentangle.getB());

				}

				for (final Intersectionobject Allsegments : currentframeobject) {

					if (Allsegments.celllabel > maxid)
						maxid = Allsegments.celllabel;

					if (Allsegments.celllabel < minid)
						minid = Allsegments.celllabel;

				}

			}

			for (int id = minid; id <= maxid; ++id) {


					for (int i = 0; i < currentframeobject.size(); ++i) {

						int size = currentframeobject.get(i).linelist.size();

						if (size > maxCurveDim)
							maxCurveDim = size;

					}


				String UniqueID = entry.getKey();

				sortedMap.put(UniqueID, currentframeobject);
				maxidcurve.put(id, maxCurveDim);

			}
		}
		return new ValuePair<HashMap<Integer, Integer>, HashMap<String, ArrayList<Intersectionobject>>>(maxidcurve,
				sortedMap);

	}

	/**
	 * 
	 * A special sorting scheme for segments to be sorted based on closeness to the
	 * refence point
	 * 
	 * @param parent
	 * @param segobject
	 * @param t
	 * @param z
	 * @return
	 */
	public static ArrayList<Pair<String, Segmentobject>> OrderCords(final InteractiveSimpleEllipseFit parent,
			final ArrayList<Pair<String, Segmentobject>> segobject, String UniqueID) {

		ArrayList<Pair<String, Segmentobject>> Sortedsegobject = Listordereing.getOrderedSegList(segobject);

		return Sortedsegobject;

	}
}