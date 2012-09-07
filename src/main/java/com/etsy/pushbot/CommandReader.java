package com.etsy.pushbot;

import com.etsy.pushbot.command.TrainCommand;
import java.io.StringBufferInputStream;
import java.util.List;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ANTLRInputStream;

public class CommandReader {

  private CommandReader() {}

  public static List<TrainCommand> parse(String message) {
    try {
      ANTLRInputStream input =
        new ANTLRInputStream(new StringBufferInputStream(message));

      CommandLexer commandLexer =
        new CommandLexer(input);

      CommonTokenStream tokens =
        new CommonTokenStream(commandLexer);

      CommandParser commandParser =
        new CommandParser(tokens);

      return commandParser.commands();

    } catch(Exception e) {
      // hmm
    }

    return null;
  }
}
