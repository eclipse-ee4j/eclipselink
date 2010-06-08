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
package org.eclipse.persistence.testing.tests.dynamic.projectxml;

//javase imports
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createLogin;
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.logLevel;

/*
 * Test cases verifying the use of simple-map-project.xml 
 */
public class SimpleMapProject {

    public static final String PACKAGE_PREFIX = 
        SimpleMapProject.class.getPackage().getName();
    static final String PROJECT_XML = 
        PACKAGE_PREFIX.replace('.', '/') + "/simple-map-project.xml";
    
    //test fixtures
    static DatabaseSession session = null;
    static DynamicHelper dynamicHelper = null;

    @BeforeClass
    public static void setUp() {
        DynamicClassLoader dcl = new DynamicClassLoader(SimpleMapProject.class.getClassLoader());
        Project project = null;
        try {
            project = DynamicTypeBuilder.loadDynamicProject(PROJECT_XML, createLogin(), dcl);
        }
        catch (IOException e) {
            //e.printStackTrace();
            fail("cannot find simple-map-project.xml");
        }
        session = project.createDatabaseSession();
        if (SessionLog.OFF == logLevel) {
            session.dontLogMessages();
        }
        else {
            session.setLogLevel(logLevel); 
        }
        dynamicHelper = new DynamicHelper(session);
        session.login();
        new DynamicSchemaManager(session).createTables(new DynamicType[0]);
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE SIMPLETABLE");
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearTable() {
        session.executeNonSelectingSQL("DELETE FROM SIMPLETABLE");
    }

    @Test
    public void verifyDescriptor() throws Exception {
        ClassDescriptor descriptor = session.getClassDescriptorForAlias("simpletableType");

        assertNotNull(descriptor);
        assertEquals("simpletable.Simpletable", descriptor.getJavaClassName());

        assertEquals(3, descriptor.getMappings().size());

        DatabaseMapping idMapping = descriptor.getMappingForAttributeName("id");
        assertNotNull(idMapping);
        assertTrue(idMapping.isDirectToFieldMapping());
        assertEquals(BigInteger.class, idMapping.getAttributeClassification());

        DatabaseMapping nameMapping = descriptor.getMappingForAttributeName("name");
        assertNotNull(nameMapping);
        assertTrue(nameMapping.isDirectToFieldMapping());
        assertEquals(String.class, nameMapping.getAttributeClassification());

        DatabaseMapping sinceMapping = descriptor.getMappingForAttributeName("since");
        assertNotNull(sinceMapping);
        assertTrue(sinceMapping.isDirectToFieldMapping());
        assertEquals(Date.class, sinceMapping.getAttributeClassification());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void createInstance() {
        DynamicType type = dynamicHelper.getType("simpletableType");

        ReportQuery countQuery = dynamicHelper.newReportQuery("simpletableType", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(0, ((Number)session.executeQuery(countQuery)).intValue());

        DynamicEntity entity = type.newDynamicEntity();
        entity.set("id", new BigInteger("1"));
        entity.set("name", "Example");
        entity.set("since", new Date(100, 06, 06));

        UnitOfWork uow = session.acquireUnitOfWork();
        uow.registerNewObject(entity);
        uow.commit();

        assertEquals(1, ((Number)session.executeQuery(countQuery)).intValue());
        session.release();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void readAll() {
        createInstance();
        DynamicType type = dynamicHelper.getType("simpletableType");

        List<DynamicEntity> allObjects = session.readAllObjects(type.getJavaClass());
        assertEquals(1, allObjects.size());
    }

    @Test
    public void readById() {
        createInstance();

        ReadObjectQuery query = dynamicHelper.newReadObjectQuery( "simpletableType");
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(1));

        DynamicEntity entity = (DynamicEntity)session.executeQuery(query);

        assertNotNull(entity);
    }

    @Test
    public void delete() {
        createInstance();

        ReadObjectQuery query = dynamicHelper.newReadObjectQuery( "simpletableType");
        query.setSelectionCriteria(query.getExpressionBuilder().get("id").equal(1));

        UnitOfWork uow = session.acquireUnitOfWork();

        DynamicEntity entity = (DynamicEntity)uow.executeQuery(query);
        assertNotNull(entity);

        uow.deleteObject(entity);
        uow.commit();

        ReportQuery countQuery = dynamicHelper.newReportQuery("simpletableType", new ExpressionBuilder());
        countQuery.addCount();
        countQuery.setShouldReturnSingleValue(true);
        assertEquals(0, ((Number)session.executeQuery(countQuery)).intValue());
    }

}