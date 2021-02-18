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

        new ForkJoinPool(p).invoke(new MyTask(res, n, p, 0, false));

        long endTime = System.nanoTime();
        System.out.println("Time: " + (endTime - res.startTime) / 1000000 + " ms");

        dataWorker.write(res, "ForkJoin");
    }
}

class MyTask extends RecursiveAction {

    private final CommonResource res;
    int n;
    int p;
    int num;
    boolean forked;

    MyTask(CommonResource res, int n, int p, int num, boolean forked) {
        this.res = res;
        this.n = n;
        this.p = p;
        this.num = num;
        this.forked = forked;
    }

    @Override
    public void compute() {
        Calculate c = new Calculate(n / p * num,
                ((num != p - 1) ? n / p * (num + 1) : n), n, true);

        if(num < p) {
            if(!forked) {
                MyTask subTask1 = new MyTask(res, n, p, num + 1, false);
                subTask1.fork();

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
                subTask1.join();
            }

            if(forked || num == 0) {
                MyTask subTask2 = new MyTask(res, n, p, num + 1, true);
                subTask2.fork();

                c.multiplyFloatArray(res.B, res.max, res.A);
                c.difArrays(res.V, res.A, res.A);

                System.out.println("Task " + (num + 1) + " end");

                subTask2.join();
            }
        }
    }
}
