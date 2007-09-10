/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.descriptors.*;

/**
 * This type added to facilitate making amendments to the interface descriptors
 * and keeping these amendments organised and collected in one spot
 */
public class Admendments {

    public Admendments() {
        super();
    }

    public static void addToManagerialJobDescriptor(ClassDescriptor des) {
        des.getInterfacePolicy().addParentInterface(Job.class);
    }
}
