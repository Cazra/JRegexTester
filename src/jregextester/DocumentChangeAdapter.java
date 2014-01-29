package jregextester;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public abstract class DocumentChangeAdapter implements DocumentListener {
  
  @Override
  public void changedUpdate(DocumentEvent e) {
    anyUpdate(e);
  }
  
  @Override
  public void insertUpdate(DocumentEvent e) {
    anyUpdate(e);
  }
  
  @Override
  public void removeUpdate(DocumentEvent e) {
    anyUpdate(e);
  }
  
  
  /** 
   * All the DocumentListener methods delegate to this method. 
   * The DocumentEvent can be queried for the type of event if necessary. 
   */
  public abstract void anyUpdate(DocumentEvent e);
}

