// Assignment: ISU
// Name: April Wei, Tyler Zeng
// Date: Jan 25, 2022
// Description: Is the sound object for the game. Helps manage the functionality of the sound
import java.io.File;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

	Clip clip;
	float previousVolume = 0;
	float currentVolume = 0;
	FloatControl fc;
	boolean mute;

	// Method name: setFile
	// Description: Sets the file location of the song
	// Parameters: URL
	// Returns: void
	public void setFile(URL url) {
		try {
			AudioInputStream sound = AudioSystem.getAudioInputStream(new File("music.wav"));
			clip = AudioSystem.getClip();
			clip.open(sound);
			fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {
		}
	}

	// Method name: play
	// Description: Plays the sound file from the beginning
	// Parameters: n/a
	// Returns: n/a
	public void play() {
		clip.setFramePosition(0);
		clip.start();
	}

	// Method name: loop
	// Description: Loops the music file
	// Parameters: n/a
	// Returns: n/a
	public void loop() {
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	// Method name: stop
	// Description: Stops the music from playing
	// Parameters: n/a
	// Returns: n/a
	public void stop() {
		clip.stop();
	}

	// Method name: volumeUp()
	// Description: Increases the music file volume
	// Parameters: n/a
	// Returns: n/a
	public void volumeUp() {
		currentVolume += 5.0f;
		if (currentVolume > 6.0f) {
			currentVolume = 6.0f;
		}
		fc.setValue(currentVolume);
	}

	// Method name: volumeDown()
	// Description: Decreases the music file volume
	// Parameters: n/a
	// Returns: n/a
	public void volumeDown() {
		currentVolume -= 5.0f;
		if (currentVolume < -80.0f) {
			currentVolume = -80.0f;
		}
		fc.setValue(currentVolume);
	}

	// Method name: volumeMute()
	// Description: Mutes the music volume
	// Parameters: n/a
	// Returns: n/a
	public void volumeMute() {
		if (mute == false) {
			previousVolume = currentVolume;
			currentVolume = -80.0f;
			fc.setValue(currentVolume);
			mute = true;
		} else if (mute == true) {
			currentVolume = previousVolume;
			fc.setValue(currentVolume);
			mute = false;
		}
	}

}
