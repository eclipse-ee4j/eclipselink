/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.helper;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * INTERNAL:
 * Use to sort vectors of strings.
 * Avoid using this class as sun.misc is not part of many VM's like Netscapes.
 */
public class DescriptorCompare implements Comparator, Serializable {

    private static final long serialVersionUID = -2792350655245140468L;

    @Override
    public int compare(Object arg1, Object arg2) {
        return ((ClassDescriptor)arg1).getJavaClassName().compareTo(((ClassDescriptor)arg2).getJavaClassName());
    }
}
