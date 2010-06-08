/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
