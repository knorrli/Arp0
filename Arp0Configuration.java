public class Arp0Configuration {
  public static int ARP_CONTROL_COUNT = 13;
  public int tempo;
  public int volume;
  public int toneLength;
  public int frequency;
  public ArpControl[] arpControls = new ArpControl [ARP_CONTROL_COUNT];

  public Arp0Configuration() {
    this.tempo = 120;
    this.volume = 100;
    this.frequency = 440;
    this.toneLength = 100;
    initArpControls();
  }

  public int getUpBeatCount() {
    int noteCount = 0;
    for (ArpControl control : this.arpControls) {
      noteCount += control.upBeats;
    }
    return noteCount;
  }

  public int getDownBeatCount() {
    int noteCount = 0;
    for (ArpControl control : this.arpControls) {
      noteCount += control.downBeats;
    }
    return noteCount;
  }

  public ArpControl getArpControl(int arptone) {
    return arpControls[arptone];
  }

  public void setTempo(int tempo) {
    this.tempo = tempo;
  }

  public void setVolume(int volume) {
    this.volume = volume;
  }

  public void setFrequency(int frequency) {
    this.frequency = frequency;
  }

  public void setToneLength(int length) {
    this.toneLength = length;
  }

  private void initArpControls() {
    for (int i = 0; i < ARP_CONTROL_COUNT; i++) {
      this.arpControls[i] = new ArpControl(i);
    }
  }
}
