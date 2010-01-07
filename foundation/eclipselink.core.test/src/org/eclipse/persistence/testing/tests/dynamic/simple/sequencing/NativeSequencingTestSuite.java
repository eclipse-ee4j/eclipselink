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
 *     dclarke - Dynamic Persistence INCUBATION - Enhancement 200045
 *               http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               et database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.simple.sequencing;

//JUnit4 imports
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.NativeSequence;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

public class NativeSequencingTestSuite extends BaseSequencingTestSuite {

    @BeforeClass
    public static void setUp() {
        session = createSession();
        dynamicHelper = new DynamicHelper(session);
        DynamicClassLoader dcl = dynamicHelper.getDynamicClassLoader(); 
        Class<?> dynamicType = dcl.createDynamicClass("simple.sequencing." + ENTITY_TYPE);
        DynamicTypeBuilder typeBuilder = new DynamicTypeBuilder(dynamicType, null, TABLE_NAME);
        typeBuilder.setPrimaryKeyFields("SID");
        typeBuilder.addDirectMapping("id", int.class, "SID");
        typeBuilder.addDirectMapping("value1", String.class, "VAL_1");
        // configureSequencing
        NativeSequence sequence = new NativeSequence();
        sequence.setPreallocationSize(1);
        ((AbstractSession)session).getProject().getLogin().setDefaultSequence(sequence);
        sequence.onConnect(session.getPlatform());
        typeBuilder.configureSequencing(sequence, ENTITY_TYPE + "_SEQ", "SID");
        
        dynamicHelper.addTypes(true, true, typeBuilder.getType());
    }

    @AfterClass
    public static void tearDown() {
        session.executeNonSelectingSQL("DROP TABLE " + TABLE_NAME);
        session.logout();
        session = null;
        dynamicHelper = null;
    }

    @After
    public void clearSimpleTypeInstances() throws Exception {
        session.executeNonSelectingSQL("DELETE FROM " + TABLE_NAME);
        session.executeNonSelectingSQL("ALTER TABLE " + TABLE_NAME + " DROP COLUMN SID");
        session.executeNonSelectingSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN SID INTEGER PRIMARY KEY AUTO_INCREMENT FIRST");
        session.getSequencingControl().resetSequencing();
        session.getSequencingControl().initializePreallocated();
    }

}