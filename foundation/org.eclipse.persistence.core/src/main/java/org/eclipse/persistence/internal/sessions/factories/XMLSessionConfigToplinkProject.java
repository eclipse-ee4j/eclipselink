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

package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.sessions.factories.model.log.DefaultSessionLogConfig;
import org.eclipse.persistence.oxm.XMLDescriptor;
/**
 * INTERNAL:
 * OX mapping project provides back compatibility for toplink
 * 10g and 11g sessions XML meta-data reading.
 */

public class XMLSessionConfigToplinkProject extends  XMLSessionConfigProject{
    @Override
    public ClassDescriptor buildSessionConfigsDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildSessionConfigsDescriptor();
        descriptor.setDefaultRootElement("toplink-sessions");
        return descriptor;
    }

    @Override
    public ClassDescriptor buildLogConfigDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildLogConfigDescriptor();
        descriptor.getInheritancePolicy().addClassIndicator(DefaultSessionLogConfig.class, "toplink-log");
        return descriptor;
    }

}
