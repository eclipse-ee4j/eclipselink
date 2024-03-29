/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.nchar;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.RelationalDescriptor;

/**
 * This class was generated by the TopLink project class generator.
 * It stores the meta-data (descriptors) that define the TopLink mappings.
 * ## TopLink - 9.0.3 (Build 423) ##
 * @see org.eclipse.persistence.sessions.factories.ProjectClassGenerator
 */
public class CharNcharProject extends org.eclipse.persistence.sessions.Project {

    public CharNcharProject() {
        setName("CharNchar");
        applyLogin();

        addDescriptor(buildCharNcharDescriptor());
    }

    @Override
    public void applyLogin() {
        DatabaseLogin login = new DatabaseLogin();
        setLogin(login);
    }

    public RelationalDescriptor buildCharNcharDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.nchar.CharNchar.class);
        descriptor.addTableName("CHARNCHAR");
        descriptor.addPrimaryKeyFieldName("CHARNCHAR.ID");

        // Descriptor properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("CHARNCHAR.ID");
        descriptor.setSequenceNumberName("CHARNCHAR_SEQ");
        descriptor.setAlias("CharNchar");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        //Named Queries

        // Event manager.

        // Mappings.
        DirectToFieldMapping chMapping = new DirectToFieldMapping();
        chMapping.setAttributeName("ch");
        chMapping.setFieldName("CHARNCHAR.CH");
        descriptor.addMapping(chMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CHARNCHAR.ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nChMapping = new DirectToFieldMapping();
        nChMapping.setAttributeName("nCh");
        nChMapping.setFieldName("CHARNCHAR.NCH");
        nChMapping.setFieldClassification(org.eclipse.persistence.platform.database.oracle.NCharacter.class);
        descriptor.addMapping(nChMapping);


        DirectToFieldMapping nStrMapping = new DirectToFieldMapping();
        nStrMapping.setAttributeName("nStr");
        nStrMapping.setFieldName("CHARNCHAR.NSTR");
        nStrMapping.setFieldClassification(org.eclipse.persistence.platform.database.oracle.NString.class);
        descriptor.addMapping(nStrMapping);

        DirectToFieldMapping strMapping = new DirectToFieldMapping();
        strMapping.setAttributeName("str");
        strMapping.setFieldName("CHARNCHAR.STR");
        descriptor.addMapping(strMapping);

        DirectToFieldMapping clobMapping = new DirectToFieldMapping();
        clobMapping.setAttributeName("clob");
        clobMapping.setFieldName("CHARNCHAR.CLB");
        clobMapping.setFieldClassification(java.sql.Clob.class);
        descriptor.addMapping(clobMapping);

        DirectToFieldMapping nClobMapping = new DirectToFieldMapping();
        nClobMapping.setAttributeName("nClob");
        nClobMapping.setFieldName("CHARNCHAR.NCLB");
        nClobMapping.setFieldClassification(org.eclipse.persistence.platform.database.oracle.NClob.class);
        descriptor.addMapping(nClobMapping);

        DirectToFieldMapping clob2Mapping = new DirectToFieldMapping();
        clob2Mapping.setAttributeName("clob2");
        clob2Mapping.setFieldName("CHARNCHAR.CLB2");
        clob2Mapping.setFieldClassification(java.sql.Clob.class);
        descriptor.addMapping(clob2Mapping);

        DirectToFieldMapping nClob2Mapping = new DirectToFieldMapping();
        nClob2Mapping.setAttributeName("nClob2");
        nClob2Mapping.setFieldName("CHARNCHAR.NCLB2");
        nClob2Mapping.setFieldClassification(org.eclipse.persistence.platform.database.oracle.NClob.class);
        descriptor.addMapping(nClob2Mapping);

        return descriptor;
    }

}
