package game;

import java.awt.Color;
import java.awt.Polygon;
import java.util.ArrayList;

// Point light using ray casts only to box coordinates to determine lit region

public class LightPoint2{

	private Color color = Color.gray;
	private int range = 256;
	private int xCoor0 = 256;
	private int yCoor0 = 256;
	private int xCoor1;
	private int yCoor1;
	private int dist1_0;
	private int anglePrecision = 60; // only used to form the trig tables
	private int scalarPrecision = 3;
	private int testMoveDist = 4;
	private ArrayList<Integer> listPOIx = new ArrayList<Integer>(0);
	private ArrayList<Integer> listPOIy = new ArrayList<Integer>(0);
	private ArrayList<Integer> listBOXx = new ArrayList<Integer>(0);
	private ArrayList<Integer> listBOXy = new ArrayList<Integer>(0);
	private ArrayList<Float> listBOXang = new ArrayList<Float>(0);
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
		
		listBoxVerts();
		drawLitPoly();
	}
	
	
	// cast rays and draw a polygon for lit area
	
	public void drawLitPoly() {
		
		// gather points of intersection
		
		float angle = 0;
		int angleIndex = 0;
		int tmpX = 0, tmpY = 0;
		boolean success = false;  // successful non-grazing hit on a box
		boolean solidHit = false; // same as 'success' but includes "exit-wound" hits
				
		for(int i = 0; i < listBOXx.size(); i++) {
			
			xCoor1 = listBOXx.get(i);
			yCoor1 = listBOXy.get(i);
			angle = listBOXang.get(i);
			angleIndex = (int) ((angle / (2*Math.PI)) * anglePrecision);
			if(angleIndex > cosTable.length)
				angleIndex = cosTable.length - 1;
						
			for(int k = 0; k < Main.getBoxes().length; k++) {
				
				for(int j = -1; j < 2; j+=2) {
					tmpX = (int) (xCoor1 + (j * testMoveDist * cosTable[angleIndex]));
					tmpY = (int) (yCoor1 + (j * testMoveDist * sinTable[angleIndex]));
					if(Main.getBoxes()[k].getPoly().contains(tmpX, tmpY) == true) {
						
						solidHit = true;
						
						if((xCoor1 + tmpX)*(xCoor1 + tmpX) + (yCoor1 + tmpY)*(yCoor1 + tmpY) > (xCoor1)*(xCoor1) + (yCoor1)*(yCoor1)
							&&  xCoor1 > xCoor0 - range && xCoor1 < xCoor0 + range && yCoor1 > yCoor0 - range && yCoor1 < yCoor0 + range) {
							success = true;
						}
					}
				}
				if(success == true) {
					listPOIx.add(xCoor1);
					listPOIy.add(yCoor1);
				}
				else if(solidHit == false){  // if the ray just grazes the box, cast a ray to get POI
					 
					int scalar = 0;
					while(true) {
						
						xCoor1 = (int) (xCoor0 + (scalar * cosTable[angleIndex]));
						yCoor1 = (int) (yCoor0 + (scalar * sinTable[angleIndex]));
						// dist1_0 = (int) Math.sqrt((xCoor0 - xCoor1)*(xCoor0 - xCoor1) + (yCoor0 - yCoor1)*(yCoor0 - yCoor1));  // distance from light point to point of intersection
						
						scalar += scalarPrecision;
						
						// end if the ray is out of bounds
						
						if(xCoor1 <= -20 || yCoor1 <= -20 || xCoor1 >= Main.getSCREENWIDTH() + 19 || yCoor1 >= Main.getSCREENHEIGHT() + 19)
							break;
						
						if(xCoor1 <= xCoor0 - range)	xCoor1 = xCoor0 - range;
						if(xCoor1 >= xCoor0 + range)	xCoor1 = xCoor0 + range;
						if(yCoor1 <= yCoor0 - range)	yCoor1 = yCoor0 - range;
						if(yCoor1 >= yCoor0 + range)	yCoor1 = yCoor0 + range;
						if(xCoor1 <= xCoor0 - range || xCoor1 >= xCoor0 + range || yCoor1 <= yCoor0 - range || yCoor1 >= yCoor0 + range)
							break;
						
						// end if ray makes a solid hit with a box
						
						if(checkIntersect(xCoor1, yCoor1) == true  &&  
						   checkIntersect(xCoor1 + scalarPrecision * sinTable[angleIndex], yCoor1 + scalarPrecision * sinTable[angleIndex]) == true) {					
							break;
						}
					}
					
					listPOIx.add(xCoor1);
					listPOIy.add(yCoor1);
				}
				success = false;
			}
			
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
	
	
	// populate x and y coordinate lists of vertices from all hitboxes (sorted by angle)
	
	public void listBoxVerts() {
		
		// clear old lists
		listBOXx = new ArrayList<Integer>(0);
		listBOXy = new ArrayList<Integer>(0);
		
		for(int i = 0; i < Main.getBoxes().length; i++) {
			
			for(int j = 0; j < Main.getBoxes()[i].getxPoints().length; j++) {
				
				listBOXx.add(Main.getBoxes()[i].getxPoints()[j]);
				listBOXy.add(Main.getBoxes()[i].getyPoints()[j]);
				listBOXang.add((float) Math.atan2(listBOXx.get(i) - yCoor0, listBOXy.get(i) - xCoor0));
			}
		}
		
		// also add the corner points of this light's square range
		listBOXx.add(xCoor0 + range);  listBOXy.add(yCoor0 + range);	
		listBOXang.add((float) Math.atan2(yCoor0+range,xCoor0+range));
		listBOXx.add(xCoor0 - range);  listBOXy.add(yCoor0 + range);	
		listBOXang.add((float) Math.atan2(yCoor0+range,xCoor0-range));
		listBOXx.add(xCoor0 + range);  listBOXy.add(yCoor0 - range);	
		listBOXang.add((float) Math.atan2(yCoor0-range,xCoor0+range));
		listBOXx.add(xCoor0 - range);  listBOXy.add(yCoor0 - range);	
		listBOXang.add((float) Math.atan2(yCoor0-range,xCoor0-range));
		
		for(int i = 0; i < listBOXang.size(); i++) {
			if(listBOXang.get(i) < 0)
				listBOXang.set(i, (float) (2*Math.PI + listBOXang.get(i)));
		}
		
		// sort list by angle
		
		int tmpX, tmpY; float tmpAng;
		
		for(int i = 1; i < listBOXx.size(); i++) {
			if(listBOXang.get(i) < listBOXang.get(i-1)) {
				
				tmpAng = listBOXang.get(i);	listBOXang.set(i, listBOXang.get(i-1)); listBOXang.set(i-1, tmpAng);
				tmpX = listBOXx.get(i);		listBOXx.set(i, listBOXx.get(i-1));		listBOXx.set(i-1, tmpX);
				tmpY = listBOXy.get(i);		listBOXy.set(i, listBOXy.get(i-1));		listBOXy.set(i-1, tmpY);
				
				i = 0;
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
