import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class ScreenReader {

	Hashtable<String,BufferedImage> characters = new Hashtable<String,BufferedImage>();
	static double LETTER_SPACING = 6;	//upper limit on space between digits
	static double COLOR_THRESHOLD = 20; //maximum difference in each RGB

	public static void main(String[] args) {
		try {
			//long t1 = System.currentTimeMillis();
			ScreenReader snr = new ScreenReader();
			snr.GetNumbers(ImageIO.read(new File("test.png")));
			//System.out.println(System.currentTimeMillis() - t1); // 4648
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	ScreenReader() throws IOException{
		for(int i = 0; i < 10; i++){
			characters.put(""+i,ImageIO.read(new File(""+i+".png")));			
		}
		characters.put(",",ImageIO.read(new File("comma.png")));
		characters.put(".",ImageIO.read(new File("period.png")));
		characters.put("d",ImageIO.read(new File("d.png")));
		characters.put("h",ImageIO.read(new File("h.png")));
		characters.put("m",ImageIO.read(new File("m.png")));
	}

	void GetNumbers(BufferedImage b) throws IOException, InterruptedException{
		ArrayList<NumLoc> matches = new ArrayList<NumLoc>();
		ArrayList<ImageMatcher> matchers = new ArrayList<ImageMatcher>();
		for(String s : characters.keySet()){
			BufferedImage b2 = characters.get(s);
			ImageMatcher m = new ImageMatcher(s,b,b2,COLOR_THRESHOLD);
			m.start();
			matchers.add(m);
		}
		for(ImageMatcher m : matchers){
			m.join();
			ArrayList<Point> locs = m.getLocations();
			for(Point p : locs){
				matches.add(new NumLoc(m.s,p,m.small.getWidth()));
			}
		}

		Collections.sort(matches);
		ArrayList<NumLoc> numbers = new ArrayList<NumLoc>();
		NumLoc c = matches.get(0);
		for(NumLoc p : matches){
			if (p == c) continue;
			if(p.y == c.y && p.x < c.x+c.w+LETTER_SPACING){
				c.concat(p);
			}
			else{
				numbers.add(c);
				c = p;
			}
		}
		numbers.add(c);
		for(NumLoc n : numbers){
			Graphics2D graphics = b.createGraphics();
			graphics.setPaint(new Color(255, 0, 0));
			graphics.drawRect(n.x, n.y, n.w, 12);
		}
		ImageIO.write(b, "png", new File("result.png"));
	}

	class NumLoc implements Comparable<NumLoc>{
		public String n;
		public int x;
		public int y;
		public int w;
		NumLoc(String n, int x, int y, int w){
			this.n = n;
			this.x = x;
			this.y = y;
			this.w = w;
		}
		NumLoc(String n, Point p, int w){
			this(n,p.x,p.y,w);
		}

		public void concat(NumLoc d){
			n += d.n;
			w = d.x - x + d.w;
		}

		public int compareTo(NumLoc n) {
			if(y > n.y) return 1;
			if(y < n.y) return -1;
			if(x > n.x) return 1;
			if(x < n.x) return -1;
			return 0;
		}

		public String toString(){
			return n+" ("+x+","+y+")";
		}
	}
}
