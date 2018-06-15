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
//     dminsky - initial implementation
package org.eclipse.persistence.testing.models.jpa.advanced;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.history.HistoryPolicy;
import org.eclipse.persistence.internal.helper.DatabaseTable;

public class AdvancedHistoryCustomizer implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        HistoryPolicy policy = new HistoryPolicy();
        
        for (DatabaseTable table : descriptor.getTables()) {
            policy.addHistoryTableName(table.getName() + "_HIST");
        }

        policy.addStartFieldName("START_DATE");
        policy.addEndFieldName("END_DATE");
        policy.setShouldUseDatabaseTime(false);
        descriptor.setHistoryPolicy(policy);
    }

}
