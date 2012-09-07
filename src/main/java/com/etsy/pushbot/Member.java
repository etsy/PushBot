package com.etsy.pushbot;

import com.etsy.pushbot.config.Config;
import com.etsy.pushbot.config.ConfigDao;
import com.notifo.client.NotifoClient;
import com.notifo.client.NotifoHttpClient;
import com.notifo.client.NotifoMessage;

public class Member {

  private String name;
  private String type = null;
  private String status = null;

  public Member(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public boolean isDark() {
    return type != null && "dark".equals(type);
  }

  public boolean isConfig() {
    return type != null && "config".equals(type);
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return this.status;
  }

  public Config getConfig() {
      try {
          return ConfigDao.getInstance().getConfigForMember(this.getName());
      }
      catch(Throwable t) {
        System.err.println(t.getMessage());
        t.printStackTrace();
      }
      return new Config();
  }

  public void onHeadOfQueue(PushBot pushBot, PushTrain pushTrain, String channel) {
      if(getConfig().sendNotifoWhenUp) {
          String notifoUsername = getConfig().notifoUsername;
          String notifoApiSecret = getConfig().notifoApiSecret;

          try {
              NotifoClient client =
                  new NotifoHttpClient(notifoUsername,
                          notifoApiSecret);

              NotifoMessage message =
                  new NotifoMessage(notifoUsername,
                          "You're at the head of the push queue.");

              client.sendMessage(message);
          }
          catch(Throwable t) {
              System.err.println(t.getMessage());
          }
      }
  }

  @Override
  public String toString() {
    String s = name;
    if(status != null) {
      s += status;
    }
    if(type != null) {
      s += " (" + type + ")";
    }
    return s;
  }
}
