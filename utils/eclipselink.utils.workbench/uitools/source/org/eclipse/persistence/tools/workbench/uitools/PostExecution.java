/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.tools.workbench.uitools;

import java.awt.Dialog;

/**
 * This <code>PostExecution</code> is used to post execute a portion of code
 * once a dialog, that was launched into a different UI thread, has been
 * disposed.
 *
 * @version 11.0.0
 * @since 11.0.0
 * @author Pascal Filion
 */
public interface PostExecution<T extends Dialog>
{
    /**
     * Notifies this post exection the dialog that was launched into a different
     * UI thread has been disposed.
     *
     * @param dialog The dialog that was launched into a different thread
     */
    public void execute(T dialog);
}
