package com.craigl.softsynth;

/**
 * Phaser Effect
 * <p>
 * Programmable effect that emulates an analog guitar phaser.
 * <p>
 * See text for details.
 * 
 * @author craiglindley
 */

public class PhaserEffect implements SampleProviderIntfc {
	
	public static final double PHASER_DRYWETMIXPERCENT_MIN     =   0.0;
	public static final double PHASER_DRYWETMIXPERCENT_MAX     = 100.0;	
	public static final double PHASER_DRYWETMIXPERCENT_DEFAULT =  50.0;	

	public static final double PHASER_SWEEPRATE_MIN_HZ     = 0.2;
	public static final double PHASER_SWEEPRATE_MAX_HZ     = 5.0;	
	public static final double PHASER_SWEEPRATE_HZ_DEFAULT = 0.5;
	
	public static final int PHASER_SWEEPRANGE_MIN     = 1;
	public static final int PHASER_SWEEPRANGE_MAX     = 7; 
	public static final int PHASER_SWEEPRANGE_DEFAULT = 5;	

	public static final double PHASER_FEEDBACKPERCENT_MIN     =   0.0;
	public static final double PHASER_FEEDBACKPERCENT_MAX     = 100.0;	
	public static final double PHASER_FEEDBACKPERCENT_DEFAULT =  10.0;
	
	private static final double PHASER_BASE_FREQUENCY = 100.0;
	
	/**
	 * Phaser Effect Class Constructor
	 * <p>
	 * Creates a PhaserEffect instance setup with default values
	 * <p>
	 * PhaserEffect is initially bypassed
	 */
	public PhaserEffect() {

		// Parameterize effect
		setBypassed(true);
		setDryWetMixPercent(PHASER_DRYWETMIXPERCENT_DEFAULT);
		setSweepRate(PHASER_SWEEPRATE_HZ_DEFAULT);
		setSweepRangeInOctaves(PHASER_SWEEPRANGE_DEFAULT);
		setFeedbackPercent(PHASER_FEEDBACKPERCENT_DEFAULT);
	}

	/**
	 * Sets the state of the PhaserEffect
	 * 
	 * @param bypassed If true the effect is bypassed; if false the effect is operational
	 */ 
	public void setBypassed(boolean bypassed) {
		
		this.bypassed = bypassed;
	}

	/**
	 * Sets the dry / wet mix for the PhaserEffect
	 * 
	 * @param dryWetMixPercent The percentage of wet signal in the mix
	 */
	public void setDryWetMixPercent(double dryWetMixPercent) {
		
		dryWetMixPercent = (dryWetMixPercent < PHASER_DRYWETMIXPERCENT_MIN) ? PHASER_DRYWETMIXPERCENT_MIN : dryWetMixPercent;
		dryWetMixPercent = (dryWetMixPercent > PHASER_DRYWETMIXPERCENT_MAX) ? PHASER_DRYWETMIXPERCENT_MAX : dryWetMixPercent;

		this.dryWetMixPercent = dryWetMixPercent;
	}

	/**
	 * Sets the sweep rate
	 * <p>
	 * Sets the rate at which the sweep effect is heard in the processed audio
	 * <p>
	 * Sweep rate frequency must be between PHASER_SWEEPRATE_MIN_HZ and PHASER_SWEEPRATE_MAX_HZ
	 * 
	 * @param sweepRate The rate of frequency sweep in Hz.
	 */
	public void setSweepRate(double sweepRate) {

		sweepRate = (sweepRate < PHASER_SWEEPRATE_MIN_HZ) ? PHASER_SWEEPRATE_MIN_HZ : sweepRate;
		sweepRate = (sweepRate > PHASER_SWEEPRATE_MAX_HZ) ? PHASER_SWEEPRATE_MAX_HZ : sweepRate;

		this.sweepRate = sweepRate;		
		initialize();	
	}

	/**
	 * Sets the sweep range
	 * <p>
	 * Sets the range of the sweep effect heard in the processed audio
	 * <p>
	 * Sweep range must be between PHASER_SWEEPRANGE_MIN and PHASER_SWEEPRANGE_MAX
	 * 
	 * @param sweepRangeInOctaves The range of frequency sweep in octaves.
	 */
	public void setSweepRangeInOctaves(int sweepRangeInOctaves) {

		sweepRangeInOctaves = (sweepRangeInOctaves < PHASER_SWEEPRANGE_MIN) ? PHASER_SWEEPRANGE_MIN : sweepRangeInOctaves;
		sweepRangeInOctaves = (sweepRangeInOctaves > PHASER_SWEEPRANGE_MAX) ? PHASER_SWEEPRANGE_MAX : sweepRangeInOctaves;

		this.sweepRangeInOctaves = sweepRangeInOctaves;		
		initialize();
	}

	/**
	 * Sets the feedback level which is the amount of output feedback to the input.
	 * 
	 * @param feedbackPercent The percentage of the output feedback to the input
	 */
	public void setFeedbackPercent(double feedbackPercent) {

		feedbackPercent = (feedbackPercent < PHASER_FEEDBACKPERCENT_MIN) ? PHASER_FEEDBACKPERCENT_MIN : feedbackPercent;
		feedbackPercent = (feedbackPercent > PHASER_FEEDBACKPERCENT_MAX) ? PHASER_FEEDBACKPERCENT_MAX : feedbackPercent;

		this.feedbackPercent = feedbackPercent;
	}

	/**
	 * Setup the provider of samples
	 * 
	 * @param provider The provider of samples for this PhaserEffect
	 */
	public void setSampleProvider(SampleProviderIntfc provider) {
		this.provider = provider;
	}

	/**
	 * Perform initialization calculation based upon sweep rate and range.
	 */
	private void initialize() {
		
		wp = minWp = (2.0 * Math.PI * PHASER_BASE_FREQUENCY) / SamplePlayer.SAMPLE_RATE;
		
		// Convert octave range to freq range
		double freqRange = Math.pow(2.0, sweepRangeInOctaves);
		
		maxWp = minWp * freqRange;
		
		currentStep = step = Math.pow(freqRange, sweepRate / (SamplePlayer.SAMPLE_RATE / 2.0));
	}

	/**
	 * Process a single sample through effect.
	 * 
	 * @param shortSample The input sample to process
	 * 
	 * @return Processed sample including wet and dry signal
	 */
	public short processSample(short shortSample) {
				
		if (bypassed) {
			return shortSample;
		}

		double sample = ((double) shortSample) / Short.MAX_VALUE;

		// Calculate A in difference equation
		double A = (1.0 - wp) / (1.0 + wp);
		
		double inSample = sample + ((feedbackPercent * thisOut4) / 100.0);
		
		// Do the first allpass filter
		thisOut1 = A * (inSample + thisOut1) - prevIn1;
		prevIn1 = inSample;
		
		// Do the second allpass filter
		thisOut2 = A * (thisOut1 + thisOut2) - prevIn2;
		prevIn2 = thisOut1;
		
		// Do the third allpass filter
		thisOut3 = A * (thisOut2 + thisOut3) - prevIn3;
		prevIn3 = thisOut2;
		
		// Do the forth allpass filter
		thisOut4 = A * (thisOut3 + thisOut4) - prevIn4;
		prevIn4 = thisOut3;
		
		// Calculate wet and dry contributions
		double dryLevel = ((100.0 - dryWetMixPercent) * sample) / 100.0;
		double wetLevel = (dryWetMixPercent * thisOut4) / 100.0;
		double outSample = dryLevel + wetLevel;
		
		if (outSample > 1.0) {
			outSample = 1.0;
		}	else if (outSample < -1.0)	{
			outSample = -1.0;
		}
					
		// Update sweep
		wp *= currentStep;		// Apply step value
		
		if(wp > maxWp) {		// Exceed max Wp ?
			currentStep = 1.0 / step;
		}	else if (wp < minWp) {	// Exceed min Wp ?
			currentStep = step;
		}
		return (short)(outSample * Short.MAX_VALUE);
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
	private double sweepRate, dryWetMixPercent, feedbackPercent;
	private int sweepRangeInOctaves;
	
	private double wp, minWp, maxWp, currentStep, step;
	private double thisOut1, thisOut2, thisOut3, thisOut4;
	private double prevIn1, prevIn2, prevIn3, prevIn4;
	
	private SampleProviderIntfc provider;
}
