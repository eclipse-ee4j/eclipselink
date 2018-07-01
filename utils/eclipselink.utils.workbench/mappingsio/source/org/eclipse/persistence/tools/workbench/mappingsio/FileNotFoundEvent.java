/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsio;

import java.io.File;
import java.util.EventObject;

/**
 * A "FileNotFound" event gets delivered whenever an "expected" file is not found.
 */
public class FileNotFoundEvent extends EventObject {
    /** the missing file */
    private File missingFile;

    public FileNotFoundEvent(Object source, File missingFile) {
        super(source);
        this.missingFile = missingFile;
    }

    public File getMissingFile() {
        return this.missingFile;
    }

}
