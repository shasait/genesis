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

/**
 *
 */
public abstract class AbstractJDeclaredTypedElement extends AbstractJTypedElement {

	private final List<JComment> _comments = new ArrayList<>();
	private JVisibility _visibility = JVisibility.PRIVATE;
	private boolean _static;

	AbstractJDeclaredTypedElement(JTypeUsage pType, String pName) {
		super(pType, pName);
	}

	public final JVisibility getVisibility() {
		return _visibility;
	}

	public final boolean isStatic() {
		return _static;
	}

	public final void setStatic(boolean pStatic) {
		_static = pStatic;
	}

	public final void setVisibility(JVisibility pVisibility) {
		_visibility = pVisibility;
	}

}
