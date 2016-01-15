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
 *
 */
public abstract class AbstractJTypedElement extends AbstractJNamed {

	private final JTypeUsage _type;
	private final List<JAnnotation> _annotations = new ArrayList<>();
	private boolean _final;

	AbstractJTypedElement(JTypeUsage pType, String pName) {
		super(pName);
		Util.assertNotNull(pType);
		Util.assertTrue(!Util.isEmpty(pName));

		_type = pType;
	}

	public final JTypeUsage getType() {
		return _type;
	}

	public final boolean isFinal() {
		return _final;
	}

	public final void setFinal(boolean pFinal) {
		_final = pFinal;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + _type + "]";
	}
}
