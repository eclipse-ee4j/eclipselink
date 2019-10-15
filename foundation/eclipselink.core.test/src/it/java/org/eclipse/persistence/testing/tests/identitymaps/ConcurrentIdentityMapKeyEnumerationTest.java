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
package org.eclipse.persistence.testing.tests.identitymaps;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * Read objects into the cache, force garbage collection, see that the cache is empty.
 */
public class ConcurrentIdentityMapKeyEnumerationTest extends TestCase {
    protected boolean shouldRun;
    protected Object storedObject;
    protected FullIdentityMap identityMap;

    public ConcurrentIdentityMapKeyEnumerationTest() {
        identityMap = new FullIdentityMap(1, null, null, false);
    }

    public void reset() {
        shouldRun = false;
    }

    public void setup() {
        storedObject = getSession().readObject(Employee.class);
        shouldRun = true;
    }

    public void test() {
        Thread enumeration = new Thread() {
            public void run() {
                while (shouldRun) {
                    Enumeration keys = identityMap.keys();
                    while (keys.hasMoreElements()) {
                        try {
                            Object key = keys.nextElement();
                        } catch (Exception ex) {
                            shouldRun = false;
                            synchronized (storedObject) {
                                storedObject.notifyAll();
                            }
                        }
                    }
                }
            }
        };
        enumeration.start();
        Thread remover = new Thread() {
            public void run() {
                Vector pk = new Vector(1);
                pk.add(((Employee)storedObject).getId());
                while (shouldRun) {
                    identityMap.put(pk, storedObject, null, 0);
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException ex) {
                        shouldRun = false;
                        synchronized (storedObject) {
                            storedObject.notifyAll();
                        }
                    }
                    identityMap.remove(pk, null);
                }
            }
        };
        remover.start();
        try {
            synchronized (storedObject) {
                storedObject.wait(30000);
            }
        } catch (InterruptedException ex) {
        }
        if (shouldRun == false) {
            throw new TestErrorException("Failed to ignore nulls while enumeration identitymap");
        } else {
            shouldRun = false;
        }
    }
}
