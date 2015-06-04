/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial implementation
 ******************************************************************************/
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
