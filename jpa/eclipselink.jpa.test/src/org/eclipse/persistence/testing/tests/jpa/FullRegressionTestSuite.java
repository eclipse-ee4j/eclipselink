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
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     01/19/2010-2.1 Guy Pelletier 
 *       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJPAJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.AdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.CacheImplJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.CallbackEventJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.EntityManagerJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.SQLResultSetMappingTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.JoinedAttributeAdvancedJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryMultipleReturnTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.ExtendedPersistenceContextJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryConstructorExpressionTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.OptimisticConcurrencyJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.advanced.compositepk.AdvancedCompositePKJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.concurrency.ConcurrencyTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.concurrency.LifecycleJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.fetchgroup.AdvancedFetchGroupJunitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.ReportQueryAdvancedJUnitTest;
import org.eclipse.persistence.testing.tests.jpa.advanced.PessimisticLockingExtendedScopeTestSuite;

import org.eclipse.persistence.testing.tests.jpa.inheritance.LifecycleCallbackJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inheritance.DeleteAllQueryInheritanceJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inheritance.EntityManagerJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.inheritance.MixedInheritanceJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.inheritance.JoinedAttributeInheritanceJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inheritance.TablePerClassInheritanceJUnitTest;

import org.eclipse.persistence.testing.tests.jpa.inherited.OrderedListJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.OrderedListAttributeChangeTrackingJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.InheritedModelJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.InheritedCallbacksJunitTest;
import org.eclipse.persistence.testing.tests.jpa.inherited.EmbeddableSuperclassJunitTest;

import org.eclipse.persistence.testing.tests.jpa.relationships.EMQueryJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.ExpressionJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.RelationshipModelJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.UniAndBiDirectionalMappingTestSuite;
import org.eclipse.persistence.testing.tests.jpa.relationships.VirtualAttributeTestSuite;

import org.eclipse.persistence.testing.tests.jpa.validation.ValidationTestSuite;
import org.eclipse.persistence.testing.tests.jpa.validation.QueryParameterValidationTestSuite;


import org.eclipse.persistence.testing.tests.jpa.jpaadvancedproperties.JPAAdvPropertiesJUnitTestCase;
import org.eclipse.persistence.testing.tests.jpa.jpql.AdvancedQueryTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexAggregateTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLComplexTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLDateTimeTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLExamplesTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLInheritanceTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLModifyTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLParameterTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLSimpleTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLValidationTestSuite;
import org.eclipse.persistence.testing.tests.jpa.jpql.JUnitNativeQueryTestSuite;
import org.eclipse.persistence.testing.tests.jpa.xml.EntityMappingsJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.cacheable.CacheableModelJunitTest;
import org.eclipse.persistence.testing.tests.jpa.cascadedeletes.CascadeDeletesJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.ddlgeneration.DDLGenerationJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.delimited.DelimitedPUTestSuite;
import org.eclipse.persistence.testing.tests.jpa.deployment.CompositeEnumerationTest;

public class FullRegressionTestSuite extends TestSuite {
    
    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("FullRegressionTestSuite");

        // Advanced model
        TestSuite suite = new TestSuite();
        suite.setName("advanced");
        suite.addTest(LifecycleJUnitTest.suite());        
        suite.addTest(ConcurrencyTest.suite());
        suite.addTest(CacheImplJUnitTest.suite());
        suite.addTest(CallbackEventJUnitTestSuite.suite());
        suite.addTest(EntityManagerJUnitTestSuite.suite());
        suite.addTest(SQLResultSetMappingTestSuite.suite());
        suite.addTest(JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(ReportQueryAdvancedJUnitTest.suite());
        suite.addTest(ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(AdvancedJPAJunitTest.suite());
        suite.addTest(AdvancedJunitTest.suite());
        suite.addTest(AdvancedCompositePKJunitTest.suite());
        suite.addTest(AdvancedFetchGroupJunitTest.suite());
        suite.addTest(PessimisticLockingExtendedScopeTestSuite.suite());
        fullSuite.addTest(suite);

        // FieldAccess advanced model
        suite = new TestSuite();
        suite.setName("fieldaccess");
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.NamedNativeQueryJUnitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.CallbackEventJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.EntityManagerJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.SQLResultSetMappingTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.JoinedAttributeAdvancedJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryMultipleReturnTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ExtendedPersistenceContextJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.ReportQueryConstructorExpressionTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.OptimisticConcurrencyJUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.AdvancedJPAJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.advanced.AdvancedJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.UniAndBiDirectionalMappingTestSuite.suite());
        suite.addTestSuite(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.ExpressionJUnitTestSuite.class);
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.VirtualAttributeTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.fieldaccess.relationships.JAXBTestSuite.suite());
        fullSuite.addTest(suite);

        // Inheritance model.
        suite = new TestSuite();
        suite.setName("inheritance");
        suite.addTest(LifecycleCallbackJunitTest.suite());
        suite.addTest(DeleteAllQueryInheritanceJunitTest.suite());
        suite.addTest(EntityManagerJUnitTestCase.suite());
        suite.addTest(MixedInheritanceJUnitTestCase.suite());
        suite.addTest(JoinedAttributeInheritanceJunitTest.suite());
        suite.addTest(TablePerClassInheritanceJUnitTest.suite());
        fullSuite.addTest(suite);

        // Inherited model.
        suite = new TestSuite();
        suite.setName("inherited");
        suite.addTest(OrderedListJunitTest.suite());
        suite.addTest(OrderedListAttributeChangeTrackingJunitTest.suite());
        suite.addTest(InheritedModelJunitTest.suite());
        suite.addTest(InheritedCallbacksJunitTest.suite());
        suite.addTest(EmbeddableSuperclassJunitTest.suite());
        fullSuite.addTest(suite);

        // Relationship model.
        suite = new TestSuite();
        suite.setName("relationships");
        suite.addTestSuite(EMQueryJUnitTestSuite.class);
        suite.addTestSuite(ExpressionJUnitTestSuite.class);
        suite.addTest(VirtualAttributeTestSuite.suite());
        suite.addTest(ValidationTestSuite.suite());
        suite.addTest(QueryParameterValidationTestSuite.suite());
        suite.addTest(UniAndBiDirectionalMappingTestSuite.suite());
        suite.addTest(RelationshipModelJUnitTestSuite.suite());
        fullSuite.addTest(suite);

        // JPQL testing model.
        suite = new TestSuite();
        suite.setName("jpql");
        suite.addTest(JUnitJPQLUnitTestSuite.suite());
        suite.addTest(JUnitJPQLSimpleTestSuite.suite());
        suite.addTest(JUnitJPQLComplexTestSuite.suite());
        suite.addTest(JUnitJPQLInheritanceTestSuite.suite());
        suite.addTest(JUnitJPQLValidationTestSuite.suite());
        suite.addTest(JUnitJPQLComplexAggregateTestSuite.suite());
        suite.addTest(JUnitJPQLDateTimeTestSuite.suite());
        suite.addTest(JUnitJPQLParameterTestSuite.suite());
        suite.addTest(JUnitJPQLExamplesTestSuite.suite());
        suite.addTest(JUnitJPQLModifyTestSuite.suite());
        suite.addTest(AdvancedQueryTestSuite.suite());
        suite.addTest(JUnitNativeQueryTestSuite.suite());
        fullSuite.addTest(suite);

        // XML model
        fullSuite.addTest(EntityMappingsJUnitTestSuite.suite());

        // DDL model
        fullSuite.addTest(DDLGenerationJUnitTestSuite.suite());

        // JPA Advanced Properties model
        fullSuite.addTest(JPAAdvPropertiesJUnitTestCase.suite());

        // DataTypes model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.datatypes.NullBindingJUnitTestCase.suite());
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.datatypes.arraypks.PrimitiveArrayPKCachingJUnitTestCase.suite());

        // DateTime model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.datetime.NullBindingJUnitTestCase.suite());

        // Lob model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.lob.LobJUnitTestCase.suite());

        // Private owned model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.privateowned.PrivateOwnedJUnitTestCase.suite());

        // Orphan removal model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.orphanremoval.OrphanRemovalJUnitTestCase.suite());

        // OSGi Deployment
        fullSuite.addTestSuite(CompositeEnumerationTest.class);

        // JPA 2.0 Metamodel model
        fullSuite.addTest(org.eclipse.persistence.testing.tests.jpa.metamodel.MetamodelTestSuite.suite());

        // JPA 2.0 Criteria JPQL model
        suite = new TestSuite();
        suite.setName("Criteria");
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaUnitTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.criteria.AdvancedCompositePKJunitTest.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.criteria.AdvancedCriteriaQueryTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.criteria.AdvancedQueryTestSuite.suite());
        suite.addTest(org.eclipse.persistence.testing.tests.jpa.criteria.JUnitCriteriaSimpleTestSuite.suite());
        // Addition of the following suite requires classpath work - as it currently does not allow the JPA Testing Browser.launch to run in the Eclipse IDE
        //suite.addTest(org.eclipse.persistence.testing.tests.jpa.criteria.metamodel.JUnitCriteriaSimpleTestSuite.suite());  
        fullSuite.addTest(suite);
        
        // JPA 2.0 Cacheable model
        fullSuite.addTest(CacheableModelJunitTest.suite());
        
        // JPA 2.0 Delimited Identifiers model
        fullSuite.addTest(DelimitedPUTestSuite.suite());

        // JPA 2.0 BeanValidation integration
        // Commented out till we can checkin a Bean Validation impl to the repository
        // To run these test against Validation RI,
        // -add reference to following file from your workspace to run.classpath in build.xml
        //  hibernate-validator-4.0.0.Beta2.jar
        //  slf4j-jdk14.jar
        //  slf4j-api-1.5.6.jar

//        fullSuite.addTest(BeanValidationJunitTest.suite());

        fullSuite.addTest(CascadeDeletesJUnitTestSuite.suite());

        return fullSuite;
    }
}
