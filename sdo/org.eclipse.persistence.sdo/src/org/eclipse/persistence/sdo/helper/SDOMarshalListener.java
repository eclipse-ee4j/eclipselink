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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/ 
package org.eclipse.persistence.sdo.helper;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshalListener;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.MarshalRecord;
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
    private QName marshalledObjectRootQName;
    private MarshalRecord rootMarshalRecord;
    private SDOTypeHelper typeHelper;

    /** maintain narrowed context from the larger HelperContext (inside the xmlMarshaller)<br>
     * Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private XMLMarshaller xmlMarshaller;

    public SDOMarshalListener(XMLMarshaller aMarshaller, SDOTypeHelper aTypeHelper) {
        xmlMarshaller = aMarshaller;
        typeHelper = aTypeHelper;
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

            String rootElementName = this.marshalledObjectRootQName.getLocalPart();
            String rootNamespaceUri = this.marshalledObjectRootQName.getNamespaceURI();
            if(rootNamespaceUri != null && !rootNamespaceUri.equals(XMLConstants.EMPTY_STRING)) {            	
                NamespaceResolver resolver = getRootMarshalRecord().getNamespaceResolver();
                if(resolver != null) {
                    String prefix = resolver.resolveNamespaceURI(this.marshalledObjectRootQName.getNamespaceURI());
                    if(prefix != null) {
                        rootElementName = prefix + XMLConstants.COLON + rootElementName;                        
                    }
                }
            }
            if ((createdSet != null) && (createdSet.size() > 0)) {
                Iterator anIterator = createdSet.iterator();
                SDODataObject nextCreatedDO = null;
                while (anIterator.hasNext()) {
                    // get path to the changeSummaryRoot (may not be the root marshalled object - may be internal)
                    nextCreatedDO = ((SDODataObject)anIterator.next());
                    String nextPath = getPathFromAncestor(nextCreatedDO, (SDODataObject)marshalledObject, changeSummary);
                    //Add sdoRef attribute...all modified objects written should have this                
                    if(nextPath == SDOConstants.EMPTY_STRING) {
                        //if this is the root, just put the root element
                        xpaths.add(SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + 
                                SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT +//
                                rootElementName);
                        
                    } else {
                        xpaths.add(SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + 
                                SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT +//
                                rootElementName + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT + nextPath);
                    }
                }
            }
            changeSummary.setCreatedXPaths(xpaths);

            //Build xpathToCS            
            String xpathMarshalledObjToCS = getPathFromAncestor(changeSummary.getRootObject(), (SDODataObject)marshalledObject, changeSummary);
            String xpathChangeSumProp = getXPathForProperty(changeSummary.getRootObject().getType().getChangeSummaryProperty());

            String xpathToCS = SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT + rootElementName;

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
                String sdoPrefix = typeHelper.getPrefix(SDOConstants.SDO_URL);

                //List unsetPropNames = new ArrayList();
                String uri = getURI(nextModifiedDO);
                String qualifiedName = getQualifiedName(nextModifiedDO);
                String sdoRefPrefix = SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT;

                if (uri == null) {
                    csNode = document.createElement(qualifiedName);
                } else {
                    csNode = document.createElementNS(uri, qualifiedName);
                }

                String nextPath = getPathFromAncestor(nextModifiedDO, (SDODataObject)marshalledObject, changeSummary);
                //Add sdoRef attribute...all modified objects written should have this                
                if(nextPath == SDOConstants.EMPTY_STRING) {
                    //if this is the root, just put the root element
                    csNode.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                            SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                            SDOConstants.CHANGESUMMARY_REF,//                                      
                            sdoRefPrefix + rootElementName);
                    
                } else {
                    csNode.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                                      SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                                      SDOConstants.CHANGESUMMARY_REF,//                                      
                                      sdoRefPrefix + rootElementName + "/" + nextPath);
                }

                //Bug6346754 Add all namespaces if they are not yet declared above.                
                Vector namespaces = nextModifiedDO.getType().getXmlDescriptor().getNonNullNamespaceResolver().getNamespaces();
                for (int j = 0; j < namespaces.size(); j++) {
                    Namespace next = (Namespace)namespaces.get(j);
                    if (declareNamespace(next.getNamespaceURI(), next.getPrefix(), changeSummary.getRootObject())) {
                        csNode.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + next.getPrefix(), next.getNamespaceURI());
                    }
                }

                List nextDOSettings = changeSummary.getOldValues(nextModifiedDO);
                DOMRecord row = new DOMRecord(csNode);
                
                Session session = ((SDOXMLHelper)typeHelper.getHelperContext().getXMLHelper()).getXmlContext().getSession(0);
                row.setSession((AbstractSession) session);

                //Iterate through SDOSettings for the current modified Object
                SDOSetting nextSetting = null;
                for (int j = 0; j < nextDOSettings.size(); j++) {
                    nextSetting = (SDOSetting)nextDOSettings.get(j);
                    if (nextSetting.isSet()) {
                        if (!nextSetting.getProperty().getType().isDataType()) {
                            if (nextSetting.getProperty().isMany()) {
                                List values = (List)nextSetting.getValue();
                                for (int k = 0; k < values.size(); k++) {
                                    doMarshal(nextSetting.getProperty(), (DataObject)values.get(k),//
                                              changeSummary, csNode, nextModifiedDO, deletedXPaths, xpathToCS, sdoPrefix, rootElementName);
                                }
                            } else {
                                doMarshal(nextSetting.getProperty(), (DataObject)nextSetting.getValue(),//
                                          changeSummary, csNode, nextModifiedDO, deletedXPaths, xpathToCS, sdoPrefix, rootElementName);
                            }
                        } else {
                            //This writes out simple values                            
                            Object value = nextSetting.getValue();

                            if (value == null) {
                                //Marshal out xsi:nil=true   
                                marshalNilAttribute(nextSetting.getProperty(), row);                            
                            } else {
                                String xPath = getXPathForProperty(nextSetting.getProperty());
                                XMLField field = new XMLField(xPath);
                                field.setNamespaceResolver(typeHelper.getNamespaceResolver());
                                row.put(field, value);
                            }
                        }
                    }
                }

                List unsetPropNames = changeSummary.getUnsetProps(nextModifiedDO);
                if (!unsetPropNames.isEmpty()) {
                    XMLConversionManager xmlConversionManager = ((SDOXMLHelper)typeHelper.getHelperContext().getXMLHelper()).getXmlConversionManager();
                    String unsetPropsString = (String)xmlConversionManager.convertObject(unsetPropNames, String.class);
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
                           Element csNode, SDODataObject modifiedObject, List deletedXPaths, String xpathToCS, String sdoPrefix, String rootElementName) {
        if (value == null) {
            //Marshal out xsi:nil=true   
            DOMRecord row = new DOMRecord(csNode);
            Session session = ((SDOXMLHelper)typeHelper.getHelperContext().getXMLHelper()).getXmlContext().getSession(0);
            row.setSession((AbstractSession) session);
            marshalNilAttribute(prop, row);                            
            return;
        }

        boolean isDeleted = false;

        //Do we need a second map or can we just check the values of the deleted map
        Object original = cs.getReverseDeletedMap().get(value);

        if ((original != null) && cs.isDeleted((DataObject)original)) {
            isDeleted = true;
        }

        String qualifiedName = getXPathForProperty(prop);
        String uri = null;
        if (prop.isOpenContent()) {
            uri = prop.getUri();
        } else {
            uri = ((XMLField)prop.getXmlMapping().getField()).getXPathFragment().getNamespaceURI();
        }

        if (isDeleted) {
            String pathToNode = getPathFromAncestor(((SDODataObject)original), modifiedObject, cs);
            String containerPath = null;
            containerPath = getQualifiedName(modifiedObject);
            deletedXPaths.add(xpathToCS + containerPath + SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT + pathToNode);

            XMLRoot xmlroot = new XMLRoot();
            xmlroot.setObject(value);//set the object to the deep copy            
            xmlroot.setNamespaceURI(uri);
            xmlroot.setLocalName(qualifiedName);
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
            String nextPath = getPathFromAncestor((SDODataObject)original, (SDODataObject)marshalledObject, cs);
            //Add sdoRef attribute...all modified objects written should have this                
            if(nextPath == SDOConstants.EMPTY_STRING) {
                //if this is the root, just put the root element
                modifiedElement.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                        SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                        SDOConstants.CHANGESUMMARY_REF,//                                
                        SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + 
                        SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT +//
                        rootElementName);
                
            } else {
                modifiedElement.setAttributeNS(SDOConstants.SDO_URL, sdoPrefix +//
                                  SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT +//
                                  SDOConstants.CHANGESUMMARY_REF,//
                                  SDOConstants.SDO_CHANGESUMMARY_REF_PATH_PREFIX + 
                                  SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT +//
                                  rootElementName + "/" + nextPath);
            }
            
            //Added for bug 6346754            
            if ((((SDODataObject)original).getContainmentProperty() != null) && ((SDODataObject)original).getContainmentProperty().getType().isDataObjectType()) {
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
                        modifiedElement.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + schemaContextPrefix, schemaContextQName.getNamespaceURI());
                    }
                    modifiedElement.setAttributeNS(XMLConstants.SCHEMA_INSTANCE_URL, schemaInstancePrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE, typeValue);
                }
            }
        }
    }
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
            uri = getUriForProperty(currentObject);
        }
        return uri;
    }

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
            qualifiedName = currentObject.getType().getXmlDescriptor().getDefaultRootElement();
        } else {
            qualifiedName = getXPathForProperty(currentObject.getContainmentProperty());
        }
        return qualifiedName;
    }

    public void setMarshalledObject(Object marshalledObject) {
        this.marshalledObject = marshalledObject;
    }

    public Object getMarshalledObject() {
        return marshalledObject;
    }

    public void setMarshalledObjectRootQName(QName rootQName) {
        this.marshalledObjectRootQName = rootQName;
    }
    
    public QName getMarshalledObjectRootQName() {
        return this.marshalledObjectRootQName;
    }
    
    public void setRootMarshalRecord(MarshalRecord rootRecord) {
        this.rootMarshalRecord = rootRecord;
    }
    
    public MarshalRecord getRootMarshalRecord() {
        return this.rootMarshalRecord;
    }
    
    private boolean declareNamespace(String uri, String prefix, DataObject theDataObject) {
        while (theDataObject != null) {
            NamespaceResolver nr = ((SDOType)theDataObject.getType()).getXmlDescriptor().getNonNullNamespaceResolver();
            String resolvedPrefix = nr.resolveNamespaceURI(uri);
            if ((resolvedPrefix != null) && !resolvedPrefix.equals("") && resolvedPrefix.equals(prefix)) {
                return false;
            }
            theDataObject = theDataObject.getContainer();
        }
        return true;
    }

    private String getUriForProperty(SDODataObject currentObject) {
        SDOProperty prop = currentObject.getContainmentProperty();
        if (prop.getXmlMapping() != null) {
            return ((XMLField)prop.getXmlMapping().getField()).getXPathFragment().getNamespaceURI();
        } else {
            return prop.getUri();
        }
    }

    private String getXPathForProperty(SDOProperty prop) {
        return getXPathForProperty(prop, false);
    }

    private String getXPathForProperty(SDOProperty prop, boolean removeText) {
        if ((prop).getXmlMapping() != null) {
            String xpath = prop.getXmlMapping().getField().getName();
            if (removeText && xpath.endsWith("/text()")) {
                xpath = xpath.substring(0, xpath.length() - 7);
            }
            return xpath;
        } else {
            String name = prop.getName();
            if (prop.isOpenContent()) {
                String uri = prop.getUri();
                if (uri != null) {
                    String prefix = typeHelper.getNamespaceResolver().resolveNamespaceURI(uri);

                    if ((prefix != null) && !prefix.equals(SDOConstants.EMPTY_STRING)) {
                        return prefix + XMLConstants.COLON + name;
                    }
                }
            }
            return name;
        }
    }

    /**
    * INTERNAL:
    * Return the XPath or SDO path from the anObject to the current internal node
    *
    * Prereq: We know that the targetObject will always have a parent as called
    * from getPath()
    *   We require a ChangeSummary object for when there are deleted
    *   objects in the path
    *
    * Matching conditions:
    *   Iterate up the tree
    *   return a non-null string for the XPath when we reach the target node
    *
    * Function is partially based on SDOCopyHelper.copy(DataObject dataObject)
    * Performance: This function is O(log n) where n=# of children in the tree
    *
    * @param currentPath
    * @param targetObject
    * @param currentObject
    * @param aSeparator (XPath separator is written only between elements - not for the first call)
    * @return String (representing the XPath)
    */
    private String getPathFromAncestorPrivate(SDOChangeSummary aChangeSummary,//
                                              String currentPath,//
                                              SDODataObject targetDO,//
                                              SDODataObject currentObject,//
                                              String aSeparator) {
        if ((currentObject == null) || (targetDO == null) || (aChangeSummary == null)) {
            return currentPath;
        }

        // Base Case: check we are at the target object first
        if (currentObject == targetDO) {
            // check for indexed property if root is a ListWrapper
            return currentPath;
        }

        // Recursive Case: O(log(n)) recursive calls, 1 for each tree level
        // get parent property based on parent property name in target, property will always be set
        // check containment for cases where we are searching for a sibling
        SDOProperty parentContainmentProperty;
        Object parent = null;

        // for already deleted dataobjects  - isDeleted=false, changeSummary= null - use oldContainer        
        if (null == currentObject.getContainer()) {
            parent = aChangeSummary.getOldContainer(currentObject);
            parentContainmentProperty = aChangeSummary.getOldContainmentProperty(currentObject);
            // handle (at root) case for non-deleted objects for the cases
            // case: ancestor not found
            // case: ancestor is actually sibling
            if ((null == parent) || (null == parentContainmentProperty)) {
                return SDOConstants.SDO_XPATH_INVALID_PATH;
            }
        } else {
            // normal non-deleted non-changeSummary case
            parent = currentObject.getContainer();
            parentContainmentProperty = currentObject.getContainmentProperty();
        }

        // get XPath using SDO path - block                                                
        String parentContainmentPropertyXPath = getXPathForProperty(parentContainmentProperty);

        // Handle ListWrapper contained DataObjects
        if (parentContainmentProperty.isMany()) {
            int index = (((SDODataObject)parent).getList(parentContainmentProperty)).indexOf(currentObject);

            if (index < 0) {

                /*
                * The current object has been deleted and was part of a ListWrapper (isMany=true)
                * Get the parent of this indexed list item and check the oldSetting (List) for the
                * original position of the indexed (Deleted) object
                */

                // get the list containing the old value of the item                
                ChangeSummary.Setting anOldSetting = aChangeSummary.getOldValue((DataObject)parent, parentContainmentProperty);
                if (anOldSetting != null) {
                    // get index directly from oldSettings based on current object - where parent was not deleted
                    List aDeletedParent = (List)anOldSetting.getValue();

                    // bug# 5587042: we will assume that index is never < 0 and remove handling code for this case where we lookup anOldSetting directly instead of via the deepCopies map                    
                    index = aDeletedParent.indexOf(aChangeSummary.getDeepCopies().get(currentObject));
                } else {
                    // bug# 5587042: we will assume that oldSetting is never null and remove handling code for this case where we would hardcode to list.size()
                }

                // see: testGetXPathFromAncestorDeletedFromChildToAncestorInsideListWrapperLoggingOn
            }
            currentPath = parentContainmentPropertyXPath +// 
                          SDOConstants.SDO_XPATH_LIST_INDEX_OPEN_BRACKET +// 
                          (1 + index) +// [indexes] start at 1
                          SDOConstants.SDO_XPATH_LIST_INDEX_CLOSE_BRACKET +//
                          aSeparator +//
                          currentPath;
        } else {
            currentPath = parentContainmentPropertyXPath +//
                          aSeparator +//
                          currentPath;
        }

        // recursive call
        return getPathFromAncestorPrivate(aChangeSummary,//
                                          currentPath,//
                                          targetDO,//
                                          (SDODataObject)parent,//
                                          SDOConstants.SDO_XPATH_SEPARATOR_FRAGMENT);// we pass an empty separator so we have \a\b and not a\b\                                          
    }

    /**
     * INTERNAL:
     * Get path for non-deleted DataObjects<br>
     * ChangeSummary is not required and is set to null.<br>
     * Assumptions:<br>
     *     target node is an ancestor of the source (this)
     * @param sourceDO
     * @param targetDO
     * @return String xpath
     */
    public String getPathFromAncestor(SDODataObject sourceDO, SDODataObject targetDO, SDOChangeSummary cs) {
        // Implementors: SDOMarshalListener
        // default to no changeSummary and xpath format

        /*
        * Algorithm:
        *   (1) Intact (non-deleted) objects:
        *     - recursively iterate up the container of each DataObject, recording property names as we go
        *   (2) Deleted objects:
        *     - use the changeSummary to get the deleted object with oldContainer state
        *     - recursively iterate up the oldContainer as in (1)
        *    Issues:
        *     - a deleted indexed object inside a ListWrapper will not retain its original index
        */

        // Base Case: The internal node is actually the root
        // Base Case: The source and target objects are equal
        // checking if this and target are equal will handle both cases above        
        if (sourceDO == targetDO) {
            // return "" empty string and handle at implementor
            return SDOConstants.EMPTY_STRING;
        } else {
            // Recursive Case: call private recursive reverse O(logn) traversal
            // function on current object            
            return getPathFromAncestorPrivate(cs,//
                                              SDOConstants.EMPTY_STRING,//
                                              targetDO,//
                                              sourceDO,//
                                              SDOConstants.EMPTY_STRING// we pass an empty separator so we have \a\b and not a\b\
            );
        }
    }

    private void marshalNilAttribute(SDOProperty property, DOMRecord row) {
        //Marshal out xsi:nil=true   
        String xsiPrefix = typeHelper.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        if ((xsiPrefix == null) || xsiPrefix.equals(SDOConstants.EMPTY_STRING)) {
            xsiPrefix = typeHelper.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
            typeHelper.getNamespaceResolver().put(xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        }
        String xPath = getXPathForProperty(property, true);
        xPath = xPath + "/@" + xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_NIL_ATTRIBUTE;

        XMLField field = new XMLField(xPath);
        field.setNamespaceResolver(typeHelper.getNamespaceResolver());
        row.put(field, XMLConstants.BOOLEAN_STRING_TRUE);
    }
}
