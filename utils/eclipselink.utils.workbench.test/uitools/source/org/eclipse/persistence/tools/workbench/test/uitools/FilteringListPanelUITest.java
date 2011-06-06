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
package org.eclipse.persistence.tools.workbench.test.uitools;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.eclipse.persistence.tools.workbench.uitools.FilteringListPanel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.Classpath;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;

/**
 * Simple test class for playing around with the FilteringListPanel.
 * 
 * Optional command line parm:
 * 	the name of a jar (or class folder) to use to populate the list box
 */
public class FilteringListPanelUITest {
	private Object[] completeList1;
	private Object[] completeList2;
	private FilteringListPanel filteringListPanel;
	private Font font;

	public static void main(String[] args) {
		new FilteringListPanelUITest().exec(args);
	}

	private FilteringListPanelUITest() {
		super();
		this.initialize();
	}

	private void initialize() {
		this.font = this.buildFont();
	}

	private Font buildFont() {
		return new Font("Dialog", Font.PLAIN, 12);
	}

	private void exec(String[] args) {
		this.completeList1 = this.buildTypeList(args);
		this.completeList2 = this.buildCompleteList2();
		JFrame frame = new JFrame(ClassTools.shortClassNameForObject(this));
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this.buildWindowListener());
		frame.getContentPane().add(this.buildMainPanel(), "Center");
		frame.setLocation(300, 300);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

	private Object[] buildTypeList(String[] args) {
		return CollectionTools.sort(CollectionTools.array(this.buildTypes(args)));
	}

	private Object[] buildCompleteList2() {
		String classpathEntry = Classpath.locationFor(this.getClass());
		return CollectionTools.sort(CollectionTools.array(this.buildTypes(new String[] {classpathEntry})));
	}

	private Iterator buildTypes(String[] args) {
		return new TransformationIterator(this.buildClassNames(args)) {
			protected Object transform(Object next) {
				return new Type((String) next);
			}
		};
	}

	private Iterator buildClassNames(String[] args) {
		if ((args == null) || (args.length == 0)) {
			return Classpath.bootClasspath().classNamesStream();
		}
		return new Classpath(new String[] {args[0]}).classNamesStream();
	}
	
	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}

	private JPanel buildMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		this.filteringListPanel = this.buildFilteringListPanel();
		panel.add(this.filteringListPanel, BorderLayout.CENTER);
		panel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private FilteringListPanel buildFilteringListPanel() {
		Object initialSelection = this.typeNamedIn("java.lang.Object", this.completeList1);
		FilteringListPanel panel = new FilteringListPanel(this.completeList1, initialSelection, this.buildStringConverter());
		panel.setTextFieldLabelText("Choose a Type (? = any char, * = any string):");
		panel.setListBoxLabelText("Matching Types:");
		panel.setComponentsFont(this.font);
		panel.setListBoxCellRenderer(this.buildRenderer());
		return panel;
	}

	private StringConverter buildStringConverter() {
		return new StringConverter() {
			public String convertToString(Object o) {
				return (o == null) ? "" : ((Type) o).getName();
			}
		};
	}

	private ListCellRenderer buildRenderer() {
		return new SimpleListCellRenderer() {
			protected Icon buildIcon(Object value) {
				return UIManager.getIcon("Tree.leafIcon");
			}
			protected String buildText(Object value) {
				return ((Type) value).getName();
			}
		};
	}

	private JPanel buildControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(this.buildSwapButton());
		panel.add(this.buildStringButton());
		panel.add(this.buildNullButton());
		panel.add(this.buildMax10Button());
		panel.add(this.buildPrintButton());
		return panel;
	}

	// ********** swap button **********

	private JButton buildSwapButton() {
		JButton button = new JButton(this.buildSwapAction());
		button.setFont(this.font);
		return button;
	}

	private Action buildSwapAction() {
		return new AbstractAction("swap") {
			public void actionPerformed(ActionEvent event) {
				FilteringListPanelUITest.this.swap();
			}
		};
	}

	/**
	 * swap in a new list
	 */
	void swap() {
		if (this.filteringListPanel.getCompleteList() == this.completeList1) {
			this.filteringListPanel.setCompleteList(this.completeList2);
		} else {
			this.filteringListPanel.setCompleteList(this.completeList1);
		}
	}

	// ********** string button **********

	private JButton buildStringButton() {
		JButton button = new JButton(this.buildStringAction());
		button.setFont(this.font);
		return button;
	}

	private Action buildStringAction() {
		return new AbstractAction("String") {
			public void actionPerformed(ActionEvent event) {
				FilteringListPanelUITest.this.selectStringType();
			}
		};
	}

	/**
	 * force a selection from "outside" the filtering list panel
	 */
	void selectStringType() {
		this.filteringListPanel.setSelection(this.typeNamed("java.lang.String"));
	}

	private Type typeNamed(String name) {
		return this.typeNamedIn(name, this.filteringListPanel.getCompleteList());
	}

	private Type typeNamedIn(String name, Object[] list) {
		for (int i = list.length; i-- > 0; ) {
			Type type = (Type) list[i];
			if (type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}

	// ********** null button **********

	private JButton buildNullButton() {
		JButton button = new JButton(this.buildNullAction());
		button.setFont(this.font);
		return button;
	}

	private Action buildNullAction() {
		return new AbstractAction("null") {
			public void actionPerformed(ActionEvent event) {
				FilteringListPanelUITest.this.selectNull();
			}
		};
	}

	/**
	 * set the current selection to null
	 */
	void selectNull() {
		this.filteringListPanel.setSelection(null);
	}

	// ********** null button **********

	private JButton buildMax10Button() {
		JButton button = new JButton(this.buildMax10Action());
		button.setFont(this.font);
		return button;
	}

	private Action buildMax10Action() {
		return new AbstractAction("max = 10") {
			public void actionPerformed(ActionEvent event) {
				FilteringListPanelUITest.this.setMax10();
			}
		};
	}

	/**
	 * toggle between allowing only 10 entries in the list box
	 * and no limit
	 */
	void setMax10() {
		if (this.filteringListPanel.getMaxListSize() == 10) {
			this.filteringListPanel.setMaxListSize(-1);
		} else {
			this.filteringListPanel.setMaxListSize(10);
		}
	}

	// ********** print button **********

	private JButton buildPrintButton() {
		JButton button = new JButton(this.buildPrintAction());
		button.setFont(this.font);
		return button;
	}

	private Action buildPrintAction() {
		return new AbstractAction("print") {
			public void actionPerformed(ActionEvent event) {
				FilteringListPanelUITest.this.printType();
			}
		};
	}

	/**
	 * print the currently selected type to the console
	 */
	void printType() {
		System.out.println("selected item: " + this.filteringListPanel.getSelection());
	}


	// ********** inner class **********
	private class Type implements Comparable {
		private String name;
	
		Type(String name) {
			super();
			this.name = name;
		}
		public String shortName() {
			return ClassTools.shortNameForClassNamed(this.name);
		}
		public String getName() {
			return this.name;
		}
		public String toString() {
			return "Type: " + this.name ;
		}
		public int compareTo(Object o) {
			return this.name.compareTo(((Type) o).name);
		}
	}

}
