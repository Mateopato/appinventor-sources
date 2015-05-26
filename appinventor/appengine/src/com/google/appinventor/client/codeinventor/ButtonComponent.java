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
}