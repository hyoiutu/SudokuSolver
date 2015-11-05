package SudokuSolver;

// 数独ソルバーの抽象クラス
abstract class AbstractSudokuSolver{
    
	// 一辺のマス数
    protected int S = 9;
    // 1ボックス当たりの一辺のマス数
    protected int side = 3;
    
    // 外部に解答を渡すための二次元配列
    protected static int[][] solvee;
    
    // 数独ソルバーの実行
    protected abstract void runSolver(int[][] sudoku);
    
    // 解読開始
    public void solve(int[][] sudoku){
        S = sudoku.length;
        side = (int)Math.sqrt(S);
        runSolver(sudoku);
    }   
}
