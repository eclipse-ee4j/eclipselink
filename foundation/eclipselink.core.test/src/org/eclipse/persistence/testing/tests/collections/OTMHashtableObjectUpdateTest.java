/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.framework.*;

public class OTMHashtableObjectUpdateTest extends org.eclipse.persistence.testing.framework.WriteObjectTest {
    public OTMHashtableObjectUpdateTest() {
        super();
    }

    public OTMHashtableObjectUpdateTest(Object originalObject) {
        super(originalObject);
    }

    protected void setup() {
        beginTransaction();
        //super.setup();
    }

    protected void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        originalObject = getSession().readObject(Restaurant.class, new ExpressionBuilder().anyOf("menus").get("type").equalsIgnoreCase("dinner"));
        Restaurant rest = (Restaurant)uow.registerObject(originalObject);
        Hashtable menus = (Hashtable)rest.getMenus();
        for (Enumeration enumtr = menus.elements(); enumtr.hasMoreElements();) {
            Menu menu = (Menu)enumtr.nextElement();
            if (menu.getType().equalsIgnoreCase("Dinner")) {
                menus.remove(menu.getType());
                menu.setType("Breakfast");
                menus.put(menu.getType(), menu);
            }
        }
        uow.commit();
    }

    /**
     * Verify if the objects match completely through allowing the session to use the descriptors.
     * This will compare the objects and all of their privately owned parts.
     */
    protected void verify() {
        Restaurant rest = (Restaurant)originalObject;
        Hashtable menus = (Hashtable)rest.getMenus();
        for (Enumeration enumtr = menus.keys(); enumtr.hasMoreElements();) {
            Object menuKey = enumtr.nextElement();
            if (menuKey.toString().equalsIgnoreCase("Dinner")) {
                throw new TestErrorException(" Key of Hashtable is not updated ---> Not Fix Yet");
            }
        }
    }
}
