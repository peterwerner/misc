package game;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import game.HitBox;


public class Main extends Applet implements Runnable, KeyListener {
		
	private static int SCREENWIDTH = 512;
	private static int SCREENHEIGHT = 512;
		
	private double fpsTimeOld = 0.0; private double fpsTimeOld_Ave = 0.0;
	private double fpsTimeNew = 0.0; private double fpsTimeNew_Ave = 0.0;
	private double fps = 0.0;		 private double fps_Ave = 0.0;
	private int frameCount = 0;
	
	private Image image;
	private URL base;
	private Graphics second;
	
	private static HitBox[] boxes = new HitBox[10];	
	private LightPoint[] lightPoints = new LightPoint[1];

	
	@Override
	public void init() {
		
		setSize(SCREENWIDTH, SCREENHEIGHT);
		setBackground(Color.black);
		setFocusable(true);
		
		Frame frame = (Frame) this.getParent().getParent();
	    frame.setTitle("Light Casting Test");
	    
	    addKeyListener(this);
	    
	    try {
			base = getDocumentBase();
		} catch (Exception e) {
		}
	    
	    // initialize objects and call relevant init methods
	    
	    for(int i = 0; i < boxes.length; i++) {
	    	boxes[i] = new HitBox();
	    }
	    for(int i = 0; i < lightPoints.length; i++) {
	    	lightPoints[i] = new LightPoint();
	    	lightPoints[i].init();
	    }
	    
		/////////////////////////////////////
	    // RANDOMIZE LOCATION OF BOXES FOR TESTING PURPOSES 
	    for(int i = 0; i < boxes.length; i++) {
			int arrayX[] = boxes[i].getxPoints();
			int arrayY[] = boxes[i].getyPoints();
			int randomX  = (int)(20 + Math.random() * ((420) + 1));
			int randomY  = (int)(20 + Math.random() * ((420) + 1));
			for(int j = 0; j < 4; j++){
				arrayX[j] += randomX;
				arrayY[j] += randomY;
			}
			boxes[i].setxPoints(arrayX);
			boxes[i].setyPoints(arrayY);
	    }   
		/////////////////////////////////////
	    
	}
	
	
	// initial start-up

	@Override
	public void start() {

		Thread thread = new Thread(this);
		thread.start();
		
	}


	@Override
	public void stop() {
	}

	
	@Override
	public void destroy() {
	}

	
	// game loop
	
	@Override
	public void run() {
		
		while(true){
						
			repaint();
			
			try{
				Thread.sleep(17);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	@Override
	public void keyPressed(KeyEvent e) {
		
		switch(e.getKeyCode()){
			case KeyEvent.VK_LEFT:		lightPoints[0].setxCoor(lightPoints[0].getxCoor() - 4);
										break;
			case KeyEvent.VK_RIGHT:		lightPoints[0].setxCoor(lightPoints[0].getxCoor() + 4);
										break;
			case KeyEvent.VK_UP:		lightPoints[0].setyCoor(lightPoints[0].getyCoor() - 4);
										break;
			case KeyEvent.VK_DOWN:		lightPoints[0].setyCoor(lightPoints[0].getyCoor() + 4);
										break;
			case KeyEvent.VK_EQUALS:	lightPoints[0].setRange(lightPoints[0].getRange() + 4);
										break;
			case KeyEvent.VK_MINUS:		lightPoints[0].setRange(lightPoints[0].getRange() - 4);
										break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		switch(e.getKeyCode()){
			case KeyEvent.VK_LEFT:			break;
			case KeyEvent.VK_RIGHT:			break;
			case KeyEvent.VK_UP:			break;
			case KeyEvent.VK_DOWN:			break;
			case KeyEvent.VK_EQUALS:		break;
			case KeyEvent.VK_MINUS:			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	
	@Override
	public void update(Graphics g) {
		
		// update objects
		
		for(int i = 0; i < boxes.length; i++) {
			boxes[i].update();
		}
		for(int i = 0; i < lightPoints.length; i++) {
			lightPoints[i].update();
		}
		
		// update graphics
		
		if (image == null) {
			image = createImage(this.getWidth(), this.getHeight());
			second = image.getGraphics();
		}
				
		second.setColor(getBackground());
		second.fillRect(0, 0, getWidth(), getHeight());
		second.setColor(getForeground());
		paint(second);

		g.drawImage(image, 0, 0, this);
		
		// calculate and print fps to console
		/*
		fpsTimeNew = System.currentTimeMillis();
		fps = 1000.0/(fpsTimeNew-fpsTimeOld);
		fpsTimeOld = fpsTimeNew;
		System.out.println(fps);
		*/
		
		// calculate average fps for past 20 frames and print to console
		
		if(frameCount >= 20){
			fpsTimeNew_Ave = System.currentTimeMillis();
			fps_Ave = fps = 20000.0/(fpsTimeNew_Ave-fpsTimeOld_Ave);
			fpsTimeOld_Ave = fpsTimeNew_Ave;
			frameCount = 0;
			System.out.println(fps_Ave);
		}
		frameCount++;
		
	}
	
	
	// paint objects to screen
	
	@Override
	public void paint(Graphics g) {
		
		// enable anti-aliasing
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// paint illumination
		for(int i = 0; i < lightPoints.length; i++) {
			//g.setColor(Color.RED);   g.drawPolygon(lightPoints[i].getLitPoly());
			g.setColor(lightPoints[i].getColor());
			//g.drawPolygon(lightPoints[i].getLitPoly());
			g.fillPolygon(lightPoints[i].getLitPoly());
		}
		
		//paint boxes
		g.setColor(Color.darkGray);
		for(int i = 0; i < boxes.length; i++) {
			g.drawPolygon(boxes[i].getPoly());
			g.fillPolygon(boxes[i].getPoly());
		}
		
		// paint light points
		g.setColor(Color.WHITE);
		for(int i = 0; i < lightPoints.length; i++) {
			int[] xP = {lightPoints[i].getxCoor() -2, lightPoints[i].getxCoor() +2, lightPoints[i].getxCoor() +2, lightPoints[i].getxCoor() -2};
			int[] yP = {lightPoints[i].getyCoor() -2, lightPoints[i].getyCoor() -2, lightPoints[i].getyCoor() +2, lightPoints[i].getyCoor() +2};	
			Polygon poly = new Polygon(xP, yP, 4);
			//g.drawPolygon(poly);
			g.fillPolygon(poly);
		}
	}
	

	
	
	
	// Getters and setters
	
	public static HitBox[] getBoxes() {
		return boxes;
	}
	public static int getSCREENWIDTH() {
		return SCREENWIDTH;
	}
	public static int getSCREENHEIGHT() {
		return SCREENHEIGHT;
	}


	public double getFps_Ave() {
		return fps_Ave;
	}
}

