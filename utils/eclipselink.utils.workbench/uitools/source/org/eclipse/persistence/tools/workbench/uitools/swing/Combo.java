/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Component;
import java.awt.Dimension;
import javax.accessibility.AccessibleContext;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.ComboPopup;

import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;

/**
 * This extension over the Swing <code>JComboBox</code> simply adds the ease of
 * using a <code>CellRendererAdapter</code> since it is not recommended to use
 * a sub-class of <code>DefaultListCellRenderer</code>. The installed UI
 * delegate can customize the default implementation for better look on the
 * operating system.
 *
 * @see Combo
 * @see CellRendererAdapter
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class Combo extends JComboBox
{
	/**
	 * Keep a reference to the <code>CellRendererAdapter</code> in order to
	 * re-install it if the look and feel changes.
	 */
	private CellRendererAdapter cellRendererAdapter;

	/**
	 * An object that will be used as the prototype display value of this combo,
	 * which will speed up the calculation of its size.
	 */
	public static final Component PROTOTYPE_DISPLAY_VALUE = buildPrototypeDisplayValue();

	/**
	 * Creates a new <code>Combo</code>.
	 */
	public Combo()
	{
		super();
	}

	/**
	 * Creates a new <code>Combo</code>.
	 *
	 * @param model The model containing the items and the selected item
	 */
	public Combo(ComboBoxModel model)
	{
		super(model);
	}

	/**
	 * Creates a simple renderer that will be used to calculate the default
	 * height of a cell. Since the only way items are rendered is by using the
	 * default renderer and formatting it with a text and icon. This will speed
	 * up the calculation of the combo's height and its popup size.
	 *
	 * @return The component used as the prototype display value
	 */
	private static Component buildPrototypeDisplayValue()
	{
		String item = "item";

		JComboBox comboBox = new JComboBox();
		comboBox.addItem(item);

		ComboPopup comboPopup = (ComboPopup) comboBox.getUI().getAccessibleChild(comboBox, 0);

		ListCellRenderer cellRenderer = comboBox.getRenderer();
		JLabel label = (JLabel) cellRenderer.getListCellRendererComponent(comboPopup.getList(), item, 0, true, true);
		label.setIcon(EmptyIcon.SMALL_ICON);

		return label;
	}

	@SuppressWarnings("unchecked")
	private ListCellRenderer buildCellRenderer(CellRendererAdapter cellRendererAdapter)
	{
		return new ComboCellRenderer
		(
			getRenderer(),
			(CellRendererAdapter) cellRendererAdapter,
			PROTOTYPE_DISPLAY_VALUE
		);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureEditor(ComboBoxEditor editor, Object item)
	{
		// This should be done automatically by changing the text field's document
		// and using a DocumentAdapter, this will help to update the underlying
		// model right away
	}

	/**
	 * Fixes the size of this combo, which can be done when the model data is
	 * fixed. The UI delegate uses the cell renderer to calculate the width by
	 * using the widest item.
	 */
	public final void fixSize()
	{
		// Make sure there is no prototype display value to ensure the
		Object oldPrototypeDisplayValue = getPrototypeDisplayValue();
		setPrototypeDisplayValue(null);

		// Add some extra space to the calculated width and
		// make it is at least 100 pixels
		Dimension size = getPreferredSize();
		size.width = Math.max(size.width + 20, 100);
		setPreferredSize(size);

		setPrototypeDisplayValue(oldPrototypeDisplayValue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccessibleContext getAccessibleContext()
	{
		if (accessibleContext == null)
		{
			accessibleContext = new AccessibleCombo();
		}

		return accessibleContext;
	}

	/**
	 * Retrieves from the UI delegate the list used in the combo popup.
	 *
	 * @return The popup list
	 */
	final JList popupList()
	{
		ComboPopup comboPopup = (ComboPopup) getAccessibleContext().getAccessibleChild(0);
		return comboPopup.getList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEnabled(boolean enabled)
	{
		popupList().setEnabled(enabled);
		super.setEnabled(enabled);
	}

	/**
	 * Installs a <code>ListCellRenderer</code> that will use the given
	 * <code>CellRendererAdapter</code> but still use the <code>ListCellRenderer</code>
	 * defined by the look and feel for retrieving the <code>Component</code>
	 * and formatting its properties (colors, font, etc).
	 *
	 * @param cellRendererAdapter The adapter used to format the items and show a
	 * representative description (text, icon, tooltip, accessible name)
	 */
	public final void setRendererAdapter(CellRendererAdapter cellRendererAdapter)
	{
		if (this.cellRendererAdapter != cellRendererAdapter)
		{
			this.cellRendererAdapter = cellRendererAdapter;

			// Update the renderer in order to use the CellRendererAdapter
			setPrototypeDisplayValue(PROTOTYPE_DISPLAY_VALUE);
			setRenderer(buildCellRenderer(cellRendererAdapter));

			// The CellRendererAdapter changed and the preferred size was set,
			// then we update it
			if (isPreferredSizeSet())
			{
				fixSize();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateUI()
	{
		super.updateUI();

		// Re-set the ListCellRenderer in order to use the CellRendererAdapter
		if (cellRendererAdapter != null)
		{
			setRenderer(buildCellRenderer(cellRendererAdapter));
		}
	}

	/**
	 * The <code>AccessibleContext</code> of this combo.
	 */
	protected class AccessibleCombo extends AccessibleJComboBox
	{
	}
}