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
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Client implements Serializable {
	public Oid id;			
	public String name;
	public AddressDescription addressDescription;


public Client() 
{
	super();
	setOid(new Oid());
}
public static Client example1()
{
	Client example = new Client();
	
	example.setName("Bob Smith");
	example.setAddressDescription(AddressDescription.example1());
	return example;
}
public static Client example2()
{
	Client example = new Client();
	
	example.setName("Lisa Ray");
	example.setAddressDescription(AddressDescription.example2());
	return example;
}
public static Client example3()
{
	Client example = new Client();
	
	example.setName("David Garner");
	example.setAddressDescription(AddressDescription.example3());
	return example;
}
public Oid getOid()
{
	return id;
}
public void setAddressDescription(AddressDescription aDescription)
{
	addressDescription = aDescription;
}
public void setName(String aName)
{
	name = aName;
}
public void setOid(Oid i)
{
	id = i;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("AGG_CLNT");

	definition.addIdentityField("CL_ID", java.math.BigDecimal.class, 15);
	definition.addField("CL_NAME", String.class, 20);
	definition.addField("CL_ADD", java.math.BigDecimal.class, 15);
	definition.addField("CL_SDATE", java.sql.Date.class);
	definition.addField("CL_EDATE", java.sql.Date.class);
	definition.addField("TYPE", String.class, 10);
	definition.addField("CL_ESDATE", java.sql.Date.class);
	definition.addField("CL_EEDATE", java.sql.Date.class);

	return definition;
}
}
