/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.framework.help;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;


/**
 * Preferences node for general help settings used by
 * the help manager and OHJ.
 */
final class HelpPreferencesNode extends AbstractPreferencesNode {

    /**
     * reposition the context to the "help" preferences node
     */
    HelpPreferencesNode(PreferencesContext context) {
        super(context);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildPropertiesPage()
     */
    protected Component buildPropertiesPage() {
        return new HelpPreferencesPage(this.getPreferencesContext());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildDisplayString()
     */
    protected String buildDisplayString() {
        return this.resourceRepository().getString("PREFERENCES.GENERAL.HELP");
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode#helpTopicId()
     */
    public String helpTopicId() {
        return "preferences.general.help";
    }

}
