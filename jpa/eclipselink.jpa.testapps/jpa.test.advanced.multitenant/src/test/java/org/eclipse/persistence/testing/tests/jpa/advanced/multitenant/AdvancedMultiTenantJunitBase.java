/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     04/01/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 2)
//     04/21/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 5)
//     05/24/2011-2.3 Guy Pelletier
//       - 345962: Join fetch query when using tenant discriminator column fails.
//     06/1/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 9)
//     06/30/2011-2.3.1 Guy Pelletier
//       - 341940: Add disable/enable allowing native queries
//     09/09/2011-2.3.1 Guy Pelletier
//       - 356197: Add new VPD type to MultitenantType
//     11/10/2011-2.4 Guy Pelletier
//       - 357474: Address primaryKey option from tenant discriminator column
//     11/15/2011-2.3.2 Guy Pelletier
//       - 363820: Issue with clone method from VPDMultitenantPolicy
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     22/05/2012-2.4 Guy Pelletier
//       - 380008: Multitenant persistence units with a dedicated emf should force tenant property specification up front.
//     01/06/2011-2.3 Guy Pelletier
//       - 371453: JPA Multi-Tenancy in Bidirectional OneToOne Relation throws ArrayIndexOutOfBoundsException
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
//     20/11/2012-2.5 Guy Pelletier
//       - 394524: Invalid query key [...] in expression
package org.eclipse.persistence.testing.tests.jpa.advanced.multitenant;

import org.eclipse.persistence.testing.framework.jpa.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.multitenant.AdvancedMultiTenantTableCreator;

import java.util.ArrayList;
import java.util.List;

public class AdvancedMultiTenantJunitBase extends JUnitTestCase {

    public static long candidateAId;
    public static long supporter1Id;
    public static long supporter2Id;
    public static int ridingId;
    public static int partyId;
    public static int masonId;

    public static int family707;
    public static int family007;
    public static int family123;
    public static int capo123Id;
    public static int soldier123Id;
    public static List<Integer> family707Mafiosos = new ArrayList<>();
    public static List<Integer> family707Contracts = new ArrayList<>();
    public static List<Integer> family007Mafiosos = new ArrayList<>();
    public static List<Integer> family007Contracts = new ArrayList<>();
    public static List<Integer> family123Mafiosos = new ArrayList<>();
    public static List<Integer> family123Contracts = new ArrayList<>();

    public AdvancedMultiTenantJunitBase() {
        super();
    }

    public AdvancedMultiTenantJunitBase(String name) {
        super(name);
        setPuName(getPersistenceUnitName());
    }

    /**
     * The setup is done as a test, both to record its failure, and to allow execution in the server.
     */
    public void testSetup() {
        new AdvancedMultiTenantTableCreator().replaceTables(JUnitTestCase.getServerSession(getPersistenceUnitName()));
    }

    public void testWriteProjectCache(){
        fail("TODO");
//        new org.eclipse.persistence.testing.tests.jpa.advanced.MetadataCachingTestSuite().testFileBasedProjectCacheLoading(getPersistenceUnitName());
    }
}
