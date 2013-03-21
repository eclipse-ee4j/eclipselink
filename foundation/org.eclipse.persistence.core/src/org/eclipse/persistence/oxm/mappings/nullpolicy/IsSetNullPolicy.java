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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.oxm.mappings.nullpolicy;

import java.lang.reflect.Method;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NillableNodeValue;
import org.eclipse.persistence.internal.oxm.NodeValue;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
import org.eclipse.persistence.internal.oxm.OptionalNodeValue;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.XPathNode;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;

/**
 * PUBLIC:
 * <b>Description</b>: 
 * This null policy allows for various configurations of isSet behavior to be set.<br>
 * Marshal:<br>
 * The boolean value of the isSet() state of a node will determine whether a node will be written out
 * for a null value.
 * Unmarshal:<br>
 *  
 * <p><b>The following instance fields can be set</b>:<ul>
 * <li>isSetMethodName: </li>
 * <li>isSetParameterTypes: </li>
 * <li>isSetParameters: </li>
 * </ul>
 * <p>
 *&nbsp;<b>Usage</b>:<br>
 * <ul>
 * <li> Set to a non-null value</li><br/>IsSet==true, value=value
 * <li> Not set</li><br/>isSet=false, value=null
 * <li> Set to null value </li><br/>isSet=true, value=null
 * <li> Set to default value </li><br/>isSet=false, value=default
 * </ul>
 * 
 * @see org.eclipse.persistence.internal.oxm.NullCapableValue
 * @since Oracle TopLink 11<i>g</i> Release 1 (11.1.1)
 */
public class IsSetNullPolicy extends AbstractNullPolicy {
    private static final Class[] PARAMETER_TYPES = {};
    private static final Object[] PARAMETERS = {};
    private String isSetMethodName;
    private Class[] isSetParameterTypes = PARAMETER_TYPES;
    private Object[] isSetParameters = PARAMETERS;
    private Method isSetMethod;

    /**
     * Default Constructor
     * Set the IsSetPerformedForAbsentNode to false to enable the other 2 flags
     * isNullRepresentedByEmptyNode and isNullRepresentedByXsiNil 
     */
    public IsSetNullPolicy() {
        super();
        isSetPerformedForAbsentNode = false;
    }

    /**
     * Specific Constructor to set the name for checking the isSet state of the mapping
     * @param anIsSetMethodName
     */
    public IsSetNullPolicy(String anIsSetMethodName) {
        this();
        setIsSetMethodName(anIsSetMethodName);
    }

    /**
     * Specific Constructor to set both the Marshal enum and the Unmarshal flags.
     * @param anIsSetMethodName
     * @param bIsNullRepresentedByEmptyNode
     * @param bIsNullRepresentedByXsiNil
     * @param aMarshalNullRepresentation
     */
    public IsSetNullPolicy(String anIsSetMethodName, //
            boolean bIsNullRepresentedByEmptyNode, boolean bIsNullRepresentedByXsiNil, //
            XMLNullRepresentationType aMarshalNullRepresentation) {
        this(anIsSetMethodName);
        setNullRepresentedByEmptyNode(bIsNullRepresentedByEmptyNode);
        setNullRepresentedByXsiNil(bIsNullRepresentedByXsiNil);
        setMarshalNullRepresentation(aMarshalNullRepresentation);
    }

    @Override
    public boolean directMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, //
            Object object, CoreSession session, NamespaceResolver namespaceResolver) {
        // Do nothing when the value is not set or we are marshaling as ABSENT_NODE (optional) 
        if (!isSet(object)) {
            return false;
        } else {
            return super.directMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }
    }

    /**
     * INTERNAL
     */
    @Override
    public void directMarshal(Field field, AbstractMarshalRecord record, Object object) {
        if(!isSet(object)) {
            return;
        }
        super.directMarshal(field, record, object);
    }
    

    /**
     * INTERNAL
     */
    @Override
    public boolean compositeObjectMarshal(XPathFragment xPathFragment, MarshalRecord marshalRecord, //
            Object object, CoreSession session, NamespaceResolver namespaceResolver) {
        // Do nothing when the value is not set or we are marshaling as ABSENT_NODE (optional)    	
        if (!isSet(object)) {
            return false;
        } else {
            return super.compositeObjectMarshal(xPathFragment, marshalRecord, object, session, namespaceResolver);
        }
    }

    /**
     * INTERNAL
     */
    @Override
    public boolean compositeObjectMarshal(AbstractMarshalRecord record, Object object, Field field, CoreAbstractSession session) {
        if (!isSet(object)) {
            return false;
        } else {
            return super.compositeObjectMarshal(record, object, field, session);
        }
    }

    public void xPathNode(XPathNode xPathNode, NullCapableValue nullCapableValue) {
        // isset optional only    	
        if (!(isNullRepresentedByXsiNil() || marshalNullRepresentation == XMLNullRepresentationType.XSI_NIL)) {
            if (xPathNode.getXPathFragment().isAttribute()) {
                return;
            }
        }

        // get the parent above the text() node    	
        XPathNode parentNode = xPathNode.getParent();

        // isset nillable only        
        if (isNullRepresentedByXsiNil() || marshalNullRepresentation == XMLNullRepresentationType.XSI_NIL) {
            XPathFragment xPathFragment = new XPathFragment();
            xPathFragment.setXPath('@' + Constants.SCHEMA_NIL_ATTRIBUTE);
            xPathFragment.setNamespaceURI(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            NodeValue aNodeValue = new NillableNodeValue(nullCapableValue);
            parentNode.addChild(xPathFragment, aNodeValue, null);
        } else {
            NodeValue aNodeValue = new OptionalNodeValue(nullCapableValue);
            parentNode.setNodeValue(aNodeValue);
        }
    }

    /**
     * INTERNAL:
     * Indicates if a null value has been set or not.
     * @param object
     * @return boolean (isSet status)
     */
    private boolean isSet(Object object) {
        try {
            Boolean isSet = (Boolean) PrivilegedAccessHelper.invokeMethod(getIsSetMethod(object.getClass()), object, isSetParameters);
            return isSet.booleanValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @return 
     */
    public String getIsSetMethodName() {
        return isSetMethodName;
    }

    /**
     * 
     * @param isSetMethodName
     */
    public void setIsSetMethodName(String anIsSetMethodName) {
        isSetMethodName = anIsSetMethodName;
    }

    /**
     * 
     * @return 
     */
    public Class[] getIsSetParameterTypes() {
        return isSetParameterTypes;
    }

    /**
     * 
     * @param parameterTypes
     */
    public void setIsSetParameterTypes(Class[] parameterTypes) {
        isSetParameterTypes = parameterTypes;
    }

    /**
     * 
     * @return
     */
    public Object[] getIsSetParameters() {
        return isSetParameters;
    }

    /**
     * 
     * @param parameters
     */
    public void setIsSetParameters(Object[] parameters) {
        isSetParameters = parameters;
    }

    private Method getIsSetMethod(Class aClass) throws NoSuchMethodException {
        if(null == isSetMethod) {
             isSetMethod = PrivilegedAccessHelper.getPublicMethod(aClass, getIsSetMethodName(), getIsSetParameterTypes(), false);
        }
        return isSetMethod;
    }

}
