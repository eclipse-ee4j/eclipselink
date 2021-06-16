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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.jdkversiondetection;

import org.eclipse.persistence.*;
import org.eclipse.persistence.internal.helper.JavaSEPlatform;

/**
 *  Simple class for manual test of JDK version detection.
 *  This class implements a main() method which makes use of our org.eclipse.persistence.Version
 *  class to detect JDK versions.  It then prints the results.
 */
public class JDKVersionDetectionTester
{

    public static void main(String[] args)
    {
        System.out.println(Version.getProduct() + " " + Version.getVersion());
        System.out.println("Build " + Version.getVersionString() + " on " + Version.getBuildDate());
        System.out.println("Java SE platform - " + JavaSEPlatform.CURRENT.toString());
    }
}
