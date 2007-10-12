/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.TypeHelper;
import java.util.HashMap;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOType;
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

    /**
     * INTERNAL:
     * @param implClass
     * @return
     */
    public Type getTypeForSimpleJavaType(Class implClass);

    /**
     * INTERNAL:
     *
     * @param uri
     * @param typeName
     * @param xsdLocalName
     * @return
     */
    public Type getOrCreateType(String uri, String typeName, String xsdLocalName);

    /**
     * INTERNAL:
     *
     * @param next
     * @return
     */
    public Type getOrCreateType(Type next);

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
    public void setTypesHashMap(HashMap typesHashMap);

    /**
     * INTERNAL:
     *
     * @return
     */
    public HashMap getTypesHashMap();

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
}