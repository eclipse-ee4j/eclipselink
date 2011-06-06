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
package org.eclipse.persistence.tools.workbench.test.models.complexinheritance;

import java.io.Serializable;
import java.util.Enumeration;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.ViewDefinition;

public class Computer implements Serializable {
	public int id;
	public int memory;
	public int processorSpeed;
	public String processorMake;
	public String manufacturer;
public static void addToDescriptor(ClassDescriptor descriptor) {

    // In order for this domain model to work with all of our tests, it must be set
    // up so that the transformation mapping below is not added twice.
    // As a result, we check for the mapping before adding it.
    // The reason this mapping is not added in the project is that some Mapping Workbench
    // tests rely on the ammendment method.
    Enumeration mappings = descriptor.getMappings().elements();
    while (mappings.hasMoreElements()){
        DatabaseMapping mapping = (DatabaseMapping)mappings.nextElement();
        if (mapping.isTransformationMapping()){
            Object ctype = ((TransformationMapping)mapping).getFieldNameToMethodNames().get("CTYPE");
            if (ctype != null){
                return;
            }
        }
    }
    
	TransformationMapping typeMapping = new TransformationMapping();
	typeMapping.addFieldTransformation("CTYPE", "getComputerType");
	descriptor.addMapping(typeMapping);

}
public static Mac example1()
{
	Mac mac = new Mac();
	mac.processorSpeed = 166;
	mac.memory = 64;
	mac.processorMake = "Motorolla 6046";
	mac.manufacturer = "Apple";
	
	return mac;
}
public static IBMPC example2()
{
	IBMPC pc = new IBMPC();
	pc.processorSpeed = 133;
	pc.memory = 32;
	pc.processorMake = "Intel Pentium";
	pc.manufacturer = "Dell";
	
	return pc;
}
public static IBMPC example3()
{
	IBMPC pc = new IBMPC();
	pc.processorSpeed = 450;
	pc.memory = 128;
	pc.processorMake = "Intel Pentium II";
	pc.manufacturer = "Compact";
	
	return pc;
}
public static PC example4()
{
	PC pc = new PC();
	pc.processorSpeed = 33;
	pc.memory = 8;
	pc.processorMake = "Motorolla 3000";
	pc.manufacturer = "Apple";
	
	return pc;
}
public static Mainframe example5()
{
	Mainframe mainframe = new Mainframe();
	mainframe.processorSpeed = 5000;
	mainframe.memory = 1600;
	mainframe.processorMake = "R6000";
	mainframe.manufacturer = "IBM";
	mainframe.numberOfProcessors = 12;
	
	return mainframe;
}
public static Class getClassFromRow(Record row)
{
	if (row.get("CTYPE").equals("PC")) {
		if (row.get("PCTYPE").equals("IBM")) {
			return IBMPC.class;
		} else if (row.get("PCTYPE").equals("MAC")) {
			return Mac.class;
		} else {
			return PC.class;
		}
	} else if (row.get("CTYPE").equals("MF")) {
		return Mainframe.class;
	} else {
		return Computer.class;
	}			
}
public String getComputerType()
{
	return "COM";
}
/**
 * Return the view for Oracle.
 */

public static ViewDefinition oracleView()
{
	ViewDefinition definition = new ViewDefinition();

	definition.setName("AllComputers");
	definition.setSelectClause("Select C.*, M.procs"
		+ " from INH_COMP C, INH_MF M"
		+ " where C.id = M.id (+)");

	return definition;
}
/**
 * Return the view for Sybase.
 */

public static ViewDefinition sybaseView()
{
	ViewDefinition definition = new ViewDefinition();

	definition.setName("AllComputers");
	definition.setSelectClause("Select C.*, M.procs"
		+ " from INH_COMP C, INH_MF M"
		+ " where C.id *= M.id");

	return definition;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("INH_COMP");

	definition.addIdentityField("id", Integer.class);
	definition.addField("memory", Integer.class);	
	definition.addField("speed", Integer.class);	
	definition.addField("make", String.class, 100);	
	definition.addField("manufac", String.class, 100);	
	definition.addField("CTYPE", Character.class, 20);	
	definition.addField("PCTYPE", Character.class, 20);	
	FieldDefinition isClone = new FieldDefinition("clone", Boolean.class);
	isClone.setShouldAllowNull(false);
	definition.addField(isClone);

	return definition;
}
}
