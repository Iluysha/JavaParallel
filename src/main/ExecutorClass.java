package main;

import source.Calculate;
import source.CommonResource;
import source.DataWorker;

import java.io.IOException;
import java.util.concurrent.*;

//MА = MD * MT + MZ - ME * MM
//A = D * MT - max(C) * B

public class ExecutorClass {
    public static void main(String[] args) throws IOException, InterruptedException {
        int n = 1000;
        int p = 4;

        DataWorker dataWorker = new DataWorker(n);
        CommonResource res = new CommonResource(n);
        ExecutorService executor = Executors.newFixedThreadPool(p);
        CountDownLatch latch = new CountDownLatch(p);

        dataWorker.read(res);
        res.startTime = System.nanoTime();

        for (int i = 0; i < p; i++) {
            executor.execute(new Thread(() -> thread(res, n, p, latch)));
        }

        executor.shutdown();
        latch.await();

        long endTime = System.nanoTime();
        System.out.println("Time: " + (endTime - res.startTime) / 1000000 + " ms");

        dataWorker.write(res, "Executor");
    }

    public static void thread(CommonResource res, int n, int p, CountDownLatch latch) {
        String name = Thread.currentThread().getName();
        int num = Integer.parseInt(name.substring(name.length() - 1)) - 1;
        Calculate c = new Calculate(n / p * num,
                ((num != p - 1) ? n / p * (num + 1) : n), n, true);
        System.out.println("Task " + (num + 1) + " start");


        c.multiplyMatrix(res.MD, res.MT, res.MA);
        c.sumMatrix(res.MA, res.MZ, res.MA);
        c.multiplyMatrix(res.ME, res.MM, res.MV);
        c.difMatrix(res.MA, res.MA, res.MV);

        c.multiplyArrayMatrix(res.MT, res.D, res.V);
        float max = c.maxInArray(res.C);

        if(res.max < max) {
            res.max = max;
        }

        latch.countDown();

        c.multiplyFloatArray(res.B, res.max, res.A);
        c.difArrays(res.V, res.A, res.A);

        System.out.println("Task " + (num + 1) + " end");
    }
}
