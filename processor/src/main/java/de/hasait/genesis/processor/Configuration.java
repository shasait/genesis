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

package de.hasait.genesis.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.lang.model.element.Element;

/**
 *
 */
public class Configuration {

	private final List<String> _locations = new ArrayList<>();

	private final Map<String, Consumer<Element>> _elementProcessors = new HashMap<>();

	private ModelWriter _modelWriter;

	public void addLocation(String pLocation) {
		_locations.add(pLocation);
	}

	public Consumer<Element> getElementProcessor(String pAnnotationQualifiedName) {
		return _elementProcessors.get(pAnnotationQualifiedName);
	}

	public List<String> getLocations() {
		return Collections.unmodifiableList(_locations);
	}

	public ModelWriter getModelWriter() {
		return _modelWriter;
	}

	public void registerElementProcessor(String pAnnotationQualifiedName, Consumer<Element> pElementProcessor) {
		_elementProcessors.put(pAnnotationQualifiedName, pElementProcessor);
	}

	public void setModelWriter(ModelWriter pModelWriter) {
		_modelWriter = pModelWriter;
	}

}
