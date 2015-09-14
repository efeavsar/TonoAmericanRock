package com.lonict.android.tonoamericanrock;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.lonict.android.tonoamericanrock.RadioStationPOJO;

/**
 * Created by Efe Avsar on 10.2.2015.
 */
public class RadioStation {

    List<RadioStationPOJO> radioStationPOJOs = new ArrayList<RadioStationPOJO>();

    public RadioStation(InputStream inputStream)
    {
        radioDataParser(inputStream);
    }
    public RadioStation(String json)
    {
        radioDataParser(json);
    }

    private void radioDataParser(String json)
    {
        try
        {

        JSONObject jsonObject = new JSONObject(json);
        JSONArray  jsonArray = jsonObject.getJSONArray("radiostations") ;
        //only one row from radiostations
            for (int i=0 ; i<jsonArray.length();i++)
            {
                JSONObject JsonObject = jsonArray.getJSONObject(i);
                RadioStationPOJO radistation = new RadioStationPOJO();
                radistation.setRadioname(JsonObject.getString("name"));
                radistation.setRadioURL(JsonObject.getString("url"));
                radistation.setRadiodesc(JsonObject.getString("description"));
                radistation.setImageUri(JsonObject.getString("image_uri"));
                radioStationPOJOs.add(radistation);
            }
        }
        catch (JSONException ex)
        {
            Log.e("ERROR",ex.toString());
        }
    }
    private void radioDataParser(InputStream inputStream)
    {
        try
        {
            String st = getFileContent(inputStream).toString();
            Log.d("XXJson",st);
            JSONObject jsonObject = new JSONObject(st);
            JSONArray  jsonArray = jsonObject.getJSONArray("radiostations") ;
            //only one row from radiostations
            for (int i=0 ; i<jsonArray.length();i++)
            {
                JSONObject JsonObject = jsonArray.getJSONObject(i);
                RadioStationPOJO radistation = new RadioStationPOJO();
                radistation.setRadioname(JsonObject.getString("name"));
                radistation.setRadioURL(JsonObject.getString("url"));
                radistation.setRadiodesc(JsonObject.getString("description"));
                radistation.setImageUri(JsonObject.getString("image_uri"));
                radioStationPOJOs.add(radistation);
            }
        }
        catch (FileNotFoundException ex)
        {
            Log.e("ERROR",ex.toString());
        }
        catch (IOException ex)
        {
            Log.e("ERROR",ex.toString());
        }
        catch (JSONException ex)
        {
            Log.e("ERROR",ex.toString());
        }
    }
    private StringBuilder getFileContent( InputStream fis ) throws UnsupportedEncodingException,IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        return out;
    }
    public List<com.lonict.android.tonoamericanrock.RadioStationPOJO> getRadioStations()
    {
        return radioStationPOJOs;
    }

}
