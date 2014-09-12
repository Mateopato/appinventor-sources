package com.google.appinventor.client.widgets.codeinventor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

/**
 * Panel to display Code.
 * @author mdabney
 *
 */
public class CodePanel extends Composite {
  public enum BlockTypes {
    component_event,
    component_set_get,
    controls_if,
    logic_boolean,
    logic_false,
    logic_negate,
    logic_compare,
    logic_operation,
    logic_or,
    math_number,
    math_compare,
    math_add,
    math_subtract,
    math_multiply,
    math_division,
    math_power,
    math_random_int,
    math_random_float,
    math_random_set_seed,
    math_on_list,
    math_single,
    math_divide,
    math_trig,
    math_atan2,
    math_convert_angles,
    math_format_as_decimal,
    math_is_a_number,
    text,
    text_join,
    text_length,
    text_isEmpty,
    text_compare,
    text_trim,
    text_changeCase,
    text_starts_at,
    text_contains,
    text_split,
    text_split_at_spaces,
    text_segment,
    text_replace_all,
    lists_create_with,
    lists_add_items,
    lists_is_in,
    lists_length,
    lists_is_empty,
    lists_pick_random_item,
    lists_position_in,
    lists_select_item,
    lists_insert_item,
    lists_replace_item,
    lists_remove_item,
    lists_append_list,
    lists_copy,
    lists_is_list,
    lists_to_csv_row,
    lists_to_csv_table,
    lists_from_csv_row,
    lists_from_csv_table,
    color_black,
    color_white,
    color_red,
    color_pink,
    color_orange,
    color_yellow,
    color_green,
    color_cyan,
    color_blue,
    color_magenta,
    color_light_gray,
    color_gray,
    color_dark_gray,
    color_make_color,
    color_split_color,
    lexical_variable_get,
    lexical_variable_set,
    procedures_defnoreturn,
  }
  
  public enum EventNames {
    Initialize,   // screen initialization
    Click,        // button click
  }
  
  private static final String CONTROL_BLOCK_CSS_CLASS = "controlblock";
  private static final String LOGIC_BLOCK_CSS_CLASS = "logicblock";
  private static final String MATH_BLOCK_CSS_CLASS = "mathblock";
  private static final String TEXT_BLOCK_CSS_CLASS = "textblock";
  private static final String LISTS_BLOCK_CSS_CLASS = "listsblock";
  private static final String COLORS_BLOCK_CSS_CLASS = "colorsblock";
  private static final String VARIABLES_BLOCK_CSS_CLASS = "variablesblock";
  private static final String PROCEDURES_BLOCK_CSS_CLASS = "proceduresblock";
  private static final String GETTER_BLOCK_CSS_CLASS = "getterblock";
  private static final String SETTER_BLOCK_CSS_CLASS = "setterblock";
  private static final String ARGUMENTS_BLOCK_CSS_CLASS = "argumentsblock";
  private static final String COMMENT_CSS_CLASS = "commentsection";

  private static final String SELECTED_BLOCK_CSS_CLASS = "selectedblock";
  private static final String SELECTED_INNER_BLOCK_CSS_CLASS = "selectedinnerblock";
  
  private static final String IMPORT_DELIMITER = "--IMPORTS--";
  private static final String GLOBALS_DELIMITER = "--GLOBALS--";
  private static final String FUNCTIONS_DELIMITER = "--FUNCTIONS--";
  
  private static final String RANDOM_IMPORT_PATH = "java.util.Random";
  private static final String RANDOM_GENERATOR_NAME = "randomGenerator";
  private static final String RANDOM_GENERATOR_INIT_PREFIX = "Random ";
  private static final String RANDOM_GENERATOR_INIT_SUFFIX = " = new Random();";
  
  private static final String BIG_DECIMAL_IMPORT_PATH = "java.math.BigDecimal";

  private static final String ARRAYS_IMPORT_PATH = "java.util.Arrays";
  private static final String ARRAYLIST_IMPORT_PATH = "java.util.ArrayList";
  private static final String LIST_IMPORT_PATH = "java.util.List";
  private static final String PATTERN_IMPORT_PATH = "java.util.regex.Pattern";
  private static final String ANDROID_COLOR_IMPORT_PATH = "android.graphics.Color";
  
  private static final int VIEWER_WINDOW_OFFSET = 170;
  
  // UI elements
  private final VerticalPanel panel;
  private final Label componentName;
  private final DisclosurePanel firstHeader;
  private final DisclosurePanel secondHeader;
  private final ScrollPanel firstHeaderScrollPanel;
  private final ScrollPanel secondHeaderScrollPanel;
  private final CheckBox showXMLBox;
  private final HTML codeLabel;
  private final HTML test1;
  private final Label test2;
  
  private String codeData;
  private String xmlData = "";
  private int selectedBlockId;
  private boolean showXML = true;
  
  private Map<String, Set<Integer>> imports = new TreeMap<String, Set<Integer>>();
  private Map<String, Set<Integer>> globals = new LinkedHashMap<String, Set<Integer>>();
  private Map<String, Set<Integer>> functions = new LinkedHashMap<String, Set<Integer>>();
  
  private boolean skipChildren = false;
  
  // TODO: handle imports, globals, functions when deleting blocks
  
  /**
   * Creates a new Code Panel.
   */
  public CodePanel() {
    int panelWidth = 330;
    
    // Initialize UI
    //ScrollPanel outerPanel = new ScrollPanel();
    VerticalPanel outerPanel = new VerticalPanel();
    outerPanel.setWidth(panelWidth + "px");
    
    selectedBlockId = 0;
    codeData = "";
    
    componentName = new Label("UI XML Code");  // TODO: fix this and next line
    //componentName.setStyleName("ode-PropertiesComponentName");
    //outerPanel.add(componentName);
    
    panel = new VerticalPanel();
    panel.setWidth("100%");
    //panel.setStylePrimaryName("ode-PropertiesPanel"); 
    outerPanel.add(panel);
    
    firstHeader = new DisclosurePanel("UI XML Code");
    //firstHeader.setStyleName("ode-PropertiesComponentName");  // TODO: create my own style
    secondHeader = new DisclosurePanel("Permissions");
    panel.add(firstHeader);
    panel.add(secondHeader);
    
    firstHeaderScrollPanel = new ScrollPanel();
    firstHeaderScrollPanel.setSize(panelWidth + "px", ((int) ((Window.getClientHeight() - VIEWER_WINDOW_OFFSET) * 8.5) / 10) + "px");
    //firstHeaderScrollPanel.setSize("500px", ((Window.getClientHeight() - VIEWER_WINDOW_OFFSET) * 6 / 10) + "px");
    firstHeader.add(firstHeaderScrollPanel);
    
    secondHeaderScrollPanel = new ScrollPanel();
    secondHeaderScrollPanel.setSize(panelWidth + "px", ((Window.getClientHeight() - VIEWER_WINDOW_OFFSET) * 1 / 10) + "px");
    //secondHeaderScrollPanel.setSize("500px", ((Window.getClientHeight() - VIEWER_WINDOW_OFFSET) * 4 / 10) + "px");
    secondHeader.add(secondHeaderScrollPanel);
    
    showXMLBox = new CheckBox("Click to hide XML");
    showXMLBox.setValue(true);
    
    showXMLBox.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        showXML = ((CheckBox) event.getSource()).getValue();
        updateCode(xmlData);
        
        if(showXML) ((CheckBox) event.getSource()).setText("Click to hide XML");
        else ((CheckBox) event.getSource()).setText("Click to show XML");
      }
    });
    
    panel.add(showXMLBox);
    
    codeLabel = new HTML("");
    
    StringBuilder sb = new StringBuilder();
    
    sb.append(htmlify("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n"));
    sb.append(htmlify("  package=\"com.example.testapp\"\n"));
    sb.append(htmlify("  android:versionCode=\"1\"\n"));
    sb.append(htmlify("  android:versionName=\"1.0\" >\n\n"));

    sb.append(htmlify("  <uses-sdk\n"));
    sb.append(htmlify("    android:minSdkVersion=\"8\"\n"));
    sb.append(htmlify("    android:targetSdkVersion=\"17\" />\n\n"));

    sb.append(htmlify("  <application\n"));
    sb.append(htmlify("    android:allowBackup=\"true\"\n"));
    sb.append(htmlify("    android:icon=\"@drawable/ic_launcher\"\n"));
    sb.append(htmlify("    android:label=\"@string/app_name\"\n"));
    sb.append(htmlify("    android:theme=\"@style/AppTheme\" >\n"));
    sb.append(htmlify("  </application>\n\n"));

    sb.append(htmlify("</manifest>"));
    
    test1 = new HTML("<pre>" + sb.toString() + "</pre>");
    test2 = new Label("test2");
    //firstHeader.add(codeLabel);
    firstHeaderScrollPanel.add(codeLabel);
    //secondHeader.add(test1);
    secondHeaderScrollPanel.add(test1);
    //secondHeader.add(test2);
    //outerPanel.add(codeLabel);
    
    initWidget(outerPanel);
  }
  
  /**
   * Highlights the selected code block.
   * 
   * @param selected  XML ID number of selected block
   * 
   * TODO: remove highlight when deselecting a block
   */
  public void changeSelection(int selected) {
    selectedBlockId = selected;
  }
  
  /**
   * Replaces current code with input text.
   * 
   * @param text  XML code to replace current code with
   */
  public void updateCode(String text) {
    codeData = "";
    xmlData = text;
    addCode(text);
  }
  
  /**
   * Adds code to code buffer.
   */
  public void addCode(String text) {
    codeData += text;
    //codeLabel.setHTML(codeData);
    
    // TODO: be a little more sophisticated here
    codeData = codeData.replaceAll("<comment.*</comment>", "");
    
    try {
      Document doc = XMLParser.parse(codeData);
      NodeList nl = doc.getElementsByTagName("*");
      Node n;
      
      codeData = "<pre>";

      /*codeData += "The first child is: " + doc.getFirstChild().getNodeName() + ", and it has " + doc.getFirstChild().getChildNodes().getLength() + " children. \n\n";
      
      for(int i = 0; i < nl.getLength(); i++) {
        n = nl.item(i);
        codeData += makeColorSpan(n.getNodeName(), "#881280") + " (" + makeAttributeString(n) + ")\n";
      }*/
      
      codeData += IMPORT_DELIMITER;
      codeData += GLOBALS_DELIMITER;
      
      codeData += visitNode(doc.getFirstChild(), 0);

      codeData += FUNCTIONS_DELIMITER;
      
      codeData = codeData.replace(IMPORT_DELIMITER, imports.isEmpty() ? "" : buildImports() + "\n");
      codeData = codeData.replace(GLOBALS_DELIMITER, globals.isEmpty() ? "" : buildGlobals() + "\n");
      codeData = codeData.replace(FUNCTIONS_DELIMITER,  functions.isEmpty() ? "" : "\n" + buildFunctions());
      
      codeData += "</pre>";
    } catch(DOMParseException e) {
      codeData += e;
    }
    
    codeLabel.setHTML(codeData);
  }
  
  private String getAttributeValueIfExists(Node n, String attribute) {
    if(n != null && n.hasAttributes() && n.getAttributes().getNamedItem(attribute) != null) {
      return n.getAttributes().getNamedItem(attribute).getNodeValue();
    }
    
    return null;
  }
  
  private String visitNode(Node n, int depth) {
    // determine the type of the XML node 
    String blockType = "";
    
    switch(n.getNodeType()) {
      case Node.TEXT_NODE:
        if(showXML) {
          if(n.getNodeValue().trim().isEmpty()) return "";
          
          return ": " + n.getNodeValue().trim() + "\n";
        } else {
          return "";
        }
      case Node.ELEMENT_NODE:
        int blockId = getAttributeValueIfExists(n, "id") != null ? Integer.parseInt(getAttributeValueIfExists(n, "id")) : -1;
        String str = showXML ? indent(depth) + addSelectionClass(makeColorSpan(n.getNodeName(), "#881280") + " (" + makeAttributeString(n) + ")", blockId) : "";

        if(showXML && n.getNodeName().compareTo("title") != 0) str += "\n";
        
        if(n.getNodeName().compareTo("block") == 0) {
          blockType = getAttributeValueIfExists(n, "type");
          
          if(blockType.compareTo("NO_SUCH_ATTRIBUTE") == 0) {
            str += makeColorSpan("// NO BLOCK TYPE... WEIRD.\n", "red");
          }
          
          try {
            Node mutation;
            Node childBlock;
            Node leftChild;
            Node rightChild;
            String operation;
            String childText;
            String leftChildText;
            String rightChildText;
            
            switch(BlockTypes.valueOf(blockType)) {
              // when event block
              case component_event:
                mutation = getChildOfType(n, "mutation", 0);
                childBlock = getChildOfType(getChildWithAttrValue(n, "statement", "name", "DO"), "block", 0);
                
                if(mutation != null) {
                  String eventName = getAttributeValueIfExists(mutation, "event_name");
                  String instanceName = getAttributeValueIfExists(mutation, "instance_name");
                  
                  // TODO: more to do here depending on the type of event
                  str += indent(depth) + addCSSClass("when " + instanceName + "." + eventName + "() {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                } else {
                  str += makeColorSpan("// NO MUTATION CHILD FOUND\n", "red");
                }
                
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth + 1) : indent(depth + 1) + "/* body of while loop */", blockId);
                str += indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "title", "statement", "#text"});
                skipChildren = true;
                
                break;
              // component getter or setter
              case component_set_get:
                mutation = getChildOfType(n, "mutation", 0);
                
                if(mutation != null) {
                  String componentType = getAttributeValueIfExists(mutation, "component_type");   // e.g., Label
                  String propertyName = getAttributeValueIfExists(mutation, "property_name");     // e.g., BackgroundColor
                  String instanceName = getAttributeValueIfExists(mutation, "instance_name");     // e.g., Label1 (custom name)
                  boolean isSet = getAttributeValueIfExists(mutation, "set_or_get").compareTo("set") == 0;
                  // TODO: is_generic
                  
                  childText = addInnerSelectionClass(getChildText(n, depth + 1, "VALUE", "/* set value */"), blockId);
                  
                  if(isSet) {
                    str += indent(depth);
                    str += addCSSClass(instanceName + ".set" + propertyName + "(", SETTER_BLOCK_CSS_CLASS, blockId);
                    str += childText;
                    str += addCSSClass(");\n", SETTER_BLOCK_CSS_CLASS, blockId);
                    
                  } else {
                    str += addCSSClass(instanceName + ".get" + propertyName + "()", GETTER_BLOCK_CSS_CLASS, blockId);
                  }
                } else {
                  str += makeColorSpan("// NO MUTATION CHILD FOUND\n", "red");
                }
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "title", "value", "next", "#text"});

                skipChildren = true;
                
                break;
              // if block
              case controls_if:
                mutation = getChildOfType(n, "mutation", 0);

                if(mutation != null) {
                  int elseifs = getAttributeValueIfExists(mutation, "elseif") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "elseif")) : 0;
                  int elses = getAttributeValueIfExists(mutation, "else") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "else")) : 0;
                  
                  str += indent(depth);
                  
                  // handle all if and else if blocks
                  for(int i = 0; i <= elseifs; ++i) {
                    String iftext = addInnerSelectionClass(getChildText(n, depth, "IF" + i, "/* NO CONDITION" + i + " */"), blockId);
                    String dotext = addInnerSelectionClass(getChildText(n, depth + 1, "DO" + i, indent(depth + 1) + "// nothing to do here!\n", "statement"), blockId);
                    
                    str += addCSSClass((i > 0 ? " else " : "") + "if(", CONTROL_BLOCK_CSS_CLASS, blockId);
                    str += iftext;
                    str += addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                    str += dotext;
                    str += indent(depth) + addCSSClass("}", CONTROL_BLOCK_CSS_CLASS, blockId);
                  }
                  
                  // handle else block
                  if(elses == 1) {
                    String dotext = addInnerSelectionClass(getChildText(n, depth + 1, "ELSE", indent(depth + 1) + "// nothing to do here!\n", "statement"), blockId);
                    
                    str += addCSSClass(" else {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                    str += dotext;
                    str += indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                  } else {
                    str += "\n";
                  }
                // simple if statement with no if/else
                } else {
                  String iftext = addInnerSelectionClass(getChildText(n, depth, "IF0", "/* NO CONDITION0 */"), blockId);
                  String dotext = addInnerSelectionClass(getChildText(n, depth + 1, "DO0", indent(depth + 1) + "// nothing to do here!\n", "statement"), blockId);
                  
                  str += indent(depth) + addCSSClass("if(", CONTROL_BLOCK_CSS_CLASS, blockId);
                  str += iftext;
                  str += addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                  str += dotext;
                  str += indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                }
                
                skipChildren = true;
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "value", "statement", "next", "#text"});
                
                break;
                
              // Logic blocks
              case logic_boolean:
                // TODO: getTitleFromBlock
                String logicBoolVal = getChildWithAttrValue(n, "title", "name", "BOOL").getFirstChild().getNodeValue();
                str += addCSSClass(logicBoolVal.toLowerCase(), LOGIC_BLOCK_CSS_CLASS, blockId);
                
                break;
              case logic_false:
                String logicFalseVal = getChildWithAttrValue(n, "title", "name", "BOOL").getFirstChild().getNodeValue();
                str += addCSSClass(logicFalseVal.toLowerCase(), LOGIC_BLOCK_CSS_CLASS, blockId);
                
                break;
              case logic_negate:
                childBlock = getChildOfType(getChildOfType(n, "value", 0), "block", 0);
                
                str += addCSSClass("!", LOGIC_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value to be negated */", blockId);
                
                skipChildren = true;
                
                break;
              case logic_compare:
                leftChild = getChildWithAttrValue(n, "value", "name", "A");
                rightChild = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", LOGIC_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* left side of comparison */", blockId);
                str += addCSSClass(" " + blockToLogicOperator(n), LOGIC_BLOCK_CSS_CLASS, blockId) + " ";
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* right side of comparison */", blockId);
                str += addCSSClass(")", LOGIC_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case logic_operation:
              case logic_or:
                leftChild = getChildWithAttrValue(n, "value", "name", "A");
                rightChild = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", LOGIC_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* left side of logic operation */", blockId);
                str += addCSSClass(" " + blockToLogicOperator(n), LOGIC_BLOCK_CSS_CLASS, blockId) + " ";
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* right side of logic operation */", blockId);
                str += addCSSClass(")", LOGIC_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
                
              /* Math blocks */
              case math_number:
                String number = getChildWithAttrValue(n, "title", "name", "NUM").getFirstChild().getNodeValue();
                str += addCSSClass(number, MATH_BLOCK_CSS_CLASS, blockId);
                
                break;
              case math_compare:
                leftChild = getChildWithAttrValue(n, "value", "name", "A");
                rightChild = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* left side of comparison */", blockId);
                str += addCSSClass(" " + blockToMathOperator(n), MATH_BLOCK_CSS_CLASS, blockId) + " ";
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* right side of comparison */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_add:
                int additions = Integer.parseInt(getChildOfType(n, "mutation", 0).getAttributes().getNamedItem("items").getNodeValue());
                
                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                
                for(int i = 0; i < additions; i++) {
                  if(i > 0) {
                    str += addCSSClass(" + ", MATH_BLOCK_CSS_CLASS, blockId);
                  }
                  
                  Node addChild = getChildWithAttrValue(n, "value", "name", "NUM" + i);
                  
                  str += addInnerSelectionClass(addChild != null ? visitNode(addChild, depth) : "/* no value specified */", blockId); 
                }

                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_subtract:
                leftChild = getChildWithAttrValue(n, "value", "name", "A");
                rightChild = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* no value specified */", blockId);
                str += addCSSClass(" - ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* no value specified */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_multiply:
                int multiplications = Integer.parseInt(getChildOfType(n, "mutation", 0).getAttributes().getNamedItem("items").getNodeValue());
                
                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                
                for(int i = 0; i < multiplications; i++) {
                  if(i > 0) {
                    str += addCSSClass(" * ", MATH_BLOCK_CSS_CLASS, blockId);
                  }
                  
                  Node multiplyChild = getChildWithAttrValue(n, "value", "name", "NUM" + i);
                  
                  str += addInnerSelectionClass(multiplyChild != null ? visitNode(multiplyChild, depth) : "/* no value specified */", blockId); 
                }

                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_division:
                leftChild = getChildWithAttrValue(n, "value", "name", "A");
                rightChild = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* no value specified */", blockId);
                str += addCSSClass(" / ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* no value specified */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_power:
                leftChild = getChildWithAttrValue(n, "value", "name", "A");
                rightChild = getChildWithAttrValue(n, "value", "name", "B");
                
                // TODO: Math.pow returns a double, may need to cast as an int
                str += addCSSClass("Math.pow(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* no value specified */", blockId);
                str += addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* no value specified */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_random_int:
                Node randomMin = getChildWithAttrValue(n, "value", "name", "FROM");
                Node randomMax = getChildWithAttrValue(n, "value", "name", "TO");

                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                // TODO: get rid of _randomGenerator if the last block using it is deleted!
                
                str += addCSSClass("(" + RANDOM_GENERATOR_NAME + ".nextInt(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(randomMax != null ? visitNode(randomMax, depth) : "/* random max */", blockId);
                str += addCSSClass(" - ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(randomMin != null ? visitNode(randomMin, depth) : "/* random min */", blockId);
                str += addCSSClass(") + ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(randomMin != null ? visitNode(randomMin, depth) : "/* random min */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_random_float:
                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                
                str+= addCSSClass(RANDOM_GENERATOR_NAME + ".nextDouble()", MATH_BLOCK_CSS_CLASS, blockId);

                break;
              case math_random_set_seed:
                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                
                childBlock = getChildOfType(getChildWithAttrValue(n, "value", "name", "NUM"), "block", 0);
                
                str += indent(depth) + addCSSClass(RANDOM_GENERATOR_NAME + ".setSeed(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* seed value */", blockId); 
                str += addCSSClass(")\n", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
                
              case math_on_list:
                mutation = getChildOfType(n, "mutation", 0);
                Node title = getChildWithAttrValue(n, "title", "name", "OP");
                int itemsInList = Integer.parseInt(mutation.getAttributes().getNamedItem("items").getNodeValue());
                operation = blockToMathListType(n).toLowerCase();
                
                if(itemsInList > 1) {
                  for(int i = 0; i < itemsInList; ++i) {
                    Node val = getChildOfType(getChildWithAttrValue(n, "value", "name", "NUM" + i), "block", 0);
                    
                    if(i != itemsInList - 1) {
                      str += addCSSClass("Math." + operation + "(", MATH_BLOCK_CSS_CLASS, blockId);
                      str += addInnerSelectionClass(val != null ? visitNode(val, depth) : "/* value " + (i + 1) + " */", blockId);
                      str += addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId);
                    } else {
                      str += addInnerSelectionClass(val != null ? visitNode(val, depth) : "/* value " + (i + 1) + " */", blockId);
                      str += addCSSClass(multiplyChars(')', itemsInList - 1), MATH_BLOCK_CSS_CLASS, blockId);
                    }
                  }
                } else if(itemsInList == 0) {
                  str += "/* nothing to see here... move along */";
                } else {
                  Node val = getChildOfType(getChildWithAttrValue(n, "value", "name", "NUM0"), "block", 0);
                  str += addInnerSelectionClass(val != null ? visitNode(val, depth) : "/* min value */", blockId);
                }
                
                skipChildren = true;
                
                break;
                
              case math_single:
                String pretext = operationToMathPretext(blockToMathSingleType(n));
                childBlock = getChildOfType(getChildWithAttrValue(n, "value", "name", "NUM"), "block", 0);

                str += addCSSClass(pretext, MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_divide:
                operation = getTitleFromBlock(n);
                leftChild = getChildWithAttrValue(n, "value", "name", "DIVIDEND");
                rightChild = getChildWithAttrValue(n, "value", "name", "DIVISOR");
                
                if(operation.compareTo("MODULO") == 0 || operation.compareTo("REMAINDER") == 0) {
                  operation = "%";
                } else if(operation.compareTo("QUOTIENT") == 0) {
                  // TODO: note this is different in behavior from App Inventor's version
                  //        In AI, 13 / 4 = 3, while 13.6 / 4 = 3.0
                  //        So, Math.floor(L / R)? This will give floats where we would normally get ints
                  operation = "/";
                } else {
                  operation = makeColorSpan("/* UNKNOWN DIVIDE TYPE: " + operation + "*/", "red");
                }

                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* value */", blockId);
                str += addCSSClass(" " + operation + " ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* value */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_trig:
                operation = getTitleFromBlock(n);
                childBlock = getChildOfType(getChildWithAttrValue(n, "value", "name", "NUM"), "block", 0);
                
                str += addCSSClass("Math." + operation.toLowerCase() + "(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_atan2:
                leftChild = getChildBlockNamed(n, "Y");
                rightChild = getChildBlockNamed(n, "X");
                
                str += addCSSClass("Math.atan2(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChild != null ? visitNode(leftChild, depth) : "/* value */", blockId);
                str += addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChild != null ? visitNode(rightChild, depth) : "/* value */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_convert_angles:
                operation = getTitleFromBlock(n);
                childBlock = getChildBlockNamed(n, "NUM");
                
                if(operation.compareTo("DEGREES_TO_RADIANS") == 0) {
                  operation = "toRadians";
                } else if(operation.compareTo("RADIANS_TO_DEGREES") == 0) {
                  operation = "toDegrees";
                } else {
                  operation = makeColorSpan("/* UNKNOWN ANGLE CONVERSION: " + operation + "*/", "red");
                }
                
                str += addCSSClass("Math." + operation + "(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value */", blockId);
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_format_as_decimal:
                Node decimalToRound = getChildBlockNamed(n, "NUM");
                Node numberOfPlaces = getChildBlockNamed(n, "PLACES");
                
                // TODO: add a hover text to describe why this convoluted method is necessary
                
                addImport(BIG_DECIMAL_IMPORT_PATH, blockId);
                
                str += addCSSClass("BigDecimal.valueOf(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(decimalToRound != null ? visitNode(decimalToRound, depth) : "/* value */", blockId);
                str += addCSSClass(").setScale(", MATH_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(numberOfPlaces != null ? visitNode(numberOfPlaces, depth) : "/* value */", blockId);
                str += addCSSClass(", BigDecimal.ROUND_HALF_UP).doubleValue()", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_is_a_number:
                childBlock = getChildBlockNamed(n, "NUM");
                
                String innerStr = addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value */", blockId);

                // TODO: add a note about why this is convoluted
                
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                
                str += addCSSClass("((Object) ", MATH_BLOCK_CSS_CLASS, blockId);
                str += innerStr;
                str += addCSSClass(").getClass().equals(Integer.class)", MATH_BLOCK_CSS_CLASS, blockId);
                
                str += addCSSClass(" || ", MATH_BLOCK_CSS_CLASS, blockId);
                
                str += addCSSClass("((Object) ", MATH_BLOCK_CSS_CLASS, blockId);
                str += innerStr;
                str += addCSSClass(").getClass().equals(Float.class)", MATH_BLOCK_CSS_CLASS, blockId);
                
                str += addCSSClass(" || ", MATH_BLOCK_CSS_CLASS, blockId);
                
                str += addCSSClass("((Object) ", MATH_BLOCK_CSS_CLASS, blockId);
                str += innerStr;
                str += addCSSClass(").getClass().equals(Double.class)", MATH_BLOCK_CSS_CLASS, blockId);
                
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
                
              /* Text blocks */
              case text:
                String textStr = getTitleFromBlock(n);
                if(textStr == null) textStr = "";
                
                str += addCSSClass("\"" + textStr + "\"", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_join:
                mutation = getChildOfType(n, "mutation", 0);
                int numTexts = getAttributeValueIfExists(mutation, "items") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "items")) : 0;
                
                // TODO: check parens
                str += addCSSClass("(", TEXT_BLOCK_CSS_CLASS, blockId);
                
                for(int i = 0; i < numTexts; ++i) {
                  childBlock = getChildBlockNamed(n, "ADD" + i);
                  
                  if(i != 0) str += addCSSClass(" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                  str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value */", blockId);
                }

                str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_length:
                childBlock = getChildBlockNamed(n, "VALUE");
                
                str += addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth) : "/* value */", blockId);
                str += addCSSClass(".length()", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_isEmpty:
                childBlock = getChildBlockNamed(n, "VALUE");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".isEmpty()", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_compare:
                leftChild = getChildBlockNamed(n, "TEXT1");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "TEXT2");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                operation = xmlTextOperationToJava(getTitleFromBlock(n));

                // TODO: check if parens are necessary
                str += addCSSClass("(", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(".compareTo(", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(") " + operation + " 0)", TEXT_BLOCK_CSS_CLASS, blockId);
                                
                skipChildren = true;
                
                break;
              case text_trim:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".trim()", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_changeCase:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                operation = xmlTextCaseChangeToJava(getTitleFromBlock(n));
                
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(operation, TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_starts_at:
                leftChild = getChildBlockNamed(n, "TEXT");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "PIECE");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(".indexOf(", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                    
                skipChildren = true;
                
                break;
              case text_contains:
                leftChild = getChildBlockNamed(n, "TEXT");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "PIECE");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(".contains(", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                    
                skipChildren = true;
                break;
              case text_split:
                leftChild = getChildBlockNamed(n, "TEXT");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "AT");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                operation = getTitleFromBlock(n);

                // TODO: Do I want to do these the way you would do them if you were coding a similar project or
                //        do I want to do them so the behavior is the same. Add a checkbox to toggle the option?
                
                // TODO: I'll need to convert these into whatever list structure I use
                // Arrays.asList(
                
                addImport(ARRAYS_IMPORT_PATH, blockId);
                addImport(PATTERN_IMPORT_PATH, blockId);
                
                str += addCSSClass("Arrays.asList((\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                
                if(operation.compareTo("SPLITATFIRST") == 0) {
                  // TODO: add help text describing why we add Pattern.quote() in all of these instances
                  str += addInnerSelectionClass(leftChildText, blockId);
                  str += addCSSClass(").split(Pattern.quote(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                  str += addInnerSelectionClass(rightChildText, blockId);
                  str += addCSSClass("), 2)", TEXT_BLOCK_CSS_CLASS, blockId);
                } else if(operation.compareTo("SPLITATFIRSTOFANY") == 0) {
                  str += addInnerSelectionClass(leftChildText, blockId);
                  str += addCSSClass(".split(", TEXT_BLOCK_CSS_CLASS, blockId);
                  // combine these into a single regex?
                  // TODO: need to make a getListAsStringArray method or something like that and Pattern each one
                  str += addInnerSelectionClass(rightChildText, blockId);
                  str += addCSSClass(", 2)", TEXT_BLOCK_CSS_CLASS, blockId);
                } else if(operation.compareTo("SPLIT") == 0) {
                  str += addInnerSelectionClass(leftChildText, blockId);
                  str += addCSSClass(").split(Pattern.quote(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                  str += addInnerSelectionClass(rightChildText, blockId);
                  str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                } else if(operation.compareTo("SPLITATANY") == 0) {
                  str += addInnerSelectionClass(leftChildText, blockId);
                  str += addCSSClass(".split(", TEXT_BLOCK_CSS_CLASS, blockId);
                  // combine these into a single regex?
                  // TODO: need to make a getListAsStringArray method or something like that and Pattern each one
                  str += addInnerSelectionClass(rightChildText, blockId);
                  str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                } else {
                  str += makeColorSpan("/* UNKNOWN SPLIT TYPE " + operation + "*/", "red");
                }

                str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                
                //str += "" + Arrays.asList("hi", "blah");
                //str += "" + Arrays.asList("what's up?".split("up"));
                //str += "" + Arrays.asList("what's up?".split("s[ u]"));
                //str += "" + Arrays.asList("what's up?".split(Pattern.quote("s[ u]")));
                
                //str += addInnerSelectionClass(leftChildText, blockId);
                //str += addInnerSelectionClass(rightChildText, blockId);

                //"".split("regex");
                //"".split("regex", 0);
                
                skipChildren = true;
                
                break;
              case text_split_at_spaces:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";

                addImport(ARRAYLIST_IMPORT_PATH, blockId);
                addImport(ARRAYS_IMPORT_PATH, blockId);
                
                // TODO: add notes about why we use "" + and Arrays.asList
                
                str += addCSSClass("new ArrayList(Arrays.asList((\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(").split([ \\t\\n\\r\\f])))", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_segment:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                Node textStartBlock = getChildBlockNamed(n, "START");
                Node textLengthBlock = getChildBlockNamed(n, "LENGTH");
                String textStartStr = textStartBlock != null ? visitNode(textStartBlock, depth) : "/* value */";
                String textLengthStr = textLengthBlock != null ? visitNode(textLengthBlock, depth) : "/* value */";
                
                // TODO: add note about why there's a -1 for start and length
                
                str += addCSSClass("(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(").substring(", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(textStartStr, blockId);
                str += addCSSClass(" - 1, ", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(textLengthStr, blockId);
                str += addCSSClass(" - 1)", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case text_replace_all:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                Node textSegmentBlock = getChildBlockNamed(n, "SEGMENT");
                Node textReplacementBlock = getChildBlockNamed(n, "REPLACEMENT");
                String textSegmentStr = textSegmentBlock != null ? visitNode(textSegmentBlock, depth) : "/* value */";
                String textReplacementStr = textReplacementBlock != null ? visitNode(textReplacementBlock, depth) : "/* value */";
                
                // TODO: add note about why "" + is needed for each string here
                // TODO: change "" + behavior based on type of inner block?

                str += addCSSClass("(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(").replaceAll(Pattern.quote(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(textSegmentStr, blockId);
                str += addCSSClass("), \"\" + ", TEXT_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(textReplacementStr, blockId);
                str += addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case color_black:
              case color_white:
              case color_red:
              case color_pink:
              case color_orange:
              case color_yellow:
              case color_green:
              case color_cyan:
              case color_blue:
              case color_magenta:
              case color_light_gray:
              case color_gray:
              case color_dark_gray:
                str += addCSSClass(makeColorSpan(getTitleFromBlock(n).replaceAll("#", "0x"), getTitleFromBlock(n)), COLORS_BLOCK_CSS_CLASS, blockId);
                break;
              case color_make_color:
                childBlock = getChildBlockNamed(n, "COLORLIST");
                childText = visitNode(childBlock, depth);
                
                str += addCSSClass("((", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".size() == 4 ? (Integer) ", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".get(3) << 24 : 0) | ((Integer) ", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".get(0) << 16) | ((Integer) ", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".get(1) << 8) | ((Integer) ", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".get(2)))", COLORS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case color_split_color:
                childBlock = getChildBlockNamed(n, "COLOR");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";

                addImport(ARRAYLIST_IMPORT_PATH, blockId);
                addImport(ARRAYS_IMPORT_PATH, blockId);
                addImport(ANDROID_COLOR_IMPORT_PATH, blockId);
                
                str += addCSSClass("new ArrayList(Arrays.asList(Color.alpha(", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass("), Color.red(", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass("), Color.green(", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass("), Color.blue(", COLORS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(")))", COLORS_BLOCK_CSS_CLASS, blockId);
                
//                String splitFunc = "";
                
//                splitFunc += addCSSClass("private List _splitColor(String color) {\n", PROCEDURES_BLOCK_CSS_CLASS);
//                splitFunc += addCSSClass("  // color will be formatted as 0xRRGGBB or 0xAARRGGBB\n", COMMENT_CSS_CLASS);
//                splitFunc += addCSSClass("  // red starts at either position 2 or 4 in the String\n", COMMENT_CSS_CLASS);
//                splitFunc += "  int redPosition = (color.length() == 8 ? 2 : 4);\n";
//                splitFunc += "  List colorList = new ArrayList();\n";
//                splitFunc += "\n";
//                splitFunc += "  for(int i = redPosition; i < color.length(); i += 2) {\n";
//                splitFunc += "    colorList.add(Integer.parseInt(color.substring(i, i + 2), 16));\n";
//                splitFunc += "  }\n";
//                splitFunc += "\n";
//                splitFunc += addCSSClass("  // add alpha component if it exists\n", COMMENT_CSS_CLASS);
//                splitFunc += "  if(color.length() == 10) {\n";
//                splitFunc += "    colorList.add(Integer.parseInt(color.substring(2, 4));\n";
//                splitFunc += "  }\n";
//                splitFunc += "\n";
//                splitFunc += "  return colorList;\n";
//                splitFunc += addCSSClass("}\n", PROCEDURES_BLOCK_CSS_CLASS);
      
//                addFunction(splitFunc, blockId);
//
//                str += addCSSClass("_splitColor(", COLORS_BLOCK_CSS_CLASS, blockId);
//                str += addInnerSelectionClass(childText, blockId);
//                str += addCSSClass(")", COLORS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_create_with:
                mutation = getChildOfType(n, "mutation", 0);
                
                str += addCSSClass("new ArrayList(", LISTS_BLOCK_CSS_CLASS, blockId);
                
                if(mutation != null) {
                  int listItems = getAttributeValueIfExists(mutation, "items") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "items")) : 0;
                  
                  if(listItems > 0) str += addCSSClass("Arrays.asList(", LISTS_BLOCK_CSS_CLASS, blockId);
                  
                  for(int i = 0; i < listItems; ++i) {
                    childBlock = getChildBlockNamed(n, "ADD" + i);
                    childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                    
                    if(i != 0) str += addCSSClass(", ", LISTS_BLOCK_CSS_CLASS, blockId);
                    str += addInnerSelectionClass(childText, blockId);
                  }
                  
                  if(listItems > 0) str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                }
                                
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_add_items:
                mutation = getChildOfType(n, "mutation", 0);
                childBlock = getChildBlockNamed(n, "LIST");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";

                // TODO: decide whether it would be best to use Arrays.asList() or separate lines
                
                if(mutation != null) {
                  int listAddItems = getAttributeValueIfExists(mutation, "items") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "items")) : 0;
                  
                  for(int i = 0; i < listAddItems; ++i) {
                    Node listAddChild = getChildBlockNamed(n, "ITEM" + i);
                    String listAddText = listAddChild != null ? visitNode(listAddChild, depth) : "/* value */";
                    
                    str += indent(depth) + addInnerSelectionClass(childText, blockId);
                    str += addCSSClass(".add(", LISTS_BLOCK_CSS_CLASS, blockId);
                    str += addInnerSelectionClass(listAddText, blockId);
                    str += addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId);
                  }
                }
                
                skipChildren = true;
                
                break;
              case lists_is_in:
                leftChild = getChildBlockNamed(n, "ITEM");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "LIST");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(".contains(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_length:
                childBlock = getChildBlockNamed(n, "LIST");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".size()", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_is_empty:
                childBlock = getChildBlockNamed(n, "LIST");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".isEmpty()", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_pick_random_item:
                childBlock = getChildBlockNamed(n, "LIST");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".get(" + RANDOM_GENERATOR_NAME + ".nextInt(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(".size()))", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_position_in:
                leftChild = getChildBlockNamed(n, "ITEM");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "LIST");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(".indexOf(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_select_item:
                leftChild = getChildBlockNamed(n, "LIST");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "NUM");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(".get(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_insert_item:
                Node listInsertList = getChildBlockNamed(n, "LIST");
                Node listInsertIndex = getChildBlockNamed(n, "INDEX");
                Node listInsertItem = getChildBlockNamed(n, "ITEM");
                String listInsertListText = listInsertList != null ? visitNode(listInsertList, depth) : "/* value */";
                String listInsertIndexText = listInsertIndex != null ? visitNode(listInsertIndex, depth) : "/* value */";
                String listInsertItemText = listInsertItem != null ? visitNode(listInsertItem, depth) : "/* value */";
                
                str += indent(depth) + addInnerSelectionClass(listInsertListText, blockId);
                str += addCSSClass(".add(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(listInsertIndexText, blockId);
                str += addCSSClass(", ", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(listInsertItemText, blockId);
                str += addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId);

                skipChildren = true;
                
                break;
              case lists_replace_item:
                Node listReplaceItemList = getChildBlockNamed(n, "LIST");
                Node listReplaceItemIndex = getChildBlockNamed(n, "NUM");
                Node listReplaceItemReplacement = getChildBlockNamed(n, "ITEM");
                String listReplaceItemListText = listReplaceItemList != null ? visitNode(listReplaceItemList, depth) : "/* value */";
                String listReplaceItemIndexText = listReplaceItemIndex != null ? visitNode(listReplaceItemIndex, depth) : "/* value */";
                String listReplaceItemReplacementText = listReplaceItemReplacement != null ? visitNode(listReplaceItemReplacement, depth) : "/* value */";
                
                str += indent(depth) + addInnerSelectionClass(listReplaceItemListText, blockId);
                str += addCSSClass(".set(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(listReplaceItemIndexText, blockId);
                str += addCSSClass(", ", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(listReplaceItemReplacementText, blockId);
                str += addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_remove_item:
                leftChild = getChildBlockNamed(n, "LIST");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "INDEX");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += indent(depth) + addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(".remove(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_append_list:
                leftChild = getChildBlockNamed(n, "LIST0");
                leftChildText = leftChild != null ? visitNode(leftChild, depth) : "/* value */";
                rightChild = getChildBlockNamed(n, "LIST1");
                rightChildText = rightChild != null ? visitNode(rightChild, depth) : "/* value */";
                
                str += indent(depth) + addInnerSelectionClass(leftChildText, blockId);
                str += addCSSClass(".addAll(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(rightChildText, blockId);
                str += addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_copy:
                childBlock = getChildBlockNamed(n, "LIST");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                str += addCSSClass("new ArrayList(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_is_list:
                childBlock = getChildBlockNamed(n, "ITEM");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";

                str += addCSSClass("(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(" instanceof ArrayList)", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_to_csv_row:
                childBlock = getChildBlockNamed(n, "LIST");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                // if we allow Android libraries:
                // addImport(android.text.TextUtils);
                // str += addCSSClass("\" + TextUtils.join("\", \"", ", LISTS_BLOCK_CSS_CLASS, blockId);
                // str += addInnerSelectionClass(childText, blockId);
                // str += addCSSClass("\"", LISTS_BLOCK_CSS_CLASS, blockId);
                
                addImport(ARRAYLIST_IMPORT_PATH, blockId);
                
                // TODO: separate addFunction into pieces
                
                String listToCSVRowFunc = "";
                listToCSVRowFunc += "private String _listToCSVRow(ArrayList list) {\n";
                listToCSVRowFunc += "  String result = \"\";\n";
                listToCSVRowFunc += "\n";
                listToCSVRowFunc += "  for(int i = 0; i < list.size(); ++i) {\n";
                listToCSVRowFunc += "    if(i != 0) result += \", \";\n";
                listToCSVRowFunc += "    result += \"\\\"\" + list.get(i) + \"\\\"\";\n";
                listToCSVRowFunc += "  }\n";
                listToCSVRowFunc += "\n";
                listToCSVRowFunc += "  return result;\n";
                listToCSVRowFunc += "}\n";
                
                addFunction(listToCSVRowFunc, blockId);
                
                str += addCSSClass("_listToCSVRow(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_to_csv_table:
                //childBlock = getChildBlockNamed(n, "LIST");
                //childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                childText = getChildText(n, depth, "LIST");
                
                addImport(ARRAYLIST_IMPORT_PATH, blockId);
                
                // TODO: separate addFunction into pieces
                
                String listToCSVTableFunc = "";
                listToCSVTableFunc += "private String _listToCSVTable(ArrayList list) {\n";
                listToCSVTableFunc += "  String result = \"\";\n";
                listToCSVTableFunc += "\n";
                listToCSVTableFunc += "  for(int i = 0; i < list.size(); ++i) {\n";
                listToCSVTableFunc += "    ArrayList rowlist = (ArrayList) list.get(i);\n";
                listToCSVTableFunc += "\n";
                listToCSVTableFunc += "    for(int j = 0; j < rowlist.size(); ++j) {\n";
                listToCSVTableFunc += "      if(j != 0) result += \", \";\n";
                listToCSVTableFunc += "      result += \"\\\"\" + rowlist.get(j) + \"\\\"\";\n";
                listToCSVTableFunc += "    }\n";
                listToCSVTableFunc += "\n";
                listToCSVTableFunc += "    result += \"\\r\\n\";\n";
                listToCSVTableFunc += "  }\n";
                listToCSVTableFunc += "\n";
                listToCSVTableFunc += "  return result;\n";
                listToCSVTableFunc += "}\n";
                
                addFunction(listToCSVTableFunc, blockId);
                
                str += addCSSClass("_listToCSVTable(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case lists_from_csv_row:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                // TODO: might need to convert any numbers to number types... maybe same with lists
                
                // TODO: add opencsv
                // TODO: determine what I'd need to do for the license for opencsv
                
                addImport("au.com.bytecode.opencsv.CSVReader", blockId);
                addImport("java.io.StringReader", blockId);
                
                //TODO: addJarFile(opencsv-3.0.jar, blockId);
                
                String listFromCSVRow = "";
                listFromCSVRow += "private ArrayList _listFromCSVRow(String row) throws Exception {\n";
                listFromCSVRow += "  CSVReader csvreader = new CSVReader(new StringReader(row));\n";
                listFromCSVRow += "  List<String[]> parsedRow = csvreader.readAll();\n";
                listFromCSVRow += "\n";
                listFromCSVRow += "  if(parsedRow != null && parsedRow.size() == 1) {\n";
                listFromCSVRow += "    \n";
                listFromCSVRow += "    return Arrays.asList(parsedRow.get(0));\n";
                listFromCSVRow += "  } else if(parsedRow.size() == 0) {\n";
                listFromCSVRow += "    return Arrays.asList(\"\");\n";
                listFromCSVRow += "  } else {\n";
                listFromCSVRow += "    throw new IllegalArgumentException(\"Could not parse row\");\n";
                listFromCSVRow += "  }\n";
                listFromCSVRow += "}\n";
                
                addFunction(listFromCSVRow, blockId);
                
                str += addCSSClass("_listFromCSVRow(", LISTS_BLOCK_CSS_CLASS, blockId);
                str += addInnerSelectionClass(childText, blockId);
                str += addCSSClass("", LISTS_BLOCK_CSS_CLASS, blockId);
                
                // add them all as strings?
                
                break;
              case lists_from_csv_table:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                break;
              case lexical_variable_get:
                break;
              case lexical_variable_set:
                break;
                
              /* Procedures blocks */
              case procedures_defnoreturn:
                
                break;
            }
          } catch(IllegalArgumentException e) {
            str += makeColorSpan("// UNHANDLED BLOCK TYPE: " + blockType + "\n", "red");
          }
        }

        if(!skipChildren) {
          for(int i = 0; n.hasChildNodes() && i < n.getChildNodes().getLength(); ++i) {
            str += visitNode(n.getChildNodes().item(i), depth + 1);
          }
        } else {
          skipChildren = false;

          // vvv MAKE SURE TO LEAVE THIS WHEN YOU REMOVE THE SKIPCHILDREN VARIABLE vvv
          
          Node nextBlock = getChildOfType(getChildOfType(n, "next", 0), "block", 0);
          if(nextBlock != null) str += visitNode(nextBlock, depth);

          // ^^^ MAKE SURE TO LEAVE THIS WHEN YOU REMOVE THE SKIPCHILDREN VARIABLE ^^^
        }
        
        return str;
        
      default:
        return "Found uncaught Node type: " + n.getNodeType() + " for node: " + n.getNodeName() + "\n";
    }
  }
  
  private String blockToMathOperator(Node block) {
    HashMap<String, String> operators = new HashMap<String, String>();
    operators.put("EQ", "==");
    operators.put("NEQ", "!=");
    operators.put("LT", "<");
    operators.put("LTE", "<=");
    operators.put("GT", ">");
    operators.put("GTE", ">=");
    
    return operators.get(getChildOfType(block, "title", 0).getFirstChild().getNodeValue());
  }
  
  private String blockToLogicOperator(Node block) {
    HashMap<String, String> operators = new HashMap<String, String>();
    operators.put("EQ", "==");
    operators.put("NEQ", "!=");
    operators.put("AND", "&&");
    operators.put("OR", "||");
    
    return operators.get(getChildOfType(block, "title", 0).getFirstChild().getNodeValue());
  }
  
  private String xmlTextOperationToJava(String operation) {
    if(operation == null) return null;
    
    HashMap<String, String> operators = new HashMap<String, String>();
    operators.put("EQUAL", "==");
    operators.put("LT", "<");
    operators.put("GT", ">");
    
    return operators.get(operation);
  }
  
  private String xmlTextCaseChangeToJava(String operation) {
    if(operation == null) return null;
    
    if(operation.compareTo("UPCASE") == 0) return ".toUpperCase()";
    if(operation.compareTo("DOWNCASE") == 0) return ".toLowerCase()";
    
    return makeColorSpan("/* UNHANDLED CASE CHANGE TYPE */", "red");
  }
  
  private String operationToMathPretext(String op) {
    HashMap<String, String> operators = new HashMap<String, String>();
    operators.put("ROOT", "Math.sqrt(");
    operators.put("ABS", "Math.abs(");
    operators.put("NEG", "-(");
    operators.put("LN", "Math.log(");
    operators.put("EXP", "Math.exp(");
    operators.put("ROUND", "Math.round(");
    operators.put("CEILING", "Math.ceil(");
    operators.put("FLOOR", "Math.floor(");
    
    return operators.get(op);
  }
  
  private String blockToMathListType(Node block) {
    return getChildOfType(block, "title", 0).getFirstChild().getNodeValue();
  }
  
  private String blockToMathSingleType(Node block) {
    return getChildOfType(block, "title", 0).getFirstChild().getNodeValue();
  }
  
  private Node getChildOfType(Node n, String name, int sibling) {
    if(n == null) return null;
    
    int siblingCounter = 0;
    
    if(n.hasChildNodes()) {
      for(int i = 0; i < n.getChildNodes().getLength(); ++i) {
        Node child = n.getChildNodes().item(i);
        
        if(child.getNodeName().compareTo(name) == 0) {
          if(siblingCounter == sibling) {
            return child;
          } else {
            siblingCounter++;
          }
        }
      }
      
      return null;
    } else {
      return null;
    }
  }
  
  private int childrenOfType(Node n, String name) {
    int count = 0;
    
    if(n.hasChildNodes()) {
      for(int i = 0; i < n.getChildNodes().getLength(); ++i) {
        Node child = n.getChildNodes().item(i);
        
        if(child.getNodeName().compareTo(name) == 0) {
            count++;
        }
      }
      
    }
    
    return count;
  }
  
  private Node getChildWithAttrValue(Node n, String nodeName, String attr, String value) {
    if(n == null) return null;
    
    if(n.hasChildNodes()) {
      for(int i = 0; i < n.getChildNodes().getLength(); ++i) {
        if(n.getChildNodes().item(i).getNodeName().compareTo(nodeName) == 0 &&
            n.getChildNodes().item(i).getAttributes() != null &&
            n.getChildNodes().item(i).getAttributes().getNamedItem(attr) != null &&
            n.getChildNodes().item(i).getAttributes().getNamedItem(attr).getNodeValue().compareTo(value) == 0) {
          return n.getChildNodes().item(i);
        }
      }
    }
    
    return null;
  }
  
  private Node getChildBlockNamed(Node n, String name) {
    //return getChildOfType(getChildWithAttrValue(n, "value", "name", name), "block", 0);
    return getChildBlockNamed(n, name, "value");
  }
  
  private Node getChildBlockNamed(Node n, String name, String subblockName) {
    return getChildOfType(getChildWithAttrValue(n, subblockName, "name", name), "block", 0);
  }
  
  private String getChildText(Node n, int depth, String name) {
    return getChildText(n, depth, name, "/* value */");
  }
  
  private String getChildText(Node n, int depth, String name, String defaultText) {
    //Node childBlock = getChildBlockNamed(n, name);
    //return childBlock != null ? visitNode(childBlock, depth) : defaultText;
    return getChildText(n, depth, name, defaultText, null);
  }
  
  private String getChildText(Node n, int depth, String name, String defaultText, String subblockName) {
    Node childBlock = subblockName == null ? getChildBlockNamed(n, name) : getChildBlockNamed(n, name, subblockName);
    return childBlock != null ? visitNode(childBlock, depth) : defaultText;
  }
  
  private String getTitleFromBlock(Node n) {
    Node titleChild = getChildOfType(n, "title", 0);
    
    if(titleChild == null || titleChild.getFirstChild() == null) return null;
    
    return titleChild.getFirstChild().getNodeValue();
  }
  
  private String checkUncaughtChildren(Node n, final String[] childTypes) {
    String str = "";
    boolean foundType;
    
    for(int i = 0; n.hasChildNodes() && i < n.getChildNodes().getLength(); ++i) {
      Node child = n.getChildNodes().item(i);
      String childName = child.getNodeName();
      foundType = false;
      
      for(int j = 0; j < childTypes.length; ++j) {
        if(childName.compareTo(childTypes[j]) == 0) {
          foundType = true;
          break;  // found it in the list
        }
      }
      
      if(!foundType) {
        str += makeColorSpan("// UNCAUGHT CHILD NAME: " + childName + "\n", "red");
      }
    }
    
    return str;
  }
  
  // TODO: tie imports and globals to blocks for highlighting
  private void addImport(String importPath, int blockId) {
    if(!imports.containsKey(importPath)) {
      Set<Integer> s = new HashSet<Integer>();
      s.add(new Integer(blockId));
      imports.put(importPath, s);
    } else {
      imports.get(importPath).add(new Integer(blockId));
    }
  }
  
  private String buildImports() {
    String importStr = "";
    
    for(String str : imports.keySet()) {
      Set<Integer> s = imports.get(str);
      String thisImport = "import " + str + ";\n";
      
      for(Integer i : s) {
        thisImport = addSelectionClass(thisImport, i);
      }
      
      importStr += thisImport;
    }
    
    return importStr;
  }
  
  private void addGlobal(String globalDefinition, int blockId) {
    if(!globals.containsKey(globalDefinition)) {
      Set<Integer> s = new LinkedHashSet<Integer>();
      s.add(new Integer(blockId));
      globals.put(globalDefinition, s);
    } else {
      globals.get(globalDefinition).add(new Integer(blockId));
    }
  }
  
  private String buildGlobals() {
    String globalStr = "";
    
    for(String str : globals.keySet()) {
      Set<Integer> s = globals.get(str);
      String thisGlobal = str + "\n";
      
      for(Integer i : s) {
        thisGlobal = addSelectionClass(thisGlobal, i);
      }
      
      globalStr += thisGlobal;
    }
    
    return globalStr;
  }
  
  private void addFunction(String functionDefinition, int blockId) {
    if(!functions.containsKey(functionDefinition)) {
      Set<Integer> s = new LinkedHashSet<Integer>();
      s.add(new Integer(blockId));
      functions.put(functionDefinition, s);
    } else {
      functions.get(functionDefinition).add(new Integer(blockId));
    }
  }
  
  private String buildFunctions() {
    String functionStr = "";
    
    for(String str : functions.keySet()) {
      Set<Integer> s = functions.get(str);
      String thisFunction = str + "\n";
      
      for(Integer i : s) {
        thisFunction = addSelectionClass(thisFunction, i);
      }
      
      functionStr += thisFunction;
    }
    return functionStr;
  }
  
  private String indent(int depth) {
    return multiplyChars(' ', depth * 2);
  }
  
  private String multiplyChars(char c, int num) {
    char[] chars = new char[num];
    Arrays.fill(chars, c);
    return new String(chars);
  }
  
  private String makeAttributeString(Node n) {
    String str = "";
    
    if(n == null || n.getAttributes().getLength() == 0) return str;
    
    for(int i = 0; i < n.getAttributes().getLength(); ++i) {
      str += makeColorSpan(n.getAttributes().item(i).getNodeName(), "#994500") + ": " + 
             makeColorSpan(n.getAttributes().item(i).getNodeValue(), "#1a1aa6") + ", ";
    }
    
    return str.substring(0, str.length() - 2);
  }
  
  /**
   * Adds class to blocks based on the type of block.
   * 
   * @param cc  CSS class to add to block of text
   */
  private String addCSSClass(String str, String cc, int blockId) {
    cc += ((blockId == selectedBlockId) ? " " + SELECTED_BLOCK_CSS_CLASS : "");
    return "<span class='" + cc + "'>" + str + "</span>";
  }
  
  private String addCSSClass(String str, String cc) {
    return "<span class='" + cc + "'>" + str + "</span>";
  }
  
  private String addSelectionClass(String str, int blockId) {
    return "<span class='" + ((blockId == selectedBlockId) ? " " + SELECTED_BLOCK_CSS_CLASS : "") + "'>" + str + "</span>";
  }
  
  private String addInnerSelectionClass(String str, int blockId) {
    return "<span class='" + ((blockId == selectedBlockId) ? " " + SELECTED_INNER_BLOCK_CSS_CLASS : "") + "'>" + str + "</span>";
  }
  
  private String makeColorSpan(String str, String color) {
    return "<span style='color:" + color + "'>" + str + "</span>";
  }

  /**
   * Clears the text in the code buffer.
   */
  public void clear() {
    codeData = "";
    codeLabel.setHTML("");
  }
  
  /**
   * Switches categories during transition between Designer and Blocks screens.
   */
  public void switchScreens(boolean blocks) {
    if(blocks) {
      componentName.setText("Activity Code");
    } else {
      componentName.setText("UI XML Code");
    }
  }
  
  private String htmlify(String s) {
    return s.replaceAll("<",  "&lt;").replaceAll(">", "&gt;");
  }
}
