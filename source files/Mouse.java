//Name: Adit Patel
//Date: June 3,2013
//Purpose: This class is used when dealing with mouse

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseMotionListener,MouseListener{
	private int mouseX, mouseY;
	private boolean mouseclicked;

	//getters and setters
	public int getMouseX() {return mouseX;}
	public void setMouseX(int mouseX) {this.mouseX = mouseX;}
	
	public int getMouseY() {return mouseY;}
	public void setMouseY(int mouseY) {this.mouseY = mouseY;}
	
	public boolean isMouseclicked() {return mouseclicked;}
	public void setMouseclicked(boolean mouseclicked) {this.mouseclicked = mouseclicked;}
	
	//implemented methods
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}		
	public void mouseDragged(MouseEvent e) {}
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	public void mouseClicked(MouseEvent e) {
		mouseclicked =true;
	}	
}
