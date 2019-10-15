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
