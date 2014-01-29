package jregextester;

import javax.swing.JFrame;

/** An GUI application for testing out java regexes. */
public class RegexFrame extends JFrame {
  
  private RegexFrame() {
    super("JRegex Tester");
    this.setSize(480, 800);
    this.add(new RegexPanel());
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setVisible(true);
  }
  
  
  /** Launches the Java Regex Tester. */
  public static void main(String[] args) {
    new RegexFrame();
  }
}

