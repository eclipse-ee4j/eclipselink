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

package org.eclipse.persistence.internal.xr;

import static org.eclipse.persistence.internal.xr.Util.PK_QUERYNAME;
import static org.eclipse.persistence.internal.xr.Util.TYPE_STR;
import static org.eclipse.persistence.internal.xr.Util.UNDERSCORE_STR;

//javase imports
import java.util.List;
import java.util.Vector;

//java eXtension imports

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.sessions.UnitOfWork;

/**
 * <p><b>INTERNAL:</b>An XR DeleteOperation is an executable representation of a <tt>DELETE</tt>
 * operation on the database.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings({"unchecked"})
public class DeleteOperation extends Operation {
    protected String descriptorName;
    protected ClassDescriptor classDescriptor;
    protected String findByPKQuery;

    public String getDescriptorName() {
        return descriptorName;
    }
    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
    }

    /**
     * Return the findByPrimaryKey query that this DeleteOperation
     * will use to acquire the object to delete.
     *
     */
    public String getFindByPKQuery() {
        // provide backward compatibility
        if (findByPKQuery == null) {
            findByPKQuery = PK_QUERYNAME + UNDERSCORE_STR + descriptorName + TYPE_STR;
        }
        return findByPKQuery;
    }

    /**
     * Set the findByPrimaryKey query that this DeleteOperation
     * will use to acquire the object to delete.
     *
     */
    public void setFindByPKQuery(String findByPKQuery) {
        this.findByPKQuery = findByPKQuery;
    }

    @Override
    public void validate(XRServiceAdapter xrService) {
        super.validate(xrService);
        if (descriptorName == null) {
            throw DBWSException.couldNotLocateDescriptorForOperation(descriptorName, getName());
        }
        if (!xrService.getORSession().getProject().getAliasDescriptors().containsKey(descriptorName)) {
            throw DBWSException.couldNotLocateDescriptorForOperation(descriptorName, getName());
        }
        classDescriptor = xrService.getORSession().getProject().getDescriptorForAlias(descriptorName);
    }

    /**
     * Execute <tt>DELETE</tt> operation on the database
     * @param   xrService parent <code>XRService</code> that owns this <code>Operation</code>
     * @param   invocation contains runtime argument values to be bound to the list of
     *          {@link Parameter}'s.
     * @return  result - can be <code>null</code> if the underlying <tt>DELETE</tt> operation on the
     *          database does not return a value
     *
     * @see  Operation
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        DatabaseQuery query = classDescriptor.getQueryManager().getQuery(getFindByPKQuery());

        // a named query created via ORM metadata processing needs initialization
        if (query instanceof JPAQuery) {
            query = ((JPAQuery) query).processSQLQuery(xrService.getORSession().getActiveSession());
        }

        UnitOfWork uow = xrService.getORSession().acquireUnitOfWork();
        Object toBeDeleted;

        // a query created via ORM metadata processing does not have parameters set, however, the operation should
        if (query.getArguments().size() == 0) {
            int idx = 0;
            for (Parameter  param : getParameters()) {
                // for custom SQL query (as configured via ORM metadata processing) we add args by position
                query.addArgument(Integer.toString(++idx), Util.SCHEMA_2_CLASS.get(param.getType()));
                query.addArgumentValue(invocation.getParameter(param.getName()));
            }
            toBeDeleted = uow.executeQuery(query);
        } else {
            // set query args or execute args for the non-JPAQuery case,
            // i.e. stored proc/funcs get populated from ORM metadata
            // whereas named queries (SQL strings) do not...
            List queryArguments = query.getArguments();
            int queryArgumentsSize = queryArguments.size();
            Vector executeArguments = new NonSynchronizedVector();
            for (int i = 0; i < queryArgumentsSize; i++) {
                String argName = (String)queryArguments.get(i);
                executeArguments.add(invocation.getParameter(argName));
            }
            toBeDeleted = uow.executeQuery(query, executeArguments);
        }

        // JPAQuery will return a single result in a Vector
        if (!isCollection() && toBeDeleted instanceof Vector) {
            if (((Vector) toBeDeleted).isEmpty()) {
                toBeDeleted = null;
            } else {
                toBeDeleted = ((Vector)toBeDeleted).firstElement();
            }
        }
        if (toBeDeleted != null) {
            uow.deleteObject(toBeDeleted);
            uow.commit();
        }
        return null;
    }
}
