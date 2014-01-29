package jregextester;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;


/** The panel containing the GUI for the Java regex tester application. */
public class RegexPanel extends JPanel {
  
  private Font textAreaFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
  private Highlighter.HighlightPainter regexPainter = new DefaultHighlightPainter(Color.YELLOW);
  
  private RegexTextArea regexArea = null;
  private JTextArea inputArea = null; 
  private JTextArea groupsArea = null;
  private JTextArea replaceWithField = null;
  private JTextArea replacementArea = null;
  
  
  
  public RegexPanel() {
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(makeScrollingTextComponent("Regular Expression: ", getRegexArea()));
    add(makeScrollingTextComponent("Matching Test String: ", getInputArea()));
    add(makeScrollingTextComponent("Groups: ", getGroupsArea()));
    add(makeScrollingTextComponent("Replace with: ", getReplaceWithField()));
    add(makeScrollingTextComponent("Replaced Test String: ", getReplacementArea()));
  }
  
  
  /** Places a JTextComponent inside a titled, bordered, vertical scrolling area. */
  private JScrollPane makeScrollingTextComponent(String title, JTextComponent component) {
    JScrollPane pane = new JScrollPane(component, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    pane.setBorder(getSectionBorder(title));
    return pane;
  }
  
  
  /** Gets the text area for writing regexes. */
  public RegexTextArea getRegexArea() {
    if(regexArea == null) {
      regexArea = new RegexTextArea();
      regexArea.setLineWrap(true);
      regexArea.setFont(textAreaFont);
    }
    return regexArea;
  }
  
  
  /** 
   * Gets the text area for writing input strings to test regexes on. 
   * Highlighting is also applied to this area as it and the regex are changed. 
   */
  public JTextComponent getInputArea() {
    if(inputArea == null) {
      inputArea = new JTextArea();
      inputArea.setLineWrap(true);
      inputArea.setFont(textAreaFont);
      
      // Updates to the input area change both the matching area's text and highlighting.
      DocumentListener docListener = new DocumentChangeAdapter() {
        public void anyUpdate(DocumentEvent e) {
          applyRegexHighlighting(inputArea, getRegexArea().getPattern());
        }
      };
      getInputArea().getDocument().addDocumentListener(docListener);
      
      // Updates to the regex area change the matching area's highlighting.
      RegexListener regexListener = new RegexListener() {
        
        public void regexUpdate(RegexEvent e) {
          applyRegexHighlighting(inputArea, e.getPattern());
        }
        
      };
      getRegexArea().addRegexListener(regexListener);
    }
    return inputArea;
  }
  
  
  
  
  /** Gets the text field for the replacement text to use in the replace area. */
  public JTextComponent getReplaceWithField() {
    if(replaceWithField == null) {
      replaceWithField = new JTextArea();
      replaceWithField.setLineWrap(true);
      replaceWithField.setFont(textAreaFont);
    }
    return replaceWithField;
  }
  
  
  /** Gets the text area for displaying the replaced portions of the input test string. */
  public JTextComponent getReplacementArea() {
    if(replacementArea == null) {
      replacementArea = new JTextArea();
      replacementArea.setLineWrap(true);
      replacementArea.setFont(textAreaFont);
      replacementArea.setEditable(false);
      replacementArea.setBackground(Color.LIGHT_GRAY);
      
      // Updates to the input area change both the matching area's text and highlighting.
      DocumentListener docListener = new DocumentChangeAdapter() {
        public void anyUpdate(DocumentEvent e) {
          replacementArea.setText(getInputArea().getText());
          String replaceWithText = getReplaceWithField().getText();
          if(!"".equals(replaceWithText)) {
            applyReplacementHighlighting(replacementArea, getRegexArea().getPattern(), replaceWithText);
          }
        }
      };
      getInputArea().getDocument().addDocumentListener(docListener);
      getReplaceWithField().getDocument().addDocumentListener(docListener);
      
      // Updates to the regex area change the matching area's highlighting.
      RegexListener regexListener = new RegexListener() {
        
        public void regexUpdate(RegexEvent e) {
          replacementArea.setText(getInputArea().getText());
          String replaceWithText = getReplaceWithField().getText();
          if(!"".equals(replaceWithText)) {
            applyReplacementHighlighting(replacementArea, getRegexArea().getPattern(), replaceWithText);
          }
        }
        
      };
      getRegexArea().addRegexListener(regexListener);
    }
    return replacementArea;
  }
  
  
  
  /** Gets the text area for displaying the match groups for the regex on the test string. */
  public JTextComponent getGroupsArea() {
    if(groupsArea == null) {
      groupsArea = new JTextArea();
      groupsArea.setLineWrap(true);
      groupsArea.setFont(textAreaFont);
      groupsArea.setEditable(false);
      groupsArea.setBackground(Color.LIGHT_GRAY);
      
      // Updates to the input area change both the matching area's text and highlighting.
      DocumentListener docListener = new DocumentChangeAdapter() {
        public void anyUpdate(DocumentEvent e) {
          groupsArea.setText(getGroupsOutput(getRegexArea().getPattern(), getInputArea().getText()));
        }
      };
      getInputArea().getDocument().addDocumentListener(docListener);
      
      // Updates to the regex area change the matching area's highlighting.
      RegexListener regexListener = new RegexListener() {
        
        public void regexUpdate(RegexEvent e) {
          groupsArea.setText(getGroupsOutput(e.getPattern(), getInputArea().getText()));
        }
        
      };
      getRegexArea().addRegexListener(regexListener);
    }
    return groupsArea; 
  }
  
  
  
  
  
  /** Applies regex highlighting to some text component. */
  private void applyRegexHighlighting(JTextComponent comp, Pattern pattern) {
    Matcher matcher = pattern.matcher(comp.getText());
    
    Highlighter h = comp.getHighlighter();
    h.removeAllHighlights();
          
    while(matcher.find()) {
      try {
        h.addHighlight(matcher.start(), matcher.end(), regexPainter);
      }
      catch(Exception e) {
        throw new RuntimeException(e);
      }
    }
    
  }
  
  
  
  /** Applies regex string replacement with highlighting to some text component. */
  private void applyReplacementHighlighting(JTextComponent comp, Pattern pattern, String repString) {
    Matcher matcher = pattern.matcher(comp.getText());
    int charsRemoved = 0;
    
    Highlighter h = comp.getHighlighter();
    h.removeAllHighlights();
    
    comp.setText(comp.getText().replaceAll(pattern.pattern(), repString));
    
    while(matcher.find()) {
      try {
        int matchLength = matcher.end() - matcher.start();
        int numRemoved = matchLength - repString.length();
        
        h.addHighlight(matcher.start()-charsRemoved, 
                      matcher.start()-charsRemoved+repString.length(), 
                      regexPainter);
                      
        charsRemoved += numRemoved;
      }
      catch(Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
  
  
  /** Returns a String containing the group information about the first match of a regex on a test string. */
  private String getGroupsOutput(Pattern pattern, String testStr) {
    String result = "";
    Matcher matcher = pattern.matcher(testStr);
    if(matcher.find()) {
      for(int i=0; i <= matcher.groupCount(); i++) {
        int startIndex = matcher.start(i);
        int endIndex = matcher.end(i);
        String groupString = "undefined";
        if(startIndex != -1) {
          groupString = testStr.substring(startIndex, endIndex);
        }
        result += "Group " + i + ": (" + groupString + ")\n";
      }
    }
    return result;
  }
  
  
  
  /** Creates a titled, etched, beveled border with padding. */
  private Border getSectionBorder(String title) {
    Border outsideBorder = new EmptyBorder(10, 10, 10, 10);
    Border insideBorder = new CompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), title), new BevelBorder(BevelBorder.LOWERED));
    return new CompoundBorder(outsideBorder, insideBorder);
  }
}