package com.google.appinventor.client.codeinventor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ButtonComponent {
  private static final Map<String, String> eventNameMap;
  private static final Map<String, String> eventParamsMap;
  
  static {
    Map<String, String> eNameMap = new HashMap<String, String>();
    eNameMap.put("Click", "Click");
    eNameMap.put("LongClick", "LongClick");
    eNameMap.put("GotFocus", "FocusChange");
    eNameMap.put("LostFocus", "FocusChange");
    eventNameMap = Collections.unmodifiableMap(eNameMap);
    Map<String, String> eParamsMap = new HashMap<String, String>();
    eParamsMap.put("Click",  "View v");
    eParamsMap.put("LongClick",  "View v");
    eParamsMap.put("GotFocus", "View v, boolean hasFocus");
    eParamsMap.put("LostFocus", "View v, boolean hasFocus");
    eventParamsMap = Collections.unmodifiableMap(eParamsMap);
  }
  
  public ButtonComponent() {
  }
  
  /**
   * Given an event's App Inventor name, return the event's Android name.
   * 
   * @param aiEventName  Event's name in App Inventor
   * @return  Event's name in Android
   */
  public static String eventNameMap(String aiEventName) {
    return eventNameMap.get(aiEventName);
  }
  
  /**
   * Given an event's App Inventor name, return the event's parameter list.
   * 
   * @param aiEventName  Event's name in App Inventor
   * @return  List of parameters for event handler signature
   */
  public static String eventParameterMap(String aiEventName) {
    return eventParamsMap.get(aiEventName);
  }
  
//  public static String getEventHandlerSignature(String componentName, String event, int blockId, int selectedBlockId, int depth) {
//    String sig = "";
//    sig += indent(depth) + HtmlWrapper.addCSSClass(componentName + ".setOn" + ButtonComponent.eventNameMap(event) + "Listener(new View.On" + ButtonComponent.eventNameMap(event) + "Listener() {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
//    sig += indent(depth + 1) + HtmlWrapper.addCSSClass("public void on" + ButtonComponent.eventNameMap(event) + "(" + ButtonComponent.eventParameterMap(event) + ") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId); 
//    
//    if(event.equals("Click") || event.equals("LongClick")) {
//      sig += indent(depth) + HtmlWrapper.addCSSClass(componentName + ".setOn" + event + "Listener(new View.On" + event + "Listener() {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
//      sig += indent(depth + 1) + HtmlWrapper.addCSSClass("public void on" + event + "(" + ButtonComponent.eventParameterMap(event) + ") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId); 
//      
//    } else if(event.equals("GotFocus")) {
//      
//    } else if(event.equals("LostFocus")) {
//      
//    }
//    
//    return sig;
//  }
}