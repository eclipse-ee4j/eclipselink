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
//     Denise Smith - 2.5.1 - Initial Implementation
package org.eclipse.persistence.internal.oxm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.mappings.VariableXPathCollectionMapping;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.ObjectMarshalContext;
import org.eclipse.persistence.internal.oxm.record.UnmarshalRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;

public class XMLVariableXPathCollectionMappingNodeValue extends XMLVariableXPathMappingNodeValue implements ContainerValue{

    VariableXPathCollectionMapping mapping;
    private int index = -1;


    public XMLVariableXPathCollectionMappingNodeValue(VariableXPathCollectionMapping variableXPathCollectionMapping) {
        mapping= variableXPathCollectionMapping;
    }

    @Override
    protected void setOrAddAttributeValue(UnmarshalRecord unmarshalRecord,
            Object value, XPathFragment xPathFragment, Object collection) {
            unmarshalRecord.addAttributeValue(this, value);
    }

    @Override
    public VariableXPathCollectionMapping getMapping() {
        return mapping;
    }

    @Override
    public boolean marshal(XPathFragment xPathFragment,
            MarshalRecord marshalRecord, Object object,
            CoreAbstractSession session, NamespaceResolver namespaceResolver) {
        if (mapping.isReadOnly()) {
            return false;
        }
        CoreContainerPolicy cp = mapping.getContainerPolicy();
        Object collection = mapping.getAttributeAccessor().getAttributeValueFromObject(object);
        if (null == collection) {
            AbstractNullPolicy wrapperNP = mapping.getWrapperNullPolicy();
            if (wrapperNP != null && wrapperNP.getMarshalNullRepresentation().equals(XMLNullRepresentationType.XSI_NIL)) {
                marshalRecord.nilSimple(namespaceResolver);
                return true;
            } else {
                return false;
            }
        }


        Object iterator = cp.iteratorFor(collection);
        if (null != iterator && cp.hasNext(iterator)) {
            XPathFragment groupingFragment = marshalRecord.openStartGroupingElements(namespaceResolver);
            marshalRecord.closeStartGroupingElements(groupingFragment);
        } else {
            return marshalRecord.emptyCollection(xPathFragment, namespaceResolver, mapping.getWrapperNullPolicy() != null);
        }


        if(marshalRecord.getMarshaller().isApplicationJSON()){
            List<XPathFragment> frags = new ArrayList();
            List<List> values = new ArrayList<List>();

            //sort the elements. Results will be a list of xpathfragments and a corresponding list of
            //collections associated with those xpathfragments
            XPathFragment xmlRootFragment;
            while(cp.hasNext(iterator)) {

                Object nextValue = cp.next(iterator, session);
                nextValue = mapping.convertObjectValueToDataValue(nextValue, session, marshalRecord.getMarshaller());
                XPathFragment frag = mapping.getXPathFragmentForValue(nextValue, marshalRecord.getNamespaceResolver(), marshalRecord.isNamespaceAware(), marshalRecord.getNamespaceSeparator());
                if(frag != null){

                        int index = frags.indexOf(frag);
                        if(index > -1){
                            values.get(index).add(nextValue);
                        }else{
                            frags.add(frag);
                            List valuesList = new ArrayList();
                            valuesList.add(nextValue);
                            values.add(valuesList);
                        }
                    }

            }

            for(int i =0;i < frags.size(); i++){
                XPathFragment nextFragment = frags.get(i);
                List listValue = values.get(i);

                if(nextFragment != null){
                   int valueSize = listValue.size();
                   if(valueSize > 1 ){
                        marshalRecord.startCollection();
                   }

                    for(int j=0;j<valueSize; j++){
                        marshalSingleValue(nextFragment, marshalRecord, object, listValue.get(j), session, namespaceResolver, ObjectMarshalContext.getInstance());
                    }

                    if(valueSize > 1 ){
                        marshalRecord.endCollection();
                    }
                }
            }
        } else{


       Object objectValue;
       while (cp.hasNext(iterator)) {
            objectValue = cp.next(iterator, session);
            objectValue = mapping.convertObjectValueToDataValue(objectValue, session, marshalRecord.getMarshaller());
            marshalSingleValue(xPathFragment, marshalRecord, object, objectValue, session, namespaceResolver, ObjectMarshalContext.getInstance());
        }
       }

        return true;
    }

    @Override
    public Object getContainerInstance() {
        return getContainerPolicy().containerInstance();
    }

    @Override
    public void setContainerInstance(Object object, Object containerInstance) {
        mapping.setAttributeValueInObject(object, containerInstance);
    }

    @Override
    public CoreContainerPolicy getContainerPolicy() {
        return mapping.getContainerPolicy();
    }

    @Override
    public boolean isContainerValue() {
        return true;
    }

    @Override
    public boolean getReuseContainer() {
        return mapping.getReuseContainer();    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isDefaultEmptyContainer() {
        return mapping.isDefaultEmptyContainer();
    }

    @Override
    public boolean isWrapperAllowedAsCollectionName() {
        return false;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;

    }

    @Override
    public boolean isMixedContentNodeValue() {
         return true;
    }

}
