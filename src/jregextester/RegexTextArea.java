package jregextester;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringEscapeUtils;

public class RegexTextArea extends JTextArea {
  
  /** The regex Pattern compiled with the text in this component. */
  private Pattern pattern = Pattern.compile("");
  
  /** Set of subscribers to RegexEvents. */
  private Set<RegexListener> regexListeners;
  
  
  
  public RegexTextArea() {
    super();
    
    regexListeners = new HashSet<>();
    
    getDocument().addDocumentListener(new DocumentChangeAdapter() {
      public void anyUpdate(DocumentEvent e) {
        try {
          String regexString = StringEscapeUtils.unescapeJava(getText());
          pattern = Pattern.compile(regexString);
        }
        catch(PatternSyntaxException ex) {
          pattern = Pattern.compile("");
        }
        catch(NullPointerException ex) {
          pattern = Pattern.compile("");
        }
        _fireRegexUpdateEvent(new RegexEvent(this, pattern));
      }
    });
  }
  
  
  /** Returns the most recent version of the regex Pattern. */
  public Pattern getPattern() {
    return pattern;
  }
  
  
  /** Adds a listener for RegexEvents published by this. */
  public void addRegexListener(RegexListener listener) {
    regexListeners.add(listener);
  }
  
  
  /** Removes a listener from this. */
  public void removeRegexListener(RegexListener listener) {
    regexListeners.remove(listener);
  }
  
  
  
  /** Notifies all subscribed RegexListener of updates to the regex. */
  private void _fireRegexUpdateEvent(RegexEvent event) {
    for(RegexListener listener : regexListeners) {
      listener.regexUpdate(event);
    }
  }
}

