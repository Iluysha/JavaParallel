package source;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class DataWorker {

    private final int n;

    public DataWorker(int n) {
        this.n = n;
    }

    public float[] randomArray() {
        float[] res = new float[n];
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            res[i] = Float.parseFloat(String.format("%." + r.nextInt(7) + "f",
                    12 * r.nextFloat()).replace(',', '.'));
        }
        return res;
    }

    public float[][] randomMatrix() {
        float[][] res = new float[n][n];
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = Float.parseFloat(String.format("%." + r.nextInt(7) + "f",
                        12 * r.nextFloat()).replace(',', '.'));
            }
        }
        return res;
    }

    public float[] readArray(String file) throws IOException {
        float[] res = new float[n];
        String str = Files.readString(Path.of(file));
        String[] arr = str.split("\\s+");
        for (int i = 0; i < n; i++) {
            res[i] = Float.parseFloat(arr[i]);
        }
        return res;
    }

    public float[][] readMatrix(String file) throws IOException {
        float[][] res = new float[n][n];
        String str = Files.readString(Path.of(file));
        String[] arr = str.split("\\s+");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = Float.parseFloat(arr[i * n + j]);
            }
        }
        return res;
    }

    public void writeArray(float[] arr, String file) {
        try(FileWriter writer = new FileWriter(file))
        {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < n; i++){
                str.append(arr[i]);
                str.append(" ");
            }
            writer.write(String.valueOf(str));
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void writeMatrix(float[][] matrix, String file) {
        try(FileWriter writer = new FileWriter(file))
        {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < n; i++){
                for (int j = 0; j < n; j++) {
                    str.append(matrix[i][j]);
                    str.append(" ");
                }
                str.append("\n");
            }
            writer.write(String.valueOf(str));
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void read(CommonResource res) throws IOException {
        String path = "src/data/input/";
        res.B = readArray(path + "B.txt");
        res.C = readArray(path + "C.txt");
        res.D = readArray(path + "D.txt");
        res.MD = readMatrix(path + "MD.txt");
        res.ME = readMatrix(path + "ME.txt");
        res.MM = readMatrix(path + "MM.txt");
        res.MT = readMatrix(path + "MT.txt");
        res.MZ = readMatrix(path + "MZ.txt");
    }

    public void write(CommonResource res, String name) {
        writeArray(res.A,"src/data/output/array/" + name + ".txt");
        writeMatrix(res.MA,"src/data/output/matrix/" + name + ".txt");
    }
}
