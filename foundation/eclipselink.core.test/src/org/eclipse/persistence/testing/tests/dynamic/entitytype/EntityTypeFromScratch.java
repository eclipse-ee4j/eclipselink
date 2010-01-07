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
 *     			 http://wiki.eclipse.org/EclipseLink/Development/Dynamic
 *     mnorman - tweaks to work from Ant command-line,
 *               et database properties from System, etc.
 *     
 * This code is being developed under INCUBATION and is not currently included 
 * in the automated EclipseLink build. The API in this code may change, or 
 * may never be included in the product. Please provide feedback through mailing 
 * lists or the bug database.
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.dynamic.entitytype;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;

//EclipseLink imports
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

/**
 * 
 * @author dclarke
 * @since EclipseLink 1.2
 */
public class EntityTypeFromScratch {

    static final String TABLE_NAME = "MY_ENTITY";
    
    @AfterClass
    public static void tearDown() {
        DatabaseSession ds = createSession();
        ds.login();
        ds.executeNonSelectingSQL("DROP TABLE " + TABLE_NAME);
        ds.logout();
    }
    
    @Test
    public void entityTypeFromDescriptor() throws Exception {
        DynamicTypeImpl entityType = buildMyEntityType();
    
        assertEquals(MyEntity.class, entityType.getJavaClass());
    
        DatabaseSession session = createSession();
        session.login();
        session.addDescriptor(entityType.getDescriptor());
        new SchemaManager(session).replaceDefaultTables();
    
        DynamicEntity entity = entityType.newDynamicEntity();
        entity.set("id", 1);
        entity.set("name", "Name");
    
        session.insertObject(entity);
    
        session.logout();
    }

    private DynamicTypeImpl buildMyEntityType() {
        DynamicTypeBuilder factory = new DynamicTypeBuilder(MyEntity.class, null, "MY_ENTITY");
        factory.setPrimaryKeyFields("ID");
        factory.addDirectMapping("id", int.class, "ID");
        factory.addDirectMapping("name", String.class, "NAME");
    
        return (DynamicTypeImpl) factory.getType();
    }

}
