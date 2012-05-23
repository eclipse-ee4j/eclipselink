/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2010-03-04 12:22:11 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.dynamic;

import java.util.ArrayList;

import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLAnyAttributeMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLBinaryDataMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLFragmentMapping;
import org.eclipse.persistence.oxm.mappings.XMLInverseReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.jaxb.dynamic.util.AttributeTransformer;
import org.eclipse.persistence.testing.jaxb.dynamic.util.FirstFieldTransformer;
import org.eclipse.persistence.testing.jaxb.dynamic.util.SecondFieldTransformer;

public class TestModelProject extends Project {

    private NamespaceResolver nsResolver;

    public TestModelProject() {
        super();

        nsResolver = new NamespaceResolver();
        nsResolver.put("ns0", "myNamespace");
        nsResolver.put("xsi", XMLConstants.SCHEMA_INSTANCE_URL);

        addDocWrapperDescriptor();
        addRootDescriptor();
        addCompositeObjectTargetDescriptor();
        addCompositeCollectionTargetDescriptor();
        addAnyObjectTargetDescriptor();
        addAnyCollectionTargetDescriptor();
        addAnyAttributeTargetDescriptor();
        addObjectReferenceTargetDescriptor();
        addObjectReferenceSubclassTargetDescriptor();
        addCollectionReferenceTargetDescriptor();
    }

    public void addDocWrapperDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.DocWrapper");
        descriptor.setDefaultRootElement("ns0:doc-wrapper");
        descriptor.setNamespaceResolver(nsResolver);

        XMLCompositeObjectMapping root = new XMLCompositeObjectMapping();
        root.setAttributeName("root");
        root.setXPath("ns0:root");
        root.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.Root");
        descriptor.addMapping(root);

        XMLCompositeObjectMapping objRefTarget = new XMLCompositeObjectMapping();
        objRefTarget.setAttributeName("objRefTarget");
        objRefTarget.setXPath("obj-ref-target");
        objRefTarget.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceTarget");
        descriptor.addMapping(objRefTarget);

        XMLCompositeCollectionMapping collRefTarget = new XMLCompositeCollectionMapping();
        collRefTarget.setAttributeName("collRefTarget");
        collRefTarget.setXPath("coll-ref-target");
        collRefTarget.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.CollectionReferenceTarget");
        descriptor.addMapping(collRefTarget);

        this.addDescriptor(descriptor);
    }

    public void addRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.Root");
        descriptor.setNamespaceResolver(nsResolver);

        XMLCompositeObjectMapping compObj = new XMLCompositeObjectMapping();
        compObj.setAttributeName("compObj");
        compObj.setXPath("comp-obj");
        compObj.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.CompositeObjectTarget");
        descriptor.addMapping(compObj);

        XMLCompositeCollectionMapping compColl = new XMLCompositeCollectionMapping();
        compColl.setAttributeName("compColl");
        compColl.setXPath("comp-coll/item");
        compColl.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.CompositeCollectionTarget");
        compColl.useCollectionClass(ArrayList.class);
        compColl.setContainerPolicy(ContainerPolicy.buildPolicyFor(ArrayList.class));
        descriptor.addMapping(compColl);

        XMLCompositeDirectCollectionMapping compDirColl = new XMLCompositeDirectCollectionMapping();
        compDirColl.setAttributeName("compDirColl");
        compDirColl.setXPath("comp-dir-coll");
        descriptor.addMapping(compDirColl);

        XMLBinaryDataMapping binData = new XMLBinaryDataMapping();
        binData.setAttributeName("binData");
        XMLField binDataField = new XMLField("bin-data");
        binDataField.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        binData.setField(binDataField);
        binData.setShouldInlineBinaryData(true);
        descriptor.addMapping(binData);

        XMLBinaryDataCollectionMapping binDataColl = new XMLBinaryDataCollectionMapping();
        binDataColl.setAttributeName("binDataColl");
        XMLField binDataCollField = new XMLField("bin-data-coll");
        binDataCollField.setSchemaType(XMLConstants.BASE_64_BINARY_QNAME);
        binDataColl.setField(binDataCollField);
        binDataColl.setShouldInlineBinaryData(true);
        descriptor.addMapping(binDataColl);

        XMLAnyObjectMapping anyObj = new XMLAnyObjectMapping();
        anyObj.setAttributeName("anyObj");
        anyObj.setField(new XMLField("any-obj"));
        descriptor.addMapping(anyObj);

        XMLCompositeObjectMapping anyAtt = new XMLCompositeObjectMapping();
        anyAtt.setAttributeName("anyAtt");
        anyAtt.setXPath("any-att");
        anyAtt.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.AnyAttributeTarget");
        descriptor.addMapping(anyAtt);

        XMLTransformationMapping transform = new XMLTransformationMapping();
        transform.setAttributeName("transform");
        transform.setAttributeTransformer(new AttributeTransformer());
        transform.addFieldTransformer("transform/first-val/text()", new FirstFieldTransformer());
        transform.addFieldTransformer("transform/second-val/text()", new SecondFieldTransformer());
        descriptor.addMapping(transform);

        XMLFragmentMapping frag = new XMLFragmentMapping();
        frag.setAttributeName("frag");
        frag.setXPath("frag");
        descriptor.addMapping(frag);

        XMLFragmentCollectionMapping fragColl = new XMLFragmentCollectionMapping();
        fragColl.setAttributeName("fragColl");
        fragColl.setXPath("frag-coll");
        fragColl.useCollectionClass(ArrayList.class);
        descriptor.addMapping(fragColl);

        XMLObjectReferenceMapping objRef = new XMLObjectReferenceMapping();
        objRef.setAttributeName("objRef");
        objRef.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceTarget");
        objRef.addSourceToTargetKeyFieldAssociation("obj-ref-id/text()", "@id");
        descriptor.addMapping(objRef);

        XMLCollectionReferenceMapping collRef = new XMLCollectionReferenceMapping();
        collRef.setAttributeName("collRef");
        collRef.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.CollectionReferenceTarget");
        collRef.addSourceToTargetKeyFieldAssociation("coll-ref-id/text()", "@id");
        descriptor.addMapping(collRef);

        XMLChoiceObjectMapping choice = new XMLChoiceObjectMapping();
        choice.setAttributeName("choice");
        choice.addChoiceElement("choice-int/text()", Integer.class);
        choice.addChoiceElement("choice-float/text()", Float.class);
        descriptor.addMapping(choice);

        XMLChoiceCollectionMapping choiceColl = new XMLChoiceCollectionMapping();
        choiceColl.setAttributeName("choiceColl");
        choiceColl.addChoiceElement("choice-coll-double/text()", Double.class);
        choiceColl.addChoiceElement("choice-coll-string/text()", String.class);
        choiceColl.addChoiceElement("choice-coll-boolean/text()", Boolean.class);
        descriptor.addMapping(choiceColl);

        this.addDescriptor(descriptor);
    }

    public void addCompositeObjectTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.CompositeObjectTarget");
        descriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping value = new XMLDirectMapping();
        value.setAttributeName("value");
        value.setXPath("value/text()");
        value.getNullPolicy().setNullRepresentedByXsiNil(true);
        value.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
        descriptor.addMapping(value);

        XMLAnyCollectionMapping anyColl = new XMLAnyCollectionMapping();
        anyColl.setAttributeName("anyColl");
        anyColl.setField(new XMLField("any-coll"));
        descriptor.addMapping(anyColl);

        this.addDescriptor(descriptor);
    }


    public void addCompositeCollectionTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.CompositeCollectionTarget");
        descriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping value = new XMLDirectMapping();
        value.setAttributeName("value");
        value.setXPath("value/text()");
        descriptor.addMapping(value);

        XMLInverseReferenceMapping invRef = new XMLInverseReferenceMapping();
        invRef.setReferenceClassName("org.persistence.testing.jaxb.dynamic.xxx.Root");
        invRef.setMappedBy("compColl");
        invRef.setAttributeName("invRef");
        descriptor.addMapping(invRef);

        this.addDescriptor(descriptor);
    }

    public void addAnyObjectTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.AnyObjectTarget");
        descriptor.setDefaultRootElement("any-obj-value");
        descriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping value = new XMLDirectMapping();
        value.setAttributeName("value");
        value.setXPath("text()");
        descriptor.addMapping(value);

        this.addDescriptor(descriptor);
    }

    public void addAnyCollectionTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.AnyCollectionTarget");
        descriptor.setDefaultRootElement("any-coll-value");
        descriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping value = new XMLDirectMapping();
        value.setAttributeName("value");
        value.setXPath("text()");
        descriptor.addMapping(value);

        this.addDescriptor(descriptor);
    }

    public void addAnyAttributeTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.AnyAttributeTarget");
        descriptor.setNamespaceResolver(nsResolver);

        XMLAnyAttributeMapping anyAtt = new XMLAnyAttributeMapping();
        anyAtt.setAttributeName("value");
        descriptor.addMapping(anyAtt);

        this.addDescriptor(descriptor);
    }

    public void addObjectReferenceTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceTarget");
        descriptor.addPrimaryKeyFieldName("@id");
        descriptor.setNamespaceResolver(nsResolver);

        descriptor.getInheritancePolicy().setClassIndicatorFieldName("@xsi:type");
        descriptor.getInheritancePolicy().addClassNameIndicator("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceTarget", "superclass");
        descriptor.getInheritancePolicy().addClassNameIndicator("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceSubclassTarget", "subclass");

        XMLDirectMapping id = new XMLDirectMapping();
        id.setAttributeName("id");
        id.setXPath("@id");
        descriptor.addMapping(id);

        XMLDirectMapping superclassValue = new XMLDirectMapping();
        superclassValue.setAttributeName("superclassValue");
        superclassValue.setXPath("superclass-value/text()");
        descriptor.addMapping(superclassValue);

        this.addDescriptor(descriptor);
    }

    public void addObjectReferenceSubclassTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceSubclassTarget");
        descriptor.setNamespaceResolver(nsResolver);

        descriptor.getInheritancePolicy().setParentClassName("org.persistence.testing.jaxb.dynamic.xxx.ObjectReferenceTarget");

        XMLDirectMapping subclassValue = new XMLDirectMapping();
        subclassValue.setAttributeName("subclassValue");
        subclassValue.setXPath("subclass-value/text()");
        descriptor.addMapping(subclassValue);

        this.addDescriptor(descriptor);
    }

    public void addCollectionReferenceTargetDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClassName("org.persistence.testing.jaxb.dynamic.xxx.CollectionReferenceTarget");
        descriptor.addPrimaryKeyFieldName("@id");
        descriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping id = new XMLDirectMapping();
        id.setAttributeName("id");
        id.setXPath("@id");
        descriptor.addMapping(id);

        XMLDirectMapping value = new XMLDirectMapping();
        value.setAttributeName("value");
        value.setXPath("value/text()");
        descriptor.addMapping(value);

        this.addDescriptor(descriptor);
    }

}