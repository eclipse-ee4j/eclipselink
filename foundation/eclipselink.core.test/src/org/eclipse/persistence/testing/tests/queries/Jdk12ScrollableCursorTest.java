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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.internal.helper.*;

/**
 * Test the scrollable cursor feature for jdk1.1 by performing a cursor read on the database
 * and iterating through the elements.
 */
public class Jdk12ScrollableCursorTest extends TestCase {
    protected Vector cursoredQueryObjects;
    protected boolean TYPE_SCROLL_INSENSITIVE_isSupported;
    protected boolean CONCUR_UPDATABLE_isSupported;

    public Jdk12ScrollableCursorTest() {

        setDescription("This test tests ScrollableCursor in jdk1.1");
    }

    protected void setup() {
        if (getSession().getPlatform().isAccess() || getSession().getPlatform().isTimesTen()) {
            throw new TestWarningException("ScrollableCursor is not supported on this platform");
        }
        if (getSession().getPlatform().isDB2()) {
            throw new TestWarningException("java.sql.SQLException: [IBM][JDBC Driver] CLI0626E" + Helper.cr() + 
                                           "Updatable result set is not supported in this version of DB2 JDBC 2.0 driver.");
        }
        TYPE_SCROLL_INSENSITIVE_isSupported = true;
        CONCUR_UPDATABLE_isSupported = true;
        if(getSession().getPlatform().isSQLServer()) {
            // In case either TYPE_SCROLL_INSENSITIVE or CONCUR_UPDATABLE used  
            // MS SQL Server  Version: 9.00.2050;  MS SQL Server 2005 JDBC Driver  Version: 1.2.2828.100 throws exception:
            // com.microsoft.sqlserver.jdbc.SQLServerException: The cursor type/concurrency combination is not supported.
            TYPE_SCROLL_INSENSITIVE_isSupported = false;
            CONCUR_UPDATABLE_isSupported = false;
        }

        this.cursoredQueryObjects = new Vector();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {

        ReadAllQuery query = new ReadAllQuery();

        query.setReferenceClass(Employee.class);
        if(TYPE_SCROLL_INSENSITIVE_isSupported && CONCUR_UPDATABLE_isSupported) {
            query.useScrollableCursor();
        } else {
            ScrollableCursorPolicy policy = new ScrollableCursorPolicy();
            if(!TYPE_SCROLL_INSENSITIVE_isSupported) {
                policy.setResultSetType(ScrollableCursorPolicy.TYPE_SCROLL_SENSITIVE);
            }
            if(!CONCUR_UPDATABLE_isSupported) {
                policy.setResultSetConcurrency(ScrollableCursorPolicy.CONCUR_READ_ONLY);
            }
            policy.setPageSize(10);
            query.useScrollableCursor(policy);
        }
        ScrollableCursor cursor = (ScrollableCursor)getSession().executeQuery(query);

        while (cursor.hasMoreElements()) {
            this.cursoredQueryObjects.addElement(cursor.nextElement());
        }
    }
}
