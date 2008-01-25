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


/**
 * This class generates a Mapping Workbench project and TopLink Deployment XML
 * from information gleaned from <CODE>orajaxb</CODE>.
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/14/2004 11:56:18
 */
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.namespace.QName;

import org.eclipse.persistence.tools.workbench.mappingsio.ProjectIOManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.InterfaceDescriptorCreationException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.MWDescriptorAfterLoadingPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWDefaultNullValuePolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWAnyObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectCollectionMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlDirectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.xml.MWXmlMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassAttribute;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWMethod;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWModifier;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWTypeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;

import org.eclipse.persistence.internal.localization.JAXBLocalization;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;

public class TopLinkJAXBMappingsGenerator {
    // ===========================================================================
    // JaxbBindingSchemas, keyed on node name.
    private Hashtable bindingSchemas;

    // All the descriptors we're creating, keyed on class name.
    private Hashtable allDescriptors;
    private String schemaFile;
    private String projectName;

    // Keep track of any NamespaceResolvers needed, these are added to the descriptors
    // just before deployment XML generation
    private Hashtable namespaceResolvers;
    private MWOXProject mwProject;
    private MWClassRepository mwRepository;
    private MWXmlSchema mwSchema;
    private String workbenchDir;
    private String resourceDir;
    private String sourceDir;
    private String implClassPkg;
    private boolean generateWorkbenchProject;
    private static final String topLinkDirName = "toplink";
    private boolean useDomPlatform = false;

    // Source Gen Stuff
    private String indent = "";
    private static final String IMPL = "Impl";
    private static final String fsep = System.getProperty("file.separator");
    private static final String lsep = System.getProperty("line.separator");
    private static final String lsep2 = lsep + lsep;
    private static int INDENT_TAB = 3;
    private StringBuffer afterLoadBuffer;
    private HashMap typesafeEnumMappings;

    // ===========================================================================
    public TopLinkJAXBMappingsGenerator() {
        this.allDescriptors = new Hashtable();
        this.namespaceResolvers = new Hashtable();
        this.typesafeEnumMappings = new HashMap();
    }

    // ===========================================================================
    public void generate(Hashtable bindingSchemas, MWOXProject project, MWXmlSchema schema, String projectName, String workbenchDir, String sourceDir, String implClassPkg, boolean useDomPlatform, boolean generateWorkbenchProject) throws TopLinkJAXBGenerationException {
        this.bindingSchemas = bindingSchemas;
        this.workbenchDir = workbenchDir;
        this.resourceDir = sourceDir;
        this.sourceDir = sourceDir;
        this.implClassPkg = implClassPkg;
        this.mwSchema = schema;
        this.mwProject = project;
        this.mwRepository = project.getClassRepository();
        this.projectName = projectName;
        this.useDomPlatform = useDomPlatform;
        this.generateWorkbenchProject = generateWorkbenchProject;
        generateTopLinkFiles(bindingSchemas);
    }

    // ==============================================================================================================================================
    // TOPLINK GENERATION
    // ==============================================================================================================================================
    private void generateTopLinkFiles(Hashtable bindingSchemas) throws TopLinkJAXBGenerationException {
        Iterator namespaces = this.mwSchema.declaredNamespaces();
        int i = 0;
        while (namespaces.hasNext()) {
            MWNamespace next = (MWNamespace)namespaces.next();
            if (!next.getNamespaceUrl().equals("") && next.getNamespacePrefix().equals("")) {
                //generate a unique prefix for that namespace
                String nsPrefix = "ns" + i;
                while (!this.mwSchema.namespaceUrlForPrefix(nsPrefix).equals("")) {
                    i++;
                    nsPrefix = "ns" + i;
                }
                next.setNamespacePrefixFromUser(nsPrefix);
                next.setDeclared(true);
            }
        }

        this.mwSchema.namespaceForUrl(XMLConstants.SCHEMA_INSTANCE_URL).setDeclared(true);
        this.mwSchema.namespaceForUrl(XMLConstants.SCHEMA_URL).setDeclared(true);

        TopLinkJAXBBindingSchema schema = null;

        // First pass: just create the Descriptors.  If we need to create a Composite mapping,
        // its reference descriptor needs to have been already been created.        
        TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("create_descriptors"));
        Enumeration e = bindingSchemas.elements();
        while (e.hasMoreElements()) {
            schema = (TopLinkJAXBBindingSchema)e.nextElement();
            boolean isAnyTypeDescriptor = false;
            if (schema.getClassName().endsWith("AnyType")) {
                isAnyTypeDescriptor = true;
            }
            generateDescriptor(schema, isAnyTypeDescriptor);

        }

        // Second pass: create Mappings.  Any reference descriptors are ready to use after first pass.        
        TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("create_mappings"));
        Enumeration e2 = bindingSchemas.elements();
        while (e2.hasMoreElements()) {
            schema = (TopLinkJAXBBindingSchema)e2.nextElement();
            if (!schema.isElement() || (schema.getSimpleTypeName() != null)) {
                if (schema.getClassName().endsWith("AnyType")) {
                    continue;//don't try to generate mappings for anytype. Do it later
                }

                // We'll do element schemas later, in setupInheritance()
                generateMappings(schema);
            }
        }

        TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("setup_inheritance"));

        setupInheritance();

        handleTypesafeEnumerations();
        //set the deployment xml and source directory options on the project
        this.mwProject.setDeploymentXMLFileName(this.resourceDir + fsep + this.projectName + ".xml");
        this.mwProject.setModelSourceDirectoryName(this.sourceDir);

        Project flProject = this.mwProject.buildRuntimeProject();

        // Write the MWP if enabled:
        if (this.generateWorkbenchProject) {
            ProjectIOManager ioMgr = new ProjectIOManager();
            try {
                ioMgr.write(this.mwProject);
            } catch (Exception ex) {
                throw new TopLinkJAXBGenerationException(ex);
            }
        }

        TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("generate_files"));
        // Write Deployment XML
        XMLProjectWriter writer = new XMLProjectWriter();
        File directory = new File(this.resourceDir);
        directory.mkdirs();
        writer.write(this.resourceDir + fsep + this.projectName + ".xml", flProject);

        // Write sessions.xml
        if (schema != null) {
            writeSessionsXml(schema.getPackageName(), this.projectName + ".xml", this.resourceDir);
            String afterLoadDir = this.sourceDir + fsep + schema.getImplClassPackage().replace('.', fsep.charAt(0)) + fsep.charAt(0) + "toplink" + fsep.charAt(0);
            writeAfterLoadClass(afterLoadDir);
        }
    }

    /**
     * A convenience method for converting a string to a QName
     *
     * @param simpleTypeName
     * @return
     */
    private QName getSimpleTypeQName(String simpleTypeName) {
        int prefixIndex = simpleTypeName.indexOf(":");

        // if non-qualified simply return a new QName with localName = simpleTypeName
        if (prefixIndex == -1) {
            return new QName(simpleTypeName);
        }

        String localName = simpleTypeName.substring(prefixIndex + 1);
        String prefix = simpleTypeName.substring(0, prefixIndex);

        // look for namespace of a user-defined prefix
        String ns = this.mwSchema.namespaceUrlForPrefix(prefix);

        // prefix is not user-defined, so look in the the built-in namespace list
        if ((ns == null) || ns.equals("")) {
            boolean found = false;

            // if a namespace wasn't found, look for one in the list of 'built in' namespaces 
            for (Iterator stream = this.mwSchema.builtInNamespaces(); stream.hasNext();) {
                MWNamespace namespace = (MWNamespace)stream.next();

                if (prefix.equals(namespace.getNamespacePrefix())) {
                    ns = namespace.getNamespaceUrl();
                    found = true;
                }
            }

            // orajaxb always prefixes the schema URL with "xs", regardless of what is actually in the schema
            if (!found && prefix.equals("xs")) {
                ns = XMLConstants.SCHEMA_URL;
            }
        }

        if ((ns != null) && !ns.equals("")) {
            return new QName(ns, localName, prefix);
        }

        return new QName(localName);
    }

    /**
        * INTERNAL:
    * For simple type definitions and properties containing typesafe enumerations, set a converter
    * on the associated mapping.
    */
    private void handleTypesafeEnumerations() {
        Iterator descIter = this.typesafeEnumMappings.keySet().iterator();

        while (descIter.hasNext()) {
            String descriptorClass = (String)descIter.next();
            MWOXDescriptor descriptor = (MWOXDescriptor)this.allDescriptors.get(descriptorClass);

            TopLinkJAXBBindingSchema bindingSchema = schemaForImplClass(descriptorClass);

            if (bindingSchema.isTypesafeEnumeration()) {
                StringBuffer descriptorAfterLoadBuffer = new StringBuffer();
                String methodName = createAfterLoadMethod(descriptor, descriptorAfterLoadBuffer);

                String key = descriptor.getMWClass().getName();
                Object mappings = this.typesafeEnumMappings.get(key);
                if ((mappings != null) && (mappings instanceof java.util.List)) {
                    Iterator mappingsIter = ((java.util.List)mappings).iterator();
                    while (mappingsIter.hasNext()) {
                        MWMapping nextMapping = (MWMapping)mappingsIter.next();
                        if ((nextMapping instanceof MWXmlDirectMapping) || (nextMapping instanceof MWXmlDirectCollectionMapping)) {
                            addTypeSafeAfterLoad(descriptor, nextMapping, bindingSchema, descriptorAfterLoadBuffer);
                        }
                    }
                    popIndent();
                    descriptorAfterLoadBuffer.append(this.indent).append("}").append(lsep2);
                    popIndent();
                    createAfterLoadClass(bindingSchema);
                    this.afterLoadBuffer.append(descriptorAfterLoadBuffer);

                    MWClass mwClass = this.mwRepository.typeNamed(bindingSchema.getImplClassPackage() + ".toplink.DescriptorAfterLoads");

                    MWClass paramType = this.mwRepository.typeNamed("org.eclipse.persistence.descriptors.ClassDescriptor");
                    MWMethod mwMethod = mwClass.addMethod(methodName);
                    mwMethod.getModifier().setStatic(true);
                    mwMethod.addMethodParameter(paramType);
                    descriptor.addAfterLoadingPolicy();
                    ((MWDescriptorAfterLoadingPolicy)descriptor.getAfterLoadingPolicy()).setPostLoadMethodClass(mwClass);
                    ((MWDescriptorAfterLoadingPolicy)descriptor.getAfterLoadingPolicy()).setPostLoadMethod(mwMethod);
                }
            }
        }
    }

    /**
    * INTERNAL:
    * Create the after load methods header for the given descriptor
    */
    private String createAfterLoadMethod(MWOXDescriptor descriptor, StringBuffer buffer) {
        pushIndent();
        buffer.append(this.indent);
        String name = descriptor.getMWClass().getName();
        int index = name.lastIndexOf(".");
        if (index != -1) {
            int end = name.length();
            name = name.substring(index + 1, end);
        }
        String methodName = "amend" + name + "Descriptor";
        buffer.append("public static void " + methodName + "(ClassDescriptor descriptor) { ").append(lsep);
        pushIndent();
        return methodName;
    }

    /**
    * INTERNAL:
    * Create the part of the after load method that adds the type safe enum converter on the mapping
    */
    private void addTypeSafeAfterLoad(MWOXDescriptor descriptor, MWMapping mapping, TopLinkJAXBBindingSchema bindingSchema, StringBuffer buffer) {
        buffer.append(this.indent);
        String attrName = mapping.getInstanceVariable().getName();

        String mappingName = attrName + "Mapping";
        buffer.append("DatabaseMapping " + mappingName + " = descriptor.getMappingForAttributeName(\"" + attrName + "\"" + ");").append(lsep);

        buffer.append(this.indent);
        String converterName = attrName + "Converter";
        buffer.append("org.eclipse.persistence.oxm.jaxb.JAXBTypesafeEnumConverter " + converterName + " = new org.eclipse.persistence.oxm.jaxb.JAXBTypesafeEnumConverter();").append(lsep);
        buffer.append(this.indent);
        String enumClass = bindingSchema.getTypesafeEnumPackage() + "." + bindingSchema.getTypesafeEnumClassName();
        buffer.append(converterName + ".setEnumClassName(\"" + enumClass + "\");").append(lsep);
        buffer.append(this.indent);
        if (mapping instanceof MWXmlDirectMapping) {
            buffer.append("((XMLDirectMapping)" + mappingName + ").setConverter(" + converterName + ");").append(lsep);
        } else if (mapping instanceof MWXmlDirectCollectionMapping) {
            buffer.append("((XMLCompositeDirectCollectionMapping)" + mappingName + ").setValueConverter(" + converterName + ");").append(lsep2);
        }
    }

    /**
    * INTERNAL:
    * Create the class header, package and import statments for the after load class and add to the afterLoadBuffer
    */
    private void createAfterLoadClass(TopLinkJAXBBindingSchema bindingSchema) {
        if (this.afterLoadBuffer == null) {
            this.afterLoadBuffer = new StringBuffer();
//            this.afterLoadBuffer.append(JaxbUtil.getPrefaceString());
            if (!bindingSchema.getPackageName().equals("")) {
                this.afterLoadBuffer.append("package ");
                this.afterLoadBuffer.append(bindingSchema.getImplClassPackage() + ".toplink");
                this.afterLoadBuffer.append(";").append(lsep2);
            }

            this.afterLoadBuffer.append("import org.eclipse.persistence.descriptors.ClassDescriptor;").append(lsep);
            this.afterLoadBuffer.append("import org.eclipse.persistence.mappings.*;").append(lsep);
            this.afterLoadBuffer.append("import org.eclipse.persistence.oxm.mappings.*;").append(lsep2);
            this.afterLoadBuffer.append("/** This class contains the after load methods referred to by the descriptors.").append(lsep).append("/* After load methods are required to specify settings that are not available").append(lsep).append("/* * in the TopLink Workbench. */").append(lsep);

            this.afterLoadBuffer.append("public class DescriptorAfterLoads {").append(lsep2);
        }
    }

    // ===========================================================================
    public void generateDescriptor(TopLinkJAXBBindingSchema schema, boolean isAnyTypeDescriptor) throws TopLinkJAXBGenerationException {
        String fullClassName = getFullImplClassName(schema);
        MWClass mwClass = this.mwRepository.typeNamed(fullClassName);

        if (schema.getIsInnerInterface()) {
            int index = fullClassName.lastIndexOf('$');
            if (index != -1) {
                mwClass.getModifier().setStatic(true);
                String declaringTypeName = fullClassName.substring(0, index);
                MWClass declaringType = this.mwRepository.typeNamed(declaringTypeName);
                mwClass.setDeclaringType(declaringType);
            }
        }

        MWOXDescriptor mwDescriptor;
        try {
            mwDescriptor = (MWOXDescriptor)this.mwProject.addDescriptorForType(mwClass);
        } catch (InterfaceDescriptorCreationException e) {
            throw new TopLinkJAXBGenerationException(e);
        }
        if (!isAnyTypeDescriptor) {
            MWSchemaContextComponent schemaContext = calculateSchemaContext(schema);
            mwDescriptor.setSchemaContext(schemaContext);
        }
        if (schema.isElement()) {
            mwDescriptor.setRootDescriptor(true);
        }

        if (schema.getExtendsNode() != null) {
            TopLinkJAXBBindingSchema extendsSchema = getSchemaByJavaName(schema.getExtendsNode());
            if (extendsSchema != null) {
                String superClassName = getFullImplClassName(extendsSchema);
                MWClass superClass = this.mwRepository.typeNamed(superClassName);
                mwClass.setSuperclass(superClass);
            }
        }

        MWClass interfaceClass = this.mwRepository.typeNamed(getFullClassName(schema, false, true));
        mwClass.addInterface(interfaceClass);

        if (isAnyTypeDescriptor) {
            mwDescriptor.setAnyTypeDescriptor(true);
            //should have exactly 1 property. Generate the appropriate mapping here.
            TopLinkJAXBProperty property = (TopLinkJAXBProperty)schema.getProperties().firstElement();
            generateAnyTypeMapping(property, mwDescriptor);
        }

        this.allDescriptors.put(fullClassName, mwDescriptor);

        Vector innerInterfaces = schema.getInnerInterfaces();
        if (innerInterfaces != null) {
            TopLinkJAXBBindingSchema innerSchema = null;
            for (int i = 0; i < innerInterfaces.size(); i++) {
                innerSchema = (TopLinkJAXBBindingSchema)innerInterfaces.elementAt(i);
                generateDescriptor(innerSchema, false);
            }
        }
    }

    // ===========================================================================
    public String getQualifiedNameForProperty(TopLinkJAXBProperty property) {
        if (property.getNamespace().equals("") || this.mwSchema.namespacePrefixForUrl(property.getNamespace()).equals("")) {
            return property.getMappingName();
        }
        String prefix = this.mwSchema.namespacePrefixForUrl(property.getNamespace());
        return prefix + ":" + property.getMappingName();
    }

    // ===========================================================================
    public MWSchemaContextComponent calculateSchemaContext(TopLinkJAXBBindingSchema schema) {
        String contextString = calculateSchemaContextString(schema);
        StringTokenizer tokenizer = new StringTokenizer(contextString, "/");

        // could not generate a context string for this schema
        if (tokenizer.countTokens() == 0) {
            return null;
        }

        // top-level context
        if (tokenizer.countTokens() == 1) {
            String token = tokenizer.nextToken();

            // handle wrapper class case where string returned is for the owning schema
            int prefixIndex = token.indexOf(":");
            String URI = "";
            String localName = token;
            if (prefixIndex != -1) {
                String prefix = token.substring(0, prefixIndex);
                localName = token.substring(prefixIndex + 1);
                URI = this.mwSchema.namespaceUrlForPrefix(prefix);
            }

            if (schema.getNamespace().equals(URI) && schema.getNodeName().equals(localName)) {
                return getSchemaContext(schema.getNamespace(), schema.getNodeName());
            }

            TopLinkJAXBBindingSchema schemaForCtx = getSchemaByNodeName(localName);
            if (schemaForCtx == null) {
                return null;
            }

            return getSchemaContext(schemaForCtx.getNamespace(), schemaForCtx.getNodeName());
        }

        // nested context, so iterate through until we have the context for this schema
        String firstToken = tokenizer.nextToken();
        int prefixIndex = firstToken.indexOf(":");
        String URI = "";
        String localName = firstToken;
        if (prefixIndex != -1) {
            String prefix = firstToken.substring(0, prefixIndex);
            localName = firstToken.substring(prefixIndex + 1);
            URI = this.mwSchema.namespaceUrlForPrefix(prefix);
        }

        // get the schema context for the first token, then nested contexts until finished
        MWSchemaContextComponent schemaContext = getSchemaContext(URI, localName);

        // iterate through the nested contexts...
        while (tokenizer.hasMoreTokens()) {
            String nextToken = tokenizer.nextToken();
            prefixIndex = nextToken.indexOf(":");
            URI = "";
            localName = nextToken;
            if (prefixIndex != -1) {
                String prefix = nextToken.substring(0, prefixIndex);
                localName = nextToken.substring(prefixIndex + 1);
                URI = this.mwSchema.namespaceUrlForPrefix(prefix);
            }
            schemaContext = schemaContext.nestedElement(URI, localName);
        }
        return schemaContext;
    }

    public String calculateSchemaContextString(TopLinkJAXBBindingSchema schema) {
        String schemaContext = "";
        if (schema.getEnclosingClassName() != null) {
            TopLinkJAXBBindingSchema enclosingSchema = schema.getEnclosingSchema();
            schemaContext += calculateSchemaContextString(enclosingSchema);
        }

        if (!schema.isClassCustomization() || schema.isGroup()) {
            if ((schema.getNamespace() != null) && !schema.getNamespace().equals("")) {
                String prefix = this.mwSchema.namespacePrefixForUrl(schema.getNamespace());
                schemaContext += ("/" + prefix + ":" + schema.getNodeName());
            } else {
                schemaContext += ("/" + schema.getNodeName());
            }
        }
        return schemaContext;
    }

    /**
     * This convenience method will return a schema context component for the
     * provided namespace and node name based on the binding schema type - group
     * complexType or element.
     *
     * @param schemaType TopLinkJAXBBindingSchema.COMPLEX_TYPE | ELEMENT | GROUP
     * @param namespace
     * @paran nodeName
     * @return schema context component for prefix:nodename or null if not found
     */
    private MWSchemaContextComponent getSchemaContext(int schemaType, String namespace, String nodeName) {
        switch (schemaType) {
        case TopLinkJAXBBindingSchema.COMPLEX_TYPE:
            return this.mwSchema.complexType(namespace, nodeName);
        case TopLinkJAXBBindingSchema.ELEMENT:
            return this.mwSchema.element(namespace, nodeName);
        case TopLinkJAXBBindingSchema.GROUP:
            return this.mwSchema.modelGroupDefinition(namespace, nodeName);
        default:
            return null;
        }
    }

    /**
     * This convenience method will return a schema context component for the
     * provided namespace and node name.
     *
     * @param namespace
     * @paran nodeName
     * @return schema context component for namespace, nodename or null if not found
     */
    private MWSchemaContextComponent getSchemaContext(String namespace, String nodeName) {
        MWSchemaContextComponent comp = this.mwSchema.element(namespace, nodeName);

        if (comp == null) {
            comp = this.mwSchema.complexType(namespace, nodeName);
        }
        if (comp == null) {
            comp = this.mwSchema.modelGroupDefinition(namespace, nodeName);
        }

        return comp;
    }

    // ===========================================================================
    public TopLinkJAXBBindingSchema schemaForImplClass(String implClassName) {
        return schemaForImplClass(implClassName, this.bindingSchemas.elements());
    }

    public TopLinkJAXBBindingSchema schemaForImplClass(String implClassName, Enumeration schemasEnum) {
        while (schemasEnum.hasMoreElements()) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)schemasEnum.nextElement();

            String elemClassName = getFullClassName(schema, true, true);

            if (elemClassName.equals(implClassName)) {
                return schema;
            } else {
                TopLinkJAXBBindingSchema returnSchema = schemaForImplClass(implClassName, schema.getInnerInterfaces().elements());
                if (returnSchema != null) {
                    return returnSchema;
                }
            }
        }
        return null;
    }

    // ===========================================================================
    private void setupInheritance() {
        setupInheritance(this.bindingSchemas.elements());
    }

    private void setupInheritance(Enumeration schemasEnum) {
        while (schemasEnum.hasMoreElements()) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)schemasEnum.nextElement();
            if (schema.getExtendsNode() != null) {
                TopLinkJAXBBindingSchema superSchema = getSchemaByJavaName(schema.getExtendsNode());
                while (superSchema != null) {
                    MWOXDescriptor mwDescriptor = (MWOXDescriptor)this.allDescriptors.get(getFullImplClassName(schema));
                    if (mwDescriptor == null) {
                        break;
                    }
                    MWOXDescriptor superDescriptor = (MWOXDescriptor)this.allDescriptors.get(getFullImplClassName(superSchema));
                    if (superSchema.getClassName().equals("AnyType")) {
                        //if the parent class is anyType, add a mapping for the Content
                        //to the subclass
                        TopLinkJAXBProperty property = (TopLinkJAXBProperty)superSchema.getProperties().firstElement();
                        generateAnyTypeMapping(property, mwDescriptor);
                        break;
                    }
                    if (schema.isElement() || schema.isComplexType()) {
                        // If we have an element type, then we don't want the TypeImpl (superclass) class in the project
                        if (superSchema.getFullClassName().equals(schema.getFullClassName() + "Type")) {
                            schema.setTypesafeEnumClassName(superSchema.getTypesafeEnumClassName());
                            schema.setTypesafeEnumPackage(superSchema.getTypesafeEnumPackage());
                        }

                        // Generate mappings on the subclass, but use the superclass' schema,
                        // because the subclass schemas don't contain the inherited attributes to map
                        try {
                            mwDescriptor.mapInheritedAttributesToSuperclass();
                        } catch (ClassNotFoundException e) {
                            //Do nothing for now, since the classes don't exist yet.
                        }
                        generateMappings(superSchema, mwDescriptor);

                    }
                    superSchema = getSchemaByJavaName(superSchema.getExtendsNode());
                }
            }
            setupInheritance(schema.getInnerInterfaces().elements());
        }
    }

    // ===========================================================================
    public void generateMappings(TopLinkJAXBBindingSchema schema) {
        String fullClassName = getFullImplClassName(schema);
        MWOXDescriptor mwDescriptor = (MWOXDescriptor)this.allDescriptors.get(fullClassName);

        generateMappings(schema, mwDescriptor);

        Vector innerInterfaces = schema.getInnerInterfaces();
        if (innerInterfaces != null) {
            TopLinkJAXBBindingSchema innerSchema = null;
            for (int i = 0; i < innerInterfaces.size(); i++) {
                innerSchema = (TopLinkJAXBBindingSchema)innerInterfaces.elementAt(i);
                generateMappings(innerSchema);
            }
        }
    }

    // ===========================================================================
    public void generateMappings(TopLinkJAXBBindingSchema schema, MWOXDescriptor mwDescriptor) {
        StringBuffer descriptorAfterLoadBuffer = null;
        String methodName = null;

        ArrayList typesafeMapping = new ArrayList();
        if (schema.getSimpleTypeName() != null) {
            String simpleTypeName = schema.getSimpleTypeName();
            MWMapping mapping = generateSimpleTypeMapping(simpleTypeName, schema.getSchemaBaseTypes(), mwDescriptor);
            boolean typeSafeEnumRef = ((schema.getTypesafeEnumClassName() != null) && (!schema.getTypesafeEnumClassName().equals("")));
            if (typeSafeEnumRef) {
                typesafeMapping.add(mapping);
            }
        } else if (schema.getSimpleContentTypeName() != null) {
            MWMapping mapping = generateSimpleTypeMapping(schema.getSimpleContentTypeName(), schema.getSimpleContentTypes(), mwDescriptor);
            boolean typeSafeEnumRef = ((schema.getTypesafeEnumClassName() != null) && (!schema.getTypesafeEnumClassName().equals("")));
            if (typeSafeEnumRef) {
                typesafeMapping.add(mapping);

            }
        }
        if (schema.getIsNillable()) {
            generateNillableMapping(mwDescriptor);
        }
        Vector properties = schema.getProperties();
        if (properties != null) {
            TopLinkJAXBProperty property = null;
            for (int i = 0; i < properties.size(); i++) {
                property = (TopLinkJAXBProperty)properties.elementAt(i);

                TopLinkJAXBBindingSchema sc = getSchemaByJavaName(property.getJavaTypeName());

                if ((property.getXMLTypes() != null) && listContainsWildcard(property.getXMLTypes())) {
                    generateAnyTypeMapping(property, mwDescriptor, true);
                    continue;
                } else if (property.isGroup() && ((sc == null) || !sc.isClassCustomization())) {
                    //Use an anytype mapping to handle artifical groups. Could be 
                    //a choice or a multiple occurance sequence.
                    generateAnyTypeMapping(property, mwDescriptor);
                    continue;
                }
                MWMapping mapping = generateMapping(schema, property, mwDescriptor);

                if ((property.getTypesafeEnumClassName() != null) && (!property.getTypesafeEnumClassName().equals(""))) {
                    typesafeMapping.add(mapping);
                }
            }
        }

        if (!typesafeMapping.isEmpty()) {
            this.typesafeEnumMappings.put(mwDescriptor.getMWClass().getName(), typesafeMapping);
        }
    }

    private void generateAnyTypeMapping(TopLinkJAXBProperty property, MWOXDescriptor mwDescriptor) {
        this.generateAnyTypeMapping(property, mwDescriptor, false);
    }

    private void generateAnyTypeMapping(TopLinkJAXBProperty property, MWOXDescriptor mwDescriptor, boolean isWildcard) {
        String attributeName = "_";// + JaxbUtil.className(property.getName());
        if (property.getCollectionTypeName() != null) {
            MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, property.getCollectionTypeName(), mwDescriptor);
            MWAnyCollectionMapping mapping = mwDescriptor.addAnyCollectionMapping(mwAttribute);
            mapping.setWildcardMapping(isWildcard);

        } else {
            MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, property.getJavaTypeName(), mwDescriptor);
            MWAnyObjectMapping mapping = mwDescriptor.addAnyObjectMapping(mwAttribute);
            mapping.setWildcardMapping(isWildcard);
        }
    }

    /**
     * Indicates if the supplied ArrayList contains a wildcard, indicated by 'any'
     *
     * @param list
     * @return true if the list contains a wildcard, false if not
     */
    private boolean listContainsWildcard(ArrayList list) {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            if (((String)it.next()).equals("any")) {
                return true;
            }
        }
        return false;
    }

    // ===========================================================================
    private MWMapping generateMapping(TopLinkJAXBBindingSchema schema, TopLinkJAXBProperty property, MWOXDescriptor fld) {
        TopLinkJAXBBindingSchema referenceClass = getSchemaByNodeName(property.getMappingName());

        TopLinkJAXBBindingSchema sc = getSchemaByJavaName(property.getJavaTypeName());
        if ((sc != null) && sc.isClassCustomization()) {
            referenceClass = sc;
        }

        if (property.isCollectionType() && !property.isAttribute()) {
            if (referenceClass != null) {
                return generateCompositeCollectionMapping(property, fld, referenceClass);
            } else {
                String javaTypeName = property.getJavaTypeName();
                if (isClassFromThisProject(javaTypeName)) {
                    return generateCompositeCollectionMapping(property, fld, getSchemaByJavaName(javaTypeName));
                } else {
                    return generateDirectCollectionMapping(property, fld);
                }
            }
        } else {
            if (referenceClass != null) {
                //Check to see if the JavaType is a primitive, in case of a 
                //global element of a simple type.
                if (isDirectType(property.getJavaTypeName())) {
                    return generateDirectMapping(property, fld, property.isAttribute());
                } else {
                    return generateCompositeObjectMapping(property, fld, referenceClass);
                }
            } else if (property.getJavaTypeName().endsWith("AnyType")) {
                referenceClass = (TopLinkJAXBBindingSchema)this.bindingSchemas.get(property.getJavaTypeName());
                return generateCompositeObjectMapping(property, fld, referenceClass);
            } else {
                String javaTypeName = property.getJavaTypeName();
                if (isClassFromThisProject(javaTypeName)) {
                    return generateCompositeObjectMapping(property, fld, getSchemaByJavaName(javaTypeName));
                } else {
                    return generateDirectMapping(property, fld, property.isAttribute());
                }
            }
        }
    }

    // ===========================================================================
    private MWClassAttribute getOrCreateAttribute(String attributeName, String type, MWOXDescriptor mwDescriptor) {
        MWClass attrType = this.mwRepository.typeNamed(type);
        Iterator it = mwDescriptor.getMWClass().allAttributes();
        while (it.hasNext()) {
            MWClassAttribute attribute = (MWClassAttribute)it.next();
            if ((attribute.getName().equals(attributeName)) && (attribute.getType().equals(attrType))) {
                return attribute;
            }
        }
        MWClassAttribute attr = mwDescriptor.getMWClass().addAttribute(attributeName, attrType);
        attr.getModifier().setAccessLevel(MWModifier.PRIVATE);
        return attr;
    }

    // ===========================================================================
    private MWMethod getOrCreateGetterMethod(String methodName, MWOXDescriptor mwDescriptor, MWClass returnType) {
        Iterator it = mwDescriptor.getMWClass().allMethods();
        while (it.hasNext()) {
            MWMethod method = (MWMethod)it.next();
            if (method.getName().equals(methodName)) {
                if ((method.getReturnType() != null) && (method.getReturnType().equals(returnType))) {
                    return method;
                }
            }
        }
        return mwDescriptor.getMWClass().addMethod(methodName, returnType);
    }

    private MWMethod getOrCreateSetterMethod(String methodName, MWOXDescriptor mwDescriptor, MWClass paramType) {
        Iterator it = mwDescriptor.getMWClass().allMethods();
        while (it.hasNext()) {
            MWMethod method = (MWMethod)it.next();
            if (method.getName().equals(methodName)) {
                if ((method.getMethodParameter(0) != null) && (method.getMethodParameter().getType().equals(paramType))) {
                    return method;
                }
            }
        }
        MWMethod method = mwDescriptor.getMWClass().addMethod(methodName);
        method.addMethodParameter(paramType);
        return method;
    }

    // ===========================================================================
    private MWMapping generateDirectCollectionMapping(TopLinkJAXBProperty property, MWOXDescriptor mwDescriptor) {
        String attributeName = "_";// + JaxbUtil.className(property.getName());
        String type;
        if (property.isCollectionType()) {
            type = property.getCollectionTypeName();
        } else {
            type = property.getJavaTypeName();
        }

        MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, type, mwDescriptor);

        MWXmlDirectCollectionMapping mapping = (MWXmlDirectCollectionMapping)mwDescriptor.addDirectCollectionMapping(mwAttribute);
        if (mwDescriptor.getSchemaContext() != null) {
            mapping.getXmlField().setXpath(getQualifiedNameForProperty(property) + "/text()");
        }

        MWClass methodType = this.mwRepository.typeNamed(type);
        mapping.setUsesMethodAccessing(true);
        mapping.setGetMethod(getOrCreateGetterMethod(getMethodName(property.getName(), type), mwDescriptor, methodType));
        mapping.setSetMethod(getOrCreateSetterMethod(setMethodName(property.getName()), mwDescriptor, methodType));
        return mapping;
    }

    private void generateNillableMapping(MWOXDescriptor mwDescriptor) {
        String typeName = "boolean";
        String attributeName = "_Nil";

        MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, typeName, mwDescriptor);
        MWXmlDirectMapping mapping = (MWXmlDirectMapping)mwDescriptor.addDirectMapping(mwAttribute);

        String prefix = this.mwSchema.namespacePrefixForUrl(XMLConstants.SCHEMA_INSTANCE_URL);
        mapping.getXmlField().setXpath("@" + prefix + ":" + "nil");
    }

    private MWMapping generateDirectMapping(TopLinkJAXBProperty property, MWOXDescriptor mwDescriptor, boolean isAttribute) {
        String attributeName = "_";// + JaxbUtil.className(property.getName());
        MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, property.getJavaTypeName(), mwDescriptor);
        MWXmlDirectMapping mapping = (MWXmlDirectMapping)mwDescriptor.addDirectMapping(mwAttribute);

        if ((property.getDefaultValue() != null) && (!property.getDefaultValue().equals(""))) {
            mapping.setUseNullValue(true);

            String typeClass = getWrapperClass(property.getJavaTypeName());
            MWClass nullValueType = null;
            if (typeClass == null) {
                nullValueType = this.mwRepository.typeNamed(property.getJavaTypeName());
            } else {
                nullValueType = this.mwRepository.typeNamed(typeClass);
            }

            mapping.getNullValuePolicy().setNullValue(property.getDefaultValue());
            mapping.getNullValuePolicy().setNullValueType(new MWTypeDeclaration((MWDefaultNullValuePolicy)mapping.getNullValuePolicy(), nullValueType));
        }

        mapping.setUsesMethodAccessing(true);

        MWClass methodType = this.mwRepository.typeNamed(property.getJavaTypeName());

        // handle primitives - we use a wrapper class to prevent writing default values for null
        // '_' is added to the method names to differentiate it from the primitive setter/getter
        if (getWrapperClass(property.getJavaTypeName()) != null) {
            mapping.setGetMethod(getOrCreateGetterMethod("_" + getMethodName(property.getName(), getWrapperClass(property.getJavaTypeName())), mwDescriptor, methodType));
            mapping.setSetMethod(getOrCreateSetterMethod("_" + setMethodName(property.getName()), mwDescriptor, methodType));
        } else {
            mapping.setGetMethod(getOrCreateGetterMethod(getMethodName(property.getName(), property.getJavaTypeName()), mwDescriptor, methodType));
            mapping.setSetMethod(getOrCreateSetterMethod(setMethodName(property.getName()), mwDescriptor, methodType));
        }

        if (mwDescriptor.getSchemaContext() != null) {
            if (isAttribute) {
                mapping.getXmlField().setXpath("@" + getQualifiedNameForProperty(property));
            } else {
                mapping.getXmlField().setXpath(getQualifiedNameForProperty(property) + "/text()");
            }
        }
        return mapping;
    }

    private MWMapping generateSimpleTypeMapping(String typeName, ArrayList schemaBaseTypes, MWOXDescriptor mwDescriptor) {
        String attributeName = "_Value";

        MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, typeName, mwDescriptor);
        MWXmlDirectMapping mapping = (MWXmlDirectMapping)mwDescriptor.addDirectMapping(mwAttribute);
        mapping.getXmlField().setXpath("text()");
        return mapping;
    }

    private MWMapping generateCompositeCollectionMapping(TopLinkJAXBProperty property, MWOXDescriptor mwDescriptor, TopLinkJAXBBindingSchema referenceClass) {
        String attributeName = "_" ;//+ JaxbUtil.className(property.getName());
        MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, property.getCollectionTypeName(), mwDescriptor);

        MWCompositeCollectionMapping mapping = mwDescriptor.addCompositeCollectionMapping(mwAttribute);
        if (mwDescriptor.getSchemaContext() != null) {
            mapping.getXmlField().setXpath(getQualifiedNameForProperty(property));
        }
        String baseTypeName = this.getFullImplClassName(referenceClass);
        MWOXDescriptor referenceDescriptor = (MWOXDescriptor)this.allDescriptors.get(baseTypeName);
        mapping.setReferenceDescriptor(referenceDescriptor);

        return mapping;
    }

    private MWMapping generateCompositeObjectMapping(TopLinkJAXBProperty property, MWOXDescriptor mwDescriptor, TopLinkJAXBBindingSchema referenceClass) {
        String attributeName = "_";// + JaxbUtil.className(property.getName());
        MWClassAttribute mwAttribute = getOrCreateAttribute(attributeName, property.getJavaTypeName(), mwDescriptor);

        MWCompositeObjectMapping mapping = mwDescriptor.addCompositeObjectMapping(mwAttribute);

        // handle class name customization
        if (referenceClass.isClassCustomization()) {
            mapping.getXmlField().setAggregated(true);
        } else if (mwDescriptor.getSchemaContext() != null) {
            mapping.getXmlField().setXpath(getQualifiedNameForProperty(property));
        }

        String baseTypeName = this.getFullImplClassName(referenceClass);
        MWOXDescriptor referenceDescriptor = (MWOXDescriptor)this.allDescriptors.get(baseTypeName);
        mapping.setReferenceDescriptor(referenceDescriptor);
        return mapping;
    }

    // ==============================================================================================================================================
    public void writeSessionsXml(String sessionName, String projectFileName, String outputDir) throws TopLinkJAXBGenerationException {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<?xml version=\"1.0\" encoding=\"US-ASCII\"?>").append(lsep);
        buffer.append("<toplink-sessions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"file://xsd/sessions_10_0_3.xsd\" version=\"0\">").append(lsep);
        buffer.append("   <session xsi:type=\"database-session\">").append(lsep);
        buffer.append("      <name>").append(sessionName).append("</name>").append(lsep);
        buffer.append("      <primary-project xsi:type=\"xml\">").append(projectFileName).append("</primary-project>").append(lsep);
        buffer.append("      <login xsi:type=\"xml-login\">").append(lsep);
        buffer.append("         <platform-class>");
        if (this.useDomPlatform) {
            buffer.append("org.eclipse.persistence.oxm.platform.DOMPlatform");
        } else {
            buffer.append("org.eclipse.persistence.oxm.platform.SAXPlatform");
        }
        buffer.append("</platform-class>").append(lsep);

        buffer.append("      </login>").append(lsep);
        buffer.append("   </session>").append(lsep);
        buffer.append("</toplink-sessions>");

        writeFile(outputDir, "sessions.xml", buffer);
    }

    private void writeAfterLoadClass(String outputDir) throws TopLinkJAXBGenerationException {
        if (this.afterLoadBuffer != null) {
            closeAfterLoadClass();
            writeFile(outputDir, "DescriptorAfterLoads.java", this.afterLoadBuffer);
        }
    }

    private void closeAfterLoadClass() throws TopLinkJAXBGenerationException {
        this.afterLoadBuffer.append(this.indent).append("}");
    }

    // ==============================================================================================================================================
    // UTILITY METHODS
    // ==============================================================================================================================================
    public boolean isClassFromThisProject(String className) {
        if (this.bindingSchemas.get(className) != null) {
            return true;
        }
        return false;
    }

    public void writeFile(String dir, String filename, StringBuffer content) throws TopLinkJAXBGenerationException {
        try {
            File directory = new File(dir);
            directory.mkdirs();
            File file = new File(dir, filename);
            FileWriter fw = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fw);

            pw.print(content.toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            throw new TopLinkJAXBGenerationException(e);
        }
    }

    private String getFullClassName(TopLinkJAXBBindingSchema schema) {
        return getFullClassName(schema, false, false);
    }

    private String getFullClassName(TopLinkJAXBBindingSchema schema, boolean fullImpl, boolean dollarSign) {
        return schema.getFullClassName(fullImpl, dollarSign);
    }

    private String getFullImplClassName(TopLinkJAXBBindingSchema schema) {
        return getFullClassName(schema, true, false);
    }

    private TopLinkJAXBBindingSchema getSchemaByJavaName(String javaName) {
        if (javaName == null) {
            return null;
        }

        return getSchemaByJavaName(javaName, this.bindingSchemas.elements());
    }

    private TopLinkJAXBBindingSchema getSchemaByJavaName(String javaName, Enumeration schemasEnum) {
        while (schemasEnum.hasMoreElements()) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)schemasEnum.nextElement();
            String schemaJavaName = getFullClassName(schema);

            if (javaName.equals(schemaJavaName)) {
                return schema;
            } else {
                TopLinkJAXBBindingSchema returnSchema = getSchemaByJavaName(javaName, schema.getInnerInterfaces().elements());
                if (returnSchema != null) {
                    return returnSchema;
                }
            }
        }
        return null;
    }

    private TopLinkJAXBBindingSchema getSchemaByNodeName(String nodeName) {
        return getSchemaByNodeName(nodeName, this.bindingSchemas.elements());
    }

    private TopLinkJAXBBindingSchema getSchemaByNodeName(String nodeName, Enumeration schemas) {
        TopLinkJAXBBindingSchema toReturn = null;
        while (schemas.hasMoreElements()) {
            TopLinkJAXBBindingSchema next = (TopLinkJAXBBindingSchema)schemas.nextElement();
            if (next.getNodeName().equals(nodeName)) {
                //If we have a complextType keep looking to see if there's also
                //an element declaration with the same name.
                if (next.isComplexType() && (toReturn == null)) {
                    toReturn = next;
                } else if (next.isElement()) {
                    return next;
                }
            } else {
                TopLinkJAXBBindingSchema returnSchema = getSchemaByNodeName(nodeName, next.getInnerInterfaces().elements());
                if (returnSchema != null) {
                    return returnSchema;
                }
            }
        }
        return toReturn;
    }

    private String convertToDollarSignNotation(String innerClassName) {
        int lastDot = innerClassName.lastIndexOf(".");
        String nameWithDollarSign = innerClassName.substring(0, lastDot);
        nameWithDollarSign += "$";
        nameWithDollarSign += innerClassName.substring(lastDot + 1);
        return nameWithDollarSign;
    }

    public String stripExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    public String getWrapperClass(String classType) {
        if (classType.equals("int")) {
            return "java.lang.Integer";
        } else if (classType.equals("boolean")) {
            return "java.lang.Boolean";
        } else if (classType.equals("char")) {
            return "java.lang.Character";
        } else if (classType.equals("short")) {
            return "java.lang.Short";
        } else if (classType.equals("byte")) {
            return "java.lang.Byte";
        } else if (classType.equals("float")) {
            return "java.lang.Float";
        } else if (classType.equals("double")) {
            return "java.lang.Double";
        } else if (classType.equals("long")) {
            return "java.lang.Long";
        }
        return null;
    }

    public String getPrimitiveType(String wrapperClass) {
        if (wrapperClass.equals("java.lang.Integer")) {
            return "int";
        } else if (wrapperClass.equals("java.lang.Boolean")) {
            return "boolean";
        } else if (wrapperClass.equals("java.lang.Character")) {
            return "char";
        } else if (wrapperClass.equals("java.lang.Short")) {
            return "short";
        } else if (wrapperClass.equals("java.lang.Byte")) {
            return "byte";
        } else if (wrapperClass.equals("java.lang.Byte[]")) {
            return "byte[]";
        } else if (wrapperClass.equals("java.lang.Float")) {
            return "float";
        } else if (wrapperClass.equals("java.lang.Double")) {
            return "double";
        } else if (wrapperClass.equals("java.lang.Long")) {
            return "long";
        }
        return null;
    }

    public ArrayList getSubSchemasFor(TopLinkJAXBBindingSchema schema) {
        ArrayList list = new ArrayList();
        Iterator schemas = this.bindingSchemas.values().iterator();
        while (schemas.hasNext()) {
            TopLinkJAXBBindingSchema child = (TopLinkJAXBBindingSchema)schemas.next();
            if ((child.getExtendsNode() != null) && (getSchemaByJavaName(child.getExtendsNode()) == schema)) {
                list.add(child);
            }
        }
        return list;
    }

    private boolean isDirectType(String type) {
        if (type == null) {
            return false;
        }
        if ((type.equals("int")) || (type.equals("boolean")) || (type.equals("float")) || (type.equals("double")) || (type.equals("long")) || (type.equals("byte")) || (type.equals("byte[]")) || (type.equals("short"))) {
            return true;
        } else if (type.equals("java.lang.String")) {
            return true;
        } else if ((type.equals("java.math.BigInteger")) || (type.equals("java.math.BigDecimal"))) {
            return true;
        } else if (type.equals("java.util.Calendar")) {
            return true;
        } else if (type.equals("javax.xml.namespace.QName")) {
            return true;
        } else if (getPrimitiveType(type) != null) {
            return true;
        }
        return false;
    }

    /**
    * Returns the set method name corresponding to a string
    * @exclude
    */
    public static String setMethodName(String s) {
        StringBuffer sbuf = new StringBuffer();
//        sbuf.append("set").append(JaxbUtil.methodName(s, false));
        return sbuf.toString();
    }

    /**
     * Returns the get method name corresponding to a string
     * @exclude
     */
    public static String getMethodName(String s, String baseType) {
        StringBuffer sbuf = new StringBuffer();

        if (baseType.equals("boolean")) {
//            sbuf.append("is").append(JaxbUtil.methodName(s, false));
        } else {
//            sbuf.append("get").append(JaxbUtil.methodName(s, false));
        }
        return sbuf.toString();
    }

    // ===========================================================================		
    private void pushIndent() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < INDENT_TAB; i++) {
            buf.append(" ");
        }
        this.indent = this.indent + buf.toString();
    }

    private void popIndent() {
        StringBuffer buf = new StringBuffer();
        int size = this.indent.length() - INDENT_TAB;
        for (int i = 0; i < size; i++) {
            buf.append(" ");
        }
        this.indent = buf.toString();
    }
}