public class ArpControl {
  public static final int CENTS_PER_OCTAVE = 1200;
  public final int arptone;
  public int upBeats;
  public int downBeats;

  public ArpControl(int arptone) {
    this.arptone = arptone;
    this.upBeats = 0;
    this.downBeats = 0;
  }

  public double relativeFrequency(double frequency) {
    return frequency * Math.pow(2.0, (cents() / CENTS_PER_OCTAVE));
  }

  public double cents() {
    return (double) (arptone * 100);
  }

  public void setUpBeats(int beats) {
    this.upBeats = beats;
  }

  public void setDownBeats(int beats) {
    this.downBeats = beats;
  }

  public String toString() {
    return "===\nTONE: "+this.arptone+"\nUP: "+this.upBeats+"\nDOWN: "+this.downBeats;
  }
}
