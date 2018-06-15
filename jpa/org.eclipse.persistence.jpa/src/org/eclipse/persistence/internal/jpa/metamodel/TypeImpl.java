/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/19/2009-2.0  dclarke  - initial API start
//     06/30/2009-2.0  mobrien - finish JPA Metadata API modifications in support
//       of the Metamodel implementation for EclipseLink 2.0 release involving
//       Map, ElementCollection and Embeddable types on MappedSuperclass descriptors
//       - 266912: JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
//     07/20/2010-2.1  mobrien - 303063: Descriptor.javaClass may not be set according to metadata contract
package org.eclipse.persistence.internal.jpa.metamodel;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedActionException;

import javax.persistence.metamodel.Type;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: Provides the implementation for the Type interface
 *  of the JPA 2.0 Metamodel API (part of the JSR-317 EJB 3.1 Criteria API)
 * <p>
 * <b>Description</b>:
 * Instances of the type Type represent persistent object
 * or attribute types.
 *
 * @see javax.persistence.metamodel.Type
 * @since EclipseLink 1.2 - JPA 2.0
 *
 * @param <X>  The type of the represented object or attribute
 */
public abstract class TypeImpl<X> implements Type<X>, Serializable {

    /** The Java Class in use that this Type represents */
    private Class<X> javaClass;

    private String javaClassName;

    protected TypeImpl(Class<X> javaClass) {
        this(javaClass, null);
    }

    protected TypeImpl(Class<X> javaClass, String javaClassName) {
        // 303063: secondary check for case where descriptor has no java class set - should never happen but should be warned about
        if(null == javaClass && null == javaClassName) {
            AbstractSessionLog.getLog().log(SessionLog.FINEST, SessionLog.METAMODEL, "metamodel_typeImpl_javaClass_should_not_be_null", this, null); // exporting (this) outside the constructor breaks concurrency
            // Default to Object to avoid a NPE - in the case where javaClass is not set or not set yet via Project.convertClassNamesToClasses()
            this.javaClass = MetamodelImpl.DEFAULT_ELEMENT_TYPE_FOR_UNSUPPORTED_MAPPINGS;
        } else {
            this.javaClassName = javaClassName;
            this.javaClass = javaClass;
        }
    }

    /**
     *  Return the represented Java type.
     *  @return Java type
     */
    public Class<X> getJavaType(ClassLoader classLoader) {
        if (javaClass == null){
            try{
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                    try {
                        javaClass = AccessController.doPrivileged(new PrivilegedClassForName(javaClassName, true, classLoader));
                    } catch (PrivilegedActionException exception) {
                        throw ValidationException.classNotFoundWhileConvertingClassNames(javaClassName, exception.getException());
                    }
                } else {
                    javaClass = org.eclipse.persistence.internal.security.PrivilegedAccessHelper.getClassForName(javaClassName, true, classLoader);
                }
            } catch (ClassNotFoundException exc){
                throw ValidationException.classNotFoundWhileConvertingClassNames(javaClassName, exc);
            }
        }
        return this.javaClass;
    }

    /**
     *  Return the represented Java type.
     *  @return Java type
     */
    @Override
    public Class<X> getJavaType() {
        if (javaClass == null){
            javaClass = ConversionManager.getDefaultManager().convertClassNameToClass(javaClassName);
        }
        return this.javaClass;
    }

    /**
     * Return the name of the java type
     */
    public String getJavaTypeName(){
        return javaClassName;
    }

    /**
     * INTERNAL:
     * Return whether this type is an Entity (true) or MappedSuperclass (false) or Embeddable (false)
     * @return
     */
    public abstract boolean isEntity();

    /**
     * INTERNAL:
     * Return whether this type is identifiable.
     * This would be EntityType and MappedSuperclassType
     * @return
     */
    protected abstract boolean isIdentifiableType();

    /**
     * INTERNAL:
     * Return whether this type is identifiable.
     * This would be EmbeddableType as well as EntityType and MappedSuperclassType
     * @return
     */
    protected abstract boolean isManagedType();

    /**
     * INTERNAL:
     * Return whether this type is an MappedSuperclass (true) or Entity (false) or Embeddable (false)
     * @return
     */
    public abstract boolean isMappedSuperclass();

    /**
     * INTERNAL:
     * Return the string representation of the receiver.
     */
    @Override
    public String toString() {
        StringBuffer aBuffer = new StringBuffer();
        aBuffer.append(this.getClass().getSimpleName());
        aBuffer.append("@");
        aBuffer.append(hashCode());
        if(null != this.getJavaType()) {
            aBuffer.append(":");
            aBuffer.append(this.getJavaType().getSimpleName());
        }
        aBuffer.append(" [ javaType: ");
        aBuffer.append(this.getJavaType());
        toStringHelper(aBuffer);
        aBuffer.append("]");
        return aBuffer.toString();
    }

    /**
     * INTERNAL:
     * Append the partial string representation of the receiver to the StringBuffer.
     */
    protected abstract void toStringHelper(StringBuffer aBuffer);
}
