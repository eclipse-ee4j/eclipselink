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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.meta;

import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ClassDescription;


/**
 * This adapter assumes that the user-supplied "class descriptions"
 * are a collection of ClassDescription objects, supplied by a "combined"
 * class repository.
 */
public class ClassDescriptionClassDescriptionAdapter
    extends DefaultClassDescriptionAdapter
{
    /**
     * provide a Singleton
     */
    private static ClassDescriptionAdapter INSTANCE;

    public static synchronized ClassDescriptionAdapter instance() {
        if (INSTANCE == null) {
            INSTANCE = new ClassDescriptionClassDescriptionAdapter();
        }
        return INSTANCE;
    }

    /**
     * Assume the "class description" is a ClassDescription object.
     */
    public String className(Object classDescription) {
        return ((ClassDescription) classDescription).getName();
    }

    /**
     * Assume the "class description" is a ClassDescription object.
     */
    public String additionalInfo(Object classDescription) {
        return ((ClassDescription) classDescription).getAdditionalInfo();
    }

}
