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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;

/**
 * This tests if refresh with no cache and no identity map works.
 */
public class RefreshNoCacheWithNoIdentityMapTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public TestClass1 firstField;
    public TestClass1 test1;
    public ReadObjectQuery query;
    public ClassDescriptor descript;

    /**
     * RefreshNoCacheWithNoIdentityMapTest constructor comment.
     */
    public RefreshNoCacheWithNoIdentityMapTest() {
        setDescription("Tests if Refresh with no cache works with" + " No Identity Map");
    }

    public void test() {
        // read one object
        firstField = (TestClass1)getSession().readObject(TestClass1.class);

        // create a new object and assign it the primary key of the previous one that was read
        test1 = new TestClass1();
        test1.setPkey((firstField.getPkey()));

        // set up a read object query on TestClass1
        query = new ReadObjectQuery();
        query.setReferenceClass(TestClass1.class);

        //load the first object read  into the second one. Since no cache is used
        //this is how refresh is done.
        query.setSelectionObject(test1);
        query.loadResultIntoSelectionObject();
        getSession().executeQuery(query);

    }

    // compare both objects to ensure they are equal.
    protected void verify() {
        if (!compareObjects(test1, firstField)) {
            throw new TestException(" Refresh with no identity and no cache does not work");
        }
    }
}
