/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWEisTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.xml.MWOXTransactionalPolicy;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlNode;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.SchemaChange;

/**
 * Queries not currently supported in OX descriptors, but may eventually be supported
 * @author lddavis
 *
 */
public final class MWOXQueryManager extends MWQueryManager implements
        MWXmlNode {

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();

        descriptor.setJavaClass(MWOXQueryManager.class);
        descriptor.getInheritancePolicy().setParentClass(MWQueryManager.class);

        return descriptor;
    }

    // **************** Constructors ******************************************

    //Toplink persistence use only please
    private MWOXQueryManager() {
        super();
    }

    public MWOXQueryManager(MWOXTransactionalPolicy descriptor) {
        super(descriptor);
    }

    @Override
    public MWReadAllQuery buildReadAllQuery(String queryName) {
        return null;
    }

    @Override
    public MWReadObjectQuery buildReadObjectQuery(String queryName) {
        return null;
    }

    public void resolveXpaths() {
    }

    public void schemaChanged(SchemaChange change) {
    }

    public boolean supportsReportQueries() {
        return false;
    }

}
