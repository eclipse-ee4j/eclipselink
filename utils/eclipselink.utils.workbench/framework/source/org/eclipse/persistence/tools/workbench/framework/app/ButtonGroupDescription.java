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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationListIterator;


/**
 * Implementor that describes how groups of <code>ComponentDescription</code> objects
 * that describe <code>AbstractButton</code> objects are formed.  Typically this class
 * can be used to define groups of ToolBar buttons or JMenu items.  These groups represent
 * buttons defined between seperators in a menu or toolbar.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentGroupDescription
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
 * @version 10.1.3
 */
public class ButtonGroupDescription implements ComponentGroupDescription
{
	private List buttons;
	
	public ButtonGroupDescription()
	{
		super();
		buttons = new ArrayList();
	}

	public void add(ComponentDescription menuItem)
	{
		buttons.add(menuItem);
	}

	public void remove(ComponentDescription menuItem)
	{
		buttons.remove(menuItem);
	}

	/**
	 * Returns an ordered list of <code>Components</code> describing this 
	 * group.
	 */
	public ListIterator components()
	{
		return new TransformationListIterator(buttons.listIterator()) 
		{
			protected Object transform(Object next)
			{
				return ((ComponentDescription)next).component();
			}
		};
	}

	public boolean hasComponents()
	{
		return buttons.size() > 0;
	}

	/**
	 * Returns an <code>Iterator</code> on a List of all of the associated 
	 * <code>FrameworkAction</code>s with this group.  These actions represent
	 * all child actions.
	 */
	public Iterator actions()
	{
		ArrayList actionIterators = new ArrayList();
		for (Iterator items =  buttons.iterator(); items.hasNext();)
		{
			ComponentDescription menuItem = (ComponentDescription)items.next();
			actionIterators.add(menuItem.actions());
		}
		return new CompositeIterator(actionIterators);
	}

	/**
	 * Per the <code>ActionContainer</code> interface, this method defines how this
	 * group updates itself based upon the given Collection of FrameworkActions. 
	 */
	public void updateOn(Collection frameworkActions)
	{
		for (Iterator buttonDescIter = new ArrayList(buttons).iterator(); buttonDescIter.hasNext();)
		{
			ComponentDescription description = (ComponentDescription)buttonDescIter.next();
			description.updateOn(frameworkActions);
			postUpdateOnActions(description, frameworkActions);
		}
	}
	
	/**
	 * Defines a "post-update" for subsequent <code>ComponentDescription</code> objects in this
	 * group. Since a <code>ComponentDescription</code> can optionally have children, it must be removed
	 * if it does not match up with any of the actions being updated on.
	 */
	protected void postUpdateOnActions(ComponentDescription button, Collection frameworkActions)
	{
		boolean containsAllActions = true;
		for (Iterator buttonActions = button.actions(); buttonActions.hasNext();)
		{
			boolean containsGivenAction = false;
			FrameworkAction menuItemAction = (FrameworkAction)buttonActions.next();
			for (Iterator frameworkActionsIter = frameworkActions.iterator(); frameworkActionsIter.hasNext() && !containsGivenAction;)
			{
				FrameworkAction frameworkAction = (FrameworkAction)frameworkActionsIter.next();
				if (frameworkAction.getClassification().equals(menuItemAction.getClassification()))
				{
					containsGivenAction = true;
				}
			}
			containsAllActions &= containsGivenAction;
		}
		if (!containsAllActions)
		{
			remove(button);
		}		
	}
}
