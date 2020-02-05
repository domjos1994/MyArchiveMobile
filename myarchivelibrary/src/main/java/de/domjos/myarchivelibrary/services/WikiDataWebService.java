package de.domjos.myarchivelibrary.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;

public class WikiDataWebService extends JSONService {
    private String id = "";
    private String companyName, firstName, lastName;
    private final static String DATE_FORMAT = "+yyyy-MM-dd'T'HH:mm:ss'Z'";
    private String claimURL = "https://www.wikidata.org/w/api.php?action=wbgetclaims&entity=%s&format=json&language=%s";
    private List personProps = Arrays.asList("P569", "P19", "P1477", "P570", "P551", "P734", "P735", "P1340", "P18");
    private List companyProps = Arrays.asList("P154", "P159", "P571", "P169");

    public WikiDataWebService(String companyName) throws JSONException, IOException {
        this.companyName = companyName;
        String searchURL = "https://www.wikidata.org/w/api.php?action=wbsearchentities&search=%s&format=json&language=%s";

        String url = String.format(searchURL, companyName.replace(" ", "%20"), Locale.getDefault().getLanguage().toLowerCase());
        String dataText = readUrl(new URL(url));
        JSONObject obj = new JSONObject(dataText);
        JSONArray jsonArray = obj.getJSONArray("search");
        if(jsonArray.length() != 0) {
            this.id = jsonArray.getJSONObject(0).getString("id");
        }
    }

    public WikiDataWebService(String firstName, String lastName) throws JSONException, IOException {
        this(firstName + "+" + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person getPerson() throws Exception {
        Person person = new Person();
        person.setFirstName(this.firstName);
        person.setLastName(this.lastName);
        if(!this.id.isEmpty()) {
            String url = String.format(this.claimURL, this.id, Locale.getDefault().getCountry().toLowerCase());
            String dataText = readUrl(new URL(url));
            JSONObject obj = new JSONObject(dataText);
            JSONObject claims = obj.getJSONObject("claims");

            for(Object objItem : this.personProps) {
                String param =  (String) objItem;
                if(claims.has(param)) {
                    JSONObject paramObj = claims.getJSONArray(param).getJSONObject(0);
                    JSONObject mainSnak = paramObj.getJSONObject("mainsnak");
                    if(mainSnak.getString("datatype").equals("wikibase-item")) {
                        JSONObject valueObj = mainSnak.getJSONObject("datavalue").getJSONObject("value");
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
                    } else if(mainSnak.getString("datatype").equals("time")) {
                        JSONObject valueObj = mainSnak.getJSONObject("datavalue").getJSONObject("value");
                        String valID = valueObj.getString("time");
                        person.setBirthDate(Converter.convertStringToDate(valID, WikiDataWebService.DATE_FORMAT));
                    } else if(mainSnak.getString("datatype").equals("commonsMedia")) {
                        String image = mainSnak.getJSONObject("datavalue").getString("value");
                        person.setImage(this.getImage(image));
                    }
                }
                if(person.getLastName().isEmpty())
                    person.setLastName(this.lastName);
                if(person.getFirstName().isEmpty())
                    person.setFirstName(this.firstName);

                if(!person.getFirstName().trim().equals(this.firstName)) {
                    person.setFirstName(this.firstName);
                }
            }
        }
        return person;
    }

    public Company getCompany() throws Exception {
        Company company = new Company();
        company.setTitle(this.companyName);
        if(!this.id.isEmpty()) {
            String url = String.format(this.claimURL, this.id, Locale.getDefault().getCountry().toLowerCase());
            String dataText = readUrl(new URL(url));
            JSONObject obj = new JSONObject(dataText);
            JSONObject claims = obj.getJSONObject("claims");

            for(Object objItem : this.companyProps) {
                String param = (String) objItem;
                if(claims.has(param)) {
                    JSONObject paramObj = claims.getJSONArray(param).getJSONObject(0);
                    JSONObject mainSnak = paramObj.getJSONObject("mainsnak");
                    if(mainSnak.getString("datatype").equals("time")) {
                        JSONObject valueObj = mainSnak.getJSONObject("datavalue").getJSONObject("value");
                        String valID = valueObj.getString("time");
                        company.setFoundation(Converter.convertStringToDate(valID, WikiDataWebService.DATE_FORMAT));
                    } else if(mainSnak.getString("datatype").equals("commonsMedia")) {
                        String image = mainSnak.getJSONObject("datavalue").getString("value");
                        company.setCover(this.getImage(image));
                    }
                }
            }
        }
        return company;
    }

    private byte[] getImage(String name) throws Exception {
        String url = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&format=json&titles=File:" + name.replace(" ", "_");
        String dataText = readUrl(new URL(url));
        JSONObject obj = new JSONObject(dataText);
        JSONObject pages = obj.getJSONObject("query").getJSONObject("pages");
        Iterator<String> iterator = pages.keys();
        if(iterator.hasNext()) {
            JSONObject info = pages.getJSONObject(iterator.next()).getJSONArray("imageinfo").getJSONObject(0);
            String imgURL = info.getString("url");
            if(!imgURL.endsWith(".svg"))  {
                return Converter.convertStringToByteArray(imgURL);
            }
        }
        return null;
    }

    private String getValue(String id) throws Exception {
        String url = String.format("https://www.wikidata.org/w/api.php?action=wbgetentities&ids=%s&languages=%s&format=json", id, Locale.getDefault().getCountry().toLowerCase());
        String dataText = readUrl(new URL(url));
        JSONObject obj = new JSONObject(dataText);
        JSONObject idObj = obj.getJSONObject("entities").getJSONObject(id);
        return idObj.getJSONObject("labels").getJSONObject(Locale.getDefault().getCountry().toLowerCase()).getString("value");
    }
}