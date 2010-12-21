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
package org.eclipse.persistence.testing.models.directmap;

import java.util.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.mappings.converters.*;

/**
 * Project definition for the DirectMapMappingsSystem
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 04, 2003
 */
public class DirectMapMappingsProject extends Project {
    public DirectMapMappingsProject() {
        setName("DirectMapMappingsProject");
        applyLogin();
        addDescriptor(buildDescriptors());
    }

    public void applyLogin() {
        DatabaseLogin login = new DatabaseLogin();
        login.usePlatform(new org.eclipse.persistence.platform.database.OraclePlatform());
        login.setDriverClassName("oracle.jdbc.OracleDriver");
        login.setConnectionString("jdbc:oracle:thin:@localhost:1521:orcl");
        login.setUserName("scott");
        login.setEncryptedPassword("tiger");
        login.setUsesExternalConnectionPooling(false);
        login.setUsesExternalTransactionController(false);

        setLogin(login);
    }

    public RelationalDescriptor buildDescriptors() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.directmap.DirectMapMappings.class);
        descriptor.addTableName("DIRECTMAPMAPPINGS");
        descriptor.addPrimaryKeyFieldName("DIRECTMAPMAPPINGS.ID");

        // RelationalDescriptor properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("DIRECTMAPMAPPINGS.ID");
        descriptor.setSequenceNumberName("dmmappings_seq");
        descriptor.setAlias("DirectMapMappings");

        // Query manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("DIRECTMAPMAPPINGS.ID");
        descriptor.addMapping(idMapping);

        DirectMapMapping directMapMapping = new DirectMapMapping();
        directMapMapping.setAttributeName("directMap");
        directMapMapping.setReferenceTableName("DIRECTMAP1");
        directMapMapping.setDirectFieldName("DIRECTMAP1.VAL");
        //"KEY" is a reserved keyword for SyBase, it can't be used as a field name
        directMapMapping.setDirectKeyFieldName("DIRECTMAP1.KEY_FIELD");
        directMapMapping.addReferenceKeyFieldName("DIRECTMAP1.ID", "DIRECTMAPMAPPINGS.ID");
        directMapMapping.setReferenceClass(Hashtable.class);
        directMapMapping.setUsesIndirection(false);
        directMapMapping.useMapClass(Hashtable.class);
        directMapMapping.setKeyClass(Integer.class);
        directMapMapping.setValueClass(String.class);
        descriptor.addMapping(directMapMapping);

        // direct map batch reading mapping
        DirectMapMapping directMapBatchMapping = new DirectMapMapping();
        directMapBatchMapping.setAttributeName("directMapForBatchRead");
        directMapBatchMapping.setReferenceTableName("DIRECTMAP2");
        directMapBatchMapping.setDirectFieldName("DIRECTMAP2.VAL");
        directMapBatchMapping.setDirectKeyFieldName("DIRECTMAP2.KEY_FIELD");
        directMapBatchMapping.addReferenceKeyFieldName("DIRECTMAP2.ID", "DIRECTMAPMAPPINGS.ID");
        directMapBatchMapping.setReferenceClass(Hashtable.class);
        directMapBatchMapping.setUsesIndirection(false);
        directMapBatchMapping.useMapClass(Hashtable.class);
        directMapBatchMapping.setKeyClass(Integer.class);
        directMapBatchMapping.setValueClass(String.class);
        directMapBatchMapping.useBatchReading();
        descriptor.addMapping(directMapBatchMapping);

        DirectMapMapping indirectionDirectMapMapping = new DirectMapMapping();
        indirectionDirectMapMapping.setAttributeName("indirectionDirectMap");
        indirectionDirectMapMapping.setReferenceTableName("DIRECTMAP3");
        indirectionDirectMapMapping.setDirectFieldName("DIRECTMAP3.VAL");
        indirectionDirectMapMapping.setDirectKeyFieldName("DIRECTMAP3.KEY_FIELD");
        indirectionDirectMapMapping.addReferenceKeyFieldName("DIRECTMAP3.ID", "DIRECTMAPMAPPINGS.ID");
        indirectionDirectMapMapping.setReferenceClass(Hashtable.class);
        indirectionDirectMapMapping.useTransparentMap();
        indirectionDirectMapMapping.useMapClass(IndirectMapSubclass.class);
        indirectionDirectMapMapping.setKeyClass(Integer.class);
        indirectionDirectMapMapping.setValueClass(String.class);
        descriptor.addMapping(indirectionDirectMapMapping);

        DirectMapMapping directMapBlobMapping = new DirectMapMapping();
        directMapBlobMapping.setAttributeName("blobDirectMap");
        directMapBlobMapping.setReferenceTableName("DIRECTMAPBLOB");
        directMapBlobMapping.setDirectFieldName("DIRECTMAPBLOB.VAL");
        directMapBlobMapping.setDirectKeyFieldName("DIRECTMAPBLOB.KEY_FIELD");
        directMapBlobMapping.addReferenceKeyFieldName("DIRECTMAPBLOB.ID", "DIRECTMAPMAPPINGS.ID");
        directMapBlobMapping.setReferenceClass(Hashtable.class);
        directMapBlobMapping.setUsesIndirection(false);
        directMapBlobMapping.useMapClass(Hashtable.class);
        directMapBlobMapping.setKeyClass(Integer.class);
        directMapBlobMapping.setValueClass(Object.class);
        directMapBlobMapping.setValueConverter(new SerializedObjectConverter(directMapBlobMapping));
        descriptor.addMapping(directMapBlobMapping);

        DirectMapMapping directHashMapMapping = new DirectMapMapping();
        directHashMapMapping.setAttributeName("directHashMap");
        directHashMapMapping.setReferenceTableName("DIRECTMAP4");
        directHashMapMapping.setDirectFieldName("DIRECTMAP4.VAL");
        directHashMapMapping.setDirectKeyFieldName("DIRECTMAP4.KEY_FIELD");
        directHashMapMapping.addReferenceKeyFieldName("DIRECTMAP4.ID", "DIRECTMAPMAPPINGS.ID");
        directHashMapMapping.setReferenceClass(HashMap.class);
        directHashMapMapping.setUsesIndirection(false);
        directHashMapMapping.useMapClass(HashMap.class);
        directHashMapMapping.setKeyClass(Integer.class);
        directHashMapMapping.setValueClass(String.class);
        descriptor.addMapping(directHashMapMapping);

        return descriptor;
    }
}
