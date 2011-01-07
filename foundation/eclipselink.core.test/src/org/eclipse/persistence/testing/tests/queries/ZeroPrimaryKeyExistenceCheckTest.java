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
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.internal.helper.Helper;

/**
 * For simple single table objects that have a primary key set to 0, it is to be expected that
 * there will be 1 insert statement per object.
 * <p>
 * An extra existence check read would be encountered if a PK is set to 0 where the object is 
 * set to check for existence.
 * <p>
 * The Helper.isZeroValidPrimaryKey value is enabled/disabled and tested
 * @author dminsky
 */
public class ZeroPrimaryKeyExistenceCheckTest extends TransactionalTestCase {

    protected boolean previousIsZeroValidPrimaryKeyValue;
    protected boolean isZeroValidPrimaryKeyValue;
    protected List objectsToBeWritten;
    protected QuerySQLTracker sqlTracker;

    public ZeroPrimaryKeyExistenceCheckTest(boolean isZeroValidPrimaryKey) {
        super();
        this.isZeroValidPrimaryKeyValue = isZeroValidPrimaryKey;
        setDescription("Test for checking 0 is valid primary key");
    }
    
    @SuppressWarnings("deprecation")
    public void setup() {
        super.setup();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        // zero valid primary key
        previousIsZeroValidPrimaryKeyValue = Helper.isZeroValidPrimaryKey;
        Helper.isZeroValidPrimaryKey = this.isZeroValidPrimaryKeyValue;
        // create tracker object for query SQL
        sqlTracker = new QuerySQLTracker(getSession());
    }
    
    public void test() {
        PolicyHolder holder = new PolicyHolder();
        holder.setFirstName("David");
        holder.setLastName("Minkoff");
        holder.setMale();
        holder.setSsn(0l); // 0 PK
        holder.setBirthDate(Helper.dateFromString("1979/03/25"));
        holder.setOccupation("Electrician");

        HealthPolicy healthPolicy = new HealthPolicy();
        healthPolicy.setPolicyNumber(0); // 0 PK
        healthPolicy.setDescription("Not bad body");
        healthPolicy.setCoverageRate((float)1.5);
        healthPolicy.setMaxCoverage(50000);
        
        HealthClaim healthClaim1 = new HealthClaim();
        healthClaim1.setId(0); // 0 PK
        healthClaim1.setDisease("Flu");
        healthClaim1.setAmount(1000);

        healthPolicy.addClaim(healthClaim1);
        
        holder.addPolicy(healthPolicy);

        objectsToBeWritten = new ArrayList();
        objectsToBeWritten.add(holder);
        objectsToBeWritten.add(healthPolicy);
        objectsToBeWritten.add(healthClaim1);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(holder);
        uow.commit();
    }
    
    public void verify() {
        int expectedNumberOfStatements = 0;
        if (isZeroValidPrimaryKeyValue) {
            expectedNumberOfStatements = objectsToBeWritten.size() + 2; // 2 x existence checks: policy and claim  
        } else {
            expectedNumberOfStatements = (objectsToBeWritten.size()); // no existence checks
        }

        int actualNumberOfSQLStatements = sqlTracker.getSqlStatements().size();

        if (expectedNumberOfStatements != actualNumberOfSQLStatements) {
            throw new TestErrorException("Expected " + expectedNumberOfStatements + " SQL statements - got " + actualNumberOfSQLStatements);
        }
    }
    
    @SuppressWarnings("deprecation")
    public void reset() {
        super.reset();
        // reset global values changed
        Helper.isZeroValidPrimaryKey = previousIsZeroValidPrimaryKeyValue;
        sqlTracker.remove();
        if (objectsToBeWritten != null) {
            objectsToBeWritten.clear();
        }
    }
    
    public String toString() {
        return super.toString() + " (isZeroValidPrimaryKey: " + isZeroValidPrimaryKeyValue + ")";
    }
    
}
