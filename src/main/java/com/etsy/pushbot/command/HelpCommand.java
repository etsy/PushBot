package com.etsy.pushbot.command;

import com.etsy.pushbot.*;

public class HelpCommand
  extends TrainCommand {

  public HelpCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    pushBot.sendMessage(channel, " See http://github.com/Etsy/PushBot#readme");
  }

  @Override
  public String toString() {
    return "help";
  }
}
