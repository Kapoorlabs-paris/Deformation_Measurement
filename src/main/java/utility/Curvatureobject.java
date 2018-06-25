package utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Curvatureobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Curvatureobject> {

	
	/*
	 * FIELDS
	 */

	public static AtomicInteger IDcounter = new AtomicInteger( -1 );

	/** Store the individual features, and their values. */

	/** A user-supplied name for this spot. */
	private String name;

	/** This spot ID. */
	private final int ID;
	
	
	public final double radiusCurvature;
	public final double perimeter;
	public final int t;
	public final int z;
	public final double[] cord;
	public final int Label;
	public final double signedradiusCurvature;
	public final double Intensity;
	
	
	public Curvatureobject(final double radiusCurvature, final double perimeter, final double signedradiusCurvature, final double Intensity, final int Label, final double[] cord, final int t, final int z) {
		super(3);
		this.radiusCurvature = radiusCurvature;
		this.perimeter = perimeter;
		this.cord = cord;
		this.signedradiusCurvature = signedradiusCurvature;
		this.Intensity = Intensity;
		this.t = t;
		this.z = z;
		this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
		this.Label = Label;
	};
		
	
	
	/*
	 * STATIC KEYS
	 */
	/**
	 * Set the name of this Spot.
	 * 
	 * @param name
	 *            the name to use.
	 */
	public void setName( final String name )
	{
		this.name = name;
	}

	public int ID()
	{
		return ID;
	}

	@Override
	public String toString()
	{
		String str;
		if ( null == name || name.equals( "" ) )
			str = "ID" + ID;
		else
			str = name;
		return str;
	}
	/**
	 * Stores the specified feature value for this spot.
	 *
	 * @param feature
	 *            the name of the feature to store, as a {@link String}.
	 * @param value
	 *            the value to store, as a {@link Double}. Using
	 *            <code>null</code> will have unpredicted outcomes.
	 */
	
	public static final String Radius = "Radius";

	public static final String Perimeter = "Perimeter";
	
	public static final String LABEL = "LABEL";

	public static final String Time = "Time";
	
	public static final String Z = "Z";
	
	public static final String LocationX = "LocationX";
	
	public static final String LocationY = "LocationY";
	
	/** The curvature features. */
	public final static String[] Curvature_features = new String[] { Radius, Perimeter, LABEL, LocationX, LocationY };
	
	
	/**
	 * The 7 features for curvatrue object include, LocationX and LocationY,
	 * Radius at that location, Perimeter for the cell label and the Z and T location
	 * of the cell
	 * .
	 */
	static int featurenumber = 7;
	public final static Collection< String > FEATURES = new ArrayList< >( featurenumber );

	public final static Map< String, String > FEATURE_NAMES = new HashMap< >( featurenumber );

	public final static Map< String, String > FEATURE_SHORT_NAMES = new HashMap< >( featurenumber );

	public final static Map< String, linkers.Dimension > FEATURE_DIMENSIONS = new HashMap< >( featurenumber );

	public final static Map< String, Boolean > IS_INT = new HashMap< >( featurenumber );

	static
	{
		FEATURES.add( Radius );
		FEATURES.add( Perimeter );
		FEATURES.add( Z );
		FEATURES.add( LABEL );
		FEATURES.add( Time );
		FEATURES.add( LocationX );
		FEATURES.add( LocationY );
		

		FEATURE_NAMES.put( Radius, "R" );
		FEATURE_NAMES.put( Perimeter, "P" );
		FEATURE_NAMES.put( Z, "Z" );
		FEATURE_NAMES.put( LABEL, "L" );
		FEATURE_NAMES.put( Time, "T" );
		FEATURE_NAMES.put( LocationX, "LocationX" );
		FEATURE_NAMES.put( LocationY, "LocationY" );

		FEATURE_SHORT_NAMES.put( Radius, "X" );
		FEATURE_SHORT_NAMES.put( Perimeter, "Y" );
		FEATURE_SHORT_NAMES.put( Z, "Z" );
		FEATURE_SHORT_NAMES.put( LABEL, "S" );
		FEATURE_SHORT_NAMES.put( Time, "T" );
		FEATURE_SHORT_NAMES.put( LocationX, "LocationX" );
		FEATURE_SHORT_NAMES.put( LocationY, "LocationY" );

		

		IS_INT.put( Radius, Boolean.FALSE );
		IS_INT.put( Perimeter, Boolean.FALSE );
		IS_INT.put( Z, Boolean.TRUE );
		IS_INT.put( LABEL, Boolean.TRUE );
		IS_INT.put( Time, Boolean.TRUE );

		IS_INT.put( LocationX, Boolean.FALSE );
		IS_INT.put( LocationY, Boolean.FALSE );
		
	}
	@Override
	public int compareTo(Curvatureobject o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public double getDoublePosition(int d) {
		return (float) getDoublePosition(d);
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) getDoublePosition(d);
	}

	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}
	
	
	
	
	
}
