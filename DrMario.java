
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

public class DrMario extends JFrame implements KeyListener{

    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if(!test1.playerTurn){
                    //print(test1.needsProjectedH);
                    return false;
                }
           // System.out.println("ignored");
         //   return;
                //System.out.println("tester");
                if(e.getKeyCode()==37){
                    if(test1.moveLeft()){
                        canvas.partialRepaint(0);
                    }
                }
                if(e.getKeyCode()==39){
                    if(test1.moveRight()){
                        canvas.partialRepaint(0);
                    }
                }
                if(e.getKeyCode()==40){
                    if(test1.moveDown()){
                        canvas.partialRepaint(0);
                        //playTimer.restart();
                    }
                }
                if(e.getKeyCode()==67){
                    new Thread(new DMGame(canvas)).start();
                    canvas.repaint();
                        //playTimer.restart();
                }

                if(e.getKeyCode()==90){
                    if(test1.rotate(true)){
                        canvas.partialRepaint(0);
                    }
                }
                if(e.getKeyCode()==88){
                    if(test1.rotate(false)){
                        canvas.partialRepaint(0);
                    }
                }
                else{
                    //print(test1.needsProjectedH);
                }
            } /*else if (e.getID() == KeyEvent.KEY_RELEASED) {
                System.out.println("2test2");
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                System.out.println("3test3");
            }*/
            return false;
        }
    }


    
    int[][] gameBoard;            //Stores the actual board.  Numbers.txt shows what the ints mean.
    MyCanvas canvas;                //Where the game is drawn to
    int sleep = 600;                //This is how long the timer delays after each event firing.  This controls game speed.
    int numPlayers = 1;             //Self explanatory?
    DMGame test1;
  
    //--------Constructors-------------------------------------------------------------
    public DrMario(){
        
       // JFrame frame = new JFrame("Test");
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        //getContentPane().addKeyListener(this);
        
/*                
        playTimer = new Timer(sleep,this);        
        gameBoard = new int[][]{
        /*	Before text replace

                {0,y,y,0,0,0,0,0},
                {r,y,0,0,0,0,0,0},
                {r,b,0,r,r,0,0,0},
                {b,y,0,0,0,0,0,0},

                {b,b,0,0,0,0,0,0},
                {y,b,0,0,0,0,0,0},
                {y,r,b,b,r,r,y,b},
                {y,r,b,y,y,r,r,y},

                {0,b,r,0,0,b,b,0},
                {b,b,0,r,r,0,0,r},
                {0,r,b,y,y,0,0,y},
                {r,y,b,b,y,r,b,0},

                {b,y,y,r,b,y,r,b},
                {y,0,r,r,b,y,0,r},
                {y,b,r,y,0,0,b,y},
                {0,y,0,0,0,0,r,b},
                };*/

             /*   {0,13,13,0,0,0,0,0},
                {7,13,0,0,0,0,0,0},
                {7,19,0,7,7,0,0,0},
                {19,13,0,0,0,0,0,0},

                {19,19,0,0,0,0,0,0},
                {13,19,0,0,0,0,0,0},
                {13,7,19,19,7,7,13,19},
                {13,7,19,13,13,7,7,13},

                {0,19,7,0,0,19,19,0},
                {19,19,0,7,7,0,0,7},
                {0,7,19,13,13,0,0,13},
                {7,13,19,19,13,7,19,0},

                {19,13,13,7,19,13,7,19},
                {13,0,7,7,19,13,0,7},
                {13,19,7,13,0,0,19,13},
                {0,13,0,0,0,0,7,19},
        };
                 {0,0,0,0,0,0,0,0},  
                {0,0,0,0,0,0,0,0},
                {0,17,16,0,7,0,0,0},
                {11,16,0,0,7,0,0,0},
                {11,16,0,11,10,0,0,0},
                {23,16,0,0,0,0,0,0},

                {23,22,0,9,0,0,21,0},
                {17,22,0,14,0,0,14,0},
                {12,6,0,0,6,6,12,18},
                {12,12,0,0,12,6,6,12},

                {0,18,6,7,0,18,18,0},
                {18,18,0,6,6,0,0,6},
                {0,6,18,12,12,0,0,12},
                {6,12,18,18,12,6,18,0},

                {18,12,12,6,18,12,6,18},
                {12,0,6,6,18,12,0,6},
                {12,18,6,12,0,0,18,12},
               // {0,17,16,0,7,0,0,0},
               // {11,16,0,0,7,0,0,0},
               // {11,16,0,11,10,0,0,0},
               // {23,16,0,0,0,0,0,0},
/*
                {23,22,0,9,0,0,21,0},
                {17,22,0,14,0,0,14,0},
                {12,6,0,0,6,6,12,18},
                {12,12,0,0,12,6,6,12},

                {0,18,6,7,0,18,18,0},
                {18,18,0,6,6,0,0,6},
                {0,6,18,12,12,0,0,12},
                {6,12,18,18,12,6,18,0},

                {18,12,12,6,18,12,6,18},
                {12,0,6,6,18,12,0,6},
                {12,18,6,12,0,0,18,12},
                
        };
        

        
       // System.out.print("---------------TestBoard---------------\n");
       // print(gameBoard);
        gameBoard = this.getNewBoard(8*8);
        bugCount = countBugs(gameBoard);
        canvas = new MyCanvas(gameBoard);
        canvas.addBoard(gameBoard);
        canvas.setSize(1024, 700);
        getContentPane().add(canvas);
        System.out.println("Bugs left: " + bugCount);
        canvas.repaint();
        try{
            Thread.sleep(sleep);
        }
            
        catch(Exception e){}
        physicsTurn();
        while(bugCount>0&&gameBoard[1][3]==0&&gameBoard[1][4]==0){
            humanTurn();
            physicsTurn();
            
           // System.out.print("---------------TestBoard-after--------------\n");
           // print(gameBoard);
        }
         System.out.print("Game over\n");
    }*/
        setSize(605, 397);
        canvas = new MyCanvas();
        //canvas.addKeyListener(this);
        while(canvas.getWidth()!=this.getWidth() || canvas.getHeight()!=this.getHeight()){
             System.out.print("*");
            canvas.setSize(605, 397);
        }
        this.getContentPane().add(canvas);
        test1 = new DMGame(canvas);
        DMGame test2 = new DMGame(test1.getGameBoard(),canvas);
        DMGame test3 = new DMGame(test1.getGameBoard(),canvas);
        DMGame test4 = new DMGame(test1.getGameBoard(),canvas);


      
        Thread t1 = new Thread(test1);
        Thread t2 = new Thread(test2);
        Thread t3 = new Thread(test3);
        Thread t4 = new Thread(test4);

        t1.start();
        //t2.start();
        for(int i=0;i<0;i++){
            new Thread(new DMGame(canvas)).start();
        }       
    }


    //---------Printing------------------------------------------------------------------
    public static void print(int[] a){
        String out = "";
        for(int i=0; i<a.length; i++){
            out+= a[i] + " ";
        }
        System.out.println(out);
    }

    public static void print(int[][] a){
        String out = "{";
        for(int i=0; i<a.length; i++){
            out+= "{";
            for(int j=0; j<a[i].length; j++){
                if(a[i][j]<10) out+= " ";
                out+=a[i][j]+",";
                }
            out+="},\n";
        }
        out+="}";
        System.out.print(out.replaceAll(",}","}"));
    }
    public static void print(int[][][] a){
        String out = "{";
        for(int k=0;k<a[0][0].length;k++){
            for(int i=0; i<a.length; i++){
                out+= "{";
                for(int j=0; j<a[i].length; j++){
                    if(a[i][j][k]<10) out+= " ";
                    out+=a[i][j][k]+",";
                    }
                out+="},  ---"+k+"\n";//8007175587
            }
            out+="}\n";
        }
        System.out.print(out.replaceAll(",}","}"));
    }
    public static String toString(int[][] a){
        String out = "";
        for(int i=0; i<a.length; i++){
            for(int j=0; j<a[i].length; j++){
                if(a[i][j]<10) out+= " ";
                out+=a[i][j]+",";
                }
            out+="\n";
        }
        return out;
    }

    public static String toString(boolean[][] a){
        String out = "";
        for(int i=0; i<a.length; i++){
            out+="|";
            for(int j=0; j<a[i].length; j++){
                    if(a[i][j]) out+="[]";
                    else out += "  ";
                }
                out+="|\n";
        }
        out += "------------------\n\n";
        return out;
    }


    public static void toString(boolean[][][] a, int c){
        String out = "";
        for(int i=0; i<a.length; i++){
            out+="|";
            for(int j=0; j<a[i].length; j++){
                    if(a[i][j][c]) out+="[]";
                    else out += "  ";
                }
                out+="|\n";
        }
        out += "------------------\n\n";
        System.out.print(out.replaceAll(",\n","\n"));
    }
    public void printCB(int[][][] a){
        String out = "";
        String out2 = "";
        for(int i=0; i<a.length; i++){
                for(int j=0; j<a[i].length; j++){
                        out+=a[i][j][0]+",";
                        out2+=a[i][j][1]+",";
                }
                out+="\n";
                out2+="\n";
        }
        System.out.print("--------------CrumbBoard-1------------\n");
        System.out.print(out.replaceAll(",\n","\n"));
        System.out.print("--------------CrumbBoard-2------------\n");
        System.out.print(out2.replaceAll(",\n","\n"));
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        //if(!humanTurn){
           // System.out.println("ignored");
         //   return;
        //}


        if(e.getKeyCode()==37){
            if(test1.moveLeft()){
                canvas.partialRepaint(0);
            }
        }
        if(e.getKeyCode()==39){
            if(test1.moveRight()){
                canvas.partialRepaint(0);
            }
        }
        if(e.getKeyCode()==40){
            if(test1.moveDown()){
                canvas.partialRepaint(0);
                //playTimer.restart();
            }
        }
        if(e.getKeyCode()==67){
            new Thread(new DMGame(canvas)).start();
            canvas.repaint();
                //playTimer.restart();

        }


        if(e.getKeyCode()==90){
            if(test1.rotate(true)){
                canvas.partialRepaint(0);
            }
        }
        if(e.getKeyCode()==88){
            if(test1.rotate(false)){
                canvas.partialRepaint(0);
            }
        }

        //throw new UnsupportedOperationException("Not supported yet.");

    }
    public void keyReleased(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    public void keyTyped(KeyEvent e) {
       // throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(String[] args){
        DrMario dm = new DrMario();
        dm.setSize(833, 405);
        //dm.getContentPane().addKeyListener(dm);

    }
}