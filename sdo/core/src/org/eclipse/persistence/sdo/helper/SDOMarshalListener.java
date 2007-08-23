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

import commonj.sdo.DataObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshalListener;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p><b>Purpose</b>: Implementation of XMLMarshalListener used when marshalling DataObjects to XML
 * <p><b>Responsibilities</b>:<ul>
 * <li> Need to do extra work to marshal org.eclipse.persistence.sdo.SDOChangeSummary objects
 * </ul>
 */
public class SDOMarshalListener implements XMLMarshalListener {
    // marshalledObject may or may not be the root object
    private Object marshalledObject;

    /** maintain narrowed context from the larger HelperContext (inside the xmlMarshaller)<br>
     * Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private XMLMarshaller xmlMarshaller;

    public SDOMarshalListener(XMLMarshaller aMarshaller) {
        xmlMarshaller = aMarshaller;
    }

    public void afterMarshal(Object obj) {
    }

    public void beforeMarshal(Object obj) {
        if (obj instanceof SDOChangeSummary) {
            SDOChangeSummary changeSummary = ((SDOChangeSummary)obj);

            //CREATED - build a list of xpaths to write to the created attribute
            //this must be done dynamically because the xpath is relative to the marshalledObject
            //so it can't be calculated until we know what object is being marshalled
            List createdSet = changeSummary.getCreated();
            List xpaths = new ArrayList(createdSet.size());
            if (changeSummary.isLoggingMapping() && (createdSet != null) && (createdSet.size() > 0)) {
                Iterator anIterator = createdSet.iterator();
                SDODataObject nextCreatedDO = null;
                while (anIterator.hasNext()) {
                    // TODO: change to ID's when available
                    // get path to the changeSummaryRoot (may not be the root marshalled object - may be internal)
                    nextCreatedDO = ((SDODataObject)anIterator.next());
                    xpaths.add(SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT//
                                +nextCreatedDO.getPathFromAncestor((SDODataObject)marshalledObject));
                }
            }
            changeSummary.setCreatedXPaths(xpaths);

            //Build xpathToCS
            String xpathMarshalledObjToCS = ((SDODataObject)changeSummary.getRootObject()).getPathFromAncestor((SDODataObject)marshalledObject);
            String xpathChangeSumProp = ((SDOProperty)((SDOType)changeSummary.getRootObject()//
            .getType()).getChangeSummaryProperty()).getXmlMapping().getField().getName();
            String xpathToCS = SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX;

            // check if the CS is at the local-cs-root or is in a child property
            if ((xpathMarshalledObjToCS != null) && !xpathMarshalledObjToCS.equals(SDOConstants.EMPTY_STRING)) {//SDO_XPATH_TO_ROOT)) {
                // CS is not on the root
                xpathToCS = xpathToCS + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT + xpathMarshalledObjToCS;
            }
            xpathToCS = xpathToCS + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT//
                         +xpathChangeSumProp + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT;

            //MODIFIED && DELETED           
            List deletedXPaths = new ArrayList();
            Document document = XMLPlatformFactory.getInstance().getXMLPlatform().createDocument();
            Element csNode = null;
            List modifiedItems = changeSummary.getModified();
            int modifiedSize = modifiedItems.size();
            List newNodes = new ArrayList(modifiedSize);
            SDODataObject nextModifiedDO = null;

            //Iterate through CS modified items
            for (int i = 0; i < modifiedSize; i++) {
                nextModifiedDO = (SDODataObject)modifiedItems.get(i);
                String sdoPrefix = ((SDOType)nextModifiedDO.getType()).getXmlDescriptor()//
                .getNonNullNamespaceResolver().resolveNamespaceURI(SDOConstants.SDO_URL);

                //List unsetPropNames = new ArrayList();
                String uri = getURI(nextModifiedDO);// TODO: see #5837243 for production fix after spec consultation for handling root property
                String qualifiedName = getQualifiedName(nextModifiedDO);// TODO: see #5837243 for production fix after spec consultation for handling root property
                String sdoRefPrefix = SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT;

                if (uri == null) {
                    csNode = document.createElement(qualifiedName);
                } else {
                    csNode = document.createElementNS(uri, qualifiedName);
                }

                //Add sdoRef attribute...all modified objects written should have this                
                csNode.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                                      SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                                      SDOConstants.CHANGESUMMARY_REF,//
                                      sdoRefPrefix + nextModifiedDO.getPathFromAncestor((SDODataObject)marshalledObject));

                List nextDOSettings = changeSummary.getOldValues(nextModifiedDO);
                DOMRecord row = new DOMRecord(csNode);

                //Iterate through SDOSettings for the current modified Object
                SDOSetting nextSetting = null;
                for (int j = 0; j < nextDOSettings.size(); j++) {
                    nextSetting = (SDOSetting)nextDOSettings.get(j);
                    if (nextSetting.isSet()) {
                        if (!nextSetting.getProperty().getType().isDataType()) {
                            if (nextSetting.getProperty().isMany()) {
                                List values = (List)nextSetting.getValue();
                                for (int k = 0; k < values.size(); k++) {
                                    doMarshal((SDOProperty)nextSetting.getProperty(), (DataObject)values.get(k),//
                                              changeSummary, csNode, nextModifiedDO, deletedXPaths, xpathToCS, sdoPrefix);
                                }
                            } else {
                                doMarshal((SDOProperty)nextSetting.getProperty(), (DataObject)nextSetting.getValue(),//
                                          changeSummary, csNode, nextModifiedDO, deletedXPaths, xpathToCS, sdoPrefix);
                            }
                        } else {
                            XMLField field = (XMLField)((SDOProperty)nextSetting.getProperty()).getXmlMapping().getField();
                            row.put(field, nextSetting.getValue());
                        }
                    }
                }

                List unsetPropNames = changeSummary.getUnsetProps(nextModifiedDO);
                if (!unsetPropNames.isEmpty()) {
                    String unsetPropsString = (String)XMLConversionManager.getDefaultXMLManager().convertObject(unsetPropNames, String.class);
                    csNode.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                                          SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                                          SDOConstants.CHANGESUMMARY_UNSET, unsetPropsString);
                }
                newNodes.add(csNode);
            }

            changeSummary.setDeletedXPaths(deletedXPaths);
            changeSummary.setModifiedDoms(newNodes);
        }
    }

    private void doMarshal(SDOProperty prop, DataObject value, SDOChangeSummary cs,//
                           Element csNode, SDODataObject modifiedObject, List deletedXPaths, String xpathToCS, String sdoPrefix) {
        if (value == null) {
            //TODO: this doesn't make sense
            return;
        }

        boolean isDeleted = false;

        //Do we need a second map or can we just check the values of the deleted map
        Object original = cs.getReverseDeletedMap().get(value);

        if ((original != null) && cs.isDeleted((DataObject)original)) {
            isDeleted = true;
        }

        XMLField xmlField = ((XMLField)prop.getXmlMapping().getField());
        String uri = xmlField.getXPathFragment().getNamespaceURI();
        String qualifiedName = xmlField.getName();

        if (isDeleted) {
            String pathToNode = ((SDODataObject)original).getPathFromAncestor(modifiedObject);
            String containerPath = null;
            containerPath = getQualifiedName(modifiedObject);
            deletedXPaths.add(xpathToCS + containerPath + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT + pathToNode);

            //TODO: do we need a new marshaller
            //TODO: need to update OX Marshaller to allow you to suppress xsi:type attribute
            XMLRoot xmlroot = new XMLRoot();
            xmlroot.setObject(value);//set the object to the deep copy            
            xmlroot.setNamespaceURI(uri);
            xmlroot.setLocalName(qualifiedName);
            //xmlroot.setSupressTypeAttribute(true);
            xmlMarshaller.marshal(xmlroot, csNode);
        } else {
            //need sdoref            
            Element modifiedElement = null;
            if (uri == null) {
                modifiedElement = csNode.getOwnerDocument().createElement(qualifiedName);
            } else {
                modifiedElement = csNode.getOwnerDocument().createElementNS(uri, qualifiedName);
            }
            
            csNode.appendChild(modifiedElement);
            modifiedElement.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                                           SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                                           SDOConstants.CHANGESUMMARY_REF,//
                                           SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT +//
                                           ((SDODataObject)original).getPathFromAncestor((SDODataObject)marshalledObject));

            //Added for bug 6346754            
            if ((((SDODataObject)original).getContainmentProperty() != null) && ((SDODataObject)original).getContainmentProperty().getType().equals(SDOConstants.SDO_DATAOBJECT)) {
                //may also need xsi:type                      
                String schemaContext = ((SDOType)value.getType()).getXmlDescriptor().getSchemaReference().getSchemaContext();
                QName schemaContextQName = ((SDOType)value.getType()).getXmlDescriptor().getSchemaReference().getSchemaContextAsQName(((SDOType)value.getType()).getXmlDescriptor().getNonNullNamespaceResolver());

                if (schemaContext != null) {
                    String typeValue = schemaContext.substring(1, schemaContext.length());
                    String schemaInstancePrefix = ((SDOType)value.getType()).getXmlDescriptor().getNonNullNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);

                    //may or may not need the xmlns declaration added.
                    String schemaContextUri = schemaContextQName.getNamespaceURI();
                    String schemaContextPrefix = ((SDOType)value.getType()).getXmlDescriptor().getNonNullNamespaceResolver().resolveNamespaceURI(schemaContextUri);
                    if (schemaContextPrefix != null) {
                        modifiedElement.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + ":" + schemaContextPrefix, schemaContextQName.getNamespaceURI());
                    }
                    modifiedElement.setAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, schemaInstancePrefix + ":" + XMLConstants.SCHEMA_TYPE_ATTRIBUTE, typeValue);
                }
            }
        }
    }

    // TODO: See bug# 5837243: we will be refactoring this function with a formal solution after spec consultation
    private String getURI(SDODataObject currentObject) {
        String uri = null;

        // check if we are at the root - no containmentProperty
        if (currentObject.getContainmentProperty() == null) {
            // get ns from type/schemaReference/schemaContext = "/ns0:PurchaseOrderType"
            // get ns:name from the ns=nsr(value==uri) name=, or 2nd entry in xmlDescriptor->table[]
            //containerPath = ((SDOType)modifiedObject.getType()).getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI(uri);
            // get uri from the type of the root object
            uri = currentObject.getType().getURI();
        } else {
            uri = ((XMLField)((SDOProperty)currentObject.getContainmentProperty())//
                .getXmlMapping().getField()).getXPathFragment().getNamespaceURI();
        }
        return uri;
    }

    // TODO: See bug# 5837243: we will be refactoring this function with a formal solution after spec consultation    
    private String getQualifiedName(SDODataObject currentObject) {
        String qualifiedName = null;

        // check if we are at the root - no containmentProperty
        if (currentObject.getContainmentProperty() == null) {
            // get ns (without name) from type/schemaReference/schemaContext = "/ns0:PurchaseOrderType"
            //qualifiedName = ((SDOType)currentObject.getType()).getXmlDescriptor().getNamespaceResolver().resolveNamespaceURI(uri);
            // property id's do not match - don't use
            //qualifiedName += ((SDOXSDHelper)currentObject.getHelperContext().getXSDHelper()).getGlobalProperty(uri, currentObject.getType().getName() , true);
            // hack: get 2nd table name
            // get ns:name from the ns=nsr(value==uri) name=, or 2nd entry in xmlDescriptor->table[]
            //TODO: Don't use lastElements
            qualifiedName = ((SDOType)currentObject.getType()).getXmlDescriptor().getTables().lastElement().getQualifiedName();
        } else {
            qualifiedName = ((SDOProperty)currentObject.getContainmentProperty()).getXmlMapping().getField().getName();
        }
        return qualifiedName;
    }

    public void setMarshalledObject(Object marshalledObject) {
        this.marshalledObject = marshalledObject;
    }

    public Object getMarshalledObject() {
        return marshalledObject;
    }
}