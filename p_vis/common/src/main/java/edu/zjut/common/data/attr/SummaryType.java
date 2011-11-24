package edu.zjut.common.data.attr;

public enum SummaryType {
	NULL,

	/**
	 * ���
	 */
	SUM,

	/**
	 * ��ֵ
	 */
	MEAN,

	/**
	 * ����
	 */
	COUNT,

	/**
	 * ����, Unique Count
	 */
	UNI_COUNT,

	/**
	 * ��Сֵ
	 */
	MIN,

	/**
	 * ���ֵ
	 */
	MAX,

	/**
	 * ��׼��, Standard Deviation
	 */
	STD_DEV,

	/**
	 * ����ϵ��, coefficient of variation
	 */
	CV
}
