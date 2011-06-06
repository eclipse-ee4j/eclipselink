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
import java.util.Date;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.Crowd;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.Person;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.PersonColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ColumnAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DateSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TableModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SpinnerTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TableCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * an example UI for testing the TableModelAdapter
 * 	"name" column is read-only text field
 * 	"birth date" column is date text field
 * 	"gone west date" column is date spinner
 * 	"eye color" column is combo-box
 * 	"evil" column is check box
 * 	"rank" column is number text field
 * 	"adventure count" column is number spinner
 * 
 * Note that the table model and row selection model share the same
 * list value model (the sorted people adapter)
 */
public class TableModelAdapterUITest {
	private CollectionValueModel eyeColorListHolder;
	private PropertyValueModel crowdHolder;
	private PropertyValueModel selectedPersonHolder;
	private ListValueModel sortedPeopleAdapter;
	private TableModel tableModel;
	private ObjectListSelectionModel rowSelectionModel;
	private Action removeAction;
	private Action renameAction;

	public static void main(String[] args) throws Exception {
		new TableModelAdapterUITest().exec(args);
	}

	protected TableModelAdapterUITest() {
		super();
	}

	protected void exec(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.eyeColorListHolder = this. buildEyeColorCollectionHolder();
		this.crowdHolder = this.buildCrowdHolder();
		this.selectedPersonHolder = this.buildSelectedPersonHolder();
		this.sortedPeopleAdapter = this.buildSortedPeopleAdapter();
		this.tableModel = this.buildTableModel();
		this.rowSelectionModel = this.buildRowSelectionModel();
		this.openWindow();
	}

	private CollectionValueModel buildEyeColorCollectionHolder() {
		return new SimpleCollectionValueModel(Person.getValidEyeColors());
	}

	private PropertyValueModel buildCrowdHolder() {
		return new SimplePropertyValueModel(this.buildCrowd());
	}

	private Crowd buildCrowd() {
		Crowd crowd = new Crowd();

		Person p = crowd.addPerson("Bilbo");
		p.setEyeColor(Person.EYE_COLOR_BROWN);
		p.setRank(22);
		p.setAdventureCount(1);

		p = crowd.addPerson("Gollum");
		p.setEyeColor(Person.EYE_COLOR_PINK);
		p.setEvil(true);
		p.setRank(2);
		p.setAdventureCount(50);

		p = crowd.addPerson("Frodo");
		p.setEyeColor(Person.EYE_COLOR_BLUE);
		p.setRank(34);
		p.setAdventureCount(1);

		p = crowd.addPerson("Samwise");
		p.setEyeColor(Person.EYE_COLOR_GREEN);
		p.setRank(19);
		p.setAdventureCount(1);

		return crowd;
	}

	private PropertyValueModel buildSelectedPersonHolder() {
		return new SimplePropertyValueModel();
	}

	private ListValueModel buildSortedPeopleAdapter() {
		return new SortedListValueModelAdapter(this.buildPeopleNameAdapter());
	}

	// the list will need to be re-sorted if a name changes
	private ListValueModel buildPeopleNameAdapter() {
		return new ItemPropertyListValueModelAdapter(this.buildPeopleAdapter(), Person.NAME_PROPERTY);
	}

	private CollectionValueModel buildPeopleAdapter() {
		return new CollectionAspectAdapter(this.crowdHolder, Crowd.PEOPLE_COLLECTION) {
			protected Iterator getValueFromSubject() {
				return ((Crowd) this.subject).people();
			}
			protected int sizeFromSubject() {
				return ((Crowd) this.subject).peopleSize();
			}
		};
	}

	private TableModel buildTableModel() {
		return new TableModelAdapter(this.sortedPeopleAdapter, this.buildColumnAdapter());
	}

	protected ColumnAdapter buildColumnAdapter() {
		return new PersonColumnAdapter();
	}

	private ObjectListSelectionModel buildRowSelectionModel() {
		ObjectListSelectionModel rsm = new ObjectListSelectionModel(new ListModelAdapter(this.sortedPeopleAdapter));
		rsm.addListSelectionListener(this.buildRowSelectionListener());
		rsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return rsm;
	}

	private ListSelectionListener buildRowSelectionListener() {
		return new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				TableModelAdapterUITest.this.rowSelectionChanged(e);
			}
		};
	}

	void rowSelectionChanged(ListSelectionEvent e) {
		Object selection = this.rowSelectionModel.getSelectedValue();
		this.selectedPersonHolder.setValue(selection);
		boolean personSelected = (selection != null);
		this.removeAction.setEnabled(personSelected);
		this.renameAction.setEnabled(personSelected);
	}

	private void openWindow() {
		JFrame window = new JFrame(ClassTools.shortClassNameForObject(this));
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setLocation(200, 200);
		window.setSize(600, 400);
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
		mainPanel.add(this.buildTablePane(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildTablePane() {
		return new JScrollPane(this.buildTable());
	}

	private JTable buildTable() {
		JTable table = new JTable(this.tableModel);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);	// see Java bug 5007652
		table.setSelectionModel(this.rowSelectionModel);
		table.setDoubleBuffered(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		int rowHeight = 20;	// start with minimum of 20

		// gone west column (spinner)
		TableColumn column = table.getColumnModel().getColumn(PersonColumnAdapter.GONE_WEST_DATE_COLUMN);
		SpinnerTableCellRenderer spinnerRenderer = this.buildDateSpinnerRenderer();
		column.setCellRenderer(spinnerRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildDateSpinnerRenderer()));
		rowHeight = Math.max(rowHeight, spinnerRenderer.getPreferredHeight());

		// eye color column (combo-box)
		// the jdk combo-box renderer looks like a text field
		// until the user starts an edit - use a custom one
		column = table.getColumnModel().getColumn(PersonColumnAdapter.EYE_COLOR_COLUMN);
		ComboBoxTableCellRenderer eyeColorRenderer = this.buildEyeColorComboBoxRenderer();
		column.setCellRenderer(eyeColorRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildEyeColorComboBoxRenderer()));
		rowHeight = Math.max(rowHeight, eyeColorRenderer.getPreferredHeight());

		// evil (check box)
		// the jdk check box renderer and editor suck - use a custom ones
		column = table.getColumnModel().getColumn(PersonColumnAdapter.EVIL_COLUMN);
		CheckBoxTableCellRenderer evilRenderer = new CheckBoxTableCellRenderer();
		column.setCellRenderer(evilRenderer);
		column.setCellEditor(new TableCellEditorAdapter(new CheckBoxTableCellRenderer()));
		rowHeight = Math.max(rowHeight, evilRenderer.getPreferredHeight());

		// adventure count column (spinner)
		column = table.getColumnModel().getColumn(PersonColumnAdapter.ADVENTURE_COUNT_COLUMN);
		spinnerRenderer = this.buildNumberSpinnerRenderer();
		column.setCellRenderer(spinnerRenderer);
		column.setCellEditor(new TableCellEditorAdapter(this.buildNumberSpinnerRenderer()));
		rowHeight = Math.max(rowHeight, spinnerRenderer.getPreferredHeight());

		table.setRowHeight(rowHeight);
		return table;
	}

	private SpinnerTableCellRenderer buildDateSpinnerRenderer() {
		return new SpinnerTableCellRenderer(new DateSpinnerModelAdapter(new SimplePropertyValueModel()));
	}

	private SpinnerTableCellRenderer buildNumberSpinnerRenderer() {
		return new SpinnerTableCellRenderer(new NumberSpinnerModelAdapter(new SimplePropertyValueModel()));
	}

	private ComboBoxTableCellRenderer buildEyeColorComboBoxRenderer() {
		return new ComboBoxTableCellRenderer(this.buildReadOnlyEyeColorComboBoxModel(), this.buildEyeColorRenderer());
	}

	private ComboBoxModel buildReadOnlyEyeColorComboBoxModel() {
		return new ComboBoxModelAdapter(this.eyeColorListHolder, new SimplePropertyValueModel());
	}

	private ListCellRenderer buildEyeColorRenderer() {
		return new EyeColorRenderer();
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(0, 1));
		controlPanel.add(this.buildButtonPanel());
		controlPanel.add(this.buildPersonPanel());
		return controlPanel;
	}

	private Component buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
		buttonPanel.add(this.buildAddButton());
		buttonPanel.add(this.buildRemoveButton());
		buttonPanel.add(this.buildRenameButton());
		buttonPanel.add(this.buildAddEyeColorButton());
		buttonPanel.add(this.buildPrintButton());
		buttonPanel.add(this.buildResetButton());
		return buttonPanel;
	}

	private Component buildPersonPanel() {
		JPanel personPanel = new JPanel(new GridLayout(1, 0));
		personPanel.add(this.buildNameTextField());
		personPanel.add(this.buildBirthDateSpinner());
		personPanel.add(this.buildGoneWestDateSpinner());
		personPanel.add(this.buildEyeColorComboBox());
		personPanel.add(this.buildEvilCheckBox());
		personPanel.add(this.buildRankSpinner());
		personPanel.add(this.buildAdventureCountSpinner());
		return personPanel;
	}


	// ********** add button **********

	private JButton buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		Action action = new AbstractAction("add") {
			public void actionPerformed(ActionEvent event) {
				TableModelAdapterUITest.this.addPerson();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void addPerson() {
		String name = this.getNameFromUser();
		if (name != null) {
			this.setSelectedPerson(this.crowd().addPerson(name));
		}
	}


	// ********** remove button **********

	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		this.removeAction = new AbstractAction("remove") {
			public void actionPerformed(ActionEvent event) {
				TableModelAdapterUITest.this.removePerson();
			}
		};
		this.removeAction.setEnabled(false);
		return this.removeAction;
	}

	void removePerson() {
		Person person = this.selectedPerson();
		if (person != null) {
			this.crowd().removePerson(person);
		}
	}


	// ********** rename button **********

	private JButton buildRenameButton() {
		return new JButton(this.buildRenameAction());
	}

	private Action buildRenameAction() {
		this.renameAction = new AbstractAction("rename") {
			public void actionPerformed(ActionEvent event) {
				TableModelAdapterUITest.this.renamePerson();
			}
		};
		this.renameAction.setEnabled(false);
		return this.renameAction;
	}

	void renamePerson() {
		Person person = this.selectedPerson();
		if (person != null) {
			String name = this.promptUserForName(person.getName());
			if (name != null) {
				person.setName(name);
				this.setSelectedPerson(person);
			}
		}
	}


	// ********** add eye color button **********

	private JButton buildAddEyeColorButton() {
		return new JButton(this.buildAddEyeColorAction());
	}

	private Action buildAddEyeColorAction() {
		Action action = new AbstractAction("add eye color") {
			public void actionPerformed(ActionEvent event) {
				TableModelAdapterUITest.this.addEyeColor();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void addEyeColor() {
		String color = this.promptUserForEyeColor();
		if (color != null) {
			this.eyeColorListHolder.addItem(color);
		}
	}

	private String promptUserForEyeColor() {
		while (true) {
			String eyeColor = JOptionPane.showInputDialog("Eye Color");
			if (eyeColor == null) {
				return null;		// user pressed <Cancel>
			}
			if ((eyeColor.length() == 0)) {
				JOptionPane.showMessageDialog(null, "The eye color is required.", "Invalid Eye Color", JOptionPane.ERROR_MESSAGE);
			} else if (CollectionTools.contains((Iterator) this.eyeColorListHolder.getValue(), eyeColor)) {
				JOptionPane.showMessageDialog(null, "The eye color already exists.", "Invalid Eye Color", JOptionPane.ERROR_MESSAGE);
			} else {
				return eyeColor;
			}
		}
	}


	// ********** print button **********

	private JButton buildPrintButton() {
		return new JButton(this.buildPrintAction());
	}

	private Action buildPrintAction() {
		Action action = new AbstractAction("print") {
			public void actionPerformed(ActionEvent event) {
				TableModelAdapterUITest.this.printCrowd();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void printCrowd() {
		System.out.println(this.crowd());
		for (Iterator stream = this.crowd().people(); stream.hasNext(); ) {
			System.out.println("\t" + stream.next());
		}
	}


	// ********** reset button **********

	private JButton buildResetButton() {
		return new JButton(this.buildResetAction());
	}

	private Action buildResetAction() {
		Action action = new AbstractAction("reset") {
			public void actionPerformed(ActionEvent event) {
				TableModelAdapterUITest.this.reset();
			}
		};
		action.setEnabled(true);
		return action;
	}

	void reset() {
		this.crowdHolder.setValue(this.buildCrowd());
	}


	// ********** new name dialog **********

	private String getNameFromUser() {
		return this.promptUserForName(null);
	}

	private String promptUserForName(String originalName) {
		while (true) {
			String name = JOptionPane.showInputDialog("Person Name");
			if (name == null) {
				return null;		// user pressed <Cancel>
			}
			if ((name.length() == 0)) {
				JOptionPane.showMessageDialog(null, "The name is required.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
			} else if (CollectionTools.contains(this.crowd().peopleNames(), name)) {
				JOptionPane.showMessageDialog(null, "The name already exists.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
			} else {
				return name;
			}
		}
	}


	// ********** name text field **********

	private Component buildNameTextField() {
		JTextField textField = new JTextField(this.buildNameDocument(), null, 0);
		textField.setEditable(false);
		return textField;
	}

	private Document buildNameDocument() {
		return new DocumentAdapter(this.buildNameAdapter());
	}

	private PropertyValueModel buildNameAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.NAME_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getName();
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setName((String) value);
			}
		};
	}


	// ********** birth date spinner **********

	private JSpinner buildBirthDateSpinner() {
		return new JSpinner(this.buildBirthDateSpinnerModel());
	}

	private SpinnerModel buildBirthDateSpinnerModel() {
		return new DateSpinnerModelAdapter(this.buildBirthDateAdapter());
	}

	private PropertyValueModel buildBirthDateAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.BIRTH_DATE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getBirthDate();
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setBirthDate((Date) value);
			}
		};
	}


	// ********** gone west date spinner **********

	private JSpinner buildGoneWestDateSpinner() {
		return new JSpinner(this.buildGoneWestDateSpinnerModel());
	}

	private SpinnerModel buildGoneWestDateSpinnerModel() {
		return new DateSpinnerModelAdapter(this.buildGoneWestDateAdapter());
	}

	private PropertyValueModel buildGoneWestDateAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.GONE_WEST_DATE_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getGoneWestDate();
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setGoneWestDate((Date) value);
			}
		};
	}


	// ********** eye color combo-box **********

	private JComboBox buildEyeColorComboBox() {
		return new JComboBox(this.buildEyeColorComboBoxModel());
	}

	private ComboBoxModel buildEyeColorComboBoxModel() {
		return new ComboBoxModelAdapter(this.eyeColorListHolder, this.buildEyeColorAdapter());
	}

	private PropertyValueModel buildEyeColorAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.EYE_COLOR_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getEyeColor();
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setEyeColor((String) value);
			}
		};
	}


	// ********** evil check box **********

	private JCheckBox buildEvilCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("evil");
		checkBox.setModel(this.buildEvilCheckBoxModel());
		return checkBox;
	}

	private ButtonModel buildEvilCheckBoxModel() {
		return new CheckBoxModelAdapter(this.buildEvilAdapter());
	}

	private PropertyValueModel buildEvilAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.EVIL_PROPERTY) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((Person) this.subject).isEvil());
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setEvil(((Boolean) value).booleanValue());
			}
		};
	}


	// ********** rank spinner **********

	private JSpinner buildRankSpinner() {
		return new JSpinner(this.buildRankSpinnerModel());
	}

	private SpinnerModel buildRankSpinnerModel() {
		return new NumberSpinnerModelAdapter(this.buildRankAdapter());
	}

	private PropertyValueModel buildRankAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.RANK_PROPERTY) {
			protected Object getValueFromSubject() {
				return new Integer(((Person) this.subject).getRank());
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setRank(((Integer) value).intValue());
			}
		};
	}


	// ********** adventure count spinner **********

	private JSpinner buildAdventureCountSpinner() {
		return new JSpinner(this.buildAdventureCountSpinnerModel());
	}

	private SpinnerModel buildAdventureCountSpinnerModel() {
		return new NumberSpinnerModelAdapter(this.buildAdventureCountAdapter());
	}

	private PropertyValueModel buildAdventureCountAdapter() {
		return new PropertyAspectAdapter(this.selectedPersonHolder, Person.ADVENTURE_COUNT_PROPERTY) {
			protected Object getValueFromSubject() {
				return new Integer(((Person) this.subject).getAdventureCount());
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setAdventureCount(((Integer) value).intValue());
			}
		};
	}


	// ********** queries **********

	private Crowd crowd() {
		return (Crowd) this.crowdHolder.getValue();
	}

	private Person selectedPerson() {
		if (this.rowSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (Person) this.rowSelectionModel.getSelectedValue();
	}

	private void setSelectedPerson(Person person) {
		this.rowSelectionModel.setSelectedValue(person);
	}


	// ********** custom renderer **********
	
	/**
	 * This is simply an example of a renderer for the embedded combo-box.
	 * It does nothing special unless you uncomment the code below....
	 */
	private class EyeColorRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			// just do something to show the renderer is working...
	//		value = ">" + value;
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}

}
