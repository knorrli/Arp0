package com.craigl.softsynth;

/**
 * The Sample Provider Interface
 * <p>
 * Java code modules are made into sample providers<br>
 * by implementing this single method interface.
 * 
 * @author craiglindley
 */

public interface SampleProviderIntfc {
	
	/**
	 * Process a buffer full of samples pulled from the sample provider
	 * 
	 * @param buffer Buffer in which the samples are to be processed
	 * 
	 * @return Count of number of bytes processed
	 */
	int getSamples(byte [] buffer);
}
