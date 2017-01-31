package com.craigl.softsynth;

import java.util.Arrays;
import javax.sound.sampled.*;

/**
 * Sample Player Class based on the javax.sound.sampled package.
 * <p>
 * Instances of this class pull samples from sample providers<br>
 * and writes them to the hardware so that they can be heard.
 * <p>
 * Various important constants are defined in this class as well.
 * <p>
 * This implementation feeds zeroed samples to the sound engine<br>
 * during initialization to prevent glitches that occur until<br>
 * engine is fully initialized.
 * <p>
 * See text for details.
 * 
 * @author craiglindley
 */

public class SamplePlayer extends Thread implements SampleProviderIntfc  {
	
	// Count of zeroed buffers to return before switching to real sample provider
	private static final int TEMP_BUFFER_COUNT = 20;
	
	// AudioFormat parameters
	public  static final int     SAMPLE_RATE = 22050;
	private static final int     SAMPLE_SIZE = 16;
	private static final int     CHANNELS = 1;
	private static final boolean SIGNED = true;
	private static final boolean BIG_ENDIAN = true;

	// Chunk of audio processed at one time
	public static final int BUFFER_SIZE = 1000;
	public static final int SAMPLES_PER_BUFFER = BUFFER_SIZE / 2;

	// Sample time values
	public static final double SAMPLE_TIME_IN_SECS = 1.0 / SAMPLE_RATE;
	public static final double BUFFER_TIME_IN_SECS = SAMPLE_TIME_IN_SECS * SAMPLES_PER_BUFFER;

	/**
	 * SamplePlayer Class Constructor
	 */
	public SamplePlayer() {
		
		// Create the audio format we wish to use
		format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, SIGNED, BIG_ENDIAN);

		// Create dataline info object describing line format
		info = new DataLine.Info(SourceDataLine.class, format);
		
		// Clear buffer initially
		Arrays.fill(sampleData, (byte) 0);
		
		// Set temp provider so zeroed buffers are consumed initially
		provider = this;
	}
	
	/**
	 * Process a buffer full of samples pulled from the sample provider
	 * <p>
	 * NOTE: this class acts as a sample provider to itself during<br>
	 * the initialization of the sound engine. Once bufferCount of buffers<br>
	 * has been processed, SamplePlayer switches to its real sample provider.<br>
	 * This is necessary to prevent glitches on startup.
	 * 
	 * @param buffer Buffer in which the samples are to be processed
	 * 
	 * @return Count of number of bytes processed
	 */
	public int getSamples(byte [] buffer) {

		bufferCount++;
		if (bufferCount >= TEMP_BUFFER_COUNT) {
			// Audio system flushed so switch to real sample provider
			provider = realProvider;
		}
		return BUFFER_SIZE;		
	}
	
	/**
	 * Start the SamplePlayer thread.
	 * <p>
	 * This thread will continue to run until either<br>
	 * the done flag gets set or the sample provider runs<br>
	 * out of samples. 
	 * <p>
	 * NOTE: once the thread ends it cannot be restarted.
	 */ 
	public void run() {

		done = false;
		int nBytesRead = 0;

		try {
			// Get line to write data to
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
			auline.start();

			while ((nBytesRead != -1) && (! done)) {
				nBytesRead = provider.getSamples(sampleData);
				if (nBytesRead > 0) {
					auline.write(sampleData, 0, nBytesRead);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();				
		} finally {
			auline.drain();
			auline.close();							
		}
	}		
	
	/**
	 * Method to start the sample player
	 */ 
	public void startPlayer() {
		
		if (hasRun) {
			System.err.println("Illegal to restart a Thread once it has been stopped");
			return;
		}
		if (realProvider != null) {
			// Indicate this thread has run 
			hasRun = true;

			// Starter up
			start();	
		}
	}
	
	/**
	 * Method to stop the sample player
	 */ 
	public void stopPlayer() {
		done = true;
	}
	
	/**
	 * Setup the real provider of samples
	 * 
	 * @param provider The provider of samples for the SamplePlayer.
	 */
	public void setSampleProvider(SampleProviderIntfc provider) {
		realProvider = provider;
	}
	
	// Instance data
	private AudioFormat format;
	private DataLine.Info info;
	private SourceDataLine auline;
	private boolean hasRun;
	private boolean done;
	private int bufferCount;
	private byte [] sampleData = new byte[BUFFER_SIZE];
	private SampleProviderIntfc provider;
	private SampleProviderIntfc realProvider;
}
