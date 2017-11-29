package tests;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CCPParameters;
import ga.ccp.Common;

public class TTTPlotBuilder {
	private String targetInstanceName;
	private InstanceReader.InstanceType instanceType;
	private double targetValue;
	private int executionTimes;
	
	private static final int DEFAULT_EXECUTION_TIMES = 200;
	private static final boolean RUN_GA_VERSION = false;
	
	public TTTPlotBuilder(String targetInstanceName, InstanceReader.InstanceType instanceType, double targetValue) {
		this(targetInstanceName, instanceType, targetValue, DEFAULT_EXECUTION_TIMES);
	}
	
	public TTTPlotBuilder(String targetInstanceName, InstanceReader.InstanceType instanceType, double targetValue, int executionTimes) {
		this.targetInstanceName = targetInstanceName;
		this.instanceType = instanceType;
		this.targetValue = targetValue;
		this.executionTimes = executionTimes;
	}
	
	public static void main(String[] args) throws Exception {
		TTTPlotBuilder tttPlotBuilder = null;
		
		if(RUN_GA_VERSION) {
			tttPlotBuilder = new TTTPlotBuilder(CCPParameters.INSTANCE_NAME, CCPParameters.INSTANCE_TYPE, 356678); //480-01
		} else {
			tttPlotBuilder = new TTTPlotBuilder(CCPParameters.INSTANCE_NAME, CCPParameters.INSTANCE_TYPE, 522443); //480-01
		}
		
		tttPlotBuilder.buildTTTPlot();
	}
	
	public void buildTTTPlot() throws Exception {
		ArrayList<ArrayList<Long>> runningTimes = getRunningTimes(this.targetInstanceName, this.instanceType);
		XYDataset dataset = createDataset(runningTimes);
		JFreeChart tttPlotchart = createChart(dataset);
		new File("TTTPlot.png").delete();
		ChartUtilities.saveChartAsPNG(new File("TTTPlot.png"), tttPlotchart, 800, 800);
	}
	
	private String getChartName() {
		return this.targetInstanceName.replace(".txt", "") + "_" + this.targetValue;
	}
	
	private JFreeChart createChart(final XYDataset dataset) {
		JFreeChart chart = ChartFactory.createScatterPlot(
				getChartName(), 
                "Running time (s)", 
                "Probability", 
                dataset, 
                PlotOrientation.VERTICAL,
                true, 
                true, 
                false
        );
		
		XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));        

        if(RUN_GA_VERSION) {
        	renderer.setSeriesPaint(2, Color.GREEN);
            renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        }
        
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);
        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(getChartName(),
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
	}
	
	private XYDataset createDataset(ArrayList<ArrayList<Long>> runningTimes) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        dataset.addSeries(createHGA1Series(runningTimes));
        dataset.addSeries(createHGA2Series(runningTimes));
        if(RUN_GA_VERSION) {
        	dataset.addSeries(createGASeries(runningTimes));
        }

        return dataset;
    }
	
	private XYSeries createHGA1Series(ArrayList<ArrayList<Long>> runningTimes) {
		return createSeries(runningTimes.get(0), "HGA1");
	}
	
	private XYSeries createHGA2Series(ArrayList<ArrayList<Long>> runningTimes) {
		return createSeries(runningTimes.get(1), "HGA2");
	}
	
	private XYSeries createGASeries(ArrayList<ArrayList<Long>> runningTimes) {
		return createSeries(runningTimes.get(2), "GA");
	}
	
	private XYSeries createSeries(ArrayList<Long> runningTimes, String seriesName) {
		XYSeries series = new XYSeries(seriesName);
		int i = 1;
		
		Collections.sort(runningTimes);
		for(int index = 0; index < runningTimes.size(); index++) {
			series.add((double)runningTimes.get(index), getI(i, runningTimes.size()));
			i++;
		}
		
		return series;
	}
	
	private double getI(int i, int n) {
		return ((double)i - 0.5) / (double)n;
	}
	
	private ArrayList<ArrayList<Long>> getRunningTimes(String instanceName, InstanceReader.InstanceType instanceType) throws Exception {
		int numberAlgorithms = RUN_GA_VERSION ? 3 : 2;
		int seed = 0;
		CCPInstanceEntity instance = null;
		
		ArrayList<ArrayList<Long>> runningTimes = new ArrayList<>(numberAlgorithms);
		for(int i = 0; i < numberAlgorithms; i++) {
			runningTimes.add(new ArrayList<>(this.executionTimes));
		}
		
		instance = InstanceReader.readerInstance(instanceType, instanceName);
		for(int i = 0; i < this.executionTimes; i++) {
			runningTimes.get(0).add(getRunningTimeHGA1Version(instance, seed));
			runningTimes.get(1).add(getRunningTimeHGA2Version(instance, seed));
			
			if(RUN_GA_VERSION) {
				runningTimes.get(2).add(getRunningTimeGAVersion(instance, seed));
			}
			seed++;
		}
		
		return runningTimes;
	}
	
	private long getRunningTimeGAVersion(CCPInstanceEntity instance, int seed) {
		long startTime = System.currentTimeMillis();
		Runner.runGAVersion(instance, this.targetValue, seed);
		return (long)Common.getRunningTime(startTime);
	}
	
	private long getRunningTimeHGA1Version(CCPInstanceEntity instance, int seed) {
		long startTime = System.currentTimeMillis();
		Runner.runHGA1Version(instance, this.targetValue, seed);
		return (long)Common.getRunningTime(startTime);
	}
	
	private long getRunningTimeHGA2Version(CCPInstanceEntity instance, int seed) {
		long startTime = System.currentTimeMillis();
		Runner.runHGA2Version(instance, this.targetValue, seed);
		return (long)Common.getRunningTime(startTime);
	}
}
