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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.preferences;

import java.awt.Component;

import org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode;
import org.eclipse.persistence.tools.workbench.framework.context.PreferencesContext;


/**
 * Preferences node for class settings used by
 * the mappings plug-in.
 */
final class ClassPreferencesNode extends AbstractPreferencesNode {

    ClassPreferencesNode(PreferencesContext context) {
        super(context);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#initialize()
     */
    protected void initialize() {
        super.initialize();
//        this.insert(new XXXXPreferencesNode(this.getPreferencesContext()), 0);
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildPropertiesPage()
     */
    protected Component buildPropertiesPage() {
        return new ClassPreferencesPage(this.getPreferencesContext());
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.AbstractPreferencesNode#buildDisplayString()
     */
    protected String buildDisplayString() {
        return this.resourceRepository().getString("PREFERENCES.MAPPINGS.CLASS");
    }

    /**
     * @see org.eclipse.persistence.tools.workbench.framework.app.PreferencesNode#helpTopicId()
     */
    public String helpTopicId() {
        return "preferences.class";
    }
}
