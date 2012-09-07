package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.PushToken;
import com.etsy.pushbot.tokens.MemberList;

public class DoneCommand extends TrainCommand {

  public DoneCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    int i = 0;
    for(PushToken token : pushTrain) {
      if(token != null && token instanceof MemberList) {
        pushTrain.remove(i);
        return;
      }
      ++i;
    }
  }

  @Override
  public String toString() {
    String s = "done";
    return s;
  }
}
