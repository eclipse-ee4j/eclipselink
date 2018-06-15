/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * Used by MWQuery if the format EJBQL is chosen
 */
public final class MWEJBQLQueryFormat
    extends MWStringQueryFormat
{
    // **************** Constructors ******************************************

    /** Default constructor - for TopLink use only. */
    private MWEJBQLQueryFormat()
    {
        super();
    }

    MWEJBQLQueryFormat(MWRelationalSpecificQueryOptions parent)
    {
        super(parent);
    }

    MWEJBQLQueryFormat(MWRelationalSpecificQueryOptions parent, String queryString)
    {
        super(parent, queryString);
    }

    String getType() {
        return MWRelationalQuery.EJBQL_FORMAT;
    }

    public boolean batchReadAttributesAllowed() {
        return true;
    }

    // **************** Conversion ********************************************

    void convertToRuntime(DatabaseQuery runtimeQuery)
    {
        runtimeQuery.setEJBQLString(getQueryString());
    }

    void convertFromRuntime(DatabaseQuery runtimeQuery)
    {
        if (runtimeQuery.getEJBQLString() != null) {
            this.setQueryString(runtimeQuery.getEJBQLString());
        }
    }

    String getRuntimeParameterName(MWQueryParameter parameter)
    {
        return String.valueOf(getQuery().getParameterIndex(parameter) + 1);
    }

    // **************** Persistence *******************************************

    public static XMLDescriptor buildDescriptor()
    {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWEJBQLQueryFormat.class);

        descriptor.getInheritancePolicy().setParentClass(MWStringQueryFormat.class);

        return descriptor;
    }

}
