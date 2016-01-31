
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.Timer;

public class DrMarioBackup extends JFrame implements KeyListener, ActionListener{
    
    private int pillI,pillJ;        //Used in certain places to track where the pill is.
    private int[] pill;             //Stores the current pill   
    private Timer playTimer;        //This is the timer that tells the piece to fall during human's turn
    private boolean humanTurn;      //TRUE while you can control the pill, false otherwise
    private int bugCount;           //How many bugs are still on the board
    int[][] gameBoard;            //Stores the actual board.  Numbers.txt shows what the ints mean.
    MyCanvas canvas;                //Where the game is drawn to
    int sleep = 600;                //This is how long the timer delays after each event firing.  This controls game speed.
    int numPlayers = 1;             //Self explanatory?
  
    //--------Constructors-------------------------------------------------------------
    public DrMarioBackup(){
        
       // JFrame frame = new JFrame("Test");
        addKeyListener(this);
        setSize(400,800);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
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
        };*/
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
                {12,18,6,12,0,0,18,12},*/
                
        };
        

        
       // System.out.print("---------------TestBoard---------------\n");
       // print(gameBoard);
        bugCount = countBugs(gameBoard);
        canvas = new MyCanvas(gameBoard);
        canvas.setSize(1024, 700);
        getContentPane().add(canvas);
        System.out.println("Bugs left: " + bugCount);
        canvas.repaint();
        try{
            Thread.sleep(sleep);
        }
            
        catch(Exception e){}
        cpuTurn();
        while(bugCount>0&&gameBoard[1][3]==0&&gameBoard[1][4]==0){
            humanTurn();
            cpuTurn();
            
           // System.out.print("---------------TestBoard-after--------------\n");
           // print(gameBoard);
        }
         System.out.print("Game over\n");
    }

    //-------------- Game Controls-----------------------------------------------
    public boolean moveDown(){
        switch(gameBoard[pillI][pillJ]%6){
            case 2: 
                if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0){              //U, bring it down
                    gameBoard[pillI+1][pillJ] = gameBoard[pillI][pillJ];
                    gameBoard[pillI][pillJ] = gameBoard[pillI-1][pillJ];
                    gameBoard[pillI-1][pillJ] = 0;
                    pillI++;
                    playTimer.restart();
                    return true;
                }
                return false;
            case 5:
                if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0&&gameBoard[pillI+1][pillJ+1]==0){         // (                      //(, bring it down
                    gameBoard[pillI+1][pillJ] = gameBoard[pillI][pillJ];
                    gameBoard[pillI+1][pillJ+1] = gameBoard[pillI][pillJ+1];
                    gameBoard[pillI][pillJ] = 0;
                    gameBoard[pillI][pillJ+1] = 0;
                    pillI++;
                    playTimer.restart();
                    return true;
                }
                return false; 
        }
        return false;      
    }
    public boolean moveLeft(){
        switch(gameBoard[pillI][pillJ]%6){
            case 2: 
                if(pillJ>0&&gameBoard[pillI][pillJ-1]==0&&gameBoard[pillI-1][pillJ-1]==0){                               //U, bring it down
                    gameBoard[pillI][pillJ-1] = gameBoard[pillI][pillJ];
                    gameBoard[pillI-1][pillJ-1] = gameBoard[pillI-1][pillJ];
                    gameBoard[pillI][pillJ] = 0;
                    gameBoard[pillI-1][pillJ] = 0;
                    pillJ--;
                    return true;                    
                }
                return false;
            case 5:
                if(pillJ>0&&gameBoard[pillI][pillJ-1]==0){         // (                      //(, bring it over
                    gameBoard[pillI][pillJ-1] = gameBoard[pillI][pillJ];
                    gameBoard[pillI][pillJ] = gameBoard[pillI][pillJ+1];
                    gameBoard[pillI][pillJ+1] = 0;
                    pillJ--;
                    return true;
                }
                return false; 
        }
        return false;        
    }
    public boolean moveRight(){
        switch(gameBoard[pillI][pillJ]%6){
            case 2: 
                if(pillJ<gameBoard[0].length-1&&gameBoard[pillI][pillJ+1]==0&&gameBoard[pillI-1][pillJ+1]==0){  //U, bring it down
                    gameBoard[pillI][pillJ+1] = gameBoard[pillI][pillJ];
                    gameBoard[pillI-1][pillJ+1] = gameBoard[pillI-1][pillJ];
                    gameBoard[pillI][pillJ] = 0;
                    gameBoard[pillI-1][pillJ] = 0;
                    pillJ++;
                    return true;
                }
                return false;
            case 5:
                if(pillJ<gameBoard[0].length-2&&gameBoard[pillI][pillJ+2]==0){        //(, bring it over
                    gameBoard[pillI][pillJ+2] = gameBoard[pillI][pillJ+1];
                    gameBoard[pillI][pillJ+1] = gameBoard[pillI][pillJ];
                    gameBoard[pillI][pillJ] = 0;
                    pillJ++;
                    return true;
                }
                return false;
        }
        return false;        
    }
    public boolean rotate(boolean counterClockwise){ //This rotates the pill right (clockwise) unless counterClockwise==true, which will rotate it the other way.  Returns TRUE if rotation was made
        switch(gameBoard[pillI][pillJ]%6){
            case 2: 
                if(pillJ<gameBoard[0].length-1&&gameBoard[pillI][pillJ+1]==0){  //U, if space to right is open
                    gameBoard[pillI][pillJ+1] = gameBoard[pillI-1][pillJ]+1;
                    gameBoard[pillI][pillJ] = gameBoard[pillI][pillJ]+3;
                    gameBoard[pillI-1][pillJ] = 0;
                }
                else if(pillJ>0&&gameBoard[pillI][pillJ-1]==0){                 //U, if right blocked but left open
                    gameBoard[pillI][pillJ-1] = gameBoard[pillI][pillJ]+3;
                    gameBoard[pillI][pillJ] = gameBoard[pillI-1][pillJ]+1;
                    gameBoard[pillI-1][pillJ] = 0;
                    pillJ--;
                }
                else{
                    return false;
                }
                break;
                
            case 5:
                if(gameBoard[pillI-1][pillJ]==0){         // (     , if directly above is not blocked
                    gameBoard[pillI-1][pillJ] = gameBoard[pillI][pillJ]-2;
                    gameBoard[pillI][pillJ] = gameBoard[pillI][pillJ+1]-2;
                    gameBoard[pillI][pillJ+1] = 0;
                }
                else if(gameBoard[pillI-1][pillJ+1]==0){    // (, if above blocked but above-right is not
                    gameBoard[pillI-1][pillJ+1] = gameBoard[pillI][pillJ]-2;
                    gameBoard[pillI][pillJ+1] = gameBoard[pillI][pillJ+1]-2;
                    gameBoard[pillI][pillJ] = 0;
                    pillJ++;
                }
                else if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0){ // ( bottom isn't blocked
                    gameBoard[pillI][pillJ] = gameBoard[pillI][pillJ]-2;
                    gameBoard[pillI+1][pillJ] = gameBoard[pillI][pillJ+1]-2;
                    gameBoard[pillI][pillJ+1] = 0;
                    pillI++;
                }
                else if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ+1]==0){ // ( bottom right isn't blocked
                    gameBoard[pillI+1][pillJ+1] = gameBoard[pillI][pillJ+1]-2;
                    gameBoard[pillI][pillJ+1] = gameBoard[pillI][pillJ]-2;
                    gameBoard[pillI][pillJ] = 0;
                    pillI++;
                    pillJ++;
                }
                else{
                    return false;
                }
                break;
        }
        if(counterClockwise){ //we need to switch the colors
            int color1 = gameBoard[pillI][pillJ]/6;
            int color2;
            switch(gameBoard[pillI][pillJ]%6){
                case 2: color2 = gameBoard[pillI-1][pillJ]/6;
                        gameBoard[pillI][pillJ] = color2*6 + 2;
                        gameBoard[pillI-1][pillJ] = color1*6 + 3;
                        break;
                case 5: color2 = gameBoard[pillI][pillJ+1]/6;
                        gameBoard[pillI][pillJ] = color2*6 + 5;
                        gameBoard[pillI][pillJ+1] = color1*6 + 4;
                        break;
            }
            
        }
        return true; 
    }

    //--------Game Flow----------------------------------------------------------------
    public void humanTurn(){
        humanTurn = true;
        pill = getNextPill();
        boolean gameOver = false;//gameBoard[1][3]!=0 || gameBoard[1][4] !=0;
        gameBoard[1][3] = pill[0];
        gameBoard[1][4] = pill[1];
        playTimer.start();
        if(gameOver){
            System.exit(0);
        }
        pillI = 1;
        pillJ = 3;
        canvas.repaint();
        while(humanTurn){
            try{
                Thread.sleep(50);
            }
            catch(Exception e){}
        }
    }
    public void cpuTurnOrig(){
        boolean loopAgain = true;
        while(loopAgain){
            while(settle(gameBoard)){ //Settle one step, redraw if it moves
                canvas.repaint();
                try{
                    Thread.sleep(sleep);
                }
                catch(Exception e){}

               // System.out.print("---------------Settling---------------\n");
               // print(gameBoard);
            }
            loopAgain = killBlocks(gameBoard);  //Board has settled, so find and kill matches, and if you do, go try to settle again
            if(loopAgain){
                canvas.repaint();
            }
            try{
                Thread.sleep(sleep);
            }
            catch(Exception e){}
        }
    }
    public void cpuTurn(){        
        boolean loopAgain = false;
        do{
            while(settle(gameBoard)){                            //Attempt to settle one step, redraw if successful
                canvas.repaint();
                try{Thread.sleep(sleep);} catch(Exception e){}
            }
            loopAgain = killBlocks(gameBoard);                   //Attempt to kill blocks, redraw if successful
            if(loopAgain){
                canvas.repaint();
                try{Thread.sleep(sleep);} catch(Exception e){}
            }
        }while(loopAgain);
    }

    //--------Game Engine Functions-----------------------------------------------------
    public boolean killBlocks(int[][] b){
        //How this algorithm works - it scans through the board, dropping a crumb (an int) on each element
        //in the array.  If an element is of different color than the previous, we start dropping a new type of
        //crumb(different int) and recall how many of the previous type of crumb we dropped.  If the number is
        // >3, we can add that crumbtype to a list of elements to "kill" (zero) later.  We don't kill them instantly
        //because 4 in a row may exist horizontally & vertically, and you don't want to get rid of your evidence
        
        int       crumbs = 0;               //Used to keep track of how many consecutive colors in a row
        int       crumbType = 0;            //Used to go back and kill stuff after finding 4+ in a row
        int       currentColor = b[0][0]/6; // 0 = blank, 1 = red, 2 =  yellow, 3 = blue

        int[][][] crumbBoard = new int[b.length][b[0].length][2];  //This is where the crumbs are dropped
        int[]     killList = new int[10];                       //List of crumbTypes to kill.
        int       killListIterator = 0;                         //For traversing the killList

        //Loop 1: scans horizontally
        for(int i=0; i<b.length; i++){
            currentColor = -1; //This is done on each new line so the last color in one row won't be considered consecutive with the first color in the next row.  This throws you down to the else statement below
            for(int j=0; j<b[i].length; j++){
                if(b[i][j]==0){//So we ignore blank spots
                    currentColor = -1;
                    continue;
                }
                if(b[i][j]/6 == currentColor){
                    crumbs++;
                }
                else{                                              //Go here if a different color is found
                    if(crumbs > 3){
                        killList[killListIterator++] = crumbType;
                    }
                    currentColor = b[i][j]/6;
                    crumbType ++;
                    crumbs = 1;
                }
                crumbBoard[i][j][0] = crumbType;//drop a crumb
            }
        }
 
       // System.out.print("-------KillList-1----------\n");
       // print(new int[][] {killList});

        //Loop 2: scans vertically
        for(int j=0; j<b[0].length; j++){
            currentColor = -1; //This throws you down to the else statement below
            for(int i=0; i<b.length; i++){
                if(b[i][j]==0){
                    currentColor = -1;
                    continue;
                }
                if(b[i][j]/6 == currentColor){
                    crumbs++;
                }
                else{
                    if(crumbs > 3){
                        killList[killListIterator++] = crumbType;
                    }
                    currentColor = b[i][j]/6;
                    crumbType ++;
                    crumbs = 1;
                }
                crumbBoard[i][j][1] = crumbType;//drop a crumb
            }
        }
        if(crumbs > 3){
            killList[killListIterator++] = crumbType;
        }

      // printCB(crumbBoard);
     //  System.out.print("-------vertical KillList-2----------\n");
     //  print(new int[][] {killList});
        
        //Now we need to kill the stuff
        if(killList[0]!=0){ //if there exists any kills on the list
            for(int i=0; i<b.length; i++){
                 for(int j=0; j<b[0].length; j++){
                    for(int k=0; killList[k]!=0; k++){ 
                        if(killList[k]==crumbBoard[i][j][1] || killList[k]==crumbBoard[i][j][0]){ //If the crumbtype is on the kill list
                           
                            switch (b[i][j]%6) { //This switch statement handles the breaking of pills in half
                                case 2:  b[i-1][j]-=2; break; // If you kill bottom of pill, make top half turn into ()
                                case 3:  b[i+1][j]--; break;  // If you kill top of pill, make bottom half turn into ()
                                case 4:  b[i][j-1]-=4;break;  // if you kill right half of pill, turn left into ()
                                case 5:  b[i][j+1]-=3;break;  // If you kill left half of pill, turn right into ()
                            }
                            b[i][j] = 0; //The KILLING!!
                        }
                    }
                }      
            }
            bugCount = countBugs(gameBoard);
            System.out.println("Bugs left: " + bugCount);
        }
        else{
            return false; //Nothing to kill
        }        
        return true;// Killed at least one thing;
    }    
    public boolean settle(int[][] b){// this lets everything fall one step, returns true if there was any movement
        boolean movement = false;           // if b is modified(aka, pieces do fall), this will turn true
        //boolean[][] fall = new boolean[b.length][b[0].length];
        
        for(int i=b.length-2; i>-1; i--){  //Starting from row above the bottom row, since bottom row can't fall
            for(int j=0; j<b[0].length; j++){  //Working from left to right                
                int type = b[i][j]%6;          //Gets the shape of the piece
                switch (type) {
                    case 1:                                            
                    case 2:  if((b[i+1][j]==0)){        //()& U will fall if under is empty
                                b[i+1][j] = b[i][j];
                                if(type==2){              // If type is U, go ahead and bring down the upper half too
                                    b[i][j] = b[i-1][j];
                                    b[i-1][j] = 0;
                                }
                                else{
                                    b[i][j] = 0;
                                }
                                movement = true;
                             }
                             break;          
                    case 5:  if((b[i+1][j]==0)&&(b[i+1][j+1]==0)){     //(   , and we don't need a case 4, b/c this case
                                  b[i+1][j] = b[i][j];                 // will handle both (since it always sees the left
                                  b[i+1][j+1] = b[i][j+1];             //side first
                                  b[i][j] = 0;
                                  b[i][j+1] = 0;
                                  j++;                                 //to skip over the other half of the pill
                                  movement = true;
                             }
                             break;
                }  
                if(i==0&&b[i][j]!=0){
                    b[i+1][j]--;
                    b[i][j] = 0;
                }
            }
        }
        return movement;
    }    
    public int[] getNextPill(){
        int i = ((int)Math.ceil(Math.random()*3))*6+5;
        int j = ((int)Math.ceil(Math.random()*3))*6+4;
        return new int[] {i,j};
        
    }
    public int countBugs(int[][] b){
        int bugs=0;
        for(int i=0; i<b.length; i++){
            for(int j=0; j<b[0].length; j++){
                if(b[i][j]%6==0&&b[i][j]!=0){
                    bugs++;
                }
            }
        }
        return bugs;
    }    
    
    //---------Printing------------------------------------------------------------------
    public void print(int[][] a){
        String out = "";
        for(int i=0; i<a.length; i++){
            for(int j=0; j<a[i].length; j++){
                    out+=a[i][j]+",";
                }
                out+="\n";
        }
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
    
    //---------Event Handling------------------------------------------------------------  
    public void actionPerformed(ActionEvent e) {
        if(moveDown()){
            canvas.repaint();
        }
        else{
            playTimer.stop();
            humanTurn = false;
        }
/*            switch(gameBoard[pillI][pillJ]%6){
                case 2: 
                    if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0){                               //U, bring it down
                        gameBoard[pillI+1][pillJ] = gameBoard[pillI][pillJ];
                        gameBoard[pillI][pillJ] = gameBoard[pillI-1][pillJ];
                        gameBoard[pillI-1][pillJ] = 0;
                        pillI++;
                        
                    }
                    else{
                        playTimer.stop();
                        humanTurn = false;
                    }
                    break;
                case 5:
                    if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0&&gameBoard[pillI+1][pillJ+1]==0){         // (                      //(, bring it down
                        gameBoard[pillI+1][pillJ] = gameBoard[pillI][pillJ];
                        gameBoard[pillI+1][pillJ+1] = gameBoard[pillI][pillJ+1];
                        gameBoard[pillI][pillJ] = 0;
                        gameBoard[pillI][pillJ+1] = 0;
                        pillI++;
                    }
                    else{
                        playTimer.stop();
                        humanTurn = false;
                    }
                    break;
            }
            canvas.repaint();
            //canvas.repaint(200,200,200,200);*/
     
    }
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyCode());
        if(!humanTurn){
           // System.out.println("ignored");
            return;
        }
        if(e.getKeyCode()==37){
            if(moveLeft()){
                canvas.repaint();
            }
        }
        if(e.getKeyCode()==39){
            if(moveRight()){
                canvas.repaint();
            }
        }
        if(e.getKeyCode()==40){
            if(moveDown()){
                canvas.repaint();
                playTimer.restart();
            }
        }       
        if(e.getKeyCode()==90){
            if(rotate(true)){
                canvas.repaint();
            }
        }
        if(e.getKeyCode()==88){
            if(rotate(false)){
                canvas.repaint();
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
        new DrMario();
    }
}