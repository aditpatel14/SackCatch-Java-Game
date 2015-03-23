//Name: Adit Patel
//Date: June 3,2013
//Purpose: To create a fun game that works smoothly while learning about programming java 

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{
	//screen dimensions and variables
	static final int WIDTH = 1024;
	static final int HEIGHT = WIDTH / 4 * 3; //4:3 aspect ratio, Height is 768
		
	Keyboard Keyboard = new Keyboard();
	Mouse Mouse = new Mouse();
	String[] ScoreArray;
	
	//These are the objects and other interactive things used in the game
	private Objects star1 = new Objects(100,500,100,75);
	private Objects star2 = new Objects(200,500,100,75);
	private Objects star3 = new Objects(300,500,100,75);
	private Objects star4 = new Objects(400,500,100,75);
	private Objects star5 = new Objects(500,500,100,75);
	private Objects penguin1 = new Objects(Mouse.getMouseX(),500,179,200);
	private Objects penguinChute = new Objects(832,635,182,134);
	private Objects fish1 = new Objects(WIDTH,HEIGHT,275-71,114-23);
	private Objects anvil1 = new Objects(WIDTH,HEIGHT,610-433, 128-38);
	private Sack sack1 = new Sack(600,500);
	private SpriteSheet penguinAndSack = new SpriteSheet();
	private SpriteSheet titleSprite = new SpriteSheet();
	private SpriteSheet instructions = new SpriteSheet();
	private SpriteSheet chute = new SpriteSheet();
	private SpriteSheet fishAndObjects = new SpriteSheet();
	private SpriteSheet forestBackground = new SpriteSheet();
	private SpriteSheet star = new SpriteSheet();
	
	//These are the game states that are used
	public  static int  gameState;
	public final static int GAME_SHOWMENU = 0;
	public final static int GAME_PLAY = 1;
	public final static int GAME_INSTRUCTION = 2;
	public final static int GAME_HIGHSCORE = 3;
	public final static int LOST_LIFE = 4;
	public final static int GAME_WRITEHIGHSCORE=5;
	
	// These are variables that are used to switch between states and act as counters for methods
	private int totalUpdates = 0;
	private static int score = 0;
	private static int lives = 3;
	private static int helpNumber = 0;
	private static int livesSubtractCount=0;
	public static String playerName="";
	public static int playerScore;
	public static int enterButtonClickedCounter=0;
	public static int playerRating;
	public static int ratingClickedCounter=0;
	public static boolean enterButtonClicked;
	
	//Variables for each of the main objects in the game:Penguin,Sack,Fish,Anvil
	private int parabolaChoice;
	private int parabolaX=600;
	private int sackCounter = 0;
	private int numberOfTimesSackReleased = 0;
	private int brokenSackPositionX;
	private int sackHitGroundCounter = 0;
	boolean sackCaught = false;
	boolean sackHitGround = false;
	private int fishParabolaChoice;
	private int fishParabolaX=600;
	private int deadFishPositionX;
	private int numberOfTimesFishFell = 0;
	boolean fishCaught = false;
	boolean fishHitGround = false;
	private int anvilParabolaChoice;
	private int anvilParabolaX=600;
	private int numberOfTimesAnvilFell = 0;
	boolean anvilCaught=false;
	boolean anvilHitGround=false;
	
	//These are some colors that are the theme of my fame and Rectangles that are often used
	Color lightOrange = new Color(255,148,40);
	Rectangle2D groundRect = new Rectangle2D.Double(0,HEIGHT,WIDTH,1);
	Rectangle2D enterButton = new Rectangle2D.Double(500,200,175,75);
	
	private JFrame frame;
	//game updates per second
	static final int UPS = 60;
	//variables for the thread
	private Thread thread;
	private boolean running;
	//used for drawing items to the screen
	private Graphics2D graphics;
	//---------------------------------------------------------------------------------------------
	
	//initialize game objects, load media(pics, music, etc)
	public void init() {
		penguinAndSack.loadSpriteSheet("penguinAndSackSprites.png");
		titleSprite.loadSpriteSheet("menu.PNG");
		instructions.loadSpriteSheet("instructions.jpg");
		chute.loadSpriteSheet("chute.png");
		fishAndObjects.loadSpriteSheet("objectSprites.png");
		forestBackground.loadSpriteSheet("forestBackground1.jpg");
		star.loadSpriteSheet("blankstar.png");
	}
	//Everything that moves and needs to be updated goes here
	public void update() {
		if (gameState == GAME_PLAY){
			updatePenguin1();
			updateSack1();
			checkForGroundHit();
			
			if (score >=60){
				updateFish1();
			}
			if (score >=120){
				updateAnvil1();
			}
			if (score >= 220){
				parabolaX--;
			}
			if (score >= 400){
				fishParabolaX--;
			}
			if (score >=600){
				anvilParabolaX--;
			}
			
			goToLifeLostStateCheck();
			dropSacks();
			resetingSACKCAUGHTboolean();
		}
		else if(gameState == GAME_WRITEHIGHSCORE){
			if(enterButtonClicked==true){
				//System.out.println("somethingClicked");
				writeScoreInTextFile();
				enterButtonClicked=false;
			}
		}
		totalUpdates++;
	}
	//All the things that are drawn go here
	public void draw() {
		if (gameState == GAME_PLAY){
			drawforestBackground();
			drawLives();
			drawSack1();
			drawPenguin1();
			checkForSackCatch();
		
			if (score >= 60){
				drawFish1();
				checkForFishCatah();
				drawDeadFish();
			}
			if (score >= 120){
				drawAnvil1();
				checkForAnvilCatah();
			}
			drawBrokenSack();
			drawChute();
			returnToMenuButton();
			drawSackDroppedCounter();
		}
		else if (gameState == GAME_WRITEHIGHSCORE){
			drawBackground();
		
			enterNameButton();
			drawPrompts();
			returnToMenuButton();
			
			drawStar();
			drawPremenentStar();
			
		}
		else if (gameState == GAME_SHOWMENU){
			showMenu();
		}
		else if (gameState == GAME_INSTRUCTION){
			drawBackground();
			drawXYCoord();
			returnToMenuButton();
			drawInstructions();
		}
		else if (gameState == GAME_HIGHSCORE){
			drawBackground();
			
			returnToMenuButton();
			
			MakeHighScoreArray();
			organizingHighScoreArray();
			drawHighScoreArray();
			
		}
		else if (gameState == LOST_LIFE){
			livesSubtractCount++;
			if (livesSubtractCount==1){
				livesSubtract();
			}
			drawBackground();
			drawPenguin1();
			drawHelperMsg();
						
			Point2D mousePosition = new Point2D.Double(Mouse.getMouseX(),Mouse.getMouseY());
			Rectangle2D playButton = new Rectangle2D.Double(800,100,175,75);
					
			graphics.setColor(Color.black);
			graphics.fill(playButton);
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Arial", Font.PLAIN, 40));
			graphics.drawString("Okay",800+40,100+50);
			if (playButton.contains(mousePosition)){
				graphics.setColor(Color.white);
				graphics.fill(playButton);
				graphics.setColor(Color.black);
				graphics.setFont(new Font("Arial", Font.PLAIN, 40));
				graphics.drawString("Okay",800+40,100+50);
				if (Mouse.isMouseclicked()){
					Mouse.setMouseclicked(false);
					resetMidplayVariables();
					livesSubtractCount=0;
					if (lives == 0){
						Mouse.setMouseclicked(false);
						playerScore=score;
						gameState = GAME_WRITEHIGHSCORE;
					}
					else{
						Mouse.setMouseclicked(false);
						gameState = GAME_PLAY;
					}
					
				}
			}
		}
	}
	
	//Methods that are used in the Menu Stage
	public void showMenu(){
				Point2D mousePosition = new Point2D.Double(Mouse.getMouseX(),Mouse.getMouseY());
				//draw title page background
				BufferedImage titlePage = titleSprite.getSprite(0,0,1024,721);
				graphics.drawImage(titlePage, 0,0,WIDTH,HEIGHT,null);
				
				Rectangle2D playButton = drawPlayButton(lightOrange,Color.white);
				Rectangle2D instructionButton = drawInstructionButton(lightOrange,Color.white);
				Rectangle2D highscoreButton = drawHighscoreButton(lightOrange,Color.white);
				Rectangle2D quitButton = drawQuitButton(Color.gray,Color.pink);
				
				if (playButton.contains(mousePosition)){
					playButton = drawPlayButton(Color.white,lightOrange);
					if (Mouse.isMouseclicked()){
						Mouse.setMouseclicked(false);
						gameState = GAME_PLAY;
					}
				}
				if(quitButton.contains(mousePosition)||instructionButton.contains(mousePosition)||playButton.contains(mousePosition)||highscoreButton.contains(mousePosition)){
					if (instructionButton.contains(mousePosition)){
						instructionButton = drawInstructionButton(Color.white,lightOrange);
						if (Mouse.isMouseclicked()){
							Mouse.setMouseclicked(false);
							gameState = GAME_INSTRUCTION;
						}
					}
					else if (highscoreButton.contains(mousePosition)){
						highscoreButton = drawHighscoreButton(Color.white,lightOrange);
						if (Mouse.isMouseclicked()){
							Mouse.setMouseclicked(false);
							gameState = GAME_HIGHSCORE;
						}
					}
					else if (quitButton.contains(mousePosition)){
						quitButton = drawQuitButton(Color.pink,Color.gray);
						if (Mouse.isMouseclicked()){
							Mouse.setMouseclicked(false);
							System.out.println("quitButtonPressed");
							running = false;
							//stop(); dosent work, ask fenty if i can remove method from code
						}
					}
				}
			}
	public Rectangle2D drawPlayButton(Color buttonColor, Color fontColor){
				graphics.setColor(buttonColor);
				BufferedImage button = titleSprite.getSprite(717,498,292,85);
				graphics.drawImage(button, WIDTH-308,429,293,90,null);
				Rectangle2D playButton = new Rectangle2D.Double(WIDTH-305,431,287,85);
				graphics.fill(playButton);
				
				graphics.setColor(fontColor);
				graphics.setFont(new Font("Arial", Font.PLAIN, 50));
				graphics.drawString("PLAY",800,490);
				
				return playButton;
			}
	public Rectangle2D drawInstructionButton(Color buttonColor, Color fontColor){
				graphics.setColor(buttonColor);
				Rectangle2D instructionButton = new Rectangle2D.Double(WIDTH-290-15,533,287,85);
				graphics.fill(instructionButton);
				
				graphics.setColor(fontColor);
				graphics.setFont(new Font("Arial", Font.PLAIN, 35));
				graphics.drawString("INSTRUCTIONS",738,590);
				
				return instructionButton;	
			}
	public Rectangle2D drawHighscoreButton(Color buttonColor, Color fontColor){
				graphics.setColor(buttonColor);
				Rectangle2D highscoreButton = new Rectangle2D.Double(WIDTH-290-15,533+113,287,85);
				graphics.fill(highscoreButton);
				
				graphics.setColor(fontColor);
				graphics.setFont(new Font("Arial", Font.PLAIN, 35));
				graphics.drawString("HIGH SCORES",745,700);
				
				return highscoreButton;
			}
	public Rectangle2D drawQuitButton(Color buttonColor, Color fontColor){
				graphics.setColor(buttonColor);
				Rectangle2D quitButton = new Rectangle2D.Double(5,5,150,50);
				graphics.fill(quitButton);
				
				graphics.setColor(fontColor);
				graphics.setFont(new Font("Arial", Font.PLAIN, 35));
				graphics.drawString("QUIT",40,45);
				
				return quitButton;
			}
	public Rectangle2D menuButton(int x, int y, Color buttonColor, Color fontColor){
				graphics.setColor(buttonColor);
				Rectangle2D menuButton = new Rectangle2D.Double(x,y,150,50);
				graphics.fill(menuButton);
				
				graphics.setColor(fontColor);
				graphics.setFont(new Font("Arial", Font.PLAIN, 35));
				graphics.drawString("MENU",x+25 ,y+38);
				
				return menuButton;
			}
	public void drawInstructions(){
				BufferedImage instructionsPic = instructions.getSprite(100,46,1000,624);
				graphics.drawImage(instructionsPic, 12,100,1000,624,null);
			}
			
	//methods that are called in the update stage
	public void updatePenguin1(){
		penguin1.setX(Mouse.getMouseX()-penguin1.getWidth()/2);
	}
	public void updateSack1(){
		int y1=(int) (0.005*((parabolaX-900)*(parabolaX-900))-500);
		if(sack1.getX()+sack1.getWidth()<0 || sack1.getX()>WIDTH || sack1.getY()+sack1.getHeight()>HEIGHT || sackCaught==true ){
			parabolaChoice = chooseRandomParabola();
			//System.out.println("s Parabola: " + parabolaChoice);
			if (parabolaChoice == 0){
				parabolaX=HEIGHT;
			}
			else if (parabolaChoice == 1){
				parabolaX=HEIGHT;
			}
			else if (parabolaChoice == 2){
				parabolaX=500;
			}
			else if (parabolaChoice ==  3){
				parabolaX=HEIGHT;
			}
			else if (parabolaChoice == 4){
				parabolaX=300;
			}
			else if (parabolaChoice == 5){
				parabolaX=500;
			}
			else if (parabolaChoice == 6){
				parabolaX=300;
			}
			else if (parabolaChoice == 7){
				parabolaX=500;
			}	
			numberOfTimesSackReleased++;
		}
		if (numberOfTimesSackReleased > 0){
			if (parabolaChoice == 0){
				y1=(int) (0.005*((parabolaX-900)*(parabolaX-900))-500);
			}
			else if (parabolaChoice == 1){
				y1=(int) (0.01*((parabolaX-900)*(parabolaX-900))-500);
			}
			else if (parabolaChoice == 2){
				y1=(int) (0.003*((parabolaX-900)*(parabolaX-900))-500);
			}
			else if (parabolaChoice == 3){
				y1=(int) (0.0009*((parabolaX-800)*(parabolaX-800))-100);
				parabolaX--;	
			}
			else if (parabolaChoice == 4){
				y1=(int) (0.01*((parabolaX-500)*(parabolaX-500))-790);
			}
			else if(parabolaChoice == 5){
				y1=(int) (0.01*((parabolaX-700)*(parabolaX-700))+2500-3000);
			}
			else if (parabolaChoice ==6){
				y1=(int) (0.01*((parabolaX-200)*(parabolaX-700))-125);
			}	
			else if (parabolaChoice == 7){
				y1=(int) (0.01*((parabolaX-400)*(parabolaX-1200))-125);
				
			}
		}	
		parabolaX--;		
		sack1.setX(parabolaX);
		sack1.setY(y1);	
	}
	public void updateFish1(){
		int y1=(int) (0.003*((fishParabolaX-900)*(fishParabolaX-900))-500);
		if(fish1.getY()+fish1.getHeight()>HEIGHT || fishCaught==true){
			fishParabolaChoice = chooseRandomParabola();
			if (fishParabolaChoice == 0){
				fishParabolaX=HEIGHT;
			}
			else if (fishParabolaChoice == 1){
				fishParabolaX=HEIGHT;
			}
			else if (fishParabolaChoice == 2){
				fishParabolaX=500;
			}
			else if (fishParabolaChoice ==  3){
				fishParabolaX=HEIGHT;
			}
			else if (fishParabolaChoice == 4){
				fishParabolaX=300;
			}
			else if (fishParabolaChoice == 5){
				fishParabolaX=500;
			}
			else if (fishParabolaChoice == 6){
				fishParabolaX=300;
			}
			else if (fishParabolaChoice == 7){
				fishParabolaX=500;
			}	
			numberOfTimesFishFell++;
		}
		//System.out.println(fishParabolaX+","+ y1);
		if (numberOfTimesFishFell > 0){
			if (fishParabolaChoice == 0){
				y1=(int) (0.005*((fishParabolaX-900)*(fishParabolaX-900))-500);
			}
			else if (fishParabolaChoice == 1){
				y1=(int) (0.01*((fishParabolaX-900)*(fishParabolaX-900))-500);
			}
			else if (fishParabolaChoice == 2){
				y1=(int) (0.003*((fishParabolaX-900)*(fishParabolaX-900))-500);
			}
			else if (fishParabolaChoice == 3){
				y1=(int) (0.0009*((fishParabolaX-800)*(fishParabolaX-800))-100);
				//fishParabolaX--;	
			}
			else if (fishParabolaChoice == 4){
				y1=(int) (0.01*((fishParabolaX-500)*(fishParabolaX-500))-790);
			}
			else if(fishParabolaChoice == 5){
				y1=(int) (0.01*((fishParabolaX-700)*(fishParabolaX-700))+2500-3000);
			}
			else if (fishParabolaChoice ==6){
				y1=(int) (0.01*((fishParabolaX-200)*(fishParabolaX-700))-125);
			}	
			else if (fishParabolaChoice == 7){
				y1=(int) (0.01*((fishParabolaX-400)*(fishParabolaX-1200))-125);
				
			}
		}	
		fishParabolaX--;		
		fish1.setX(fishParabolaX);
		fish1.setY(y1);	
	}
	public void updateAnvil1(){
		int y1=(int) (0.0009*((anvilParabolaX-800)*(anvilParabolaX-800))-100);
		if(anvil1.getY()+anvil1.getHeight()>HEIGHT || anvilCaught==true){
			anvilParabolaChoice = chooseRandomParabola();
			if (anvilParabolaChoice == 0){
				anvilParabolaX=HEIGHT;
			}
			else if (anvilParabolaChoice == 1){
				anvilParabolaX=HEIGHT;
			}
			else if (anvilParabolaChoice == 2){
				anvilParabolaX=500;
			}
			else if (anvilParabolaChoice ==  3){
				anvilParabolaX=HEIGHT;
			}
			else if (anvilParabolaChoice == 4){
				anvilParabolaX=300;
			}
			else if (anvilParabolaChoice == 5){
				anvilParabolaX=500;
			}
			else if (anvilParabolaChoice == 6){
				anvilParabolaX=300;
			}
			else if (anvilParabolaChoice == 7){
				anvilParabolaX=500;
			}	
			numberOfTimesAnvilFell++;
		}
		//System.out.println(anvilParabolaX+","+ y1);
		if (numberOfTimesAnvilFell > 0){
			if (anvilParabolaChoice == 0){
				y1=(int) (0.005*((anvilParabolaX-900)*(anvilParabolaX-900))-500);
			}
			else if (anvilParabolaChoice == 1){
				y1=(int) (0.01*((anvilParabolaX-900)*(anvilParabolaX-900))-500);
			}
			else if (anvilParabolaChoice == 2){
				y1=(int) (0.003*((anvilParabolaX-900)*(anvilParabolaX-900))-500);
			}
			else if (anvilParabolaChoice == 3){
				y1=(int) (0.0009*((anvilParabolaX-800)*(anvilParabolaX-800))-100);
				//anvilParabolaX--;	
			}
			else if (anvilParabolaChoice == 4){
				y1=(int) (0.01*((anvilParabolaX-500)*(anvilParabolaX-500))-790);
			}
			else if(anvilParabolaChoice == 5){
				y1=(int) (0.01*((anvilParabolaX-700)*(anvilParabolaX-700))+2500-3000);
			}
			else if (anvilParabolaChoice ==6){
				y1=(int) (0.01*((anvilParabolaX-200)*(anvilParabolaX-700))-125);
			}	
			else if (anvilParabolaChoice == 7){
				y1=(int) (0.01*((anvilParabolaX-400)*(anvilParabolaX-1200))-125);
				
			}
		}	
		anvilParabolaX--;		
		anvil1.setX(anvilParabolaX);
		anvil1.setY(y1);	
	}
	public int chooseRandomParabola(){
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(8);
		return randomInt;		
	}
	public void goToLifeLostStateCheck(){
		if (anvilCaught == true || fishCaught == true || sackHitGroundCounter>=10 ||sackCounter ==6){
			gameState = LOST_LIFE;
		}
	}
	public void checkForGroundHit(){
		if (sack1.getBoundaryRectangle().intersects(groundRect)){
			parabolaX=HEIGHT;
			sackHitGround=true;
			brokenSackPositionX=sack1.getX();
			sackHitGroundCounter++;
			if(score>=5)
				score=score-5;
		}
		if (fish1.getBoundaryRectangle().intersects(groundRect)){
			fishParabolaX=HEIGHT;
			fishHitGround=true;
			deadFishPositionX=fish1.getX();
			helpNumber=3;
		}
		if (anvil1.getBoundaryRectangle().intersects(groundRect)){
			anvilParabolaX=HEIGHT;
			anvilHitGround=true;
			
			helpNumber=3;
		}
	}
	public void dropSacks(){
		Point2D mousePosition = new Point2D.Double(Mouse.getMouseX(),Mouse.getMouseY());
		if (penguinChute.getBoundaryRectangle().contains(mousePosition) && sackCounter>0 ){
			if (Mouse.isMouseclicked() && sackCounter>0){
				Mouse.setMouseclicked(false);
				sackCounter--;
				score=score+10;
			}
		}
		else
			Mouse.setMouseclicked(false);
	}
	public void resetingSACKCAUGHTboolean(){
		if (sackCaught==true){
			sackCounter++;
			if (sackCounter>6){
				sackCounter=6;
			}	
			sackCaught=false;
		}
	}
	public void writeScoreInTextFile(){
		try {
			PrintWriter scoreFileWriter = new PrintWriter(new BufferedWriter(new FileWriter("highscore.txt", true)));
			scoreFileWriter.println(playerName+"="+score);
			scoreFileWriter.close();
	    } catch (IOException e) {
            System.out.println("Unable to read file " + "highscore.txt".toString());
        }
	}
	
	//methods that are called in the draw stage
	public void drawBackground(){
		Color lightOrange = new Color(255,148,40);
		graphics.setColor(lightOrange);
		Rectangle2D background = new Rectangle2D.Double(0,0,WIDTH, HEIGHT);
		graphics.fill(background);		
	}
	public void drawforestBackground(){
		BufferedImage forest = forestBackground.getSprite(0,0,1022,766);
		graphics.drawImage(forest,0,0,WIDTH,HEIGHT,null);
	}
	public void drawLives(){
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("Arial", Font.PLAIN, 40));
		graphics.drawString("Lives: "+lives,10, 43);
	}
	public void drawSackDroppedCounter(){
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("Arial", Font.PLAIN, 40));
		graphics.drawString("Sacks Dropped: " + sackHitGroundCounter,210, 43);
	}
	public void drawPenguin1(){
		
		//System.out.println(anvilCaught);
		if (anvilCaught == true){
			penguin1.setY(500+90);
			penguin1.setWidth(668-403);
			penguin1.setHeight(305-186);
			BufferedImage penguinAnvilCrash = fishAndObjects.getSprite(403,186,668-403,305-186);
			graphics.drawImage(penguinAnvilCrash,penguin1.getX(),penguin1.getY(),668-403,305-186,null);
			
			
		//	gameState = LOST_LIFE;
		}
		else if (fishCaught == true){//fish and penguin
			penguin1.setY(500+90);
			penguin1.setWidth(297-31);
			penguin1.setHeight(365-239);
			BufferedImage penguinFishCrash = fishAndObjects.getSprite(31,239,297-31,365-239);
			graphics.drawImage(penguinFishCrash,penguin1.getX(),penguin1.getY(),297-31,365-239,null);
			
			
		//	gameState = LOST_LIFE;
		}
		else if (sackCounter == 0){
			penguin1.setY(500);
			penguin1.setWidth(179);
			penguin1.setHeight(200);
			BufferedImage penguin = penguinAndSack.getSprite(22,31,179,200);
			graphics.drawImage(penguin, Mouse.getMouseX()-penguin1.getWidth()/2,penguin1.getY(),179,200,null);
			
		}
		else if (sackCounter == 1){
			penguin1.setY(500);
			penguin1.setWidth(200);
			penguin1.setHeight(200);
			BufferedImage oneSackHeld = penguinAndSack.getSprite(242, 28, 200, 200);
			graphics.drawImage(oneSackHeld,penguin1.getX(),penguin1.getY(),200,200,null);
			
		}
		else if (sackCounter == 2){
			penguin1.setY(500-18);
			penguin1.setWidth(682-476);
			penguin1.setHeight(639-420);
			BufferedImage twoSacksHeld = penguinAndSack.getSprite(467, 11,206,219);
			graphics.drawImage(twoSacksHeld,penguin1.getX(),penguin1.getY(),682-476,639-420,null);
			
		}
		else if (sackCounter == 3){
			penguin1.setY(500-41);
			penguin1.setWidth(909-706);
			penguin1.setHeight(251-8);
			BufferedImage threeSacksHeld = penguinAndSack.getSprite(706,8,909-706,251-8);
			graphics.drawImage(threeSacksHeld,penguin1.getX(),penguin1.getY(),909-706,251-8,null);
			
		}
		else if (sackCounter == 4){
			penguin1.setY(500-63);
			penguin1.setWidth(1134-932);
			penguin1.setHeight(290-24);
			BufferedImage fourSacksHeld = penguinAndSack.getSprite(932,24,1134-932,290-24);
			graphics.drawImage(fourSacksHeld,penguin1.getX(),penguin1.getY(),1134-932,290-24,null);
			
		}
		else if (sackCounter == 5){
			penguin1.setY(500-84);
			penguin1.setWidth(213-10);
			penguin1.setHeight(584-294);
			BufferedImage fiveSacksHeld = penguinAndSack.getSprite(10,294,213-10,584-294);
			graphics.drawImage(fiveSacksHeld,penguin1.getX(),penguin1.getY(),213-10,584-294,null);
			
		}
		else if (sackCounter == 6){
			penguin1.setY(500+90);
			penguin1.setWidth(554-242);
			penguin1.setHeight(584-413);
			BufferedImage penguinCrash = penguinAndSack.getSprite(242,413,554-242,584-413);
			graphics.drawImage(penguinCrash,penguin1.getX(),penguin1.getY(),554-242,584-413,null);
			//lives--;
			//gameState = LOST_LIFE;
		}
		//graphics.draw(penguin1.getBoundaryRectangle());
	}
	public void drawSack1() {
		BufferedImage sackImage = penguinAndSack.getSprite(931,476,1088-931,556-476);
		graphics.drawImage(sackImage, sack1.getX(), sack1.getY(), 1088-931,556-476, null);
	}
	public void drawFish1(){
		BufferedImage fishOne = fishAndObjects.getSprite(71,23,275-71,114-23);
		graphics.drawImage(fishOne, fish1.getX(), fish1.getY(), 275-71, 114-23,null);
	}
	public void drawAnvil1(){
		BufferedImage Anvil = fishAndObjects.getSprite(433,38,610-433,128-38);
		graphics.drawImage(Anvil, anvil1.getX(), anvil1.getY(), 610-433, 128-38,null);
	}
	public void checkForSackCatch(){
		if (sack1.getBoundaryRectangle().intersects(penguin1.getX(),penguin1.getY(),penguin1.getWidth(),penguin1.getHeight()/3)){
			sackCaught=true;
			parabolaX=HEIGHT;
		}
	}
	public void checkForFishCatah(){
		if (fish1.getBoundaryRectangle().intersects(penguin1.getX(),penguin1.getY(),penguin1.getWidth(),penguin1.getHeight()/3)  ){
			fishCaught=true;
			fishParabolaX=HEIGHT;
		}
	}
	public void checkForAnvilCatah(){
		if (anvil1.getBoundaryRectangle().intersects(penguin1.getX(),penguin1.getY(),penguin1.getWidth(),penguin1.getHeight()/3/*penguin1.getBoundaryRectangle()*/ )  ){
			anvilCaught=true;
			helpNumber=3;
			anvilParabolaX=HEIGHT;
		}
		
	}
	public void drawDeadFish(){
		if (fishHitGround==true){
			BufferedImage deadFish = fishAndObjects.getSprite(8,154,324-8,218-154);
			graphics.drawImage(deadFish,deadFishPositionX,HEIGHT-(218-154),324-8,218-154,null);
		}
		if (fishHitGround == true && totalUpdates%(1.5*UPS)==0){
			fishHitGround=false;
		}
	}
	public void drawBrokenSack(){
		if (sackHitGround==true){
			BufferedImage brokenSack = penguinAndSack.getSprite(623,486,859-623,563-486);
			graphics.drawImage(brokenSack,brokenSackPositionX,HEIGHT-(563-486),859-623,563-486,null);
		}
		if (sackHitGround==true && totalUpdates%(1.5*UPS)==0){
			sackHitGround=false;
		}
	}
	public void drawChute(){
		BufferedImage chute1 = chute.getSprite(1023,599,  1282-1023,733-599 );
		graphics.drawImage(chute1,penguinChute.getX(),penguinChute.getY(),penguinChute.getWidth(),penguinChute.getHeight(),null);
		drawScoreOnChute();
	}
	public void drawScoreOnChute(){
		graphics.setColor(Color.BLUE);
		graphics.setFont(new Font("Arial", Font.PLAIN, 20));
		graphics.drawString("Score:",872+20,635+80);
		graphics.setFont(new Font("Arial", Font.PLAIN, 50));
		graphics.drawString(""+score,872+20,635+80+40);
	}
	public void resetMidplayVariables(){
		parabolaX=600;
		sackCounter = 0;
		numberOfTimesSackReleased = 0;
		brokenSackPositionX=0;//null
		sackHitGroundCounter = 0;
		sackCaught = false;
		sackHitGround = false;
		
		fishParabolaX=600;
		deadFishPositionX=0;//null
		numberOfTimesFishFell = 0;
		fishCaught = false;
		fishHitGround = false;
		
		anvilParabolaX=600;
		
		numberOfTimesAnvilFell = 0;
		anvilCaught = false;
		anvilHitGround = false;
		
		//System.out.println("variables got reset");
	}
	public void returnToMenuButton(){
		Point2D mousePosition = new Point2D.Double(Mouse.getMouseX(),Mouse.getMouseY());
		Rectangle2D menuButton = menuButton(850,5, Color.white,Color.black);
		if (menuButton.contains(mousePosition)){
			menuButton = menuButton(850,5,Color.black,Color.white);
			if (Mouse.isMouseclicked()){
				Mouse.setMouseclicked(false);
				restartGame();
				gameState = GAME_SHOWMENU;
				
			}
		}
	}
	public void restartGame(){
		parabolaX=600;
		sackCounter = 0;
		numberOfTimesSackReleased = 0;
		brokenSackPositionX=0;//null
		sackHitGroundCounter = 0;
		sackCaught = false;
		sackHitGround = false;
		
		fishParabolaX=600;
		deadFishPositionX=0;//null
		numberOfTimesFishFell = 0;
		fishCaught = false;
		fishHitGround = false;
		
		anvilParabolaX=600;
	
		numberOfTimesAnvilFell = 0;
		anvilCaught = false;
		anvilHitGround = false;
		
		totalUpdates = 0;
		score = 0;
		lives = 3;
		helpNumber = 0;
		livesSubtractCount=0;
		playerName="";
		playerScore=0;
		enterButtonClickedCounter=0;
		playerRating=0;
		ratingClickedCounter=0;
		
	}
	public void drawPrompts(){
		graphics.setColor(Color.BLUE);
		graphics.setFont(new Font("Arial", Font.PLAIN, 60));
		graphics.drawString("HIGHSCORES",100 ,70);
		
		graphics.setColor(Color.WHITE);
		Rectangle2D nameSpace = new Rectangle2D.Double(450-20,160-35,500,50);
		graphics.fill(nameSpace);
		
		graphics.setColor(Color.BLACK);
		graphics.setFont(new Font("Arial", Font.PLAIN, 30));
		graphics.drawString("Write your name here:",100,160);
		graphics.drawString(playerName,450,160);
	}
	public void enterNameButton(){
		if(enterButtonClickedCounter == 0){
			Point2D mousePosition = new Point2D.Double(Mouse.getMouseX(),Mouse.getMouseY());
			graphics.setColor(Color.black);
			graphics.fill(enterButton);
			graphics.setColor(Color.white);
			graphics.setFont(new Font("Arial", Font.PLAIN, 40));
			graphics.drawString("Enter",500+40,200+50);
			if (enterButton.contains(mousePosition)){
				graphics.setColor(Color.white);
				graphics.fill(enterButton);
				graphics.setColor(Color.black);
				graphics.setFont(new Font("Arial", Font.PLAIN, 40));
				graphics.drawString("Enter",500+40,200+50);
				if (Mouse.isMouseclicked()){			
					Mouse.setMouseclicked(false);
					livesSubtractCount=0;
					enterButtonClicked=true;
					enterButtonClickedCounter++;
				}
				else
					Mouse.setMouseclicked(false);
			}
			Mouse.setMouseclicked(false);
		}
	}
	public void drawXYCoord(){
		graphics.setColor(Color.black);
		graphics.setFont(new Font("Arial", Font.PLAIN, 40));
		graphics.drawString("(" + Mouse.getMouseX()+ "," + Mouse.getMouseY() + ")",1,41);
	}
	public void livesSubtract(){
		if (anvilCaught==true){
			lives--;
			helpNumber=3;
		}
		else if (fishCaught==true){
			lives--;
			helpNumber=3;
		}
		else if (sackHitGroundCounter>=10){
			lives--;
			helpNumber=2;
		}
		else if (sackCounter==6){
			lives--;
			helpNumber=1;
		}
	}
	public void drawHelperMsg(){
		graphics.setColor(Color.blue);
		graphics.setFont(new Font("Arial", Font.PLAIN, 60));
		graphics.drawString("HELPER MESSAGE:",70,100);
		
		graphics.setColor(Color.black);
		graphics.setFont(new Font("Arial", Font.PLAIN, 40));
		if (helpNumber==1){
			graphics.drawString("Do not carry more than 5 sacks!",100,200+90);
		}
		else if(helpNumber == 2){
			graphics.drawString("Be Careful, if you drop 10 sacks you will lose a life!", 100,200+90);
			graphics.drawString("You will also lose 5 points for every sack you drop!", 100,200+90+45);
		}
		else if (helpNumber ==3){
			graphics.drawString("Do not catch or touch anything but sacks!",100,200+90);	
		}
		
		graphics.setColor(Color.darkGray);
		graphics.drawString("Lives Left: "+lives,100,200);
		graphics.drawString("Your score is: "+score,100,200+45);
		
	}
		
	//Methods that deal with the high score and rating
	public void MakeHighScoreArray(){
		int arraySize=GetArraySize();
		ScoreArray= new String [arraySize];
		try{
			FileReader fr= new FileReader("highscore.txt");
			BufferedReader br = new BufferedReader (fr);
			for (int i=0;i<arraySize;i++){
				ScoreArray[i]=br.readLine();
			}
			br.close();
		} catch (IOException e){
			System.out.println("file not found");
		}
	}
	public int GetArraySize(){
		int arraySizeCounter=0;
		String str;
		try{
			FileReader fr= new FileReader("highscore.txt");
			BufferedReader br = new BufferedReader (fr);
			
			while ((str=br.readLine())!= null){
				arraySizeCounter++;
			}
			br.close();
		} catch (IOException e){
			System.out.println("file not found");
		}
		return arraySizeCounter;
	}
	public void organizingHighScoreArray(){
		int arraySize=GetArraySize();
		int[] score = new int[arraySize];
		String[] name = new String[arraySize];
		for(int i=0;i<arraySize;i++){
			score[i]= Integer.parseInt(ScoreArray[i].substring(ScoreArray[i].indexOf("=")+1,ScoreArray[i].length()));
			name[i]=ScoreArray[i].substring(0, ScoreArray[i].indexOf("="));
		}
		String temp2;
		int temp;
		for (int a = 0; a < arraySize-1; ++a) {
		    for (int b = 0; b < arraySize-1; ++b) {
		        if (score[b] < score[b + 1]) {
		            temp = score[b];
		            temp2= name[b];
		            
		            score[b] = score[b + 1];
		            name[b] = name[b+1];
		            
		            score[b + 1] = temp;
		            name[b+1]=temp2;
		        }
		    }
		}
		for(int i=0; i<arraySize; i++){
			ScoreArray[i]=name[i]+"="+score[i];
		}
	}
	public void drawHighScoreArray(){
		graphics.setColor(Color.BLUE);
		graphics.setFont(new Font("Arial", Font.PLAIN, 60));
		graphics.drawString("HIGHSCORES",200 ,70);
		graphics.setFont(new Font("Arial", Font.PLAIN, 25));
		int arraySize=GetArraySize();
		for (int i = 0; i<arraySize; i++){
			graphics.setColor(Color.BLACK);
			if (i<=19){
				graphics.drawString((i+1)+". "+ScoreArray[i],100,125+(i*32));
			}
			else if(i>=20 && i<=39){
				graphics.drawString((i+1)+". "+ScoreArray[i],500,125+((i-20)*32));
			}
		}		
	}
	public void drawStar(){
		if (ratingClickedCounter == 0){
			graphics.setColor(Color.blue);
			graphics.setFont(new Font("Arial", Font.PLAIN, 45));
			graphics.drawString("Please Rate this Game:",100,500);
			
			Point2D mousePosition = new Point2D.Double(Mouse.getMouseX(),Mouse.getMouseY());
			BufferedImage imageOfStar = star.getSprite(0, 17, 198-0, 205-17);
			BufferedImage BonusLife = fishAndObjects.getSprite(649,13,830-649,185-13);
			graphics.drawImage(imageOfStar, star1.getX(), star1.getY(),75,75,null);
			graphics.drawImage(imageOfStar, star2.getX(), star2.getY(),75,75,null);
			graphics.drawImage(imageOfStar, star3.getX(), star3.getY(),75,75,null);
			graphics.drawImage(imageOfStar, star4.getX(), star4.getY(),75,75,null);
			graphics.drawImage(imageOfStar, star5.getX(), star5.getY(),75,75,null);
			
			if (star1.getBoundaryRectangle().contains(mousePosition)){
				graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
				if (Mouse.isMouseclicked()){	
					Mouse.setMouseclicked(false);
					playerRating=1;
					ratingClickedCounter++;
				}
			}
			else if (star2.getBoundaryRectangle().contains(mousePosition)){
				graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
				graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
				if (Mouse.isMouseclicked()){	
					Mouse.setMouseclicked(false);
					graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
					graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
					playerRating=2;
					ratingClickedCounter++;
				}
			}
			else if (star3.getBoundaryRectangle().contains(mousePosition)){
				graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
				graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
				graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
				if (Mouse.isMouseclicked()){
					Mouse.setMouseclicked(false);
					graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
					graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
					graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
					playerRating=3;
					ratingClickedCounter++;
				}
			}
			else if (star4.getBoundaryRectangle().contains(mousePosition)){
				graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
				graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
				graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
				graphics.drawImage(BonusLife, star4.getX(), star4.getY(),75,75,null);
				if (Mouse.isMouseclicked()){	
					Mouse.setMouseclicked(false);
					graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
					graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
					graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
					graphics.drawImage(BonusLife, star4.getX(), star4.getY(),75,75,null);
					playerRating=4;
					ratingClickedCounter++;
				}
			}
			else if (star5.getBoundaryRectangle().contains(mousePosition)){
				graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
				graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
				graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
				graphics.drawImage(BonusLife, star4.getX(), star4.getY(),75,75,null);
				graphics.drawImage(BonusLife, star5.getX(), star5.getY(),75,75,null);
				if (Mouse.isMouseclicked()){	
					Mouse.setMouseclicked(false);
					playerRating=5;
					ratingClickedCounter++;
				}
			}
		}
	}
	public void drawPremenentStar(){
		BufferedImage BonusLife = fishAndObjects.getSprite(649,13,830-649,185-13);
		if (playerRating==1){
			graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
			showWhatYouRated();
		}
		else if (playerRating==2){
			graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
			graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
			showWhatYouRated();
		}
		else if (playerRating==3){
			graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
			graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
			graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
			showWhatYouRated();
		}
		else if (playerRating==4){
			graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
			graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
			graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
			graphics.drawImage(BonusLife, star4.getX(), star4.getY(),75,75,null);
			showWhatYouRated();
		}
		else if (playerRating==5){
			graphics.drawImage(BonusLife, star1.getX(), star1.getY(),75,75,null);
			graphics.drawImage(BonusLife, star2.getX(), star2.getY(),75,75,null);
			graphics.drawImage(BonusLife, star3.getX(), star3.getY(),75,75,null);
			graphics.drawImage(BonusLife, star4.getX(), star4.getY(),75,75,null);
			graphics.drawImage(BonusLife, star5.getX(), star5.getY(),75,75,null);
			showWhatYouRated();
		}
		
	}
	public void showWhatYouRated(){
		graphics.setColor(Color.black);
		graphics.setFont(new Font("Arial", Font.PLAIN, 30));
		graphics.drawString("Thank You For Rating this Game!",100,625);
		graphics.drawString("You rated this game "+playerRating+"/5!", 100,675);
		if (ratingClickedCounter==1){
			writeRatingtoTextFile();
			ratingClickedCounter++;
		}		
	}
	public void writeRatingtoTextFile(){
		try {
			PrintWriter scoreFileWriter = new PrintWriter(new BufferedWriter(new FileWriter("Rating.txt", true)));
			scoreFileWriter.println(playerName+" rated the Game: "+playerRating);
			scoreFileWriter.close();
	    } catch (IOException e) {
            System.out.println("Unable to read file");
        }
	}

	//=============================================================================================
 	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.add(game); //game is a component because it extends Canvas
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);		
		game.start();
	}
	public Game() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(size);
		System.out.println(HEIGHT);
		frame = new JFrame();
		
		//KEYBOARD and MOUSE handling code goes here
		addMouseMotionListener(Mouse);
		addMouseListener(Mouse);
		addKeyListener(Keyboard);

	}

	//starts a new thread for the game
	public synchronized void start() {
		thread = new Thread(this, "Game");
		running = true;
		thread.start();	
	}
	
	//main game loop
	public void run() {
		init();
		long startTime = System.nanoTime();
		double ns = 1000000000.0 / UPS;
		double delta = 0;
		int frames = 0;
		int updates = 0;
			   
		long secondTimer = System.nanoTime();
		while(running) {
			long now = System.nanoTime();
			delta += (now - startTime) / ns;
			startTime = now;
			while(delta >= 1) {
				update();
				delta--;
				updates++;
			}
			render();
			frames++;
			    
			if(System.nanoTime() - secondTimer > 1000000000) {
				this.frame.setTitle(updates + " ups  ||  " + frames + " fps" +"   Game made by: Adit Patel");
				secondTimer += 1000000000;
				frames = 0;
				updates = 0;
			}
		}
	 System.exit(0);
	}
	public void render() {
		BufferStrategy bs = getBufferStrategy(); //method from Canvas class
		
		if(bs == null) {
			createBufferStrategy(3); //creates it only for the first time the loop runs (trip buff)
			return;
		}
		
		graphics = (Graphics2D)bs.getDrawGraphics();
		draw();
		graphics.dispose();
		bs.show();
	}
	
	//stops the game thread and quits
	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}		
}
