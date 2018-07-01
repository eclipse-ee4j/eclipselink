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
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.oxm.mappings;

import java.util.Vector;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathCollectionMapping;
import org.eclipse.persistence.internal.oxm.mappings.XMLContainerMapping;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Session;
public class XMLVariableXPathCollectionMapping extends XMLCompositeCollectionMapping implements VariableXPathCollectionMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, Session, XMLUnmarshaller, XMLRecord>, XMLMapping, XMLContainerMapping {

    protected String variableAttributeName;
    protected String variableGetMethodName;
    protected String variableSetMethodName;

    private AttributeAccessor variableAttributeAccessor;

    private boolean isAttribute;

    public void initialize(AbstractSession session) throws DescriptorException {
       super.initialize(session);

        if(variableAttributeAccessor == null){

            if(variableAttributeName != null){
                this.variableAttributeAccessor = new InstanceVariableAttributeAccessor();
                this.variableAttributeAccessor.setAttributeName(variableAttributeName);
            }else if(variableGetMethodName != null){
                this.variableAttributeAccessor = new MethodAttributeAccessor();
                this.variableAttributeAccessor.setAttributeName("VARIABLE");
                ((MethodAttributeAccessor)this.variableAttributeAccessor).setGetMethodName(variableGetMethodName);
                if(variableSetMethodName == null){
                    this.variableAttributeAccessor.setIsWriteOnly(true);
                }else{
                    ((MethodAttributeAccessor)this.variableAttributeAccessor).setSetMethodName(variableSetMethodName);
                }
            }
        }
        this.variableAttributeAccessor.initializeAttributes(this.getReferenceClass());
    }

    public void useMapClass(String concreteContainerClassName) {
        MapContainerPolicy policy = new MapContainerPolicy(concreteContainerClassName);
        this.setContainerPolicy(policy);
    }

    protected void initializeMapContainerPolicy(AbstractSession session, MapContainerPolicy cp){
           super.initializeMapContainerPolicy(session, cp);
           if(variableAttributeName != null){
               cp.setKeyName(variableAttributeName);
           }else if(variableGetMethodName != null){
               cp.setKeyMethodName(variableGetMethodName);
           }
       }

    @Override
    protected Vector collectFields() {
        if(field != null){
            return super.collectFields();
        }
       // Vector fields = new Vector(1);
        //fields.addElement(this.getField());
        //return fields;
        return NO_FIELDS;
    }

public Vector getFields() {
    return collectFields();
}

//    public Vector getFields() {
  //      return fields;
        //return NO_FIELDS;
    // }

    protected void initializeReferenceDescriptorAndField(AbstractSession session){
         if (getReferenceClass() == null) {
             throw DescriptorException.referenceClassNotSpecified(this);
         }

         setReferenceDescriptor(session.getDescriptor(getReferenceClass()));


         ClassDescriptor refDescriptor = this.getReferenceDescriptor();
         if (refDescriptor == null) {
             session.getIntegrityChecker().handleError(DescriptorException.descriptorIsMissing(getReferenceClass().getName(), this));
             return;
         }

         if(field != null){
           setField(getDescriptor().buildField(this.field));
           setFields(collectFields());
         }

         if (hasConverter()) {
             getConverter().initialize(this, session);
         }
   }

    public boolean isAbstractCompositeCollectionMapping(){
        return false;
    }

    public String getVariableAttributeName() {
        return variableAttributeName;
    }

    public void setVariableAttributeName(String variableAttributeName) {
        this.variableAttributeName = variableAttributeName;
    }

    public String getVariableGetMethodName() {
        return variableGetMethodName;
    }

    public void setVariableGetMethodName(String variableGetMethodName) {
        this.variableGetMethodName = variableGetMethodName;
    }

    public String getVariableSetMethodName() {
        return variableSetMethodName;
    }

    public void setVariableSetMethodName(String variableSetMethodName) {
        this.variableSetMethodName = variableSetMethodName;
    }


    public AttributeAccessor getVariableAttributeAccessor() {
        return variableAttributeAccessor;
    }

    public void setVariableAttributeAccessor(
            AttributeAccessor variableAttributeAccessor) {
        this.variableAttributeAccessor = variableAttributeAccessor;
    }


     public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) throws DescriptorException {
          if (this.isReadOnly()) {
                return;
            }

            Object attributeValue = this.getAttributeValueFromObject(object);
            ContainerPolicy cp = this.getContainerPolicy();

            Object iter = cp.iteratorFor(attributeValue);
            if(null != iter) {
                while(cp.hasNext(iter)) {
                    Object element = cp.next(iter, session);
                    // convert the value - if necessary
                    element = convertObjectValueToDataValue(element, session, ((XMLRecord) row).getMarshaller());
                    if(element != null) {
                        XMLField variableField = new XMLField();
                        XMLRecord xmlRow = (XMLRecord)row;
                        //variableField.setXPathFragment(getXPathFragmentForValue(element,(XMLRecord)row));
                        variableField.setXPathFragment(getXPathFragmentForValue(element,xmlRow.getNamespaceResolver(), xmlRow.isNamespaceAware(), xmlRow.getNamespaceSeparator()));
                        row.put(variableField, buildCompositeRow(variableField, element, session, row, writeType));
                    }
                }
            }
     }

     protected AbstractRecord buildCompositeRow(XMLField variableField, Object attributeValue, AbstractSession session, AbstractRecord parentRow, WriteType writeType) {
         ClassDescriptor  classDesc = getReferenceDescriptor(attributeValue, session);
         return buildCompositeRowForDescriptor(classDesc, attributeValue, session, (XMLRecord)parentRow, writeType);
    }

     public XPathFragment getXPathFragmentForValue(Object obj, NamespaceResolver nr, boolean isNamespaceAware,char sep) {
            Object value = getVariableAttributeAccessor().getAttributeValueFromObject(obj);
            if(value == null){
                throw XMLMarshalException.nullValueNotAllowed(getVariableAttributeName(), getReferenceClassName());
            }

            String returnString;
            String uri = null;
            if(value instanceof QName){
                returnString = ((QName)value).getLocalPart();
                uri = ((QName)value).getNamespaceURI();
            }else{
                returnString = (String)value;
            }
            XPathFragment frag = new XPathFragment();
            frag.setLocalName(returnString);
            if(isNamespaceAware && uri != null && uri.length() >0){
                String prefix = nr.resolveNamespaceURI(uri);
                if(prefix == null){
                       prefix = nr.generatePrefix();
                    //marshalRecord.namespaceDeclaration(prefix, uri);
                       frag.setGeneratedPrefix(true);
                }
                if(prefix != null && prefix.length() >0){
                    frag.setPrefix(prefix);
                    //returnString = prefix + sep + returnString;
                }
            }

            //frag.setXPath(returnString);
            //frag.setLocalName(localName);

            frag.setNamespaceURI(uri);

            return frag;
        }

        public boolean isAttribute() {
            return isAttribute;
        }

        public void setAttribute(boolean isAttribute) {
            this.isAttribute = isAttribute;
        }

        public void useMapClassName(String concreteContainerClassName, String methodName) {
            // the reference class has to be specified before coming here
            if (this.getReferenceClassName() == null) {
                throw DescriptorException.referenceClassNotSpecified(this);
            }
            MapContainerPolicy policy = new MapContainerPolicy(concreteContainerClassName);
            policy.setKeyName(methodName, getReferenceClass().getName());
            this.setContainerPolicy(policy);
        }
}
