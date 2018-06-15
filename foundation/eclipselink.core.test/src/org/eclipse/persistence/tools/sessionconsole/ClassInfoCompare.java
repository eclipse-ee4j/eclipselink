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

import java.util.Comparator;

/**
 * Use to sort vectors of descriptors.
 */
public class ClassInfoCompare implements Comparator {
    public int compare(Object arg1, Object arg2) {
        return ((ClassInfo)arg1).descriptor.getJavaClass().getName().compareTo(((ClassInfo)arg2).descriptor.getJavaClass().getName());
    }
}
