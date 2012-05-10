/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     dmccann - Nov 4/2008 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.sdo.helper.delegates;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDODataFactory;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
/**
 * <p><b>Purpose</b>: Helper to provide access to SDO Data Factory.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Create DataObjects for given uri/typename pairs, interface class or type
 * </ul>
 */
public class SDODataFactoryDelegate implements SDODataFactory {
    private HelperContext aHelperContext;

    public SDODataFactoryDelegate(HelperContext aContext) {
        // set context before initializing maps
        aHelperContext = aContext;
    }
    
    public DataObject create(String uri, String typeName) {
        Type sdoType = getHelperContext().getTypeHelper().getType(uri, typeName);
        if (sdoType != null) {
            return create(sdoType);
        }
        throw new IllegalArgumentException(SDOException.typeNotFound(uri, typeName));
    }

    public DataObject create(Class interfaceClass) {
        if (interfaceClass == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("create", "interfaceClass"));
        }
        Type type = getHelperContext().getTypeHelper().getType(interfaceClass);
        if (type != null) {
            return create(type);
        }
        
        // at this point the type may not have been defined or there could be a classloader issue
        SDOXMLHelper xmlHelper = (SDOXMLHelper) getHelperContext().getXMLHelper();
        ClassLoader contextLoader = xmlHelper.getLoader();
        ClassLoader interfaceLoader = interfaceClass.getClassLoader();
        boolean loadersAreRelated = false;

        // check the hierarchy to see if the interface loader is a parent of the context loader
        while (contextLoader != null && !loadersAreRelated) {
            if (contextLoader == interfaceLoader) {
                loadersAreRelated = true;
            }
            ClassLoader parentLoader = contextLoader.getParent();
            if(contextLoader == parentLoader) {
                break;
            }
            contextLoader = parentLoader;
        }
        
        throw new IllegalArgumentException(SDOException.typeNotFoundForInterface(interfaceClass.getName(), loadersAreRelated));
    }

    public DataObject create(Type type) {
        if (type == null) {
            throw new IllegalArgumentException(SDOException.cannotPerformOperationWithNullInputParameter("create", "type"));
        }
        SDOType sdoType = (SDOType) type;
        
        if (sdoType.isAbstract()) {
            //throw illegal arg exception 
            //spec page 40                        
            throw new IllegalArgumentException(SDOException.errorCreatingDataObjectForType(sdoType.getURI(), sdoType.getName()));
        }

        if(sdoType.isDataType()) {
            SDOTypeHelper sth = (SDOTypeHelper) getHelperContext().getTypeHelper();
            sdoType = (SDOType) sth.getWrappersHashMap().get(sdoType.getQName());
        }

        Class implClass;
        try {
            implClass = sdoType.getImplClass();
            if (implClass != null) {
                // initialization of the properties Map Implementation will be done in the default constructor call below
                // testcase is in org.apache.tuscany.sdo.test
                SDODataObject theDataObject = (SDODataObject)implClass.newInstance();
                theDataObject._setType(sdoType);
                theDataObject._setHelperContext(getHelperContext());
                return theDataObject;
            }
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(SDOException.errorCreatingDataObjectForClass(e, (sdoType).getInstanceClassName(), sdoType.getURI(), sdoType.getName()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(SDOException.errorCreatingDataObjectForClass(e, (sdoType).getInstanceClassName(), sdoType.getURI(), sdoType.getName()));
        }
        SDODataObject dataObject = new SDODataObject();
        dataObject._setType(sdoType);
        dataObject._setHelperContext(getHelperContext());
        return dataObject;
    }

    /**
     * INTERNAL:
     * Return the current helperContext associated with this delegate.
     */
    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    /**
     * INTERNAL:
     * Set the current helperContext to be associated with this delegate
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
