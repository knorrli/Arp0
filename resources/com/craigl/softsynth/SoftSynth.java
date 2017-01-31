package com.craigl.softsynth;

import com.craigl.softsynth.AdvancedOscillator.MOD_TYPE;
import com.craigl.softsynth.BasicOscillator.WAVESHAPE;

/**
 * Demo code for the soft synth components
 * <p>
 * See text for details.
 * 
 * @author craiglindley
 */

public class SoftSynth {
	
	// Over the Rainbow, abridged.
	private Note [] song = {
			new Note(0,2),
			new Note(60,2), new Note(72,2), new Note(71,4), new Note(67,8),
			new Note(69,8), new Note(71,4), new Note(72,4), new Note(60,2),
			new Note(69,2), new Note(67,1),			
			new Note(57,2), new Note(65,2), new Note(64,4), new Note(60,8),
			new Note(62,8), new Note(64,4), new Note(65,4), new Note(62,4),
			new Note(59,8), new Note(60,8), new Note(62,4), new Note(64,4),
			new Note(60,1),
			new Note(0,4)
	};
	
	private void delay(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch(Exception e) {
			// Do nothing
		}		
	}

	/**
	 * This example illustrates the AdvancedOscillator's FM capability
	 */ 
	public void exampleEleven() {
		
		// Create an oscillator
		AdvancedOscillator osc = new AdvancedOscillator();
		
		// Set the frequency
		osc.setFrequency(400);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SIN);
		
		osc.setLfoFrequency(1.0);
		osc.setModulationDepth(1.0);
		osc.setLfoWaveshape(WAVESHAPE.SIN);
		osc.setModulationType(MOD_TYPE.FM);
		
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(osc);
		
		// Start the player
		player.startPlayer();
		
		delay(1000 * 5);		
		
		// Stop the player
		player.stopPlayer();
	}

	/**
	 * This example illustrates the AdvancedOscillator's AM capability
	 */ 
	public void exampleTen() {
		
		// Create an oscillator
		AdvancedOscillator osc = new AdvancedOscillator();
		
		// Set the frequency
		osc.setFrequency(440);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SIN);
		
		osc.setLfoFrequency(2.0);
		osc.setModulationDepth(1.0);
		osc.setLfoWaveshape(WAVESHAPE.SIN);
		osc.setModulationType(MOD_TYPE.AM);
		
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(osc);
		
		// Start the player
		player.startPlayer();
		
		delay(1000 * 5);		
		
		// Stop the player
		player.stopPlayer();
	}

	/**
	 * This example illustrates the AdvancedOscillator's detune capability
	 */ 
	public void exampleNine() {
		
		// Create an oscillator
		AdvancedOscillator osc = new AdvancedOscillator();
		
		// Set the frequency
		osc.setFrequency(440);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SIN);
		
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(osc);
		
		// Start the player
		player.startPlayer();
		
		// Detune by half step each loop
		for (int i = 0; i < 12; i++) {

			delay(1000 * 1);		
			osc.setDetuneInCents(100 * (i + 1));
		}
		osc.setDetuneInCents(0);
		delay(1000 * 2);
		
		// Stop the player
		player.stopPlayer();
	}

	/**
	 * This example illustrates the AdvancedOscillator's range function
	 */ 
	public void exampleEight() {
		
		// Create an oscillator
		AdvancedOscillator osc = new AdvancedOscillator();
		
		// Set the frequency
		osc.setFrequency(100);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SQU);
		
		// Create a VCA
		VCA vca = new VCA();

		// Set range to 8'
		osc.setFrequencyRange(8);

		// Set the VCA's sample provider
		vca.setSampleProvider(osc);

		// Parameterize the envelope generator
		vca.setAttackTimeInMS(1);
		vca.setDecayTimeInMS(100);
		vca.setSustainLevel(0.01);
		vca.setReleaseTimeInMS(250);		

		// Create music player
		MusicPlayer mp = new MusicPlayer(osc, vca, null, song);
		
		// Set the music player's sample provider
		mp.setSampleProvider(vca);
		
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(mp);
		
		// Start the player
		player.startPlayer();		

		// Play song normal range
		mp.playSong();

		// Set range to 4'
		osc.setFrequencyRange(4);
		
		// Play song in lower range
		mp.playSong();
		
		// Set range to 2'
		osc.setFrequencyRange(2);
		
		// Play song in higher range
		mp.playSong();
		
		// Stop the player
		player.stopPlayer();
	}

	/**
	 * This example illustrates the delay effect
	 */ 
	public void exampleSeven() {
		
		// Create an oscillator
		BasicOscillator osc = new BasicOscillator();
		
		// Set the frequency
		osc.setFrequency(100);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SQU);
		
		// Create a VCA
		VCA vca = new VCA();

		// Set the VCA's sample provider
		vca.setSampleProvider(osc);

		// Parameterize the envelope generator
		vca.setAttackTimeInMS(1);
		vca.setDecayTimeInMS(100);
		vca.setSustainLevel(0.01);
		vca.setReleaseTimeInMS(250);		

		// Create a delay effect
		DelayEffect de = new DelayEffect();
		de.setSampleProvider(vca);
		de.setDelayInMs(250);
		de.setFeedbackPercent(10);
		
		// Create music player
		MusicPlayer mp = new MusicPlayer(osc, vca, null, song);
		
		// Set the music player's sample provider
		mp.setSampleProvider(de);
		
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(mp);
		
		// Start the player
		player.startPlayer();		

		// Play song without the delay
		mp.playSong();

		// Turn the delay on
		de.setBypassed(false);

		// Play song with the delay
		mp.playSong();
		
		// Stop the player
		player.stopPlayer();
	}
	
	/**
	 * This example illustrates the phaser effect
	 */ 
	public void exampleSix() {
		
		// Create an oscillator
		BasicOscillator osc = new BasicOscillator();
		
		// Set the frequency
		osc.setFrequency(100);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SQU);
		
		// Create a VCA
		VCA vca = new VCA();

		// Set the VCA's sample provider
		vca.setSampleProvider(osc);

		// Parameterize the envelope generator
		vca.setAttackTimeInMS(1);
		vca.setDecayTimeInMS(100);
		vca.setSustainLevel(0.1);
		vca.setReleaseTimeInMS(250);		

		// Create a phaser effect
		PhaserEffect fe = new PhaserEffect();
		fe.setSampleProvider(vca);
		fe.setDryWetMixPercent(60);
		fe.setSweepRate(2.0);
		fe.setSweepRangeInOctaves(7);
		fe.setFeedbackPercent(50);
		
		// Create music player
		MusicPlayer mp = new MusicPlayer(osc, vca, null, song);
		
		// Set the music player's sample provider
		mp.setSampleProvider(fe);
		
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(mp);
		
		// Start the player
		player.startPlayer();		

		// Play song without the delay
		mp.playSong();

		// Turn the delay on
		fe.setBypassed(false);

		// Play song with the delay
		mp.playSong();
		
		// Stop the player
		player.stopPlayer();
	}
	
	/**
	 * This example illustrates the use of the VCF and VCA while playing a tune
	 */ 
	public void exampleFive() {
		
		// Create an oscillator
		BasicOscillator osc = new BasicOscillator();
		
		// Set frequency
		osc.setFrequency(100);

		// Set waveshape
		osc.setWaveshape(WAVESHAPE.SQU);
		
		// Create a VCF
		VCF vcf = new VCF();

		// Set the VCF's sample provider
		vcf.setSampleProvider(osc);

		// Parameterize the envelope generator
		vcf.setAttackTimeInMS(1000);
		vcf.setDecayTimeInMS(100);
		vcf.setSustainLevel(0.5);
		vcf.setReleaseTimeInMS(1000);
		
		// Parameterize the filter
		vcf.setCutoffFrequencyInHz(1000);
		vcf.setResonance(0.85);
		vcf.setDepth(2.0);
		
		// Create a VCA
		VCA vca = new VCA();

		// Set the VCA's sample provider
		vca.setSampleProvider(vcf);

		// Parameterize the envelope generator
		vca.setAttackTimeInMS(1);
		vca.setDecayTimeInMS(100);
		vca.setSustainLevel(0.25);
		vca.setReleaseTimeInMS(1000);		
				
		// Create music player
		MusicPlayer mp = new MusicPlayer(osc, vca, vcf, song);
		
		// Set the music player's sample provider
		mp.setSampleProvider(vca);
		
		// Parameterize the music player
		mp.setLoopCount(2);

		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(mp);
		
		// Start the player
		player.startPlayer();		

		// Play the song
		mp.playSong();
		
		// Stop the player
		player.stopPlayer();
	}
	
	/**
	 * This example illustrates VCF operation
	 */ 
	public void exampleFour() {
		
		// Create an oscillator
		BasicOscillator osc = new BasicOscillator();
		
		// Set the frequency
		osc.setFrequency(100);
		
		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SQU);
		
		// Create a VCF
		VCF vcf = new VCF();

		// Set the VCF's sample provider
		vcf.setSampleProvider(osc);

		// Parameterize the envelope generator
		vcf.setAttackTimeInMS(2000);
		vcf.setDecayTimeInMS(1);
		vcf.setSustainLevel(1.0);
		vcf.setReleaseTimeInMS(2000);
		
		// Parameterize the filter
		vcf.setCutoffFrequencyInHz(1000);
		vcf.setResonance(0.85);
		vcf.setDepth(2.0);
				
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(vcf);
		
		// Start the player
		player.startPlayer();
		
		// Initiate note on event
		vcf.noteOn();
		
		delay(1000 * 4);
		
		// Release note event
		vcf.noteOff();
		
		delay(1000 * 4);

		// Stop the player
		player.stopPlayer();
	}
	
	/**
	 * This example illustrates VCA operation
	 */ 
	public void exampleThree() {
		
		// Create an oscillator
		BasicOscillator osc = new BasicOscillator();
		
		// Set the frequency
		osc.setFrequency(500);

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SIN);
		
		// Create a VCA
		VCA vca = new VCA();

		// Set the VCA's sample provider
		vca.setSampleProvider(osc);

		// Parameterize the envelope generator
		vca.setAttackTimeInMS(600);
		vca.setDecayTimeInMS(200);
		vca.setSustainLevel(0.2);
		vca.setReleaseTimeInMS(1000);
				
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the sample player's sample provider
		player.setSampleProvider(vca);
		
		// Start the player
		player.startPlayer();
		
		// Initiate note on event
		vca.noteOn();
		
		delay(1000 * 4);
		
		// Release note event
		vca.noteOff();
		
		delay(1000 * 4);

		// Stop the player
		player.stopPlayer();
	}
	
	/**
	 * This example illustrates changing oscillator frequencies
	 */ 
	public void exampleTwo() {
		
		// Create an oscillator sample producer
		BasicOscillator osc = new BasicOscillator();
		
		// Set the frequency
		osc.setFrequency(400);
		
		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SIN);
				
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the player's sample provider
		player.setSampleProvider(osc);
		
		// Start the player
		player.startPlayer();
		
		delay(1000 * 4);		

		osc.setFrequency(800);

		delay(1000 * 4);		

		osc.setFrequency(1600);

		delay(1000 * 4);

		osc.setFrequency(3200);
		
		delay(1000 * 4);

		// Stop the player
		player.stopPlayer();
	}
	
	/**
	 * This example illustrate all three oscillator waveshapes
	 */ 
	public void exampleOne() {
		
		// Create an oscillator sample producer
		BasicOscillator osc = new BasicOscillator();
		
		// Set the frequency
		osc.setFrequency(500);

		// Set the waveashape
		osc.setWaveshape(WAVESHAPE.SIN);
				
		// Create a sample player
		SamplePlayer player = new SamplePlayer();
		
		// Sets the player's sample provider
		player.setSampleProvider(osc);
		
		// Start the player
		player.startPlayer();
		
		delay(1000 * 4);		

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SQU);

		delay(1000 * 4);		

		// Set the waveshape
		osc.setWaveshape(WAVESHAPE.SAW);

		delay(1000 * 4);
		
		// Stop the player
		player.stopPlayer();
	}

	/**
	 * SoftSynth entry point
	 * 
	 * @param args A single numeric digit is expected which determines<br>
	 * which example to run. Valid values are 1 .. 7 at the present time.
	 */
	public static void main(String [] args) {

		SoftSynth synth = new SoftSynth();

		if (args.length == 1) {
			int exampleIndex = Integer.parseInt(args[0]);

			switch(exampleIndex) {
			case 1:
				synth.exampleOne();
				break;
				
			case 2:
				synth.exampleTwo();
				break;
				
			case 3:
				synth.exampleThree();
				break;
				
			case 4:
				synth.exampleFour();
				break;
				
			case 5:
				synth.exampleFive();
				break;
				
			case 6:
				synth.exampleSix();
				break;
				
			case 7:
				synth.exampleSeven();
				break;
				
			case 8:
				synth.exampleEight();
				break;
				
			case 9:
				synth.exampleNine();
				break;
				
			case 10:
				synth.exampleTen();
				break;
				
			case 11:
				synth.exampleEleven();
				break;
				
			default:
				System.err.println("Only values 1 through 11 are valid");
				break;
			}
		}
	}
}
