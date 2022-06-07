// Assignment: ISU
// Name: April Wei, Tyler Zeng
// Date: Jan 25, 2022
// Description: A rendition of Celeste. A single-player platformer where the character attempts to reach the top of the mountain.

// Imports
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Queue;
import java.awt.event.*;

public class testDriver extends JPanel implements KeyListener, ActionListener, Runnable {

	static JFrame frame;
	Font font;

	/*
	 * Game state 1-4 is the menu options. 5-9 are the levels. 10 is when paused. 11
	 * is the end. screen. 1 = main menu 2 = settings 3 = how to play 4 = credits 5
	 * = level 1 10 = pause screen
	 */
	int gameState = 1;
	int prevGameState;
	Thread thread;
	// Main menu
	int menuOption = 1;

	// Settings menu
	int settingOption = 1;
	int pauseOption = 1;
	int musicVolume = 50;
	int sfxVolume = 50;
	boolean setVol = false;

	// Character
	character MC = new character();

	// Strawberries
	Rectangle sr1 = new Rectangle(50 * 14, 50 * 5, 50, 50);
	Rectangle sr2 = new Rectangle(50 * 16, 50 * 10, 50, 50);
	Rectangle sr3 = new Rectangle(50 * 16, 50 * 15, 50, 50);
	Rectangle sr4 = new Rectangle(50 * 5, 50 * 7, 50, 50);
	Rectangle sr5 = new Rectangle(50 * 24, 50 * 13, 50, 50);
	Strawberry s1 = new Strawberry(50 * 14, 50 * 5);
	Strawberry s2 = new Strawberry(50 * 16, 50 * 10);
	Strawberry s3 = new Strawberry(50 * 16, 50 * 15);
	Strawberry s4 = new Strawberry(50 * 5, 50 * 7);
	Strawberry s5 = new Strawberry(50 * 24, 50 * 13);
	boolean s11 = true;
	boolean s22 = true;
	boolean s33 = true;
	boolean s44 = true;
	boolean s55 = true;

	// Leaderboard
	static HashMap<String, Integer> highScore = new HashMap<>();
	static LinkedList<Leaderboard> sorted = new LinkedList<>();
	Leaderboard score;

	// Boundary walls
	Rectangle rect = new Rectangle(MC.getX(), MC.getY(), 50, 50);
	static ArrayList<Rectangle> walls1 = new ArrayList<>();
	static ArrayList<Rectangle> walls2 = new ArrayList<>();
	static ArrayList<Rectangle> walls3 = new ArrayList<>();
	static ArrayList<Rectangle> walls4 = new ArrayList<>();
	static ArrayList<Rectangle> walls5 = new ArrayList<>();

	// Animations
	int i = 0;
	Image[] imgIdleRight = new Image[9];
	Image[] imgIdleLeft = new Image[9];
	Image[] imgWalkRight = new Image[12];
	Image[] imgWalkLeft = new Image[12];
	Image[] imgClimbRight = new Image[15];
	Image[] imgClimbLeft = new Image[15];
	char direction = 'R';

	// Movement
	boolean jump, left, right;
	int speed = 8; // SPEED //
	int jumpSpeed = 15; // JUMP SPEED //
	int xVel = 0;
	int yVel = 0;
	int gravity = 1;
	boolean airborne = false;
	boolean hasDouble = true;
	boolean doubleJump = false;
	boolean climbUp;
	boolean climbDown;
	boolean climbing;

	// Spawn locations
	int defaultx1 = 50 * 2;
	int defaulty1 = 50 * 14;
	int defaultx2 = 50 * 2;
	int defaulty2 = 50 * 15;
	int defaultx3 = 50 * 2;
	int defaulty3 = 50 * 13;
	int defaultx4 = 50 * 2;
	int defaulty4 = 50 * 12;
	int defaultx5 = 50 * 2;
	int defaulty5 = 50 * 14;

	// High score
	static Calendar cal = Calendar.getInstance();

	// Timer
	// Score algorithm (500 - seconds taken) + (countStraw * 50)
	int startTimeSeconds = (int) System.currentTimeMillis() / 1000;
	static int end;

	// FPS
	int FPS = 60;

	// Timer
	javax.swing.Timer tm = new javax.swing.Timer(120, this);

	// Level arrays
	static char[][] level1 = new char[18][26];
	static char[][] level2 = new char[18][26];
	static char[][] level3 = new char[18][26];
	static char[][] level4 = new char[18][26];
	static char[][] level5 = new char[18][26];
	static Queue<char[][]> levels = new LinkedList<>();

	// Music
	Sound sound = new Sound();
	Sound sfx = new Sound();

	// Method name: testDriver
	// Description: The constructor of the class
	// Parameters: n/a
	// Returns: n/a
	public testDriver() {

		// Jump variables
		jump = false;
		left = false;
		right = false;

		// Audio file
		URL soundURL = getClass().getResource("music.wav");
		playMusic(soundURL);

		// Importing animations
		for (int imageNo = 0; imageNo <= 8; imageNo++) {
			String s = "animation/idle0" + imageNo + ".png";
			imgIdleRight[imageNo] = new ImageIcon(s).getImage();
		}

		for (int imageNo = 0; imageNo <= 8; imageNo++) {
			String s = "animation/idle0" + imageNo + "left.png";
			imgIdleLeft[imageNo] = new ImageIcon(s).getImage();
		}

		for (int imageNo = 0; imageNo <= 11; imageNo++) {
			String s = "animation/walk" + imageNo + ".png";
			imgWalkRight[imageNo] = new ImageIcon(s).getImage();
		}

		for (int imageNo = 0; imageNo <= 11; imageNo++) {
			String s = "animation/walk" + imageNo + "left.png";
			imgWalkLeft[imageNo] = new ImageIcon(s).getImage();
		}

		for (int imageNo = 0; imageNo <= 14; imageNo++) {
			String s = "animation/climb" + imageNo + ".png";
			imgClimbRight[imageNo] = new ImageIcon(s).getImage();
		}

		for (int imageNo = 0; imageNo <= 14; imageNo++) {
			String s = "animation/climb" + imageNo + "left.png";
			imgClimbLeft[imageNo] = new ImageIcon(s).getImage();
		}

		setDoubleBuffered(true);

		// Default JFrame settings
		setPreferredSize(new Dimension(1300, 900));
		frame.setLocation(75, 0);
		frame.addKeyListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Reads in Renogare font file, sets to default font
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, new File("misc/renogare.ttf"));
			setFont(font.deriveFont(60f));
		} catch (IOException | FontFormatException e) {
		}

		tm.start();
		thread = new Thread(this);
		thread.start();
	}

	// Method name: paintComponent
	// Description: Paints the screen
	// Parameters: Graphics g
	// Returns: void
	public void paintComponent(Graphics g) {
		if ((gameState >= 1 && gameState <= 4) || gameState == 11 || gameState == 10 || gameState == 12)
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/mountain.gif"), 0, 0, 1300, 900, this);

		// Main menu
		if (gameState == 1) {
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/logo.png"), 10, 70, 450, 300, this);

			// Play button
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/play.png"), 100, 372, 50, 50, this);
			if (menuOption == 1)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Play", 160, 420);

			// Settings button
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/settings.png"), 100, 445, 50, 50, this);
			if (menuOption == 2)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Settings", 160, 490);

			// How to Play button
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/journal.png"), 100, 514, 50, 50, this);
			if (menuOption == 3)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("How to Play", 160, 560);

			// Credits button
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/credits.png"), 100, 583, 50, 50, this);
			if (menuOption == 4)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Credits", 160, 630);

			// Leaderboard button
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/leaderboard.png"), 100, 652, 50, 50, this);
			if (menuOption == 5)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Leaderboard", 160, 700);

			// Exit button
			g.drawImage(Toolkit.getDefaultToolkit().getImage("icon/exit.png"), 100, 721, 50, 50, this);
			if (menuOption == 6)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Exit", 160, 770);

			// Note
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("C to Select", 1140, 825);
		}
		// Settings page
		else if (gameState == 2) {
			g.setColor(Color.white);
			g.drawString("Settings", 500, 150);

			// Volume control
			if (settingOption == 1)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Music volume", 400, 300);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/left.png"), 450, 350, this);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/right.png"), 725, 350, this);
			g.setColor(Color.white);
			if (musicVolume < 100)
				g.drawString(musicVolume + "%", 560, 405);
			else
				g.drawString(musicVolume + "%", 540, 405);

			g.setFont(font.deriveFont(20f));
			g.drawString("Press X to go back", 525, 800);
		}

		// How to Play page
		else if (gameState == 3) {
			g.setFont(font.deriveFont(60f));
			g.setColor(Color.white);
			g.drawString("How to Play", 450, 150);

			g.setFont(font.deriveFont(40f));
			g.drawString("Goal: Reach the top of", 400, 275);
			g.drawString("Celeste Mountain", 450, 325);

			g.drawString("Don't touch the spikes or fall into the void", 195, 400);
			g.drawString("Collect as many strawberries as you can", 200, 500);

			g.drawString("Arrow keys to move", 420, 575);
			g.drawString("C to jump", 525, 625);
			g.drawString("X to dash - only once midair", 330, 675);
			g.drawString("Z to climb - watch your stamina", 290, 725);

			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("Press X to go back", 525, 800);
		}
		// Credits page
		else if (gameState == 4) {
			g.setFont(font.deriveFont(60f));
			g.setColor(Color.white);
			g.drawString("Credits", 520, 150);

			g.setFont(font.deriveFont(40f));
			g.drawString("Made by:", 540, 275);
			g.drawString("Tyler Zeng", 520, 325);
			g.drawString("April Wei", 540, 375);

			g.drawString("January 25, 2021", 440, 500);
			g.drawString("ICS4U ISU Project", 430, 550);

			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("Press X to go back", 525, 800);

			// Level 1
		} else if (gameState == 5) {

			// Image background
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/01.png"), 0, 0, 1300, 900, this);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/02.png"), 0, 0, 1300, 900, this);

			// Tile drawing
			for (int x = 0; x < 18; x++) {
				for (int y = 0; y < 26; y++) {
					if (level1[x][y] != 'N') {
						g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/" + level1[x][y] + ".png"), 50 * y,
								50 * x, 50, 50, this);
					}
				}
			}

			// Character
			drawMC(g);

			// Strawberry
			if (s11)
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), s1.getX(), s1.getY(), 50, 50,
						this);

			// Strawberry score
			g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), 0, 0, 50, 50, this);
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("x" + MC.getStaw(), 60, 34);
			// LEVEL 2
		} else if (gameState == 6) {
			// Image background
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/01.png"), 0, 0, 1300, 900, this);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/02.png"), 0, 0, 1300, 900, this);

			// Tile drawing
			for (int x = 0; x < 18; x++) {
				for (int y = 0; y < 26; y++) {
					if (level2[x][y] != 'N') {
						g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/" + level2[x][y] + ".png"), 50 * y,
								50 * x, 50, 50, this);
					}
				}
			}

			// Character
			drawMC(g);

			// Strawberry
			if (s22)
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), s2.getX(), s2.getY(), 50, 50, this);

			// Strawberry score
			g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), 0, 0, 50, 50, this);
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("x" + MC.getStaw(), 60, 34);

		} else if (gameState == 7) {
			// Image background
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/01.png"), 0, 0, 1300, 900, this);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/02.png"), 0, 0, 1300, 900, this);

			// Tile drawing
			for (int x = 0; x < 18; x++) {
				for (int y = 0; y < 26; y++) {
					if (level3[x][y] != 'N') {
						g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/" + level3[x][y] + ".png"), 50 * y,
								50 * x, 50, 50, this);
					}
				}
			}
			// Character
			drawMC(g);

			// Strawberry
			if (s33)
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), s3.getX(), s3.getY(), 50, 50, this);

			// Strawberry score
			g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), 0, 0, 50, 50, this);
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("x" + MC.getStaw(), 60, 34);

		} else if (gameState == 8) {

			// Image background
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/01.png"), 0, 0, 1300, 900, this);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/02.png"), 0, 0, 1300, 900, this);

			// Tile drawing
			for (int x = 0; x < 18; x++) {
				for (int y = 0; y < 26; y++) {
					if (level4[x][y] != 'N') {
						g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/" + level4[x][y] + ".png"), 50 * y,
								50 * x, 50, 50, this);
					}
				}
			}

			// Character
			drawMC(g);

			// Strawberry
			if (s44)
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), s4.getX(), s4.getY(), 50, 50, this);

			// Strawberry score
			g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), 0, 0, 50, 50, this);
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("x" + MC.getStaw(), 60, 34);

		} else if (gameState == 9) {
			// Image background
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/01.png"), 0, 0, 1300, 900, this);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("misc/02.png"), 0, 0, 1300, 900, this);

			// Tile drawing
			for (int x = 0; x < 18; x++) {
				for (int y = 0; y < 26; y++) {
					if (level5[x][y] != 'N') {
						g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/" + level5[x][y] + ".png"), 50 * y,
								50 * x, 50, 50, this);
					}
				}
			}
			// Character
			drawMC(g);

			// Strawberry
			if (s55)
				g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), s5.getX(), s5.getY(), 50, 50, this);

			// Strawberry score
			g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), 0, 0, 50, 50, this);
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("x" + MC.getStaw(), 60, 34);
		}

		// Pause menu
		else if (gameState == 10) {
			// Image background
			g.setFont(font.deriveFont(60f));
			g.setColor(Color.white);
			g.drawString("PAUSE", 510, 275);

			g.setFont(font.deriveFont(40f));
			// Resume
			if (pauseOption == 1)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Resume", 535, 490);

			// Main menu
			if (pauseOption == 2)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Main menu", 500, 590);

			// Exit
			if (pauseOption == 3)
				g.setColor(Color.cyan);
			else
				g.setColor(Color.white);
			g.drawString("Exit game", 510, 690);

		}

		// Leaderboard
		else if (gameState == 11) {
			int y = 400;
			int loop = 0;
			g.setFont(font.deriveFont(60f));
			g.setColor(Color.white);
			g.drawString("Leaderboards", 425, 200);
			g.setFont(font.deriveFont(20f));
			g.setColor(Color.white);
			g.drawString("Press X to go back", 525, 800);

			g.setFont(font.deriveFont(40f));
			g.drawString("Date", 400, 300);
			g.drawString("Score", 800, 300);

			g.setFont(font.deriveFont(20f));
			for (Leaderboard i : sorted) {
				if (loop < 3) {
					g.drawString(i.getDate(), 400, y);
					g.drawString(Integer.toString(i.getScore()), 840, y);
					y += 100;
					loop++;
				} else {
					break;
				}
			}
		}

		// Win screen
		else if (gameState == 12) {
			g.setFont(font.deriveFont(60f));
			g.setColor(Color.white);
			g.drawString("You win!", 500, 200);
			g.setFont(font.deriveFont(40f));
			g.drawString("Strawberries collected: " + MC.countStraw, 350, 300);
			g.drawImage(Toolkit.getDefaultToolkit().getImage("blocks/A.png"), 925, 260, 50, 50, this);
			g.drawString("Time taken: " + end + " seconds", 400, 400);
			g.setFont(font.deriveFont(50f));
			g.setColor(Color.cyan);
			g.drawString("Final Score", 475, 600);
			g.setFont(font.deriveFont(45f));
			g.setColor(Color.white);
			g.drawString(Integer.toString(score.getScore()), 600, 675);
			g.setFont(font.deriveFont(20f));
			g.drawString("Press C to exit", 560, 750);
		}

	}

	// Method name: drawMC
	// Description: Paints the character (and animations)
	// Parameters: Graphics g
	// Returns: void
	public void drawMC(Graphics g) {
		try {
			// Animations
			if (MC.getIdle()) {

				// Idle animations
				if (direction == 'R') {
					g.setColor(new Color(133, 48, 80));
					if (i >= 4) {
						g.fillRect(MC.getX() + 22, MC.getY() - 3, 23, 30);
						g.fillRect(MC.getX() + 14, MC.getY() + 4, 20, 20);
					}
					else {
						g.fillRect(MC.getX() + 22, MC.getY() - 7, 23, 30);
						g.fillRect(MC.getX() + 14, MC.getY(), 20, 20);
					}
					g.drawImage(imgIdleRight[i], MC.getX(), MC.getY(), 50, 50, this);
				} else if (direction == 'L') {
					g.setColor(new Color(133, 48, 80));
					if (i >= 4) {
						g.fillRect(MC.getX() + 5, MC.getY() - 3, 23, 30);
						g.fillRect(MC.getX() + 16, MC.getY() + 4, 20, 20);
					}
					else {
						g.fillRect(MC.getX() + 5, MC.getY() - 7, 23, 30);
						g.fillRect(MC.getX() + 16, MC.getY(), 20, 20);
					}
					g.drawImage(imgIdleLeft[i], MC.getX(), MC.getY(), 50, 50, this);
				}

				// Walking animations
			} else if (right && !airborne && !climbing) {
				g.setColor(new Color(133, 48, 80));
				if (i >= 1 && i <= 3 || i >= 6 && i <= 9) {
					g.fillRect(MC.getX() + 22, MC.getY(), 23, 30);
					g.fillRect(MC.getX() + 14, MC.getY() + 7, 20, 20);
				}
				else {
					g.fillRect(MC.getX() + 22, MC.getY() - 4, 23, 30);
					g.fillRect(MC.getX() + 14, MC.getY() + 3, 20, 20);
				}
				g.drawImage(imgWalkRight[i], MC.getX(), MC.getY(), 50, 50, this);
			} else if (left && !airborne && !climbing) {
				g.setColor(new Color(133, 48, 80));
				if (i >= 1 && i <= 3 || i >= 6 && i <= 9) {
					g.fillRect(MC.getX() + 5, MC.getY(), 23, 30);
					g.fillRect(MC.getX() + 16, MC.getY() + 7, 20, 20);
				}
				else {
					g.fillRect(MC.getX() + 5, MC.getY() - 4, 23, 30);
					g.fillRect(MC.getX() + 16, MC.getY() + 3, 20, 20);
				}
				g.drawImage(imgWalkLeft[i], MC.getX(), MC.getY(), 50, 50, this);
			}

			// Climbing
			else if ((climbUp || climbDown) && hasClimb() || climbing && hasClimb()) {
				g.setColor(new Color(133, 48, 80));
				if (direction == 'R') {
					if (i >= 1 && i <= 2) {
						g.fillRect(MC.getX()+22, MC.getY()-5, 23, 30);
						g.fillRect(MC.getX()+14, MC.getY()+3, 20, 20);
					}
					else if (i >= 4 && i <= 8 || i == 12) {
						g.fillRect(MC.getX()+18,  MC.getY()-8,  23,  30);
						g.fillRect(MC.getX()+10, MC.getY(), 20, 20);
					}
					else if (i >= 9 && i <= 11) {
						g.fillRect(MC.getX()+26, MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+18, MC.getY(), 20, 20);
					}
					else if (i == 13) {
						g.fillRect(MC.getX()+14,  MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+6, MC.getY(), 20, 20);
					}
					else if (i == 14) {
						g.fillRect(MC.getX()+10,  MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+2, MC.getY(), 20, 20);
					}
					else {
						g.fillRect(MC.getX()+22, MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+14, MC.getY(), 20, 20);
					}
					g.drawImage(imgClimbRight[i], MC.getX(), MC.getY(), 50, 50, this);

				} else if (direction == 'L') {
					if (i >= 1 && i <= 2) {
						g.fillRect(MC.getX()+5,  MC.getY()-5, 23, 30);
						g.fillRect(MC.getX()+16, MC.getY()+3, 20, 20);
					}
					else if (i >= 4 && i <= 8 || i == 12) {
						g.fillRect(MC.getX()+9,  MC.getY()-8,  23,  30);
						g.fillRect(MC.getX()+20, MC.getY(), 20, 20);
					}
					else if (i >= 9 && i <= 11) {
						g.fillRect(MC.getX()+1, MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+12, MC.getY(), 20, 20);
					}
					else if (i == 13) {
						g.fillRect(MC.getX()+13,  MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+24, MC.getY(), 20, 20);
					}
					else if (i == 14) {
						g.fillRect(MC.getX()+17,  MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+28, MC.getY(), 20, 20);
					}
					else {
						g.fillRect(MC.getX()+5, MC.getY()-8, 23, 30);
						g.fillRect(MC.getX()+16, MC.getY(), 20, 20);
					}
					g.drawImage(imgClimbLeft[i], MC.getX(), MC.getY(), 50, 50, this);
				}
			}
			// Jumping
			else if (hasDouble) {
				g.setColor(new Color(133, 48, 80));
				if (direction == 'R') {
					g.fillRect(MC.getX() + 22, MC.getY() - 7, 23, 30);
					g.fillRect(MC.getX() + 14, MC.getY(), 20, 20);
					g.drawImage(imgIdleRight[0], MC.getX(), MC.getY(), 50, 50, this);
				} else if (direction == 'L') {
					g.fillRect(MC.getX() + 5, MC.getY() - 7, 23, 30);
					g.fillRect(MC.getX() + 16, MC.getY(), 20, 20);
					g.drawImage(imgIdleLeft[0], MC.getX(), MC.getY(), 50, 50, this);
				}
			}

			if (!hasDouble && airborne) {
				g.setColor(new Color(0, 200, 255));
				if (direction == 'R') {
					g.fillRect(MC.getX() + 22, MC.getY() - 7, 23, 30);
					g.fillRect(MC.getX() + 14, MC.getY(), 20, 20);
					g.drawImage(imgIdleRight[0], MC.getX(), MC.getY(), 50, 50, this);
				} else if (direction == 'L') {
					g.fillRect(MC.getX() + 5, MC.getY() - 7, 23, 30);
					g.fillRect(MC.getX() + 16, MC.getY(), 20, 20);
					g.drawImage(imgIdleLeft[0], MC.getX(), MC.getY(), 50, 50, this);
				}
			}
		} catch (Exception e) {
		}
	}

	// Method name: actionPerformed
	// Description: Does a task based on the action performed
	// Parameters: Action event
	// Returns: void
	public void actionPerformed(ActionEvent e) {
		if (!climbing && !jump && !right && !left && !doubleJump)
			MC.setIdle(true);
		if (MC.getIdle()) {
			if (i >= 8)
				i = 0;
			else
				i++;
		} else if ((right || left) && !(hasClimb() && climbing)) {
			if (i >= 11)
				i = 0;
			else
				i++;
		} else if (climbUp || climbDown) {
			if (i >= 14)
				i = 0;
			else
				i++;
		}
		rect.x = MC.getX();
		rect.y = MC.getY();
		// System.out.println(airborne);
		repaint();
	}

	// Method name: keyPressed
	// Description: Does a task based on the key pressed
	// Parameters: KeyEvent
	// Returns: void
	public void keyPressed(KeyEvent e) {

		// DOWN //
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {

			// Down arrow in menu screen
			if (gameState == 1) {
				if (menuOption < 6)
					menuOption++;
			}

			// Down arrow in pause screen
			if (gameState == 10) {
				if (pauseOption < 3) {
					pauseOption++;
				}
			}
			// Climbing down in game
			if (gameState >= 5 && gameState < 10) {
				if (hasClimb() && climbing) {
					climbDown = true;
					MC.setIdle(false);
					MC.depleteStamina();
				}
			}
		}

		// UP //
		if (e.getKeyCode() == KeyEvent.VK_UP) {

			// Up arrow in menu screen
			if (gameState == 1) {
				if (menuOption > 1)
					menuOption--;
			}
			// Up arrow in setting screen
			if (gameState == 2) {
				if (settingOption > 1) {
					settingOption--;
				}
			}
			// Up arrow in pause screen
			if (gameState == 10) {
				if (pauseOption > 1) {
					pauseOption--;
				}
			}
			// Climbing up in game
			if (gameState >= 5 && gameState < 10) {
				if (hasClimb() && climbing) {
					climbUp = true;
					MC.setIdle(false);
					MC.depleteStamina();
				}
			}
		}

		// LEFT //
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {

			// Left arrow in setting screen
			if (gameState == 2) {
				if (settingOption == 1 && musicVolume > 0) {
					musicVolume--;
					sound.volumeDown();
				} else if (settingOption == 1 && musicVolume == 0) {
					sound.volumeMute();
				}
				if (settingOption == 2 && sfxVolume > 0)
					sfxVolume -= 25;
			}

			// Moving left
			if (gameState >= 5 && gameState < 10 && !(hasClimb() && climbing)) {
				left = true;
				right = false;
				direction = 'L';
				MC.setIdle(false);
			}

		}

		// RIGHT //
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

			// Down arrow in menu screen
			if (gameState == 2) {
				if (settingOption == 1 && musicVolume < 100) {
					musicVolume++;
					sound.volumeUp();
				}
				if (settingOption == 2 && sfxVolume < 100)
					sfxVolume += 25;
			}

			// Moving right
			if (gameState >= 5 && gameState < 10 && !(hasClimb() && climbing)) {
				right = true;
				left = false;
				direction = 'R';
				MC.setIdle(false);
				// sfx.play();
			}
		}

		// C //
		if (e.getKeyCode() == KeyEvent.VK_C) {

			// C key in menu screen
			if (gameState == 1) {
				if (menuOption == 1) {
					gameState = 5;
				}
				if (menuOption == 6) {
					System.exit(0);
				}
				if (menuOption == 1) {
					gameState = 5;
				} else if (menuOption == 5) {
					gameState = 11;
				} else
					gameState = menuOption;
			}

			// C key in settings screen
			if (gameState == 2) {
				if (settingOption == 1) {
					setVol = true;
				}
			}
			// Jumping in game
			if (gameState >= 5 && gameState < 10) {
				jump = true;
				MC.setIdle(false);
			}
			// Unpausing
			if (gameState == 10) {
				if (pauseOption == 1) {
					gameState = prevGameState;
				} else if (pauseOption == 2) {
					gameState = 1;
					MC.setX(defaultx1);
					MC.setY(defaulty1);
				} else if (pauseOption == 3) {
					System.exit(0);
				}
			}

			// System.exit
			if (gameState == 12) {
				gameState = 1;
				MC.setX(defaultx1);
				MC.setY(defaulty1);
				rect.x = defaultx1;
				rect.y = defaulty1;
				airborne = false;
				doubleJump = false;
				hasDouble = true;
				MC.resetStraw();
				startTimeSeconds = (int) System.currentTimeMillis() / 1000;
				s11 = true;
				s22 = true;
				s33 = true;
				s44 = true;
				s55 = true;
			}
		}

		// X //
		if (e.getKeyCode() == KeyEvent.VK_X) {
			// Going back from setting to menu
			if ((menuOption >= 2 && menuOption <= 4) || gameState == 11) {
				gameState = 1;
				settingOption = 1;
			}

			// Dash in game
			if (gameState >= 5 && gameState < 10) {
				if (airborne && hasDouble) {
					hasDouble = false;
					doubleJump = true;
					climbing = false;
					climbUp = false;
					climbDown = false;
					MC.setIdle(false);
				}
			}

		}

		// Z //
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			if (gameState >= 5 && gameState < 10) {
				climbing = true;
				if (hasClimb()) {
					MC.setIdle(false);
				}
			}
		}

		// ESC //
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			// Open setting
			if (gameState >= 5 && gameState < 10 && airborne == false) {
				prevGameState = gameState;
				gameState = 10;
			}

		}
	}

	// NOT IN USE
	public void keyTyped(KeyEvent e) {
	}

	// Method name: keyReleased
	// Description: Does a task based on the key released
	// Parameters: KeyEvent
	// Returns: void
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = false;
			if (!climbing && !jump && !left && !doubleJump) {
				MC.setIdle(true);
				if (i >= 8)
					i = 0;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			left = false;
			if (!climbing && !jump && !right && !doubleJump) {
				MC.setIdle(true);
				if (i >= 8)
					i = 0;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			jump = false;
			if (!climbing && !left && !right && !doubleJump) {
				MC.setIdle(true);
				if (i >= 8)
					i = 0;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			doubleJump = false;
			if (!climbing && !jump && !right && !left) {
				MC.setIdle(true);
				if (i >= 8)
					i = 0;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			climbing = false;
			climbUp = false;
			climbDown = false;
			if (!jump && !left && !right && !doubleJump) {
				MC.setIdle(true);
				if (i >= 8)
					i = 0;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			climbUp = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			climbDown = false;
		}
	}

	// Method name: keepBounds
	// Description: Checks the boundaries of the character
	// Parameters: Rectangle
	// Returns: void
	void keepBounds(Rectangle wall) {
		int tempX;

		// Sets airborne value
		if (gameState == 5) {
			if (MC.getX() % 50 == 0
					|| MC.getX() % 50 != 0 && level1[(MC.getY() + 50) / 50][MC.getX() / 50 + 1] == 'N') {
				tempX = MC.getX() / 50;
			} else {
				tempX = MC.getX() / 50 + 1;
			}
			if (level1[(MC.getY() + 50) / 50][tempX] == 'N' && level1[(MC.getY()) / 50][(MC.getX()) / 50] == 'N') {
				airborne = true;
			} else if (level1[(MC.getY() + 50) / 50][tempX] != 'N') {
				airborne = false;
			}
		}
		if (gameState == 6) {
			if (MC.getX() % 50 == 0
					|| MC.getX() % 50 != 0 && level2[(MC.getY() + 50) / 50][MC.getX() / 50 + 1] == 'N') {
				tempX = MC.getX() / 50;
			} else {
				tempX = MC.getX() / 50 + 1;
			}
			if (level2[(MC.getY() + 50) / 50][tempX] == 'N' && level2[(MC.getY()) / 50][(MC.getX()) / 50] == 'N') {
				airborne = true;
			} else if (level2[(MC.getY() + 50) / 50][tempX] != 'N') {
				airborne = false;
			}
			if (MC.getY() >= 800) {
				MC.setY(defaulty2);
				MC.setX(defaultx2);
				MC.subStraw();
				s22 = true;
			}
		}
		if (gameState == 7) {
			if (MC.getY() >= 850) {
				MC.setY(defaulty3);
				MC.setX(defaultx3);
			}
			if (MC.getX() % 50 == 0
					|| MC.getX() % 50 != 0 && level3[(MC.getY() + 50) / 50][MC.getX() / 50 + 1] == 'N') {
				tempX = MC.getX() / 50;
			} else {
				tempX = MC.getX() / 50 + 1;
			}
			if (level3[(MC.getY() + 50) / 50][tempX] == 'N' && level3[(MC.getY()) / 50][(MC.getX()) / 50] == 'N') {
				airborne = true;
			} else if (level3[(MC.getY() + 50) / 50][tempX] != 'N') {
				airborne = false;
			}
			if (MC.getY() >= 800) {
				MC.setY(defaulty3);
				MC.setX(defaultx3);
				if (!s33) {
					MC.subStraw();
					s33 = true;
				}
			}
		}
		if (gameState == 8) {
			if (MC.getX() % 50 == 0
					|| MC.getX() % 50 != 0 && level4[(MC.getY() + 50) / 50][MC.getX() / 50 + 1] == 'N') {
				tempX = MC.getX() / 50;
			} else {
				tempX = MC.getX() / 50 + 1;
			}
			if (level4[(MC.getY() + 50) / 50][tempX] == 'N' && level4[(MC.getY()) / 50][(MC.getX()) / 50] == 'N') {
				airborne = true;
			} else if (level4[(MC.getY() + 50) / 50][tempX] != 'N') {
				airborne = false;
			}
			if (MC.getY() >= 800) {
				MC.setY(defaulty4);
				MC.setX(defaultx4);
				if (!s44) {
					MC.subStraw();
					s44 = true;
				}
			}
		}
		if (gameState == 9) {
			if (MC.getX() % 50 == 0
					|| MC.getX() % 50 != 0 && level5[(MC.getY() + 50) / 50][MC.getX() / 50 + 1] == 'N') {
				tempX = MC.getX() / 50;
			} else {
				tempX = MC.getX() / 50 + 1;
			}
			if (level5[(MC.getY() + 50) / 50][tempX] == 'N' && level5[(MC.getY()) / 50][(MC.getX()) / 50] == 'N') {
				airborne = true;
			} else if (level5[(MC.getY() + 50) / 50][tempX] != 'N') {
				airborne = false;
			}
			if (MC.getY() >= 800) {
				MC.setY(defaulty5);
				MC.setX(defaultx5);
				if (!s55) {
					MC.subStraw();
					s55 = true;
				}
			}
		}

		// Collision
		if (rect.intersects(wall)) {
			double left1 = rect.getX();
			double right1 = rect.getX() + rect.getWidth();
			double top1 = rect.getY();
			double bottom1 = rect.getY() + rect.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();

			// rect collides from left side of the wall
			if (right1 > left2 && left1 < left2 && right1 - left2 < bottom1 - top2 && right1 - left2 < bottom2 - top1) {
				// Level 1
				if (gameState == 5) {
					checkSpikes(wall, 1, level1, defaultx1, defaulty1);
				}

				// Level 2
				else if (gameState == 6) {
					checkSpikes(wall, 1, level2, defaultx2, defaulty2);
				}

				// Level 3
				else if (gameState == 7) {
					checkSpikes(wall, 1, level3, defaultx3, defaulty3);
				}

				// Level 4
				else if (gameState == 8) {
					checkSpikes(wall, 1, level4, defaultx4, defaulty4);

					// Level 5
				} else if (gameState == 9) {
					checkSpikes(wall, 1, level5, defaultx5, defaulty5);
				}
			}
			// rect collides from right side of the wall
			else if (left1 < right2 && right1 > right2 && right2 - left1 < bottom1 - top2
					&& right2 - left1 < bottom2 - top1) {
				// Level 1
				if (gameState == 5) {
					checkSpikes(wall, 2, level1, defaultx1, defaulty1);
				}

				// Level 2
				else if (gameState == 6) {
					checkSpikes(wall, 2, level2, defaultx2, defaulty2);
				}

				// Level 3
				else if (gameState == 7) {
					checkSpikes(wall, 2, level3, defaultx3, defaulty3);
				}

				// Level 4
				else if (gameState == 8) {
					checkSpikes(wall, 2, level4, defaultx4, defaulty4);

					// Level 5
				} else if (gameState == 9) {
					checkSpikes(wall, 2, level5, defaultx5, defaulty5);
				}
			}
			// rec collides from topside of the rectangle
			else if (bottom1 > top2 && top1 <= top2) {
				// Level 1
				if (gameState == 5) {
					checkSpikes(wall, 3, level1, defaultx1, defaulty1);
				}

				// Level 2
				else if (gameState == 6) {
					checkSpikes(wall, 3, level2, defaultx2, defaulty2);
				}

				// Level 3
				else if (gameState == 7) {
					checkSpikes(wall, 3, level3, defaultx3, defaulty3);
				}

				// Level 4
				else if (gameState == 8) {
					checkSpikes(wall, 3, level4, defaultx4, defaulty4);

					// Level 5
				} else if (gameState == 9) {
					checkSpikes(wall, 3, level5, defaultx5, defaulty5);
				}
			} else if (top1 < bottom2 && bottom1 > bottom2) {
				// rect collides from bottom side of the wall
				rect.y = wall.y + wall.height;
				MC.setY(wall.y + wall.height);
			}
		}

		// Checks if intersects with the strawberries
		else if (rect.intersects(sr1) && gameState == 5 && s11) {
			MC.setStraw();
			hasDouble = true;
			s11 = false;
		} else if (rect.intersects(sr2) && gameState == 6 && s22) {
			MC.setStraw();
			hasDouble = true;
			s22 = false;
		} else if (rect.intersects(sr3) && gameState == 7 && s33) {
			MC.setStraw();
			hasDouble = true;
			s33 = false;
		} else if (rect.intersects(sr4) && gameState == 8 && s44) {
			MC.setStraw();
			hasDouble = true;
			s44 = false;
		} else if (rect.intersects(sr5) && gameState == 9 && s55) {
			MC.setStraw();
			hasDouble = true;
			s55 = false;
		}
	}

	// Method name: checkSpikes
	// Description: Detects spike collisions
	// Parameters: Rectangle wall, int n (type of collision - top, right, left side), char [][] level, int defaultx, defaulty
	// Returns: void
	public void checkSpikes(Rectangle wall, int n, char [][] level, int defaultx, int defaulty) {
		if (n == 1) {
			if (level[(int) (wall.getY() / 50)][(int) (wall.getX() / 50)] == 'S' || level[(int) (wall.getY() / 50)][(int) (wall.getX() / 50)] == '5') {
				spikeReset(defaultx, defaulty);
			}
			else {
				rect.x = wall.x - rect.width;
				MC.setX(wall.x - rect.width);
			}
		}
		else if (n == 2) {
			if (wall.getX()/50-1 >= 0 && level[(int)(wall.getY()/50)][(int)(wall.getX()/50-1)] == 'S' || wall.getX()/50-1 >= 0 && level[(int)(wall.getY()/50)][(int)(wall.getX()/50-1)] == '5') {
				spikeReset(defaultx, defaulty);
			}
			else {
				rect.x = wall.x + wall.width;
				MC.setX(wall.x + wall.width);
			}
		}
		else if (n == 3) {
			if (level[(int) (wall.getY() / 50)][(int) (wall.getX() / 50)] == 'S' || level[(int) (wall.getY() / 50)][(int) (wall.getX() / 50)] == '5') {
				spikeReset(defaultx, defaulty);
			}
			// rect collides from top side of the wall
			else {
				airborne = false;
				hasDouble = true;
				yVel = 0;
				rect.y = wall.y - rect.height;
				MC.setY(wall.y - rect.height);
			}
		}
	}

	// Method name: spikeReset
	// Description: Resets the character back to starting x and y if dying by touching spikes
	// Parameters: int defaultx, defaulty
	// Returns: void
	public void spikeReset(int defaultx, int defaulty) {
		MC.setY(defaulty);
		MC.setX(defaultx);
		rect.x = defaultx;
		rect.y = defaulty;
		airborne = false;
		hasDouble = true;
		setStrawTrue();
		MC.subStraw();
	}

	// Method name: setStrawTrue()
	// Description: Sets strawberry on level back to true after dying
	// Parameters: n/a
	// Returns: void
	public void setStrawTrue() {
		if (gameState == 5) s11 = true;
		else if (gameState == 6) s22 = true;
		else if (gameState == 7) s33 = true;
		else if (gameState == 8) s44 = true;
		else if (gameState == 9) s55 = true;
	}

	// Method name: hasClimb
	// Description: Checks if character has climb
	// Parameters: n/a
	// Returns: void
	public boolean hasClimb() {
		if (MC.getStamina() == 0 || !hasDouble) {
			return false;
		} else {
			if ((MC.getX()+50)/50 <= 25 && (MC.getY()+50)/50 <= 17 && MC.getY()/50+1 <= 17 && MC.getY()/50-1 >= 0 && MC.getX()/50-1 >= 0) {
				int tempY;
				if (gameState == 5) {
					if (MC.getY() % 50 == 0
							|| MC.getY() % 50 != 0 && level1[(MC.getY() + 50) / 50 + 1][(MC.getX() + 50) / 50] == 'N') {
						tempY = MC.getY() / 50;
					} else {
						tempY = MC.getY() / 50 + 1;
					}
					if (MC.getX() / 50 - 1 >= 0)
						return (MC.getX() % 50 == 0 && level1[tempY][MC.getX() / 50 - 1] != 'N'
						&& level1[tempY][MC.getX() / 50 - 1] != 'S'
						|| level1[tempY][MC.getX() / 50 + 1] != 'N' && level1[tempY][MC.getX() / 50 + 1] != 'S');
					else
						return false;
				} else if (gameState == 6) {
					if (MC.getY() % 50 == 0
							|| MC.getY() % 50 != 0 && level2[(MC.getY() + 50) / 50 + 1][(MC.getX() + 50) / 50] == 'N') {
						tempY = MC.getY() / 50;
					} else {
						tempY = MC.getY() / 50 + 1;
					}
					if (MC.getX() / 50 - 1 >= 0)
						return (MC.getX() % 50 == 0 && level2[tempY][MC.getX() / 50 - 1] != 'N'
						&& level2[tempY][MC.getX() / 50 - 1] != 'S'
						|| level2[tempY][MC.getX() / 50 + 1] != 'N' && level2[tempY][MC.getX() / 50 + 1] != 'S');
					else
						return false;
				} else if (gameState == 7) {
					if (MC.getY() % 50 == 0
							|| MC.getY() % 50 != 0 && level3[(MC.getY() + 50) / 50 + 1][(MC.getX() + 50) / 50] == 'N') {
						tempY = MC.getY() / 50;
					} else {
						tempY = MC.getY() / 50 + 1;
					}
					if (MC.getX() / 50 - 1 >= 0)
						return (MC.getX() % 50 == 0 && level3[tempY][MC.getX() / 50 - 1] != 'N'
						|| level3[tempY][MC.getX() / 50 + 1] != 'N');
					else
						return false;
				} else if (gameState == 8) {
					if (MC.getY() % 50 == 0
							|| MC.getY() % 50 != 0 && level4[(MC.getY() + 50) / 50 + 1][(MC.getX() + 50) / 50] == 'N') {
						tempY = MC.getY() / 50;
					} else {
						tempY = MC.getY() / 50 + 1;
					}
					if (MC.getX() / 50 - 1 >= 0)
						return (MC.getX() % 50 == 0 && level4[tempY][MC.getX() / 50 - 1] != 'N'
						|| level4[tempY][MC.getX() / 50 + 1] != 'N');
					else
						return false;
				} else if (gameState == 9) {
					if (MC.getY() % 50 == 0
							|| MC.getY() % 50 != 0 && level5[(MC.getY() + 50) / 50 + 1][(MC.getX() + 50) / 50] == 'N') {
						tempY = MC.getY() / 50;
					} else {
						tempY = MC.getY() / 50 + 1;
					}
					if (MC.getX() / 50 - 1 >= 0)
						return (MC.getX() % 50 == 0 && level5[tempY][MC.getX() / 50 - 1] != 'N'
						|| level5[tempY][MC.getX() / 50 + 1] != 'N');
					else
						return false;
				} else
					return false;
			}
			else return false;
		}
	}

	// Method name: move
	// Description: Moves the character
	// Parameters: n/a
	// Returns: void
	void move() {
		// Regular movement
		if (left)
			xVel = -speed;
		else if (right)
			xVel = speed;
		else
			xVel = 0;

		if (climbUp && hasClimb())
			yVel = speed / 2;
		else if (climbDown && hasClimb())
			yVel = -speed / 2;
		else if (climbing && hasClimb())
			yVel = 0;
		else {
			// Jump
			if (airborne && doubleJump) {
				yVel = jumpSpeed;
				doubleJump = false;
			} else if (airborne) {
				yVel -= gravity;
			} else {
				yVel = 0;
				if (jump) {
					airborne = true;
					doubleJump = true;
					yVel = jumpSpeed;
				}
			}
		}
		MC.setX(MC.getX() + xVel);
		MC.setY(MC.getY() - yVel);
		rect.x += xVel;
		rect.y -= yVel;
	}

	// Method name: run
	// Description: Calls method when game is called
	// Parameters: n/a
	// Returns: void
	public void run() {
		while (true) {
			//			System.out.println(MC.getX() + " " + MC.getY());
			move();
			// X bounds
			if (MC.getX() < 0) {
				MC.setX(0);
				rect.x = 0;
			} else if (MC.getX() >= 1250) {
				MC.setX(1250);
				rect.x = 1250;
			}
			if (airborne == false)
				MC.setStamina(100);
			else if (climbing && hasClimb() && !climbUp && !climbDown)
				MC.depleteStamina();
			if (gameState == 5) {
				if (MC.getY() < 25) {
					gameState = 6;
					MC.setY(defaulty2);
					MC.setX(defaultx2);
				}
				for (Rectangle r : walls1)
					keepBounds(r);
			}
			if (gameState == 6) {
				if (MC.getY() < 25) {
					gameState = 7;
					MC.setY(defaulty4);
					MC.setX(defaultx4);
				}
				for (Rectangle r : walls2) {
					keepBounds(r);
				}
			}
			if (gameState == 7) {
				if (MC.getY() <= 200 && MC.getX() <= 150 || MC.getY() <= 650 && MC.getX() >= 1250) {
					MC.setY(defaulty3);
					MC.setX(defaultx3);
				}
				// Y bounds
				if (MC.getY() >= 850) {
					MC.setY(defaulty3);
					MC.setX(defaultx3);
				}
				if (MC.getY() < 25) {
					gameState = 8;
					MC.setY(defaulty4);
					MC.setX(defaultx4);
				}
				for (Rectangle r : walls3) {
					keepBounds(r);
				}
			}
			if (gameState == 8) {
				if (MC.getX() >= 1225) {
					MC.setX(1225);
				}
				if (MC.getY() <= 100 && MC.getX() <= 350 || MC.getY() <= 100 && MC.getX() >= 700) {
					MC.setY(defaulty4);
					MC.setX(defaultx4);
				}
				if (MC.getY() < 25) {
					gameState = 9;
					MC.setY(defaulty5); // change
					MC.setX(defaultx5);
				}
				for (Rectangle r : walls4) {
					keepBounds(r);
				}
			}
			if (gameState == 9) {
				if (MC.getY() <= 50 && MC.getX() >= 600 || MC.getX() <= 200 && MC.getY() <= 400) {
					MC.setY(defaulty5);
					MC.setX(defaultx5);
				}
				if (MC.getY() < 25) {
					gameState = 12;
					end = ((int) System.currentTimeMillis() / 1000) - startTimeSeconds;
					String time = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(cal.getTime());
					System.out.println(end);
					System.out.println(time);
					if (100 - end >= 0)
						score = new Leaderboard(time, (100 - end) + (50 * MC.countStraw));
					else
						score = new Leaderboard(time, (50 * MC.countStraw));
					try {
						System.out.println(true);
						PrintWriter output = new PrintWriter(new FileWriter("highscore.txt", true));
						output.println(score.getDate() + "`" + score.getScore());
						output.close();
					} catch (IOException e) {
					}
				}
				for (Rectangle r : walls5) {
					keepBounds(r);
				}
			}
			this.repaint();
			try {
				Thread.sleep(1000 / FPS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Method name: playMusic
	// Description: Plays a music file
	// Parameters: URL
	// Returns: void
	public void playMusic(URL url) {
		sound.setFile(url);
		sound.play();
		sound.loop();
	}

	// ------- MAIN METHOD -------
	public static void main(String[] args) {
		// Highscore time
		int score = 0;
		String date = "";
		// Import leaderboard stats
		try {
			BufferedReader in = new BufferedReader(new FileReader("highscore.txt"));
			String line = "";
			while ((line = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "`");
				while (st.hasMoreTokens()) {
					date = st.nextToken();
					score = Integer.parseInt(st.nextToken());
				}
				highScore.put(date, score);
			}
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		Collection<Integer> scores = highScore.values();
		Iterator<String> dates = highScore.keySet().iterator();

		// Adds the keyset and value collection into linked list of word objects
		for (Integer i : scores) {
			sorted.add(new Leaderboard(dates.next(), i));
		}
		Collections.sort(sorted);

		// Import array levels from textfiles
		for (int i = 1; i < 6; i++) {
			char[][] level = new char[18][26];
			try {
				String file = "levels/level" + i + ".txt";
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line = "";
				for (int y = 0; y < 18; y++) {
					line = in.readLine();
					for (int x = 0; x < 26; x++) {
						level[y][x] = line.charAt(x);
					}
				}
				levels.add(level);
				in.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}

		level1 = levels.remove();
		level2 = levels.remove();
		level3 = levels.remove();
		level4 = levels.remove();
		level5 = levels.remove();

		// Adding rectangle
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 18; j++) {
				if (level1[j][i] != 'N') {
					walls1.add(new Rectangle(i * 50, j * 50, 50, 50));
				}
			}
		}
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 18; j++) {
				if (level2[j][i] != 'N') {
					walls2.add(new Rectangle(i * 50, j * 50, 50, 50));
				}
			}
		}
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 18; j++) {
				if (level3[j][i] != 'N') {
					walls3.add(new Rectangle(i * 50, j * 50, 50, 50));
				}
			}
		}
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 18; j++) {
				if (level4[j][i] != 'N') {
					walls4.add(new Rectangle(i * 50, j * 50, 50, 50));
				}
			}
		}
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 18; j++) {
				if (level5[j][i] != 'N') {
					walls5.add(new Rectangle(i * 50, j * 50, 50, 50));
				}
			}
		}
		frame = new JFrame("Celeste");
		testDriver panel = new testDriver();
		Image icon = Toolkit.getDefaultToolkit().getImage("icon/icon.png");
		frame.setIconImage(icon);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}