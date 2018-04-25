package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

import java.util.LinkedList;
import java.util.List;

public class MultiCommand extends TrainCommand {

  // A set of commands
  private List<TrainCommand> commandList =
    new LinkedList<TrainCommand>();

  public MultiCommand() { }

  public void addCommand(TrainCommand command) {
    commandList.add(command);
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    for(TrainCommand command : commandList) {
      command.onCommand(pushBot, pushTrain, channel, sender);
    }
  }

  @Override
  public String toString() {
    String s = "";
    for(TrainCommand command : commandList) {
      s += command.toString() + ";";
    }
    return s;
  }
}
