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
 *     ailitchev - bug 251732: query on interface fails
 ******************************************************************************/

package org.eclipse.persistence.testing.tests.interfaces;

import java.util.Vector;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class ReadAllConformInUowTest extends TestCase {

    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();

        Email email = new Email();
        Email emailClone = (Email)uow.registerObject(email);
        emailClone.setAddress("info@Conform.com");
        
        Phone phone = new Phone();
        Phone phoneClone = (Phone)uow.registerObject(phone); 
        phoneClone.setNumber("123 456-7890");
        
        uow.assignSequenceNumbers();
        
        Number emailId = emailClone.getId();
        Number phoneId = phoneClone.getId();
        Vector ids = new Vector(2);
        ids.add(emailId);
        ids.add(phoneId);
        
        ReadAllQuery query = new ReadAllQuery(Contact.class);
        Expression exp = query.getExpressionBuilder().get("id").in(ids);
        query.setSelectionCriteria(exp);
        query.conformResultsInUnitOfWork();
        
        Vector result;
        try {
            result = (Vector)uow.executeQuery(query);
        } finally {
            uow.release();
        }
        
        // Note that in theory there could be up to 4 objects returned:
        // one of each class corresponding to each id - therefore check directly for our objects
        // instead of checking the number of the objects returned.
        String errorMsg = "";
        if(!result.contains(emailClone)) {
            errorMsg += "The result doesn't contain Email; ";
        }
        if(!result.contains(phoneClone)) {
            errorMsg += "The result doesn't contain Phone.";
        }
        if(errorMsg.length() > 0) {
            throw new TestErrorException(errorMsg);
        }
    }
}
