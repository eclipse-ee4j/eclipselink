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
