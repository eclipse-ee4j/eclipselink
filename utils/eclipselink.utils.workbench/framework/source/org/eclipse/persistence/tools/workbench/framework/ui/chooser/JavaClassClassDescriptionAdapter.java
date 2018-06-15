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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;


/**
 * The Java class adapter assumes that the user-supplied "class descriptions"
 * are a collection of Java classes.
 */
public class JavaClassClassDescriptionAdapter
    extends DefaultClassDescriptionAdapter
{
    private static ClassDescriptionAdapter INSTANCE;

    public static synchronized ClassDescriptionAdapter instance() {
        if (INSTANCE == null) {
            INSTANCE = new JavaClassClassDescriptionAdapter();
        }
        return INSTANCE;
    }

    /**
     * Assume the "class description" is a Java Class object.
     */
    public String className(Object classDescription) {
        return ((Class) classDescription).getName();
    }

}

