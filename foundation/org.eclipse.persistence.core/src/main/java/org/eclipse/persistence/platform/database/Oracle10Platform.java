/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     06/26/2018 - Will Dazey
//       - 532160 : Add support for non-extension OracleXPlatform classes
package org.eclipse.persistence.platform.database;

/**
 * <p><b>Purpose:</b>
 * Provides Oracle version specific behavior when 
 * org.eclipse.persistence.oracle bundle is not available.
 */
public class Oracle10Platform extends Oracle9Platform {

    public Oracle10Platform(){
        super();
    }

    /**
     * Build the hint string used for first rows.
     * 
     * Allows it to be overridden
     * @param max
     * @return
     */
    protected String buildFirstRowsHint(int max){
        //bug 374136: override setting the FIRST_ROWS hint as this is not needed on Oracle10g
        return "";
    }
    
    /**
     * INTERNAL:
     * Indicate whether app. server should unwrap connection
     * to use lob locator.
     * No need to unwrap connection because
     * writeLob method doesn't use oracle proprietary classes.
     */
    @Override
    public boolean isNativeConnectionRequiredForLobLocator() {
        return false;
    }
}
