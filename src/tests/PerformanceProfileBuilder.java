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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
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
		ArrayList<ArrayList<Double>> heuristicsResults = getHeuristicsResults480();
		ArrayList<ArrayList<Double>> heuristicsDeviations = computeDeviationEachHeuristic(heuristicsResults, heuristicsResults.get(0).size());
		XYDataset dataset = createDataset(heuristicsDeviations, getHeuristicsNames());
		JFreeChart performanceProfileChart = createChart(dataset);
		new File("PerformanceProfile.png").delete();
		ChartUtilities.saveChartAsPNG(new File("PerformanceProfile.png"), performanceProfileChart, 800, 800);
	}
	
	private ArrayList<ArrayList<Double>> getHeuristicsResults(){
		//int heuristicQty = 4;
		ArrayList<ArrayList<Double>> heuristicsResults = new ArrayList<>();
		
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
		
		//HGA2 - 240
		heuristicResult = new ArrayList<>();
		heuristicResult.add(222999.67);
		heuristicResult.add(202289.82);
		heuristicResult.add(196042.09);
		heuristicResult.add(221651.61);
		heuristicResult.add(192464.59);
		heuristicResult.add(213711.97);
		heuristicResult.add(206339.62);
		heuristicResult.add(201619.99);
		heuristicResult.add(206580.12);
		heuristicResult.add(188603.62);
		heuristicResult.add(201211.0);
		heuristicResult.add(197692.45);
		heuristicResult.add(199319.34);
		heuristicResult.add(226133.84);
		heuristicResult.add(187689.24);
		heuristicResult.add(200230.42);
		heuristicResult.add(191582.04);
		heuristicResult.add(191478.15);
		heuristicResult.add(196099.56);
		heuristicResult.add(209429.64);
		heuristicsResults.add(heuristicResult);
		
		//GA - 240
		/*heuristicResult = new ArrayList<>();
		heuristicResult.add(176725.53);
		heuristicResult.add(180810.85);
		heuristicResult.add(176112.65);
		heuristicResult.add(180761.04);
		heuristicResult.add(160354.34);
		heuristicResult.add(188783.16);
		heuristicResult.add(185505.02);
		heuristicResult.add(174771.8);
		heuristicResult.add(173475.5);
		heuristicResult.add(161538.65);
		heuristicResult.add(168888.51);
		heuristicResult.add(175242.23);
		heuristicResult.add(170055.28);
		heuristicResult.add(176530.91);
		heuristicResult.add(158852.47);
		heuristicResult.add(175163.74);
		heuristicResult.add(178082.18);
		heuristicResult.add(159429.58);
		heuristicResult.add(162221.58);
		heuristicResult.add(181634.65);
		heuristicsResults.add(heuristicResult);*/
		
		
		return heuristicsResults;
	}

	private ArrayList<ArrayList<Double>> getHeuristicsResults480(){
		//int heuristicQty = 4;
		ArrayList<ArrayList<Double>> heuristicsResults = new ArrayList<>();
		
		ArrayList<Double> heuristicResult = new ArrayList<>();
		//VND - 480
		heuristicResult.add(547892.82);
		heuristicResult.add(502322.88);
		heuristicResult.add(486391.16);
		heuristicResult.add(513016.49);
		heuristicResult.add(474143.57);
		heuristicResult.add(523003.55);
		heuristicResult.add(536212.13);
		heuristicResult.add(523419.36);
		heuristicResult.add(548621.16);
		heuristicResult.add(511338.69);
		heuristicResult.add(516058.83);
		heuristicResult.add(491899.13);
		heuristicResult.add(524255.44);
		heuristicResult.add(505311.16);
		heuristicResult.add(508386.57);
		heuristicResult.add(541827.8);
		heuristicResult.add(529671.66);
		heuristicResult.add(516230.55);
		heuristicResult.add(514385.62);
		heuristicResult.add(508986.95);
		heuristicsResults.add(heuristicResult);
		
		heuristicResult = new ArrayList<>();
		//HGA1 - 480
		heuristicResult.add(538274.51);
		heuristicResult.add(494233.2);
		heuristicResult.add(474178.27);
		heuristicResult.add(499211.81);
		heuristicResult.add(443003.92);
		heuristicResult.add(515271.29);
		heuristicResult.add(509335.81);
		heuristicResult.add(515118.17);
		heuristicResult.add(536750.28);
		heuristicResult.add(505869.64);
		heuristicResult.add(504676.21);
		heuristicResult.add(483658.43);
		heuristicResult.add(510764.03);
		heuristicResult.add(497304.56);
		heuristicResult.add(489746.01);
		heuristicResult.add(534209.93);
		heuristicResult.add(517870.96);
		heuristicResult.add(505095.91);
		heuristicResult.add(503640.3);
		heuristicResult.add(494866.03);
		heuristicsResults.add(heuristicResult);
		
		//HGA2 - 480
		heuristicResult = new ArrayList<>();
		heuristicResult.add(541323.56);
		heuristicResult.add(494566.99);
		heuristicResult.add(461741.33);
		heuristicResult.add(505371.37);
		heuristicResult.add(468730.05);
		heuristicResult.add(518994.6);
		heuristicResult.add(533060.38);
		heuristicResult.add(511998.19);
		heuristicResult.add(534446.77);
		heuristicResult.add(502661.21);
		heuristicResult.add(508544.57);
		heuristicResult.add(487454.46);
		heuristicResult.add(517925.12);
		heuristicResult.add(497255.93);
		heuristicResult.add(500697.78);
		heuristicResult.add(521722.88);
		heuristicResult.add(519700.8);
		heuristicResult.add(512049.1);
		heuristicResult.add(513157.12);
		heuristicResult.add(500638.28);
		heuristicsResults.add(heuristicResult);
		
		//GA - 480
		heuristicResult = new ArrayList<>();
		heuristicResult.add(371292.34);
		heuristicResult.add(368320.93);
		heuristicResult.add(375478.81);
		heuristicResult.add(367377.17);
		heuristicResult.add(366978.19);
		heuristicResult.add(380293.87);
		heuristicResult.add(381508.56);
		heuristicResult.add(366324.49);
		heuristicResult.add(375566.68);
		heuristicResult.add(367397.06);
		heuristicResult.add(366949.46);
		heuristicResult.add(364741.59);
		heuristicResult.add(381547.33);
		heuristicResult.add(371176.39);
		heuristicResult.add(361874.84);
		heuristicResult.add(380892.31);
		heuristicResult.add(381859.37);
		heuristicResult.add(362101.91);
		heuristicResult.add(365856.74);
		heuristicResult.add(379352.56);
		heuristicsResults.add(heuristicResult);
		
		
		return heuristicsResults;
	}

	
	private ArrayList<String> getHeuristicsNames(){
		return new ArrayList<String>(Arrays.asList("VND", "HGA1", "HGA2", "GA"));
		//return new ArrayList<String>(Arrays.asList("VND", "HGA1", "HGA2"));
	}
	
	private String getChartName() {
		//return "Performance Profile comparing the best three analysed heuristics";
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
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        renderer.setSeriesPaint(0, Color.RED);
        //renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        
        renderer.setSeriesPaint(1, Color.BLUE);
        //renderer.setSeriesStroke(1, new BasicStroke(1.0f));        

        renderer.setSeriesPaint(2, Color.GREEN);
        
        //renderer.setSeriesPaint(3, Color.BLACK);
        
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
