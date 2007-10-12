/**
 * <copyright>
 *
 * Service Data Objects
 * Version 2.1.0
 * Licensed Materials
 *
 * (c) Copyright BEA Systems, Inc., International Business Machines Corporation, 
 * Oracle Corporation, Primeton Technologies Ltd., Rogue Wave Software, SAP AG., 
 * Software AG., Sun Microsystems, Sybase Inc., Xcalia, Zend Technologies, 
 * 2005, 2006. All rights reserved.
 *
 * </copyright>
 * 
 */

package commonj.sdo.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

/**
 * Delegates DataObject serialization while ensuring implementation independent 
 * java.io.Serialization.  An implementation of DataObject
 * returns an ExternalizableDelegator from its writeReplace() method.
 * 
 * The root DataObject is the object returned from do.getRootObject() where
 *   do is the DataObject being serialized in a java.io.ObjectOutputStream.
 *   When do.getContainer() == null then do is a root object.
 * 
 * The byte format for each DataObject in the stream is:
 * [0] [path] [root]   // when do is not a root object
 * [1] [rootXML]       // when do is a root object
 * 
 * where:
 * [0] is the byte 0, serialized using writeByte(0).
 * [1] is the byte 1, serialized using writeByte(1).
 *   
 * [path] is an SDO path expression from the root DataObject to the serialized 
 *          DataObject such that root.getDataObject(path) == do.
 *          Serialized using writeUTF(path).
 * 
 * [root] is the root object serialized using writeObject(root).
 * 
 * [rootXML] is the GZip of the XML serialization of the root DataObject.
 *          The XML serialization is the same as 
 *          XMLHelper.INSTANCE.save(root, "commonj.sdo", "dataObject", stream);
 *          where stream is a GZIPOutputStream, length is the number of bytes 
 *          in the stream, and bytes are the contents of the stream.
 *          Serialized using writeInt(length), write(bytes).
 *
 */
public class ExternalizableDelegator implements Externalizable
{
  public interface Resolvable extends Externalizable
  {
    Object readResolve() throws ObjectStreamException;
  }
  
  static final long serialVersionUID = 1;
  transient Resolvable delegate;
  
  public ExternalizableDelegator()
  {
    delegate = HelperProvider.createResolvable();
  }

  public ExternalizableDelegator(Object target)
  {
    delegate = HelperProvider.createResolvable(target);
  }

  public void writeExternal(ObjectOutput out) throws IOException
  {
    delegate.writeExternal(out);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
  {
    delegate.readExternal(in);
  }

  public Object readResolve() throws ObjectStreamException
  {
    return delegate.readResolve();
  }
}
