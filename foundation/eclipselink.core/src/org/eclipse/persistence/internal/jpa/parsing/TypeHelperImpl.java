/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedGetField;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Implement type helper methods specified by TypeHelper.
 * This implementation uses Class instances to represent a type.
 */
public class TypeHelperImpl 
    extends BasicTypeHelperImpl implements TypeHelper {

    /** The session. */
    private final AbstractSession session;

    /** The class loader used to resolve type names. */
    private final ClassLoader classLoader;

    /** */
    public TypeHelperImpl(AbstractSession session, ClassLoader classLoader) {
        this.session = session;
        this.classLoader = classLoader;
    }

    /** Returns a type representation for the specified type name or null if
     * there is no such type. 
     */
    public Object resolveTypeName(String typeName) {
        try {
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    return (Class)AccessController.doPrivileged(
                        new PrivilegedClassForName(typeName, true, classLoader));
                } catch (PrivilegedActionException exception) {
                    return null;
                }
            } else {
                return PrivilegedAccessHelper.getClassForName(typeName, true, classLoader);
            }
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    /** Returns the type of the attribute with the specified name in the
     * specified owner class. 
     */ 
    public Object resolveAttribute(Object ownerClass, String attribute) {
        DatabaseMapping mapping = resolveAttributeMapping(ownerClass, attribute);
        return getType(mapping);
    }
    
    /** Returns the type of the class corresponding to the spcified abstract
     * schema type. 
     */
    public Object resolveSchema(String schemaName) {
        ClassDescriptor descriptor = session.getDescriptorForAlias(schemaName);
        return (descriptor != null) ? descriptor.getJavaClass() : null;
    }

    /** Returns the enum constant if the specified type denotes an enum type
     * and the specified constant denotes a constant of the enum type. 
     */
    public Object resolveEnumConstant(Object type, String constant) {
        Class clazz = getJavaClass(type);
        Object[] constants = clazz.getEnumConstants();
        if (constants != null) {
            for (int i = 0; i < constants.length; i++) {
                if (constant.equals(constants[i].toString())) {
                    return constants[i];
                }
            }
        }
        return null;
    }

    /** Returns true if the specified type denotes an entity class. */
    public boolean isEntityClass(Object type) {
        ClassDescriptor desc = getDescriptor(type);
        return (desc != null) && !desc.isAggregateDescriptor();
    }

    /** Returns true if the specified type denotes an embedded class. */
    public boolean isEmbeddable(Object type) {
        ClassDescriptor desc = getDescriptor(type);
        return (desc != null) && desc.isAggregateDescriptor();
    }

    /** Returns true if the specified type denotes an embedded attribute. */
    public boolean isEmbeddedAttribute(Object ownerClass, String attribute) {
        DatabaseMapping mapping = 
            resolveAttributeMapping(ownerClass, attribute);
        return (mapping != null) && mapping.isAggregateMapping();
    }

    /** Returns true if the specified type denotes a simple state attribute. */
    public boolean isSimpleStateAttribute(Object ownerClass, String attribute) {
        DatabaseMapping mapping = 
            resolveAttributeMapping(ownerClass, attribute);
        return (mapping != null) && mapping.isDirectToFieldMapping();
    }
    
    /** Returns true if the specified attribute denotes a single valued
     * or collection valued relationship attribute. 
     */
    public boolean isRelationship(Object ownerClass, String attribute) {
        DatabaseMapping mapping = 
            resolveAttributeMapping(ownerClass, attribute);
        return (mapping != null) && 
               (mapping.isObjectReferenceMapping() || 
                mapping.isOneToManyMapping() || 
                mapping.isManyToManyMapping());
    }

    /** Returns true if the specified attribute denotes a single valued
     * relationship attribute. 
     */
    public boolean isSingleValuedRelationship(Object ownerClass, 
                                              String attribute) {
        DatabaseMapping mapping = 
            resolveAttributeMapping(ownerClass, attribute);
        return (mapping != null) && mapping.isObjectReferenceMapping();
    }
    
    /** Returns true if the specified attribute denotes a collection valued
     * relationship attribute. 
     */
    public boolean isCollectionValuedRelationship(Object ownerClass, 
                                                  String attribute) {
        DatabaseMapping mapping = 
            resolveAttributeMapping(ownerClass, attribute);
        return (mapping != null) && 
            (mapping.isOneToManyMapping() || mapping.isManyToManyMapping());
    }

    // ===== Internal helper methods =====

    /** Returns the class descriptor if the specified non-null type is a class
     * object. 
     */
    private ClassDescriptor getDescriptor(Object type) {
        ClassDescriptor desc = null;
        if (type instanceof Class) {
            desc = session.getDescriptor((Class)type);
        } else if (type instanceof ClassDescriptor) {
            desc = (ClassDescriptor)type;
        }
        return desc;
    }

    /** Returns the mapping for the specified attribute of the psecified
     * class. The method returns null if the class is not known or if the
     * class does not have an attribute of the specified name.
     */
    private DatabaseMapping resolveAttributeMapping(Object ownerClass, 
                                                    String attr) {
        ClassDescriptor desc = getDescriptor(ownerClass);
        return (desc == null) ? null : desc.getMappingForAttributeName(attr);
    }

    /** */
    private Object getType(DatabaseMapping mapping) {
        if (mapping == null) {
            return null;
        }
        Object type = null;
        if (mapping.isForeignReferenceMapping()) {
            ClassDescriptor descriptor = mapping.getReferenceDescriptor();
            type = descriptor == null ? null : descriptor.getJavaClass();
        } else if (mapping.isAggregateMapping()) {
            // Return the ClassDescriptor as the type representation in case
            // of an embeeded. This makese sure that any property or field
            // access of the embedded uses the correct mapping information and
            // not the shell of the descriptors as stored by the session.
            type = ((AggregateMapping)mapping).getReferenceDescriptor();
        } else {
            type = mapping.getAttributeClassification();
        }
        return type;
    }

}
