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

import com.google.common.math.Quantiles.Scale;

import costMatrix.PixelratiowDistCostFunction;
import curvatureUtils.DisplaySelected;
import ellipsoidDetector.Distance;
import ellipsoidDetector.Intersectionobject;
import ellipsoidDetector.KymoSaveobject;
import hashMapSorter.SortTimeorZ;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Line;
import ij.measure.Calibration;
import kalmanForSegments.Segmentobject;
import kalmanForSegments.TrackSegmentModel;
import kalmanTracker.ETrackCostFunction;
import kalmanTracker.IntersectionobjectCollection;
import kalmanTracker.KFsearch;
import kalmanTracker.NearestNeighbourSearch;
import kalmanTracker.NearestNeighbourSearch2D;
import kalmanTracker.TrackModel;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.Scale2D;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveSimpleEllipseFit.ValueChange;
import track.TrackingFunctions;
import utility.CreateTable;
import utility.Curvatureobject;
import utility.FlagNode;
import utility.Listordereing;
import utility.NNFlagsearchKDtree;
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

	public void MakeKymo(HashMap<String, ArrayList<Segmentobject>> sortedMappair, long[] size, String TrackID) {

		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityAKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityBKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());

		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

		RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

		RandomAccess<FloatType> ranacimageA = IntensityAKymo.randomAccess();

		RandomAccess<FloatType> ranacimageB = IntensityBKymo.randomAccess();

		while (itZ.hasNext()) {

			Map.Entry<String, Integer> entry = itZ.next();

			int time = entry.getValue();

			String timeID = entry.getKey();

			ArrayList<Segmentobject> currentlist = sortedMappair.get(TrackID + timeID);

			ranac.setPosition(time, 0);
			ranacimageA.setPosition(time, 0);
			ranacimageB.setPosition(time, 0);
			int count = 1;
			if (currentlist != null) {
				for (Segmentobject currentobject : currentlist) {
					

					ranac.setPosition(count, 1);
					ranac.get().setReal(currentobject.Curvature);

					ranacimageA.setPosition(count, 1);
					ranacimageA.get().setReal(currentobject.IntensityA);

					ranacimageB.setPosition(count, 1);
					ranacimageB.get().setReal(currentobject.IntensityB);

					count++;

				}
		}
		}

		double[] calibration = new double[] { parent.timecal, parent.calibration };
		Calibration cal = new Calibration();
		cal.setFunction(Calibration.STRAIGHT_LINE, calibration, "s um");
		ImagePlus Curveimp = ImageJFunctions.show(CurvatureKymo);
		Curveimp.setTitle("Curvature Kymo for TrackID: " + TrackID);
		Curveimp.setCalibration(cal);

		ImagePlus IntensityAimp = ImageJFunctions.show(IntensityAKymo);
		IntensityAimp.setTitle("Intensity ChA Kymo for TrackID: " + TrackID);
		IntensityAimp.setCalibration(cal);

		if(parent.twochannel) {
		ImagePlus IntensityBimp = ImageJFunctions.show(IntensityBKymo);
		IntensityBimp.setTitle("Intensity ChB for TrackID: " + TrackID);
		IntensityBimp.setCalibration(cal);
		IntensityBimp.updateAndRepaintWindow();
		}
		Curveimp.updateAndRepaintWindow();
		IntensityAimp.updateAndRepaintWindow();
		
	}

	
	public void MakeInterKymo(HashMap<String, ArrayList<Intersectionobject>> sortedMappair, long[] size,
			String TrackID) {

		RandomAccessibleInterval<FloatType> CurvatureKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityAKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccessibleInterval<FloatType> IntensityBKymo = new ArrayImgFactory<FloatType>().create(size,
				new FloatType());
		RandomAccess<FloatType> ranacimageA = IntensityAKymo.randomAccess();

		RandomAccess<FloatType> ranacimageB = IntensityBKymo.randomAccess();
		Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
        
		RandomAccess<FloatType> ranac = CurvatureKymo.randomAccess();

		while (itZ.hasNext()) {

			Map.Entry<String, Integer> entry = itZ.next();

			int time = entry.getValue();
			String timeID = entry.getKey();

			ArrayList<Intersectionobject> currentlist = sortedMappair.get(TrackID + timeID);

			ranac.setPosition(time - 1, 0);
			ranacimageA.setPosition(time - 1 , 0);
			ranacimageB.setPosition(time - 1, 0);
			if (currentlist != null) {
				for (Intersectionobject currentobject : currentlist) {

					int count = 0;

					ArrayList<double[]> sortedlinelist = currentobject.linelist;

					for (int i = 0; i < sortedlinelist.size(); ++i) {

						ranac.setPosition(count, 1);
						ranac.get().set((float) sortedlinelist.get(i)[2]);

						ranacimageA.setPosition(count, 1);
						ranacimageA.get().setReal(sortedlinelist.get(i)[3]);

						ranacimageB.setPosition(count, 1);
						ranacimageB.get().setReal(sortedlinelist.get(i)[4]);

						count++;
					
					}

				}
			}

		
		}
		double[] calibration = new double[] { parent.timecal, parent.calibration };
		Calibration cal = new Calibration();
		cal.setFunction(Calibration.STRAIGHT_LINE, calibration, "s um");
		ImagePlus Curveimp = ImageJFunctions.show(CurvatureKymo);
		Curveimp.setTitle("Curvature Kymo for TrackID: " + TrackID);
		Curveimp.setCalibration(cal);

		ImagePlus IntensityAimp = ImageJFunctions.show(IntensityAKymo);
		IntensityAimp.setTitle("Intensity ChA Kymo for TrackID: " + TrackID);
		IntensityAimp.setCalibration(cal);

		if(parent.twochannel) {
		ImagePlus IntensityBimp = ImageJFunctions.show(IntensityBKymo);
		IntensityBimp.setTitle("Intensity ChB for TrackID: " + TrackID);
		IntensityBimp.setCalibration(cal);
		IntensityBimp.updateAndRepaintWindow();
		}
		Curveimp.updateAndRepaintWindow();
		IntensityAimp.updateAndRepaintWindow();
		

		KymoSaveobject Kymos = new KymoSaveobject(CurvatureKymo, IntensityAKymo, IntensityBKymo);
		parent.KymoFileobject.put(TrackID, Kymos);
		
		

		int hyperslicedimension = 1;
		ArrayList<Pair<Integer, Double>> poslist = new ArrayList<Pair<Integer, Double>>();
		for (long pos = 0; pos< CurvatureKymo.dimension(hyperslicedimension) - 1; ++pos) {
			
			
			RandomAccessibleInterval< FloatType > CurveView =
                    Views.hyperSlice( CurvatureKymo, hyperslicedimension, pos );

			
				RandomAccess<FloatType> Cranac = CurveView.randomAccess();

				
			
			Iterator<Map.Entry<String, Integer>> itZSec = parent.AccountedZ.entrySet().iterator();
			
			 double rms = 0;
			while (itZSec.hasNext()) {
				
               
				Map.Entry<String, Integer> entry = itZSec.next();

				int time = entry.getValue();
				
				
				Cranac.setPosition(time - 1, 0);
			
				rms+=Cranac.get().get() * Cranac.get().get();
				
				
			}
			poslist.add(new ValuePair<Integer, Double>((int)pos, Math.sqrt(rms/parent.AccountedZ.size())));
			
			
		}
		parent.StripList.put(TrackID, poslist);
		parent.updatePreview(ValueChange.THIRDDIMmouse);
	}

	
	
	
	
	
	
	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		parent.Cardframe.validate();

		parent.prestack = new ImageStack((int) parent.originalimg.dimension(0), (int) parent.originalimg.dimension(1),
				java.awt.image.ColorModel.getRGBdefault());

		parent.resultDraw.clear();
		parent.Tracklist.clear();
		parent.denseTracklist.clear();

		parent.SegmentTracklist.clear();
		parent.table.removeAll();

		TrackingFunctions track = new TrackingFunctions(parent);
		if (parent.ndims > 3) {

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();

			while (itZ.hasNext()) {

				int z = itZ.next().getValue();

				

					SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

					parent.parentgraphZ.put(Integer.toString(z), simplegraph);

					CurvedLineage();

				
			}

		}

		else {

			

				SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simplegraph = track.Trackfunction();

				parent.parentgraphZ.put(Integer.toString(1), simplegraph);

				CurvedLineage();

				SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge> simpledensegraph = track
						.Trackdensefunction();

				parent.parentdensegraphZ.put(Integer.toString(1), simpledensegraph);

				CurveddenseLineage();


				Binobject densesortedMappair = GetZTdenseTrackList(
						parent);
				parent.sortedMappair = densesortedMappair.sortedmap;
				int TimedimensionKymo = parent.AccountedZ.size();

				/*
				 * HashMap<Integer, Integer> idmap = sortedMappair.getA();
				 * 
				 * Iterator<Map.Entry<Integer, Integer>> it = idmap.entrySet().iterator();
				 * 
				 * while (it.hasNext()) {
				 * 
				 * Map.Entry<Integer, Integer> mapentry = it.next(); int id = mapentry.getKey();
				 * 
				 * int Xkymodimension = mapentry.getValue();
				 * 
				 * long[] size = new long[] { TimedimensionKymo, Xkymodimension };
				 * MakeInterKymo(sortedMappair.getB(), size, id);
				 * 
				 * }
				 */

				// For dense plot
				HashMap<String, Integer> denseidmap = densesortedMappair.maxid;

				Iterator<Map.Entry<String, Integer>> denseit = denseidmap.entrySet().iterator();
				while (denseit.hasNext()) {

					Map.Entry<String, Integer> mapentry = denseit.next();
					String id = mapentry.getKey();

					int Xkymodimension = mapentry.getValue();

					long[] size = new long[] { TimedimensionKymo, Xkymodimension + 1 };
					MakeInterKymo(densesortedMappair.sortedmap, size, id);

				}
				

			}

		


		

		try {
			get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

	}

	public void CurvedLineage() {
		

		
			DisplaySelected.markAll(parent);
			DisplaySelected.selectAll(parent);

		
		if (parent.ndims < 3) {

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
						if (Angleset.size() > parent.AccountedZ.size() / 2) {
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
					}
					for (int id = minid; id <= maxid; ++id) {
						Intersectionobject bestangle = null;
						if (model.trackIntersectionobjects(id) != null
								&& model.trackIntersectionobjects(id).size() > parent.AccountedZ.size() / 2) {

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

						}

					}
				}
			}

		}

	}

	public void CurveddenseLineage() {

		for (Map.Entry<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>> entryZ : parent.parentdensegraphZ
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

					if (Angleset.size() > parent.AccountedZ.size() / 2) {
						Iterator<Intersectionobject> Angleiter = Angleset.iterator();

						while (Angleiter.hasNext()) {

							Intersectionobject currentangle = Angleiter.next();
							parent.denseTracklist.add(new ValuePair<String, Intersectionobject>(
									Integer.toString(id) + entryZ.getKey(), currentangle));
						}
						Collections.sort(parent.denseTracklist, ThirdDimcomparison);
						if (parent.fourthDimensionSize > 1)
							Collections.sort(parent.denseTracklist, FourthDimcomparison);
					}
				}

				for (int id = minid; id <= maxid; ++id) {
					Intersectionobject bestangle = null;
					if (model.trackIntersectionobjects(id) != null
							&& model.trackIntersectionobjects(id).size() > parent.AccountedZ.size() / 2) {

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
			curvatureUtils.CurvatureTable.CreateTableTrackView(parent);
		}

	}

	public void CurvedSegmentLineage() {
			

	

			DisplaySelected.markAll(parent);
			DisplaySelected.selectAll(parent);

		

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

						final HashSet<Segmentobject> Angleset = model.trackSegmentobjects(id);

						final ArrayList<Segmentobject> Anglelist = new ArrayList<Segmentobject>();

						for (Segmentobject current : Angleset) {

							Anglelist.add(current);

						}

						if (Anglelist.size() > parent.AccountedZ.size() / 2)
							parent.HashSegmentTrackList.put(id + entryZ.getKey(), Anglelist);
					}

					for (final Integer id : model.trackIDs(true)) {

						model.setName(id, "Track" + id + entryZ.getKey());

						HashMap<String, ArrayList<Segmentobject>> HashSegmentTrackList = SortTimeorZ
								.sortByCordSeg(parent.HashSegmentTrackList);

						Iterator<Segmentobject> Angleiter = HashSegmentTrackList.get(id + entryZ.getKey()).iterator();

						while (Angleiter.hasNext()) {

							Segmentobject currentangle = Angleiter.next();

							parent.SegmentTracklist.add(new ValuePair<String, Segmentobject>(
									Integer.toString(id) + entryZ.getKey(), currentangle));
						}

					}
					Comparator<Pair<String, Segmentobject>> ThirdDimcomparison = new Comparator<Pair<String, Segmentobject>>() {

						@Override
						public int compare(final Pair<String, Segmentobject> A, final Pair<String, Segmentobject> B) {

							return A.getB().z - B.getB().z;

						}

					};

					Comparator<Pair<String, Segmentobject>> FourthDimcomparison = new Comparator<Pair<String, Segmentobject>>() {

						@Override
						public int compare(final Pair<String, Segmentobject> A, final Pair<String, Segmentobject> B) {

							return A.getB().t - B.getB().t;

						}

					};

					Collections.sort(parent.SegmentTracklist, ThirdDimcomparison);

					for (int id = minid; id <= maxid; ++id) {
						Segmentobject bestangle = null;

						if (model.trackSegmentobjects(id) != null
								&& model.trackSegmentobjects(id).size() > parent.AccountedZ.size() / 2) {
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

	public static Pair<HashMap<String, Integer>, HashMap<String, ArrayList<Segmentobject>>> GetZTSegTrackList(
			final InteractiveSimpleEllipseFit parent) {

		int maxCurveDim = 0;

		HashMap<String, Integer> maxidcurve = new HashMap<String, Integer>();
		HashMap<String, ArrayList<Segmentobject>> sortedMap = new HashMap<String, ArrayList<Segmentobject>>();
		HashSet<String> TrackIDset = new HashSet<String>();
		for (Pair<String, Segmentobject> preangle : parent.SegmentTracklist) {
			String TrackID = preangle.getA();
			TrackIDset.add(TrackID);
		}

		Iterator<String> iter = TrackIDset.iterator();

		while (iter.hasNext()) {

			String TrackID = iter.next();

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int z = entry.getValue();

				String timeID = entry.getKey();

				ArrayList<Segmentobject> currentframeobject = new ArrayList<Segmentobject>();
				for (Pair<String, Segmentobject> currentangle : parent.SegmentTracklist) {

					if (currentangle.getB().z == z && currentangle.getA().equals(TrackID)) {

						currentframeobject.add(currentangle.getB());

					}

					if (currentframeobject.size() > maxCurveDim) {

						maxCurveDim = currentframeobject.size();

					}

				}
				sortedMap.put(TrackID + timeID, currentframeobject);
				maxidcurve.put(TrackID, maxCurveDim);
			}
		}
		return new ValuePair<HashMap<String, Integer>, HashMap<String, ArrayList<Segmentobject>>>(maxidcurve,
				sortedMap);

	}

	public static Pair<HashMap<String, Integer>, HashMap<String, ArrayList<Intersectionobject>>> GetZTTrackList(
			final InteractiveSimpleEllipseFit parent) {

		int maxCurveDim = 0;

		HashMap<String, Integer> maxidcurve = new HashMap<String, Integer>();

		HashMap<String, ArrayList<Intersectionobject>> sortedMap = new HashMap<String, ArrayList<Intersectionobject>>();
		HashSet<String> TrackIDset = new HashSet<String>();
		for (Pair<String, Intersectionobject> preangle : parent.Tracklist) {
			String TrackID = preangle.getA();
			TrackIDset.add(TrackID);
		}

		Iterator<String> iter = TrackIDset.iterator();

		while (iter.hasNext()) {

			String TrackID = iter.next();

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int z = entry.getValue();

				String timeID = entry.getKey();

				ArrayList<Intersectionobject> currentframeobject = new ArrayList<Intersectionobject>();
				for (Pair<String, Intersectionobject> currentangle : parent.Tracklist) {

					if (currentangle.getB().z == z && currentangle.getA().equals(TrackID)) {

						currentframeobject.add(currentangle.getB());

					}

					for (int i = 0; i < currentframeobject.size(); ++i) {

						int size = currentframeobject.get(i).linelist.size();

						if (size > maxCurveDim)
							maxCurveDim = size;

					}

				}
				sortedMap.put(TrackID + timeID, currentframeobject);
				maxidcurve.put(TrackID, maxCurveDim);
			}
		}
		return new ValuePair<HashMap<String, Integer>, HashMap<String, ArrayList<Intersectionobject>>>(maxidcurve,
				sortedMap);

	}

	public static Binobject GetZTdenseTrackList(
			final InteractiveSimpleEllipseFit parent) {

		int maxCurveDim = 0;

		double binwidth = 0;
		
		HashMap<String, Integer> maxidcurve = new HashMap<String, Integer>();

		HashMap<String, Double> bincurve = new HashMap<String, Double>();
		
		
		HashMap<String, ArrayList<Intersectionobject>> sortedMap = new HashMap<String, ArrayList<Intersectionobject>>();

		Set<String> TrackIDset = new HashSet<String>();
		for (Pair<String, Intersectionobject> preangle : parent.denseTracklist) {
			String TrackID = preangle.getA();
			TrackIDset.add(TrackID);
		}

		Iterator<String> iter = TrackIDset.iterator();

		while (iter.hasNext()) {

			String TrackID = iter.next();

			Iterator<Map.Entry<String, Integer>> itZ = parent.AccountedZ.entrySet().iterator();
			while (itZ.hasNext()) {

				Map.Entry<String, Integer> entry = itZ.next();

				int z = entry.getValue();

				String timeID = entry.getKey();

				ArrayList<Intersectionobject> currentframeobject = new ArrayList<Intersectionobject>();

				for (Pair<String, Intersectionobject> currentangle : parent.denseTracklist) {

					if (currentangle.getB().z == z && currentangle.getA().equals(TrackID)) {

						currentframeobject.add(currentangle.getB());

					}

					for (int i = 0; i < currentframeobject.size(); ++i) {

						int size = currentframeobject.get(i).linelist.size();
						binwidth = size;
						if (size > maxCurveDim)
							maxCurveDim = size;

					}
					

				}

				sortedMap.put(TrackID + timeID, currentframeobject);
				maxidcurve.put(TrackID, maxCurveDim);
                bincurve.put(TrackID, binwidth); 
				 
			}
		}
		
		Binobject  binned = new Binobject(maxidcurve, sortedMap, bincurve);

		return binned;

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