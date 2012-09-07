package com.etsy.pushbot.config;

import java.io.IOException;
import java.util.Collection;
import org.codehaus.jackson.map.ObjectMapper;

public class Status {

    private static final ObjectMapper mapper
        = new ObjectMapper();

    public Boolean isHold = false;
    public Boolean isEveryoneReady = false;
    public String driver = "";
    public String head = "";
    public Integer memberCount = 0;
    public String headState = null;

    public Status() {}

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
}
