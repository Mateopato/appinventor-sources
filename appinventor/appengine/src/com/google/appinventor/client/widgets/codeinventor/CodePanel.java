package com.google.appinventor.client.widgets.codeinventor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Panel to display Code.
 * @author mdabney
 *
 */
public class CodePanel extends Composite {
  // UI elements
  private final VerticalPanel panel;
  private final Label componentName;
  
  private String codeData;
  
  /**
   * Creates a new Code Panel.
   */
  public CodePanel() {
    // Initialize UI
    VerticalPanel outerPanel = new VerticalPanel();
    outerPanel.setWidth("100%");
    
    codeData = "";
    
    componentName = new Label("CodeTestText");  // TODO: fix this and next line
    componentName.setStyleName("ode-PropertiesComponentName");
    outerPanel.add(componentName);
    
    panel = new VerticalPanel();
    panel.setWidth("100%");
    panel.setStylePrimaryName("ode-PropertiesPanel"); // TODO: create my own style
    outerPanel.add(panel);
    
    initWidget(outerPanel);
  }
  
  /**
   * Adds code to code buffer.
   */
  public void addCode(String text) {
    codeData += text;
  }

  /**
   * Clears the text in the code buffer.
   */
  public void clear() {
    codeData = "";
  }
}
