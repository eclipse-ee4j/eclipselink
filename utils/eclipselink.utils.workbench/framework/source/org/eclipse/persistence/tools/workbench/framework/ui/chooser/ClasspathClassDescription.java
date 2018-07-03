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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import org.eclipse.persistence.tools.workbench.utility.string.StringTools;

/**
 * Straightforward description of a class found on a classpath.
 */
public class ClasspathClassDescription {
    private String className;
    private String classpathEntry;

    ClasspathClassDescription(String className, String classpathEntry) {
        super();
        this.className = className;
        this.classpathEntry = classpathEntry;
    }

    public String getClassName() {
        return this.className;
    }

    public String getClasspathEntry() {
        return this.classpathEntry;
    }

    public String toString() {
        return StringTools.buildToStringFor(this, this.className + " - " + this.classpathEntry);
    }


    // ********** adapter **********

    public static class Adapter extends DefaultClassDescriptionAdapter {
        public String className(Object classDescription) {
            return ((ClasspathClassDescription) classDescription).getClassName();
        }
        public String additionalInfo(Object classDescription) {
            return ((ClasspathClassDescription) classDescription).getClasspathEntry();
        }
    }

}
