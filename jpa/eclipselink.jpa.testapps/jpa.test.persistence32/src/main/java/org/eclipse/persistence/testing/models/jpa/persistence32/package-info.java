/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
@TableGenerators({
        @TableGenerator(
                name = "TableGeneratorPackageScope",
                table = "PERSISTENCE32_TABLE_PCKG_GENERATOR",
                pkColumnName = "SEQ_NAME",
                valueColumnName = "SEQ_COUNT"
        ),
        @TableGenerator(
                table = "PERSISTENCE32_TABLE_PCKG_GENERATOR1",
                pkColumnName = "SEQ_NAME",
                valueColumnName = "SEQ_COUNT"
        )
})
@SequenceGenerators({
        @SequenceGenerator(
                name = "SequenceGeneratorPackageScope",
                sequenceName = "PERSISTENCE32_SEQUENCE_PCKG_GENERATOR",
                allocationSize = 1
        ),
        @SequenceGenerator(
                sequenceName = "PERSISTENCE32_SEQUENCE_PCKG_GENERATOR1",
                allocationSize = 1
        )
})

package org.eclipse.persistence.testing.models.jpa.persistence32;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.TableGenerators;

