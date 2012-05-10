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
package org.eclipse.persistence.tools.workbench.framework.uitools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.persistence.tools.workbench.framework.resources.DefaultResourceRepository;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ListModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.AdaptableListCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;
import org.eclipse.persistence.tools.workbench.uitools.swing.CompositeIcon;


/**
 * A <code>CheckList</code> is a regular list that adds the capability to
 * select/deselect items in the list. When using a mouse, a click on the check
 * icon will toggle the selection. The selection can also be changed using the
 * space bar.
 * <p>
 * The selected items are stored into the <code>ListSelectionModel</code> passed
 * to this list.
 * <p>
 * Here the default layout:
 * <pre>
 * _______________________
 * |                   |^|
 * | x Item1           | |
 * | o Item2           |||
 * | x Item3           |||
 * |   ...             | |
 * |                   |v|
 * -----------------------</pre>
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class CheckList extends AccessibleTitledPanel
{
	/**
	 * This label is used to tell JAWS the check selection has changed, this
	 * seems to be the only way JAWS knows when the selection has changed.
	 */
	JLabel accessibleLabel;

	/**
	 * Holds the list so we can give it the focus.
	 */
	EditableCheckList listBox;

	/**
	 * The model used to store the selected items.
	 */
	ListSelectionModel selectionModel;

	/**
	 * Creates a new <code>CheckList</code>.
	 */
	private CheckList()
	{
		super(new BorderLayout());
	}

	/**
	 * Creates a new <code>CheckList</code> and uses a default {@link LabelDecorator}.
	 * 
	 * @param itemHolder The holder of the items to be shown in the list
	 * @param selectionModel The model where the selected items are stored
	 */
	public CheckList(CollectionValueModel itemHolder,
						  ListSelectionModel selectionModel)
	{
		this(itemHolder, selectionModel, CellRendererAdapter.DEFAULT_CELL_RENDERER_ADAPTER);
	}

	/**
	 * Creates a new <code>CheckList</code>.
	 * 
	 * @param itemHolder The holder of the items to be shown in the list
	 * @param selectionModel The model where the selected items are stored
	 * @param labelDecorator The {@link LabelDecorator} used to decorate the
	 * items contained in the given collection holder
	 */
	public CheckList(CollectionValueModel itemHolder,
						  ListSelectionModel selectionModel,
						  CellRendererAdapter labelDecorator)
	{
		this(new CollectionListValueModelAdapter(itemHolder),
			  selectionModel,
			  labelDecorator);
	}

	/**
	 * Creates a new <code>CheckList</code> and uses a default {@link LabelDecorator}.
	 * 
	 * @param itemHolder The holder of the items to be shown in the list
	 * @param selectionModel The model where the selected items are stored
	 */
	public CheckList(ListValueModel itemHolder,
						  ListSelectionModel selectionModel)
	{
		this(itemHolder, selectionModel, CellRendererAdapter.DEFAULT_CELL_RENDERER_ADAPTER);
	}

	/**
	 * Creates a new <code>CheckList</code>.
	 * 
	 * @param itemHolder The holder of the items to be shown in the list
	 * @param selectionModel The model where the selected items are stored
	 * @param labelDecorator The {@link LabelDecorator} used to decorate the
	 * items contained in the given collection holder
	 */
	public CheckList(ListValueModel itemHolder,
						  ListSelectionModel selectionModel,
						  CellRendererAdapter labelDecorator)
	{
		this();
		initialize(itemHolder, selectionModel, labelDecorator);
	}

	/**
	 * Creates a <code>FocusListener</code> that will ask the list to repaint
	 * itself in order to update the renders. Unfortunately, this is not done
	 * automatically by Swing.
	 *
	 * @return A new <code>FocusListener</code>
	 */
	private FocusListener buildFocusListener()
	{
		return new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				CheckList.this.listBox.repaint();
			}

			public void focusLost(FocusEvent e)
			{
				CheckList.this.listBox.repaint();
			}
		};
	}

	/**
	 * Creates a new <code>ListModel</code> which will keep the listener up to
	 * date with the changes made to the given <code>ListValueModel</code>.
	 * 
	 * @param itemHolder The holder of the items to be shown in the list
	 * @return A new <code>ListModel</code>
	 */
	private ListModel buildListModelAdapter(ListValueModel itemHolder)
	{
		return new ListModelAdapter(itemHolder);
	}

	/**
	 * Creates the listener reponsible to keep the UI in sync with the selection
	 * model.
	 *
	 * @return A new <code>ListSelectionListener</code>
	 */
	private ListSelectionListener buildSelectionModelListener()
	{
		return new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting())
					return;

				for (int index = e.getFirstIndex(); index <= e.getLastIndex(); index++)
				{
					repaintCell(index);
				}
			}
		};
	}

	/**
	 * Initializes this <code>CheckList</code>.
	 * 
	 * @param itemHolder The holder of the items to be shown in the list
	 * @param selectionModel The model where the selected items are stored
	 * @param cellRendererAdapter The {@link CellRendererAdapter} used to
	 * decorate the items contained in the given collection holder
	 * @exception NullPointerException The item holder, the selectionModel or the
	 * <code>CellRendererAdapter</code> can't be <code>null</code>
	 */
	protected void initialize(ListValueModel itemHolder,
									  ListSelectionModel listSelectionModel,
									  CellRendererAdapter cellRendererAdapter)
	{
		if ((itemHolder == null) || (listSelectionModel == null) || (cellRendererAdapter == null))
			throw new NullPointerException("ListValueModel, ListSelectionModel, CellRendererAdapter cannot be null");

		this.selectionModel = listSelectionModel;
		listSelectionModel.addListSelectionListener(buildSelectionModelListener());

		// Create the list
		this.listBox = new EditableCheckList(buildListModelAdapter(itemHolder));
		this.listBox.addFocusListener(buildFocusListener());
		this.listBox.setCellRenderer(new CheckBoxCellRenderer(cellRendererAdapter));
		add(new JScrollPane(this.listBox), BorderLayout.CENTER);

		installEditing();

		// This status bar pane is required to tell JAWS when the check selection
		// is changed, this seems to be the only way to make it work
		add(new StatusBarPane(), BorderLayout.PAGE_START);
	}

	/**
	 * Installs the necessary handlers on the given list to mimic an real cell
	 * editor.
	 */
	private void installEditing()
	{
		// Install a mouse listener which will select/unselect when clicking on
		// the check icon
		MouseHandler mouseHandler = new MouseHandler();
		this.listBox.addMouseListener(mouseHandler);
		this.listBox.addMouseMotionListener(mouseHandler);

		// Map the space bar to the key in the ActionMap
		InputMap inputMap = (InputMap) UIManager.get("List.focusInputMap");
		inputMap.put(KeyStroke.getKeyStroke("SPACE"),          "pressed");
		inputMap.put(KeyStroke.getKeyStroke("released SPACE"), "released");

		// Map the actions
		ActionMap actionMap = this.listBox.getActionMap();
		actionMap.put("pressed",      new PressedAction());
		actionMap.put("released",     new ReleasedAction());
	}

   /**
	 * Determines whether the <code>AccessibleContext</code> of this
	 * <code>CheckList</code> has been created or not.
	 * 
	 * @return <code>true<code> if {@link accessibleContext} is not <code>null</code>,
	 * otherwise <code>false<code>
	 */
	boolean isAccessibleContextInitialized()
	{
		return (this.accessibleContext != null);
	}

	/**
	 * Repaints the list by optimizing its painting by only painting the cell
	 * located at the given index.
	 *
	 * @param index The index of the cell to repaint
	 */
	void repaintCell(int index)
	{
		Rectangle bounds = this.listBox.getCellBounds(index, index);
		this.listBox.repaint(bounds);
	}

	/**
	 * Redirects the focus selection to the actual component of this widget that
	 * can really accept the focus, which is the list itself.
	 */
	public void requestFocus()
	{
		this.listBox.requestFocus();
	}

	/**
	 * Redirects the focus selection to the actual component of this widget that
	 * can really accept the focus, which is the list itself.
	 */
	public boolean requestFocus(boolean temporary)
	{
		return this.listBox.requestFocus(temporary);
	}

	/**
	 * Redirects the focus selection to the actual component of this widget that
	 * can really accept the focus, which is the list itself.
	 */
	public boolean requestFocusInWindow()
	{
		return this.listBox.requestFocusInWindow();
	}

	/**
	 * Redirects the focus selection to the actual component of this widget that
	 * can really accept the focus, which is the list itself.
	 */
	protected boolean requestFocusInWindow(boolean temporary)
	{
		return this.listBox.requestFocusInWindow(temporary);
	}

	/**
	 * Updates the active descendant of the list in order for a screen reader to
	 * be notified of the change.
	 *
	 * @param index The index of the item to be either selected or unselected,
	 * the index has to be a valid index
	 */
	private void updateActiveDescendant(int index)
	{
		if (this.accessibleContext != null)
		{
			AccessibleContext accessible = this.listBox.getAccessibleContext();
			Accessible accessibleChild = accessible.getAccessibleChild(index);
			this.accessibleLabel.setText(accessibleChild.getAccessibleContext().getAccessibleName());
		}
	}

	/**
	 * Updates the interval of selected items in the {@link #selectionModel}
	 * based on the given index. If the item is selected, then it will become
	 * unselected and vice versa.
	 *
	 * @param index The index of the item to be either selected or unselected,
	 * the index has to be a valid index
	 */
	void updateSelection(int index)
	{
		updateSelection(new int[] { index });
	}

	/**
	 * Updates the selected items in the {@link #selectionModel} based on the
	 * given list of indices. If the item is selected, then it will become
	 * unselected and vice versa.
	 *
	 * @param indices The array of indices of new unselected/selected items,
	 * the indices have to be valid
	 */
	void updateSelection(int[] indices)
	{
		// Retrieve the count of items that are already selected
		// in the given list of indices
		int selectedCount = 0;

		for (int index = indices.length; --index >= 0;)
		{
			if (this.selectionModel.isSelectedIndex(indices[index])) {
				selectedCount++;
			}
		}

		// Determine the action to perform: select all or unselect all
		boolean select = (indices.length != selectedCount);

		// Update the selection interval
		for (int index = 0; index < indices.length; index++)
		{
			int anIndex = indices[index];

			if (select)
				this.selectionModel.addSelectionInterval(anIndex, anIndex);
			else
				this.selectionModel.removeSelectionInterval(anIndex, anIndex);
		}

		// Update the accessible list's children
		for (int index = 0; index < indices.length; index++)
		{
			updateActiveDescendant(indices[index]);
		}
	}

	/**
	 * This check box is used by {@link CheckList.ListCellRenderer} to render
	 * items as a checked/unchecked item.
	 */
	private class CheckBox extends JCheckBox
	{
		/**
		 * Creates a new <code>CheckBox</code>.
		 */
		private CheckBox()
		{
			super();
			initialize();
		}

		/**
		 * Initializes the UI of this check box to be properly rendered in a list.
		 */
		private void initialize()
		{
			setRolloverEnabled(true);
			setBorderPainted(true);
		}

		/**
		 * Sets the default check box icon, which does not override the check icon.
		 *
		 * @param icon The new default icon or <code>null</code> to remove the old
		 * icon
		 */
		public void setIcon(Icon icon)
		{
			if (UIManager.getLookAndFeel().getID().equals("GTK"))
				return;

			if (icon != null)
			{
				super.setIcon(new CompositeIcon(UIManager.getIcon("CheckBox.icon"), 3, icon));
			}
			else
			{
				super.setIcon(UIManager.getIcon("CheckBox.icon"));
			}
		}
	}

	/**
	 * This <code>ListCellRenderer</code> is responsible to decorate values from
	 * the list model using a check box.
	 */
	private class CheckBoxCellRenderer extends AdaptableListCellRenderer
	{
		/**
		 * Specified the index of the item that should be shown as armed, which
		 * means the mouse has been pressed on the item but not released yet.
		 */
		int armedIndex;

		/**
		 * Specified the index of the item that should be shown as pressed, which
		 * means the mouse has been pressed on the item but not released yet.
		 */
		int pressedIndex;

		/**
		 * Specified the index of the item that should be shown that the mouse is
		 * over it.
		 */
		int rolloverIndex;

		/**
		 * Creates a new <code>ListCellRenderer</code>.
		 *
		 * @param cellRenderer The {@link CellRendererAdapter} used to decorate
		 * the items of a list
		 */
		CheckBoxCellRenderer(CellRendererAdapter adapter)
		{
			super(adapter);
			this.pressedIndex = -1;
			this.armedIndex = -1;
			this.rolloverIndex = -1;
		}

		/**
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list,
																	 Object value,
																	 int index,
																	 boolean selected,
																	 boolean cellHasFocus)
		{
			// Make sure the AccessibleContext is initialized so that the
			// accessible name can be updated
			if (CheckList.this.isAccessibleContextInitialized())
			{
				this.getAccessibleContext();
			}

			super.getListCellRendererComponent(list, value, index, selected, cellHasFocus);

			CheckBox checkBox = new CheckBox();
			updateCheckBoxUI(checkBox, selected);
			updateCheckBoxVisual(checkBox);
			updateCheckBoxButtonModel(checkBox, index);

			return checkBox;
		}

		/**
		 * Updates the states of the check label's button model based on the
		 * selection state of the item rendered.
		 *
		 * @param checkBox The actual component returned by this
		 * <code>ListCellRenderer</code>.
		 * @param index The index of the item to be rendered
		 */
		private void updateCheckBoxButtonModel(CheckBox checkBox, int index)
		{
			ButtonModel model = checkBox.getModel();
			model.setPressed(this.pressedIndex == index);
			model.setArmed(this.armedIndex == index);
			model.setRollover(this.rolloverIndex == index);
			checkBox.setSelected(CheckList.this.selectionModel.isSelectedIndex(index)); // This needs to be done after setPressed
		}

		/**
		 * Updates the UI part of the check label.
		 *
		 * @param checkBox The actual component returned by this
		 * <code>ListCellRenderer</code>.
		 * @param selected Specifies whether the item rendered is selected or not
		 */
		private void updateCheckBoxUI(CheckBox checkBox, boolean selected)
		{
			checkBox.setHorizontalAlignment(getHorizontalAlignment());
			checkBox.setHorizontalTextPosition(getHorizontalTextPosition());
			checkBox.setVerticalAlignment(getVerticalAlignment());
			checkBox.setVerticalTextPosition(getVerticalTextPosition());
			checkBox.setComponentOrientation(getComponentOrientation());
			checkBox.setFont(getFont());
			checkBox.setEnabled(isEnabled());
			checkBox.setOpaque(isOpaque());
			checkBox.setBorder(getBorder());

			if (!CheckList.this.listBox.hasFocus() && selected)
			{
				checkBox.setForeground(CheckList.this.listBox.getForeground());
				checkBox.setBackground(UIManager.getColor("Panel.background"));
			}
			else
			{
				checkBox.setForeground(getForeground());
				checkBox.setBackground(getBackground());
			}
		}

		/**
		 * Updates the icon and text of the check label.
		 *
		 * @param checkBox The actual component returned by this
		 * <code>ListCellRenderer</code>.
		 */
		private void updateCheckBoxVisual(CheckBox checkBox)
		{
			checkBox.setIcon(getIcon());
			checkBox.setText(getText());

			// Only update the accessible name if a screen reader is running
			if (isAccessibleContextInitialized())
			{
				checkBox.getAccessibleContext().setAccessibleName(this.accessibleContext.getAccessibleName());
			}
		}
	}

	/**
	 * This customized <code>JList</code> upgrade the accessiblity to support
	 * checked items.
	 */
	private class EditableCheckList extends SwingComponentFactory.AccessibleList
	{
		/**
		 * Creates a new <code>EditableCheckList</code>.
		 *
		 * @param model The <code>ListModel</code> containing the items of the list
		 */
		EditableCheckList(ListModel model)
		{
			super(model);
		}

		/**
		 * Returns the <code>AccessibleContext</code> associated with this
		 * <code>EditableCheckList</code>.
		 * 
		 * @return An <code>AccessibleEditableCheckList</code> that serves as the
		 * <code>AccessibleContext</code> of this <code>EditableCheckList</code>
		 */
		public AccessibleContext getAccessibleContext()
		{
			if (this.accessibleContext == null) {
				this.accessibleContext = new AccessibleEditableCheckList();
			}

			return this.accessibleContext;
		}

		public boolean requestFocusInWindow(boolean temporary)
		{
			return super.requestFocusInWindow(temporary);
		}

		/**
		 * The <code>AccessibleContext</code> for this <code>EditableCheckList</code>.
		 */
		protected class AccessibleEditableCheckList extends AccessibleAccessibleList
																  implements ListSelectionListener
		{
			String ACCESSIBLE_CHECKBOX_CHECKED;
			String ACCESSIBLE_CHECKBOX_NOT_CHECKED;

			protected AccessibleEditableCheckList()
			{
				super();
				initialize();
			}

			public Accessible getAccessibleAt(Point location)
			{
				int index = locationToIndex(location);
				return getAccessibleChild(index);
			}

			public Accessible getAccessibleChild(int index)
			{
            if ((index < 0) || (index >= getModel().getSize()))
					return null;

            return new AccessibleEditableCheckListChild(EditableCheckList.this, index);
			}

			protected void initialize()
			{
				DefaultResourceRepository repository = new DefaultResourceRepository(UIToolsResourceBundle.class);

				this.ACCESSIBLE_CHECKBOX_CHECKED     = " " + repository.getString("ACCESSIBLE_CHECKLIST_CHECKBOX_CHECKED");
				this.ACCESSIBLE_CHECKBOX_NOT_CHECKED = " " + repository.getString("ACCESSIBLE_CHECKLIST_CHECKBOX_NOT_CHECKED");
			}

			protected class AccessibleEditableCheckListChild extends AccessibleJListChild
			{
				private final int index;

				protected AccessibleEditableCheckListChild(JList list, int index)
				{
					super(list, index);
					this.index = index;
				}

				public String getAccessibleName()
				{
					String name = super.getAccessibleName();

					if (CheckList.this.selectionModel.isSelectedIndex(this.index)) {
						name += AccessibleEditableCheckList.this.ACCESSIBLE_CHECKBOX_CHECKED;
					} else {
						name += AccessibleEditableCheckList.this.ACCESSIBLE_CHECKBOX_NOT_CHECKED;
					}
					return name;
				}
			}
		}
	}

	/**
	 * This handler is responsible to update the check icon displayed on screen.
	 * Certain look and feel show a different state of the check icon based on
	 * rollover, pressed, etc. This class also update the selection on mouse
	 * clicked.
	 */
	private class MouseHandler extends MouseAdapter
										implements MouseMotionListener
	{
		private int currentPressedIndex = -1;

		private int locationToIndex(Point location)
		{
			int iconWidth = SwingTools.checkBoxIconWidth();
			int index = CheckList.this.listBox.locationToIndex(location);
			Rectangle bounds = CheckList.this.listBox.getCellBounds(index, index);

			if (!CheckList.this.listBox.getComponentOrientation().isLeftToRight())
			{
				bounds.x += (bounds.width - iconWidth);
			}

			bounds.width = iconWidth;

			return bounds.contains(location.x, location.y) ? index : -1;
		}

		public void mouseDragged(MouseEvent e)
		{
			mouseMoved(e);
		}

		public void mouseExited(MouseEvent e)
		{
			CheckBoxCellRenderer renderer = (CheckBoxCellRenderer) CheckList.this.listBox.getCellRenderer();
			int oldRolloverIndex = renderer.rolloverIndex;

			if (oldRolloverIndex != -1)
			{
				renderer.armedIndex    = -1;
				renderer.rolloverIndex = -1;

				repaintCell(oldRolloverIndex);
			}
		}

		public void mouseMoved(MouseEvent e)
		{
			if (CheckList.this.listBox.getModel().getSize() == 0) {
				return;
			}

			Point location = e.getPoint();

			// Get the index of the item where the mouse is
			int index = CheckList.this.listBox.locationToIndex(location);

			// Get the cell bounds of the cell over
			Rectangle bounds = CheckList.this.listBox.getCellBounds(index, index);

			// The mouse is not moved on an item
			if (!bounds.contains(location))
				index = -1;

			CheckBoxCellRenderer renderer = (CheckBoxCellRenderer) CheckList.this.listBox.getCellRenderer();
			int oldRolloverIndex = renderer.rolloverIndex;

			// Nothing to repaint
			if (oldRolloverIndex == index)
				return;

			// Update the properties
			renderer.rolloverIndex = index;
			renderer.armedIndex    = (index == this.currentPressedIndex) ? index : -1;

			// Paint the new rollover cell
			if (index != -1)
			{
				repaintCell(index);
			}

			// Repaint the old rollover cell
			if (oldRolloverIndex != -1)
			{
				repaintCell(oldRolloverIndex);
			}
		}

		public void mousePressed(MouseEvent e)
		{
			if (!SwingUtilities.isLeftMouseButton(e))
				return;

			int index = locationToIndex(e.getPoint());

			if (index != -1)
			{
				CheckBoxCellRenderer renderer = (CheckBoxCellRenderer) CheckList.this.listBox.getCellRenderer();

				renderer.armedIndex   = index;
				renderer.pressedIndex = index;
				this.currentPressedIndex   = index;

				repaintCell(index);
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			if (!SwingUtilities.isLeftMouseButton(e))
				return;

			CheckBoxCellRenderer renderer = (CheckBoxCellRenderer) CheckList.this.listBox.getCellRenderer();

			// Get the index of the item where the mouse is
			Point location = e.getPoint();
			int newIndex = CheckList.this.listBox.locationToIndex(location);
			Rectangle bounds = CheckList.this.listBox.getCellBounds(newIndex, newIndex);

			// The mouse was released outside of an item
			if (!bounds.contains(location))
			{
				newIndex = -1;
			}

			// The mouse was released outside of the check icon or not on the item
			// where the mouse was pressed
			if ((this.currentPressedIndex != newIndex) ||
				 (locationToIndex(e.getPoint()) == -1))
			{
				this.currentPressedIndex = -1;
			}

			renderer.armedIndex    = this.currentPressedIndex;
			renderer.pressedIndex  = -1;
			renderer.rolloverIndex = newIndex;

			// Only change the selection if the mouse was released on the check icon
			if (this.currentPressedIndex != -1)
			{
				updateSelection(this.currentPressedIndex);
			}

			this.currentPressedIndex = -1;

			// The mouse is released on another cell, show it with rollover
			if (newIndex != -1)
			{
				repaintCell(newIndex);
			}
		}
	}

	/**
	 * This action is responsible to update the renderer by updating the pressed
	 * and armed indices.
	 */
	private class PressedAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			int[] indices = CheckList.this.listBox.getSelectedIndices();

			// Nothing to be updated
			if (indices.length != 1)
				return;

			// Update the renderer properties
			CheckBoxCellRenderer renderer = (CheckBoxCellRenderer) CheckList.this.listBox.getCellRenderer();
			renderer.armedIndex = indices[0];
			renderer.pressedIndex = indices[0];

			// Update the UI
			repaintCell(indices[0]);
		}
	}

	/**
	 * This action is responsible to update the renderer and the selection.
	 */
	private class ReleasedAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			int[] selectedIndices = CheckList.this.listBox.getSelectedIndices();

			if (selectedIndices.length > 0)
			{
				CheckBoxCellRenderer renderer = (CheckBoxCellRenderer) CheckList.this.listBox.getCellRenderer();
				renderer.armedIndex = -1;
				renderer.pressedIndex = -1;

				updateSelection(selectedIndices);
			}
		}
	}

	/**
	 * This panel takes care to support accessibility regarding the check
	 * selection change, which seems to not be supported through JList.
	 */
	private class StatusBarPane extends JPanel
	{
		public StatusBarPane()
		{
			super(new BorderLayout());
		}

		/**
		 * Creates the status bar that is required for JAWS to say the accessible
		 * label's text.
		 */
		private void createStatusBar()
		{
			CheckList.this.accessibleLabel = new AccessibleLabel();
			CheckList.this.accessibleLabel.setVisible(false);

			StatusBar statusBar = new StatusBar();
			statusBar.setVisible(false);
			statusBar.add(CheckList.this.accessibleLabel);

			add(statusBar, BorderLayout.CENTER);
			validate();
		}

		/**
		 * Returns the <code>AccessibleContext</code> associated with this
		 * <code>ContentPane</code>.
		 * 
		 * @return An <code>AccessibleContentPane</code> that serves as the
		 * <code>AccessibleContext</code> of this <code>ContentPane</code>
		 */
		public AccessibleContext getAccessibleContext()
		{
			if (this.accessibleContext == null)
			{
				this.accessibleContext = new AccessibleStatusBarPane();
				createStatusBar();
			}

			return this.accessibleContext;
		}

		/**
		 * This <code>JLabel</code> is intended to fire an event that will ask
		 * JAWS to read the description.
		 */
		private class AccessibleLabel extends JLabel
		{

			public void setText(String text)
			{
				String oldText = getText();
				super.setText(text);

				if (this.accessibleContext != null) {
					this.accessibleContext.firePropertyChange(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, oldText, text);
				}
			}
		}

		/**
		 * The <code>AccessibleContext</code> of this pane.
		 */
		protected class AccessibleStatusBarPane extends AccessibleJPanel
		{
			// nothing here...
		}

		/**
		 * This <code>StatusBar</code> is required for JAWS to read the
		 * accessible label when a new text has been set.
		 */
		private class StatusBar extends JPanel
		{
			public AccessibleContext getAccessibleContext()
			{
				if (this.accessibleContext == null) {
					this.accessibleContext = new AccessibleStatusBar();
				}

				return this.accessibleContext;
			}

			protected class AccessibleStatusBar extends AccessibleJPanel
			{
				public AccessibleRole getAccessibleRole()
				{
					return AccessibleRole.STATUS_BAR;
				}
			}
		}
	}
}
