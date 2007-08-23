/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.exceptions.i18n;

import java.util.ListResourceBundle;

/**
 * INTERNAL:
 * English EJBExceptionResourceBundle for TLWL EJBExceptionFactory messages.
 *
 * Creation date: (2/23/00 9:47:38 AM)
 * @author: TopLink Maintenance Team
 */
public class EJBExceptionResource extends ListResourceBundle {
    static final Object[][] contents = {
                                           { "10001", "Exception [EJB - {0}]: Exception creating bean of type [{1}]." },
                                           { "10002", "Exception [EJB - {0}]: Error removing primary key [{1}] for bean of type [{2}]." },
                                           { "10003", "Exception [EJB - {0}]: An EJB Exception has occurred in bean of type [{1}]." },
                                           { "10004", "Exception [EJB - {0}]: Cannot find bean of type [{1}] using finder: [{2}]." },
                                           { "10007", "Exception [EJB - {0}]: Exception creating bean of type [{1}]. Bean already exists." },
                                           { "10008", "Exception [EJB - {0}]: Cannot find bean of type [{1}] using finder [{2}]." },
                                           { "10009", "Exception [EJB - {0}]: Cannot find bean of type [{2}] using findByPrimaryKey with primary key [{1}]." },
                                           { "10010", "Exception [EJB - {0}]: Exception creating bean of type [{1}]. Create cannot be invoked on this bean since it has been deployed as read only." },
                                           { "10011", "Exception [EJB - {0}]: Exception removing bean of type [{1}]. Remove cannot be invoked on this bean since it has been deployed as read only." },
                                           { "10014", "Exception [EJB - {0}]: Error in non-transactional write for bean of type [{1}]." },
                                           { "10016", "Exception [EJB - {0}]: An error occurred while getting transaction from TransactionManager." },
                                           { "10021", "Exception [EJB - {0}]: An error occurred while attempting to assign sequence numbers for bean of type [{1}]." },
                                           { "10022", "Exception [EJB - {0}]: RemoteException on method call [{1}] for bean of type {2}." },
                                           { "10023", "Exception [EJB - {0}]: Sequence Exception for bean of type {1}." },
                                           { "10025", "Exception [EJB - {0}]: An internal error has occurred while accessing the EJBContext [{1}]." },
                                           { "10026", "Exception [EJB - {0}]: An internal error has occurred locating the generated subclass [{1}]." },
                                           { "10027", "Exception [EJB - {0}]: An internal error has occurred in a server process. EJBContext [{1}] not properly initialized." },
                                           { "10028", "Exception [EJB - {0}]: The specified attribute [{1}] cannot be merged since it does not exist for beans of type [{2}]." },
                                           { "10029", "Exception [EJB - {0}]: An internal error occurred accessing the bean or primary key class." },
                                           { "10030", "Exception [EJB - {0}]: An internal error occurred accessing the bean of type [{1}] or primary key class." },
                                           { "10031", "Exception [EJB - {0}]: Unable to prepare bean of type [{1}] with pk [{2}] for invocation." },
                                           { "10032", "Exception [EJB - {0}]: Finder not yet implemented: {2}{1}." },
                                           { "10033", "Exception [EJB - {0}]: findByPrimaryKey invoked with null primary key for bean of type [{1}]." },
                                           { "10034", "Exception [EJB - {0}]: Remove on bean of type [{1}] invoked with null primary key." },
                                           { "10036", "Exception [EJB - {0}]: An error occurred during code generation." },
                                           { "10037", "Exception [EJB - {0}]: Error while executing ejbSelectMethod: [{1}] on bean of type [{2}]." },
                                           { "10038", "Exception [EJB - {0}]: Error while executing ejbHomeMethod: [{1}]." },
                                           { "10040", "Exception [EJB - {0}]: No transaction was active on invocation of {1} method." },
                                           { "10041", "Exception [EJB - {0}]: The parameter passed to the {1} method has already been deleted." },
                                           { "10043", "Exception [EJB - {0}]: Unable to wrap the results of the finder because " + "they have already been wrapped.  If using a custom query (ex: a redirect query), " + "then be sure that you called 'setShouldUseWrapperPolicy(false)'." },
                                           
    { "10045", "Exception [EJB - {0}]: Unable to wrap the results of the finder as " + "EJBLocalObjects because there is no Local interface.  If executing an ejbSelect " + "query, make sure the <result-type-mapping> tag is correctly." },
                                           
    { "10046", "Exception [EJB - {0}]: Unable to wrap the results of the finder as " + "EJBObjects because there is no Remote interface.  If executing an ejbSelect " + "query, make sure the <result-type-mapping> tag is correctly." },
                                           
    { "10047", "Exception [EJB - {0}]: Trying to create a bean with a null primary key.  Check your " + "bean's ejbCreate method to ensure the primary key fields are set. If TopLink " + "sequencing is being used, make sure that sequencing is setup correctly on all the " + "beans in the TopLink project." },
                                           
    { "10049", "Exception [EJB - {0}]: The argument [{1}] of type [{2}] is not of the correct type for the" + " relationship.  See section 10.3.6 of the EJB 2.1 specification. The argument type expected was [{3}]." },
                                           { "10051", "Exception [EJB - {0}]: An attempt was made to access a Collection/Iterator outside a transaction " + "or in one other than the one in which it was acquired.  See section 10.3.8 of " + "the EJB 2.1 specification." },
                                           { "10052", "Exception [EJB - {0}]: Cannot find bean of type [{2}] with primary key [{1}] for remote method invocation." },
                                           { "10053", "Exception [EJB - {0}]: Cannot find bean of type [{2}] with primary key [{1}] for local method invocation." },
                                           { "10054", "Exception [EJB - {0}]: An internal error has occurred while accessing the bean of type [{1}]." },
                                           { "10055", "Exception [EJB - {0}]: An error has occurred while cascading the delete from the bean of type [{1}] to the [{2}] relationship." },
                                           { "10056", "Exception [EJB - {0}]: An error has occurred while performing relationship maintenance for the bean of type [{1}] with the [{2}] relationship." },
                                           { "10057", "Exception [EJB - {0}]: Multiple results were found while executing the single object finder [{2}] for the bean of type [{1}]." },
                                           { "10058", "Exception [EJB - {0}]: Unable to invoke method [{1}] on bean of type [{2}] because this bean is read only." },
                                           { "10059", "Exception [EJB - {0}]: Unable to invoke method [{1}] on bean of type [{2}] because the target bean " + "of type [{3}] is read only. Either the target bean's object model(in the case of bi-directional relationship) or database table would not be modified by this operation. " },
                                           { "10060", "Exception [EJB - {0}]: Unable to invoke method [{1}] on collection [{2}] in bean of type [{3}] because the source bean is read only." },
                                           { "10061", "Exception [EJB - {0}]: Unable to invoke method [{1}] on collection [{2}] in bean of type [{3}] because the target bean of type [{4}] is read only." + "Either the target bean's object model(in the case of bi-directional relationship) or database table would not be modified by this operation. " },
                                           { "10062", "Exception [EJB - {0}]: Unable to retrieve underlying data for object type : {2} primary keys: {1}.  Transaction must be restarted and objects refreshed." },
                                           { "10063", "Exception [EJB - {0}]: Unknown collection type : {1} passed to container policy." },
                                           { "10064", "Exception [EJB - {0}]: Any changes to fields must be performed within a transaction." },
                                           { "10065", "Exception [EJB - {0}]: An error occurred while adding object {1} to collection {2}." },
                                           { "10066", "Exception [EJB - {0}]: An error occurred while removing object {1} from collection {2}." },
                                           { "10067", "Exception [EJB - {0}]: An error occurred while performing relationship maitenance for the field {1}." },
                                           { "10068", "Exception [EJB - {0}]: An error occurred with the DataSource {1}.  TopLink CMP requires use of a <managed-data-source>.  A <native-data-source> cannot be used." },
                                           { "10069", "Exception [EJB - {0}]: Failed lookup of object under JNDI name: {1}." },
                                           { "10070", "Exception [EJB - {0}]: An internal error occurred accessing the bean of type [{1}] or primary key class of type [{2}]." },
};

    /**
     * Return the lookup table.
     */
    protected Object[][] getContents() {
        return contents;
    }
}
