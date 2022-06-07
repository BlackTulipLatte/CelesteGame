// Assignment: ISU
// Name: April Wei, Tyler Zeng
// Date: Jan 25, 2022
// Description: Is the leaderboard object for the game. Contains the date at which the score was acheived and the value of the score
import java.util.*;

public class Leaderboard implements Comparable<Leaderboard> {

	// Variables
	private String date;
	private int score;

	// Method name: Leaderboard
	// Description: Constructor which creates leaderboard object
	// Parameters: n/a
	// Returns: n/a
	public Leaderboard(String date, int score) {
		this.date = date;
		this.score = score;
	}

	// Getter
	public String getDate() {
		return date;
	}

	public int getScore() {
		return score;
	}

	// Method name: equals
	// Description: Helps the hashCode method discern if the object is equal or not
	// Parameters: word object
	// Returns: boolean
	public boolean equals(Leaderboard a) {
		return a.score == score;
	}

	// Method name: compareTo
	// Description: Helps differentiate/sort between word objects
	// Parameters: word object
	// Returns: int
	public int compareTo(Leaderboard a) {
		return a.score - score;
	}

	// Method name: toString
	// Description: Prints the date and score
	// Parameters: n/a
	// Returns: String
	public String toString() {
		return date + " " + score;
	}

}
