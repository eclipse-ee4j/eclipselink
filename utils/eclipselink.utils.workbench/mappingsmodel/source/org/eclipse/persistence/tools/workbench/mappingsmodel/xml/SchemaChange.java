/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;

public final class SchemaChange
{
	// **************** Attributes ********************************************
	
	/** The schema that changed */
	private MWXmlSchema schema;
	
	private int changeType;
		public final static int SCHEMA_STRUCTURE_CHANGED 			= 0;
		public final static int SCHEMA_NAMESPACE_PREFIXES_CHANGED 	= 1;
	
	
	// **************** Static creators ***************************************
	
	public static SchemaChange schemaStructureChange(MWXmlSchema schema) {
		return new SchemaChange(schema, SCHEMA_STRUCTURE_CHANGED);
	}
	
	public static SchemaChange namespacePrefixesChanged(MWXmlSchema schema) {
		return new SchemaChange(schema, SCHEMA_NAMESPACE_PREFIXES_CHANGED);
	}
	
	
	private SchemaChange(MWXmlSchema schema, int changeType) {
		super();
		this.schema = schema;
		this.changeType = changeType;
	}
	
	
	// **************** API ***************************************************
	
	public MWXmlSchema getSchema() {
		return this.schema;
	}
	
	public int getChangeType() {
		return this.changeType;
	}
}
