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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;

import org.eclipse.persistence.oxm.XMLDescriptor;

public final class FileResourceSpecification
    extends ResourceSpecification
{
    private static String KEY = "FILE_RESOURCE";


    // **************** Static methods ****************************************

    public static XMLDescriptor buildDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(FileResourceSpecification.class);

        descriptor.getInheritancePolicy().setParentClass(ResourceSpecification.class);

        return descriptor;
    }


    // **************** Constructors ******************************************

    /** For TopLink only */
    private FileResourceSpecification() {
        super();
    }

    public FileResourceSpecification(MWModel parent, String filePath) {
        super(parent, filePath);
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

        File absoluteFile = this.absoluteFile();
        if ( ! absoluteFile.exists()) {
            throw ResourceException.nonexistentResourceException(null);
        }

        try {
            return absoluteFile.toURL();
        }
        catch (MalformedURLException mue) {
            throw ResourceException.incorrectlySpecifiedResourceException(mue);
        }
    }


    // **************** Internal **********************************************

    private File absoluteFile() {
        File file = new File(this.location);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(this.getProject().getSaveDirectory(), this.location);
    }


    // **************** TopLink **********************************************

    /**
     * convert to platform-independent representation
     */
    protected String getLocationForTopLink2() {
        return this.location.replace('\\', '/');
    }

    /**
     * convert to platform-specific representation
     */
    protected void setLocationForTopLink2(String fileName) {
        this.location = new File(fileName).getPath();
    }

}
