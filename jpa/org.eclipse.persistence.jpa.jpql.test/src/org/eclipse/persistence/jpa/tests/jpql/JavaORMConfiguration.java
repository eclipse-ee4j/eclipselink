/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.jpa.jpql.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.spi.IType;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.java.JavaQuery;
import org.eclipse.persistence.jpa.jpql.util.iterator.IterableIterator;
import org.junit.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class JavaORMConfiguration extends JavaManagedTypeProvider
                                  implements IORMConfiguration {

	private String ormXmlFileName;
	private boolean populated;
	private Map<String, IQuery> queries;

	/**
	 * Creates a new <code>JavaORMConfiguration</code>.
	 *
	 * @param ormXmlFileName
	 */
	public JavaORMConfiguration(String ormXmlFileName) {
		super(null); // TODO
		this.ormXmlFileName = ormXmlFileName;
	}

	protected void addQuery(Map<String, IQuery> queries, Node node) {

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

	protected String buildLocation() throws Exception {
		URL url = getClass().getResource("/META-INF/" + ormXmlFileName);
		Assert.assertNotNull("/META-INF/" + ormXmlFileName + " could not be found on the class path", url);
		return url.toURI().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IterableIterator<IEntity> entities() {
		populate();
		return super.entities();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IEntity getEntity(String entityName) {
		populate();
		return super.getEntity(entityName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IManagedType getManagedType(IType type) {
		populate();
		return super.getManagedType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	public IQuery getNamedQuery(String queryName) {
		populate();
		return queries.get(queryName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IterableIterator<IManagedType> managedTypes() {
		populate();
		return super.managedTypes();
	}

	protected void populate() {

		if (!populated) {

			populated = true;

			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder =  factory.newDocumentBuilder();
				Document document = builder.parse(buildLocation());

				populateQueries(document);
				populateManagedTypes(document);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void populateManagedTypes(Document document) throws Exception {

		NodeList nodeList = document.getElementsByTagName("entity");

		for (int index = nodeList.getLength(); --index >= 0; ) {
			Node node = nodeList.item(index);
			String typeName = node.getAttributes().getNamedItem("class").getNodeValue();
			addEntity(Class.forName(typeName));
		}

		nodeList = document.getElementsByTagName("mapped-superclass");

		for (int index = nodeList.getLength(); --index >= 0; ) {
			Node node = nodeList.item(index);
			String typeName = node.getAttributes().getNamedItem("class").getNodeValue();
			addMappedSuperclass(Class.forName(typeName));
		}

		nodeList = document.getElementsByTagName("embeddable");

		for (int index = nodeList.getLength(); --index >= 0; ) {
			Node node = nodeList.item(index);
			String typeName = node.getAttributes().getNamedItem("class").getNodeValue();
			addEmbeddable(Class.forName(typeName));
		}
	}

	protected void populateQueries(Document document) {

		queries = new HashMap<String, IQuery>();
		NodeList nodeList = document.getElementsByTagName("named-query");

		for (int index = nodeList.getLength(); --index >= 0; ) {
			Node node = nodeList.item(index);
			addQuery(queries, node);
		}
	}
}