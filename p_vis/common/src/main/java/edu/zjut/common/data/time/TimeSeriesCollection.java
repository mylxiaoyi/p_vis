package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class TimeSeriesCollection {

	private TimeType type;
	private List<TimeSeriesData> dataList;

	private TimePeriod timeMin;
	private TimePeriod timeMax;

	float valueMin, valueMax;

	/**
	 * ����Сʱ������ʱ��������
	 */
	private List<TimePeriod> allTimes;

	public TimeSeriesCollection(TimeType type) {
		this.type = type;
		dataList = new ArrayList<TimeSeriesData>();
	}

	public void addSeries(TimeSeriesData series) {
		dataList.add(series);
	}

	public void removeSeries(TimeSeriesData series) {
		dataList.remove(series);
	}

	/**
	 * ���ʱ��������������. ����ʱ��ֲ�������ȫһ��, ��Сʱ��, ���ʱ��, ȱʧֵ
	 */
	public void buildTimeSeries() {
		TreeSet<TimePeriod> timeSet = new TreeSet<TimePeriod>();
		for (TimeSeriesData series : dataList) {
			series.build();
			timeSet.addAll(series.getTimes());
		}

		timeMin = timeSet.first();
		timeMax = timeSet.last();

		allTimes = new ArrayList<TimePeriod>();
		for (TimePeriod time = timeMin; time.compareTo(timeMax) <= 0; time = time
				.rollDate(type, 1)) {
			allTimes.add(time);
		}

		// �����귶Χ
		valueMin = Float.MAX_VALUE;
		valueMax = Float.MIN_VALUE;
		for (TimeSeriesData series : dataList) {
			List<Float> values = series.getValues();
			for (float value : values) {
				if (value < valueMin)
					valueMin = value;
				if (value > valueMax)
					valueMax = value;
			}
		}
	}

	public TimePeriod getTimeMin() {
		return timeMin;
	}

	public TimePeriod getTimeMax() {
		return timeMax;
	}

	public float getValueMin() {
		return valueMin;
	}
	
	public float getValueMax() {
		return valueMax;
	}


	public TimeSeriesData get(int i) {
		return dataList.get(i);
	}

	public int size() {
		return dataList.size();
	}

	public List<TimePeriod> getAllTimes() {
		return allTimes;
	}

	public TimePeriod getTime(int index) {
		return allTimes.get(index);
	}

	public TimeType getTimeType() {
		return type;
	}

	public String[] getNames() {
		String[] names = new String[dataList.size()];
		for (int i = 0; i < dataList.size(); i++) {
			names[i] = dataList.get(i).getValueName();
		}

		return names;
	}
}
