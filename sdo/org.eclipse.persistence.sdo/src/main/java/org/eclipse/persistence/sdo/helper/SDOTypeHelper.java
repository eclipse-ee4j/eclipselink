/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;

import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.NamespaceResolver;

/**
 * <p><b>Purpose</b>: Helper to provide access to declared SDO Types.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Look up a Type given the uri and typeName or interfaceClass.
 * <li> SDO Types are available through the getType("commonj.sdo", typeName) method.
 * <li> Defines Types from DataObjects.
 * </ul>
 */
public interface SDOTypeHelper extends TypeHelper {

    /**
     * INTERNAL:
     *
     * @param sdoType
     * @return
     */
    Class getJavaWrapperTypeForSDOType(Type sdoType);

    SDOType getTypeForImplClass(Class implClass);

    /**
     * INTERNAL:
     * @param implClass
     * @return
     */
    Type getTypeForSimpleJavaType(Class implClass);

    /**
     * INTERNAL:
     *
     * @param newType
     */
    void addType(SDOType newType);

    /**
     * INTERNAL:
     *
     * @param aType
     * @return
     */
    QName getXSDTypeFromSDOType(Type aType);

    /**
     * INTERNAL:
     *
     * @param aName
     * @return
     */
    SDOType getSDOTypeFromXSDType(QName aName);

    /**
     * INTERNAL:
     *
     * @param typesHashMap
     */
    void setTypesHashMap(Map typesHashMap);

    /**
     * INTERNAL:
     *
     * @return
     */
    Map getTypesHashMap();

    /**
     * INTERNAL:
     * Return the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @return a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    Map getWrappersHashMap();

    /**
     * INTERNAL:
     * Set the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @param   aMap        a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    void setWrappersHashMap(Map aMap);

    /**
     * INTERNAL:
     *
     */
    void reset();

    /**
     * INTERNAL:
     * Return the helperContext that this instance is associated with.
     * @return
     */
    HelperContext getHelperContext();

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.
     *
     * @param helperContext
     */
    void setHelperContext(HelperContext helperContext);

    /**
     * INTERNAL:
     * Add the given namespace uri and prefix to the global namespace resolver.
     */
    String addNamespace(String prefix, String uri);

    /**
      * INTERNAL:
      * Return the prefix for the given uri, or generate a new one if necessary
      */
    String getPrefix(String uri);

    /**
    * INTERNAL:
    * Return the NamespaceResolver
    */
    NamespaceResolver getNamespaceResolver();

    /**
    * INTERNAL:
    * Return the Map of Open Content Properties
    */
    Map getOpenContentProperties();

    void addWrappersToProject(Project toplinkProject);

    Map getInterfacesToSDOTypeHashMap();

    Map<Class, SDOType> getImplClassesToSDOType();

    List getAnonymousTypes();

}
