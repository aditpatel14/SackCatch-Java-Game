//Name: Adit Patel
//Date: June 3,2013
//Purpose: This class is used when dealing with keyboards

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{
	private int keyboardX,keyboardY;
	private boolean keyClicked;
		
	public void keyPressed(KeyEvent e) {
		char code = e.getKeyChar();
	}
	public void keyReleased(KeyEvent e) {
	
	}
	public void keyTyped(KeyEvent e) {
		if(Game.gameState == Game.GAME_WRITEHIGHSCORE){
			if (e.getKeyChar() == ''&&Game.playerName.length()>0){
				Game.playerName = Game.playerName.substring(0, Game.playerName.length()-1);
			}
			else {
				Game.playerName += e.getKeyChar();
			}
		}
	}
	
	//getters and setters
	public int getKeyboardX() {return keyboardX;}
	public void setKeyboardX(int keyboardX) {this.keyboardX = keyboardX;}
	public int getKeyboardY() {	return keyboardY;}
	public void setKeyboardY(int keyboardY) {this.keyboardY = keyboardY;}
	public boolean isKeyClicked() {return keyClicked;}
	public void setKeyClicked(boolean keyClicked) {this.keyClicked = keyClicked;}
	
}
