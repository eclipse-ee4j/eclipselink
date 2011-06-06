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
package org.eclipse.persistence.tools.workbench.test.framework.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.NewNameDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsIconResourceFileNameMap;
import org.eclipse.persistence.tools.workbench.framework.uitools.UIToolsResourceBundle;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ObjectListSelectionModel;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


/**
 * play around with the New Name Dialog
 */
public class NewNameDialogUITest {

	private CollectionValueModel testModels;
	private ListModel listModel;
	private ObjectListSelectionModel objectListSelectionModel;
	private PropertyValueModel originalNameIsLegalHolder;
	private PropertyValueModel comparisonIsCaseSensitiveHolder;
	private PropertyValueModel zedIsAllowedHolder;
	private WorkbenchContext context;

	public static void main(String[] args) throws Exception {
		new NewNameDialogUITest().exec(args);
	}

	private NewNameDialogUITest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		this.testModels = new SimpleCollectionValueModel(new ArrayList());
		this.listModel = new ListModelAdapter(new SortedListValueModelAdapter(new ItemPropertyListValueModelAdapter(this.testModels, TestModel.NAME_PROPERTY)));
		this.objectListSelectionModel = new ObjectListSelectionModel(this.listModel);
		this.originalNameIsLegalHolder = new SimplePropertyValueModel(Boolean.FALSE);
		this.comparisonIsCaseSensitiveHolder = new SimplePropertyValueModel(Boolean.FALSE);
		this.zedIsAllowedHolder = new SimplePropertyValueModel(Boolean.TRUE);
		this.context = this.buildWorkbenchContext();
		this.openWindow();
	}

	private WorkbenchContext buildWorkbenchContext() {
		TestWorkbenchContext testAC = new TestWorkbenchContext(UIToolsResourceBundle.class, UIToolsIconResourceFileNameMap.class.getName());
		testAC.setCurrentWindow(this.buildWindow());
		return testAC;
	}

	private Window buildWindow() {
		return new JFrame(this.getClass().getName());
	}

	private void openWindow() {
		JFrame window = (JFrame) this.context.getCurrentWindow();
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setSize(300, 400);
		window.setLocation(200, 300);
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
		mainPanel.add(this.buildListBox(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildListBox() {
		JList listBox = new JList();
		listBox.setModel(this.listModel);
		listBox.setSelectionModel(this.objectListSelectionModel);
		listBox.setDoubleBuffered(true);
		listBox.setCellRenderer(this.buildListCellRenderer());
		return new JScrollPane(listBox);
	}

	private ListCellRenderer buildListCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return ((TestModel) value).getName();
			}
		};
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(0, 1));	// vertical
		controlPanel.add(this.buildButtonPanel());
		controlPanel.add(this.buildOriginalNameIsLegalCheckBox());
		controlPanel.add(this.buildCaseSensitiveCheckBox());
		controlPanel.add(this.buildAllowZedCheckBox());
		return controlPanel;
	}

	private Component buildButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 0));	// horizontal
		buttonPanel.add(this.buildAddButton());
		buttonPanel.add(this.buildDeleteButton());
		buttonPanel.add(this.buildRenameButton());
		return buttonPanel;
	}


	// ********** buttons **********

	// ********** add **********
	private JButton buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		Action action = new AbstractAction("add") {
			public void actionPerformed(ActionEvent event) {
				NewNameDialogUITest.this.add();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void add() {
		NewNameDialog dialog = this.buildNewNameDialog("add");
		dialog.show();
		if (dialog.wasCanceled()) {
			return;
		}
		TestModel newModel = new TestModel(dialog.getNewName());
		this.testModels.addItem(newModel);
		this.objectListSelectionModel.setSelectedValue(newModel);
	}

	// ********** delete **********
	private JButton buildDeleteButton() {
		return new JButton(this.buildDeleteAction());
	}

	private Action buildDeleteAction() {
		Action action = new AbstractAction("delete") {
			public void actionPerformed(ActionEvent event) {
				NewNameDialogUITest.this.delete();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void delete() {
		if (this.objectListSelectionModel.isSelectionEmpty()) {
			return;
		}
		this.testModels.removeItems(Arrays.asList(this.objectListSelectionModel.getSelectedValues()));
	}

	// ********** rename **********
	private JButton buildRenameButton() {
		return new JButton(this.buildRenameAction());
	}

	private Action buildRenameAction() {
		Action action = new AbstractAction("rename") {
			public void actionPerformed(ActionEvent event) {
				NewNameDialogUITest.this.rename();
			}
		};
		action.setEnabled(true);
		return action;
	}

	private void rename() {
		if (this.objectListSelectionModel.isSelectionEmpty()) {
			return;
		}
		if (this.objectListSelectionModel.getMinSelectionIndex() != this.objectListSelectionModel.getMaxSelectionIndex()) {
			// more than one entry is selected
			return;
		}
		TestModel tm = (TestModel) this.objectListSelectionModel.getSelectedValue();
		NewNameDialog dialog = this.buildNewNameDialog("rename", tm.getName());
		dialog.show();
		if (dialog.wasCanceled()) {
			return;
		}
		tm.setName(dialog.getNewName());
//		objectListSelectionModel.setSelectedValue(tm);
	}


	// ********** check boxes **********

	private JCheckBox buildOriginalNameIsLegalCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("original name is legal");
		checkBox.setModel(new CheckBoxModelAdapter(this.originalNameIsLegalHolder));
		return checkBox;
	}

	private JCheckBox buildCaseSensitiveCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("case sensitive");
		checkBox.setModel(new CheckBoxModelAdapter(this.comparisonIsCaseSensitiveHolder));
		return checkBox;
	}

	private JCheckBox buildAllowZedCheckBox() {
		JCheckBox checkBox = new JCheckBox();
		checkBox.setText("allow Z");
		checkBox.setModel(new CheckBoxModelAdapter(this.zedIsAllowedHolder));
		return checkBox;
	}


	// ********** dialog creation **********

	private NewNameDialog buildNewNameDialog(String title) {
		return this.buildNewNameDialog(title, null);
	}

	private NewNameDialog buildNewNameDialog(String title, String originalName) {
		NewNameDialog.Builder builder = new NewNameDialog.Builder();
		builder.setComparisonIsCaseSensitive(this.comparisonIsCaseSensitive());
		builder.setDocumentFactory(this.buildDocumentFactory());
		builder.setExistingNames(this.existingNames());
		builder.setIllegalNames(this.illegalNames());
		builder.setOriginalName(originalName);
		builder.setOriginalNameIsLegal(this.originalNameIsLegal());
		builder.setTextFieldDescription("Model name");
		builder.setTitle(title);
		return builder.buildDialog(this.context);
	}

	private boolean comparisonIsCaseSensitive() {
		return ((Boolean) this.comparisonIsCaseSensitiveHolder.getValue()).booleanValue();
	}

	private NewNameDialog.DocumentFactory buildDocumentFactory() {
		if (this.zedIsAllowed()) {
			return this.buildDefaultDocumentFactory();
		}
		return this.buildNoZedDocumentFactory();
	}

	private NewNameDialog.DocumentFactory buildDefaultDocumentFactory() {
		return new NewNameDialog.DocumentFactory() {
			public Document buildDocument() {
				return new PlainDocument();
			}
		};
	}

	private Iterator existingNames() {
		return new TransformationIterator((Iterator) this.testModels.getValue()) {
			protected Object transform(Object next) {
				return ((TestModel) next).getName();
			}
		};
	}

	private Iterator illegalNames() {
		Collection illegalNames = new ArrayList();
		illegalNames.add("rats");
		illegalNames.add("darn");
		illegalNames.add("dang");
		illegalNames.add("jeez");
		return illegalNames.iterator();
	}

	private NewNameDialog.DocumentFactory buildNoZedDocumentFactory() {
		return new NewNameDialog.DocumentFactory() {
			public Document buildDocument() {
				return new NoZedDocument();
			}
		};
	}

	private boolean originalNameIsLegal() {
		return ((Boolean) this.originalNameIsLegalHolder.getValue()).booleanValue();
	}

	private boolean zedIsAllowed() {
		return ((Boolean) this.zedIsAllowedHolder.getValue()).booleanValue();
	}


// ********** inner classes **********

private class TestModel extends AbstractModel implements Comparable {
	private String name;
		public static final String NAME_PROPERTY = "name";

	public TestModel(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}
	public int compareTo(Object o) {
		return this.name.compareToIgnoreCase(((TestModel) o).name);
	}
	public String toString() {
		return "TestModel(" + this.getName() + ")";
	}
}


private static class NoZedDocument extends PlainDocument {
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		if (str != null) {
			str = StringTools.removeAllOccurrences(str, 'z');
			str = StringTools.removeAllOccurrences(str, 'Z');
		}
		super.insertString(offset, str, a);
	}
}

}
