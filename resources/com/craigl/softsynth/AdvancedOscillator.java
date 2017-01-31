package com.craigl.softsynth;

/**
 * AdvancedOscillator Class
 * <p>
 * An AdvancedOscillator has two BasicOscillators within it.<br>
 * The first provides the basic oscillator functionality and<br>
 * the second functions as a Low Frequency Oscillator (LFO) for modulation.
 * <p>
 * This AdvancedOscillator also has range controls and can be detuned.
 * 
 * @author craiglindley
 */
public class AdvancedOscillator extends BasicOscillator {
	
	public static final int CENTS_DETUNE_MIN =    0;
	public static final int CENTS_DETUNE_MAX = 1200;
	
	public static final double MOD_DEPTH_MIN = 0.0;
	public static final double MOD_DEPTH_MAX = 1.0;
	
	private static final int CENTS_PER_OCTAVE = 1200;
	
	/**
	 * Modulation type enumeration
	 */
	public enum MOD_TYPE {
	    NONE, AM, FM 
	}
	
	public AdvancedOscillator() {
		
		rangeMultiplier = 1.0;
		detuneMultiplier = 1.0;
		modulationType = MOD_TYPE.NONE;
	}
	
	/**
	 * Set the frequency of the oscillator in Hz.
	 * 
	 * @param frequency Frequency in Hz for this oscillator
	 */
	public void setFrequency(double frequency) {
		
		this.frequency = frequency;
	}

	/**
	 * Set oscillator range
	 * <p>
	 * Range is specified in feet as follows:<br>
	 * 16' is 2 octaves below normal<br>
	 * 8' is 1 octave below normal<br>
	 * 4' is normal range<br>
	 * 2' is 1 octave above normal<br>
	 * 1' is 2 octaves above normal
	 * 
	 * @param rangeSpecifier The range tag as above.
	 */
	public void setFrequencyRange(int rangeSpecifier) {
		
		switch(rangeSpecifier) {

		case 16:
			rangeMultiplier = 0.25;
			break;
			
		case 8:
			rangeMultiplier = 0.5;
			break;
			
		case 4:
	   default:
			rangeMultiplier = 1.0;
			break;
			
		case 2:
			rangeMultiplier = 2.0;
			break;

		case 1:
			rangeMultiplier = 4.0;
			break;			
		}		
	}
	
	/**
	 * Set modulation type for oscillator
	 * 
	 * @param modulationType Determines the type of modulation for this oscillator
	 */
	public void setModulationType(MOD_TYPE modulationType) {
		
		this.modulationType = modulationType;
	}
	
	/**
	 * Set modulation depth
	 * <p>
	 * Controls the depth of the modulation supplied by the LFO to the main oscillator
	 * <p>
	 * depth must be between MOD_DEPTH_MIN and MOD_DEPTH_MAX.
	 * 
	 * @param modulationDepth The depth of the modulation
	 */
	public void setModulationDepth(double modulationDepth) {
		
		modulationDepth = (modulationDepth < MOD_DEPTH_MIN) ? MOD_DEPTH_MIN : modulationDepth;
		modulationDepth = (modulationDepth > MOD_DEPTH_MAX) ? MOD_DEPTH_MAX : modulationDepth;
		
		this.modulationDepth = modulationDepth;
	}
		
	/**
	 * Set oscillator detune
	 * <p>
	 * Range of detune is between CENTS_DETUNE_MIN and CENTS_DETUNE_MAX
	 * 
	 * @param detuneCents Cents to detune this oscillator
	 */
	public void setDetuneInCents(int detuneCents) {
		
		if (detuneCents == 0) {
			detuneMultiplier = 1.0;
		}
		
		detuneCents = (detuneCents < CENTS_DETUNE_MIN) ? CENTS_DETUNE_MIN : detuneCents;
		detuneCents = (detuneCents > CENTS_DETUNE_MAX) ? CENTS_DETUNE_MAX : detuneCents;
		
		detuneMultiplier = Math.pow(2.0, ((double) detuneCents / CENTS_PER_OCTAVE));	
	}	
	
	/**
	 * Set the waveshape for the LFO
	 * 
	 * @param waveshape Determines the waveshape of the LFO oscillator
	 */
	public void setLfoWaveshape(WAVESHAPE waveshape) {
		
		lfo.setWaveshape(waveshape);
	}

	/**
	 * Set the frequency of the LFO.
	 * 
	 * @param frequency Frequency in Hz for the LFO oscillator
	 */
	public void setLfoFrequency(double frequency) {
		
		lfo.setFrequency(frequency);
	}

	/**
	 * Return the next sample of the oscillator's waveform
	 * 
	 * @return Next oscillator sample
	 */
	protected double getSample() {

		double freq = frequency;

		// Are we frequency modulating
		if ((modulationType == MOD_TYPE.FM) && (modulationDepth != 0.0)) {
			double lfoValue = lfo.getSample() * modulationDepth;
			freq *= Math.pow(2.0, lfoValue);
		}
		// Apply frequency multiplier
		freq *= rangeMultiplier;

		// Apply detuning multiplier
		freq *= detuneMultiplier;
		
		// Set frequency of osc
		super.setFrequency(freq);

		// Get an osc sample
		double sample = super.getSample();

		// Are we amplitude modulating
		if (modulationType == MOD_TYPE.AM)  {
			double lfoOffset = (lfo.getSample() + 1.0) / 2.0;
			double m = 1.0 - (modulationDepth * lfoOffset);
			sample *=  m;
		}
		// Return the osc sample
		return sample;
	}	
	
	/**
	 * Get a buffer of oscillator samples
	 * 
	 * @param buffer Array to fill with samples
	 * 
	 * @return Count of bytes produced.
	 */
	public int getSamples(byte [] buffer) {
		int index = 0;
		for (int i = 0; i < SamplePlayer.SAMPLES_PER_BUFFER; i++) {
			double ds = getSample() * Short.MAX_VALUE;
			short ss = (short) Math.round(ds);
			buffer[index++] = (byte)(ss >> 8);
			buffer[index++] = (byte)(ss & 0xFF);			
		}
		return SamplePlayer.BUFFER_SIZE;
	}
		
	// Instance data
	private double frequency;
	private BasicOscillator lfo = new BasicOscillator();
	private MOD_TYPE modulationType;
	private double modulationDepth;
	private double rangeMultiplier;
	private double detuneMultiplier;

}
