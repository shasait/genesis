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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public abstract class AbstractJType<IMPL extends AbstractJType<IMPL>> extends AbstractJDeclaredTypedElement {

	private final List<JField> _fields = new ArrayList<JField>();
	private final List<AbstractJMethod> _methods = new ArrayList<AbstractJMethod>();
	private final List<AbstractJType> _innerTypes = new ArrayList<AbstractJType>();
	private final List<String> _customCodes = new ArrayList<String>();
	private final List<AbstractJPattern<IMPL>> _patterns = new ArrayList<AbstractJPattern<IMPL>>();

	AbstractJType(final JTypeUsage pType) {
		super(pType, pType.getType().getName());
		GenesisUtils.assertTrue(pType.getType().getType() == null);

		pType.getType().setType(this);
		setVisibility(JVisibility.PUBLIC);
	}

	public final JField addField(final JTypeUsage pType, final String pName) {
		final JField field = new JField(pType, pName);
		initField(field);
		_fields.add(field);
		return field;
	}

	public List<JField> getFields() {
		return Collections.unmodifiableList(_fields);
	}

	protected void initField(final JField pField) {
		// nop
	}

}
