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
package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleListCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.string.SimpleStringMatcher;
import org.eclipse.persistence.tools.workbench.utility.string.StringConverter;
import org.eclipse.persistence.tools.workbench.utility.string.StringMatcher;


/**
 * This panel presents an entry field and a list box of choices that
 * allows the user to filter the entries in the list box by entering
 * a pattern in the entry field.
 * 
 * By default, two wildcards are allowed in the pattern:
 * 	'*' will match any set of zero or more characters
 * 	'?' will match any single character
 * 
 * The panel consists of 4 components that can be customized:
 * 	- 1 text field
 * 	- 1 list box
 * 	- 2 labels, one for each of the above
 * 
 * Other aspects of the panel's behavior can be changed:
 * 	- the string converter determines how the objects in the
 * 		list are converted to strings and compared to the pattern
 * 		entered in the text field; by default the converter simply
 * 		uses the result of the object's #toString() method
 * 		(if you replace the string converter, you will probably
 * 		want to replace the list box's cell renderer also)
 * 	- the string matcher can also be changed if you would
 * 		like different pattern matching behavior than that
 * 		described above
 * 	- you can specify the maximum size of the list - this may
 * 		force the user to enter a pattern restrictive enough
 * 		to result in a list smaller than the maximum size; the
 * 		default is -1, which disables the restriction
 * 
 * This panel is not a typical panel, in the sense that it does not share
 * its model with clients via value models. Instead, this panel's model
 * is set and queried directly because it is designed to be used in a
 * dialog that directs the user's behavior (as opposed to a "normal"
 * window).
 */
public class FilteringListPanel extends JPanel {

	/**
	 * The complete list of available choices
	 * (as opposed to the partial list held by the list box).
	 */
	private Object[] completeList;

	/**
	 * An adapter used to convert the objects in the list
	 * to strings so they can be run through the matcher
	 * and displayed in the text field.
	 */
	StringConverter stringConverter;

	/** The text field. */
	private JTextField textField;
	private JLabel textFieldLabel;
	private DocumentListener textFieldListener;

	/** The list box. */
	private JList listBox;
	private JLabel listBoxLabel;

	/** The maximum number of entries displayed in the list box. */
	private int maxListSize;

	/**
	 * The matcher used to filter the list against
	 * the pattern entered in the text field. By default,
	 * this allows the two wildcard characters described in
	 * the class comment.
	 */
	private StringMatcher stringMatcher;

	/**
	 * Performance tweak: We use this buffer instead of
	 * a temporary variable during filtering so we don't have
	 * to keep re-allocating it.
	 */
	private Object[] buffer;

	private static final Border TEXT_FIELD_LABEL_BORDER = BorderFactory.createEmptyBorder(0, 0, 5, 0);
	private static final Border LIST_BOX_LABEL_BORDER = BorderFactory.createEmptyBorder(5, 0, 5, 0);


	// ********** constructors **********

	/**
	 * Construct a FilteringListPanel with the specified list of choices
	 * and initial selection. Use the default string converter to convert the
	 * choices and selection to strings (which simply calls #toString() on
	 * the objects).
	 */
	public FilteringListPanel(Object[] completeList, Object initialSelection) {
		this(completeList, initialSelection, StringConverter.DEFAULT_INSTANCE);
	}

	/**
	 * Construct a FilteringListPanel with the specified list of choices
	 * and initial selection. Use the specified string converter to convert the
	 * choices and selection to strings.
	 */
	public FilteringListPanel(Object[] completeList, Object initialSelection, StringConverter stringConverter) {
		super(new BorderLayout());
		this.completeList = completeList;
		this.stringConverter = stringConverter;
		this.initialize(initialSelection);
	}


	// ********** initialization **********

	private void initialize(Object initialSelection) {
		this.maxListSize = this.defaultMaxListSize();
		this.buffer = new Object[this.max()];

		this.textFieldListener = this.buildTextFieldListener();

		this.stringMatcher = this.buildStringMatcher();

		this.initializeLayout(initialSelection);
	}

	/**
	 * Return the current max number of entries allowed in the list box.
	 */
	private int max() {
		if (this.maxListSize == -1) {
			return this.completeList.length;
		}
		return Math.min(this.maxListSize, this.completeList.length);
	}

	/**
	 * Build a listener that will listen to changes in the text field
	 * and filter the list appropriately.
	 */
	private DocumentListener buildTextFieldListener() {
		return new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				FilteringListPanel.this.filterList();
			}
			public void changedUpdate(DocumentEvent e) {
				FilteringListPanel.this.filterList();
			}
			public void removeUpdate(DocumentEvent e) {
				FilteringListPanel.this.filterList();
			}
			public String toString() {
				return "text field listener";
			}
		};
	}

	private int defaultMaxListSize() {
		return -1;
	}

	private StringMatcher buildStringMatcher() {
		return new SimpleStringMatcher();
	}

	private void initializeLayout(Object initialSelection) {
		// text field
		JPanel textFieldPanel = new JPanel(new BorderLayout());
		this.textFieldLabel = new JLabel();
		this.textFieldLabel.setBorder(TEXT_FIELD_LABEL_BORDER);
		textFieldPanel.add(this.textFieldLabel, BorderLayout.NORTH);

		this.textField = new JTextField();
		this.textField.getDocument().addDocumentListener(this.textFieldListener);
		this.textFieldLabel.setLabelFor(this.textField);
		textFieldPanel.add(this.textField, BorderLayout.CENTER);

		this.add(textFieldPanel, BorderLayout.NORTH);

		// list box
		JPanel listBoxPanel = new JPanel(new BorderLayout());
		this.listBoxLabel = new JLabel();
		this.listBoxLabel.setBorder(LIST_BOX_LABEL_BORDER);
		listBoxPanel.add(this.listBoxLabel, BorderLayout.NORTH);

		this.listBox = new JList();
		this.listBox.setDoubleBuffered(true);
		this.listBox.setModel(this.buildPartialArrayListModel(this.completeList, this.max()));
		this.listBox.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// performance tweak(?)
		this.listBox.setPrototypeCellValue(this.prototypeCellValue());
		this.listBox.setPrototypeCellValue(null);
		this.listBox.setCellRenderer(this.buildDefaultCellRenderer());
		this.listBoxLabel.setLabelFor(this.listBox);
		// bug 2777802 - scroll bars shouldn't be on the tab sequence
		JScrollPane listBoxScrollPane = new JScrollPane(this.listBox);
		listBoxScrollPane.getHorizontalScrollBar().setFocusable(false);
		listBoxScrollPane.getVerticalScrollBar().setFocusable(false);
		listBoxPanel.add(listBoxScrollPane, BorderLayout.CENTER);

		// initialize the widgets
		this.listBox.setSelectedValue(initialSelection, true);
		this.textField.select(0, this.textField.getText().length());

		this.add(listBoxPanel, BorderLayout.CENTER);
	}


	// ********** public API **********

	public Object getSelection() {
		return this.listBox.getSelectedValue();
	}

	public void setSelection(Object selection) {
		this.listBox.setSelectedValue(selection, true);
	}

	public Object[] getCompleteList() {
		return this.completeList;
	}

	/**
	 * rebuild the filtering buffer and re-apply the filter
	 * to the new list
	 */
	public void setCompleteList(Object[] completeList) {
		this.completeList = completeList;
		if (this.buffer.length < this.max()) {
			// the buffer will never shrink - might want to re-consider... -bjv
			this.buffer = new Object[this.max()];
		}
		this.filterList();
	}

	public int getMaxListSize() {
		return this.maxListSize;
	}

	public void setMaxListSize(int maxListSize) {
		this.maxListSize = maxListSize;
		if (this.buffer.length < this.max()) {
			// the buffer will never shrink - might want to re-consider... -bjv
			this.buffer = new Object[this.max()];
		}
		this.filterList();
	}

	public StringConverter getStringConverter() {
		return this.stringConverter;
	}

	/**
	 * apply the new filter to the list
	 */
	public void setStringConverter(StringConverter stringConverter) {
		this.stringConverter = stringConverter;
		this.filterList();
	}

	/**
	 * allow client code to access the text field
	 * (so we can set the focus)
	 */
	public JTextField getTextField() {
		return this.textField;
	}

	/**
	 * allow client code to access the text field label
	 */
	public JLabel getTextFieldLabel() {
		return this.textFieldLabel;
	}

	/**
	 * convenience method
	 */
	public void setTextFieldLabelText(String text) {
		this.textFieldLabel.setText(text);
	}

	/**
	 * allow client code to access the list box
	 * (so we can add mouse listeners for double-clicking)
	 */
	public JList getListBox() {
		return this.listBox;
	}

	/**
	 * convenience method
	 */
	public void setListBoxCellRenderer(ListCellRenderer renderer) {
		this.listBox.setCellRenderer(renderer);
	}

	/**
	 * allow client code to access the list box label
	 */
	public JLabel getListBoxLabel() {
		return this.listBoxLabel;
	}

	/**
	 * convenience method
	 */
	public void setListBoxLabelText(String text) {
		this.listBoxLabel.setText(text);
	}

	/**
	 * convenience method
	 */
	public void setComponentsFont(Font font) {
		this.textFieldLabel.setFont(font);
		this.textField.setFont(font);
		this.listBoxLabel.setFont(font);
		this.listBox.setFont(font);
	}

	public StringMatcher getStringMatcher() {
		return this.stringMatcher;
	}

	/**
	 * re-apply the filter to the list
	 */
	public void setStringMatcher(StringMatcher stringMatcher) {
		this.stringMatcher = stringMatcher;
		this.filterList();
	}


	// ********** internal methods **********

	/**
	 * Allow subclasses to disable performance tweak
	 * by returning null here.
	 */
	protected String prototypeCellValue() {
		return "==========> A_STRING_THAT_IS_DEFINITELY_LONGER_THAN_EVERY_STRING_IN_THE_LIST <==========";
	}

	/**
	 * By default, use the string converter to build the text
	 * used by the list box's cell renderer.
	 */
	protected ListCellRenderer buildDefaultCellRenderer() {
		return new SimpleListCellRenderer() {
			protected String buildText(Object value) {
				return FilteringListPanel.this.stringConverter.convertToString(value);
			}
		};
	}

	/**
	 * Something has changed that requires us to filter the list.
	 * 
	 * This method is synchronized because a fast typist can
	 * generate events quicker than we can filter the list. (? -bjv)
	 */
	synchronized void filterList() {
		// temporarily stop listening to the list box selection, since we will
		// be changing the selection during the filtering and don't want
		// that to affect the text field
		this.filterList(this.textField.getText());
	}

	/**
	 * Filter the contents of the list box to match the
	 * specified pattern.
	 */
	private void filterList(String pattern) {
		if (pattern.length() == 0) {
			this.listBox.setModel(this.buildPartialArrayListModel(this.completeList, this.max()));
		} else {
			this.stringMatcher.setPatternString(pattern);
			int j = 0;
			int len = this.completeList.length;
			int max = this.max();
			for (int i = 0; i < len; i++) {
				if (this.stringMatcher.matches(this.stringConverter.convertToString(this.completeList[i]))) {
					this.buffer[j++] = this.completeList[i];
				}
				if (j == max) {
					break;
				}
			}
			this.listBox.setModel(this.buildPartialArrayListModel(this.buffer, j));
		}

		// after filtering the list, determine the appropriate selection
		if (this.listBox.getModel().getSize() == 0) {
			this.listBox.getSelectionModel().clearSelection();
		} else {
			this.listBox.getSelectionModel().setAnchorSelectionIndex(0);
			this.listBox.getSelectionModel().setLeadSelectionIndex(0);
			this.listBox.ensureIndexIsVisible(0);
		}
	}

	/**
	 * Build a list model that wraps only a portion of the specified array.
	 * The model will include the array entries from 0 to (size - 1).
	 */
	private ListModel buildPartialArrayListModel(final Object[] array, final int size) {
		return new AbstractListModel() {
			public int getSize() {
				return size;
			}
			public Object getElementAt(int index) {
				return array[index];
			}
		};
	}
}
