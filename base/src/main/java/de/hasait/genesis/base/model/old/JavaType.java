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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.hasait.genesis.base.GeneratorEnv;
import de.hasait.genesis.base.util.GenesisUtils;
import de.hasait.genesis.base.util.JavaContentBuffer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @deprecated Use {@link GeneratorEnv#getModel}.
 */
@Deprecated
public class JavaType {

	private static final String USER_DEFINED_CUSTOM_CODE = "### USER DEFINED CUSTOM CODE ###"; //$NON-NLS-1$

	private static List<String> findUserDefinedCustomCodeBlocks(final File pFile) throws IOException {
		final List<String> result = new ArrayList<String>();

		final String content = GenesisUtils.readFileToString(pFile);
		if (content != null) {
			final String newline = content.contains("\r\n") ? "\r\n" : "\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			final String[] lines = content.split(newline);
			Integer startLine = null;
			for (int lineI = 0; lineI < lines.length; lineI++) {
				final String line = lines[lineI];
				if (startLine == null) {
					if (line.contains(USER_DEFINED_CUSTOM_CODE)) {
						startLine = lineI - 1;
					}
				} else {
					if (line.equals("\t}")) { //$NON-NLS-1$
						final StringBuilder sb = new StringBuilder(lines[startLine]);
						for (int blockLineI = startLine + 1; blockLineI <= lineI; blockLineI++) {
							sb.append(newline);
							sb.append(lines[blockLineI]);
						}
						result.add(sb.toString());
						startLine = null;
					}
				}
			}
		}

		return result;
	}

	private final String _packageName;
	private final String _name;
	private final Set<String> _imports = new TreeSet<String>();
	private final Set<String> _implements = new TreeSet<String>();
	private final List<Property> _properties = new ArrayList<Property>();
	private final List<JavaContentBuffer> _constructors = new ArrayList<JavaContentBuffer>();
	private final List<JavaContentBuffer> _appends = new ArrayList<JavaContentBuffer>();
	private boolean _abstract;
	private String _superName;
	private JavaContentBuffer _linesBefore = null;

	public JavaType(final String pPackageName, final String pName) {
		super();

		_packageName = pPackageName;
		_name = pName;
	}

	public void addImplements(final String pExpression) {
		_implements.add(pExpression);
	}

	public void addImport(final String pExpression) {
		_imports.add(pExpression);
	}

	public void addImports(final Iterable<String> pExpressions) {
		for (final String expression : pExpressions) {
			addImport(expression);
		}
	}

	public void addProperty(final Property pProperty) {
		_properties.add(pProperty);
	}

	public JavaContentBuffer appendConstructor() {
		final JavaContentBuffer buffer = new JavaContentBuffer();
		_constructors.add(buffer);
		return buffer;
	}

	public JavaContentBuffer appendNext() {
		final JavaContentBuffer buffer = new JavaContentBuffer();
		_appends.add(buffer);
		return buffer;
	}

	public Set<String> getImports() {
		return Collections.unmodifiableSet(_imports);
	}

	public String getName() {
		return _name;
	}

	public String getPackageName() {
		return _packageName;
	}

	public List<Property> getProperties() {
		return Collections.unmodifiableList(_properties);
	}

	public String getSuperName() {
		return _superName;
	}

	public boolean isAbstract() {
		return _abstract;
	}

	public JavaContentBuffer linesBefore() {
		if (_linesBefore == null) {
			_linesBefore = new JavaContentBuffer();
		}
		return _linesBefore;
	}

	public void setAbstract(final boolean pAbstract) {
		_abstract = pAbstract;
	}

	public void setSuperName(final String pSuperName) {
		_superName = pSuperName;
	}

	public File writeToFilesystem(final File pPackageFolder) throws Exception {
		final String javaFileName = _name + ".java"; //$NON-NLS-1$
		final File javaFile = new File(pPackageFolder, javaFileName);

		final List<String> userDefinedCustomCodeBlocks = findUserDefinedCustomCodeBlocks(javaFile);

		final String content = createContent(userDefinedCustomCodeBlocks);

		try {
			GenesisUtils.writeIfNonWhitespaceChanged(content, javaFile);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		return javaFile;
	}

	public void writeToGeneratorEnv(final GeneratorEnv pGeneratorEnv) throws Exception {
		final String qualifiedName = StringUtils.isEmpty(_packageName) ? _name : _packageName + "." + _name;
		PrintWriter pw = pGeneratorEnv.createJavaSrcFile(qualifiedName);
		try {
			pw.print(createContent(null));
		} finally {
			IOUtils.closeQuietly(pw);
		}
	}

	private String createContent(final List<String> pUserDefinedCustomCodeBlocks) {
		final JavaContentBuffer cb = new JavaContentBuffer();
		cb.a(JavaContentBuffer.TOKEN_PACKAGE).aSPACE().a(_packageName).pSC();
		cb.p();
		if (!_imports.isEmpty()) {
			for (final String importName : _imports) {
				cb.a(JavaContentBuffer.TOKEN_IMPORT).aSPACE().a(importName).pSC();
			}
			cb.p();
		}

		cb.javaDocStart();
		cb.p("GENERATED CODE!"); //$NON-NLS-1$
		cb.javaDocEnd();
		if (_linesBefore != null) {
			cb.p(_linesBefore.getContent());
		}
		cb.a(JavaContentBuffer.TOKEN_PUBLIC).aSPACE();
		if (_abstract) {
			cb.a(JavaContentBuffer.TOKEN_ABSTRACT).aSPACE();
		}
		cb.a(JavaContentBuffer.TOKEN_CLASS).aSPACE().a(_name).aSPACE();
		if (_superName != null) {
			cb.a(JavaContentBuffer.TOKEN_EXTENDS).aSPACE().a(_superName).aSPACE();
		}
		if (!_implements.isEmpty()) {
			cb.a(JavaContentBuffer.TOKEN_IMPLEMENTS).aSPACE();
			for (final String implementsName : _implements) {
				cb.a(implementsName).aSPACE();
			}
		}
		cb.pi("{"); //$NON-NLS-1$
		cb.p();

		for (final Property property : _properties) {
			if (property.isFieldEnabled()) {
				property.writeField(cb);
			}
		}

		for (final JavaContentBuffer append : _constructors) {
			cb.p(append.getContent());
			cb.p();
		}

		for (final Property property : _properties) {
			if (property.isAccessorsEnabled()) {
				property.writeAccessors(cb);
			}
		}

		for (final JavaContentBuffer append : _appends) {
			cb.p(append.getContent());
			cb.p();
		}

		if (pUserDefinedCustomCodeBlocks != null) {
			for (final String userDefinedCustomCodeBlock : pUserDefinedCustomCodeBlocks) {
				cb.pni(userDefinedCustomCodeBlock);
			}
		}

		cb.blockEnd();

		return cb.getContent();
	}

}
