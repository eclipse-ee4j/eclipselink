/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
@Entity(name="AnnMergeSmallProject")
@Table(name="CMP3_ANN_MERGE_PROJECT")
@DiscriminatorValue("2")
public class SmallProject extends Project {
}
