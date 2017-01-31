package com.craigl.softsynth;

/**
 * Digital equivalent of a Voltage Controller Filter or VCF.
 * <p>
 * This code implements a Moog like 24 db/octave resonant Low Pass Filter.<br>
 * Filter code from www.musicdsp.org site Moog VCF in comments section.
 * <p>
 * VCF is meant to be driven by an Envelope Generator which controls<br>
 * the cutoff frequency of the filter.
 * <p>
 * See text for details.
 * 
 * @author craiglindley
 */

public class VCF extends EnvelopeGenerator implements SampleProviderIntfc {
	
	public static final double MIN_CUTOFF = 20.0;
	public static final double MAX_CUTOFF = 8000.0;
	public static final double MIN_DEPTH = -2.0;
	public static final double MAX_DEPTH = 2.0;	
	
	/**
	 * Set the static cutoff frequency of the filter.
	 * <p>
	 * Cutoff frequency must be between MIN_CUTOFF and MAX_CUTOFF.
	 * <p>
	 * Envelope signal varies the cutoff frequency from this static value.
	 * 
	 * @param cutoff Cutoff frequency in Hz
	 */ 
	public void setCutoffFrequencyInHz(double cutoff) {
		
		cutoff = (cutoff < MIN_CUTOFF) ? MIN_CUTOFF : cutoff;
		cutoff = (cutoff > MAX_CUTOFF) ? MAX_CUTOFF : cutoff;
		
		cutoffFrequencyInHz = cutoff;
		this.cutoff = cutoff;

		recalculate();
	}

	/**
	 * Set the resonance of the filter.
	 * <p>
	 * Valid values are between 0.0 and 1.0 where<br>
	 * 0.0 is no resonance and 1.0 is full resonance or oscillation.
	 * 
	 * @param resonance The resonance value to set
	 */ 
	public void setResonance(double resonance) {

		this.resonance = resonance;

		recalculate();
	}
		
	/**
	 * Set the depth of the filter effect.
	 * <p>
	 * depth == 0 env gen has no affect on cutoff frequency<br>
	 * depth < 0 env gen drives cutoff sweep downward<br>
	 * depth == -1 sweep is 1 octave; depth = -2 sweep is 2 octaves<br>
	 * depth > 0 env gen drives cutoff sweep upward<br>
	 * depth == 1 sweep is 1 octave; depth = 2 sweep is 2 octaves<br>
	 * <p>
	 * Depth value must be between MIN_DEPTH and MAX_DEPTH.
	 * 
	 * @param depth The depth to set
	 */
	public void setDepth(double depth) {
		
		depth = (depth < MIN_DEPTH) ? MIN_DEPTH : depth;
		depth = (depth > MAX_DEPTH) ? MAX_DEPTH : depth;

		this.depth = depth;
	}

	/**
	 * Setup the provider of samples
	 * 
	 * @param provider The provider of samples for the VCF.
	 */
	public void setSampleProvider(SampleProviderIntfc provider) {
		this.provider = provider;
	}

	/**
	 * Recalculate filter parameters on changes to cutoff or resonance
	 */
	private void recalculate() {
		
		double f = (cutoff + cutoff) / (double) SamplePlayer.SAMPLE_RATE;
		p = f * (1.8 - (0.8 * f));
		k = p + p - 1.0;

		double t = (1.0 - p) * 1.386249;
		double t2 = 12.0 + t * t;
		r = resonance * (t2 + 6.0 * t) / (t2 - 6.0 * t);
	}

	/**
	 * Process a single sample through the filter
	 * 
	 * @param input The input sample to process
	 * 
	 * @return Filtered sample
	 */
	private short processSample(short input) {
		// Process input
		x = ((double) input/Short.MAX_VALUE) - r*y4;
		
		// Four cascaded one pole filters (bilinear transform)
		y1 =  x*p +  oldx*p - k*y1;
		y2 = y1*p + oldy1*p - k*y2;
		y3 = y2*p + oldy2*p - k*y3;
		y4 = y3*p + oldy3*p - k*y4;
		
		// Clipper band limited sigmoid
		y4 -= (y4*y4*y4) / 6.0;
		
		oldx = x; oldy1 = y1; oldy2 = y2; oldy3 = y3;
		return (short) (y4 * Short.MAX_VALUE);
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
			
			// Get value from envelope generator in the range 0.0 .. 1.0
			double v = getValue();
			
			// Calculate actual cutoff freq given depth and env gen modifiers
			cutoff = cutoffFrequencyInHz * Math.pow(2.0, depth * v);
			recalculate();
			
			// Return processed sample from filter
			s = processSample(s);
			
			// Store the processed sample
			buffer[index++] = (byte)(s >> 8);
			buffer[index++] = (byte)(s & 0xFF);			
		}
		return SamplePlayer.BUFFER_SIZE;
	}

	// Instance data
	private double resonance, depth, cutoff, cutoffFrequencyInHz;
	private double x, r, p, k, y1, y2, y3, y4, oldx, oldy1, oldy2, oldy3;
	private SampleProviderIntfc provider;
}
