package azim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageDrawingPanel extends JPanel{
	
	
	/*		Fields		*/
	private BufferedImage bi;
	private Image bi_Scaled;
	private double ratio;
	private int imageW;
	private int imageH;
	private int imagePanelW;
	private int imagePanelH;
	private int minIntensity_R;
	private int minIntensity_G;
	private int minIntensity_B;
	private int maxIntensity_R;
	private int maxIntensity_G;
	private int maxIntensity_B;
	
	private int operationIndex;
	private int[][] imageArray;
	private int[][] imageArray_R;
	private int[][] imageArray_G;
	private int[][] imageArray_B;
	private int[][] tempImageArray_R;
	private int[][] tempImageArray_G;
	private int[][] tempImageArray_B;
	private int[][] temp_2_Array;
	private int[][] temp_3_Array;
	
	MedianCut MC;
	int[] pivots_R = null;
	int[] pivots_G = null;
	int[] pivots_B = null;
	int[] thresholds_R = null;
	int[] thresholds_G = null;
	int[] thresholds_B = null;
	int[] count_R = null;
	int[] count_G = null;
	int[] count_B = null;
	int givenLevel = 2;
	int medianLevel = 2;
	int n_Cubes = 0;
	int isB_W = 0;
	/*		Constructors	*/
	public ImageDrawingPanel() {
		super();
	}

	public ImageDrawingPanel(File imageFile) {
		
		try {
			bi = ImageIO.read(imageFile);
			imageW = bi.getWidth(null);
			imageH = bi.getHeight(null);
			
			imageArray = new int[imageW*imageH][3];
			imageArray_R = new int[imageW][imageH];
			imageArray_G = new int[imageW][imageH];
			imageArray_B = new int[imageW][imageH];
			tempImageArray_R = new int[imageW][imageH];
			tempImageArray_G = new int[imageW][imageH];
			tempImageArray_B = new int[imageW][imageH];
			temp_2_Array = new int[imageW][imageH];
			temp_3_Array = new int[imageW][imageH];
			
			convertToArray(bi);//imageArray_X[][] will be initialized here.
			
			if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
				BufferedImage bi_temp = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
				Graphics bi_tempG = bi_temp.getGraphics();
				bi_tempG.drawImage(bi, 0, 0, null);
				bi = bi_temp;
			}
		} catch (IOException e) {
			System.out.println("Image could not be read.");
			//System.exit(1);
		}
	}
	
	public void setOperationIndex(int oi){
		this.operationIndex = oi;
	}
	public int getImageW() {
		return imageW;
	}

	public int getImageH() {
		return imageH;
	}
	public void setImagePanelW(int w) {
		imagePanelW = w;
	}

	public void setImagePanelH(int h) {
		imagePanelH = h;
	}
	public void setGivenLevel(int k){
		givenLevel = k;
	}
	public void setMedianLevel(int k){
		medianLevel = k;
		//With k level, we will have 2^k cubes:
		n_Cubes = (int)Math.pow((double)2, (double)k);
	}
	public void setBWCheckbox(int k){	//k is 0 (not checked) or 1 (checked)
		isB_W = k;
	}
	public void deallocateIDP(){
		imageArray = null;
		imageArray_R = null;
		imageArray_G = null;
		imageArray_B = null;
		tempImageArray_R = null;
		tempImageArray_G = null;
		tempImageArray_B = null;
		temp_2_Array = null;
		temp_3_Array = null;
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		

		
		
		/*________________S W I T C H_________________*/
		
		switch (operationIndex) {
		case 0:			//OPEN
			//repaint();
			//g.clearRect(0, 0, 1000, 1000);
			bi = convertToBufferedImage(tempImageArray_R, tempImageArray_G, tempImageArray_B);
			bi_Scaled = reScale(bi);
			g2.drawImage(bi_Scaled, 0, 0, null);
			break;
			
		case 1:			//DITHERING
			
			thresholdFinder();
			averageDithering();
			if(isB_W == 1)
				grayScale();
			bi = convertToBufferedImage(tempImageArray_R, tempImageArray_G, tempImageArray_B);
			bi_Scaled = reScale(bi);
			g2.drawImage(bi_Scaled, 0, 0, null);
			break;
		case 2:			//MEDIANCUT
			
			//Create a MedianCut object
			MC = new MedianCut(imageArray, imageW, imageH);
			//Create divisions of the main cube
			MC.createCuboids(n_Cubes);
			//then replace each color of the image with the average color of a proper cube
			int ii = 0; 
			int jj = 0;
			for(int i = 0; i < imageW*imageH; i++){	//i < number of pixels
				ii = i / imageH;
				jj = i % imageH;
				
				for(int c = 0; c < n_Cubes; c++){
					if(	MC.list[c].min[0] < imageArray[i][0] &&
						MC.list[c].min[1] < imageArray[i][1] &&
						MC.list[c].min[2] < imageArray[i][2] &&
						MC.list[c].max[0] > imageArray[i][0] &&
						MC.list[c].max[1] > imageArray[i][1] &&
						MC.list[c].max[2] > imageArray[i][2]){
							
//							imageArray[i][0] = MC.list[c].averageColor[0];
//							imageArray[i][1] = MC.list[c].averageColor[1];
//							imageArray[i][2] = MC.list[c].averageColor[2];
							
							//replace tempImageArray width averageColors
							
							tempImageArray_R[ii][jj] = MC.list[c].averageColor[0];
							tempImageArray_G[ii][jj] = MC.list[c].averageColor[1];
							tempImageArray_B[ii][jj] = MC.list[c].averageColor[2];
							
//							if(tempImageArray_R[ii][jj] > 255 || tempImageArray_R[ii][jj] < 0){
//								System.out.println("RED >> OUT OF RANGE:"+tempImageArray_R[ii][jj]);
//							}
//							if(tempImageArray_G[ii][jj] > 255 || tempImageArray_G[ii][jj] < 0){
//								System.out.println("GREEN >> OUT OF RANGE:"+tempImageArray_G[ii][jj]);
//							}
//							if(tempImageArray_B[ii][jj] > 255 || tempImageArray_B[ii][jj] < 0){
//								System.out.println("BLUE >> OUT OF RANGE:"+tempImageArray_B[ii][jj]);
//							}
					}
				}
			}
			bi = convertToBufferedImage(tempImageArray_R, tempImageArray_G, tempImageArray_B);
			bi_Scaled = reScale(bi);
			g2.drawImage(bi_Scaled, 0, 0, null);
			break;
		case 3:
			bi = convertToBufferedImage(imageArray_R, imageArray_G, imageArray_B);
			bi_Scaled = reScale(bi);
			g2.drawImage(bi_Scaled, 0, 0, null);
			break;
			default:
		}
	}
	
	
	/*		Methods		*/
	
	
	
	void convertToArray(BufferedImage bi){
		
		for (int i = 0; i < imageW; i++){
			for (int j = 0; j < imageH; j++) {
				int colorValue = bi.getRGB(i, j);
				Color a = new Color(colorValue);
				imageArray_R[i][j] = a.getRed();
				imageArray_G[i][j] = a.getGreen();
				imageArray_B[i][j] = a.getBlue();
				tempImageArray_R[i][j] = imageArray_R[i][j];
				tempImageArray_G[i][j] = imageArray_G[i][j];
				tempImageArray_B[i][j] = imageArray_B[i][j];
				
				imageArray[(i*imageH) +j][0] = a.getRed();
				imageArray[(i*imageH) +j][1] = a.getGreen();
				imageArray[(i*imageH) +j][2] = a.getBlue();
			}
		}
		System.out.println("CONVERTION TO ARRAY DONE.");
		
	}
	
	BufferedImage convertToBufferedImage(int [][] array_R, int [][] array_G, int [][] array_B){
		
		final BufferedImage outputImage = new BufferedImage(imageW, imageH, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) outputImage.getGraphics();
		
		for (int i = 0; i < imageW; i++){
			for (int j = 0; j < imageH; j++) {
				int r = array_R[i][j];
				int g = array_G[i][j];
				int b = array_B[i][j];
				g2d.setColor(new Color(r, g, b));
				g2d.fillRect(i, j, 1, 1);
			}
		}
		System.out.println("CONVERTION TO BUFFEREDIMAGE DONE.");
		return outputImage;
	}
	
	Image reScale(BufferedImage bufImg){
		
		//scale the image to fit into the imagePanel-------------------------------
		if(imageW > imagePanelW){
			ratio = (double)imagePanelW / (double)imageW;
			}
		else if(imageH > imagePanelH){
			ratio = (double)imagePanelH / (double)imageH;
			}		
		else
			ratio = 1;
				
				
		return (bi.getScaledInstance((int)(imageW * ratio), (int)(imageH * ratio), bi.SCALE_DEFAULT));
		//-------------------------------------------------------------------------
	}
	//Find k-1 thresholds for a given k and store them in an array
	void thresholdFinder(){
		
//		int p_R = 0;//Ratio of max and min intensity of Red channel
//		int p_G = 0;//Ratio of max and min intensity of Green channel
//		int p_B = 0;//Ratio of max and min intensity of Blue channel
		int p; //256 divided by number of intervals = lengh of each intervla 
		int k = givenLevel - 1;
		pivots_R = new int[k + 1];//to store pivots
		pivots_G = new int[k + 1];//to store pivots
		pivots_B = new int[k + 1];//to store pivots
		
		thresholds_R = new int[k];//to store averages
		thresholds_G = new int[k];//to store averages
		thresholds_B = new int[k];//to store averages
		
		count_R = new int[k];//to store number of colors in each interval
		count_G = new int[k];
		count_B = new int[k];
		
//		maxIntensity_R = 0;		//initialized to black, but it should get the value of the brightest pixel.
//		maxIntensity_G = 0;
//		maxIntensity_B = 0;
//		minIntensity_R = 255;	//initialized to white, but it should get the value of the darkest pixel.
//		minIntensity_G = 255;
//		minIntensity_B = 255;
		
		/****************** R Channel *******************/
		//Find global min and max
//		for (int i = 0; i < imageW; i++){
//			for (int j = 0; j < imageH; j++) {
//				if(imageArray_R[i][j] > maxIntensity_R) {maxIntensity_R = imageArray_R[i][j];}
//				else if(imageArray_R[i][j] < minIntensity_R) {minIntensity_R = imageArray_R[i][j];}
//			}
//		}
//		System.out.println("Max_R: "+ maxIntensity_R + "\tMin_R: "+ minIntensity_R);
//		p_R = (maxIntensity_R - minIntensity_R) / k;
		p = 256 / k;	//length of each interval
		//Find pivots (i.e sides of intervals)
		for(int i = 0; i < k + 1 ; i++){
			pivots_R[i] = (i*p);
			System.out.println("pivots_R["+ i +"] =" + pivots_R[i]);
		}
		
		//find thresholds (i.e the average in each interval)
		for(int i = 0; i < imageW; i++){
			for(int j = 0; j < imageH; j++){
				for(int t = 0; t < k; t++){	//iterates for all desired number of colors
					if(imageArray_R[i][j] >= pivots_R[t] && imageArray_R[i][j] < pivots_R[t+1]){
						thresholds_R[t] += imageArray_R[i][j];
						count_R[t]++;
					}
					//System.out.println("thresholds_R["+i+"] = " + thresholds_R[i]);
				}
			}
		}
		for(int i = 0; i < k; i++)
			thresholds_R[i] = thresholds_R[i] / count_R[i]; 
		
		/****************** G Channel *******************/
		//Find global min and max
//		for (int i = 0; i < imageW; i++){
//			for (int j = 0; j < imageH; j++) {
//				if(imageArray_G[i][j] > maxIntensity_G) {maxIntensity_G = imageArray_G[i][j];}
//				else if(imageArray_G[i][j] < minIntensity_G) {minIntensity_G = imageArray_G[i][j];}
//			}
//		}
//		System.out.println("Max_G: "+ maxIntensity_G + "\tMin_G: "+ minIntensity_G);
//		p_G = (maxIntensity_G - minIntensity_G) / k;
		//Find pivots (i.e sides of intervals)
		
		for(int i = 0; i < k + 1 ; i++){
			pivots_G[i] = minIntensity_G + (i*p);
			System.out.println("pivots_G["+ i +"] =" + pivots_G[i]);
		}
		//find thresholds (i.e the average in each interval)
		for(int i = 0; i < imageW; i++){
			for(int j = 0; j < imageH; j++){
				for(int t = 0; t < k; t++){	//iterates for all desired number of colors
					if(imageArray_G[i][j] >= pivots_G[t] && imageArray_G[i][j] < pivots_G[t+1]){
						thresholds_G[t] += imageArray_G[i][j];
						count_G[t]++;
					}
					//System.out.println("thresholds_R["+i+"] = " + thresholds_R[i]);
				}
			}
		}
		for(int i = 0; i < k; i++)
			thresholds_G[i] = thresholds_G[i] / count_G[i]; 
		/****************** B Channel *******************/
		//Find global min and max
//		for (int i = 0; i < imageW; i++){
//			for (int j = 0; j < imageH; j++) {
//				if(imageArray_B[i][j] > maxIntensity_B) {maxIntensity_B = imageArray_B[i][j];}
//				else if(imageArray_B[i][j] < minIntensity_B) {minIntensity_B = imageArray_B[i][j];}
//			}
//		}
//		System.out.println("Max_B: "+ maxIntensity_B + "\tMin_B: "+ minIntensity_B);
//		p_B = (maxIntensity_B - minIntensity_B) / k;
		
		//Find pivots (i.e sides of intervals)
		for(int i = 0; i < k + 1 ; i++){
			pivots_B[i] = minIntensity_B + (i*p);	//pivots_B[0] = min, pivots_B[1] = min + 1p_B, pivots_B[2] = min + 2p_B, ...
			System.out.println("pivots_B["+ i +"] =" + pivots_B[i]);
		}
		
		//find thresholds (i.e the average in each interval)
		for(int i = 0; i < imageW; i++){
			for(int j = 0; j < imageH; j++){
				for(int t = 0; t < k; t++){	//iterates for all desired number of colors
					if(imageArray_B[i][j] >= pivots_B[t] && imageArray_B[i][j] < pivots_B[t+1]){
						thresholds_B[t] += imageArray_B[i][j];
						count_B[t]++;
					}
					//System.out.println("thresholds_R["+i+"] = " + thresholds_R[i]);
				}
			}
		}
		for(int i = 0; i < k; i++)
			thresholds_B[i] = thresholds_B[i] / count_B[i]; 
	}
	void averageDithering(){
		
		int k = givenLevel - 1;
		
		for (int i = 0; i < imageW; i++){
			for (int j = 0; j < imageH; j++) {
				for(int l = 0 ; l < k ; l++){
					/**** R Channel ***/
					if(imageArray_R[i][j] >= pivots_R[l] && imageArray_R[i][j] < pivots_R[l + 1])
						if(imageArray_R[i][j] <= thresholds_R[l])
							tempImageArray_R[i][j] = pivots_R[l];
						else
							tempImageArray_R[i][j] = pivots_R[l + 1];
					
					/**** G Channel ***/
					if(imageArray_G[i][j] >= pivots_G[l] && imageArray_G[i][j] < pivots_G[l + 1])
						if(imageArray_G[i][j] <= thresholds_G[l])
							tempImageArray_G[i][j] = pivots_G[l];
						else
							tempImageArray_G[i][j] = pivots_G[l + 1];
					
					/**** B Channel ***/
					if(imageArray_B[i][j] >= pivots_B[l] && imageArray_B[i][j] < pivots_B[l + 1])
						if(imageArray_B[i][j] <= thresholds_B[l])
							tempImageArray_B[i][j] = pivots_B[l];
						else
							tempImageArray_B[i][j] = pivots_B[l + 1];
				}
				
			}
		}
	}
	
	void grayScale(){
		int result = 0;
		for (int i = 0; i < imageW; i++){
			for (int j = 0; j < imageH; j++) {
				result = (int) (tempImageArray_R[i][j] * 0.299 +
								tempImageArray_G[i][j] * 0.587 +
								tempImageArray_B[i][j] * 0.114) ;
				tempImageArray_R[i][j] = result;
				tempImageArray_G[i][j] = result;
				tempImageArray_B[i][j] = result;
			}
		}
	}
	void histStreching(){
		
		/*for (int i = 0; i < imageW; i++){
			for (int j = 0; j < imageH; j++) {
				temp_1_Array[i][j] = (255 / (maxIntensity - minIntensity)) * imageArray[i][j] - minIntensity;
			}
		}*/
	}
}
