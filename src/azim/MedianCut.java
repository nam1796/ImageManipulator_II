package azim;



public class MedianCut {

	private int[][] tempHist;	
	final int HSIZE = 256;		// size of image histogram
	public Cube[] list;

	
	public MedianCut(int[][] pixels, int width, int height) {
		
		tempHist = new int[width*height][3];
		for(int i = 0; i < width*height; i++){
			tempHist[i][0] = pixels[i][0];
			tempHist[i][1] = pixels[i][1];
			tempHist[i][2] = pixels[i][2];
		}
	}
	
	
	
	public void createCuboids(int maxCubes){
		
		int lr, lg, lb;
		int level, ncubes, splitpos;
		int longdim=0;						//longest dimension of cube
		Cube cube, cubeA, cubeB;			//'cubeA' and 'cubeB' are subdivisions of the cube 'cube'
		list = new Cube[maxCubes];
		
		ncubes = 0;
		cube = new Cube();
		cube.lower[0] = 0;
		cube.lower[1] = 0;
		cube.lower[2] = 0;
		cube.upper[0] = 255;
		cube.upper[1] = 255;
		cube.upper[2] = 255;
		cube.level = 0;
		cube.initialize(tempHist);	//min, max, average will be set (based on the lower and upper colors)
		list[ncubes++] = cube;
		while(ncubes < maxCubes){
			level = maxCubes;
			splitpos = -1;	//position of the cube in the list to be divided.
			for (int k=0; k<=ncubes-1; k++) {
				
				if (list[k].level < level) {
					level = list[k].level;
					splitpos = k;
				}
			}
			
			if(splitpos == -1)
				break;
			//Find the cube with the lowest level
			cube = list[splitpos];
			// Find longest dimension of this cube
			lr = cube.upper[0] - cube.lower[0];		//red length
			lg = cube.upper[1] - cube.lower[1];		//green length
			lb = cube.upper[2] - cube.lower[2];		//blue length
			
			if (lr >= lg && lr >= lb) longdim = 0;	//R
			if (lg >= lr && lg >= lb) longdim = 1;	//G
			if (lb >= lr && lb >= lg) longdim = 2;	//B
			//Sort the array of colors in this cube
			//quickSort(cube.colors, cube.lower[longdim], cube.upper[longdim], longdim);
			quickSort(cube.colors, 0, cube.colors.length-1, longdim);
			
			
			
			//Find median
			int median = cube.count / 2; //cube.colors[c][]
			System.out.println("MEDIAN: "+median +"\t LEVEL: "+ level);
			System.out.println("cube.colors[median-1][0]:"+ cube.colors[median-1][0]);
			//System.out.println("cube.colors.length: "+cube.colors.length +"\t tempHist: "+tempHist.length);
			// Now split "cube" at the median and add the two new
			// cubes to the list of cubes.
			
			/*				CUBE_A			*/	//MEDIAN:75696		LEVEL:0 	cube.colors.length:50464		tempHist: 50464
			cubeA = new Cube();
			int[][] AColors = new int[median][3];
			cubeA.upper[0] = cube.colors[median-1][0];
			cubeA.upper[1] = cube.colors[median-1][1];
			cubeA.upper[2] = cube.colors[median-1][2];
			
			cubeA.lower[0] = cube.lower[0];
			cubeA.lower[1] = cube.lower[1];
			cubeA.lower[2] = cube.lower[2];
			
			for(int t = 0 ; t < median; t++){
				AColors[t][0]= cube.colors[t][0];
				AColors[t][1]= cube.colors[t][1];
				AColors[t][2]= cube.colors[t][2];
				
			}
			cubeA.level = cube.level + 1;
			cubeA.initialize(AColors);
			
			list[splitpos] = cubeA;				// Replace this cube with its parent
			
			System.out.println("-------------LEVEL"+cubeA.level+"-------");
			System.out.println("cubeA.upper[0]: "+cubeA.upper[0]);
			System.out.println("cubeA.upper[1]: "+cubeA.upper[1]);
			System.out.println("cubeA.upper[2]: "+cubeA.upper[2]);
			System.out.println("cubeA.lower[0]: "+cubeA.lower[0]);
			System.out.println("cubeA.lower[1]: "+cubeA.lower[1]);
			System.out.println("cubeA.lower[2]: "+cubeA.lower[2]);
			System.out.println("-------------------------------------");
			
			/*				CUBE_B			*/
			cubeB = new Cube();
			int BColors[][] = new int[cube.count-median][3];
			cubeB.upper[0] = cube.upper[0];
			cubeB.upper[1] = cube.upper[1];
			cubeB.upper[2] = cube.upper[2];
			System.out.println("cube.colors[median][0]"+cube.colors[median][0]);
			cubeB.lower[0] = cube.colors[median][0];
			cubeB.lower[1] = cube.colors[median][1];
			cubeB.lower[2] = cube.colors[median][2];
			
			for(int t = median ; t < cube.count; t++){
				BColors[t-median][0]= cube.colors[t][0];
				BColors[t-median][1]= cube.colors[t][1];
				BColors[t-median][2]= cube.colors[t][2];
			}
			cubeB.level = cube.level + 1;
			cubeB.initialize(BColors);
			list[ncubes++] = cubeB;
			
			System.out.println("-------------LEVEL"+cubeB.level+"-------");
			System.out.println("cubeB.upper[0]: "+cubeB.upper[0]);
			System.out.println("cubeB.upper[1]: "+cubeB.upper[1]);
			System.out.println("cubeB.upper[2]: "+cubeB.upper[2]);
			System.out.println("cubeB.lower[0]: "+cubeB.lower[0]);
			System.out.println("cubeB.lower[1]: "+cubeB.lower[1]);
			System.out.println("cubeB.lower[2]: "+cubeB.lower[2]);
			System.out.println("-------------------------------------");
		}
		
	}
	
	
	void quickSort(int[][] arr, int lo, int hi, int longdim) {
		// Based on the QuickSort method by James Gosling from Sun's SortDemo applet
		//http://www.cfar.umd.edu/~goldman/sort/1.1/QSortAlgorithm.java
		if (arr == null || arr.length == 0)
			return;
 
		if (lo >= hi)
			return;
 
		//pick the pivot
		int middle = lo + (hi - lo) / 2;
		int pivot = arr[middle][longdim];
 
		//make left < pivot and right > pivot
		int i = lo, j = hi;
		while (i <= j) {
			while (arr[i][longdim] < pivot) {
				i++;
			}
 
			while (arr[j][longdim] > pivot) {
				j--;
			}
 
			if (i <= j) {
				int temp_1 = arr[i][(longdim + 1) % 3];
				int temp_2 = arr[i][(longdim + 2) % 3];
				int temp_3 = arr[i][(longdim + 3) % 3];
				arr[i][(longdim + 1) % 3] = arr[j][(longdim + 1) % 3];
				arr[i][(longdim + 2) % 3] = arr[j][(longdim + 2) % 3];
				arr[i][(longdim + 3) % 3] = arr[j][(longdim + 3) % 3];
				
				arr[j][(longdim + 1) % 3] = temp_1;
				arr[j][(longdim + 2) % 3] = temp_2;
				arr[j][(longdim + 3) % 3] = temp_3;
				i++;
				j--;
			}
		}
//		for(int t = 0; t < arr.length;t++){
//			if(arr[t][0]>255 || arr[t][0] < 0)
//				System.out.println("SortERROR: arr[i][0]="+arr[t][0]);
//			if(arr[t][1]>255 || arr[t][1] < 0)
//				System.out.println("SortERROR: arr[i][1]="+arr[t][1]);
//			if(arr[t][2]>255 || arr[t][2] < 0)
//				System.out.println("SortERROR: arr[i][2]="+arr[t][2]);
//		}
		//recursively sort two sub parts
		if (lo < j)
			quickSort(arr, lo, j,longdim);
 
		if (hi > i)
			quickSort(arr, i, hi,longdim);
	}
	

}
