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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public final class JModel {

	private final JPackage _rootPackage = new JPackage();

	private final Set<AbstractJType<?>> _createdTypes = new HashSet<>();

	public JModel() {
		super();
	}

	public JClass createClass(final @Nonnull String pQualifiedClassName) {
		final JTypeReference jTypeReference = _rootPackage.createOfGetTypeReference(pQualifiedClassName);
		final JTypeUsage jTypeUsage = jTypeReference.createUsage();
		final JClass jClass = new JClass(jTypeUsage);
		_createdTypes.add(jClass);
		return jClass;
	}

	public JTypeReference createOrGetTypeReference(final @Nonnull String pQualifiedTypeName) {
		return _rootPackage.createOfGetTypeReference(pQualifiedTypeName);
	}

	public JTypeReference createOrGetTypeReference(final @Nonnull Class<?> pClass) {
		GenesisUtils.assertNotNull(pClass);

		return createOrGetTypeReference(pClass.getName());
	}

	public Collection<AbstractJType<?>> getCreatedTypes() {
		return Collections.unmodifiableSet(_createdTypes);
	}

}
