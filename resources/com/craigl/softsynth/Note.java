package com.craigl.softsynth;

/**
 * Simple object representing a note in a musical tune.
 * <p>
 * A Note object contains a midi note number and<br>
 * a duration tag where:
 * <p> 
 * 1 is whole note; 2 is half note; 4 is quarter note; 8 is eighth note<br>
 * 16 is sixteenth note; 32 is thirty second note.
 * 
 * @author craiglindley
 */

public class Note {

	/**
	 * Note Class Constructor
	 * 
	 * @param mnn Midi note number of note
	 * @param dt Duration tag of note
	 */
	public Note(int mnn, int dt) {

		midiNoteNumber = mnn;
		durationTag = dt;
	}

	// Instance data
	public int midiNoteNumber;
	public int durationTag;
}
