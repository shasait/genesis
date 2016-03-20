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

package testapp;

import java.util.Date;
import java.util.List;

import de.hasait.genesis.scriptgen.ScriptGen;

/**
 *
 */
@ScriptGen(script = "metaData.js")
public class Class1 {

	private int intg, ints, intgs;

	private Integer integerg, integers, integergs;

	private boolean booleang, booleans, booleangs;

	private String stringg, strings, stringgs;

	private List<Date> listg, lists, listgs;

	private int[] intag, intas, intags;

	private Date[] dateag, dateas, dateags;

	public Date[] getDateag() {
		return dateag;
	}

	public Date[] getDateags() {
		return dateags;
	}

	public int[] getIntag() {
		return intag;
	}

	public int[] getIntags() {
		return intags;
	}

	public Integer getIntegerg() {
		return integerg;
	}

	public Integer getIntegergs() {
		return integergs;
	}

	public int getIntg() {
		return intg;
	}

	public int getIntgs() {
		return intgs;
	}

	public List<Date> getListg() {
		return listg;
	}

	public List<Date> getListgs() {
		return listgs;
	}

	public String getStringg() {
		return stringg;
	}

	public String getStringgs() {
		return stringgs;
	}

	public boolean isBooleang() {
		return booleang;
	}

	public boolean isBooleangs() {
		return booleangs;
	}

	public void setBooleangs(boolean pBooleangs) {
		booleangs = pBooleangs;
	}

	public void setBooleans(boolean pBooleans) {
		booleans = pBooleans;
	}

	public void setDateags(Date[] pDateags) {
		dateags = pDateags;
	}

	public void setDateas(Date[] pDateas) {
		dateas = pDateas;
	}

	public void setIntags(int[] pIntags) {
		intags = pIntags;
	}

	public void setIntas(int[] pIntas) {
		intas = pIntas;
	}

	public void setIntegergs(Integer pIntegergs) {
		integergs = pIntegergs;
	}

	public void setIntegers(Integer pIntegers) {
		integers = pIntegers;
	}

	public void setIntgs(int pIntgs) {
		intgs = pIntgs;
	}

	public void setInts(int pInts) {
		ints = pInts;
	}

	public void setListgs(List<Date> pListgs) {
		listgs = pListgs;
	}

	public void setLists(List<Date> pLists) {
		lists = pLists;
	}

	public void setStringgs(String pStringgs) {
		stringgs = pStringgs;
	}

	public void setStrings(String pStrings) {
		strings = pStrings;
	}

}
