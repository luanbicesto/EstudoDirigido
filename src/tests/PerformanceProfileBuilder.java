package tests;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PerformanceProfileBuilder {
	
	public static void main(String[] args) throws IOException {
		PerformanceProfileBuilder performanceProfileBuilder = new PerformanceProfileBuilder();
		performanceProfileBuilder.buildPerformanceProfileChart();
	}
	
	public void buildPerformanceProfileChart() throws IOException {
		ArrayList<ArrayList<Double>> heuristicsResults = getHeuristicsResults();
		ArrayList<ArrayList<Double>> heuristicsDeviations = computeDeviationEachHeuristic(heuristicsResults, heuristicsResults.get(0).size());
		XYDataset dataset = createDataset(heuristicsDeviations, getHeuristicsNames());
		JFreeChart performanceProfileChart = createChart(dataset);
		new File("PerformanceProfile.png").delete();
		ChartUtilities.saveChartAsPNG(new File("PerformanceProfile.png"), performanceProfileChart, 800, 800);
	}
	
	private ArrayList<ArrayList<Double>> getHeuristicsResults(){
		int heuristicQty = 2;
		ArrayList<ArrayList<Double>> heuristicsResults = new ArrayList<>(heuristicQty);
		
		ArrayList<Double> heuristicResult = new ArrayList<>();
		//VND - 240
		heuristicResult.add(222931.65);
		heuristicResult.add(201691.73);
		heuristicResult.add(196199.95);
		heuristicResult.add(222455.67);
		heuristicResult.add(192892.46);
		heuristicResult.add(214256.87);
		heuristicResult.add(206830.64);
		heuristicResult.add(202576.92);
		heuristicResult.add(206739.99);
		heuristicResult.add(189846.25);
		heuristicResult.add(202179.11);
		heuristicResult.add(198691.78);
		heuristicResult.add(199484.53);
		heuristicResult.add(226584.34);
		heuristicResult.add(188426.05);
		heuristicResult.add(201554.40);
		heuristicResult.add(192373.26);
		heuristicResult.add(192334.36);
		heuristicResult.add(196657.15);
		heuristicResult.add(209825.28);
		heuristicsResults.add(heuristicResult);
		
		heuristicResult = new ArrayList<>();
		//HGA1 - 240
		heuristicResult.add(221617.75);
		heuristicResult.add(201381.85);
		heuristicResult.add(194689.96);
		heuristicResult.add(222798.51);
		heuristicResult.add(192273.5);
		heuristicResult.add(213933.56);
		heuristicResult.add(205502.39);
		heuristicResult.add(200003.86);
		heuristicResult.add(207157.41);
		heuristicResult.add(187164.73);
		heuristicResult.add(202354.02);
		heuristicResult.add(197170.57);
		heuristicResult.add(200281.55);
		heuristicResult.add(227205.94);
		heuristicResult.add(183158.31);
		heuristicResult.add(201710.69);
		heuristicResult.add(192107.58);
		heuristicResult.add(191628.91);
		heuristicResult.add(195699.22);
		heuristicResult.add(206928.35);
		heuristicsResults.add(heuristicResult);
		
		
		return heuristicsResults;
	}
	
	private ArrayList<String> getHeuristicsNames(){
		return new ArrayList<String>(Arrays.asList("VND", "HGA1"));
	}
	
	private String getChartName() {
		return "Performance Profile comparing the four analysed heuristics";
	}
	
	private JFreeChart createChart(final XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(
				getChartName(), 
                "Deviation", 
                "Instances Probability Amount", 
                dataset
                /*PlotOrientation.VERTICAL,
                true, 
                true, 
                false*/
        );
		
		XYPlot plot = chart.getXYPlot();
        /*XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        renderer.setSeriesPaint(0, Color.RED);
        //renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        
        renderer.setSeriesPaint(1, Color.BLUE);
        //renderer.setSeriesStroke(1, new BasicStroke(1.0f));        

        if(RUN_GA_VERSION) {
        	renderer.setSeriesPaint(2, Color.GREEN);
            renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        }
        
        plot.setRenderer(renderer);*/
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
	
	private XYDataset createDataset(ArrayList<ArrayList<Double>> heuristicsDeviations, ArrayList<String> heuristicNames) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        for(int i = 0; i < heuristicsDeviations.size(); i++) {
        	dataset.addSeries(createSeries(heuristicsDeviations.get(i), heuristicNames.get(i)));
        }

        return dataset;
    }
	
	private XYSeries createSeries(ArrayList<Double> heuristicDeviations, String seriesName) {
		XYSeries series = new XYSeries(seriesName);
		int i = 1;
		
		Collections.sort(heuristicDeviations);
		for(int index = 0; index < heuristicDeviations.size(); index++) {
			series.add((double)heuristicDeviations.get(index), getPercentage(heuristicDeviations.size(), i));
			i++;
		}
		
		return series;
	}
	
	private double getPercentage(int amountInstancesAnalysed, int sequenceInstanceIndex) {
		double offset = 100 / (double)amountInstancesAnalysed;
		return offset * sequenceInstanceIndex;
	}
	
	private ArrayList<ArrayList<Double>> computeDeviationEachHeuristic(ArrayList<ArrayList<Double>> heuristicsResults, int instancesQty) {
		ArrayList<ArrayList<Double>> heuristicsDeviations = new ArrayList<>(heuristicsResults.size());
		
		initializeMatrix(heuristicsDeviations, heuristicsResults.size());
		for(int i = 0; i < instancesQty; i++) {
			setDeviationsSpecificInstance(heuristicsResults, heuristicsDeviations, i);
		}
		
		return heuristicsDeviations;
	}
	
	private void setDeviationsSpecificInstance(ArrayList<ArrayList<Double>> heuristicsResults, 
			                                   ArrayList<ArrayList<Double>> heuristicsDeviations, int instanceIndex) {
		double maxResultHeuristics = 0.0, heuristResult = 0.0;
		ArrayList<Double> heuristicDeviation= null;
		
		maxResultHeuristics = findMaxResultOfInstance(heuristicsResults, instanceIndex);
		for(int heuristicIndex = 0; heuristicIndex < heuristicsResults.size(); heuristicIndex++) {
			heuristicDeviation = heuristicsDeviations.get(heuristicIndex);
			heuristResult = heuristicsResults.get(heuristicIndex).get(instanceIndex);
			heuristicDeviation.add(getDeviation(heuristResult, maxResultHeuristics));
		}
	}
	
	private double getDeviation(double instanceResult, double maxResult) {
		return ((maxResult - instanceResult) / maxResult) * 100.0;
	}
	
	private void initializeMatrix(ArrayList<ArrayList<Double>> matrix, int size) {
		for(int i = 0; i < size; i++) {
			matrix.add(new ArrayList<>());
		}
	}
	
	private double findMaxResultOfInstance(ArrayList<ArrayList<Double>> heuristicsResults, int instanceIndex) {
		double max = 0.0;
		
		for(int i = 0; i < heuristicsResults.size(); i++) {
			ArrayList<Double> heuristicResults = heuristicsResults.get(i);
			if(heuristicResults.get(instanceIndex) > max) {
				max = heuristicResults.get(instanceIndex);
			}
		}
		
		return max;
	}
}
