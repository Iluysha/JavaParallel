package main;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import source.*;

//MА = MD * MT + MZ - ME * MM
//A = D * MT - max(C) * B

public class CallableClass {
    public static void main(String[] args) throws IOException, InterruptedException {
        int n = 1000;
        int p = 4;

        DataWorker dataWorker = new DataWorker(n);
        CommonResource res = new CommonResource(n);
        ExecutorService executor = Executors.newFixedThreadPool(p);
        CyclicBarrier cb = new CyclicBarrier(p);
        ReentrantLock locker = new ReentrantLock();
        MyLockThread callable = new MyLockThread(res, n, p, locker, cb);

        dataWorker.read(res);
        res.startTime = System.nanoTime();

        for (int i = 0; i < p; i++) {
            executor.submit(callable);
        }

        executor.shutdown();
        if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
            long endTime = System.nanoTime();
            System.out.println("Time: " + (endTime - res.startTime) / 1000000 + " ms");

            dataWorker.write(res, "Callable");
        }
    }

    static class MyLockThread implements Callable<String> {

        private final CommonResource res;
        int n;
        int p;
        private final ReentrantLock locker;
        CyclicBarrier cb;

        MyLockThread(CommonResource res, int n, int p, ReentrantLock locker, CyclicBarrier cb) {
            this.res = res;
            this.n = n;
            this.p = p;
            this.locker = locker;
            this.cb = cb;
        }

        @Override
        public String call() {
            String name = Thread.currentThread().getName();
            int num = Integer.parseInt(name.substring(name.length() - 1)) - 1;
            Calculate c = new Calculate(n / p * num,
                    ((num != p - 1) ? n / p * (num + 1) : n), n, true);
            System.out.println("Task " + (num + 1) + " start");

            float max = c.firstCalculate(res, c);

            locker.lock();
            if (res.max < max) {
                res.max = max;
            }
            locker.unlock();

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
}
