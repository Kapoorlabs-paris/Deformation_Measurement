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
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();

	/** A user-supplied name for this spot. */
	private String name;

	/** This spot ID. */
	private final int ID;
	
	
	final double radiusCurvature;
	final double perimeter;
	final int t;
	final int z;
	final String Label;
	
	public Curvatureobject(final double radiusCurvature, final double perimeter, final String Label, final int t, final int z) {
		super(3);
		this.radiusCurvature = radiusCurvature;
		this.perimeter = perimeter;
		this.t = t;
		this.z = z;
		this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
		this.Label = Label;
		putFeature( Time,Double.valueOf( t ) );
		putFeature( Radius, Double.valueOf(radiusCurvature ) );
		putFeature( Perimeter, Double.valueOf( perimeter ) );
		putFeature( Label, Double.valueOf(Label) );
	}
	
	
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
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	/** The name of the blob X position feature. */
	public static final String Radius = "Radius";

	/** The name of the blob Y position feature. */
	public static final String Perimeter = "Perimeter";
	
	/** The label of the blob position feature. */
	public static final String LABEL = "LABEL";

	/** The name of the frame feature. */
	public static final String Time = "Time";
	
	/** The name of the frame feature. */
	public static final String Z = "Z";
	
	/** The position features. */
	public final static String[] Curvature_features = new String[] { Radius, Perimeter, LABEL };
	
	public final Double getFeature( final String feature )
	{
		return features.get( feature );
	}
	/**
	 * The 7 privileged spot features that must be set by a spot detector:
	 * {@link #QUALITY}, {@link #POSITION_X}, {@link #POSITION_Y},
	 * {@link #POSITION_Z}, {@link #POSITION_Z}, {@link #RADIUS}, {@link #FRAME}
	 * .
	 */
	public final static Collection< String > FEATURES = new ArrayList< >( 4 );

	/** The 7 privileged spot feature names. */
	public final static Map< String, String > FEATURE_NAMES = new HashMap< >( 4 );

	/** The 7 privileged spot feature short names. */
	public final static Map< String, String > FEATURE_SHORT_NAMES = new HashMap< >( 4 );

	/** The 7 privileged spot feature dimensions. */
	public final static Map< String, linkers.Dimension > FEATURE_DIMENSIONS = new HashMap< >( 4 );

	/** The 7 privileged spot feature isInt flags. */
	public final static Map< String, Boolean > IS_INT = new HashMap< >( 4 );

	static
	{
		FEATURES.add( Radius );
		FEATURES.add( Perimeter );
		FEATURES.add( Z );
		FEATURES.add( LABEL );
		FEATURES.add( Time );

		FEATURE_NAMES.put( Radius, "R" );
		FEATURE_NAMES.put( Perimeter, "P" );
		FEATURE_NAMES.put( Z, "Z" );
		FEATURE_NAMES.put( LABEL, "L" );
		FEATURE_NAMES.put( Time, "T" );
		

		FEATURE_SHORT_NAMES.put( Radius, "X" );
		FEATURE_SHORT_NAMES.put( Perimeter, "Y" );
		FEATURE_SHORT_NAMES.put( Z, "Z" );
		FEATURE_SHORT_NAMES.put( LABEL, "S" );
		FEATURE_SHORT_NAMES.put( Time, "T" );
		

		

		IS_INT.put( Radius, Boolean.FALSE );
		IS_INT.put( Perimeter, Boolean.FALSE );
		IS_INT.put( Z, Boolean.FALSE );
		IS_INT.put( LABEL, Boolean.FALSE );
		IS_INT.put( Time, Boolean.FALSE );
		
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
