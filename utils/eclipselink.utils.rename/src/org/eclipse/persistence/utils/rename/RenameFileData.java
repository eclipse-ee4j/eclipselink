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

package org.eclipse.persistence.utils.rename;

public class RenameFileData {

    private String fileContentsString = null;
    private boolean changed = false;

    public RenameFileData(String fileContentsString, boolean changed) {
        this.fileContentsString = fileContentsString;
        this.changed = changed;
    }

    public String getFileContentsString() {
        return fileContentsString;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setFileContentsString(String contents) {
        this.fileContentsString = contents;
    }

}
