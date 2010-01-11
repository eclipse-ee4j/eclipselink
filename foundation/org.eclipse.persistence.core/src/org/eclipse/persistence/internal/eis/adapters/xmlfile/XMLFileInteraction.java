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
package org.eclipse.persistence.internal.eis.adapters.xmlfile;

import java.io.*;
import java.util.*;
import javax.resource.*;
import javax.resource.cci.*;
import org.eclipse.persistence.eis.EISDOMRecord;
import org.eclipse.persistence.eis.EISException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.oxm.XMLField;

/**
 * Interaction to XML file JCA adapter.
 * Executes the interaction spec against the file and DOM.
 *
 * @author James
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class XMLFileInteraction implements Interaction {

    /** Store the connection the interaction was created from. */
    protected XMLFileConnection connection;

    /**
     * Default constructor.
     */
    public XMLFileInteraction(XMLFileConnection connection) {
        this.connection = connection;
    }

    public void clearWarnings() {
    }

    public void close() {
    }

    /**
     * Execute the interaction spec.
     * The spec define the type of CRUD operation and the file/DOM to perform it on.
     * The spec may also contain and XQuery/XPath to evaluate against the DOM.
     * The input is used for insert/update to contain the data (DOM) to insert into the file.
     */
    public Record execute(InteractionSpec spec, Record input) throws ResourceException {
        // Use auto-commit if not in a transaction
        boolean autocommit = false;
        if (!connection.getXMLFileTransaction().isInTransaction()) {
            autocommit = true;
            connection.getXMLFileTransaction().begin();
        }
        try {
            XMLFileInteractionSpec xmlSpec = (XMLFileInteractionSpec)spec;
            File file = new File(this.connection.getConnectionSpec().getDirectory() + "/" + xmlSpec.getFileName());
            Record recordToReturn = null;
            // Check CRUD type and process acordingly
            if (xmlSpec.getInteractionType() == XMLFileInteractionSpec.READ) {
                autocommit = false;
                recordToReturn = executeRead(xmlSpec, file);
            } else if (xmlSpec.getInteractionType() == XMLFileInteractionSpec.INSERT) {
            	recordToReturn = executeInsert(xmlSpec, file, (EISDOMRecord)input);
            } else if (xmlSpec.getInteractionType() == XMLFileInteractionSpec.UPDATE) {
            	recordToReturn = executeUpdate(xmlSpec, file, (EISDOMRecord)input);
            } else if (xmlSpec.getInteractionType() == XMLFileInteractionSpec.DELETE) {
            	recordToReturn = executeDelete(xmlSpec, file);
            }
            if (recordToReturn != null) {
            	((EISDOMRecord) recordToReturn).setSession(((EISDOMRecord)input).getSession());
            }
            return recordToReturn;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new ResourceException(exception.toString());
        } finally {
            if (autocommit) {
                connection.getXMLFileTransaction().commit();
            }
        }
    }

    /**
     * Execute the read operation.
     */
    public Record executeRead(XMLFileInteractionSpec spec, File file) throws Exception {
        // Parse file and return document, or xpath/xquery result from dom.	
        if (!file.exists()) {
            return null;
        }
        EISDOMRecord fileRecord = connection.getXMLFileTransaction().retrieveDOMRecord(file);

        // Check for and process XPath or XQuery		
        if (spec.getXPath() != null) {
            // Either a value/string or list of element records is returned
            Object result = fileRecord.getValues(buildField(spec));
            EISDOMRecord output = new EISDOMRecord();
            if (result instanceof List) {
                List results = (List)result;
                output.setDOM(output.createNewDocument("results"));
                if (results != null) {
                    for (int index = 0; index < results.size(); index++) {
                        output.add(new DatabaseField("result"), results.get(index));
                    }
                }
            } else {
                output.setDOM(output.createNewDocument("results"));
                output.add(new DatabaseField("result"), result);
            }
            return output;
        } else {
            return fileRecord;
        }
    }

    /**
     * Execute the insert operation.
     */
    public Record executeInsert(XMLFileInteractionSpec spec, File file, EISDOMRecord input) throws Exception {
        // Write input record dom to file, or insert dom node.	
        EISDOMRecord outputToFile = input;

        // If xpath, parse, insert node, then write back	
        if (spec.getXPath() != null) {
            // If the file exists get tx dom.
            outputToFile = connection.getXMLFileTransaction().retrieveDOMRecord(file);
            outputToFile.add(buildField(spec), input);
        }
        return null;
    }

    /**
     * Execute the update operation.
     */
    public Record executeUpdate(XMLFileInteractionSpec spec, File file, EISDOMRecord input) throws Exception {
        // If the file does not exist must first create the file.
        if (!file.exists()) {
            return null;
        }

        // Write input record dom to file, or insert dom node.
        EISDOMRecord outputToFile = input;

        // If xpath, get tx dom, find and update node, (tx commit will write back)			
        if (spec.getXPath() != null) {
            outputToFile = connection.getXMLFileTransaction().retrieveDOMRecord(file);
            outputToFile.put(buildField(spec), input);
        }
        return null;
    }

    /**
     * Execute the delete operation.
     */
    public Record executeDelete(XMLFileInteractionSpec spec, File file) throws Exception {
        // Delete file, or remove dom node.
        if (!file.exists()) {
            return null;
        }

        // If xpath, get tx dom, delete node, (tx commit will write back)	
        if (spec.getXPath() != null) {
            EISDOMRecord outputToFile = connection.getXMLFileTransaction().retrieveDOMRecord(file);
            outputToFile.remove(buildField(spec));
        } else {
            boolean success = file.delete();

            if (!success) {
                throw EISException.couldNotDeleteFile(new Object[] { file });
            }
        }
        return null;
    }

    /**
     * Execute the interaction and set the output into the output record.
     * Return true or false if the execute returned data (similar to row-count).
     */
    public boolean execute(InteractionSpec spec, Record input, Record output) throws ResourceException {
        EISDOMRecord result = (EISDOMRecord)execute(spec, input);
        if (result == null) {
            return false;
        }
        ((EISDOMRecord)output).setDOM(result.getDOM());
        return true;
    }

    public Connection getConnection() {
        return connection;
    }

    public ResourceWarning getWarnings() {
        return null;
    }

    /**
     * INTERNAL:
     * All values are printed as ? to allow for parameter binding or translation during the execute of the call.
     */
    protected DatabaseField buildField(XMLFileInteractionSpec spec) {
        XMLField xmlField = new XMLField(spec.getXPath());
        xmlField.setNamespaceResolver(spec.getNamespaceResolver());
        return xmlField;
    }
}
