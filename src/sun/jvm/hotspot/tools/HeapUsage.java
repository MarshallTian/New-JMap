package sun.jvm.hotspot.tools;

import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSYoungGen;
import sun.jvm.hotspot.gc_implementation.parallelScavenge.ParallelScavengeHeap;
import sun.jvm.hotspot.gc_implementation.shared.MutableSpace;
import sun.jvm.hotspot.gc_interface.CollectedHeap;
import sun.jvm.hotspot.memory.GenCollectedHeap;
import sun.jvm.hotspot.memory.Generation;
import sun.jvm.hotspot.runtime.VM;
import sun.jvm.hotspot.runtime.VM.Flag;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * Created by root on 5/1/15.
 */
public class HeapUsage extends Tool {
    private static String alignment = "   ";
    public HeapUsage() {}
    public void run() {
        CollectedHeap heap = VM.getVM().getUniverse().heap();
        HashMap flagMap = new HashMap();
        Flag[] flags = VM.getVM().getCommandLineFlags();
        if (flags == null) return;
        for(int psh = 0; psh < flags.length; ++psh) {
            flagMap.put(flags[psh].getName(), flags[psh]);
        }

        //this.printValue("MinHeapFreeRatio = ", this.getFlagValue("MinHeapFreeRatio", flagMap));
        if (heap instanceof GenCollectedHeap) {
            GenCollectedHeap var12 = (GenCollectedHeap) heap;

            for (int youngGen = 0; youngGen < var12.nGens(); ++youngGen) {
                Generation oldGen = var12.getGen(youngGen);
                System.out.println(youngGen);
            }
        } else {
            ParallelScavengeHeap var13 = (ParallelScavengeHeap) heap;
            PSYoungGen var15 = var13.youngGen();

            while (true) {
                // Eden FromSpace ToSpace OldGeneration PermGeneration
                System.out.print(System.currentTimeMillis());
                double eden = mutableUsage(var15.edenSpace());
                double from = mutableUsage(var15.fromSpace());
                double to = mutableUsage(var15.toSpace());

                double old = (double)var13.oldGen().used() * 100.0D / (double)var13.oldGen().capacity();

                double perm = (double)var13.permGen().used() * 100.0D / (double)var13.permGen().capacity();

                System.out.printf(" %2.2f %2.2f %2.2f %2.2f %2.2f\n", eden, from, to, old, perm);

                //System.out.println(System.currentTimeMillis());
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private double mutableUsage(MutableSpace space) {
        return (double)space.used() * 100.0D / (double)space.capacity();
    }

    private void printPSYoungGen(PSYoungGen youngGen) {
        System.out.println("Eden Space: ");
        this.printMutableSpace(youngGen.edenSpace());
    }

    private void printMutableSpace(MutableSpace space) {
        this.printValMB("capacity = ", space.capacity());
        this.printValMB("used     = ", space.used());
        long free = space.capacity() - space.used();
        this.printValMB("free     = ", free);
        System.out.println(alignment + (double)space.used() * 100.0D / (double)space.capacity() + "% used");
    }

    private void printValue(String title, long value) {
        System.out.println(" " + title + value);
    }

    private void printValMB(String title, long value) {
        if(value < 0L) {
            System.out.println(alignment + title + (value >>> 20) + " MB");
        } else {
            double mb = (double)value / 1048576.0D;
            System.out.println(alignment + title + value + " (" + mb + "MB)");
        }
    }

    private long getFlagValue(String name, Map flagMap) {
        Flag f = (Flag)flagMap.get(name);
        return f != null?(f.isBool()?(f.getBool()?1L:0L):Long.parseLong(f.getValue())):-1L;
    }


}
