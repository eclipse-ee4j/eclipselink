/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;

import java.util.ArrayList;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.platform.DOMPlatform;
import org.eclipse.persistence.sessions.Project;

/**
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/15/2004 10:16:25
 */
public class TopLinkJAXBBindingSchemaProject extends Project {
    private NamespaceResolver _resolver;

    // ===========================================================================
    public static void main(String[] args) {
        TopLinkJAXBBindingSchemaProject proj = new TopLinkJAXBBindingSchemaProject();
        XMLContext context = new XMLContext(proj);
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();
        TopLinkJAXBBindingSchemaCollection obj = (TopLinkJAXBBindingSchemaCollection)unmarshaller.unmarshal(new java.io.File("_tljaxb_cust.xml"));
        for (int i = 0; i < obj.getBindings().size(); i++) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)obj.getBindings().elementAt(i);
            System.out.println(schema.dump());
        }
    }

    // ===========================================================================
    public TopLinkJAXBBindingSchemaProject() {
        this._resolver = new NamespaceResolver();
        this._resolver.put("jaxb", "http://java.sun.com/xml/ns/jaxb");
        this._resolver.put("orajaxb", "http://www.oracle.com/xml/ns/orajaxb");
        this._resolver.put("xs", "http://www.w3.org/2001/XMLSchema");

        addDescriptor(getTopLinkJAXBBindingSchemaDescriptor());
        addDescriptor(getTopLinkJAXBPropertyDescriptor());
        addDescriptor(getTopLinkJAXBBindingSchemaCollectionDescriptor());

        XMLLogin login = new XMLLogin();
        login.setPlatform(new DOMPlatform());

        setLogin(login);
    }

    // ===========================================================================
    public XMLDescriptor getTopLinkJAXBBindingSchemaDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBBindingSchema.class);

        XMLDirectMapping namespaceMapping = new XMLDirectMapping();
        namespaceMapping.setAttributeName("namespace");
        namespaceMapping.setGetMethodName("getNamespace");
        namespaceMapping.setSetMethodName("setNamespace");
        namespaceMapping.setXPath("orajaxb:namespace/@value");
        descriptor.addMapping(namespaceMapping);

        XMLDirectMapping customizationNameMapping = new XMLDirectMapping();
        customizationNameMapping.setAttributeName("customizationName");
        customizationNameMapping.setGetMethodName("getCustomizationName");
        customizationNameMapping.setSetMethodName("setCustomizationName");
        customizationNameMapping.setXPath("orajaxb:customization/@name");
        descriptor.addMapping(customizationNameMapping);

        XMLDirectMapping nodeNameMapping = new XMLDirectMapping();
        nodeNameMapping.setAttributeName("fullNodeName");
        nodeNameMapping.setGetMethodName("getFullNodeName");
        nodeNameMapping.setSetMethodName("setFullNodeName");
        nodeNameMapping.setXPath("@node");
        descriptor.addMapping(nodeNameMapping);

        XMLDirectMapping classNameMapping = new XMLDirectMapping();
        classNameMapping.setAttributeName("className");
        classNameMapping.setGetMethodName("getClassName");
        classNameMapping.setSetMethodName("setClassName");
        classNameMapping.setXPath("jaxb:class/@name");
        descriptor.addMapping(classNameMapping);

        XMLDirectMapping packageNameMapping = new XMLDirectMapping();
        packageNameMapping.setAttributeName("packageName");
        packageNameMapping.setGetMethodName("getPackageName");
        packageNameMapping.setSetMethodName("setPackageName");
        packageNameMapping.setXPath("jaxb:class/orajaxb:package/@name");
        descriptor.addMapping(packageNameMapping);

        XMLDirectMapping typesafeEnumClassNameMapping = new XMLDirectMapping();
        typesafeEnumClassNameMapping.setAttributeName("typesafeEnumClassName");
        typesafeEnumClassNameMapping.setGetMethodName("getTypesafeEnumClassName");
        typesafeEnumClassNameMapping.setSetMethodName("setTypesafeEnumClassName");
        typesafeEnumClassNameMapping.setXPath("jaxb:typesafeEnumClass/@name");
        descriptor.addMapping(typesafeEnumClassNameMapping);

        XMLDirectMapping typesafeEnumPackageMapping = new XMLDirectMapping();
        typesafeEnumPackageMapping.setAttributeName("typesafeEnumPackage");
        typesafeEnumPackageMapping.setGetMethodName("getTypesafeEnumPackage");
        typesafeEnumPackageMapping.setSetMethodName("setTypesafeEnumPackage");
        typesafeEnumPackageMapping.setXPath("jaxb:typesafeEnumClass/orajaxb:package/@name");
        descriptor.addMapping(typesafeEnumPackageMapping);

        XMLDirectMapping extendsNodeMapping = new XMLDirectMapping();
        extendsNodeMapping.setAttributeName("extendsNode");
        extendsNodeMapping.setGetMethodName("getExtendsNode");
        extendsNodeMapping.setSetMethodName("setExtendsNode");
        extendsNodeMapping.setXPath("jaxb:class/orajaxb:extendsNode/@name");
        descriptor.addMapping(extendsNodeMapping);

        XMLDirectMapping isInnerInterfaceMapping = new XMLDirectMapping();
        isInnerInterfaceMapping.setAttributeName("isInnerInterface");
        isInnerInterfaceMapping.setGetMethodName("getIsInnerInterface");
        isInnerInterfaceMapping.setSetMethodName("setIsInnerInterface");
        isInnerInterfaceMapping.setXPath("jaxb:class/orajaxb:isInnerInterface/@value");
        isInnerInterfaceMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isInnerInterfaceMapping);

        XMLCompositeDirectCollectionMapping enclosingClassNameMapping = new XMLCompositeDirectCollectionMapping();
        enclosingClassNameMapping.setAttributeName("enclosingClassNames");
        enclosingClassNameMapping.setGetMethodName("getEnclosingClassNames");
        enclosingClassNameMapping.setSetMethodName("setEnclosingClassName");
        enclosingClassNameMapping.setXPath("ancestor::*/jaxb:class/@name");
        descriptor.addMapping(enclosingClassNameMapping);

        XMLDirectMapping enclosingPackageMapping = new XMLDirectMapping();
        enclosingPackageMapping.setAttributeName("enclosingClassPackage");
        enclosingPackageMapping.setGetMethodName("getEnclosingClassPackage");
        enclosingPackageMapping.setSetMethodName("setEnclosingClassPackage");
        enclosingPackageMapping.setXPath("parent::*/jaxb:class/orajaxb:package/@name");
        descriptor.addMapping(enclosingPackageMapping);

        XMLDirectMapping simpleTypeMapping = new XMLDirectMapping();
        simpleTypeMapping.setAttributeName("simpleType");
        simpleTypeMapping.setGetMethodName("getSimpleTypeName");
        simpleTypeMapping.setSetMethodName("setSimpleTypeName");
        simpleTypeMapping.setXPath("jaxb:class/orajaxb:javaType/@name");
        descriptor.addMapping(simpleTypeMapping);

        XMLCompositeDirectCollectionMapping schemaBaseTypeMapping = new XMLCompositeDirectCollectionMapping();
        schemaBaseTypeMapping.setAttributeName("schemaBaseTypes");
        schemaBaseTypeMapping.setGetMethodName("getSchemaBaseTypes");
        schemaBaseTypeMapping.setSetMethodName("setSchemaBaseTypes");
        schemaBaseTypeMapping.useCollectionClass(ArrayList.class);
        schemaBaseTypeMapping.setXPath("jaxb:class/orajaxb:javaType/@xmlType");
        descriptor.addMapping(schemaBaseTypeMapping);

        XMLDirectMapping simpleContentTypeNameMapping = new XMLDirectMapping();
        simpleContentTypeNameMapping.setAttributeName("simpleContentTypeName");
        simpleContentTypeNameMapping.setGetMethodName("getSimpleContentTypeName");
        simpleContentTypeNameMapping.setSetMethodName("setSimpleContentTypeName");
        simpleContentTypeNameMapping.setXPath("orajaxb:javaType/@name");
        descriptor.addMapping(simpleContentTypeNameMapping);

        XMLCompositeDirectCollectionMapping simpleContentTypeMapping = new XMLCompositeDirectCollectionMapping();
        simpleContentTypeMapping.setAttributeName("simpleContentTypes");
        simpleContentTypeMapping.setGetMethodName("getSimpleContentTypes");
        simpleContentTypeMapping.setSetMethodName("setSimpleContentTypes");
        simpleContentTypeMapping.useCollectionClass(ArrayList.class);
        simpleContentTypeMapping.setXPath("orajaxb:javaType/@xmlType");
        descriptor.addMapping(simpleContentTypeMapping);

        XMLDirectMapping nillableMapping = new XMLDirectMapping();
        nillableMapping.setAttributeName("isNillable");
        nillableMapping.setGetMethodName("getIsNillable");
        nillableMapping.setSetMethodName("setIsNillable");
        nillableMapping.setXPath("jaxb:class/orajaxb:isNillable/@value");
        nillableMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(nillableMapping);

        XMLCompositeCollectionMapping bindingsMapping = new XMLCompositeCollectionMapping();
        bindingsMapping.setAttributeName("properties");
        bindingsMapping.setGetMethodName("getProperties");
        bindingsMapping.setSetMethodName("setProperties");
        bindingsMapping.setXPath("jaxb:bindings");
        bindingsMapping.setReferenceClass(org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBProperty.class);
        descriptor.addMapping(bindingsMapping);

        descriptor.setNamespaceResolver(this._resolver);

        return descriptor;
    }

    public XMLDescriptor getTopLinkJAXBPropertyDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBProperty.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setGetMethodName("getName");
        nameMapping.setSetMethodName("setName");
        nameMapping.setXPath("jaxb:property/@name");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping isInnerInterfaceMapping = new XMLDirectMapping();
        isInnerInterfaceMapping.setAttributeName("isInnerInterface");
        isInnerInterfaceMapping.setGetMethodName("getIsInnerInterface");
        isInnerInterfaceMapping.setSetMethodName("setIsInnerInterface");
        isInnerInterfaceMapping.setXPath("jaxb:property/orajaxb:isInnerInterface/@value");
        isInnerInterfaceMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(isInnerInterfaceMapping);

        XMLDirectMapping collectionTypeMapping = new XMLDirectMapping();
        collectionTypeMapping.setAttributeName("collectionTypeName");
        collectionTypeMapping.setGetMethodName("getCollectionTypeName");
        collectionTypeMapping.setSetMethodName("setCollectionTypeName");
        collectionTypeMapping.setXPath("jaxb:property/@collectionType");
        descriptor.addMapping(collectionTypeMapping);

        XMLDirectMapping nodeTypeMapping = new XMLDirectMapping();
        nodeTypeMapping.setAttributeName("nodeType");
        nodeTypeMapping.setGetMethodName("getNodeType");
        nodeTypeMapping.setSetMethodName("setNodeType");
        nodeTypeMapping.setXPath("@node");
        descriptor.addMapping(nodeTypeMapping);

        XMLDirectMapping javaTypeMapping = new XMLDirectMapping();
        javaTypeMapping.setAttributeName("javaTypeName");
        javaTypeMapping.setGetMethodName("getJavaTypeName");
        javaTypeMapping.setSetMethodName("setJavaTypeName");
        javaTypeMapping.setXPath("jaxb:property/jaxb:baseType/jaxb:javaType/@name");
        descriptor.addMapping(javaTypeMapping);

        XMLCompositeDirectCollectionMapping xmlTypeMapping = new XMLCompositeDirectCollectionMapping();
        xmlTypeMapping.setAttributeName("xmlTypes");
        xmlTypeMapping.setGetMethodName("getXMLTypes");
        xmlTypeMapping.setSetMethodName("setXMLTypes");
        xmlTypeMapping.useCollectionClass(ArrayList.class);
        xmlTypeMapping.setXPath("jaxb:property/jaxb:baseType/orajaxb:xmlType/@name");
        descriptor.addMapping(xmlTypeMapping);

        XMLDirectMapping namespaceMapping = new XMLDirectMapping();
        namespaceMapping.setAttributeName("namespace");
        namespaceMapping.setGetMethodName("getNamespace");
        namespaceMapping.setSetMethodName("setNamespace");
        namespaceMapping.setXPath("orajaxb:namespace/@value");
        descriptor.addMapping(namespaceMapping);

        XMLDirectMapping defaultValueMapping = new XMLDirectMapping();
        defaultValueMapping.setAttributeName("defaultValue");
        defaultValueMapping.setGetMethodName("getDefaultValue");
        defaultValueMapping.setSetMethodName("setDefaultValue");
        defaultValueMapping.setXPath("jaxb:property/orajaxb:defaultValue/@value");
        descriptor.addMapping(defaultValueMapping);

        XMLDirectMapping generateIsSetMapping = new XMLDirectMapping();
        generateIsSetMapping.setAttributeName("generateIsSetMethod");
        generateIsSetMapping.setGetMethodName("getGenerateIsSetMethod");
        generateIsSetMapping.setSetMethodName("setGenerateIsSetMethod");
        generateIsSetMapping.setXPath("jaxb:property/@generateIsSetMethod");
        generateIsSetMapping.setNullValue(Boolean.FALSE);
        descriptor.addMapping(generateIsSetMapping);

        descriptor.setNamespaceResolver(this._resolver);

        return descriptor;
    }

    public XMLDescriptor getTopLinkJAXBBindingSchemaCollectionDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBBindingSchemaCollection.class);
        descriptor.setDefaultRootElement("jaxb:bindings");

        XMLCompositeCollectionMapping bindingsMapping = new XMLCompositeCollectionMapping();
        bindingsMapping.setAttributeName("bindings");
        bindingsMapping.setGetMethodName("getBindings");
        bindingsMapping.setSetMethodName("setBindings");
        bindingsMapping.setXPath("//jaxb:bindings/jaxb:class/parent::*");
        bindingsMapping.setReferenceClass(org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBBindingSchema.class);
        descriptor.addMapping(bindingsMapping);

        XMLCompositeCollectionMapping typesafeMappings = new XMLCompositeCollectionMapping();
        typesafeMappings.setAttributeName("typesafeBindings");
        typesafeMappings.setGetMethodName("getTypesafeBindings");
        typesafeMappings.setSetMethodName("setTypesafeBindings");
        typesafeMappings.setXPath("//jaxb:bindings/jaxb:typesafeEnumClass/parent::*");
        typesafeMappings.setReferenceClass(org.eclipse.persistence.oxm.jaxb.compiler.TopLinkJAXBBindingSchema.class);
        descriptor.addMapping(typesafeMappings);

        descriptor.setNamespaceResolver(this._resolver);

        return descriptor;
    }
}