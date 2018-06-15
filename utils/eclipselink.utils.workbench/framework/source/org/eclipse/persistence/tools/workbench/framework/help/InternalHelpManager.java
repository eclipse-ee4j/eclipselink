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
package org.eclipse.persistence.tools.workbench.framework.help;

/**
 * Used by HelpFacade internally.
 */
interface InternalHelpManager extends HelpManager {

    /**
     * Set whether we had problems loading the "local" help book.
     */
    void setLocalHelpFailed(boolean localHelpFailed);

    /**
     * The host application has finished its start-up;
     * the Help system can now interact with the user.
     */
    void launchComplete();

}
