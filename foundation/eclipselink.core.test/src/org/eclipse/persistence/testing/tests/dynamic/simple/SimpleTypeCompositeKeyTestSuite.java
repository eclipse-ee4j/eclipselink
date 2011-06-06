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
 *     dclarke - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     mnorman - tweaks to work from Ant command-line,
 *               get database properties from System, etc.
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.simple;

//javase imports
import java.util.Calendar;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

public class SimpleTypeCompositeKeyTestSuite extends SimpleTypeTestSuite {

    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader(); 
        Class<?> javaType = dcl.createDynamicClass("simple.Simple");
        DynamicTypeBuilder typeBuilder = new DynamicTypeBuilder(javaType, null, "SIMPLE_TYPE");
        typeBuilder.setPrimaryKeyFields("SID1", "SID2");
        typeBuilder.addDirectMapping("id1", int.class, "SID1");
        typeBuilder.addDirectMapping("id2", int.class, "SID2");
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
    
    @Override
    protected void assertDefaultValues(DynamicEntity simpleInstance) {
        assertNotNull(simpleInstance);

        assertEquals("id1 not default value", 
            0, simpleInstance.<Integer>get("id1").intValue());
        assertEquals("id2 not default value", 
            0, simpleInstance.<Integer>get("id2").intValue());
        assertFalse("value1 set on new instance", simpleInstance.isSet("value1"));
        assertEquals("value2 not default value on new instance",
            false, simpleInstance.<Boolean>get("value2").booleanValue());
        assertFalse("value3 set on new instance", simpleInstance.isSet("value3"));
        assertFalse("value4 set on new instance", simpleInstance.isSet("value4"));
    }

    @Override
    public DynamicEntity createSimpleInstance(Session session, int id) {
        DynamicType simpleEntityType = dynamicHelper.getType("Simple");
        Assert.assertNotNull(simpleEntityType);

        DynamicEntity simpleInstance = simpleEntityType.newDynamicEntity();
        simpleInstance.set("id1", id);
        simpleInstance.set("id2", id);
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
        assertEquals(simpleInstance.get("id1"), foundEntity.get("id1"));
        assertEquals(simpleInstance.get("id2"), foundEntity.get("id2"));
        assertEquals(simpleInstance.get("value1"), foundEntity.get("value1"));
        assertEquals(simpleInstance.get("value2"), foundEntity.get("value2"));

        return simpleInstance;
    }

    @Override
    protected DynamicEntity find(DynamicHelper helper, Session session, Object id) {
        ReadObjectQuery findQuery = helper.newReadObjectQuery( "Simple");
        ExpressionBuilder eb = findQuery.getExpressionBuilder();
        findQuery.setSelectionCriteria(eb.get("id1").equal(id).and(eb.get("id2").equal(id)));
        return (DynamicEntity) session.executeQuery(findQuery);
    }

}