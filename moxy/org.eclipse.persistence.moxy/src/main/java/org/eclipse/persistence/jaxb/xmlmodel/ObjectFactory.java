/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.jaxb.xmlmodel;

import java.util.List;
import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.eclipse.persistence.jaxb.xmlmodel package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@jakarta.xml.bind.annotation.XmlRegistry
public class ObjectFactory {

    private final static QName _XmlJavaTypeAdapter_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-java-type-adapter");
    private final static QName _XmlAttribute_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-attribute");
    private final static QName _XmlInverseReference_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-inverse-reference");
    private final static QName _JavaAttribute_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "java-attribute");
    private final static QName _XmlClassExtractor_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-class-extractor");
    private final static QName _XmlJoinNodes_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-join-nodes");
    private final static QName _XmlNullPolicy_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-null-policy");
    private final static QName _XmlElementRefs_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element-refs");
    private final static QName _XmlAnyElement_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-any-element");
    private final static QName _XmlSeeAlso_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-see-also");
    private final static QName _XmlValue_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-value");
    private final static QName _XmlAccessMethods_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-access-methods");
    private final static QName _XmlTransient_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-transient");
    private final static QName _XmlVariableNode_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-variable-node");
    private final static QName _XmlElement_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element");
    private final static QName _XmlAbstractNullPolicy_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-abstract-null-policy");
    private final static QName _XmlElementRef_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-element-ref");
    private final static QName _XmlIsSetNullPolicy_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-is-set-null-policy");
    private final static QName _XmlTransformation_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-transformation");
    private final static QName _XmlElements_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-elements");
    private final static QName _XmlProperties_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-properties");
    private final static QName _XmlAnyAttribute_QNAME = new QName("http://www.eclipse.org/eclipselink/xsds/persistence/oxm", "xml-any-attribute");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.persistence.jaxb.xmlmodel
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XmlSchemaType }
     *
     */
    public XmlSchemaType createXmlSchemaType() {
        return new XmlSchemaType();
    }

    /**
     * Create an instance of {@link XmlType }
     *
     */
    public XmlType createXmlType() {
        return new XmlType();
    }

    /**
     * Create an instance of {@link XmlElementRef }
     *
     */
    public XmlElementRef createXmlElementRef() {
        return new XmlElementRef();
    }

    /**
     * Create an instance of {@link XmlMap }
     *
     */
    public XmlMap createXmlMap() {
        return new XmlMap();
    }

    /**
     * Create an instance of {@link XmlSchemaTypes }
     *
     */
    public XmlSchemaTypes createXmlSchemaTypes() {
        return new XmlSchemaTypes();
    }

    /**
     * Create an instance of {@link XmlElements }
     *
     */
    public XmlElements createXmlElements() {
        return new XmlElements();
    }

    /**
     * Create an instance of {@link XmlTransformation.XmlReadTransformer }
     *
     */
    public XmlTransformation.XmlReadTransformer createXmlTransformationXmlReadTransformer() {
        return new XmlTransformation.XmlReadTransformer();
    }

    /**
     * Create an instance of {@link XmlElement }
     *
     */
    public XmlElement createXmlElement() {
        return new XmlElement();
    }

    /**
     * Create an instance of {@link XmlTransient }
     *
     */
    public XmlTransient createXmlTransient() {
        return new XmlTransient();
    }

    /**
     * Create an instance of {@link XmlSchema }
     *
     */
    public XmlSchema createXmlSchema() {
        return new XmlSchema();
    }

    /**
     * Create an instance of {@link XmlJavaTypeAdapters }
     *
     */
    public XmlJavaTypeAdapters createXmlJavaTypeAdapters() {
        return new XmlJavaTypeAdapters();
    }

    /**
     * Create an instance of {@link XmlAnyElement }
     *
     */
    public XmlAnyElement createXmlAnyElement() {
        return new XmlAnyElement();
    }

    /**
     * Create an instance of {@link XmlRootElement }
     *
     */
    public XmlRootElement createXmlRootElement() {
        return new XmlRootElement();
    }

    /**
     * Create an instance of {@link XmlBindings }
     *
     */
    public XmlBindings createXmlBindings() {
        return new XmlBindings();
    }

    /**
     * Create an instance of {@link XmlSchema.XmlNs }
     *
     */
    public XmlSchema.XmlNs createXmlSchemaXmlNs() {
        return new XmlSchema.XmlNs();
    }

    /**
     * Create an instance of {@link XmlBindings.XmlEnums }
     *
     */
    public XmlBindings.XmlEnums createXmlBindingsXmlEnums() {
        return new XmlBindings.XmlEnums();
    }

    /**
     * Create an instance of {@link org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry }
     *
     */
    public org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry createXmlRegistry() {
        return new org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry();
    }
    /**
     * Create an instance of {@link XmlVariableNode }
     *
     */
    public XmlVariableNode createXmlVariableNode() {
        return new XmlVariableNode();
    }

    /**
     * Create an instance of {@link XmlJavaTypeAdapter }
     *
     */
    public XmlJavaTypeAdapter createXmlJavaTypeAdapter() {
        return new XmlJavaTypeAdapter();
    }

    /**
     * Create an instance of {@link JavaType.JavaAttributes }
     *
     */
    public JavaType.JavaAttributes createJavaTypeJavaAttributes() {
        return new JavaType.JavaAttributes();
    }

    /**
     * Create an instance of {@link XmlEnum }
     *
     */
    public XmlEnum createXmlEnum() {
        return new XmlEnum();
    }

    /**
     * Create an instance of {@link org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl }
     *
     */
    public org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl createXmlRegistryXmlElementDecl() {
        return new org.eclipse.persistence.jaxb.xmlmodel.XmlRegistry.XmlElementDecl();
    }

    /**
     * Create an instance of {@link XmlElementWrapper }
     *
     */
    public XmlElementWrapper createXmlElementWrapper() {
        return new XmlElementWrapper();
    }

    /**
     * Create an instance of {@link XmlBindings.JavaTypes }
     *
     */
    public XmlBindings.JavaTypes createXmlBindingsJavaTypes() {
        return new XmlBindings.JavaTypes();
    }

    /**
     * Create an instance of {@link XmlIsSetNullPolicy }
     *
     */
    public XmlIsSetNullPolicy createXmlIsSetNullPolicy() {
        return new XmlIsSetNullPolicy();
    }

    /**
     * Create an instance of {@link XmlAnyAttribute }
     *
     */
    public XmlAnyAttribute createXmlAnyAttribute() {
        return new XmlAnyAttribute();
    }

    /**
     * Create an instance of {@link JavaType }
     *
     */
    public JavaType createJavaType() {
        return new JavaType();
    }

    /**
     * Create an instance of {@link XmlMap.Key }
     *
     */
    public XmlMap.Key createXmlMapKey() {
        return new XmlMap.Key();
    }

    /**
     * Create an instance of {@link XmlNullPolicy }
     *
     */
    public XmlNullPolicy createXmlNullPolicy() {
        return new XmlNullPolicy();
    }

    /**
     * Create an instance of {@link XmlTransformation }
     *
     */
    public XmlTransformation createXmlTransformation() {
        return new XmlTransformation();
    }

    /**
     * Create an instance of {@link XmlBindings.XmlRegistries }
     *
     */
    public XmlBindings.XmlRegistries createXmlBindingsXmlRegistries() {
        return new XmlBindings.XmlRegistries();
    }

    /**
     * Create an instance of {@link XmlMap.Value }
     *
     */
    public XmlMap.Value createXmlMapValue() {
        return new XmlMap.Value();
    }

    /**
     * Create an instance of {@link XmlValue }
     *
     */
    public XmlValue createXmlValue() {
        return new XmlValue();
    }

    /**
     * Create an instance of {@link XmlAttribute }
     *
     */
    public XmlAttribute createXmlAttribute() {
        return new XmlAttribute();
    }

    /**
     * Create an instance of {@link XmlAccessMethods }
     *
     */
    public XmlAccessMethods createXmlAccessMethods() {
        return new XmlAccessMethods();
    }

    /**
     * Create an instance of {@link XmlEnumValue }
     *
     */
    public XmlEnumValue createXmlEnumValue() {
        return new XmlEnumValue();
    }

    /**
     * Create an instance of {@link XmlIsSetNullPolicy.IsSetParameter }
     *
     */
    public XmlIsSetNullPolicy.IsSetParameter createXmlIsSetNullPolicyIsSetParameter() {
        return new XmlIsSetNullPolicy.IsSetParameter();
    }

    /**
     * Create an instance of {@link XmlElementRefs }
     *
     */
    public XmlElementRefs createXmlElementRefs() {
        return new XmlElementRefs();
    }

    /**
     * Create an instance of {@link XmlJoinNodes.XmlJoinNode }
     *
     */
    public XmlJoinNodes.XmlJoinNode createXmlJoinNodesXmlJoinNode() {
        return new XmlJoinNodes.XmlJoinNode();
    }

    /**
     * Create an instance of {@link XmlJoinNodes }
     *
     */
    public XmlJoinNodes createXmlJoinNodes() {
        return new XmlJoinNodes();
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlJavaTypeAdapter }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-java-type-adapter", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlJavaTypeAdapter> createXmlJavaTypeAdapter(XmlJavaTypeAdapter value) {
        return new JAXBElement<>(_XmlJavaTypeAdapter_QNAME, XmlJavaTypeAdapter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlAttribute }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-attribute", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlAttribute> createXmlAttribute(XmlAttribute value) {
        return new JAXBElement<>(_XmlAttribute_QNAME, XmlAttribute.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlInverseReference }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-inverse-reference", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlInverseReference> createXmlInverseReference(XmlInverseReference value) {
        return new JAXBElement<>(_XmlInverseReference_QNAME, XmlInverseReference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JavaAttribute }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "java-attribute")
    public JAXBElement<JavaAttribute> createJavaAttribute(JavaAttribute value) {
        return new JAXBElement<>(_JavaAttribute_QNAME, JavaAttribute.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlClassExtractor }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-class-extractor")
    public JAXBElement<XmlClassExtractor> createXmlClassExtractor(XmlClassExtractor value) {
        return new JAXBElement<>(_XmlClassExtractor_QNAME, XmlClassExtractor.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlNullPolicy }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-null-policy", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "xml-abstract-null-policy")
    public JAXBElement<XmlNullPolicy> createXmlNullPolicy(XmlNullPolicy value) {
        return new JAXBElement<>(_XmlNullPolicy_QNAME, XmlNullPolicy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlElementRefs }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-element-refs", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlElementRefs> createXmlElementRefs(XmlElementRefs value) {
        return new JAXBElement<>(_XmlElementRefs_QNAME, XmlElementRefs.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlAnyElement }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-any-element", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlAnyElement> createXmlAnyElement(XmlAnyElement value) {
        return new JAXBElement<>(_XmlAnyElement_QNAME, XmlAnyElement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-see-also")
    public JAXBElement<List<String>> createXmlSeeAlso(List<String> value) {
        return new JAXBElement<List<String>>(_XmlSeeAlso_QNAME, ((Class) List.class), null, (value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlValue }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-value", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlValue> createXmlValue(XmlValue value) {
        return new JAXBElement<>(_XmlValue_QNAME, XmlValue.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlAccessMethods }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-access-methods")
    public JAXBElement<XmlAccessMethods> createXmlAccessMethods(XmlAccessMethods value) {
        return new JAXBElement<>(_XmlAccessMethods_QNAME, XmlAccessMethods.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlTransient }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-transient", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlTransient> createXmlTransient(XmlTransient value) {
        return new JAXBElement<>(_XmlTransient_QNAME, XmlTransient.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlVariableNode }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-variable-node", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlVariableNode> createXmlVariableNode(XmlVariableNode value) {
        return new JAXBElement<>(_XmlVariableNode_QNAME, XmlVariableNode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlElement }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-element", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlElement> createXmlElement(XmlElement value) {
        return new JAXBElement<>(_XmlElement_QNAME, XmlElement.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlAbstractNullPolicy }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-abstract-null-policy")
    public JAXBElement<XmlAbstractNullPolicy> createXmlAbstractNullPolicy(XmlAbstractNullPolicy value) {
        return new JAXBElement<>(_XmlAbstractNullPolicy_QNAME, XmlAbstractNullPolicy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlElementRef }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-element-ref", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlElementRef> createXmlElementRef(XmlElementRef value) {
        return new JAXBElement<>(_XmlElementRef_QNAME, XmlElementRef.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlIsSetNullPolicy }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-is-set-null-policy", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "xml-abstract-null-policy")
    public JAXBElement<XmlIsSetNullPolicy> createXmlIsSetNullPolicy(XmlIsSetNullPolicy value) {
        return new JAXBElement<>(_XmlIsSetNullPolicy_QNAME, XmlIsSetNullPolicy.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlTransformation }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-transformation", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlTransformation> createXmlTransformation(XmlTransformation value) {
        return new JAXBElement<>(_XmlTransformation_QNAME, XmlTransformation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlElements }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-elements", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlElements> createXmlElements(XmlElements value) {
        return new JAXBElement<>(_XmlElements_QNAME, XmlElements.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlProperties }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-properties")
    public JAXBElement<XmlProperties> createXmlProperties(XmlProperties value) {
        return new JAXBElement<>(_XmlProperties_QNAME, XmlProperties.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlAnyAttribute }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-any-attribute", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlAnyAttribute> createXmlAnyAttribute(XmlAnyAttribute value) {
        return new JAXBElement<>(_XmlAnyAttribute_QNAME, XmlAnyAttribute.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XmlJoinNodes }{@code >}}
     *
     */
    @jakarta.xml.bind.annotation.XmlElementDecl(namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", name = "xml-join-nodes", substitutionHeadNamespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", substitutionHeadName = "java-attribute")
    public JAXBElement<XmlJoinNodes> createXmlJoinNodes(XmlJoinNodes value) {
        return new JAXBElement<>(_XmlJoinNodes_QNAME, XmlJoinNodes.class, null, value);
    }
}
