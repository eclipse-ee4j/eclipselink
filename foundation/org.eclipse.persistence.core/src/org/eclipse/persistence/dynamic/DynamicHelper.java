/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dclarke, mnorman - Dynamic Persistence
 *       http://wiki.eclipse.org/EclipseLink/Development/Dynamic 
 *       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/
package org.eclipse.persistence.dynamic;

//javase imports
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.internal.dynamic.DynamicPropertiesManager;
import org.eclipse.persistence.internal.dynamic.DynamicTypeImpl;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.schemaframework.DynamicSchemaManager;

/**
 * A DynamicHelper provides some utility methods to simplify application
 * development with dynamic types. Since the application does not have static
 * references to the dynamic types it must use entity names. This helper
 * provides simplified access to methods that would typically require the static
 * classes.
 * 
 * @author dclarke, mnorman
 * @since EclipseLink 1.2
 */
public class DynamicHelper {

    protected DatabaseSession session;
    protected Map<String, ClassDescriptor> fqClassnameToDescriptor = 
        new HashMap<String, ClassDescriptor>();

    public DynamicHelper(DatabaseSession session) {
        this.session = session;
        Collection<ClassDescriptor> descriptors = session.getDescriptors().values();
        for (ClassDescriptor desc : descriptors) {
            if (desc.getJavaClassName() != null) {
                fqClassnameToDescriptor.put(desc.getJavaClassName(), desc);
            }
        }
    }

    public DatabaseSession getSession() {
        return this.session;
    }

    /**
     * Lookup the dynamic type for an alias. This is required to get the type
     * for factory creation but can also be used to provide the application with
     * access to the meta model (type and properties) allowing for dynamic use
     * as well as optimized data value retrieval from an entity.
     */
    public DynamicType getType(String typeName) {
        ClassDescriptor cd = fqClassnameToDescriptor.get(typeName);
        if (cd == null) {
            cd = getSession().getClassDescriptorForAlias(typeName);
        }

        if (cd == null) {
            return null;
        }
        return getType(cd);
    }

    public static DynamicType getType(ClassDescriptor descriptor) {
        return (DynamicType) descriptor.getProperty(DynamicType.DESCRIPTOR_PROPERTY);
    }

    /**
     * Provide access to the entity's type.
     * 
     * @param entity
     * @return
     * @throws ClassCastException
     *             if entity is not an instance of {@link DynamicEntityImpl}
     */
    public static DynamicType getType(DynamicEntity entity) throws ClassCastException {
        return ((DynamicEntityImpl) entity).getType();
    }

    /**
     * Remove a dynamic type from the system.
     * 
     * This implementation assumes that the dynamic type has no relationships to
     * it and that it is not involved in an inheritance relationship. If there
     * are concurrent processes using this type when it is removed some
     * exceptions may occur.
     * 
     * @param session
     * @param typeName
     */
    public void removeType(String typeName) {
        DynamicType type = getType(typeName);

        if (type != null) {
            getSession().getIdentityMapAccessor().initializeIdentityMap(type.getJavaClass());

            ClassDescriptor descriptor = type.getDescriptor();
            fqClassnameToDescriptor.remove(descriptor.getJavaClassName());
            getSession().getProject().getOrderedDescriptors().remove(descriptor);
            getSession().getProject().getDescriptors().remove(type.getJavaClass());
            ((AbstractSession)getSession()).getCommitManager().getCommitOrder().remove(type.getJavaClass());
        }
    }

    /**
     * 
     */
    public DynamicEntity newDynamicEntity(String typeName) {
        DynamicType type = getType(typeName);

        if (type == null) {
            throw new IllegalArgumentException("DynamicHelper.createQuery: Dynamic type not found: " + typeName);
        }

        return type.newDynamicEntity();
    }

    /**
     * Helper method to simplify creating a native ReadAllQuery using the entity
     * type name (descriptor alias)
     */
    public ReadAllQuery newReadAllQuery(String typeName) {
        DynamicType type = getType(typeName);

        if (type == null) {
            throw new IllegalArgumentException("DynamicHelper.createQuery: Dynamic type not found: " + typeName);
        }

        return new ReadAllQuery(type.getJavaClass());
    }

    /**
     * Helper method to simplify creating a native ReadObjectQuery using the
     * entity type name (descriptor alias)
     */
    public ReadObjectQuery newReadObjectQuery(String typeName) {
        DynamicType type = getType(typeName);

        if (type == null) {
            throw new IllegalArgumentException("DynamicHelper.createQuery: Dynamic type not found: " + typeName);
        }

        return new ReadObjectQuery(type.getJavaClass());
    }

    /**
     * Helper method to simplify creating a native ReportQuery using the entity
     * type name (descriptor alias)
     */
    public ReportQuery newReportQuery(String typeName, ExpressionBuilder builder) {
        DynamicType type = getType(typeName);

        if (type == null) {
            throw new IllegalArgumentException("DynamicHelper.createQuery: Dynamic type not found: " + typeName);
        }

        return new ReportQuery(type.getJavaClass(), builder);
    }

    public DynamicClassLoader getDynamicClassLoader() {
        return DynamicClassLoader.lookup(getSession());
    }

    /**
     * Add one or more EntityType instances to a session and optionally generate
     * needed tables with or without FK constraints.
     * 
     * @param session
     * @param createMissingTables
     * @param generateFKConstraints
     * @param types
     */
    public void addTypes(boolean createMissingTables, boolean generateFKConstraints, DynamicType... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("No types provided");
        }
        Collection<ClassDescriptor> descriptors = new ArrayList<ClassDescriptor>(types.length);
        for (int index = 0; index < types.length; index++) {
            DynamicType typ = types[index];
            ClassDescriptor desc = typ.getDescriptor();
            DynamicTypeImpl typImpl = ((DynamicTypeImpl)typ);
            typImpl.setDescriptor(desc);
            try {
                Field dpmField = desc.getJavaClass().getField(DynamicPropertiesManager.PROPERTIES_MANAGER_FIELD);
                DynamicPropertiesManager dpm = (DynamicPropertiesManager)dpmField.get(null);
                dpm.setType(typImpl);
                typImpl.setDynamicPropertiesManager(dpm);
            }
            catch (Exception e) {
                // this is bad! Without the dpmField, not much to do but die :-( ...
                e.printStackTrace();
                return;
            }
            descriptors.add(desc);
            if (!types[index].getDescriptor().requiresInitialization((AbstractSession) session)) {
                types[index].getDescriptor().getInstantiationPolicy().initialize((AbstractSession) session);
            }
        }
        session.addDescriptors(descriptors);
        for (ClassDescriptor desc : descriptors) {
            if (desc.getJavaClassName() != null) {
                fqClassnameToDescriptor.put(desc.getJavaClassName(), desc);
            }
        }

        if (createMissingTables) {
            if (!getSession().isConnected()) {
                getSession().login();
            }
            new DynamicSchemaManager(session).createTables(generateFKConstraints, types);
        }
    }

    /**
     * A SessionCustomizer which configures all descriptors as dynamic entity
     * types.
     */
    public static class SessionCustomizer implements org.eclipse.persistence.config.SessionCustomizer {

        public void customize(Session session) throws Exception {
            DynamicClassLoader dcl = DynamicClassLoader.lookup(session);

            for (Iterator<?> i = session.getProject().getDescriptors().values().iterator(); i.hasNext();) {
                new DynamicTypeBuilder(dcl, (ClassDescriptor) i.next(), null);
            }
        }
    }

}
