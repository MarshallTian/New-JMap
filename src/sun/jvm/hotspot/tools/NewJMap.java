package sun.jvm.hotspot.tools;

import sun.jvm.hotspot.tools.HeapSummary;
import sun.jvm.hotspot.tools.Tool;

/**
 * Created by root on 4/30/15.
 */
public class NewJMap extends Tool {
    public NewJMap(int m) {
        mode = m;
    }

    private int mode;

    public void run() {
        Tool tool = new HeapUsage();
        //Tool tool = new HeapSummary();
        tool.setAgent(getAgent());
        tool.setDebugeeType(getDebugeeType());
        tool.run();
    }

    public static void main(String[] args) {
        int mode = 0;
        NewJMap jmap = new NewJMap(mode);
        String[] a = args;

        jmap.start(a);
        jmap.stop();
    }
}
