/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.eis.adapters.xmlfile;

import java.io.StringWriter;

import javax.resource.cci.*;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.w3c.dom.*;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

/**
 * Platform for XML file emulated JCA adapter.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFilePlatform extends EISPlatform {

    /** XML file interaction spec properties. */
    public static String FILE_NAME = "fileName";
    public static String XPATH = "xPath";
    public static String XQUERY = "xQuery";
    public static String DOM = "dom";

    /**
     * Default constructor.
     */
    public XMLFilePlatform() {
        super();
        this.shouldConvertDataToStrings = true;
        this.isMappedRecordSupported = false;
        this.isIndexedRecordSupported = false;
        this.isDOMRecordSupported = true;
        this.supportsLocalTransactions = true;
    }

    /**
     * Allow the platform to build the interaction spec based on properties defined in the interaction.
     */
    @Override
    public InteractionSpec buildInteractionSpec(EISInteraction interaction) {
        InteractionSpec spec = interaction.getInteractionSpec();
        if (spec == null) {
            NamespaceResolver namespaceResolver = null;
            try {
                namespaceResolver = ((EISDescriptor)interaction.getQuery().getDescriptor()).getNamespaceResolver();
            } catch (Exception e) {
                //do nothing, the namespaceResolver will just be null
            }
            XMLFileInteractionSpec fileSpec = new XMLFileInteractionSpec(namespaceResolver);
            fileSpec.setFileName((String)interaction.getProperty(FILE_NAME));
            fileSpec.setXPath((String)interaction.getProperty(XPATH));
            fileSpec.setXQuery((String)interaction.getProperty(XQUERY));
            fileSpec.setDOM((Element)interaction.getProperty(DOM));
            if (interaction.getQuery().isDeleteObjectQuery()) {
                fileSpec.setInteractionType(XMLFileInteractionSpec.DELETE);
            } else if (interaction.getQuery().isInsertObjectQuery()) {
                fileSpec.setInteractionType(XMLFileInteractionSpec.INSERT);
            } else if (interaction.getQuery().isModifyQuery()) {
                fileSpec.setInteractionType(XMLFileInteractionSpec.UPDATE);
            } else if (interaction.getQuery().isReadQuery()) {
                fileSpec.setInteractionType(XMLFileInteractionSpec.READ);
            }
            if (interaction instanceof XQueryInteraction) {
                fileSpec.setXPath(((XQueryInteraction)interaction).getXQueryString());
            }
            spec = fileSpec;
        }
        return spec;
    }

    /**
     * INTERNAL:
     * Create platform-default Sequence
     */
    @Override
    protected Sequence createPlatformDefaultSequence() {
        return new XMLFileSequence();
    }
    
    /**
     * INTERNAL:
     * Allow the platform to initialize the CRUD queries to defaults.
     * Configure the CRUD operations using GET/PUT and DELETE.
     */
    @Override
    public void initializeDefaultQueries(DescriptorQueryManager queryManager, AbstractSession session) {
        String dataType = ((EISDescriptor)queryManager.getDescriptor()).getDataTypeName();
        StringWriter writer = new StringWriter();
        writer.write(dataType);
        writer.write('[');
        for (DatabaseField field : queryManager.getDescriptor().getPrimaryKeyFields()) {
            writer.write(field.getName());
            writer.write("='#");
            writer.write(field.getName());
            writer.write("'");
        }
        writer.write(']');
        String queryString = writer.toString();
        // Insert
        if (!queryManager.hasInsertQuery()) {
            XQueryInteraction call = new XQueryInteraction();
            call.setXQueryString(dataType);
            call.setFunctionName("insert");
            call.setProperty("fileName", dataType + ".xml");
            queryManager.setInsertCall(call);
        }
        
        // Update
        if (!queryManager.hasUpdateQuery()) {
            XQueryInteraction call = new XQueryInteraction();
            call.setXQueryString(queryString);
            call.setFunctionName("update");
            call.setProperty("fileName", dataType + ".xml");
            queryManager.setUpdateCall(call);
        }

        // Read
        if (!queryManager.hasReadObjectQuery()) {
            XQueryInteraction call = new XQueryInteraction();
            call.setXQueryString(queryString);
            call.setFunctionName("read");
            call.setProperty("fileName", dataType + ".xml");
            call.setOutputResultPath("result");
            queryManager.setReadObjectCall(call);
        }
        if (!queryManager.hasReadAllQuery()) {
            XQueryInteraction call = new XQueryInteraction();
            call.setXQueryString(dataType);
            call.setFunctionName("read");
            call.setProperty("fileName", dataType + ".xml");
            call.setOutputResultPath("result");
            queryManager.setReadAllCall(call);
        }
        
        // Delete
        if (!queryManager.hasDeleteQuery()) {
            XQueryInteraction call = new XQueryInteraction();
            call.setXQueryString(queryString);
            call.setFunctionName("delete");
            call.setProperty("fileName", dataType + ".xml");
            queryManager.setDeleteCall(call);
        }
    }
}
