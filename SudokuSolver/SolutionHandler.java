package SudokuSolver;

import java.util.List;

import SudokuSolver.DancingLinks.DancingNode;

public interface SolutionHandler{
    void handleSolution(List<DancingNode> solution);
}

// 
class SudokuHandler implements SolutionHandler{
	// 数独問題の一辺の長さを9とする
    int size = 9;

    // 解答のリストを二次元配列に変換、そして数独ソルバーのフィールドに代入
    public void handleSolution(List<DancingNode> answer){
    	// 二次元配列への変換
        int[][] result = parseBoard(answer);
        // フィールドに代入
        AbstractSudokuSolver.solvee = result;
    }

    private int[][] parseBoard(List<DancingNode> answer){
        int[][] result = new int[size][size];
        for(DancingNode n : answer){
            DancingNode rcNode = n;
            int min = Integer.parseInt(rcNode.C.name);
            // ノードnと同じ行で一番左の列のノードを見つける
            for(DancingNode tmp = n.L; tmp != n; tmp = tmp.L){
            	// 探索中のノードの列番号
                int val = Integer.parseInt(tmp.C.name);
                // 探索中のノードの列番号が探索中で最も左側にある列番号よりも小さい場合
                if (val < min){
                	// 最も左側にあるノードを列番号とともに上書きする
                    min = val;
                    rcNode = tmp;
                }
            }
            // 最も左側にあるノードの列番号
            int ans1 = Integer.parseInt(rcNode.C.name);
            // 二番目に左側にあるノードの列番号
            int ans2 = Integer.parseInt(rcNode.R.C.name);
            // 解答を入れる行を指定
            int r = ans1 / size;
            // 解答を入れる列を指定
            int c = ans1 % size;
            // r行c列目の解答
            int num = (ans2 % size) + 1;
            // 解答を入れる二次元配列resultのr行c列目に解答numを代入
            result[r][c] = num;
        }
        return result;
    }
    public SudokuHandler(int board){
    	size = board;
    }
}