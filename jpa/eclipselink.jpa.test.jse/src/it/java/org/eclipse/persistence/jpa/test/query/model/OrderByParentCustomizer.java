/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.test.query.model;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorCustomizer;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.DirectCollectionMapping;

/**
 * Orders the notes of an {@link OrderByParent} by a custom ordering on a field of their collection table, and its remarks
 * by a custom ordering on a field without table.
 */
public class OrderByParentCustomizer implements DescriptorCustomizer {

    @Override
    public void customize(ClassDescriptor descriptor) {
        ((DirectCollectionMapping) descriptor.getMappingForAttributeName("notes")).addOrdering(new ExpressionBuilder().getField("ORDER_BY_NOTES.NOTE").descending());
        ((DirectCollectionMapping) descriptor.getMappingForAttributeName("remarks")).addOrdering(new ExpressionBuilder().getField("REMARK").descending());
    }
}
