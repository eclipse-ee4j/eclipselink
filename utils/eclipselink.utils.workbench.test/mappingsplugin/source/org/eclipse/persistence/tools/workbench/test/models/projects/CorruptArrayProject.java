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
package org.eclipse.persistence.tools.workbench.test.models.projects;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWTable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWTableDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.relational.MWRelationalProject;

public class CorruptArrayProject extends RelationalTestProject {


	public static MWRelationalProject emptyProject() {
		MWRelationalProject project = new MWRelationalProject("CorruptArrayTypes", spiManager(), db2Platform());

		// Defaults policy  
   		project.getDefaultsPolicy().setMethodAccessing(true);
		return project;
	}

	@Override
	protected MWProject buildEmptyProject() {
		return emptyProject();
	}
    
    public MWTableDescriptor getCorruptArrayDescriptor() {
        return (MWTableDescriptor)  getProject().descriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.corruptarray.CorruptArray");
    }
   

	public void initializeCorruptArrayTable() {
		MWTable table = database().addTable("CORRUPT_ARRAY");
		
		addField(table,"BLOB_FIELD", "BLOB", 64000);
		addField(table,"CLOB_FIELD", "CLOB", 64000);
	}

	@Override
	protected void initializeDatabase() {
		this.initializeCorruptArrayTable();
	}

	@Override
	protected void initializeDescriptors() {
		super.initializeDescriptors();

		this.addDescriptorForTypeNamed("org.eclipse.persistence.tools.workbench.test.models.corruptarray.CorruptArray");
		
		initializeCorruptArrayDescriptor();
	}

	public void initializeCorruptArrayDescriptor() {
		
		MWTableDescriptor descriptor = getCorruptArrayDescriptor();
        descriptor.getMWClass().attributeNamed("blobField").generateAllAccessors();
        descriptor.getMWClass().attributeNamed("clobField").generateAllAccessors();
		MWTable table = tableNamed("CORRUPT_ARRAY");
		descriptor.setPrimaryTable(table);
	
		//direct to fields
		addDirectMapping(descriptor, "blobField", table, "BLOB_FIELD");
		addDirectMapping(descriptor, "clobField", table, "CLOB_FIELD");

	}
    
}
