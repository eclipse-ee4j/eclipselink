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
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.MWObjectTypeConverter;
import org.eclipse.persistence.tools.workbench.mappingsmodel.mapping.relational.MWRelationalDirectMapMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.project.MWTransactionalProjectDefaultsPolicy;

//Support for RelationalDirectMapMapping was added in 10.1.3.  
public class EmployeeProject extends LegacyEmployeeProject {
	
	public EmployeeProject() {
		super();
	}

	@Override
	protected void legacyRemoveExtraAttributesFromEmployeeClass() {
		//override to do nothing
	}

    @Override
	public void initializeDescriptors() {
        super.initializeDescriptors();
        getProject().getDefaultsPolicy().addAdvancedPolicyDefault(MWTransactionalProjectDefaultsPolicy.RETURNING_POLICY);
    }
    
	@Override
	public void initializeEmployeeDescriptor() {
		super.initializeEmployeeDescriptor();
		MWTableDescriptor employeeDescriptor = getEmployeeDescriptor();
		MWTable emailTable = database().tableNamed("EMAIL_ADDRESS");
		
		//direct map mappings
		MWRelationalDirectMapMapping fooMapMapping =
			(MWRelationalDirectMapMapping) employeeDescriptor.addDirectMapMapping(employeeDescriptor.getMWClass().attributeNamed("emailAddressMap"));
		fooMapMapping.setTargetTable(emailTable);
		fooMapMapping.setReference(emailTable.referenceNamed("EMAIL_ADDRESS_EMPLOYEE"));
		fooMapMapping.setDirectKeyColumn(emailTable.columnNamed("DESCRIPTION"));
		MWObjectTypeConverter objectTypeConverter = fooMapMapping.setObjectTypeConverter();
		try {
			objectTypeConverter.addValuePair("foo", "foo");
		} catch (MWObjectTypeConverter.ConversionValueException e) {
			//shoulnd't happen
		}
		fooMapMapping.setDirectValueColumn(emailTable.columnNamed("ADDRESS"));
		fooMapMapping.setSerializedObjectDirectKeyConverter();
        
		//TODO proxy indirection, when runtime supports it        
//        MWRelationalTransformationMapping mapping = (MWRelationalTransformationMapping) employeeDescriptor.mappingNamed("normalHours");
//        mapping.setUseProxyIndirection();
        
//        MWOneToOneMapping oneToOneMapping = (MWOneToOneMapping) employeeDescriptor.mappingNamed("address");
//        oneToOneMapping.setUseProxyIndirection();       
        
	}
	
	public void initializeEmailTable() {	
		MWTable emailTable = database().addTable("EMAIL_ADDRESS");
		addPrimaryKeyField(emailTable,"EMP_ID", "integer");
		addField(emailTable,"DESCRIPTION", "integer");		
		addField(emailTable,"ADDRESS", "varchar");		
	}
	
	
	@Override
	protected void initializeDatabase() {
		super.initializeDatabase();

		initializeEmailTable();
	
		//create references
		
		MWTable employeeTable = database().tableNamed("EMPLOYEE");
		MWTable emailTable = database().tableNamed("EMAIL_ADDRESS");
		
		addReferenceOnDB("EMAIL_ADDRESS_EMPLOYEE", emailTable, employeeTable, "EMP_ID", "EMP_ID");
	}
}
