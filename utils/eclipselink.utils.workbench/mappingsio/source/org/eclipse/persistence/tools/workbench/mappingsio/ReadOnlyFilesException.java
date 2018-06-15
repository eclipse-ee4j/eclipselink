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
package org.eclipse.persistence.tools.workbench.mappingsio;

import java.io.File;
import java.util.Collection;

/**
 * This exception will be thrown by a ProjectWriter, and its calling
 * ProjectIOManager, if any read-only files are encountered while
 * writing out a project.
 */
public class ReadOnlyFilesException extends Exception {
    /** The list of read-only files. */
    private File[] files;

    ReadOnlyFilesException(File[] files) {
        super();
        this.files = files;
    }

    ReadOnlyFilesException(Collection files) {
        this((File[]) files.toArray(new File[files.size()]));
    }

    public File[] getFiles() {
        return this.files;
    }

}
