/**
 * Taken from
 * http://neopatel.blogspot.com/2011/04/logging-to-graphite-monitoring-tool.html
 */
package com.etsy.pushbot;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class GraphiteLogger {
    private String graphiteHost;
    private int graphitePort;

    public GraphiteLogger(String graphiteHost,
                          int graphitePort) {
        this.graphiteHost = graphiteHost;
        this.graphitePort = graphitePort;
    }
    
    public String getGraphiteHost() {
        return graphiteHost;
    }

    public void setGraphiteHost(String graphiteHost) {
        this.graphiteHost = graphiteHost;
    }

    public int getGraphitePort() {
        return graphitePort;
    }

    public void setGraphitePort(int graphitePort) {
        this.graphitePort = graphitePort;
    }

    public void logToGraphite(String key, long value) {
        Map<String,Long> stats = new HashMap<String,Long>();
        stats.put(key, value);
        logToGraphite(stats);
    }

    public void logToGraphite(Map<String,Long> stats) {
        if (stats.isEmpty()) {
            return;
        }

        try {
            logToGraphite("ci.push", stats);
        } catch (Throwable t) {
            System.out.println("Can't log to graphite " + t.getMessage());
        }
    }

    private void logToGraphite(String nodeIdentifier, Map<String,Long> stats) throws Exception {
        Long curTimeInSec = System.currentTimeMillis() / 1000;
        StringBuffer lines = new StringBuffer();
        for (Map.Entry entry : stats.entrySet()) {
            String key = nodeIdentifier + "." + entry.getKey();
            lines.append(key).append(" ").append(entry.getValue()).append(" ").append(curTimeInSec).append("\n"); //even the last line in graphite 
        }
        logToGraphite(lines);
    }

    private void logToGraphite(StringBuffer lines) throws Exception {
        String msg = lines.toString();
        System.out.println("Writing [{}] to graphite " + msg);
        Socket socket = new Socket(graphiteHost, graphitePort);
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(msg);
            writer.flush();
            writer.close();
        } finally {
            socket.close();
        }
    }
}
