package edu.zjut.common.io;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gicentre.utils.colour.ColourTable;

import au.com.bytecode.opencsv.CSVReader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import edu.zjut.commom.color.ColourScaling;
import edu.zjut.common.data.AttributeData;
import edu.zjut.common.data.DataSetForApps;
import edu.zjut.common.data.AttrType;
import edu.zjut.common.data.EsriFeatureObj;
import edu.zjut.common.data.GeometryData;
import edu.zjut.common.data.SummaryType;
import edu.zjut.common.data.TimeData;
import edu.zjut.common.io.DataConfig.Attr;
import edu.zjut.common.io.DataConfig.Attr.Attribute;
import edu.zjut.common.io.DataConfig.ColorMap;
import edu.zjut.common.io.DataConfig.Geo;
import edu.zjut.common.io.DataConfig.Geo.Feature;
import edu.zjut.common.io.DataConfig.Time;

/**
 * 
 * @author yulewei
 * 
 */
public class DataSetLoader {

	/**
	 * �����ļ�
	 */
	protected String configFile;

	protected DataConfig config;

	private AttributeData attrData;
	private GeometryData geoData;
	private TimeData timeData;

	protected DataSetForApps dataForApps;

	public DataSetLoader(String xmlfile) {
		this.configFile = xmlfile;
		this.config = DataConfig.loadConfig(xmlfile);

		readAttributeData(config.attr);
		readGeometryData(config.geo);
		readTimeData(config.time);

		dataForApps = new DataSetForApps(attrData, geoData, timeData);
	}

	/**
	 * ��������
	 * 
	 * @param config
	 */
	public void readAttributeData(Attr attrConfig) {
		ArrayList<Attribute> attrList = attrConfig.attrList;

		int keyCol = 0;
		int nameCol = 1;

		int len = attrList.size();

		AttrType[] dataTypes = new AttrType[len];
		String[] attributeNames = new String[len];
		SummaryType[] summaryTypes = new SummaryType[len];
		ColourTable[] colorTables = new ColourTable[len];
		for (int i = 0; i < len; i++) {
			Attribute attr = attrList.get(i);
			if (attr.dataType.equalsIgnoreCase("id")) {
				dataTypes[i] = AttrType.ID;
			}
			if (attr.dataType.equalsIgnoreCase("int")) {
				dataTypes[i] = AttrType.INT;
			} else if (attr.dataType.equalsIgnoreCase("double")) {
				dataTypes[i] = AttrType.DOUBLE;
			} else if (attr.dataType.equalsIgnoreCase("string")) {
				dataTypes[i] = AttrType.STRING;
			}
			attributeNames[i] = attr.name;
			summaryTypes[i] = attr.summaryType == null ? null : SummaryType
					.valueOf(attr.summaryType.toUpperCase());
			colorTables[i] = (attr.colorMap == null ? null
					: parseColorMap(attr.colorMap));

			if (attr.name.equals(attrConfig.name))
				nameCol = i;
		}

		Object[] columnArrays = readFileContent(dataTypes, attrList,
				attrConfig.fileName, 1);

		this.attrData = new AttributeData(nameCol, dataTypes, attributeNames,
				columnArrays, summaryTypes, colorTables);
	}

	/**
	 * ��������
	 * 
	 * @param config
	 */
	public void readGeometryData(Geo geoConfig) {
		HashMap<String, Geometry> nameGeometrys = new HashMap<String, Geometry>();
		List<EsriFeatureObj[]> layers = new ArrayList<EsriFeatureObj[]>();

		ArrayList<Feature> featureList = geoConfig.featureList;

		for (Feature feature : featureList) {
			if (feature.fileType.equalsIgnoreCase("csv")) {
				try {
					loadGeoCSV(nameGeometrys, feature);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (feature.fileType.equalsIgnoreCase("geojson")) {
				loadGeoJSON(layers, feature);
			}
		}

		this.geoData = new GeometryData(nameGeometrys, layers);
	}

	/**
	 * ʱ������
	 * 
	 * @param config
	 */
	public void readTimeData(Time timeConfig) {

	}

	/**
	 * ֻ��ȡ�����ļ�ָ������
	 * 
	 * @param attrList
	 * @param fileName
	 */
	private Object[] readFileContent(AttrType[] dataTypes,
			ArrayList<Attribute> attrList, String fileName, int beg) {

		Object[] columnArrays = new Object[attrList.size()];

		List<String[]> fileContent = null;

		try {
			CSVReader reader = new CSVReader(new FileReader(fileName));
			fileContent = reader.readAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		int len = fileContent.size() - beg;

		// ��ʼ��columnArrays
		for (int i = 0; i < dataTypes.length; i++) {
			switch (dataTypes[i]) {
			case ID:
			case INT:
				columnArrays[i] = new int[len];
				break;
			case DOUBLE:
				columnArrays[i] = new double[len];
				break;
			case STRING:
				columnArrays[i] = new String[len];
				break;
			}
		}

		for (int row = beg; row < len + beg; row++) {
			String[] line = fileContent.get(row);

			int[] ints = null;
			double[] doubles = null;
			String[] strings = null;

			for (int i = 0; i < attrList.size(); i++) {
				Attribute attr = attrList.get(i);
				int col = attr.colIdx;
				String item = line[col - 1];
				switch (dataTypes[i]) {
				case ID:
				case INT:
					ints = (int[]) columnArrays[i];
					ints[row - beg] = Integer.parseInt(item);
					break;
				case DOUBLE:
					doubles = (double[]) columnArrays[i];
					doubles[row - beg] = item.equals("") ? 0 : Double
							.parseDouble(item);
					break;
				case STRING:
					strings = (String[]) columnArrays[i];
					strings[row - beg] = item;
					break;
				}
			}
		}

		return columnArrays;
	}

	private ColourTable parseColorMap(ColorMap colorMap) {
		ColourTable colourTable = null;

		if (colorMap.preset != null) {
			int type = 0;
			try {
				type = ColourTable.class.getField(colorMap.preset).getInt(
						ColourTable.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			colourTable = ColourTable.getPresetColourTable(type);
		} else {
			colourTable = new ColourTable();
		}

		return colourTable;
	}

	private void loadGeoCSV(HashMap<String, Geometry> nameGeometrys,
			Feature feature) throws IOException {

		GeometryFactory geometryFactory = new GeometryFactory();

		CSVReader reader = new CSVReader(new FileReader(feature.fileName));
		List<String[]> fileContent = reader.readAll();

		for (int i = 0; i < fileContent.size(); i++) {
			String[] line = fileContent.get(i);
			String name = line[feature.col.key - 1];
			String xs = line[feature.col.x - 1];
			String ys = line[feature.col.y - 1];
			double x = Double.parseDouble(xs);
			double y = Double.parseDouble(ys);
			Coordinate coord = new Coordinate(y, x);
			Geometry geometry = geometryFactory.createPoint(coord);
			nameGeometrys.put(name, geometry);
		}
	}

	private void loadGeoJSON(List<EsriFeatureObj[]> layers, Feature feature) {
		EsriJSONParser parser = new EsriJSONParser(feature.fileName);
		EsriFeatureObj[] features = parser.getFeatures();
		layers.add(features);
	}

	public void setDataForApps(DataSetForApps dataForApps) {
		this.dataForApps = dataForApps;
	}

	public DataSetForApps getDataForApps() {
		return dataForApps;
	}

	public String getFileName() {
		return configFile;
	}
}
