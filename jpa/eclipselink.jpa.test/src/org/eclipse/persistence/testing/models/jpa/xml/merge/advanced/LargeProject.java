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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.xml.merge.advanced;

import javax.persistence.*;

/**
 * This class is used to test XML and annotation merging. This class is mapped
 * in: eclipselink-xml-merge-model/orm-annotation-merge-advanced-entity-mappings.xml
 *
 * This class is currently marked as metadata-complete=true meaning all the
 * annotations defined here should be ignored (somewhat defeating the purpose
 * of XML and Annotation merging testing)
 *
 * Also there are no automated tests that go along with these models, see the
 * test suite: EntityMappingsMergeAdvancedJUnitTestCase. It tests through
 * inspecting descriptor settings only and by no means does extensive
 * validation of all the metadata and defaults.
 */
@Entity(name="AnnMergeLargeProject")
@Table(name="CMP3_ANN_MERGE_LPROJECT")
@DiscriminatorValue("1")
@NamedQuery(
    name="ann_merge_findWithBudgetLargerThan",
    query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
)
public class LargeProject extends Project {
    private double budget;

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
