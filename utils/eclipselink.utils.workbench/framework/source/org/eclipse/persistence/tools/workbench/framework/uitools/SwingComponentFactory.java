/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.tree.TreeModel;

import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.LabelArea;
import org.eclipse.persistence.tools.workbench.uitools.app.NullPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.ComboBoxTableCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.chooser.ListChooser;
import org.eclipse.persistence.tools.workbench.uitools.swing.CachingComboBoxModel;
import org.eclipse.persistence.tools.workbench.uitools.swing.Combo;
import org.eclipse.persistence.tools.workbench.uitools.swing.ComboBoxEditorAdapter;
import org.eclipse.persistence.tools.workbench.uitools.swing.ComboBoxEditorWithDefault;
import org.eclipse.persistence.tools.workbench.uitools.swing.ComboBoxModelWithDefaultHandler;
import org.eclipse.persistence.tools.workbench.uitools.swing.EmptyIcon;
import org.eclipse.persistence.tools.workbench.uitools.swing.ExpandablePane;
import org.eclipse.persistence.tools.workbench.uitools.swing.SpinnerWithDefaultHandler;
import org.eclipse.persistence.tools.workbench.uitools.swing.TriStateCheckBox;
import org.eclipse.persistence.tools.workbench.uitools.swing.TriStateCheckBox.TriStateButtonModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.string.BidiStringConverter;


public class SwingComponentFactory
{
	private static DoubleClickMouseHandler doubleClickMouseHandler = new DoubleClickMouseHandler();
	private static Border STANDARD_EMPTY_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

	/**
	 * The key to be used when a component needs to be linked to an accessible
	 * component so accessibility can use the name of that accessible component.
	 * This constant is protected in <code>JComponent</code> and can't be
	 * referenced.
	 */
	public static final String LABELED_BY_KEY = "labeledBy";

	/**
	 * Adds the given listener in order to be notified when a double click has
	 * been performed on the given component.
	 */
	public static void addDoubleClickMouseListener(Component component,
	                                               DoubleClickMouseListener listener)
	{
		MouseListener[] listeners = component.getListeners(MouseListener.class);

		if (!CollectionTools.contains(listeners, doubleClickMouseHandler))
		{
			component.addMouseListener(doubleClickMouseHandler);
		}

		doubleClickMouseHandler.addMouseListener(component, listener);
	}

	/**
	 * Creates a new <code>JButton</code> and sets the text, mnemonic and
	 * mnemonic index using the given resource key.
	 */
	public static JButton buildButton(String key, ResourceRepository resourceRepository)
	{
		JButton button = new JButton();
		button.setName(key);
		setTextFor(button, key, resourceRepository);
		return button;
	}
	
	/**
	 * Creates a new <code>JButton</code> and sets the text, mnemonic and
	 * mnemonic index using the given resource key.
	 * It also uses the given labeler to augment the accessible context of the 
	 * button.
	 */
	public static final JButton buildBrowseButton(
		String key, ResourceRepository resourceRepository, Accessible labeler)
	{
		JButton button = new BrowseButton(labeler);
		button.setName(key);
		setTextFor(button, key, resourceRepository);
		return button;
	}
	
	
	
	/**
	 * Creates a new <code>JCheckBox</code> and sets the text, mnemonic, and
	 * mnemonic index using the given resource key.
	 * Must pass in a buttonModel because of a bug in Swing.
	 * The mnemonic will only work properly if set after the model is set.
	 */
	public static JCheckBox buildCheckBox(String key, ButtonModel buttonModel, ResourceRepository resourceRepository)
	{
		JCheckBox checkBox = new JCheckBox();
		checkBox.setModel(buttonModel);
		checkBox.setName(key);
		setTextFor(checkBox, key, resourceRepository);
		return checkBox;
	}

	private static PropertyValueModel buildCommentAdapter(ValueModel nodeModelHolder) {
		return new PropertyAspectAdapter(nodeModelHolder, AbstractNodeModel.COMMENT_PROPERTY) {
			@Override
			protected Object getValueFromSubject() {
				return ((AbstractNodeModel) this.subject).getComment();
			}
			@Override
			protected void setValueOnSubject(Object value) {
				((AbstractNodeModel) this.subject).setComment((String) value);
			}
		};
	}

	private static Document buildCommentDocument(ValueModel nodeModelHolder) {
		return new DocumentAdapter(buildCommentAdapter(nodeModelHolder));
	}

	/**
	 * Build a panel for editing the node model's comment.
	 */
	public static JComponent buildCommentPanel(ValueModel nodeModelHolder, ResourceRepository resourceRepository) {
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel panel = new JPanel(new GridBagLayout());

		// label
		JLabel label = buildLabel("COMMENT_LABEL", resourceRepository);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.LINE_START;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(label, constraints);

		// text area
		JTextArea textArea = new JTextArea(buildCommentDocument(nodeModelHolder));
		textArea.setFont(label.getFont());

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 0, 0, 0);

		JScrollPane scrollPane = new JScrollPane(textArea);
		panel.add(scrollPane, constraints);

		label.setLabelFor(textArea);

		// Have a height that is equivalent of 2 lines
		int height = textArea.getFontMetrics(textArea.getFont()).getHeight();
		Insets borderInsets = textArea.getBorder().getBorderInsets(textArea);
		height = 2*height + borderInsets.top + borderInsets.bottom;

		Dimension size = new Dimension(10, height);
		scrollPane.getViewport().setPreferredSize(size);
		scrollPane.getViewport().setMinimumSize(size);
		scrollPane.getViewport().setMaximumSize(size);

		return panel;
	}

	/**
	 * Creates a pane where there is a multi-line label and a check box below it.
	 *
	 * @param message - The displayed localized message 
	 * @param resultHolder - The boolean holder that holds the result of the
	 * 		Do Not Show Me Again check box 
	 * @param resourceRepository - From which resource strings are retrieved
	 * @return A pane with its widgets
	 */
	public static JComponent buildDoNotAskAgainPanel(
		String message, PropertyValueModel resultHolder, ResourceRepository resourceRepository
	) {
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel container = new JPanel(new GridBagLayout());
		
		// Message area
		LabelArea label = new LabelArea(message);
		label.setScrollable(true);
		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(0, 0, 0, 0);
		container.add(label, constraints);
		
		// Don't ask me again check box
		JCheckBox dontAskMeAgainCheckBox = 
			buildCheckBox(
				"DONT_SHOW_THIS_AGAIN_CHECK_BOX",
				new CheckBoxModelAdapter(resultHolder),
				resourceRepository
			);
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.LINE_START;
		constraints.insets		= new Insets(20, 0, 0, 0);
		container.add(dontAskMeAgainCheckBox, constraints);

		return container;
	}


	/**
	 * Creates a new <code>JLabel</code> and sets at the text,
	 * mnemonic and mnemonic index using the given resource key.
	 */
	public static JLabel buildLabel(String key, ResourceRepository resourceRepository)
	{
		JLabel label = new JLabel();
		label.setName(key);
		setTextFor(label, key, resourceRepository);
		return label;
	}

	public static JList buildList() {
		return new AccessibleList();
	}

	public static JList buildList(ListModel model) {
		return new AccessibleList(model);
	}

	public static JList buildList(Vector items) {
		return new AccessibleList(items);
	}

	public static Border buildTitledBorder(ResourceRepository resourceRepository, String key) {
	    return BorderFactory.createTitledBorder(resourceRepository.getString(key));
	}
	
	/**
	 * Creates a new <code>JRadioButton</code> and sets the text, mnemonic, and
	 * mnemonic index using the given resource key.  
	 * Must pass in a buttonModel because of a bug in Swing.
	 * The mnemonic will only work properly if set after the model is set.
	 */
	public static JRadioButton buildRadioButton(String key, ButtonModel buttonModel, ResourceRepository resourceRepository) {
		JRadioButton radioButton = new JRadioButton();
		radioButton.setModel(buttonModel);
		radioButton.setName(key);
		setTextFor(radioButton, key, resourceRepository);
		return radioButton;
	}

	/**
	 * Creates a new <code>JSpinner</code> that handles number. The length of the
	 * editor will be set with a default columns count of 4.
	 *
	 * @param model The spinner's model, which only handles number
	 * @return A new <code>JSpinner</code>
	 */
	public static JSpinner buildSpinnerNumber(SpinnerNumberModel model) {
		return buildSpinnerNumber(model, 4);
	}

	/**
	 * Creates a new <code>JSpinner</code> that handles number.
	 *
	 * @param columns The number of columns the spinner's editor should have, the
	 * default value is 4, even though on screen the width is greater than 4, a
	 * column count of 3 makes it too narrow
	 * @param model The spinner's model, which only handles number
	 * @return A new <code>JSpinner</code>
	 */
	public static JSpinner buildSpinnerNumber(SpinnerNumberModel model,
															  int columns)
	{
		JSpinner spinner = new JSpinner(model);

		// Set the number of columns
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		JFormattedTextField textField = editor.getTextField();
		textField.setColumns(columns);

		// JSpinner seems to ignore the value set in the model
		try
		{
			String value = textField.getFormatter().valueToString(model.getValue());
			textField.setText(value);
		}
		catch (ParseException e)
		{
			if (model.getValue() != null)
			{
				textField.setText(model.getValue().toString());
			}
		}

		return spinner;
	}

	/**
	 * Returns the standard 2-pixel, empty border
	 * for placing around panels etc.
	 */
	public static Border buildStandardEmptyBorder() {
		return STANDARD_EMPTY_BORDER;
	}

	public static JTable buildTable(TableModel tableModel) {
		return buildTable(tableModel, null, null);
	}

	public static JTable buildTable(
		TableModel tableModel,
		ListSelectionModel selectionModel)
	{
		return buildTable(tableModel, null, selectionModel);
	}
	
	public static JTable buildTable(
		TableModel tableModel, 
		TableColumnModel columnModel,
		ListSelectionModel selectionModel)
	{
		JTable table = new InternalJTable(tableModel, columnModel, selectionModel);

		// Removed this functionality
		// See MW bug 3766389
		// Use the MW TableCellEditorAdapter -> check boxes, combo boxes, and spinners
		//  will commit edits automatically.  Text fields, editable combo boxes,
		//  and editable spinners will lose information unless the edit is committed
		//  (return key is entered) prior to focus being lost.
		// table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);	// see Java bug 5007652

		table.setDoubleBuffered(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		return table;	
	}

	/**
	 * This will make sure the editor is cancelled when the subject of the given
	 * holder is changed. This will prevent an exception thrown when trying to
	 * set a value at the wrong index because the editor was never stopped.
	 */
	public static void attachTableEditorCanceler(final JTable table, ValueModel subjectHolder) {
		subjectHolder.addPropertyChangeListener(ValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (table.isEditing())
					table.getCellEditor().cancelCellEditing();
			}
		});
	}
	
	/**
	 * This will make sure values get set that are entered manually in JSpinners even
	 * when the enter key is not pressed.
	 * @param spinner
	 * @param subjectHolder
	 */
	public static void attachDateSpinnerCommiter(final JSpinner spinner, ValueModel subjectHolder) {
		subjectHolder.addPropertyChangeListener(ValueModel.VALUE, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					spinner.commitEdit();				
				} catch (ParseException exception) {
					//not much to do here but move on
				}
			}
		});
	}

	public static JTextField buildTextField(Document document) {
		JTextField textField = new JTextField();
		textField.setDocument(document);
		return textField;
	}

	public static JTree buildTree() {
		return new AccessibleTree();
	}

	public static JTree buildTree(TreeModel model) {
		return new AccessibleTree(model);
	}

	public static void installCellRenderer(JComboBox comboBox,
	                                       ListCellRenderer cellRenderer) {

		comboBox.setPrototypeDisplayValue(ComboBoxTableCellRenderer.prototypeLabel);
		comboBox.setRenderer (
			new InternalListCellRenderer(cellRenderer, comboBox.getRenderer())
		);
	}

	public static void installComboBoxEditorWithDefault(JComboBox comboBox,
	            ValueModel subjectHolder,
	            PropertyValueModel valueHolder,
	            ValueModel defaultValueHolder,
	            BidiStringConverter editorValueConverter,
	            Object nullValue)
		{
		installComboBoxEditorWithDefault
		(
			comboBox,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			editorValueConverter,
			nullValue,
			null
		);
	}

	public static void installComboBoxEditorWithDefault(JComboBox comboBox,
            ValueModel subjectHolder,
            PropertyValueModel valueHolder,
            ValueModel defaultValueHolder,
            BidiStringConverter editorValueConverter,
            Object nullValue,
            CellRendererAdapter cellRendererAdapter)
	{
		comboBox.setEditable(true);

		ComboBoxEditorWithDefault comboBoxEditor = new ComboBoxEditorWithDefault
		(
				comboBox.getEditor(),
				subjectHolder,
				valueHolder,
				defaultValueHolder,
				cellRendererAdapter
		);
	
		ComboBoxEditorAdapter editor = new ComboBoxEditorAdapter
		(
				comboBoxEditor,
				editorValueConverter,
				nullValue
		);
	
		comboBox.setEditor(editor);
	}

	/**
	 * Removes the given listener in order to stop receiving notification when a
	 * double click has been performed on the given component.
	 */
	public static void removeDoubleClickMouseListener(Component component,
	                                                  DoubleClickMouseListener listener)
	{
		doubleClickMouseHandler.removeMouseListener(component, listener);

		if (!doubleClickMouseHandler.hasListeners(component))
		{
			component.removeMouseListener(doubleClickMouseHandler);
		}
	}

	/**
	 * Sets the text and the mnemonic for the given button using the given key.
	 *
	 * @param button The button to be udpated
	 * @param key The key used to retrieve the text and the mnemonic
	 * @param arguments The message text posses patterns to be replaced with the
	 * array of values. The pattern is usually {i} where 'i' is a digit
	 */
	public static void setTextFor(AbstractButton button,
											  String key,
											  Object[] arguments,
											  ResourceRepository resourceRepository)
	{
		button.setText(resourceRepository.getString(key, arguments));
		button.setMnemonic(resourceRepository.getMnemonic(key));
		button.setDisplayedMnemonicIndex(resourceRepository.getMnemonicIndex(key));
	}

	/**
	 * Sets the text and the mnemonic for the given button using the given key.
	 *
	 * @param button The button to be udpated
	 * @param key The key used to retrieve the text and the mnemonic
	 */
	public static void setTextFor(AbstractButton button, 
											  String key, 
											  ResourceRepository resourceRepository)
	{
		setTextFor(button, key, EMPTY_OBJECT_ARRAY, resourceRepository);
	}

	/**
	 * Sets the text and the mnemonic for the given label using the given key.
	 *
	 * @param button The label to be udpated
	 * @param key The key used to retrieve the text and the mnemonic
	 * @param argument The tooltip text posses a pattern to be replaced with this
	 * value. The pattern is usually {0}
	 */
	public static void setTextFor(JLabel label,
											  String key,
											  Object argument,
											  ResourceRepository resourceRepository)
	{
		setTextFor(label, key, new Object[] { argument }, resourceRepository);
	}

	/**
	 * Sets the text and the mnemonic for the given label using the given key.
	 *
	 * @param button The label to be udpated
	 * @param key The key used to retrieve the text and the mnemonic
	 * @param arguments The message text posses patterns to be replaced with the
	 * array of values. The pattern is usually {i} where 'i' is a digit
	 */
	public static void setTextFor(JLabel label,
									  String key,
									  Object[] arguments,
									  ResourceRepository resourceRepository)
	{
		label.setText(resourceRepository.getString(key, arguments));
		label.setDisplayedMnemonic(resourceRepository.getMnemonic(key));
		label.setDisplayedMnemonicIndex(resourceRepository.getMnemonicIndex(key));
	}

	/**
	 * Sets the text and the mnemonic for the given label using the given key.
	 *
	 * @param button The label to be udpated
	 * @param key The key used to retrieve the text and the mnemonic
	 */
	public static void setTextFor(JLabel label, String key, ResourceRepository resourceRepository)
	{
		setTextFor(label, key, EMPTY_OBJECT_ARRAY, resourceRepository);
	}
	
	/**
	 * Updates the accessible name of the given button with the accessible name
	 * of the given label. This is required for accessibility because a button
	 * like a Browse button does not describe for what the button will browse.
	 *
	 * @param label The label that is usually attached to the widget that can
	 * have a browse button
	 * @param button The button to update its accessible name
	 */
	public static void updateButtonAccessibleName(JLabel label, AbstractButton button)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(label.getAccessibleContext().getAccessibleName());
		sb.append(" ");
		sb.append(button.getAccessibleContext().getAccessibleName());
		button.getAccessibleContext().setAccessibleName(sb.toString());
	}
	
	
	/**
	 * This extension over JButton is meant to change the behavior of
	 * AccessibleContext.getAccessibleName() and
	 * AccessibleContext.getAccessibleDescription() so that it returns the
	 * labeler's accessible name or description in addition to the button's.
	 */
	protected static class BrowseButton 
		extends JButton
	{
		Accessible labeler;
		
		protected BrowseButton(Accessible labeler) {
			super();
			this.labeler = labeler;
		}
		
		@Override
		public AccessibleContext getAccessibleContext() {
			if (this.accessibleContext == null) {
				if (this.labeler == null) {
					return super.getAccessibleContext();
				}
				this.accessibleContext = new AccessibleBrowseButton();
			}
			return this.accessibleContext;
		}
		
		protected class AccessibleBrowseButton
			extends AccessibleJButton 
		{
			@Override
			public String getAccessibleDescription() {
				StringBuffer sb = new StringBuffer();
				sb.append(BrowseButton.this.labeler.getAccessibleContext().getAccessibleDescription());
				sb.append(" ");
				sb.append(super.getAccessibleDescription());
				return sb.toString();
			}
			
			@Override
			public String getAccessibleName() {
				StringBuffer sb = new StringBuffer();
				sb.append(BrowseButton.this.labeler.getAccessibleContext().getAccessibleName());
				sb.append(" ");
				sb.append(super.getAccessibleName());
				return sb.toString();
			}
		}
	}
	
	
	/**
	 * This list makes sure the accessible parent is not the scroll pane but the
	 * parent container, which makes JAWS detects a group box if one exists.
	 */
	public static class AccessibleList extends JList
	{
		public AccessibleList()
		{
			super();
		}

		public AccessibleList(ListModel model)
		{
			super(model);
		}

		public AccessibleList(Vector items)
		{
			super(items);
		}

		@Override
		public AccessibleContext getAccessibleContext()
		{
			if (this.accessibleContext == null) {
				this.accessibleContext = new AccessibleAccessibleList();
			}
			return this.accessibleContext;
		}

		protected class AccessibleAccessibleList extends AccessibleJList
		{
			@Override
			public String getAccessibleName()
			{
				String name = super.getAccessibleName();

				if (name != null)
					return name;

				// JList -> JViewport -> JScrollPane -> parent container
				if (getParent() instanceof JViewport)
				{
					JComponent container = (JComponent) getParent().getParent().getParent();
					return container.getAccessibleContext().getAccessibleName();
				}

				return null;
			}
		}
	}

	/**
	 * This list makes sure the accessible parent is not the scroll pane but the
	 * parent container, which makes JAWS detects a group box if one exists.
	 */
	public static class AccessibleTree extends JTree
	{
		public AccessibleTree()
		{
			super();
		}

		public AccessibleTree(TreeModel model)
		{
			super(model);
		}

		@Override
		public AccessibleContext getAccessibleContext()
		{
			if (this.accessibleContext == null) {
				this.accessibleContext = new AccessibleAccessibleTree();
			}
			return this.accessibleContext;
		}

		protected class AccessibleAccessibleTree extends AccessibleJTree
		{
			@Override
			public String getAccessibleName()
			{
				String name = super.getAccessibleName();

				if (name != null)
					return name;

				// JList -> JViewport -> JScrollPane -> parent container
				if (getParent() instanceof JViewport)
				{
					JComponent container = (JComponent) getParent().getParent().getParent();
					return container.getAccessibleContext().getAccessibleName();
				}

				return null;
			}
		}
	}

	/**
	 * This <code>MouseListener</code> is responsible to notify all the registered
	 * <code>DoubleClickMouseListener</code>s when a double click has been
	 * performed on a component.
	 */
	private static class DoubleClickMouseHandler extends MouseAdapter
	{
		/**
		 * Keeps a list of <code>DoubleClickMouseListener</code>s that were
		 * registered on a certain component.
		 */
		private final Map componentMouseListeners;

		/**
		 * The object responsible to determine if a mouse click is actually the
		 * second of two clicks that will trigger a double click event.
		 */
		private final DoubleClickTrigger trigger;

		/**
		 * Creates a new <code>DoucleClickMouseHandler</code>.
		 */
		DoubleClickMouseHandler()
		{
			super();
			this.trigger = new DoubleClickTrigger();
			this.componentMouseListeners = new Hashtable();
		}

		/**
		 * Adds the given listener on the given component in order to notify it
		 * upon double click.
		 */
		void addMouseListener(Component component,
		                      DoubleClickMouseListener listener)
		{
			List listeners = (List) this.componentMouseListeners.get(component);

			if (listeners == null)
			{
				listeners = new Vector();
				this.componentMouseListeners.put(component, listeners);
			}

			listeners.add(listener);
		}

		/**
		 * Notifies all the registered <code>DoubleClickMouseListener</code>s that a
		 * double click has been done on the component.
		 *
		 * @param e The second <code>MouseEvent</code> which has been passed to a
		 * mouseReleased event
		 */
		private void fireMouseDoucleClicked(MouseEvent e)
		{
			List listeners = (List) this.componentMouseListeners.get(e.getComponent());

			for (Iterator iter = listeners.iterator(); iter.hasNext();)
			{
				((DoubleClickMouseListener) iter.next()).mouseDoubleClicked(e);
			}
		}

		/**
		 * Determines if there are any <code>DoubleClickMouseListener</code>s
		 * registered on the given component.
		 */
		boolean hasListeners(Component component)
		{
			return this.componentMouseListeners.containsKey(component);
		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 */
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (this.trigger.isDoubleClick(e))
			{
				fireMouseDoucleClicked(e);
			}
		}

		/**
		 * Removes the given listener from the list of registered listeners.
		 */
		void removeMouseListener(Component component,
		                         DoubleClickMouseListener listener)
		{
			List listeners = (List) this.componentMouseListeners.get(component);

			if ((listeners == null) || !listeners.contains(listener))
				throw new IllegalStateException("Can't remove a listener that was not registered");

			listeners.remove(listener);

			if (listeners.isEmpty())
			{
				listeners.remove(component);
			}
		}
	}

	private static class InternalJTable extends JTable
	{
		InternalJTable(TableModel tableModel,
							TableColumnModel tableColumnModel,
							ListSelectionModel listSelectionModel)
		{
			super(tableModel, tableColumnModel, listSelectionModel);
		}

		@Override
		public Color getBackground()
		{
			if (!isEnabled())
				return UIManager.getColor("control");

			return super.getBackground();
		}

		@Override
		public Color getForeground()
		{
			if (!isEnabled())
				return UIManager.getColor("controlText");

			return super.getForeground();
		}

		private boolean isMousePressed(MouseEvent e)
		{
			return (e.getModifiers() & MouseEvent.MOUSE_PRESSED) != 0;
		}

		@Override
		protected void processMouseEvent(MouseEvent e)
		{
			// Make sure the editing is stopped before a MousePressed is
			// dispatched to a new editor
			if (isEditing() &&
				 SwingUtilities.isLeftMouseButton(e) &&
				 isMousePressed(e))
			{
				Rectangle bounds = getCellRect(getEditingRow(), getEditingColumn(), true);

				if (!bounds.contains(e.getX(), e.getY()))
				{
					getCellEditor().stopCellEditing();
				}
			}

			super.processMouseEvent(e);
		}

		@Override
		public void setEnabled(boolean enabled)
		{
			boolean oldEnabled = isEnabled();
			super.setEnabled(enabled);

			if ((oldEnabled != enabled) && (getParent() != null))
			{
				getParent().setBackground(getBackground());
				getParent().repaint();
			}
		}
	}

	private static class InternalListCellRenderer implements ListCellRenderer {

		private final ListCellRenderer customRenderer;
		private final ListCellRenderer defaultDelegate;

		public InternalListCellRenderer(ListCellRenderer customRenderer,
		                                ListCellRenderer defaultDelegate) {

			super();

			this.customRenderer  = customRenderer;
			this.defaultDelegate = defaultDelegate;
		}

		public Component getListCellRendererComponent(JList list,
		                                              Object value,
		                                              int index,
		                                              boolean selected,
		                                              boolean cellHasFocus) {

			// Don't need to format the cell, use the prototype label directly
			if (value == ComboBoxTableCellRenderer.prototypeLabel) {
				return ComboBoxTableCellRenderer.prototypeLabel;
			}

			// This will set the icon, text, accessible text from our own renderer
			JLabel ourLabel = (JLabel) customRenderer.getListCellRendererComponent(list, value, index, selected, cellHasFocus);

			// This will allow the look and feel to set anything in order to
			// reflect the native OS
			JLabel actualLabel = (JLabel) defaultDelegate.getListCellRendererComponent(list, null, index, selected, cellHasFocus);

			// Copy the properties to the actual label
			actualLabel.setText(ourLabel.getText());
			actualLabel.setIcon(ourLabel.getIcon());
			actualLabel.setToolTipText(ourLabel.getToolTipText());
			actualLabel.getAccessibleContext().setAccessibleName(ourLabel.getAccessibleContext().getAccessibleName());

			if (!list.hasFocus() && selected && list.isFocusable()) {
				actualLabel.setForeground(list.getForeground());
				actualLabel.setBackground(UIManager.getColor("Panel.background"));
			}

			// The height of the combo's popup isn't calculated correctly because
			// the height is different when there is no icon, because of that, a
			// scroll pane is shown when it should not
			if (actualLabel.getIcon() == null) {
				actualLabel.setIcon(EmptyIcon.SMALL_ICON);
				actualLabel.setPreferredSize(actualLabel.getPreferredSize());
				actualLabel.setIcon(null);
			}

			return actualLabel;
		}
	}
	
	/**
	 * This extension over <code>JTextArea</code> simply makes sure the minimum
	 * height is always no smaller than 5 times the font's height.
	 */
	private static class TextArea extends JTextArea
	{
		private int minimumHeight;
	
		TextArea(Document document)
		{
			super(document);
			initialize();
		}
	
		/**
		 * Calculates a height that is equivalent to 5 times the height of the
		 * font's height.
		 *
		 * @return A height that equals 5 times the font's height plus the top and
		 * bottom margins
		 */
		private int calculateMinimumHeight()
		{
			int minimumHeight = getFontMetrics(getFont()).getHeight() * 5;
			Insets insets = getInsets();
			minimumHeight += (insets.top + insets.bottom);
			return minimumHeight;
		}
	
		/*
		 * (non-Javadoc)
		 */
		@Override
		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}
	
		/*
		 * (non-Javadoc)
		 */
		@Override
		public Dimension getPreferredSize()
		{
			Dimension size = super.getPreferredSize();
	
			if (minimumHeight == 0)
			{
				minimumHeight = calculateMinimumHeight();
			}
	
			size.height = Math.max(minimumHeight, size.height);
			return size;
		}
	
		private void initialize()
		{
			setFont(UIManager.getFont("Label.font"));
			Insets margin = getMargin();
	
			setBorder(BorderFactory.createCompoundBorder());
		}
	}

	/**
	 * Creates a new titled border.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param repository The repository used to retrieve the localized string
	 * @return A new titled border
	 */
	public static Border buildTitledBorder(String key, ResourceRepository repository)
	{
		return BorderFactory.createTitledBorder(repository.getString(key));
	}

	/**
	 * Creates a new border that uses a titled border and adds the right internal
	 * margin.
	 *
	 * @param key The key used to retrieve the localized string used as the title
	 * @param repository The repository used to retrieve the localized text
	 * @return A new border that is usually used to group a set of components
	 */
	public static Border buildPaneTitledBorder(String key,
	                                           ResourceRepository repository)
	{
		return BorderFactory.createCompoundBorder
		(
			buildTitledBorder(key, repository),
			BorderFactory.createEmptyBorder(0, 5, 5, 5)
		);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code> where the internal pane will
	 * be collapsed or expanded based on the expanded value.
	 *
	 * @param key The key used to retrieve the expandable button's text
	 * @param internalPane The pane that will be expanded or collapsed
	 * @param expanded <code>true</code> if the pane needs to be expanded by
	 * default or <code>false</code> to have it collapsed
	 * @param repository The <code>ResourceRepository</code> used to retrieve
	 * the button's text and the icons
	 */
	public static ExpandablePane buildExpandablePanel(String key,
	                                                  JComponent internalPane,
	                                                  boolean expanded,
	                                                  ResourceRepository repository)
	{
		return buildExpandablePanel
		(
			key,
			key,
			internalPane,
			expanded,
			repository
		);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code> where the internal pane will
	 * be collapsed by default.
	 *
	 * @param expandedTextKey The key used to retrieve the button's text when the
	 * pane is collapsed
	 * @param collapsedTextKey The key used to retrieve the button's text when the
	 * pane is expanded
	 * @param internalPane The pane that will be expanded or collapsed
	 * @param repository The <code>ResourceRepository</code> used to retrieve
	 * the button's text and the icons
	 */
	public static ExpandablePane buildExpandablePanel(String key,
	                                                   JComponent internalPane,
	                                                   ResourceRepository repository)
	{
		return buildExpandablePanel
		(
			key,
			key,
			internalPane,
			false,
			repository
		);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code> where the internal pane will
	 * be collapsed or expanded based on the expanded value.
	 *
	 * @param expandedTextKey The key used to retrieve the button's text when the
	 * pane is collapsed
	 * @param collapsedTextKey The key used to retrieve the button's text when the
	 * pane is expanded
	 * @param internalPane The pane that will be expanded or collapsed
	 * @param expanded <code>true</code> if the pane needs to be expanded by
	 * default or <code>false</code> to have it collapsed
	 * @param repository The <code>ResourceRepository</code> used to retrieve
	 * the button's text and the icons
	 */
	public static ExpandablePane buildExpandablePanel(String expandedTextKey,
	                                                   String collapsedTextKey,
	                                                   JComponent internalPane,
	                                                   boolean expanded,
	                                                   ResourceRepository repository)
	{
		return new ExpandablePane
		(
			repository.getString(expandedTextKey),
			repository.getString(collapsedTextKey),
			repository.getIcon("expand"),
			repository.getIcon("collapse"),
			repository.getIcon("expand.disabled"),
			repository.getIcon("collapse.disabled"),
			repository.getIcon("expand.focus"),
			repository.getIcon("collapse.focus"),
			repository.getIcon("expand.pressed"),
			repository.getIcon("collapse.pressed"),
			internalPane,
			expanded
		);
	}

	/**
	 * Creates a new <code>ExpandablePanel</code> where the internal pane will
	 * be collapsed by default.
	 *
	 * @param key The key used to retrieve the expandable button's text
	 * @param internalPane The pane that will be expanded or collapsed
	 * @param expanded <code>true</code> if the pane needs to be expanded by
	 * default or <code>false</code> to have it collapsed
	 * @param repository The <code>ResourceRepository</code> used to retrieve
	 * the button's text and the icons
	 */
	public static ExpandablePane buildExpandablePanel(String expandedTextKey,
	                                                   String collapsedTextKey,
	                                                   JComponent internalPane,
	                                                   ResourceRepository repository)
	{
		return buildExpandablePanel
		(
			expandedTextKey,
			collapsedTextKey,
			internalPane,
			false,
			repository
		);
	}
	
	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledComboBox(String key,
	                                              ComboBoxModel model,
	                                              CellRendererAdapter rendererAdapter,
	                                              JComponent rightComponent,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
         rendererAdapter,
			rightComponent,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledComboBox(String key,
	                                              ComboBoxModel model,
	                                              CellRendererAdapter renderer,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent The component to be shown after the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledComboBox(String key,
	                                              ComboBoxModel model,
	                                              JComponent rightComponent,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			rightComponent,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledComboBox(String key,
	                                              ComboBoxModel model,
	                                              ListCellRenderer renderer,
	                                              JComponent rightComponent,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			rightComponent,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledComboBox(String key,
	                                              ComboBoxModel model,
	                                              ListCellRenderer renderer,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledComboBox(String key,
	                                              ComboBoxModel model,
		                                           ResourceRepository repository,
		                                           ComponentAligner leftAligner,
		                                           ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a new editable combo box that has a label and possibly a right
	 * component.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The combo box's model
	 * @param valueHolder
	 * @param defaultValueHolder
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter This <code>Transformer</code> is used to
	 * transform the editor's value to an object to be set in the combo's model
	 * or <code>null</code> is the combo is not editable
	 * @param nullValue The value that acts as the "null" value
	 * @param rightComponent The right component or <code>null</code> is none is
	 * specified
	 * @param editable <code>true</code> to turn to allow the combo to be
	 * editable; <code>false</code> otherwise
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @param topAlignedLabel
	 * @return A new component containing a label and a combo box
	 */
	private static JComponent buildLabeledComboBoxImp(String key,
	                                                  ComboBoxModel model,
	                                                  CellRendererAdapter rendererAdapter,
	                                                  JComponent rightComponent,
	                                                  Object nullValue,
		                                               ResourceRepository repository,
		                                               ComponentAligner leftAligner,
		                                               ComponentAligner rightAligner,
		                                               boolean topAlignedLabel,
		                                               boolean listChooser)
	{
		return buildLabeledComponent
		(
			key,
			listChooser ? buildListChooser(model, rendererAdapter) : buildComboBox(model, rendererAdapter),
			(rightComponent == null) ? new Spacer() : rightComponent,
			repository,
			leftAligner,
			rightAligner,
			topAlignedLabel
		);
	}

	/**
	 * Creates a new editable combo box that has a label and possibly a right
	 * component.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The combo box's model
	 * @param valueHolder
	 * @param defaultValueHolder
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter This <code>Transformer</code> is used to
	 * transform the editor's value to an object to be set in the combo's model
	 * or <code>null</code> is the combo is not editable
	 * @param nullValue The value that acts as the "null" value
	 * @param rightComponent The right component or <code>null</code> is none is
	 * specified
	 * @param editable <code>true</code> to turn to allow the combo to be
	 * editable; <code>false</code> otherwise
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @param topAlignedLabel
	 * @return A new component containing a label and a combo box
	 */
	private static JComponent buildLabeledComboBoxImp(String key,
	                                                  ComboBoxModel model,
	                                                  ListCellRenderer renderer,
	                                                  JComponent rightComponent,
	                                                  Object nullValue,
		                                               ResourceRepository repository,
		                                               ComponentAligner leftAligner,
		                                               ComponentAligner rightAligner,
		                                               boolean topAlignedLabel,
		                                               boolean listChooser)
	{
		return buildLabeledComponent
		(
			key,
			listChooser ? buildListChooser(model, renderer) : buildComboBox(model, renderer),
			(rightComponent == null) ? new Spacer() : rightComponent,
			repository,
			leftAligner,
			rightAligner,
			topAlignedLabel
		);
	}
	
	/**
	 * Creates a new <code>JComboBox</code> which will use the given data model
	 * which will be rendered by the given <code>ListCellRenderer</code>.
	 *
	 * @param model The model holding the items listed in the combo box
	 * @param cellRenderer The renderer used to decorate the items contained in
	 * the model
	 * @return A new <code>Combo</code>
	 */
	public static Combo buildComboBox(ComboBoxModel model,
	                                  ListCellRenderer cellRenderer)
	{
		Combo combo = buildComboBox(model);

		if (cellRenderer != null)
		{
			installCellRenderer(combo, cellRenderer);
		}

		return combo;
	}

	/**
	 * Creates a new <code>JComboBox</code> which will use the given data model.
	 *
	 * @param model The model holding the items listed in the combo box
	 * @return A new <code>Combo</code>
	 */
	public static Combo buildComboBox(ComboBoxModel model)
	{
		Combo combo = new Combo(model);
		return combo;
	}

	/**
	 * Creates a new <code>ListChooser</code> which will use the given data model
	 * which will be rendered by the given <code>ListCellRenderer</code>.
	 *
	 * @param model The model holding the items listed in the combo box
	 * @param cellRenderer The renderer used to decorate the items contained in
	 * the model
	 * @return A new <code>ListChooser</code>
	 */
	public static ListChooser buildListChooser(ComboBoxModel model,
	                                           ListCellRenderer cellRenderer)
	{
		ListChooser comboBox = buildListChooser(model);

		if (cellRenderer != null)
		{
			installCellRenderer(comboBox, cellRenderer);
		}

		return comboBox;
	}

	/**
	 * Creates a new <code>ListChooser</code> which will use the given data model.
	 *
	 * @param model The model holding the items listed in the combo box
	 * @return A new <code>ListChooser</code>
	 */
	public static ListChooser buildListChooser(ComboBoxModel model)
	{
		ListChooser chooser;

		if (model instanceof CachingComboBoxModel)
		{
			chooser = new ListChooser((CachingComboBoxModel) model);
		}
		else
		{
			chooser = new ListChooser(model);
		}

		chooser.setLongListSize(Integer.MAX_VALUE);
		return chooser;
	}

	/**
	 * Creates a container that has a label and the given chooser.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and the chooser
	 */
	public static JComponent buildLabeledComponent(String key,
	                                              JComponent component,
	                                              JComponent rightComponent,
                                                 ResourceRepository repository,
                                                 ComponentAligner leftAligner,
                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			key,
			component,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false
		);
	}

	/**
	 * Creates a container where a label and the given component are laid out
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledComponent(String key,
	                                               JComponent component,
	                                               ResourceRepository repository,
	                                               ComponentAligner leftAligner,
	                                               ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			key,
			component,
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label, a center component and a right
	 * component, the right component is usually a button or a spacer.
	 *
	 * @param key The key used to retrieve the localized string for the label
	 * @param centerComponent The component that is labeled and that is centered
	 * in the returned container, it takes all the horizontal space. For lists
	 * and trees, the component will also expand vertically
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and the chooser
	 */
	private static JComponent buildLabeledComponent(String key,
	                                                JComponent centerComponent,
	                                                JComponent rightComponent,
                                                   ResourceRepository repository,
                                                   ComponentAligner leftAligner,
                                                   ComponentAligner rightAligner,
                                                   boolean topAlignedLabel)
	{
		return buildLabeledComponent
		(
			buildLabel(key, repository),
			centerComponent,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			topAlignedLabel
		);
	}

	/**
	 * Creates a container that has a label, a center component and a right
	 * component, the right component is usually a button or a spacer.
	 *
	 * @param key The key used to retrieve the localized string for the label
	 * @param centerComponent The component that is labeled and that is centered
	 * in the returned container, it takes all the horizontal space. For lists
	 * and trees, the component will also expand vertically
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and the chooser
	 */
	public static JComponent buildLabeledComponent(JComponent leftComponent,
	                                               JComponent centerComponent,
	                                               JComponent rightComponent,
	                                               ResourceRepository repository,
	                                               ComponentAligner leftAligner,
	                                               ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			leftComponent,
			centerComponent,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false
		);
	}

	/**
	 * Creates a container that has a label, a center component and a right
	 * component, the right component is usually a button or a spacer.
	 *
	 * @param key The key used to retrieve the localized string for the label
	 * @param centerComponent The component that is labeled and that is centered
	 * in the returned container, it takes all the horizontal space. For lists
	 * and trees, the component will also expand vertically
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and the chooser
	 */
	private static JComponent buildLabeledComponent(JComponent leftComponent,
	                                                JComponent centerComponent,
	                                                JComponent rightComponent,
	                                                ResourceRepository repository,
	                                                ComponentAligner leftAligner,
	                                                ComponentAligner rightAligner,
	                                                boolean topAlignedLabel)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Pane pane = new Pane(new GridBagLayout());

		// Left component
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = topAlignedLabel ? 2 : 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);

		pane.add(leftComponent, constraints);
		leftAligner.add(leftComponent);

		// Center component
		constraints.gridx      = topAlignedLabel ? 0 : 1;
		constraints.gridy      = topAlignedLabel ? 1 : 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.insets     = new Insets(topAlignedLabel ? 1 : 0, topAlignedLabel ? 0 : 5, 0, 0);

		// Lists, trees or any scrollable components need to expand since
		// the pane might expand vertically it its container
		if ((centerComponent instanceof JScrollPane) ||
		    (centerComponent instanceof Scrollable))
		{
			constraints.weighty = 1;
			constraints.fill    = GridBagConstraints.BOTH;
		}
		// Other widgets like spinner, texts, combos should not expand vertically
		else
		{
			constraints.weighty = 0;
			constraints.fill    = GridBagConstraints.HORIZONTAL;
		}

		pane.add(centerComponent, constraints);

		if (leftComponent instanceof JLabel)
		{
			JLabel label = (JLabel) leftComponent;
			label.setLabelFor(centerComponent);
		}
		else
		{
			centerComponent.putClientProperty(LABELED_BY_KEY, leftComponent);
		}

		// Right component
		if (rightComponent != null)
		{
			constraints.gridx      = topAlignedLabel ? 1 : 2;
			constraints.gridy      = topAlignedLabel ? 1 : 0;
			constraints.gridwidth  = 1;
			constraints.gridheight = 1;
			constraints.weightx    = 0;
			constraints.weighty    = 0;
			constraints.fill       = GridBagConstraints.NONE;
			constraints.anchor     = GridBagConstraints.LINE_START;
			constraints.insets     = new Insets(topAlignedLabel ? 1 : 0, 5, 0, 0);

			pane.add(rightComponent, constraints);
			rightAligner.add(rightComponent);

			if ((rightComponent instanceof AbstractButton) &&
			    (leftComponent  instanceof Accessible))
			{
				updateButtonAccessibleName((Accessible) leftComponent, rightComponent);
			}
		}

		return pane;
	}
	
	private static JComponent buildLabeledEditableComboBoxImp(String key,
	            ComboBoxModel model,
	            ValueModel subjectHolder,
	            PropertyValueModel valueHolder,
	            ValueModel defaultValueHolder,
	            ListCellRenderer renderer,
	            BidiStringConverter editorValueConverter,
	            Object nullValue,
	            JComponent rightComponent,
	            ResourceRepository repository,
	            ComponentAligner leftAligner,
	            ComponentAligner rightAligner,
	            boolean topAlignedLabel,
	            boolean listChooser)
	{
		JComponent container = buildLabeledComboBoxImp
		(
			key,
			new ComboBoxModelWithDefaultHandler(model, defaultValueHolder),
			renderer,
			rightComponent,
			nullValue,
			repository,
			leftAligner,
			rightAligner,
			topAlignedLabel,
			listChooser
		);
		
		JComboBox comboBox = (JComboBox) container.getComponent(1);
		
		installComboBoxEditorWithDefault
		(
			comboBox,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			editorValueConverter,
			nullValue
		);
		
		return container;
	}	
	/**
	 * Updates the accessible name of the given <code>Accessible</code> component
	 * by retrieving its accessible name. This is required for accessibility
	 * because a button like a Browse button does not describe its function.
	 *
	 * @param accessible The <code>Accessible</code> component that is used to
	 * retrieve the accessible name of the attached "labeler"
	 * @param button The component to update its accessible name
	 */
	public static void updateButtonAccessibleName(Accessible accessible,
	                                              JComponent button)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(accessible.getAccessibleContext().getAccessibleName());
		sb.append(' ');
		sb.append(button.getAccessibleContext().getAccessibleName());
		button.getAccessibleContext().setAccessibleName(sb.toString());
	}
	
	/**
	 * Creates a new <code>ListChooser</code> which will use the given data model
	 * which will be rendered by the given <code>ListCellRenderer</code>.
	 *
	 * @param model The model holding the items listed in the combo box
	 * @param cellRenderer The renderer used to decorate the items contained in
	 * the model
	 * @return A new <code>ListChooser</code>
	 */
	public static ListChooser buildListChooser(ComboBoxModel model,
	                                           CellRendererAdapter cellRendererAdapter)
	{
		ListChooser chooser = buildListChooser(model);
		if (cellRendererAdapter != null) {
			chooser.setRendererAdapter(cellRendererAdapter);
		}
		return chooser;
	}

	/**
	 * Creates a new <code>JComboBox</code> which will use the given data model
	 * which will be rendered by the given <code>ListCellRenderer</code>.
	 *
	 * @param model The model holding the items listed in the combo box
	 * @param cellRenderer The renderer used to decorate the items contained in
	 * the model
	 * @return A new <code>Combo</code>
	 */
	public static Combo buildComboBox(ComboBoxModel model,
	                                  CellRendererAdapter cellRendererAdapter)
	{
		Combo combo = buildComboBox(model);

		if (cellRendererAdapter != null)
		{
			combo.setRendererAdapter(cellRendererAdapter);
		}

		return combo;
	}

	private static JComponent buildLabeledEditableComboBoxImp(String key,
	                                                          ComboBoxModel model,
	                                                          ValueModel subjectHolder,
	                                                          PropertyValueModel valueHolder,
	                                                          ValueModel defaultValueHolder,
	                                                          CellRendererAdapter renderer,
	                                                          BidiStringConverter editorValueConverter,
	                                                          Object nullValue,
	                                                          JComponent rightComponent,
	                                                          ResourceRepository repository,
	                                                          ComponentAligner leftAligner,
	                                                          ComponentAligner rightAligner,
	                                                          boolean topAlignedLabel,
	                                                          boolean listChooser)
	{
		JComponent container = buildLabeledComboBoxImp
		(
			key,
			new ComboBoxModelWithDefaultHandler(model, defaultValueHolder),
			renderer,
			rightComponent,
			nullValue,
			repository,
			leftAligner,
			rightAligner,
			topAlignedLabel,
			listChooser
		);
	
		JComboBox comboBox = (JComboBox) container.getComponent(1);
	
		installComboBoxEditorWithDefault
		(
			comboBox,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			editorValueConverter,
			nullValue
		);
	
		return container;
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      CellRendererAdapter renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      CellRendererAdapter renderer,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      CellRendererAdapter renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
	                                                      ComponentAligner leftAligner,
	                                                      ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
	                                                      ComponentAligner leftAligner,
	                                                      ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValue,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValue,
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValue,
	                                                      ListCellRenderer renderer,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValue,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableComboBox(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ResourceRepository repository,
	                                                      ComponentAligner leftAligner,
	                                                      ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      CellRendererAdapter renderer,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ListCellRenderer renderer,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
	                                                      ComponentAligner leftAligner,
	                                                      ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValue,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValue,
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      BidiStringConverter editorValueConverter,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValue,
	                                                      ListCellRenderer renderer,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValue,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      ListCellRenderer renderer,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      Object nullValue,
	                                                      JComponent rightComponent,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                      ComboBoxModel model,
	                                                      ValueModel subjectHolder,
	                                                      PropertyValueModel valueHolder,
	                                                      ValueModel defaultValueHolder,
	                                                      Object nullValue,
	                                                      ResourceRepository repository,
		                                                   ComponentAligner leftAligner,
		                                                   ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledEditableListChooser(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledListChooser(String key,
	                                                 ComboBoxModel model,
	                                                 CellRendererAdapter renderer,
	                                                 JComponent rightComponent,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			rightComponent,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledListChooser(String key,
	                                                 ComboBoxModel model,
	                                                 CellRendererAdapter renderer,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent The component to be shown after the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledListChooser(String key,
	                                                 ComboBoxModel model,
	                                                 JComponent rightComponent,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			rightComponent,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledListChooser(String key,
	                                                 ComboBoxModel model,
	                                                 ListCellRenderer renderer,
	                                                 JComponent rightComponent,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			rightComponent,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledListChooser(String key,
	                                              ComboBoxModel model,
	                                              ListCellRenderer renderer,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildLabeledListChooser(String key,
	                                              ComboBoxModel model,
		                                           ResourceRepository repository,
		                                           ComponentAligner leftAligner,
		                                           ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			false,
			true
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledNonEditableTextField(String key,
	                                                          Document document,
	                                                          JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		JTextField textField = buildTextField(document);
		textField.setEditable(false);
	
		return buildLabeledComponent
		(
			key,
			textField,
			rightComponent,
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledNonEditableTextField(String key,
	                                                          Document document,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledNonEditableTextField
		(
			key,
			document,
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	public static JComponent buildLabeledSpinnerDate(String labelKey,
	                                                 SpinnerDateModel model,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			labelKey,
			buildSpinnerDate(model),
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label and a spinner.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The spinner's model, which only handles number
	 * @param columns The number of columns the spinner's editor should have, the
	 * default value is 4, even though on screen the width is greater than 4, a
	 * column count of 3 makes it too narrow
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a spin button
	 */
	public static JComponent buildLabeledSpinnerNumber(String key,
	                                                   SpinnerNumberModel model,
	                                                   int columns,
	                                                   ResourceRepository repository,
	                                                   ComponentAligner leftAligner,
	                                                   ComponentAligner rightAligner)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Pane pane = new Pane(new GridBagLayout());
	
		// Left component
		JLabel label = buildLabel(key, repository);
		pane.add(label, constraints);
		leftAligner.add(label);
	
		// Center component
		JSpinner spinner = buildSpinnerNumber(model, columns);
	
		constraints.weightx = 1;
		constraints.fill    = GridBagConstraints.NONE;
		constraints.anchor  = GridBagConstraints.LINE_START;
		constraints.insets  = new Insets(0, 5, 0, 0);
	
		pane.add(spinner, constraints);
		label.setLabelFor(spinner);
		spinner.setName(key);
	
		// Right component
		Spacer spacer = new Spacer();
	
		constraints.weightx = 0;
		constraints.fill    = GridBagConstraints.NONE;
	
		pane.add(spacer, constraints);
		rightAligner.add(spacer);
	
		return pane;
	}

	/**
	 * Creates a container that has a label and a spinner.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The spinner's model, which only handles number
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a spin button
	 */
	public static JComponent buildLabeledSpinnerNumber(String key,
	                                                   SpinnerNumberModel model,
	                                                   ResourceRepository repository,
	                                                   ComponentAligner leftAligner,
	                                                   ComponentAligner rightAligner)
	{
		return buildLabeledSpinnerNumber
		(
		 	key,
		 	model,
		 	4,
		 	repository,
		 	leftAligner,
		 	rightAligner
		 );
	}

	/**
	 * Creates a container that has a label and a spinner.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The spinner's model, which only handles number
	 * @param columns The number of columns the spinner's editor should have, the
	 * default value is 4, even though on screen the width is greater than 4, a
	 * column count of 3 makes it too narrow
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a spin button
	 */
	public static JComponent buildLabeledSpinnerNumber(String key,
	                                                   ValueModel subjectHolder,
	                                                   PropertyValueModel valueHolder,
	                                                   ValueModel defaultValueHolder,
	                                                   Comparable minimumValue,
	                                                   Comparable maximumValue,
	                                                   Number stepSize,
	                                                   int columns,
	                                                   ResourceRepository repository,
	                                                   ComponentAligner leftAligner,
	                                                   ComponentAligner rightAligner)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		Pane pane = new Pane(new GridBagLayout());
	
		// Left component
		JLabel label = buildLabel(key, repository);
		pane.add(label, constraints);
		leftAligner.add(label);
	
		// Center component
		JSpinner spinner = buildSpinnerNumber
		(
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			minimumValue,
			maximumValue,
			stepSize,
			columns
		);
	
		constraints.weightx = 1;
		constraints.fill    = GridBagConstraints.NONE;
		constraints.anchor  = GridBagConstraints.LINE_START;
		constraints.insets  = new Insets(0, 5, 0, 0);
	
		pane.add(spinner, constraints);
		label.setLabelFor(spinner);
		spinner.setName(key);
	
		// Right component
		Spacer spacer = new Spacer();
	
		constraints.weightx = 0;
		constraints.fill    = GridBagConstraints.NONE;
	
		pane.add(spacer, constraints);
		rightAligner.add(spacer);
	
		return pane;
	}

	/**
	 * Creates a container that has a label and a spinner.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The spinner's model, which only handles number
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a spin button
	 */
	public static JComponent buildLabeledSpinnerNumber(String key,
	                                                   ValueModel subjectHolder,
	                                                   PropertyValueModel valueHolder,
	                                                   ValueModel defaultValueHolder,
	                                                   Comparable minimumValue,
	                                                   Comparable maximumValue,
	                                                   Number stepSize,
	                                                   ResourceRepository repository,
	                                                   ComponentAligner leftAligner,
	                                                   ComponentAligner rightAligner)
	{
		return buildLabeledSpinnerNumber
		(
		 	key,
		 	subjectHolder,
		 	valueHolder,
		 	defaultValueHolder,
		 	minimumValue,
		 	maximumValue,
		 	stepSize,
		 	4,
		 	repository,
		 	leftAligner,
		 	rightAligner
		 );
	}

	/**
	 * Creates a new <code>JSpinner</code> that handles date.
	 *
	 * @param model The spinner's model, which only handles date
	 * @return A new <code>JSpinner</code>
	 */
	public static JSpinner buildSpinnerDate(SpinnerDateModel model)
	{
		JSpinner spinner = new JSpinner();
		spinner.setModel(model);
		return spinner;
	}

	/**
	 * Creates a new <code>JSpinner</code> that handles number.
	 *
	 * @param model The spinner's model, which only handles number
	 * @param columns The number of columns the spinner's editor should have, the
	 * default value is 4, even though on screen the width is greater than 4, a
	 * column count of 3 makes it too narrow
	 * @return A new <code>JSpinner</code>
	 */
	public static JSpinner buildSpinnerNumber(ValueModel subjectHolder,
	                                          PropertyValueModel valueHolder,
	                                          ValueModel defaultValueHolder,
											  Comparable minimumValue,
										      Comparable maximumValue,
										      Number stepSize,
											  int columns)
	{
		SpinnerWithDefaultHandler handler = new SpinnerWithDefaultHandler
		(
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			minimumValue,
			maximumValue,
			stepSize
		);
	
		JSpinner spinner = handler.getSpinner();
	
		// Set the number of columns
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		JFormattedTextField textField = editor.getTextField();
		textField.setColumns(columns);
	
		return spinner;
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledTextField(String key,
	                                               Document document,
	                                               int columns,
	                                               JComponent rightComponent,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			key,
			buildTextField(document, columns),
			rightComponent,
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledTextField(String key,
	                                               Document document,
	                                               int columns,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledTextField
		(
			key,
			document,
			columns,
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledTextField(String key,
	                                               Document document,
	                                               JComponent rightComponent,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			key,
			buildTextField(document),
			rightComponent,
			repository,
			leftAligner,
			rightAligner
		);
	}

	public static JComponent buildLabeledTextField(String key,
            									Document document,
            									JTextField field,
            									ResourceRepository repository,
            									ComponentAligner leftAligner,
            									ComponentAligner rightAligner)
	{
		field.setDocument(document);
		return buildLabeledComponent
				(
					key,
					field,
					repository,
					leftAligner,
					rightAligner
				);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildLabeledTextField(String key,
	                                               Document document,
	                                              ResourceRepository repository,
	                                              ComponentAligner leftAligner,
	                                              ComponentAligner rightAligner)
	{
		return buildLabeledTextField
		(
			key,
			document,
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a new <code>JTextField</code> with the given document.
	 *
	 * @param document The text field's document
	 * @param columns The number of columns to use to calculate the preferred
	 * width >= 0; if <code>columns</code> is set to zero, the preferred width
	 * will be whatever naturally results from the component implementation
	 * @return A new <code>JTextField</code>
	 */
	public static JTextField buildTextField(Document document, int columns)
	{
		JTextField textField = new JTextField(document, null, columns);
		return textField;
	}

	/**
	 * Creates a new <code>JToggleButton</code> and sets the text, mnemonic and
	 * mnemonic index using the given resource key.
	 *
	 * @param key The key used to retrieve the localized text
	 * @param repository The repository used to retrieve the localized string
	 * @return A new button
	 */
	public static JToggleButton buildToggleButton(String key,
	                                              ResourceRepository repository)
	{
		JToggleButton button = new JToggleButton();
		button.setName(key);
		setTextFor(button, key, repository);
		return button;
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param labelKey The key used to retrieve the localized string
	 * @param comboBoxModel The model of the combo box
	 * @param cellRenderer The <code>ListCellRenderer</code> used to decorate all
	 * the items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledComboBox(String labelKey,
	                                                 ComboBoxModel model,
	                                                 CellRendererAdapter cellRenderer,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			labelKey,
			model,
			cellRenderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledComboBox(String key,
	                                                 ComboBoxModel model,
	                                                 JComponent rightComponent,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledComboBox(String key,
	                                                 ComboBoxModel model,
	                                                 ListCellRenderer renderer,
	                                                 JComponent rightComponent,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param labelKey The key used to retrieve the localized string
	 * @param comboBoxModel The model of the combo box
	 * @param cellRenderer The <code>ListCellRenderer</code> used to decorate all
	 * the items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledComboBox(String labelKey,
	                                                 ComboBoxModel model,
	                                                 ListCellRenderer cellRenderer,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			labelKey,
			model,
			cellRenderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledComboBox(String key,
	                                                 ComboBoxModel model,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                                 ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label, a center component and a right
	 * component, the right component is usually a button or a spacer.
	 *
	 * @param key The key used to retrieve the localized string for the label
	 * @param centerComponent The component that is labeled and that is centered
	 * in the returned container, it takes all the horizontal space. For lists
	 * and trees, the component will also expand vertically
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and the chooser
	 */
	public static JComponent buildTopLabeledComponent(JComponent leftComponent,
	                                                  JComponent centerComponent,
	                                                  JComponent rightComponent,
	                                                  ResourceRepository repository,
	                                                  ComponentAligner leftAligner,
	                                                  ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			leftComponent,
			centerComponent,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true
		);
	}

	/**
	 * Creates a container where a label and the given component are laid out
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @param rightComponent
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildTopLabeledComponent(String key,
	                                                  JComponent component,
	                                                  JComponent rightComponent,
	                                                  ResourceRepository repository,
	                                                  ComponentAligner leftAligner,
	                                                  ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			key,
			component,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true
		);
	}

	/**
	 * Creates a container where a label and the given component are laid out
	 *
	 * @param key The key used to retrieve the localized string
	 * @param component The component that is labeled
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildTopLabeledComponent(String key,
	                                                  JComponent component,
	                                                  ResourceRepository repository,
	                                                  ComponentAligner leftAligner,
	                                                  ComponentAligner rightAligner)
	{
		return buildLabeledComponent
		(
			key,
			component,
			new Spacer(),
			repository,
			leftAligner,
			rightAligner,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ListCellRenderer renderer,
	                                                         BidiStringConverter editorValueConverter,
	                                                         Object nullValue,
	                                                         JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ListCellRenderer renderer,
	                                                         BidiStringConverter editorValueConverter,
	                                                         Object nullValue,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ListCellRenderer renderer,
	                                                         BidiStringConverter editorValueConverter,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ListCellRenderer renderer,
	                                                         Object nullValue,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ListCellRenderer renderer,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         Object nullValue,
	                                                         JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         Object nullValue,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ResourceRepository repository,
		                                                      ComponentAligner leftAligner,
		                                                      ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         ListCellRenderer renderer,
	                                                         BidiStringConverter editorValueConverter,
	                                                         Object nullValue,
	                                                         JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         ListCellRenderer renderer,
	                                                         BidiStringConverter editorValueConverter,
	                                                         Object nullValue,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueModel,
	                                                         ListCellRenderer renderer,
	                                                         BidiStringConverter editorValueConverter,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueModel,
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         ListCellRenderer renderer,
	                                                         Object nullValue,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         ListCellRenderer renderer,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValue,
	                                                         Object nullValue,
	                                                         JComponent rightComponent,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValue,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         Object nullValue,
	                                                         ResourceRepository repository,
	                                                         ComponentAligner leftAligner,
	                                                         ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableComboBox(String key,
	                                                         ComboBoxModel model,
	                                                         ValueModel subjectHolder,
	                                                         PropertyValueModel valueHolder,
	                                                         ValueModel defaultValueHolder,
	                                                         ResourceRepository repository,
		                                                      ComponentAligner leftAligner,
		                                                      ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			false
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            JComponent rightComponent,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ListCellRenderer renderer,
	                                                            BidiStringConverter editorValueConverter,
	                                                            Object nullValue,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ListCellRenderer renderer,
	                                                            BidiStringConverter editorValueConverter,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ListCellRenderer renderer,
	                                                            Object nullValue,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ListCellRenderer renderer,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            Object nullValue,
	                                                            JComponent rightComponent,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            Object nullValue,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			NullPropertyValueModel.instance(),
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            JComponent rightComponent,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            ListCellRenderer renderer,
	                                                            BidiStringConverter editorValueConverter,
	                                                            Object nullValue,
	                                                            JComponent rightComponent,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            ListCellRenderer renderer,
	                                                            BidiStringConverter editorValueConverter,
	                                                            Object nullValue,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			editorValueConverter,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param editorValueConverter
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueModel,
	                                                            ListCellRenderer renderer,
	                                                            BidiStringConverter editorValueConverter,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueModel,
			renderer,
			editorValueConverter,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            ListCellRenderer renderer,
	                                                            Object nullValue,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            ListCellRenderer renderer,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			renderer,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValue,
	                                                            Object nullValue,
	                                                            JComponent rightComponent,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValue,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param nullValue
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            Object nullValue,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			nullValue,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledEditableListChooser(String key,
	                                                            ComboBoxModel model,
	                                                            ValueModel subjectHolder,
	                                                            PropertyValueModel valueHolder,
	                                                            ValueModel defaultValueHolder,
	                                                            ResourceRepository repository,
	                                                            ComponentAligner leftAligner,
	                                                            ComponentAligner rightAligner)
	{
		return buildLabeledEditableComboBoxImp
		(
			key,
			model,
			subjectHolder,
			valueHolder,
			defaultValueHolder,
			(CellRendererAdapter) null,
			BidiStringConverter.DEFAULT_INSTANCE,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledListChooser(String key,
	                                                    ComboBoxModel model,
	                                                    JComponent rightComponent,
	                                                    ResourceRepository repository,
	                                                    ComponentAligner leftAligner,
	                                                    ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param renderer The <code>ListCellRenderer</code> used to decorate all the
	 * items in the model
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed, <code>null</code> can
	 * also be passed so that no filler will be added by default
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledListChooser(String key,
	                                                    ComboBoxModel model,
	                                                    ListCellRenderer renderer,
	                                                    JComponent rightComponent,
	                                                    ResourceRepository repository,
	                                                    ComponentAligner leftAligner,
	                                                    ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			renderer,
			null,
			rightComponent,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param labelKey The key used to retrieve the localized string
	 * @param comboBoxModel The model of the combo box
	 * @param cellRenderer The <code>ListCellRenderer</code> used to decorate all
	 * the items in the model
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledListChooser(String labelKey,
	                                                    ComboBoxModel model,
	                                                    ListCellRenderer cellRenderer,
	                                                    ResourceRepository repository,
	                                                    ComponentAligner leftAligner,
	                                                    ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			labelKey,
			model,
			cellRenderer,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a combo box.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param model The model of the combo box
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a combo box
	 */
	public static JComponent buildTopLabeledListChooser(String key,
	                                                    ComboBoxModel model,
	                                                    ResourceRepository repository,
	                                                    ComponentAligner leftAligner,
	                                                    ComponentAligner rightAligner)
	{
		return buildLabeledComboBoxImp
		(
			key,
			model,
			(CellRendererAdapter) null,
			null,
			null,
			repository,
			leftAligner,
			rightAligner,
			true,
			true
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildTopLabeledNonEditableTextField(String key,
	                                                             Document document,
	                                                             JComponent rightComponent,
	                                                             ResourceRepository repository,
	                                                             ComponentAligner leftAligner,
	                                                             ComponentAligner rightAligner)
	{
		JTextField textField = buildTextField(document);
		textField.setEditable(false);
	
		return buildTopLabeledComponent
		(
			key,
			textField,
			rightComponent,
			repository,
			leftAligner,
			rightAligner
		);
	}

	public static JComponent buildTopLabeledTextArea(String key,
	                                                 Document document,
	                                                 ResourceRepository repository,
	                                                 ComponentAligner leftAligner,
	                                             	 ComponentAligner rightAligner)
	{
		return buildTopLabeledComponent
		(
			key,
			buildTextArea(document),
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	public static JComponent buildTopLabeledTextField(String key,
	                                                  Document document,
	                                                  int columns,
	                                                  ResourceRepository repository,
	                                                  ComponentAligner leftAligner,
	                                             	  ComponentAligner rightAligner)
	{
		return buildTopLabeledComponent
		(
			key,
			buildTextField(document, columns),
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param rightComponent A component to be added to the right of the labeled
	 * component, usually a browse button would be passed
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildTopLabeledTextField(String key,
	                                                  Document document,
	                                                  JComponent rightComponent,
	                                                  ResourceRepository repository,
	                                                  ComponentAligner leftAligner,
	                                             	  ComponentAligner rightAligner)
	{
		return buildTopLabeledComponent
		(
			key,
			buildTextField(document),
			rightComponent,
			repository,
			leftAligner,
			rightAligner
		);
	}

	/**
	 * Creates a container that has a label and a text field.
	 *
	 * @param key The key used to retrieve the localized string
	 * @param document The document of the text field
	 * @param repository The repository used to retrieve the localized string
	 * @param leftAligner The label will be added to this <code>ComponentAligner</code>
	 * @param rightAligner The right component will be added to this
	 * <code>ComponentAligner</code>
	 * @return A new component containing a label and a text field
	 */
	public static JComponent buildTopLabeledTextField(String key,
	                                                  Document document,
	                                                  ResourceRepository repository,
	                                                  ComponentAligner leftAligner,
	                                                  ComponentAligner rightAligner)
	{
		return buildTopLabeledTextField
		(
			key,
			document,
			new Spacer(),
			repository,
			leftAligner,
			rightAligner
		);
	}

	public static JTextArea buildTextArea(Document document)
	{
		TextArea textArea = new TextArea(document);
		return textArea;
	}

	/**
	 * Creates a new <code>TriStateCheckBox</code>, which supports three states
	 * for the selection: selected, partially selected and unselected.
	 *
	 * @param key The key used to retrieve the localized text and the mnemonic
	 * @param model The model storing the selection state
	 * @param repository The <code>ResourceRepository</code> containing the
	 * localized text
	 */
	public static TriStateCheckBox buildTriStateCheckBox(String key,
	                                                     TriStateButtonModel model,
	                                                     ResourceRepository repository)
	{
		TriStateCheckBox checkBox = new TriStateCheckBox();
		checkBox.setModel(model);
		checkBox.setOpaque(false);
		setTextFor(checkBox, key, repository);
		return checkBox;
	}

}
