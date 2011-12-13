package edu.zjut.common.data.time;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class TimeSeriesData {
	private String timeName;
	private String valueName;

	private SortedMap<TimePeriod, Float> data;

	private List<TimePeriod> times;
	private List<Float> values;

	private float valueMin;
	private float valueMax;

	public TimeSeriesData(String timeName, String valueName) {
		this.timeName = timeName;
		this.valueName = valueName;

		data = new TreeMap<TimePeriod, Float>();
	}

	public void add(TimePeriod time, float value) {
		data.put(time, value);
	}

	/**
	 * ����Сʱ������ʱ��֮�����ȱʧ����
	 */
	public void build() {

		times = new ArrayList<TimePeriod>();
		values = new ArrayList<Float>();

		times.addAll(data.keySet());
		for (TimePeriod time : times) {
			values.add(data.get(time));
		}

		// �����귶Χ
		valueMin = Float.MAX_VALUE;
		valueMax = Float.MIN_VALUE;
		for (float value : values) {
			if (value < valueMin)
				valueMin = value;
			if (value > valueMax)
				valueMax = value;
		}

		// for (TimePeriod time = timeMin; time.compareTo(timeMax) <= 0; time =
		// time
		// .rollDate(type, 1)) {
		// times.add(time);
		//
		// Float value = data.get(time);
		// if (value == null) {
		// data.put(time, null);
		// values.add(null);
		//
		// // TODO ��0.0���ȱʡֵ???
		// // values.add(0.0f);
		// } else {
		// values.add(value);
		// }
		// }
	}

	public int size() {
		return data.size();
	}

	public String getTimeName() {
		return timeName;
	}

	public String getValueName() {
		return valueName;
	}

	public TimePeriod getTimeMin() {
		return times.get(0);
	}

	public TimePeriod getTimeMax() {
		return times.get(times.size() - 1);
	}

	public float getValueMin() {
		return valueMin;
	}

	public float getValueMax() {
		return valueMax;
	}

	public TimePeriod getTime(int index) {
		return times.get(index);
	}

	public Float getValue(int index) {
		return values.get(index);
	}

	public List<TimePeriod> getTimes() {
		return times;
	}

	public void setTimes(List<TimePeriod> times) {
		this.times = times;
	}

	public List<Float> getValues() {
		return values;
	}

	public void setValues(List<Float> values) {
		this.values = values;
	}
}
