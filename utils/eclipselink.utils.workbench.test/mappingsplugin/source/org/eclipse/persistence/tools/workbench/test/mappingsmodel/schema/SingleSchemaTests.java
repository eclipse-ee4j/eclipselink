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
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.schema;

import java.io.File;
import java.net.URISyntaxException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.utility.io.FileTools;

public class SingleSchemaTests 
	extends SchemaTests 
{
	
	public static Test suite() {
		return new TestSuite(SingleSchemaTests.class);
	}
	
	public SingleSchemaTests(String name) {
		super(name);
	}
	
	public void testLoadSchemaFromFile() 
		throws ResourceException, URISyntaxException
	{
		String absoluteSchemaFilePath = FileTools.resourceFile("/schema/" + this.adjustSchemaName("BasicSchema.xsd")).getAbsolutePath();
		File testSchemasDirectory = new File(absoluteSchemaFilePath).getParentFile().getParentFile();
		String testSchemasDirectoryPath = testSchemasDirectory.getPath();
		String relativeSchemaFilePath = "." + absoluteSchemaFilePath.substring(testSchemasDirectoryPath.length());
		
		MWOXProject project = new MWOXProject("Test Load Schema From File", MappingsModelTestTools.buildSPIManager());
		project.setSaveDirectory(testSchemasDirectory);
		MWXmlSchemaRepository repository = project.getSchemaRepository();
		
		// test absolute file location
		MWXmlSchema absolutePathSchema = repository.createSchemaFromFile("AbsolutePathSchemaFromFile", absoluteSchemaFilePath);
		absolutePathSchema.reload();
		
		// test relative file location
		MWXmlSchema relativePathSchema = repository.createSchemaFromFile("RelativePathSchemaFromFile", relativeSchemaFilePath);
		relativePathSchema.reload();
	}
	
	public void testLoadSchemaFromUrl()
		throws ResourceException, URISyntaxException
	{
		String schemaUrlString = this.getClass().getResource("/schema/" + this.adjustSchemaName("BasicSchema.xsd")).toString();
		
		MWOXProject project = new MWOXProject("Test Load Schema From URL", MappingsModelTestTools.buildSPIManager());
		MWXmlSchemaRepository repository = project.getSchemaRepository();
		MWXmlSchema schema = repository.createSchemaFromUrl("SchemaFromUrl", schemaUrlString);
		schema.reload();
	}
	
	public void testLoadSchemaFromClasspath()
		throws ResourceException, URISyntaxException
	{
		String absoluteSchemaFilePath = FileTools.resourceFile("/schema/" + this.adjustSchemaName("BasicSchema.xsd")).getAbsolutePath();
		File testSchemasDirectory = new File(absoluteSchemaFilePath).getParentFile().getParentFile();
		String testSchemasDirectoryPath = testSchemasDirectory.getPath();
		String schemaResourceName = absoluteSchemaFilePath.substring(testSchemasDirectoryPath.length() + 1);
		
		// test schema not on classpath
		MWOXProject project = new MWOXProject("Test Load Schema From Classpath", MappingsModelTestTools.buildSPIManager());
		MWXmlSchemaRepository schemaRepository = project.getSchemaRepository();
		try {
			schemaRepository.createSchemaFromClasspath("SchemaFromClasspath", schemaResourceName);
			assertTrue("ResourceException was not thrown.", false);
		}
		catch (ResourceException re) {}
		catch (Throwable t) {
			assertTrue("ResourceException was not thrown.", false);
		}
		
		// test schema on classpath
		project.getRepository().addClasspathEntry(testSchemasDirectoryPath);
		schemaRepository.createSchemaFromClasspath("SchemaFromClasspath", schemaResourceName);
	}
	
	public void testLoadEmptySchemas()
		throws ResourceException
	{
		MWXmlSchema schema;
		
		schema = this.loadSchema("EmptySchema");
		assertEquals(schema.targetNamespaceUrl(), "");
		
		schema = this.loadSchema("EmptySchemaWithTargetNS");
		assertEquals(schema.targetNamespaceUrl(), "http://www.target-namespace.com");
	}
	
	public void testLoadBasicSchema()
		throws ResourceException
	{
		this.loadSchema("BasicSchema");	
	}
	
	public void testRefreshBasicSchema()
		throws ResourceException
	{
		MWXmlSchema basicSchema = this.loadSchema("BasicSchema");
		int originalAttributeCount = basicSchema.attributeCount();
		int originalElementCount = basicSchema.elementCount();
		int originalTypeCount = basicSchema.typeCount();
		int originalGroupCount = basicSchema.modelGroupDefinitionCount();
		
		basicSchema.reload();
		
		assertTrue("The number of attributes changed.", basicSchema.attributeCount() == originalAttributeCount);
		assertTrue("The number of elements changed.", basicSchema.elementCount() == originalElementCount);
		assertTrue("The number of types changed.", basicSchema.typeCount() == originalTypeCount);
		assertTrue("The number of groups changed.", basicSchema.modelGroupDefinitionCount() == originalGroupCount);
		
		this.reloadSchema(basicSchema, "BasicSchemaWithComponentsRemoved");
		
		assertTrue("The number of attributes did not decrease.", basicSchema.attributeCount() < originalAttributeCount);
		assertTrue("The number of elements did not decrease.", basicSchema.elementCount() < originalElementCount);
		assertTrue("The number of types did not decrease.", basicSchema.typeCount() < originalTypeCount);
		assertTrue("The number of groups did not decrease.", basicSchema.modelGroupDefinitionCount() < originalGroupCount);
		
		this.reloadSchema(basicSchema, "BasicSchemaWithComponentsAdded");
		
		assertTrue("The number of attributes did not increase.", basicSchema.attributeCount() > originalAttributeCount);
		assertTrue("The number of elements did not increase.", basicSchema.elementCount() > originalElementCount);
		assertTrue("The number of types did not increase.", basicSchema.typeCount() > originalTypeCount);
		assertTrue("The number of groups did not increase.", basicSchema.modelGroupDefinitionCount() > originalGroupCount);
	}
	
	public void testRefreshSimpleType()
		throws ResourceException
	{
		MWXmlSchema schema = this.loadSchema("BasicSimpleType");
		
		assertNotNull(schema.simpleType("simple-type-1"));
		assertNotNull(schema.simpleType("simple-type-2"));
		assertNotNull(schema.simpleType("simple-type-3"));
	}
	
	public void testRefreshComplexType()
		throws ResourceException
	{
		MWXmlSchema schema = this.loadSchema("BasicComplexType");
		MWComplexTypeDefinition complexType = schema.complexType("complex-type");
		int originalTotalElementCount = complexType.totalElementCount();
		int originalAttributeCount = complexType.attributeCount();
		
		assertEquals(complexType.getBaseType().getName(), "anyType");
		assertTrue("The type is abstract.", ! complexType.isAbstract());
		assertTrue("The number of total elements is zero.", originalTotalElementCount != 0);
		assertTrue("The number of attributes is zero.", originalAttributeCount != 0);
		
		schema.reload();
		
		assertTrue("The abstract flag changed.", ! complexType.isAbstract());
		assertTrue("The number of total elements changed.", complexType.totalElementCount() == originalTotalElementCount);
		assertTrue("The number of attributes changed.", complexType.attributeCount() == originalAttributeCount);
		
		this.reloadSchema(schema, "BasicComplexTypeWithReferences");
		assertTrue("The number of total elements changed.", complexType.totalElementCount() == originalTotalElementCount);
		assertTrue("The number of attributes changed.", complexType.attributeCount() == originalAttributeCount);
		
		this.reloadSchema(schema, "BasicComplexTypeWithSimpleBaseType");
		
		assertNotNull(complexType.getBaseType());
		assertTrue("The number of total elements is not zero.", complexType.totalElementCount() == 0);
		
		this.reloadSchema(schema, "BasicComplexTypeWithComplexBaseType");
		
		assertNotNull(complexType.getBaseType());
		assertTrue("The base type is not abstract.", ((MWComplexTypeDefinition) complexType.getBaseType()).isAbstract());
		assertTrue("The number of total elements did not increase.", complexType.totalElementCount() > originalTotalElementCount);
		assertTrue("The number of attributes did not increase.", complexType.attributeCount() > originalAttributeCount);
		
		this.reloadSchema(schema, "BasicComplexType");
		assertEquals(complexType.getBaseType().getName(), "anyType");
		assertTrue("The number of total elements did not decrease.", complexType.totalElementCount() == originalTotalElementCount);
		assertTrue("The number of attributes did not decrease.", complexType.attributeCount() == originalAttributeCount);
	}
	
	public void testRefreshType()
		throws ResourceException
	{
		MWXmlSchema schema = this.loadSchema("BasicType_Simple");
		assertNotNull(schema.simpleType("type"));
		
		this.reloadSchema(schema, "BasicType_Complex");
		assertNotNull(schema.complexType("type"));
		
		this.reloadSchema(schema, "BasicType_Simple");
		assertNotNull(schema.simpleType("type"));
	}
	
	public void testRefreshTopLevelAttribute()
		throws ResourceException
	{
		MWXmlSchema schema = this.loadSchema("BasicAttribute");
		MWAttributeDeclaration attribute = schema.attribute("attribute");
		
		assertNotNull(attribute);
		assertNotNull(attribute.getType());
	}
	
	public void testRefreshTopLevelElement()
		throws ResourceException
	{
		MWXmlSchema schema = this.loadSchema("BasicElementWithSimpleType");
		
		assertNotNull(schema.element("element").getType());
		
		this.reloadSchema(schema, "BasicElementWithComplexType");
		
		assertNotNull(schema.element("element").getType());
		
		this.reloadSchema(schema, "BasicElementWithSimpleType");
		
		assertNotNull(schema.element("element").getType());
	}
		
	public void testLoadForDebugging()
		throws ResourceException
	{
		/*
		MWXmlSchema schema = MWXmlSchema.createFromUrl(null, "DEBUG_SCHEMA", "file://C:/Paul/XMLSpy/Examples/OrgChart.xsd");
		assertTrue("Comment this code out, you clown!", false);
		schema.reload();
		*/
	}

}
