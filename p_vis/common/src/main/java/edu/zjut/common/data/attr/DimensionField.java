package edu.zjut.common.data.attr;

import com.vividsolutions.jts.geom.Point;

import edu.zjut.common.data.geo.GeoLayer;

/**
 * ά��.
 * 
 * @author yulewei
 */
public class DimensionField extends DataField {

	boolean isObservation = false;
	boolean isGeoName = false;
	GeoLayer geoData = null;

	public DimensionField(int colIdx, String name, FieldType dataType,
			Object[] columnValues) {
		super(colIdx, name, dataType, columnValues);
		this.isObservation = false;
        System.out.println("DimensionField constructor");
	}

	public DimensionField(int colIdx, String name, FieldType dataType,
			Object[] columnValues, boolean isObservation) {
		super(colIdx, name, dataType, columnValues);
		this.isObservation = isObservation;
        System.out.println("DimensionField constructor observation");
	}

	public boolean isObservation() {
		return isObservation;
	}

	public void setObservation(boolean isObservation) {
		this.isObservation = isObservation;
	}

	public boolean isGeoName() {
		return isGeoName;
	}

	public GeoLayer getGeoData() {
		return geoData;
	}

	/**
	 *  ������ƶ�Ӧ�����ĵ�, ����γ�Ⱥ;�������MeasureField. ��γ��, �پ���
	 */
	public MeasureField[] buildLatLonFields() {
		if (isGeoName == false)
			return null;

		String[] names = (String[]) columnValues;
		Double[] centroidX = new Double[names.length];
		Double[] centroidY = new Double[names.length];		

        // geoData is NULL?
        if (geoData == null)
            System.out.println("Bad geoData is NULL");
        else
            System.out.println("Good geoData is not NULL");

        for (String name: names)
            System.out.println("name = " + name);

        System.out.println(geoData.getFeatures().length);
		for (int i = 0; i < names.length; i++) {
            if (geoData.getGeometry(names[i]) == null)
                System.out.println("Bad NULL");
			Point centroid = geoData.getGeometry(names[i]).getCentroid();
			centroidX[i] = centroid.getX();
			centroidY[i] = centroid.getY();
		}

		MeasureField fieldX = new MeasureField(colIdx, name, FieldType.DOUBLE,
				centroidX, SummaryType.MEAN, null);
		MeasureField fieldY = new MeasureField(colIdx, name, FieldType.DOUBLE,
				centroidY, SummaryType.MEAN, null);

		return new MeasureField[] { fieldX, fieldY };
	}

	public void setGeoName(boolean isGeoName, GeoLayer geoData) {
        System.out.println("DimensionField::setGeoName");
		this.isGeoName = isGeoName;
		this.geoData = geoData;
	}
}
