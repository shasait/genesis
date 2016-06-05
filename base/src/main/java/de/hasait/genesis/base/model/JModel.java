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

import javax.annotation.Generated;
import javax.annotation.Nonnull;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public final class JModel {

	private final Class<?> _generatorClass;

	private final JPackage _rootPackage = new JPackage();

	private final Set<AbstractJType<?>> _createdTypes = new HashSet<AbstractJType<?>>();

	public JModel(final @Nonnull Class<?> pGeneratorClass) {
		super();
		GenesisUtils.assertNotNull(pGeneratorClass);

		_generatorClass = pGeneratorClass;
	}

	public JClass createClass(final @Nonnull String pQualifiedClassName) {
		final JTypeReference jTypeReference = _rootPackage.createOfGetTypeReference(pQualifiedClassName);
		final JTypeUsage jTypeUsage = jTypeReference.createUsage();
		final JClass jClass = new JClass(jTypeUsage);
		_createdTypes.add(jClass);
		addGeneratedAnnotation(jClass);
		return jClass;
	}

	public JTypeReference createOrGetTypeReference(final @Nonnull String pQualifiedTypeName) {
		return _rootPackage.createOfGetTypeReference(pQualifiedTypeName);
	}

	public JTypeReference createOrGetTypeReference(final @Nonnull Class<?> pClass) {
		GenesisUtils.assertNotNull(pClass);

		if (pClass.isPrimitive()) {
			return JTypeReference.PRIMITIVE_TYPES.get(pClass);
		}

		return createOrGetTypeReference(pClass.getName());
	}

	public Collection<AbstractJType<?>> getCreatedTypes() {
		return Collections.unmodifiableSet(_createdTypes);
	}

	private void addGeneratedAnnotation(final @Nonnull AbstractJType<?> pJType) {
		final JTypeReference generatedTypeReference = createOrGetTypeReference(Generated.class);
		pJType.addImport(generatedTypeReference);
		final JAnnotation generatedAnnotation = pJType.addAnnotation(generatedTypeReference);
		generatedAnnotation.addArgAssignment("value", JSrcExpression.stringValue(_generatorClass.getCanonicalName()));
	}

}
