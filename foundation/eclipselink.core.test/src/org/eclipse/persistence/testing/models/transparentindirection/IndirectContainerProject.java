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
package org.eclipse.persistence.testing.models.transparentindirection;

import java.util.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * override the generated methods with hand-modified methods;
 * allow different container policies (Collection/Vector, Map/Hashtable)
 */
public abstract class IndirectContainerProject extends GeneratedIndirectContainerProject {
    public IndirectContainerProject() {
        super();
    }

    /**
     * modifications are marked with "bjv"
     */
    protected void buildOrderDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(this.orderClass());// bjv
        Vector vector = new Vector();
        vector.addElement("ORD");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("ORD.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("order_seq");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTCOLLECTIONMAPPING
        org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping = new org.eclipse.persistence.mappings.DirectCollectionMapping();
        directcollectionmapping.setAttributeName("contacts");
        directcollectionmapping.setIsReadOnly(false);
        directcollectionmapping.setUsesIndirection(false);
        directcollectionmapping.setIsPrivateOwned(true);
        this.configureContactContainer(directcollectionmapping);// bjv
        directcollectionmapping.setDirectFieldName("CONTACT.NAME");
        directcollectionmapping.setReferenceTableName("CONTACT");
        directcollectionmapping.addReferenceKeyFieldName("CONTACT.ORDER_ID", "ORD.ID");
        descriptor.addMapping(directcollectionmapping);

        // SECTION: DIRECTCOLLECTIONMAPPING
        org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping1 = new org.eclipse.persistence.mappings.DirectCollectionMapping();
        directcollectionmapping1.setAttributeName("contacts2");
        directcollectionmapping1.setIsReadOnly(false);
        directcollectionmapping1.setUsesIndirection(false);
        directcollectionmapping1.setIsPrivateOwned(true);
        this.configureContactContainer2(directcollectionmapping1);// bjv
        directcollectionmapping1.setDirectFieldName("CONTACT2.NAME");
        directcollectionmapping1.setReferenceTableName("CONTACT2");
        directcollectionmapping1.addReferenceKeyFieldName("CONTACT2.ORDER_ID", "ORD.ID");
        descriptor.addMapping(directcollectionmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("customerName");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("ORD.CUSTNAME");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("id");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("ORD.ID");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: MANYTOMANYMAPPING
        org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping = new org.eclipse.persistence.mappings.ManyToManyMapping();
        manytomanymapping.setAttributeName("salesReps");
        manytomanymapping.setIsReadOnly(false);
        manytomanymapping.setUsesIndirection(false);
        manytomanymapping.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.SalesRep.class);
        manytomanymapping.setIsPrivateOwned(false);
        this.configureSalesRepContainer(manytomanymapping);// bjv
        manytomanymapping.setRelationTableName("ORDREP");
        manytomanymapping.addSourceRelationKeyFieldName("ORDREP.ORDER_ID", "ORD.ID");
        manytomanymapping.addTargetRelationKeyFieldName("ORDREP.SALEREP_ID", "SALEREP.ID");
        descriptor.addMapping(manytomanymapping);

        // SECTION: MANYTOMANYMAPPING
        org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping1 = new org.eclipse.persistence.mappings.ManyToManyMapping();
        manytomanymapping1.setAttributeName("salesReps2");
        manytomanymapping1.setIsReadOnly(false);
        manytomanymapping1.setUsesIndirection(false);
        manytomanymapping1.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.SalesRep.class);
        manytomanymapping1.setIsPrivateOwned(false);
        this.configureSalesRepContainer2(manytomanymapping1);// bjv
        manytomanymapping1.setRelationTableName("ORDREP2");
        manytomanymapping1.addSourceRelationKeyFieldName("ORDREP2.ORDER_ID", "ORD.ID");
        manytomanymapping1.addTargetRelationKeyFieldName("ORDREP2.SALEREP_ID", "SALEREP.ID");
        descriptor.addMapping(manytomanymapping1);

        // SECTION: ONETOMANYMAPPING
        org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping = new org.eclipse.persistence.mappings.OneToManyMapping();
        onetomanymapping.setAttributeName("lines");
        onetomanymapping.setIsReadOnly(false);
        onetomanymapping.setUsesIndirection(false);
        onetomanymapping.setReferenceClass(org.eclipse.persistence.testing.models.transparentindirection.OrderLine.class);
        onetomanymapping.setIsPrivateOwned(true);
        this.configureLineContainer(onetomanymapping);// bjv
        onetomanymapping.addTargetForeignKeyFieldName("ORDLINE.ORDER_ID", "ORD.ID");
        descriptor.addMapping(onetomanymapping);

        // SECTION: TRANSFORMATIONMAPPING
        org.eclipse.persistence.mappings.TransformationMapping transformationmapping = new org.eclipse.persistence.mappings.TransformationMapping();
        transformationmapping.setAttributeName("total");
        transformationmapping.setIsReadOnly(false);
        transformationmapping.setUsesIndirection(true);
        transformationmapping.setAttributeTransformation("getTotalFromRow");
        transformationmapping.addFieldTransformation("ORD.TOTT", "getTotalTens");
        transformationmapping.addFieldTransformation("ORD.TOTO", "getTotalOnes");
        descriptor.addMapping(transformationmapping);

        // SECTION: TRANSFORMATIONMAPPING
        org.eclipse.persistence.mappings.TransformationMapping transformationmapping2 = new org.eclipse.persistence.mappings.TransformationMapping();
        transformationmapping2.setAttributeName("total2");
        transformationmapping2.setIsReadOnly(false);
        transformationmapping2.setUsesIndirection(false);
        transformationmapping2.setAttributeTransformation("getTotalFromRow2");
        transformationmapping2.addFieldTransformation("ORD.TOTT2", "getTotalTens2");
        transformationmapping2.addFieldTransformation("ORD.TOTO2", "getTotalOnes2");
        descriptor.addMapping(transformationmapping2);

        this.modifyOrderDescriptor(descriptor);// bjv
        addDescriptor(descriptor);
    }

    /**
     * modifications are marked with "bjv"
     */
    protected void buildOrderLineDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.transparentindirection.OrderLine.class);
        Vector vector = new Vector();
        vector.addElement("ORDLINE");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("ORDLINE.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("orderline");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("ORDLINE.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("itemName");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("ORDLINE.ITEM_NAME");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping2 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping2.setAttributeName("quantity");
        directtofieldmapping2.setIsReadOnly(false);
        directtofieldmapping2.setFieldName("ORDLINE.QUANTITY");
        descriptor.addMapping(directtofieldmapping2);

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("order");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(false);
        onetoonemapping.setReferenceClass(this.orderClass());// bjv
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("ORDLINE.ORDER_ID", "ORD.ID");
        descriptor.addMapping(onetoonemapping);
        addDescriptor(descriptor);
    }

    /**
     * modifications are marked with "bjv"
     */
    protected void buildSalesRepDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        // SECTION: DESCRIPTOR
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.transparentindirection.SalesRep.class);
        Vector vector = new Vector();
        vector.addElement("SALEREP");
        descriptor.setTableNames(vector);
        descriptor.addPrimaryKeyFieldName("SALEREP.ID");

        // SECTION: PROPERTIES
        descriptor.setIdentityMapClass(org.eclipse.persistence.internal.identitymaps.FullIdentityMap.class);
        descriptor.setSequenceNumberName("salesrep");
        descriptor.setSequenceNumberFieldName("ID");
        descriptor.setExistenceChecking("Check cache");
        descriptor.setIdentityMapSize(100);

        // SECTION: COPY POLICY
        descriptor.createCopyPolicy("constructor");

        // SECTION: INSTANTIATION POLICY
        descriptor.createInstantiationPolicy("constructor");

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping.setAttributeName("id");
        directtofieldmapping.setIsReadOnly(false);
        directtofieldmapping.setFieldName("SALEREP.ID");
        descriptor.addMapping(directtofieldmapping);

        // SECTION: DIRECTTOFIELDMAPPING
        org.eclipse.persistence.mappings.DirectToFieldMapping directtofieldmapping1 = new org.eclipse.persistence.mappings.DirectToFieldMapping();
        directtofieldmapping1.setAttributeName("name");
        directtofieldmapping1.setIsReadOnly(false);
        directtofieldmapping1.setFieldName("SALEREP.NAME");
        descriptor.addMapping(directtofieldmapping1);

        // SECTION: MANYTOMANYMAPPING
        org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping = new org.eclipse.persistence.mappings.ManyToManyMapping();
        manytomanymapping.setAttributeName("orders");
        manytomanymapping.setIsReadOnly(true);
        manytomanymapping.setUsesIndirection(false);
        manytomanymapping.setReferenceClass(this.orderClass());// bjv
        manytomanymapping.setIsPrivateOwned(false);
        manytomanymapping.useCollectionClass(java.util.Vector.class);
        manytomanymapping.setRelationTableName("ORDREP");
        manytomanymapping.addSourceRelationKeyFieldName("ORDREP.SALEREP_ID", "SALEREP.ID");
        manytomanymapping.addTargetRelationKeyFieldName("ORDREP.ORDER_ID", "ORD.ID");
        descriptor.addMapping(manytomanymapping);

        // SECTION: MANYTOMANYMAPPING
        org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping1 = new org.eclipse.persistence.mappings.ManyToManyMapping();
        manytomanymapping1.setAttributeName("orders2");
        manytomanymapping1.setIsReadOnly(true);
        manytomanymapping1.setUsesIndirection(false);
        manytomanymapping1.setReferenceClass(this.orderClass());// bjv
        manytomanymapping1.setIsPrivateOwned(false);
        manytomanymapping1.useCollectionClass(java.util.Vector.class);
        manytomanymapping1.setRelationTableName("ORDREP2");
        manytomanymapping1.addSourceRelationKeyFieldName("ORDREP2.SALEREP_ID", "SALEREP.ID");
        manytomanymapping1.addTargetRelationKeyFieldName("ORDREP2.ORDER_ID", "ORD.ID");
        descriptor.addMapping(manytomanymapping1);
        addDescriptor(descriptor);
    }

    protected abstract void configureContactContainer(org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping);

    protected void configureContactContainer2(org.eclipse.persistence.mappings.DirectCollectionMapping directcollectionmapping) {
        directcollectionmapping.useCollectionClass(java.util.Stack.class);
    }

    protected abstract void configureLineContainer(org.eclipse.persistence.mappings.OneToManyMapping onetomanymapping);

    protected abstract void configureSalesRepContainer(org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping);

    protected void configureSalesRepContainer2(org.eclipse.persistence.mappings.ManyToManyMapping manytomanymapping) {
        manytomanymapping.useMapClass(org.eclipse.persistence.testing.tests.transparentindirection.TestHashtable.class, "getKey");
    }

    protected abstract void modifyOrderDescriptor(RelationalDescriptor descriptor);

    protected abstract Class orderClass();
}
