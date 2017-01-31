public class Arp0 {

  public static void main(String [] args) {
    Arp0Configuration config = new Arp0Configuration();
    config.setToneLength(50);
    config.getArpControl(0).setUpBeats(1);
    config.getArpControl(4).setUpBeats(1);
    config.getArpControl(7).setUpBeats(1);
    config.getArpControl(10).setUpBeats(1);
    config.getArpControl(12).setUpBeats(1);
    Arpeggiator arp = new Arpeggiator(config);

    SamplePlayer player = new SamplePlayer();
    player.setSampleProvider(arp);
    player.startPlayer();
  }
}
