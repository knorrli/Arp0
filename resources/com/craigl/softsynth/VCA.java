package com.craigl.softsynth;

/**
* Digital equivalent of a Voltage Controller Amplifier or VCA.
* <p>
* VCA is meant to be driven by an Envelope Generator which controls<br>
* the gain throught the amplifier.
* <p>
* See text for details.
* 
* @author craiglindley
*/
public class VCA extends EnvelopeGenerator implements SampleProviderIntfc {
	
	/**
	 * VCA Class Constructor
	 * <p>
	 * Creates an VCA instance and initializes it to default values
	 */
	public VCA() {
		// Set envelope generator to reasonable values
		setAttackTimeInMS(1);
		setDecayTimeInMS(1000);
		setSustainLevel(0.5);
		setReleaseTimeInMS(2000);
	}
	
	/**
	 * Setup the provider of samples
	 * 
	 * @param provider The provider of samples for the VCA.
	 */
	public void setSampleProvider(SampleProviderIntfc provider) {
		this.provider = provider;
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
			short s = (short)((b2 << 8) + b1);
			
			// Apply envelope value to sample
			s *= getValue();
			
			// Store the processed sample
			buffer[index++] = (byte)(s >> 8);
			buffer[index++] = (byte)(s & 0xFF);			
		}
		return SamplePlayer.BUFFER_SIZE;
	}
	
	// Instance data
	private SampleProviderIntfc provider;
}
