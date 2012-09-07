package com.etsy.pushbot.command;

import com.etsy.pushbot.*;

import java.net.InetAddress;

public class ConfigCommand
  extends TrainCommand {

  public ConfigCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {

      try {
          InetAddress address = InetAddress.getLocalHost();
          String hostName = address.getHostName();
          String url = "http://" + hostName + ":8080/#/" + sender;
          pushBot.sendMessage(channel, url);
      }
      catch(Throwable t) {
          t.printStackTrace();
      }
  }

  @Override
  public String toString() {
      return "config";
  }
}
