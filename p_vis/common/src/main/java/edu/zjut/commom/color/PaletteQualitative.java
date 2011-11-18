package edu.zjut.commom.color;

import java.awt.Color;
import java.util.HashMap;

import org.gicentre.utils.colour.ColourTable;

/**
 * ���Ƶ�ɫ��, <a href="http://colorbrewer2.org/">http://colorbrewer2.org/</a>
 */
public class PaletteQualitative extends Palette
{
	public PaletteQualitative()
	{
		super(-1);
		coloursMap = new HashMap<String, Color>();
	}

	/**
	 * ���û�ָ����ɫӳ���ϵ
	 * 
	 * @param values
	 * @param colour
	 */
	public PaletteQualitative(String[] values, Color[] colour)
	{
		super(-1);
		coloursMap = new HashMap<String, Color>();
		for (int i = 0; i < values.length; i++)
		{
			coloursMap.put(values[i], colour[i]);
		}
	}

	/**
	 * ʹ��Colorbrewer��ɫ����
	 * 
	 * @param type
	 * @param values
	 */
	public PaletteQualitative(int type, String[] values)
	{
		super(type);
		colourTable = ColourTable.getPresetColourTable(type); //
		coloursMap = new HashMap<String, Color>();
		for (int i = 0; i < values.length; i++)
		{
			coloursMap.put(values[i], new Color(colourTable.findColour(i + 1)));
		}
	}

	public Color getColour(Object _index)
	{
		String index = (String) _index;
		if (coloursMap.containsKey(index))
			return coloursMap.get(index);
		else
			return new Color(120, 120, 120);
	}

	public String[] getAllKeys()
	{
		return coloursMap.keySet().toArray(
				new String[coloursMap.keySet().size()]);
	}

	/**
	 * ��������ӳ����ɫ
	 */
	private HashMap<String, Color> coloursMap;

	private ColourTable colourTable;

}
