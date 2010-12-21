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

// JDK
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * This <code>GridLayout</code> class is a layout manager that lays out a
 * container's components in a rectangular grid but also provides a
 * <code>GridBagConstraints</code> to determine which component needs to keep
 * their preferred/minimum width and/or height. Compare to <code>GridLayout</code>,
 * this version does not lay out components that are not visible.
 * <p>
 * For instance:
 * <pre>
 * JPanel panel = new JPanel(new GrigLayout(1, 6));</pre>
 * creates a grid of 1 row and 6 columns.
 * <pre>
 * GridConstraints constraints = new GridConstraints();
 *
 * panel.add(new ToolBarButton("Back"));
 * panel.add(new ToolBarButton("Next"));
 *
 * constraints.fill = GridConstraints.NONE;
 * panel.add(RigidBox.box(), constraints);<pre>
 * The rigid box will not be resized as Back and Next will be.
 * <pre>
 * panel.add(new JButton("Cancel"));
 *
 * constraints.fill = GridConstraints.NONE;
 * panel.add(RigidBox.box(10));
 *
 * constraints.fill = GridConstraints.VERTICAL;
 * panel.add(new ToolBarButton("Help me on this"));</pre>
 * The last button will keep its horizontal size but the height will be the
 * height of the taller component. Also, it is not needed to add a constraints
 * object when the component can be resized both horizontally and vertically.
 * <p>
 * The layout will be:
 * <pre>
 * _______________________________________________________
 * | ________ ________   ________      _________________ |
 * | | Back | | Next |   |Cancel|      |Help me on this| |
 * | ¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯   ¯¯¯¯¯¯¯¯      ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯
 * </pre>
 * <b>Important:</b> <code>GridLayout</code> may give unexpected layout when
 * using a grid mxn. However, 1xn or mx1 works fine.
 *
 * @version 10.1.3
 * @author Pascal Filion
 */
public class GridLayout implements LayoutManager2,
											  Serializable
{
	/**
	 * This is the number of columns specified for the grid. The number of columns
	 * can be changed at any time. This should be a non negative integer, where
	 * '0' means 'any number' meaning that the number of Columns in that dimension
	 * depends on the other dimension.
	 */
	private int columns;

	/**
	 *
	 */
	private int componentIndex;

	/**
	 * A cache of the component layout constraints.
	 */
	private Hashtable constraintsTable = new Hashtable();

	/**
	 * This is the horizontal gap (in pixels) which specifies the space between
	 * columns. They can be changed at any time. This should be a non negative
	 * integer.
	 */
	private int hgap;

	/**
	 * This is the number of rows specified for the grid. The number of rows can
	 * be changed at any time. This should be a non negative integer, where '0'
	 * means 'any number' meaning that the number of Rows in that dimension
	 * depends on the other dimension.
	 */
	private int rows;

	/**
	 * This is the vertical gap (in pixels) which specifies the space between
	 * rows. They can be changed at any time. This should be a non negative
	 * integer.
	 *  
	 */
	private int vgap;

	/**
	 * Creates a grid layout with a default of one column per component, in a
	 * single row.
	 */
	public GridLayout()
	{
		super();
	}

	/**
	 * Creates a grid layout with the specified number of rows and columns. All
	 * components in the layout are given equal size.
	 * <p>
	 * One, but not both, of <code>rowCount</code> and <code>columnCount</code>
	 * can be zero, which means that any number of objects can be placed in a row
	 * or in a column.
	 * 
	 * @param rowCount The rows, with the value zero meaning any number of rows
	 * @param columnCount The columns, with the value zero meaning
	 */
	public GridLayout(int rowCount, int columnCount)
	{
		super();
		setRows(rowCount);
		setColumns(columnCount);
	}

	/**
	 * Creates a grid layout with the specified number of rows and columns. All
	 * components in the layout are given equal size.
	 * <p>
	 * In addition, the horizontal and vertical gaps are set to the specified
	 * values. Horizontal gaps are placed at the left and right edges, and between
	 * each of the columns. Vertical gaps are placed at the top and bottom edges,
	 * and between each of the rows.
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can be
	 * zero, which means that any number of objects can be placed in a row or in a
	 * column.
	 * 
	 * @param rowCount The rows, with the value zero meaning any number of rows
	 * @param columnCount The columns, with the value zero meaning any number of
	 * columns
	 * @param horizontalGap The horizontal gap
	 * @param verticalGap The vertical gap
	 */
	public GridLayout(int rowCount, int columnCount, int horizontalGap, int verticalGap)
	{
		this(rowCount, columnCount);
		setHgap(horizontalGap);
		setVgap(verticalGap);
	}

	/**
	 * Adds the specified component to the layout, using the specified constraint
	 * object. If the constraints is null <code>null</code>, creates a default
	 * constraints with <code>fill = BOTH</code>.
	 * 
	 * @param component The component to be added
	 * @param constraints Where/how the component is added to the layout
	 */
	public void addLayoutComponent(Component component, Object constraints)
	{
		if (constraints == null) {
			constraints = new GridBagConstraints();
			((GridBagConstraints) constraints).fill = GridBagConstraints.BOTH;
		}

		else if (!(constraints instanceof GridBagConstraints))
		{
			throw new IllegalArgumentException("The constraints has to be GridBagConstraints");
		}

		constraintsTable.put(component, ((GridBagConstraints) constraints).clone());
	}

	/**
	 * Adds the specified component with the specified name to the layout.
	 *
	 * @param name the name of the component
	 * @param component the component to be added
	 */
	public void addLayoutComponent(String name, Component component)
	{
	}

	/**
	 * Gets the number of columns in this layout.
	 *
	 * @return The number of columns in this layout
	 */
	public int getColumns()
	{
		return columns;
	}
	    
	/**
	 * Gets the constraints for the specified component. A copy of the actual
	 * <code>GridBagConstraints</code> object is returned.
	 * 
	 * @param component the component to be queried
	 * @return The constraint for the specified component in this grid layout; a
	 * copy of the actual constraint object is returned
	 */
	public GridBagConstraints getConstraints(Component component)
	{
		GridBagConstraints constraints = (GridBagConstraints) constraintsTable.get(component);

		if (constraints == null)
		{
			constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;
			constraintsTable.put(component, constraints);
		}

		return (GridBagConstraints) constraints.clone();
	}

	/**
	 * Gets the horizontal gap between components.
	 *
	 * @return The horizontal gap between components
	 */
	public int getHgap()
	{
		return hgap;
	}

	/**
	 * Returns the alignment on the x axis. Determines alignment relative to other
	 * components.
	 * 
	 * @return 0.5f
	 */
	public float getLayoutAlignmentX(Container target)
	{
		return 0.5f;
	}

	/**
	 * Returns the alignment on the y axis. Determines alignment relative to other
	 * components.
	 * 
	 * @return 0.5f
	 */
	public float getLayoutAlignmentY(Container target)
	{
		return 0.5f;
	}

	/**
	 * Returns the size needed to layout the given parent based on the minimum
	 * size of its children if the given useMinimumSize is <code>true</code>
	 * otherwise use the preferred size of its children.
	 * 
	 * @param parent The parent that needs to have a its size calculated
	 * @param useMinimumSize <code>true</code> to use the minimum size of the
	 * parent's children; <code>false</code> to use the preferred size
	 * @return The size needed to propery layout the given container
	 */
	protected Dimension getLayoutSize(Container parent, boolean useMinimumSize)
	{
		synchronized (parent.getTreeLock())
		{
			Dimension componentSize;
			Component component;
			GridBagConstraints constraints;
			Insets insets = parent.getInsets();
			int horizontalInsets = insets.left + insets.right;
			int verticalInsets = insets.top + insets.bottom;
			
			int componentCount = parent.getComponentCount();
			int resizabledWidthComponentCount = 0;
			int resizabledHeightComponentCount = 0;
			int rowCount = getRows();
			int columnCount = getColumns();

			int maxComponentWidth = 0;
			int maxComponentHeight = 0;
			int rigidComponentWidth = 0;
			int rigidComponentHeight = 0;

			// Make sure we do not count invisible component
			for (int index = componentCount; --index >= 0;)
			{
				component = parent.getComponent(index);

				if (!component.isVisible())
				{
					// For single row
					if (columnCount < 2)
						rowCount--;

					// For single column
					if (rowCount < 2)
						columnCount--;

					componentCount--;
				}
			}

			// Based on the row count, determine the column count
			if (rowCount > 0)
				columnCount = (componentCount + rowCount - 1) / rowCount;
			else if (columnCount > 0)
				rowCount = (componentCount + columnCount - 1) / columnCount;
			else
				return new Dimension();

			// Look in the parent to see if there are components implementing
			// ComponentResizable and returning false so that we won't count their size
			for (int index = parent.getComponentCount(); --index >= 0;)
			{
				component = parent.getComponent(index);

				if (!component.isVisible())
					continue;

				constraints = getConstraints(component);

				// Ask the component its the desired size
				if (useMinimumSize)
					componentSize = component.getMinimumSize();
				else
					componentSize = component.getPreferredSize();

				if (constraints.fill != GridBagConstraints.BOTH)
				{
					// For a single row
					if ((rowCount < 2) &&
						 ((constraints.fill == GridBagConstraints.VERTICAL) ||
						  (constraints.fill == GridBagConstraints.NONE)))
					{
						rigidComponentWidth += componentSize.width;
					}
					else if (constraints.fill != GridBagConstraints.NONE)
					{
						resizabledWidthComponentCount++;
						maxComponentWidth = Math.max(maxComponentWidth, componentSize.width);
					}
					else
						maxComponentWidth = Math.max(maxComponentWidth, componentSize.width);

					// For a single column
					if ((columnCount < 2) &&
						 ((constraints.fill == GridBagConstraints.HORIZONTAL) ||
						  (constraints.fill == GridBagConstraints.NONE)))
					{
						rigidComponentHeight += componentSize.height;
					}
					else if (constraints.fill != GridBagConstraints.NONE)
					{
						resizabledHeightComponentCount++;
						maxComponentHeight = Math.max(maxComponentHeight, componentSize.height);
					}
					else
						maxComponentHeight = Math.max(maxComponentHeight, componentSize.height);
				}
				else
				{
					resizabledWidthComponentCount++;
					resizabledHeightComponentCount++;
					maxComponentWidth = Math.max(maxComponentWidth, componentSize.width);
					maxComponentHeight = Math.max(maxComponentHeight, componentSize.height);
				}
			}

			// Calculate the dimension needed by the parent
			int width  = 0;
			int height = 0;

			// For a single row
			if (rowCount < 2)
			{
				width  = horizontalInsets +
							rigidComponentWidth +
							(maxComponentWidth * resizabledWidthComponentCount) +
							(columnCount - 1) * getHgap();

				height = verticalInsets +
							Math.max(rigidComponentHeight, maxComponentHeight) +
							(rowCount - 1) * getVgap();
			}
			// For a single column
			else
			{
				width  = horizontalInsets +
							Math.max(rigidComponentWidth, maxComponentWidth) +
							(columnCount - 1) * getHgap();

				height = verticalInsets +
							rigidComponentHeight +
							(maxComponentHeight * resizabledHeightComponentCount) +
							(rowCount - 1) * getVgap();
			}

			return new Dimension(width, height);
		}
	}
	
	/**
	 * Returns the next visible component starting at the index.
	 * 
	 * @param container The container to layout
	 * @param index The index the begin the search of the first visible component
	 * @return The next component visible from the index
	 */
	private Component getNextVisibleComponent(Container parent, int index)
	{
		Component component = null;
		int componentCount = parent.getComponentCount();

		while (componentIndex < componentCount)
		{
			component = parent.getComponent(componentIndex++);

			if (component.isVisible())
				return component;

			component.setBounds(0, 0, 0, 0);
		}

		return null;
	}

	/**
	 * Gets the number of rows in this layout.
	 *
	 * @return The number of rows in this layout
	 */
	public int getRows()
	{
		return rows;
	}
	    
	/**
	 * Gets the vertical gap between components.
	 *
	 * @return The vertical gap between components.
	 */
	public int getVgap()
	{
		return vgap;
	}

	/**
	 * Invalidates the layout, indicating that if the layout manager has cached
	 * information it should be discarded.
	 * 
	 * @param target The container to invalidate
	 */
	public void invalidateLayout(Container target)
	{
	}

	/**
	 * Lays out the specified container using this layout.
	 * <p>
	 * This method reshapes the components in the specified target container in
	 * order to satisfy the constraints of the <code>GridLayout</code> object.
	 * <p>
	 * The grid layout manager determines the size of individual components by
	 * dividing the free space in the container into equal-sized portions
	 * according to the number of rows and columns in the layout. The container's
	 * free space equals the container's size minus any insets and any specified
	 * horizontal or vertical gap. All components in a grid layout are given the
	 * same size.
	 * 
	 * @param parent The container in which to do the layout
	 */
	public void layoutContainer(Container parent)
	{
		if (parent == null)
			return;

		synchronized (parent.getTreeLock())
		{
			int componentCount = parent.getComponentCount();

			if (componentCount == 0)
				return;

			Component component;
			Dimension preferredSize;
			GridBagConstraints constraints;
			Insets insets = parent.getInsets();
			boolean leftToRight = parent.getComponentOrientation().isLeftToRight();
			int horizontalInsets = insets.left + insets.right;
			int verticalInsets = insets.top + insets.bottom;
			int width = parent.getWidth() - horizontalInsets;
			int height = parent.getHeight() - verticalInsets;

			int resizabledWidthComponentCount = 0;
			int resizabledHeightComponentCount = 0;
			int rowCount = getRows();
			int columnCount = getColumns();

			int maxComponentWidth = 0;
			int maxComponentHeight = 0;
			int rigidComponentWidth = 0;
			int rigidComponentWidthCount = 0;
			int rigidComponentHeight = 0;
			int rigidComponentHeightCount = 0;

			double resizeFactorWidth = 1;
			double resizeFactorHeight = 1;

			// Step 1. Make sure we do not count invisible component
			for (int index = componentCount; --index >= 0;)
			{
				component = parent.getComponent(index);

				if (!component.isVisible())
				{
					// For single row
					if (columnCount < 2)
						rowCount--;

					// For single column
					else if (rowCount < 2)
						columnCount--;

					componentCount--;
				}
			}

			// Based on the row count, determine the column count
			if (rowCount > 0)
				columnCount = (componentCount + rowCount - 1) / rowCount;
			else if (columnCount > 0)
				rowCount = (componentCount + columnCount - 1) / columnCount;
			else
				return; // TODO: TO SEE IF WE CAN DO THAT

			// Step 2. Calculate the max width and height for resizable component
			//         and keep track of the none resizable space
			for (int index = 0; index < parent.getComponentCount(); index++)
			{
				component = parent.getComponent(index);

				if (!component.isVisible())
					continue;

				preferredSize = component.getPreferredSize();
				constraints = getConstraints(component);

				if (constraints.fill != GridBagConstraints.BOTH)
				{
					// For a single row
					if ((rowCount < 2) &&
						 ((constraints.fill == GridBagConstraints.VERTICAL) ||
						  (constraints.fill == GridBagConstraints.NONE)))
					{
						rigidComponentWidth += preferredSize.width;
						rigidComponentWidthCount++;
					}
					else if (constraints.fill != GridBagConstraints.NONE)
					{
						resizabledWidthComponentCount++;
						maxComponentWidth = Math.max(maxComponentWidth, preferredSize.width);
					}
					else
						maxComponentWidth = Math.max(maxComponentWidth, preferredSize.width);

					// For a single column
					if ((columnCount < 2) &&
						 ((constraints.fill == GridBagConstraints.HORIZONTAL) ||
						  (constraints.fill == GridBagConstraints.NONE)))
					{
						rigidComponentHeight += preferredSize.height;
						rigidComponentHeightCount++;
					}
					else if (constraints.fill != GridBagConstraints.NONE)
					{
						resizabledHeightComponentCount++;
						maxComponentHeight = Math.max(maxComponentHeight, preferredSize.height);
					}
					else
						maxComponentHeight = Math.max(maxComponentHeight, preferredSize.height);
				}
				else
				{
					resizabledWidthComponentCount++;
					resizabledHeightComponentCount++;
					maxComponentWidth = Math.max(maxComponentWidth, preferredSize.width);
					maxComponentHeight = Math.max(maxComponentHeight, preferredSize.height);
				}
			}

			// Step 3. Calculate the remaining space that will be shared between resizabled components
			int widthLeft = width  - horizontalInsets - (componentCount - 1) * getHgap();
			int heightLeft = height - verticalInsets  - (componentCount - 1) * getVgap();

			if (resizabledWidthComponentCount == 0)
				resizabledWidthComponentCount = 1;

			// For a single row
			if (rowCount < 2)
			{
				widthLeft -= rigidComponentWidth;
				maxComponentWidth  = widthLeft / resizabledWidthComponentCount;

				maxComponentHeight = parent.getHeight();// Since it's one row, the height is the height of the parent Math.max(maxComponentHeight, maxRigidComponentHeight);
			}

			// For a single column
			if (columnCount < 2)
			{
				maxComponentWidth = width;// Since it's one row, the width is the width of the parent Math.max(maxComponentWidth, maxRigidComponentWidth);

				heightLeft -= rigidComponentHeight;
				maxComponentHeight  = heightLeft / resizabledWidthComponentCount;
			}

			if ((rigidComponentWidthCount > 0) && (rigidComponentWidth > width))
				resizeFactorWidth = width / (double) rigidComponentWidth;

			if ((rigidComponentHeightCount > 0) && (rigidComponentHeight > height))
				resizeFactorHeight = height / (double) rigidComponentHeight;

			// Step 4. Layout each column
			int x;
			int y = insets.top;
			componentIndex = 0;

			if (leftToRight)
				x = insets.left;
			else
				x = insets.right + width - insets.left;

			width = 0;
			height = 0;

			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
			{
				// Layout each row
				for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
				{
					component = getNextVisibleComponent(parent, componentIndex);

					if (component == null)
						break;

					constraints = getConstraints(component);

					// Use the preferred size of the component instead of the calculated size
					if (constraints.fill != GridBagConstraints.BOTH)
					{
						preferredSize = component.getPreferredSize();

						if ((constraints.fill == GridBagConstraints.VERTICAL) ||
							 (constraints.fill == GridBagConstraints.NONE))
						{
							width = (int) (preferredSize.width * resizeFactorWidth);
						}
						else
						{
							width = maxComponentWidth;
						}

						if ((constraints.fill == GridBagConstraints.HORIZONTAL) ||
							 (constraints.fill == GridBagConstraints.NONE))
						{
							height = (int) (preferredSize.height * resizeFactorHeight);
						}
						else
						{
							height = maxComponentHeight;
						}

						if (leftToRight)
							component.setBounds(x, y, width, height);
						else
							component.setBounds(x - width, y, width, height);

						if (columnCount > 1)
							height = Math.max(maxComponentHeight, height);
					}

					// Use the calculated size
					else
					{
						width = maxComponentWidth;
						height = maxComponentHeight;

						if (leftToRight)
							component.setBounds(x, y, width, height);
						else
							component.setBounds(x - width, y, width, height);
					}

					if (leftToRight)
						x += width + getHgap();
					else
						x -= (width - getHgap());
				}

				if (leftToRight)
					x = insets.left;
				else
					x = insets.right + width - insets.left;

				y += height + getVgap();
			}
		}
	}

	/** 
	 * Returns the maximum size of this component.
	 *
	 * @return <code>(Integer.MAX_VALUE, Integer.MAX_VALUE)</code>
	 */
	public Dimension maximumLayoutSize(Container target)
	{
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Returns the minimum layout size needed by the given container based on the
	 * minimum size of its visible children.
	 * 
	 * @param parent The container used to calculate the minimum size
	 * @return The minimum layout size needed by the given container
	 */
	public Dimension minimumLayoutSize(Container parent)
	{
		return getLayoutSize(parent, true);
	}

	/**
	 * Returns the preferred layout size needed by the given container based on
	 * the preferred size of its visible children.
	 * 
	 * @param parent The container used to calculate the preferred size
	 * @return The preferred layout size needed by the given container
	 */
	public Dimension preferredLayoutSize(Container parent)
	{
		return getLayoutSize(parent, false);
	}

	/**
	 * Removes the specified component from the layout.
	 *
	 * @param component the component to be removed
	 */
	public void removeLayoutComponent(Component component)
	{
		constraintsTable.remove(component);
	}

	/**
	 * Sets the number of columns in this layout to the specified value.
	 *
	 * @param columns The number of columns in this layout
	 */
	public void setColumns(int columns)
	{
		this.columns = columns;
	}

	/**
	 * Sets the constraints for the specified component in this layout.
	 *
	 * @param Component The component to be modified
	 * @param constraints The constraints to be applied
	 */
	public void setConstraints(Component Component, GridBagConstraints constraints)
	{
		constraintsTable.put(Component, constraints.clone());
	}

	/**
	 * Sets the horizontal gap between components to the specified value.
	 *
	 * @param hgap The horizontal gap between components
	 */
	public void setHgap(int hgap)
	{
		this.hgap = hgap;
	}

	/**
	 * Sets the number of rows in this layout to the specified value.
	 *
	 * @param rows The number of rows in this layout
	 */
	public void setRows(int rows)
	{
		this.rows = rows;
	}

	/**
	 * Sets the vertical gap between components to the specified value.
	 *
	 * @param vgap The vertical gap between components
	 */
	public void setVgap(int vgap)
	{
		this.vgap = vgap;
	}
}
