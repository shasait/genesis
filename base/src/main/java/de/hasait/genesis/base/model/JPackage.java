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

package de.hasait.genesis.base.model;

import java.util.HashMap;
import java.util.Map;

import de.hasait.genesis.base.util.GenesisUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public final class JPackage extends AbstractJNamed {

	private final JPackage _parent;

	private final boolean _root;

	private final String _qualifiedName;

	private final Map<String, JPackage> _childrenByName = new HashMap<String, JPackage>();

	private final Map<String, JTypeReference> _typesByName = new HashMap<String, JTypeReference>();

	JPackage(final JPackage pParent, final String pName) {
		super(pName);
		GenesisUtils.assertNotNull(pParent);

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

	public String buildQualifiedName(final String pRelativeName) {
		return _root ? pRelativeName : getQualifiedName() + "." + pRelativeName;
	}

	public JTypeReference createOfGetTypeReference(final String pQualifiedTypeName) {
		return createOrGetChildObject(pQualifiedTypeName, _typesByName, new Creator<JTypeReference>() {
			@Override
			public JTypeReference create(final JPackage pParent, final String pName) {
				return new JTypeReference(pParent, pName);
			}
		});
	}

	public JPackage createOrGetChildPackage(final String pQualifiedPackageName) {
		return createOrGetChildObject(pQualifiedPackageName, _childrenByName, new Creator<JPackage>() {
			@Override
			public JPackage create(final JPackage pParent, final String pName) {
				return new JPackage(pParent, pName);
			}
		});
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

	private <T> T createOrGetChildObject(final String pQualifiedName, final Map<String, T> pMap, final Creator<T> pCreator) {
		GenesisUtils.assertTrue(!StringUtils.isEmpty(pQualifiedName));

		final String[] parts = pQualifiedName.split("\\.", 2);

		if (parts.length == 2) {
			final String packagePart = parts[0];
			if (!_childrenByName.containsKey(packagePart)) {
				_childrenByName.put(packagePart, new JPackage(this, packagePart));
			}
			final JPackage child = _childrenByName.get(packagePart);
			return child.createOrGetChildObject(parts[1], pMap, pCreator);
		} else {
			return pCreator.create(this, parts[0]);
		}
	}

	private interface Creator<T> {
		T create(JPackage pParent, String pName);
	}

}
