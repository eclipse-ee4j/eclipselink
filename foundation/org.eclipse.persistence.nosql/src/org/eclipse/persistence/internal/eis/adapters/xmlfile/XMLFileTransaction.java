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
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.cci.LocalTransaction;

import org.eclipse.persistence.eis.EISDOMRecord;

/**
 * Transaction to XML file JCA adapter.
 * Transaction are supported to a limited degree through writing the doms to the file system on commit.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileTransaction implements LocalTransaction {
    protected boolean isInTransaction;
    protected Map domFiles;
    protected XMLFileConnection connection;

    /**
     * Default constructor.
     */
    public XMLFileTransaction(XMLFileConnection connection) {
        this.connection = connection;
        this.isInTransaction = false;
    }

    /**
     * Record that a transaction has begun.
     */
    @Override
    public void begin() {
        this.isInTransaction = true;
        this.domFiles = new HashMap(10);
    }

    /**
     * Return if currently within a transaction.
     */
    public boolean isInTransaction() {
        return isInTransaction;
    }

    /**
     * Write each of the transactional DOM records back to their files.
     */
    @Override
    public void commit() throws ResourceException {
        try {
            // store any dom to their files
            for (Iterator doms = domFiles.entrySet().iterator(); doms.hasNext();) {
                Map.Entry entry = (Map.Entry)doms.next();
                String fileName = (String)entry.getKey();
                EISDOMRecord record = (EISDOMRecord)entry.getValue();

                try (Writer fileWriter = new FileWriter(fileName)) {
                    record.transformToWriter(fileWriter);
                    fileWriter.flush();
                }
            }
        } catch (Exception exception) {
            throw new ResourceException(exception.toString());
        }
        this.domFiles = new HashMap(10);
        this.isInTransaction = false;
    }

    /**
     * Throw away each of the DOM records in the transactional cache.
     */
    @Override
    public void rollback() {
        // throw away doms
        this.domFiles = new HashMap(10);
        this.isInTransaction = false;
    }

    /**
     * Return the transactional copy of the file's DOM record.
     * This will be written on commit.
     */
    public EISDOMRecord retrieveDOMRecord(File file) throws Exception {
        // Check for transactional copy.
        EISDOMRecord fileRecord = (EISDOMRecord)this.domFiles.get(file.getPath());
        if (fileRecord == null) {
            // If the file exists parse it, otherwise create a new record.
            if (file.exists()) {
                Reader fileReader = new FileReader(file);
                fileRecord = new EISDOMRecord();
                // Parse file.
                fileRecord.transformFromXML(fileReader);
                fileReader.close();
            } else {
                fileRecord = new EISDOMRecord();
                fileRecord.setDOM(fileRecord.createNewDocument("root"));
            }
            this.domFiles.put(file.getPath(), fileRecord);
        }
        return fileRecord;
    }
}
