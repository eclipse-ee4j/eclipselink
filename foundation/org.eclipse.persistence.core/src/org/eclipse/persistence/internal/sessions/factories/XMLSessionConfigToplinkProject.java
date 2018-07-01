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
    public ClassDescriptor buildSessionConfigsDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildSessionConfigsDescriptor();
        descriptor.setDefaultRootElement("toplink-sessions");
        return descriptor;
    }

    public ClassDescriptor buildLogConfigDescriptor() {
        XMLDescriptor descriptor = (XMLDescriptor)super.buildLogConfigDescriptor();
        descriptor.getInheritancePolicy().addClassIndicator(DefaultSessionLogConfig.class, "toplink-log");
        return descriptor;
    }

}
