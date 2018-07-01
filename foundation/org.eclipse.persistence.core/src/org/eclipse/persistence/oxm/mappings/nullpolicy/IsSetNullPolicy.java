/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.oxm.mappings.nullpolicy;

import java.lang.reflect.Method;

import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.NullCapableValue;
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
 * <li> Set to a non-null value<br>IsSet==true, value=value</li>
 * <li> Not set<br>isSet=false, value=null</li>
 * <li> Set to null value<br>isSet=true, value=null</li>
 * <li> Set to default value<br>isSet=false, value=default</li>
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
        parentNode.setNullCapableValue(nullCapableValue);
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
     * @param anIsSetMethodName
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
