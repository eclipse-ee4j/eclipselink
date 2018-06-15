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
package org.eclipse.persistence.tools.workbench.mappingsmodel.resource;

import java.net.URL;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class ClasspathResourceSpecification
    extends ResourceSpecification
{
    private static String KEY = "CLASSPATH_RESOURCE";


    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(ClasspathResourceSpecification.class);

        descriptor.getInheritancePolicy().setParentClass(ResourceSpecification.class);

        return descriptor;
    }


    // **************** Constructors ******************************************

    /** For TopLink only */
    private ClasspathResourceSpecification() {
        super();
    }

    public ClasspathResourceSpecification(MWModel parent, String resourceName) {
        super(parent, resourceName);
    }


    // **************** ResourceSpecification contract ************************

    public String getSourceKey() {
        return KEY;
    }

    protected URL resourceUrl()
        throws ResourceException
    {
        if (this.location == null || this.location == "") {
            throw ResourceException.unspecifiedResourceException(null);
        }

        URL resourceUrl = this.getRepository().findResource(this.location);

        if (resourceUrl == null) {
            throw ResourceException.nonexistentResourceException(null);
        }

        return resourceUrl;
    }
}
