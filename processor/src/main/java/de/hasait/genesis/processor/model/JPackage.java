/*
 * Copyright (C) 2016 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hasait.genesis.processor.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import de.hasait.genesis.processor.Util;

/**
 *
 */
public final class JPackage extends AbstractJNamed {

	private final JPackage _parent;

	private final boolean _root;

	private final String _qualifiedName;

	private final Map<String, JPackage> _childrenByName = new HashMap<>();

	private final Map<String, JTypeReference> _typesByName = new HashMap<>();

	JPackage(JPackage pParent, String pName) {
		super(pName);
		Util.assertNotNull(pParent);

		_parent = pParent;
		_root = false;
		_qualifiedName = _parent.buildQualifiedName(pName);
	}

	/**
	 * Constructor for root package.
	 */
	JPackage() {
		super("#ROOT#");

		_parent = null;
		_root = true;
		_qualifiedName = null;
	}

	public String buildQualifiedName(String pRelativeName) {
		return _root ? pRelativeName : getQualifiedName() + "." + pRelativeName;
	}

	public JTypeReference createOfGetTypeReference(String pQualifiedTypeName) {
		return createOrGetChildObject(pQualifiedTypeName,
									  (pParent, pName) -> _typesByName.computeIfAbsent(pName, pUnused -> new JTypeReference(pParent, pName))
		);
	}

	public JPackage createOrGetChildPackage(String pQualifiedPackageName) {
		return createOrGetChildObject(pQualifiedPackageName,
									  (pParent, pName) -> _childrenByName.computeIfAbsent(pName, pUnused -> new JPackage(pParent, pName))
		);
	}

	public JPackage getParent() {
		return _parent;
	}

	public String getQualifiedName() {
		return _qualifiedName;
	}

	public boolean isRoot() {
		return _root;
	}

	private <T> T createOrGetChildObject(String pQualifiedName, BiFunction<JPackage, String, T> pCreator) {
		Util.assertTrue(!Util.isEmpty(pQualifiedName));

		String[] parts = pQualifiedName.split("\\.", 2);

		if (parts.length == 2) {
			JPackage child = _childrenByName.computeIfAbsent(parts[0], pName -> new JPackage(this, pName));
			return child.createOrGetChildObject(parts[1], pCreator);
		} else {
			return pCreator.apply(this, parts[0]);
		}
	}

}
