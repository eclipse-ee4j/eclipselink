/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.transparentindirection;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.transparentindirection.*;

public class NullCollectionTest extends TestCase {

    public AbstractOrder testOrder;
    
    public void reset() {
        rollbackTransaction();
    }

    public void setup() {
        beginTransaction();

        this.testOrder = new Order("Tommy 2Tone");

        this.testOrder.addSalesRep(new SalesRep("Slippery Sam"));
        this.testOrder.addSalesRep(new SalesRep("Slippery Sam's Brother"));
        this.testOrder.addSalesRep(new SalesRep("Slippery Samantha"));

        this.testOrder.addSalesRep2(new SalesRep("Edgar"));
        this.testOrder.addSalesRep2(new SalesRep("Rick"));
        this.testOrder.addSalesRep2(new SalesRep("Dan"));
        this.testOrder.addSalesRep2(new SalesRep("Johnny"));

        this.testOrder.addContact("Tommy");
        this.testOrder.addContact("Pato");
        this.testOrder.addContact("Ranking Roger");

        this.testOrder.addContact2("Jimmy");
        this.testOrder.addContact2("Robert");
        this.testOrder.addContact2("John");
        this.testOrder.addContact2("Keith");

        this.testOrder.addLine(new OrderLine("Specials", 1));
        this.testOrder.addLine(new OrderLine("General Public", 3));
        this.testOrder.addLine(new OrderLine("Madness", 1));

        this.testOrder.setTotal(765);
        this.testOrder.total2 = 987;
        
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(this.testOrder);
        uow.commit();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    public void test() {
        Session session = getSession();
        UnitOfWork uow = session.acquireUnitOfWork();
        Order order = (Order)uow.readObject(Order.class);
        order.salesReps = null;
        try {
            uow.commit();
        } catch (org.eclipse.persistence.exceptions.DatabaseException ex) {
            throw new TestErrorException(ex.toString());
        }
    }

}
