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

/**
 * Generic type bound.
 */
public final class JTypeArgument {

	public static JTypeArgument createAny() {
		return new JTypeArgument(null, false, false);
	}

	public static JTypeArgument createExact(final JTypeUsage pType) {
		return new JTypeArgument(pType, false, false);
	}

	public static JTypeArgument createExtends(final JTypeUsage pType) {
		return new JTypeArgument(pType, true, false);
	}

	public static JTypeArgument createSuper(final JTypeUsage pType) {
		return new JTypeArgument(pType, false, true);
	}

	private final JTypeUsage _type;
	private final boolean _extends;
	private final boolean _super;

	private JTypeArgument(final JTypeUsage pType, final boolean pExtends, final boolean pSuper) {
		_type = pType;
		_extends = pExtends;
		_super = pSuper;
	}

	public final JTypeUsage getType() {
		return _type;
	}

	public final boolean isExtends() {
		return _extends;
	}

	public final boolean isSuper() {
		return _super;
	}

}
