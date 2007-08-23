/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.oxm.mappings;

import java.lang.reflect.Method;
import org.eclipse.persistence.internal.oxm.NillableNodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Session;

public class IsSetNodeNullPolicy extends NillableNodeNullPolicy {
    private final static Class[] PARAMETER_TYPES = {};
    private final static Object[] PARAMETERS = {};
    private String isSetMethodName;
    private Class[] parameterTypes = PARAMETER_TYPES;
    private Object[] parameters = PARAMETERS;

    public String getIsSetMethodName() {
        return isSetMethodName;
    }

    public void setIsSetMethodName(String isSetMethodName) {
        this.isSetMethodName = isSetMethodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
    
    public boolean directMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        Boolean isSet;
        try {
            Class objectClass = object.getClass();
            PrivilegedGetMethod privilegedGetMethod = new PrivilegedGetMethod(objectClass, getIsSetMethodName(), getParameterTypes(), false);
            Method isSetMethod = (Method) privilegedGetMethod.run();
            PrivilegedMethodInvoker privilegedMethodInvoker = new PrivilegedMethodInvoker(isSetMethod, object, parameters); 
            isSet = (Boolean) privilegedMethodInvoker.run();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        if(!isSet.booleanValue()) {
            return false;
        }

        return super.directMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);     
    }
    
    public boolean compositeObjectMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        if(!isSet(object)) {
            return false;
        }
        return super.compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
    }
    
    /**
     * When using the DOM Platform, this method is responsible for marshalling
     * null values for the XML Composite Object Mapping.
     * @param record
     * @param object
     * @param field
     * @return true if this method caused any objects to be marshalled, else 
     * false.
     */
    public boolean compositeObjectMarshal(XMLRecord record, Object object, XMLField field) {
        if(!isSet(object)) {
            return false;
        }
        return super.compositeObjectMarshal(record, object, field);
    }

    /**
     * Indicates if a null value has been set or not
     */
    private boolean isSet(Object object) {
        Boolean isSet;
        try {
            Class objectClass = object.getClass();
            PrivilegedGetMethod privilegedGetMethod = new PrivilegedGetMethod(objectClass, getIsSetMethodName(), getParameterTypes(), false);
            Method isSetMethod = (Method) privilegedGetMethod.run();
            PrivilegedMethodInvoker privilegedMethodInvoker = new PrivilegedMethodInvoker(isSetMethod, object, parameters); 
            isSet = (Boolean) privilegedMethodInvoker.run();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return isSet.booleanValue();
    }
    
    public boolean isNullCapabableValue() {
        return false;
    }
    
    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue) {
        XPathNode parentNode = xPathNode.getParent();
        XPathFragment xPathFragment = new XPathFragment();
        xPathFragment.setXPath('@' + XMLConstants.SCHEMA_NIL_ATTRIBUTE);
        xPathFragment.setNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        NillableNodeValue nillableNodeValue = new NillableNodeValue(nullCapableValue);
        parentNode.addChild(xPathFragment, nillableNodeValue, null);
    }
}
