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
package org.eclipse.persistence.testing.tests.jpql;

import java.util.*;
import org.eclipse.persistence.testing.framework.*;

public class JPQLParameterTestCase extends JPQLTestCase {
    private Vector arguments;
    private Vector argumentNames;

    public void test() {
        try {
            populateQuery();
            getSession().logMessage("Running EJBQL -> " + getEjbqlString());
            setReturnedObjects(getSession().executeQuery(getQuery(), getArguments()));
        } catch (Exception e) {
            throw new TestErrorException(e.getMessage());
        }
    }

    public boolean hasArguments() {
        return (arguments != null) && (!arguments.isEmpty());
    }

    public Vector getArguments() {
        return arguments;
    }

    public Vector getArgumentNames() {
        return argumentNames;
    }

    //populate the arguments of the query
    public void populateQuery() {
        if (getArgumentNames() == null) {
            return;
        }
        getQuery().setArguments(getArgumentNames());
    }

    public void setArguments(Vector theArguments) {
        this.arguments = theArguments;
    }

    public void setArgumentNames(Vector theArgumentNames) {
        this.argumentNames = theArgumentNames;
    }
}
