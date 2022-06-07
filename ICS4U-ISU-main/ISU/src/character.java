// Assignment: ISU
// Name: April Wei, Tyler Zeng
// Date: Jan 25, 2022
// Description: Instantiates the character object
public class character {
	private boolean idle;
	private int stamina;

	private int x = 50 * 2;
	private int y = 50 * 14;
	int countStraw = 0;

	// Method name: character
	// Description: Constructor which creates character object
	// Parameters: n/a
	// Returns: n/a
	public character() {
		stamina = 100;
		idle = true;
	}

	// Getters and setters
	public boolean getIdle() {
		return idle;
	}

	public int getStamina() {
		return stamina;
	}

	public String getStaw() {
		return Integer.toString(countStraw);
	}

	public void setIdle(boolean b) {
		idle = b;
	}

	public void setStamina(int n) {
		stamina = n;
	}

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

	public void setStraw() {
		countStraw++;
	}

	public void resetStraw() {
		countStraw = 0;
	}

	// Method name: subStraw
	// Description: subtracts 1 from strawberry count
	// Parameters: n/a
	// Returns: n/a
	public void subStraw() {
		if (countStraw > 0) {
			countStraw--;
		}
	}

	// Method name: depleteStamina
	// Description: reduces the character's stamina
	// Parameters: n/a
	// Returns: n/a
	public void depleteStamina() {
		if (stamina > 0) {
			stamina -= 10;
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}