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
package org.eclipse.persistence.testing.tests.collections;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.framework.*;

public class OTMPrivateOwnedDeleteObjectTest extends AutoVerifyTestCase {
    public OTMPrivateOwnedDeleteObjectTest() {
        super();
    }

    public void reset() {
        rollbackTransaction();
    }

    protected void setup() {
        beginTransaction();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Restaurant res = (Restaurant)getSession().readObject(Restaurant.class);
        Restaurant resClone = (Restaurant)uow.registerObject(res);
        resClone.getMenus();
        getSession().getIdentityMapAccessor().initializeIdentityMap(Menu.class);
        resClone.setMenus(new Hashtable());
        uow.commit();
        if (!res.getMenus().isEmpty()) {
            throw new TestErrorException("Not All were Deleted");
        }
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
    }
}
