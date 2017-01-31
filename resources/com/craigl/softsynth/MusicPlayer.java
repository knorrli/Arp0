package com.craigl.softsynth;

/**
 * This class plays simple tunes specified in terms of an array of Note objects.
 * <p>
 * A MusicPlayer object manipulates the oscillator frequency and sends<br>
 * noteOn and noteOff events to both the associated VCA and VCF<br>
 * as required to play the specified tune.
 * <p>
 * The timing for the tune is derived from the sample stream. See text for details.
 * 
 * @author craiglindley
 */
public class MusicPlayer implements SampleProviderIntfc {
		
	private static final double THIRTY_SECOND_NOTE_DURATION_IN_SECS = 0.08;
	private static final int REFERENCE_NOTE_NUMBER = 69;
	private static final int REFERENCE_NOTE_FREQ = 440;	
	private static final int NOTES_PER_OCTAVE = 12;		
		
	/**
	 * MusicPlayer Class Constructor
	 * <p>
	 * Creates and initializes a MusicPlayer instance.
	 * 
	 * @param osc Reference to the oscillator instance used to play the tune
	 * @param vca Reference to the VCA that is controlling note sound durations; if any.
	 * @param vcf Reference to the VCF that is altering the tune's sound; if any.
	 * @param notes Array of Note objects containing the tune to play
	 */
	public MusicPlayer(BasicOscillator osc, VCA vca, VCF vcf, Note [] notes) {
		
		// Save incoming
		this.osc = osc;
		this.vca = vca;
		this.vcf = vcf;
		this.notes = notes;	
		
		loopCount = 1;
	}
	
	/**
	 * Set the number of times the tune plays before completing.
	 * 
	 * @param loopCount Number of times to play the tune.
	 */
	public void setLoopCount(int loopCount) {
		
		this.loopCount = loopCount;
	}
	
	/**
	 * Method called to play the tune.
	 * <p>
	 * This method can be called over and over to play the tune repeatedly.
	 */
	public void playSong() {

		int notesInSong = notes.length;

		do {
			for (int n = 0; n < notesInSong; n++) {
				// Get note from song
				Note note = notes[n];

				// Get midi note number for note
				int mnn = note.midiNoteNumber;

				// A mnn of 0 is a rest so no need to set osc frequency
				if (mnn != 0) {
					// Convert to real frequency
					double frequency = midiNoteNumberToFrequency(mnn);

					// Inform osc
					osc.setFrequency(frequency);
				}
				// Get duration indication
				int durationTics = getNoteDurationInTics(note.durationTag);

				// Play the note if it is not a rest
				if (mnn != 0) {
					if (vca != null) {
						vca.noteOn();
					}
					if (vcf != null) {
						vcf.noteOn();
					}
				}
				// Delay for the duration of note or rest
				delay(durationTics);

				if (vca != null) {
					vca.noteOff();
				}
				if (vcf != null) {
					vcf.noteOff();
				}
			}

		} while (--loopCount > 0);
	}

	/**
	 * Normally components process the samples in the buffer they are passed<br>
	 * but here, only a count of the number of times this method has been called<br>
	 * is processed. The samples returned from this method are from the<br>
	 * the previous sample provider in the signal chain.
	 * 
	 * @param buffer Buffer in which the samples are to be processed
	 * 
	 * @return Count of number of bytes processed
	 */
	public int getSamples(byte [] buffer) {

		// Decrement the tic count
		ticCount--;
		
		// Return the samples from the previous provider in the chain
		return provider.getSamples(buffer);
	}
	
	/**
	 * Delay function that monitors the ticCount and returns when it reaches zero.
	 * 
	 * @param tics Count of tic (or buffer refills) to delay
	 */
	private void delay(int tics) {

		ticCount = tics;

		while (ticCount > 0) {
			try {
				Thread.sleep(2);
			} catch(Exception e) {
				// Do nothing
			}
		}
	}

	/**
	 * Method that converts midi note numbers to actual frequency in Hz
	 * <p>
	 * See text for details.
	 * 
	 * @param mnn Midi note number to resolve
	 * 
	 * @return Frequency in Hz which corresponds to the mnn.
	 */
	private double midiNoteNumberToFrequency(int mnn) {		  
		
		// Convert a midi note number to a frequency in Hz
		double soundOffset = (mnn - REFERENCE_NOTE_NUMBER) / (double) NOTES_PER_OCTAVE;
		return REFERENCE_NOTE_FREQ * Math.pow(2.0, soundOffset);
	}
	
	/**
	 * Calculate the duration of a note in terms of tics (buffer refills).
	 * 
	 * @param durationTag Tag from note indicting the duration.
	 * <p>
	 * 1 is whole note; 2 is half note; 4 is quarter note; 8 is eighth note<br>
	 * 16 is sixteenth note; 32 is thirty second note.
	 * 
	 * @return Tic count corresponding to note duration
	 */
	private int getNoteDurationInTics(int durationTag) {
		
		double seconds;
		
		switch(durationTag) {
		case 1:
			seconds = 32 * THIRTY_SECOND_NOTE_DURATION_IN_SECS;
			break;
		case 2:
			seconds = 16 * THIRTY_SECOND_NOTE_DURATION_IN_SECS;
			break;
		default:
		case 4:
			seconds = 8 * THIRTY_SECOND_NOTE_DURATION_IN_SECS;
			break;
		case 8:
			seconds = 4 * THIRTY_SECOND_NOTE_DURATION_IN_SECS;
			break;
		case 16:
			seconds = 2 * THIRTY_SECOND_NOTE_DURATION_IN_SECS;
			break;
		case 32:
			seconds = THIRTY_SECOND_NOTE_DURATION_IN_SECS;
			break;
		}
		return (int) Math.round(seconds / SamplePlayer.BUFFER_TIME_IN_SECS);
	}
	
	/**
	 * Setup the provider of samples
	 * 
	 * @param provider The provider of samples for this MusicPlayer
	 */
	public void setSampleProvider(SampleProviderIntfc provider) {
		this.provider = provider;
	}

	// Instance data
	private BasicOscillator osc;
	private VCA vca;
	private VCF vcf;
	private Note [] notes;
	private int loopCount;
	private int ticCount;
	private SampleProviderIntfc provider;
}
