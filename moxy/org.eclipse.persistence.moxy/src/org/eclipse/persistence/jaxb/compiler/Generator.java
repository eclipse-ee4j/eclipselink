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
package org.eclipse.persistence.jaxb.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;

import org.eclipse.persistence.core.sessions.CoreProject;
import org.eclipse.persistence.internal.oxm.Constants;
import org.eclipse.persistence.internal.oxm.mappings.Descriptor;
import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.sessions.Project;

/**
 * INTERNAL:
 *  <p><b>Purpose:</b>The purpose of this class is to act as an entry point into the 
 *  TopLink JAXB 2.0 Generation framework
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Run initial processing on a list of classes to create TypeInfo meta data</li>
 *  <li>Provide API to generate Schema Files</li>
 *  <li>Provide API to generate a TopLink Project</li>
 *  <li>Act as an integration point with WebServices</li>
 *  </ul>
 *  <p> This class acts as an entry point into JAXB 2.0 Generation. A Generator is created with a 
 *  specific set of JAXB 2.0 Annotated classes and then performs actions on those, such as
 *  generating schema files, or generating TopLink Projects. Additional information is returned
 *  from the schema generation methods as a means of integration with WebServices.
 *  
 *  @author  mmacivor
 *  @since   Oracle TopLink 11.1.1.0.0
 *  @see AnnotationsProcessor
 *  @see MappingsGenerator
 *  @see SchemaGenerator
 */
public class Generator {
    private AnnotationsProcessor annotationsProcessor;
    private SchemaGenerator schemaGenerator;
    private MappingsGenerator mappingsGenerator;
    private Helper helper;
    private Map<Type, TypeMappingInfo> typeToTypeMappingInfo;

    /**
     * This is the preferred constructor.
     * This constructor creates a Helper using the JavaModelInput 
	 * instance's JavaModel. Annotations are processed here as well.
     * 
     * @param jModelInput
     */
    public Generator(JavaModelInput jModelInput) {
        helper = new Helper(jModelInput.getJavaModel());
        annotationsProcessor = new AnnotationsProcessor(helper);
        schemaGenerator = new SchemaGenerator(helper);
        mappingsGenerator = new MappingsGenerator(helper);
        annotationsProcessor.processClassesAndProperties(jModelInput.getJavaClasses(), null);
    }
    
    /**
     * This constructor will process and apply the given XmlBindings as appropriate.  Classes
     * declared in the bindings will be amalgamated with any classes in the JavaModelInput.
     *  
     * If xmlBindings is null or empty, AnnotationsProcessor will be used to process 
     * annotations as per usual.
     *  
     * @param jModelInput
     * @param xmlBindings map of XmlBindings keyed on package name
     * @param cLoader
     */
    public Generator(JavaModelInput jModelInput, Map<String, XmlBindings> xmlBindings, ClassLoader cLoader, String defaultTargetNamespace, boolean enableXmlAccessorFactory) {
        helper = new Helper(jModelInput.getJavaModel());
        annotationsProcessor = new AnnotationsProcessor(helper);
        annotationsProcessor.setXmlAccessorFactorySupport(enableXmlAccessorFactory);
        annotationsProcessor.setDefaultTargetNamespace(defaultTargetNamespace);
        schemaGenerator = new SchemaGenerator(helper);
        mappingsGenerator = new MappingsGenerator(helper);
        if (xmlBindings != null && xmlBindings.size() > 0) {
            new XMLProcessor(xmlBindings).processXML(annotationsProcessor, jModelInput, null, null);
        } else {
            annotationsProcessor.processClassesAndProperties(jModelInput.getJavaClasses(), null);
        }
    }
    
    /**
     * This constructor creates a Helper using the JavaModelInput 
	 * instance's JavaModel and a map of javaclasses that were generated from Type objects.
	 * Annotations are processed here as well.
     * 
     * @param jModelInput
     */
    public Generator(JavaModelInput jModelInput, TypeMappingInfo[] typeMappingInfos, JavaClass[] javaClasses, Map<Type, TypeMappingInfo> typeToTypeMappingInfo, String defaultTargetNamespace) {
        helper = new Helper(jModelInput.getJavaModel());
        annotationsProcessor = new AnnotationsProcessor(helper);
        annotationsProcessor.setDefaultTargetNamespace(defaultTargetNamespace);
        schemaGenerator = new SchemaGenerator(helper);
        mappingsGenerator = new MappingsGenerator(helper);
        this.typeToTypeMappingInfo = typeToTypeMappingInfo;
        annotationsProcessor.processClassesAndProperties(javaClasses, typeMappingInfos);
    }
    
    /**
     * This constructor will process and apply the given XmlBindings as appropriate.  Classes
     * declared in the bindings will be amalgamated with any classes in the JavaModelInput.
     *  
     * If xmlBindings is null or empty, AnnotationsProcessor will be used to process 
     * annotations as per usual.
     *  
     * @param jModelInput
     * @param javaClassToType
     * @param xmlBindings map of XmlBindings keyed on package name
     * @param cLoader
     */    
    public Generator(JavaModelInput jModelInput, TypeMappingInfo[] typeMappingInfos, JavaClass[] javaClasses, Map<Type, TypeMappingInfo> typeToTypeMappingInfo, Map<String, XmlBindings> xmlBindings, ClassLoader cLoader, String defaultTargetNamespace, boolean enableXmlAccessorFactory) {
        helper = new Helper(jModelInput.getJavaModel());
        annotationsProcessor = new AnnotationsProcessor(helper);
        annotationsProcessor.setXmlAccessorFactorySupport(enableXmlAccessorFactory);
        annotationsProcessor.setDefaultTargetNamespace(defaultTargetNamespace);
        schemaGenerator = new SchemaGenerator(helper);
        mappingsGenerator = new MappingsGenerator(helper);
        this.typeToTypeMappingInfo = typeToTypeMappingInfo;
        if (xmlBindings != null && xmlBindings.size() > 0) {
            new XMLProcessor(xmlBindings).processXML(annotationsProcessor, jModelInput, typeMappingInfos, javaClasses);
        } else {
        	annotationsProcessor.processClassesAndProperties(javaClasses, typeMappingInfos);
        }
    }

    /**
     * This event is called when mappings generation is completed,
     * and provides a chance to deference anything that is no longer
     * needed (to reduce the memory footprint of this object).
     */
    public void postInitialize() {
        mappingsGenerator = null;
        annotationsProcessor.postInitialize();
        schemaGenerator = null;
    }

    /**
     * 
     */
    public boolean hasMarshalCallbacks() {
        return getMarshalCallbacks()!=null && getMarshalCallbacks().size()>0;
    }
    
    public boolean hasUnmarshalCallbacks() {
        return getUnmarshalCallbacks()!=null && getUnmarshalCallbacks().size()>0;
    }
    
    /**
     * INTERNAL:
     * 
     * @param javaClass
     * @return
     */
    public SchemaTypeInfo addClass(JavaClass javaClass) {
        return annotationsProcessor.addClass(javaClass);
    }

    public CoreProject generateProject() throws Exception {
        mappingsGenerator.getClassToGeneratedClasses().putAll(annotationsProcessor.getArrayClassesToGeneratedClasses());
        CoreProject p = mappingsGenerator.generateProject(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToPackageInfoMappings(), annotationsProcessor.getGlobalElements(), annotationsProcessor.getLocalElements(), annotationsProcessor.getTypeMappingInfoToGeneratedClasses(), annotationsProcessor.getTypeMappingInfoToAdapterClasses(),annotationsProcessor.isDefaultNamespaceAllowed());
        annotationsProcessor.getArrayClassesToGeneratedClasses().putAll(mappingsGenerator.getClassToGeneratedClasses());
        return p;
    }

    public java.util.Collection<Schema> generateSchema() {
        schemaGenerator.generateSchema(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToPackageInfoMappings(), null, annotationsProcessor.getArrayClassesToGeneratedClasses());
        return schemaGenerator.getAllSchemas();
    }
    
    public HashMap<String, SchemaTypeInfo> generateSchemaFiles(String schemaPath, Map<QName, Type> additionalGlobalElements) throws FileNotFoundException {
        // process any additional global elements
        processAdditionalElements(additionalGlobalElements, annotationsProcessor);

        schemaGenerator.generateSchema(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToPackageInfoMappings(), annotationsProcessor.getGlobalElements(), annotationsProcessor.getArrayClassesToGeneratedClasses());
        CoreProject proj = new SchemaModelProject();
        XMLContext context = new XMLContext((Project)proj);
        XMLMarshaller marshaller = context.createMarshaller();
        Descriptor schemaDescriptor = (Descriptor)proj.getDescriptor(Schema.class);

        java.util.Collection<Schema> schemas = schemaGenerator.getAllSchemas();
        for(Schema schema : schemas) {
            File file = new File(schemaPath + "/" + schema.getName());
            NamespaceResolver schemaNamespaces = schema.getNamespaceResolver();
            schemaNamespaces.put(Constants.SCHEMA_PREFIX, "http://www.w3.org/2001/XMLSchema");
            schemaDescriptor.setNamespaceResolver(schemaNamespaces);
            marshaller.marshal(schema, new FileOutputStream(file));
        }
        return schemaGenerator.getSchemaTypeInfo();
    }

    public HashMap<String, SchemaTypeInfo> generateSchemaFiles(SchemaOutputResolver outputResolver, Map<QName, Type> additionalGlobalElements) {
        // process any additional global elements
        processAdditionalElements(additionalGlobalElements, annotationsProcessor);
        
        schemaGenerator.generateSchema(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToPackageInfoMappings(), annotationsProcessor.getGlobalElements(), annotationsProcessor.getArrayClassesToGeneratedClasses(), outputResolver);
        CoreProject proj = new SchemaModelProject();
        XMLContext context = new XMLContext((Project)proj);
        XMLMarshaller marshaller = context.createMarshaller();

        Descriptor schemaDescriptor = (Descriptor)proj.getDescriptor(Schema.class);

        java.util.Collection<Schema> schemas = schemaGenerator.getAllSchemas();
        for(Schema schema : schemas) {
            try {
                NamespaceResolver schemaNamespaces = schema.getNamespaceResolver();
                schemaNamespaces.put(Constants.SCHEMA_PREFIX, "http://www.w3.org/2001/XMLSchema");
                schemaDescriptor.setNamespaceResolver(schemaNamespaces);
                // make sure we don't call into the provided output resolver more than once
                javax.xml.transform.Result target;
                if (schema.hasResult()) {
                    target = schema.getResult();
                } else {
                    target = outputResolver.createOutput(schema.getTargetNamespace(), schema.getName());
                }
                marshaller.marshal(schema, target);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		return schemaGenerator.getSchemaTypeInfo();
    }
    
    /**
     * Convenience method that processes a given map of QName-Type entries.  For each an ElementDeclaration
     * is created and added to the given AnnotationsProcessor instance's map of global elements.
     * 
     * It is assumed that the map of QName-Type entries contains Type instances that are either a Class or
     * a ParameterizedType.
     *  
     * @param additionalGlobalElements
     * @param annotationsProcessor
     */
    private void processAdditionalElements(Map<QName, Type> additionalGlobalElements, AnnotationsProcessor annotationsProcessor) {
        if (additionalGlobalElements != null) {
            ElementDeclaration declaration;
            for(Entry<QName, Type> entry : additionalGlobalElements.entrySet()) {
                QName key = entry.getKey();
                Type type = entry.getValue();
                TypeMappingInfo tmi = null;
                if(this.typeToTypeMappingInfo != null) {
                    tmi = this.typeToTypeMappingInfo.get(type);
                }

                if(tmi != null) {
                    if(annotationsProcessor.getTypeMappingInfoToGeneratedClasses().get(tmi) != null) {
                        type =  annotationsProcessor.getTypeMappingInfoToGeneratedClasses().get(tmi);
                    }
                }
                JavaClass jClass = null;
                if (type instanceof Class) {
                    Class tClass = (Class) type; 
                    jClass = helper.getJavaClass(tClass);
                }
                // if no type is available don't do anything
                if (jClass != null) {
                    declaration = new ElementDeclaration(key, jClass, jClass.getQualifiedName(), false);
                    annotationsProcessor.getGlobalElements().put(key, declaration);
                }
            }
        }
    }
    
    public java.util.HashMap getUnmarshalCallbacks() {
        return annotationsProcessor.getUnmarshalCallbacks();
    }

    public java.util.HashMap getMarshalCallbacks() {
        return annotationsProcessor.getMarshalCallbacks();
    }
    
    public MappingsGenerator getMappingsGenerator() {
    	return this.mappingsGenerator;
    }

    public AnnotationsProcessor getAnnotationsProcessor() {
        return annotationsProcessor;
    }
    
    public void setTypeToTypeMappingInfo(Map<Type, TypeMappingInfo> typesToTypeMapping) {
        this.typeToTypeMappingInfo = typesToTypeMapping;
    }

    public Map<Type, TypeMappingInfo> getTypeToTypeMappingInfo() {
        return this.typeToTypeMappingInfo;
    }

}