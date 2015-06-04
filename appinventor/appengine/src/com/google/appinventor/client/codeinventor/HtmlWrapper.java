package com.google.appinventor.client.codeinventor;

import java.util.Arrays;

public class HtmlWrapper {
  private static final String SELECTED_BLOCK_CSS_CLASS = "selectedblock";
  private static final String SELECTED_INNER_BLOCK_CSS_CLASS = "selectedinnerblock";
  
  public static final String CONTROL_BLOCK_CSS_CLASS = "controlblock";
  public static final String LOGIC_BLOCK_CSS_CLASS = "logicblock";
  public static final String MATH_BLOCK_CSS_CLASS = "mathblock";
  public static final String TEXT_BLOCK_CSS_CLASS = "textblock";
  public static final String LISTS_BLOCK_CSS_CLASS = "listsblock";
  public static final String COLORS_BLOCK_CSS_CLASS = "colorsblock";
  public static final String VARIABLES_BLOCK_CSS_CLASS = "variablesblock";
  public static final String PROCEDURES_BLOCK_CSS_CLASS = "proceduresblock";
  public static final String GETTER_BLOCK_CSS_CLASS = "getterblock";
  public static final String SETTER_BLOCK_CSS_CLASS = "setterblock";
  public static final String ARGUMENTS_BLOCK_CSS_CLASS = "argumentsblock";
  public static final String COMMENT_CSS_CLASS = "commentsection";
  public static final String SYSTEM_CSS_CLASS = "systemblock";
  
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
   * @return  If block is selected, the value of str enclosed in highlighting HTML span, otherwise str
   */
  public static String addSelectionClass(String str, int blockId, int selectedBlockId) {
    return ((blockId == selectedBlockId) ? "<span class=' " + SELECTED_BLOCK_CSS_CLASS + "'>" + str + "</span>" : str);
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
    return ((blockId == selectedBlockId) ? "<span class=' " + SELECTED_INNER_BLOCK_CSS_CLASS + "'>" + str + "</span>" : str);
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
  
  /**
   * Returns a string of num c's
   * 
   * @param c  Character to multiply
   * @param num  Number of characters desired
   * @return  String containing num c's
   */
  public static String multiplyChars(char c, int num) {
    char[] chars = new char[num];
    Arrays.fill(chars, c);
    return new String(chars);
  }
  
  /**
   * Adds spaces to indent depth layers
   * @param depth  Number of layers to indent
   * @return  String of depth * 2 spaces
   */
  public static String indent(int depth) {
    return HtmlWrapper.multiplyChars(' ', depth * 2);
  }
}
