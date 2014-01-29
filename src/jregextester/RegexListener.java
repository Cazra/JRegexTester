package jregextester;

import java.util.EventListener;

public interface RegexListener extends EventListener {
  
  /** Handler for when the listener receives an event indicating that a regex has been updated. */
  public void regexUpdate(RegexEvent e);
  
}
