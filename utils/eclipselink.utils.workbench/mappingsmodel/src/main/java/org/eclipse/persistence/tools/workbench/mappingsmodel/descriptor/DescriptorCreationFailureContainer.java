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
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * An "DescriptorCreationFailureListener" that simply gathers up the
 * failures and makes them available for later investigation.
 */

public class DescriptorCreationFailureContainer
    implements DescriptorCreationFailureListener{

    private Collection failureEvents = new Vector();


    public void descriptorCreationFailure(DescriptorCreationFailureEvent e) {
        this.failureEvents.add(e);

    }

    public Iterator failureEvents() {
        return this.failureEvents.iterator();
    }

    /** return the names of the classes that failed to have descriptors created */
    public Iterator failureClassNames() {
        return new TransformationIterator(failureEvents()) {
            protected Object transform(Object next) {
                return ((DescriptorCreationFailureEvent) next).getClassName();
            }
        };
    }

    /** return the source when the failure occurred */
    public String failureResourceStringKeyForClassNamed(String className) {
        for(Iterator failureEvents = failureEvents(); failureEvents.hasNext(); ) {
            DescriptorCreationFailureEvent event = (DescriptorCreationFailureEvent) failureEvents.next();
            if (event.getClassName().equals(className)) {
                return event.getResourceStringKey();
            }
        }
        return null;
    }

    /** return whether any failures occurred */
    public boolean isEmpty() {
        return this.failureEvents.isEmpty();
    }

    /** return whether any failures occurred */
    public boolean containsFailures() {
        return ! isEmpty();
    }
}
