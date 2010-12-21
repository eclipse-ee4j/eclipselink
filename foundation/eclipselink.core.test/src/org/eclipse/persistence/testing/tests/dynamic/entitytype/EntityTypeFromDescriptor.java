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
package org.eclipse.persistence.testing.tests.dynamic.entitytype;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicType;
import org.eclipse.persistence.dynamic.DynamicTypeBuilder;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

//domain-specific (testing) imports
import static org.eclipse.persistence.testing.tests.dynamic.DynamicTestingHelper.createSession;

/**
 * 
 * @author dclarke
 * @since EclipseLink 1.2
 */
public class EntityTypeFromDescriptor {

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
        DatabaseSession session = createSession();
        session.login();
        DynamicClassLoader dcl = DynamicClassLoader.lookup(session);

        ClassDescriptor descriptor = buildMyEntityDescriptor();
        assertFalse(descriptor.isAggregateDescriptor());

        DynamicType entityType = 
            (DynamicType)new DynamicTypeBuilder(dcl, descriptor, null).getType();
        MyEntity.DPM.setType(entityType);

        assertFalse(descriptor.isAggregateDescriptor());
        assertEquals(MyEntity.class, entityType.getJavaClass());

        session.addDescriptor(entityType.getDescriptor());
        new SchemaManager(session).replaceDefaultTables();

        DynamicEntity entity = entityType.newDynamicEntity();
        entity.set("id", 1);
        entity.set("name", "Name");

        session.insertObject(entity);

        session.logout();

    }

    /**
     * Verify that the descriptor for a dynamic type fails without the
     * additional configuration which is applied to the descriptor during the
     * EntityType creation.
     */
    @Test
    public void invalidDescriptorWithoutEntityType() throws Exception {
        RelationalDescriptor descriptor = buildMyEntityDescriptor();

        DatabaseSession session = createSession();
        session.addDescriptor(descriptor);

        try {
            session.login();
        }
        catch (IntegrityException ie) {
            assertEquals(descriptor.getMappings().size(), 
                ie.getIntegrityChecker().getCaughtExceptions().size());

            // Verify NoSuchField errors for each mapping
            for (int index = 0; index < descriptor.getMappings().size(); index++) {
                DescriptorException ex = (DescriptorException)
                    ie.getIntegrityChecker().getCaughtExceptions().get(index);
                assertEquals(
                    DescriptorException.NO_SUCH_FIELD_WHILE_INITIALIZING_ATTRIBUTES_IN_INSTANCE_VARIABLE_ACCESSOR,
                    ex.getErrorCode());
            }
            return;
        }
        fail("Expected IntegrityException not thrown");
    }

    private RelationalDescriptor buildMyEntityDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(MyEntity.class);
        descriptor.setTableName(TABLE_NAME);
        descriptor.addPrimaryKeyFieldName("ID");

        AbstractDirectMapping mapping = (AbstractDirectMapping)descriptor.addDirectMapping("id", "ID");
        mapping.setAttributeClassification(int.class);
        mapping = (AbstractDirectMapping)descriptor.addDirectMapping("name", "NAME");
        mapping.setAttributeClassification(String.class);

        return descriptor;
    }

}