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

package de.hasait.genesis.base.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.Nonnull;

import de.hasait.genesis.base.ModelWriter;
import de.hasait.genesis.base.ModelWriterEnv;
import de.hasait.genesis.base.model.AbstractJType;
import de.hasait.genesis.base.model.JModel;
import de.hasait.genesis.base.util.GenesisUtils;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class FreemarkerModelWriter implements ModelWriter {

	static void write(final Configuration pConfiguration, final Writer pWriter, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		write(pConfiguration, pWriter, pModel.getClass().getSimpleName() + ".ftl", pModel, pParams);
	}

	static void write(final Configuration pConfiguration, final Writer pWriter, final String pTemplateName, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		final Template template = pConfiguration.getTemplate(pTemplateName);
		template.process(new Context(pModel, pParams), pWriter);
	}

	private final Configuration _configuration;

	public FreemarkerModelWriter() {
		super();

		_configuration = new Configuration(Configuration.VERSION_2_3_23);
		_configuration.setDefaultEncoding("UTF-8");
		_configuration.setSharedVariable("delegate", new DelegateDirective());
		final String ftlPath = "/" + StringUtils.replaceChars(ModelWriter.class.getPackage().getName(), '.', '/') + "/ftl/";
		final ClassTemplateLoader templateLoader = new ClassTemplateLoader(getClass(), ftlPath);
		_configuration.setTemplateLoader(new HeaderFilterTemplateLoader(templateLoader));
	}

	@Override
	public void write(@Nonnull final JModel pModel, @Nonnull final ModelWriterEnv pEnv) throws Exception {
		GenesisUtils.assertNotNull(pModel);
		GenesisUtils.assertNotNull(pEnv);

		for (final AbstractJType<?> jType : pModel.getCreatedTypes()) {
			final Writer writer = pEnv.createJavaSrcFile(jType.getType().getType().getQualifiedName());
			try {
				write(_configuration, writer, jType, null);
			} finally {
				IOUtils.closeQuietly(writer);
			}
		}
	}

	public static class Context {

		private final Object _model;
		private final Map _params;

		private Context(final Object pModel, final Map pParams) {
			_model = pModel;
			_params = pParams;
		}

		public Object getModel() {
			return _model;
		}

		public Map getParams() {
			return _params;
		}
	}

}
