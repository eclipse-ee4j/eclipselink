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
/* $Header: DataObjectInputStream.java 22-nov-2006.16:27:02 mfobrien Exp $ */
/*
   DESCRIPTION
    This class wraps the ObjectInputStream with a HelperContext instance in order to preserve state and
    make the InputStream context aware.

   NOTES
    Implementor is ExternalizableDelegator

   MODIFIED    (MM/DD/YY)
    mfobrien    11/22/06 - Creation
 */

/**
 *  @version $Header: DataObjectInputStream.java 22-nov-2006.16:27:02 mfobrien Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.sdo.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

public class DataObjectInputStream extends ObjectInputStream {
    private HelperContext aHelperContext;

    /**
     * Constructor for single static context clients
     * @throws IOException
     */
    public DataObjectInputStream() throws IOException {
        super();
        // default to static context
        aHelperContext = HelperProvider.getDefaultContext();
    }

    /**
     * Constructor for single static context clients
     * @param in
     * @throws IOException
     */
    public DataObjectInputStream(InputStream in) throws IOException {
        super(in);
        // default to static context
        aHelperContext = HelperProvider.getDefaultContext();
    }

    /**
     * Constructor for general use by multi-threaded clients
     * @param in
     * @param aContext
     * @throws IOException
     */
    public DataObjectInputStream(InputStream in, HelperContext aContext) throws IOException {
        super(in);
        // set a static or dynamic-instance context
        aHelperContext = aContext;
    }

    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
