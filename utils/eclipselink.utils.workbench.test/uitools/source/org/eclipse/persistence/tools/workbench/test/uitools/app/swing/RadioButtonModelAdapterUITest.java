/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.RadioButtonModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * Play around with a set of radio buttons.
 */
public class RadioButtonModelAdapterUITest {

	private TestModel testModel;
	private PropertyValueModel testModelHolder;
	private PropertyValueModel colorHolder;
	private ButtonModel redButtonModel;
	private ButtonModel greenButtonModel;
	private ButtonModel blueButtonModel;

	public static void main(String[] args) throws Exception {
		new RadioButtonModelAdapterUITest().exec(args);
	}

	private RadioButtonModelAdapterUITest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		this.testModel = new TestModel();
		this.testModelHolder = new SimplePropertyValueModel(this.testModel);
		this.colorHolder = this.buildColorHolder(this.testModelHolder);
		this.redButtonModel = this.buildRadioButtonModelAdapter(this.colorHolder, TestModel.RED);
		this.greenButtonModel = this.buildRadioButtonModelAdapter(this.colorHolder, TestModel.GREEN);
		this.blueButtonModel = this.buildRadioButtonModelAdapter(this.colorHolder, TestModel.BLUE);
		this.openWindow();
	}

	private PropertyValueModel buildColorHolder(ValueModel subjectHolder) {
		return new PropertyAspectAdapter(subjectHolder, TestModel.COLOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((TestModel) this.subject).getColor();
			}
			protected void setValueOnSubject(Object value) {
				((TestModel) this.subject).setColor((String) value);
			}
		};
	}

	private ButtonModel buildRadioButtonModelAdapter(PropertyValueModel colorPVM, String color) {
		return new RadioButtonModelAdapter(colorPVM, color);
	}

	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(400, 100);
		window.setLocation(200, 200);
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
		mainPanel.add(this.buildRadioButtonPanel(), BorderLayout.NORTH);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildRadioButtonPanel() {
		JPanel taskListPanel = new JPanel(new GridLayout(1, 0));
		taskListPanel.add(this.buildRedRadioButton());
		taskListPanel.add(this.buildGreenRadioButton());
		taskListPanel.add(this.buildBlueRadioButton());
		return taskListPanel;
	}

	private JRadioButton buildRedRadioButton() {
		JRadioButton radioButton = new JRadioButton();
		radioButton.setText("red");
		radioButton.setModel(this.redButtonModel);
		return radioButton;
	}

	private JRadioButton buildGreenRadioButton() {
		JRadioButton radioButton = new JRadioButton();
		radioButton.setText("green");
		radioButton.setModel(this.greenButtonModel);
		return radioButton;
	}

	private JRadioButton buildBlueRadioButton() {
		JRadioButton radioButton = new JRadioButton();
		radioButton.setText("blue");
		radioButton.setModel(this.blueButtonModel);
		return radioButton;
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(1, 0));
		controlPanel.add(this.buildResetColorButton());
		controlPanel.add(this.buildClearModelButton());
		controlPanel.add(this.buildRestoreModelButton());
		controlPanel.add(this.buildPrintModelButton());
		return controlPanel;
	}

	private JButton buildResetColorButton() {
		return new JButton(this.buildResetColorAction());
	}

	private Action buildResetColorAction() {
		Action action = new AbstractAction("reset color") {
			public void actionPerformed(ActionEvent event) {
				RadioButtonModelAdapterUITest.this.resetColor();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void resetColor() {
		this.testModel.setColor(TestModel.DEFAULT_COLOR);
	}

	private JButton buildClearModelButton() {
		return new JButton(this.buildClearModelAction());
	}

	private Action buildClearModelAction() {
		Action action = new AbstractAction("clear model") {
			public void actionPerformed(ActionEvent event) {
				RadioButtonModelAdapterUITest.this.clearModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void clearModel() {
		this.testModelHolder.setValue(null);
	}

	private JButton buildRestoreModelButton() {
		return new JButton(this.buildRestoreModelAction());
	}

	private Action buildRestoreModelAction() {
		Action action = new AbstractAction("restore model") {
			public void actionPerformed(ActionEvent event) {
				RadioButtonModelAdapterUITest.this.restoreModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void restoreModel() {
		this.testModelHolder.setValue(this.testModel);
	}

	private JButton buildPrintModelButton() {
		return new JButton(this.buildPrintModelAction());
	}

	private Action buildPrintModelAction() {
		Action action = new AbstractAction("print model") {
			public void actionPerformed(ActionEvent event) {
				RadioButtonModelAdapterUITest.this.printModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void printModel() {
		System.out.println(this.testModel);
	}


private static class TestModel extends AbstractModel {
	private String color;
		public static final String COLOR_PROPERTY = "color";
		public static final String RED = "red";
		public static final String GREEN = "green";
		public static final String BLUE = "blue";
		public static final String DEFAULT_COLOR = RED;
		public static final String[] VALID_COLORS = {
			RED,
			GREEN,
			BLUE
		};

	public TestModel() {
		this(DEFAULT_COLOR);
	}
	public TestModel(String color) {
		this.color = color;
	}
	public String getColor() {
		return this.color;
	}
	public void setColor(String color) {
		if ( ! CollectionTools.contains(VALID_COLORS, color)) {
			throw new IllegalArgumentException(color);
		}
		Object old = this.color;
		this.color = color;
		this.firePropertyChanged(COLOR_PROPERTY, old, color);
	}
	public String toString() {
		return "TestModel(" + this.color + ")";
	}
}

}
