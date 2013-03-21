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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.persistence.core.descriptors.CoreInheritancePolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.mappings.Field;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.MarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.record.NodeRecord;
import org.eclipse.persistence.oxm.record.UnmarshalRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * INTERNAL:
 * <p><b>Purpose</b>:  Perform the unmarshal and marshal operations based on the
 * object-to-XML mapping metadata.</p>
 * <p><b>Responsibilities</b>:<ul>
 * <li>Convert mapping metadata to a tree of XPathNodes.  This tree is then
 * traversed during unmarshal and marshal operations.</li>
 * <li>Create records appropriate to this implementation of ObjectBuilder.</li>
 * </ul>
 */
public class TreeObjectBuilder extends XMLObjectBuilder implements ObjectBuilder<AbstractRecord, AbstractSession, ClassDescriptor, XMLMarshaller> {

    private XPathObjectBuilder xPathObjectBuilder;

    public TreeObjectBuilder(ClassDescriptor descriptor) {
        super(descriptor);
        xPathObjectBuilder = new XPathObjectBuilder(descriptor);
    }

    @Override
    protected void initialize(ClassDescriptor descriptor) {
        int descriptorMappingsSize = descriptor.getMappings().size();
        this.mappingsByField = new HashMap(descriptorMappingsSize);
        this.fieldsMap = new HashMap(descriptorMappingsSize);
        this.cloningMappings = new ArrayList(descriptorMappingsSize);
    }

    public XPathNode getRootXPathNode() {
        return xPathObjectBuilder.getRootXPathNode();
    }

    @Override
    public List<DatabaseMapping> getPrimaryKeyMappings() {
        if(null == primaryKeyMappings) {
            primaryKeyMappings = new ArrayList<DatabaseMapping>(1);
        }
        return primaryKeyMappings;
    }

    public List getTransformationMappings() {
        return xPathObjectBuilder.getTransformationMappings();
    }

    public List getContainerValues() {
        return xPathObjectBuilder.getContainerValues();
    }

    public List getNullCapableValues() {
        return xPathObjectBuilder.getNullCapableValues();
    }

    public List getDefaultEmptyContainerValues() {
        return xPathObjectBuilder.getDefaultEmptyContainerValues();
    }

    public void initialize(org.eclipse.persistence.internal.sessions.AbstractSession session) {
        super.initialize(session);
        Descriptor xmlDescriptor = (Descriptor)getDescriptor();

        // INHERITANCE
        if (xmlDescriptor.hasInheritance()) {
            CoreInheritancePolicy inheritancePolicy = xmlDescriptor.getInheritancePolicy();
            
            if (!inheritancePolicy.hasClassExtractor()) {
                Field classIndicatorField = new XMLField(inheritancePolicy.getClassIndicatorFieldName());
                classIndicatorField.setNamespaceResolver(xmlDescriptor.getNamespaceResolver());
            }
        }

        if(!xmlDescriptor.isLazilyInitialized()) {
            xPathObjectBuilder.lazyInitialize();
        }
    }

    @Override
    public AbstractRecord buildRow(AbstractRecord record, Object object, org.eclipse.persistence.internal.sessions.AbstractSession session, WriteType writeType) {
        return (AbstractRecord) buildRow((XMLRecord) record, object, session, null, null);
    }

    public org.eclipse.persistence.internal.oxm.record.XMLRecord buildRow(org.eclipse.persistence.internal.oxm.record.XMLRecord record, Object object, CoreAbstractSession session, XMLMarshaller marshaller, XPathFragment rootFragment) {
        return xPathObjectBuilder.buildRow(record, object, session, marshaller, rootFragment);
    }

    public boolean marshalAttributes(MarshalRecord marshalRecord, Object object, CoreAbstractSession session) {
        return xPathObjectBuilder.marshalAttributes(marshalRecord, object, session);
    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(AbstractSession session) {
        xPathObjectBuilder.lazyInitialize();
        org.eclipse.persistence.internal.oxm.record.UnmarshalRecordImpl uRec = new org.eclipse.persistence.internal.oxm.record.UnmarshalRecordImpl(this);
        uRec.setSession(session);
        return new UnmarshalRecord(uRec);
    }

    /**
     * Create a new row/record for the object builder with the given name.
     * This allows subclasses to define different record types.
     */
    public AbstractMarshalRecord createRecord(String rootName, AbstractSession session) {
        NodeRecord nRec = new NodeRecord(rootName, getNamespaceResolver());
        nRec.setSession(session);
        return nRec;
    }

    /**
     * Create a new row/record for the object builder with the given name.
     * This allows subclasses to define different record types.
     */
    public AbstractMarshalRecord createRecord(String rootName, Node parent, AbstractSession session) {
        NodeRecord nRec = new NodeRecord(rootName, getNamespaceResolver(), parent);
        nRec.setSession(session);
        return nRec;
    }

    /**
     * Create a new row/record for the object builder.
     * This allows subclasses to define different record types.
     */
    public AbstractRecord createRecord(int size, AbstractSession session) {
        return createRecord(session);
    }

    @Override
    public List addExtraNamespacesToNamespaceResolver(Descriptor desc, AbstractMarshalRecord marshalRecord, CoreAbstractSession session, boolean allowOverride, boolean ignoreEqualResolvers) {
        return xPathObjectBuilder.addExtraNamespacesToNamespaceResolver(desc, marshalRecord, session, allowOverride, ignoreEqualResolvers);
    }

    @Override
    public boolean addClassIndicatorFieldToRow(AbstractMarshalRecord abstractMarshalRecord) {
        if (descriptor.hasInheritance() && !xPathObjectBuilder.isXsiTypeIndicatorField()) {
            InheritanceRecord inheritanceRecord = new InheritanceRecord(abstractMarshalRecord);
            descriptor.getInheritancePolicy().addClassIndicatorFieldToRow(inheritanceRecord);
            return true;
        }
        return false;
    }

    @Override
    public Class classFromRow(org.eclipse.persistence.internal.oxm.record.UnmarshalRecord unmarshalRecord, AbstractSession session) {
        UnmarshalRecord inheritanceRecord = new UnmarshalRecord(unmarshalRecord);
        return descriptor.getInheritancePolicy().classFromRow(inheritanceRecord, session);
    }

    private static class InheritanceRecord extends org.eclipse.persistence.oxm.record.XMLRecord {

        private AbstractMarshalRecord abstractMarshalRecord;

        public InheritanceRecord(AbstractMarshalRecord abstractMarshalRecord) {
            this.abstractMarshalRecord = abstractMarshalRecord;
        }

        @Override
        public String getLocalName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getNamespaceURI() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Document getDocument() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node getDOM() {
            throw new UnsupportedOperationException();
        }

        
        @Override
        public String transformToXML() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNamespaceAware() {
            return abstractMarshalRecord.isNamespaceAware();
        }

        @Override
        public boolean hasCustomNamespaceMapper() {
            return abstractMarshalRecord.hasCustomNamespaceMapper();
        }

        @Override
        public char getNamespaceSeparator() {
            return abstractMarshalRecord.getNamespaceSeparator();
        }

        @Override
        public Object put(DatabaseField key, Object value) {
            return abstractMarshalRecord.put(key, value);
        }
  
    }

}