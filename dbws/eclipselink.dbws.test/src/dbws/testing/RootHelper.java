//Copyright (c) 2006, Oracle. All rights reserved.

package dbws.testing;

// J2SE imports
import java.util.Vector;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;

/**
 * <p>
 * <b>INTERNAL:</b> A simple wrapper for a collection of objects. When
 * marshalling objects via MOXy, one cannot pass a Vector to the
 * XMLMarshaller, so this class is used instead along with a 'default' O-X
 * mapping as XMLAnyCollectionMapping
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

public class RootHelper {

    @SuppressWarnings("unchecked")
    public Vector roots = new NonSynchronizedVector();

    public RootHelper() {
        super();
    }

    @Override
    public String toString() {
      return roots.toString();
    }

}
