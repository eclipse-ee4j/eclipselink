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

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.insurance.*;

import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.sessions.*;

/**
 * EL Bug 245448 - Add regression tests for querying across  relationships using 
 * nested joining and DailyCacheInvalidationPolicy
 * - testing nested joining
 * - no indirection throughout
 * - daily expiration policy for cache invalidation
 * - foreign reference relationship on 1:M with batching + private owned
 * - 1:1 back-pointer for above not private owned, no batching, no joining
 */
public class QueryExecutionTimeSetOnBuildObjectTest extends TransactionalTestCase {

    protected QuerySQLTracker sqlTracker;
    
    private CacheInvalidationPolicy oldHolderCacheInvalidationPolicy;
    
    private boolean oldPoliciesShouldBatchRead;
    private boolean oldPoliciesShouldBePrivateOwned;
    
    private boolean oldPolicyHolderShouldBeBatchRead;
    private boolean oldPolicyHolderShouldBePrivateOwned;
    private int oldPolicyHolderJoining;
    
    private boolean oldAddressShouldBeBatchRead;
    private boolean oldAddressShouldBePrivateOwned;
    private int oldAddressJoining;

    public QueryExecutionTimeSetOnBuildObjectTest() {
        super();
        setDescription("Test that no duplicate sql is generated for nested join queries without indirection");
    }
    
    public void setup() {
        super.setup();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        
        ClassDescriptor holderDescriptor = getSession().getDescriptor(PolicyHolder.class);
        ClassDescriptor policyDescriptor = getSession().getDescriptor(Policy.class);
        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);

        // cache invalidation
        oldHolderCacheInvalidationPolicy = holderDescriptor.getCacheInvalidationPolicy();
        holderDescriptor.setCacheInvalidationPolicy(new DailyCacheInvalidationPolicy(0, 0, 0, 0));
        
        // PolicyHolder -> Policies batch reading
        OneToManyMapping policyHolderToPoliciesMapping = (OneToManyMapping) holderDescriptor.getMappingForAttributeName("policies");
        oldPoliciesShouldBatchRead = policyHolderToPoliciesMapping.shouldUseBatchReading();
        policyHolderToPoliciesMapping.setUsesBatchReading(true);
        
        // PolicyHolder -> Policies private owned
        oldPoliciesShouldBePrivateOwned = policyHolderToPoliciesMapping.isPrivateOwned();
        policyHolderToPoliciesMapping.setIsPrivateOwned(true);
      
        // Policy -> PolicyHolder batch reading
        OneToOneMapping policyToPolicyHolderMapping = (OneToOneMapping) policyDescriptor.getMappingForAttributeName("policyHolder");
        oldPolicyHolderShouldBeBatchRead = policyToPolicyHolderMapping.shouldUseBatchReading();
        policyToPolicyHolderMapping.setUsesBatchReading(false);
        
        // Policy -> PolicyHolder private owned
        oldPolicyHolderShouldBePrivateOwned = policyToPolicyHolderMapping.isPrivateOwned();
        policyToPolicyHolderMapping.setIsPrivateOwned(false);
        
        // Policy -> PolicyHolder joining
        oldPolicyHolderJoining = policyToPolicyHolderMapping.getJoinFetch();
        policyToPolicyHolderMapping.setJoinFetch(ForeignReferenceMapping.NONE);
        policyToPolicyHolderMapping.getDescriptor().reInitializeJoinedAttributes();
        
        // Address -> PolicyHolder batch reading
        OneToOneMapping addressToPolicyHolderMapping = (OneToOneMapping)addressDescriptor.getMappingForAttributeName("policyHolder");
        oldAddressShouldBeBatchRead = addressToPolicyHolderMapping.shouldUseBatchReading();
        addressToPolicyHolderMapping.setUsesBatchReading(false);
        
        // Address -> PolicyHolder joining
        oldAddressJoining = addressToPolicyHolderMapping.getJoinFetch();
        addressToPolicyHolderMapping.setJoinFetch(ForeignReferenceMapping.INNER_JOIN); // joining must be enabled on Address -> PolicyHolder
        addressToPolicyHolderMapping.getDescriptor().reInitializeJoinedAttributes();
        
        // Address -> PolicyHolder private owned
        oldAddressShouldBePrivateOwned = addressToPolicyHolderMapping.isPrivateOwned();
        addressToPolicyHolderMapping.setIsPrivateOwned(false);
        
        PolicyHolder holder = new PolicyHolder();
        holder.setFirstName("David");
        holder.setLastName("Minkoff");
        holder.setMale();
        holder.setSsn(515376);
        holder.setBirthDate(Helper.dateFromString("1979/03/25"));
        holder.setOccupation("Electrician");
        
        Address address = new Address();
        address.setCity("Calgary");
        address.setCountry("Canada");
        address.setState("AB");
        address.setStreet("Suite 840, 401 - 9th Avenue SW");
        address.setZipCode("T2P3C5");
        
        HealthPolicy healthPolicy1 = new HealthPolicy();
        healthPolicy1.setPolicyNumber(515377);
        healthPolicy1.setDescription("policy 1");
        healthPolicy1.setCoverageRate((float)1.5);
        healthPolicy1.setMaxCoverage(50000);
        
        HealthPolicy healthPolicy2 = new HealthPolicy();
        healthPolicy2.setPolicyNumber(515378);
        healthPolicy2.setDescription("policy 2");
        healthPolicy2.setCoverageRate((float)1.5);
        healthPolicy2.setMaxCoverage(50000);
        
        HealthPolicy healthPolicy3 = new HealthPolicy();
        healthPolicy3.setPolicyNumber(515379);
        healthPolicy3.setDescription("policy 3");
        healthPolicy3.setCoverageRate((float)1.5);
        healthPolicy3.setMaxCoverage(50000);
        
        holder.setAddress(address);
        holder.addPolicy(healthPolicy1);
        holder.addPolicy(healthPolicy2);
        holder.addPolicy(healthPolicy3);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerObject(holder);
        uow.commit();
        
        // create tracker object for query sql
        sqlTracker = new QuerySQLTracker(getSession());
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }
    
    public void test() {
        // query from address -> policyHolder -> Policy to trigger nested joins (no indirection)
        UnitOfWork uow = getSession().acquireUnitOfWork();
        Expression expression = new ExpressionBuilder().get("zipCode").equal("T2P3C5");
        uow.readObject(Address.class, expression); 
        uow.release();
    }
    
    public void verify() {
        // Insurance model expected SQL with configured adjustments in setup()
        // 1 x main select with joining from address -> policyholder
        // SELECT t1.CITY, t1.ZIPCODE, t1.STATE, t1.COUNTRY, t1.STREET, t1.SSN, t0.SSN, t0.L_NAME, t0.OCC, t0.BDATE, t0.F_NAME, t0.SEX FROM HOLDER t0, INS_ADDR t1 WHERE ((t1.ZIPCODE = T2P3C5) AND (t0.SSN = t1.SSN))
        // 1x policy type inheritance query
        // SELECT DISTINCT POL_TYPE FROM POLICY WHERE (SSN = 515376)
        // 1 x policy select based on type from policy inheritance query and policyholder pk
        // SELECT POL_ID, POL_TYPE, MAX_COV, DESCRIPT, SSN, COV_RATE FROM POLICY WHERE ((SSN = 515376) AND (POL_TYPE = 2))
        // 3 x claim selects from 3 policies read previously (no joining or batch reading configured)
        // SELECT DISTINCT CLM_TYPE FROM CLAIM WHERE (POL_ID = 515378)
        // SELECT DISTINCT CLM_TYPE FROM CLAIM WHERE (POL_ID = 515379)
        // SELECT DISTINCT CLM_TYPE FROM CLAIM WHERE (POL_ID = 515377)
        // 1 x PolicyHolder -> Child select (as mapped)
        // SELECT t0.CHILD_NAME FROM CHILDNAM t0 WHERE (t0.HOLDER_ID = 515376)
        // 1 x PolicyHolder -> Phone select (as mapped)
        // SELECT TYPE, PHONE_NUMBER, AREACODE FROM INS_PHONE WHERE (HOLDER_SSN = 515376)
        // = 8 statements expected

        int expectedNumberOfStatements = 8;
        int actualNumberOfSQLStatements = sqlTracker.getSqlStatements().size();

        if (expectedNumberOfStatements != actualNumberOfSQLStatements) {
            throw new TestErrorException("Expected " + expectedNumberOfStatements + " SQL statements - got " + actualNumberOfSQLStatements);
        }
    }
    
    public void reset() {
        super.reset();

        ClassDescriptor holderDescriptor = getSession().getDescriptor(PolicyHolder.class);
        ClassDescriptor policyDescriptor = getSession().getDescriptor(Policy.class);
        ClassDescriptor addressDescriptor = getSession().getDescriptor(Address.class);

        // cache invalidation
        oldHolderCacheInvalidationPolicy = holderDescriptor.getCacheInvalidationPolicy();
        holderDescriptor.setCacheInvalidationPolicy(oldHolderCacheInvalidationPolicy);
        
        // PolicyHolder -> Policies batch reading
        OneToManyMapping policiesMapping = (OneToManyMapping) holderDescriptor.getMappingForAttributeName("policies");
        policiesMapping.setUsesBatchReading(oldPoliciesShouldBatchRead);
        
        // PolicyHolder -> Policies private owned
        policiesMapping.setIsPrivateOwned(oldPoliciesShouldBePrivateOwned);
        
        // Policy -> PolicyHolder batch reading
        OneToOneMapping policyHolderMapping = (OneToOneMapping) policyDescriptor.getMappingForAttributeName("policyHolder");
        policyHolderMapping.setUsesBatchReading(oldPolicyHolderShouldBeBatchRead);
        
        // Policy -> PolicyHolder private owned
        policyHolderMapping.setIsPrivateOwned(oldPolicyHolderShouldBePrivateOwned);
        
        // Policy -> PolicyHolder joining
        policyHolderMapping.setJoinFetch(oldPolicyHolderJoining);
        policyHolderMapping.getDescriptor().reInitializeJoinedAttributes();

        // Address -> PolicyHolder batch reading
        OneToOneMapping addressToPolicyHolderMapping = (OneToOneMapping)addressDescriptor.getMappingForAttributeName("policyHolder");
        addressToPolicyHolderMapping.setUsesBatchReading(oldAddressShouldBeBatchRead);
        
        // Address -> PolicyHolder joining
        addressToPolicyHolderMapping.setJoinFetch(oldAddressJoining);
        addressToPolicyHolderMapping.getDescriptor().reInitializeJoinedAttributes();
        
        // Address -> PolicyHolder private owned
        addressToPolicyHolderMapping.setIsPrivateOwned(oldAddressShouldBePrivateOwned);

        sqlTracker.remove();
    }
    
}
