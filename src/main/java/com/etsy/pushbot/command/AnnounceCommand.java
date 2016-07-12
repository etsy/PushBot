package com.etsy.pushbot.command;

import com.etsy.pushbot.*;
import com.etsy.pushbot.tokens.MemberList;
import com.etsy.pushbot.tokens.PushToken;
import org.apache.log4j.Logger;

public class AnnounceCommand
  extends TrainCommand {

  private static Logger log = Logger.getLogger(AnnounceCommand.class);

  private String message;
  private String channel;

  public AnnounceCommand(String message) {
    this.message = message;
    this.channel = null;
  }

  public AnnounceCommand(String message, String channel) {
    this.message = message;
    this.channel = channel;
  }

  public void onCommand(PushBot pushBot,
                        PushTrain pushTrain,
                        String channel,
                        String sender) {
    // Do not make announcement if it is to a different channel than
    // we are configured for, unless we didn't configurea a channel,
    // in which case go for it.
    if(this.channel != null && !channel.equals(this.channel)) {
        log.warn("Announce to different channel, not sending");
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
        if(null != this.message) {
            String msg = this.message;
            if (((MemberList)token).size() > 1) {
                msg = memberNames + ": " + msg;
            }
            pushBot.sendMessage(channel, msg);
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
