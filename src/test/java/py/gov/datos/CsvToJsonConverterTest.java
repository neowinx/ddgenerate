package py.gov.datos;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.wink.json4j.JSONArray;
import org.junit.Test;

/*
 * @author	Arturo Volpe
 * @copyright	2015 Governance and Democracy Program USAID-CEAMSO
 * @license 	http://www.gnu.org/licenses/gpl-2.0.html
 * 
 * USAID-CEAMSO
 * Copyright (C) 2014 Governance and Democracy Program
 * http://ceamso.org.py/es/proyectos/20-programa-de-democracia-y-gobernabilidad
 * 
 ----------------------------------------------------------------------------
 * This file is part of the Governance and Democracy Program USAID-CEAMSO,
 * is distributed as free software in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. You can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License version 2 as published by the 
 * Free Software Foundation, accessible from <http://www.gnu.org/licenses/> or write 
 * to Free Software Foundation (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, 
 * MA 02111-1301, USA.
 ---------------------------------------------------------------------------
 * Este archivo es parte del Programa de Democracia y Gobernabilidad USAID-CEAMSO,
 * es distribuido como software libre con la esperanza que sea de utilidad,
 * pero sin NINGUNA GARANTÍA; sin garantía alguna implícita de ADECUACION a cualquier
 * MERCADO o APLICACION EN PARTICULAR. Usted puede redistribuirlo y/o modificarlo 
 * bajo los términos de la GNU Lesser General Public Licence versión 2 de la Free 
 * Software Foundation, accesible en <http://www.gnu.org/licenses/> o escriba a la 
 * Free Software Foundation (FSF) Inc., 51 Franklin St, Fifth Floor, Boston, 
 * MA 02111-1301, USA.
 */
public class CsvToJsonConverterTest {

	CsvToJsonConverter converter = new CsvToJsonConverter();

	@Test
	public void testMakeIndex() throws Exception {

		File f = new File("src/test/resources/csv/Clases.csv");

		JSONArray result = converter.getClassAsJson(f, Collections.<String, String> emptyMap());

		assertEquals(18, result.size());

		assertEquals("Planificacion", result.getJSONObject(0).get("Clases"));
		assertEquals("Documentos", result.getJSONObject(17).get("Clases"));

	}

	@Test
	public void testMakeClass() throws Exception {

		File f = new File("src/test/resources/csv/Proveedor.csv");

		JSONArray result = converter.getClassDataAsJson(f, Collections.<String, String> emptyMap());

		assertEquals(5, result.size());
		assertEquals("id", result.getJSONObject(0).get("propiedad en json"));
		assertEquals("email", result.getJSONObject(4).get("propiedad en json"));
	}

	@Test
	public void testParseHeader() throws Exception {

		List<String> header = converter.loadHeader(
				"Clases;Label: Español;Label: Inglés;Descripción: Español;Descripción: Inglés;Clase equivalente");

		assertThat(header, hasItem("Clases"));
		assertThat(header, hasItem("Label: Español"));
		assertThat(header, hasItem("Label: Inglés"));
		assertThat(header, hasItem("Descripción: Español"));
		assertThat(header, hasItem("Descripción: Inglés"));
		assertThat(header, hasItem("Clase equivalente"));
	}

	@Test
	public void testCreateName() throws Exception {

		assertEquals("test/json/src.json", converter.buildFileName("test/", new File("src")));
	}
}
