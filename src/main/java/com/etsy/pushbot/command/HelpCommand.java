package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.ChannelNotFoundException;

public class HelpCommand
  extends TrainCommand {

  public HelpCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    try {
        pushBot.sendMessage(channel, " See http://github.com/Etsy/PushBot#readme");
    }
    catch (ChannelNotFoundException cne_exception){
        System.err.println(cne_exception.getMessage());
    }
  }

  @Override
  public String toString() {
    return "help";
  }
}
