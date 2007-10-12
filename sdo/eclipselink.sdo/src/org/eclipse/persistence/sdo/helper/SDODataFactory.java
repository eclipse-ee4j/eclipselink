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

import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOType;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;
import org.eclipse.persistence.exceptions.SDOException;

/**
 * <p><b>Purpose</b>: The implementation of commonj.sdo.helper.DataFactory</p>
 */
public class SDODataFactory implements DataFactory {
    // hold the context containing all helpers so that we can preserve inter-helper relationships
    private HelperContext aHelperContext;

    public SDODataFactory() {
        // TODO: JIRA129 - default to static global context - Do Not use this convenience constructor outside of JUnit testing
        //aHelperContext = HelperProvider.getDefaultContext();
    }

    public SDODataFactory(HelperContext aContext) {
        aHelperContext = aContext;
    }

    /**
     * Create a DataObject of the Type specified by typeName with the given package uri.
     * @param uri The uri of the Type.
     * @param typeName The name of the Type.
     * @return the created DataObject.
     * @throws IllegalArgumentException if the uri and typeName does
     *    not correspond to a Type this factory can instantiate.
     */
    public DataObject create(String uri, String typeName) {
        Type sdoType = getHelperContext().getTypeHelper().getType(uri, typeName);
        if (sdoType != null) {
            return create(sdoType);
        }
        throw new IllegalArgumentException(SDOException.typeNotFound(uri, typeName));
    }

    /**
     * Create a DataObject supporting the given interface.
     * InterfaceClass is the interface for the DataObject's Type.
     * The DataObject created is an instance of the interfaceClass.
     * @param interfaceClass is the interface for the DataObject's Type.
     * @return the created DataObject.
     * @throws IllegalArgumentException if the instanceClass does
     *    not correspond to a Type this factory can instantiate.
     */
    public DataObject create(Class interfaceClass) {
        if (interfaceClass == null) {
            throw new IllegalArgumentException(SDOException.typeNotFoundForInterface(null));
        }
        Type type = getHelperContext().getTypeHelper().getType(interfaceClass);
        if ((type != null) && (type.getInstanceClass() != null)) {
            return create(type);
        }
        throw new IllegalArgumentException(SDOException.typeNotFoundForInterface(interfaceClass.getName()));
    }

    /**
     * Create a DataObject of the Type specified.
     * @param type The Type.
     * @return the created DataObject.
     * @throws IllegalArgumentException if the Type
     *    cannot be instantiated by this factory.
     */
    public DataObject create(Type type) {
        if (type.isAbstract() || type.isDataType()) {
            //throw illegal arg exception 
            //spec page 40                        
            throw new IllegalArgumentException(SDOException.errorCreatingDataObjectForType(type.getURI(), type.getName()));
        }

        Class typedDataObjectClass = ((SDOType)type).getInstanceClass();
        if (typedDataObjectClass != null) {
            try {
                Class implClass = ((SDOType)type).getImplClass();
                if (implClass != null) {
                    // initialization of the properties Map Implementation will be done in the default constructor call below
                    // testcase is in org.apache.tuscany.sdo.test
                    SDODataObject theDataObject = (SDODataObject)implClass.newInstance();
                    theDataObject._setType(type);
                    theDataObject._setHelperContext(getHelperContext());
                    return theDataObject;
                }
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(SDOException.errorCreatingDataObjectForClass(e, typedDataObjectClass.getName(), type.getURI(), type.getName()));
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(SDOException.errorCreatingDataObjectForClass(e, typedDataObjectClass.getName(), type.getURI(), type.getName()));
            }
        }
        SDODataObject dataObject = new SDODataObject();
        dataObject._setType(type);
        dataObject._setHelperContext(getHelperContext());
        return dataObject;

    }

    public HelperContext getHelperContext() {
        if(null == aHelperContext) {
            aHelperContext = HelperProvider.getDefaultContext();
        }
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}