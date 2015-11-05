package SudokuSolver;

import java.util.Arrays;

public class SudokuDLX extends AbstractSudokuSolver{
	
	// コンストラクタ
	public SudokuDLX(){
		solvee = new int[S][S];
	}
    // 数独問題をもとにEPC行列の初期値を決定する
    private int[][] makeExactCoverGrid(int[][] sudoku){
    	// EPC行列を生成する
        int[][] R = sudokuExactCover();
        for(int i = 1; i <= S; i++){
            for(int j = 1; j <= S; j++){
                int n = sudoku[i - 1][j - 1];
                // 数独問題において空白じゃないマスの場合
                if (n != 0){
                	// nの値を探索
                    for(int num = 1; num <= S; num++){
                    	// 探索用の変数numとnが一致しない場合
                        if (num != n){
                        	// EPC行列のi行j列目のnumが値の場合の行を0で満たす
                            Arrays.fill(R[getIdx(i, j, num)], 0);
                        }
                    }
                }
            }
        }
        return R;
    }

    // ECP行列の生成
    private int[][] sudokuExactCover(){
        int[][] R = new int[9 * 9 * 9][9 * 9 * 4];

        int hBase = 0;
        
        
        // 1列目の9の倍数分1が入り2列目に移るあとは729列目まで繰り返し
        /* 1 2 3 4 5 6 7 8 9 ... 729
         * 1
         * 1
         * 1
         * 1
         * 1
         * 1
         * 1
         * 1
         * 1
         *   1
         *   1
         *   1
         *   .
         *   .
         *   .
         *                  .
         *                  .
         *                  .
         *                  1
         *                  1
         *                  1
         *                     .
         *                     .
         *                     .
         *                       .
         *                       .
         *                       .
         *                       1
         *                       1
         *                       1
         */
        // row-column constraints
        for(int r = 1; r <= S; r++){
            for(int c = 1; c <= S; c++, hBase++){
                for(int n = 1; n <= S; n++){
                    R[getIdx(r, c, n)][hBase] = 1;
                }
            }
        }
        //1列目から9列目まで階段状に1を代入
        /* 1 2 3 4 5 6 7 8 9
         * 1
         *   1
         *     1
         *       1
         *         1
         *           1
         *             1
         *               1
         *                 1
         * 1
         *   1
         *     ...
         *     
         *     .
         *     .
         *     .
         */
        // row-number constraints
        for(int r = 1; r <= S; r++){
            for(int n = 1; n <= S; n++, hBase++){
                for(int c1 = 1; c1 <= S; c1++){
                    R[getIdx(r, c1, n)][hBase] = 1;
                }
            }
        }

        // 1列目から729行目まで階段状に1を代入
        /* 1 2 3 4 5 6 7 8 9 ... 729
         * 1
         *   1
         *     1
         *       1
         *         1
         *           1
         *             1
         *               1
         *                 1
         *                   ...
         *                       1
         */
        // column-number constraints

        for(int c = 1; c <= S; c++){
            for(int n = 1; n <= S; n++, hBase++){
                for(int r1 = 1; r1 <= S; r1++){
                    R[getIdx(r1, c, n)][hBase] = 1;
                }
            }
        }

        /* 1列目から9列目まで階段状に1を代入するのを3回繰り返したのち
         * 10列目から18列目まで階段状に1を代入するのを3回繰り返す。
         * というのを3回繰り返したのち(つまりこの場合は27列目まで階段が及ぶ)
         * 28行目から36行目まで階段状に1を代入する.これを729行目に達するまで行う
         * 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 ... 727 728 729
         * 1
         *   1
         *     1
         *       1
         *         1
         *           1
         *             1
         *               1
         *                 1
         *                   1
         * 1
         *   1
         *     1
         *       1
         *         1
         *           1
         *             1
         *               1
         *                 1
         *                   1
         * 1
         *   1
         *     1
         *       1
         *         1
         *           1
         *             1
         *               1
         *                 1
         *                   1
         *                     1
         *                       1
         *                         1
         *                           1
         *                             1
         *                               1
         *                                 1
         *                                   1
         *                                     1
         *                     1
         *                       1
         *                         1
         *                           1
         *                             1
         *                               1
         *                                 1
         *                                   1
         *                                     1
         *                     1
         *                       1
         *                         1
         *                           1
         *                             1
         *                               1
         *                                 1
         *                                   1
         *                                     1
         *                                       1
         *                                         1
         *                                           1
         *                                             1
         *                                               1
         *                                                 1
         *                                                   1
         *                                                     1
         *                                                       1
         *                                       1
         *                                         1
         *                                           1
         *                                             1
         *                                               1
         *                                                 1
         *                                                   1
         *                                                     1
         *                                                       1
         *                                       1
         *                                         1
         *                                           1
         *                                             1
         *                                               1
         *                                                 1
         *                                                   1
         *                                                     1
         *                                                       1
         * 1
         *   1
         *     .
         *     .
         *     .
         *                                                         1
         *                                                           1
         *                                                             1
         *                                                             .
         *                                                             .
         *                                                             .
         *                                                             
		 */
        // box-number constraints

        for(int br = 1; br <= S; br += side){
            for(int bc = 1; bc <= S; bc += side){
                for(int n = 1; n <= S; n++, hBase++){
                    for(int rDelta = 0; rDelta < side; rDelta++){
                        for(int cDelta = 0; cDelta < side; cDelta++){
                            R[getIdx(br + rDelta, bc + cDelta, n)][hBase] = 1;
                        }
                    }
                }
            }
        }

        return R;
    }

    // 数独問題においてrow行col列目の値numがEPC行列における何行目かを返すメソッド
    private int getIdx(int row, int col, int num){
        return (row - 1) * S * S + (col - 1) * S + (num - 1);
    }
    // 引数に取った数独問題の解読を行う
    protected void runSolver(int[][] sudoku){
    	// coverに数独問題を基に作られたEPC行列を代入
        int[][] cover = makeExactCoverGrid(sudoku);
        // coverとSudokuHandlerを基にダンシングリンクを生成
        DancingLinks dlx = new DancingLinks(cover, new SudokuHandler(S));
        // Knuth's Algorithm Xで解読を行う
        dlx.runSolver();
    }


}
