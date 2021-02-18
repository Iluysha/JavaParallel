package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.Callable;
import source.*;

//MА = MD * MT + MZ - ME * MM
//A = D * MT - max(C) * B

class BlockingQueueClass {
    public static void main(String[] args) throws InterruptedException, IOException {
        int n = 1000;
        int p = 4;

        DataWorker dataWorker = new DataWorker(n);
        CommonResource res = new CommonResource(n);
        ExecutorService executor = Executors.newFixedThreadPool(p);
        CyclicBarrier cb = new CyclicBarrier(p);
        BlockingQueue<Float> queue = new ArrayBlockingQueue<>(p);
        queue.put((float) 0);
        List<Future<String>>  futures = new ArrayList<>();
        MyBlockThread callable = new MyBlockThread(res, n, p, queue, cb);

        dataWorker.read(res);
        res.startTime = System.nanoTime();

        for (int i = 0; i < p; i++) {
            Future future = executor.submit(callable);
            futures.add(future);
        }

        executor.shutdown();
        if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
            long endTime = System.nanoTime();
            System.out.println("Time: " + (endTime - res.startTime) / 1000000 + " ms");

            dataWorker.write(res, "BlockingQueue");
        }
    }
}

class MyBlockThread implements Callable {

    private final CommonResource res;
    int n;
    int p;
    private final BlockingQueue<Float> queue;
    CyclicBarrier cb;

    MyBlockThread(CommonResource res, int n, int p, BlockingQueue<Float> queue, CyclicBarrier cb) {
        this.res = res;
        this.n = n;
        this.p = p;
        this.queue = queue;
        this.cb = cb;
    }

    @Override
    public Object call() throws InterruptedException {
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
        max = Math.max(queue.take(), max);
        queue.put(max);
        res.max = max;

        try { cb.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }

        c.multiplyFloatArray(res.B, res.max, res.A);
        c.difArrays(res.V, res.A, res.A);
        System.out.println("Task " + (num + 1) + " end");
        return null;
    }
}
