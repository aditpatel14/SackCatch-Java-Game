//Name: Adit Patel
//Date: June 3,2013
//Purpose: This class is used when i have to create an object in the game such as stars, or fish, or anvil

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Objects {
	int x,y,height,width;
	public Objects(int x, int y, int width, int height){
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);		
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
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
}	
