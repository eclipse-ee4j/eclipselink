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
package org.eclipse.persistence.tools.workbench.framework.context;

/**
 * Wrap another context and redirect its application context
 * to another application context. Useful for nodes.
 */
public class ApplicationContextWorkbenchContext
	extends WorkbenchContextWrapper
{
	private ApplicationContext applicationContext;


	// ********** constructor/initialization **********

	/**
	 * Construct a context with an expanded resource repository
	 * that adds the resources in the specified resource bundle and icon map
	 * to the original resource repository.
	 */
	public ApplicationContextWorkbenchContext(WorkbenchContext delegate, ApplicationContext applicationContext) {
		super(delegate);
		this.applicationContext = applicationContext;
	}


	// ********** non-delegated behavior **********

	/**
	 * @see WorkbenchContext#getApplicationContext()
	 */
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	
	// ********** additional behavior **********

	/**
	 * Return the original application context.
	 */
	public ApplicationContext delegateApplicationContext() {
		return this.getDelegate().getApplicationContext();
	}

}
