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
package org.eclipse.persistence.tools.workbench.test.uitools.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.eclipse.persistence.tools.workbench.test.uitools.BorderIcon;
import org.eclipse.persistence.tools.workbench.test.uitools.QuadrantIcon;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.swing.CompositeIcon;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * Test out CompositeIcon by building various combinations of
 * FourSquareIcons.
 */
public class CompositeIconUITest {
	private DefaultListModel entryListModel;

	private DefaultComboBoxModel orientationModel;
		private static final String ORIENTATION_HORIZONTAL = "horizontal";
		private static final String ORIENTATION_VERTICAL = "vertical";
		private static final String[] ORIENTATION_CHOICES = new String[] {ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL};

	private DefaultComboBoxModel alignmentModel;
		private static final String ALIGNMENT_CENTER = "center";
		private static final String ALIGNMENT_TOP = "top";
		private static final String ALIGNMENT_BOTTOM = "bottom";
		private static final String ALIGNMENT_LEADING = "leading";
		private static final String ALIGNMENT_TRAILING = "trailing";
		private static final String[] HORIZONTAL_ALIGNMENT_CHOICES = new String[] {ALIGNMENT_CENTER, ALIGNMENT_TOP, ALIGNMENT_BOTTOM};
		private static final String[] VERTICAL_ALIGNMENT_CHOICES = new String[] {ALIGNMENT_CENTER, ALIGNMENT_LEADING, ALIGNMENT_TRAILING};

	private SpinnerModel redXModel;
	private SpinnerModel redYModel;
	private SpinnerModel gap1SizeModel;
	private SpinnerModel greenXModel;
	private SpinnerModel greenYModel;
	private SpinnerModel gap2SizeModel;
	private SpinnerModel blueXModel;
	private SpinnerModel blueYModel;


	public static void main(String[] args) throws Exception {
		new CompositeIconUITest().exec(args);
	}

	public CompositeIconUITest() {
		super();
		this.initializeModels();
	}

	private void initializeModels() {
		this.entryListModel = new DefaultListModel();
		this.orientationModel = new DefaultComboBoxModel(ORIENTATION_CHOICES);
		this.orientationModel.setSelectedItem(ORIENTATION_HORIZONTAL);
		this.alignmentModel = new DefaultComboBoxModel(HORIZONTAL_ALIGNMENT_CHOICES);
		this.alignmentModel.setSelectedItem(ALIGNMENT_CENTER);
		this.orientationModel.addListDataListener(this.buildOrientationListener());
		this.redXModel = this.buildIconSizeModel();
		this.redYModel = this.buildIconSizeModel();
		this.gap1SizeModel = this.buildGapSizeModel();
		this.greenXModel = this.buildIconSizeModel();
		this.greenYModel = this.buildIconSizeModel();
		this.gap2SizeModel = this.buildGapSizeModel();
		this.blueXModel = this.buildIconSizeModel();
		this.blueYModel = this.buildIconSizeModel();
	}

	/**
	 * if the selected orientation changes, synch the alignment choices
	 */
	private ListDataListener buildOrientationListener() {
		return new ListDataListener() {
			public void intervalAdded(ListDataEvent e) {
				// this does not affect the selection
			}
			public void intervalRemoved(ListDataEvent e) {
				// this does not affect the selection
			}
			public void contentsChanged(ListDataEvent e) {
				if ((e.getIndex0() == -1) && (e.getIndex1() == -1)) {
					CompositeIconUITest.this.synchronizeAlignmentModel();
				}
			}
		};
	}

	private SpinnerModel buildIconSizeModel() {
		return new SpinnerNumberModel(15, -50, 50, 1);
	}

	private SpinnerModel buildGapSizeModel() {
		return new SpinnerNumberModel(3, -50, 50, 1);
	}

	private void exec(String[] args) throws Exception {
		JFrame window = new JFrame(ClassTools.shortClassNameForObject(this));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout());
		window.getContentPane().add(this.buildListBox(), BorderLayout.CENTER);
		window.getContentPane().add(this.buildControlPanel(), BorderLayout.SOUTH);
		window.setLocation(200, 200);
		window.setSize(500, 500);
		window.setVisible(true);
	}

	private Component buildListBox() {
		JList listBox = new JList(this.entryListModel);
		listBox.setCellRenderer(new EntryRenderer());
		return new JScrollPane(listBox);
	}

	private Component buildControlPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(this.buildPositionSettingsControlPanel());
		panel.add(this.buildSizesControlPanel());
		panel.add(this.buildAddButton());
		return panel;
	}

	private Component buildPositionSettingsControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JComboBox(this.orientationModel));
		panel.add(new JComboBox(this.alignmentModel));
		return panel;
	}

	private Component buildSizesControlPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 0));

		panel.add(new JLabel("red:"));
		panel.add(new JSpinner(this.redXModel));
		panel.add(new JSpinner(this.redYModel));

		panel.add(new JLabel("gap:"));
		panel.add(new JSpinner(this.gap1SizeModel));

		panel.add(new JLabel("green:"));
		panel.add(new JSpinner(this.greenXModel));
		panel.add(new JSpinner(this.greenYModel));

		panel.add(new JLabel("gap:"));
		panel.add(new JSpinner(this.gap2SizeModel));

		panel.add(new JLabel("blue:"));
		panel.add(new JSpinner(this.blueXModel));
		panel.add(new JSpinner(this.blueYModel));

		return panel;
	}

	private Component buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		return new AbstractAction("add") {
			public void actionPerformed(ActionEvent event) {
				CompositeIconUITest.this.add();
			}
		};
	}


	void add() {
		Icon[] icons = new Icon[] {this.buildRedIcon(), this.buildGreenIcon(), this.buildBlueIcon()};
		int[] gaps = new int[] {this.gap1Size(), this.gap2Size()};
		Icon icon = new CompositeIcon(icons, gaps, this.orientationInt(), this.alignmentInt(), "RGB icon");
		icon = new BorderIcon(icon);
		this.entryListModel.addElement(new Entry(icon, this.buildTextDescription()));
	}

	private Icon buildRedIcon() {
		return new QuadrantIcon(Color.RED, this.redIconX(), this.redIconY());
	}

	private int redIconX() {
		return this.extractIntValueFrom(this.redXModel);
	}

	private int redIconY() {
		return this.extractIntValueFrom(this.redYModel);
	}

	private Icon buildGreenIcon() {
		return new QuadrantIcon(Color.GREEN, this.greenIconX(), this.greenIconY());
	}

	private int greenIconX() {
		return this.extractIntValueFrom(this.greenXModel);
	}

	private int greenIconY() {
		return this.extractIntValueFrom(this.greenYModel);
	}

	private Icon buildBlueIcon() {
		return new QuadrantIcon(Color.BLUE, this.blueIconX(), this.blueIconY());
	}

	private int blueIconX() {
		return this.extractIntValueFrom(this.blueXModel);
	}

	private int blueIconY() {
		return this.extractIntValueFrom(this.blueYModel);
	}

	private int gap1Size() {
		return this.extractIntValueFrom(this.gap1SizeModel);
	}

	private int gap2Size() {
		return this.extractIntValueFrom(this.gap2SizeModel);
	}

	private int extractIntValueFrom(SpinnerModel sm) {
		return ((Integer) sm.getValue()).intValue();
	}

	private String orientationString() {
		return (String) this.orientationModel.getSelectedItem();
	}

	private int orientationInt() {
		String orient = this.orientationString();
		if (orient == ORIENTATION_HORIZONTAL) {
			return SwingConstants.HORIZONTAL;
		}
		if (orient == ORIENTATION_VERTICAL) {
			return SwingConstants.VERTICAL;
		}
		throw new IllegalStateException(orient);
	}

	private String alignmentString() {
		return (String) this.alignmentModel.getSelectedItem();
	}

	private int alignmentInt() {
		String align = this.alignmentString();
		if (align == ALIGNMENT_CENTER) {
			return SwingConstants.CENTER;
		}
		if (align == ALIGNMENT_TOP) {
			return SwingConstants.TOP;
		}
		if (align == ALIGNMENT_BOTTOM) {
			return SwingConstants.BOTTOM;
		}
		if (align == ALIGNMENT_LEADING) {
			return SwingConstants.LEADING;
		}
		if (align == ALIGNMENT_TRAILING) {
			return SwingConstants.TRAILING;
		}
		throw new IllegalStateException(align);
	}

	// "horizontal center : 16 [3] 16 [-7] 16"
	private String buildTextDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.orientationString());
		sb.append(' ');
		sb.append(this.alignmentString());
		sb.append(" : (");
		sb.append(this.redIconX());
		sb.append('x');
		sb.append(this.redIconY());
		sb.append(") ");
		sb.append(this.gap1Size());
		sb.append(" (");
		sb.append(this.greenIconY());
		sb.append('x');
		sb.append(this.greenIconX());
		sb.append(") ");
		sb.append(this.gap2Size());
		sb.append(" (");
		sb.append(this.blueIconX());
		sb.append('x');
		sb.append(this.blueIconY());
		sb.append(')');
		return sb.toString();
	}


	void synchronizeAlignmentModel() {
		this.alignmentModel.removeAllElements();
		switch (this.orientationInt()) {
			case SwingConstants.HORIZONTAL:
				this.setAlignmentModel(HORIZONTAL_ALIGNMENT_CHOICES);
				break;
			case SwingConstants.VERTICAL:
				this.setAlignmentModel(VERTICAL_ALIGNMENT_CHOICES);
				break;
			default:
				throw new IllegalStateException();
		}
		this.alignmentModel.setSelectedItem(ALIGNMENT_CENTER);
	}

	private void setAlignmentModel(String[] entries) {
		for (int i = 0; i < entries.length; i++) {
			this.alignmentModel.addElement(entries[i]);
		}
	}


	// ********** nested classes **********

	private static class Entry {
		private Icon icon;
		private String text;
		Entry(Icon icon, String text) {
			super();
			this.icon = icon;
			this.text = text;
		}
		public Icon getIcon() {
			return this.icon;
		}
		public String getText() {
			return this.text;
		}
	}

	private static class EntryRenderer extends SimpleListCellRenderer {
		protected Icon buildIcon(Object value) {
			return ((Entry) value).getIcon();
		}
		protected String buildText(Object value) {
			return ((Entry) value).getText();
		}
	}

}
