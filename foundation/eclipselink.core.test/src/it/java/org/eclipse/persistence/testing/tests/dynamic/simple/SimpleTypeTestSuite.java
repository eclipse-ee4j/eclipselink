/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
package org.eclipse.persistence.testing.tests.dynamic.simple;

//javase imports

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.exceptions.DynamicException;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.IdentityMapAccessor;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;

import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleTypeTestSuite {

    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;

    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader();
        Class<?> javaType = dcl.createDynamicClass("simple.Simple");
        DynamicTypeBuilder typeBuilder = new DynamicTypeBuilder(javaType, null, "SIMPLE_TYPE");
        typeBuilder.setPrimaryKeyFields("SID");
        typeBuilder.addDirectMapping("id", int.class, "SID");
        typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        typeBuilder.addDirectMapping("value2", boolean.class, "VAL_2");
        typeBuilder.addDirectMapping("value3", Calendar.class, "VAL_3");
        typeBuilder.addDirectMapping("value4", Character.class, "VAL_4");
        dynamicHelper.addTypes(true, false, typeBuilder.getType());
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLE_TYPE");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @Test(expected=DynamicException.class)
    public void invalidDirectMappingSet_id() throws Exception {
        DynamicType type = dynamicHelper.getType("Simple");
        DynamicEntity entity = type.newDynamicEntity();

        entity.set("id", 1l);
    }

    @Test
    public void verifyConfig() throws Exception {
        ClassDescriptor descriptor = session.getClassDescriptorForAlias("Simple");
        assertNotNull("No descriptor found for alias='Simple'", descriptor);

        DynamicTypeImpl simpleType = (DynamicTypeImpl)dynamicHelper.getType("Simple");
        assertNotNull("'Simple' EntityType not found", simpleType);

        assertEquals(1 + descriptor.getPrimaryKeyFields().size(), simpleType.getMappingsRequiringInitialization().size());

        assertEquals(descriptor, simpleType.getDescriptor());
    }

    @Test
    public void find() {
        createSimpleInstance(session, 1);

        DynamicEntity simpleInstance = find(dynamicHelper, session, 1);
        assertNotNull("Could not find simple instance with id = 1", simpleInstance);

        simpleInstance = find(dynamicHelper, session, 1);
        assertNotNull("Could not find simple instance with id = Integer(1)", simpleInstance);
    }

    @Test
    public void simpleInstance_CRUD() {
        IdentityMapAccessor cache = session.getIdentityMapAccessor();

        DynamicEntity simpleInstance = createSimpleInstance(session, 1);
        assertNotNull(simpleInstance);

        assertTrue(cache.containsObjectInIdentityMap(simpleInstance));
        cache.initializeAllIdentityMaps();
        assertFalse(cache.containsObjectInIdentityMap(simpleInstance));

    }

    @Test
    public void verifyDefaultValuesFromEntityType() throws Exception {
        DynamicType type = dynamicHelper.getType("Simple");

        assertNotNull(type);

        DynamicEntity simpleInstance = type.newDynamicEntity();
        assertDefaultValues(simpleInstance);
    }

    @Test
    public void verifyDefaultValuesFromDescriptor() throws Exception {
        DynamicType simpleType = dynamicHelper.getType("Simple");
        assertNotNull(simpleType);

        DynamicEntity simpleInstance = (DynamicEntity) simpleType.getDescriptor().getObjectBuilder().buildNewInstance();
        assertDefaultValues(simpleInstance);
    }

    protected void assertDefaultValues(DynamicEntity simpleInstance) {
        assertNotNull(simpleInstance);

        assertEquals("id not default value",
            0, simpleInstance.<Integer>get("id").intValue());
        assertFalse("value1 set on new instance", simpleInstance.isSet("value1"));
        assertEquals("value2 not default value on new instance",
            false, simpleInstance.<Boolean>get("value2"));
        assertFalse("value3 set on new instance", simpleInstance.isSet("value3"));
        assertFalse("value4 set on new instance", simpleInstance.isSet("value4"));
    }

    public DynamicEntity createSimpleInstance(Session session, int id) {
        DynamicType simpleEntityType = dynamicHelper.getType("Simple");
        Assert.assertNotNull(simpleEntityType);

        DynamicEntity simpleInstance = simpleEntityType.newDynamicEntity();
        simpleInstance.set("id", id);
        simpleInstance.set("value2", true);

        ReportQuery countQuery = dynamicHelper.newReportQuery("Simple", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(0, ((Number) session.executeQuery(countQuery)).intValue());

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(simpleInstance);
        uow.commit();

        assertEquals(1, ((Number) session.executeQuery(countQuery)).intValue());

        DynamicEntity foundEntity = find(dynamicHelper, session, 1);

        assertNotNull(foundEntity);
        assertEquals(simpleInstance.<Integer>get("id"), foundEntity.<Integer>get("id"));
        assertEquals(simpleInstance.<String>get("value1"), foundEntity.<String>get("value1"));
        assertEquals(simpleInstance.<Boolean>get("value2"), foundEntity.<Boolean>get("value2"));

        session.release();

        return simpleInstance;
    }

    protected DynamicEntity find(DynamicHelper helper, Session session, Object id) {
        ReadObjectQuery findQuery = helper.newReadObjectQuery("Simple");
        findQuery.setSelectionCriteria(findQuery.getExpressionBuilder().get("id").equal(id));
        return (DynamicEntity) session.executeQuery(findQuery);
    }

    @Before
    @After
    public void clearSimpleTypeInstances() {
        session.executeNonSelectingSQL("DELETE FROM SIMPLE_TYPE");
        session.getIdentityMapAccessor().initializeAllIdentityMaps();
    }
}
