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

import javax.annotation.Nullable;

/**
 * Generic type bound. (2nd bound not supported yet)
 */
public final class JTypeArgument implements JSrcSupported {

	public static JTypeArgument createAny() {
		return new JTypeArgument(null, null, false, false);
	}

	public static JTypeArgument createExact(final JTypeUsage pType) {
		return new JTypeArgument(null, pType, false, false);
	}

	public static JTypeArgument createExtends(final JTypeUsage pType) {
		return new JTypeArgument(null, pType, true, false);
	}

	public static JTypeArgument createSuper(final JTypeUsage pType) {
		return new JTypeArgument(null, pType, false, true);
	}

	public static JTypeArgument createVar(final String pVarName) {
		return new JTypeArgument(pVarName, null, false, false);
	}

	public static JTypeArgument createVarExtends(final String pVarName, final JTypeUsage pType) {
		return new JTypeArgument(pVarName, pType, true, false);
	}

	public static JTypeArgument createVarSuper(final String pVarName, final JTypeUsage pType) {
		return new JTypeArgument(pVarName, pType, false, true);
	}

	private final String _varName;
	private final JTypeUsage _type;
	private final boolean _extends;
	private final boolean _super;

	private JTypeArgument(final String pVarName, final JTypeUsage pType, final boolean pExtends, final boolean pSuper) {
		super();

		_varName = pVarName;
		_type = pType;
		_extends = pExtends;
		_super = pSuper;
	}

	@Nullable
	public JTypeUsage getType() {
		return _type;
	}

	@Nullable
	public String getVarName() {
		return _varName;
	}

	public boolean isExtends() {
		return _extends;
	}

	public boolean isSuper() {
		return _super;
	}

	public String toSrc(final SrcContext pContext) {
		final StringBuilder sb = new StringBuilder();
		if (_type == null || _extends || _super) {
			sb.append(_varName == null ? "?" : _varName);
		}
		if (_extends) {
			sb.append(" extends ");
		}
		if (_super) {
			sb.append(" super ");
		}
		if (_type != null) {
			sb.append(_type.toSrc(pContext));
		}
		return sb.toString();
	}

}
