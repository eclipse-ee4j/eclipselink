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
package org.eclipse.persistence.tools.workbench.scplugin.model;

import org.eclipse.persistence.tools.workbench.scplugin.model.meta.ClassRepository;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;


/**
 * Base class for Session Configuration model.
 */
public abstract class SCModel extends AbstractNodeModel {

    /**
     * Constructor for SCModel.
     */
    protected SCModel() {

        super();
    }

    protected SCModel( SCModel parent) {

        super( parent);
    }

    /**
     * Returns the <code>ClassRepository</code> that should be used by the
     * sessions.xml.
     *
     * @return The repository for classpath entries and classes
     */
    public abstract ClassRepository getClassRepository();

    public String displayString() {
        return ClassTools.shortClassNameForObject( this);
    }
}
