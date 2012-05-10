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
package org.eclipse.persistence.tools.workbench.test.uitools.cell;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Document;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.Crowd;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TableModelAdapterTests.Person;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DateSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.NumberSpinnerModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.CheckBoxTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.EditingNode;
import org.eclipse.persistence.tools.workbench.uitools.cell.NodeTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.NodeTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.NullTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SpinnerTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.SpinnerTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TextFieldTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.TextFieldTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;


/**
 * an example UI for testing various tree cell editors
 */
public class TreeCellEditorUITest {
	CollectionValueModel eyeColorListHolder;
	private PropertyValueModel crowdHolder;
	private PropertyValueModel selectedPersonHolder;
	private TreeModel treeModel;
	private TreeSelectionModel treeSelectionModel;
	private Map renderers;
	private Map editors;

	// hold these actions so we can enable/disable them
	private Action removeAction;
	private Action renameAction;


	public static void main(String[] args) throws Exception {
		new TreeCellEditorUITest().exec(args);
	}

	protected TreeCellEditorUITest() {
		super();
	}

	protected void exec(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.eyeColorListHolder = this. buildEyeColorCollectionHolder();
		this.crowdHolder = this.buildCrowdHolder();
		this.selectedPersonHolder = this.buildSelectedPersonHolder();
		this.treeModel = this.buildTreeModel();
		this.treeSelectionModel = this.buildTreeSelectionModel();
		this.renderers = new HashMap();
		this.editors = new HashMap();
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

	private TreeModel buildTreeModel() {
		return new TreeModelAdapter(this.buildCrowdNodeAdapter());
	}

	private PropertyValueModel buildCrowdNodeAdapter() {
		return new TransformationPropertyValueModel(this.crowdHolder) {
			protected Object transform(Object value) {
				return new CrowdNode((Crowd) value);
			}
		};
	}

	private TreeSelectionModel buildTreeSelectionModel() {
		TreeSelectionModel tsm = new DefaultTreeSelectionModel();
		tsm.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//		tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tsm.addTreeSelectionListener(this.buildTreeSelectionListener());
		return tsm;
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreeCellEditorUITest.this.treeSelectionChanged(e);
			}
		};
	}

	void treeSelectionChanged(TreeSelectionEvent e) {
		Person selection = this.selectedPerson();
		this.selectedPersonHolder.setValue(selection);
		boolean personSelected = (selection != null);
		this.removeAction.setEnabled(personSelected);
		this.renameAction.setEnabled(personSelected);
	}

	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
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
		mainPanel.add(this.buildTreePane(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildTreePane() {
		return new JScrollPane(this.buildTree());
	}

	private JTree buildTree() {
		JTree tree = new JTree(this.treeModel);
		tree.setSelectionModel(this.treeSelectionModel);
		tree.setCellRenderer(new NodeTreeCellRenderer());
		tree.setCellEditor(new NodeTreeCellEditor());
		tree.setEditable(this.isEditable());
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		tree.setDoubleBuffered(true);

		// comment out this line to try variable row height (which doesn't look so good...)
		tree.setRowHeight(26);

		return tree;
	}

	protected boolean isEditable() {
		return true;
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
				TreeCellEditorUITest.this.addPerson();
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
				TreeCellEditorUITest.this.removePerson();
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
				TreeCellEditorUITest.this.renamePerson();
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
				TreeCellEditorUITest.this.addEyeColor();
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
				TreeCellEditorUITest.this.printCrowd();
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
				TreeCellEditorUITest.this.reset();
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


	// ********** renderer/editor caches **********

	void putRenderer(Object key, TreeCellRenderer renderer) {
		this.renderers.put(key, renderer);
	}

	TreeCellRenderer getRenderer(Object key) {
		return (TreeCellRenderer) this.renderers.get(key);
	}

	void putEditor(Object key, TreeCellEditor editor) {
		this.editors.put(key, editor);
	}

	TreeCellEditor getEditor(Object key) {
		return (TreeCellEditor) this.editors.get(key);
	}


	// ********** queries **********

	private Crowd crowd() {
		return (Crowd) this.crowdHolder.getValue();
	}

	private CrowdNode crowdNode() {
		return (CrowdNode) this.treeModel.getRoot();
	}

	private LocalNode selectedNode() {
		if (this.treeSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (LocalNode) this.treeSelectionModel.getSelectionPath().getLastPathComponent();
	}

	private Person selectedPerson() {
		if (this.treeSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return this.selectedNode().getPerson();
	}

	// ********** behavior **********

	private void setSelectedPerson(Person person) {
		this.setSelectedPersonNode(this.crowdNode().personNodeFor(person));
	}

	private void setSelectedPersonNode(PersonNode personNode) {
		this.treeSelectionModel.setSelectionPath(new TreePath(personNode.path()));
	}


// ********** nodes **********

/**
 * interface defining protocol required by all nodes used in
 * the tree
 */
interface LocalNode extends EditingNode {

	/**
	 * return the person associated with the node:
	 * 	- a crowd node will return null
	 * 	- a person node will return its person
	 * 	- an attribute node will return its parent's person
	 */
	Person getPerson();

}


class CrowdNode extends AbstractTreeNodeValueModel implements LocalNode {
	private Crowd crowd;
	private ListValueModel childrenModel;
	private CollectionChangeListener crowdListener;

	// ********** construction/initialization **********

	public CrowdNode(Crowd crowd) {
		super();
		this.crowd = crowd;
		this.childrenModel = this.buildChildrenModel(crowd);
	}
	protected void initialize() {
		super.initialize();
		this.crowdListener = this.buildCrowdListener();
	}
	private CollectionChangeListener buildCrowdListener() {
		return new CollectionChangeListener() {
			public void collectionChanged(CollectionChangeEvent e) {
				CrowdNode.this.crowdChanged();
			}
			public void itemsAdded(CollectionChangeEvent e) {
				CrowdNode.this.crowdChanged();
			}
			public void itemsRemoved(CollectionChangeEvent e) {
				CrowdNode.this.crowdChanged();
			}
		};
	}
	void crowdChanged() {
		this.fireStateChanged();
	}

	// the list should be sorted
	protected ListValueModel buildChildrenModel(Crowd c) {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter(c));
	}
	// the display string (name) of each person node can change
	protected ListValueModel buildDisplayStringAdapter(Crowd c) {
		return new ItemPropertyListValueModelAdapter(this.buildPersonNodeAdapter(c), Displayable.DISPLAY_STRING_PROPERTY);
	}
	// wrap the persons in the list in person nodes
	protected ListValueModel buildPersonNodeAdapter(Crowd c) {
		return new TransformationListValueModelAdapter(this.buildPeopleAdapter(c)) {
			protected Object transformItem(Object item) {
				return CrowdNode.this.buildPersonNode((Person) item);
			}
		};
	}
	protected PersonNode buildPersonNode(Person person) {
		return new PersonNode(this, person);
	}
	// the list of children can change
	protected CollectionValueModel buildPeopleAdapter(Crowd c) {
		return new CollectionAspectAdapter(Crowd.PEOPLE_COLLECTION, c) {
			protected Iterator getValueFromSubject() {
				return ((Crowd) this.subject).people();
			}
			protected int sizeFromSubject() {
				return ((Crowd) this.subject).peopleSize();
			}
		};
	}

	// ********** LocalNode implementation **********

	public Person getPerson() {
		return null;
	}
	public Object getCellValue() {
		return this.crowd;
	}
	public TreeCellRenderer getRenderer() {
		TreeCellRenderer renderer = TreeCellEditorUITest.this.getRenderer(this.getClass());
		if (renderer == null) {
			renderer = this.buildRenderer();
			TreeCellEditorUITest.this.putRenderer(this.getClass(), renderer);
		}
		return renderer;
	}
	private TreeCellRenderer buildRenderer() {
		return new SimpleTreeCellRenderer() {
			protected String buildText(Object value) {
				return "Crowd of " + ((Crowd) value).peopleSize() + " person(s)";
			}
		};
	}
	public TreeCellEditor getEditor() {
		return NullTreeCellEditor.instance();
	}

	// ********** TreeNodeValueModel implementation **********

	public Object getValue() {
		return this.crowd;
	}
	public void setValue(Object value) {
		Object old = this.crowd;
		this.crowd = (Crowd) value;
		this.firePropertyChanged(VALUE, old, value);
	}
	public TreeNodeValueModel getParent() {
		return null;
	}
	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		this.crowd.addCollectionChangeListener(Crowd.PEOPLE_COLLECTION, this.crowdListener);
	}
	protected void disengageValue() {
		this.crowd.removeCollectionChangeListener(Crowd.PEOPLE_COLLECTION, this.crowdListener);
	}

	// ********** queries **********

	public PersonNode personNodeFor(Person person) {
		for (Iterator stream = (Iterator) this.childrenModel.getValue(); stream.hasNext(); ) {
			PersonNode pNode = (PersonNode) stream.next();
			if (pNode.getValue() == person) {
				return pNode;
			}
		}
		return null;
	}

	// ********** standard methods **********

	public String toString() {
		return "Crowd(" + this.crowd.peopleSize() + " people)";
	}

}


class PersonNode extends AbstractTreeNodeValueModel implements LocalNode, Displayable {
	private Person person;
	private CrowdNode crowdNode;		// parent node
	private ListValueModel childrenModel;
	private PropertyChangeListener personListener;

	// ********** construction/initialization **********

	public PersonNode(CrowdNode crowdNode, Person person) {
		super();
		this.crowdNode = crowdNode;
		this.person = person;
		this.childrenModel = this.buildChildrenModel();
	}
	protected void initialize() {
		super.initialize();
		this.personListener = this.buildPersonListener();
	}
	private PropertyChangeListener buildPersonListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PersonNode.this.personChanged(e);
			}
		};
	}

	protected ListValueModel buildChildrenModel() {
		Object[] children = new Object[6];
		children[0] = new BirthDateNode(this);
		children[1] = new GoneWestDateNode(this);
		children[2] = new EyeColorNode(this);
		children[3] = new EvilNode(this);
		children[4] = new RankNode(this);
		children[5] = new AdventureCountNode(this);
		return new SimpleListValueModel(Arrays.asList(children));
	}

	// ********** LocalNode implementation **********

	public Person getPerson() {
		return this.person;
	}
	public Object getCellValue() {
		return this.person;
	}
	public TreeCellRenderer getRenderer() {
		TreeCellRenderer renderer = TreeCellEditorUITest.this.getRenderer(this.getClass());
		if (renderer == null) {
			renderer = this.buildRenderer();
			TreeCellEditorUITest.this.putRenderer(this.getClass(), renderer);
		}
		return renderer;
	}
	private TreeCellRenderer buildRenderer() {
		return new SimpleTreeCellRenderer() {
			protected String buildText(Object value) {
				return ((Person) value).getName();
			}
		};
	}
	public TreeCellEditor getEditor() {
		return NullTreeCellEditor.instance();
	}

	// ********** TreeNodeValueModel implementation **********

	public Object getValue() {
		return this.person;
	}
	public void setValue(Object value) {
		Object old = this.person;
		this.person = (Person) value;
		this.firePropertyChanged(VALUE, old, value);
	}
	public TreeNodeValueModel getParent() {
		return this.crowdNode;
	}
	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}

	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		this.person.addPropertyChangeListener(Person.NAME_PROPERTY, this.personListener);
	}
	protected void disengageValue() {
		this.person.removePropertyChangeListener(Person.NAME_PROPERTY, this.personListener);
	}

	// ********** Displayable implementation **********

	public String displayString() {
		return this.person.getName();
	}

	public Icon icon() {
		return null;
	}

	// ********** behavior **********

	protected void personChanged(PropertyChangeEvent e) {
		// we need to notify listeners that our "internal state" has changed
		this.fireStateChanged();
		// our display string stays in synch with the model's name
		this.firePropertyChanged(DISPLAY_STRING_PROPERTY, e.getOldValue(), e.getNewValue());
	}

	// ********** standard methods **********

	// use the standard Displayable comparator
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}

	public String toString() {
		return "PersonNode(" + this.person + ")";
	}

}


abstract class PersonAttributeNode extends AbstractTreeNodeValueModel implements LocalNode {
	private PropertyValueModel attributeAdapter;
	private PersonNode personNode;		// parent node
	private PropertyChangeListener attributeListener;

	// ********** construction/initialization **********

	public PersonAttributeNode(PersonNode personNode) {
		super();
		this.initialize(personNode);
	}
	protected void initialize() {
		super.initialize();
		this.attributeListener = this.buildAttributeListener();
	}
	protected PropertyChangeListener buildAttributeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				PersonAttributeNode.this.attributeChanged(e);
			}
		};
	}
	protected void initialize(PersonNode node) {
		this.personNode = node;
		this.attributeAdapter = this.buildAttributeAdapter();
	}

	protected abstract PropertyValueModel buildAttributeAdapter();

	// ********** LocalNode implementation **********

	public Person getPerson() {
		return this.personNode.getPerson();
	}
	public Object getCellValue() {
		return this.getValue();
	}
	public TreeCellRenderer getRenderer() {
		TreeCellRenderer renderer = TreeCellEditorUITest.this.getRenderer(this.getClass());
		if (renderer == null) {
			renderer = this.buildRenderer();
			TreeCellEditorUITest.this.putRenderer(this.getClass(), renderer);
		}
		return renderer;
	}
	protected abstract TreeCellRenderer buildRenderer();
	public TreeCellEditor getEditor() {
		TreeCellEditor editor = TreeCellEditorUITest.this.getEditor(this.getClass());
		if (editor == null) {
			editor = this.buildEditor();
//			editor.addCellEditorListener(TreeCellEditorUITest.this.getEditor());
			TreeCellEditorUITest.this.putEditor(this.getClass(), editor);
		}
		return editor;
	}
	protected TreeCellEditor buildEditor() {
		return NullTreeCellEditor.instance();
	}

	// ********** TreeNodeValueModel implementation **********

	public Object getValue() {
		return this.attributeAdapter.getValue();
	}
	public void setValue(Object value) {
		this.attributeAdapter.setValue(value);
	}
	public TreeNodeValueModel getParent() {
		return this.personNode;
	}
	public ListValueModel getChildrenModel() {
		return NullListValueModel.instance();
	}

	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		this.attributeAdapter.addPropertyChangeListener(ValueModel.VALUE, this.attributeListener);
	}
	protected void disengageValue() {
		this.attributeAdapter.removePropertyChangeListener(ValueModel.VALUE, this.attributeListener);
	}

	// ********** behavior **********

	protected void attributeChanged(PropertyChangeEvent e) {
		// we need to notify listeners that our "value" has changed
		this.firePropertyChanged(VALUE, e.getOldValue(), e.getNewValue());
	}

}


class BirthDateNode extends PersonAttributeNode {

	// ********** construction/initialization **********

	public BirthDateNode(PersonNode personNode) {
		super(personNode);
	}
	protected PropertyValueModel buildAttributeAdapter() {
		return new PropertyAspectAdapter(Person.BIRTH_DATE_PROPERTY, this.getPerson()) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getBirthDate();
			}
			protected void setValueOnSubject(Object value) {
				try {
					Date date = DateFormat.getDateInstance(DateFormat.SHORT).parse((String) value);
					((Person) this.subject).setBirthDate(date);
				} catch (ParseException ex) {
					ex.printStackTrace();
					// simply don't set the date if we get an exception
				}
			}
		};
	}

	// ********** LocalNode implementation **********

	protected TreeCellRenderer buildRenderer() {
		return new TextFieldTreeCellRenderer("Birth:");
	}
	public TreeCellEditor buildEditor() {
		// build a new renderer - do not re-use the original
		return new TextFieldTreeCellEditor((TextFieldTreeCellRenderer) this.buildRenderer());
	}

	// ********** standard methods **********

	public String toString() {
		return "BirthDateNode(" + this.getValue() + ")";
	}

}


class GoneWestDateNode extends PersonAttributeNode {

	// ********** construction/initialization **********

	public GoneWestDateNode(PersonNode personNode) {
		super(personNode);
	}
	protected PropertyValueModel buildAttributeAdapter() {
		return new PropertyAspectAdapter(Person.GONE_WEST_DATE_PROPERTY, this.getPerson()) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getGoneWestDate();
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setGoneWestDate((Date) value);
			}
		};
	}

	// ********** LocalNode implementation **********

	protected TreeCellRenderer buildRenderer() {
		return new SpinnerTreeCellRenderer("Gone West:", this.buildRendererModel());
	}
	protected SpinnerModel buildRendererModel() {
		return new DateSpinnerModelAdapter(new SimplePropertyValueModel());
	}
	public TreeCellEditor buildEditor() {
		// build a new renderer - do not re-use the original
		return new SpinnerTreeCellEditor((SpinnerTreeCellRenderer) this.buildRenderer());
	}

	// ********** standard methods **********

	public String toString() {
		return "GoneWestDateNode(" + this.getValue() + ")";
	}

}


class EyeColorNode extends PersonAttributeNode {

	// ********** construction/initialization **********

	public EyeColorNode(PersonNode personNode) {
		super(personNode);
	}
	protected PropertyValueModel buildAttributeAdapter() {
		return new PropertyAspectAdapter(Person.EYE_COLOR_PROPERTY, this.getPerson()) {
			protected Object getValueFromSubject() {
				return ((Person) this.subject).getEyeColor();
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setEyeColor((String) value);
			}
		};
	}

	// ********** LocalNode implementation **********

	protected TreeCellRenderer buildRenderer() {
		return new ComboBoxTreeCellRenderer(this.buildComboBoxModel(), "Eyes:");
	}
	private ComboBoxModel buildComboBoxModel() {
		return new ComboBoxModelAdapter(TreeCellEditorUITest.this.eyeColorListHolder, new SimplePropertyValueModel());
	}
	public TreeCellEditor buildEditor() {
		// build a new renderer - do not re-use the original
		return new ComboBoxTreeCellEditor((ComboBoxTreeCellRenderer) this.buildRenderer());
	}

	// ********** standard methods **********

	public String toString() {
		return "EyeColorNode(" + this.getValue() + ")";
	}

}


class EvilNode extends PersonAttributeNode {

	// ********** construction/initialization **********

	public EvilNode(PersonNode personNode) {
		super(personNode);
	}
	protected PropertyValueModel buildAttributeAdapter() {
		return new PropertyAspectAdapter(Person.EVIL_PROPERTY, this.getPerson()) {
			protected Object getValueFromSubject() {
				return Boolean.valueOf(((Person) this.subject).isEvil());
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setEvil(((Boolean) value).booleanValue());
			}
		};
	}

	// ********** LocalNode implementation **********

	protected TreeCellRenderer buildRenderer() {
		return new CheckBoxTreeCellRenderer("Evil");
	}
	public TreeCellEditor buildEditor() {
		// build a new renderer - do not re-use the original
		return new CheckBoxTreeCellEditor((CheckBoxTreeCellRenderer) this.buildRenderer());
	}

	// ********** standard methods **********

	public String toString() {
		return "EvilNode(" + this.getValue() + ")";
	}

}


class RankNode extends PersonAttributeNode {

	// ********** construction/initialization **********

	public RankNode(PersonNode personNode) {
		super(personNode);
	}
	protected PropertyValueModel buildAttributeAdapter() {
		return new PropertyAspectAdapter(Person.RANK_PROPERTY, this.getPerson()) {
			protected Object getValueFromSubject() {
				return new Integer(((Person) this.subject).getRank());
			}
			protected void setValueOnSubject(Object value) {
				try {
					int rank = Integer.parseInt((String) value);
					((Person) this.subject).setRank(rank);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					// simply don't set the rank if we get an exception
				}
			}
		};
	}

	// ********** LocalNode implementation **********

	protected TreeCellRenderer buildRenderer() {
		return new TextFieldTreeCellRenderer("Rank:");
	}
	public TreeCellEditor buildEditor() {
		// build a new renderer - do not re-use the original
		return new TextFieldTreeCellEditor((TextFieldTreeCellRenderer) this.buildRenderer());
	}

	// ********** standard methods **********

	public String toString() {
		return "RankNode(" + this.getValue() + ")";
	}

}


class AdventureCountNode extends PersonAttributeNode {

	// ********** construction/initialization **********

	public AdventureCountNode(PersonNode personNode) {
		super(personNode);
	}
	protected PropertyValueModel buildAttributeAdapter() {
		return new PropertyAspectAdapter(Person.ADVENTURE_COUNT_PROPERTY, this.getPerson()) {
			protected Object getValueFromSubject() {
				return new Integer(((Person) this.subject).getAdventureCount());
			}
			protected void setValueOnSubject(Object value) {
				((Person) this.subject).setAdventureCount(((Integer) value).intValue());
			}
		};
	}

	// ********** LocalNode implementation **********

	protected TreeCellRenderer buildRenderer() {
		return new SpinnerTreeCellRenderer("Adventures:");
	}
	public TreeCellEditor buildEditor() {
		// build a new renderer - do not re-use the original
		return new SpinnerTreeCellEditor((SpinnerTreeCellRenderer) this.buildRenderer());
	}

	// ********** standard methods **********

	public String toString() {
		return "AdventureCountNode(" + this.getValue() + ")";
	}

}

}
