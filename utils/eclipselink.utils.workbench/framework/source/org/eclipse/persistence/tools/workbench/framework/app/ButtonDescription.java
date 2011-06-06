/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.Icon;

import org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * This class describes how to build an <code>AbstractButton</code> based
 * upon the specified <code>FrameworkAction</code>, optional associated 
 * resources, and the given <code>ButtonCreator</code>.
 * 
 * @see org.eclipse.persistence.tools.workbench.framework.app.ComponentDescription
 * @see org.eclipse.persistence.tools.workbench.framework.action.FrameworkAction
 * @version 10.1.3
 */
public class ButtonDescription implements ComponentDescription
{
	private FrameworkAction action;
	private String text;
	private String toolTip;
	private Icon icon;
	private int mnemonic;
	private boolean overrideActionResources = false;
	private AbstractButton button;
	private ButtonCreator buttonCreator;
	
	
	/**
	 * Use this constructor to override the resources specified in the associated
	 * <code>FrameworkAction</code>.
	 */
	public ButtonDescription(FrameworkAction action, ButtonCreator buttonCreator,
			String text, String toolTip, int mnemonic, Icon icon)
	{
		this(action, buttonCreator);
		overrideActionResources = true;
		this.action = action;
		this.text = text;
		this.toolTip = toolTip;
		this.icon = icon;
		this.mnemonic = mnemonic;
	}

	/**
	 * Use this contructor to accept the associated resources specified in the 
	 * given <code>FrameworkAction</code>.
	 */
	public ButtonDescription(FrameworkAction action, ButtonCreator buttonCreator)
	{
		super();
		this.action = action;
		this.buttonCreator = buttonCreator;
	}
	
	protected void initializeButtonResources(AbstractButton button)
	{
		// only override resources specified in the FrameworkAction
		// if object was built with that intent.
		if (overrideActionResources)
		{
			button.setText(text);
			button.setIcon(icon);
			button.setToolTipText(toolTip);
			button.setMnemonic(mnemonic);
		}
	}
	
	public Component component()
	{
		button = buttonCreator.createButton(getAction());
		if (overrideActionResources)
		{
			initializeButtonResources(button);
		}
		return button;
	}

	protected FrameworkAction getAction()
	{
		return action;
	}
	
	public Iterator actions()
	{
		return new SingleElementIterator(action);
	}
	
	public void updateOn(Collection frameworkActions)
	{
		// do nothing.  This is a leaf node.
	}
	
	void setButtonCreator(ButtonCreator buttonCreator)
	{
		this.buttonCreator = buttonCreator;
	}
	
	/**
	 * Implementors of this interface should know how to build an instance
	 * of an <code>AbstractButton</code> based on the provided 
	 * <code>FrameworkAction</code>.
	 */
	public static interface ButtonCreator
	{
		/**
		 * Creates an instance of an <code>AbstractButton</code> based upon
		 * the given <code>FrameworAction</code>.
		 */
		public AbstractButton createButton(FrameworkAction action);
	}
}
