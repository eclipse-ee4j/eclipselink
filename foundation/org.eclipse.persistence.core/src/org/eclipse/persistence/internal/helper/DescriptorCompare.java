/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import java.util.Comparator;

import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * INTERNAL:
 * Use to sort vectors of strings.
 * Avoid using this class as sun.misc is not part of many VM's like Netscapes.
 */
public class DescriptorCompare implements Comparator {
    public int compare(Object arg1, Object arg2) {
        return ((ClassDescriptor)arg1).getJavaClassName().compareTo(((ClassDescriptor)arg2).getJavaClassName());
    }
}
