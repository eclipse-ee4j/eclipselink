/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.xml;


/**
 * This policy is used by <code>XMLFileAccessorFilePolicy</code> to
 * "normalize" file names; i.e. convert invalid characters into something
 * acceptable to the current O/S.
 *
 * @author Big Country
 * @since TOPLink/Java 4.5
 * @deprecated since OracleAS TopLink 10<i>g</i> (10.1.3).  This class is replaced by
 *         {@link org.eclipse.persistence.oxm}
 */
public interface FileNameNormalizer {

    /**
     * Convert the specified, <i>unqualified</i> file name into something
     * that should be palatable as a file name
     * (e.g. replace invalid characters with escape sequences).
     * The name must be unqualified so we don't convert any legitimate
     * file name separators.
     */
    String normalize(String unqualifiedFileName);
}