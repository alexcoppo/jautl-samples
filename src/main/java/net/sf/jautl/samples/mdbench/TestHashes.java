/*
    Copyright (c) 2000-2012 Alessandro Coppo
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:
    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package net.sf.jautl.samples.mdbench;

import net.sf.jautl.md.*;
import net.sf.jautl.utility.benchmark.Benchmarker;
import net.sf.jautl.utility.benchmark.Stopwatch;
import net.sf.jautl.utility.commandline.*;

/**
 * This class handles the overall test and benchmarking of message digest engines.
 */
public class TestHashes {
    public static void main(String args[]) {
        CommandLineParser clp = new CommandLineParser();

        try {
            Flag hlp = clp.add(new Flag("help", ""));
            Parameter eng = clp.add(new Parameter("engine", "Adler32", "one among Adler32, Gouulburn, MD2, MD5, Murmur2A, Murmur3_32, SHA1, RIPEMD128, RIPEMD160, SHA2-256, SHA2-384, SHA2-512"));
            Parameter bs = clp.add(new Parameter("block-size", "1024", "hash block size [bytes]"));
            Parameter bt = clp.add(new Parameter("bench-time", "1", "the minimum benchmark run time in seconds"));

            clp.parse(args);

            if (hlp.exists()) {
                clp.printUsage(null, System.err);
                return;
            }

            String engine = eng.getString();

            DigestEngine de = null;

            if (engine.equals("Adler32")) de = new Adler32();
            if (engine.equals("Goulburn")) de = new Goulburn();
            if (engine.equals("MD2")) de = new MD2();
            if (engine.equals("MD5")) de = new MD5();
            if (engine.equals("Murmur2A")) de = new Murmur2A();
            if (engine.equals("Murmur3_32")) de = new Murmur3_32();
            if (engine.equals("SHA1")) de = new SHA1();
            if (engine.equals("RIPEMD128")) de = new RIPEMD128();
            if (engine.equals("RIPEMD160")) de = new RIPEMD160();
            if (engine.equals("SHA2-256")) de = new SHA2_256();
            if (de == null) return;

            int blockSize = bs.getInt();
            MDAdaptor mda = new MDAdaptor(de, blockSize);

            System.out.println("Timer Granularity Estimate [msec]:" + Stopwatch.granularityEstimate(1.0) + "\n");
            
            long loops = Benchmarker.estimateLoopCount(mda, bt.getDouble());
            System.out.println("Loops estimate:" + loops + "\n");

            double elapsedTime = Benchmarker.benchmark(mda, loops);
            double loopTime = elapsedTime / loops;
            double loopsPerSecond = loops / elapsedTime;
            
            System.out.println("Loop time        [usec] : " + loopTime * 1e6);
            System.out.println("Loops/second            : " + loopsPerSecond);
            if (bs.getInt() > 0)
                System.out.println("Throughput [MBytes/sec] : " + blockSize * loopsPerSecond / 1048576.);
        } catch (SyntaxException clse) {
            clp.printUsage(clse, System.err);
        }
    }
}