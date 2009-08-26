/*
 * Copyright (c) 2008, Oracle. All rights reserved.
 *
 * This software is the proprietary information of Oracle Corporation.
 * Use is subject to license terms.
 */
package org.eclipse.persistence.tools.workbench.uitools.swing;

import java.awt.Component;
import java.awt.Image;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

import org.eclipse.persistence.tools.workbench.uitools.cell.CellRendererAdapter;

/**
 * This renderer is responsible to delegate to the encapsulated renderer
 * (which was the renderer set by the UI delegate) the task to render the
 * label based on the look and feel and then the <code>CellRendererAdapter</code>
 * is used to format the text, icon, tooltip and accessible name.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class ComboCellRenderer implements ListCellRenderer,
                                         UIResource
{
	/**
	 * The <code>ListCellRenderer</code> set by the UI delegate that will
	 * return the actual <code>Component</code>
	 */
	final ListCellRenderer cellRenderer;

	/**
	 * The adapter used to update the text, icon, tooltip and accessible name.
	 */
	final CellRendererAdapter cellRendererAdapter;

	/**
	 * The object used as the prototype value, which is used to speed up the
	 * calculation of the widget.
	 */
	private final Component prototypeRenderer;

	/**
	 * Creates a new <code>ListCellRenderer</code>.
	 *
	 * @param cellRenderer The <code>ListCellRenderer</code> set by the UI
	 * delegate that will return the actual <code>Component</code>
	 * @param cellRendererAdapter The adapter used to update the text, icon,
	 * tooltip and accessible name
	 * @param prototypeRenderer
	 */
	ComboCellRenderer(ListCellRenderer cellRenderer,
	                     CellRendererAdapter cellRendererAdapter,
	                     Component prototypeRenderer)
	{
		super();

		this.cellRenderer        = cellRenderer;
		this.prototypeRenderer   = prototypeRenderer;
		this.cellRendererAdapter = cellRendererAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	public Component getListCellRendererComponent(JList list,
	                                              Object value,
	                                              int index,
	                                              boolean selected,
	                                              boolean cellHasFocus)
	{
		// Don't need to format the cell, use the prototype renderer directly
		if (value == prototypeRenderer)
		{
			return prototypeRenderer;
		}

		// This will allow the look and feel to set anything in order to
		// reflect the native OS
		JLabel renderer = (JLabel) cellRenderer.getListCellRendererComponent
		(
			list,
			null,
			index,
			selected,
			cellHasFocus
		);

		renderer.setEnabled(list.isEnabled());

		// Update the renderer's properties by converting the value to
		// something displayable (text, icon, tooltip)
		updateRenderer(renderer, value);

		// If the list does not have the focus, show the selected cell has
		// "partially" selected
		if (selected         &&
		    !list.hasFocus() &&
		     list.isFocusable())
		{
			renderer.setForeground(list.getForeground());
			renderer.setBackground(UIManager.getColor("Panel.background"));
		}

		// Grey out the icon
		if (!list.isEnabled()                  &&
		    renderer.getIcon()         != null &&
		    renderer.getDisabledIcon() == null &&
		    renderer.getIcon() instanceof ImageIcon)
		{
			ImageIcon icon = (ImageIcon) renderer.getIcon();
			Image image = GrayFilter.createDisabledImage(icon.getImage());
			renderer.setIcon(new ImageIcon(image));
		}

		return renderer;
	}

	private void updateRenderer(JLabel renderer, Object value)
	{
		renderer.setText(cellRendererAdapter.buildText(value));
		renderer.setIcon(cellRendererAdapter.buildIcon(value));
		renderer.setToolTipText(cellRendererAdapter.buildToolTipText(value));
		renderer.getAccessibleContext().setAccessibleName(cellRendererAdapter.buildAccessibleName(value));
	}
}