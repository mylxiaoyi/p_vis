package org.gicentre.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gicentre.data.summary.SummariseField;
import org.gicentre.data.summary.SummariseNode;

/**
 * 
 * @author yulewei
 */
public class DataUtil {

	/**
	 * Summarises the contents as a tree of nodes, conditioning by the array of
	 * datafields supplied
	 * 
	 * @param hierFields
	 *            Array of conditioning datafields
	 * @param sumFields
	 *            Collection of summaryfields
	 * @param dataFilter
	 *            DataFilter
	 * @return The root node of a tree that summarises the data
	 */
	public static SummariseNode getSummary(List<Record> records,
			final DataField[] hierFields, List<SummariseField> sumFields,
			DataFilter dataFilter) {

		int numLevels = hierFields.length;

		if (numLevels == 0) {
			return new SummariseNode(null, null, 0, null, -1, -1,
					new HashMap<SummariseField, Object>());
		}

		// ����, �Ա㰴����������
		Collections.sort(records, new Comparator<Record>() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Record o1, Record o2) {
				for (DataField field : hierFields) {
					Comparable<Object> v1 = (Comparable<Object>) o1
							.getValue(field);
					Comparable<Object> v2 = (Comparable<Object>) o2
							.getValue(field);

					int compareValue = v1.compareTo(v2);
					if (compareValue != 0) {
						return compareValue;
					}
				}
				return 0;
			}
		});

		List<Record> sortedRecords = new ArrayList<Record>();
		if (dataFilter == null) {
			sortedRecords.addAll(records);
		} else {
			for (Record record : records) {
				if (dataFilter.matches(record)) {
					sortedRecords.add(record);
				}
			}
		}
		if (sortedRecords.isEmpty()) {
			return null;
		}

		// �����, ����ÿ�������ֵ�ͷ�������
		ArrayList<Object>[] curHierValues = new ArrayList[numLevels];
		ArrayList<Integer>[] curHierRowIdxs = new ArrayList[numLevels];
		for (int i = 0; i < numLevels; i++) {
			curHierValues[i] = new ArrayList<Object>();
			curHierRowIdxs[i] = new ArrayList<Integer>();
		}

		int rowIdx = 0;
		Object[] prevGroupByValues = new Object[numLevels];
		for (Record record : sortedRecords) {
			Object[] curGroupByValues = new Object[numLevels];
			boolean rollAllLevel = false;
			for (int i = 0; i < numLevels; i++) {
				curGroupByValues[i] = record.getValue(hierFields[i]);
				if (rowIdx == 0
						|| rollAllLevel
						|| (prevGroupByValues[i] != null && !prevGroupByValues[i]
								.equals(curGroupByValues[i]))) {
					rollAllLevel = true;
					curHierValues[i].add(curGroupByValues[i]);
					curHierRowIdxs[i].add(rowIdx);
				}
			}
			rowIdx++;
			prevGroupByValues = curGroupByValues;
		}
		for (int i = 0; i < curHierRowIdxs.length; i++) {
			curHierRowIdxs[i].add(rowIdx);
		}

		// �Է���ķ�ʽ����ȫ��SummariseNode, �����ڵ���
		int[] rollUpIdxs = new int[numLevels];
		int curDepth = numLevels - 1;
		HashSet<SummariseNode>[] nodesToAdd = new HashSet[hierFields.length];
		for (int i = 0; i < hierFields.length; i++) {
			nodesToAdd[i] = new HashSet<SummariseNode>();
		}

		while (true) {
			if (curDepth > 0) {
				int index1 = curHierRowIdxs[curDepth - 1]
						.get(rollUpIdxs[curDepth - 1] + 1);
				int index2 = curHierRowIdxs[curDepth].get(rollUpIdxs[curDepth]);
				if (index1 == index2) {
					curDepth--;
				} else {
					curDepth = numLevels - 1;
				}
			} else {
				curDepth = numLevels - 1;
			}

			// ��������Summarise���
			int startRowIdx = curHierRowIdxs[curDepth]
					.get(rollUpIdxs[curDepth]);
			int endRowIdx = curHierRowIdxs[curDepth]
					.get(rollUpIdxs[curDepth] + 1) - 1;

			HashMap<SummariseField, Object> summaryValues = new HashMap<SummariseField, Object>();

			for (SummariseField summaryField : sumFields) {
				Object value = summaryField.compute(sortedRecords.subList(
						startRowIdx, endRowIdx + 1));
				summaryValues.put(summaryField, value);
			}

			// ����order
			int order = startRowIdx;
			Object groupByValue = curHierValues[curDepth]
					.get(rollUpIdxs[curDepth]);
			List<Object> orderedValues = hierFields[curDepth].getOrderValues();
			if (orderedValues != null) {
				order = orderedValues.indexOf(groupByValue);
			}

			SummariseNode summaryNode = new SummariseNode(hierFields[curDepth],
					groupByValue, order, sortedRecords, startRowIdx, endRowIdx,
					summaryValues);

			if (curDepth < numLevels - 1) {
				for (SummariseNode node : nodesToAdd[curDepth + 1]) {
					summaryNode.add(node);
				}
				nodesToAdd[curDepth + 1].clear();
			}

			nodesToAdd[curDepth].add(summaryNode);

			rollUpIdxs[curDepth]++;

			if (curDepth == 0
					&& rollUpIdxs[curDepth] >= curHierRowIdxs[curDepth].size() - 1)
				break;
		}

		// ������ڵ�
		HashMap<SummariseField, Object> summaryValues = new HashMap<SummariseField, Object>();
		for (SummariseField field : sumFields) {
			Object value = field.compute(sortedRecords);
			summaryValues.put(field, value);
		}

		SummariseNode root = new SummariseNode(null, null, 0, sortedRecords, 0,
				records.size() - 1, summaryValues);
		for (SummariseNode node : nodesToAdd[0]) {
			root.add(node);
		}

		return root;
	}
}
