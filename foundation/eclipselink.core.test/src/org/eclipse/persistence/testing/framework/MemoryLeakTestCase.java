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
package org.eclipse.persistence.testing.framework;

import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;

/**
 * Generic test for testing for memory leaks.
 */
public abstract class MemoryLeakTestCase extends TestCase {

    /** Stores objects that should garbage collect. */
    protected List<WeakReference> weakList;
    /** Set number of objects allowed not to garbage collect. */
    protected int threshold = 0;

    /**
     * Add a weak reference to the object to ensure it garbage collects.
     */
    public void addWeakReference(Object reference) {
        this.weakList.add(new WeakReference(reference));
    }

    /**
     * Add a weak reference to the object to ensure it garbage collects.
     */
    public void addWeakReferences(List references) {
        for (Object ref : references) {
            addWeakReference(ref);
        }
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setup() {
       this.weakList = new ArrayList();
       getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void verify() {
        forceGC();
        int count = 0;
        for (WeakReference ref : this.weakList) {
            if (ref.get() != null) {
                count++;
            }
        }
        if (count > this.threshold) {
            throwError("Objects did not garbage collect: " + count);
        }
    }
}
