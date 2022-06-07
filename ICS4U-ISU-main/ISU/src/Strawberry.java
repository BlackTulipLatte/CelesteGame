// Assignment: ISU
// Name: April Wei, Tyler Zeng
// Date: Jan 25, 2022
// Description: Is the strawberry object for the game. Contains the date at which the score was acheived and the value of the score

public class Strawberry {

	private int x;
	private int y;

	// Method name: Strawberry
	// Description: Constructor which creates character object
	// Parameters: n/a
	// Returns: n/a
    public Strawberry(int x, int y) {
    	this.x = x;
    	this.y = y;
    }

    // Getters
    public int getX() {
    	return x;
    }
    public int getY() {
    	return y;
    } 
}