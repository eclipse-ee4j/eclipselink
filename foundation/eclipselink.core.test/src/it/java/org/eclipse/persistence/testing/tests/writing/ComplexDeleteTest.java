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
package org.eclipse.persistence.testing.tests.writing;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;

/**
 * PUBLIC: A version of DeleteObjectTest which can handle deleting dependant
 * yet non-private parts.
 * Required for bug 3019934, tests that deleting multiple objects and
 * @author Stephen McRitchie
 */
public class ComplexDeleteTest extends ComplexUpdateTest {
    protected Vector dependants;

    public ComplexDeleteTest() {
        this(null, new Vector(2));
    }

    public ComplexDeleteTest(Object originalObject) {
        this(originalObject, new Vector(2));
    }

    public ComplexDeleteTest(Object originalObject, Vector dependants) {
        super(originalObject);
        this.dependants = dependants;
        usesUnitOfWork = true;
    }

    protected void changeObject() {
        UnitOfWork uow = (UnitOfWork)getSession();

        for (Enumeration enumtr = getDependants().elements(); enumtr.hasMoreElements();) {
            Object dependant = enumtr.nextElement();
            if (dependant instanceof ReadAllQuery) {
                uow.deleteAllObjects((Vector)uow.executeQuery((ReadAllQuery)dependant));
            } else if (dependant instanceof ReadObjectQuery) {
                uow.deleteObject(uow.executeQuery((ReadObjectQuery)dependant));
            } else {
                ReadObjectQuery query = new ReadObjectQuery(dependant.getClass());
                query.setSelectionObject(dependant);
                Object clone = uow.executeQuery(query);
                uow.deleteObject(clone);
            }
        }
        uow.deleteObject(workingCopy);
    }

    public Vector getDependants() {
        return dependants;
    }

    public void setDependents(Vector dependants) {
        this.dependants = dependants;
    }

    protected void verify() {
        this.objectToBeWritten = null;
        super.verify();
    }
}
