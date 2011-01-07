/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.resource.ResourceException;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchemaRepository;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public abstract class SchemaTests 
	extends TestCase 
{
	public SchemaTests(String name) {
		super(name);
	}
	
	protected String adjustSchemaName(String schemaName) {
		return ClassTools.shortClassNameForObject(this) + "/" + schemaName;
	}
	
	protected MWXmlSchema loadSchema(String schemaName)
		throws ResourceException
	{
		schemaName = this.adjustSchemaName(schemaName);
		MWOXProject project = new MWOXProject("Schema Test Project", MappingsModelTestTools.buildSPIManager());
		MWXmlSchemaRepository repository = project.getSchemaRepository();
		
		MWXmlSchema schema = repository.createSchemaFromUrl(schemaName, this.getClass().getResource("/schema/" + schemaName + ".xsd").toString());
		schema.reload();
		return schema;
	}
	
	protected void reloadSchema(MWXmlSchema schema, String newSchemaName)
		throws ResourceException
	{
		newSchemaName = this.adjustSchemaName(newSchemaName);
		schema.setUrlSchemaLocation(this.getClass().getResource("/schema/" + newSchemaName + ".xsd").toString());
		schema.reload();
	}
}
