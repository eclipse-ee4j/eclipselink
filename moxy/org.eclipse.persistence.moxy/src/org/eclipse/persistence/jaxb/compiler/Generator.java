/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
import java.util.HashMap;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.schema.SchemaModelProject;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaModelInput;
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
        annotationsProcessor.processClassesAndProperties(jModelInput.getJavaClasses());
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

    public Project generateProject() throws Exception {
        return mappingsGenerator.generateProject(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToNamespaceMappings(), annotationsProcessor.getGlobalElements());
    }
    
    public java.util.Collection<Schema> generateSchema() {
        schemaGenerator.generateSchema(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToNamespaceMappings(), null);
        return schemaGenerator.getAllSchemas();
    }
    
    public HashMap<String, SchemaTypeInfo> generateSchemaFiles(String schemaPath, HashMap<QName, String> additionalElements) throws FileNotFoundException {
        schemaGenerator.generateSchema(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToNamespaceMappings(), annotationsProcessor.getGlobalElements());
        Project proj = new SchemaModelProject();
        XMLContext context = new XMLContext(proj);
        XMLMarshaller marshaller = context.createMarshaller();
        XMLDescriptor schemaDescriptor = (XMLDescriptor)proj.getDescriptor(Schema.class);

        java.util.Collection<Schema> schemas = schemaGenerator.getAllSchemas();
        int schemaCount = 0;
        for(Schema schema : schemas) {
            File file = new File(schemaPath + "/" + schema.getName());
            NamespaceResolver schemaNamespaces = schema.getNamespaceResolver();
            schemaNamespaces.put(XMLConstants.SCHEMA_PREFIX, "http://www.w3.org/2001/XMLSchema");
            schemaDescriptor.setNamespaceResolver(schemaNamespaces);
            marshaller.marshal(schema, new FileOutputStream(file));
            schemaCount++;
        }
        return schemaGenerator.getSchemaTypeInfo();
    }
    
    public HashMap<String, SchemaTypeInfo> generateSchemaFiles(SchemaOutputResolver outputResolver, HashMap<QName, String> additonalGlobalElements) {
        schemaGenerator.generateSchema(annotationsProcessor.getTypeInfoClasses(), annotationsProcessor.getTypeInfo(), annotationsProcessor.getUserDefinedSchemaTypes(), annotationsProcessor.getPackageToNamespaceMappings(), annotationsProcessor.getGlobalElements());
        Project proj = new SchemaModelProject();
        XMLContext context = new XMLContext(proj);
        XMLMarshaller marshaller = context.createMarshaller();

        XMLDescriptor schemaDescriptor = (XMLDescriptor)proj.getDescriptor(Schema.class);

        java.util.Collection<Schema> schemas = schemaGenerator.getAllSchemas();
        int schemaCount = 0;
        for(Schema schema : schemas) {
            try {
                NamespaceResolver schemaNamespaces = schema.getNamespaceResolver();
                schemaNamespaces.put(XMLConstants.SCHEMA_PREFIX, "http://www.w3.org/2001/XMLSchema");
                schemaDescriptor.setNamespaceResolver(schemaNamespaces);
                javax.xml.transform.Result target = outputResolver.createOutput(schema.getTargetNamespace(), schema.getName());
                marshaller.marshal(schema, target);
                schemaCount++;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
		return schemaGenerator.getSchemaTypeInfo();
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
    
}
