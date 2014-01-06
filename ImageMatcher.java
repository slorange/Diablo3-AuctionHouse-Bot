import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImageMatcher extends Thread{//TODO implement Boyer-Moore for images: running time O(n/m)
	BufferedImage big;
	BufferedImage small;
	double threshold;
	public String s;
	private ArrayList<Point> locs = new ArrayList<Point>();
	
	ImageMatcher(String s, BufferedImage big, BufferedImage small, double threshold){
		this.s = s;
		this.big = big;
		this.small = small;
		this.threshold = threshold;
	}
	
	public void run() {
		//System.out.println(s + " starting");
		for (int y = 0; y < big.getHeight()-small.getHeight(); y++) {
			for (int x = 0; x < big.getWidth()-small.getWidth(); x++) {
				if (isImageDuplicate(x, y)){
					locs.add(new Point(x,y));
					//System.out.println("Found " + s);
				}
			}
		}
		//System.out.println(s + " done");
    }
	
	public ArrayList<Point> getLocations(){
		return locs;
	}

	private boolean isImageDuplicate(int bx, int by) {
		for (int y = small.getHeight()-1; y >= 0 ; y--) { //backwards is faster for commas and m
			for (int x = 0; x < small.getWidth(); x++) {
				if(!sameColor(big.getRGB(bx+x, by+y), small.getRGB(x, y))){
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean sameColor(int c1, int c2){
		int  red = (c1 & 0x00ff0000) >> 16;
		int  green = (c1 & 0x0000ff00) >> 8;
		int  blue = c1 & 0x000000ff;
		int  red2 = (c2 & 0x00ff0000) >> 16;
		int  green2 = (c2 & 0x0000ff00) >> 8;
		int  blue2 = c2 & 0x000000ff;
		return Math.abs(red - red2) < threshold && Math.abs(green - green2) < threshold && Math.abs(blue - blue2) < threshold;
	}
}
