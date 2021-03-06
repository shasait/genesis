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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 * Reference to a type.
 */
public final class JTypeReference extends AbstractJNamed implements JSrcSupported {

	public static final JTypeReference VOID = new JTypeReference("void", false, true);
	public static final JTypeReference BOOLEAN = new JTypeReference("boolean", true, false);
	public static final JTypeReference BYTE = new JTypeReference("byte", true, false);
	public static final JTypeReference SHORT = new JTypeReference("short", true, false);
	public static final JTypeReference INT = new JTypeReference("int", true, false);
	public static final JTypeReference LONG = new JTypeReference("long", true, false);
	public static final JTypeReference FLOAT = new JTypeReference("float", true, false);
	public static final JTypeReference DOUBLE = new JTypeReference("double", true, false);
	public static final JTypeReference CHAR = new JTypeReference("char", true, false);

	public static final Map<Class<?>, JTypeReference> PRIMITIVE_TYPES;

	private static final String JAVA_LANG_PACKAGE = "java.lang";

	static {
		final Map<Class<?>, JTypeReference> map = new HashMap<Class<?>, JTypeReference>();
		map.put(void.class, VOID);
		map.put(boolean.class, BOOLEAN);
		map.put(byte.class, BYTE);
		map.put(short.class, SHORT);
		map.put(int.class, INT);
		map.put(long.class, LONG);
		map.put(float.class, FLOAT);
		map.put(double.class, DOUBLE);
		map.put(char.class, CHAR);
		PRIMITIVE_TYPES = Collections.unmodifiableMap(map);
	}

	private final JPackage _package;
	private final boolean _primitive;
	private final boolean _void;
	private final String _qualifiedName;
	private AbstractJType _type;

	JTypeReference(final @Nonnull JPackage pPackage, final String pName) {
		super(pName);
		GenesisUtils.assertNotNull(pPackage);

		_package = pPackage;
		_primitive = false;
		_void = false;
		_qualifiedName = _package.buildQualifiedName(pName);
	}

	private JTypeReference(final String pName, final boolean pPrimitive, final boolean pVoid) {
		super(pName);

		_package = null;
		_primitive = pPrimitive;
		_void = pVoid;
		_qualifiedName = pName;
	}

	public JTypeUsage createUsage(final JTypeArgument... pArguments) {
		return new JTypeUsage(this, pArguments);
	}

	@Nonnull
	public JPackage getPackage() {
		return _package;
	}

	@Nonnull
	public String getQualifiedName() {
		return _qualifiedName;
	}

	public AbstractJType getType() {
		return _type;
	}

	public boolean isJavaLang() {
		return _package != null && JAVA_LANG_PACKAGE.equals(_package.getQualifiedName());
	}

	public boolean isPrimitive() {
		return _primitive;
	}

	public boolean isVoid() {
		return _void;
	}

	public String toSrc(final SrcContext pContext) {
		return toSrc(pContext, false);
	}

	public String toSrc(final SrcContext pContext, boolean pIgnoreImports) {
		if (isJavaLang() || (!pIgnoreImports && pContext.getType().containsImport(this))) {
			return getName();
		}
		return getQualifiedName();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + _qualifiedName + "]";
	}

	void setType(final AbstractJType pType) {
		GenesisUtils.assertTrue(!_primitive);
		GenesisUtils.assertTrue(!_void);

		_type = pType;
	}
}
