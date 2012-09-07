package com.etsy.pushbot.command;

import com.etsy.pushbot.*;

abstract public class TrainCommand {

  abstract public void onCommand(PushBot pushBot,
                                 PushTrain pushTrain,
                                 String channel,
                                 String sender);

  abstract public String toString();
}
