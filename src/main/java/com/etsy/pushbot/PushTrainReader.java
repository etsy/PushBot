package com.etsy.pushbot;

import java.io.StringBufferInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ANTLRInputStream;

public class PushTrainReader {

  private PushTrainReader() {}

  public static PushTrain parse(String pushTrainString) {
    try {
      ANTLRInputStream input =
        new ANTLRInputStream(new StringBufferInputStream(pushTrainString));

      PushTrainLexer pushTrainLexer =
        new PushTrainLexer(input);

      CommonTokenStream tokens = new CommonTokenStream(pushTrainLexer);

      PushTrainParser pushTrainParser =
        new PushTrainParser(tokens);

      return pushTrainParser.push_train();

    } catch(Exception e) {
      // hmm
    }

    return null;
  }
}
