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
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.WeakObjectWrapper;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.mappings.Mapping;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.record.DOMRecord;
import org.eclipse.persistence.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.documentpreservation.DocumentPreservationPolicy;
import org.eclipse.persistence.queries.FetchGroup;
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
    private boolean xsiTypeIndicatorField;

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
        if (isXmlDescriptor() && ((Descriptor)getDescriptor()).shouldPreserveDocument()) {
            Object pk = extractPrimaryKeyFromObject(object, session);
            if ((pk == null) || (pk instanceof CacheId) && (((CacheId)pk).getPrimaryKey().length == 0)) {
                pk = new CacheId(new Object[]{ new WeakObjectWrapper(object) });
            }
            CacheKey cacheKey = session.getIdentityMapAccessorInstance().getCacheKeyForObject(pk, getDescriptor().getJavaClass(), getDescriptor(), false);
            if ((cacheKey != null) && (cacheKey.getRecord() != null)) {
                XMLRecord nestedRecord = (XMLRecord)cacheKey.getRecord();
                nestedRecord.setMarshaller(parentRecord.getMarshaller());
                nestedRecord.setLeafElementType(parentRecord.getLeafElementType());
                parentRecord.setLeafElementType((XPathQName)null);
                return buildIntoNestedRow(nestedRecord, object, session);
            }
        }
        Element newNode = XPathEngine.getInstance().createUnownedElement(parentRecord.getDOM(), (Field)xmlField);
        XMLRecord nestedRecord = new DOMRecord(newNode);
        nestedRecord.setNamespaceResolver(parentRecord.getNamespaceResolver());
        nestedRecord.setMarshaller(parentRecord.getMarshaller());
        nestedRecord.setLeafElementType(parentRecord.getLeafElementType());
        parentRecord.setLeafElementType((XPathQName)null);
        return buildIntoNestedRow(nestedRecord, object, session);

    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(AbstractSession session) {
        return (AbstractRecord) createRecord(getDescriptor().getTableName(), session);
    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(int size, AbstractSession session) {
        return (AbstractRecord) createRecord(getDescriptor().getTableName(), session);
    }

    /**
     * Create a new row/record for the object builder with the given name. This
     * allows subclasses to define different record types.
     */
    public AbstractMarshalRecord createRecord(String rootName, AbstractSession session) {
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
    public AbstractMarshalRecord createRecord(String rootName, Node parent, AbstractSession session) {
        NamespaceResolver namespaceResolver = getNamespaceResolver();
        XMLRecord xmlRec = new DOMRecord(rootName, namespaceResolver, parent);
        xmlRec.setSession(session);
        return xmlRec;
    }

    public AbstractRecord createRecordFor(Object attributeValue, Field xmlField, XMLRecord parentRecord, Mapping mapping) {
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

        DOMRecord nestedRecord = new DOMRecord(newNode);
        nestedRecord.setMarshaller(parentRecord.getMarshaller());
        nestedRecord.setLeafElementType(parentRecord.getLeafElementType());
        parentRecord.setLeafElementType((XPathQName)null);
        nestedRecord.setDocPresPolicy(policy);
        nestedRecord.setXOPPackage(parentRecord.isXOPPackage());
        nestedRecord.setReferenceResolver(((DOMRecord) parentRecord).getReferenceResolver());
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
    @Override
    public Object buildObject(ObjectBuildingQuery query, AbstractRecord databaseRow, JoinedAttributeManager joinManager) throws DatabaseException, QueryException {
        XMLRecord row = (XMLRecord) databaseRow;
        row.setSession(query.getSession());

        XMLUnmarshaller unmarshaller = row.getUnmarshaller();
        Object parent = row.getOwningObject();

        if (!(isXmlDescriptor() || getDescriptor().isDescriptorTypeAggregate())) {
            return super.buildObject(query, databaseRow, joinManager);
        }
        query.getSession().startOperationProfile(SessionProfiler.ObjectBuilding, query, SessionProfiler.ALL);
        ClassDescriptor concreteDescriptor = getDescriptor();
        Object domainObject = null;

        // only need to check in the root case since the nested case is handled
        // in the mapping
        if (concreteDescriptor.hasInheritance() && (parent == null)) {
            // look for an xsi:type attribute in the xml document
            InheritancePolicy inheritancePolicy = concreteDescriptor.getInheritancePolicy();
            Class classValue = inheritancePolicy.classFromRow(databaseRow, query.getSession());
            if ((classValue == null) && isXmlDescriptor()) {
                // no xsi:type attribute - look for type indicator on the
                // default root element
                QName leafElementType = ((Descriptor) concreteDescriptor).getDefaultRootElementType();

                // if we have a user-set type, try to get the class from the
                // inheritance policy
                if (leafElementType != null) {
                    XPathQName xpathQName = new XPathQName(leafElementType, row.isNamespaceAware());
                    Object indicator = inheritancePolicy.getClassIndicatorMapping().get(xpathQName);
                    if (indicator != null) {
                        classValue = (Class) indicator;
                    }
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
                    throw DescriptorException.missingClassIndicatorField(databaseRow, inheritancePolicy.getDescriptor());
                }
            }
        }
        domainObject = concreteDescriptor.getObjectBuilder().buildNewInstance();
        row.setCurrentObject(domainObject);
        if ((unmarshaller != null) && (unmarshaller.getUnmarshalListener() != null)) {
            unmarshaller.getUnmarshalListener().beforeUnmarshal(domainObject, parent);
        }
        concreteDescriptor.getObjectBuilder().buildAttributesIntoObject(domainObject, null, databaseRow, query, joinManager, null, false, query.getSession());
        if (isXmlDescriptor() && ((Descriptor) concreteDescriptor).getPrimaryKeyFieldNames().size() > 0) {
            Object pk = extractPrimaryKeyFromRow(databaseRow, query.getSession());
            if ((pk != null) && (((CacheId) pk).getPrimaryKey().length > 0)) {
                DOMRecord domRecord = (DOMRecord) databaseRow;
                domRecord.getReferenceResolver().putValue(concreteDescriptor.getJavaClass(), pk, domainObject);
            }
        }
        DocumentPreservationPolicy docPresPolicy = ((DOMRecord) row).getDocPresPolicy();
        if (docPresPolicy != null) {
            // EIS XML Cases won't have a doc pres policy set
            ((DOMRecord) row).getDocPresPolicy().addObjectToCache(domainObject, ((DOMRecord) row).getDOM());
        }
        query.getSession().endOperationProfile(SessionProfiler.ObjectBuilding, query, SessionProfiler.ALL);
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
        addNamespaceDeclarations((row).getDocument());
        writeOutMappings(row, object, session);


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

    public void writeOutMappings(XMLRecord row, Object object, AbstractSession session) {
        List<DatabaseMapping> mappings = getDescriptor().getMappings();
        for (int index = 0; index < mappings.size(); index++) {
            DatabaseMapping mapping = mappings.get(index);
            mapping.writeFromObjectIntoRow(object, row, session, WriteType.UNDEFINED);
        }
    }
    public void addNamespaceDeclarations(Document document) {
        NamespaceResolver namespaceResolver = getNamespaceResolver();

        if (namespaceResolver == null) {
            return;
        }

        Element docElement = document.getDocumentElement();
        if(namespaceResolver.getDefaultNamespaceURI() != null) {
            docElement.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE, namespaceResolver.getDefaultNamespaceURI());
        }

        Enumeration prefixes = namespaceResolver.getPrefixes();
        String prefix;
        String namespace;
        while (prefixes.hasMoreElements()) {
            prefix = (String)prefixes.nextElement();
            namespace = namespaceResolver.resolveNamespacePrefix(prefix);
            docElement.setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + prefix, namespace);
        }
    }

    /**
     * Override method in superclass in order to set the session on the record.
     * Each mapping is recursed to assign values from the Record to the attributes in the domain object.
     */
    @Override
    public void buildAttributesIntoObject(Object domainObject, CacheKey cacheKey, AbstractRecord databaseRow, ObjectBuildingQuery query, JoinedAttributeManager joinManager, FetchGroup executionFetchGroup, boolean forRefresh, AbstractSession targetSession) throws DatabaseException {
        ((XMLRecord)databaseRow).setSession(query.getSession().getExecutionSession(query));
        super.buildAttributesIntoObject(domainObject, cacheKey, databaseRow, query, joinManager, executionFetchGroup, forRefresh, targetSession);
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
    @Override
    public Object extractPrimaryKeyFromExpression(boolean requiresExactMatch, Expression expression, AbstractRecord translationRow, AbstractSession session) {
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

    @Override
    public Object extractPrimaryKeyFromObject(Object domainObject, AbstractSession session) {
        if (getDescriptor().hasInheritance() && (domainObject.getClass() != getDescriptor().getJavaClass()) && (!domainObject.getClass().getSuperclass().equals(getDescriptor().getJavaClass()))) {
            return session.getDescriptor(domainObject.getClass()).getObjectBuilder().extractPrimaryKeyFromObject(domainObject, session);
        }
        List<DatabaseField> descriptorPrimaryKeyFields = getDescriptor().getPrimaryKeyFields();
        if (null == descriptorPrimaryKeyFields || descriptorPrimaryKeyFields.size() == 0) {
            return null;
        }
        return super.extractPrimaryKeyFromObject(domainObject, session);
    }

    public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object object, AbstractSession session) {
        return buildIntoNestedRow(row, null, object, session, null, null, false);
    }

    public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object object, AbstractSession session, Descriptor refDesc, Field xmlField) {
        return buildIntoNestedRow(row, null, object, session, refDesc, xmlField, false);
    }
   public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object originalObject, Object object, AbstractSession session, Descriptor refDesc, Field xmlField, boolean wasXMLRoot) {
        // PERF: Avoid synchronized enumerator as is concurrency bottleneck.
        XMLRecord record = (XMLRecord)row;
        record.setSession(session);

        XMLMarshaller marshaller = record.getMarshaller();

        if ((marshaller != null) && (marshaller.getMarshalListener() != null)) {
            marshaller.getMarshalListener().beforeMarshal(object);
        }
        List extraNamespaces = null;
        if (isXmlDescriptor()) {
            Descriptor xmlDescriptor = (Descriptor)getDescriptor();
            extraNamespaces = addExtraNamespacesToNamespaceResolver(xmlDescriptor, record, session, false, false);
            writeExtraNamespaces(extraNamespaces, record);
            record.addXsiTypeAndClassIndicatorIfRequired(xmlDescriptor, refDesc, xmlField, originalObject, object, wasXMLRoot, false);
        }

        writeOutMappings(record, object, session);
        // If this descriptor is involved in inheritance add the class type.
        if (isXmlDescriptor()) {
            record.removeExtraNamespacesFromNamespaceResolver(extraNamespaces, session);
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
            isXMLDescriptor = getDescriptor() instanceof Descriptor;
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
    
    protected List<Namespace> addExtraNamespacesToNamespaceResolver(Descriptor desc, AbstractMarshalRecord marshalRecord, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers) {
        return marshalRecord.addExtraNamespacesToNamespaceResolver(desc, session, allowOverride, ignoreEqualResolvers);
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
            ((Element)xmlRecord.getDOM()).setAttributeNS(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI, javax.xml.XMLConstants.XMLNS_ATTRIBUTE + Constants.COLON + next.getPrefix(), next.getNamespaceURI());
        }
    }

    public void initialize(AbstractSession session) throws DescriptorException {
        mappingsByField.clear();
        if(null != readOnlyMappingsByField) {
            readOnlyMappingsByField.clear();
        }
        if(null != mappingsByAttribute) {
            mappingsByAttribute.clear();
        }
        cloningMappings.clear();
        if(null != eagerMappings) {
            eagerMappings.clear();
        }
        if(null != relationshipMappings) {
            relationshipMappings.clear();
        }

        for (Enumeration mappings = this.descriptor.getMappings().elements();
                 mappings.hasMoreElements();) {
            DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();

            // Add attribute to mapping association
            if (!mapping.isWriteOnly()) {
                if(mappingsByAttribute != null) {
                    getMappingsByAttribute().put(mapping.getAttributeName(), mapping);
                }
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
                if(null != relationshipMappings) {
                    relationshipMappings.add(mapping);
                }
            }

            // Add field to mapping association
            for (DatabaseField field : mapping.getFields()) {

                if (mapping.isReadOnly()) {
                    if(null != readOnlyMappingsByField) {
                        List readOnlyMappings = getReadOnlyMappingsByField().get(field);
    
                        if (readOnlyMappings == null) {
                            readOnlyMappings = new ArrayList();
                            getReadOnlyMappingsByField().put(field, readOnlyMappings);
                        }
    
                        readOnlyMappings.add(mapping);
                    }
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
        this.isSimple = null == relationshipMappings || relationshipMappings.isEmpty();

        initializePrimaryKey(session);
        initializeJoinedAttributes();

        if (this.descriptor.usesSequenceNumbers()) {
            DatabaseMapping sequenceMapping = getMappingForField(this.descriptor.getSequenceNumberField());
            if ((sequenceMapping != null) && sequenceMapping.isDirectToFieldMapping()) {
                setSequenceMapping((AbstractDirectMapping)sequenceMapping);
            }
        }
        
        
        
        if (descriptor.hasInheritance() ) {
        	Field indicatorField = (Field) descriptor.getInheritancePolicy().getClassIndicatorField();
            if(indicatorField != null){
               if (indicatorField.getLastXPathFragment().getNamespaceURI() != null && indicatorField.getLastXPathFragment().getNamespaceURI().equals(javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)
                        && indicatorField.getLastXPathFragment().getLocalName().equals(Constants.SCHEMA_TYPE_ATTRIBUTE)){
                     xsiTypeIndicatorField = true;
               }
               
            }
        }
    }

    public boolean isXMLObjectBuilder() {
        return true;
    }

    public boolean isXsiTypeIndicatorField() {
        return xsiTypeIndicatorField;
    }

}
