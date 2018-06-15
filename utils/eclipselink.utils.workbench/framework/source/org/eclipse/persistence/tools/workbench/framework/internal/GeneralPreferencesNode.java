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
package org.eclipse.persistence.tools.workbench.framework.internal;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;
import org.eclipse.persistence.tools.workbench.framework.help.HelpFacade;


/**
 * Preferences node for general (non-plug-in-specific) settings used by
 * the framework.
 */
final class GeneralPreferencesNode extends AbstractPreferencesNode {

    /**
     * reposition the context to the "general" preferences node
     */
    GeneralPreferencesNode(PreferencesContext context) {
        super((PreferencesContext) context.buildRedirectedPreferencesContext(FrameworkApplication.GENERAL_PREFERENCES_NODE));
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#initialize()
     */
    protected void initialize() {
        super.initialize();
        this.insert(HelpFacade.buildHelpPreferencesNode(this.getPreferencesContext()), 0);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildPropertiesPage()
     */
    protected Component buildPropertiesPage() {
        return new GeneralPreferencesPage(this.getPreferencesContext());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildDisplayString()
     */
    protected String buildDisplayString() {
        return this.resourceRepository().getString("PREFERENCES.GENERAL");
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode#helpTopicId()
     */
    public String helpTopicId() {
        return "preferences.general";
    }

}
