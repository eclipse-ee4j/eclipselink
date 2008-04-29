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

package javax.ejb.spi;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import javax.ejb.EJBObject;
import javax.ejb.EJBHome;


/**
 * The HandleDelegate interface is implemented by the EJB container. 
 * It is used by portable implementations of javax.ejb.Handle and
 * javax.ejb.HomeHandle.
 * It is not used by EJB components or by client components.
 * It provides methods to serialize and deserialize EJBObject and
 * EJBHome references to streams.
 *
 * <p> The HandleDelegate object is obtained by JNDI lookup at the
 * reserved name "java:comp/HandleDelegate".
 */

public interface HandleDelegate {
    /**
     * Serialize the EJBObject reference corresponding to a Handle.
     *
     * <p> This method is called from the writeObject method of 
     * portable Handle implementation classes. The ostream object is the
     * same object that was passed in to the Handle class's writeObject.
     *
     * @param ejbObject The EJBObject reference to be serialized.
     *
     * @param ostream The output stream.
     *
     * @exception IOException The EJBObject could not be serialized
     *    because of a system-level failure.
     */
    public void writeEJBObject(EJBObject ejbObject, ObjectOutputStream ostream)
	throws IOException;


    /**
     * Deserialize the EJBObject reference corresponding to a Handle.
     *
     * <p> readEJBObject is called from the readObject method of 
     * portable Handle implementation classes. The istream object is the
     * same object that was passed in to the Handle class's readObject.
     * When readEJBObject is called, istream must point to the location
     * in the stream at which the EJBObject reference can be read.
     * The container must ensure that the EJBObject reference is 
     * capable of performing invocations immediately after deserialization.
     *
     * @param istream The input stream.
     *
     * @return The deserialized EJBObject reference.
     *
     * @exception IOException The EJBObject could not be deserialized
     *    because of a system-level failure.
     * @exception ClassNotFoundException The EJBObject could not be deserialized
     *    because some class could not be found.
     */
    public javax.ejb.EJBObject readEJBObject(ObjectInputStream istream)
	throws IOException, ClassNotFoundException;

    /**
     * Serialize the EJBHome reference corresponding to a HomeHandle.
     *
     * <p> This method is called from the writeObject method of 
     * portable HomeHandle implementation classes. The ostream object is the
     * same object that was passed in to the Handle class's writeObject.
     *
     * @param ejbHome The EJBHome reference to be serialized.
     *
     * @param ostream The output stream.
     *
     * @exception IOException The EJBObject could not be serialized
     *    because of a system-level failure.
     */
    public void writeEJBHome(EJBHome ejbHome, ObjectOutputStream ostream)
	throws IOException;

    /**
     * Deserialize the EJBHome reference corresponding to a HomeHandle.
     *
     * <p> readEJBHome is called from the readObject method of 
     * portable HomeHandle implementation classes. The istream object is the
     * same object that was passed in to the HomeHandle class's readObject.
     * When readEJBHome is called, istream must point to the location
     * in the stream at which the EJBHome reference can be read.
     * The container must ensure that the EJBHome reference is 
     * capable of performing invocations immediately after deserialization.
     *
     * @param istream The input stream.
     *
     * @return The deserialized EJBHome reference.
     *
     * @exception IOException The EJBHome could not be deserialized
     *    because of a system-level failure.
     * @exception ClassNotFoundException The EJBHome could not be deserialized
     *    because some class could not be found.
     */
    public javax.ejb.EJBHome readEJBHome(ObjectInputStream istream)
	throws IOException, ClassNotFoundException;
}
