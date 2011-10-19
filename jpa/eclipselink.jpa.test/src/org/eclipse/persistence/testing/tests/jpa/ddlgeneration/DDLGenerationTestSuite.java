/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle, Frank Schwarz. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     08/20/2008-1.0.1 Nathan Beyer (Cerner) 
 *       - 241308: Primary key is incorrectly assigned to embeddable class 
 *                 field with the same name as the primary key field's name
 *     01/12/2009-1.1 Daniel Lo, Tom Ware, Guy Pelletier
 *       - 247041: Null element inserted in the ArrayList 
 *     07/17/2009 - tware -  added tests for DDL generation of maps
 *     01/22/2010-2.0.1 Guy Pelletier 
 *       - 294361: incorrect generated table for element collection attribute overrides
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     09/15/2010-2.2 Chris Delahunt
 *       - 322233 - AttributeOverrides and AssociationOverride dont change field type info
 *     11/17/2010-2.2.0 Chris Delahunt 
 *       - 214519: Allow appending strings to CREATE TABLE statements
 *     11/23/2010-2.2 Frank Schwarz 
 *       - 328774: TABLE_PER_CLASS-mapped key of a java.util.Map does not work for querying
 *     01/04/2011-2.3 Guy Pelletier 
 *       - 330628: @PrimaryKeyJoinColumn(...) is not working equivalently to @JoinColumn(..., insertable = false, updatable = false)
 *     01/06/2011-2.3 Guy Pelletier
 *       - 312244: can't map optional one-to-one relationship using @PrimaryKeyJoinColumn
 *     01/11/2011-2.3 Guy Pelletier  
 *       - 277079: EmbeddedId's fields are null when using LOB with fetchtype LAZY
 ******************************************************************************/   
package org.eclipse.persistence.testing.tests.jpa.ddlgeneration;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

import javax.persistence.EntityManager;

/**
 * JUnit test case(s) for DDL generation.
 */
public class DDLGenerationTestSuite extends DDLGenerationJUnitTestSuite {
    // This is the persistence unit name on server as for persistence unit name "ddlGeneration" in J2SE
    private static final String DDL_PU = "MulitPU-1";

    public DDLGenerationTestSuite() {
        super();
    }

    public DDLGenerationTestSuite(String name) {
        super(name);
        setPuName(DDL_PU);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.setName("DDLGenerationTestSuite");
        suite.addTest(new DDLGenerationTestSuite("testSetup"));
        List<String> tests = new ArrayList<String>();
        tests.add("testDDLPkConstraintErrorIncludingRelationTableColumnName");
        tests.add("testOptionalPrimaryKeyJoinColumnRelationship");
        tests.add("testPrimaryKeyJoinColumns");
        tests.add("testDDLEmbeddableMapKey");
        tests.add("testDDLAttributeOverrides");
        tests.add("testDDLAttributeOverridesOnElementCollection");
        tests.add("testDDLUniqueKeysAsJoinColumns");
        tests.add("testDDLUniqueConstraintsByAnnotations");
        tests.add("testDDLUniqueConstraintsByXML");
        tests.add("testDDLSubclassEmbeddedIdPkColumnsInJoinedStrategy");
        tests.add("testBug241308");
        tests.add("testDDLUnidirectionalOneToMany");
        tests.add("testCascadeMergeOnManagedEntityWithOrderedList");
        tests.add("testDirectCollectionMapping");
        tests.add("testAggregateCollectionMapping");
        tests.add("testOneToManyMapping");
        tests.add("testUnidirectionalOneToManyMapping");
        tests.add("testManyToManyMapping");
        tests.add("testManyToManyWithMultipleJoinColumns");
        tests.add("testEmbeddedManyToMany");
        tests.add("testAssociationOverrideToEmbeddedManyToMany");
        tests.add("testLAZYLOBWithEmbeddedId");
        if (! JUnitTestCase.isJPA10()) {
            tests.add("testCreateMafiaFamily707");
            tests.add("testCreateMafiaFamily007");
            tests.add("testValidateMafiaFamily707");
            tests.add("testValidateMafiaFamily007");
        }
        Collections.sort(tests);
        for (String test : tests) {
            suite.addTest(new DDLGenerationTestSuite(test));
        }
        return suite;
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        // Trigger DDL generation
        EntityManager em = createEntityManager(DDL_PU);
        closeEntityManager(em);
        clearCache(DDL_PU);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
