/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivelibrary.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.CustomField;

public class TextService {
    private String path;

    public TextService(String path) {
        this.path = path;
    }

    public List<String> getHeader() throws Exception {
        CSVReader reader = this.getReader();
        List<String[]> lines = reader.readAll();
        reader.close();

        if(lines != null) {
            if (lines.size() != 0) {
                return Arrays.asList(lines.get(0));
            }
        }
        return new LinkedList<>();
    }

    public List<Map<String, String>> readFile() throws Exception {
        List<Map<String, String>> items = new LinkedList<>();

        CSVReader reader = this.getReader();
        List<String[]> lines = reader.readAll();

        if(lines != null) {
            if(lines.size() != 0) {
                String[] header = lines.get(0);

                for(int row = 1; row <= lines.size()-1; row++) {
                    Map<String, String> mpRow = new LinkedHashMap<>();
                    for(int column = 0; column <= lines.get(row).length - 1; column++) {
                        mpRow.put(header[column], lines.get(row)[column]);
                    }
                    items.add(mpRow);
                }
            }
        }
        reader.close();
        return items;
    }

    public void writeFile(List<BaseMediaObject> objects) throws Exception {
        List<Map<String, String>> ls = new LinkedList<>();
        for(BaseMediaObject baseMediaObject : objects) {
            Map<String, String> mp = new LinkedHashMap<>();
            mp.put("title", baseMediaObject.getTitle());
            mp.put("originalTitle", baseMediaObject.getOriginalTitle());
            mp.put("price", String.valueOf(baseMediaObject.getPrice()));
            mp.put("code", baseMediaObject.getCode());
            if(baseMediaObject.getCategory() != null) {
                mp.put("category", baseMediaObject.getCategory().getTitle());
            }
            mp.put("description", baseMediaObject.getDescription());
            mp.put("ratingOwn", String.valueOf(baseMediaObject.getRatingOwn()));
            mp.put("ratingWeb", String.valueOf(baseMediaObject.getRatingWeb()));
            mp.put("ratingNote", baseMediaObject.getRatingNote());

            StringBuilder persons = new StringBuilder();
            for(Person person : baseMediaObject.getPersons()) {
                persons.append(person.getFirstName()).append(" ").append(person.getLastName()).append(", ");
            }
            mp.put("persons", persons.toString());

            StringBuilder companies = new StringBuilder();
            for(Company company : baseMediaObject.getCompanies()) {
                companies.append(company.getTitle()).append(", ");
            }
            mp.put("companies", companies.toString());

            StringBuilder customFields = new StringBuilder();
            for(Map.Entry<CustomField, String> customFieldValue : baseMediaObject.getCustomFieldValues().entrySet()) {
                customFields.append(String.format("%s: %s, ", customFieldValue.getKey().getTitle(), customFieldValue.getValue()));
            }
            mp.put("customFields", customFields.toString());
            ls.add(mp);
        }

        CSVWriter csvWriter = new CSVWriter(new FileWriter(this.path));
        csvWriter.writeNext(ls.get(0).keySet().toArray(new String[]{}));
        for(Map<String, String> entry : ls) {
            csvWriter.writeNext(entry.values().toArray(new String[]{}));
        }
        csvWriter.close();
    }

    private char getSplitter() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(this.path));
        String line = bufferedReader.readLine();

        char splitter = '\t';
        if(line.contains(";")) {
            splitter = ';';
        } else if(line.contains(",")) {
            splitter = ',';
        }
        bufferedReader.close();
        return splitter;
    }

    private CSVReader getReader() throws Exception {
        CSVParser parser = new CSVParserBuilder().withSeparator(this.getSplitter()).build();
        return new CSVReaderBuilder(new FileReader(this.path)).withCSVParser(parser).build();
    }
}
