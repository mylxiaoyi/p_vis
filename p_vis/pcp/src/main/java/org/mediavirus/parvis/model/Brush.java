/*

Copyright (c) 2001, 2002, 2003 Flo Ledermann <flo@subnet.at>

This file is part of parvis - a parallel coordiante based data visualisation
tool written in java. You find parvis and additional information on its
website at http://www.mediavirus.org/parvis.

parvis is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

parvis is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with parvis (in the file LICENSE.txt); if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

package org.mediavirus.parvis.model;

import java.awt.Color;

import java.io.*;

/**
 * Represents a set of "brushed" records.
 * @author  Flo Ledermann flo@subnet.at
 */
public class Brush {
        
    /** The color to use to draw the records on the screen. */
    private Color color;
    
    /** Holds value of property brushValue. */
    private double[] brushValues;
    
    private int numBrushed = 0;
    
    /** Holds value of property name. */
    private String name;
    
    /** Creates a new instance of Brush */
    public Brush(int numValues, Color color) {
        brushValues = new double[numValues];
        this.color = color;
    }
    
    /** Creates a new instance of Brush */
    public Brush(int numValues){
        this(numValues, Color.black);
    }
    
    /** Creates a new instance of Brush */
    public Brush(double[] brushValues){
        this.setBrushValues(brushValues);
    }
        
    
    /** Getter for property color.
     * @return Value of property color.
     */
    public Color getColor() {
        return this.color;
    }
    
    /** Setter for property color.
     * @param color New value of property color.
     */
    public void setColor(Color color) {
        this.color = color;
    }
    
    /** Indexed getter for property brushValue.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public double getBrushValue(int index) {
        return this.brushValues[index];
    }    
        
    /** Indexed setter for property brushValue.
     * @param index Index of the property.
     * @param brushValue New value of the property at <CODE>index</CODE>.
     */
    public void setBrushValue(int index, double brushValue) {
        if ((brushValue > 0.0f) && (brushValues[index] == 0.0f)){
            numBrushed++;
        }
        else if ((brushValue == 0.0f) && (brushValues[index] > 0.0f)){
            numBrushed--;
        }
        
        this.brushValues[index] = brushValue;
    }    
    
    /**
     * Sets the array with brush vallues directly.
     */
    public void setBrushValues(double[] newValues){
        this.brushValues = (double[])newValues.clone();
        
        numBrushed = 0;
        for (int i=0; i<brushValues.length; i++){
            if (brushValues[i] > 0.0f) numBrushed++;
        }
    }
    
    /**
     * Returns the nuber of records of the dataset.
     */
    public int getNumValues(){
        return brushValues.length;
    }
    
    /**
     * Returns the number of brushed records.
     */
    public int getNumBrushed(){
        return numBrushed;
    }
    
    /**
     * Returns a new Brush, which is the result of a subtraction of another brush
     * from the values of this brush.
     */
    public Brush subtract(Brush secondBrush){
        Brush newBrush = new Brush(brushValues.length);
        
        for (int i=0; i<brushValues.length; i++){
        	double newVal = brushValues[i] - secondBrush.getBrushValue(i);
            if (newVal < 0.0f) newVal = 0.0f;
            newBrush.setBrushValue(i,newVal);
        }
        
        return newBrush;
    }
    
    /**
     * Returns a new brush which is the result of a merging of this brush
     * and another brush.
     */
    public Brush add(Brush secondBrush){
        Brush newBrush = new Brush(brushValues.length);
        
        for (int i=0; i<brushValues.length; i++){
        	double newVal = brushValues[i] + secondBrush.getBrushValue(i);
            if (newVal > 1.0f) newVal = 1.0f;
            newBrush.setBrushValue(i,newVal);
        }
        
        return newBrush;        
    }
    
    /**
     * Returns a new Brush which is the result of an intersection operation of 
     * this brush with another brush.
     */
    public Brush intersect(Brush secondBrush){
        Brush newBrush = new Brush(brushValues.length);
        
        for (int i=0; i<brushValues.length; i++){
            newBrush.setBrushValue(i,Math.min(brushValues[i], secondBrush.getBrushValue(i)));
        }
        
        return newBrush;
    }

    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString(){
        if (name != null) {
            return getName();
        }
        else {
            return "<unnamed brush>";
        }
    }
    
    /**
     * Returns a new Brush identical to this one.
     */
    public Object clone(){
        Brush newBrush = new Brush((double[])brushValues.clone());
        newBrush.setName(getName());
        newBrush.setColor(getColor());
        
        return newBrush;
    }
    
    /**
     * 1.Zeile: "Name des Channels" = String
     * 2.Zeile: "Anzahl der Datenwerte" = integer
     * 3.-"Anzahl der Datenwerte+2"Zeilen: je ein "Datenwert" = float
     */
    public void writeToFile(File f, boolean overwrite) throws IOException{
        if (!f.exists() || overwrite){
            FileWriter out = new FileWriter(f);
            
            String bname;
            if (name != null) bname = name;
            else bname = "<unnamed brush>";
            
            out.write(bname + "\n");
            out.write(brushValues.length + "\n");
            for (int i=0; i<brushValues.length; i++){
                out.write(brushValues[i] + "\n");
            }
            
            out.close();           
        }
        else {
            throw new IOException("File " + f.getAbsolutePath() + " exists!");
        }
    }
    
}