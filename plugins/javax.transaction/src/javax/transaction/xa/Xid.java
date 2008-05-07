/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

package javax.transaction.xa;

/**
 * The Xid interface is a Java mapping of the X/Open transaction identifier
 * XID structure. This interface specifies three accessor methods to 
 * retrieve a global transaction's format ID, global transaction ID, 
 * and branch qualifier. The Xid interface is used by the transaction 
 * manager and the resource managers. This interface is not visible to
 * the application programs.
 */
public interface Xid {
    
    /**
     * Maximum number of bytes returned by getGtrid.
     */
    final static int MAXGTRIDSIZE = 64;

    /**
     * Maximum number of bytes returned by getBqual.
     */
    final static int MAXBQUALSIZE = 64;

    /**
     * Obtain the format identifier part of the XID.
     *
     * @return Format identifier. O means the OSI CCR format.
     */
    int getFormatId();

    /**
     * Obtain the global transaction identifier part of XID as an array 
     * of bytes.
     *
     * @return Global transaction identifier.
     */
    byte[] getGlobalTransactionId();

    /**
     * Obtain the transaction branch identifier part of XID as an array 
     * of bytes.
     *
     * @return Global transaction identifier.
     */
    byte[] getBranchQualifier();
}
