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
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * @author Guy Pelletier
 * @version 1.0
 * @date March 21, 2005
 */
public class BidirectionalRelationshipProject extends Project {
    public BidirectionalRelationshipProject(DatabaseSession session) {
        setName("BidirectionalRelationshipProject");
        applyLogin(session);
        addDescriptor(buildTeamDescriptor());
        addDescriptor(buildPlayerDescriptor());
    }

    public void applyLogin(DatabaseSession session) {
        DatabaseLogin login = (DatabaseLogin)session.getLogin().clone();
        setLogin(login);
    }

    public RelationalDescriptor buildTeamDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Team.class);
        descriptor.addTableName("TEAM");
        descriptor.addPrimaryKeyFieldName("TEAM.ID");

        // RelationalDescriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);

        descriptor.setSequenceNumberFieldName("TEAM.ID");
        descriptor.setSequenceNumberName("team_seq");
        descriptor.setAlias("Team");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("m_id");
        idMapping.setFieldName("TEAM.ID");
        descriptor.addMapping(idMapping);

        OneToManyMapping onetomanymapping = new OneToManyMapping();
        onetomanymapping.setAttributeName("m_players");
        onetomanymapping.setReferenceClass(Player.class);
        onetomanymapping.useTransparentMap("getId");
        onetomanymapping.addTargetForeignKeyFieldName("PLAYER.TEAM_ID", "TEAM.ID");
        descriptor.addMapping(onetomanymapping);

        return descriptor;
    }

    public RelationalDescriptor buildPlayerDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Player.class);
        descriptor.addTableName("PLAYER");
        descriptor.addPrimaryKeyFieldName("PLAYER.ID");
        descriptor.setAlias("Player");

        // RelationalDescriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("m_id");
        idMapping.setFieldName("PLAYER.ID");
        descriptor.addMapping(idMapping);

        OneToOneMapping onetoonemapping = new OneToOneMapping();
        onetoonemapping.setAttributeName("m_team");
        onetoonemapping.setReferenceClass(Team.class);
        onetoonemapping.useBasicIndirection();
        onetoonemapping.addForeignKeyFieldName("PLAYER.TEAM_ID", "TEAM.ID");
        onetoonemapping.setRelationshipPartnerAttributeName("m_players");
        descriptor.addMapping(onetoonemapping);

        return descriptor;
    }
}
