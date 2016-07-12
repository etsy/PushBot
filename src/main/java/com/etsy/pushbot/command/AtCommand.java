package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;
import org.apache.log4j.Logger;

public class AtCommand
  extends TrainCommand {

  private static Logger log = Logger.getLogger(AtCommand.class);

  private String state;
  private String channel;

  public AtCommand(String state) {
    this.state = state;
    this.channel = null;
  }

  public AtCommand(String state, String channel) {
    this.state = state;
    this.channel = channel;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    if(this.channel != null && !channel.equals(this.channel)) {
        return;
    }

    if(pushTrain.isQuietPush()) {
        return;
    }

    for(PushToken token : pushTrain) {
        if(token == null || !(token instanceof MemberList)) {
            continue;
        }
        ((MemberList)token).setState(this.state);
        String memberNames = "";
        for(Member member : ((MemberList)token)) {
            member.setStatus(null);
            memberNames += member.getName() + " ";
        }
        return;
    }
  }

  @Override
  public String toString() {
    String s = "at " + state;
    return s;
  }
}
