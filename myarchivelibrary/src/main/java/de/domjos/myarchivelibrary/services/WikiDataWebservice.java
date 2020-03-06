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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;

public class WikiDataWebservice extends JSONService {
    private final static String DATA_TYPE = "datatype", VALUE = "value", DATA_VALUE = "datavalue", TIME = "time";

    private String id = "";
    private String companyName, firstName, lastName;
    private final static String DATE_FORMAT = "+yyyy-MM-dd'T'HH:mm:ss'Z'";
    private String claimURL = "https://www.wikidata.org/w/api.php?action=wbgetclaims&entity=%s&format=json&language=%s";
    private List personProps = Arrays.asList("P569", "P734", "P735", "P18");
    private List companyProps = Arrays.asList("P154", "P159", "P571", "P169");
    private final String LANGUAGE = Locale.getDefault().getLanguage().toLowerCase();

    public WikiDataWebservice(String companyName) throws JSONException, IOException {
        this.companyName = companyName;
        String searchURL = "https://www.wikidata.org/w/api.php?action=wbsearchentities&search=%s&format=json&language=%s";

        String url = String.format(searchURL, companyName.replace(" ", "%20"), LANGUAGE);
        String dataText = readUrl(new URL(url));
        JSONObject obj = new JSONObject(dataText);
        JSONArray jsonArray = obj.getJSONArray("search");
        if(jsonArray.length() != 0) {
            this.id = jsonArray.getJSONObject(0).getString("id");
        }
    }

    public WikiDataWebservice(String firstName, String lastName) throws JSONException, IOException {
        this(firstName + "+" + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person getPerson() throws IOException, JSONException, ParseException {
        Person person = new Person();
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        if(!this.id.isEmpty()) {
            String url = String.format(this.claimURL, this.id, LANGUAGE);
            String dataText = readUrl(new URL(url));
            JSONObject obj = new JSONObject(dataText);
            JSONObject claims = obj.getJSONObject("claims");

            for(Object objItem : this.personProps) {
                String param =  (String) objItem;
                if(claims.has(param)) {
                    JSONObject paramObj = claims.getJSONArray(param).getJSONObject(0);
                    JSONObject mainSnak = paramObj.getJSONObject("mainsnak");
                    if(mainSnak.getString(WikiDataWebservice.DATA_TYPE).equals("wikibase-item")) {
                        JSONObject valueObj = mainSnak.getJSONObject(WikiDataWebservice.DATA_VALUE).getJSONObject(WikiDataWebservice.VALUE);
                        String valID = valueObj.getString("id");
                        switch (param) {
                            case "P734":
                                person.setLastName(this.getValue(valID));
                                break;
                            case "P735":
                                person.setFirstName(this.getValue(valID));
                                break;
                            default:
                                // nothing to do here
                        }
                    } else if(mainSnak.getString(WikiDataWebservice.DATA_TYPE).equals(WikiDataWebservice.TIME)) {
                        JSONObject valueObj = mainSnak.getJSONObject(WikiDataWebservice.DATA_VALUE).getJSONObject(WikiDataWebservice.VALUE);
                        String valID = valueObj.getString(WikiDataWebservice.TIME);
                        person.setBirthDate(ConvertHelper.convertStringToDate(valID, WikiDataWebservice.DATE_FORMAT));
                    } else if(mainSnak.getString(WikiDataWebservice.DATA_TYPE).equals("commonsMedia")) {
                        String image = mainSnak.getJSONObject(WikiDataWebservice.DATA_VALUE).getString(WikiDataWebservice.VALUE);
                        person.setImage(this.getImage(image));
                    }
                }
                if(person.getLastName().isEmpty()) {
                    person.setLastName(this.lastName);
                }
                if(person.getFirstName().isEmpty()) {
                    person.setFirstName(this.firstName);
                }

                if(!person.getFirstName().trim().equals(this.firstName)) {
                    person.setFirstName(this.firstName);
                }
            }
        }
        return person;
    }

    public Company getCompany() throws IOException, JSONException, ParseException {
        Company company = new Company();
        company.setTitle(this.companyName);
        if(!this.id.isEmpty()) {
            String url = String.format(this.claimURL, this.id, LANGUAGE);
            String dataText = readUrl(new URL(url));
            JSONObject obj = new JSONObject(dataText);
            JSONObject claims = obj.getJSONObject("claims");

            for(Object objItem : this.companyProps) {
                String param = (String) objItem;
                if(claims.has(param)) {
                    JSONObject paramObj = claims.getJSONArray(param).getJSONObject(0);
                    JSONObject mainSnak = paramObj.getJSONObject("mainsnak");
                    if(mainSnak.getString(WikiDataWebservice.DATA_TYPE).equals(WikiDataWebservice.TIME)) {
                        JSONObject valueObj = mainSnak.getJSONObject(WikiDataWebservice.DATA_VALUE).getJSONObject(WikiDataWebservice.VALUE);
                        String valID = valueObj.getString(WikiDataWebservice.TIME);
                        company.setFoundation(ConvertHelper.convertStringToDate(valID, WikiDataWebservice.DATE_FORMAT));
                    } else if(mainSnak.getString(WikiDataWebservice.DATA_TYPE).equals("commonsMedia")) {
                        String image = mainSnak.getJSONObject(WikiDataWebservice.DATA_VALUE).getString(WikiDataWebservice.VALUE);
                        company.setCover(this.getImage(image));
                    }
                }
            }
        }
        return company;
    }

    private byte[] getImage(String name) throws IOException, JSONException {
        String url = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:" + name.replace(" ", "_");
        String dataText = readUrl(new URL(url));
        JSONObject obj = new JSONObject(dataText);
        JSONObject pages = obj.getJSONObject("query").getJSONObject("pages");
        Iterator<String> iterator = pages.keys();
        if(iterator.hasNext()) {
            JSONObject info = pages.getJSONObject(iterator.next()).getJSONArray("imageinfo").getJSONObject(0);
            String imgURL = info.getString("url");
            if(!imgURL.endsWith(".svg"))  {
                byte[] bytes = ConvertHelper.convertStringToByteArray(imgURL);
                double length = bytes.length / (1024.0 * 1024.0);
                if(length >= 1) {
                    return null;
                } else {
                    return bytes;
                }
            }
        }
        return null;
    }

    private String getValue(String id) throws IOException, JSONException {
        String url = String.format("https://www.wikidata.org/w/api.php?action=wbgetentities&ids=%s&languages=%s&format=json", id, LANGUAGE);
        String dataText = readUrl(new URL(url));
        JSONObject obj = new JSONObject(dataText);
        JSONObject idObj = obj.getJSONObject("entities").getJSONObject(id);
        return idObj.getJSONObject("labels").getJSONObject(LANGUAGE).getString(WikiDataWebservice.VALUE);
    }
}