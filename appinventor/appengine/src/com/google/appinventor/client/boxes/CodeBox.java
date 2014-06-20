/**
 * 
 */
package com.google.appinventor.client.boxes;

import static com.google.appinventor.client.Ode.MESSAGES;
import com.google.appinventor.client.widgets.boxes.Box;

/**
 * Box implementation for code panels.
 * 
 * @author mdabney
 *
 */
public final class CodeBox extends Box {
  // Singleton code box instance
  private static final CodeBox INSTANCE = new CodeBox();
  
  /**
   * Return the code box.
   * 
   * @return code box
   */
  public static CodeBox getCodeBox() {
    return INSTANCE;
  }
  
  private CodeBox() {
    super(MESSAGES.codeBoxCaption(),
         200,      // height
         true,   // minimizable  TODO: make this horizontal minimizable
         false,   // removable
         false,   // startMinimized TODO: start minimized
         false,   // usePadding
         false);  // highlightCaption
  }
}
