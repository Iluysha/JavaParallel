package main;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import source.*;

//MА = MD * MT + MZ - ME * MM
//A = D * MT - max(C) * B

public class ForkJoinClass {

    public static void main(String[] args) throws IOException {
        int n = 1000;
        int p = 4;

        DataWorker dataWorker = new DataWorker(n);
        CommonResource res = new CommonResource(n);

        dataWorker.read(res);
        res.startTime = System.nanoTime();

        new ForkJoinPool(p).invoke(new SubTask1(res, n, p, 0));
        new ForkJoinPool(p).invoke(new SubTask2(res, n, p, 0));

        long endTime = System.nanoTime();
        System.out.println("Time: " + (endTime - res.startTime) / 1000000 + " ms");

        dataWorker.write(res, "ForkJoin");
    }
}

class SubTask1 extends RecursiveAction {

    private final CommonResource res;
    int n;
    int p;
    int num;

    SubTask1(CommonResource res, int n, int p, int num) {
        this.res = res;
        this.n = n;
        this.p = p;
        this.num = num;
    }

    @Override
    public void compute() {
        Calculate c = new Calculate(n / p * num,
                ((num != p - 1) ? n / p * (num + 1) : n), n, true);

        if(num < p) {
            SubTask1 subTask1 = new SubTask1(res, n, p, num + 1);
            subTask1.fork();

            System.out.println("Task " + (num + 1) + " start");

            float max = c.firstCalculate(res, c);

            if(res.max < max) {
                res.max = max;
            }
            subTask1.join();
        }
    }
}

class SubTask2 extends RecursiveAction {

    private final CommonResource res;
    int n;
    int p;
    int num;

    SubTask2(CommonResource res, int n, int p, int num) {
        this.res = res;
        this.n = n;
        this.p = p;
        this.num = num;
    }

    @Override
    public void compute() {
        Calculate c = new Calculate(n / p * num,
                ((num != p - 1) ? n / p * (num + 1) : n), n, true);

        if(num < p) {
            SubTask2 subTask2 = new SubTask2(res, n, p, num + 1);
            subTask2.fork();

            c.multiplyFloatArray(res.B, res.max, res.A);
            c.difArrays(res.V, res.A, res.A);

            System.out.println("Task " + (num + 1) + " end");

            subTask2.join();
        }
    }
}
