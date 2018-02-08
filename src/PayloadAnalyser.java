import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramDataset;

class PayLoad{
	String appName;
	String payLoad;
	String tag;
	
	PayLoad(String a, String p, String t){
		appName = a;
		payLoad = p;
		tag = t;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(String payLoad) {
		this.payLoad = payLoad;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
}

public class PayloadAnalyser {
	
	static double[] test = {0,0,0,0,0,0,0,0,0,0};
	static HistogramDataset dataset;
	
	static BufferedReader in = null;
	
	
	static int[] srcPayloadSum = new int[265];
	static int[] destPayloadSum = new int [265];
	//static double[] srcPayloadAverage = new double[265];
	static int numSrcPayloads = 0;
	
	static JTextArea txtSource = null;
	static JTextArea txtDest = null;

	static boolean running = false;
	
	static Timer timer = null;
	
	static ButtonGroup sourceGroup = null;
	static ButtonGroup destGroup = null;
	
	static JLabel statusLabel = null;

	static int packetNumber = 0;
	static int sampleA = 0;
	static int sampleB = 0;
	
	public static void openFile(){
		//System.out.println("About to create file stuff");
		try{ 
			//String currentDir = System.getProperty("user.dir");
	        //System.out.println("Current dir using System:" +currentDir);
			in = new BufferedReader(new FileReader("payload.csv"));
		}
		catch(IOException e){
			System.out.println("Error: Could not open file");
		}
		for(int i = 0; i < srcPayloadSum.length; i++){
			srcPayloadSum[i] = 0;
			//srcPayloadAverage[i] = 0;
		}
	}
	private static JFreeChart createChart(String chartName) {
	    
		
	    dataset = new HistogramDataset();
	    
	    //dataset.addSeries("Payload", createSeries(), 256, 0, 255);
	    
	    
	    
	    JFreeChart chart = ChartFactory.createHistogram(
	              chartName, 
	              null, 
	              null, 
	              dataset, 
	              PlotOrientation.VERTICAL, 
	              true, 
	              false, 
	              false
	          );

	    chart.setBackgroundPaint(new Color(230,230,230));
	    XYPlot xyplot = (XYPlot)chart.getPlot();
	    xyplot.setForegroundAlpha(0.8F);
	    xyplot.setBackgroundPaint(Color.WHITE);
	    xyplot.setDomainGridlinePaint(new Color(150,150,150));
	    xyplot.setRangeGridlinePaint(new Color(150,150,150));
	    XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
	    xybarrenderer.setShadowVisible(false);
	    xybarrenderer.setBarPainter(new StandardXYBarPainter()); 
	    //	    xybarrenderer.setDrawBarOutline(false);
	    return chart;
	  }
	
	/*public static double[] charCounts(String s) {
		  double[] counts = new double[256]; // maximum value of an ASCII character
		  
		  char[] c = s.toCharArray();
		  
		  for (int i=0;i<c.length;++i) {
		      counts[c[i]]++;
			
		  }
		   
		  return counts;
		}
	*/
	public static double[] charCounts(String s) {
		  
		  double[] counts = new double[256]; // maximum value of an ASCII character
		  char[] c = s.toCharArray();
		  //numSrcPayloads++;
		  for (int i=0;i<c.length;++i) {
			  counts[c[i]]++;
		  }
		  return counts;
	}
	public static double[] averageSource(String s) {
		  char[] c = s.toCharArray();
		  int total = 0;
		  for (int i=0;i<c.length;++i) {
			  srcPayloadSum[c[i]]++;
		  }
		  for(int i = 0; i < 256; i++){
			  total = total + srcPayloadSum[i];
		  }
		  //int index = 0;
		  List<Double> list = new ArrayList<Double>();
		  for(int l = 0; l < 256; l++){
			  int run = (int)(255 * ((double)srcPayloadSum[l] / (double)total));
			  for(int j = 0; j < run; j++){
				  list.add((double) l);
				  //index++;
			  }
			  
		  }
		  return convertDoubles(list);
	}
	public static double[] averageDest(String s) {
		  char[] c = s.toCharArray();
		  int total = 0;
		  for (int i=0;i<c.length;++i) {
			  destPayloadSum[c[i]]++;
		  }
		  for(int i = 0; i < 256; i++){
			  total = total + destPayloadSum[i];
		  }
		  List<Double> list = new ArrayList<Double>();
		  for(int l = 0; l < 256; l++){
			  int run = (int)(255 * ((double)destPayloadSum[l] / (double)total));
			  for(int j = 0; j < run; j++){
				  list.add((double) l);
			  }
		  }
		  return convertDoubles(list);
	}
	public static double[] convertDoubles(List<Double> doubles)
	{
	    double[] ret = new double[doubles.size()];
	    Iterator<Double> iterator = doubles.iterator();
	    int i = 0;
	    while(iterator.hasNext())
	    {
	        ret[i] = iterator.next().doubleValue();
	        i++;
	    }
	    return ret;
	}
	public static double[] getOrdinals(String s){
		double[] ords = new double[s.length()];
		char[] c = s.toCharArray();
		for(int i = 0; i < s.length(); i++){
			ords[i] = c[i];
		}
		
		return ords;
	}
	/*public static String getNextPacket(String appName){
		String str;
		try{
			if (in != null){
				if ( (str = in.readLine()) != null) {
					str = str.replace("\"", "");
					String[] parts = str.split(",");
					
					if (parts[0].equals(appName))
						return parts[1]; // Payload part of input
					else
						return null;
				}
			}
			
		}
		catch(IOException e){
			
		}
		return null;
	}*/
	public static PayLoad getNextPacket(){
		String str;
		try{
			if (in != null){
				if ( (str = in.readLine()) != null) {
					//System.out.println("Going to split the strings : " + str);
					str = str.replace("\"", "");
					String[] parts = str.split(",");
										
					return new PayLoad(parts[0], parts[1], parts[2]);
					//return parts[1]; // Payload part of input
					
				}
			}
			
		}
		catch(IOException e){
			
		}
		return null;
	}
	public static double[] createSeries(){
		double series[] = new double[256];
		for(int i = 0; i < series.length; i++){
			series[i] = Math.random() * 256;
		}
		return series;
	}
	public static void updateSourceChart(JFreeChart chart, PayLoad payLoad){
		
		//System.out.println("About to get next packet");
		//String payload = getNextPacket(sourceGroup.getSelection().getActionCommand());
		String p = payLoad.getPayLoad();
		if (p != null){
			txtSource.setText(p);
			//average(payload);
			HistogramDataset d = new HistogramDataset();
			d.addSeries("Payload", getOrdinals(p), 256, 0, 255);
			d.addSeries("Average", averageSource(p), 256, 0, 255);
			//d.addSeries("Average", srcPayloadAverage, 256, 0, 255);
			
			chart.getXYPlot().setDataset(d);
		}
		
		
	}
	public static void updateDestChart(JFreeChart chart, PayLoad payLoad){
		//String payload = getNextPacket(destGroup.getSelection().getActionCommand());
		String p = payLoad.getPayLoad();
		if (p != null){
			txtDest.setText(p);
			//average(payload);
			HistogramDataset d = new HistogramDataset();
			d.addSeries("Payload", getOrdinals(p), 256, 0, 255);
			d.addSeries("Average", averageDest(p), 256, 0, 255);
			//d.addSeries("Average", srcPayloadAverage, 256, 0, 255);
			
			chart.getXYPlot().setDataset(d);
		}
		
		
		
	}
	
	public static void main(String s[]) {
		JFrame frame = new JFrame("Network Packet - Payload Analyser");
		
		// Add a window listner for close button
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				try {
					in.close();
				} catch (IOException e1) {
					System.out.println("Error: Problem closing file");
				}
				System.exit(0);
			}
		});
		 //Handle file opening for reading packets
		 openFile();
		
		 final JFreeChart sourceChart = createChart("Sample A");
		 final JFreeChart destChart = createChart("Sample B");
		 
		 JRadioButton[] sourceFilter = new JRadioButton[5];
		 JRadioButton[] destFilter = new JRadioButton[5];
		 
		 sourceFilter[0] = new JRadioButton();
		 sourceFilter[1] = new JRadioButton();
		 sourceFilter[2] = new JRadioButton();
		 sourceFilter[3] = new JRadioButton();
		 sourceFilter[4] = new JRadioButton();
		 
		 destFilter[0] = new JRadioButton();
		 destFilter[1] = new JRadioButton();
		 destFilter[2] = new JRadioButton();
		 destFilter[3] = new JRadioButton();
		 destFilter[4] = new JRadioButton();
		 
		 JPanel sourceFilterPanel = new JPanel();
		 JPanel destFilterPanel = new JPanel();
		 JPanel filterPanel = new JPanel();
		 
		 sourceFilterPanel.setLayout(new FlowLayout());
		 destFilterPanel.setLayout(new FlowLayout());
		 		 
		 
		 sourceFilter[0].setText("HTTPWeb");
		 sourceFilter[0].setActionCommand("HTTPWeb");
		 sourceFilter[1].setText("FTP");
		 sourceFilter[1].setActionCommand("FTP");
		 sourceFilter[2].setText("DNS");
		 sourceFilter[2].setActionCommand("DNS");
		 sourceFilter[3].setText("WindowsFileSharing");
		 sourceFilter[3].setActionCommand("WindowsFileSharing");
		 sourceFilter[4].setText("POP");
		 sourceFilter[4].setActionCommand("POP");
		 
		 
		 destFilter[0].setText("HTTPWeb");
		 destFilter[0].setActionCommand("HTTPWeb");
		 destFilter[1].setText("FTP");
		 destFilter[1].setActionCommand("FTP");
		 destFilter[2].setText("DNS");
		 destFilter[2].setActionCommand("DNS");
		 destFilter[3].setText("WindowsFileSharing");
		 destFilter[3].setActionCommand("WindowsFileSharing");
		 destFilter[4].setText("POP");
		 destFilter[4].setActionCommand("POP");
		 
		 sourceGroup = new ButtonGroup();
		 destGroup = new ButtonGroup();
		 
		 sourceGroup.add(sourceFilter[0]);
		 sourceGroup.add(sourceFilter[1]);
		 sourceGroup.add(sourceFilter[2]);
		 sourceGroup.add(sourceFilter[3]);
		 sourceGroup.add(sourceFilter[4]);
		 sourceGroup.setSelected(sourceFilter[0].getModel(), true);
		 
		 destGroup.add(destFilter[0]);
		 destGroup.add(destFilter[1]);
		 destGroup.add(destFilter[2]);
		 destGroup.add(destFilter[3]);
		 destGroup.add(destFilter[4]);
		 destGroup.setSelected(destFilter[1].getModel(), true);
		 
		 sourceFilterPanel.add(sourceFilter[0]);
		 sourceFilterPanel.add(sourceFilter[1]);
		 sourceFilterPanel.add(sourceFilter[2]);
		 sourceFilterPanel.add(sourceFilter[3]);
		 sourceFilterPanel.add(sourceFilter[4]);
		 
		 destFilterPanel.add(destFilter[0]);
		 destFilterPanel.add(destFilter[1]);
		 destFilterPanel.add(destFilter[2]);
		 destFilterPanel.add(destFilter[3]);
		 destFilterPanel.add(destFilter[4]);
		 
		 filterPanel.add(sourceFilterPanel);
		 filterPanel.add(destFilterPanel);
		 
		 txtSource = new JTextArea("PAYLOAD:");
		 txtSource.setRows(10);
		 txtSource.setColumns(55);
		 txtSource.setLineWrap(true);
		 
		 txtDest = new JTextArea("PAYLOAD:");
		 txtDest.setRows(10);
		 txtDest.setColumns(55);
		 txtDest.setLineWrap(true);
		 
		 JPanel statusPanel = new JPanel();
		 statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		 statusPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		 statusLabel = new JLabel("status");
		 statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		 statusPanel.add(statusLabel);
		 
		 
		 JPanel payloadPanel = new JPanel();
		 payloadPanel.setLayout(new BorderLayout());
		 payloadPanel.add(filterPanel, BorderLayout.NORTH);
		 payloadPanel.add(new JScrollPane(txtSource), BorderLayout.WEST);
		 payloadPanel.add(new JScrollPane(txtDest), BorderLayout.EAST);
		 payloadPanel.add(statusPanel, BorderLayout.SOUTH);
		 
		 
		 
		 ChartPanel sourceCpanel = new ChartPanel(sourceChart);
		 ChartPanel destCpanel = new ChartPanel(destChart);
		 
		 
		//Create the menu bar.
		 JMenuBar menuBar = new JMenuBar();

		 //Build the first menu.
		 JMenu menu = new JMenu("Packet Analyser");
		 
		 menu.setMnemonic(KeyEvent.VK_A);
		 menu.getAccessibleContext().setAccessibleDescription(
		         "The only menu in this program that has menu items");
		 
		 JMenuItem menuStart = new JMenuItem("Start");
		 menuStart.setMnemonic(KeyEvent.VK_S);
		 JMenuItem menuStop = new JMenuItem("Stop");
		 menuStop.setMnemonic(KeyEvent.VK_T);
		 
		//adding action listener to menu items
			menuStart.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						if (!running && timer != null){
							timer.start();
							running = true;
						}
							
					}
				}
			);
			menuStop.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						if (running && timer != null){
							timer.stop();
							running = false;
						}
							
					}
				}
			);
		 
		 
		 menu.add(menuStart);
		 menu.add(menuStop);
		 
		 menuBar.add(menu);
		 
		 
		
		 frame.setJMenuBar(menuBar);
		 
		 frame.getContentPane().setLayout(new BorderLayout());
		 frame.getContentPane().add(sourceCpanel, BorderLayout.WEST);
		 frame.getContentPane().add(destCpanel, BorderLayout.EAST);
		 frame.getContentPane().add(payloadPanel, BorderLayout.SOUTH);
		 
		 
		    
		 timer = new Timer(10, new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	
	            	// Update the charts here
	            	PayLoad payLoad = getNextPacket();
	            	
	            	if (payLoad != null){
	            		packetNumber++;
		            	
		            	//System.out.println("Processing packet number: " + packetNumber);
		            	if (payLoad.getAppName().equals(sourceGroup.getSelection().getActionCommand())){
		            	//if (payLoad.getTag().equals("Normal")){
		            		updateSourceChart(sourceChart, payLoad);
		            		sampleA++;
		            	}
		            	//else if (payLoad.getTag().equals("Attack")){
		            	else if (payLoad.getAppName().equals(destGroup.getSelection().getActionCommand())){
		            		updateDestChart(destChart, payLoad);
		            		sampleB++;
		            	}
		            	statusLabel.setText("Processing packet No." + packetNumber + " Sample A: " + sampleA + " Sample B: " + sampleB);
	            	}
	            	
	            	
	                
	            }
	        });
	       
		frame.pack();
		frame.setVisible(true);
	}
	
}