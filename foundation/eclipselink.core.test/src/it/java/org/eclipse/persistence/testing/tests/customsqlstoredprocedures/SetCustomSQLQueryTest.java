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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.queries.*;
import java.util.*;

public class SetCustomSQLQueryTest extends org.eclipse.persistence.testing.framework.TransactionalTestCase {
    private DataModifyQuery myDataModifyQueryObj;
    private Vector myArgs4Query;
    private boolean hasArguments;

    public SetCustomSQLQueryTest(String s) {
        super();
        setName(getName() + "(" + s + ")");
        setDescription("This tests the execution of the custom SQL string");
        setMyDataModifyQueryObj(new DataModifyQuery(s));
        setHasArguments(false);

    }

    public SetCustomSQLQueryTest(String s, Vector v, Vector myV) {
        this(s);
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            String myArgument = (String)e.nextElement();
            getMyDataModifyQueryObj().addArgument(myArgument);
        }
        setMyArgs4Query(myV);
        setHasArguments(true);
    }

    public boolean getHasArguments() {
        return hasArguments;
    }

    public Vector getMyArgs4Query() {
        return myArgs4Query;
    }

    public org.eclipse.persistence.queries.DataModifyQuery getMyDataModifyQueryObj() {
        return myDataModifyQueryObj;
    }

    protected void setHasArguments(boolean newValue) {
        this.hasArguments = newValue;
    }

    protected void setMyArgs4Query(Vector newValue) {
        this.myArgs4Query = newValue;
    }

    protected void setMyDataModifyQueryObj(org.eclipse.persistence.queries.DataModifyQuery newValue) {
        this.myDataModifyQueryObj = newValue;
    }

    public void test() {
        //just execute the SQL
        if (hasArguments) {
            getSession().executeQuery(getMyDataModifyQueryObj(), getMyArgs4Query());
        } else {
            getSession().executeQuery(getMyDataModifyQueryObj());
        }
    }
}
