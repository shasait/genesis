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

import java.util.ArrayList;
import java.util.List;

import de.hasait.genesis.processor.Util;

/**
 * Usage of a type with optional generic arguments.
 */
public final class JTypeUsage {

	private final JTypeReference _type;

	private final List<JTypeArgument> _arguments = new ArrayList<>();

	public JTypeUsage(JTypeReference pType, JTypeArgument... pArguments) {
		super();
		Util.assertNotNull(pType);

		_type = pType;

		for (JTypeArgument argument : pArguments) {
			addArgument(argument);
		}
	}

	public void addArgument(JTypeArgument pArgument) {
		Util.assertNotNull(pArgument);

		_arguments.add(pArgument);
	}

	public JTypeReference getType() {
		return _type;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + _type + "<" + _arguments + ">" + "]";
	}
}