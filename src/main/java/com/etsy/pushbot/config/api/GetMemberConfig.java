package com.etsy.pushbot.config.api;

import com.etsy.pushbot.config.ConfigDao;
import com.etsy.pushbot.config.Config;
import com.etsy.pushbot.config.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


@Path("/member/{member}")
public class GetMemberConfig {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("member") String member) {
        try {
            Config config = ConfigDao.getInstance().getConfigForMember(member);
            return new Response<Config>(config).toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
            return new Response<String>(1, t.getMessage()).toString();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String set(@PathParam("member") String member, String configJson) {
        Config config = Config.fromString(configJson);

        try {
            ConfigDao.getInstance().setConfigForMember(member, config);
            return new Response<String>(0,"success").toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
            return new Response<String>(1, t.getMessage()).toString();
        }
    }


}
