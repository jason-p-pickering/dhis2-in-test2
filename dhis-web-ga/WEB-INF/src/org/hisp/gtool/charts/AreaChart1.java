package org.hisp.gtool.charts;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.VerticalAlignment;

public class AreaChart1 {

//	Row Keys
	String[] series;
	// Column Keys
	String[] categories;
	// data...	
	double[][] data;
	
	String chartTitle;
	String xAxis_Title;
	String yAxis_Title;
	
// Data Related Line Chart
	
	/*
	 String[] series2;
	 // Column Keys
	  String[] categories2;
	*/
	
	// data...	
	double[][] data2;
	
	public String getChartTitle() {
		return chartTitle;
	}


	public void setChartTitle(String chartTitle) {
		this.chartTitle = chartTitle;
	}


	//	constructor
	public AreaChart1() { }

	
	private DefaultCategoryDataset  getDataset() 
	{    
		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series.length;i++)
		{
			for(int j=0;j<categories.length;j++)
			{
				dataset.addValue(data[i][j], series[i], categories[j]);
			}
		}
		
		return dataset;
	}

//	 Function For Creating Dataset for Linechart
	private DefaultCategoryDataset  getDataset2() 
	{    
		
		//	create the dataset...
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
					
		for(int i=0;i<series.length;i++)
		{
			series[i]+="(Target)";
			for(int j=0;j<categories.length;j++)
			{
				dataset.addValue(data2[i][j], series[i], categories[j]);
			}
		}
		
		return dataset;
	}
	
 public String getChartViewer(HttpServletRequest request, HttpServletResponse response) {
	 DefaultCategoryDataset dataset = getDataset();
	 final JFreeChart chart = ChartFactory.createAreaChart(
	            "",             // chart title
	            "Category",               // domain axis label
	            "Value",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );
	 
	        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	        // set the background color for the chart...
//	        final StandardLegend legend = (StandardLegend) chart.getLegend();
	  //      legend.setAnchor(StandardLegend.SOUTH);

	//        chart.setBackgroundPaint(Color.white);
	 		
	 /* TITLE */
     		final TextTitle mainTitle = new TextTitle("Area Chart");
     		mainTitle.setFont(new Font("times", Font.BOLD, 13));
     		mainTitle.setPosition(RectangleEdge.TOP);     
     		mainTitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
     		chart.addSubtitle(mainTitle);	 
	        
	/* SUB TITLE */     
     	     final TextTitle subtitle = new TextTitle(chartTitle);
     	     subtitle.setFont(new Font("times", Font.BOLD, 13));
     	     subtitle.setPosition(RectangleEdge.TOP);     
     	     subtitle.setVerticalAlignment(VerticalAlignment.BOTTOM);
     	     chart.addSubtitle(subtitle);

	        final CategoryPlot plot = chart.getCategoryPlot();
	        plot.setForegroundAlpha(0.5f);
	        
	  //      plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
	//        plot.setBackgroundPaint(Color.lightGray);
	//        plot.setDomainGridlinesVisible(true);
	//        plot.setDomainGridlinePaint(Color.white);
	//        plot.setRangeGridlinesVisible(true);
	//        plot.setRangeGridlinePaint(Color.white);
	        
	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setLabel(xAxis_Title);
	        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	        domainAxis.setLowerMargin(0.0);
	        domainAxis.setUpperMargin(0.0);
	        domainAxis.addCategoryLabelToolTip("Type 1", "The first type.");
	        domainAxis.addCategoryLabelToolTip("Type 2", "The second type.");
	        domainAxis.addCategoryLabelToolTip("Type 3", "The third type.");
	        
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setLabel(yAxis_Title);
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setLabelAngle(0 * Math.PI / 2.0);
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        rangeAxis.setUpperMargin(0.15);
	        rangeAxis.setLowerMargin(0.15);

	        // disable bar outlines...
	        final CategoryItemRenderer renderer = plot.getRenderer();
	        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
	        renderer.setItemLabelsVisible(true); 

	        // For Line Chart
	        final CategoryDataset dataset2 = getDataset2();
	   	 
	        final CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
	        renderer2.setSeriesStroke(0, new BasicStroke(2.0f));
	        renderer2.setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.setDataset(1, dataset2);
	        plot.setRenderer(1, renderer2);
	        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	        // OPTIONAL CUSTOMISATION COMPLETED.




   ChartRenderingInfo info = null;
   HttpSession session = request.getSession();
   try {

     //Create RenderingInfo object
     response.setContentType("text/html");
     info = new ChartRenderingInfo(new StandardEntityCollection());
     BufferedImage chartImage = chart.createBufferedImage(800, 500, info);

     // putting chart as BufferedImage in session, 
     // thus making it available for the image reading action Action.
     session.setAttribute("chartImage", chartImage);

     PrintWriter writer = new PrintWriter(response.getWriter());
     
     
     ChartUtilities.writeImageMap(writer,"imageMap",info,true);
     
     
     writer.flush();
   
   }
   catch (Exception e) {
      // handel your exception here
   }
  
   String pathInfo = "http://";
   pathInfo += request.getServerName();
   int port = request.getServerPort();
   pathInfo += ":"+String.valueOf(port);
   pathInfo += request.getContextPath();
   String chartViewer = pathInfo + "/servlet/ChartViewer";
   return chartViewer;
 }


public String[] getCategories() {
	return categories;
}


public void setCategories(String[] categories) {
	this.categories = categories;
}


public double[][] getData() {
	return data;
}


public void setData(double[][] data) {
	this.data = data;
}


public String[] getSeries() {
	return series;
}


public void setSeries(String[] series) {
	this.series = series;
}





public double[][] getData2() {
	return data2;
}


public void setData2(double[][] data2) {
	this.data2 = data2;
}


public String getXAxis_Title() {
	return xAxis_Title;
}


public void setXAxis_Title(String axis_Title) {
	xAxis_Title = axis_Title;
}


public String getYAxis_Title() {
	return yAxis_Title;
}


public void setYAxis_Title(String axis_Title) {
	yAxis_Title = axis_Title;
}



	
}
