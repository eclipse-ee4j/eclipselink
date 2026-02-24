/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2024 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     03/03/2010 - 2.1 Michael O'Brien
//       - 302316: clear the object cache when testing stored procedure returns on SQLServer
//         to avoid false positives visible only when debugging in DatabaseCall.buildOutputRow()
//       - 260263: SQLServer 2005/2008 requires stored procedure creation select clause variable and column name matching
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
//     09/03/2010-2.2 Guy Pelletier
//       - 317286: DB column lenght not in sync between @Column and @JoinColumn
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     10/27/2010-2.2 Guy Pelletier
//       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
//     11/01/2010-2.2 Guy Pelletier
//       - 322916: getParameter on Query throws NPE
//     08/07/2016-2.7 Dalia Abo Sheasha
//       - 499335: Multiple embeddable fields can't reference same object
//     09/04/2018-3.0 Ravi Babu Tummuru
//       - 538183: SETTING QUERYHINTS.CURSOR ON A NAMEDQUERY THROWS QUERYEXCEPTION

package org.eclipse.persistence.testing.tests.jpa.advanced;

import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.Attribute.PersistentAttributeType;
import jakarta.persistence.metamodel.Bindable.BindableType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.Type.PersistenceType;
import jakarta.persistence.spi.LoadState;
import jakarta.persistence.spi.ProviderUtil;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.CascadePolicy;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.descriptors.invalidation.TimeToLiveCacheInvalidationPolicy;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.history.AsOfClause;
import org.eclipse.persistence.indirection.IndirectCollection;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.indirection.DatabaseValueHolder;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.internal.jpa.metamodel.MapAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.UnidirectionalOneToManyMapping;
import org.eclipse.persistence.queries.CursoredStream;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DoesExistQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.testing.framework.JoinedAttributeTestHelper;
import org.eclipse.persistence.testing.framework.QuerySQLTracker;
import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.AdvancedTableCreator;
import org.eclipse.persistence.testing.models.jpa.advanced.Bag;
import org.eclipse.persistence.testing.models.jpa.advanced.BarCode;
import org.eclipse.persistence.testing.models.jpa.advanced.Bill;
import org.eclipse.persistence.testing.models.jpa.advanced.BillAction;
import org.eclipse.persistence.testing.models.jpa.advanced.BillLine;
import org.eclipse.persistence.testing.models.jpa.advanced.BillLineItem;
import org.eclipse.persistence.testing.models.jpa.advanced.Buyer;
import org.eclipse.persistence.testing.models.jpa.advanced.Cost;
import org.eclipse.persistence.testing.models.jpa.advanced.Customer;
import org.eclipse.persistence.testing.models.jpa.advanced.Dealer;
import org.eclipse.persistence.testing.models.jpa.advanced.Department;
import org.eclipse.persistence.testing.models.jpa.advanced.Door;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee;
import org.eclipse.persistence.testing.models.jpa.advanced.EmployeePopulator;
import org.eclipse.persistence.testing.models.jpa.advanced.EmploymentPeriod;
import org.eclipse.persistence.testing.models.jpa.advanced.Equipment;
import org.eclipse.persistence.testing.models.jpa.advanced.EquipmentCode;
import org.eclipse.persistence.testing.models.jpa.advanced.GoldBuyer;
import org.eclipse.persistence.testing.models.jpa.advanced.HugeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Jigsaw;
import org.eclipse.persistence.testing.models.jpa.advanced.JigsawPiece;
import org.eclipse.persistence.testing.models.jpa.advanced.LargeProject;
import org.eclipse.persistence.testing.models.jpa.advanced.Loot;
import org.eclipse.persistence.testing.models.jpa.advanced.OrderedEntityA;
import org.eclipse.persistence.testing.models.jpa.advanced.OrderedEntityZ;
import org.eclipse.persistence.testing.models.jpa.advanced.Oyster;
import org.eclipse.persistence.testing.models.jpa.advanced.Pearl;
import org.eclipse.persistence.testing.models.jpa.advanced.PhoneNumber;
import org.eclipse.persistence.testing.models.jpa.advanced.Product;
import org.eclipse.persistence.testing.models.jpa.advanced.Project;
import org.eclipse.persistence.testing.models.jpa.advanced.Quantity;
import org.eclipse.persistence.testing.models.jpa.advanced.Room;
import org.eclipse.persistence.testing.models.jpa.advanced.SmallProject;
import org.eclipse.persistence.testing.models.jpa.advanced.ToDoList;
import org.eclipse.persistence.testing.models.jpa.advanced.Violation;
import org.eclipse.persistence.testing.models.jpa.advanced.Violation.ViolationID;
import org.eclipse.persistence.testing.models.jpa.advanced.ViolationCode;
import org.eclipse.persistence.testing.models.jpa.advanced.ViolationCode.ViolationCodeId;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredFunctionDefinition;
import org.junit.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * This test suite tests EclipseLink JPA annotations extensions.
 */
public class AdvancedJPAJunitTest extends JUnitTestCase {
    private static int empId;
    private static int penelopeId;
    private static int deptId;
    private static int buyerId;
    private static long visa = 1234567890;
    private static long amex = 1987654321;
    private static long diners = 1192837465;
    private static long mastercard = 1647382910;
    private static long rbc = 4783;
    private static long scotia = 8732;
    private static long td = 839362;
    private static long cibc = 948274;
    private static String newResponsibility = "The most useless responsibility ever.";

    public AdvancedJPAJunitTest() {
        super();
    }

    public AdvancedJPAJunitTest(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        super.setUp();
        clearCache();
    }

    public static Test suite() {
        TestSuite suite = (TestSuite) suiteSpring();
        suite.addTest(new AdvancedJPAJunitTest("testTransparentIndirectionValueHolderSessionReset"));
        suite.addTest(new AdvancedJPAJunitTest("testTransparentIndirectionQuerySessionReset"));
        suite.addTest(new AdvancedJPAJunitTest("testHistoryRelationshipQueryInitialization"));
        return suite;
    }

    public static Test suiteSpring() {
        TestSuite suite = new TestSuite();
        suite.setName("AdvancedJPAJunitTest");

        suite.addTest(new AdvancedJPAJunitTest("testSetup"));
        suite.addTest(new AdvancedJPAJunitTest("testRelationshipReadDuringClone"));

        suite.addTest(new AdvancedJPAJunitTest("testExistenceCheckingSetting"));
        suite.addTest(new AdvancedJPAJunitTest("testJoinColumnForeignKeyFieldLength"));
        suite.addTest(new AdvancedJPAJunitTest("testEmployeeFetchWithAlias"));

        suite.addTest(new AdvancedJPAJunitTest("testJoinFetchAnnotation"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCacheSettings"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyEmployeeCustomizerSettings"));

        suite.addTest(new AdvancedJPAJunitTest("testUpdateEmployee"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyUpdatedEmployee"));

        suite.addTest(new AdvancedJPAJunitTest("testCreateNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testVerifyNewBuyer"));
        suite.addTest(new AdvancedJPAJunitTest("testBuyerOptimisticLocking"));
        suite.addTest(new AdvancedJPAJunitTest("testOptimisticLockExceptionOnMerge"));
        suite.addTest(new AdvancedJPAJunitTest("testOptimisticLockExceptionOnMergeWithAssumeExists"));
        suite.addTest(new AdvancedJPAJunitTest("testVersionUpdateForOwnedMappings"));

        suite.addTest(new AdvancedJPAJunitTest("testGiveFredAnObjectTypeConverterChange"));
        suite.addTest(new AdvancedJPAJunitTest("testUpdatePenelopesPhoneNumberStatus"));
        suite.addTest(new AdvancedJPAJunitTest("testRemoveJillWithPrivateOwnedPhoneNumbers"));

        suite.addTest(new AdvancedJPAJunitTest("testCreateNewEquipment"));
        suite.addTest(new AdvancedJPAJunitTest("testAddNewEquipmentToDepartment"));
        suite.addTest(new AdvancedJPAJunitTest("testRemoveDepartmentWithPrivateOwnedEquipment"));
        suite.addTest(new AdvancedJPAJunitTest("testUpdateReadOnlyEquipmentCode"));

        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQuery"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryByIndex"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryInOut"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryWithRawData"));
        suite.addTest(new AdvancedJPAJunitTest("testModifyNamedStoredProcedureQueryWithRawData"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryWithResultSetMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedStoredProcedureQueryWithResultSetFieldMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testNamedFunction"));
        suite.addTest(new AdvancedJPAJunitTest("testNonTriggerLazyForSProc"));

        suite.addTest(new AdvancedJPAJunitTest("testMethodBasedTransformationMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testClassBasedTransformationMapping"));
        suite.addTest(new AdvancedJPAJunitTest("testTransformationMappingWithColumnAnnotation"));

        suite.addTest(new AdvancedJPAJunitTest("testCursorStream"));
        suite.addTest(new AdvancedJPAJunitTest("testCursoredNativeQueryDefinedViaAnnotation"));
        suite.addTest(new AdvancedJPAJunitTest("testProperty"));

        suite.addTest(new AdvancedJPAJunitTest("testBackpointerOnMerge"));

        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalPersist"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalUpdate"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalFetchJoin"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalTargetLocking_AddRemoveTarget"));
        suite.addTest(new AdvancedJPAJunitTest("testUnidirectionalTargetLocking_DeleteSource"));

        suite.addTest(new AdvancedJPAJunitTest("testMapBuildReferencesPKList"));
        suite.addTest(new AdvancedJPAJunitTest("testListBuildReferencesPKList"));
        suite.addTest(new AdvancedJPAJunitTest("testValuePKListMissingElement"));
        suite.addTest(new AdvancedJPAJunitTest("testEnumeratedPrimaryKeys"));

        suite.addTest(new AdvancedJPAJunitTest("testAttributeOverrideToMultipleSameDefaultColumnName"));
        suite.addTest(new AdvancedJPAJunitTest("testJoinFetchWithRefreshOnRelatedEntity"));
        suite.addTest(new AdvancedJPAJunitTest("testSharedEmbeddedAttributeOverrides"));

        suite.addTest(new AdvancedJPAJunitTest("testEmployeeToProjectWithBatchFetchTypeInReverseIteration"));
        suite.addTest(new AdvancedJPAJunitTest("testEmployeeToProjectWithBatchFetchTypeInCustomIteration"));
        suite.addTest(new AdvancedJPAJunitTest("testEmployeeToProjectWithBatchFetchTypeInRandomIteration"));
        suite.addTest(new AdvancedJPAJunitTest("testEmployeeToProjectWithBatchFetchTypeWithSmallFetchSize"));

        // These tests use JPA 2.0 entity manager API
        suite.addTest(new AdvancedJPAJunitTest("testQueryGetParameter"));

        // Run this test only when the JPA 2.0 specification is enabled on the server, or we are in SE mode with JPA 2.0 capability
        suite.addTest(new AdvancedJPAJunitTest("testMetamodelMinimalSanityTest"));

        suite.addTest(new AdvancedJPAJunitTest("testProjectToEmployeeWithBatchFetchJoinFetch"));
        suite.addTest(new AdvancedJPAJunitTest("testEmployeeToPhoneNumberWithBatchFetchJoinFetch"));
        suite.addTest(new AdvancedJPAJunitTest("testEmployeeToAddressWithBatchFetchJoinFetch"));

        suite.addTest(new AdvancedJPAJunitTest("testQueryJoinBasicCollectionTableUsingQueryResultsCache"));
        suite.addTest(new AdvancedJPAJunitTest("testNullValueInCollectionWithOrderColumn"));

        // Bug 453865
        suite.addTest(new AdvancedJPAJunitTest("testJoinWithOrderByOnElementCollectionList"));
        suite.addTest(new AdvancedJPAJunitTest("testJoinWithOrderByOnElementCollectionMap"));

        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        ServerSession session = JUnitTestCase.getServerSession();
        new AdvancedTableCreator().replaceTables(session);
        // The EquipmentCode class 'should' be set to read only. We want
        // to be able to create a couple in the Employee populator, so
        // force the read only to false. If EquipmentCode is not
        // actually read only, don't worry, we set the original read
        // only value back on the descriptor and the error will be
        // caught in a later test in this suite.
        ClassDescriptor descriptor = session.getDescriptor(EquipmentCode.class);
        boolean shouldBeReadOnly = descriptor.shouldBeReadOnly();
        descriptor.setShouldBeReadOnly(false);

        // Populate the database with our examples.
        EmployeePopulator employeePopulator = new EmployeePopulator(supportsStoredProcedures());
        employeePopulator.buildExamples();
        employeePopulator.persistExample(session);

        descriptor.setShouldBeReadOnly(shouldBeReadOnly);

        clearCache();

        //create stored function when database supports it
        if (supportsStoredFunctions()) {
            SchemaManager schema = new SchemaManager(session);
            schema.replaceObject(buildStoredFunction());
        }
    }

    public StoredFunctionDefinition buildStoredFunction() {
        StoredFunctionDefinition func = new StoredFunctionDefinition();
        func.setName("StoredFunction_In");
        func.addArgument("P_IN", Long.class);
        func.setReturnType(Long.class);
        // Works for Oracle and MySQL, MS SQL should use SCHEMABINDING keyword.
        if (getPlatform().isOracle() || getPlatform().isMySQL()) {
            func.addCharacteristic("DETERMINISTIC");
        }
        func.addStatement("RETURN P_IN * 1000");
        return func;
    }

    /* Test named function 'StoredFunction_In'*/
    public void testNamedFunction() {
        if (!supportsStoredFunctions()) {
            warning("this test is not suitable for running on dbs that don't support stored function");
            return;
        }
        EntityManager em = createEntityManager();
        Query query;
        try {
            query = em.createNamedQuery("StoredFunction_In");
            query.setParameter("P_IN", 1);
            long result = (Long)query.getSingleResult();
            if (result != 1000L) {
                fail("Incorrect result returned:" + result);
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Bug 322916
     */
    public void testQueryGetParameter() {
        EntityManager em = createEntityManager();

        try {
            Query query = em.createQuery("select e from Employee e where e.salary = :sal").setParameter("sal", 1000);
            query.getParameter("sal");
        } catch (Exception e) {
            fail("Get parameter on test failed: " + e);
        } finally {
            closeEntityManager(em);
        }
    }

    public void testMapBuildReferencesPKList(){
        EntityManager em = createEntityManager();
        beginTransaction(em);
        ClassDescriptor descriptor;
        AbstractSession session = (AbstractSession) JpaHelper.getEntityManager(em).getActiveSession();
        descriptor = session.getDescriptorForAlias("ADV_DEPT");
        Department dept = (Department) em.createQuery("SELECT d FROM ADV_DEPT d WHERE d.equipment IS NOT EMPTY").getResultList().get(0);
        ForeignReferenceMapping mapping = (ForeignReferenceMapping) descriptor.getMappingForAttributeName("equipment");
        Object[] pks = mapping.buildReferencesPKList(dept, mapping.getAttributeValueFromObject(dept), session);
        assertEquals("PK list is of incorrect size.  pks.size: " + pks.length + " expected: " + (dept.getEquipment().size() * 2), pks.length, (dept.getEquipment().size() * 2));
        @SuppressWarnings({"unchecked"})
        Map<Integer, Equipment> equipments = (Map<Integer, Equipment>) mapping.valueFromPKList(pks, null, session);
        assertEquals("ValueFromPKList returned list of different size from actual entity.", equipments.size(), dept.getEquipment().size());
        for (Equipment equip : dept.getEquipment().values()){
            assertTrue("Equipment not found in ValueFromPKList list", equipments.containsKey(equip.getId()));
        }
        rollbackTransaction(em);
    }

    @SuppressWarnings({"unchecked"})
    public void testListBuildReferencesPKList(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Jigsaw jigsaw = new Jigsaw();
        for (int i = 1; i < 11; i++) {
            jigsaw.addPiece(new JigsawPiece(i));
        }
        em.persist(jigsaw);


        em.flush();

        clearCache();

        AbstractSession session = (AbstractSession) JpaHelper.getEntityManager(em).getActiveSession();
        ClassDescriptor descriptor = session.getDescriptorForAlias("Jigsaw");

        Jigsaw foundJigsaw = em.find(Jigsaw.class, jigsaw.getId());
        int expectedNumber = foundJigsaw.getPieces().size();

        ForeignReferenceMapping mapping = (ForeignReferenceMapping) descriptor.getMappingForAttributeName("pieces");
        Object[] pks = mapping.buildReferencesPKList(foundJigsaw, mapping.getAttributeValueFromObject(foundJigsaw), session);
        assertEquals("PK list is of incorrect size", expectedNumber, pks.length);

        List<JigsawPiece> elements = (List<JigsawPiece>) mapping.valueFromPKList(pks, null, session);
        assertEquals("ValueFromPKList returned list of different size from actual entity.", expectedNumber, elements.size());

        for (JigsawPiece element : elements){
            assertTrue("Entity id " + element.getId() + " not found in ValueFromPKList list", foundJigsaw.getPieces().contains(element));
        }

        elements = (List<JigsawPiece>) mapping.valueFromPKList(pks, null, session);
        assertEquals("ValueFromPKList returned list of different size from actual entity.", expectedNumber, elements.size());

        for (JigsawPiece element : elements){
            assertTrue("Entity id " + element.getId() + " not found in ValueFromPKList list", foundJigsaw.getPieces().contains(element));
        }
        rollbackTransaction(em);
    }

    @SuppressWarnings({"unchecked"})
    public void testValuePKListMissingElement(){
        if (isOnServer()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        Jigsaw jigsaw = new Jigsaw();
        for (int i = 1; i < 11; i++) {
            jigsaw.addPiece(new JigsawPiece(i));
        }
        em.persist(jigsaw);
        commitTransaction(em);
        try {

            AbstractSession session = (AbstractSession) JpaHelper.getEntityManager(em).getActiveSession();
            ClassDescriptor descriptor = session.getDescriptorForAlias("Jigsaw");

            Jigsaw foundJigsaw = em.find(Jigsaw.class, jigsaw.getId());
            int expectedNumber = foundJigsaw.getPieces().size();

            OneToManyMapping mapping = (OneToManyMapping) descriptor.getMappingForAttributeName("pieces");
            Object[] pks = mapping.buildReferencesPKList(foundJigsaw, mapping.getAttributeValueFromObject(foundJigsaw), session);
            assertEquals("PK list is of incorrect size", expectedNumber, pks.length);

            session.getIdentityMapAccessor().invalidateObject(foundJigsaw.getPieces().get(2));
            DatabaseRecord fks = new DatabaseRecord();
            for (DatabaseField field : mapping.getSourceKeyFields()){
                fks.add(field, descriptor.getObjectBuilder().extractValueFromObjectForField(foundJigsaw, field, session));
            }

            mapping.writeFromObjectIntoRow(foundJigsaw, fks, session, DatabaseMapping.WriteType.UNDEFINED);

            List<JigsawPiece> elements = (List<JigsawPiece>) mapping.valueFromPKList(pks, fks, session);
            assertEquals("ValueFromPKList returned list of different size from actual entity.", expectedNumber, elements.size());
            assertFalse("Collection contains unexpected null", elements.contains(null));
            for (JigsawPiece element : elements) {
                assertTrue("Entity id " + element.getId() + " not found in ValueFromPKList list", foundJigsaw.getPieces().contains(element));
            }
            em.refresh(foundJigsaw.getPieces().get(2));
            session.getIdentityMapAccessor().invalidateObject(foundJigsaw.getPieces().get(5));

            elements = (List<JigsawPiece>) mapping.valueFromPKList(pks, fks, session);
            assertEquals("ValueFromPKList returned list of different size from actual entity.", expectedNumber, elements.size());
            assertFalse("Collection contains unexpected null", elements.contains(null));
            for (JigsawPiece element : elements) {
                assertTrue("Entity id " + element.getId() + " not found in ValueFromPKList list", foundJigsaw.getPieces().contains(element));
            }

        } finally {
            try {
                beginTransaction(em);
                em.remove(jigsaw);
                commitTransaction(em);
            } catch (Exception e) {
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Bug 328114
     */
    public void testAttributeOverrideToMultipleSameDefaultColumnName() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);
            Loot loot = new Loot();
            Bag bag = new Bag();
            Quantity quantity = new Quantity();
            quantity.value = 11;
            bag.quantity = quantity;
            Cost cost = new Cost();
            cost.value = 5.99;
            bag.cost = cost;
            loot.bag = bag;
            em.persist(loot);
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * This test performs minimal sanity testing on the advanced JPA model
     * in order to verify metamodel creation.<p>
     * See the metamodel test package suite for full regression tests.
     * See SVN rev# 5124
     * http://fisheye2.atlassian.com/changelog/~author=mobrien/eclipselink/?cs=5124
     */
    public void testMetamodelMinimalSanityTest() {
        EntityManager em = createEntityManager();
        // pre-clear metamodel to enable test reentry (SE only - not EE)
        if(!isOnServer()) {
            ((EntityManagerFactoryDelegate) em.getEntityManagerFactory()).setMetamodel(null);
        }
        Metamodel metamodel = em.getMetamodel();
        // get declared attributes
        EntityType<LargeProject> entityLargeProject = metamodel.entity(LargeProject.class);
        Set<Attribute<LargeProject, ?>> declaredAttributes = entityLargeProject.getDeclaredAttributes();
        assertTrue(!declaredAttributes.isEmpty()); // instead of a assertEquals(1, size) for future compatibility with changes to Buyer

        // check that getDeclaredAttribute and getDeclaredAttributes return the same attribute
        Attribute<LargeProject, ?> budgetAttribute = entityLargeProject.getDeclaredAttribute("budget");
        assertNotNull(budgetAttribute);
        Attribute<LargeProject, ?> budgetSingularAttribute = entityLargeProject.getDeclaredSingularAttribute("budget");
        assertNotNull(budgetSingularAttribute);
        assertEquals(budgetSingularAttribute, budgetAttribute);
        assertTrue(declaredAttributes.contains(budgetSingularAttribute));
        // check the type
        Class<?> budgetClass = budgetSingularAttribute.getJavaType();
        // Verify whether we expect a boxed class or not
        assertEquals(double.class, budgetClass);
        //assertEquals(Double.class, budgetClass);

        // Test LargeProject.budget.buyingDays

        //Test new Jakarta Persistence 3.2 method jakarta.persistence.metamodel.Metamodel.entity(java.lang.String)
        EntityType<Buyer> entityBuyerByName = (EntityType<Buyer>) metamodel.entity("org.eclipse.persistence.testing.models.jpa.advanced.Buyer");
        assertNotNull(entityBuyerByName);

        // Check an EnumSet on an Entity
        EntityType<Buyer> entityBuyer = metamodel.entity(Buyer.class);
        // public enum Weekdays { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY }
        // private EnumSet<Weekdays> buyingDays;
        assertNotNull(entityBuyer);
        // check persistence type
        assertEquals(PersistenceType.ENTITY, entityBuyer.getPersistenceType());
        assertEquals(Buyer.class, entityBuyer.getJavaType());
        // verify EnumSet is a SingularAttribute
        Attribute<? super Buyer, ?> buyingDaysAttribute = entityBuyer.getAttribute("buyingDays");
        assertNotNull(buyingDaysAttribute);
        // Check persistent attribute type
        assertEquals(PersistentAttributeType.BASIC, buyingDaysAttribute.getPersistentAttributeType());
        // Non-spec check on the attribute impl type
        // EnumSet is not a Set in the Metamodel - it is a treated as a BasicType single object (SingularAttributeType)
        // BasicTypeImpl@8980685:EnumSet [ javaType: class java.util.EnumSet]
        assertFalse(((SingularAttributeImpl<?, ?>)buyingDaysAttribute).isPlural());
        BindableType buyingDaysElementBindableType = ((SingularAttributeImpl<?, ?>)buyingDaysAttribute).getBindableType();
        assertEquals(BindableType.SINGULAR_ATTRIBUTE, buyingDaysElementBindableType);
        @SuppressWarnings({"rawtypes"})
        SingularAttribute<? super Buyer, EnumSet> buyingDaysSingularAttribute = entityBuyer.getSingularAttribute("buyingDays", EnumSet.class);
        assertNotNull(buyingDaysSingularAttribute);
        assertFalse(buyingDaysSingularAttribute.isCollection());

        // http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_74:_20090909:_Implement_IdentifiableType.hasSingleIdAttribute.28.29
        // Check for Id that exists
        boolean expectedIAExceptionThrown = false;
        boolean hasSingleIdAttribute = false;
        try {
            hasSingleIdAttribute = entityBuyer.hasSingleIdAttribute();
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
            expectedIAExceptionThrown = true;
        }
        assertFalse(expectedIAExceptionThrown);
        assertTrue(hasSingleIdAttribute);

        // Verify that the BasicMap Buyer.creditCards is picked up properly
        //* @param <X> The type the represented Map belongs to
        //* @param <K> The type of the key of the represented Map
        //* @param <V> The type of the value of the represented Map
        //public class MapAttributeImpl<X, K, V> extends PluralAttributeImpl<X, java.util.Map<K, V>, V>
        Attribute<? super Buyer, ?> buyerCreditCards = entityBuyer.getAttribute("creditCards");
        assertNotNull(buyerCreditCards);
        assertTrue(buyerCreditCards.isCollection());
        assertTrue(buyerCreditCards instanceof MapAttributeImpl);
        MapAttribute<? super Buyer, ?, ?> buyerCreditCardsMap = entityBuyer.getMap("creditCards");

        // Verify owning type
        assertNotNull(buyerCreditCardsMap);
        assertEquals(entityBuyer, buyerCreditCardsMap.getDeclaringType());

        // Verify Map Key
        assertEquals(String.class, buyerCreditCardsMap.getKeyJavaType());

        // Verify Map Value
        assertEquals(Long.class, buyerCreditCardsMap.getElementType().getJavaType());
    }

    /**
     * Verifies that existence-checking metadata is correctly processed.
     */
    public void testExistenceCheckingSetting() {
        ServerSession session = JUnitTestCase.getServerSession();

        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        assertEquals("Employee existence checking was incorrect", DoesExistQuery.CheckDatabase, employeeDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());

        ClassDescriptor projectDescriptor = session.getDescriptor(Project.class);
        assertEquals("Project existence checking was incorrect", DoesExistQuery.CheckCache, projectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());

        ClassDescriptor smallProjectDescriptor = session.getDescriptor(SmallProject.class);
        assertEquals("SmallProject existence checking was incorrect", DoesExistQuery.AssumeExistence, smallProjectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());

        ClassDescriptor largeProjectDescriptor = session.getDescriptor(LargeProject.class);
        assertEquals("LargeProject existence checking was incorrect", DoesExistQuery.AssumeNonExistence, largeProjectDescriptor.getQueryManager().getDoesExistQuery().getExistencePolicy());
    }

    public void testJoinColumnForeignKeyFieldLength() {
        ServerSession session = JUnitTestCase.getServerSession();

        ClassDescriptor employeeDescriptor = session.getDescriptor(Employee.class);
        ClassDescriptor buyerDescriptor = session.getDescriptor(Buyer.class);

        //////////////////// Test fk from a secondary table ////////////////////
        DatabaseTable secondaryTable = employeeDescriptor.getTable("CMP3_SALARY");
        Map<DatabaseField, DatabaseField> keys = employeeDescriptor.getAdditionalTablePrimaryKeyFields().get(secondaryTable);

        for (DatabaseField pkField : keys.keySet()) {
            DatabaseField fkField =  keys.get(pkField);
            assertEquals("The secondary table foreign field [" + fkField.getName() + "(" + fkField.getLength() + ")] did not have the same length as the primary key field [" + pkField.getName() + "(" + pkField.getLength() + ")]", fkField.getLength(), pkField.getLength());
        }

        ////////////////////////// Test a M-1 mapping //////////////////////////
        DatabaseMapping mapping = employeeDescriptor.getMappingForAttributeName("address");
        assertNotNull("The address mapping from Employee was not found", mapping);
        assertTrue("The address mapping is no longer a one to one mapping", mapping.isOneToOneMapping());

        keys = ((OneToOneMapping) mapping).getSourceToTargetKeyFields();

        for (DatabaseField fkField : keys.keySet()) {
            DatabaseField pkField =  keys.get(fkField);
            assertEquals("The address mapping foreign field [" + fkField.getName() + "(" + fkField.getLength() + ")] did not have the same length as the primary key field [" + pkField.getName() + "(" + pkField.getLength() + ")]", fkField.getLength(), pkField.getLength());
        }

        ////////////////// Test a unidirectional 1-M mapping ///////////////////
        mapping = employeeDescriptor.getMappingForAttributeName("dealers");
        assertNotNull("The dealers mapping from Employee was not found", mapping);
        assertTrue("The dealers mapping is no longer a unidirectional one to many mapping", mapping.isUnidirectionalOneToManyMapping());

        keys = ((UnidirectionalOneToManyMapping) mapping).getTargetForeignKeysToSourceKeys();

        for (DatabaseField fkField : keys.keySet()) {
            DatabaseField pkField =  keys.get(fkField);
            assertEquals("The dealers mapping foreign key field [" + fkField.getName() + "(" + fkField.getLength() + ")] did not have the same length as the primary key field [" + pkField.getName() + "(" + pkField.getLength() + ")]", fkField.getLength(), pkField.getLength());
        }

        ////////////////////////// Test a M-M mapping //////////////////////////
        mapping = employeeDescriptor.getMappingForAttributeName("projects");
        assertNotNull("The projects mapping from Employee was not found", mapping);
        assertTrue("The projects mapping is no longer a many to many mapping", mapping.isManyToManyMapping());

        List<DatabaseField> sourceKeys = ((ManyToManyMapping) mapping).getSourceKeyFields();
        List<DatabaseField> sourceRelationKeys = ((ManyToManyMapping) mapping).getSourceRelationKeyFields();

        for (int i = 0; i < sourceKeys.size(); i++) {
            DatabaseField sourcePrimaryKey = sourceKeys.get(i);
            DatabaseField sourceRelationForeignKey = sourceRelationKeys.get(i);

            assertEquals("The projects mapping source relation foreign key field [" + sourceRelationForeignKey.getName() + "(" + sourceRelationForeignKey.getLength() + ")] did not have the same length as the source primary key field [" + sourcePrimaryKey.getName() + "(" + sourcePrimaryKey.getLength() + ")]", sourcePrimaryKey.getLength(), sourceRelationForeignKey.getLength());
        }

        List<DatabaseField> targetKeys = ((ManyToManyMapping) mapping).getSourceKeyFields();
        List<DatabaseField> targetRelationKeys = ((ManyToManyMapping) mapping).getSourceRelationKeyFields();

        for (int i = 0; i < targetKeys.size(); i++) {
            DatabaseField targetPrimaryKey = targetKeys.get(i);
            DatabaseField targetRelationForeignKey = targetRelationKeys.get(i);

            assertEquals("The projects mapping target relation foreign key field [" + targetRelationForeignKey.getName() + "(" + targetRelationForeignKey.getLength() + ")] did not have the same length as the target primary key field [" + targetPrimaryKey.getName() + "(" + targetPrimaryKey.getLength() + ")]", targetPrimaryKey.getLength(), targetRelationForeignKey.getLength());
        }

        //////////////////////// Test a basic collection ///////////////////////
        mapping = employeeDescriptor.getMappingForAttributeName("responsibilities");
        assertNotNull("The responsibilities mapping from Employee was not found", mapping);
        assertTrue("The responsibilities mapping is no longer a direct collection mapping", mapping.isDirectCollectionMapping());

        List<DatabaseField> primaryKeys = ((DirectCollectionMapping) mapping).getSourceKeyFields();
        List<DatabaseField> foreignKeys = ((DirectCollectionMapping) mapping).getReferenceKeyFields();

        for (int i = 0; i < primaryKeys.size(); i++) {
            DatabaseField primaryKey = primaryKeys.get(i);
            DatabaseField foreignKey = foreignKeys.get(i);

            assertEquals("The responsibilities mapping foreign key field [" + foreignKey.getName() + "(" + foreignKey.getLength() + ")] did not have the same length as the source primary key field [" + primaryKey.getName() + "(" + primaryKey.getLength() + ")]", primaryKey.getLength(), foreignKey.getLength());
        }

        //////// Test an element collection mapping (direct collection) ////////
        mapping = buyerDescriptor.getMappingForAttributeName("creditLines");
        assertNotNull("The creditLines mapping from Buyer was not found", mapping);
        assertTrue("The creditLines mapping is no longer an element collection mapping", mapping.isDirectCollectionMapping());

        primaryKeys = ((DirectCollectionMapping) mapping).getSourceKeyFields();
        foreignKeys = ((DirectCollectionMapping) mapping).getReferenceKeyFields();

        for (int i = 0; i < primaryKeys.size(); i++) {
            DatabaseField primaryKey = primaryKeys.get(i);
            DatabaseField foreignKey = foreignKeys.get(i);

            assertEquals("The creditLines mapping foreign key field [" + foreignKey.getName() + "(" + foreignKey.getLength() + ")] did not have the same length as the source primary key field [" + primaryKey.getName() + "(" + primaryKey.getLength() + ")]", primaryKey.getLength(), foreignKey.getLength());
        }

        // Items not directly tested: element collection using a map and basic map.
    }

    /**
     * Verifies that settings from the Employee cache annotation have been set.
     */
    public void testVerifyEmployeeCacheSettings() {
        EntityManager em = createEntityManager("default1");
        ClassDescriptor descriptor;
        if (isOnServer()) {
            descriptor = getServerSession("default1").getDescriptorForAlias("Employee");
        } else {
            descriptor = ((EntityManagerImpl) em).getServerSession().getDescriptorForAlias("Employee");
        }

        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found in the default1 PU.");
        } else {
            assertEquals("Incorrect cache type() setting.", descriptor.getIdentityMapClass(), ClassConstants.SoftCacheWeakIdentityMap_Class);
            assertEquals("Incorrect cache size() setting, was: " + descriptor.getIdentityMapSize() + " expected: 730.", 730, descriptor.getIdentityMapSize());
            assertFalse("Incorrect cache isolated() setting.", descriptor.isIsolated());
            assertFalse("Incorrect cache alwaysRefresh() setting.", descriptor.shouldAlwaysRefreshCache());

            // The diableHits() setting gets changed in the employee customizer.
            // Its setting is checked in the test below.

            CacheInvalidationPolicy policy = descriptor.getCacheInvalidationPolicy();
            assertTrue("Incorrect cache expiry() policy setting.", policy instanceof TimeToLiveCacheInvalidationPolicy);
            assertEquals("Incorrect cache expiry() setting.", 1000000, ((TimeToLiveCacheInvalidationPolicy) policy).getTimeToLive());

            assertEquals("Incorrect cache coordinationType() settting.", ClassDescriptor.INVALIDATE_CHANGED_OBJECTS, descriptor.getCacheSynchronizationType());
        }

        closeEntityManager(em);
    }

    /**
     * Verifies that settings from the EmployeeCustomizer have been set.
     */
    public void testVerifyEmployeeCustomizerSettings() {
        EntityManager em = createEntityManager();

        ClassDescriptor descriptor = getServerSession().getDescriptorForAlias("Employee");

        if (descriptor == null) {
            fail("A descriptor for the Employee alias was not found.");
        } else {
            assertFalse("Disable cache hits was true. Customizer should have made it false.", descriptor.shouldDisableCacheHits());
        }

        closeEntityManager(em);
    }

    /**
     * Verifies that the join-fetch annotation was read correctly.
     */
    public void testJoinFetchAnnotation() {
        ServerSession session = JUnitTestCase.getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(Employee.class);
        if (((ForeignReferenceMapping)descriptor.getMappingForAttributeName("department")).getJoinFetch() != ForeignReferenceMapping.OUTER_JOIN) {
            fail("JoinFetch annotation not read correctly for Employee.department.");
        }
    }

    /**
     * Tests:
     * - BasicCollection mapping
     * - Serialized Basic of type EnumSet.
     * - BasicCollection that uses an Enum converter (by detection).
     */
    public void testUpdateEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) em.createNamedQuery("findAllSQLEmployees", Employee.class);
            @SuppressWarnings({"unchecked"})
            Collection<Employee> employees = query.getResultCollection();

            if (employees.isEmpty()) {
                fail("No Employees were found. Test requires at least one Employee to be created in the EmployeePopulator.");
            } else {
                Employee emp = employees.iterator().next();
                emp.addResponsibility(newResponsibility);
                emp.setMondayToFridayWorkWeek();
                empId = emp.getId();
                commitTransaction(em);
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Verifies:
     * - a BasicCollection mapping.
     * - a BasicCollection that uses an Enum converter (by detection).
     */
    @SuppressWarnings({"unchecked"})
    public void testVerifyUpdatedEmployee() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Employee emp = em.find(Employee.class, empId);

            assertNotNull("The updated employee was not found.", emp);

            boolean found = false;
            for (String responsibility : (Collection<String>) emp.getResponsibilities()) {
                if (responsibility.equals(newResponsibility)) {
                    found = true;
                    break;
                }
            }
            commitTransaction(em);
            assertTrue("The new responsibility was not added.", found);
            assertTrue("The basic collection using enums was not persisted correctly.", emp.worksMondayToFriday());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    @SuppressWarnings({"unchecked"})
    public void testVersionUpdateForOwnedMappings(){
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Employee emp = em.find(Employee.class, empId);
            int currentVersion = emp.getVersion();
            assertNotNull("The employee was not found for Id.", empId);

            emp.getResponsibilities().add("UpdateVersionField");
            commitTransaction(em);
            assertEquals("Did not increment version for change to direct collection", (int) emp.getVersion(), ++currentVersion);
            beginTransaction(em);
            emp = em.find(Employee.class, empId);
            emp.getDealers().add(em.merge(new Dealer("update", "version")));
            commitTransaction(em);
            assertEquals("Did not increment version for change to uni-directional one to many with join table", (int) emp.getVersion(), ++currentVersion);
            beginTransaction(em);
            emp = em.find(Employee.class, empId);
            emp.getProjects().add(em.merge(new LargeProject("versionUpdate")));
            commitTransaction(em);
            assertEquals("Did not increment version for change to owned ManyToMany", (int) emp.getVersion(), ++currentVersion);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests:
     * - BasicMap mapping with a TypeConverter on the map value and an
     *   ObjectTypeConverter on the map key.
     * - Basic with a custom converter
     * - Serialized Basic of type EnumSet.
     */
    public void testCreateNewBuyer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            GoldBuyer buyer = new GoldBuyer();

            buyer.setName("Guy Pelletier");
            buyer.setGender("Made of testosterone");
            buyer.setDescription("Loves to spend");

            buyer.addVisa(visa);
            buyer.addAmex(amex);
            buyer.addDinersClub(diners);
            buyer.addMastercard(mastercard);

            buyer.addRoyalBankCreditLine(rbc);
            buyer.addScotiabankCreditLine(scotia);
            buyer.addTorontoDominionCreditLine(td);
            buyer.addCanadianImperialCreditLine(cibc);

            buyer.setSaturdayToSundayBuyingDays();

            em.persist(buyer);
            commitTransaction(em);
            buyerId = buyer.getId();
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Verifies:
     * - BasicMap mapping with a TypeConverter on the map value and an
     *   ObjectTypeConverter on the map key.
     * - Basic with a custom converter
     * - Serialized Basic of type EnumSet.
     */
    public void testVerifyNewBuyer() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            GoldBuyer buyer = em.find(GoldBuyer.class, buyerId);

            assertNotNull("The new buyer was not found", buyer);
            assertTrue("Gender was not persisted correctly.", buyer.isMale());
            assertTrue("Visa card did not persist correctly.", buyer.hasVisa(visa));
            assertTrue("Amex card did not persist correctly.", buyer.hasAmex(amex));
            assertTrue("Diners Club card did not persist correctly.", buyer.hasDinersClub(diners));
            assertTrue("Mastercard card did not persist correctly.", buyer.hasMastercard(mastercard));

            assertTrue("RBC credit line did not persist correctly.", buyer.hasRoyalBankCreditLine(rbc));
            assertTrue("Scotia credit line did not persist correctly.", buyer.hasScotiabankCreditLine(scotia));
            assertTrue("TD credit line did not persist correctly.", buyer.hasTorontoDominionCreditLine(td));
            assertTrue("CIBC credit line did not persist correctly.", buyer.hasCanadianImperialCreditLine(cibc));

            assertTrue("The serialized enum set was not persisted correctly.", buyer.buysSaturdayToSunday());
            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests an OptimisticLocking policy set on Buyer.
     */
    public void testBuyerOptimisticLocking() {
        // Cannot create parallel entity managers in the server.
        if (isOnServer()) {
            return;
        }

        EntityManager em1 = createEntityManager();
        EntityManager em2 = createEntityManager();
        em1.getTransaction().begin();
        em2.getTransaction().begin();
        RuntimeException caughtException = null;
        try {
            GoldBuyer buyer1 = em1.find(GoldBuyer.class, buyerId);
            GoldBuyer buyer2 = em2.find(GoldBuyer.class, buyerId);

            buyer1.setName("Geezer");
            buyer2.setName("Guyzer");
            // Uses field locking, so need to update version.
            buyer1.setVersion(buyer1.getVersion() + 1);
            buyer2.setVersion(buyer2.getVersion() + 1);

            em1.getTransaction().commit();
            em2.getTransaction().commit();

            em1.close();
            em2.close();
        } catch (RuntimeException e) {
            caughtException = e;
            if (em1.getTransaction().isActive()){
                em1.getTransaction().rollback();
            }

            if (em2.getTransaction().isActive()){
                em2.getTransaction().rollback();
            }

            em1.close();
            em2.close();
        }
        if (caughtException == null) {
            fail("Optimistic lock exception was not thrown.");
        } else if (!(caughtException.getCause() instanceof jakarta.persistence.OptimisticLockException)) {
            // Re-throw exception to ensure stacktrace appears in test result.
            throw caughtException;
        }
    }

    /**
     * Tests an ObjectTypeConverter on a direct to field mapping.
     */
    public void testGiveFredAnObjectTypeConverterChange() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) em.createNamedQuery("findAllEmployeesByFirstName", Employee.class);
            query.setParameter("firstname", "Fred");
            @SuppressWarnings({"unchecked"})
            Collection<Employee> employees = query.getResultCollection();

            if (employees.isEmpty()) {
                fail("No employees named Fred were found. Test requires at least one Fred to be created in the EmployeePopulator.");
            } else {
                Employee fred = employees.iterator().next();
                fred.setFemale();
                fred.setFirstName("Penelope");
                penelopeId = fred.getId();

                commitTransaction(em);

                // Clear cache and clear the entity manager
                clearCache();
                em.clear();

                Employee penelope = em.find(Employee.class, penelopeId);
                assertTrue("Fred's ObjectTypeConverter change to Penelope didn't occur.", penelope.isFemale());
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a BasicCollection on an entity that uses a composite primary key.
     */
    public void testUpdatePenelopesPhoneNumberStatus() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Employee emp = em.find(Employee.class, penelopeId);
            assertNotNull("The employee with id: [" + penelopeId + "] was not found.", emp);

            for (PhoneNumber phoneNumber : emp.getPhoneNumbers()) {
                phoneNumber.addStatus(PhoneNumber.PhoneStatus.ACTIVE);
                phoneNumber.addStatus(PhoneNumber.PhoneStatus.ASSIGNED);
            }

            commitTransaction(em);

            // Clear cache and clear the entity manager
            clearCache();
            em.clear();

            Employee emp2 = em.find(Employee.class, penelopeId);

            for (PhoneNumber phone : emp2.getPhoneNumbers()) {
                assertTrue("", phone.getStatus().contains(PhoneNumber.PhoneStatus.ACTIVE));
                assertTrue("", phone.getStatus().contains(PhoneNumber.PhoneStatus.ASSIGNED));
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Test that an embedded attribute can correctly share a field override with
     * its parent class and write a non-null value when it is inserted and then modified.
     * EL Bug 393520
     */
    public void testSharedEmbeddedAttributeOverrides() {
        EntityManager em = createEntityManager();
        try {
            Product product = new Product();
            product.setName("Scottish Shortbread" );
            product.setCountryCode("GBR");
            product.setBarCode1(new BarCode("123-456-789", "GBR"));
            product.setBarCode2(null);

            beginTransaction(em);
            em.persist(product);
            commitTransaction(em);

            Product productReRead = em.find(Product.class, product.getId());
            productReRead.setName("Beef Jerky");
            productReRead.setCountryCode("USA");
            productReRead.setBarCode1(new BarCode("722-666-489", "USA"));
            productReRead.setBarCode2(null);

            beginTransaction(em);
            em.merge(productReRead);
            commitTransaction(em);

            Product productReRead2 = em.find(Product.class, product.getId());
            productReRead2.setName("OREO");
            productReRead2.setCountryCode("USA");
            String oreoCodeNumber = "762-230-051";
            BarCode barcode = new BarCode(oreoCodeNumber, "USA");
            productReRead2.setBarCode1(barcode);
            productReRead2.setBarCode2(barcode);

            beginTransaction(em);
            em.merge(productReRead2);
            commitTransaction(em);

            Product oreoProduct = em.find(Product.class, product.getId());
            BarCode oreoBarCode1 = oreoProduct.getBarCode1();
            BarCode oreoBarCode2 = oreoProduct.getBarCode2();
            assertEquals(oreoCodeNumber, oreoBarCode1.getCodeNumber());
            assertEquals(oreoCodeNumber, oreoBarCode2.getCodeNumber());
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            throw e;
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Remove an object from the shared cache as if garbage collected
     * NPE will occur when it is read in using the CacheStoreMode.BYPASS setting outside a transaction.
     */
    public void testReadingEntityGarbageCollectedFromSharedCache(){
        EntityManager em = createEntityManager();
        Employee emp = (Employee)em.createQuery("Select e from Employee e").getResultList().get(0);
        em.clear();
        clearCache();
//        CacheKey key = this.getServerSession().getIdentityMapAccessorInstance().getCacheKeyForObject(emp);
//        key.setObject(null);
        try{
            //query for the shared object that has a cachekey but was garbage collected from the shared cache
            Query q = em.createQuery("Select f from Employee f where f.id ="+emp.getId());
            //only seems to reproduce with this setting and not in an early transaction
            q.setHint(QueryHints.CACHE_STORE_MODE, CacheStoreMode.BYPASS);
            try {
                Employee clone = (Employee)q.getSingleResult();
                clone.toString();
            }catch (NullPointerException e){
                fail("NPE occured building an Entity whos reference in the shared cache was garbage collected: "+e);
            }
        }finally {
            if (this.isTransactionActive(em)) {
                this.rollbackTransaction(em);
            }
            getServerSession().getIdentityMapAccessorInstance().initializeAllIdentityMaps();
        }
    }


    /**
     * Tests a @PrivateOwned @OneToMany mapping.
     */
    public void testRemoveJillWithPrivateOwnedPhoneNumbers() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) em.createNamedQuery("findAllEmployeesByFirstName", Employee.class);
            query.setParameter("firstname", "Jill");
            @SuppressWarnings({"unchecked"})
            Collection<Employee> employees = query.getResultCollection();

            if (employees.isEmpty()) {
                fail("No employees named Jill were found. Test requires at least one Jill to be created in the EmployeePopulator.");
            } else {
                Employee jill = employees.iterator().next();
                Collection<PhoneNumber> phoneNumbers = jill.getPhoneNumbers();

                if (phoneNumbers.isEmpty()) {
                    fail("Jill does not have any phone numbers. Test requires that Jill have atleast one phone number created in the EmployeePopulator.");
                }

                // Re-assign her managed employees and remove from her list.
                for (Employee employee : jill.getManagedEmployees()) {
                    employee.setManager(jill.getManager());
                }
                jill.getManagedEmployees().clear();

                int jillId = jill.getId();

                em.remove(jill);
                commitTransaction(em);

                assertNull("Jill herself was not removed.", em.find(Employee.class, jillId));

                for (PhoneNumber phoneNumber : phoneNumbers) {
                    assertNull("Jill's phone numbers were not deleted.", em.find(PhoneNumber.class, phoneNumber.buildPK()));
                }
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Creates some new equipment objects.
     */
    public void testCreateNewEquipment() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            // Persist some equipment.
            Equipment equip1 = new Equipment();
            equip1.setDescription("Toaster");
            em.persist(equip1);

            Equipment equip2 = new Equipment();
            equip1.setDescription("Bucket");
            em.persist(equip2);

            Equipment equip3 = new Equipment();
            equip1.setDescription("Broom");
            em.persist(equip3);

            commitTransaction(em);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests adding objects to a 1-M mapping that uses a map.
     */
    public void testAddNewEquipmentToDepartment() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) em.createNamedQuery("findAllSQLEquipment", Employee.class);
            @SuppressWarnings({"unchecked"})
            Collection<Equipment> equipment = query.getResultCollection();

            if (equipment.isEmpty()) {
                fail("No Equipment was found. testCreateNewEquipment should have created new equipment and should have run before this test.");
            } else {
                Department department = new Department();
                department.setName("Department with equipment");

                for (Equipment e : equipment) {
                    department.addEquipment(e);
                }

                em.persist(department);
                deptId = department.getId();
                commitTransaction(em);
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests that backpointers are not changed after a merge operation.
     */
    public void testBackpointerOnMerge() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            // create a new department
            Department department = new Department();
            department.setName("Football");
            // persist the department
            em.persist(department);
            commitTransaction(em);
            closeEntityManager(em);

            // add equipment to the department
            em = createEntityManager();
            beginTransaction(em);
            Equipment equipment = new Equipment();
            equipment.setDescription("Shields & Dummies");
            department.addEquipment(equipment);
            em.merge(department);
            commitTransaction(em);
            closeEntityManager(em);

            assertSame(department.getEquipment().get(0), equipment);
            assertEquals(System.identityHashCode(department.getEquipment().get(0)), System.identityHashCode(equipment));
            assertEquals(department.getId(), equipment.getDepartment().getId());
            assertSame("The department instance (backpointer) from equipment was modified after merge.", department, equipment.getDepartment());
            assertEquals("The department instance (backpointer) from equipment was modified after merge.", System.identityHashCode(department), System.identityHashCode(equipment.getDepartment()));
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            throw e;
        }
    }

    /**
     * Tests a @NamedStoredProcedureQuery.
     */
    public void testNamedStoredProcedureQuery() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Address address2 = (Address) em.createNamedQuery("SProcAddress").setParameter("ADDRESS_ID", address1.getID()).getSingleResult();
            assertNotNull("Address returned from stored procedure is null", address2);
            assertNotSame("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1, address2); // new
            assertTrue("Address build correctly using stored procedure", (address2.getID() == address1.getID()));
            assertEquals("Address build correctly using stored procedure", address2.getStreet(), address1.getStreet());
            assertEquals("Address build correctly using stored procedure", address2.getCountry(), address1.getCountry());
            assertEquals("Address build correctly using stored procedure", address2.getProvince(), address1.getProvince());

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a @NamedStoredProcedureQuery using indexed parameters.
     */
    public void testNamedStoredProcedureQueryByIndex() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Address address2 = (Address) em.createNamedQuery("SProcAddressByIndex").setParameter("ADDRESS_ID", address1.getID()).getSingleResult();
            assertNotNull("Address returned from stored procedure is null", address2);
            assertNotSame("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1, address2); // new
            assertTrue("Address build correctly using stored procedure", (address2.getID() == address1.getID()));
            assertEquals("Address build correctly using stored procedure", address2.getStreet(), address1.getStreet());
            assertEquals("Address build correctly using stored procedure", address2.getCountry(), address1.getCountry());
            assertEquals("Address build correctly using stored procedure", address2.getProvince(), address1.getProvince());

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a @NamedStoredProcedureQuery using a result-set mapping.
     * 304400: Note: this test does not actually test a ResultSet return - it uses output parameters.
     *              To enable ResultSet testing - set returnsResultSet=true via orm.xml, annotation or via program call like the the core tests.
     */
    public void testNamedStoredProcedureQueryWithResultSetMapping() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Address address2 = (Address) em.createNamedQuery("SProcAddressWithResultSetMapping").setParameter("address_id_v", address1.getID()).getSingleResult();
            assertNotNull("Address returned from stored procedure is null", address2);
            assertNotSame("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1, address2); // new
            assertTrue("Address not found using stored procedure", (address2.getID() == address1.getID()));

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a @NamedStoredProcedureQuery using a result-set mapping.
     * 304400: Note: this test does not actually test a ResultSet return - it uses output parameters.
     *              To enable ResultSet testing - set returnsResultSet=true via orm.xml, annotation or via program call like the the core tests.
     */
    public void testNamedStoredProcedureQueryWithResultSetFieldMapping() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263: we do not need to clear the cache for non-entity returns
            Object[] values = (Object[]) em.createNamedQuery("SProcAddressWithResultSetFieldMapping").setParameter("address_id_v", address1.getID()).getSingleResult();
            assertTrue("Address data not found or returned using stored procedure", ((values!=null) && (values.length==6)) );
            assertNotNull("No results returned from store procedure call", values[1]);
            assertEquals("Address not found using stored procedure", address1.getStreet(), values[1]);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a @NamedStoredProcedureQuery.
     */
    public void testNamedStoredProcedureQueryInOut() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263 and 302316: clear the cache or we will end up with a false positive when comparing the entity to itself later
            em.clear();

            Query aQuery = em.createNamedQuery("SProcInOut").setParameter("ADDRESS_ID", address1.getID());
            Address address2 = (Address) aQuery.getSingleResult();

            assertNotNull("Address returned from stored procedure is null", address2);
            assertNotSame("Address returned is the same cached instance that was persisted - the cache must be disabled for this test", address1, address2); // new
            assertEquals("Address not found using stored procedure", address1.getID(), address2.getID());
            assertTrue("Address.street data returned doesn't match persisted address.street", address1.getStreet().equalsIgnoreCase(address2.getStreet()));

        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);

            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    /**
     * Tests a @NamedStoredProcedureQuery that returns raw data
     * bug 254946
     */
    public void testNamedStoredProcedureQueryWithRawData() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);

            // 260263: we do not need to clear the cache for non-entity returns
            Query aQuery = em.createNamedQuery("SProcInOutReturningRawData").setParameter("ADDRESS_ID", address1.getID());
            Object[] objectdata = (Object[])aQuery.getSingleResult();

            assertTrue("Address data not found or returned using stored procedure", ((objectdata!=null)&& (objectdata.length==2)) );
            assertTrue("Address Id data returned doesn't match persisted address", (address1.getID() == (Long) objectdata[0]) );
            assertEquals("Address Street data returned doesn't match persisted address", address1.getStreet(), objectdata[1]);
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    // Test for lazy OneToOne relation not getting triggered
    public void testNonTriggerLazyForSProc() {
        if (!supportsStoredProcedures()) {
            return;
        }
        Employee employee2;
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Employee employee1 = new Employee();

            employee1.setFirstName("Me");
            employee1.setId(11);
            HugeProject hp = new HugeProject("big proj");

            employee1.setHugeProject(hp);
            em.persist(hp);
            em.persist(employee1);

            commitTransaction(em);
            em.clear();

            beginTransaction(em);

            Query q = em.createNamedQuery("SProcEmployee");
            q.setParameter("EMP_ID", employee1.getId());
            q.setFlushMode(FlushModeType.COMMIT);
            q.setHint(QueryHints.REFRESH, HintValues.TRUE);
            q.setHint(QueryHints.REFRESH_CASCADE, CascadePolicy.CascadeByMapping);
            employee2 = (Employee) q.getSingleResult();

            ProviderUtil util = (new PersistenceProvider()).getProviderUtil();
            //status can be LoadState.NOT_LOADED or LoadState.UNKNOWN
            Assert.assertNotEquals("ProviderUtil returned LOADED for isLoaded for hugeProject when it should not.", util.isLoadedWithReference(employee2, "hugeProject"), LoadState.LOADED);

        } catch (RuntimeException e) {
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    /**
     * Tests an OptimisticLockingException is thrown when calling merge a detached and removed object.
     * bug 272704
     */
    public void testOptimisticLockExceptionOnMerge() {
        EntityManager em1 = createEntityManager();
        beginTransaction(em1);

        RuntimeException caughtException = null;
        try {
            Employee emp = new Employee();
            emp.setLastName("OptimisticLockExceptionOnMerge");
            emp.setVersion(10);
            em1.persist(emp);
            commitTransaction(em1);
            closeEntityManager(em1);

            em1 = createEntityManager();
            beginTransaction(em1);

            emp = em1.find(Employee.class, emp.getId());
            em1.remove(emp);
            commitTransaction(em1);
            closeEntityManager(em1);

            em1 = createEntityManager();
            beginTransaction(em1);
            //this is expected to throw an OptimisticLockException, because the version is >1
            em1.merge(emp);

            commitTransaction(em1);

        } catch (RuntimeException e) {
            caughtException = e;
            if (isTransactionActive(em1)){
                rollbackTransaction(em1);
            }
        }
        closeEntityManager(em1);
        if (caughtException == null) {
            fail("Optimistic lock exception was not thrown.");
        } else if (!(caughtException instanceof jakarta.persistence.OptimisticLockException)) {
            // Re-throw exception to ensure stacktrace appears in test result.
            throw caughtException;
        }
    }

    /**
     * Tests an OptimisticLockingException is thrown when calling merge a detached and removed object.
     * bug 272704
     */
    public void testOptimisticLockExceptionOnMergeWithAssumeExists() {
        org.eclipse.persistence.sessions.Project project = getServerSession().getProject();
        ClassDescriptor descriptor = project.getDescriptor(Employee.class);
        int existencePolicy = descriptor.getQueryManager().getDoesExistQuery().getExistencePolicy();

        descriptor.getQueryManager().assumeExistenceForDoesExist();

        try {
            testOptimisticLockExceptionOnMerge();
        } finally {
            descriptor.getQueryManager().getDoesExistQuery().setExistencePolicy(existencePolicy);
        }
    }

    /**
     * Tests a @NamedStoredProcedureQuery that returns raw data using executeUpdate().
     */
    public void testModifyNamedStoredProcedureQueryWithRawData() {
        if (!supportsStoredProcedures()) {
            return;
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Address address1 = new Address();

            address1.setCity("Ottawa");
            address1.setPostalCode("K1G6P3");
            address1.setProvince("ON");
            address1.setStreet("123 Street");
            address1.setCountry("Canada");

            em.persist(address1);
            commitTransaction(em);
            beginTransaction(em);

            em.createNamedQuery("SProcInOutReturningRawData").setParameter("ADDRESS_ID", address1.getID()).getSingleResult();
       } catch (RuntimeException exception) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw exception;
        }
        commitTransaction(em);
        closeEntityManager(em);
    }

    /**
     * Tests a @PrivateOwned @OneToMany mapping.
     */
    public void testRemoveDepartmentWithPrivateOwnedEquipment() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Department department = em.find(Department.class, deptId);

            if (department == null) {
                fail("Department with id="+deptId+", was not found.");
            } else {
                Collection<Equipment> equipment = department.getEquipment().values();

                if (equipment.isEmpty()){
                    fail("Department with id="+deptId+", did not have any equipment.");
                } else {
                    em.remove(department);
                    commitTransaction(em);

                    assertNull("Department itself was not removed.", em.find(Department.class, deptId));

                    for (Equipment e : equipment) {
                        assertNull("New equipment was not deleted.", em.find(Equipment.class, e.getId()));
                    }
                }
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    public void testRelationshipReadDuringClone(){
        EntityManager em = createEntityManager();
        AbstractSession session = getServerSession();
        ClassDescriptor departmentDesc = session.getDescriptor(Department.class);
        DescriptorEventAdapter listener = new DescriptorEventAdapter(){
            @Override
            public void postClone(DescriptorEvent event) {
                ((Department)event.getObject()).getEquipment().size();
            }
        };
        departmentDesc.getDescriptorEventManager().addListener(listener);
        em.createQuery("SELECT e from Equipment e where e.department is not null").getResultList();
        departmentDesc.getDescriptorEventManager().removeListener(listener);
        departmentDesc.getDescriptorEventManager().initialize(session);
        closeEntityManager(em);
    }

    /**
     * Tests trying to update a read only class.
     */
    public void testUpdateReadOnlyEquipmentCode() {
        EntityManager em = createEntityManager();
        beginTransaction(em);

        try {
            Query query = em.createNamedQuery("findSQLEquipmentCodeA");
            EquipmentCode equipmentCode = (EquipmentCode) query.getSingleResult();

            equipmentCode.setCode("Z");
            commitTransaction(em);

            // Nothing should have been written to the database. Query for
            // EquipmentCode A again. If an exception is caught, then it was
            // not found, therefore, updated on the db.
            try {
                query = em.createNamedQuery("findSQLEquipmentCodeA");
                query.getSingleResult();
            } catch (Exception e) {
                fail("The read only EquipmentA was modified");
            }
        } catch (RuntimeException e) {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }

            closeEntityManager(em);
            // Re-throw exception to ensure stacktrace appears in test result.
            throw e;
        }

        closeEntityManager(em);
    }

    public void testMethodBasedTransformationMapping() {
        internalTestTransformationMapping("normalHours");
    }

    public void testClassBasedTransformationMapping() {
        internalTestTransformationMapping("overtimeHours");
    }

    //Bug#391251 : Test for @Column outside WriteTransformer annotation
    public void testTransformationMappingWithColumnAnnotation() {
        Door door = new Door();
        door.setId(100);
        door.setHeight(8);
        door.setWidth(5);
        door.setRoom(null);
        int year = 2013;
        int month = 1;
        int day = 30;
        door.setSaleDate(Helper.dateFromYearMonthDate(year, month - 1, day));

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(door);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        int id = door.getId();
        String errorMsg = "";
        try {
            this.clearCache();
            em = createEntityManager();
            door = em.find(Door.class, 100);
            Calendar calendarSaleDate = Calendar.getInstance();
            calendarSaleDate.setTime(door.getSaleDate());
            if(calendarSaleDate.get(Calendar.YEAR) != year || calendarSaleDate.get(Calendar.MONTH) != (month - 1) || calendarSaleDate.get(Calendar.DAY_OF_MONTH) != day) {
               errorMsg = "saleDate = " + door.getSaleDate().toString() + " is wrong";
            }
        } catch (RuntimeException ex) {
            fail("Failed to fetch data for testTransformationMappingWithColumnAnnotation" + ex.getMessage());
        }

        // clean up
        beginTransaction(em);
        try {
            door = em.find(Door.class, door.getId());
            em.remove(door);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        if (!errorMsg.isEmpty()) {
            fail(errorMsg);
        }
    }

    protected void internalTestTransformationMapping(String attributeName) {
        // setup: create an Employee, insert into db
        int startHour = 8, startMin = 30, startSec = 15;
        int endHour = 17, endMin = 15, endSec = 45;
        String firstName = attributeName;
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        if(attributeName.equals("normalHours")) {
            employee.setStartTime(Helper.timeFromHourMinuteSecond(startHour, startMin, startSec));
            employee.setEndTime(Helper.timeFromHourMinuteSecond(endHour, endMin, endSec));
        } else if(attributeName.equals("overtimeHours")) {
            employee.setStartOvertime(Helper.timeFromHourMinuteSecond(startHour, startMin, startSec));
            employee.setEndOvertime(Helper.timeFromHourMinuteSecond(endHour, endMin, endSec));
        } else {
            throw new RuntimeException("Unknown attributeName");
        }
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.persist(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
        int id = employee.getId();

        // test
        // clear cache
        this.clearCache();
        // read the employee from the db
        em = createEntityManager();
        employee = em.find(Employee.class,id);

        // verify
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        if(attributeName.equals("normalHours")) {
            calendarStart.setTime(employee.getStartTime());
            calendarEnd.setTime(employee.getEndTime());
        } else if(attributeName.equals("overtimeHours")) {
            calendarStart.setTime(employee.getStartOvertime());
            calendarEnd.setTime(employee.getEndOvertime());
        }
        String errorMsg = "";
        if(calendarStart.get(Calendar.HOUR_OF_DAY) != startHour || calendarStart.get(Calendar.MINUTE) != startMin || calendarStart.get(Calendar.SECOND) != startSec) {
            if(attributeName.equals("normalHours")) {
                errorMsg = "startTime = " + employee.getStartTime().toString() + " is wrong";
            } else if(attributeName.equals("overtimeHours")) {
                errorMsg = "startOvertime = " + employee.getStartOvertime().toString() + " is wrong";
            }
        }
        if(calendarEnd.get(Calendar.HOUR_OF_DAY) != endHour || calendarEnd.get(Calendar.MINUTE) != endMin || calendarEnd.get(Calendar.SECOND) != endSec) {
            if(!errorMsg.isEmpty()) {
                errorMsg = errorMsg + "; ";
            }
            if(attributeName.equals("normalHours")) {
                errorMsg = "endTime = " + employee.getEndTime().toString() + " is wrong";
            } else if(attributeName.equals("overtimeHours")) {
                errorMsg = "endOvertime = " + employee.getEndOvertime().toString() + " is wrong";
            }
        }

        // clean up
        beginTransaction(em);
        try {
            employee = em.find(Employee.class, employee.getId());
            em.remove(employee);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        if(!errorMsg.isEmpty()) {
            fail(errorMsg);
        }
    }
    
    // test case for Bug25872190 ElBug538183
    public void testCursoredNativeQueryDefinedViaAnnotation() {

        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            Query q = em.createNamedQuery("allTestEntitiesAnnotated");
            q.setHint(QueryHints.CURSOR, true);
            try {
                CursoredStream stream = (CursoredStream) q.getSingleResult();
                assertTrue(stream.hasNext());
            } catch (QueryException qe) {
               fail("Cursored Native Query via Annotation threw QueryException");
            }
            commitTransaction(em);
        }finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }

    public void testCursorStream() {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        Room room1 = new Room();
        Door east = new Door();
        try {
        //Setup Rooms and Doors
        room1.setId(10001);
        east.setId(100);
        east.setHeight(8);
        east.setWidth(5);
        int year = 2013;
        int month = 1;
        int day = 30;
        east.setSaleDate(Helper.dateFromYearMonthDate(year, month - 1, day));
        east.setRoom(room1);
        room1.addDoor(east);

        try {
            em.persist(room1);
            commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        em = createEntityManager();

        Query resultsXferQuery = em.createQuery("SELECT r, d "
                + "FROM Room r "
                + "JOIN FETCH r.doors "
                + "LEFT JOIN Door d "
                + "WHERE d MEMBER OF r.doors"
                );

        resultsXferQuery.setHint(QueryHints.CURSOR, true);

        try {
            CursoredStream stream = (CursoredStream)resultsXferQuery.getSingleResult();
            while (!stream.atEnd()) {
                stream.read();
            }
            stream.clear();
            stream.close();
        } catch (NullPointerException npe) {
            fail("CursoredStream with join threw an NPE");
        }
        // clean up
        beginTransaction(em);
        room1 = em.find(Room.class, room1.getId());
        em.remove(room1);
        commitTransaction(em);
        } finally {
            if (isTransactionActive(em)){
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    /**
     * Tests Property and Properties annotations
     */
    public void testProperty() {
        EntityManager em = createEntityManager();
        ClassDescriptor descriptor = getServerSession().getDescriptorForAlias("Employee");
        ClassDescriptor aggregateDescriptor = getServerSession().getDescriptor(EmploymentPeriod.class);

        closeEntityManager(em);

        StringBuilder errorMsg = new StringBuilder();

        if (descriptor == null) {
            errorMsg.append(" Descriptor for Employee alias was not found;");
        }
        if (aggregateDescriptor == null) {
            errorMsg.append(" Descriptor for EmploymentPeriod.class was not found;");
        }
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }

        // verify properties set on Employee instance
        errorMsg.append(verifyPropertyValue(descriptor, "entityName", String.class, "Employee"));
        errorMsg.append(verifyPropertyValue(descriptor, "entityIntegerProperty", Integer.class, 1));

        // each attribute of Employee was assigned a property attributeName with the value attribute name.
        for(DatabaseMapping mapping : descriptor.getMappings()) {
            errorMsg.append(verifyPropertyValue(mapping, "attributeName", String.class, mapping.getAttributeName()));
        }

        // attribute m_lastName has many properties of different types
        DatabaseMapping mapping = descriptor.getMappingForAttributeName("lastName");
        errorMsg.append(verifyPropertyValue(mapping, "BooleanProperty", Boolean.class, Boolean.TRUE));
        errorMsg.append(verifyPropertyValue(mapping, "ByteProperty", Byte.class, (byte) 1));
        errorMsg.append(verifyPropertyValue(mapping, "CharacterProperty", Character.class, 'A'));
        errorMsg.append(verifyPropertyValue(mapping, "DoubleProperty", Double.class, 1.0));
        errorMsg.append(verifyPropertyValue(mapping, "FloatProperty", Float.class, 1F));
        errorMsg.append(verifyPropertyValue(mapping, "IntegerProperty", Integer.class, 1));
        errorMsg.append(verifyPropertyValue(mapping, "LongProperty", Long.class, 1L));
        errorMsg.append(verifyPropertyValue(mapping, "ShortProperty", Short.class, (short) 1));
        errorMsg.append(verifyPropertyValue(mapping, "BigDecimalProperty", java.math.BigDecimal.class, java.math.BigDecimal.ONE));
        errorMsg.append(verifyPropertyValue(mapping, "BigIntegerProperty", java.math.BigInteger.class, java.math.BigInteger.ONE));
        errorMsg.append(verifyPropertyValue(mapping, "byte[]Property", byte[].class, new byte[]{1, 2, 3, 4}));
        errorMsg.append(verifyPropertyValue(mapping, "char[]Property", char[].class, new char[]{'a', 'b', 'c'}));
        errorMsg.append(verifyPropertyValue(mapping, "Byte[]Property", Byte[].class, new byte[]{1, 2, 3, 4}));
        errorMsg.append(verifyPropertyValue(mapping, "Character[]Property", Character[].class, new char[]{'a', 'b', 'c'}));
        errorMsg.append(verifyPropertyValue(mapping, "TimeProperty", java.sql.Time.class, Helper.timeFromString("13:59:59")));
        errorMsg.append(verifyPropertyValue(mapping, "TimeStampProperty", java.sql.Timestamp.class, Helper.timestampFromString("2008-04-10 13:59:59")));
        errorMsg.append(verifyPropertyValue(mapping, "DateProperty", java.sql.Date.class, Helper.dateFromString("2008-04-10")));

        // verify property set on EmploymentPeriod embeddable
        errorMsg.append(verifyPropertyValue(aggregateDescriptor, "embeddableClassName", String.class, "EmploymentPeriod"));

        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }
    protected String verifyPropertyValue(ClassDescriptor descriptor, String propertyName, Class<?> expectedPropertyValueType, Object expectedPropertyValue) {
        return verifyPropertyValue(propertyName, descriptor.getProperty(propertyName), expectedPropertyValueType, expectedPropertyValue, descriptor.getJavaClass().getSimpleName() + " descriptor");
    }
    protected String verifyPropertyValue(DatabaseMapping mapping, String propertyName, Class<?> expectedPropertyValueType, Object expectedPropertyValue) {
        return verifyPropertyValue(propertyName, mapping.getProperty(propertyName), expectedPropertyValueType, expectedPropertyValue, mapping.getAttributeName() + " attribute");
    }
    protected String verifyPropertyValue(String propertyName, Object propertyValue, Class<?> expectedPropertyValueType, Object expectedPropertyValue, String masterName) {
        String errorMsg = "";
        String errorPrefix = " property " + propertyName + " for " + masterName;
        if(expectedPropertyValueType == null) {
            if(propertyValue != null) {
                errorMsg = errorPrefix + " value is " + propertyValue + " , was expected to be null.";
            }
            return errorMsg;
        }
        if(propertyValue == null) {
            errorMsg = errorPrefix + " is missing;";
        } else if(!expectedPropertyValueType.isInstance(propertyValue)) {
            errorMsg = errorPrefix + " is instance of " + propertyValue.getClass().getName() + ", " + expectedPropertyValueType.getName() + " was expected;";
        } else {
            if(propertyValue.getClass().isArray()) {
                if(Array.getLength(propertyValue) != Array.getLength(expectedPropertyValue)) {
                    errorMsg = errorPrefix + " has array value of size " + Array.getLength(propertyValue) + ", " + Array.getLength(expectedPropertyValue) + " was expected;";
                } else {
                    for(int i=0; i < Array.getLength(propertyValue); i++) {
                        if(!Array.get(propertyValue, i).equals(Array.get(expectedPropertyValue, i))) {
                            errorMsg = errorPrefix + " has array with "+i+"th element value " + Array.get(propertyValue, i).toString() + ", " + Array.get(expectedPropertyValue, i).toString() + " was expected;";
                        }
                    }
                }
            } else if (!propertyValue.equals(expectedPropertyValue)) {
                errorMsg = errorPrefix + " has value " + propertyValue + ", " + expectedPropertyValue.toString() + " was expected;";
            }
        }
        return errorMsg;
    }

    public void testUnidirectionalPersist() {
        String lastName = "testUnidirectionalPersist";

        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName);

        // clear cache
        clearCache();

        // read the persisted employees back
        EntityManager em = createEntityManager();
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        closeEntityManager(em);

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            //clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Persisted " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        ServerSession session = JUnitTestCase.getServerSession();
        StringBuilder errorMsg = new StringBuilder();
        for (Employee value : employeesPersisted) {
            for (Employee employee : employeesRead) {
                if (value.getFirstName().equals(employee.getFirstName())) {
                    if (!session.compareObjects(value, employee)) {
                        errorMsg.append("Employee ").append(value.getFirstName()).append("  was not persisted correctly.");
                    }
                }
            }
        }

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }

    public void testUnidirectionalUpdate() {
        String lastName = "testUnidirectionalUpdate";

        // em used for both persist and update
        EntityManager em = createEntityManager();
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName, em);
        // update persisted employees:
        beginTransaction(em);
        try {
            // add a new dealer to the first employee
            employeesPersisted.get(0).addDealer(new Dealer("New", lastName));
            // remove a dealer from the second employee
            employeesPersisted.get(1).getDealers().remove(1);
            // move a dealer from the first to the second employee
            employeesPersisted.get(1).addDealer(employeesPersisted.get(0).getDealers().remove(0));
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }

        // clear cache
        clearCache();

        em = createEntityManager();
        // read the updated employees back
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();

        // verify number persisted and read is the same
        if(employeesPersisted.size() != employeesRead.size()) {
            // clean-up
            deleteEmployeesWithUnidirectionalMappings(lastName);
            fail("Updated " + employeesPersisted.size() + " employees, but read back " + employeesRead.size());
        }

        // verify that the persisted and read objects are equal
        ServerSession session = JUnitTestCase.getServerSession();
        beginTransaction(em);
        StringBuilder errorMsg = new StringBuilder();
        try{
            for (Employee value : employeesPersisted) {
                for (Employee employee : employeesRead) {
                    if (isOnServer()) {
                        Employee emp1 = em.find(Employee.class, value.getId());
                        Employee emp2 = em.find(Employee.class, employee.getId());
                        if (emp1.getFirstName().equals(emp2.getFirstName())) {
                            if (!session.compareObjects(emp1, emp2)) {
                                errorMsg.append("Employee ").append(emp1.getFirstName()).append("  was not updated correctly.");
                            }
                        }
                    } else {
                        if (value.getFirstName().equals(employee.getFirstName())) {
                            if (!session.compareObjects(value, employee)) {
                                errorMsg.append("Employee ").append(value.getFirstName()).append("  was not updated correctly.");
                            }
                        }
                    }
                }
            }
        } finally {
           if(this.isTransactionActive(em)) {
               rollbackTransaction(em);
              }
               closeEntityManager(em);
        }

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }

    public void testUnidirectionalFetchJoin() {
        String lastName = "testUnidirectionalFetchJoin";

        // persist employees
        persistEmployeesWithUnidirectionalMappings(lastName);

        // clear cache
        clearCache();

        EntityManager em = createEntityManager();
        // read the persisted employees back - without fetch join
        List<Employee> employeesRead = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        closeEntityManager(em);

        // clear cache
        clearCache();

        // read the persisted employees back - with fetch join.
        em = createEntityManager();
        List<Employee> employeesReadWithFetchJoin = em.createQuery("SELECT e FROM Employee e JOIN FETCH e.dealers WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();
        closeEntityManager(em);

        // verify that the persisted and read employees are the same.
        // The comparison cascades to all references and requires the same state of indirection:
        // it fails in case an object has triggered indirection for particular attribute and compared object's indirection for this attribute is not triggered.
        // The expected result of join fetch query is Employee.dealers being triggered - so need to trigger it on the control collection (getDealers.size() does that);
        // also the expected result should have an object for each row returned - therefore number of inclusions of each Employee equals its dealers.size()
        List<Employee> employeesControl = new ArrayList<>();
        for (Employee employee : employeesRead) {
            int nDialers = employee.getDealers().size();
            for (int j = 0; j < nDialers; j++) {
                employeesControl.add(employee);
            }
        }
        ServerSession session = JUnitTestCase.getServerSession();
        String errorMsg = JoinedAttributeTestHelper.compareCollections(employeesControl, employeesReadWithFetchJoin, session.getClassDescriptor(Employee.class), session);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(!errorMsg.isEmpty()) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalTargetLocking_AddRemoveTarget() {
        String lastName = "testUnidirectionalTargetLocking_ART";

        EntityManager em = createEntityManager();
        // persist employees
        List<Employee> employeesPersisted = persistEmployeesWithUnidirectionalMappings(lastName, em);

        // remove a dealer from the second employee:
        Dealer dealer;
        beginTransaction(em);
        try {
            Employee emp1 = em.find(Employee.class, employeesPersisted.get(1).getId());
            dealer = emp1.getDealers().remove(1);
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        String errorMsg = "";

        // verify the version both in the cache and in the db
        int version2 = getVersion(em, dealer);
        if(version2 != 2) {
            errorMsg += "In the cache the removed dealer's version is " + version2 + " (2 was expected); ";
        }
        beginTransaction(em);
        try {
            Dealer dealer2 = em.find(Dealer.class, dealer.getId());
            em.refresh(dealer2);
            version2 = getVersion(em, dealer2);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        if(version2 != 2) {
            errorMsg += "In the db the removed dealer's version is " + version2 + " (2 was expected); ";
        }
        beginTransaction(em);
        Dealer dealer3;
        try {
            Employee emp2 = em.find(Employee.class, employeesPersisted.get(1).getId());
            dealer3 = em.find(Dealer.class, dealer.getId());
            emp2.getDealers().add(dealer3);
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        // verify the version both in the cache and in the db
        int version3 = getVersion(em, dealer3);
        if(version3 != 3) {
            errorMsg += "In the cache the added dealer's version is " + version3 + " (3 was expected); ";
        }

        beginTransaction(em);
        try {
            Dealer dealer4 = em.find(Dealer.class, dealer3.getId());
            em.refresh(dealer4);
            version3 = getVersion(em, dealer4);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        if(version3 != 3) {
            errorMsg += "In the db the added dealer's version is " + version3 + " (3 was expected)";
        }

        closeEntityManager(em);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(!errorMsg.isEmpty()) {
            fail(errorMsg);
        }
    }

    public void testUnidirectionalTargetLocking_DeleteSource() {
        String lastName = "testUnidirectionalTargetLocking_DS";

        // persist employees (there should be two of them)
        List<Employee> persistedEmployees = persistEmployeesWithUnidirectionalMappings(lastName);
        // cache their dealers' ids
        ArrayList<Integer> dealersIds = new ArrayList<>();
        for (Employee emp : persistedEmployees) {
            for (int j = 0; j < emp.getDealers().size(); j++) {
                dealersIds.add(emp.getDealers().get(j).getId());
            }
        }

        // clear cache
        clearCache();

        EntityManager em = createEntityManager();
        beginTransaction(em);
        // read the persisted employees
        List<Employee> readEmployees = em.createQuery("SELECT OBJECT(e) FROM Employee e WHERE e.lastName = '"+lastName+"'", Employee.class).getResultList();

        // trigger indirection on the second employee's dealers
        readEmployees.get(1).getDealers().size();

        // delete the Employees (there should be two of them).
        try {
            for(Employee emp:  readEmployees) {
                em.remove(emp);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
                closeEntityManager(em);
            }
        }

        // find employees' dealers and verify their versions - all should be 2.
        beginTransaction(em);
        StringBuilder errorMsg = new StringBuilder();
        try{
            for (Integer dealersId : dealersIds) {
                Dealer dealer = em.find(Dealer.class, dealersId);

                // verify the version both in the cache and in the db
                int version2 = getVersion(em, dealer);
                if (version2 != 2) {
                    errorMsg.append("In the cache dealer ").append(dealer.getFirstName()).append("'s version is ").append(version2).append(" (2 was expected); ");
                }
                em.refresh(dealer);

                version2 = getVersion(em, dealer);
                if (version2 != 2) {
                    errorMsg.append("In the db dealer ").append(dealer.getFirstName()).append("'s version is ").append(version2).append(" (2 was expected); ");
                }
            }
        } finally {
           if(this.isTransactionActive(em)) {
               rollbackTransaction(em);
           }
        }

        closeEntityManager(em);

        // clean-up
        deleteEmployeesWithUnidirectionalMappings(lastName);

        // non-empty error message means the test has failed
        if(errorMsg.length() > 0) {
            fail(errorMsg.toString());
        }
    }

    /**
     * Fix for bug 247078: eclipselink-orm.xml schema should allow lob and
     * enumerated on version and id mappings
     */
    public void testEnumeratedPrimaryKeys(){
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            ViolationCode codeA = new ViolationCode();
            codeA.setId(ViolationCodeId.A);
            codeA.setDescription("Violation A");
            em.persist(codeA);

            ViolationCode codeB = new ViolationCode();
            codeB.setId(ViolationCodeId.B);
            codeB.setDescription("Violation B");
            em.persist(codeB);

            ViolationCode codeC = new ViolationCode();
            codeC.setId(ViolationCodeId.C);
            codeC.setDescription("Violation C");
            em.persist(codeC);

            ViolationCode codeD = new ViolationCode();
            codeD.setId(ViolationCodeId.D);
            codeD.setDescription("Violation D");
            em.persist(codeD);

            Violation violation = new Violation();
            violation.setId(ViolationID.V1);
            violation.getViolationCodes().add(codeA);
            violation.getViolationCodes().add(codeC);
            violation.getViolationCodes().add(codeD);
            em.persist(violation);

            commitTransaction(em);

            // Force the read to hit the database and make sure the violation is read back.
            clearCache();
            em.clear();
            Violation refreshedViolation = em.find(Violation.class, violation.getId());
            assertNotNull("Unable to read back the violation", refreshedViolation);
            assertTrue("Violation object did not match after refresh", getServerSession().compareObjects(violation, refreshedViolation));
        } catch (Exception e) {
            fail("An error occurred: " + e.getMessage());
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Bug 400022
     * Test batch fetch with join fetch with batching on a M:M
     */
    public void testProjectToEmployeeWithBatchFetchJoinFetch() {
        clearCache();
        EntityManager em = createEntityManager();
        beginTransaction(em);
        // Count SQL.
        QuerySQLTracker counter = new QuerySQLTracker(getServerSession());
        try {
            TypedQuery<Project> query = em.createQuery("SELECT p FROM Project p", Project.class);
            query.setHint(QueryHints.BATCH, "p.teamMembers");
            query.setHint(QueryHints.BATCH, "p.teamMembers.projects");
            query.setHint(QueryHints.BATCH, "p.teamMembers.phoneNumbers.owner");
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.FETCH, "p.teamMembers.address");
            query.setHint(QueryHints.FETCH, "p.teamMembers.phoneNumbers");

            List<Project> results = query.getResultList();

            // XXX - size here may vary depending on the model
            if (isWeavingEnabled() && counter.getSqlStatements().size() > 9) {
                fail("Should have been 9 queries but was: " + counter.getSqlStatements().size());
            }

            for (Project project : results) {
                assertNotNull("Project cannot be null", project);
                Employee employee = project.getTeamLeader();
                if (employee != null) {
                    employee.getProjects().size();
                }
            }

            if (isWeavingEnabled() && counter.getSqlStatements().size() > 18) {
                fail("Should have been 18 queries but was: " + counter.getSqlStatements().size());
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
            if (counter != null) {
                counter.remove();
            }
        }
    }

    /**
     * Bug 400022
     * Test batch fetch with join fetch on a 1:M
     */
    public void testEmployeeToPhoneNumberWithBatchFetchJoinFetch() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            Employee emp1 = new Employee();
            emp1.setMale();
            emp1.setFirstName("Mickey");
            emp1.setLastName("O'Neil");

            Employee emp2 = new Employee();
            emp2.setMale();
            emp2.setFirstName("Tony");
            emp2.setLastName("Bullet Tooth");

            em.persist(emp1);
            em.persist(emp2);

            em.flush();

            TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e", Employee.class);
            query.setHint(QueryHints.BATCH, "e.projects");
            query.setHint(QueryHints.BATCH, "e.projects.teamMembers");
            query.setHint(QueryHints.BATCH, "e.phoneNumbers.owner");
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.FETCH, "e.address");
            query.setHint(QueryHints.FETCH, "e.phoneNumbers");

            List<Employee> results = query.getResultList();
            for (Employee employee : results) {
                assertNotNull("Employee cannot be null", employee);
                Collection<PhoneNumber> phoneNumbers = employee.getPhoneNumbers();
                for (PhoneNumber phoneNumber : phoneNumbers) {
                    assertNotNull("PhoneNumber cannot be null", phoneNumber);
                }
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 400022
     * Test batch fetch with join fetch on a 1:1
     */
    public void testEmployeeToAddressWithBatchFetchJoinFetch() {
        EntityManager em = createEntityManager();

        try {
            beginTransaction(em);

            Employee emp1 = new Employee();
            emp1.setMale();
            emp1.setFirstName("Mickey");
            emp1.setLastName("O'Neil");

            Employee emp2 = new Employee();
            emp2.setMale();
            emp2.setFirstName("Tony");
            emp2.setLastName("Bullet Tooth");

            em.persist(emp1);
            em.persist(emp2);

            em.flush();

            TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e", Employee.class);
            query.setHint(QueryHints.BATCH, "e.projects");
            query.setHint(QueryHints.BATCH, "e.projects.teamMembers");
            query.setHint(QueryHints.BATCH, "e.phoneNumbers.owner");
            query.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
            query.setHint(QueryHints.FETCH, "e.address");
            query.setHint(QueryHints.FETCH, "e.phoneNumbers");

            List<Employee> results = query.getResultList();
            for (Employee employee : results) {
                assertNotNull("Employee cannot be null", employee);
                String addrString = String.valueOf(employee.getAddress());
            }
        } finally {
            rollbackTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 416003
     * Test batch fetch with join fetch on a 1:m
     */
    public void testEmployeeFetchWithAlias() {
        EntityManager em = createEntityManager();

        try {
            TypedQuery<Employee> query = em.createQuery("SELECT d FROM Employee d join fetch d.phoneNumbers p", Employee.class);

            List<Employee> results = query.getResultList();
            assertTrue ("Test failed because extra join resulted in too many results", results.size() < 30);
        }finally{
            closeEntityManager(em);
        }
    }


    /**
     * Bug 415082 - JoinFetch does not refresh cache even though REFRESH hint is set to true
     * Test that a refresh is performed on a queried and related object, with fetchjoins
     */
    public void testJoinFetchWithRefreshOnRelatedEntity() {
        // create test entities
        Employee emp = null;
        Department dept = null;
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            emp = new Employee("Bob", "Robertson");
            dept = new Department("Pomology");
            emp.setDepartment(dept);

            em.persist(emp);
            em.persist(dept);
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }

        // update tables directly
        em = createEntityManager();
        try {
            beginTransaction(em);

            Session session = ((JpaEntityManager)em.getDelegate()).getActiveSession();
            if(session.getDatasourcePlatform().isSQLServer()) {
                session.executeNonSelectingSQL("UPDATE t0 SET t0.F_NAME='Joe', t0.L_NAME='Josephson' FROM CMP3_EMPLOYEE t0 WHERE t0.EMP_ID=" + emp.getId());
                session.executeNonSelectingSQL("UPDATE t1 SET t1.NAME='Xenobiology' FROM CMP3_DEPT t1 WHERE t1.ID=" + dept.getId());
            } else if (session.getDatasourcePlatform().isPostgreSQL()){
                session.executeNonSelectingSQL("UPDATE CMP3_EMPLOYEE SET F_NAME='Joe', L_NAME='Josephson' WHERE EMP_ID=" + emp.getId());
                session.executeNonSelectingSQL("UPDATE CMP3_DEPT SET NAME='Xenobiology' WHERE ID=" + dept.getId());
            } else {
                session.executeNonSelectingSQL("UPDATE CMP3_EMPLOYEE t0 SET t0.F_NAME='Joe', t0.L_NAME='Josephson' WHERE t0.EMP_ID=" + emp.getId());
                session.executeNonSelectingSQL("UPDATE CMP3_DEPT t1 SET t1.NAME='Xenobiology' WHERE t1.ID=" + dept.getId());
            }

            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }

        // perform refreshing query
        em = createEntityManager();
        try {
            Query query = em.createQuery("select e from Employee e where e.id = :pk");
            query.setParameter("pk", emp.getId());
            query.setHint(QueryHints.REFRESH, HintValues.TRUE);
            Employee empReturned = (Employee)query.getSingleResult();

            // validate refresh
            assertNotNull("Employee should not be null", empReturned);
            Department deptReturned = empReturned.getDepartment();
            assertNotNull("Department should not be null", deptReturned);
            assertEquals("Employee should have the same id", emp.getId(), empReturned.getId());
            assertEquals("Department should have the same id", dept.getId(), deptReturned.getId());
            assertEquals("Employee's firstName should be refreshed", "Joe", empReturned.getFirstName());
            assertEquals("Employee's lastName should be refreshed", "Josephson", empReturned.getLastName());
            assertEquals("Department's name should be refreshed", "Xenobiology", deptReturned.getName());
        } finally {
            closeEntityManager(em);
        }

        // clean up
        em = createEntityManager();
        try {
            beginTransaction(em);
            em.remove(em.find(Employee.class, emp.getId()));
            em.remove(em.find(Department.class, dept.getId()));
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Bug 464088
     * Test non-historized Entity eager-referencing a historized Entity's queries function correctly
     * when read all queries are executed on a 'historical' session before a 'regular' session.
     * Uses very specialized non-cacheable entities: Oyster bi-directional eager 1:1 to: Pearl(historized)
     */
    public void testHistoryRelationshipQueryInitialization() {
        // setup
        EntityManager em = createEntityManager();
        
        Oyster oyster = null;
        Pearl pearl = null;
        try {
            beginTransaction(em);
            
            oyster = new Oyster();
            oyster.setColor("Black");
            pearl = new Pearl();
            pearl.setName("Bob");
            oyster.setPearl(pearl);
            pearl.setOyster(oyster);
            
            em.persist(oyster);
            em.flush();
            
            try {
                Thread.sleep(1000); // pause for a bit to allow timestamps to be different
            } catch (InterruptedException ie) {} // ignore
            
            oyster.getPearl().setName("Doug"); // change related entity for history entry
            
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
        
        // test
        em = createEntityManager();
        try {
            Calendar asOfDate = Calendar.getInstance();
            asOfDate.set(1999, 1, 1, 12, 0, 0);
            
            // execute a historical query against the non-historical Oyster entity
            Session client = JpaHelper.getServerSession(em.getEntityManagerFactory()).acquireClientSession();
            Session historical = client.acquireHistoricalSession(new AsOfClause(asOfDate));
            
            @SuppressWarnings({"unchecked"})
            List<Oyster> oysters = (List<Oyster>)historical.readAllObjects(Oyster.class);
            assertTrue("Historical query: Oysters should be non-empty", !oysters.isEmpty());
            for (Oyster oysterElem : oysters) {
                assertNull("Historical query: Oyster should not have a pearl", oysterElem.getPearl());
            }
            
            // execute a non historical query through JPQL against the Oyster entity
            oysters = em.createQuery("SELECT e FROM Oyster e", Oyster.class).getResultList();
            assertTrue("Jakarta Persistence query: Oysters should be non-empty", !oysters.isEmpty());
            for (Oyster oysterElem : oysters) {
                assertNotNull("Jakarta Persistence query: Oyster should have a pearl, historical query executed", oysterElem.getPearl());
            }
        } finally {
            closeEntityManager(em);
        }
        
        // reset
        em = createEntityManager();
        try {
            beginTransaction(em);
            oyster = em.find(Oyster.class, oyster.getId());
            if (oyster != null) {
                em.remove(oyster);
            }
            pearl = em.find(Pearl.class, pearl.getId());
            if (pearl != null) {
                em.remove(pearl);
            }
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Bug 470007 - NPE in normalize() when querying on ElementCollection-{@literal >}CollectionTable, with query results caching enabled
     * Tests querying across an Entity (ToDoList) containing an ElementCollection with a CollectionTable, referencing a
     * basic type (String). A NullPointerException was previously observed when query results caching is enabled on the query.  
     */
    public void testQueryJoinBasicCollectionTableUsingQueryResultsCache() {
        // setup
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            
            String commonName = "My List";
            String commonItem = "Feed the cat";
            
            // setup
            // ToDoList has a Set<String>, mapped to an @ElementCollection, using @CollectionTable (String)
            ToDoList bobsList = new ToDoList(1, commonName);
            bobsList.addItem(commonItem);
            bobsList.addItem("Cook dinner");
            bobsList.addItem("Watch TV");
            
            ToDoList jensList = new ToDoList(2, commonName);
            jensList.addItem("Feed the dog");
            jensList.addItem("Hire and fire");
            jensList.addItem("Eat chocolate");
            
            ToDoList bertsList = new ToDoList(3, commonName);
            bertsList.addItem(commonItem);
            bertsList.addItem("Feed the kids");
            bertsList.addItem("Feed the wife");
            
            em.persist(bobsList);
            em.persist(jensList);
            em.persist(bertsList);
            em.flush();
            
            // test
            TypedQuery<ToDoList> query = em.createQuery("SELECT tdl FROM ToDoList tdl JOIN tdl.items items " +
                    "WHERE tdl.name = :p_name AND items = :p_itemName", ToDoList.class);
            query.setHint(QueryHints.QUERY_RESULTS_CACHE, HintValues.TRUE);
            
            query.setParameter("p_name", commonName);
            query.setParameter("p_itemName", commonItem);
            List<ToDoList> listsReturned = query.getResultList();
            
            // verify
            assertNotNull("Query results should not be null", listsReturned);
            assertSame("Query results size", 2, listsReturned.size()); // 2 results expected
            
            for (ToDoList aList : listsReturned) {
                assertEquals(commonName, aList.getName());
                assertTrue(aList.getItems().contains(commonItem));
            }
            
        } finally {
            // reset - rollback
            if (isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
        }
    }
    
    /**
     * Bug 470161 - ServerSession links RepeatableWriteUnitOfWork via entity / IndirectList / QueryBasedValueHolder\
     * 
     * Through the UoW: Persist a new object, read an existing object in (building from rows, original not put in the
     * shared cache), associate existing object with new object, commit. Previously, after the changes are merged 
     * into the shared cache, the transparent collection on the existing object has a wrapped VH from the initial uow 
     * query, which internally references the uow session, not the shared session.
     */
    public void testTransparentIndirectionValueHolderSessionReset() {
        Employee emp = null;
        Dealer dealer = null;
        
        // setup
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);
            dealer = new Dealer();
            dealer.setFirstName("Angle");
            dealer.setLastName("Bracket");
            em.persist(dealer);
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
            clearCache(); // start test with an empty cache
        }
        
        // test
        em = createEntityManager();
        try {
            beginTransaction(em);
            
            emp = new Employee();
            emp.setFemale();
            emp.setFirstName("Case");
            emp.setLastName("Statement");
            em.persist(emp);

            TypedQuery<Dealer> query = em.createQuery("select d from Dealer d where d.firstName = :firstName and d.lastName = :lastName", Dealer.class);
            query.setParameter("firstName", dealer.getFirstName());
            query.setParameter("lastName", dealer.getLastName());
            List<Dealer> resultsList = query.getResultList();
            assertTrue("List returned should be non-empty", !resultsList.isEmpty());
            
            Dealer dealerFound = resultsList.get(0);
            emp.addDealer(dealerFound);
            
            commitTransaction(em);
            
            // verify valueholder configuration in shared cache
            Session parentSession = JpaHelper.getServerSession(em.getEntityManagerFactory());
            Dealer cachedDealer = (Dealer) parentSession.getIdentityMapAccessor().getFromIdentityMap(dealer);
            assertNotNull("Dealer with id should be in the cache: " + dealer.getId(), cachedDealer);
            
            ClassDescriptor descriptor = parentSession.getDescriptor(Dealer.class);
            DatabaseMapping mapping = descriptor.getMappingForAttributeName("customers");
            IndirectCollection<?, ?> indirectCollection = (IndirectCollection<?, ?>) mapping.getAttributeValueFromObject(cachedDealer);
            assertFalse("Collection VH should be uninstantiated", indirectCollection.isInstantiated());
            
            DatabaseValueHolder<?> dbValueHolder = (DatabaseValueHolder<?>) indirectCollection.getValueHolder();
            assertFalse("Referenced VH should be uninstantiated", dbValueHolder.isInstantiated());
            
            Session vhSession = dbValueHolder.getSession();
            assertSame("Dealer.customers VH session should reference the shared session", parentSession, vhSession);
        } finally {
            closeEntityManager(em);
        }
        
        // reset
        em = createEntityManager();
        try {
            beginTransaction(em);
            emp = em.find(Employee.class, emp.getId());
            if (emp != null) {
                em.remove(emp);
            }
            dealer = em.find(Dealer.class, dealer.getId());
            if (dealer != null) {
                em.remove(dealer);
            }
            commitTransaction(em);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Bug 489898 - RepeatableWriteUnitOfWork linked by QueryBasedValueHolder in shared cache in specific scenario
     *
     * Complex scenario: In a transaction, associate an existing object to a new object, refresh the existing object.
     * In a second transaction, read the new object and traverse relationships to the existing object, and trigger
     * an indirect relationship. The existing wrapped indirection query on the indirect relationship should
     * ensure that the UnitOfWork (RepeatableWriteUnitOfWork) used for the query is unreferenced correctly, to
     * avoid referencing it within the shared cache, via the existing referenced query.
     */
    public void testTransparentIndirectionQuerySessionReset() {
        Bill bill = null;
        BillLine billLine = null;
        BillLineItem billLineItem = null;
        BillAction billAction = null;

        // setup
        EntityManager em = createEntityManager();
        try {
            beginTransaction(em);

            bill = new Bill();
            bill.setOrderIdentifier("Test Bill");

            billLine = new BillLine();
            billLine.setQuantity(6);
            bill.addBillLine(billLine);

            billLineItem = new BillLineItem();
            billLineItem.setItemName("Test Widget");
            billLine.addBillLineItem(billLineItem);

            em.persist(bill);
            em.persist(billLine);
            em.persist(billLineItem);

            commitTransaction(em);

            assertNotNull("bill should be non-null", bill);
            assertNotNull("bill's id should be non-null", bill.getId());
            assertNotNull("billLine should be non-null", billLine);
            assertNotNull("billLine's id should be non-null", billLine.getId());
            assertNotNull("billLineItem should be non-null", billLineItem);
            assertNotNull("billLineItem's id should be non-null", billLineItem.getId());
        } finally {
            closeEntityManager(em);
            clearCache(); // start test with an empty cache
        }

        try {
            // test - txn #1 : read, modify, persist, refresh related Entity
            em = createEntityManager();
            try {
                beginTransaction(em);

                Bill billReRead = em.createQuery("SELECT b FROM Bill b where b.id=" + bill.getId(), Bill.class).getSingleResult();
                assertNotNull(billReRead);
                BillLine billLineReRead = billReRead.getBillLines().get(0);
                assertNotNull(billLineReRead);

                billAction = new BillAction();
                billAction.setBillLine(billLineReRead);
                billAction.setPriority(2);

                em.persist(billAction);

                em.refresh(billLineReRead); // refresh

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            // test - txn #2 : read, modify and trigger relationship on related Entity
            em = createEntityManager();
            try {
                beginTransaction(em);

                Bill billReRead = em.createQuery("SELECT b FROM Bill b where b.id=" + bill.getId(), Bill.class).getSingleResult();
                billReRead.setStatus(Bill.STATUS_PROCESSING); // DM: if there is no update to Order, issue doesn't occur

                BillAction billActionReRead = em.createQuery("SELECT a FROM BillAction a where a.id=" + billAction.getId(), BillAction.class).getSingleResult();
                assertNotNull(billActionReRead);

                BillLine billLineReRead = billActionReRead.getBillLine();
                assertNotNull(billLineReRead);

                billLineReRead.getBillLineItems().size(); // Access & trigger BillLine -> BillLineItems list

                commitTransaction(em);
            } finally {
                if (isTransactionActive(em)) {
                    rollbackTransaction(em);
                }
                closeEntityManager(em);
            }

            // verify
            // Failure case: non-null session (a UnitOfWork/RepeatableWriteUnitOfWork) referenced in the wrapped ValueHolder's query.
            ServerSession srv = getServerSession();
            ClassDescriptor descriptor = srv.getDescriptor(billLine);
            Long blId = billLine.getId();

            BillLine cachedBillLine = (BillLine)srv.getIdentityMapAccessor().getFromIdentityMap(blId, BillLine.class);
            assertNotNull("BillLine from shared cache is null with id: " + blId, cachedBillLine);

            OneToManyMapping mapping = (OneToManyMapping)srv.getDescriptor(cachedBillLine).getMappingForAttributeName("billLineItems");
            IndirectContainer<?> billLineItemsVH = (IndirectContainer<?>) mapping.getAttributeValueFromObject(cachedBillLine);
            assertNotNull("BillLineItems ValueHolder should not be null", billLineItemsVH);

            ValueHolderInterface<?> wrappedVH = billLineItemsVH.getValueHolder();
            assertNotNull("Wrapped ValueHolder should not be null", wrappedVH);

            if (wrappedVH instanceof QueryBasedValueHolder) {
                DatabaseQuery query = ((QueryBasedValueHolder<?>)wrappedVH).getQuery();
                if (query.getSession() != null && query.getSession().isUnitOfWork()) {
                    fail("UnitOfWork referenced in Query from wrapped QueryBasedValueHolder in shared cache");
                }
            }
        } finally {
            // reset
            em = createEntityManager();
            try {
                beginTransaction(em);
                bill = em.find(Bill.class, bill.getId());
                if (bill != null) {
                    em.remove(bill);
                }
                billLine = em.find(BillLine.class, billLine.getId());
                if (billLine != null) {
                    em.remove(billLine);
                }
                billLineItem = em.find(BillLineItem.class, billLineItem.getId());
                if (billLineItem != null) {
                    em.remove(billLineItem);
                }
                if (billAction != null) {
                    billAction = em.find(BillAction.class, billAction.getId());
                    if (billAction != null) {
                        em.remove(billAction);
                    }
                }
                commitTransaction(em);
            } finally {
                closeEntityManager(em);
            }
        }
    }

    /**
     * Bug 412056
     * Test batch fetch with size smaller than results in reverse order
     */
    public void testEmployeeToProjectWithBatchFetchTypeInReverseIteration() {
        final String lastName = "testEmployeeToProject";

        // Set up
        Set<Employee> employeesToRemove = new HashSet<>();
        EntityManager em = createEntityManager();
        for (int i = 0; i < 100; i++) {
            beginTransaction(em);
            Employee employee = new Employee();
            employee.setLastName(lastName);
            employeesToRemove.add(employee);
            em.persist(employee);
            for (int j = 0; j < 20; j++) {
                Project project = new Project();
                employee.addProject(project);
                em.persist(project);
            }
            commitTransaction(em);
        }

        JpaEntityManager jpaEntityManager = (JpaEntityManager) em.getDelegate();
        jpaEntityManager.getUnitOfWork().getIdentityMapAccessor().initializeAllIdentityMaps();
        try {
            Expression exp = new ExpressionBuilder(Employee.class);
            @SuppressWarnings({"unchecked"})
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) jpaEntityManager.createQuery(exp, Employee.class);
            ((ReadAllQuery) query.getDatabaseQuery()).addBatchReadAttribute("projects");
            ((ReadAllQuery) query.getDatabaseQuery()).setBatchFetchType(BatchFetchType.IN);
            ((ReadAllQuery) query.getDatabaseQuery()).setBatchFetchSize(10);
            @SuppressWarnings({"unchecked"})
            List<Employee> employees = query.getResultList();

            // Trigger the bug
            Collections.reverse(employees);

            int count = 0;
            try {
                for (Employee employee : employees) {
                    if(lastName.equals(employee.getLastName())) {
                        for (Project project : employee.getProjects()) {
                            count++;
                        }
                    }
                }
                Assert.assertEquals("Project objects received are not as many as expected", 2000, count);
            } catch (ArrayIndexOutOfBoundsException x) {
                Assert.fail(Helper.printStackTraceToString(x));
            }
        } finally {
            // Clean up
            beginTransaction(em);
            for (Employee employee : employeesToRemove) {
                employee = em.merge(employee);
                for (Project project : employee.getProjects()) {
                    em.remove(em.merge(project));
                }
                em.remove(employee);
            }
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 412056
     * Test batch fetch with size smaller than results with custom iteration
     */
    public void testEmployeeToProjectWithBatchFetchTypeInCustomIteration() {
        final String lastName = "testEmployeeToProject";

        // Set up
        Set<Employee> employeesToRemove = new HashSet<>();
        EntityManager em = createEntityManager();
        for (int i = 0; i < 100; i++) {
            beginTransaction(em);
            Employee employee = new Employee();
            employee.setLastName(lastName);
            employeesToRemove.add(employee);
            em.persist(employee);
            for (int j = 0; j < 20; j++) {
                Project project = new Project();
                employee.addProject(project);
                em.persist(project);
            }
            commitTransaction(em);
        }

        JpaEntityManager jpaEntityManager = (JpaEntityManager) em.getDelegate();
        jpaEntityManager.getUnitOfWork().getIdentityMapAccessor().initializeAllIdentityMaps();
        try {
            Expression exp = new ExpressionBuilder(Employee.class);
            @SuppressWarnings({"unchecked"})
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) jpaEntityManager.createQuery(exp, Employee.class);
            ((ReadAllQuery) query.getDatabaseQuery()).addBatchReadAttribute("projects");
            ((ReadAllQuery) query.getDatabaseQuery()).setBatchFetchType(BatchFetchType.IN);
            ((ReadAllQuery) query.getDatabaseQuery()).setBatchFetchSize(10);
            @SuppressWarnings({"unchecked"})
            List<Employee> employees = query.getResultList();

            // Trigger the bug
            employees.add(employees.remove(0));

            int count = 0;
            try {
                for (Employee employee : employees) {
                    if(lastName.equals(employee.getLastName())) {
                        for (Project project : employee.getProjects()) {
                            count++;
                        }
                    }
                }
                Assert.assertEquals("Project objects received are not as many as expected", 2000, count);
            } catch (ArrayIndexOutOfBoundsException x) {
                Assert.fail(Helper.printStackTraceToString(x));
            }
        } finally {
            // Clean up
            beginTransaction(em);
            for (Employee employee : employeesToRemove) {
                employee = em.merge(employee);
                for (Project project : employee.getProjects()) {
                    em.remove(em.merge(project));
                }
                em.remove(employee);
            }
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 412056
     * Test batch fetch with size one less than results in random order
     */
    public void testEmployeeToProjectWithBatchFetchTypeInRandomIteration() {
        final String lastName = "testRandomEmployeeToProject";

        // Set up
        Set<Employee> employeesToRemove = new HashSet<>();
        EntityManager em = createEntityManager();
        for (int i = 0; i < 100; i++) {
            beginTransaction(em);
            Employee employee = new Employee();
            employee.setLastName(lastName);
            employeesToRemove.add(employee);
            em.persist(employee);
            for (int j = 0; j < 20; j++) {
                Project project = new Project();
                employee.addProject(project);
                em.persist(project);
            }
            commitTransaction(em);
        }

        JpaEntityManager jpaEntityManager = (JpaEntityManager) em.getDelegate();
        jpaEntityManager.getUnitOfWork().getIdentityMapAccessor().initializeAllIdentityMaps();
        try {
            Expression exp = new ExpressionBuilder(Employee.class).get("lastName").equal(lastName);
            @SuppressWarnings({"unchecked"})
            EJBQueryImpl<Employee> query = (EJBQueryImpl<Employee>) jpaEntityManager.createQuery(exp, Employee.class);
            ((ReadAllQuery) query.getDatabaseQuery()).addBatchReadAttribute("projects");
            ((ReadAllQuery) query.getDatabaseQuery()).setBatchFetchType(BatchFetchType.IN);
            ((ReadAllQuery) query.getDatabaseQuery()).setBatchFetchSize(99);
            @SuppressWarnings({"unchecked"})
            List<Employee> employees = query.getResultList();

            // Trigger the bug
            Collections.shuffle(employees);

            int count = 0;
            try {
                for (Employee employee : employees) {
                    for (Project project : employee.getProjects()) {
                        count++;
                    }
                }
                Assert.assertEquals("Project objects received are not as many as expected", 2000, count);
            } catch (ArrayIndexOutOfBoundsException x) {
                Assert.fail(Helper.printStackTraceToString(x));
            }
        } finally {
            // Clean up
            beginTransaction(em);
            for (Employee employee : employeesToRemove) {
                employee = em.merge(employee);
                for (Project project : employee.getProjects()) {
                    em.remove(em.merge(project));
                }
                em.remove(employee);
            }
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 1148 (GitHub)
     * Test batch fetch with small fetch size
     */
    public void testEmployeeToProjectWithBatchFetchTypeWithSmallFetchSize() {
        final String lastName = "testRandomEmployeeToProject";

        // Set up
        Set<Employee> employeesToRemove = new HashSet<>();
        Set<Integer> employeeIds = new HashSet<>();
        EntityManager em = createEntityManager();
        for (int i = 0; i < 100; i++) {
            beginTransaction(em);
            Employee employee = new Employee();
            employee.setLastName(lastName);
            employeesToRemove.add(employee);
            em.persist(employee);
            employeeIds.add(employee.getId());
            for (int j = 0; j < 20; j++) {
                Project project = new Project();
                employee.addProject(project);
                em.persist(project);
            }
            commitTransaction(em);
        }

        JpaEntityManager jpaEntityManager = (JpaEntityManager) em.getDelegate();
        jpaEntityManager.getUnitOfWork().getIdentityMapAccessor().initializeAllIdentityMaps();
        try {
            String jpql = "SELECT employee FROM Employee employee WHERE employee.id IN :ids";
            TypedQuery<Employee> query = em.createQuery(jpql, Employee.class)
                    .setParameter("ids", employeeIds)
                    .setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN)
                    .setHint(QueryHints.BATCH, "employee.projects.properties")
                    .setHint(QueryHints.BATCH_SIZE, 3);
            List<Employee> employees = query.getResultList();

            // Trigger the bug
            Collections.shuffle(employees);

            int count = 0;
            try {
                for (Employee employee : employees) {
                    for (Project project : employee.getProjects()) {
                        count++;
                    }
                }
                Assert.assertEquals("Project objects received are not as many as expected", 2000, count);
            } catch (ArrayIndexOutOfBoundsException x) {
                Assert.fail(Helper.printStackTraceToString(x));
            }
        } finally {
            // Clean up
            beginTransaction(em);
            for (Employee employee : employeesToRemove) {
                employee = em.merge(employee);
                for (Project project : employee.getProjects()) {
                    em.remove(em.merge(project));
                }
                em.remove(employee);
            }
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 502085 - @OneToMany with @OrderColumn contains a null element after specific scenario
     * 
     */
    public void testNullValueInCollectionWithOrderColumn() {
        // Create test data
        EntityManager em = createEntityManager();
        beginTransaction(em);
        
        OrderedEntityA newEntityA1 = new OrderedEntityA(1L, "Entity-A1");
        OrderedEntityA newEntityA2 = null; // save for later
        OrderedEntityZ newEntityZ1 = new OrderedEntityZ(1L, "Entity-Z1");
        OrderedEntityZ newEntityZ2 = new OrderedEntityZ(2L, "Entity-Z2");
        newEntityZ1.addEntityA(newEntityA1);
        
        em.persist(newEntityA1);
        em.persist(newEntityZ1);
        em.persist(newEntityZ2);
        
        commitTransaction(em);
        closeEntityManager(em);
        
        try {
            // Test
            em = createEntityManager();
            beginTransaction(em);
            
            // load Entity Z2 for unrelated modification within same EM
            OrderedEntityZ entityZ2 = em.createQuery("SELECT z FROM OrderedEntityZ z WHERE z.id = 2", OrderedEntityZ.class).getSingleResult();
            entityZ2.setDescription("Entity-Z2-MODIFIED"); // make a change
            em.persist(entityZ2); // important to persist modification first
            
            // load Entity Z1 for modification
            OrderedEntityZ entityZ1 = em.createQuery("SELECT z FROM OrderedEntityZ z WHERE z.id = 1", OrderedEntityZ.class).getSingleResult();
            
            // Remove A1 from collection, add A2 to collection, persist
            entityZ1.removeEntityA(entityZ1.getEntityAs().get(0));
            newEntityA2 = new OrderedEntityA(2L, "Entity-A2");
            entityZ1.addEntityA(newEntityA2);
            
            em.persist(newEntityA2);
            
            commitTransaction(em);
            closeEntityManager(em);
            
            // Verify
            em = createEntityManager();
            
            entityZ1 = em.createQuery("SELECT z FROM OrderedEntityZ z WHERE z.id = 1", OrderedEntityZ.class).getSingleResult();
            List<OrderedEntityA> entityAList = entityZ1.getEntityAs();
            
            // validate that Entity Z1's collection is size 1 and contains a valid Entity A2 with correct description
            assertEquals("invalid collection size", 1, entityAList.size());
            OrderedEntityA firstEntityA = entityAList.get(0);
            assertNotNull("null value in collection", firstEntityA);
            assertEquals("wrong description in entity", newEntityA2.getDescription(), firstEntityA.getDescription());
            
            closeEntityManager(em);
        } finally {
            if (em != null && em.isOpen()) {
                closeEntityManager(em);
            }
            // clean up
            em = createEntityManager();
            beginTransaction(em);
            
            newEntityA1 = em.find(OrderedEntityA.class, newEntityA1.getId());
            if (newEntityA1 != null) {
                em.remove(newEntityA1);
            }
            if (newEntityA2 != null) {
                newEntityA2 = em.find(OrderedEntityA.class, newEntityA2.getId());
                if (newEntityA2 != null) {
                    em.remove(newEntityA2);
                }
            }
            newEntityZ1 = em.find(OrderedEntityZ.class, newEntityZ1.getId());
            if (newEntityZ1 != null) {
                em.remove(newEntityZ1);
            }
            newEntityZ2 = em.find(OrderedEntityZ.class, newEntityZ2.getId());
            if (newEntityZ2 != null) {
                em.remove(newEntityZ2);
            }
            
            commitTransaction(em);
            closeEntityManager(em);
        }
    }

    /**
     * Bug 453865
     * Test joining across an @ElementCollection (List collection implementation) with an ORDER BY clause.
     */
    public void testJoinWithOrderByOnElementCollectionList() {
        EntityManager em = createEntityManager();
        try {
            String jpql = "SELECT resp FROM Employee e JOIN e.responsibilities AS resp " +
                    "WHERE e.lastName = 'Smith' ORDER BY resp";
            TypedQuery<String> query = em.createQuery(jpql, String.class);
            List<String> results = query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * Bug 453865
     * Test joining across an @ElementCollection (Map collection implementation) with an ORDER BY clause.
     */
    public void testJoinWithOrderByOnElementCollectionMap() {
        EntityManager em = createEntityManager();
        try {
            String jpql = "SELECT value(mapEntry) FROM Buyer b JOIN b.creditCards mapEntry " +
                    "WHERE key(mapEntry) = 'VI' ORDER BY value(mapEntry)";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            List<Long> results = query.getResultList();
        } finally {
            closeEntityManager(em);
        }
    }
    
    protected int getVersion(EntityManager em, Dealer dealer) {
        Vector<Integer> pk = new Vector<>(1);
        pk.add(dealer.getId());

        return getServerSession().getDescriptor(Dealer.class).getOptimisticLockingPolicy().getWriteLockValue(dealer, pk, getServerSession());
    }

    protected List<Employee> createEmployeesWithUnidirectionalMappings(String lastName) {
        int n = 2;
        List<Employee> employees = new ArrayList<>(n);
        for(int i=0; i<n; i++) {
            Employee emp = new Employee();
            emp.setFirstName(Integer.toString(i+1));
            emp.setLastName(lastName);
            employees.add(emp);
            for(int j=0; j<n; j++) {
                Dealer dealer = new Dealer();
                dealer.setFirstName(emp.getFirstName() + "_" + (j + 1));
                dealer.setLastName(lastName);
                emp.addDealer(dealer);
                for(int k=0; k<n; k++) {
                    Customer customer = new Customer();
                    customer.setFirstName(dealer.getFirstName() + "_" + (k + 1));
                    customer.setLastName(lastName);
                    dealer.addCustomer(customer);
                }
            }
        }
        return employees;
    }

    protected List<Employee> persistEmployeesWithUnidirectionalMappings(String lastName) {
        EntityManager em = createEntityManager();
        try {
            return persistEmployeesWithUnidirectionalMappings(lastName, em);
        } finally {
            closeEntityManager(em);
        }
    }

    protected List<Employee> persistEmployeesWithUnidirectionalMappings(String lastName, EntityManager em) {
        List<Employee> employees = createEmployeesWithUnidirectionalMappings(lastName);
        beginTransaction(em);
        try {
            for (Employee employee : employees) {
                em.persist(employee);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
        }
        return employees;
    }

    protected void deleteEmployeesWithUnidirectionalMappings(String lastName) {
        EntityManager em = createEntityManager();
        beginTransaction(em);
        try {
            em.createQuery("DELETE FROM AdvancedCustomer c WHERE c.lastName = '"+lastName+"'").executeUpdate();
            em.createQuery("DELETE FROM Dealer d WHERE d.lastName = '"+lastName+"'").executeUpdate();
            Query q = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = '"+lastName+"'");
            for (Object oldData : q.getResultList()) {
                em.remove(oldData);
            }
            commitTransaction(em);
        } finally {
            if(this.isTransactionActive(em)) {
                rollbackTransaction(em);
            }
            closeEntityManager(em);
            clearCache();
        }
    }
}
