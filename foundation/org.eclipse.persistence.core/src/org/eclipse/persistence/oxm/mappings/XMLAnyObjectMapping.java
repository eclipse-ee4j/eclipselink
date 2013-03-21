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
package org.eclipse.persistence.oxm.mappings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.internal.descriptors.DescriptorIterator;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.XMLObjectBuilder;
import org.eclipse.persistence.internal.oxm.XPathEngine;
import org.eclipse.persistence.internal.oxm.XPathFragment;
import org.eclipse.persistence.internal.oxm.mappings.AnyObjectMapping;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.ChangeRecord;
import org.eclipse.persistence.internal.sessions.MergeManager;
import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverter;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.remote.DistributedSession;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * <p>Any object XML mappings map an attribute that contains a single object to  an XML element.
 * The referenced object may be of any type (including String), and does not need not be related
 * to any other particular type through inheritance or a common interface.  The corresponding
 * object attribute value should be generic enough for all possible application values.  Note that
 * each of the referenced objects (except String) must specify a default root element on their
 * descriptor.
 *
 * <p><b>Any object XML mappings are useful with the following XML schema constructs</b>:<ul>
 * <li> any </li>
 * <li> choice </li>
 * <li> substitution groups </li>
 * </ul>
 *
 * <p><b>Setting the XPath</b>: TopLink XML mappings make use of XPath statements to find the relevant
 * data in an XML document.  The XPath statement is relative to the context node specified in the descriptor.
 * The XPath may contain node type, path, and positional information.  The XPath is specified on the
 * mapping using the <code>setXPath</code> method.  Note that for XML Any Object Mappings the XPath is
 * optional.
 *
 * <p>The following XPath statements may be used to specify the location of XML data relating to an object's
 * name attribute:
 *
 * <p><table border="1">
 * <tr>
 * <th id="c1" align="left">XPath</th>
 * <th id="c2" align="left">Description</th>
 * </tr>
 * <tr>
 * <td headers="c1">contact-method</td>
 * <td headers="c2">The name information is stored in the contact-method element.</td>
 * </tr>
 * <tr>
 * <td headers="c1" nowrap="true">contact-method/info</td>
 * <td headers="c2">The XPath statement may be used to specify any valid path.</td>
 * </tr>
 * <tr>
 * <td headers="c1">contact-method[2]</td>
 * <td headers="c2">The XPath statement may contain positional information.  In this case the contact
 * information is stored in the second occurrence of the contact-method element.</td>
 * </tr>
 * </table>
 *
 * <p><b>Mapping an element of type xs:anyType as an Any Object Mapping</b>:
 * <!--
 * <?xml version="1.0" encoding="UTF-8"?>
 * <xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema">
 *     <xsd:element name="customer" type="customer-type"/>
 *     <xsd:complexType name="customer-type">
 *         <xsd:sequence>
 *             <xsd:element name="contact-method" type="xsd:anyType"/>
 *         </xsd:sequence>
 *     </xsd:complexType>
 *     <xsd:element name="address">
 *         <xsd:complexType>
 *             <xsd:sequence>
 *                 <xsd:element name="street" type="xsd:string"/>
 *                 <xsd:element name="city" type="xsd:string"/>
 *             </xsd:sequence>
 *         </xsd:complexType>
 *     </xsd:element>
 *     <xsd:element name="phone-number" type="xsd:string"/>
 * </xsd:schema>
 * -->
 *
 * <p><em>XML Schema</em><br>
 * <code>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
 * &lt;xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="customer" type="customer-type"/&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:complexType name="customer-type"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="contact-method" type="xsd:anyType"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="address"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:complexType&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="street" type="xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;xsd:element name="city" type="xsd:string"/&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:sequence&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/xsd:complexType&gt;<br>
 * &nbsp;&nbsp;&lt;/xsd:element&gt;<br>
 * &nbsp;&nbsp;&lt;xsd:element name="phone-number" type="xsd:string"/&gt;<br>
 * &lt;/xsd:schema&gt;<br>
 * </code>
 *
 * <p><em>Code Sample</em><br>
 * <code>
 * XMLAnyObjectMapping contactMethodMapping = new XMLAnyObjectMapping();<br>
 * contactMethodMapping.setAttributeName("contactMethod");<br>
 * contactMethodMapping.setXPath("contact-method");<br>
 * </code>
 *
 * <p><b>More Information</b>: For more information about using the XML Any Object Mapping, see the
 * "Understanding XML Mappings" chapter of the Oracle TopLink Developer's Guide.
 *
 * @since Oracle TopLink 10<i>g</i> Release 2 (10.1.3)
 */
public class XMLAnyObjectMapping extends XMLAbstractAnyMapping implements XMLMapping, AnyObjectMapping<AbstractSession, AttributeAccessor, ContainerPolicy, XMLConverter, ClassDescriptor, DatabaseField, XMLMarshaller, Session, UnmarshalKeepAsElementPolicy, XMLUnmarshaller, XMLRecord> {
    private XMLField field;
    private boolean useXMLRoot;
    private boolean areOtherMappingInThisContext = true;
    private XMLConverter converter;
    private boolean isMixedContent = true;

    public XMLAnyObjectMapping() {
        useXMLRoot = false;
    }

    /**
    * INTERNAL:
    * Clone the attribute from the clone and assign it to the backup.
    */
    public void buildBackupClone(Object clone, Object backup, UnitOfWorkImpl unitOfWork) {
        throw DescriptorException.invalidMappingOperation(this, "buildBackupClone");
    }

    /**
    * INTERNAL:
    * Clone the attribute from the original and assign it to the clone.
    */
    @Override
    public void buildClone(Object original, CacheKey cacheKey, Object clone, Integer refreshCascade, AbstractSession cloningSession) {
        throw DescriptorException.invalidMappingOperation(this, "buildClone");
    }

    public void buildCloneFromRow(AbstractRecord databaseRow, JoinedAttributeManager joinManager, Object clone, CacheKey sharedCacheKey, ObjectBuildingQuery sourceQuery, UnitOfWorkImpl unitOfWork, AbstractSession executionSession) {
        throw DescriptorException.invalidMappingOperation(this, "buildCloneFromRow");
    }

    /**
     * INTERNAL:
     * Cascade perform delete through mappings that require the cascade
     */
    public void cascadePerformRemoveIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //objects referenced by this mapping are not registered as they have
        // no identity, this is a no-op.
    }

    /**
     * INTERNAL:
     * Cascade registerNew for Create through mappings that require the cascade
     */
    public void cascadeRegisterNewIfRequired(Object object, UnitOfWorkImpl uow, Map visitedObjects) {
        //Our current XML support does not make use of the UNitOfWork.
    }

    public Object clone() {
        // Bug 3037701 - clone the AttributeAccessor
        XMLAnyCollectionMapping mapping = null;
        mapping = (XMLAnyCollectionMapping) super.clone();
        mapping.setContainerPolicy(this.getContainerPolicy());
        mapping.setField(this.getField());
        return mapping;
    }

    /**
    * INTERNAL:
    * This method was created in VisualAge.
    * @return prototype.changeset.ChangeRecord
    */
    public ChangeRecord compareForChange(Object clone, Object backup, ObjectChangeSet owner, AbstractSession session) {
        throw DescriptorException.invalidMappingOperation(this, "compareForChange");
    }

    /**
    * INTERNAL:
    * Compare the attributes belonging to this mapping for the objects.
    */
    public boolean compareObjects(Object firstObject, Object secondObject, AbstractSession session) {
        throw DescriptorException.invalidMappingOperation(this, "compareObjects");
    }

    /**
    * INTERNAL:
    * An object has been serialized from the server to the client.
    * Replace the transient attributes of the remote value holders
    * with client-side objects.
    */
    public void fixObjectReferences(Object object, Map objectDescriptors, Map processedObjects, ObjectLevelReadQuery query, DistributedSession session) {
        throw DescriptorException.invalidMappingOperation(this, "fixObjectReferences");
    }

    public DatabaseField getField() {
        return field;
    }

    /**
    * INTERNAL:
    * Iterate on the appropriate attribute value.
    */
    public void iterate(DescriptorIterator iterator) {
        throw DescriptorException.invalidMappingOperation(this, "iterate");
    }

    public void setXPath(String xpath) {
        this.field = new XMLField(xpath);
    }

    /**
    * INTERNAL:
    * Merge changes from the source to the target object.
    */
    @Override
    public void mergeChangesIntoObject(Object target, ChangeRecord changeRecord, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        throw DescriptorException.invalidMappingOperation(this, "mergeChangesIntoObject");
    }

    /**
    * INTERNAL:
    * Merge changes from the source to the target object.
    */
    @Override
    public void mergeIntoObject(Object target, boolean isTargetUninitialized, Object source, MergeManager mergeManager, AbstractSession targetSession) {
        throw DescriptorException.invalidMappingOperation(this, "mergeIntoObject");
    }

    public void setField(DatabaseField field) {
        this.field = (XMLField) field;
    }

    public Object valueFromRow(AbstractRecord row, JoinedAttributeManager joinManager, ObjectBuildingQuery sourceQuery, CacheKey cacheKey, AbstractSession executionSession, boolean isTargetProtected, Boolean[] wasCacheUsed) throws DatabaseException {
        XMLRecord record = (XMLRecord) row;

        if (getField() != null) {
            //Get the nested row represented by this field to build the Object from
            Object nested = record.get(getField());
            if (nested instanceof Vector) {
                nested = ((Vector) nested).firstElement();
            }
            if (!(nested instanceof XMLRecord)) {
                return null;
            }
            record = (XMLRecord) nested;
        }
        return buildObjectValuesFromDOMRecord((DOMRecord) record, executionSession, sourceQuery, joinManager);
    }

    private Object buildObjectValuesFromDOMRecord(DOMRecord record, AbstractSession session, ObjectBuildingQuery query, JoinedAttributeManager joinManager) {
        //This DOMRecord represents the root node of the AnyType instance
        //Grab ALL children to populate the collection.
        Node root = record.getDOM();
        NodeList nodes = root.getChildNodes();
        Collection unmappedChildren = getUnmappedChildNodes(nodes);
        Iterator iter = unmappedChildren.iterator();
        int i = 0;
        int length = unmappedChildren.size();
        while (iter.hasNext()) {
            Object objectValue = null;
            org.w3c.dom.Node next = (Node) iter.next();
            if (next.getNodeType() == Node.TEXT_NODE) {
                if ((i == (length - 1)) || (next.getNodeValue().trim().length() > 0)) {
                    objectValue = next.getNodeValue();
                    objectValue = convertDataValueToObjectValue(objectValue, session, record.getUnmarshaller());
                    return objectValue;
                }
            } else if (next.getNodeType() == Node.ELEMENT_NODE) {
                ClassDescriptor referenceDescriptor = null;

                //In this case it must be an element so we need to dig up the descriptor
                //make a nested record and build an object from it.                
                DOMRecord nestedRecord = (DOMRecord) record.buildNestedRow((Element) next);

                if (!useXMLRoot) {
                    return buildObjectForNonXMLRoot(getDescriptor(nestedRecord, session, null), getConverter(), query, record, nestedRecord, joinManager, session, next, null, null);
                }
                // need to wrap the object in an XMLRoot
                String schemaType = ((Element) next).getAttributeNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
                QName schemaTypeQName = null;
                XPathFragment frag = new XPathFragment();
                if ((null != schemaType) && (schemaType.length() > 0)) {
                    frag.setXPath(schemaType);
                    if (frag.hasNamespace()) {
                        String prefix = frag.getPrefix();
                        XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
                        String url = xmlPlatform.resolveNamespacePrefix(next, prefix);
                        frag.setNamespaceURI(url);
                        schemaTypeQName = new QName(url, frag.getLocalName());
                    }
                    XMLContext xmlContext = nestedRecord.getUnmarshaller().getXMLContext();
                    referenceDescriptor = xmlContext.getDescriptorByGlobalType(frag);
                }
                if (referenceDescriptor == null) {
                    try {
                        referenceDescriptor = getDescriptor(nestedRecord, session, new QName(nestedRecord.getNamespaceURI(), nestedRecord.getLocalName()));
                    } catch (XMLMarshalException e) {
                        referenceDescriptor = null;
                    }
                }
                // if KEEP_ALL_AS_ELEMENT is set, or we don't have a descriptor and KEEP_UNKNOWN_AS_ELEMENT
                // is set, then we want to return either an Element or an XMLRoot wrapping an Element
                if (getKeepAsElementPolicy() == UnmarshalKeepAsElementPolicy.KEEP_ALL_AS_ELEMENT || (referenceDescriptor == null && getKeepAsElementPolicy() == UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT)) {
                    Object objVal = buildObjectNoReferenceDescriptor(nestedRecord, getConverter(), session, next, null, null);
                    // wrap the object in an XMLRoot
                    // if we know the descriptor use it to wrap the Element in an XMLRoot (if necessary)
                    if (referenceDescriptor != null) {
                        return ((XMLDescriptor) referenceDescriptor).wrapObjectInXMLRoot(objVal, next.getNamespaceURI(), next.getLocalName(), next.getPrefix(), false, record.isNamespaceAware(),record.getUnmarshaller());
                    }
                    // no descriptor, so manually build the XMLRoot
                    return buildXMLRoot(next, objVal);
                }
                // if we have a descriptor use it to build the object and wrap it in an XMLRoot
                if (referenceDescriptor != null) {
                    return buildObjectAndWrapInXMLRoot(referenceDescriptor, getConverter(), query, record, nestedRecord, joinManager, session, next, null, null);
                }
                // no descriptor, but could be TEXT to wrap and return
                XMLRoot rootValue;
                if ((rootValue = buildXMLRootForText(next, schemaTypeQName, getConverter(), session, record)) != null) {
                    return rootValue;
                }
            }
            i++;
        }
        return null;
    }

    @Override
    public void writeFromObjectIntoRow(Object object, AbstractRecord row, AbstractSession session, WriteType writeType) throws DescriptorException {
        if (this.isReadOnly()) {
            return;
        }
        Object attributeValue = this.getAttributeValueFromObject(object);
        if (attributeValue == null) {
            return;
        }
        writeSingleValue(attributeValue, object, (XMLRecord) row, session);
    }

    public void writeSingleValue(Object value, Object parent, XMLRecord row, AbstractSession session) {
        DOMRecord record = (DOMRecord) row;
        Node root = record.getDOM();
        Object objectValue = value;
        objectValue = convertObjectValueToDataValue(objectValue, session, row.getMarshaller());
        if (field != null) {
            root = XPathEngine.getInstance().create((XMLField) getField(), root, session);
        }
        org.w3c.dom.Document doc = record.getDocument();

        XMLField xmlRootField = null;
        boolean wasXMLRoot = false;
        Object originalObject = objectValue;
        Node toReplace = getNodeToReplace(root);
        if (usesXMLRoot() && (objectValue instanceof XMLRoot)) {
            xmlRootField = new XMLField();
            wasXMLRoot = true;
            XPathFragment frag = new XPathFragment();
            if ((((XMLRoot) objectValue)).getNamespaceURI() != null) {
                frag.setNamespaceURI(((XMLRoot) objectValue).getNamespaceURI());
            } 
            frag.setXPath(((XMLRoot) objectValue).getLocalName());

            xmlRootField.setXPathFragment(frag);
            xmlRootField.setNamespaceResolver(row.getNamespaceResolver());

            objectValue = ((XMLRoot) objectValue).getObject();
        }
        if (objectValue instanceof String) {
            writeSimpleValue(xmlRootField, record, session, originalObject, objectValue, root, toReplace, wasXMLRoot);
        } else if (objectValue instanceof org.w3c.dom.Node) {
            Node importedCopy = doc.importNode((Node) objectValue, true);
            root.appendChild(importedCopy);
        } else {
            XMLDescriptor referenceDescriptor = (XMLDescriptor) session.getDescriptor(objectValue.getClass());
            if (referenceDescriptor == null) {
                writeSimpleValue(xmlRootField, record, session, originalObject, objectValue, root, toReplace, wasXMLRoot);
                return;
            }
            if (wasXMLRoot) {
                if (((XMLRoot) originalObject).getNamespaceURI() != null) {
                    String prefix = referenceDescriptor.getNonNullNamespaceResolver().resolveNamespaceURI(((XMLRoot) originalObject).getNamespaceURI());
                    if ((prefix == null) || prefix.length() == 0) {
                        prefix = row.getNamespaceResolver().resolveNamespaceURI(((XMLRoot) originalObject).getNamespaceURI());
                    }
                    if ((prefix == null) || prefix.length() == 0) {
                        xmlRootField.getXPathFragment().setGeneratedPrefix(true);
                        prefix = row.getNamespaceResolver().generatePrefix();
                    }
                    xmlRootField.getXPathFragment().setXPath(prefix + XMLConstants.COLON + ((XMLRoot) originalObject).getLocalName());
                }
            }

            DOMRecord nestedRecord = (DOMRecord) buildCompositeRow(objectValue, session, referenceDescriptor, row, xmlRootField, originalObject, wasXMLRoot);

            if ((nestedRecord != null) && (toReplace != null)) {
                if (nestedRecord.getDOM() != toReplace) {
                    root.replaceChild(nestedRecord.getDOM(), toReplace);
                }
            } else if (nestedRecord != null) {
                root.appendChild(nestedRecord.getDOM());
            } else if (toReplace != null) {
                root.removeChild(toReplace);
            }
        }
    }

    protected AbstractRecord buildCompositeRow(Object attributeValue, AbstractSession session, XMLDescriptor referenceDescriptor, AbstractRecord parentRow, DatabaseField field, Object originalObject, boolean wasXMLRoot) {
        if ((field == null) && (referenceDescriptor != null) && (referenceDescriptor.getDefaultRootElement() != null)) {
            field = referenceDescriptor.buildField(referenceDescriptor.getDefaultRootElement());
        }

        if ((field != null) && (referenceDescriptor != null)) {
            ((XMLRecord) parentRow).setLeafElementType(referenceDescriptor.getDefaultRootElementType());
            XMLObjectBuilder objectBuilder = (XMLObjectBuilder) referenceDescriptor.getObjectBuilder();
            
            XMLRecord child = (XMLRecord) objectBuilder.createRecordFor(attributeValue, (XMLField) field, (XMLRecord) parentRow, this);
            child.setNamespaceResolver(((XMLRecord) parentRow).getNamespaceResolver());
            objectBuilder.buildIntoNestedRow(child, originalObject, attributeValue, session, referenceDescriptor, (XMLField) field, wasXMLRoot);
            return child;
        }
        return null;
    }

    public void initialize(AbstractSession session) throws DescriptorException {
        if (getField() != null) {
            setField(getDescriptor().buildField(getField()));
        }
        if (null != converter) {
            converter.initialize(this, session);
        }
    }

    public boolean isXMLMapping() {
        return true;
    }

    public Vector getFields() {
        return this.collectFields();
    }

    public void setUseXMLRoot(boolean useXMLRoot) {
        this.useXMLRoot = useXMLRoot;
    }

    public boolean usesXMLRoot() {
        return useXMLRoot;
    }


    private ArrayList getUnmappedChildNodes(NodeList nodes) {
        ArrayList unmappedNodes = new ArrayList();
        int length = nodes.getLength();
        for (int i = 0; i < length; i++) {
            Node next = nodes.item(i);
            if (isUnmappedContent(next)) {
                unmappedNodes.add(next);
            }
        }
        return unmappedNodes;
    }

    private XPathFragment getFragmentToCompare(XMLField field, XMLField context) {
        if (field == null) {
            return null;
        }
        if (context == null) {
            return field.getXPathFragment();
        }
        XPathFragment fieldFrag = field.getXPathFragment();
        XPathFragment contextFrag = context.getXPathFragment();

        while ((fieldFrag != null) && (contextFrag != null)) {
            if (fieldFrag.equals(contextFrag)) {
                if (contextFrag.getNextFragment() == null) {
                    return fieldFrag.getNextFragment();
                } else {
                    contextFrag = contextFrag.getNextFragment();
                    fieldFrag = fieldFrag.getNextFragment();
                }
            } else {
                return null;
            }
        }
        return null;
    }

    private boolean isUnmappedContent(Node node) {
        if (!areOtherMappingInThisContext) {
            return true;
        }
        XMLDescriptor parentDesc = (XMLDescriptor) this.getDescriptor();
        XMLField field = (XMLField) this.getField();
        Iterator mappings = parentDesc.getMappings().iterator();
        int mappingsInContext = 0;
        while (mappings.hasNext()) {
            DatabaseMapping next = (DatabaseMapping) mappings.next();
            if (!(next == this)) {
                XMLField nextField = (XMLField) next.getField();
                XPathFragment frag = getFragmentToCompare(nextField, field);
                if (frag != null) {
                    mappingsInContext++;
                    if (((node.getNodeType() == Node.TEXT_NODE) || (node.getNodeType() == Node.CDATA_SECTION_NODE)) && frag.nameIsText()) {
                        return false;
                    }
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        String nodeNS = node.getNamespaceURI();
                        String fragNS = frag.getNamespaceURI();
                        String nodeLocalName = node.getLocalName();
                        String fragLocalName = frag.getLocalName();
                        if ((nodeNS == fragNS) || ((nodeNS != null) && (fragNS != null) && nodeNS.equals(fragNS))) {
                            if ((nodeLocalName == fragLocalName) || ((nodeLocalName != null) && (fragLocalName != null) && nodeLocalName.equals(fragLocalName))) {
                                return false;
                            }
                        }
                    }
                }
            }
            if (mappingsInContext == 0) {
                this.areOtherMappingInThisContext = false;
            }
        }
        return true;
    }

    public Node getNodeToReplace(Node parent) {
        //find the first child node that this any applies to.
        Node next = parent.getFirstChild();
        while(next != null) {
            if ((next.getNodeType() == Node.ELEMENT_NODE) || (next.getNodeType() == Node.TEXT_NODE) || (next.getNodeType() == Node.CDATA_SECTION_NODE)) {
                if (isUnmappedContent(next)) {
                    return next;
                }
            }
            next = next.getNextSibling();
        }
        return null;
    }

    private void writeSimpleValue(XMLField xmlRootField, DOMRecord row, AbstractSession session, Object originalObject, Object value, Node root, Node toReplace, boolean wasXMLRoot) {
        org.w3c.dom.Document doc = row.getDocument();
        if (wasXMLRoot) {
            if (((XMLRoot) originalObject).getNamespaceURI() != null) {
                String prefix = row.getNamespaceResolver().resolveNamespaceURI(((XMLRoot) originalObject).getNamespaceURI());
                if ((prefix == null) || prefix.length() == 0) {
                    xmlRootField.getXPathFragment().setGeneratedPrefix(true);
                    prefix = row.getNamespaceResolver().generatePrefix();
                }
                xmlRootField.getXPathFragment().setXPath(prefix + XMLConstants.COLON + ((XMLRoot) originalObject).getLocalName());
            }
        }

        if (null == xmlRootField) {
            Text textNode = doc.createTextNode((String) value);
            if (toReplace != null) {
                root.replaceChild(textNode, toReplace);
            } else {
                root.appendChild(textNode);
            }
        } else {
            QName qname = ((XMLRoot) originalObject).getSchemaType();
            if(qname != null && !qname.equals(XMLConstants.STRING_QNAME)){
                xmlRootField.setSchemaType(qname);
                xmlRootField.setIsTypedTextField(true);
                xmlRootField.addJavaConversion(value.getClass(), qname);
            }
            Node newNode = XPathEngine.getInstance().create(xmlRootField, root, value, session);   
        }
    }   
    
    public XMLConverter getConverter() {
        return converter;
    }
    
    public void setConverter(XMLConverter converter) {
        this.converter = converter;
    }
    
    public boolean isMixedContent() {
        return this.isMixedContent;
    }
    
    public void setMixedContent(boolean mixed) {
        this.isMixedContent = mixed;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertObjectValueToDataValue(Object value, Session session, XMLMarshaller marshaller) {
        if (null != converter) {
            return converter.convertObjectValueToDataValue(value, session, marshaller);
        }
        return value;
    }

    /**
     * INTERNAL
     * @since EclipseLink 2.5.0
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session, XMLUnmarshaller unmarshaller) {
        if (null != converter) {
            return converter.convertDataValueToObjectValue(fieldValue, session, unmarshaller);
        }
        return fieldValue;
    }

}
