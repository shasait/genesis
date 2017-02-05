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

package de.hasait.genesis.base.freemarker;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.Nonnull;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import de.hasait.genesis.base.ModelWriter;
import de.hasait.genesis.base.ModelWriterEnv;
import de.hasait.genesis.base.model.AbstractJType;
import de.hasait.genesis.base.model.JModel;
import de.hasait.genesis.base.model.SrcContext;
import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public class FreemarkerModelWriter implements ModelWriter {

	private static final ThreadLocal<Map<Class<?>, Template>> TEMPLATE_CACHE = new ThreadLocal<Map<Class<?>, Template>>();
	private static final ThreadLocal<SrcContext> SRC_CONTEXT = new ThreadLocal<SrcContext>();

	static void write(final Configuration pConfiguration, final Writer pWriter, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		final Map<Class<?>, Template> templateCache = TEMPLATE_CACHE.get();

		Class<?> currentType = pModel.getClass();
		Template template = templateCache.get(currentType);
		if (template == null) {
			final LinkedList<TypeNode> queue = new LinkedList<TypeNode>();
			queue.add(new TypeNode(null, currentType));

			TemplateNotFoundException firstE = null;

			do {
				// take first from queue
				TypeNode current = queue.removeFirst();
				currentType = current._type;

				// determine template
				template = templateCache.get(currentType);
				if (template == null) {
					final String templateName = currentType.getSimpleName() + ".ftl";
					try {
						template = pConfiguration.getTemplate(templateName);
					} catch (final TemplateNotFoundException e) {
						if (firstE == null) {
							firstE = e;
						}
					}
				}

				if (template != null) {
					// fill cache including parents
					templateCache.put(currentType, template);
					while (true) {
						templateCache.put(currentType, template);
						current = current._parent;
						if (current == null) {
							break;
						}
						currentType = current._type;
					}
				} else {
					// fill queue with next nodes
					for (final Class<?> interfaceType : currentType.getInterfaces()) {
						queue.add(new TypeNode(current, interfaceType));
					}
					final Class<?> superclassType = currentType.getSuperclass();
					if (superclassType != null) {
						queue.add(new TypeNode(current, superclassType));
					}
				}
			} while (template == null && !queue.isEmpty());

			if (template == null) {
				throw firstE;
			}
		}

		write(pConfiguration, pWriter, template, pModel, pParams);
	}

	static void write(final Configuration pConfiguration, final Writer pWriter, final Template pTemplate, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		pTemplate.process(new Context(pModel, pParams), pWriter);
	}

	static void write(final Configuration pConfiguration, final Writer pWriter, final String pTemplateName, final Object pModel, final Map pParams)
			throws IOException, TemplateException {
		final Template template = pConfiguration.getTemplate(pTemplateName);
		write(pConfiguration, pWriter, template, pModel, pParams);
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

		TEMPLATE_CACHE.set(new HashMap<Class<?>, Template>());
		try {
			for (final AbstractJType<?> jType : pModel.getCreatedTypes()) {
				SRC_CONTEXT.set(new SrcContext(jType));
				final Writer writer = pEnv.createJavaSrcFile(jType.getType().getType().getQualifiedName());
				try {
					write(_configuration, writer, jType, null);
				} finally {
					IOUtils.closeQuietly(writer);
				}
			}

		} finally {
			SRC_CONTEXT.remove();
			TEMPLATE_CACHE.remove();
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

		public SrcContext getSrcContext() {
			return SRC_CONTEXT.get();
		}
	}

	private static class TypeNode {
		public final TypeNode _parent;
		public final Class<?> _type;

		public TypeNode(final TypeNode pPrev, final Class<?> pThis) {
			_parent = pPrev;
			_type = pThis;
		}
	}

}
