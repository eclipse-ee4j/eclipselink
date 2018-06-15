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
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import javax.resource.cci.*;

/**
 * Provides the behavior of instantiating a XML file adapter ConnectionSpec.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileConnectionSpec implements ConnectionSpec {

    /** Stored the default directory for file access. */
    protected String directory;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public XMLFileConnectionSpec() {
        this("./");
    }

    /**
     * PUBLIC:
     * Construct the spec with the default directory.
     */
    public XMLFileConnectionSpec(String directory) {
        this.directory = directory;
    }

    /**
     * PUBLIC:
     * Return the default directory for file access.
     */
    protected String getDirectory() {
        return directory;
    }

    /**
     * PUBLIC:
     * Set the default directory for file access.
     */
    protected void setDirectory(String directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "XMLFileConnectionSpec(" + getDirectory() + ")";
    }
}
