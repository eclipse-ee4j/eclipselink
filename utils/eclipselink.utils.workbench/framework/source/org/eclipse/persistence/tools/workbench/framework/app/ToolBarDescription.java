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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractButton;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;


/**
 * Implementing class describes how a <code>JToolBar</code> should be built 
 * and merged with other <code>ToolBarDescriptions</code> based upon contained
 * actions.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription
 * @version 10.1.3
 */
public class ToolBarDescription implements GroupContainerDescription
{
	
	private List buttonGroups;
	
	public ToolBarDescription()
	{
		super();
		this.buttonGroups = new ArrayList();
	}
	
	/**
	 * Returns the <code>JToolBar</code> represented by this description.
	 */
	public Component component()
	{
		JToolBar toolBar = new JToolBar();
		for (Iterator items = components(); items.hasNext();)
		{
			toolBar.add((AbstractButton)items.next());
		}
		
		return toolBar;
	}
	
	/**
	 * Returns an ordered list of <code>ToolBarButtons</code> including seperators
	 * contained in this <code>JToolBar</code>.
	 */
	public ListIterator components()
	{
		ArrayList items = new ArrayList(); 
		for (Iterator groups = this.buttonGroups.iterator(); groups.hasNext();)
		{
			ToolBarButtonGroupDescription group = (ToolBarButtonGroupDescription)groups.next();
			for (Iterator buttons = group.components(); buttons.hasNext();)
			{
				items.add(buttons.next());
			}
			// while there are still more groups, add a seperator.
			if (groups.hasNext())
			{
				JToolBar.Separator seperator = new JToolBar.Separator(null);
				seperator.setOrientation(JSeparator.VERTICAL);
				items.add(seperator);
			}
		}
		return items.listIterator();
	}
	
	/**
	 * Adds a <code>ComponentGroupDescription</code> to the tool bar which describes
	 * a group of tool bar buttons.  Default implemented parameter would be 
	 * <code>ToolBarButtonGroupDescription</code>.
	 */
	public void add(ComponentGroupDescription buttonGroup)
	{
		this.buttonGroups.add(buttonGroup);
	}
	
	/**
	 * Removes a <code>ComponentGroupDescription</code> to the tool bar which describes
	 * a group of tool bar buttons.  Default implemented parameter would be 
	 * <code>ToolBarButtonGroupDescription</code>.
	 */
	public void remove(ComponentGroupDescription buttonGroup)
	{
		this.buttonGroups.remove(buttonGroup);
	}
	/**
	 * Returns all of the <code>FrameworkActions</code> contained in this tool bar
	 * represented by its buttons and sub menus. 
	 */
	public Iterator actions()
	{
		ArrayList actionIterators = new ArrayList();
		for (Iterator groups =  this.buttonGroups.iterator(); groups.hasNext();)
		{
			ComponentGroupDescription menuGroup = (ComponentGroupDescription)groups.next();
			actionIterators.add(menuGroup.actions());
		}

		
		return new CompositeIterator(actionIterators);
	}
	
	/**
	 * Updates the contents of this tool bar based upon the actions contained
	 * in the given <code>FrameworkAction</code> Collection.
	 */
	public void updateOn(Collection frameworkActions)
	{
		for (Iterator groups = this.buttonGroups.iterator(); groups.hasNext();)
		{
			((ActionContainer)groups.next()).updateOn(frameworkActions);
		}
	}

	/**
	 * Merges the contents of this tool bar based upon the actions described in
	 * the given <code>ActionContainer</code>.
	 */
	public void mergeWith(ActionContainer toolBarDescription)
	{
		updateOn(CollectionTools.collection(toolBarDescription.actions()));
	}
	
	public boolean hasComponents()
	{
		return this.buttonGroups.size() > 0;
	}
}
