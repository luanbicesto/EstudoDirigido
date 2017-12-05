package tests;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
		ArrayList<ArrayList<Double>> heuristicsDeviations = computeDeviationEachHeuristic(heuristicsResults, heuristicsResults.size());
		XYDataset dataset = createDataset(heuristicsDeviations, getHeuristicsNames());
		JFreeChart performanceProfileChart = createChart(dataset);
		new File("PerformanceProfile.png").delete();
		ChartUtilities.saveChartAsPNG(new File("TTTPlot.png"), performanceProfileChart, 800, 800);
	}
	
	private ArrayList<ArrayList<Double>> getHeuristicsResults(){
		int heuristicQty = 2;
		ArrayList<ArrayList<Double>> heuristicsResults = new ArrayList<>(heuristicQty);
		
		ArrayList<Double> heuristicResult = new ArrayList<>();
		
		
		return heuristicsResults;
	}
	
	private ArrayList<String> getHeuristicsNames(){
		return new ArrayList<>();
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
		ArrayList<ArrayList<Double>> heuristicsDeviations = new ArrayList<>();
		
		initializeMatrix(heuristicsDeviations);
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
			heuristicDeviation.set(instanceIndex, getDeviation(heuristResult, maxResultHeuristics));
		}
	}
	
	private double getDeviation(double instanceResult, double maxResult) {
		return (maxResult - instanceResult) / maxResult;
	}
	
	private void initializeMatrix(ArrayList<ArrayList<Double>> matrix) {
		int matrixSize = matrix.size();
		
		for(int i = 0; i < matrixSize; i++) {
			matrix = new ArrayList<>();
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
