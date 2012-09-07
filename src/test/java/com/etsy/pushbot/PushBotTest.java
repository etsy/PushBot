package com.etsy.pushbot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
*/
public class PushBotTest
  extends TestCase
{
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public PushBotTest( String testName )
  {
    super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
    return new TestSuite( PushBotTest.class );
  }

  /**
   *
   */
  public void testPushTrainReader()
  {
    assertEquals("asm + gio | zz",
        PushTrainReader.parse("asm,gio|zz").toString());

    assertEquals("asm + gio | zz",
        PushTrainReader.parse("asm + gio | zz").toString());

    assertEquals("HOLD \"message string\" | asm + gio | zz",
        PushTrainReader.parse("HOLD \"message string\" | asm + gio | zz").toString());
  }

  /**
   *
   */
  public void testCommandReader()
  {
    assertEquals("join",
        CommandReader.parse(".join").get(0).toString());

    assertEquals("join HOLD",
        CommandReader.parse(".join HOLD").get(0).toString());

    assertEquals("join with asm",
        CommandReader.parse(".join with asm").get(0).toString());

    assertEquals("join config with asm",
        CommandReader.parse(".join config with asm").get(0).toString());

    assertEquals("done",
        CommandReader.parse(".done").get(0).toString());

    assertEquals("hold",
        CommandReader.parse(".hold").get(0).toString());

    assertEquals("hold \"message string\"",
        CommandReader.parse(".hold \"message string\"").get(0).toString());

    assertEquals("unhold",
        CommandReader.parse(".unhold").get(0).toString());

    assertEquals("help",
        CommandReader.parse(".help").get(0).toString());
  }

  public void testJoinCommand()
  {
    assertEquals("clear", PushTrainReader.parse("").toString());

    PushTrain pushTrain = PushTrainReader.parse("gio");
    assertNotNull(pushTrain);

    CommandReader.parse(".join").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("gio | asm", pushTrain.toString());

    CommandReader.parse(".done").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm", pushTrain.toString());

    CommandReader.parse(".join HOLD").get(0).onCommand(null, pushTrain, "#channel", "gio");
    assertEquals("asm | gio (HOLD)", pushTrain.toString());

    CommandReader.parse(".pop").get(0).onCommand(null, pushTrain, "#channel", "gio");
    assertEquals("asm", pushTrain.toString());

    CommandReader.parse(".join hold").get(0).onCommand(null, pushTrain, "#channel", "gio");
    assertEquals("asm | gio (HOLD)", pushTrain.toString());

    CommandReader.parse(".join").get(0).onCommand(null, pushTrain, "#channel", "asm");
    CommandReader.parse(".join").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm | gio (HOLD) | asm + asm", pushTrain.toString());

    CommandReader.parse(".nevermind").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("gio (HOLD)", pushTrain.toString());
  }

  public void testHoldCommand()
  {
    PushTrain pushTrain = PushTrainReader.parse("asm");
    assertNotNull(pushTrain);

    CommandReader.parse(".hold").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("HOLD | asm", pushTrain.toString());

    CommandReader.parse(".unhold").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm", pushTrain.toString());
  }

  /*
  public void testSpacesInJoinCommand()
  {
    PushTrain pushTrain = PushTrainReader.parse("clear");
    assertNotNull(pushTrain);

    CommandReader.parse(".join ").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm", pushTrain.toString());
  }
  */


  public void testMessageCommand()
  {
    PushTrain pushTrain = PushTrainReader.parse("clear");

    CommandReader.parse(".message \"test\"").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("clear !! \"test\"", pushTrain.toString());

    CommandReader.parse(".join").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm !! \"test\"", pushTrain.toString());

    CommandReader.parse(".message -").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm", pushTrain.toString());
  }

  public void testMultipleCommands()
  {
    PushTrain pushTrain = PushTrainReader.parse("clear");

    /*
    CommandReader.parse(".join; .message \"foo\"").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("clear !! \"test\"", pushTrain.toString());
    */
  }

  public void testPopCommand()
  {
    assertEquals("clear", PushTrainReader.parse("").toString());

    PushTrain pushTrain = PushTrainReader.parse("adam_s | adam_s + asm");
    assertNotNull(pushTrain);

    CommandReader.parse(".pop").get(0).onCommand(null, pushTrain, "#channel", "adam_s");
    assertEquals("adam_s | asm", pushTrain.toString());

    CommandReader.parse(".pop").get(0).onCommand(null, pushTrain, "#channel", "adam_s");
    assertEquals("asm", pushTrain.toString());
  }

  public void testMemberListMessage()
  {
    PushTrain pushTrain = PushTrainReader.parse("clear");
    assertNotNull(pushTrain);

    CommandReader.parse(".join HOLD \"hold message\"").get(0).onCommand(null, pushTrain, "#channel", "asm");
    assertEquals("asm (HOLD) \"hold message\"", pushTrain.toString());
  }


}
