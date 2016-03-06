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

package de.hasait.genesis.processor.generator;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import de.hasait.genesis.processor.GeneratorEnv;
import de.hasait.genesis.processor.model.JClass;
import de.hasait.genesis.processor.model.JCustomExpression;
import de.hasait.genesis.processor.model.JField;
import de.hasait.genesis.processor.model.JModel;
import de.hasait.genesis.processor.model.JTypeArgument;
import de.hasait.genesis.processor.model.JTypeReference;
import de.hasait.genesis.processor.model.JTypeUsage;
import de.hasait.genesis.processor.model.JVisibility;
import de.hasait.genesis.processor.util.GenesisUtils;

/**
 *
 */
public final class GenerateMetaData {

	public static void genesis(final GeneratorEnv pGeneratorEnv) throws Exception {
		final TypeElement typeElement = pGeneratorEnv.getTypeElement();

		final JModel model = pGeneratorEnv.getModel();

		final JClass mdClass = model.createRelativeClass("_M");

		final JTypeReference javaLangStringTR = model.createOrGetTypeReference(String.class);
		final JTypeUsage javaLangStringTU = javaLangStringTR.createUsage();

		final JTypeReference javaLangClassTR = model.createOrGetTypeReference(Class.class);
		final JTypeUsage javaLangClassAnyTU = javaLangClassTR.createUsage(JTypeArgument.createAny());

		final JField sourceNameField = mdClass.addField(javaLangStringTU, "SOURCE__QUALIFIED_NAME");
		sourceNameField.setVisibility(JVisibility.PUBLIC);
		sourceNameField.setStatic(true);
		sourceNameField.setFinal(true);
		sourceNameField.setInitializer(new JCustomExpression("\"" + typeElement.getQualifiedName() + "\""));

		final String sourceTypeJavaSrc = pGeneratorEnv.typeMirrorToJavaSrc(typeElement.asType());

		final JField sourceTypeField = mdClass.addField(javaLangClassAnyTU, "SOURCE__TYPE");
		sourceTypeField.setVisibility(JVisibility.PUBLIC);
		sourceTypeField.setStatic(true);
		sourceTypeField.setFinal(true);
		sourceTypeField.setInitializer(new JCustomExpression(sourceTypeJavaSrc));

		final Set<String> processedPropertyNames = new HashSet<>();

		for (final Element subElement : typeElement.getEnclosedElements()) {
			final String propertyName = GenesisUtils.determinePropertyNameFromAccessor(subElement);
			if (propertyName != null && !processedPropertyNames.contains(propertyName)) {
				processedPropertyNames.add(propertyName);
				final String propertyNameUU = GenesisUtils.camelCaseToUpperUnderscore(propertyName);

				final TypeMirror propertyTM = GenesisUtils.determinePropertyTypeFromAccessor(subElement);
				final String propertyTypeJavaSrc = pGeneratorEnv.typeMirrorToJavaSrc(propertyTM);

				final JField propertyNameField = mdClass.addField(javaLangStringTU, "PROPERTY__" + propertyNameUU + "__NAME");
				propertyNameField.setVisibility(JVisibility.PUBLIC);
				propertyNameField.setStatic(true);
				propertyNameField.setFinal(true);
				propertyNameField.setInitializer(new JCustomExpression("\"" + propertyName + "\""));

				final JField propertyTypeField = mdClass.addField(javaLangClassAnyTU, "PROPERTY__" + propertyNameUU + "__TYPE");
				propertyTypeField.setVisibility(JVisibility.PUBLIC);
				propertyTypeField.setStatic(true);
				propertyTypeField.setFinal(true);
				propertyTypeField.setInitializer(new JCustomExpression(propertyTypeJavaSrc));
			}
		}
	}

	private GenerateMetaData() {
		super();
	}

}
