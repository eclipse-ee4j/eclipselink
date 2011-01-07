/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.impl.ExternalizableDelegator;
import commonj.sdo.impl.HelperProvider;

/**
 * INTERNAL:
 * <p><b>Purpose:</b></p>
 * This class performs serialization/deserialization of an SDODataObject.
 * <p><b>Responsibilities:</b></p>
 * <ul>
 * <li>Provide/override default Java serializable access to a DataObject</li>
 * </ul>
 * Serialization Process
 * 
 * <p/>Serialization and de-serialization of objects occurs during DAS transactions, 
 *   Web Service transactions in the SOAP envelope, EJB container passivation, 
 *   web container session saving or directly in an application using the function 
 *   ObjectOutputStream.writeObject(Object).  
 *   The Serializable and Externalizable framework handles automatic or user defined 
 *   reading/writing of streams depending on which interface functions are realized in the implementing classes.
 *   
 * <p/>The Serializable interface has no operations - therefore a class that implements 
 * it needs to add no additional functionality.  
 *     Why do this? - For security.  The security manager in the JVM will only serialize objects at 
 * runtime if they are flagged as Serializable (or Externalizable) so that by default 
 * java classes do not expose themselves to serialization.  (See p49 of Java Security 2nd edition).
 * 
 * <p/>There are 3 levels of serialization control.
 * <ul><li><i>1) Default Serialization</i><br/></li>
 *     Here we make the class implement Serializable, mark non-serializable fields as 
 * transient and implement no new functions.
 * <li><i>2) Partial custom Serialization</i><br/></li>
 *     Here we make the class implement Serializable and implement the optional functions 
 * writeObject and readObject to handle custom serialization of the current class while 
 * using the default serialization for super and subtypes.
 * 
 * <li><b>3) Fully customized Serialization - current implementation</b>.</li><br/>
 *     Here we make the class implement Externalizable and implement the functions 
 * readResolve, writeReplace, readExternal, writeExternal.
 * Supertypes and subtypes must also implement these functions.
 * </ul>
 * <p/>The SDO 2.01 specification details the high level structure of the 
 * serialization format on page 64, section 6 - Java Serialization of DataObjects.  
 *     The process will involve gzip serialization of the xml data with UTF representation of the 
 * Xpath address of the current DataObject inside the entire tree along with its identification as root/no-root in 
 * binary 1/0 format as follows.
 *
 * <p/><ul><li><b>Security:</b></li><br/>
 *     The following public functions expose a data replacement vulnerability where an 
 *     outside client can gain access and modify their constants.  
 *     We may need to wrap the GZIP streams in some sort of encryption when we are not 
 *     using HTTPS or SSL/TLS on the wire.
 *     
 * public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
 * public void writeExternal(ObjectOutput out) throws IOException
 * <li><b>Concurrency:</b></li><br/>
 *     Avoid synchronized classes that will queue threaded clients such as Enumeration, Vector etc.
 * We need to discuss how this API will be used by containers like an EJB container that can 
 * invoke multithreaded clients.
 * 
 * 	<li><b>Scalability:</b></li><br/>
 * 	<li>XML Serialization Size is 4GB:</li><br/>
 *     There is a limitation set by the SDO Specification on the size of the DataObject serialization.  
 *     According to the spec we must use an integer to define the size of the GZIP buffer that is serialized.  
 *     This size is limited to +/- 2GB.  This limitation is actually set by the JVM itself because a 
 *     call to buffer.length returns a signed 32 bit integer.
 * <p/>
 * <li><b>Performance:</b></li><br/>
 *    Using custom serialization via the Externalizable interface is 30% faster than the 
 *    default java serialization because the JVM does not need to discover the class definition.
 * </ul>
 * @since Oracle TopLink 11.1.1.0.0
 */
public class SDOResolvable implements ExternalizableDelegator.Resolvable {

    /** Unique hash ID of this Externalizable class. Use [serialver org.eclipse.persistence.sdo.SDOResolvable] */
    private static final long serialVersionUID = 2807334877368539299L;

    /** Root element name for all DataObjects undergoing serialization = sdo:dataObject */
    public static final String DEFAULT_ROOT_ELEMENT_NAME = "dataObject";

    /** root object with helper context id identifier */
    public static final int SDO_HELPER_CONTEXT_ID_IDENTIFIER = 2;
    
    /** root object serialization type identifier = 1 */
    public static final int SDO_ROOT_OBJECT_IDENTIFIER = 1;

    /** internal object serialization type identifier = 0 */
    public static final int SDO_INTERNAL_OBJECT_IDENTIFIER = 0;

	/** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    /** member field holding DataObject being serialized/deserialized */
    private transient SDODataObject theSDODataObject;

	/** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    /** hold the context containing all helpers so that we can preserve inter-helper relationships */
    private transient HelperContext aHelperContext;

    public SDOResolvable() {
        aHelperContext = HelperProvider.getDefaultContext();
    }

    /**
     * Default constructor for deserialization 
     */
    public SDOResolvable(HelperContext aContext) {
        aHelperContext = aContext;
    }

    /**
     * Constructor for serialization 
     */
    public SDOResolvable(Object target, HelperContext aContext) {
        // set the serialized/deserialized object holder
        theSDODataObject = (SDODataObject)target;
        aHelperContext = aContext;
    }

    /**
     * Purpose: This function is called after readExternal to return the
     * recently deserialized object retrieved from the ObjectInputStream.
     * Here there is an opportunity to replace the object with a Singleton version
     */
    public Object readResolve() throws ObjectStreamException {
        // return object previously constructed in readExternal()
        return theSDODataObject;
    }

    /**
     * Purpose: Serialize an SDODataObject to an ObjectOutputStream This
     * function is mandated by the Externalizable interface. It writes binary
     * data in the same order as was will be read back in readExternal().
     *
     * Prerequisites: An object has already been constructed and associated with
     * the theSDODataObject member
     */
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        GZIPOutputStream aGZIPOutputStream = null;
        ByteArrayOutputStream aByteOutputStream = null;

        // check whether we are a root DataObject, write gzip of the root
        if ((theSDODataObject.getContainer() == null)) {
            try {
                // is a root object
                String identifier = null;
                if(this.aHelperContext.getClass() == SDOHelperContext.class) {
                    identifier = ((SDOHelperContext)this.aHelperContext).getIdentifier();
                }
                if(identifier != null && !(identifier.equals(""))) {
                    objectOutput.writeByte(SDO_HELPER_CONTEXT_ID_IDENTIFIER);
                    objectOutput.writeUTF(identifier);
                } else {
                    objectOutput.writeByte(SDO_ROOT_OBJECT_IDENTIFIER);
                }
                
                
                // write root xml
                aByteOutputStream = new ByteArrayOutputStream();

                // chain a GZIP stream to the byte stream
                aGZIPOutputStream = new GZIPOutputStream(aByteOutputStream);

                // write XML Serialization of the root DataObject to the GZIP output stream
                XMLDocument aDocument = aHelperContext.getXMLHelper().createDocument(//
                		theSDODataObject, //
                		SDOConstants.SDO_URL, // root element URI
                		SDOConstants.SDO_PREFIX + SDOConstants.SDO_XPATH_NS_SEPARATOR_FRAGMENT + DEFAULT_ROOT_ELEMENT_NAME);
                ((SDOXMLHelper) aHelperContext.getXMLHelper()).serialize(aDocument, aGZIPOutputStream, null);
                // finished the stream to move compressed data from the Deflater
                aGZIPOutputStream.finish();
                // flush the streams
                aGZIPOutputStream.flush();
                aByteOutputStream.flush();

                // get bytes from ByteOutputStream
                byte[] buf = aByteOutputStream.toByteArray();

                // write gzip buffer length
                objectOutput.writeInt(buf.length);// compressed xml file length
                // write gzip buffer to ostream
                objectOutput.write(buf);            
            } finally {
                // close streams on all Exceptions
                if (aGZIPOutputStream != null) {// Clover: false case testing requires IO/comm failure
                    aGZIPOutputStream.close();
                }
                if (aByteOutputStream != null) {// Clover: false case testing requires IO/comm failure
                    aByteOutputStream.close();
                }
            }
        } else {
            // Internal non-root object, write the path to the from this object to the root
            // call this function recursively again for the root object serialization
            objectOutput.writeByte(SDO_INTERNAL_OBJECT_IDENTIFIER);

            // write path to the root
            String aPath = theSDODataObject._getPath();
            objectOutput.writeUTF(aPath);

            // write root (via indirect recursion to SDOResolvable)
            ((ObjectOutputStream)objectOutput).writeObject(theSDODataObject.getRootObject());
        }
    }

    /**
     * Purpose: Deserialize from an ObjectInputStream into an SDODataObject This
     * function is mandated by the Externalizable interface. It reads back
     * binary data in the same order as was written in writeExternal(). An
     * object has already been constructed with the no-arg constructor before
     * this function fills in the member fields.
     *
     * The deserialized object will be returned later in a call from the
     * ObjectInputStream to readResolve()
     */
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        ByteArrayInputStream aByteInputStream = null;
        GZIPInputStream aGZIPInputStream = null;

        // read first byte to get DataObject type [0 = internal, 1 = root]
        int dataObjectIdentifier = objectInput.read();

        /**
         * based on the first byte of the object stream we will deserialize into
         * either of the following.
         *
         * Internal: byte(0) = 0 [path] = XPath string in UTF format with the
         * location of the object in relation to the root [root] = theserialized
         * root DataObject (2 calls to this function)
         *
         * Root: byte(0) = 1 [rootXML] = the GZIP binary stream of the XML
         * serialization of the root DataObject (1 call to this function)
         */

        // check whether we are a root DataObject, write gzip of the root
        switch (dataObjectIdentifier) {
        case SDO_INTERNAL_OBJECT_IDENTIFIER:
            // not a root object, read the [path] and [root]
            // read path to the root
            String xPathString = objectInput.readUTF();

            // read root (an ExternalizableDelegator) via an indirect recursive call back to
            // the root case below will resolve the root
            SDODataObject deserializedDataObject = (SDODataObject)objectInput.readObject();

            // point SDODataObject member to the internal node based on XPath retrieved
            theSDODataObject = (SDODataObject)deserializedDataObject.get(xPathString);
            // return internal DataObject in next readResolve callback
            break;
        case SDO_ROOT_OBJECT_IDENTIFIER:
            try {
                // get length of gzip stream
                int aStreamLength = objectInput.readInt();

                // read in gzip bytes [rootXML]
                byte[] aGZIPByteArray = new byte[aStreamLength];

                // read in the length of bytes - EOF, buffering and stream
                // length is handled internally by this function
                objectInput.readFully(aGZIPByteArray);

                // setup input stream chaining
                aByteInputStream = new ByteArrayInputStream(aGZIPByteArray);

                // chain a GZIP stream to the byte stream
                aGZIPInputStream = new GZIPInputStream(aByteInputStream);

                // read XML Serialization of the root DataObject from the GZIP input stream
                XMLDocument aDocument = aHelperContext.getXMLHelper().load(aGZIPInputStream);

                theSDODataObject = (SDODataObject)aDocument.getRootObject();            
            } finally {
                // close streams on all Exceptions
                if (aGZIPInputStream != null) {// Clover: false case testing requires IO/comm failure
                    aGZIPInputStream.close();
                }
                if (aByteInputStream != null) {// Clover: false case testing requires IO/comm failure
                    aByteInputStream.close();
                }
            }
            break;
        case SDO_HELPER_CONTEXT_ID_IDENTIFIER:
            try {
                String helperContextIdentifier = objectInput.readUTF();
                // get length of gzip stream
                int aStreamLength = objectInput.readInt();

                // read in gzip bytes [rootXML]
                byte[] aGZIPByteArray = new byte[aStreamLength];

                // read in the length of bytes - EOF, buffering and stream
                // length is handled internally by this function
                objectInput.readFully(aGZIPByteArray);

                // setup input stream chaining
                aByteInputStream = new ByteArrayInputStream(aGZIPByteArray);

                // chain a GZIP stream to the byte stream
                aGZIPInputStream = new GZIPInputStream(aByteInputStream);

                // read XML Serialization of the root DataObject from the GZIP input stream
                HelperContext contextToUse = SDOHelperContext.getHelperContext(helperContextIdentifier);

                XMLDocument aDocument = contextToUse.getXMLHelper().load(aGZIPInputStream);

                theSDODataObject = (SDODataObject)aDocument.getRootObject();            
            } finally {
                // close streams on all Exceptions
                if (aGZIPInputStream != null) {// Clover: false case testing requires IO/comm failure
                    aGZIPInputStream.close();
                }
                if (aByteInputStream != null) {// Clover: false case testing requires IO/comm failure
                    aByteInputStream.close();
                }
            }
            break;
        }
    }

    /**
     * 
     * @return
     */
    public HelperContext getHelperContext() {
        return aHelperContext;
    }

    /**
     * 
     * @param helperContext
     */
    public void setHelperContext(HelperContext helperContext) {
        aHelperContext = helperContext;
    }
}
