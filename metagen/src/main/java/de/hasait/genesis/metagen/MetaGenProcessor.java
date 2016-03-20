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

package de.hasait.genesis.metagen;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import de.hasait.genesis.base.AbstractGenesisProcessor;
import de.hasait.genesis.base.GeneratorEnv;
import de.hasait.genesis.base.model.JClass;
import de.hasait.genesis.base.model.JCustomExpression;
import de.hasait.genesis.base.model.JField;
import de.hasait.genesis.base.model.JModel;
import de.hasait.genesis.base.model.JTypeArgument;
import de.hasait.genesis.base.model.JTypeReference;
import de.hasait.genesis.base.model.JTypeUsage;
import de.hasait.genesis.base.model.JVisibility;
import de.hasait.genesis.base.util.GenesisUtils;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MetaGenProcessor extends AbstractGenesisProcessor {

	public MetaGenProcessor() {
		super();
	}

	@Override
	public synchronized void init(final ProcessingEnvironment pProcessingEnv) {
		super.init(pProcessingEnv);

		registerGenerator(MetaGen.class, this::processMetaGen);
	}

	private void processMetaGen(final MetaGen pMetaGenAnnotation, final GeneratorEnv pGeneratorEnv) throws Exception {
		final TypeElement typeElement = (TypeElement) pGeneratorEnv.getAnnotatedElement();

		final JModel model = pGeneratorEnv.getModel();

		final JClass mdClass = pGeneratorEnv.createRelativeClass("_MD");

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
}
