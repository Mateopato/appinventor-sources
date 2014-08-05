package com.google.appinventor.client.widgets.codeinventor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.google.appinventor.client.editor.youngandroid.YaBlocksEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
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
    text_join,
    lexical_variable_get,
    lexical_variable_set,
    procedures_defnoreturn,
  }
  
  public enum EventNames {
    Initialize,   // screen initialization
    Click,        // button click
  }
  
  // TODO: probably don't need these since it's handled in CSS
  /*private static final String CONTROL_BLOCK_COLOR = "#b18e35";
  private static final String LOGIC_BLOCK_COLOR = "#77ab41";
  private static final String MATH_BLOCK_COLOR = "#3f71b5";
  private static final String TEXT_BLOCK_COLOR = "#b32d5e";
  private static final String LISTS_BLOCK_COLOR = "#49a6d4";
  private static final String COLORS_BLOCK_COLOR = "#7d7d7d";
  private static final String VARIABLES_BLOCK_COLOR = "#d05f2d";
  private static final String PROCEDURES_BLOCK_COLOR = "#7c5385";
  private static final String GETTER_BLOCK_COLOR = "#439970";
  private static final String SETTER_BLOCK_COLOR = "#266643";
  private static final String ARGUMENTS_BLOCK_COLOR = "#de8f6c";*/
  
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
  
  private static final String SELECTED_BLOCK_CSS_CLASS = "selectedblock";
  
  private static final String INCLUDE_DELIMITER = "--INCLUDES--";
  private static final String GLOBALS_DELIMITER = "--GLOBALS--";
  
  private static final String RANDOM_INCLUDE_PATH = "java.util.Random";
  private static final String RANDOM_GENERATOR_NAME = "randomGenerator";
  private static final String RANDOM_GENERATOR_INIT_PREFIX = "Random ";
  private static final String RANDOM_GENERATOR_INIT_SUFFIX = " = new Random();";
  
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
  
  private Set<String> includes = new TreeSet<String>();
  private Set<String> globals = new LinkedHashSet<String>();
  
  private boolean skipChildren = false;
  
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
   */
  public void changeSelection(int selected) {
    //codeLabel.setHTML("Block " + selected + " is selected\n" + codeData + "");
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
      
      codeData += INCLUDE_DELIMITER + "\n" + GLOBALS_DELIMITER + "\n"; 
      
      codeData += visitNode(doc.getFirstChild(), 0);
      
      codeData = codeData.replace(INCLUDE_DELIMITER, buildIncludes());
      codeData = codeData.replace(GLOBALS_DELIMITER, buildGlobals());
      
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
  
  String blockType = "";
  
  private String visitNode(Node n, int depth) {
    // determine the type of the XML node
    switch(n.getNodeType()) {
      case Node.TEXT_NODE:
        if(showXML) {
          if(n.getNodeValue().trim().isEmpty()) return "";
          
          return ": " + n.getNodeValue().trim() + "\n";
        } else {
          return "";
        }
      case Node.ELEMENT_NODE:
        String str = showXML ? indent(depth) + makeColorSpan(n.getNodeName(), "#881280") + " (" + makeAttributeString(n) + ")" : "";
        String postText = "";
        
        if(showXML && n.getNodeName().compareTo("title") != 0) str += "\n";
        
        if(n.getNodeName().compareTo("block") == 0) {
          int blockId = Integer.parseInt(getAttributeValueIfExists(n, "id"));
          blockType = getAttributeValueIfExists(n, "type");
          
          if(blockType.compareTo("NO_SUCH_ATTRIBUTE") == 0) {
            str += makeColorSpan("// NO BLOCK TYPE... WEIRD.\n", "red");
          }
          
          try {
            Node mutation;
            
            switch(BlockTypes.valueOf(blockType)) {
              /* when event block */
              case component_event:
                /*for(int i = 0; n.hasChildNodes() && i < n.getChildNodes().getLength(); ++i) {
                  Node child = n.getChildNodes().item(i);
                  String childName = child.getNodeName();
                  
                  if(childName.compareTo("mutation") == 0) {            // provides the necessary info about the event
                    String eventName = getAttributeValueIfExists(child, "event_name");
                    String instanceName = getAttributeValueIfExists(child, "instance_name");
                    
                    str += indent(depth) + addCSSClass("when " + instanceName + "." + eventName + "() {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                  } else if(childName.compareTo("title") == 0) {       // info is stored in <mutation> blocks anyway
                  } else if(childName.compareTo("statement") == 0) {   // this is the body of the event block
                  } else if(childName.compareTo("#text") == 0) {       // ignore
                  } else {
                    str += makeColorSpan("// UNCAUGHT CHILD NAME: " + childName + "\n", "red");
                  }
                }
                
                postText = indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                */
                mutation = getChildOfType(n, "mutation", 0);
                
                if(mutation != null) {
                  String eventName = getAttributeValueIfExists(mutation, "event_name");
                  String instanceName = getAttributeValueIfExists(mutation, "instance_name");
                  
                  str += indent(depth) + addCSSClass("when " + instanceName + "." + eventName + "() {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                } else {
                  str += makeColorSpan("// NO MUTATION CHILD FOUND\n", "red");
                }
                
                postText = indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "title", "statement", "#text"});
                
                break;
              /* component getter or setter */
              case component_set_get:
                /*for(int i = 0; n.hasChildNodes() && i < n.getChildNodes().getLength(); ++i) {
                  Node child = n.getChildNodes().item(i);
                  String childName = child.getNodeName();
                  
                  if(childName.compareTo("mutation") == 0) {
                    String componentType = getAttributeValueIfExists(child, "component_type");   // e.g., Label
                    String propertyName = getAttributeValueIfExists(child, "property_name");     // e.g., BackgroundColor
                    String instanceName = getAttributeValueIfExists(child, "instance_name");     // e.g., Label1 (custom name)
                    boolean isSet = getAttributeValueIfExists(child, "set_or_get").compareTo("set") == 0;
                    // is_generic
                    
                    if(isSet) {
                      str += indent(depth) + addCSSClass(instanceName + ".set" + propertyName + "(", SETTER_BLOCK_CSS_CLASS, blockId);
                      postText = addCSSClass(")\n", SETTER_BLOCK_CSS_CLASS, blockId);
                    } else {
                      str += addCSSClass(instanceName + ".get" + propertyName + "()", GETTER_BLOCK_CSS_CLASS, blockId);
                    }
                  } else if(childName.compareTo("title") == 0) {   // info is stored in <mutation> blocks
                  } else if(childName.compareTo("value") == 0) {   // this is where the child data is
                  } else if(childName.compareTo("next") == 0) {    // if there's a block after this one
                  } else if(childName.compareTo("#text") == 0) {   // ignore
                  } else {
                    str += makeColorSpan("// UNCAUGHT CHILD NAME: " + childName + "\n", "red");
                  }
                }*/
                
                mutation = getChildOfType(n, "mutation", 0);
                
                if(mutation != null) {
                  String componentType = getAttributeValueIfExists(mutation, "component_type");   // e.g., Label
                  String propertyName = getAttributeValueIfExists(mutation, "property_name");     // e.g., BackgroundColor
                  String instanceName = getAttributeValueIfExists(mutation, "instance_name");     // e.g., Label1 (custom name)
                  boolean isSet = getAttributeValueIfExists(mutation, "set_or_get").compareTo("set") == 0;
                  // is_generic
                  
                  if(isSet) {
                    str += indent(depth) + addCSSClass(instanceName + ".set" + propertyName + "(", SETTER_BLOCK_CSS_CLASS, blockId);
                    postText = addCSSClass(");\n", SETTER_BLOCK_CSS_CLASS, blockId);
                  } else {
                    str += addCSSClass(instanceName + ".get" + propertyName + "()", GETTER_BLOCK_CSS_CLASS, blockId);
                  }
                } else {
                  str += makeColorSpan("// NO MUTATION CHILD FOUND\n", "red");
                }
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "title", "value", "next", "#text"});
                
                break;
              /* if block */
              case controls_if:
                mutation = getChildOfType(n, "mutation", 0);

                if(mutation != null) {
                  int elseifs = getAttributeValueIfExists(mutation, "elseif") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "elseif")) : 0;
                  int elses = getAttributeValueIfExists(mutation, "else") != null ? Integer.parseInt(getAttributeValueIfExists(mutation, "else")) : 0;
                  
                  // handle all if and else if blocks
                  for(int i = 0; i <= elseifs; ++i) {
                    Node ifblock = getChildWithAttrValue(n, "value", "name", "IF" + i);
                    Node doblock = getChildWithAttrValue(n, "statement", "name", "DO" + i);
                    
                    str += (i == 0 ? indent(depth) : "") + addCSSClass((i > 0 ? " else " : "") + "if(", CONTROL_BLOCK_CSS_CLASS, blockId);
                    str += visitIfBlock(ifblock, i, depth);
                    str += addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                    str += visitDoBlock(doblock, depth);
                    str += indent(depth) + addCSSClass("}", CONTROL_BLOCK_CSS_CLASS, blockId);
                  }
                  
                  // handle else block
                  if(elses == 1) {
                    Node doblock = getChildWithAttrValue(n, "statement", "name", "ELSE");
                    
                    str += addCSSClass(" else {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                    str += visitDoBlock(doblock, depth);
                    str += indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                  } else {
                    str += "\n";
                  }
                // simple if statement with no if/else
                } else {
                  Node ifblock = getChildWithAttrValue(n, "value", "name", "IF0");
                  Node doblock = getChildWithAttrValue(n, "statement", "name", "DO0");
                  
                  str += indent(depth) + addCSSClass("if(", CONTROL_BLOCK_CSS_CLASS, blockId);
                  str += visitIfBlock(ifblock, 0, depth);
                  str += addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                  str += visitDoBlock(doblock, depth);
                  str += indent(depth) + addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId);
                }
                
                skipChildren = true;
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "value", "statement", "next", "#text"});
                
                break;
                
              /* Logic blocks */
              case logic_boolean:
                String logicBoolVal = getChildWithAttrValue(n, "title", "name", "BOOL").getFirstChild().getNodeValue();
                str += addCSSClass(logicBoolVal.toLowerCase(), LOGIC_BLOCK_CSS_CLASS, blockId);
                
                break;
              case logic_false:
                String logicFalseVal = getChildWithAttrValue(n, "title", "name", "BOOL").getFirstChild().getNodeValue();
                str += addCSSClass(logicFalseVal.toLowerCase(), LOGIC_BLOCK_CSS_CLASS, blockId);
                
                break;
              case logic_negate:
                str += addCSSClass("!", LOGIC_BLOCK_CSS_CLASS, blockId);
                
                break;
              case logic_compare:
                Node leftLogic = getChildWithAttrValue(n, "value", "name", "A");
                Node rightLogic = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", LOGIC_BLOCK_CSS_CLASS, blockId);
                str += leftLogic != null ? visitNode(leftLogic, depth) : "/* left side of comparison */";
                str += " " + addCSSClass(blockToLogicOperator(n), LOGIC_BLOCK_CSS_CLASS, blockId) + " ";
                str += rightLogic != null ? visitNode(rightLogic, depth) : "/* right side of comparison */";
                str += addCSSClass(")", LOGIC_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case logic_operation:
              case logic_or:
                Node leftAnd = getChildWithAttrValue(n, "value", "name", "A");
                Node rightAnd = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", LOGIC_BLOCK_CSS_CLASS, blockId);
                str += leftAnd != null ? visitNode(leftAnd, depth) : "/* left side of logic operation */";
                str += " " + addCSSClass(blockToLogicOperator(n), LOGIC_BLOCK_CSS_CLASS, blockId) + " ";
                str += rightAnd != null ? visitNode(rightAnd, depth) : "/* right side of logic operation */";
                str += addCSSClass(")", LOGIC_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
                
              /* Math blocks */
              case math_number:
                String number = getChildWithAttrValue(n, "title", "name", "NUM").getFirstChild().getNodeValue();
                str += addCSSClass(number, MATH_BLOCK_CSS_CLASS, blockId);
                
                break;
              case math_compare:
                Node left = getChildWithAttrValue(n, "value", "name", "A");
                Node right = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += left != null ? visitNode(left, depth) : "/* left side of comparison */";
                str += " " + addCSSClass(blockToMathOperator(n), MATH_BLOCK_CSS_CLASS, blockId) + " ";
                str += right != null ? visitNode(right, depth) : "/* right side of comparison */";
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
                  
                  str += addChild != null ? visitNode(addChild, depth) : "/* no value specified */"; 
                }

                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_subtract:
                Node minuend = getChildWithAttrValue(n, "value", "name", "A");
                Node subtrahend = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += minuend != null ? visitNode(minuend, depth) : "/* no value specified */";
                str += addCSSClass(" - ", MATH_BLOCK_CSS_CLASS, blockId);
                str += subtrahend != null ? visitNode(subtrahend, depth) : "/* no value specified */";
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
                  
                  str += multiplyChild != null ? visitNode(multiplyChild, depth) : "/* no value specified */"; 
                }

                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_division:
                Node dividend = getChildWithAttrValue(n, "value", "name", "A");
                Node divisor = getChildWithAttrValue(n, "value", "name", "B");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId);
                str += dividend != null ? visitNode(dividend, depth) : "/* no value specified */";
                str += addCSSClass(" / ", MATH_BLOCK_CSS_CLASS, blockId);
                str += divisor != null ? visitNode(divisor, depth) : "/* no value specified */";
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_power:
                Node base = getChildWithAttrValue(n, "value", "name", "A");
                Node power = getChildWithAttrValue(n, "value", "name", "B");
                
                // TODO: Math.pow returns a double, may need to cast as an int
                str += addCSSClass("Math.pow(", MATH_BLOCK_CSS_CLASS, blockId);
                str += base != null ? visitNode(base, depth) : "/* no value specified */";
                str += addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId);
                str += power != null ? visitNode(power, depth) : "/* no value specified */";
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_random_int:
                Node randomMin = getChildWithAttrValue(n, "value", "name", "FROM");
                Node randomMax = getChildWithAttrValue(n, "value", "name", "TO");

                addInclude(RANDOM_INCLUDE_PATH);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX);
                // TODO: get rid of _randomGenerator if the last block using it is deleted!
                
                str += addCSSClass("(" + RANDOM_GENERATOR_NAME + ".nextInt(", MATH_BLOCK_CSS_CLASS, blockId);
                str += randomMax != null ? visitNode(randomMax, depth) : "/* random max */";
                str += addCSSClass(" - ", MATH_BLOCK_CSS_CLASS, blockId);
                str += randomMin != null ? visitNode(randomMin, depth) : "/* random min */";
                str += addCSSClass(") + ", MATH_BLOCK_CSS_CLASS, blockId);
                str += randomMin != null ? visitNode(randomMin, depth) : "/* random min */";
                str += addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId);
                
                skipChildren = true;
                
                break;
              case math_random_float:
                addInclude(RANDOM_INCLUDE_PATH);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX);
                
                str+= addCSSClass(RANDOM_GENERATOR_NAME + ".nextDouble()", MATH_BLOCK_CSS_CLASS, blockId);

                break;
              case math_random_set_seed:
                break;
                
              /* Text blocks */
              case text_join:
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
            if(blockType.startsWith("color_")) {
              for(int i = 0; n.hasChildNodes() && i < n.getChildNodes().getLength(); ++i) {
                Node child = n.getChildNodes().item(i);
                String childName = child.getNodeName();
                
                if(childName.compareTo("title") == 0) {
                  if(child.hasChildNodes() && child.getChildNodes().item(0).getNodeName().compareTo("#text") == 0) {
                    str += indent(depth) + makeColorSpan("// FOUND COLOR: ", "#3f71b5") + makeColorSpan(child.getChildNodes().item(0).getNodeValue(), child.getChildNodes().item(0).getNodeValue()) + "\n";
                  }
                }
              }
            } else {
              str += makeColorSpan("// UNHANDLED BLOCK TYPE: " + blockType + "\n", "red");
            }
          }
          
          /*if(blockType.compareTo("component_event") == 0) {
          } else if(blockType.compareTo("component_set_get") == 0) {
            
          } else if(blockType.compareTo("controls_if") == 0) {
            
          } else if(blockType.compareTo("math_compare") == 0) {
            
          } else if(blockType.compareTo("math_number") == 0) {
            
          } else if(blockType.compareTo("logic_boolean") == 0) {
            
          } else if(blockType.compareTo("logic_operation") == 0) {
          
          } else if(blockType.compareTo("procedures_defnoreturn") == 0) {
            
          }*/
        }

        if(!skipChildren) {
          for(int i = 0; n.hasChildNodes() && i < n.getChildNodes().getLength(); ++i) {
            str += visitNode(n.getChildNodes().item(i), depth + 1);
          }
        } else {
          skipChildren = false;
          
          // VVV MAKE SURE TO LEAVE THIS WHEN YOU REMOVE THE SKIPCHILDREN VARIABLE VVV
          
          // need to handle next blocks here
          Node nextNode = getChildOfType(n, "next", 0);
          
          if(nextNode != null) {
            Node nextBlock = getChildOfType(nextNode, "block", 0);
            
            if(nextBlock != null) {
              str += visitNode(nextBlock, depth);
            }
          }

          // ^^^ MAKE SURE TO LEAVE THIS WHEN YOU REMOVE THE SKIPCHILDREN VARIABLE ^^^
        }
        
        if(!postText.isEmpty()) {
          str += postText;
        }
        
        return str;
        
      default:
        return "Found uncaught Node type: " + n.getNodeType() + " for node: " + n.getNodeName() + "\n";
    }
  }
  
  private String visitIfBlock(Node ifblock, int blockNumber, int depth) {
    String str = "";
    
    // if condition
    if(ifblock != null) {
      // do this inline
      //str += "condition" + blockNumber + " exists";
      str += visitNode(ifblock, depth);
    } else {
      str += "/* NO CONDITION" + blockNumber + " */";
    }
    
    return str;
  }
  
  private String visitDoBlock(Node doblock, int depth) {
    String str = "";
    
    // do block
    if(doblock != null) {
      // visit doblock (depth + 1)
      //str += indent(depth + 1) + "// DO" + blockNumber + " exists!\n";
      str += visitNode(doblock, depth);
    } else {
      str += indent(depth + 1) + "// nothing to do here!\n";
    }
    
    return str;
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
  
  private Node getChildOfType(Node n, String name, int sibling) {
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
  
  // TODO: tie includes and globals to blocks for highlighting
  private void addInclude(String includePath) {
    if(!includes.contains(includePath)) {
      includes.add(includePath);
    }
  }
  
  private String buildIncludes() {
    String includeStr = "";
    
    for(String str : includes) {
      includeStr += "include " + str + ";\n";
    }
    
    return includeStr;
  }
  
  private void addGlobal(String globalDefinition) {
    if(!globals.contains(globalDefinition)) {
      globals.add(globalDefinition);
    }
  }
  
  private String buildGlobals() {
    String globalStr = "";
    
    for(String str : globals) {
      globalStr += str + "\n";
    }
    
    return globalStr;
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
