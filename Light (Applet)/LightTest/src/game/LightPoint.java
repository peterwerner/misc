package game;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

// Point light using 360 degree ray casts to determine lit region

public class LightPoint{

	private Color color = Color.gray;
	private int range = 150;
	private int xCoor0 = 256;
	private int yCoor0 = 256;
	private int xCoor1;
	private int yCoor1;
	private double dist1_0;
	private int anglePrecision = 200;
	private int scalarPrecision = 4;
	private ArrayList<Integer> listPOIx = new ArrayList<Integer>(0);
	private ArrayList<Integer> listPOIy = new ArrayList<Integer>(0);
	private ArrayList<Integer> listBOXx = new ArrayList<Integer>(0);
	private ArrayList<Integer> listBOXy = new ArrayList<Integer>(0);
	private float[] sinTable = new float[anglePrecision];
	private float[] cosTable = new float[anglePrecision];
	private Polygon litPoly = new Polygon();
	
	
	public void init() {
		
		// create a table of sin and cos values so we don't have to calculate these every time
		
		for(int i = 0; i < anglePrecision; i++) {
			sinTable[i] = (float) Math.sin(i * 2 * Math.PI / anglePrecision);
			cosTable[i] = (float) Math.cos(i * 2 * Math.PI / anglePrecision);
		}
		
	}
	
	
	public void update() {
		
		//listBoxVerts();
		drawLitPoly();
	}
	
	
	// cast rays and draw a polygon for lit area
	
	public void drawLitPoly() {
		
		// cast rays and gather points of intersection
		
		for(int angleIndex = 0; angleIndex < anglePrecision; angleIndex++){
			
			int scalar = 0;
			while(true) {
				
				xCoor1 = (int) (xCoor0 + (scalar * cosTable[angleIndex]));
				yCoor1 = (int) (yCoor0 + (scalar * sinTable[angleIndex]));
				dist1_0 = Math.sqrt((xCoor0 - xCoor1)*(xCoor0 - xCoor1) + (yCoor0 - yCoor1)*(yCoor0 - yCoor1));  // distance from light point to point of intersection
				
				scalar += scalarPrecision;
				
				if(dist1_0 > range || xCoor1 <= -20 || yCoor1 <= -20 || xCoor1 >= Main.getSCREENWIDTH() + 19 || yCoor1 >= Main.getSCREENHEIGHT() + 19)
					break;
				
				if(checkIntersect(xCoor1, yCoor1) == true) {					
					break;
				}
			}
			
			listPOIx.add(xCoor1);
			listPOIy.add(yCoor1);
		}
		
		// construct a polygon from the points of intersection
		
		int xPoints[] = new int[listPOIx.size()];
		int yPoints[] = new int[listPOIx.size()];
		
		for(int i = 0; i < listPOIx.size(); i++) {
			double tempX = listPOIx.get(i);
			double tempY = listPOIy.get(i);
			xPoints[i] = (int)tempX;
			yPoints[i] = (int)tempY;
		}
		
		litPoly.npoints = listPOIx.size();
		litPoly.xpoints = xPoints;
		litPoly.ypoints = yPoints;
		
		// reset list of intersection points
		
		listPOIx = new ArrayList<Integer>(0);
		listPOIy = new ArrayList<Integer>(0);
	}
	
	
	// check if a particular point is intersecting any hitboxes
	
	public boolean checkIntersect(double xCoor, double yCoor) {
		
		for(int i = 0; i < Main.getBoxes().length; i++)
			if(Main.getBoxes()[i].getPoly().contains(xCoor, yCoor))
				return true;
		
		return false;
	}
	
	
	// populate x and y coordinate lists of vertices from all hitboxes
	
	public void listBoxVerts() {
		
		for(int i = 0; i < Main.getBoxes().length; i++) {
			
			for(int j = 0; j < Main.getBoxes()[i].getxPoints().length; j++) {
				
				listBOXx.add(Main.getBoxes()[i].getxPoints()[j]);
				listBOXy.add(Main.getBoxes()[i].getyPoints()[j]);
			}
		}
	}
	
	
	
	
	// Getters and setters
	
	public int getxCoor() {
		return xCoor0;
	}
	public void setxCoor(int xCoor) {
		this.xCoor0 = xCoor;
	}
	public int getyCoor() {
		return yCoor0;
	}
	public void setyCoor(int yCoor) {
		this.yCoor0 = yCoor;
	}
	public Polygon getLitPoly() {
		return litPoly;
	}
	public void setLitPoly(Polygon litPoly) {
		this.litPoly = litPoly;
	}


	public void setAnglePrecision(int anglePrecision) {
		this.anglePrecision = anglePrecision;
	}


	public int getAnglePrecision() {
		return anglePrecision;
	}


	public int getRange() {
		return range;
	}


	public void setRange(int range) {
		this.range = range;
	}


	public Color getColor() {
		return color;
	}


	public void setColor(Color color) {
		this.color = color;
	}
}
