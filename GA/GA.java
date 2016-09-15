import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.*;

// Each MyPolygon has a color and a Polygon object
class MyPolygon {

	Polygon polygon;
	Color color;

	public MyPolygon(Polygon _p, Color _c) {
		polygon = _p;
		color = _c;
	}

	public Color getColor() {
		return color;
	}

	public Polygon getPolygon() {
		return polygon;
	}

	public void setColor(Color col) {
		color = col;
	}

	public void setPolygon(Polygon pol) {
		polygon = pol;
	}

	public MyPolygon cloneMe(){
		int[] xp = new int[polygon.npoints];
		int[] yp = new int[polygon.npoints];
		for(int i = 0; i < polygon.npoints; i++){
			xp[i] = polygon.xpoints[i];
			yp[i] = polygon.ypoints[i];
		}
		Polygon p = new Polygon(xp, yp, polygon.npoints);
		Color c = new Color(color.getRed(), color.getGreen(), color.getBlue());
		MyPolygon mp = new MyPolygon(p, c);
		return mp;
	}
}


// Each GASolution has a list of MyPolygon objects
class GASolution {

	ArrayList<MyPolygon> shapes;

	// width and height are for the full resulting image
	int width, height;

	public GASolution(int _width, int _height) {
		shapes = new ArrayList<MyPolygon>();
		width = _width;
		height = _height;
	}

	public void addPolygon(MyPolygon p) {
		shapes.add(p);
	}

	public ArrayList<MyPolygon> getShapes() {
		return shapes;
	}

	public int size() {
		return shapes.size();
	}

	// Create a BufferedImage of this solution
	// Use this to compare an evolved solution with
	// a BufferedImage of the target image
	//
	// This is almost surely NOT the fastest way to do this...
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (MyPolygon p : shapes) {
			Graphics g2 = image.getGraphics();
			g2.setColor(p.getColor());
			Polygon poly = p.getPolygon();
			if (poly.npoints > 0) {
				g2.fillPolygon(poly);
			}
		}
		return image;
	}

	public String toString() {
		return "" + shapes;
	}
}


// A Canvas to draw the highest ranked solution each epoch
class GACanvas extends JComponent{

    int width, height;
    GASolution solution;

    public GACanvas(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
    	width = WINDOW_WIDTH;
    	height = WINDOW_HEIGHT;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setImage(GASolution sol) {
  	    solution = sol;
    }

    public void paintComponent(Graphics g) {
		BufferedImage image = solution.getImage();
		g.drawImage(image, 0, 0, null);
    }
}


public class GA extends JComponent{

    GACanvas canvas;
    int width, height;
    BufferedImage realPicture;
    ArrayList<GASolution> population;
		ArrayList<Double> populationFitnesses;
		Random rand = new Random();

    // Adjust these parameters as necessary for your simulation
    double MUTATION_RATE = 0.01;
    double CROSSOVER_RATE = 0.6;
    int MAX_POLYGON_POINTS = 5;
    int MAX_POLYGONS = 10;

    public GA(GACanvas _canvas, BufferedImage _realPicture) {
        canvas = _canvas;
        realPicture = _realPicture;
        width = realPicture.getWidth();
        height = realPicture.getHeight();
        population = new ArrayList<GASolution>();
				populationFitnesses = new ArrayList<Double>();

        // You'll need to define the following functions
        createPopulation(100);	// Make 50 new, random chromosomes
				generatePopulationFitness();
    }

    // YOUR CODE GOES HERE!

		//Partially taken from https://rogeralsing.com/2008/12/09/genetic-programming-mona-lisa-faq/
		public double fitness(BufferedImage image){
			double fitness = 0;
			for(int x = 0; x < 1000; x++){
				int rx = rand.nextInt(realPicture.getWidth());
				int ry = rand.nextInt(realPicture.getHeight());
				Color cSol = new Color(realPicture.getRGB(rx, ry));
				Color cGen = new Color(image.getRGB(rx, ry));

				double deltaRed = cSol.getRed() - cGen.getRed();
				double deltaBlue = cSol.getBlue() - cGen.getBlue();
				double deltaGreen = cSol.getGreen() - cGen.getGreen();
				fitness += Math.sqrt(deltaRed*deltaRed + deltaBlue*deltaBlue + deltaGreen*deltaGreen);
			}
			// System.out.println(fitness);
			// double fit = (255*255*3*100) / fitness;
			double fit = fitness/1000;
			// System.out.println(1/Math.log(fit));
			// System.out.println(fitness + " " + 1/Math.log(fit));
			return (1/fit);
		}

		public GASolution generateRandomSolution(){
			GASolution sol = new GASolution(width, height);
			for(int i = 0; i < MAX_POLYGONS; i++){
				int[] xCoord = new int[MAX_POLYGON_POINTS];
				int[] yCoord = new int[MAX_POLYGON_POINTS];
				for(int j = 0; j < MAX_POLYGON_POINTS; j++){
					xCoord[j] = rand.nextInt(width);
					yCoord[j] = rand.nextInt(height);
				}
				Polygon poly = new Polygon(xCoord, yCoord, MAX_POLYGON_POINTS);
				Color col = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
				MyPolygon myp = new MyPolygon(poly, col);
				sol.addPolygon(myp);
			}
			return sol;
		}

		public void createPopulation(int n){
			for(int i = 0; i < n; i++){
				population.add(generateRandomSolution());
			}
			generatePopulationFitness();
		}

		public void generatePopulationFitness(){
			populationFitnesses.clear();
			for(GASolution sol : population){
				populationFitnesses.add(fitness(sol.getImage()));
			}
		}

		public GASolution pickFitParent(){
			double totalFitness = 0;
			for(double d : populationFitnesses){
				totalFitness += d;
			}
			double range = rand.nextDouble()*totalFitness;
			int ind = -1;
			while(range >= 0){
				ind++;
				range -= populationFitnesses.get(ind);
			}
			// getBestSolution();
			// System.out.println("parent " + populationFitnesses.get(ind));
			return population.get(ind);
		}

		public GASolution crossover(GASolution sa, GASolution sb){
			GASolution cross = new GASolution(width, height);
			for(int i = 0; i < MAX_POLYGONS/2; i++){
				cross.addPolygon(sa.getShapes().get(i).cloneMe());
			}
			for(int i = MAX_POLYGONS/2; i < MAX_POLYGONS; i++){
				cross.addPolygon(sb.getShapes().get(i).cloneMe());
			}
			return cross;
		}

		public GASolution mutate(GASolution child){
			GASolution newChild = new GASolution(width, height);
			for(MyPolygon pol : child.getShapes()){
				int[] xCoord = pol.getPolygon().xpoints;
				int[] yCoord = pol.getPolygon().ypoints;
				if(rand.nextDouble() < MUTATION_RATE){
					int coord = rand.nextInt(MAX_POLYGON_POINTS);
					pol.polygon.xpoints[coord] += (int)(rand.nextGaussian()*width/2);
					pol.polygon.ypoints[coord] += (int)(rand.nextGaussian()*height/2);
				}

				if(rand.nextDouble() < MUTATION_RATE){
					Double rr = (rand.nextGaussian()*64 + pol.getColor().getRed());
					int red = Math.max(Math.min(rr.intValue(),255),0);
					// System.out.println(red);
					Color newR = new Color(red, pol.getColor().getGreen(), pol.getColor().getBlue());
					pol.setColor(newR);
				}

				if(rand.nextDouble() < MUTATION_RATE){
					Double gg = (rand.nextGaussian()*64 + pol.getColor().getGreen());
					int green = Math.max(Math.min(gg.intValue(),255),0);
					// System.out.println(green);
					Color newG = new Color(pol.getColor().getRed(), green, pol.getColor().getBlue());
					pol.setColor(newG);
				}

				if(rand.nextDouble() < MUTATION_RATE){
					Double bb = (rand.nextGaussian()*64 + pol.getColor().getBlue());
					int blue = Math.max(Math.min(bb.intValue(),255),0);
					// System.out.println(blue);
					Color newB = new Color(pol.getColor().getRed(), pol.getColor().getGreen(), blue);
					pol.setColor(newB);
				}
				// if(rand.nextDouble() < MUTATION_RATE){
				// 	Color newc = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
				// 	pol.setColor(newc);
				// }
			}
			return child;
		}

		public GASolution getBestSolution(){
			int maxIndex = 0;
			double maxFit = 0;
			for(int i = 0; i < populationFitnesses.size(); i++){
				if(populationFitnesses.get(i) > maxFit){
					maxFit = populationFitnesses.get(i);
					maxIndex = i;
				}
			}
			System.out.println(maxFit);
			return population.get(maxIndex);
		}

		public void generateNewPopulation(){
			ArrayList<GASolution> newPop = new ArrayList();
			while(newPop.size() < population.size()*CROSSOVER_RATE){
				GASolution p1 = pickFitParent();
				GASolution p2 = pickFitParent();
				GASolution child = crossover(p1, p2);
				child = mutate(child);
				newPop.add(child);
			}
			while(newPop.size() < population.size()){
				newPop.add(pickFitParent());
			}
			population = newPop;
		}

		public void evolve(int epochs){
			for(int i = 0; i < epochs; i++){
				generateNewPopulation();
				generatePopulationFitness();
				if((i % 100) == 0){
					canvas.setImage(getBestSolution());
					// canvas.paintComponent(canvas.getGraphics());
					canvas.repaint();
				}
			}
		}

    public void runSimulation() {
			evolve(100000000);
    }

    public static void main(String[] args) throws IOException {

        String realPictureFilename = "test.jpg";

        BufferedImage realPicture = ImageIO.read(new File(realPictureFilename));

        JFrame frame = new JFrame();
        frame.setSize(realPicture.getWidth(), realPicture.getHeight());
        frame.setTitle("GA Simulation of Art");

        GACanvas theCanvas = new GACanvas(realPicture.getWidth(), realPicture.getHeight());
        frame.add(theCanvas);
        frame.setVisible(true);

        GA pt = new GA(theCanvas, realPicture);
            pt.runSimulation();
    }
}
