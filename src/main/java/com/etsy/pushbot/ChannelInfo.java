package com.etsy.pushbot;

import java.util.Date;

public class ChannelInfo {

  private String name;
  private volatile String topic;
  private Date topicSetDate;
  private boolean hasBadTopic = false;

  public ChannelInfo(String name) {
    this.name = name;
  }

  public String getTopic() {
    if(topic == null) {
      return "";
    }
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
    this.topicSetDate = new Date();
  }

  /**
   * Set to true to indicate that the current topic is
   * unparseable and no commands should be accepted until the
   * topic is manually fixed
   */
  public void setHasBadTopic(boolean hasBadTopic) {
    this.hasBadTopic = hasBadTopic;
  }

  public boolean getHasBadTopic() {
    return this.hasBadTopic;
  }

  public int getTopicAgeInSeconds() {
    Date now = new Date();
    return (int)((now.getTime() - this.topicSetDate.getTime())/1000);
  }

  public PushTrain getPushTrain() {
    if(topic == null) {
      return null;
    }
    return PushTrainReader.parse(topic);
  }
}
