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
package org.eclipse.persistence.tools.workbench.test.uitools.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.FilteringListBrowser;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.IndirectComboBoxModel;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;

public class IndirectComboBoxModelUITests
{
	protected JFrame window;
	private TestModel testModel;
	private PropertyValueModel testModelHolder;
	private PropertyValueModel colorHolder;
	protected CachingComboBoxModel colorComboBoxModel;
	
	public static void main(String[] args) throws Exception {
		new IndirectComboBoxModelUITests().exec(args);
	}

	protected IndirectComboBoxModelUITests() {
		super();
	}

	protected void exec(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());	// Metal LAF
//		UIManager.setLookAndFeel(com.sun.java.swing.plaf.windows.WindowsLookAndFeel.class.getName());
//		UIManager.setLookAndFeel(com.sun.java.swing.plaf.motif.MotifLookAndFeel.class.getName());
//		UIManager.setLookAndFeel(oracle.bali.ewt.olaf.OracleLookAndFeel.class.getName());
		this.testModel = this.buildTestModel();
		this.testModelHolder = new SimplePropertyValueModel(this.testModel);
		this.colorHolder = this.buildColorHolder(this.testModelHolder);
		this.colorComboBoxModel = this.buildIndirectComboBoxModel(this.colorHolder, this.testModelHolder);
		this.openWindow();
	}

	private PropertyValueModel buildColorHolder(ValueModel vm) {
		return new PropertyAspectAdapter(vm, TestModel.COLOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((TestModel) this.subject).getColor();
			}
			protected void setValueOnSubject(Object value) {
				((TestModel) this.subject).setColor((String) value);
			}
		};
	}

	protected TestModel buildTestModel() {
		return new TestModel();
	}

	private CachingComboBoxModel buildIndirectComboBoxModel(PropertyValueModel selectionHolder, PropertyValueModel pvm) {
		return new IndirectComboBoxModel(selectionHolder, pvm) {
			protected ListIterator listValueFromSubject(Object subject) {
				return ((TestModel) subject).validColors().listIterator();
			}
		};
	}

	private void openWindow() {
		this.window = new JFrame(ClassTools.shortNameFor(this.getClass()));
		this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.window.addWindowListener(this.buildWindowListener());
		this.window.getContentPane().add(this.buildMainPanel(), "Center");
		this.window.setLocation(300, 300);
		this.window.setSize(400, 150);
		this.window.setVisible(true);
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
		return panel;
	}

	private JComboBox buildComboBox() {
		JComboBox comboBox = new JComboBox(this.colorComboBoxModel);
		comboBox.setRenderer(this.buildComboBoxRenderer());
		return comboBox;
	}

	protected ListCellRenderer buildComboBoxRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return super.buildText(value);
			}
		};
	}

	private ListChooser buildListChooser1() {
		return new LocalListChooser1(this.colorComboBoxModel);
	}

	private ListChooser buildListChooser2() {
		return new LocalListChooser2(this.colorComboBoxModel);
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(2, 0));
		controlPanel.add(this.buildResetColorButton());
		controlPanel.add(this.buildClearModelButton());
		controlPanel.add(this.buildRestoreModelButton());
		controlPanel.add(this.buildPrintModelButton());
		controlPanel.add(this.buildAddTwentyButton());
		controlPanel.add(this.buildRemoveTwentyButton());
		return controlPanel;
	}

	// ********** reset color button **********
	private JButton buildResetColorButton() {
		return new JButton(this.buildResetColorAction());
	}

	private Action buildResetColorAction() {
		Action action = new AbstractAction("reset color") {
			public void actionPerformed(ActionEvent event) {
				IndirectComboBoxModelUITests.this.resetColor();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void resetColor() {
		this.testModel.setColor(TestModel.DEFAULT_COLOR);
	}

	// ********** clear model button **********
	private JButton buildClearModelButton() {
		return new JButton(this.buildClearModelAction());
	}

	private Action buildClearModelAction() {
		Action action = new AbstractAction("clear model") {
			public void actionPerformed(ActionEvent event) {
				IndirectComboBoxModelUITests.this.clearModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void clearModel() {
		this.testModelHolder.setValue(null);
	}

	// ********** restore model button **********
	private JButton buildRestoreModelButton() {
		return new JButton(this.buildRestoreModelAction());
	}

	private Action buildRestoreModelAction() {
		Action action = new AbstractAction("restore model") {
			public void actionPerformed(ActionEvent event) {
				IndirectComboBoxModelUITests.this.restoreModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void restoreModel() {
		this.testModelHolder.setValue(this.testModel);
	}

	// ********** print model button **********
	private JButton buildPrintModelButton() {
		return new JButton(this.buildPrintModelAction());
	}

	private Action buildPrintModelAction() {
		Action action = new AbstractAction("print model") {
			public void actionPerformed(ActionEvent event) {
				IndirectComboBoxModelUITests.this.printModel();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void printModel() {
		System.out.println(this.testModel);
	}

	// ********** add 20 button **********
	private JButton buildAddTwentyButton() {
		return new JButton(this.buildAddTwentyAction());
	}
	
	private Action buildAddTwentyAction() {
		Action action = new AbstractAction("add 20") {
			public void actionPerformed(ActionEvent event) {
				IndirectComboBoxModelUITests.this.addTwenty();
			}
		};
		action.setEnabled(true);
		return action;
	}
	
	void addTwenty() {
		this.testModel.addTwenty();
	}

	// ********** remove 20 button **********
	private JButton buildRemoveTwentyButton() {
		return new JButton(this.buildRemoveTwentyAction());
	}

	private Action buildRemoveTwentyAction() {
		Action action = new AbstractAction("remove 20") {
			public void actionPerformed(ActionEvent event) {
				IndirectComboBoxModelUITests.this.removeTwenty();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void removeTwenty() {
		this.testModel.removeTwenty();
	}


	protected static class TestModel extends AbstractModel {
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
			private int nextColorNumber = 0;
	
		public List validColors() {
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
			return this.color;
		}
		public void setColor(String color) {
			this.checkColor(color);
			Object old = this.color;
			this.color = color;
			this.firePropertyChanged(COLOR_PROPERTY, old, color);
		}
		public void checkColor(String c) {
			if ( ! validColors().contains(c)) {
				throw new IllegalArgumentException(c);
			}
		}
		public void addTwenty() {
			for (int i = this.nextColorNumber; i < this.nextColorNumber + 20; i++) {
				validColors.add(validColors.size(), "color" + i);
			}
			this.nextColorNumber += 20;
		}
		public void removeTwenty() {
			for (int i = 0; i < 20; i++) {
				if (validColors.size() > DEFAULT_VALID_COLORS.length) {
					validColors.remove(validColors.size() - 1);
				}
			}
		}
		public String toString() {
			return "TestModel(" + this.color + ")";
		}
	}
	
	
	private class LocalListChooser1 extends ListChooser {
		public LocalListChooser1(CachingComboBoxModel model) {
			super(model);
		}
	}
	
	
	private class LocalListChooser2 extends ListChooser {
		public LocalListChooser2(CachingComboBoxModel model) {
			super(model);
		}
		protected ListBrowser buildBrowser() {
			return new FilteringListBrowser();
		}
	}

}
