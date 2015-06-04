package com.google.appinventor.client.widgets.codeinventor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.appinventor.client.Ode;
import com.google.appinventor.client.codeinventor.ButtonComponent;
import com.google.appinventor.client.codeinventor.HtmlWrapper;
import com.google.appinventor.client.output.OdeLog;
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

// TODO: Figure out if I need to do anything with IntFraction (see runtime.scm comment about call-component-method)
// appinventor-sources/appinventor/buildserver/src/com/google/appinventor/buildserver/resources/runtime.scm

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
    controls_forRange,
    controls_forEach,
    controls_while,
    controls_choose,
    controls_do_then_return,
    controls_eval_but_ignore,
    controls_openAnotherScreen,
    controls_openAnotherScreenWithStartValue,
    controls_getStartValue,
    controls_closeScreen,
    controls_closeScreenWithValue,
    controls_closeApplication,
    controls_getPlainStartText,
    controls_closeScreenWithPlainText,
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
    math_abs,
    math_neg,
    math_round,
    math_ceiling,
    math_floor,
    math_divide,
    math_trig,
    math_cos,
    math_tan,
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
  
  public enum PrimitiveType {
    Boolean,
    Text,
    Integer,
    Decimal,
    Number,
    List,
    Color,
    Unknown,
    None,
  }
  
  private static final String PACKAGE_NAME = "codeinventor.generated";
  
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
  private static final String SYSTEM_CSS_CLASS = "systemblock";

  private static final String SELECTED_BLOCK_CSS_CLASS = "selectedblock";
  private static final String SELECTED_INNER_BLOCK_CSS_CLASS = "selectedinnerblock";
  
  private static final String IMPORT_DELIMITER = "--IMPORTS--";
  private static final String GLOBALS_DELIMITER = "--GLOBALS--";
  private static final String FUNCTIONS_DELIMITER = "--FUNCTIONS--";
  private static final String COMPONENTS_DELIMITER = "--COMPONENTS--";
  
  private static final String OPEN_SCREEN_START_VALUE = "startValue";
  
  private static final String RANDOM_IMPORT_PATH = "java.util.Random";
  private static final String RANDOM_GENERATOR_NAME = "randomGenerator";
  private static final String RANDOM_GENERATOR_INIT_PREFIX = "Random ";
  private static final String RANDOM_GENERATOR_INIT_SUFFIX = " = new Random();";
  
  private static final String BIG_DECIMAL_IMPORT_PATH = "java.math.BigDecimal";

  private static final String ACTIVITY_IMPORT_PATH = "android.app.Activity";
  private static final String BUNDLE_IMPORT_PATH = "android.os.Bundle";
  
  private static final String ARRAYS_IMPORT_PATH = "java.util.Arrays";
  private static final String ARRAYLIST_IMPORT_PATH = "java.util.ArrayList";
  private static final String LIST_IMPORT_PATH = "java.util.List";
  private static final String PATTERN_IMPORT_PATH = "java.util.regex.Pattern";
  private static final String ANDROID_COLOR_IMPORT_PATH = "android.graphics.Color";
  
  private static final String BUTTON_IMPORT_PATH = "android.widget.Button";
  private static final String CANVAS_IMPORT_PATH = "android.graphics.Canvas";
  
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
  
  private String codeData = "";
  private String xmlData = "";
  private int selectedBlockId = 0;
  private boolean showXML = true;
  
  // TODO: check ordering of globals--was LinkedHashMap, changed to TreeMap for sorting. See which looks better.
  private Map<String, Set<Integer>> imports = new TreeMap<String, Set<Integer>>();
  private Map<String, Set<Integer>> globals = new TreeMap<String, Set<Integer>>();
  private Map<String, Set<Integer>> functions = new LinkedHashMap<String, Set<Integer>>();
  private Map<String, Set<Integer>> components = new TreeMap<String, Set<Integer>>();
  
  private Map<String, String> componentImportMap = new HashMap<String, String>(); // map component name to import statement
  //private Map<Integer, Node> nodeIdMap = new HashMap<Integer, Node>();
  
  private Map<String, Map<String, Node>> componentEvents = new TreeMap<String, Map<String, Node>>();
    
  private boolean skipChildren = false;
  
  /**
   * Creates a new Code Panel.
   */
  public CodePanel() {
    int panelWidth = 330;
    
    // TODO: move this to a function
    componentImportMap.put("Button", BUTTON_IMPORT_PATH);
    componentImportMap.put("Canvas", CANVAS_IMPORT_PATH);
    
    // Initialize UI
    //ScrollPanel outerPanel = new ScrollPanel();
    VerticalPanel outerPanel = new VerticalPanel();
    outerPanel.setWidth(panelWidth + "px");
    
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
    
    sb.append("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n");
    sb.append("  package=\"com.example.testapp\"\n");
    sb.append("  android:versionCode=\"1\"\n");
    sb.append("  android:versionName=\"1.0\" >\n\n");

    sb.append("  <uses-sdk\n");
    sb.append("    android:minSdkVersion=\"8\"\n");
    sb.append("    android:targetSdkVersion=\"17\" />\n\n");

    sb.append("  <application\n");
    sb.append("    android:allowBackup=\"true\"\n");
    sb.append("    android:icon=\"@drawable/ic_launcher\"\n");
    sb.append("    android:label=\"@string/app_name\"\n");
    sb.append("    android:theme=\"@style/AppTheme\" >\n");
    sb.append("  </application>\n\n");

    sb.append("</manifest>");
    
    test1 = new HTML("<pre>" + HtmlWrapper.htmlify(sb.toString()) + "</pre>");
    test2 = new Label("test2");
    firstHeaderScrollPanel.add(codeLabel);
    secondHeaderScrollPanel.add(test1);
    
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
    OdeLog.log("Selection changed: " + selected);
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
    //OdeLog.log("XML:\n\n" + text);
    
    try {
      Document doc = XMLParser.parse(codeData);
      //NodeList nl = doc.getElementsByTagName("*");
      //Node n;
      
      //nodeIdMap.clear();
      imports.clear();
      globals.clear();
      functions.clear();
      components.clear();
      componentEvents.clear();
      
      codeData = "<pre>";

      /*codeData += "The first child is: " + doc.getFirstChild().getNodeName() + ", and it has " + doc.getFirstChild().getChildNodes().getLength() + " children. \n\n";
      
      for(int i = 0; i < nl.getLength(); i++) {
        n = nl.item(i);
        codeData += makeColorSpan(n.getNodeName(), "#881280") + " (" + makeAttributeString(n) + ")\n";
      }*/
      
      // TODO: add user and project to package name (edu.clemson.cs.codeinventor.<user>.<project>)
      //   I can get this, but probably don't want it: OdeLog.wlog("User email is: " + Ode.getInstance().getUser().getUserEmail());
      String projectSuffix = getProjectName() != null ? "." + getProjectName() : "";
      codeData += HtmlWrapper.addCSSClass("package", SYSTEM_CSS_CLASS) + " edu.clemson.cs.codeinventor" + projectSuffix + ";\n\n";
      
      addImport(ACTIVITY_IMPORT_PATH, -2);
      addImport(BUNDLE_IMPORT_PATH, -2);
      
      codeData += IMPORT_DELIMITER;
      
      String screenName = getScreenName() != null ? getScreenName() : "";
      codeData += HtmlWrapper.addCSSClass("public class", SYSTEM_CSS_CLASS) + " " + screenName +" " + HtmlWrapper.addCSSClass("extends", SYSTEM_CSS_CLASS) + " Activity {\n";
      
      codeData += GLOBALS_DELIMITER;
      
      codeData += HtmlWrapper.indent(1) + "@Override\n";
      codeData += HtmlWrapper.indent(1) + HtmlWrapper.addCSSClass("protected void", SYSTEM_CSS_CLASS) + " onCreate(Bundle savedInstanceState) {\n";
      codeData += HtmlWrapper.indent(2) + HtmlWrapper.addCSSClass("super", SYSTEM_CSS_CLASS) + ".onCreate(savedInstanceState);\n";
      codeData += HtmlWrapper.indent(2) + "setContentView(R.layout.activity_" + screenName + ");\n\n"; // TODO: lowercase screen name?
      
      codeData += COMPONENTS_DELIMITER;
      
      codeData += visitNode(doc.getFirstChild(), 1); // depth increases by 1 before code starts
      
      codeData += processComponentEvents();  // TODO: add line space above if there's code in above visitNode call

      codeData += HtmlWrapper.indent(1) + "}\n"; // end onCreate
      codeData += "}\n";             // end class
      
      codeData += FUNCTIONS_DELIMITER;
      
      codeData = codeData.replace(IMPORT_DELIMITER, imports.isEmpty() ? "" : buildImports() + "\n");
      codeData = codeData.replace(GLOBALS_DELIMITER, globals.isEmpty() ? "" : buildGlobals() + "\n");
      codeData = codeData.replace(COMPONENTS_DELIMITER,  components.isEmpty() ? "" : buildComponents() + "\n");
      codeData = codeData.replace(FUNCTIONS_DELIMITER,  functions.isEmpty() ? "" : "\n" + buildFunctions());
      
      codeData += "</pre>";
    } catch(DOMParseException e) {
      codeData += e;
    }
    
    codeLabel.setHTML(codeData);
  }
  
  private String getAttributeValueIfExists(Node n, String attribute) {
    return getAttributeValueIfExists(n, attribute, null);
  }
  
  private String getAttributeValueIfExists(Node n, String attribute, String defaultValue) {
    if(n != null && n.hasAttributes() && n.getAttributes().getNamedItem(attribute) != null) {
      return n.getAttributes().getNamedItem(attribute).getNodeValue();
    }
    
    return defaultValue;
  }
  
  private String processComponentEvents() {
    String str = "";
    int depth = 2;
    
    for(String componentName : componentEvents.keySet()) {
      boolean focusDone = false;
      str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("/**\n" + HtmlWrapper.indent(depth) + " * " + componentName + " event handlers\n" + HtmlWrapper.indent(depth) + " */\n", COMMENT_CSS_CLASS);
      
      for(String eventName : componentEvents.get(componentName).keySet()) {
        Node n = componentEvents.get(componentName).get(eventName);
        Node mutation = getChildOfType(n, "mutation", 0);
        Node childBlock = getChildOfType(getChildWithAttrValue(n, "statement", "name", "DO"), "block", 0);
        
        //String eventName = getAttributeValueIfExists(mutation, "event_name");
        String instanceName = getAttributeValueIfExists(mutation, "instance_name");
        String componentType = getAttributeValueIfExists(mutation, "component_type");
        
        int blockId = Integer.parseInt(getAttributeValueIfExists(n, "id", "-2"));  // blocklySelectChange returns -1 if no block is selected
        
        if(componentImportMap.containsKey(componentType)) {
          addImport(componentImportMap.get(componentType), blockId);
        } else {
          OdeLog.wlog("MISSING COMPONENT TYPE: " + componentType);
        }
        
        addGlobal(componentType + " " + instanceName + ";", blockId);
        addComponent(instanceName + " = (" + componentType + ") findViewById(R.id." + instanceName + ");", blockId);
        
        if((eventName.equals("GotFocus") || eventName.equals("LostFocus"))) {
          if(!focusDone) {
            Node gotFocusChild = getChildOfType(getChildWithAttrValue(componentEvents.get(componentName).get("GotFocus"), "statement", "name", "DO"), "block", 0);
            Node lostFocusChild = getChildOfType(getChildWithAttrValue(componentEvents.get(componentName).get("LostFocus"), "statement", "name", "DO"), "block", 0);
            
            int gotFocusId = Integer.parseInt(getAttributeValueIfExists(componentEvents.get(componentName).get("GotFocus"), "id", "-2"));
            int lostFocusId = Integer.parseInt(getAttributeValueIfExists(componentEvents.get(componentName).get("LostFocus"), "id", "-2"));
            
            // TODO: obfuscate the variables so they don't conflict with a user variable?
            // TODO: keep track of variables I've used to ensure no conflicts? -- v and hasFocus here
            String hasFocusVar = "hasFocus";
            String viewVar = "componentView";
            str += HtmlWrapper.indent(depth) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass(componentName + ".setOnFocusChangeListener(new View.OnFocusChangeListener() {\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            str += HtmlWrapper.indent(depth + 1) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass("public void onFocusChange(View " + viewVar + ", boolean " + hasFocusVar + ") {\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            str += HtmlWrapper.indent(depth + 2) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass("if(" + hasFocusVar + ") {\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            str += HtmlWrapper.addInnerSelectionClass(gotFocusChild != null ? visitNode(gotFocusChild, depth + 3) : HtmlWrapper.indent(depth + 3) + "/* when " + componentName + ".GotFocus event handler */\n", gotFocusId, selectedBlockId);
            str += HtmlWrapper.indent(depth + 2) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass("} else {\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            str += HtmlWrapper.addInnerSelectionClass(lostFocusChild != null ? visitNode(lostFocusChild, depth + 3) : HtmlWrapper.indent(depth + 3) + "/* when " + componentName + ".LostFocus event handler */\n", lostFocusId, selectedBlockId);
            str += HtmlWrapper.indent(depth + 2) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            str += HtmlWrapper.indent(depth + 1) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            str += HtmlWrapper.indent(depth) + HtmlWrapper.addSelectionClass(HtmlWrapper.addCSSClass("});\n", CONTROL_BLOCK_CSS_CLASS, lostFocusId, selectedBlockId), gotFocusId, selectedBlockId);
            
            focusDone = true;
          }
        } else {
          // TODO: more to do here depending on the type of event
          if(componentType.equals("Button")) {
            str += getEventHandlerSignature(instanceName, componentType, eventName, blockId, depth);
            str += HtmlWrapper.addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth + 2) : HtmlWrapper.indent(depth + 2) + "/* when " + componentName + "." + eventName + " event handler */\n", blockId, selectedBlockId);
            str += HtmlWrapper.indent(depth + 1) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
            str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("});\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
          } else {
            str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("when " + instanceName + "." + eventName + "() {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
            str += HtmlWrapper.addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth + 1) : HtmlWrapper.indent(depth + 1) + "/* when " + componentName + "." + eventName + " event handler */\n", blockId, selectedBlockId);
            str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
          }
        }
        
        str += "\n";
      }
    }

    return str.length() >= 2 ? str.substring(0, str.length() - 2) : str;
  }
  
  private String visitNode(Node n, int depth) {
    // determine the type of the XML node 
    String blockType = "";
    String comment = getNodeComment(n);  // TODO: add comment in correct location for each block type
    
    switch(n.getNodeType()) {
      case Node.TEXT_NODE:
        if(showXML) {
          if(n.getNodeValue().trim().isEmpty()) return "";
          
          return ": " + n.getNodeValue().trim() + "\n";
        } else {
          return "";
        }
      case Node.ELEMENT_NODE:
        int blockId = Integer.parseInt(getAttributeValueIfExists(n, "id", "-2"));  // blocklySelectChange returns -1 if no block is selected
        String str = showXML ? HtmlWrapper.indent(depth) + HtmlWrapper.addSelectionClass(HtmlWrapper.makeColorSpan(n.getNodeName(), "#881280") + " (" + makeAttributeString(n) + ")", blockId, selectedBlockId) : "";

//        if(blockId >= 0) {
//          nodeIdMap.put(blockId, n);
//        }
        
        if(showXML && n.getNodeName().compareTo("title") != 0) str += "\n";
        
        if(n.getNodeName().compareTo("block") == 0) {
          blockType = getAttributeValueIfExists(n, "type");
          
          if(blockType.compareTo("NO_SUCH_ATTRIBUTE") == 0) {
            OdeLog.wlog("Block has no type: " + n);
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
            
            String instanceName;
            String componentType;
            
            switch(BlockTypes.valueOf(blockType)) {
              // when event block
              case component_event:
                mutation = getChildOfType(n, "mutation", 0);
//                childBlock = getChildOfType(getChildWithAttrValue(n, "statement", "name", "DO"), "block", 0);
//                
                String eventName = getAttributeValueIfExists(mutation, "event_name");
                instanceName = getAttributeValueIfExists(mutation, "instance_name");
                componentType = getAttributeValueIfExists(mutation, "component_type");
//                
//                if(componentImportMap.containsKey(componentType)) {
//                  addImport(componentImportMap.get(componentType), blockId);
//                } else {
//                  OdeLog.wlog("MISSING COMPONENT TYPE: " + componentType + ": " + componentImportMap.get(componentType));
//                }
//                
//                addComponent(componentType + " " + instanceName + " = (" + componentType + ") findViewById(R.id." + instanceName + ");", blockId);
//                
//                // TODO: more to do here depending on the type of event
//                if(componentType.equals("Button")) {
//                  str += getEventHandlerSignature(instanceName, componentType, eventName, blockId, depth);
//                  str += HtmlWrapper.addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth + 2) : HtmlWrapper.indent(depth + 2) + "/* body of event loop */\n", blockId, selectedBlockId);
//                  str += HtmlWrapper.indent(depth + 1) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
//                  str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("});\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
//                } else {
//                  str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("when " + instanceName + "." + eventName + "() {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
//                  str += HtmlWrapper.addInnerSelectionClass(childBlock != null ? visitNode(childBlock, depth + 1) : HtmlWrapper.indent(depth + 1) + "/* body of event loop */\n", blockId, selectedBlockId);
//                  str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
//                }
                
                if(componentEvents.containsKey(instanceName)) {
                  componentEvents.get(instanceName).put(eventName, n);
                } else {
                  TreeMap l = new TreeMap();
                  l.put(eventName, n);
                  componentEvents.put(instanceName, l);
                }
                
                skipChildren = true;
                
                break;
              // component getter or setter
              case component_set_get:
                mutation = getChildOfType(n, "mutation", 0);
                
                componentType = getAttributeValueIfExists(mutation, "component_type");   // e.g., Label
                String propertyName = getAttributeValueIfExists(mutation, "property_name");     // e.g., BackgroundColor
                instanceName = getAttributeValueIfExists(mutation, "instance_name");     // e.g., Label1 (custom name)
                boolean isSet = getAttributeValueIfExists(mutation, "set_or_get").compareTo("set") == 0;
                // TODO: is_generic
                
                childText = HtmlWrapper.addInnerSelectionClass(getChildText(n, depth + 1, "VALUE", "/* set value */"), blockId, selectedBlockId);
                
                if(isSet) {
                  str += HtmlWrapper.indent(depth);
                  str += HtmlWrapper.addCSSClass(instanceName + ".set" + propertyName + "(", SETTER_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += childText;
                  str += HtmlWrapper.addCSSClass(");\n", SETTER_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  
                } else {
                  str += HtmlWrapper.addCSSClass(instanceName + ".get" + propertyName + "()", GETTER_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                }
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "title", "value", "next", "#text"});

                skipChildren = true;
                
                break;
              // if block
              case controls_if:
                mutation = getChildOfType(n, "mutation", 0);

                if(mutation != null) {
                  int elseifs = Integer.parseInt(getAttributeValueIfExists(mutation, "elseif", "0"));
                  int elses = Integer.parseInt(getAttributeValueIfExists(mutation, "else", "0"));
                  
                  str += HtmlWrapper.indent(depth);
                  
                  // handle all if and else if blocks
                  for(int i = 0; i <= elseifs; ++i) {
                    String iftext = HtmlWrapper.addInnerSelectionClass(getChildText(n, depth, "IF" + i, "/* NO CONDITION" + i + " */"), blockId, selectedBlockId);
                    String dotext = HtmlWrapper.addInnerSelectionClass(getChildText(n, depth + 1, "DO" + i, HtmlWrapper.indent(depth + 1) + "// nothing to do here!\n", "statement"), blockId, selectedBlockId);
                    
                    str += HtmlWrapper.addCSSClass((i > 0 ? " else " : "") + "if(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    str += iftext;
                    str += HtmlWrapper.addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    str += dotext;
                    str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  }
                  
                  // handle else block
                  if(elses == 1) {
                    String dotext = HtmlWrapper.addInnerSelectionClass(getChildText(n, depth + 1, "ELSE", HtmlWrapper.indent(depth + 1) + "// nothing to do here!\n", "statement"), blockId, selectedBlockId);
                    
                    str += HtmlWrapper.addCSSClass(" else {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    str += dotext;
                    str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  } else {
                    str += "\n";
                  }
                // simple if statement with no if/else
                } else {
                  String iftext = HtmlWrapper.addInnerSelectionClass(getChildText(n, depth, "IF0", "/* NO CONDITION0 */"), blockId, selectedBlockId);
                  String dotext = HtmlWrapper.addInnerSelectionClass(getChildText(n, depth + 1, "DO0", HtmlWrapper.indent(depth + 1) + "// nothing to do here!\n", "statement"), blockId, selectedBlockId);
                  
                  str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("if(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += iftext;
                  str += HtmlWrapper.addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += dotext;
                  str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                }
                
                skipChildren = true;
                
                str += checkUncaughtChildren(n, new String[]{"mutation", "value", "statement", "next", "#text"});
                
                break;
              case controls_forRange:
                String forIndexName = getTitleFromBlock(n);
                String forStart = getChildText(n, depth, "START");
                String forEnd = getChildText(n, depth, "END");
                String forStep = getChildText(n, depth, "STEP");
                childText = getChildText(n, depth + 1, "DO", HtmlWrapper.indent(depth + 1) + "/* for loop body */", "statement");

                // TODO: this solution is pretty ugly. Any way to make it better?
                //        forStep > 0 ? index <= forEnd : index >= forEnd
                
                //if(comment != null) str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("// " + comment, COMMENT_CSS_CLASS, blockId, selectedBlockId) + "\n";
                str += createCommentString(comment, false, blockId, depth);
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("for(int ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forIndexName, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" = ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forStart, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("; ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                // ugly part
                str += HtmlWrapper.addInnerSelectionClass(forStep, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" > 0 ? ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forIndexName, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" <= ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forEnd, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" : ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forIndexName, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" >= ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                // end ugly part
                str += HtmlWrapper.addInnerSelectionClass(forEnd, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("; ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forIndexName, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" += ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forStep, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case controls_forEach:
                String forItem = getTitleFromBlock(n);
                String forList = getChildText(n, depth, "LIST");
                childText = getChildText(n, depth + 1, "DO", HtmlWrapper.indent(depth + 1) + "/* for loop body */", "statement");

                // TODO: need to remember that this variable is of type Object somehow for casting
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("for(Object ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forItem, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" : ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(forList, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_while:
                String whileCondition = getChildText(n, depth, "TEST");
                childText = getChildText(n, depth + 1, "DO", HtmlWrapper.indent(depth + 1) + "// while loop body", "statement");

                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("while(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(whileCondition, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("}\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_choose:
                String chooseTest = getChildText(n, depth, "TEST");
                String chooseThen = getChildText(n, depth, "THENRETURN");
                String chooseElse = getChildText(n, depth, "ELSERETURN");
                
                str += HtmlWrapper.addCSSClass("(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(chooseTest, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" ? ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(chooseThen, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" : ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(chooseElse, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
                
              case controls_do_then_return:
                childText = getChildText(n, depth + 1, "STM", "/* value */", "statement");
                String ctrlDoReturnValue = getChildText(n, depth, "VALUE");
                
                // TODO: if I go this route, just remove the block from Code Inventor
                str += HtmlWrapper.addCSSClass("/* This block is not supported by Code Inventor */", COMMENT_CSS_CLASS, blockId, selectedBlockId);
                
                // TODO: implement this somehow if possible?
                //   Options:
                //     Create an interface like Runnable that returns an object
                //       issue: would not have access to scope
                //     Run do parts above the current block
                //       issue: do/result is probably inside an if/choose so it needs to be run conditionally
                //       issue: make sure to run multiple do/results in order
                //     Just not implement it? Would need to add a note about why it's not there
                
//                int x = 2;
//                
//                new Object() {
//                  public Object run() {
//                    return x;
//                  }
//                }.run();
                
                skipChildren = true;
                break;
              case controls_eval_but_ignore:
                childText = getChildText(n, depth, "VALUE");
                
                // TODO: add a note about why this will generate compile errors if they do something silly
                //        Java is statically typed
                //        Java can't execute expressions without evaluating the result
                //
                //        Only use this block to call procedures?
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId) + ";\n";
                
                skipChildren = true;
                break;
              case controls_openAnotherScreen:
                childText = getChildText(n, depth, "SCREEN");
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("startActivity(new Intent().setClassName(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("\"" + PACKAGE_NAME + "\"", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("\"" + PACKAGE_NAME + ".\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("));\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_openAnotherScreenWithStartValue:
                leftChildText = getChildText(n, depth, "SCREENNAME");
                rightChildText = getChildText(n, depth, "STARTVALUE");
                
                // TODO: add a field with type information for getData on the other side
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("startActivity(new Intent().setClassName(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("\"" + PACKAGE_NAME + "\"", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("\"" + PACKAGE_NAME + ".\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("                          .putExtra(", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("\"" + OPEN_SCREEN_START_VALUE + "\"", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("));\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_getStartValue:
                // TODO: probably should be a bit smarter about the type here if possible
                //   getBoolean(String key)
                //   getDouble(String key)
                //   getFloat(String key)
                //   getInt(String key)
                //   getString(String key)
                //   Maybe get(String key) and check it like math's is a number
                //   Add an extra field in the bundle that tells the type!
                // TODO: will need to remember the type of this like I do for variables?
                
                // this extracts the object out of the JSON-encoded string data passed to a new activity
                
                str += HtmlWrapper.addCSSClass("getIntent().getExtras().get(\"" + OPEN_SCREEN_START_VALUE + "\")", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_closeScreen:
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("finish();\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_closeScreenWithValue:
                // TODO: Add a field to remember the type
                childText = getChildText(n, depth, "SCREEN");
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("setResult(Activity.RESULT_OK, new Intent().putExtra(\"" + OPEN_SCREEN_START_VALUE + "\", ", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("));\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("finish();\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_closeApplication:
                // TODO: need to propagate this back down to the main application screen then call exit
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("System.exit(0);\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                break;
              case controls_getPlainStartText:
                // TODO: not implemented
                
                // this simply outputs the JSON-encoded string value of the start argument
                
                skipChildren = true;
                break;
              case controls_closeScreenWithPlainText:
                // TODO: not implemented
                childText = getChildText(n, depth, "TEXT");
                
                skipChildren = true;
                break;
                
              // Logic blocks
              case logic_boolean:
                // TODO: getTitleFromBlock
                String logicBoolVal = getTitleFromBlock(n).toLowerCase();
                str += HtmlWrapper.addCSSClass(logicBoolVal, LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                break;
              case logic_false:
                String logicFalseVal = getTitleFromBlock(n).toLowerCase();
                str += HtmlWrapper.addCSSClass(logicFalseVal, LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                break;
              case logic_negate:
                childText = getChildText(n, depth, "BOOL", "/* value to be negated */");
                
                str += HtmlWrapper.addCSSClass("!", LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case logic_compare:
                leftChildText = getChildText(n, depth, "A", "/* left side of comparison */");
                rightChildText = getChildText(n, depth, "B", "/* right side of comparison */");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" " + blockToLogicOperator(n) + " ", LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case logic_operation:
              case logic_or:
                leftChildText = getChildText(n, depth, "A", "/* left side of logic operation */");
                rightChildText = getChildText(n, depth, "B", "/* right side of logic operation */");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" " + blockToLogicOperator(n), LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId) + " ";
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LOGIC_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
                
              /* Math blocks */
              case math_number:
                String number = getTitleFromBlock(n);
                str += HtmlWrapper.addCSSClass(number, MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                //if(comment != null) str += " /* " + comment + " */";
                str += createCommentString(comment, true, blockId, depth);
                
                skipChildren = true;
                
                break;
              case math_compare:
                leftChildText = getChildText(n, depth, "A", "/* left side of comparison */");
                rightChildText = getChildText(n, depth, "B", "/* right side of comparison */");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" " + blockToMathOperator(n) + " ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_add:
                int additions = Integer.parseInt(getChildOfType(n, "mutation", 0).getAttributes().getNamedItem("items").getNodeValue());
                
                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                for(int i = 0; i < additions; i++) {
                  if(i > 0) str += HtmlWrapper.addCSSClass(" + ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);                  
                  childText = getChildText(n, depth, "NUM" + i, "/* value */");
                  str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId); 
                }

                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_subtract:
                leftChildText = getChildText(n, depth, "A", "/* value */");
                rightChildText = getChildText(n, depth, "B", "/* value */");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" - ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_multiply:
                int multiplications = Integer.parseInt(getChildOfType(n, "mutation", 0).getAttributes().getNamedItem("items").getNodeValue());
                
                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                for(int i = 0; i < multiplications; i++) {
                  if(i > 0) str += HtmlWrapper.addCSSClass(" * ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  childText = getChildText(n, depth, "NUM" + i, "/* value */");                  
                  str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId); 
                }

                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_division:
                leftChildText = getChildText(n, depth, "A", "/* value */");
                rightChildText = getChildText(n, depth, "B", "/* value */");

                // TODO: check if enclosing parentheses are necessary before adding them
                str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" / ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_power:
                leftChildText = getChildText(n, depth, "A", "/* value */");
                rightChildText = getChildText(n, depth, "B", "/* value */");
                
                // TODO: Math.pow returns a double, may need to cast as an int
                str += HtmlWrapper.addCSSClass("Math.pow(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_random_int:
                String randomMin = getChildText(n, depth, "FROM", "/* random min */");
                String randomMax = getChildText(n, depth, "TO", "/* random max */");

                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                // TODO: get rid of _randomGenerator if the last block using it is deleted!
                
                str += HtmlWrapper.addCSSClass("(" + RANDOM_GENERATOR_NAME + ".nextInt(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(randomMax, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" - ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(randomMin, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(") + ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(randomMin, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_random_float:
                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                
                str+= HtmlWrapper.addCSSClass(RANDOM_GENERATOR_NAME + ".nextDouble()", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);

                break;
              case math_random_set_seed:
                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                
                childText = getChildText(n, depth, "NUM", "/* seed value */");
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass(RANDOM_GENERATOR_NAME + ".setSeed(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")\n", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
                
              case math_on_list:
                mutation = getChildOfType(n, "mutation", 0);
                int itemsInList = Integer.parseInt(mutation.getAttributes().getNamedItem("items").getNodeValue());
                operation = blockToMathListType(n).toLowerCase();
                
                if(itemsInList > 1) {
                  for(int i = 0; i < itemsInList; ++i) {
                    childText = getChildText(n, depth, "NUM" + i, "/* value */");
                    
                    if(i != itemsInList - 1) {
                      str += HtmlWrapper.addCSSClass("Math." + operation + "(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                      str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                      str += HtmlWrapper.addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    } else {
                      str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                      str += HtmlWrapper.addCSSClass(HtmlWrapper.multiplyChars(')', itemsInList - 1), MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    }
                  }
                } else if(itemsInList == 0) {
                  str += "/* nothing to see here... move along */";
                } else {
                  // only one item in max/min call, just output that item
                  childText = getChildText(n, depth, "NUM0", "/* min value */");
                  str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                }
                
                skipChildren = true;
                
                break;
                
              case math_single:
              case math_abs:
              case math_neg:
              case math_round:
              case math_ceiling:
              case math_floor:
                String pretext = operationToMathPretext(blockToMathSingleType(n));
                childText = getChildText(n, depth, "NUM", "/* value */");

                str += HtmlWrapper.addCSSClass(pretext, MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_divide:
                operation = getTitleFromBlock(n);
                leftChildText = getChildText(n, depth, "DIVIDEND");
                rightChildText = getChildText(n, depth, "DIVISOR");
                
                if(operation.compareTo("MODULO") == 0 || operation.compareTo("REMAINDER") == 0) {
                  str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(" % ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                } else if(operation.compareTo("QUOTIENT") == 0) {
                  // TODO: add comment describing why this is a complicated formula

                  String divideText = HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId); 
                  divideText += HtmlWrapper.addCSSClass(" / ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  divideText += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId); 

                  str += HtmlWrapper.addCSSClass("(((Object) (", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += divideText;
                  str += HtmlWrapper.addCSSClass(")).getClass().equals(Integer.class) ? ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += divideText;
                  str += HtmlWrapper.addCSSClass(" : Math.floor(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += divideText;
                  str += HtmlWrapper.addCSSClass("))", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                } else {
                  OdeLog.wlog("UNKNOWN MATH_DIVIDE TYPE: " + operation);
                }
                
                skipChildren = true;
                
                break;
              case math_trig:
              case math_cos:
              case math_tan:
                operation = getTitleFromBlock(n);
                childText = getChildText(n, depth, "NUM");
                
                str += HtmlWrapper.addCSSClass("Math." + operation.toLowerCase() + "(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_atan2:
                leftChildText = getChildText(n, depth, "Y");
                rightChildText = getChildText(n, depth, "X");
                
                str += HtmlWrapper.addCSSClass("Math.atan2(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_convert_angles:
                operation = getTitleFromBlock(n);
                childText = getChildText(n, depth, "NUM");
                
                if(operation.compareTo("DEGREES_TO_RADIANS") == 0) {
                  operation = "toRadians";
                } else if(operation.compareTo("RADIANS_TO_DEGREES") == 0) {
                  operation = "toDegrees";
                } else {
                  OdeLog.wlog("UNKNOWN MATH_CONVERT_ANGLES CONVERSION: " + operation);
                }
                
                str += HtmlWrapper.addCSSClass("Math." + operation + "(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_format_as_decimal:
                String decimalToRound = getChildText(n, depth, "NUM");
                String numberOfPlaces = getChildText(n, depth, "PLACES");
                
                // TODO: add a hover text to describe why this convoluted method is necessary
                
                addImport(BIG_DECIMAL_IMPORT_PATH, blockId);
                
                str += HtmlWrapper.addCSSClass("BigDecimal.valueOf(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(decimalToRound, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(").setScale(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(numberOfPlaces, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", BigDecimal.ROUND_HALF_UP).doubleValue()", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case math_is_a_number:
                childText = getChildText(n, depth, "NUM");
                String innerStr = HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);

                // TODO: add a note about why this is convoluted
                
                str += HtmlWrapper.addCSSClass("(", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                str += HtmlWrapper.addCSSClass("((Object) ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += innerStr;
                str += HtmlWrapper.addCSSClass(").getClass().equals(Integer.class)", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                str += HtmlWrapper.addCSSClass(" || ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                str += HtmlWrapper.addCSSClass("((Object) ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += innerStr;
                str += HtmlWrapper.addCSSClass(").getClass().equals(Float.class)", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                str += HtmlWrapper.addCSSClass(" || ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                str += HtmlWrapper.addCSSClass("((Object) ", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += innerStr;
                str += HtmlWrapper.addCSSClass(").getClass().equals(Double.class)", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                str += HtmlWrapper.addCSSClass(")", MATH_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
                
              /* Text blocks */
                
              // TODO: for each text method that takes a text argument, add ("" + __) if argument is not text
              //        this includes: length, is empty, compare texts, trim, upcase, starts at,  
              //        contains, split at first, split at spaces, segment, and replace all
              case text:
                String textStr = getTitleFromBlock(n);
                if(textStr == null) textStr = "";
                
                str += HtmlWrapper.addCSSClass("\"" + textStr + "\"", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_join:
                mutation = getChildOfType(n, "mutation", 0);
                int numTexts = Integer.parseInt(getAttributeValueIfExists(mutation, "items", "0"));
                
                // TODO: check parens
                if(numTexts >= 1) str += HtmlWrapper.addCSSClass("(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                // TODO: only add opening quotes if the first item in the list isn't a string
                if(numTexts >= 1) str += HtmlWrapper.addCSSClass("\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                for(int i = 0; i < numTexts; ++i) {
                  childText = getChildText(n, depth, "ADD" + i);
                  if(i != 0) str += HtmlWrapper.addCSSClass(" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                }

                if(numTexts >= 1) str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_length:
                childText = getChildText(n, depth, "VALUE");
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".length()", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_isEmpty:
                childText = getChildText(n, depth, "VALUE");
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".isEmpty()", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_compare:
                leftChildText = getChildText(n, depth, "TEXT1");
                rightChildText = getChildText(n, depth, "TEXT2");
                operation = xmlTextOperationToJava(getTitleFromBlock(n));

                // TODO: check if parens are necessary
                str += HtmlWrapper.addCSSClass("(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".compareTo(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(") " + operation + " 0)", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                                
                skipChildren = true;
                
                break;
              case text_trim:
                childText = getChildText(n, depth, "TEXT");
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".trim()", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_changeCase:
                childText = getChildText(n, depth, "TEXT");
                operation = mapXmlTextCaseChangeToJava(getTitleFromBlock(n));
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(operation, TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_starts_at:
                leftChildText = getChildText(n, depth, "TEXT");
                rightChildText = getChildText(n, depth, "PIECE");
                
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".indexOf(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    
                skipChildren = true;
                
                break;
              case text_contains:
                leftChildText = getChildText(n, depth, "TEXT");
                rightChildText = getChildText(n, depth, "PIECE");
                
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".contains(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    
                skipChildren = true;
                break;
              case text_split:
                leftChildText = getChildText(n, depth, "TEXT");
                rightChildText = getChildText(n, depth, "AT");
                operation = getTitleFromBlock(n);

                // TODO: Do I want to do these the way you would do them if you were coding a similar project or
                //        do I want to do them so the behavior is the same. Add a checkbox to toggle the option?
                
                addImport(ARRAYS_IMPORT_PATH, blockId);
                addImport(PATTERN_IMPORT_PATH, blockId);
                
                str += HtmlWrapper.addCSSClass("Arrays.asList((\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                if(operation.compareTo("SPLITATFIRST") == 0) {
                  // TODO: add help text describing why we add Pattern.quote() in all of these instances
                  str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(").split(Pattern.quote(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass("), 2)", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                } else if(operation.compareTo("SPLITATFIRSTOFANY") == 0) {
                  str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(".split(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  // combine these into a single regex?
                  // TODO: need to make a getListAsStringArray method or something like that and Pattern each one
                  str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(", 2)", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                } else if(operation.compareTo("SPLIT") == 0) {
                  str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(").split(Pattern.quote(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                } else if(operation.compareTo("SPLITATANY") == 0) {
                  str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(".split(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  // combine these into a single regex?
                  // TODO: need to make a getListAsStringArray method or something like that and Pattern each one
                  str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                  str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                } else {
                  OdeLog.wlog("UNKNOWN TEXT_SPLIT TYPE " + operation);
                }

                str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                //str += "" + Arrays.asList("hi", "blah");
                //str += "" + Arrays.asList("what's up?".split("up"));
                //str += "" + Arrays.asList("what's up?".split("s[ u]"));
                //str += "" + Arrays.asList("what's up?".split(Pattern.quote("s[ u]")));
                
                //str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                //str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);

                //"".split("regex");
                //"".split("regex", 0);
                
                skipChildren = true;
                
                break;
              case text_split_at_spaces:
                childText = getChildText(n, depth, "TEXT");

                addImport(ARRAYLIST_IMPORT_PATH, blockId);
                addImport(ARRAYS_IMPORT_PATH, blockId);
                
                // TODO: add notes about why we use "" + and Arrays.asList
                
                str += HtmlWrapper.addCSSClass("new ArrayList(Arrays.asList((\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(").split([ \\t\\n\\r\\f])))", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_segment:
                childText = getChildText(n, depth, "TEXT");
                String textStartStr = getChildText(n, depth, "START");
                String textLengthStr = getChildText(n, depth, "LENGTH");
                
                // TODO: add note about why there's a -1 for start and length
                //        and .substring using start + end instead of start + length
                
                str += HtmlWrapper.addCSSClass("(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(").substring(", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(textStartStr, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" - 1, ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(textStartStr, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" - 1 + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(textLengthStr, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case text_replace_all:
                childText = getChildText(n, depth, "TEXT");
                String textSegmentStr = getChildText(n, depth, "SEGMENT");
                String textReplacementStr = getChildText(n, depth, "REPLACEMENT");
                
                // TODO: add note about why "" + is needed for each string here
                // TODO: change "" + behavior based on type of inner block?

                str += HtmlWrapper.addCSSClass("(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(").replaceAll(Pattern.quote(\"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(textSegmentStr, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("), \"\" + ", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(textReplacementStr, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", TEXT_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
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
                str += HtmlWrapper.addCSSClass(HtmlWrapper.makeColorSpan(getTitleFromBlock(n).replaceAll("#", "0x"), getTitleFromBlock(n)), COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                break;
              case color_make_color:
                childText = getChildText(n, depth, "COLORLIST");
                
                str += HtmlWrapper.addCSSClass("((", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".size() == 4 ? (Integer) ", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".get(3) << 24 : 0) | ((Integer) ", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".get(0) << 16) | ((Integer) ", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".get(1) << 8) | ((Integer) ", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".get(2)))", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case color_split_color:
                childText = getChildText(n, depth, "COLOR");

                addImport(ARRAYLIST_IMPORT_PATH, blockId);
                addImport(ARRAYS_IMPORT_PATH, blockId);
                addImport(ANDROID_COLOR_IMPORT_PATH, blockId);
                
                str += HtmlWrapper.addCSSClass("new ArrayList(Arrays.asList(Color.red(", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("), Color.green(", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("), Color.blue(", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass("), Color.alpha(", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")))", COLORS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_create_with:
                mutation = getChildOfType(n, "mutation", 0);
                
                str += HtmlWrapper.addCSSClass("new ArrayList(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                if(mutation != null) {
                  int listItems = Integer.parseInt(getAttributeValueIfExists(mutation, "items", "0"));
                  
                  if(listItems > 0) str += HtmlWrapper.addCSSClass("Arrays.asList(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  
                  for(int i = 0; i < listItems; ++i) {
                    childText = getChildText(n, depth, "ADD" + i);
                    
                    if(i != 0) str += HtmlWrapper.addCSSClass(", ", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                  }
                  
                  if(listItems > 0) str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                }
                                
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_add_items:
                mutation = getChildOfType(n, "mutation", 0);
                childText = getChildText(n, depth, "LIST");

                // TODO: decide whether it would be best to use Arrays.asList() or separate lines
                
                if(mutation != null) {
                  int listAddItems = Integer.parseInt(getAttributeValueIfExists(mutation, "items", "0"));
                  
                  for(int i = 0; i < listAddItems; ++i) {
                    String listAddText = getChildText(n, depth, "ITEM" + i);
                    
                    str += HtmlWrapper.indent(depth) + HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                    str += HtmlWrapper.addCSSClass(".add(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                    str += HtmlWrapper.addInnerSelectionClass(listAddText, blockId, selectedBlockId);
                    str += HtmlWrapper.addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                  }
                }
                
                skipChildren = true;
                
                break;
              case lists_is_in:
                leftChildText = getChildText(n, depth, "ITEM");
                rightChildText = getChildText(n, depth, "LIST");
                
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".contains(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_length:
                childText = getChildText(n, depth, "LIST");
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".size()", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_is_empty:
                childText = getChildText(n, depth, "LIST");
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".isEmpty()", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_pick_random_item:
                childText = getChildText(n, depth, "LIST");
                
                addImport(RANDOM_IMPORT_PATH, blockId);
                addGlobal(RANDOM_GENERATOR_INIT_PREFIX + RANDOM_GENERATOR_NAME + RANDOM_GENERATOR_INIT_SUFFIX, blockId);
                
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".get(" + RANDOM_GENERATOR_NAME + ".nextInt(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".size()))", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_position_in:
                leftChildText = getChildText(n, depth, "ITEM");
                rightChildText = getChildText(n, depth, "LIST");
                
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".indexOf(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_select_item:
                leftChildText = getChildText(n, depth, "LIST");
                rightChildText = getChildText(n, depth, "NUM");
                
                // TODO: add note about why -1 is here, highlight text in some way
                
                str += HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".get(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" - 1)", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_insert_item:
                String listInsertListText = getChildText(n, depth, "LIST");
                String listInsertIndexText = getChildText(n, depth, "INDEX");
                String listInsertItemText = getChildText(n, depth, "ITEM");
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addInnerSelectionClass(listInsertListText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".add(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(listInsertIndexText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(listInsertItemText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);

                skipChildren = true;
                
                break;
              case lists_replace_item:
                String listReplaceItemListText = getChildText(n, depth, "LIST");
                String listReplaceItemIndexText = getChildText(n, depth, "NUM");
                String listReplaceItemReplacementText = getChildText(n, depth, "ITEM");

                str += HtmlWrapper.indent(depth) + HtmlWrapper.addInnerSelectionClass(listReplaceItemListText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".set(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(listReplaceItemIndexText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(", ", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(listReplaceItemReplacementText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_remove_item:
                leftChildText = getChildText(n, depth, "LIST");
                rightChildText = getChildText(n, depth, "INDEX");
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".remove(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_append_list:
                leftChildText = getChildText(n, depth, "LIST0");
                rightChildText = getChildText(n, depth, "LIST1");
                
                str += HtmlWrapper.indent(depth) + HtmlWrapper.addInnerSelectionClass(leftChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(".addAll(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(rightChildText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(");\n", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_copy:
                childText = getChildText(n, depth, "LIST");
                
                str += HtmlWrapper.addCSSClass("new ArrayList(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_is_list:
                childText = getChildText(n, depth, "ITEM");

                str += HtmlWrapper.addCSSClass("(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                // TODO: if argument is a primitive (number, boolean), cast as Object
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(" instanceof ArrayList)", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_to_csv_row:
                childText = getChildText(n, depth, "LIST");
                
                // if we allow Android libraries:
                // addImport(android.text.TextUtils);
                // str += HtmlWrapper.addCSSClass("\" + TextUtils.join("\", \"", ", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                // str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                // str += HtmlWrapper.addCSSClass("\"", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
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
                
                str += HtmlWrapper.addCSSClass("_listToCSVRow(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_to_csv_table:
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
                
                str += HtmlWrapper.addCSSClass("_listToCSVTable(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                skipChildren = true;
                
                break;
              case lists_from_csv_row:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                // TODO: might need to convert any numbers to number types... maybe same with lists
                
                // TODO: add opencsv
                // TODO: determine what I'd need to do for the license for opencsv
                
                // TODO: catch thrown error in try/catch
                
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
                
                str += HtmlWrapper.addCSSClass("_listFromCSVRow(", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                str += HtmlWrapper.addInnerSelectionClass(childText, blockId, selectedBlockId);
                str += HtmlWrapper.addCSSClass(")", LISTS_BLOCK_CSS_CLASS, blockId, selectedBlockId);
                
                // add them all as strings?
                
                break;
              case lists_from_csv_table:
                childBlock = getChildBlockNamed(n, "TEXT");
                childText = childBlock != null ? visitNode(childBlock, depth) : "/* value */";
                
                // TODO: implement this
                
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
            str += HtmlWrapper.makeColorSpan("// UNHANDLED BLOCK TYPE: " + blockType + "\n", "red");
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
  
  private String mapXmlTextCaseChangeToJava(String operation) {
    if(operation == null) return null;
    
    if(operation.compareTo("UPCASE") == 0) return ".toUpperCase()";
    if(operation.compareTo("DOWNCASE") == 0) return ".toLowerCase()";
    
    return HtmlWrapper.makeColorSpan("/* UNHANDLED CASE CHANGE TYPE */", "red");
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
  
  private String getNodeComment(Node n) {
    if(n == null) return null;
    
    if(n.hasChildNodes()) {
      for(int i = 0; i < n.getChildNodes().getLength(); ++i) {
        if(n.getChildNodes().item(i).getNodeName().compareTo("comment") == 0 &&
            n.getChildNodes().item(i).getFirstChild() != null) {
          return HtmlWrapper.htmlify(n.getChildNodes().item(i).getFirstChild().getNodeValue());
        }
      }
    }
    
    return null;
  }
  
  private String createCommentString(String comment, boolean isInline, int blockId, int depth) {
    if(comment == null) return "";
    
    if(isInline) {
      return HtmlWrapper.addCSSClass(" /* " + comment + " */ ", COMMENT_CSS_CLASS, blockId, selectedBlockId); 
    } else {
      return HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass("// " + comment, COMMENT_CSS_CLASS, blockId, selectedBlockId) + "\n"; 
    }
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
  
//  private PrimitiveType getPrimitiveType(Node n) {
//    
//    
//    return PrimitiveType.Unknown;
//  }
  
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
        str += HtmlWrapper.makeColorSpan("// UNCAUGHT CHILD NAME: " + childName + "\n", "red");
      }
    }
    
    return str;
  }
  
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
      String thisImport = HtmlWrapper.addCSSClass("import", SYSTEM_CSS_CLASS) + " " + str + ";\n";
      
      for(Integer i : s) {
        thisImport = HtmlWrapper.addSelectionClass(thisImport, i, selectedBlockId);
      }
      
      importStr += thisImport;
    }
    
    return importStr;
  }
  
  /**
   * Adds a global definition to the list of global definitions. This allows 
   * multiple blocks that require creation of a global variable to exist 
   * without defining the global variable more than once. Instead, 
   * selection highlighting is added for each block that creates the global
   * variable when creating the single instance.
   * 
   * Definitions include the variable type, name, initialization (if any), 
   * and line-ending semicolon. E.g.,
   * 
   * Button button1;
   * 
   * @param globalDefinition  The definition of the global variable
   * @param blockId  Current block ID to add to the list of IDs attached
   *                   to this definition
   */
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
        thisGlobal = HtmlWrapper.addSelectionClass(thisGlobal, i, selectedBlockId);
      }
      
      globalStr += HtmlWrapper.indent(1) + thisGlobal;
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
        thisFunction = HtmlWrapper.addSelectionClass(thisFunction, i, selectedBlockId);
      }
      
      functionStr += thisFunction;
    }
    return functionStr;
  }
  
  private void addComponent(String componentDefinition, int blockId) {
    if(!components.containsKey(componentDefinition)) {
      Set<Integer> s = new HashSet<Integer>();
      s.add(new Integer(blockId));
      components.put(componentDefinition, s);
    } else {
      components.get(componentDefinition).add(new Integer(blockId));
    }
  }
  
  private String buildComponents() {
    String componentStr = "";
    
    for(String str : components.keySet()) {
      Set<Integer> s = components.get(str);
      String thisComponent = str + "\n";
      
      for(Integer i : s) {
        thisComponent = HtmlWrapper.addSelectionClass(thisComponent, i, selectedBlockId);
      }
      
      componentStr += HtmlWrapper.indent(2) + thisComponent;
    }
    
    return componentStr;
  }
  
  private String getEventHandlerSignature(String componentName, String componentType, String event, int blockId, int depth) {
    //TODO: import View
    String sig = "";
    sig += HtmlWrapper.indent(depth) + HtmlWrapper.addCSSClass(componentName + ".setOn" + ButtonComponent.eventNameMap(event) + "Listener(new View.On" + ButtonComponent.eventNameMap(event) + "Listener() {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId);
    sig += HtmlWrapper.indent(depth + 1) + HtmlWrapper.addCSSClass("public void on" + ButtonComponent.eventNameMap(event) + "(" + ButtonComponent.eventParameterMap(event) + ") {\n", CONTROL_BLOCK_CSS_CLASS, blockId, selectedBlockId); 
    return sig;
  }
  
  private String getProjectName() {
    try {
      return Ode.getInstance().getProjectManager().getProject(Ode.getInstance().getCurrentFileEditor().getProjectId()).getProjectName();
    } catch (Exception e) {
      return null;
    }
  }
  
  private String getScreenName() {
    try {
      return Ode.getInstance().getCurrentYoungAndroidSourceNode().getFormName();
    } catch (Exception e) {
      return null;
    }
  }
  
  private String makeAttributeString(Node n) {
    String str = "";
    
    if(n == null || n.getAttributes().getLength() == 0) return str;
    
    for(int i = 0; i < n.getAttributes().getLength(); ++i) {
      str += HtmlWrapper.makeColorSpan(n.getAttributes().item(i).getNodeName(), "#994500") + ": " + 
             HtmlWrapper.makeColorSpan(n.getAttributes().item(i).getNodeValue(), "#1a1aa6") + ", ";
    }
    
    return str.substring(0, str.length() - 2);
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
  
  /**
   * Switches projects
   * 
   * @param projectName The name of the project we're switching to
   */
  public void switchProjects(String projectName) {
    OdeLog.wlog("Project name is: " + projectName);
  }
}
