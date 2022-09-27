/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.internal.descriptors.InstanceVariableAttributeAccessor;
import org.eclipse.persistence.internal.descriptors.MethodAttributeAccessor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.NamespaceResolver;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathObjectMapping;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.sessions.Session;

public class XMLVariableXPathObjectMapping extends XMLCompositeObjectMapping  implements VariableXPathObjectMapping<AbstractSession, AttributeAccessor, ContainerPolicy, Converter, ClassDescriptor, DatabaseField, XMLMarshaller, Session,  XMLUnmarshaller, XMLRecord>, XMLMapping {

    protected String variableAttributeName;
    protected String variableGetMethodName;
    protected String variableSetMethodName;

    private AttributeAccessor variableAttributeAccessor;

    private boolean isAttribute;

    public void initialize(AbstractSession session) throws DescriptorException {
        //modified so that reference class on composite mappings is no longer mandatory
        String referenceClassName = getReferenceClassName();
        if ((this.referenceClass == null) && (referenceClassName != null)) {
            if (!referenceClassName.equals(XMLConstants.UNKNOWN_OR_TRANSIENT_CLASS)) {
                setReferenceClass(session.getDatasourcePlatform().getConversionManager().convertClassNameToClass(referenceClassName));
            }
        }
        if (this.referenceClass != null) {
            super.initialize(session);
        } else {
            //below should be the same as AbstractCompositeObjectMapping.initialize
            if (this.field == null) {
                throw DescriptorException.fieldNameNotSetInMapping(this);
            }

            setField(getDescriptor().buildField(this.field));
            setFields(collectFields());
            // initialize the converter - if necessary
            if (hasConverter()) {
                getConverter().initialize(this, session);
            }
        }
        if(null != getContainerAccessor()) {
            getContainerAccessor().initializeAttributes(this.referenceClass);
        }

        if(variableAttributeName != null){
            this.variableAttributeAccessor = new InstanceVariableAttributeAccessor();
            this.variableAttributeAccessor.setAttributeName(variableAttributeName);
            this.variableAttributeAccessor.initializeAttributes(this.getReferenceClass());
        }else if(variableGetMethodName != null){
            this.variableAttributeAccessor = new MethodAttributeAccessor();
            this.variableAttributeAccessor.setAttributeName("VARIABLE");
            ((MethodAttributeAccessor)this.variableAttributeAccessor).setGetMethodName(variableGetMethodName);
            if(variableSetMethodName == null){
                this.variableAttributeAccessor.setIsWriteOnly(true);
            }else{
                ((MethodAttributeAccessor)this.variableAttributeAccessor).setSetMethodName(variableSetMethodName);
            }
              this.variableAttributeAccessor.initializeAttributes(this.getReferenceClass());
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
         if (hasConverter()) {
             getConverter().initialize(this, session);
         }
   }

    public boolean isAbstractCompositeObjectMapping(){
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

    public void writeSingleValue(Object value, Object parent, XMLRecord xmlRow, AbstractSession session) {
        Object attributeValue = convertObjectValueToDataValue(value, session, xmlRow.getMarshaller());

        if (attributeValue != null) {
            XMLField variableField = new XMLField();
            variableField.setXPathFragment(getXPathFragmentForValue(attributeValue, xmlRow.getNamespaceResolver(), xmlRow.isNamespaceAware(), xmlRow.getNamespaceSeparator()));
            xmlRow.put(variableField, buildCompositeRow(variableField, attributeValue, session, xmlRow, WriteType.UNDEFINED));
        }
    }

    protected AbstractRecord buildCompositeRow(XMLField variableField, Object attributeValue, AbstractSession session, AbstractRecord parentRow, WriteType writeType) {
         ClassDescriptor  classDesc = getReferenceDescriptor(attributeValue, session);
         return buildCompositeRowForDescriptor(classDesc, attributeValue, session, (XMLRecord)parentRow, writeType);
    }

     public XPathFragment getXPathFragmentForValue(Object obj, NamespaceResolver nr, boolean isNamespaceAware, char namespaceSep) {
            Object value = getVariableAttributeAccessor().getAttributeValueFromObject(obj);
            String returnString;
            String uri = null;
            if(value instanceof QName){
                returnString = ((QName)value).getLocalPart();
                uri = ((QName)value).getNamespaceURI();
            }else{
                returnString = (String)value;
            }
            XPathFragment frag = new XPathFragment();
            if(isNamespaceAware && uri != null && uri.length() >0){

                String prefix = nr.resolveNamespaceURI(uri);
                if(prefix == null){
                       prefix = nr.generatePrefix();
                    //marshalRecord.namespaceDeclaration(prefix, uri);
                       frag.setGeneratedPrefix(true);
                }
                if(prefix != null && prefix.length() >0){
                    frag.setPrefix(prefix);
                    returnString = prefix + namespaceSep + returnString;
                }
                //In case of JSON marshalling namespace separator there should different like '.', than  default in XPathFragment ':'.
                //Namespace separator from there should be promoted to XPathFragment to correctly set localName.
                if(namespaceSep != 0){
                    frag.setNamespaceSeparator(namespaceSep);
                }
            }
            frag.setXPath(returnString);
            frag.setNamespaceURI(uri);


            return frag;
        }


        public boolean isAttribute() {
            return isAttribute;
        }

        public void setAttribute(boolean isAttribute) {
            this.isAttribute = isAttribute;
        }



}
