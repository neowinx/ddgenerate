package py.gov.datos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Convierte un archivo en formato CSV al formato JSON.
 * 
 * <p>
 * Si el archivo es Clases.csv, ignora hasta encontrar una linea que empieza con
 * "Clases", en caso contrario omite hasta encontrar "propiedad json", luego
 * parsea una linea como la cabecera y el resto del documento como un objeto de
 * un array JSON.
 * </p>
 * 
 * @author Arturo Volpe
 *
 */
public class CsvToJsonConverter implements FileConverter {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private final static String FOLDER = "json/";

	@Override
	public List<File> convert(List<File> files, String path, Map<String, String> params) {

		File folder = new File(path + FOLDER);
		folder.mkdir();
		List<File> toRet = new ArrayList<>();

		try {
			for (File f : files) {
				if (f.getName().equals("Clases.csv")) {
					toRet.add(writeJson(getClassAsJson(f, params), new File(buildFileName(path, f))));
				} else {
					toRet.add(writeJson(getClassDataAsJson(f, params), new File(buildFileName(path, f))));
				}
			}

		} catch (IOException ioexception) {

		} catch (JSONException jsonException) {

		}
		return toRet;
	}

	/**
	 * Dado un path genera el nombre de la clase donde debería estar.
	 * 
	 * @param path
	 *            patch base, por ejemplo "out"
	 * @param f
	 *            archivo a convertir
	 * @return un string que es path + {@link #FOLDER} y el nombre base del
	 *         archivo con extensión json.
	 */
	protected String buildFileName(String path, File f) {
		return path + FOLDER + FilenameUtils.getBaseName(f.getName()) + ".json";
	}

	/**
	 * Escribe un {@link JSONArray} en un archivo.
	 * 
	 * @param array
	 *            json a escribir
	 * @param file
	 *            archivo donde escribir
	 * @return archivo escrito
	 * @throws IOException
	 *             si el archivo no existe o no hay permisos suficientes.
	 * @throws JSONException
	 *             si el array esta mal formado.
	 */
	protected File writeJson(JSONArray array, File file) throws IOException, JSONException {

		Files.write(file.toPath(), array.toString(4).getBytes());
		return file;
	}

	/**
	 * Omite un archivo CSV hasta encontrar clases y luego lo transforma a json
	 * utilizando la primera linea como propiedades.
	 */
	protected JSONArray getClassAsJson(File asList, Map<String, String> emptyMap) throws IOException, JSONException {

		return csvToJson(asList, "Clases");
	}

	/**
	 * Omite un archivo CSV hasta encontrar "propiedad en json" y luego lo
	 * transforma a json utilizando la primera linea como propiedades.
	 */
	protected JSONArray getClassDataAsJson(File f, Map<String, String> emptyMap) throws IOException, JSONException {

		return csvToJson(f, "propiedad en json");
	}

	private JSONArray csvToJson(File file, String flagToStart) throws IOException, JSONException {
		JSONArray toRet = new JSONArray();

		List<String> lines = Files.readAllLines(file.toPath(), Charsets.ISO_8859_1);
		List<String> header = null;

		boolean empezoArchivo = false;
		for (String s : lines) {

			if (s.trim().isEmpty())
				continue;

			if (!empezoArchivo) {
				if (s.startsWith(flagToStart)) {
					empezoArchivo = true;
					header = loadHeader(s);
				}
				continue;
			}

			toRet.add(parseAndAdd(header, s));

		}

		return toRet;
	}

	protected JSONObject parseAndAdd(List<String> header, String line) throws JSONException {

		List<String> elements = Arrays.asList(line.split(";"));

		JSONObject jo = new JSONObject();
		for (int i = 0; i < header.size(); i++) {
			if (elements.size() <= i) {
				LOG.warn("La linea {} contiene menos items ({}) que la cabecera ({}), i={}", line, elements.size(),
						header.size(), i);
				break;

			}
			jo.put(header.get(i), elements.get(i));
		}

		return jo;
	}

	protected List<String> loadHeader(String s) {

		return Arrays.asList(s.split(";"));
	}

}