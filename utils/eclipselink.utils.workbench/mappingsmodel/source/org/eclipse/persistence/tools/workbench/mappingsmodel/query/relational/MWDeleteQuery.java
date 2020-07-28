/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DeleteObjectQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;


public final class MWDeleteQuery extends MWAbstractCustomQuery {

    private MWDeleteQuery() {
        super();
    }

    MWDeleteQuery(MWQueryManager queryManager) {
        super(queryManager);
    }

    // ******************* Static Methods *******************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWDeleteQuery.class);
        descriptor.getDescriptorInheritancePolicy().setParentClass(MWAbstractCustomQuery.class);

        return descriptor;
    }

    @Override
    protected DatabaseQuery buildRuntimeQuery() {
        return new DeleteObjectQuery();
    }

}
