/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.jpql.tests;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.jpa.jpql.spi.IJPAVersion;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.junit.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaORMConfiguration extends JavaManagedTypeProvider
                                 implements IORMConfiguration {

	private String ormXmlFileName;
	private Map<String, IQuery> queries;

	/**
	 * Creates a new <code>JavaORMConfiguration</code>.
	 */
	JavaORMConfiguration(String ormXmlFileName) {
		super(IJPAVersion.DEFAULT_VERSION);
		this.ormXmlFileName = ormXmlFileName;
	}

	private void addQuery(Map<String, IQuery> queries, Node node) {

		NamedNodeMap attributes = node.getAttributes();
		Attr nameNode = (Attr) attributes.getNamedItem("name");
		NodeList children = node.getChildNodes();

		for (int childIndex = children.getLength(); --childIndex >= 0; ) {
			Node child = children.item(childIndex);

			if (child.getNodeName().equals("query")) {
				queries.put(nameNode.getValue(), new JavaQuery(this, child.getTextContent()));
			}
		}
	}

	private String buildLocation() throws Exception {
		URL url = getClass().getResource("/META-INF/" + ormXmlFileName);
		Assert.assertNotNull("/META-INF/" + ormXmlFileName + " could not be found on the class path", url);
		return url.toURI().toString();
	}

	private Map<String, IQuery> buildQueries() {
		Map<String, IQuery> queries = new HashMap<String, IQuery>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder =  factory.newDocumentBuilder();
			Document document = builder.parse(buildLocation());
			NodeList nodeList = document.getElementsByTagName("named-query");

			for (int index = nodeList.getLength(); --index >= 0; ) {
				Node node = nodeList.item(index);
				addQuery(queries, node);
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return queries;
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getNamedQuery(String queryName) {
		initializeQueries();
		return queries.get(queryName);
	}

	private void initializeQueries() {
		if (queries == null) {
			queries = buildQueries();
		}
	}
}