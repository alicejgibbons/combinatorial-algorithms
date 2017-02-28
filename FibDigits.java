
/*
*   Output: the last 100 digits of the billionth Fibonacci number
*	Alice Gibbons
*   Fall 2015

*	Matrix:	| 1	 1 | ^n  = 	| fib(n+1)	fib(n)	 |
*			| 1	 0 |	   	| fib(n)	fib(n-1) |
*/

import java.math.BigInteger;

//Based off <http://introcs.cs.princeton.edu/java/95linear/Matrix.java>
class Matrix {
    private final int M = 2;             // number of rows
    private final int N = 2;             // number of columns
    private final BigInteger[][] data;   // M-by-N array

    // create M-by-N matrix of 0's
    public Matrix(int M, int N) {
        this.data = new BigInteger[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                    this.data[i][j] = new BigInteger("0");
    }

    // create matrix based on 2d array
    public Matrix(BigInteger[][] data) {
        this.data = new BigInteger[M][N];
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                    this.data[i][j] = data[i][j];
    }

    // create and return the N-by-N identity matrix
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = new BigInteger("1");
        return I;
    }

    // return C = A * B
    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.N != B.M) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(A.M, B.N);
        for (int i = 0; i < C.M; i++){
            for (int j = 0; j < C.N; j++){
                for (int k = 0; k < A.N; k++){
                    C.data[i][j] = (C.data[i][j]).add((A.data[i][k]).multiply(B.data[k][j]));
                }
            }
        }
        return C;
    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) 
                System.out.println(" " + data[i][j]);
            System.out.println();
        }
    }

    //return A % n
    public Matrix modulous(BigInteger n) {
    	Matrix A = this;
    	Matrix C = new Matrix(M, N);
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++){
            	C.data[i][j] = (A.data[i][j]).mod(n);
            }
        }
        return C;
    }
}

class FibDigits {
	private static final int M = 2;         // number of rows
    private static final int N = 2;  		// number of cols

    //Based off: <https://en.wikipedia.org/wiki/Modular_exponentiation>
	public static Matrix matrixModExp(Matrix A, BigInteger b, BigInteger c){
		BigInteger zero = new BigInteger("0");
		BigInteger one = new BigInteger("1");
		BigInteger two = new BigInteger("2");

		if(b.compareTo(zero) == 0){
			return (A.identity(N));
		}

		BigInteger bMinusOne = b.subtract(one);
		if((b.mod(two)).compareTo(one) == 0){
			return(A.times(matrixModExp(A, bMinusOne, c)).modulous(c));
			//D = D.mod(c);
		}
		BigInteger bDividedTwo = b.divide(two);
		Matrix D = matrixModExp(A, bDividedTwo, c);
		D = (D.times(D)).modulous(c);
		return(D);
	}

	public static void main(String args[]){
		BigInteger zero = new BigInteger("0");
		BigInteger one = new BigInteger("1");
		BigInteger data[][] = {{one, one}, {one, zero}};
		Matrix A = new Matrix(data);
		BigInteger c = new BigInteger("10000000000000000000000000000000000000000000000000" +
										"000000000000000000000000000000000000000000000000000");
		BigInteger b = new BigInteger("999999999");
		A = matrixModExp(A, b, c);
		A.show();
	}
}


