package com.craigl.softsynth;

/**
 * Delay Effect
 * <p>
 * Variable delay between 1 and 2000 milliseconds
 * 
 * @author craiglindley
 */

public class DelayEffect implements SampleProviderIntfc {

	public static final double DELAY_DRYWETMIXPERCENT_MIN     =   0.0;
	public static final double DELAY_DRYWETMIXPERCENT_MAX     = 100.0;	
	public static final double DELAY_DRYWETMIXPERCENT_DEFAULT =  50.0;	

	public static final int DELAY_DELAY_MIN     =    1;
	public static final int DELAY_DELAY_MAX     = 2000;	
	public static final int DELAY_DELAY_DEFAULT =   50;	

	public static final double DELAY_FEEDBACKPERCENT_MIN     =   0.0;
	public static final double DELAY_FEEDBACKPERCENT_MAX     = 100.0;	
	public static final double DELAY_FEEDBACKPERCENT_DEFAULT =  10.0;
	
	private static final int DELAY_BUFFER_SIZE = SamplePlayer.SAMPLE_RATE * 3;

	/**
	 * Delay Effect Class Constructor
	 * <p>
	 * Creates a DelayEffect instance setup with default values
	 * <p>
	 * DelayEffect is initially bypassed
	 */	
	public DelayEffect() {
		
		readIndex = 0;
		writeIndex = 0;
		
		// Parameterize effect
		setBypassed(true);
		setDryWetMixPercent(DELAY_DRYWETMIXPERCENT_DEFAULT);
		setDelayInMs(DELAY_DELAY_DEFAULT);
		setFeedbackPercent(DELAY_FEEDBACKPERCENT_DEFAULT);
	}


	/**
	 * Sets the state of the DelayEffect
	 * 
	 * @param bypassed If true the effect is bypassed; if false the effect is operational
	 */
	public void setBypassed(boolean bypassed) {
		
		this.bypassed = bypassed;
	}

	/**
	 * Sets the dry / wet mix for the DelayEffect
	 * 
	 * @param dryWetMixPercent The percentage of wet signal in the mix
	 */
	public void setDryWetMixPercent(double dryWetMixPercent) {
		
		dryWetMixPercent = (dryWetMixPercent < DELAY_DRYWETMIXPERCENT_MIN) ? DELAY_DRYWETMIXPERCENT_MIN : dryWetMixPercent;
		dryWetMixPercent = (dryWetMixPercent > DELAY_DRYWETMIXPERCENT_MAX) ? DELAY_DRYWETMIXPERCENT_MAX : dryWetMixPercent;

		this.dryWetMixPercent = dryWetMixPercent;
	}

	/**
	 * Sets the delay time for the DelayEffect
	 * <p>
	 * Valid values between 1 and 2000 milliseconds
	 * 
	 * @param delayInMs Delay time in milliseconds
	 */
	public void setDelayInMs(int delayInMs) {

		delayInMs = (delayInMs < DELAY_DELAY_MIN) ? DELAY_DELAY_MIN : delayInMs;
		delayInMs = (delayInMs > DELAY_DELAY_MAX) ? DELAY_DELAY_MAX : delayInMs;

		int delayInSamples = (int) (0.001 * delayInMs * SamplePlayer.SAMPLE_RATE);
		
		readIndex = writeIndex - delayInSamples;
		if (readIndex < 0) {
			readIndex += DELAY_BUFFER_SIZE;
		}
	}

	/**
	 * Sets the feedback level which is the amount of output feedback to the input.
	 * 
	 * @param feedbackPercent The percentage of the output feedback to the input
	 */
	public void setFeedbackPercent(double feedbackPercent) {

		feedbackPercent = (feedbackPercent < DELAY_FEEDBACKPERCENT_MIN) ? DELAY_FEEDBACKPERCENT_MIN : feedbackPercent;
		feedbackPercent = (feedbackPercent > DELAY_FEEDBACKPERCENT_MAX) ? DELAY_FEEDBACKPERCENT_MAX : feedbackPercent;

		this.feedbackPercent = feedbackPercent;
	}

	/**
	 * Setup the provider of samples
	 * 
	 * @param provider The provider of samples for this DelayEffect
	 */
	public void setSampleProvider(SampleProviderIntfc provider) {
		this.provider = provider;
	}

	/**
	 * Process a single sample through the effect.
	 * 
	 * @param inputSample The input sample to process
	 * 
	 * @return Processed sample including wet and dry signal
	 */
	private short processSample(short inputSample) {
		
		if (bypassed) {
			return inputSample;
		}
		
		short delayedSample = delayBuffer[readIndex++];

		double dryLevel = ((100.0 - dryWetMixPercent) * inputSample) / 100.0;
		double wetLevel = (dryWetMixPercent * delayedSample) / 100.0;
		
		short outputSample = (short) (dryLevel + wetLevel);

/*
  		if (outputSample > Short.MAX_VALUE) {
 
			outputSample = Short.MAX_VALUE;
			System.out.println("outputSample max");
		}	else if (outputSample < Short.MIN_VALUE) {
			outputSample = Short.MIN_VALUE;
			System.out.println("outputSample min");
		}
*/		
		inputSample += (delayedSample * feedbackPercent) / 100.0;
		
/*
 		if (inputSample > Short.MAX_VALUE) {
			inputSample = Short.MAX_VALUE;
			System.out.println("inputSample max");
		}	else if (inputSample < Short.MIN_VALUE) {
			inputSample = Short.MIN_VALUE;
			System.out.println("inputSample min");
		}
*/
		delayBuffer[writeIndex++] = inputSample;
				
		// Update indices
		readIndex  %= DELAY_BUFFER_SIZE;
		writeIndex %= DELAY_BUFFER_SIZE;
		
		return outputSample;		
	}

	/**
	 * Process a buffer full of samples pulled from the sample provider
	 * 
	 * @param buffer Buffer in which the samples are to be processed
	 * 
	 * @return Count of number of bytes processed
	 */
	public int getSamples(byte [] buffer) {

		// Grab samples to manipulate from this modules sample provider
		provider.getSamples(buffer);
		
		int index = 0;
		for (int i = 0; i < SamplePlayer.SAMPLES_PER_BUFFER; i++) {
			// Get a sample to process
			byte b2 = buffer[index];
			byte b1 = buffer[index+1];

			// Convert bytes into short sample
			short s = (short)((((int) b2) << 8) + b1);
			
			// Process the sample
			s = processSample(s);
			
			// Store the processed sample
			buffer[index++] = (byte)(s >> 8);
			buffer[index++] = (byte)(s & 0xFF);			
		}
		return SamplePlayer.BUFFER_SIZE;
	}

	// Instance data
    private boolean bypassed;
    private double dryWetMixPercent;   
	private double feedbackPercent;
	private int readIndex;
	private int writeIndex;
	
	private short [] delayBuffer = new short [DELAY_BUFFER_SIZE];
	
	private SampleProviderIntfc provider;
}
