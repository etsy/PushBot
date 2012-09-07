package com.etsy.pushbot.command;

import com.etsy.pushbot.*;

public class MessageCommand
  extends TrainCommand {

  private String message;

  public MessageCommand(String message) {
    this.message = message;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    if(this.message.equals("-")) {
      this.message = null;
    }
    pushTrain.setMessage(this.message);
  }

  @Override
  public String toString() {
    return "help";
  }
}
