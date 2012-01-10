package edu.zjut.common.data.geo;

import java.util.List;

public class GeometryData {

	/**
	 * �������ƹ���ͼ��
	 */
	private List<GeoLayer> geoNames;

	/**
	 * ����ͼ������
	 */
	private List<GeoLayer> layers;

	public GeometryData(List<GeoLayer> geoNames, List<GeoLayer> layers) {
		this.geoNames = geoNames;
		this.layers = layers;
	}

	public List<GeoLayer> getGeoNames() {
		return geoNames;
	}

	public List<GeoLayer> getLayers() {
		return layers;
	}
}
