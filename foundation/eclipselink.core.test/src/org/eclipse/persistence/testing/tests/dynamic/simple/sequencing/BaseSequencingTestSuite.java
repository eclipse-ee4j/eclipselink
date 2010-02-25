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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.simple.sequencing;

//JUnit4 imports
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

//domain-specific (testing) imports

public abstract class BaseSequencingTestSuite  {

    public static final String TABLE_NAME = "SIMPLE_TABLE_SEQ";
    public static final String SEQ_TABLE_NAME = "TEST_SEQ";
    public static final String ENTITY_TYPE = "Simple";
    
    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;

    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptor = dynamicHelper.getSession().getClassDescriptorForAlias(ENTITY_TYPE);
        assertNotNull("No descriptor found for alias: " + ENTITY_TYPE, descriptor);

        DynamicType simpleType = dynamicHelper.getType(ENTITY_TYPE);
        assertNotNull("EntityType not found for alias: " + ENTITY_TYPE, simpleType);

        assertEquals(descriptor, simpleType.getDescriptor());

        assertTrue("Descriptor does not use sequencing", descriptor.usesSequenceNumbers());
        verifySequencingConfig(dynamicHelper.getSession(), descriptor);
    }

    protected void verifySequencingConfig(Session session, ClassDescriptor descriptor) {
    }
    
    @Test
    public void createSingleInstances() throws Exception {
        DynamicEntity simpleInstance = createSimpleInstance(dynamicHelper, session, 1);

        ReportQuery countQuery = dynamicHelper.newReportQuery(ENTITY_TYPE, new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);

        assertEquals(1, count(dynamicHelper, session));

        IdentityMapAccessor cache = session.getIdentityMapAccessor();
        assertTrue(cache.containsObjectInIdentityMap(simpleInstance));
        cache.initializeAllIdentityMaps();

        DynamicEntity findResult = find(dynamicHelper, session, 1);

        assertNotNull(findResult);
        assertEquals(simpleInstance.get("id"), findResult.get("id"));
        assertEquals(simpleInstance.get("value1"), findResult.get("value1"));

        session.release();
    }

    @Test
    public void createTwoInstances() throws DatabaseException, Exception {
        DynamicEntity simpleInstance1 = createSimpleInstance(dynamicHelper, session, 1);
        DynamicEntity simpleInstance2 = createSimpleInstance(dynamicHelper, session, 2);

        ReportQuery countQuery = dynamicHelper.newReportQuery(ENTITY_TYPE, new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);

        assertEquals(2, count(dynamicHelper, session));

        IdentityMapAccessor cache = session.getIdentityMapAccessor();
        assertTrue(cache.containsObjectInIdentityMap(simpleInstance1));
        assertTrue(cache.containsObjectInIdentityMap(simpleInstance2));

        cache.initializeAllIdentityMaps();

        DynamicEntity findResult1 = find(dynamicHelper, session, 1);
        DynamicEntity findResult2 = find(dynamicHelper, session, 2);

        assertNotNull(findResult1);
        assertNotNull(findResult2);
        assertEquals(simpleInstance1.get("id"), findResult1.get("id"));
        assertEquals(simpleInstance2.get("value1"), findResult2.get("value1"));

        session.release();
    }

    protected DynamicEntity find(DynamicHelper helper, Session session, int id) {
        ReadObjectQuery findQuery = helper.newReadObjectQuery(ENTITY_TYPE);
        findQuery.setSelectionCriteria(findQuery.getExpressionBuilder().get("id").equal(id));
        return (DynamicEntity) session.executeQuery(findQuery);
    }

    protected int count(DynamicHelper helper, Session session) {
        ReportQuery countQuery = helper.newReportQuery(ENTITY_TYPE, new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);

        return ((Number) session.executeQuery(countQuery)).intValue();
    }

    protected DynamicEntity createSimpleInstance(DynamicHelper helper, Session session, int expectedId) {
        DynamicType simpleEntityType = helper.getType(ENTITY_TYPE);
        assertNotNull(simpleEntityType);

        DynamicEntity simpleInstance = simpleEntityType.newDynamicEntity();
        simpleInstance.set("value1", TABLE_NAME);

        UnitOfWork uow = session.acquireUnitOfWork();

        assertEquals(0, simpleInstance.get("id"));
        uow.registerNewObject(simpleInstance);
        assertEquals(0, simpleInstance.get("id"));

        // uow.assignSequenceNumber(simpleInstance);

        uow.commit();
        assertEquals(expectedId, simpleInstance.get("id"));

        return simpleInstance;
    }

}