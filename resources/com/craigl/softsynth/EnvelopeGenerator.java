package com.craigl.softsynth;

/**
 * Envelope Generator
 * <p>
 * Sometime called an ADSR for attack, decay, sustain and release
 * <p>
 * This component produces a time varying value between 0.0 and 1.0<br>
 * with controlled attack slope, decay slope, sustain value and release slope.
 * <p>
 * This components getValue method must be called at the sample rate<br>
 * as all timing is derived from the sample timing.
 * <p>
 * This component is built as a state machine. See text for details.
 * 
 * @author craiglindley
 */

public class EnvelopeGenerator {
	
	// Parameter ranges
	public static final int MS_MIN = 1;
	public static final int MS_MAX = 5000;
	public static final double SUSTAIN_MIN = 0.0;
	public static final double SUSTAIN_MAX = 1.0;
	
	// States of the Envelope Generator
	private enum SM_STATE {
		STATE_IDLE, STATE_ATTACK, STATE_DECAY, STATE_SUSTAIN, STATE_RELEASE
	}
	
	/**
	 * EnvelopeGenerator Class Constructor
	 * <p>
	 * EnvelopeGenerator instance is initialized and placed into the idle state<br>
	 * awaiting a noteOn event to begin operation.
	 */
	public EnvelopeGenerator() {
		
		noteOn = false;
		noteOff = false;
		count = 0;
		state = SM_STATE.STATE_IDLE;
		sustainLevel = 0.0;

		// Calculate sample time
		sampleTime = (1.0 / SamplePlayer.SAMPLE_RATE);
	}
	
	/**
	 * This method is called to initiate the envelope generation process.<br>
	 * The state machine transitions through the attack, decay and sustain<br>
	 * states then awaits the noteOff event.
	 */
	public void noteOn() {
		noteOn = true;
	}

	/**
	 * A noteOff event completes the envelope generation process and returns<br>
	 * the EnvelopeGenerator to the idle state awaiting the next noteOn event.
	 */
	public void noteOff() {
		noteOff = true;
	}

	/**
	 * Sets the attack time of the generated envelope. This is the time<br>
	 * for the envelope value to go from 0.0 to 1.0.
	 * <p>
	 * Valid attack times MS_MIN <= attackTime <= MS_MAX
	 * 
	 * @param ms The attack time in milliseconds 
	 */
	public void setAttackTimeInMS(int ms) {
		
		// Range check incoming value
		ms = (ms < MS_MIN) ? MS_MIN : ms;
		ms = (ms > MS_MAX) ? MS_MAX : ms;
		
		double temp = ((0.001 * ms) / sampleTime);
		attackCount = (int) temp;
		attackSlope = (1.0 / temp);
	}

	/**
	 * Sets the decay time of the generated envelope. This is the time<br>
	 * for the envelope value to go from 1.0 to the sustain level.
	 * <p>
	 * Valid decay times MS_MIN <= decayTime <= MS_MAX
	 * 
	 * @param ms The decay time in milliseconds 
	 */
	public void setDecayTimeInMS(int ms) {
		
		// Range check incoming value
		ms = (ms < MS_MIN) ? MS_MIN : ms;
		ms = (ms > MS_MAX) ? MS_MAX : ms;

		decayMS = ms;
		double temp = ((0.001 * ms) / sampleTime);
		decayCount = (int) temp;
		decaySlope = ((1.0 - sustainLevel) / temp);
	}

	/**
	 * Sets the sustain level of the generated envelope.
	 * <p>
	 * The sustain portion of the envelope is between the end<br>
	 * of the decay interval and the noteOff event.
	 * <p>
	 * Valid sustain values SUSTAIN_MIN <= sustain value <= SUSTAIN_MAX<br>
	 * or between 0.0 and 1.0. 
	 * 
	 * @param level The sustain level to produce
	 */	
	public void setSustainLevel(double level) {
 
		// Range check incoming value
		sustainLevel = (level < SUSTAIN_MIN) ? SUSTAIN_MIN : level;
		sustainLevel = (level > SUSTAIN_MAX) ? SUSTAIN_MAX : level;
		
		// Recalculate decay and release times accordingly
		setDecayTimeInMS(decayMS);
		setReleaseTimeInMS(releaseMS);
	}

	/**
	 * Sets the release time of the generated envelope. This is the time<br>
	 * for the envelope value to go from the sustain level to 0.0.
	 * <p>
	 * Valid release times MS_MIN <= releaseTime <= MS_MAX
	 * 
	 * @param ms The release time in milliseconds 
	 */	
	public void setReleaseTimeInMS(int ms) {
		
		// Range check incoming value
		ms = (ms < MS_MIN) ? MS_MIN : ms;
		ms = (ms > MS_MAX) ? MS_MAX : ms;

		releaseMS = ms;
		double temp = ((0.001 * ms) / sampleTime);
		releaseCount = (int) temp;
		releaseSlope = (sustainLevel / temp);
	}

	/**
	 * Run the envelope generator state machine to return the next value
	 * <p>
	 * This method must be called each sample time to maintain accurate timing.
	 * 
	 * @return The envelope value between 0.0 and 1.0
	 */ 
	public double getValue() {
		
		double value = 0.0;
		
		switch (state) {
			// Process the idle state
			case STATE_IDLE:
				noteOff = false;
				if (noteOn) {
					noteOn = false;
					count = 0;
					state = SM_STATE.STATE_ATTACK;
				}
				break;
				
			// Process the attack state
			case STATE_ATTACK:
				// Did another noteOn event occur?
				if (noteOn) {
					state = SM_STATE.STATE_IDLE;
					break;
				}
				// Calculate the value to return
				value = count * attackSlope;

				// Has attack time elapsed ?
				if (count >= attackCount) {
					count = 0;
					state = SM_STATE.STATE_DECAY;
				}	else	{
					count++;
				}
				break;
				
			// Process the decay state
			case STATE_DECAY:
				// Did another noteOn event occur?
				if (noteOn) {
					state = SM_STATE.STATE_IDLE;
					break;
				}
				// Calculate the value to return
				value = 1.0 - (count * decaySlope);

				// Has decay time elapsed ?
				if (count >= decayCount) {
					state = SM_STATE.STATE_SUSTAIN;
				}	else	{
					count++;
				}
				break;
				
			// Process the sustain state
			case STATE_SUSTAIN:
				// Did another noteOn event occur?
				if (noteOn) {
					state = SM_STATE.STATE_IDLE;
					break;
				}
				// Get value to return
				value = sustainLevel;

				// Did a noteOff event occur ?
				if (noteOff) {
					noteOff = false;
					count = 0;
					state = SM_STATE.STATE_RELEASE;
				}
				break;
				
			// Process the release state
			case STATE_RELEASE:
				// Did another noteOn event occur?
				if (noteOn) {
					state = SM_STATE.STATE_IDLE;
					break;
				}
				// Calculate the value to return
				value = sustainLevel - (count * releaseSlope);
				if (value < 0) {
					value = 0;
				}

				// Has release time elapsed ?
				if (count >= releaseCount) {
					state = SM_STATE.STATE_IDLE;
				}	else	{
					count++;
				}
				break;
		}
		return value;
	}	
	
	// Instance data
	private boolean noteOn;
	private boolean noteOff;
	private int count;
	private SM_STATE state;
	private double sustainLevel;
	private double sampleTime;
	private int attackCount;
	private double attackSlope;
	private int decayMS;
	private int decayCount;
	private double decaySlope;
	private int releaseMS;
	private int releaseCount;
	private double releaseSlope;
}
