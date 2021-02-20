package main;

import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import source.*;

//MА = MD * MT + MZ - ME * MM
//A = D * MT - max(C) * B

public class SemaphoreClass {
    public static void main(String[] args) throws  IOException {
        int n = 1000;
        int p = 4;

        DataWorker dataWorker = new DataWorker(n);
        CommonResource res = new CommonResource(n);
        CyclicBarrier cb1 = new CyclicBarrier(p);
        CyclicBarrier cb2 = new CyclicBarrier(p);
        Semaphore semaphore = new Semaphore(1, true);

        dataWorker.read(res);
        res.startTime = System.nanoTime();

        for (int i = 0; i < p; i++) {
            new Thread(() -> {
                try {
                    thread(dataWorker, res, n, p, cb1, cb2, semaphore);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void thread(DataWorker dataWorker, CommonResource res, int n, int p, CyclicBarrier cb1,
                              CyclicBarrier cb2, Semaphore semaphore) throws InterruptedException {
        String name = Thread.currentThread().getName();
        int num = Integer.parseInt(name.substring(name.length() - 1));
        Calculate c = new Calculate(n / p * num,
                ((num != p - 1) ? n / p * (num + 1) : n), n, true);
        System.out.println("Task " + (num + 1) + " start");

        float max = c.firstCalculate(res, c);

        semaphore.acquire();
        if(res.max < max) {
            res.max = max;
        }
        semaphore.release();

        try { cb1.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }

        c.multiplyFloatArray(res.B, res.max, res.A);
        c.difArrays(res.V, res.A, res.A);

        try { cb2.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Task " + (num + 1) + " end");

        if(num == 0) {
            long endTime = System.nanoTime();
            System.out.println("Time: " + (endTime - res.startTime) / 1000000 + " ms");
            dataWorker.write(res, "Semaphore");
        }
    }
}
