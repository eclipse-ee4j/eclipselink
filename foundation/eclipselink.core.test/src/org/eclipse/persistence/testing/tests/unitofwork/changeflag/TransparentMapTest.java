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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.models.collections.Menu;
import org.eclipse.persistence.testing.models.collections.Restaurant;


/**
 * This test checks for change tracking on Indirect Maps. And TopLink change
 * tracking of maps in general
 */
public class TransparentMapTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Menu transfer;
    public Menu transfer2;

    public Restaurant clone;
    public Restaurant clone2;

    public Object licenceKey;
    public Boolean licenceValue;

    public String licenceType;

    //stuff changed

    public TransparentMapTest() {
        setDescription("This test verifies that Tranparent Map works with change tracking");
    }

    public void reset() {
        if (getAbstractSession().isInTransaction()) {
            getAbstractSession().rollbackTransaction();
            getSession().getIdentityMapAccessor().initializeIdentityMaps();
        }
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("This test cannot be run through the remote.");
        }
        getAbstractSession().beginTransaction();
    }

    /*
     * This test creates an object and registers it with a unit of work.  It then serializes that
     * object and deserializes it.  Adds an object onto the origional then performs serialization
     * sequence again.  Then deepMergeClone is attempted and the results are compared to verify that
     * the merge worked.
     */

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        this.clone = 
                (Restaurant)uow.readObject(Restaurant.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("name").equal("Chez Abuse"));
        this.clone2 = 
                (Restaurant)uow.readObject(Restaurant.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("name").equal("Pedro's"));

        Set set = clone.getMenus().entrySet();
        Object object = set.iterator().next();
        Map.Entry entry = (Map.Entry)object;
        this.transfer = (Menu)clone.getMenus().values().iterator().next();
        this.transfer2 = (Menu)clone2.getMenus().values().iterator().next();

        Menu menu1 = this.transfer;
        Menu menu2 = this.transfer2;

        this.transfer = new Menu("LateAfternoon");
        this.transfer.setItems(menu1.getItems());

        this.transfer2 = new Menu("EarlyMorning");
        this.transfer2.setItems(menu2.getItems());

        this.clone2.removeMenu(menu2);

        HashMap menues = new HashMap(this.clone2.getMenus());
        menues.put(this.transfer.getKey(), this.transfer);
        this.transfer.setOwner(this.clone2);


        this.clone.removeMenu(menu1);
        this.clone.addMenu(this.transfer2);

        this.clone2.setMenus(menues);

        //test removal
        this.licenceKey = "Smoking License";
        clone.getLicenses().remove(this.licenceKey);

        //test addition
        clone.getLicenses().put("Site Licence", Boolean.TRUE);

        //test update
        this.licenceType = "Alcohol License";
        this.licenceValue = (Boolean)clone.getLicenses().get(licenceType);
        clone.getLicenses().put(licenceType, new Boolean((!(this.licenceValue).booleanValue())));

        uow.commit();
    }
    /*
     * Checks to see that the names of the updated version and the origional are the same
     */

    public void verify() {
        Restaurant cachedRestaurant = (Restaurant)getSession().readObject(this.clone);
        Restaurant cachedRestaurant2 = (Restaurant)getSession().readObject(this.clone2);
        Menu cachedTransfer = (Menu)getSession().readObject(this.transfer);
        Menu cachedTransfer2 = (Menu)getSession().readObject(this.transfer2);

        if (cachedRestaurant.getMenus().containsKey(cachedTransfer.getKey())) {
            throw new TestErrorException("Failed to track changes to Map without indirection");
        }
        if (!cachedRestaurant2.getMenus().containsKey(cachedTransfer.getKey())) {
            throw new TestErrorException("Failed to track changes when new entire map set without indirection");
        }

        if (cachedRestaurant2.getMenus().containsKey(cachedTransfer2.getKey())) {
            throw new TestErrorException("Failed to track changes when new entire map set without indirection");
        }
        if (!cachedRestaurant.getMenus().containsKey(cachedTransfer2.getKey())) {
            throw new TestErrorException("Failed to merge changes to Map with no indirection");
        }

        if (clone.getLicenses().containsKey(this.licenceKey)) {
            throw new TestErrorException("Failed to track remove from Indirect Map");
        }
        if (!clone.getLicenses().containsKey("Site Licence")) {
            throw new TestErrorException("Failed to track add to Indirect Map");
        }

        if (clone.getLicenses().get(this.licenceType).equals(this.licenceValue)) {
            throw new TestErrorException("Failed to track change to value of Indirect Map");
        }

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        cachedRestaurant = (Restaurant)getSession().readObject(this.clone);
        cachedRestaurant2 = (Restaurant)getSession().readObject(this.clone2);
        cachedTransfer = (Menu)getSession().readObject(this.transfer);
        cachedTransfer2 = (Menu)getSession().readObject(this.transfer2);

        if (cachedRestaurant.getMenus().containsKey(cachedTransfer.getKey())) {
            throw new TestErrorException("Failed to track changes to Map without indirection on database");
        }
        if (!cachedRestaurant2.getMenus().containsKey(cachedTransfer.getKey())) {
            throw new TestErrorException("Failed to track changes when new entire map set without indirection on database");
        }

        if (cachedRestaurant2.getMenus().containsKey(cachedTransfer2.getKey())) {
            throw new TestErrorException("Failed to track changes when new entire map set without indirection on database");
        }
        if (!cachedRestaurant.getMenus().containsKey(cachedTransfer2.getKey())) {
            throw new TestErrorException("Failed to merge changes to Map with no indirection on database");
        }

        if (clone.getLicenses().containsKey(this.licenceKey)) {
            throw new TestErrorException("Failed to track remove from Indirect Map on database");
        }
        if (!clone.getLicenses().containsKey("Site Licence")) {
            throw new TestErrorException("Failed to track add to Indirect Map on database");
        }

        if (clone.getLicenses().get(this.licenceType).equals(this.licenceValue)) {
            throw new TestErrorException("Failed to track change to value of Indirect Map on database");
        }

    }
}
