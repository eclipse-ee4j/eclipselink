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

package org.eclipse.persistence.internal.xr;

//javase imports
import java.util.List;
import java.util.Vector;

//java eXtension imports

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.UnitOfWork;
import static org.eclipse.persistence.internal.xr.Util.PK_QUERYNAME;

/**
 * <p><b>INTERNAL:</b>An XR DeleteOperation is an executable representation of a <tt>DELETE</tt>
 * operation on the database.
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
@SuppressWarnings({"unchecked"/*, "rawtypes"*/})
public class DeleteOperation extends Operation {

    protected String descriptorName;
    protected ClassDescriptor classDescriptor;

    public String getDescriptorName() {
        return descriptorName;
    }
    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public ClassDescriptor getClassDescriptor() {
        return classDescriptor;
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
     * @see  {@link Operation}
     */
    @Override
    public Object invoke(XRServiceAdapter xrService, Invocation invocation) {
        ReadObjectQuery roq =
            (ReadObjectQuery)classDescriptor.getQueryManager().getQuery(PK_QUERYNAME);
        List queryArguments = roq.getArguments();
        int queryArgumentsSize = queryArguments.size();
        Vector executeArguments = new NonSynchronizedVector();
        for (int i = 0; i < queryArgumentsSize; i++) {
            String argName = (String)queryArguments.get(i);
            executeArguments.add(invocation.getParameter(argName));
        }
        UnitOfWork uow = xrService.getORSession().acquireUnitOfWork();
        Object toBeDeleted = uow.executeQuery(roq, executeArguments);
        if (toBeDeleted != null) {
            uow.deleteObject(toBeDeleted);
            uow.commit();
        }
        return null;
    }
}