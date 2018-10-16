package strassen;

public class Main {
    static int[][] a;
    static int[][] b;
    static int[][] c;

    public static void main(String[] args) {
        int i;
        double coef=0;
        double coef_total=0;
        int count=0;
        for (i = 64; i <= 512; i++) {
            count++;
            a = new int[i][i];
            b = new int[i][i];
            c = new int[i][i];
            for (int j = 0; j < i; j++) {
                // do the for in the row according to the column size
                for (int k = 0; k < i; k++) {
                    // multiple the random by 10 and then cast to in
                    a[j][k] = (int) Math.round(Math.random() * 10);
                    b[j][k] = (int) Math.round(Math.random() * 10);
                }


            }
            long mul=0;
            long st=0;
            for(int j=0; j<5; j++){
                mul += multiply(a, b);

            }
            for (int j=0; j<5; j++){
                st += strassenmul(a, b);
            }
            double avgmul = mul / 5.0;//(5 * 10^3 find average then convert it to microsec)
            double avgst  = st / 5.0;//(5 *10^3 find average then convert it to microsec)

            if (avgst < avgmul) {
                System.out.println("strassen threshold " + i + " " + avgmul + " " + avgst);
            }else{
                System.out.println("loop finished " + i + " " + avgmul + " " + avgst);
            }
            coef_total += avgst/Math.pow(i,2.81);

        }
        coef = coef_total/count;
        System.out.println(coef);

    }

    public static long multiply(int[][] a, int[][] b) {
        long startTime = System.nanoTime();
        int i, j;
        for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
                c[i][j] = 0;
                for (int k = 0; k < a.length; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
//        for(i=0; i<c.length; i++){
//            for(j=0; j<c[0].length; j++){
//                System.out.print(c[i][j] + " ");
//            }
//            System.out.println();
//        }
        long estimatedTime = System.nanoTime() - startTime;

        return estimatedTime;
    }
    public static void convmul(int[][] C,int [][]A ,int[][] B,int n){
        int i,j,k,t;
        for (i=0;i<n;i++)
            for (j=0;j<n;j++)
            {
                for (k=0,t=0;k<n;k++)
                    t+=A[i][k]*B[k][j];
                C[i][j]=t;
            }
    }

    public static long strassenmul(int[][] a, int[][] b) {
        long startTime = System.nanoTime();
        int[][] c = strassen(a, b);
//        for(int i=0; i<c.length; i++){
//            for(int j=0; j<c[0].length; j++){
//                System.out.print(c[i][j] + " ");
//            }
//            System.out.println();
//        }
        long estimatedTime = System.nanoTime() - startTime;
        return estimatedTime;
    }

    public static int[][] strassen(int[][] a, int[][] b) {
        int n = a.length;
        int[][] R = new int[n][n];
        /* if n is smaller than 65, do normal maxtrix multiplication */
        if (n <= 64){
            convmul(R,a,b,n);
        }
        else {
            int[][] A11 = new int[n / 2][n / 2];int[][] A12 = new int[n / 2][n / 2];
            int[][] A21 = new int[n / 2][n / 2];int[][] A22 = new int[n / 2][n / 2];
            int[][] B11 = new int[n / 2][n / 2];int[][] B12 = new int[n / 2][n / 2];
            int[][] B21 = new int[n / 2][n / 2];int[][] B22 = new int[n / 2][n / 2];
            divide(a,A11,A12,A21,A22);
            divide(b,B11,B12,B21,B22);
            int[][] M1 = strassen(add(A11, A22), add(B11, B22));
            int[][] M2 = strassen(add(A21, A22), B11);
            int[][] M3 = strassen(A11, sub(B12, B22));
            int[][] M4 = strassen(A22, sub(B21, B11));
            int[][] M5 = strassen(add(A11, A12), B22);
            int[][] M6 = strassen(sub(A21, A11), add(B11, B12));
            int[][] M7 = strassen(sub(A12, A22), add(B21, B22));

            int[][] C11 = add(sub(add(M1, M4), M5), M7);
            int[][] C12 = add(M3, M5);
            int[][] C21 = add(M2, M4);
            int[][] C22 = add(sub(add(M1, M3), M2), M6);
            merge(R,C11,C12,C21,C22);
        }
        return R;
    }




    //subtract two matrices
    public static int[][] sub(int[][] A, int[][] B)
    {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B[i][j];
        return C;
    }
    //add two matrices
    public static int[][] add(int[][] A, int[][] B)
    {
        int n = A.length;
        int[][] C = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];
        return C;
    }

    public static void divide(int[][] d, int[][] d11,int[][] d12,int[][] d21,int[][] d22) {
        int i, j;
        int n = d11.length;

        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++) {
                d11[i][j] = d[i][j];
                d12[i][j] = d[i][j + n];
                d21[i][j] = d[i + n][j];
                d22[i][j] = d[i + n][j + n];
            }
    }
    //split parent matrices to child matrices
    public static void split(int[][] P, int[][] C, int iB, int jB)
    {
        for(int i1 = 0, i2 = iB; i1 < C.length; i1++, i2++)
            for(int j1 = 0, j2 = jB; j1 < C.length; j1++, j2++)
                C[i1][j1] = P[i2][j2];
    }
    //copy child matrices to form parent elements
    public static int[][] merge(int[][] a,int[][] a11, int[][] a12, int[][] a21, int[][] a22){
        int i,j;
        int n = a11.length;
        for(i=0;i<n;i++)
            for(j=0;j<n;j++)
            {
                a[i][j]=a11[i][j];
                a[i][j+n]=a12[i][j];
                a[i+n][j]=a21[i][j];
                a[i+n][j+n]=a22[i][j];
            }
        return a;
    }


}

