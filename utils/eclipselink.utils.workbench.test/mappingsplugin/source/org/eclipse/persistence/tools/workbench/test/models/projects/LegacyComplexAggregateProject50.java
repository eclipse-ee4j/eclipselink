/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Collection;

import org.eclipse.persistence.tools.workbench.mappingsmodel.db.MWColumn;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWRelationalClassDescriptor;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational.MWUserDefinedQueryKey;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

/**
 * The default query timeout used to be zero, but is now -1.  The expected project that
 * is built in memory during legacy 5.0 unit testing has to reflect this new state.  This
 * class correctly sets the timeout value for 5.0 unit test.
 */
public class LegacyComplexAggregateProject50 extends ComplexAggregateProject
{
	/**
	 * 0 is the expected value for query timeout in legacy 50 unit tests.
	 */
	@Override
	protected Integer getDefaultQueryTimeout() {
		return MWQuery.QUERY_TIMEOUT_NO_TIMEOUT;
	}
	
	@Override
	protected String getDefaultQueryLockMode() {
		return MWQuery.NO_LOCK;
	}
	
	//I know this is ugly.  Trying to compensate for the fact that we had no paths to fields
	//for query keys in our legacy projects.  The MW allowed you to specify a query key on the
	//aggregate descriptor but then you could not specify a field on the aggregate mapping
	//This code removes the query key from the built up mw project so that the paths to fields
	//will be updated and remove the pth to field for the query key.  Then it uses reflection
	//to add the query key back so as not to cause the paths to fields to be updated again

	@Override
	public void initializeDescriptors() {
		super.initializeDescriptors();

		getAddressDescriptionDescriptor().removeQueryKey((MWUserDefinedQueryKey) getAddressDescriptionDescriptor().queryKeyNamed("id"));

		MWUserDefinedQueryKey queryKey = (MWUserDefinedQueryKey) 
			ClassTools.newInstance(
					MWUserDefinedQueryKey.class,
					new Class[] {java.lang.String.class, MWRelationalClassDescriptor.class, MWColumn.class},
					new Object[] {"id", getAddressDescriptionDescriptor(), null}
			);
		
		Collection userDefinedQueryKeys = (Collection) ClassTools.getFieldValue(getAddressDescriptionDescriptor(), "userDefinedQueryKeys");
		userDefinedQueryKeys.add(queryKey);
	}
}
