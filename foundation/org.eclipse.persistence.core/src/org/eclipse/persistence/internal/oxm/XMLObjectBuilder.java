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
package org.eclipse.persistence.internal.oxm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.descriptors.Namespace;
import org.eclipse.persistence.internal.descriptors.ObjectBuilder;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.oxm.WeakObjectWrapper;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLRoot;
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
        if (isXmlDescriptor() && ((XMLDescriptor)getDescriptor()).shouldPreserveDocument()) {
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
        Element newNode = XPathEngine.getInstance().createUnownedElement(parentRecord.getDOM(), (XMLField)xmlField);
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
        parentRecord.setLeafElementType((XPathQName)null);
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
    @Override
    public Object buildObject(ObjectBuildingQuery query, AbstractRecord databaseRow, JoinedAttributeManager joinManager) throws DatabaseException, QueryException {
        XMLRecord row = (XMLRecord) databaseRow;
        row.setSession(query.getSession());

        XMLUnmarshaller unmarshaller = row.getUnmarshaller();
        Object parent = row.getOwningObject();

        Object pk = extractPrimaryKeyFromRow(databaseRow, query.getSession());
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
                QName leafElementType = ((XMLDescriptor) concreteDescriptor).getDefaultRootElementType();

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
        concreteDescriptor.getObjectBuilder().buildAttributesIntoObject(domainObject, null, databaseRow, query, joinManager, false, query.getSession());
        if (isXmlDescriptor() && ((XMLDescriptor) concreteDescriptor).getPrimaryKeyFieldNames().size() > 0) {
            if ((pk == null) || (((CacheId) pk).getPrimaryKey().length == 0)) {
                pk = new CacheId(new Object[] { new WeakObjectWrapper(domainObject) });
            }
            CacheKey key = query.getSession().getIdentityMapAccessorInstance().acquireDeferredLock(pk, concreteDescriptor.getJavaClass(), concreteDescriptor, query.isCacheCheckComplete());
            if (((XMLDescriptor) concreteDescriptor).shouldPreserveDocument()) {
                key.setRecord(databaseRow);
            }
            key.setObject(domainObject);
            key.releaseDeferredLock();
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

        writeOutMappings(row, object, session);


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
    @Override
    public void buildAttributesIntoObject(Object domainObject, CacheKey cacheKey, AbstractRecord databaseRow, ObjectBuildingQuery query, JoinedAttributeManager joinManager, boolean forRefresh, AbstractSession targetSession) throws DatabaseException {
        ((XMLRecord)databaseRow).setSession(query.getSession().getExecutionSession(query));
        super.buildAttributesIntoObject(domainObject, cacheKey, databaseRow, query, joinManager, forRefresh, targetSession);
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

    public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object object, AbstractSession session, XMLDescriptor refDesc, XMLField xmlField) {
        return buildIntoNestedRow(row, null, object, session, refDesc, xmlField, false);
    }
   public AbstractRecord buildIntoNestedRow(AbstractRecord row, Object originalObject, Object object, AbstractSession session, XMLDescriptor refDesc, XMLField xmlField, boolean wasXMLRoot) {
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
            extraNamespaces = addExtraNamespacesToNamespaceResolver(xmlDescriptor, record, session, false, false);
            writeExtraNamespaces(extraNamespaces, record);
            addXsiTypeAndClassIndicatorIfRequired(record, xmlDescriptor, refDesc, xmlField, originalObject, object, wasXMLRoot, false);
        }

        writeOutMappings(record, object, session);
        // If this descriptor is involved in inheritance add the class type.
        if (isXmlDescriptor()) {
            XMLDescriptor xmlDescriptor = (XMLDescriptor)getDescriptor();
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

   protected void writeXsiTypeAttribute(XMLDescriptor xmlDescriptor, XMLRecord row, QName typeValueQName, boolean addToNamespaceResolver) {
       if (typeValueQName == null){
           return;
       }
       String typeValue = typeValueQName.getLocalPart();
       String uri = typeValueQName.getNamespaceURI();
       if(row.isNamespaceAware() && uri != null && !uri.equals(XMLConstants.EMPTY_STRING) && !uri.equals(row.getNamespaceResolver().getDefaultNamespaceURI())){
           String prefix = row.getNamespaceResolver().resolveNamespaceURI(uri);
           if(prefix != null && !prefix.equals(XMLConstants.EMPTY_STRING)){
               typeValue = prefix + row.getNamespaceSeparator() + typeValue;
           } else if (uri.equals(XMLConstants.SCHEMA_URL)) {
               prefix = row.getNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_PREFIX);
               typeValue = prefix + row.getNamespaceSeparator() + typeValue;
               writeNamespace(row, prefix, uri, addToNamespaceResolver);
           }else if (typeValueQName.getPrefix() != null && !typeValueQName.getPrefix().equals(XMLConstants.EMPTY_STRING)){
               typeValue = typeValueQName.getPrefix() + row.getNamespaceSeparator() + typeValue;
               writeNamespace(row, typeValueQName.getPrefix(), uri, addToNamespaceResolver);
           }
       }
       writeXsiTypeAttribute(xmlDescriptor, row, typeValue, addToNamespaceResolver);
   }

    protected void writeXsiTypeAttribute(XMLDescriptor xmlDescriptor, XMLRecord row, String typeValue, boolean addToNamespaceResolver) {
        if (xmlDescriptor == null) {
            writeXsiTypeAttribute(row, typeValue, addToNamespaceResolver);
            return;
        }
        XMLField xmlField = null;
        if(row.isNamespaceAware()){
            String xsiPrefix = null;
            boolean generated = false;

            xsiPrefix = row.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
            if (xsiPrefix == null) {
                xsiPrefix = xmlDescriptor.getNonNullNamespaceResolver().generatePrefix(XMLConstants.SCHEMA_INSTANCE_PREFIX);
                generated = true;
                writeNamespace(row, xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL, addToNamespaceResolver);
            }
            xmlField = (XMLField)xmlDescriptor.buildField(XMLConstants.ATTRIBUTE + xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
            if (generated) {
                xmlField.getLastXPathFragment().setGeneratedPrefix(true);
            }
        }else{
            xmlField = (XMLField)xmlDescriptor.buildField(XMLConstants.ATTRIBUTE + XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
        }

        xmlField.getLastXPathFragment().setNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        row.add(xmlField, typeValue);
    }

    protected void writeXsiTypeAttribute(XMLRecord row, String typeValue, boolean addToNamespaceResolver) {
        XMLField xmlField = null;
        if (row.isNamespaceAware()){
            String xsiPrefix = null;
            boolean generated = false;

            xsiPrefix = row.getNamespaceResolver().resolveNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
            if (xsiPrefix == null) {
                xsiPrefix = XMLConstants.SCHEMA_INSTANCE_PREFIX;
                generated = true;
                writeNamespace(row, xsiPrefix, XMLConstants.SCHEMA_INSTANCE_URL, addToNamespaceResolver);
            }
            xmlField = new XMLField(XMLConstants.ATTRIBUTE + xsiPrefix + XMLConstants.COLON + XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
            if (generated) {
                xmlField.getLastXPathFragment().setGeneratedPrefix(true);
            }
        } else {
            xmlField = new XMLField(XMLConstants.ATTRIBUTE + XMLConstants.SCHEMA_TYPE_ATTRIBUTE);
        }

        xmlField.getLastXPathFragment().setNamespaceURI(XMLConstants.SCHEMA_INSTANCE_URL);
        row.add(xmlField, typeValue);
    }

    protected XMLField writeNamespace(XMLRecord nestedRecord, String prefix, String url, boolean addToNamespaceResolver) {
    	
    	String existingPrefix = nestedRecord.getNamespaceResolver().resolveNamespaceURI(url);
    	if(existingPrefix == null || (existingPrefix != null && !existingPrefix.equals(XMLConstants.EMPTY_STRING) && !existingPrefix.equals(prefix))){
    	
            XMLField xmlField = new XMLField("@" + XMLConstants.XMLNS + XMLConstants.COLON + prefix);
            xmlField.setNamespaceResolver(nestedRecord.getNamespaceResolver());
            xmlField.getXPathFragment().setNamespaceURI(XMLConstants.XMLNS_URL);
            nestedRecord.add(xmlField, url);
            if(addToNamespaceResolver){
                nestedRecord.getNamespaceResolver().put(prefix, url);
            }
            return xmlField;
    	}
        return null;
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
    
    protected List addExtraNamespacesToNamespaceResolver(XMLDescriptor desc, XMLRecord marshalRecord, AbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers) {
        if (marshalRecord.hasEqualNamespaceResolvers() && !ignoreEqualResolvers) {
            return null;
        }

        NamespaceResolver descriptorNamespaceResolver = desc.getNamespaceResolver();
        if(null == descriptorNamespaceResolver || !descriptorNamespaceResolver.hasPrefixesToNamespaces()) {
            return null;
        }
        Map<String, String> prefixesToNamespaces = descriptorNamespaceResolver.getPrefixesToNamespaces();
        if(prefixesToNamespaces.size() == 0) {
            return null;
        }
        List returnList = new ArrayList(prefixesToNamespaces.size());
        NamespaceResolver marshalRecordNamespaceResolver = marshalRecord.getNamespaceResolver();
        for(Entry<String, String> entry: prefixesToNamespaces.entrySet()) {

            //if isn't already on a parentadd namespace to this element
            String prefix = marshalRecordNamespaceResolver.resolveNamespaceURI(entry.getValue());

            if (prefix == null || prefix.length() == 0) {
                //if there is no prefix already declared for this uri in the nr add this one
                //unless that prefix is already bound to another namespace uri
                prefix = entry.getKey();
                if(marshalRecord.hasCustomNamespaceMapper()) {
                    String newPrefix = marshalRecord.getMarshaller().getNamespacePrefixMapper().getPreferredPrefix(entry.getValue(), prefix, true);
                    if(newPrefix != null && !(newPrefix.length() == 0)) {
                        prefix = newPrefix;
                    }
                }
                String uri = marshalRecordNamespaceResolver.resolveNamespacePrefix(prefix);
                if(marshalRecord.hasCustomNamespaceMapper() || allowOverride || uri == null || uri.length() == 0) {
                    //if this uri is unknown, the cutom mapper will return the preferred prefix for this uri
                    marshalRecordNamespaceResolver.put(entry.getKey(), entry.getValue());
                    returnList.add(new Namespace(prefix, entry.getValue()));
                }
            } else if(allowOverride) {
                //if overrides are allowed, add the prefix if the URI is different
                if (!prefix.equals(entry.getKey()) && !(marshalRecord.hasCustomNamespaceMapper())) {
                    //if prefix exists for uri but is different then add this
                    //unless using a custom namespace prefix mapper. Then prefix is expected to be different
                    marshalRecordNamespaceResolver.put(entry.getKey(), entry.getValue());
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
            XMLField indicatorField = (XMLField) descriptor.getInheritancePolicy().getClassIndicatorField();
            if(indicatorField != null){
               if (indicatorField.getLastXPathFragment().getNamespaceURI() != null && indicatorField.getLastXPathFragment().getNamespaceURI().equals(XMLConstants.SCHEMA_INSTANCE_URL)
                        && indicatorField.getLastXPathFragment().getLocalName().equals(XMLConstants.SCHEMA_TYPE_ATTRIBUTE)){
                     xsiTypeIndicatorField = true;
               }
               
            }
        }
    }

    public boolean isXMLObjectBuilder() {
        return true;
    }

    /**
     * @param record The XMLRecord to write to
     * @param xmlDescriptor The Descriptor of the object we are writing
     * @param referenceDescriptor The known descriptor for this mapping (if it exisits)
     * @param xmlField
     * @param originalObject Could be an XMLRoot otherwise the same as obj
     * @param obj The object being marshalled
     * @param wasXMLRoot boolean if the originalObject was an XMLRoot
     * @param addToNamespaceResolver boolean if we should add generated namespaces to the NamespaceResolver
     */
    public boolean addXsiTypeAndClassIndicatorIfRequired(XMLRecord record, XMLDescriptor xmlDescriptor, XMLDescriptor referenceDescriptor, XMLField xmlField, Object originalObject, Object obj, boolean wasXMLRoot, boolean addToNamespaceResolver) {
        if (wasXMLRoot) {
            XMLSchemaReference xmlRef = xmlDescriptor.getSchemaReference();

            if (descriptor != null) {
                XMLRoot xr = (XMLRoot) originalObject;

                if (xmlRef == null) {
                    return false;
                }
                String xmlRootLocalName = xr.getLocalName();
                String xmlRootUri = xr.getNamespaceURI();

                XPathQName qName = new XPathQName(xmlRootUri, xmlRootLocalName, record.isNamespaceAware());
                XMLDescriptor xdesc = record.getMarshaller().getXMLContext().getDescriptor(qName);
                if (xdesc != null) {
                    boolean writeTypeAttribute = xdesc.getJavaClass() != descriptor.getJavaClass();
                    if (writeTypeAttribute) {
                        QName typeValueQName = getTypeValueToWriteAsQName(record, xdesc, xmlRef, addToNamespaceResolver);
                        writeXsiTypeAttribute(xmlDescriptor, record, typeValueQName, addToNamespaceResolver);
                        return true;
                    }
                    return false;

                }

                if (xr.getDeclaredType() != null && xr.getDeclaredType() == xr.getObject().getClass()) {
                    return false;
                }

                boolean writeTypeAttribute = true;
                int tableSize = descriptor.getTableNames().size();
                for (int i = 0; i < tableSize; i++) {
                    if (!writeTypeAttribute) {
                        return false;
                    }
                    String defaultRootQualifiedName = (String) xmlDescriptor.getTableNames().get(i);
                    if (defaultRootQualifiedName != null) {
                        String defaultRootLocalName = null;
                        String defaultRootUri = null;
                        int colonIndex = defaultRootQualifiedName.indexOf(XMLConstants.COLON);
                        if (colonIndex > 0) {
                            String defaultRootPrefix = defaultRootQualifiedName.substring(0, colonIndex);
                            defaultRootLocalName = defaultRootQualifiedName.substring(colonIndex + 1);
                            if (xmlDescriptor.getNamespaceResolver() != null) {
                                defaultRootUri = xmlDescriptor.getNamespaceResolver().resolveNamespacePrefix(defaultRootPrefix);
                            }
                        } else {
                            defaultRootLocalName = defaultRootQualifiedName;
                        }

                        if (xmlRootLocalName != null) {
                            if ((((defaultRootLocalName == null) && (xmlRootLocalName == null)) || (defaultRootLocalName.equals(xmlRootLocalName)))
                                && (((defaultRootUri == null) && (xmlRootUri == null)) || ((xmlRootUri != null) && (defaultRootUri != null) && (defaultRootUri.equals(xmlRootUri))))) {
                                // if both local name and uris are equal then don't need to write type attribute
                                return false;
                            }
                        }
                    } else {
                        // no default rootElement was set
                        // if xmlRootName = null then writeTypeAttribute = false
                        if (xmlRootLocalName == null) {
                            return false;
                        }
                    }
                }
                if (writeTypeAttribute && xmlRef != null) {
                    QName typeValueQName = getTypeValueToWriteAsQName(record, xmlDescriptor, xmlRef, addToNamespaceResolver);
                    writeXsiTypeAttribute(xmlDescriptor, record, typeValueQName, addToNamespaceResolver);

                    return true;
                }
            }
            return false;
        } else {
            return addXsiTypeAndClassIndicatorIfRequired(record, xmlDescriptor, referenceDescriptor, xmlField, addToNamespaceResolver);
        }
    }

    public boolean addXsiTypeAndClassIndicatorIfRequired(XMLRecord record, XMLDescriptor xmlDescriptor, XMLDescriptor referenceDescriptor, XMLField xmlField, boolean addToNamespaceResolver) {
        if (descriptor.hasInheritance() && !xsiTypeIndicatorField) {
            xmlDescriptor.getInheritancePolicy().addClassIndicatorFieldToRow(record);
            return true;
        }

        QName leafType = null;
        if (xmlField != null) {
            leafType = xmlField.getLeafElementType();

            XMLSchemaReference xmlRef = xmlDescriptor.getSchemaReference();
            if (xmlRef != null) {
                if (leafType == null) {
                    if (xmlRef.getType() == XMLSchemaReference.ELEMENT) {
                        return false;
                    }
                    if (referenceDescriptor == null) {
                        QName typeValueQName = getTypeValueToWriteAsQName(record, xmlDescriptor, xmlRef, addToNamespaceResolver);
                        writeXsiTypeAttribute(xmlDescriptor, record, typeValueQName, addToNamespaceResolver);
                        return true;
                    }
                } else if (((xmlRef.getType() == XMLSchemaReference.COMPLEX_TYPE) || (xmlRef.getType() == XMLSchemaReference.SIMPLE_TYPE)) && xmlRef.getSchemaContext() != null && xmlRef.isGlobalDefinition()) {
                    QName ctxQName = xmlRef.getSchemaContextAsQName(xmlDescriptor.getNamespaceResolver());
                    if (!ctxQName.equals(leafType)) {
                        QName typeValueQName = getTypeValueToWriteAsQName(record, xmlDescriptor, xmlRef, addToNamespaceResolver);
                        writeXsiTypeAttribute(xmlDescriptor, record, typeValueQName, addToNamespaceResolver);
                        return true;
                    }
                }
            }
        }

        if (referenceDescriptor != null && referenceDescriptor == xmlDescriptor) {
            return false;
        }
        if (xmlDescriptor.hasInheritance() && !xmlDescriptor.getInheritancePolicy().isRootParentDescriptor()) {
            InheritancePolicy inheritancePolicy = xmlDescriptor.getInheritancePolicy();
            XMLField indicatorField = (XMLField) inheritancePolicy.getClassIndicatorField();
            if (indicatorField != null && xsiTypeIndicatorField) {
                Object classIndicatorValueObject = inheritancePolicy.getClassIndicatorMapping().get(xmlDescriptor.getJavaClass());
                QName classIndicatorQName = null;
                if (classIndicatorValueObject instanceof QName) {
                    classIndicatorQName = (QName) classIndicatorValueObject;

                } else {
                    String classIndicatorValue = (String) inheritancePolicy.getClassIndicatorMapping().get(xmlDescriptor.getJavaClass());
                    int nsindex = classIndicatorValue.indexOf(XMLConstants.COLON);
                    String localName = null;
                    String prefix = null;
                    if (nsindex != -1) {
                        localName = classIndicatorValue.substring(nsindex + 1);
                        prefix = classIndicatorValue.substring(0, nsindex);
                    } else {
                        localName = classIndicatorValue;
                    }
                    String namespaceURI = xmlDescriptor.getNonNullNamespaceResolver().resolveNamespacePrefix(prefix);
                    classIndicatorQName = new QName(namespaceURI, localName);
                }
                if (leafType == null || !classIndicatorQName.equals(leafType)) {
                    if (inheritancePolicy.hasClassExtractor()) {
                        inheritancePolicy.addClassIndicatorFieldToRow(record);
                    } else {
                        writeXsiTypeAttribute(xmlDescriptor, record, classIndicatorQName, addToNamespaceResolver);
                    }
                    return true;
                }
                return false;
            }

        }
        return false;
    }

    private QName getTypeValueToWriteAsQName(XMLRecord record, XMLDescriptor descriptorToWrite, XMLSchemaReference xmlRef, boolean addToNamespaceResolver){
            QName contextAsQName = xmlRef.getSchemaContextAsQName();
            if(contextAsQName == null){
            	contextAsQName = xmlRef.getSchemaContextAsQName(record.getNamespaceResolver());
            }
            if (contextAsQName != null) {
                String uri = contextAsQName.getNamespaceURI();
                String localPart = contextAsQName.getLocalPart();
                String prefix = record.getNamespaceResolver().resolveNamespaceURI(uri);
                if (prefix == null) {
                    String defaultUri = record.getNamespaceResolver().getDefaultNamespaceURI();
                    if (defaultUri != null && defaultUri.equals(uri)) {
                        return new QName(localPart);
                    }else if(XMLConstants.EMPTY_STRING.equals(uri)){
                    	return new QName(localPart);
                    }else{
                        prefix = record.getNamespaceResolver().generatePrefix();
                        return new QName(uri, localPart, prefix);                        
                    }
                } else {
                	 return new QName(uri, localPart, prefix);
                }
            }
            return null;
    }
}
