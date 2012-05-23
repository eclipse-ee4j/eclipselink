/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/19/2010-2.1 ailitchev - Bug 244124 - Add Nested FetchGroup 
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.jpa.fetchgroups;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.indirection.IndirectContainer;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.queries.AttributeItem;
import org.eclipse.persistence.internal.queries.EntityFetchGroup;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.FetchGroupTracker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.Server;

/**
 * Test utility to verify the state of entities after they are loaded, copied,
 * or detached with respect to a defined FetchGroup.
 * 
 * @author dclarke
 * @since EclipseLink 2.1.0
 */
public class FetchGroupAssert {

    /**
     * Verify that a FetchGroup is valid with respect to the mappings of the
     * provided entity class.
     */
    public static boolean isValid(FetchGroup fetchGroup, EntityManagerFactory emf, Class<?> entityClass) {
        assertNotNull(fetchGroup);
        Session session = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession();
        ClassDescriptor descriptor = session.getDescriptor(entityClass);
        try {
            for (Map.Entry<String, AttributeItem> entry : fetchGroup.getItems().entrySet()) {
                AttributeItem item = entry.getValue();
                DatabaseMapping mapping = descriptor.getMappingForAttributeName(item.getAttributeName());

                if (mapping.isForeignReferenceMapping()) {
                    if (item.getGroup() != null) {
                        if (!isValid((FetchGroup)item.getGroup(), emf, ((ForeignReferenceMapping) mapping).getReferenceClass())) {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        } catch (QueryException qe) {
            return false;
        }
        return true;
    }

    /**
     * Verify that the attribute path specified is loaded in the provided entity
     */
    public static void assertFetchedAttribute(EntityManagerFactory emf, Object entity, String... attribute) {
        assertNotNull("EntityManagerFactory is null", emf);
        assertNotNull("Entity is null", entity);
        Server session = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession();
        assertNotNull("No Server session found for: " + emf, session);
        ClassDescriptor desc = session.getClassDescriptor(entity);
        assertNotNull("No descriptor found for: " + entity, desc);

        Object value = entity;
        if (attribute.length > 1) {
            String attrName = attribute[1];

            if (desc.hasFetchGroupManager()) {
                assertTrue("Attribute: '" + attrName + "' not fetched on: " + value, desc.getFetchGroupManager().isAttributeFetched(value, attrName));
            }
            DatabaseMapping mapping = desc.getMappingForAttributeName(attrName);
            value = mapping.getAttributeValueFromObject(value);

            if (value instanceof IndirectContainer) {
                value = ((IndirectContainer) value).getValueHolder();
            }
            if (value instanceof ValueHolderInterface) {
                ValueHolderInterface vhi = (ValueHolderInterface) value;
                assertTrue("ValueHolder for: '" + attrName + "' not instantiated", vhi.isInstantiated());
                value = vhi.getValue();
            }
            String[] tail = new String[attribute.length - 1];
            System.arraycopy(attribute, 1, tail, 0, attribute.length - 1);
            if (value instanceof Collection<?>) {
                for (Object obj : ((Collection<?>) value)) {
                    assertFetchedAttribute(emf, value, tail);
                }
            } else {
                assertFetchedAttribute(emf, value, tail);
            }
        } else {
            // This is where the actual end attribute in the path is validated.
            if (desc.hasFetchGroupManager()) {
                assertTrue(desc.getFetchGroupManager().isAttributeFetched(value, attribute[0]));
            }
        }
    }

    /**
     * Verify that the attribute path is loaded.
     */
    public static void assertNotFetchedAttribute(EntityManagerFactory emf, Object entity, String... attribute) {
        assertNotNull("EntityManagerFactory is null", emf);
        assertNotNull("Entity is null", entity);
        Server session = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession();
        assertNotNull("No Server session found for: " + emf, session);
        ClassDescriptor desc = session.getClassDescriptor(entity);
        assertNotNull("No descriptor found for: " + entity, desc);

        Object value = entity;
        for (int index = 0; index < attribute.length - 1; index++) {
            String attrName = attribute[index];

            if (desc.hasFetchGroupManager()) {
                assertTrue("Attribute: '" + attrName + "' not fetched on: " + value, desc.getFetchGroupManager().isAttributeFetched(value, attrName));
            }
            DatabaseMapping mapping = desc.getMappingForAttributeName(attrName);
            value = mapping.getAttributeValueFromObject(value);

            if (value instanceof IndirectContainer) {
                value = ((IndirectContainer) value).getValueHolder();
            }
            if (value instanceof ValueHolderInterface) {
                ValueHolderInterface vhi = (ValueHolderInterface) value;
                assertTrue("ValueHolder for: '" + attrName + "' not instantiated", vhi.isInstantiated());
                value = vhi.getValue();
            }
        }
    }

    /**
     * Assert that the entity provided has the attributes defined in the
     * FetchGroup loaded.
     * 
     * @param emf
     * @param entity
     * @param fetchGroup
     */
    public static void assertFetched(EntityManagerFactory emf, Object entity, FetchGroup fetchGroup) {
        assertNotNull("Null entity", entity);
        assertNotNull("No FetchGroup provided", fetchGroup);
        if (!(entity instanceof FetchGroupTracker)) {
            System.out.println();
        }
        assertTrue("Entity does not implement FetchGroupTracker: " + entity, entity instanceof FetchGroupTracker);

        FetchGroupTracker tracker = (FetchGroupTracker) entity;
        assertNotNull("FetchGroup on entity is null", tracker._persistence_getFetchGroup());
        FetchGroup groupToCompare = fetchGroup;
        if(!fetchGroup.isEntityFetchGroup()) {
            groupToCompare = new EntityFetchGroup(fetchGroup);
        }
        assertTrue("FetchGroup on entity does not equal provided", tracker._persistence_getFetchGroup().equals(groupToCompare));

        Server session = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession();
        assertNotNull(session);
        ClassDescriptor descriptor = session.getClassDescriptor(entity);
        assertNotNull(descriptor);
        assertTrue("", descriptor.getJavaClass().isAssignableFrom(entity.getClass()));

        for (DatabaseMapping mapping : descriptor.getMappings()) {
            if (descriptor.getObjectBuilder().getPrimaryKeyMappings().contains(mapping)) {
                assertTrue("PrimaryKey mapping not fetched: " + entity, tracker._persistence_isAttributeFetched(mapping.getAttributeName()));
            } else if (descriptor.usesOptimisticLocking() && descriptor.getOptimisticLockingPolicy() instanceof VersionLockingPolicy && ((VersionLockingPolicy) descriptor.getOptimisticLockingPolicy()).getVersionMapping() == mapping) {
                assertTrue("Optimistic version mapping not fetched: " + entity, tracker._persistence_isAttributeFetched(mapping.getAttributeName()));
            } else if (tracker._persistence_getFetchGroup().containsAttribute(mapping.getAttributeName())) {
                assertTrue(tracker._persistence_isAttributeFetched(mapping.getAttributeName()));
                // EntityFetchGroup never has nested fetch groups. 
/*                AttributeItem attrFI = tracker._persistence_getFetchGroup().getItem(mapping.getAttributeName());
                if (attrFI.getGroup() != null) {
                    Object value = mapping.getAttributeValueFromObject(entity);
                    if (value instanceof IndirectContainer) {
                        assertTrue(((IndirectContainer) value).isInstantiated());
                        Collection<?> values = (Collection<?>) value;
                        for (Object val : values) {
                            assertFetched(emf, val, (FetchGroup)attrFI.getGroup());
                        }
                        return;
                    }
                    if (value instanceof ValueHolderInterface) {
                        assertTrue(((ValueHolderInterface) value).isInstantiated());
                        value = ((ValueHolderInterface) value).getValue();
                    }
                    if (value != null) {
                        assertFetched(emf, value, (FetchGroup)attrFI.getGroup());
                    }
                }*/
            } else { // Should not be fetched
                assertFalse(tracker._persistence_isAttributeFetched(mapping.getAttributeName()));
            }
        }
    }

    public static void assertDefaultFetched(EntityManagerFactory emf, Object entity) {
        assertNotNull("Null entity", entity);

        ClassDescriptor descriptor = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession().getClassDescriptor(entity);
        assertNotNull("No descriptor found for: " + entity, descriptor);

        assertTrue("No FetchGroupManager on: " + descriptor, descriptor.hasFetchGroupManager());

        FetchGroup defaultFG = descriptor.getFetchGroupManager().getDefaultFetchGroup();
        assertNotNull("No default FetchGroup on: " + descriptor, defaultFG);

        assertFetched(emf, entity, defaultFG);
    }

    public static void assertFetched(EntityManagerFactory emf, Object entity, String fetchGroupName) {
        assertNotNull("Null entity", entity);

        ClassDescriptor descriptor = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession().getClassDescriptor(entity);
        assertNotNull("No descriptor found for: " + entity, descriptor);

        assertTrue("No FetchGroupManager on: " + descriptor, descriptor.hasFetchGroupManager());

        FetchGroup fg = descriptor.getFetchGroupManager().getFetchGroup(fetchGroupName);

        assertNotNull("No FetchGroup named: " + fetchGroupName, fg);

        assertFetched(emf, entity, fg);
    }

    /**
     * Verify that the provided entity does not have a FetchGroup configured on
     * it.
     */
    public static void assertNoFetchGroup(EntityManagerFactory emf, Object entity) {
        if (entity instanceof FetchGroupTracker) {
            FetchGroupTracker tracker = (FetchGroupTracker) entity;

            assertNull("Entity: " + entity + " has: " + tracker._persistence_getFetchGroup(), tracker._persistence_getFetchGroup());
        }
    }

    public static void assertConfig(EntityManagerFactory emf, String entityName, FetchGroup defaultFetchGroup) {
        assertConfig(emf, entityName, defaultFetchGroup, 0);
    }

    public static void assertConfig(EntityManagerFactory emf, String entityName, FetchGroup defaultFetchGroup, int numNamedFetchGroups) {
        ClassDescriptor descriptor = ((org.eclipse.persistence.jpa.JpaEntityManager)emf.createEntityManager()).getServerSession().getClassDescriptorForAlias(entityName);
        assertNotNull("Not descriptor found for: " + entityName, descriptor);
        assertConfig(descriptor, defaultFetchGroup, numNamedFetchGroups);
    }

    public static void assertConfig(ClassDescriptor descriptor, FetchGroup defaultFetchGroup, int numNamedFetchGroups) {
        String entityName = descriptor.getAlias();

        assertTrue("FetchGroupTracker not implemented by: " + entityName, FetchGroupTracker.class.isAssignableFrom(descriptor.getJavaClass()));

        if (defaultFetchGroup == null) {
            assertNull("Default FetchGroup not null: " + entityName, descriptor.getFetchGroupManager().getDefaultFetchGroup());
        } else {
//            assertEquals("Default FetchGroup does not match", defaultFetchGroup, descriptor.getFetchGroupManager().getDefaultFetchGroup());
            if(defaultFetchGroup != descriptor.getFetchGroupManager().getDefaultFetchGroup()) {
                fail("Default FetchGroup does not match");
            }
        }

        if(numNamedFetchGroups != descriptor.getFetchGroupManager().getFetchGroups().size()) {
            fail("Incorrect number of Named FetchGroups: " + entityName);
        }
    }

    public static void assertConfig(ClassDescriptor descriptor, FetchGroup defaultFetchGroup) {
        assertConfig(descriptor, defaultFetchGroup, 0);
    }
}
