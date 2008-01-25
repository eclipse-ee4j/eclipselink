/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.meta;

import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalField;

/**
 * someday paul will document this interface... :-(
 */
public interface MWClassRefreshPolicy
{
	void refreshAttributes(MWClass mwClass, ExternalField[] externalFields);
	
	void finalizeRefresh(MWClass mwClass);
}
