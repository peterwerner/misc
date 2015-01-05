package game;

import java.awt.Polygon;


public class HitBox {	

	private int nPoints = 4;
	private int[] xPoints = {10,50,50,10};
	private int[] yPoints = {10,10,50,50};	
	private Polygon poly = new Polygon(xPoints, yPoints, nPoints);
	
	public void update() {
		poly.npoints = nPoints;
		poly.xpoints = xPoints;
		poly.ypoints = yPoints;
	}
	
	
	// Getters and setters
	
	public void setxPoints(int[] xPoints) {
		this.xPoints = xPoints;
	}
	public void setyPoints(int[] yPoints) {
		this.yPoints = yPoints;
	}
	public void setPoly(Polygon poly) {
		this.poly = poly;
	}
	public void setnPoints(int nPoints) {
		this.nPoints = nPoints;
	}
	public int getnPoints() {
		return nPoints;
	}
	public int[] getxPoints() {
		return xPoints;
	}
	public int[] getyPoints() {
		return yPoints;
	}
	public Polygon getPoly() {
		return poly;
	}
}
