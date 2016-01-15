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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.hasait.genesis.processor.Util;

/**
 *
 */
public final class JModel {

	private final String _baseQualifiedPackageName;
	private final String _baseTypeName;

	private final JPackage _rootPackage = new JPackage();

	private final Set<AbstractJType<?>> _createdTypes = new HashSet<>();

	public JModel(String pBaseQualifiedPackageName, String pBaseTypeName) {
		super();

		_baseQualifiedPackageName = pBaseQualifiedPackageName;
		_baseTypeName = pBaseTypeName;
	}

	public JClass createClass(String pQualifiedClassName) {
		JTypeReference jTypeReference = _rootPackage.createOfGetTypeReference(pQualifiedClassName);
		JTypeUsage jTypeUsage = jTypeReference.createUsage();
		JClass jClass = new JClass(jTypeUsage);
		_createdTypes.add(jClass);
		return jClass;
	}

	public JTypeReference createOrGetTypeReference(String pQualifiedTypeName) {
		return _rootPackage.createOfGetTypeReference(pQualifiedTypeName);
	}

	public JTypeReference createOrGetTypeReference(Class<?> pClass) {
		Util.assertNotNull(pClass);

		return createOrGetTypeReference(pClass.getName());
	}

	public JClass createRelativeClass(String pSuffix) {
		String className = _baseTypeName + pSuffix;
		String qualifiedClassName = _baseQualifiedPackageName == null ? className : _baseQualifiedPackageName + "." + className;
		return createClass(qualifiedClassName);
	}

	public Collection<AbstractJType<?>> getCreatedTypes() {
		return Collections.unmodifiableSet(_createdTypes);
	}

}
