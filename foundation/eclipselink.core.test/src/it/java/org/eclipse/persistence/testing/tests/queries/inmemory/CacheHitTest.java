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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import java.io.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.logging.*;

/**
 * Test selecting using an object's primary key to ensure that it does not go to the databaase.
 */
public class CacheHitTest extends TestCase {
    protected Object originalObject;
    protected Object objectToRead;
    protected Object objectRead;
    protected SessionLog oldLog;
    protected StringWriter tempStream;

    public CacheHitTest() {
        setDescription("This test verifies that selecting with primary key obtains a cache hit.");
    }

    public CacheHitTest(Object originalObject) {
        this();
        setName(getName() + "(" + originalObject.getClass() + ")");
        this.originalObject = originalObject;
    }

    /**
     * Load the object into the cache.
     */
    protected void loadObjectIntoCache() {
        objectToRead = getSession().readObject(originalObject);
    }

    /**
     * Query the object by primary key.
     */
    protected Object readObject() {
        return getSession().readObject(originalObject);
    }

    public void reset() {
        if (oldLog != null) {
            getSession().setSessionLog(oldLog);
        }
    }

    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        loadObjectIntoCache();

        oldLog = getSession().getSessionLog();
        tempStream = new StringWriter();

        DefaultSessionLog newLog = new DefaultSessionLog();
        newLog.setWriter(tempStream);
        newLog.setLevel(SessionLog.FINE);
        getSession().setSessionLog(newLog);
    }

    public void test() {
        objectRead = readObject();
    }

    protected void verify() {
        if (objectRead != objectToRead) {
            throw new TestErrorException("Expecting: " + objectToRead + " retrieved: " + objectRead);
        }

        if (tempStream.toString().length() > 0) {
            throw new TestErrorException("The read went to the database, but should not have, '" + tempStream.toString() + "'");
        }
    }
}
