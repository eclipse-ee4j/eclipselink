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

import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.OptionalNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.security.PrivilegedGetMethod;
import org.eclipse.persistence.internal.security.PrivilegedMethodInvoker;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.record.MarshalRecord;
import org.eclipse.persistence.sessions.Session;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: 
 * This subclass of OptionalNodeNullPolicy is responsible for handling how
 * non-nillable values are marshaled and unmarshaled in the following cases.</p>
 * <ul>
 * <li> Set to a non-null value</li><br/>IsSet==true, value=value
 * <li> Not set</li><br/>isSet=false, value=null
 * <li> Set to null value </li><br/>isSet=true, value=null
 * <li> Set to default value </li><br/>isSet=false, value=default
 * </ul>
 */
public class IsSetOptionalNodeNullPolicy extends OptionalNodeNullPolicy {
    private final static Class[] PARAMETER_TYPES = {};
    private final static Object[] PARAMETERS = {};
    private String isSetMethodName;
    private Class[] parameterTypes = PARAMETER_TYPES;
    private Object[] parameters = PARAMETERS;

    public String getIsSetMethodName() {
        return isSetMethodName;
    }

    public void setIsSetMethodName(String isSetMethodNameString) {
        isSetMethodName = isSetMethodNameString;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypesArray) {
        parameterTypes = parameterTypesArray;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parametersArray) {
        parameters = parametersArray;
    }
    
    public boolean directMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
        Boolean isSet;
        Class objectClass = object.getClass();
        try {            
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

        // write out empty element
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);
      	return true;
    }
    
    public boolean compositeObjectMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, Object object, Session session, NamespaceResolver namespaceResolver) {
    	// this function is not fully supported yet
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

        // this call is really only valid when using DOM - TBD
        // write out empty element - we need to differentiate between object=null and object=new Object() with null fields
        //marshalRecord.element(xPathFragment.getNamespaceURI(), xPathFragment.getLocalName(), xPathFragment.getShortName());
        XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
        marshalRecord.closeStartGroupingElements(groupingFragment);
        return true;
    }
    
    public boolean valueIsNull(Attributes attributes) {
    	// we return whether attributes are present
        return attributes.getLength() < 1;
    }

    public boolean valueIsNull(Element element) {
    	// no attributes and no child nodes
        return null == element || !element.hasAttributes();    	
    }

    public boolean isNullCapabableValue() {
    	// we are not doing a set when the element is missing
        return false;
    }

    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue) {
    	if(xPathNode.getXPathFragment().isAttribute()) {
    		return;
    	}
    	// get the parent above the text() node
        XPathNode parentNode = xPathNode.getParent();
        OptionalNodeValue optionalNodeValue = new OptionalNodeValue(nullCapableValue);        
        parentNode.setNodeValue(optionalNodeValue);        
    }
}