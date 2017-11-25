package tests;

import ga.ccp.Common;

public class LineResultTableEntity {
	private String instanceName;
	private double valueStateOfArt;
	private double gaValue;
	private double hga1Value;
	private double hga2Value;
	private final String LATEX_COLUMN_SPACE = " & ";
	private final String LATEX_NEW_LINE_SYMBOL = " \\\\";
	private final int DECIMAL_PLACES = 2;
	
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public double getValueStateOfArt() {
		return valueStateOfArt;
	}
	public void setValueStateOfArt(double valueStateOfArt) {
		this.valueStateOfArt = valueStateOfArt;
	}
	public double getGaValue() {
		return gaValue;
	}
	public void setGaValue(double gaValue) {
		this.gaValue = gaValue;
	}
	public double getHga1Value() {
		return hga1Value;
	}
	public void setHga1Value(double hga1Value) {
		this.hga1Value = hga1Value;
	}
	public double getHga2Value() {
		return hga2Value;
	}
	public void setHga2Value(double hga2Value) {
		this.hga2Value = hga2Value;
	}
	public double getGapGA() {
		return applyPercentage((getValueStateOfArt() - getGaValue()) / getValueStateOfArt());
	}
	public double getGapHGA1() {
		return applyPercentage((getValueStateOfArt() - getHga1Value()) / getValueStateOfArt());
	}
	public double getGapHGA2() {
		return applyPercentage((getValueStateOfArt() - getHga2Value()) / getValueStateOfArt());
	}
	
	private double applyPercentage(double number) {
		return Common.round(number * 100, DECIMAL_PLACES);
	}
	
	public String getLatexResultLine() {
		return getInstanceName().replace("_", "\\_") + LATEX_COLUMN_SPACE +
			   getValueStateOfArt() + LATEX_COLUMN_SPACE +	
			   Common.round(getGaValue(), DECIMAL_PLACES) + LATEX_COLUMN_SPACE +
			   Common.round(getHga1Value(), DECIMAL_PLACES) + LATEX_COLUMN_SPACE +
			   Common.round(getHga2Value(), DECIMAL_PLACES) + LATEX_COLUMN_SPACE +
			   getGapGA() + LATEX_COLUMN_SPACE +
			   getGapHGA1() + LATEX_COLUMN_SPACE +
			   getGapHGA2() + LATEX_NEW_LINE_SYMBOL;
				
	}


}
