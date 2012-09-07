package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.Hold;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class UnholdCommand
  extends TrainCommand {

  public UnholdCommand() {}

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    if(pushTrain == null || pushTrain.size() < 1) {
      System.out.println("empty");
      return;
    }
    PushToken pushToken = pushTrain.get(0);
    if(pushToken != null && pushToken instanceof Hold) {
       pushTrain.removeFirst();
    }
  }

  @Override
  public String toString() {
    return "unhold";
  }
}
