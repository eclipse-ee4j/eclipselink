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
package org.eclipse.persistence.testing.tests.queries;

import java.util.*;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.sessions.*;

/**
 * Runs cursored stream tests with conforming.
 * @bug 3009570 Conforming Cursors
 * @author Stephen McRitchie
 */
	
public class CursoredStreamConformingTest extends CursoredStreamTest 
{
	protected ConformingTestConfiguration configuration;
	protected boolean useNestedUnitOfWork;

public CursoredStreamConformingTest() {
	super(Employee.class, (new ExpressionBuilder()).get("salary").greaterThan(50000));
	configuration = new ConformingTestConfiguration();
}

public CursoredStreamConformingTest(boolean useNestedUnitOfWork) {
	this();
	if (useNestedUnitOfWork) {
		setName(getName() + ":NestedUnitOfWork");
		this.useNestedUnitOfWork = true;
	}
	
}
protected void setup() 
{	
	getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

	configuration.setup(getSession());
	UnitOfWork unitOfWork = configuration.getUnitOfWork();
	
	ReadAllQuery query = new ReadAllQuery(getReferenceClass(), joinExpression);
	query.conformResultsInUnitOfWork();
	Vector result = (Vector)unitOfWork.executeQuery(query);

	// Since conforming can not preserve order, must simply compare the
	// two results by PK.
	Vector expectedPKs = new Vector(result.size());
	for (Enumeration enumtr = result.elements(); enumtr.hasMoreElements();) {
		Employee emp = (Employee)enumtr.nextElement();
		expectedPKs.add(emp.getId());
	}
	setNormalQueryObjects(expectedPKs);
	
	configuration.reset();
	getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

	configuration.setup(getSession());
	if (useNestedUnitOfWork) {
		getExecutor().setSession(configuration.getUnitOfWork().acquireUnitOfWork());		
	} else {
		getExecutor().setSession(configuration.getUnitOfWork());
	}
}
public void test ( ) {
	
	ReadAllQuery query = new ReadAllQuery();
	CursoredStream stream = null;
	
	try {
		Object databaseObject;
	
		cursoredQueryObjects = new Vector();
	
		query.setReferenceClass(getReferenceClass());
		query.setSelectionCriteria(joinExpression);
		query.conformResultsInUnitOfWork();
		query.useCursoredStream();
		stream = (CursoredStream) getSession().executeQuery(query);
		
		// Test dual cursors and read(int)
		CursoredStream stream2 = (CursoredStream) getSession().executeQuery(query);
		try {
		stream2.read(5);
		} catch (org.eclipse.persistence.exceptions.QueryException ex) {} // ignore at end	
		stream2.close();
		while (!stream.atEnd()) {
			databaseObject = stream.read();		
			getCursoredQueryObjects().addElement(((Employee)databaseObject).getId());
			stream.releasePrevious();	
		}
	} finally {	
		if (stream != null) {
			stream.close();
		}
	}	
}
/**
 * Verify if number of query objects matches number of cursor objects
 */
protected void verify() 
{	
	//System.out.println("Normal objects: " + getNormalQueryObjects());
	//System.out.println("Conforming objects: " + getCursoredQueryObjects());
	if (getNormalQueryObjects().size() != getCursoredQueryObjects().size()) {
		throw new TestErrorException("The number of streamed objects does not match the number of objects stored on the database ");
	}

	if (getNormalQueryObjects().size() == 0) {
		throw new TestWarningException("no object with the specified selection criteria was found ");
	}

	for (Enumeration enumtr = getNormalQueryObjects().elements(); enumtr.hasMoreElements();) {
		Object nextObject = enumtr.nextElement();
		if (!getCursoredQueryObjects().remove(nextObject)) {
			throw new TestErrorException("The following element was not found in the conformed result: " + nextObject);
		}
	}	
}

public void reset() {
	if (useNestedUnitOfWork) {
		getExecutor().getSession().release();
	}
	getExecutor().setSession(configuration.getUnitOfWork().getParent());
	configuration.reset();
}
}
