package edu.zjut.common.data.geo;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Feature Object:
 * http://help.arcgis.com/en/arcgisserver/10.0/apis/rest/feature.html
 * 
 * "Feature Object"��"attributes"��"geometry"���������.
 * "attributes"��ʱֻ����"OBJECTID"��"NAME"����������, "geometry"��JTS���ο����·�װ.
 * 
 * @author yulewei
 * 
 */
public class EsriFeatureObj
{
	public int objectId;

	/**
	 * displayFieldName
	 */
	public String name;

	/**
	 * ��JTS���ο����·�װ
	 */
	public Geometry geometry;

	public EsriFeatureObj(int objectId, String name, Geometry geometry)
	{
		this.objectId = objectId;
		this.name = name;
		this.geometry = geometry;
	}	
}
