package azim;

public class Cube {
	// structure for a cube in color space
	int[] lower; // one corner's index in histogram
	int[] upper; // another corner's index in histogram
	int count; // cube's histogram count
	int level; // cube's level
	int[] min;
	int[] max;
	int[][] colors;
	int[] averageColor; // R: averageColor[0]
						// G: averageColor[1]
						// B: averageColor[2]
	int lowerIndex = 0;
	int upperIndex = 0;

	Cube() {
		count = 0;
		lower = new int[3];
		upper = new int[3];
		min = new int[3];
		max = new int[3];
		averageColor = new int[3];
	}

	public void initialize(int[][] a) {

		int lowerDist = 442;
		int upperDist = 0;
		int d = 0;
		int r_sum = 0;
		int g_sum = 0;
		int b_sum = 0;
		count = a.length;
		colors = new int[a.length][3]; // colors[][3]
		// Find min and max of each color
		for (int i = 0; i < a.length; i++) {
			
//			if(a[i][0]>255 || a[i][0]<0)	System.out.println("Class:Cube-->R ="+ a[i][0]);
//			if(a[i][1]>255 || a[i][1]<0)	System.out.println("Class:Cube-->G ="+ a[i][1]);
//			if(a[i][2]>255 || a[i][2]<0)	System.out.println("Class:Cube-->B ="+ a[i][2]);
			
			colors[i][0] = a[i][0]; // store given colors in the field of the cube
			colors[i][1] = a[i][1];
			colors[i][2] = a[i][2];
			r_sum += colors[i][0];
			g_sum += colors[i][1];
			b_sum += colors[i][2];
		}
		
		for (int i = 0; i < colors.length; i++) {

			// Distance of each color from the lower point of the cube
			d = (colors[i][0] - lower[0]) * (colors[i][0] - lower[0])
					+ (colors[i][1] - lower[1]) * (colors[i][1] - lower[1])
					+ (colors[i][2] - lower[2]) * (colors[i][2] - lower[2]);

			if (d < lowerDist) {
				lowerDist = d;
				lowerIndex = i;
			}
			if (d > upperDist) {
				upperDist = d;
				upperIndex = i;
			}
		}
		min[0] = colors[lowerIndex][0];
		min[1] = colors[lowerIndex][1];
		min[2] = colors[lowerIndex][2];

		max[0] = colors[upperIndex][0];
		max[1] = colors[upperIndex][1];
		max[2] = colors[upperIndex][2];
		
		System.out.println("MAX[0]="+max[0]+" MIN[0]="+min[0]);
		System.out.println("MAX[1]="+max[1]+" MIN[1]="+min[1]);
		System.out.println("MAX[2]="+max[2]+" MIN[2]="+min[2]);
		
		averageColor[0] = min[0] + Math.abs(max[0] - min[0]) / 2;
		averageColor[1] = min[1] + Math.abs(max[1] - min[1]) / 2;
		averageColor[2] = min[2] + Math.abs(max[2] - min[2]) / 2;
		
		System.out.println("RED  ::: averageColor: " + averageColor[0]);
		System.out.println("GREEN  ::: averageColor: " + averageColor[1]);
		System.out.println("BLUE  ::: averageColor: " + averageColor[2]);
		
	}
	
	

}
