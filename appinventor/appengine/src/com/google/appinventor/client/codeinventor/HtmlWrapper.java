package com.google.appinventor.client.codeinventor;

public class HtmlWrapper {
  private static final String SELECTED_BLOCK_CSS_CLASS = "selectedblock";
  private static final String SELECTED_INNER_BLOCK_CSS_CLASS = "selectedinnerblock";
  
  public HtmlWrapper() {
  }

  /**
   * Adds class to blocks based on the type of block.
   * 
   * @param str  Value to add class to
   * @param cc  CSS class to add to block of text
   * @param blockId  Block's ID value
   * @param selectedBlockId  ID value of the currently selected block
   * @return  The value of str enclosed in an HTML span tag with class cc
   */
  public static String addCSSClass(String str, String cc, int blockId, int selectedBlockId) {
    cc += ((blockId == selectedBlockId) ? " " + SELECTED_BLOCK_CSS_CLASS : "");
    return "<span class='" + cc + "'>" + str + "</span>";
  }
  
  /**
   * Adds class to blocks based on the type of block.
   * 
   * @param str  Value to add class to
   * @param cc  CSS class to add to block of text
   * @return  The value of str enclosed in an HTML span tag with class cc
   */
  public static String addCSSClass(String str, String cc) {
    return "<span class='" + cc + "'>" + str + "</span>";
  }
  
  /**
   * Adds conditional selection highlighting surrounding text.
   * 
   * @param str  Code to add highlighting to
   * @param blockId  Block's ID value
   * @param selectedBlockId  ID value of the currently selected block
   * @return  The value of str enclosed in a highlighted HTML span
   */
  public static String addSelectionClass(String str, int blockId, int selectedBlockId) {
    return "<span class='" + ((blockId == selectedBlockId) ? " " + SELECTED_BLOCK_CSS_CLASS : "") + "'>" + str + "</span>";
  }
  
  /**
   * Add conditional inner selection highlighting surrounding text.
   * 
   * @param str  Code to add highlighting to
   * @param blockId  Block's ID value
   * @param selectedBlockId  ID value of the currently selected block
   * @return  The value of str enclosed in an inner block highlighted HTML span
   */
  public static String addInnerSelectionClass(String str, int blockId, int selectedBlockId) {
    return "<span class='" + ((blockId == selectedBlockId) ? " " + SELECTED_INNER_BLOCK_CSS_CLASS : "") + "'>" + str + "</span>";
  }
  
  /**
   * Adds HTML color span surrounding text.
   * 
   * @param str  Code to add highlighting to
   * @param color  HTML color string
   * @return  The value of str enclosed in a colored HTML span
   */
  public static String makeColorSpan(String str, String color) {
    return "<span style='color:" + color + "'>" + str + "</span>";
  }

  /**
   * Escapes special HTML characters (less than, greater than signs) for HTML display.
   * 
   * @param s  Code to convert to HTML characters
   * @return  Converted code
   */
  public static String htmlify(String s) {
    return s.replaceAll("<",  "&lt;").replaceAll(">", "&gt;");
  }
}
