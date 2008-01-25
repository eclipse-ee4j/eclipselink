/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.framework.context;

import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;

/**
 * This interface defines the "context" passed to the various
 * preferences nodes during construction. We cannot use the
 * application context because the preferences UI is not
 * associated with a particular workbench window - it is
 * shared among them all.
 */
public interface PreferencesContext extends ApplicationContext {

	/** Return the trigger that will cause changes to preferences to be accepted. */
	ValueModel getBufferTrigger();

}
