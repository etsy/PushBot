package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.Hold;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class HoldCommand
  extends TrainCommand {

  private String message;

  public HoldCommand() {}

  public void setMessage(String message) {
    this.message = message;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    Hold hold = new Hold();
    hold.setMessage(this.message);
    pushTrain.addFirst(hold);
  }

  @Override
  public String toString() {
    String s = "hold";
    if(message != null) {
      s += " " + message;
    }
    return s;
  }
}
