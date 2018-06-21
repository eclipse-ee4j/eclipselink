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
package org.eclipse.persistence.tools.workbench.scplugin.model.adapter;

import org.eclipse.persistence.internal.sessions.factories.model.log.JavaLogConfig;

/**
 * Session Configuration model adapter class for the
 * TopLink Foudation Library class JavaLogConfig
 *
 * @see JavaLogConfig
 *
 * @author Tran Le
 */
public final class JavaLogAdapter extends LogAdapter {

    public static final String DEFAULT_LOGGING_CLASS = "java.util.logging.*";

    /**
     * Creates a new JavaLogAdapter for the specified model object.
     */
    JavaLogAdapter( SCAdapter parent, JavaLogConfig scConfig) {

        super( parent, scConfig);
    }
    /**
     * Creates a new JavaLogAdapter.
     */
    protected JavaLogAdapter( SCAdapter parent) {

        super( parent);
    }
    /**
     * Factory method for building this model.
     */
    protected Object buildModel() {
        return new JavaLogConfig();
    }

    public void toString( StringBuffer sb) {

        sb.append( "TODO: toString()");
    }

    /**
     * Returns this Config Model Object.
     */
    private final JavaLogConfig log() {

        return ( JavaLogConfig)this.getModel();
    }
}
