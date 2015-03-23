//Name: Adit Patel
//Date: June 3,2013
//Purpose: I used this class when i need to interact with sack only because the interaction method is different

import java.awt.geom.Rectangle2D;

public class Sack {
	private int x,y;
	private final int width = 154, height = 76;

	public Sack(int x, int y){
		setX(x);
		setY(y);
		getWidth();
		getHeight();		
	}
	
	public Rectangle2D.Double getBoundaryRectangle(){
		Rectangle2D.Double  rect = new Rectangle2D.Double(x, y, width, height);
		return rect;
	}

	//getters and setters
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

}
