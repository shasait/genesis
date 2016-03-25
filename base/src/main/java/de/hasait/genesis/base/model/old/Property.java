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

package de.hasait.genesis.base.model.old;

import java.util.ArrayList;
import java.util.List;

import de.hasait.genesis.base.GeneratorEnv;
import de.hasait.genesis.base.util.JavaContentBuffer;
import org.apache.commons.lang3.StringUtils;

/**
 * @deprecated Use {@link GeneratorEnv#getModel}.
 */
@Deprecated
public class Property {

	public static Property create(final String pType, final Multiplicity pMultiplicity, final String pProperty, final boolean pRequired) {
		final String fieldName;
		final String propertyFLUCSingle = StringUtils.capitalize(pProperty);
		final String propertyFLUCAuto;

		if (pMultiplicity.isSingular()) {
			propertyFLUCAuto = propertyFLUCSingle;
			fieldName = "_" + pProperty; //$NON-NLS-1$
		} else {
			propertyFLUCAuto = propertyFLUCSingle + "s"; //$NON-NLS-1$
			fieldName = "_" + pProperty + "s"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return new Property(pType, pMultiplicity, fieldName, propertyFLUCAuto, propertyFLUCSingle, pRequired);
	}

	public static Property create(final String pType, final Multiplicity pMultiplicity, final String pFieldName, final String pPropertyFLUCAuto, final String pPropertyFLUCSingle, final boolean pRequired) {
		return new Property(pType, pMultiplicity, pFieldName, pPropertyFLUCAuto, pPropertyFLUCSingle, pRequired);
	}

	private static String determineFieldInitializingExpression(final Multiplicity pMultiplicity, final String pType) {
		switch (pMultiplicity) {
			case MANY_SET:
				return "new" + JavaContentBuffer.TOKEN_SPACE + "HashSet<" + pType + ">()"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			case MANY_LIST:
				return "new" + JavaContentBuffer.TOKEN_SPACE + "ArrayList<" + pType + ">()"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			default:
				break;
		}
		return null;
	}

	private static String determineFullType(final Multiplicity pMultiplicity, final String pType) {
		switch (pMultiplicity) {
			case ONE:
				return pType;
			case MANY_SET:
				return "Set<" + pType + ">"; //$NON-NLS-1$ //$NON-NLS-2$
			case MANY_LIST:
				return "List<" + pType + ">"; //$NON-NLS-1$ //$NON-NLS-2$
			default:
				throw new RuntimeException(pMultiplicity + " unsupported"); //$NON-NLS-1$
		}
	}

	private final String _type;
	private final Multiplicity _multiplicity;
	private final String _fieldName;
	private final String _propertyFLUCAuto;
	private final String _propertyFLUCSingle;
	private final boolean _required;
	private final List<String> _linesBeforeField = new ArrayList<String>();
	private boolean _fieldEnabled = true;
	private boolean _accessorsEnabled = true;
	private Property _otherRelationSide;

	private Property(final String pType, final Multiplicity pMultiplicity, final String pFieldName, final String pPropertyFLUCAuto, final String pPropertyFLUCSingle, final boolean pRequired) {
		super();

		_type = pType;
		_multiplicity = pMultiplicity;
		_fieldName = pFieldName;
		_propertyFLUCAuto = pPropertyFLUCAuto;
		_propertyFLUCSingle = pPropertyFLUCSingle;
		_required = pRequired;
	}

	public void addLineBeforeField(final String pLine) {
		_linesBeforeField.add(pLine);
	}

	public void clearLinesBeforeField() {
		_linesBeforeField.clear();
	}

	public String getFieldName() {
		return _fieldName;
	}

	public Multiplicity getMultiplicity() {
		return _multiplicity;
	}

	public String getPropertyFLUCAuto() {
		return _propertyFLUCAuto;
	}

	public String getPropertyFLUCSingle() {
		return _propertyFLUCSingle;
	}

	public String getType() {
		return _type;
	}

	public boolean isAccessorsEnabled() {
		return _accessorsEnabled;
	}

	public boolean isFieldEnabled() {
		return _fieldEnabled;
	}

	public boolean isRequired() {
		return _required;
	}

	public void setAccessorsEnabled(final boolean pWriteAccessorsEnabled) {
		_accessorsEnabled = pWriteAccessorsEnabled;
	}

	public void setFieldEnabled(final boolean pWriteFieldEnabled) {
		_fieldEnabled = pWriteFieldEnabled;
	}

	public void setOtherRelationSide(final Property pOtherRelationSide) {
		_otherRelationSide = pOtherRelationSide;
	}

	public void writeAccessors(final JavaContentBuffer pJavaContentBuffer) {
		final JavaContentBuffer cb = pJavaContentBuffer;

		final String fullType = determineFullType(_multiplicity, _type);
		final String parameterName = "p" + _propertyFLUCSingle; //$NON-NLS-1$

		cb.a(JavaContentBuffer.TOKEN_PUBLIC).aSPACE().a(fullType).aSPACE().a("get").a(_propertyFLUCAuto) //$NON-NLS-1$
		  .a("(").a(")").aSPACE().blockStart(); //$NON-NLS-1$ //$NON-NLS-2$
		cb.a("return").aSPACE().a(_fieldName).pSC(); //$NON-NLS-1$
		cb.blockEnd();
		cb.p();

		if (_multiplicity.isSingular()) {
			cb.aPUBLIC().aSPACE().aVOID().aSPACE().a("set").a(_propertyFLUCSingle) //$NON-NLS-1$
			  .a("(").a(fullType).aSPACE().a(parameterName).a(")").aSPACE().blockStart(); //$NON-NLS-1$ //$NON-NLS-2$
			if (_otherRelationSide != null && _otherRelationSide._accessorsEnabled) {
				// TODO exit both side loop if unchanged
				cb.ifIsBlockStart(_fieldName, parameterName);
				cb.returnStatement(null);
				cb.blockEnd();
				cb.ifIsNotBlockStart(_fieldName, JavaContentBuffer.TOKEN_NULL);
				final String oldValueVariableName = "old" + _propertyFLUCSingle; //$NON-NLS-1$
				cb.declareLocalVariable(true, fullType, oldValueVariableName);
				cb.assignValue(oldValueVariableName, _fieldName);
				cb.assignValue(_fieldName, JavaContentBuffer.TOKEN_NULL);
				_otherRelationSide.writeDestroyRelationCall(cb, oldValueVariableName, JavaContentBuffer.TOKEN_THIS);
				cb.blockEnd();
			}
			cb.a(_fieldName).aSPACE().a("=").aSPACE().a(parameterName).pSC(); //$NON-NLS-1$
			if (_otherRelationSide != null && _otherRelationSide._accessorsEnabled) {
				cb.ifIsNotBlockStart(_fieldName, JavaContentBuffer.TOKEN_NULL);
				_otherRelationSide.writeCreateRelationCall(cb, parameterName, JavaContentBuffer.TOKEN_THIS);
				cb.blockEnd();
			}
			cb.blockEnd();
			cb.p();
		} else {
			cb.aPUBLIC().aSPACE().aVOID().aSPACE().a("clear").a(_propertyFLUCAuto) //$NON-NLS-1$
			  .a("(").a(")").aSPACE().blockStart(); //$NON-NLS-1$ //$NON-NLS-2$
			cb.a(_fieldName).a(".").a("clear").a("(").a(")").pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			cb.blockEnd();
			cb.p();
			cb.a(JavaContentBuffer.TOKEN_PUBLIC).aSPACE().aVOID().aSPACE().a("add").a(_propertyFLUCSingle) //$NON-NLS-1$
			  .a("(").a(_type).aSPACE().a(parameterName).a(")").aSPACE().blockStart(); //$NON-NLS-1$ //$NON-NLS-2$
			if (_otherRelationSide != null && _otherRelationSide._accessorsEnabled) {
				cb.ifBlockStart(_fieldName, ".contains(", parameterName, ")"); //$NON-NLS-1$ //$NON-NLS-2$
				cb.returnStatement(null);
				cb.blockEnd();
			}
			cb.a(_fieldName).a(".").a("add").a("(").a(parameterName).a(")").pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			if (_otherRelationSide != null && _otherRelationSide._accessorsEnabled) {
				_otherRelationSide.writeCreateRelationCall(cb, parameterName, JavaContentBuffer.TOKEN_THIS);
			}
			cb.blockEnd();
			cb.p();
			cb.a(JavaContentBuffer.TOKEN_PUBLIC).aSPACE().aVOID().aSPACE().a("remove").a(_propertyFLUCSingle) //$NON-NLS-1$
			  .a("(").a(_type).aSPACE().a(parameterName).a(")").aSPACE().blockStart(); //$NON-NLS-1$ //$NON-NLS-2$
			if (_otherRelationSide != null && _otherRelationSide._accessorsEnabled) {
				cb.ifBlockStart("!", _fieldName, ".contains(", parameterName, ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				cb.returnStatement(null);
				cb.blockEnd();
			}
			cb.a(_fieldName).a(".").a("remove").a("(").a(parameterName).a(")")
			  .pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			if (_otherRelationSide != null && _otherRelationSide._accessorsEnabled) {
				_otherRelationSide.writeDestroyRelationCall(cb, parameterName, JavaContentBuffer.TOKEN_THIS);
			}
			cb.blockEnd();
			cb.p();
		}
	}

	public void writeCreateRelationCall(final JavaContentBuffer pJavaContentBuffer, final String pField, final String pReference) {
		final JavaContentBuffer cb = pJavaContentBuffer;

		if (_multiplicity.isSingular()) {
			cb.a(pField).a(".").a("set").a(_propertyFLUCSingle).a("(").a(pReference).a(")")
			  .pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		} else {
			cb.a(pField).a(".").a("add").a(_propertyFLUCSingle).a("(").a(pReference).a(")")
			  .pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	public void writeDestroyRelationCall(final JavaContentBuffer pJavaContentBuffer, final String pField, final String pReference) {
		final JavaContentBuffer cb = pJavaContentBuffer;

		if (_multiplicity.isSingular()) {
			cb.a(pField).a(".").a("set").a(_propertyFLUCSingle).a("(").a("null").a(")")
			  .pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		} else {
			cb.a(pField).a(".").a("remove").a(_propertyFLUCSingle).a("(").a(pReference).a(")")
			  .pSC(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	public void writeField(final JavaContentBuffer pJavaContentBuffer) {
		final JavaContentBuffer cb = pJavaContentBuffer;

		for (final String line : _linesBeforeField) {
			cb.a(line).p();
		}

		cb.a(JavaContentBuffer.TOKEN_PRIVATE).aSPACE();

		final String fullType = determineFullType(_multiplicity, _type);
		cb.a(fullType);
		cb.aSPACE();
		cb.a(_fieldName);

		final String fieldInitializingExpression = determineFieldInitializingExpression(_multiplicity, _type);
		if (fieldInitializingExpression != null) {
			cb.aSPACE().a("=").aSPACE().a(fieldInitializingExpression); //$NON-NLS-1$
		}

		cb.pSC();
		cb.p();
	}

}
