/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
