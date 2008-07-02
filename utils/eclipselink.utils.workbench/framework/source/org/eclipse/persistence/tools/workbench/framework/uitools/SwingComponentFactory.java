/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
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
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.CheckBoxModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


public class SwingComponentFactory
{
	private static DoubleClickMouseHandler doubleClickMouseHandler = new DoubleClickMouseHandler();
	private static Border STANDARD_EMPTY_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

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
		// Do not use "new JSpinner(SpinButtonModel)", the value is not set in the
		// UI when model already contains it, but by going through
		// JSpinner.setModel(SpinButtonModel), then the value is set, the bug has
		// not yet been located
		JSpinner spinner = new JSpinner();

		// Generate a number with the proper number of columns
		char[] numbers = new char[columns];
		Arrays.fill(numbers, '5');
		Integer tempMaximum = new Integer(new String(numbers));

		// Because there is no "nicely" way to set the number of columns of the
		// JSpinner's editor, we set a temporary max value with the corresponding
		// number of columns
		Comparable maximum = model.getMaximum();
		model.setMaximum(tempMaximum);
		spinner.setModel(model);
		model.setMaximum(maximum);

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
	private static void setTextFor(AbstractButton button,
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
	private static void setTextFor(AbstractButton button, 
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
	private static void setTextFor(JLabel label,
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
	private static void setTextFor(JLabel label, String key, ResourceRepository resourceRepository)
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
}
