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
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import track.TrackingFunctions;
import utility.CreateTable;
import utility.Curvatureobject;
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

			//	SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

			//	parent.parentgraphZ.put(Integer.toString(z), simplegraph);
				
				SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> simpleSegmentgraph = track.TrackSegmentfunction();

				parent.parentgraphSegZ.put(Integer.toString(z), simpleSegmentgraph);

			}

			// CurvedLineage();

			CurvedSegmentLineage();
		}

		else {

		//	SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

		//	parent.parentgraphZ.put(Integer.toString(1), simplegraph);

			SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge> simpleSegmentgraph = track.TrackSegmentfunction();

			parent.parentgraphSegZ.put(Integer.toString(1), simpleSegmentgraph);

			// CurvedLineage();

			CurvedSegmentLineage();
		}

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public void CurvedLineage() {

		if (parent.ndims < 3) {
			DisplaySelected.mark(parent);
			DisplaySelected.select(parent);

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

								return A.getB().time - B.getB().time;

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
					

					}

					for (int id = minid; id <= maxid; ++id) {
						Segmentobject bestangle = null;
						
						if (model.trackSegmentobjects(id) != null) {
							List<Segmentobject> sortedList = new ArrayList<Segmentobject>(
									model.trackSegmentobjects(id));

							Collections.sort(sortedList, new Comparator<Segmentobject>() {

								@Override
								public int compare(Segmentobject o1, Segmentobject o2) {

									return o1.time - o2.time;
								}

							});

							Iterator<Segmentobject> iterator = sortedList.iterator();

							int count = 0;
							while (iterator.hasNext()) {

								Segmentobject currentangle = iterator.next();
								if (count == 0)
									bestangle = currentangle;
								if (parent.originalimg.numDimensions() > 3) {
									if (currentangle.time == parent.fourthDimension) {
										bestangle = currentangle;
										count++;
										break;
									}
								} else if (parent.originalimg.numDimensions() <= 3) {
									if (currentangle.time == parent.thirdDimension) {
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

}