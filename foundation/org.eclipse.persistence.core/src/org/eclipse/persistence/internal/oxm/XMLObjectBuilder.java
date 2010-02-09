/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.WeakObjectWrapper;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLMapping;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.queries.ObjectBuildingQuery;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * INTERNAL:
 * <p><b>Purpose</b>: Object-to-XML specific ObjectBuilder.</p>
 * @author Rick Barkhouse - rick.barkhouse@oracle.com
 * @since TopLink 10<i>i</i>, 03/31/2003 16:29:40
 */
public class XMLObjectBuilder extends ObjectBuilder {
    private Boolean isXMLDescriptor;

    /**
     * Create an XML object builder for the descriptor.
     */
    public XMLObjectBuilder(ClassDescriptor descriptor) {
        super(descriptor);
    }

    /**
     * Build the nested row into the parent dom.
     */
    public AbstractRecord buildRow(Object object, AbstractSession session, DatabaseField xmlField, XMLRecord parentRecord) {
        return buildRow(object, session, xmlField, parentRecord, false);
    }

    /**
     * Build the nested row into the parent dom.
     */
    public AbstractRecord buildRow(Object object, AbstractSession session, DatabaseField xmlField, XMLRecord parentRecord, boolean addXsiType) {
        if (isXmlDescriptor() && ((XMLDescriptor)getDescriptor()).shouldPreserveDocument()) {
            Vector pk = extractPrimaryKeyFromObject(object, session);
            if ((pk == null) || (pk.size() == 0)) {
                pk = new Vector();
                pk.addElement(new WeakObjectWrapper(object));
            }
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyForObject(pk, getDescriptor().getJavaClass(), getDescriptor());
            if ((cacheKey != null) && (cacheKey.getRecord() != null)) {
                XMLRecord nestedRecord = (XMLRecord)cacheKey.getRecord();
                nestedRecord.setMarshaller(parentRecord.getMarshaller());
                nestedRecord.setLeafElementType(parentRecord.getLeafElementType());
                parentRecord.setLeafElementType(null);
                return buildIntoNestedRow(nestedRecord, object, session, addXsiType);
            }
        }
        Element newNode = XPathEngine.getInstance().createUnownedElement(parentRecord.getDOM(), (XMLField)xmlField);
        XMLRecord nestedRecord = new DOMRecord(newNode);
        nestedRecord.setNamespaceResolver(parentRecord.getNamespaceResolver());
        nestedRecord.setMarshaller(parentRecord.getMarshaller());
        nestedRecord.setLeafElementType(parentRecord.getLeafElementType());
        parentRecord.setLeafElementType(null);
        return buildIntoNestedRow(nestedRecord, object, session, addXsiType);
    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(AbstractSession session) {
        return createRecord(getDescriptor().getTableName(), session);
    }

    
    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(int size, AbstractSession session) {
        return createRecord(getDescriptor().getTableName(), session);
    }

    /**
     * Create a new row/record for the object builder with the given name. This
     * allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(String rootName, AbstractSession session) {
        NamespaceResolver namespaceResolver = getNamespaceResolver();
        XMLRecord xmlRec = new DOMRecord(rootName, namespaceResolver);
        xmlRec.setSession(session);
        return xmlRec;
    }

    /**
     * Create a new row/record for the object builder with the given name and
     * namespace resolver instead of the namespace resolver from the descriptor.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(String rootName, String rootUri, AbstractSession session) {
        XMLRecord xmlRec = new DOMRecord(rootName, rootUri);
        xmlRec.setSession(session);
    	return xmlRec;
    }

    /**
     * Create a new row/record for the object builder with the given name. This
     * allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(String rootName, Node parent, AbstractSession session) {
        NamespaceResolver namespaceResolver = getNamespaceResolver();
        XMLRecord xmlRec = new DOMRecord(rootName, namespaceResolver, parent);
        xmlRec.setSession(session);
        return xmlRec;
    }
    
    public AbstractRecord createRecordFor(Object attributeValue, XMLField xmlField, XMLRecord parentRecord, XMLMapping mapping) {
        DocumentPreservationPolicy policy = parentRecord.getDocPresPolicy();
        Element newNode = null;
        if(policy != null) {
            newNode = (Element)policy.getNodeForObject(attributeValue);
        }
        if(newNode == null) {
           newNode = XPathEngine.getInstance().createUnownedElement(parentRecord.getDOM(), xmlField);
           if(xmlField.isSelfField()) {
               policy.addObjectToCache(attributeValue, newNode, mapping);
           } else {
               policy.addObjectToCache(attributeValue, newNode);
           }
        }
        
        XMLRecord nestedRecord = new DOMRecord(newNode);
        nestedRecord.setMarshaller(parentRecord.getMarshaller());
        nestedRecord.setLeafElementType(parentRecord.getLeafElementType());
        parentRecord.setLeafElementType(null);
        nestedRecord.setDocPresPolicy(policy);
        nestedRecord.setXOPPackage(parentRecord.isXOPPackage());
        return nestedRecord;
    }
    
    public AbstractRecord createRecordFor(Object object, DocumentPreservationPolicy docPresPolicy) {
        Element cachedNode = null;
        XMLRecord record = null;
        if(docPresPolicy != null) {
            cachedNode = (Element)docPresPolicy.getNodeForObject(object);
        }
        if(cachedNode == null) {
            record = new DOMRecord(getDescriptor().getTableName(), getNamespaceResolver());
            docPresPolicy.addObjectToCache(object, record.getDOM());
        } else {
            record = new DOMRecord(cachedNode);
        }
        record.setDocPresPolicy(docPresPolicy);
        return record;
    }
    
    public AbstractRecord createRecordFor(Object object, DocumentPreservationPolicy docPresPolicy, String rootName, String rootUri) {
        Element cachedNode = null;
        XMLRecord record = null;
        if(docPresPolicy != null) {
            cachedNode = (Element)docPresPolicy.getNodeForObject(object);
        }
        if(cachedNode == null) {
            record = new DOMRecord(rootName, rootUri);
            docPresPolicy.addObjectToCache(object, record.getDOM());
        } else {
            record = new DOMRecord(cachedNode);
        }
        record.setDocPresPolicy(docPresPolicy);
        return record;
    }

    /**
     * Create a new row/record for the object builder. This allows subclasses to
     * define different record types.  This will typically be called when a 
     * record will be used for temporarily holding on to primary key fields.
     */
    protected AbstractRecord createRecordForPKExtraction(int size, AbstractSession session) {
        NamespaceResolver namespaceResolver = getNamespaceResolver();
        XMLRecord xmlRec = new DOMRecord(getDescriptor().getTableName(), namespaceResolver);
        xmlRec.setSession(session);
        return xmlRec;
    }
    
    /**
     * INTERNAL: Override the parent's buildObject to allow for the caching of
     * aggregate objects in OX. By caching aggregates along with XML Nodes that
     * they were created from, we are able to preserve the structure and
     * unmapped content of the document that was used to create these objects.
     */
    public Object buildObject(ObjectBuildingQuery query, AbstractRecord databaseRow, JoinedAttributeManager joinManager) throws DatabaseException, QueryException {
        XMLRecord row = (XMLRecord)databaseRow;
        row.setSession(query.getSession());
        
        XMLUnmarshaller unmarshaller = row.getUnmarshaller();
        Object parent = row.getOwningObject();

        Vector pk = extractPrimaryKeyFromRow(databaseRow, query.getSession());
        if (!(isXmlDescriptor() || getDescriptor().isDescriptorTypeAggregate())) {
            return super.buildObject(query, databaseRow, joinManager);
        }
        query.getSession().startOperationProfile(SessionProfiler.OBJECT_BUILDING, query, SessionProfiler.ALL);
        ClassDescriptor concreteDescriptor = getDescriptor();
        Object domainObject = null;

        // only need to check in the root case since the nested case is handled
        // in the mapping
        if (concreteDescriptor.hasInheritance() && (parent == null)) {
            // look for an xsi:type attribute in the xml document
            Class classValue = concreteDescriptor.getInheritancePolicy().classFromRow(databaseRow, query.getSession());
            if ((classValue == null) && isXmlDescriptor()) {
                // no xsi:type attribute - look for type indicator on the
                // default root element
                QName leafElementType = ((XMLDescriptor)concreteDescriptor).getDefaultRootElementType();

                // if we have a user-set type, try to get the class from the
                // inheritance policy
                if (leafElementType != null) {
                    Object indicator = concreteDescriptor.getInheritancePolicy().getClassIndicatorMapping().get(leafElementType);
                    // if the inheritance policy does not contain the user-set
                    // type, throw an exception
                    if (indicator == null) {
                        throw DescriptorException.missingClassForIndicatorFieldValue(leafElementType, concreteDescriptor.getInheritancePolicy().getDescriptor());
                    }
                    classValue = (Class)indicator;
                }
            }

            // if we found the class, use it - otherwise, use the descriptor
            // class, if non-abstract
            if (classValue != null) {
                concreteDescriptor = query.getSession().getDescriptor(classValue);
                if ((concreteDescriptor == null) && query.hasPartialAttributeExpressions()) {
                    concreteDescriptor = getDescriptor();
                }
                if (concreteDescriptor == null) {
                    throw QueryException.noDescriptorForClassFromInheritancePolicy(query, classValue);
                }
            } else {
                // make sure the class is non-abstract
                if (Modifier.isAbstract(concreteDescriptor.getJavaClass().getModifiers())) {
                    // throw an exception
                    throw DescriptorException.missingClassIndicatorField(databaseRow, concreteDescriptor.getInheritancePolicy().getDescriptor());
                }
            }
        }
        domainObject = concreteDescriptor.getObjectBuilder().buildNewInstance();
        row.setCurrentObject(domainObject);
        if ((unmarshaller != null) && (unmarshaller.getUnmarshalListener() != null)) {
            unmarshaller.getUnmarshalListener().beforeUnmarshal(domainObject, parent);
        }
        concreteDescriptor.getObjectBuilder().buildAttributesIntoObject(domainObject, databaseRow, query, joinManager, false);
        if (isXmlDescriptor() && ((XMLDescriptor)concreteDescriptor).getPrimaryKeyFieldNames().size() > 0) {
            if ((pk == null) || (pk.size() == 0)) {
                pk = new Vector();
                pk.addElement(new WeakObjectWrapper(domainObject));
            }
            CacheKey key = query.getSession().getIdentityMapAccessorInstance().acquireDeferredLock(pk, concreteDescriptor.getJavaClass(), concreteDescriptor);
            if (((XMLDescriptor)concreteDescriptor).shouldPreserveDocument()) {
                key.setRecord(databaseRow);
            }
            key.setObject(domainObject);
            key.releaseDeferredLock();
        }
        DocumentPreservationPolicy docPresPolicy = ((DOMRecord)row).getDocPresPolicy();
        if(docPresPolicy != null) {
            //EIS XML Cases won't have a doc pres policy set
            ((DOMRecord)row).getDocPresPolicy().addObjectToCache(domainObject, ((DOMRecord)row).getDOM());
        }
        query.getSession().endOperationProfile(SessionProfiler.OBJECT_BUILDING, query, SessionProfiler.ALL);
        if ((unmarshaller != null) && (unmarshaller.getUnmarshalListener() != null)) {
            unmarshaller.getUnmarshalListener().afterUnmarshal(domainObject, parent);
        }
        return domainObject;
    }

    public AbstractRecord buildRow(AbstractRecord databaseRow, Object object, AbstractSession session) {
        return buildRow(databaseRow, object, session, false);
    }

    public AbstractRecord buildRow(AbstractRecord databaseRow, Object object, AbstractSession session, boolean wasXMLRoot) {
        XMLRecord row = (XMLRecord)databaseRow;
        row.setSession(session);
        
        XMLMarshaller marshaller = row.getMarshaller();
        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().beforeMarshal(object);
        }

        writeOutMappings(row, object, session);
        // PERF: Avoid synchronized enumerator as is concurrency bottleneck.
        // If this descriptor is involved in inheritance add the class type.
        if (getDescriptor().hasInheritance()) {
            boolean shouldAddClassIndicatorFieldToRow = true;
            if (isXmlDescriptor() && !wasXMLRoot) {
                XMLDescriptor xmlDescriptor = (XMLDescriptor)getDescriptor();
                if ((xmlDescriptor.getDefaultRootElementType() != null) && (xmlDescriptor.getSchemaReference() != null)) {
                    XMLSchemaReference xmlRef = xmlDescriptor.getSchemaReference();
                    if ((xmlRef.getType() == 1) && xmlRef.isGlobalDefinition()) {
                        QName ctx = xmlRef.getSchemaContextAsQName(xmlDescriptor.getNamespaceResolver());
                        if (ctx != null) {
                            // at this point we are either writing out the
                            // schema context or nothing at all
                            shouldAddClassIndicatorFieldToRow = false;
                            if (!ctx.equals(xmlDescriptor.getDefaultRootElementType())) {
                                // need to write out ctx
                                row.add(xmlDescriptor.getInheritancePolicy().getClassIndicatorField(), xmlRef.getSchemaContext().substring(1));
                            }
                        }
                    }
                }
            }
            if (shouldAddClassIndicatorFieldToRow) {
                getDescriptor().getInheritancePolicy().addClassIndicatorFieldToRow(row);
            }
        }

        // If this descriptor has multiple tables then we need to append the
        // primary keys for
        // the non default tables.
        if (!getDescriptor().isAggregateDescriptor()) {
            addPrimaryKeyForNonDefaultTable(row);
        }

        addNamespaceDeclarations((row).getDocument());

        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().afterMarshal(object);
        }
        return row;
    }

    public void writeOutMappings(XMLRecord row, Object object, AbstractSession session) {
        Vector mappings = getDescriptor().getMappings();
        for (int index = 0; index < mappings.size(); index++) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.get(index);
            mapping.writeFromObjectIntoRow(object, row, session);
        }
    }
    public void addNamespaceDeclarations(Document document) {
        NamespaceResolver namespaceResolver = getNamespaceResolver();

        if (namespaceResolver == null) {
            return;
        }

        Element docElement = document.getDocumentElement();
        if(namespaceResolver.getDefaultNamespaceURI() != null) {
            docElement.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS, namespaceResolver.getDefaultNamespaceURI());
        }

        Enumeration prefixes = namespaceResolver.getPrefixes();
        String prefix;
        String namespace;
        while (prefixes.hasMoreElements()) {
            prefix = (String)prefixes.nextElement();
            namespace = namespaceResolver.resolveNamespacePrefix(prefix);
            docElement.setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + prefix, namespace);
        }
    }
    
    /**
     * Override method in superclass in order to set the session on the record.
     * Each mapping is recursed to assign values from the Record to the attributes in the domain object.
     */
    public void buildAttributesIntoObject(Object domainObject, AbstractRecord databaseRow, ObjectBuildingQuery query, JoinedAttributeManager joinManager, boolean forRefresh) throws DatabaseException {
        ((XMLRecord)databaseRow).setSession(query.getSession().getExecutionSession(query));
        super.buildAttributesIntoObject(domainObject, databaseRow, query, joinManager, forRefresh);
    }

    /**
     * Override method in superclass in order to set the session on the record.
     * Return the row with primary keys and their values from the given expression.
     */
    public AbstractRecord extractPrimaryKeyRowFromExpression(Expression expression, AbstractRecord translationRow, AbstractSession session) {
        AbstractRecord primaryKeyRow = createRecord(getPrimaryKeyMappings().size(), session);
        expression.getBuilder().setSession(session.getRootSession(null));
        // Get all the field & values from expression   
        boolean isValid = expression.extractPrimaryKeyValues(true, getDescriptor(), primaryKeyRow, translationRow);
        if (!isValid) {
            return null;
        }

        // Check that the sizes match up 
        if (primaryKeyRow.size() != getDescriptor().getPrimaryKeyFields().size()) {
            return null;
        }

        return primaryKeyRow;
    }
    
    /**
     * Override method in superclass in order to set the session on the record.
     * Return the row with primary keys and their values from the given expression.
     */
    public Vector extractPrimaryKeyFromExpression(boolean requiresExactMatch, Expression expression, AbstractRecord translationRow, AbstractSession session) {
        AbstractRecord primaryKeyRow = createRecord(getPrimaryKeyMappings().size(), session);
        expression.getBuilder().setSession(session.getRootSession(null));
        // Get all the field & values from expression.
        boolean isValid = expression.extractPrimaryKeyValues(requiresExactMatch, getDescriptor(), primaryKeyRow, translationRow);
        if (requiresExactMatch && (!isValid)) {
            return null;
        }

        // Check that the sizes match.
        if (primaryKeyRow.size() != getDescriptor().getPrimaryKeyFields().size()) {
            return null;
        }

        return extractPrimaryKeyFromRow(primaryKeyRow, session);
    }
    
    public Vector extractPrimaryKeyFromObject(Object domainObject, AbstractSession session) {
        if (getDescriptor().hasInheritance() && (domainObject.getClass() != getDescriptor().getJavaClass()) && (!domainObject.getClass().getSuperclass().equals(getDescriptor().getJavaClass()))) {
            return session.getDescriptor(domainObject.getClass()).getObjectBuilder().extractPrimaryKeyFromObject(domainObject, session);
        }
        if (getDescriptor().getPrimaryKeyFields().size() == 0) {
            return null;
        }
        return super.extractPrimaryKeyFromObject(domainObject, session);
    }

    public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object object, AbstractSession session) {
        return buildIntoNestedRow(row, object, session, false);
    }

    public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object object, AbstractSession session, boolean shouldWriteXsiType) {
        // PERF: Avoid synchronized enumerator as is concurrency bottleneck.
        XMLRecord record = (XMLRecord)row;
        record.setSession(session);
        
        XMLMarshaller marshaller = record.getMarshaller();

        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().beforeMarshal(object);
        }
        List extraNamespaces = null;
        if (isXmlDescriptor()) {
            XMLDescriptor xmlDescriptor = (XMLDescriptor)getDescriptor();
            extraNamespaces = addExtraNamespacesToNamespaceResolver(xmlDescriptor, record, session);
            writeExtraNamespaces(extraNamespaces, record);

        }

        writeOutMappings(record, object, session);
        // If this descriptor is involved in inheritance add the class type.
        if (isXmlDescriptor()) {
            XMLDescriptor xmlDescriptor = (XMLDescriptor)getDescriptor();

            XMLSchemaReference xmlRef = xmlDescriptor.getSchemaReference();
            if (shouldWriteXsiType) {
                writeXsiTypeAttribute(xmlDescriptor, (DOMRecord)record, xmlRef.getSchemaContext().substring(1));
            }
            if (getDescriptor().hasInheritance()) {
                if ((record.getLeafElementType() != null) && ((xmlRef.getType() == 1) && xmlRef.isGlobalDefinition())) {
                    // only interested in global COMPLEX_TYPE
                    QName ctxQName = xmlRef.getSchemaContextAsQName(xmlDescriptor.getNamespaceResolver());
                    if (ctxQName.equals(record.getLeafElementType())) {
                        // don't write out xsi:type attribute
                    } else {
                        // write out the target descriptor's schema context as xsi:type
                        XMLField xmlField = (XMLField)xmlDescriptor.getInheritancePolicy().getClassIndicatorField();
                        if (xmlField.getLastXPathFragment().isAttribute()) {
                            writeXsiTypeAttribute(xmlDescriptor, (DOMRecord)record, xmlRef.getSchemaContext().substring(1));
                        } else {
                            writeXsiTypeAttribute(xmlDescriptor, (DOMRecord)record, xmlRef.getSchemaContext().substring(1));
                        }
                    }

                    // if the user has set the element type, but the target descriptor's
                    // schema context in not a global complex type or is null, delegate
                    // the work to the inheritance policy
                } else {
                    // no user-set element type, so proceed via inheritance policy
                    if (!xmlDescriptor.getInheritancePolicy().hasClassExtractor()) {
                      XMLField xmlField = (XMLField)xmlDescriptor.getInheritancePolicy().getClassIndicatorField();
                      if (xmlField.getLastXPathFragment().isAttribute()) {
                          getDescriptor().getInheritancePolicy().addClassIndicatorFieldToRow(row);
                      } else {
                          getDescriptor().getInheritancePolicy().addClassIndicatorFieldToRow(row);
                      }
                    }
                }
            }
            removeExtraNamespacesFromNamespaceResolver(record, extraNamespaces, session);
        }

        // If this descriptor has multiple tables then we need to append the
        // primary keys for
        // the non default tables.
        if (!getDescriptor().isAggregateDescriptor()) {
            addPrimaryKeyForNonDefaultTable(row);
        }
        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().afterMarshal(object);
        }
        return row;
    }

    private void writeXsiTypeAttribute(XMLDescriptor xmlDescriptor, DOMRecord row, String typeValue) {
        String xsiPrefix = null;
        boolean generated = false;

        xsiPrefix = row.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        if (xsiPrefix == null) {
            xsiPrefix = xmlDescriptor.getNonNullNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
            generated = true;
            writeXsiNamespace(row, xsiPrefix);
        }
        XMLField xmlField = (XMLField)xmlDescriptor.buildField(XMLConstants.ATTRIBUTE + xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
        if (generated) {
            xmlField.getLastXPathFragment().setGeneratedPrefix(true);
        }
        xmlField.getLastXPathFragment().setNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        row.add(xmlField, typeValue);
    }

    private void writeXsiNamespace(DOMRecord nestedRecord, String xsiPrefix) {
        if(nestedRecord.getDOM().getNodeType() == Node.ELEMENT_NODE) {
            ((Element)nestedRecord.getDOM()).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL);
        }
    }

    public NamespaceResolver getNamespaceResolver() {
        NamespaceResolver namespaceResolver = null;
        if (isXmlDescriptor()) {
            namespaceResolver = ((XMLDescriptor)getDescriptor()).getNamespaceResolver();
        } else if (getDescriptor() instanceof org.eclipse.persistence.eis.EISDescriptor) {
            namespaceResolver = ((org.eclipse.persistence.eis.EISDescriptor)getDescriptor()).getNamespaceResolver();
        }
        return namespaceResolver;
    }
    /**
     * Indicates if the object builder's descriptor is an XMLDescriptor.
     * The value is lazily initialized.
     *
     * @return
     */
    protected boolean isXmlDescriptor() {
        if (isXMLDescriptor == null) {
            isXMLDescriptor = getDescriptor() instanceof XMLDescriptor;
        }
        return isXMLDescriptor.booleanValue();
    }

    /**
     * Set the descriptor.  This method is overridden so the
     * isXMLDescriptor Boolean can be reset.
     */
    public void setDescriptor(ClassDescriptor aDescriptor) {
        super.setDescriptor(aDescriptor);
        isXMLDescriptor = null;
    }

    protected List addExtraNamespacesToNamespaceResolver(XMLDescriptor desc, XMLRecord marshalRecord, AbstractSession session) {
        if (((XMLLogin)session.getDatasourceLogin()).hasEqualNamespaceResolvers()) {
            return null;
        }

        List returnList = new ArrayList();
        
        for(Entry<String, String> entry: desc.getNonNullNamespaceResolver().getPrefixesToNamespaces().entrySet()) {

            //if isn't already on a parentadd namespace to this element
            String prefix = marshalRecord.getNamespaceResolver().resolveNamespaceURI(entry.getValue());

            if (prefix == null || prefix.length() == 0) {
                //if there is no prefix already declared for this uri in the nr add this one                //
                marshalRecord.getNamespaceResolver().put(entry.getKey(), entry.getValue());
                returnList.add(new Namespace(entry.getKey(), entry.getValue()));
            } else {
                //if prefix is the same do nothing 
                if (!prefix.equals(entry.getKey())) {
                    //if prefix exists for uri but is different then add this
                    marshalRecord.getNamespaceResolver().put(entry.getKey(), entry.getValue());
                    returnList.add(new Namespace(entry.getKey(), entry.getValue()));
                }
            }
        }
        return returnList;
    }

    public void writeExtraNamespaces(List extraNamespaces, XMLRecord xmlRecord) {        
        if(extraNamespaces == null){
          return;
        }
        
        if(xmlRecord.getDOM().getNodeType() != Node.ELEMENT_NODE) {
            return;
        }

        for (int i = 0; i < extraNamespaces.size(); i++) {
            Namespace next = (Namespace)extraNamespaces.get(i);
            ((Element)xmlRecord.getDOM()).setAttributeNS(XMLConstants.XMLNS_URL, XMLConstants.XMLNS + XMLConstants.COLON + next.getPrefix(), next.getNamespaceURI());
        }
    }

    public void removeExtraNamespacesFromNamespaceResolver(XMLRecord marshalRecord, List extraNamespaces, AbstractSession session) {       
       if (extraNamespaces == null){
         return;
       }

        for (int i = 0; i < extraNamespaces.size(); i++) {
            Namespace nextExtraNamespace = (Namespace)extraNamespaces.get(i);
            String uri = marshalRecord.getNamespaceResolver().resolveNamespacePrefix(nextExtraNamespace.getPrefix());
            if ((uri != null) && uri.equals(nextExtraNamespace.getNamespaceURI())) {
                marshalRecord.getNamespaceResolver().removeNamespace(nextExtraNamespace.getPrefix());
            }
        }
    }
    
    public void initialize(AbstractSession session) throws DescriptorException {
        getMappingsByField().clear();
        getReadOnlyMappingsByField().clear();
        getMappingsByAttribute().clear();
        getCloningMappings().clear();
        getEagerMappings().clear();
        getRelationshipMappings().clear();

        for (Enumeration mappings = this.descriptor.getMappings().elements();
                 mappings.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();

            // Add attribute to mapping association
            if (!mapping.isWriteOnly()) {
                getMappingsByAttribute().put(mapping.getAttributeName(), mapping);
            }
            // Cache mappings that require cloning.
            if (mapping.isCloningRequired()) {
                getCloningMappings().add(mapping);
            }
            // Cache eager mappings.
            if (mapping.isForeignReferenceMapping() && ((ForeignReferenceMapping)mapping).usesIndirection() && (!mapping.isLazy())) {
                getEagerMappings().add(mapping);
            }
            // Cache relationship mappings.
            if (!mapping.isDirectToFieldMapping()) {
                getRelationshipMappings().add(mapping);
            }

            // Add field to mapping association
            for (DatabaseField field : mapping.getFields()) {

                if (mapping.isReadOnly()) {
                    List readOnlyMappings = getReadOnlyMappingsByField().get(field);
    
                    if (readOnlyMappings == null) {
                        readOnlyMappings = new ArrayList();
                        getReadOnlyMappingsByField().put(field, readOnlyMappings);
                    }
    
                    readOnlyMappings.add(mapping);
                } else {
                    if (mapping.isAggregateObjectMapping()) {
                        // For Embeddable class, we need to test read-only 
                        // status of individual fields in the embeddable.
                        ObjectBuilder aggregateObjectBuilder = ((AggregateObjectMapping)mapping).getReferenceDescriptor().getObjectBuilder();
                        
                        // Look in the non-read-only fields mapping
                        DatabaseMapping aggregatedFieldMapping = aggregateObjectBuilder.getMappingForField(field);
    
                        if (aggregatedFieldMapping == null) { // mapping must be read-only
                            List readOnlyMappings = getReadOnlyMappingsByField().get(field);
                            
                            if (readOnlyMappings == null) {
                                readOnlyMappings = new ArrayList();
                                getReadOnlyMappingsByField().put(field, readOnlyMappings);
                            }
    
                            readOnlyMappings.add(mapping);
                        } else {
                            getMappingsByField().put(field, mapping);
                        }
                    } else { // Not an embeddable mapping
                        if (!getMappingsByField().containsKey(field)) {  
                            getMappingsByField().put(field, mapping);
                        }
                    }
                }
            }
        }
        this.isSimple = getRelationshipMappings().isEmpty();

        initializePrimaryKey(session);
        initializeJoinedAttributes();
        
        if (this.descriptor.usesSequenceNumbers()) {
            DatabaseMapping sequenceMapping = getMappingForField(this.descriptor.getSequenceNumberField());
            if ((sequenceMapping != null) && sequenceMapping.isDirectToFieldMapping()) {
                setSequenceMapping((AbstractDirectMapping)sequenceMapping);
            }
        }
    }    
}
