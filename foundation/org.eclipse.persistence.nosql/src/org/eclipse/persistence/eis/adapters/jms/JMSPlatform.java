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
package org.eclipse.persistence.eis.adapters.jms;


// JDK imports
import javax.jms.*;
import javax.resource.*;
import javax.resource.cci.*;

// TopLink imports
import org.eclipse.persistence.eis.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.internal.eis.adapters.jms.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;

// Misc. imports
import org.w3c.dom.*;

/**
 * Platform for Oracle JMS JCA adapter.
 *
 * @author Dave McCann
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class JMSPlatform extends EISPlatform {
    // interaction spec properties
    public static String MODE = "mode";// indicates send or receive interaction
    public static String SEND = "send";// used with MODE - send interaction
    public static String RECEIVE = "receive";// used with MODE - receive interaction
    public static String SEND_RECEIVE = "sendAndReceive";// used with MODE - send/receive in one interaction
    public static String SELECTOR = "selector";// message selector - JMSCorrelationID
    public static String TIMEOUT = "timeout";// timeout for receive interaction
    public static String DESTINATION = "destination";// queue 'name' param of the JNDI entry in jms.xml
    public static String DESTINATION_URL = "destinationURL";// JNDI lookup name of queue
    public static String REPLY_TO_DESTINATION = "replyToDestination";// replyTo queue 'name' param of the JNDI entry 
    public static String REPLY_TO_DESTINATION_URL = "replyToDestinationURL";// JNDI lookup name of queue where response is to be placed (by entity processing the request)

    /**
     * Default constructor.
     */
    public JMSPlatform() {
        super();
        setShouldConvertDataToStrings(true);
        setIsMappedRecordSupported(false);
        setIsIndexedRecordSupported(true);
        setIsDOMRecordSupported(true);
        setSupportsLocalTransactions(true);
        setRequiresAutoCommit(true);
    }

    /**
     * Allow the platform to build the interaction spec based on properties defined in the interaction.
     */
    public InteractionSpec buildInteractionSpec(EISInteraction interaction) {
        String property;

        InteractionSpec spec = interaction.getInteractionSpec();
        if (spec == null) {
            CciJMSInteractionSpec jmsSpec;
            if (interaction.getProperty(MODE) == null) {
                throw EISException.propertyNotSet(MODE);
            }

            // determine interaction type:  send, receive, or send/receive
            if (interaction.getProperty(MODE).equals(SEND)) {
                jmsSpec = new CciJMSSendInteractionSpec();
            } else if (interaction.getProperty(MODE).equals(RECEIVE)) {
                jmsSpec = new CciJMSReceiveInteractionSpec();
            } else if (interaction.getProperty(MODE).equals(SEND_RECEIVE)) {
                jmsSpec = new CciJMSSendReceiveInteractionSpec();
            } else {
                throw EISException.invalidProperty(MODE);
            }

            // set the message selector - if one exists
            property = (String)interaction.getProperty(SELECTOR);
            if (property != null) {
                jmsSpec.setMessageSelector(property);
            }

            // set properties on the interaction as defined by the user
            if (jmsSpec instanceof CciJMSSendInteractionSpec) {
                // send interaction
                // set the send destination URL - if one exists
                property = (String)interaction.getProperty(DESTINATION_URL);
                if (property != null) {
                    jmsSpec.setDestinationURL(property);
                } else {
                    // if no JNDI lookup, set the send destination  - if one exists
                    property = (String)interaction.getProperty(DESTINATION);
                    if (property != null) {
                        jmsSpec.setDestination(property);
                    } else {
                        // need to have either the destination or JNDI lookup defined
                        throw EISException.propertiesNotSet(DESTINATION_URL, DESTINATION);
                    }
                }

                // set the reply to destination URL - if one exists
                property = (String)interaction.getProperty(REPLY_TO_DESTINATION_URL);
                if (property != null) {
                    ((CciJMSSendInteractionSpec)jmsSpec).setReplyToDestinationURL(property);
                } else {
                    // if no JNDI lookup, set the send destination  - if one exists
                    property = (String)interaction.getProperty(REPLY_TO_DESTINATION);
                    if (property != null) {
                        ((CciJMSSendInteractionSpec)jmsSpec).setReplyToDestination(property);
                    } else {
                        // need to have either the destination or JNDI lookup defined
                        throw EISException.propertiesNotSet(REPLY_TO_DESTINATION_URL, REPLY_TO_DESTINATION);
                    }
                }
            } else if (jmsSpec instanceof CciJMSReceiveInteractionSpec) {
                // receive interaction
                // set the receive destination URL - if one exists
                property = (String)interaction.getProperty(DESTINATION_URL);
                if (property != null) {
                    jmsSpec.setDestinationURL(property);
                } else {
                    // if no JNDI lookup, set the receive destination - if one exists
                    property = (String)interaction.getProperty(DESTINATION);
                    if (property != null) {
                        jmsSpec.setDestination(property);
                    } else {
                        // need to have either the destination or JNDI lookup defined
                        throw EISException.propertiesNotSet(DESTINATION_URL, DESTINATION);
                    }
                }

                // set the timeout value - if one exists
                property = (String)interaction.getProperty(TIMEOUT);
                if (property != null) {
                    ((CciJMSReceiveInteractionSpec)jmsSpec).setTimeout(property);
                }
            } else {
                // send/receive interaction
                // set the send destination URL - if one exists
                property = (String)interaction.getProperty(DESTINATION_URL);
                if (property != null) {
                    ((CciJMSSendReceiveInteractionSpec)jmsSpec).setDestinationURL(property);
                } else {
                    // if no JNDI lookup, set the send destination  - if one exists
                    property = (String)interaction.getProperty(DESTINATION);
                    if (property != null) {
                        ((CciJMSSendReceiveInteractionSpec)jmsSpec).setDestination(property);
                    } else {
                        // need to have either the destination or JNDI lookup defined
                        throw EISException.propertiesNotSet(DESTINATION_URL, DESTINATION);
                    }
                }

                // set the replyTo destination URL - if one exists
                property = (String)interaction.getProperty(REPLY_TO_DESTINATION_URL);
                if (property != null) {
                    ((CciJMSSendReceiveInteractionSpec)jmsSpec).setReplyToDestinationURL(property);
                } else {
                    // if no JNDI lookup, set the receive destination  - if one exists
                    property = (String)interaction.getProperty(REPLY_TO_DESTINATION);
                    if (property != null) {
                        ((CciJMSSendReceiveInteractionSpec)jmsSpec).setReplyToDestination(property);
                    } else {
                        // need to have either the destination or JNDI lookup defined
                        // 17009
                        throw EISException.propertiesNotSet(REPLY_TO_DESTINATION_URL, REPLY_TO_DESTINATION);
                    }
                }

                // set the timeout value - if one exists
                property = (String)interaction.getProperty(TIMEOUT);
                if (property != null) {
                    ((CciJMSSendReceiveInteractionSpec)jmsSpec).setTimeout(property);
                }
            }
            spec = jmsSpec;
        }
        return spec;
    }

    /**
     * Allow the platform to handle the creation of the DOM record.
     * Creates an indexed record (mapped records are not supported).
     */
    public Record createDOMRecord(String recordName, EISAccessor accessor) {
        try {
            return accessor.getRecordFactory().createIndexedRecord(recordName);
        } catch (ResourceException exception) {
            throw EISException.createException(exception);
        }
    }

    /**
     * Stores the XML DOM value into the record.
     * Convert the DOM to text and add to the indexed record.
     */
    public void setDOMInRecord(Element dom, Record record, EISInteraction call, EISAccessor accessor) {
        IndexedRecord indexedRecord = (IndexedRecord)record;
        indexedRecord.add(new org.eclipse.persistence.oxm.record.DOMRecord(dom).transformToXML());
    }

    /**
     * Allow the platform to handle the creation of the Record for the DOM record.
     * Translate the indexed record text into a DOM record.
     */
    public AbstractRecord createDatabaseRowFromDOMRecord(Record record, EISInteraction call, EISAccessor accessor) {
        IndexedRecord indexedRecord = (IndexedRecord)record;
        if (indexedRecord.size() == 0) {
            return null;
        }

        EISDOMRecord domRecord = new EISDOMRecord();

        // the record is assumed to contain either:
        //   - a TextMessage object if a converter was not defined
        //   - a string of XML if a converter was defined
        Object recordData = indexedRecord.get(0);
        if (recordData instanceof TextMessage) {
            try {
                domRecord.transformFromXML(((TextMessage)recordData).getText());
            } catch (Exception ex) {
                return null;
            }
        } else if (recordData instanceof String) {
            domRecord.transformFromXML((String)recordData);
        } else {
            throw EISException.unsupportedMessageInOutputRecord();
        }
        return domRecord;
    }
}
