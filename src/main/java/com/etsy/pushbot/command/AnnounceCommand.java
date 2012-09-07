package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;

public class AnnounceCommand
  extends TrainCommand {

  private String message;
  private String channel;

  public AnnounceCommand(String message, String channel) {
    this.message = message;
    this.channel = channel;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    if(!channel.equals(this.channel)) {
        return;
    }

    for(PushToken token : pushTrain) {
        if(token == null || !(token instanceof MemberList)) {
            continue;
        }
        String memberNames = "";
        for(Member member : ((MemberList)token)) {
            memberNames += member.getName() + " ";
        }
        if(null != this.message && ((MemberList)token).size() > 1) {
            pushBot.sendMessage(channel, memberNames + ": " + this.message);
        }
        return;
    }
  }

  @Override
  public String toString() {
    String s = "message " + message;
    return s;
  }
}
