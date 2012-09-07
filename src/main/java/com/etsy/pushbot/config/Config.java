package com.etsy.pushbot.config;

import java.io.IOException;
import java.util.Collection;
import org.codehaus.jackson.map.ObjectMapper;

public class Config {

    private static final ObjectMapper mapper 
        = new ObjectMapper();

    public Boolean quietDrive = false;
    public Boolean sendNotifoWhenUp = false;
    public String notifoUsername = "";
    public String notifoApiSecret = "";

    public Config() {}

    public Boolean isQuietDrive() { return quietDrive; }

    public String toString()
    {
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException e) {
            System.err.println("IO errors are not possible here");
            System.err.println(e.getMessage());
        }

        return "";
    }

    public static Config fromString(String json) {
        try {
            return mapper.readValue(json, Config.class);
        } catch (IOException e) {
            System.err.println(json);
            e.printStackTrace();
        }
        return null;
    }

}
