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
 * @since EclipseLink 1.x
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
                throw DBWSException.couldNotLocateDescriptorForOperation(descriptor,getName());
            }
            ClassDescriptor cd = xrService.getORSession().getProject().
                getDescriptorForAlias(descriptor);
            if (cd.getQueryManager().getQuery(name) == null) {
                throw DBWSException.couldNotLocateQueryForDescriptor(name, descriptor);
            }
        }
        else if (xrService.getORSession().getQuery(name) == null) {
            throw DBWSException.couldNotLocateQueryForSession(name,xrService.getORSession().getName());
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
