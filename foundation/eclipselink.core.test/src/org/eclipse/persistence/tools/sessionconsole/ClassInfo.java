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
package org.eclipse.persistence.tools.sessionconsole;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * Used in class list to show info on the class.
 */
public class ClassInfo {
    public ClassDescriptor descriptor;
    public boolean shouldShowPackage = true;

    public ClassInfo(ClassDescriptor descriptor, boolean shouldShowPackage) {
        this.descriptor = descriptor;
        this.shouldShowPackage = shouldShowPackage;
    }

    public String toString() {
        if (this.descriptor == null) {
            return "NULL";
        } else if (this.shouldShowPackage) {
            return this.descriptor.getJavaClass().getName();
        } else {
            return Helper.getShortClassName(this.descriptor.getJavaClass());
        }
    }
}
