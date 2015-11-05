// Author: Rafal Szymanski <me@rafal.io>

// Implementation of the Dancing Links algorithm for exact cover.

package SudokuSolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// ダンシングリンクという左右上下方向へのリスト
public class DancingLinks{
    class DancingNode{
        DancingNode L, R, U, D; // 左右上下にリンクしているダンシングリンク
        ColumnNode C; // thisが何列目かを表す為のノード(:=カラムノード)

        // ノードn1を現在のノードの下にリンクする
        DancingNode hookDown(DancingNode n1){
            assert (this.C == n1.C); // n1がthisと同じ列じゃない時java.lang.AssertionError
            // n1の下には元々のノードの下にリンクしていたノードをつける
            n1.D = this.D;
            // 上記でリンクしたノードを双方向にする
            n1.D.U = n1;
            // n1の上は元々のノードとする
            n1.U = this;
            // 上記でリンクしたノードを双方向にする
            this.D = n1;
            // 上下を新しくリンクしたn1を返す
            return n1;
        }

        // ノードn1を現在のノードの右にリンクする
        DancingNode hookRight(DancingNode n1){
        	// n1の右には元々のノードの右にりんくしていたノードをつける
            n1.R = this.R;
            // 上記でリンクしたノードを双方向にする。
            n1.R.L = n1;
            // n1の左は元々のノードとする
            n1.L = this;
            // 上記でリンクしたノードを双方向にする
            this.R = n1;
            // 左右を新しくリンクしたn1を返す
            return n1;
        }
        
        // 現在のノードthisをthisの左右のリンクからはずす
        void unlinkLR(){
        	// ノードの左のリンクをthisからthisの右とする
            this.L.R = this.R;
            // ノードの右のリンクをthisからthisの左とする
            this.R.L = this.L;
            // アップデート回数をインクリメント
            updates++;
        }
        // thisからリンクをはずした左右のノードをthisにリンクし直す
        void relinkLR(){
        	// thisの左右はそれぞれ右左にthisをリンクする
            this.L.R = this.R.L = this;
            // アップデート回数をインクリメント
            updates++;
        }
        // 現在のノードthisを上下のリンクからはずす
        void unlinkUD(){
        	// ノードの下のリンクをthisからthisの上とする
            this.U.D = this.D;
            // ノードの上のリンクをthisからthisの下とする
            this.D.U = this.U;
            // アップデート回数をインクリメント
            updates++;
        }
        // thisからリンクを外した左右のノードをthisにリンクし直す
        void relinkUD(){
        	// thisの上下はそれぞれ下上にthisをリンクする
            this.U.D = this.D.U = this;
            // アップデート回数をインクリメント
            updates++;
        }
        // コンストラクタ
        public DancingNode(){
        	// 左右上下thisになるよう循環する
            L = R = U = D = this;
        }
        // コンストラクタ
        public DancingNode(ColumnNode c){
            this();
            // 何列目にノードを置くかをカラムノードで指定する
            C = c;
        }
    }
    // 何列目のノードかを表す指標の為のノード(=カラムノード)
    class ColumnNode extends DancingNode{
        // 現在の列にあるノードの値が1であるノードの個数
    	int size;
    	// カラムノードの名前(何列目か)
        String name;
        
        // コンストラクタ
        public ColumnNode(String n){
            super();
            size = 0;
            name = n;
            // カラムノードCには自分自身を代入
            C = this;
        }
        // 自分自身はカラムノードである。自分自身が指している列にあるノードがある行をすべて削除する
        void cover(){
        	unlinkLR(); // まず自分自身を消去
            // 自分の列上にある行はすべて削除
            for(DancingNode i = this.D; i != this; i = i.D){
                for(DancingNode j = i.R; j != i; j = j.R){
                    j.unlinkUD();
                    j.C.size--; // 削除した分だけsizeも減る
                }
            }
            header.size--; // not part of original
        }
        // coverメソッド内で実行されるunlinkLRでthisの左右からthisにアクセスすることはできないが
        // thisからは左右上下にアクセス可能である。それを利用したrelinkUDメソッドとrelinkLRメソッドを実行する
        // そこからすべてのリンクを構成しなおす
        void uncover(){
            for(DancingNode i = this.U; i != this; i = i.U){
                for(DancingNode j = i.L; j != i; j = j.L){
                    j.C.size++; // 繋ぎなおす分だけsizeをインクリメント
                    j.relinkUD(); // リンクを切ったすべてのノードの上下を再リンク
                }
            }
            relinkLR(); // thisの左右を再リンク
            header.size++; // not part of original

        }
        
        
    }

    private ColumnNode header; // カラムノードの先頭
    private int updates = 0;
    private SolutionHandler handler;
    // 解答のノードリスト
    private List<DancingNode> answer;
    

    // アルゴリズムの心臓部
    // Knuth's Algorithm X
    private void knuthsAlgorithmX(int k){
        if (header.R == header){ // すべてのノードが消えたら
        	
        	// 解答を整理して二次元配列にするhandleSolutionにダンシングリンクを入れる
            handler.handleSolution(answer);
        } 
        else{
        	// カラムノードに含まれる1の数が最も少ないカラムノードをcに代入
            ColumnNode c = selectColumnNodeHeuristic();
            // まずc自身をダンシングリンクから外す
            c.cover();
            for(DancingNode r = c.D; r != c; r = r.D){
                answer.add(r); // cの列を解候補に追加する
                // cと共通のリンクを持つ列のノードもすべて削除
                for(DancingNode j = r.R; j != r; j = j.R){
                    j.C.cover();
                    
                }
                
                // ダンシングリンクのノードがなくなるまで再帰的に処理
                knuthsAlgorithmX(k+1);
                /*
                // 解答のノードリストにある最後のノードを削除して削除したものをrに代入
                r = answer.remove(answer.size() - 1);
                //cをrがある列のカラムノードとする(=最後の列のカラムノード)
                c = r.C;
                
                // アルゴリズムの処理上除去したノードなどの再構成を行う
                for(DancingNode j = r.L; j != r; j = j.L){
                    j.C.uncover();
                }*/
                
            }
            //c.uncover();
        }
        
        
    }

    // カラムノードのフィールドsizeの数が最も少ないカラムノードを返す
    private ColumnNode selectColumnNodeHeuristic(){
        int min = Integer.MAX_VALUE; // minに整数の中で最大の数字を入れる
        ColumnNode ret = null; // 空のカラムノードretを作る
        // 先頭のカラムノードから最後まで探索
        for(ColumnNode c = (ColumnNode) header.R; c != header; c = (ColumnNode)c.R){
            // 現在の最小値よりも小さいsizeがある場合
        	if (c.size < min){
        		// minを更新
                min = c.size;
                // retにそのカラムノードを代入
                ret = c;
            }
        }
        // retを返す
        return ret;
    }
    
    

    // 引数に取られたECP(Exact Cover Problem)行列をもとにダンシングリンクを構成する
    // ECP行列の1の値を持っている部分のみダンシングリンク化するという疎行列を形成する
    private ColumnNode makeDLXBoard(int[][] grid){
        final int COLS = grid[0].length; // ECP行列の列数
        final int ROWS = grid.length; // ECP行列の行数

        // カラムノードの先頭を"header"という名前を付けてインスタンス化
        ColumnNode headerNode = new ColumnNode("header");
        // カラムノードの集合
        ArrayList<ColumnNode> columnNodes = new ArrayList<ColumnNode>();

        // 全ての列に関してカラムノードをインスタンス化する
        // 名前は何列目かを表す
        for(int i = 0; i < COLS; i++){
        	// i列目のカラムノードをインスタンス化
            ColumnNode n = new ColumnNode(Integer.toString(i));
            // カラムノードのリストに追加
            columnNodes.add(n);
            // headerとi-1列目の間にi列目のカラムノードをリンクさせる
            headerNode = (ColumnNode) headerNode.hookRight(n);
        }
        // headerはCOLS-1列目のカラムノードとする
        //headerNode = headerNode.R.C;
        headerNode = (ColumnNode)headerNode.R;

        for(int i = 0; i < ROWS; i++){
            DancingNode prev = null;
            for(int j = 0; j < COLS; j++){
            	// ECP行列のi行j列目の値が1の場合...条件式1
                if (grid[i][j] == 1){
                	// カラムノードのリストからj列目のノードを取り出しcolに代入
                    ColumnNode col = columnNodes.get(j);
                    // j列目にダンシングノードを作る
                    DancingNode newNode = new DancingNode(col);
                    // 条件式1に初めて入る場合
                    if (prev == null){
                    	// newNodeをprevに代入
                        prev = newNode;
                    }
                    // newNode0行j列目に代入
                    // 左右上下とリンクする
                    col.U.hookDown(newNode);
                    prev = prev.hookRight(newNode);
                    // j列目に含まれる1の数をインクリメント
                    col.size++;
                }
            }
        }
        // 0列目のノードはすべて値が１だからsizeはCOLS
        headerNode.size = COLS;
        // 先頭のカラムノードを返す
        return headerNode;
    }
    
    // 値を上書きした回数を表示
    private void showInfo(){
        System.out.println("Number of updates: " + updates);
    }
    // コンストラクタ
    public DancingLinks(int[][] grid, SolutionHandler h){
        header = makeDLXBoard(grid);
        handler = h;
    }
    // 数独解読の実行
    public void runSolver(){
    	// 数値のアップデート回数を初期化
        updates = 0;
        // 解答用のリストをインスタンス化
        answer = new LinkedList<DancingNode>();
        // Knuth's Algorithm Xの実行
        knuthsAlgorithmX(0);
        // アップデート回数の表示
        showInfo();
    }

}
