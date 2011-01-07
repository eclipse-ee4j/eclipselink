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
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import java.util.Collection;

public final class DefaultMWClassRefreshPolicy 
	extends AbstractMWClassRefreshPolicy
{
	// singleton
	private static MWClassRefreshPolicy INSTANCE;

	/**
	 * Return the singleton.
	 */
	public static synchronized MWClassRefreshPolicy instance() {
		if (INSTANCE == null) {
			INSTANCE = new DefaultMWClassRefreshPolicy();
		}
		return INSTANCE;
	}

	private DefaultMWClassRefreshPolicy() 
	{
		super();
	}
	
	protected void resolveMissingAttributes(MWClass mwClass, Collection missingAttributes)
	{
		mwClass.removeAttributes(missingAttributes);
	}
	
	public void finalizeRefresh(MWClass mwClass)
	{
		mwClass.clearEjb20Attributes();
	}
}
