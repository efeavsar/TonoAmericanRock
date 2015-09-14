package com.lonict.android.tonoamericanrock;

/**
 * Created by Efe Avsar on 10.2.2015.
 */

public class RadioStationPOJO {

    protected String radioname  ;
    protected String radioURL ;
    protected String radiodesc ;
    protected String imageUri ;

    public String getRadioName()
    {
        return this.radioname;
    }
    public void setRadioname(String s)
    {
        this.radioname = s  ;
    }
    public String getRadioURL()
    {
        return this.radioURL;
    }
    public void setRadioURL(String s)
    {
        this.radioURL = s ;
    }
    public String getRadiodesc()
    {
         return this.radiodesc ;
    }
    public void setRadiodesc (String s)
    {
        this.radiodesc = s;
    }
    public String getImageUri()
    {
        return this.imageUri;
    }
    public void setImageUri(String s)
    {
        this.imageUri = s ;
    }

}
