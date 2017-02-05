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

package de.hasait.genesis.scriptgen;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import de.hasait.genesis.base.util.GenesisUtils;

/**
 *
 */
public class GenesisUtilsTest {

	@Test
	public void testCamelCaseToUpperUnderscore() throws Exception {
		assertEquals("PROPERTY_NAME", GenesisUtils.camelCaseToUpperUnderscore("propertyName"));
		assertEquals("PROPERTY_NAME", GenesisUtils.camelCaseToUpperUnderscore("PropertyName"));
		assertEquals("PROPERTY", GenesisUtils.camelCaseToUpperUnderscore("property"));
		assertEquals("PROPERTY", GenesisUtils.camelCaseToUpperUnderscore("Property"));
		assertEquals("P", GenesisUtils.camelCaseToUpperUnderscore("p"));
		assertEquals("P", GenesisUtils.camelCaseToUpperUnderscore("P"));
		assertEquals("", GenesisUtils.camelCaseToUpperUnderscore(""));
		assertEquals("", GenesisUtils.camelCaseToUpperUnderscore(""));
		assertEquals(null, GenesisUtils.camelCaseToUpperUnderscore(null));
		assertEquals(null, GenesisUtils.camelCaseToUpperUnderscore(null));
	}

}