import java.util.Arrays;

public class Arpeggiator implements SampleProviderIntfc {

  private Arp0Configuration config;
  private BasicOscillator osc;

  public Arpeggiator(Arp0Configuration config) {
    this.config = config;
    initOscillator();
  }

  public int getSamples(byte[] buffer) {
    int index = 0;
    int sampleCount = (SamplePlayer.SAMPLES_PER_BUFFER / (this.config.getUpBeatCount() + this.config.getDownBeatCount()));
    for (ArpControl arpControl : this.config.arpControls) {
      osc.setFrequency(arpControl.relativeFrequency(this.config.frequency));
      for (int repetition = 0; repetition < arpControl.upBeats; repetition++) {
        index = fillBuffer(buffer, sampleCount, index);
      }
    }

    for (int arpControlIndex = this.config.arpControls.length; arpControlIndex > 0; arpControlIndex--) {
      ArpControl arpControl = this.config.arpControls[arpControlIndex - 1];
      osc.setFrequency(arpControl.relativeFrequency(this.config.frequency));
      for (int repetition = 0; repetition < arpControl.downBeats; repetition++) {
        index = fillBuffer(buffer, sampleCount, index);
      }
    }
    return SamplePlayer.BUFFER_SIZE;
  }

  private int fillBuffer(byte[] buffer, int sampleCount, int bufferIndex) {
    int toneLengthThreshold = (sampleCount / 100) * this.config.toneLength;
    for (int i = 0; i < sampleCount; i++) {
      if (i < toneLengthThreshold) {
        double ds = osc.getSample() * Short.MAX_VALUE;
        short ss = (short) Math.round(ds);
        buffer[bufferIndex++] = (byte)(ss >> 8);
        buffer[bufferIndex++] = (byte)(ss & 0xFF);
      } else {
        buffer[bufferIndex++] = 0;
        buffer[bufferIndex++] = 0;
      }
    }
    return bufferIndex;
  }

  private void initOscillator() {
    BasicOscillator osc = new BasicOscillator();
    osc.setFrequency(this.config.frequency);
    osc.setWaveshape(BasicOscillator.WAVESHAPE.SIN);
    this.osc = osc;
  }
}
