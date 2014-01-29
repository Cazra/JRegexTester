package jregextester;

import java.util.EventObject;
import java.util.regex.Pattern;


/** An event associated with a regular expression being updated. */
public class RegexEvent extends EventObject {
  
  private Pattern pattern;
  
  public RegexEvent(Object source, Pattern regexPattern) {
    super(source);
    pattern = regexPattern;
  }
  
  
  /** Returns the regex Pattern associated with this event. */
  public Pattern getPattern() {
    return pattern;
  }
  
}