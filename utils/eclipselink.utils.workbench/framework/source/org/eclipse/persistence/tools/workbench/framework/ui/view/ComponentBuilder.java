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
package org.eclipse.persistence.tools.workbench.framework.ui.view;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;


/**
 * This interface describes how a <code>Component</code> should be built based upon the 
 * given <code>ApplicationNode</code>.  Typically, implementing classes of this interface
 * are used in conjunction with <code>TabbedPropertiesPage</code> to create
 * the necessary <code>AbstractPropertiesPage</code> or other <code>Component</code>
 * type instance.  In this particular situation, creation of the <code>Component</code>
 * necessary to populate the tabbed page needs to be done on demand.  Thus, the need for 
 * a class that knows how to build this component based upon the <code>ApplicationNode</code>.
 * 
 * @version 10.0.3
 */
public interface ComponentBuilder
{	
	/**
	 * Builds and returns the associated UI <code>Component</code> based upon the
	 * given <code>PropertyValueModel</code> node holder.  If caching this built <code>Component</code>
	 * is necessary, it is the responsibility of the implementor of this method to
	 * handle this case in the implementing class.
	 */
	public Component buildComponent(PropertyValueModel nodeHolder);
}
