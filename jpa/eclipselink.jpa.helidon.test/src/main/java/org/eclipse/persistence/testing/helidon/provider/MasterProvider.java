/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.helidon.provider;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.persistence.testing.helidon.dao.MasterDao;
import org.eclipse.persistence.testing.helidon.models.MasterEntity;

@ApplicationScoped
public class MasterProvider {

    @Inject
    private MasterDao masterDao;

    public MasterEntity getMasterOne() {
        MasterEntity masterEntity = masterDao.find(MasterEntity.class, 1L);
        return masterEntity;
    }

    public List<MasterEntity> getMasterAll() {
        List<MasterEntity> masterEntities = masterDao.findByNamedQuery("MasterEntity.findAll");
        return masterEntities;
    }

    public void createRemove() {
        MasterEntity masterEntity = new MasterEntity(3L, "Master 3");
        masterDao.create(masterEntity);
        masterDao.remove(masterEntity);
    }

}
