/*
 * Copyright (C) 2017 by Sebastian Hasait (sebastian at hasait dot de)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 * Usage of a type with optional generic arguments.
 */
public final class JTypeUsage implements JSrcSupported {

	private final JTypeReference _type;

	private final List<JTypeArgument> _arguments = new ArrayList<JTypeArgument>();

	private final List<Integer> _arrayDimensions = new ArrayList<Integer>();

	public JTypeUsage(final @Nonnull JTypeReference pType, final JTypeArgument... pArguments) {
		this(pType, Arrays.asList(pArguments), null);
	}

	public JTypeUsage(final @Nonnull JTypeReference pType, final @Nullable List<JTypeArgument> pArguments, final @Nullable List<Integer> pArrayDimensions) {
		super();
		GenesisUtils.assertNotNull(pType);

		_type = pType;

		if (pArguments != null) {
			for (final JTypeArgument argument : pArguments) {
				addArgument(argument);
			}
		}
		if (pArrayDimensions != null) {
			for (final Integer dimension : pArrayDimensions) {
				addArrayDimension(dimension);
			}
		}
	}

	public void addArgument(final @Nonnull JTypeArgument pArgument) {
		GenesisUtils.assertNotNull(pArgument);

		_arguments.add(pArgument);
	}

	public void addArrayDimension(final @Nullable Integer pDimension) {
		_arrayDimensions.add(pDimension);
	}

	public JTypeUsage erasure() {
		return _arguments.isEmpty() ? this : new JTypeUsage(_type, null, _arrayDimensions);
	}

	public JTypeReference getType() {
		return _type;
	}

	public String toSrc(final SrcContext pContext) {
		final StringBuilder sb = new StringBuilder();
		sb.append(_type.toSrc(pContext));
		if (!_arguments.isEmpty()) {
			sb.append('<');
			boolean first = true;
			for (final JTypeArgument argument : _arguments) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				sb.append(argument.toSrc(pContext));
			}
			sb.append('>');
		}
		for (final Integer dimension : _arrayDimensions) {
			sb.append('[');
			if (dimension != null) {
				sb.append(dimension);
			}
			sb.append(']');
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + _type + "<" + _arguments + ">" + _arrayDimensions + "]";
	}

}
