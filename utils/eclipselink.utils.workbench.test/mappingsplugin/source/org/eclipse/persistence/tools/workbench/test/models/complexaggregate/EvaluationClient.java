/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.models.complexaggregate;

import java.io.Serializable;

import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class EvaluationClient extends Client implements Serializable {
	public Period evaluationPeriod;
public static Client example1() {
	EvaluationClient example = new EvaluationClient();
	
	example.setName("Will Smith");
	example.setAddressDescription(AddressDescription.example1());
	example.setEvaluationPeriod(Period.example4());
	return example;
}
public static Client example2() {
	EvaluationClient example = new EvaluationClient();
	
	example.setName("Jimmy Ray");
	example.setAddressDescription(AddressDescription.example2());
	example.setEvaluationPeriod(Period.example5());
	return example;
}
public static Client example3() {
	EvaluationClient example = new EvaluationClient();
	
	example.setName("Richard Garner");
	example.setAddressDescription(AddressDescription.example3());
	example.setEvaluationPeriod(Period.example6());
	return example;
}
public void setEvaluationPeriod(Period aPeriod)
{
	evaluationPeriod = aPeriod;
}
/**
 * Return a platform independant definition of the database table.
 */

public static TableDefinition tableDefinition()
{
	TableDefinition definition = new TableDefinition();

	definition.setName("AGG_ECNT");

	definition.addIdentityField("CL_ID", java.math.BigDecimal.class, 15);
	definition.addField("EV_SDATE", java.sql.Date.class);
	definition.addField("EV_EDATE", java.sql.Date.class);

	return definition;
}
}
