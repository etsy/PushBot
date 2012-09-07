package com.etsy.pushbot.command;

import com.etsy.pushbot.*;

public class SayCommand
  extends TrainCommand {

  private String where;
  private String what;

  public void setMessage(String message) {
    String[] whereWhat = message.replaceAll("\"$|^\"", "").trim().split(" ", 2);
    if (whereWhat.length == 2) {
        this.where = whereWhat[0].trim();
        this.what = whereWhat[1].trim();
    }
  }

  public SayCommand() {
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    if (this.where == null) {
        return;
    }
    pushBot.sendMessage(this.where, this.what);
  }

  @Override
  public String toString() {
    return "say";
  }
}
