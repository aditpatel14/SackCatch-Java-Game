//Name: Adit Patel
//Date: June 3,2013
//Purpose: This class is used when dealing getting and creating sprites to use in the game

import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//used to load and manage sprite sheets
public class SpriteSheet {
	public BufferedImage spriteSheet; //stores sprite sheet image

	//loads the sprite sheet with the given path (or filename) 
	public void loadSpriteSheet(String fileName) {
		try {
			BufferedImage sheet = ImageIO.read(new File(fileName));
			spriteSheet = sheet;
		} catch (IOException e) {System.out.println("No Image");}
	}
	//selects part of the sprite sheet and creates an image based on a subsection
	//at left-top point (x,y) with width w and height h
	public BufferedImage getSprite(int x, int y, int w, int h) {
		BufferedImage sprite = spriteSheet.getSubimage(x, y, w, h);
		return sprite;
	}
}