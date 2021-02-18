package source;

public class CommonResource {

    public float max = 0;
    public float[] B, C, D, V, A;
    public float[][] MD, ME, MM, MT, MZ, MV, MA;
    public long startTime;

    public CommonResource(int n) {
        this.V = new float[n];
        this.A = new float[n];
        this.MV = new float[n][n];
        this.MA = new float[n][n];
    }
}
