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

package org.eclipse.persistence.tools.workbench.test;

import org.eclipse.persistence.tools.PackageRenamer;

public class TlRenamer {

    /**
     * @param args
     */
    public static void main(String[] args) {

        PackageRenamer renamer = new PackageRenamer(new String[] {
                "C:/Temp/tlrename.properties",
                "C:/eclipse/workspace/eclipselink.utils.workbench.test",
                "C:/Temp/output/eclipselink.utils.workbench.test",
                "C:/Temp/mwtesttlrename.log"});
        renamer.run();

    }

}
