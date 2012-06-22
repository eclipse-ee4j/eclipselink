/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.framework.ui.chooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import org.eclipse.persistence.tools.workbench.framework.context.ApplicationContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultListChooser;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsResourceBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.chooser.FilteringListBrowser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;



/**
 * Play around with a set of combo-boxes.
 * 
 * DefaultLongListBrowserDialogUITest subclasses this class; so be
 * careful when making changes.
 */
public class DefaultListChooserUITest {

	protected JFrame window;
	private TestModel testModel;
	private PropertyValueModel testModelHolder;
	private PropertyValueModel colorHolder;
	private ListValueModel colorListHolder;
	protected ComboBoxModel colorComboBoxModel;
	private int nextColorNumber = 0;
	
	private PropertyValueModel lookAndFeelHolder;
	
	private PropertyValueModel enabledHolder;
	private PropertyValueModel choosableHolder;
	

	public static void main(String[] args) throws Exception {
		new DefaultListChooserUITest().exec(args);
	}

	public DefaultListChooserUITest() {
		super();
	}

	protected void exec(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		testModel = new TestModel();
		testModelHolder = new SimplePropertyValueModel(testModel);
		colorHolder = this.buildColorHolder(testModelHolder);
		colorListHolder = this.buildColorListHolder();
		colorComboBoxModel = this.buildComboBoxModelAdapter(colorListHolder, colorHolder);
		this.lookAndFeelHolder = this.buildLookAndFeelHolder();
		this.enabledHolder = this.buildEnabledHolder();
		this.choosableHolder = this.buildChoosableHolder();
		this.openWindow();
	}

	private PropertyValueModel buildColorHolder(ValueModel testModelHolder) {
		return new PropertyAspectAdapter(testModelHolder, TestModel.COLOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((TestModel) subject).getColor();
			}
			protected void setValueOnSubject(Object value) {
				((TestModel) subject).setColor((String) value);
			}
		};
	}

	private ListValueModel buildColorListHolder() {
		return new SimpleListValueModel(TestModel.validColors());
//		return new AbstractReadOnlyListValueModel() {
//			public Object getValue() {
//				return new ArrayListIterator(TestModel.VALID_COLORS);
//			}
//			public int size() {
//				return TestModel.VALID_COLORS.length;
//			}
//		};
	}

	private ComboBoxModel buildComboBoxModelAdapter(ListValueModel listHolder, PropertyValueModel selectionHolder) {
		return new ComboBoxModelAdapter(listHolder, selectionHolder);
	}
	
	private PropertyValueModel buildLookAndFeelHolder() {
		return new SimplePropertyValueModel(UIManager.getSystemLookAndFeelClassName());
	}
	
	private PropertyValueModel buildEnabledHolder() {
		return new SimplePropertyValueModel(Boolean.TRUE);
	}
	
	private PropertyValueModel buildChoosableHolder() {
		return new SimplePropertyValueModel(Boolean.TRUE);
	}

	private void openWindow() {
		window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setLocation(300, 300);
		window.setSize(500, 250);
		window.setVisible(true);
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}

	private Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.buildComboBoxPanel(), BorderLayout.NORTH);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}
	
	protected JPanel buildComboBoxPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(this.buildComboBox());
		panel.add(this.buildComboBox());
		panel.add(this.buildListChooser1());
		panel.add(this.buildListChooser2());
		panel.add(this.buildDefaultListChooser());
		return panel;
	}

	private JComboBox buildComboBox() {
		JComboBox comboBox = new JComboBox(colorComboBoxModel);
		this.enabledHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildEnabledListener(comboBox));
		this.lookAndFeelHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLookAndFeelListener(comboBox));
		return comboBox;
	}

	private ListChooser buildListChooser1() {
		ListChooser chooser = new LocalListChooser1(colorComboBoxModel);
		this.enabledHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildEnabledListener(chooser));
		this.choosableHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildChoosableListener(chooser));
		this.lookAndFeelHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLookAndFeelListener(chooser));
		return chooser;
	}

	private ListChooser buildListChooser2() {
		ListChooser chooser = new LocalListChooser2(colorComboBoxModel);
		this.enabledHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildEnabledListener(chooser));
		this.choosableHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildChoosableListener(chooser));
		this.lookAndFeelHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLookAndFeelListener(chooser));
		return chooser;
	}

	private DefaultListChooser buildDefaultListChooser() {
		DefaultListChooser chooser = new DefaultListChooser(colorComboBoxModel, this.buildWorkbenchContextHolder());
		this.enabledHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildEnabledListener(chooser));
		this.choosableHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildChoosableListener(chooser));
		this.lookAndFeelHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildLookAndFeelListener(chooser));
		return chooser;
	}
	
	private PropertyChangeListener buildEnabledListener(final JComboBox comboBox) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				comboBox.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
			}
		};
	}
	
	private PropertyChangeListener buildChoosableListener(final ListChooser chooser) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				chooser.setChoosable(((Boolean) evt.getNewValue()).booleanValue());
			}
		};
	}
	
	private PropertyChangeListener buildLookAndFeelListener(final JComponent component) {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					UIManager.setLookAndFeel((String) evt.getNewValue());
				}
				catch (Exception e) {}
				
				component.updateUI();
			}
		};
	}
	
	private WorkbenchContextHolder buildWorkbenchContextHolder() {
		final TestWorkbenchContext testContext = new TestWorkbenchContext(UIToolsResourceBundle.class, UIToolsIconResourceFileNameMap.class.getName());
		testContext.setCurrentWindow(window);
		
		return new WorkbenchContextHolder() {
			public WorkbenchContext getWorkbenchContext() {
				return testContext;
			}

			public ApplicationContext getApplicationContext() {
				return testContext.getApplicationContext();
			}
		};
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(2, 1));
		controlPanel.add(this.buildUIControlPanel());
		controlPanel.add(this.buildModelControlPanel());
		return controlPanel;
	}
	
	private Component buildUIControlPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 0));
		panel.add(this.buildLookAndFeelControlPanel());
		panel.add(this.buildComboBoxControlPanel());
		return panel;
	}
	
	private Component buildLookAndFeelControlPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("look and feel: "));
		panel.add(this.buildLookAndFeelComboBox());
		return panel;
	}
	
	private JComboBox buildLookAndFeelComboBox() {
		return new JComboBox(new ComboBoxModelAdapter(this.buildLookAndFeelListValue(), this.lookAndFeelHolder));
	}
	
	private ListValueModel buildLookAndFeelListValue() {
		List lookAndFeelValues = new ArrayList();
		lookAndFeelValues.add(MetalLookAndFeel.class.getName());
		lookAndFeelValues.add(WindowsLookAndFeel.class.getName());
		lookAndFeelValues.add(MotifLookAndFeel.class.getName());
		lookAndFeelValues.add("oracle.bali.ewt.olaf.OracleLookAndFeel");	// remove compile-time dependency on OLAF
		
		return new SimpleListValueModel(lookAndFeelValues);
	}
	
	private Component buildComboBoxControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(1, 0));
		controlPanel.add(this.buildEnabledCheckBox());
		controlPanel.add(this.buildChoosableCheckBox());
		return controlPanel;
	}
	
	private JCheckBox buildEnabledCheckBox() {
		JCheckBox checkBox = new JCheckBox("set enabled");
		checkBox.setModel(new CheckBoxModelAdapter(this.enabledHolder));
		return checkBox;
	}
	
	private JCheckBox buildChoosableCheckBox() {
		JCheckBox checkBox = new JCheckBox("set choosable");
		checkBox.setModel(new CheckBoxModelAdapter(this.choosableHolder));
		return checkBox;
	}
	
	private Component buildModelControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(2, 0));
		controlPanel.add(this.buildResetColorButton());
		controlPanel.add(this.buildClearModelButton());
		controlPanel.add(this.buildRestoreModelButton());
		controlPanel.add(this.buildPrintModelButton());
		controlPanel.add(this.buildAddTenButton());
		controlPanel.add(this.buildRemoveTenButton());
		return controlPanel;
	}

	// ********** reset color button **********
	private JButton buildResetColorButton() {
		return new JButton(this.buildResetColorAction());
	}

	private Action buildResetColorAction() {
		Action action = new AbstractAction("reset color") {
			public void actionPerformed(ActionEvent event) {
				DefaultListChooserUITest.this.resetColor();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void resetColor() {
		testModel.setColor(TestModel.DEFAULT_COLOR);
	}

	// ********** clear model button **********
	private JButton buildClearModelButton() {
		return new JButton(this.buildClearModelAction());
	}

	private Action buildClearModelAction() {
		Action action = new AbstractAction("clear model") {
			public void actionPerformed(ActionEvent event) {
				DefaultListChooserUITest.this.clearModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void clearModel() {
		testModelHolder.setValue(null);
	}

	// ********** restore model button **********
	private JButton buildRestoreModelButton() {
		return new JButton(this.buildRestoreModelAction());
	}

	private Action buildRestoreModelAction() {
		Action action = new AbstractAction("restore model") {
			public void actionPerformed(ActionEvent event) {
				DefaultListChooserUITest.this.restoreModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void restoreModel() {
		testModelHolder.setValue(testModel);
	}

	// ********** print model button **********
	private JButton buildPrintModelButton() {
		return new JButton(this.buildPrintModelAction());
	}

	private Action buildPrintModelAction() {
		Action action = new AbstractAction("print model") {
			public void actionPerformed(ActionEvent event) {
				DefaultListChooserUITest.this.printModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void printModel() {
		System.out.println(testModel);
	}

	// ********** add 20 button **********
	private JButton buildAddTenButton() {
		return new JButton(this.buildAddTenAction());
	}

	private Action buildAddTenAction() {
		Action action = new AbstractAction("add 20") {
			public void actionPerformed(ActionEvent event) {
				DefaultListChooserUITest.this.addTen();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void addTen() {
		for (int i = nextColorNumber; i < nextColorNumber + 20; i++) {
			colorListHolder.addItem(colorListHolder.size(), "color" + i);
		}
		nextColorNumber += 20;
	}

	// ********** remove 20 button **********
	private JButton buildRemoveTenButton() {
		return new JButton(this.buildRemoveTenAction());
	}

	private Action buildRemoveTenAction() {
		Action action = new AbstractAction("remove 20") {
			public void actionPerformed(ActionEvent event) {
				DefaultListChooserUITest.this.removeTen();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void removeTen() {
		for (int i = 0; i < 20; i++) {
			if (colorListHolder.size() > 0) {
				colorListHolder.removeItem(colorListHolder.size() - 1);
			}
		}
	}


private static class TestModel extends AbstractModel {
	private String color;
		public static final String COLOR_PROPERTY = "color";
		public static final String RED = "red";
		public static final String ORANGE = "orange";
		public static final String YELLOW = "yellow";
		public static final String GREEN = "green";
		public static final String BLUE = "blue";
		public static final String INDIGO = "indigo";
		public static final String VIOLET = "violet";
		public static final String DEFAULT_COLOR = RED;
		public static List validColors;
		public static final String[] DEFAULT_VALID_COLORS = {
			RED,
			ORANGE,
			YELLOW,
			GREEN,
			BLUE,
			INDIGO,
			VIOLET
		};

	public static List validColors() {
		if (validColors == null) {
			validColors = buildDefaultValidColors();
		}
		return validColors;
	}
	public static List buildDefaultValidColors() {
		List result = new ArrayList();
		CollectionTools.addAll(result, DEFAULT_VALID_COLORS);
		return result;
	}

	public TestModel() {
		this(DEFAULT_COLOR);
	}
	public TestModel(String color) {
		this.color = color;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		if ( ! validColors().contains(color)) {
			throw new IllegalArgumentException(color);
		}
		Object old = this.color;
		this.color = color;
		this.firePropertyChanged(COLOR_PROPERTY, old, color);
	}
	public String toString() {
		return "TestModel(" + color + ")";
	}
}


// ********** inner classes **********

private class LocalListChooser1 extends ListChooser {

	public LocalListChooser1(ComboBoxModel model) {
		super(model);
	}
}


private class LocalListChooser2 extends ListChooser {

	public LocalListChooser2(ComboBoxModel model) {
		super(model);
	}
	
	protected ListBrowser buildBrowser() {
		return new FilteringListBrowser();
	}
}

}
