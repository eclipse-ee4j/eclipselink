/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

/**
 * INTERNAL:
 * Use to sort vectors of strings.
 * Avoid using this class as sun.misc is not part of many VM's like Netscapes.
 */
public class DescriptorCompare implements Comparator<ClassDescriptor>, Serializable {

    @Serial
    private static final long serialVersionUID = -2792350655245140468L;

    /**
     * Default constructor.
     */
    public DescriptorCompare() {
    }

    @Override
    public int compare(ClassDescriptor arg1, ClassDescriptor arg2) {
        return arg1.getJavaClassName().compareTo(arg2.getJavaClassName());
    }
}
