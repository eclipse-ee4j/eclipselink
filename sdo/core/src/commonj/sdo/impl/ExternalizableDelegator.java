/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package commonj.sdo.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import org.eclipse.persistence.sdo.SDOResolvable;
import org.eclipse.persistence.sdo.helper.DataObjectInputStream;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import commonj.sdo.helper.HelperContext;

/**
 * <p>Delegates DataObject serialization while ensuring implementation independent
 * java.io.Serialization.  An implementation of DataObject
 * returns an ExternalizableDelegator from its writeReplace() method.</p>
 *
 * <p>The root DataObject is the object returned from do.getRootObject() where
 *   do is the DataObject being serialized in a java.io.ObjectOutputStream.
 *   When do.getContainer() == null then do is a root object.</p>
 *
 * <p>The byte format for each DataObject in the stream is:
 * <br>[0] [path] [root]   // when do is not a root object
 * <br>[1] [rootXML]       // when do is a root object<p>
 *
 * <p>where:
 * <br>[0] is the byte 0, serialized using writeByte(0).
 * <br>[1] is the byte 1, serialized using writeByte(1).</p>
 *
 * <p>[path] is an SDO path expression from the root DataObject to the serialized
 *          DataObject such that root.getDataObject(path) == do.
 *          Serialized using writeUTF(path).</p>
 *
 * <p>[root] is the root object serialized using writeObject(root).</p>
 *
 * <p>[rootXML] is the GZip of the XML serialization of the root DataObject.
 *          The XML serialization is the same as
 *          XMLHelper.INSTANCE.save(root, "commonj.sdo", "dataObject", stream);
 *          where stream is a GZIPOutputStream, length is the number of bytes
 *          in the stream, and bytes are the contents of the stream.
 *          Serialized using writeInt(length), write(bytes).</p>
 *
 */
public class ExternalizableDelegator implements Externalizable {
    public interface Resolvable extends Externalizable {
        Object readResolve() throws ObjectStreamException;
    }

    static final long serialVersionUID = 1;
    transient Resolvable delegate;

    public ExternalizableDelegator() {
        // Use default static context, this delegate will be reset in readExternal() when it is non-static
        delegate = HelperProvider.createResolvable();
    }

    public ExternalizableDelegator(Object target, HelperContext aContext) {
        // JIRA129: pass the helperContext to the constructor to enable non-static contexts
        // check for context type (if non-static SDOHelperContext then we need to cast to use the non-interface createResolvable function
        // to remove this instanceof check - add createResolvable to the HelperContext interface
        if (aContext instanceof SDOHelperContext) {
            delegate = ((SDOHelperContext)aContext).createResolvable(target);
        } else {
            // use static helper
            delegate = HelperProvider.createResolvable(target);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        delegate.writeExternal(out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // This function is indirectly called by InputStream.readExternalData().
        // We reset the static helperContext set in the default constructor during is.readObject()
        // with the passed in context from the client (either static or dynamic instance)
        if (in instanceof DataObjectInputStream) {
            // only reset non-static implementations
            ((SDOResolvable)delegate).setHelperContext(((DataObjectInputStream)in).getHelperContext());
        }
        delegate.readExternal(in);
    }

    public Object readResolve() throws ObjectStreamException {
        return delegate.readResolve();
    }
}
