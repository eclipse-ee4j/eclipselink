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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
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
    public Class getJavaWrapperTypeForSDOType(Type sdoType);

    public SDOType getTypeForImplClass(Class implClass);

    /**
     * INTERNAL:
     * @param implClass
     * @return
     */
    public Type getTypeForSimpleJavaType(Class implClass);

    /**
     * INTERNAL:
     *
     * @param newType
     * @return
     */
    public void addType(SDOType newType);

    /**
     * INTERNAL:
     *
     * @param aType
     * @return
     */
    public QName getXSDTypeFromSDOType(Type aType);

    /**
     * INTERNAL:
     *
     * @param aName
     * @return
     */
    public SDOType getSDOTypeFromXSDType(QName aName);

    /**
     * INTERNAL:
     *
     * @param typesHashMap
     */
    public void setTypesHashMap(Map typesHashMap);

    /**
     * INTERNAL:
     *
     * @return
     */
    public Map getTypesHashMap();

    /**
     * INTERNAL:
     * Return the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @return a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    public Map getWrappersHashMap();

    /**
     * INTERNAL:
     * Set the map of Wrapper objects (SDOWrapperTypes that wrap a primitive document).
     * @param   aMap        a HashMap of SDOWrapperTypes, keyed on the XSD type that it wraps.
     */
    public void setWrappersHashMap(Map aMap);

    /**
     * INTERNAL:
     *
     */
    public void reset();

    /**
     * INTERNAL:
     * Return the helperContext that this instance is associated with.
     * @return
     */
    public HelperContext getHelperContext();

    /**
     * INTERNAL:
     * Set the helperContext that this instance is associated with.
     *
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext);

    /**
     * INTERNAL:
     * Add the given namespace uri and prefix to the global namespace resolver.
     */
    public String addNamespace(String prefix, String uri);

    /**
      * INTERNAL:
      * Return the prefix for the given uri, or generate a new one if necessary
      */
    public String getPrefix(String uri);

    /**
    * INTERNAL:
    * Return the NamespaceResolver
    */
    public NamespaceResolver getNamespaceResolver();

    /**
    * INTERNAL:
    * Return the Map of Open Content Properties
    */
    public Map getOpenContentProperties();

    public void addWrappersToProject(Project toplinkProject);

    public Map getInterfacesToSDOTypeHashMap();

    public Map<Class, SDOType> getImplClassesToSDOType();

    public List getAnonymousTypes();

}
