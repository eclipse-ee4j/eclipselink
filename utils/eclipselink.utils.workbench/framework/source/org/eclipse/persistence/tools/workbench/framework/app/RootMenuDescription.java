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
package org.eclipse.persistence.tools.workbench.framework.app;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;


/**
 * Implements the <code>GroupContainerDescription</code> and describes a root
 * menu.  Since the root menu is merely a container of menu items, no <code>FrameworkAction</code>
 * or resources are required.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.GroupContainerDescription
 * @version 10.1.3
 */
public class RootMenuDescription implements GroupContainerDescription
{ 
	private List buttonGroups;

	public RootMenuDescription()
	{
		super();
		this.buttonGroups = new ArrayList();
	}

	/**
	 * Adds a <code>ComponentGroupDescription</code> to the menu which describes
	 * a group of menu items.  Default implemented parameter would be 
	 * <code>MenuGroupDescription</code>.
	 */
	public void add(ComponentGroupDescription menuGroup)
	{
		buttonGroups.add(menuGroup);		
	}

	/**
	 * Removes a <code>ComponentGroupDescription</code> to the menu which describes
	 * a group of menu items.  Default implemented parameter would be 
	 * <code>MenuGroupDescription</code>.
	 */
	public void remove(ComponentGroupDescription menuGroup)
	{
		buttonGroups.remove(menuGroup);
	}


	public boolean hasComponents()
	{
		return buttonGroups.size() > 0;
	}

	/**
	 * Returns the root <code>JMenu</code>.
	 */
	public Component component()
	{
		JMenu menu = new JMenu();
		for (Iterator items = components(); items.hasNext();)
		{
			menu.add((Component)items.next());
		}
		return menu;
	}

	/**
	 * All actions expressed as menu items in this menu.
	 */
	public Iterator actions()
	{
		ArrayList actionIterators = new ArrayList();
		for (Iterator groups =  buttonGroups.iterator(); groups.hasNext();)
		{
			ComponentGroupDescription menuGroup = (ComponentGroupDescription)groups.next();
			actionIterators.add(menuGroup.actions());
		}
		return new CompositeIterator(actionIterators);
	}
	
	/**
	 * Updates this menu and all of its children based upon the Collection
	 * of <code>FrameworkActions</code>.
	 */
	public void updateOn(Collection frameworkActions)
	{
		for (Iterator groups = buttonGroups.iterator(); groups.hasNext();)
		{
			ComponentGroupDescription groupWrapper =  (ComponentGroupDescription)groups.next();
			groupWrapper.updateOn(frameworkActions);
		}
		// clean up groups whose menu items have been removed entirely...
		removeEmptyGroups();
	}
	
	/**
	 * Merges this menu based upon the <code>FrameworkActions</code> expressed
	 * in the given <code>ActionContainer</code>.
	 */
	public void mergeWith(ActionContainer menuDescription)
	{
		updateOn(CollectionTools.collection(menuDescription.actions()));
	}
	
	private void removeEmptyGroups()
	{
		for (Iterator groups = new ArrayList(buttonGroups).iterator(); groups.hasNext();)
		{
			ComponentGroupDescription groupWrapper =  (ComponentGroupDescription)groups.next();
			if ( ! groupWrapper.hasComponents())
			{
				buttonGroups.remove(groupWrapper);
			}
		}
	}
	
	/**
	 * Returns all of the menu items contained in this menu in an ordered List.
	 */
	public ListIterator components()
	{
		ArrayList listOfItems = new ArrayList();
		for (Iterator groups = buttonGroups.iterator(); groups.hasNext();)
		{
			ComponentGroupDescription groupWrapper =  (ComponentGroupDescription)groups.next();
			for (Iterator menuItems =groupWrapper.components(); menuItems.hasNext();)
			{
				JMenuItem menuItem = (JMenuItem) menuItems.next();
				listOfItems.add(menuItem);
			}
			// if this is not the last group, then add a seperator.
			if (groups.hasNext())
			{
				listOfItems.add(new JPopupMenu.Separator());
			}
		}
		return listOfItems.listIterator();
	}
}
