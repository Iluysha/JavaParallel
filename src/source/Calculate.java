package source;

public class Calculate {

    private final int a;
    private final int b;
    private final int n;
    private final boolean kahan;

    public Calculate(int a, int b, int n, boolean kahan) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.kahan = kahan;
    }

    public float kahanSum(float[] arr) {
        float sum = 0;
        float c = 0;

        for (float num : arr) {
            float y = num - c;
            float t = sum + y;
            c = (t - sum) - y;
            sum = t;
        }

        return sum;
    }

    public float sum(float[] arr) {
        float sum = 0;

        for (float num : arr) {
            sum = sum + num;
        }

        return sum;
    }

    public void difArrays(float[] arr1, float[] arr2, float[] res) {
        for (int i = a; i < b; i++) {
            res[i] = arr1[i] - arr2[i];
        }
    }

    public float maxInArray(float[] arr){
        float res = arr[a];
        for (int i = a + 1; i < b; i++) {
            if(res < arr[i]){
                res = arr[i];
            }
        }
        return res;
    }

    public void multiplyFloatArray(float[] arr, float num, float[] res) {
        for (int i = a; i < b; i++) {
            res[i] = arr[i] * num;
        }
    }

    public void sumMatrix(float[][] matrix1, float[][] matrix2, float[][] res){
        for (int i = a; i < b; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
    }

    public void difMatrix(float[][] matrix1, float[][] matrix2, float[][] res){
        for (int i = a; i < b; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
    }

    public void multiplyArrayMatrix(float[][] matrix, float[] arr, float[] res) {
        float[] temp = new float[n];
        for (int j = a; j < b; j++) {
            for (int i = 0; i < n; i++) {
                temp[i] = matrix[i][j] * arr[i];
            }
            res[j] = kahan ? kahanSum(temp) : sum(temp);
        }
    }

    public void multiplyMatrix(float[][] matrix1, float[][] matrix2, float[][] res) {
        for (int i = a; i < b; i++) {
            float[] temp = new float[n];
            for (int j = 0; j < n; j++) {
                res[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    temp[k] = matrix1[i][k] * matrix2[k][j];
                }
                res[i][j] = kahan ? kahanSum(temp) : sum(temp);
            }
        }
    }

    public float firstCalculate(CommonResource res, Calculate c) {
        c.multiplyMatrix(res.MD, res.MT, res.MA);
        c.sumMatrix(res.MA, res.MZ, res.MA);
        c.multiplyMatrix(res.ME, res.MM, res.MV);
        c.difMatrix(res.MA, res.MA, res.MV);

        c.multiplyArrayMatrix(res.MT, res.D, res.V);
        return c.maxInArray(res.C);
    }
}
