/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * <p><b>INTERNAL:</b> NamedQueryHandler retrieves the TopLink {@link DatabaseQuery}
 * from the named query
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */
public class NamedQueryHandler extends QueryHandler {

    protected String name;
    protected String descriptor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(XRServiceAdapter xrService, QueryOperation queryOperation) {
        if (descriptor != null) {
            if (!xrService.getORSession().getProject().getAliasDescriptors().
                containsKey(descriptor)) {
                throw new DBWSException("The descriptor '" + descriptor
                    + "' for operation '" + getName()
                    + "' does not exist in the TopLink O-R project");
            }
            ClassDescriptor cd = xrService.getORSession().getProject().
                getDescriptorForAlias(descriptor);
            if (cd.getQueryManager().getQuery(name) == null) {
                throw new DBWSException("The query '" + name
                    + "' does not exist for descriptor '" + descriptor + "'");
            }
        }
        else if (xrService.getORSession().getQuery(name) == null) {
            throw new DBWSException("The query '" + name
                + "' does not exist for session '" + xrService.getORSession().getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(XRServiceAdapter xrService, QueryOperation queryOperation) {
        if (descriptor != null) {
            ClassDescriptor cd = xrService.getORSession().getProject().
                getDescriptorForAlias(descriptor);
            databaseQuery = cd.getQueryManager().getQuery(name);
        }
        else {
            databaseQuery = xrService.getORSession().getQuery(name);
        }
    }

}
