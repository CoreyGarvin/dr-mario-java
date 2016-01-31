import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/** An instance of a game of Dr. Mario.  
 * @author Corey
 */
public class DMGame implements Runnable, ActionListener{
    private int gameID =-1;         //Used so the canvas can tell where repaint calls are coming from


    private int pillI,pillJ;        //Used in certain places to track where the pill is.
    private int[] pill;             //Stores the current pill
    public int[] nextPill;         //Used for seeing the pill you'll get next
    private Timer playTimer;        //This is the timer that tells the piece to fall during human's turn
    public boolean playerTurn;      //TRUE while you can control the pill, false otherwise
    private boolean cpuPlayer=true;             // AI will play in human's spot
     static boolean predeterminedPills= true;
    private int bugCount;           //How many bugs are still on the board
    int[][] gameBoard;              //Stores the actual board.  Numbers.txt shows what the ints mean.
    int[][] pillBoard;              //Pill moves around on this board while it is player's turn, then gets laid on gameBoard
    
    public int[] bugsPerColumn;
    
    //for recording game progress
    public int turn =0;
    public int[] thisTurn;
    public static int[][] pills;  // a predeterminted list of pills


    int sleep = 500;                //This is how long the timer delays after each event firing.  This controls game speed.
    int numPlayers = 1;             //I'm thinking this is how many players per board, for coop stuff possibly
    MyCanvas canvas;                //So each DM Game can call repaint()
    
    //static String[] choiceText;
    //int ct = 0;
   
    //Used for AI
    int reachableCount;  //how many reachable cells there are
    boolean[][] reachables; //map of reachable areas on the gameboard (true=reachable)
    int [][][] needsProjectedV;
    int [][][] needsProjectedH;
    public boolean[][] bugStackProjectedV;

    public int[][] gbc;
    boolean[][] reachChecked; //has the cell already been checked for reachability (the reachability checking algorithm looks left & right.  this keeps that from happening forever
    int[][][] needsV;  //each element needs[][] will be an int[] listing the pieces that would be beneficial to be in this spot
    int[][][] needsH;
    public boolean[][][] bugStackV; // whenever a need is placed, a boolean is placed on this board if the the bordering stack contains a bug
    public boolean[][][] bugStackH;
    public int[][] bugStackDepthV;
    public int[][] bugStackDepthH;
    
    public DMGame(){
        this(getNewBoard(10),null);
    }
    public DMGame(MyCanvas canvas){
        this(getNewBoard(20),canvas);
    }
    public DMGame(int[][] gameBoard){
        this(gameBoard,null);
    }
    public DMGame(int[][] gameBoard, MyCanvas canvas){
        //ct =0;
        
//        pills = new int[10000][2];
//        System.out.println("{");
//        for(int i=0;i<10000;i++){
//            pills[i] = getNextPill();
//            System.out.println("{"+pills[i][0]+","+pills[i][1]+"},");
//        }
//        System.out.println("}");System.exit(0);
        if(predeterminedPills) pills = Pills.pills;
        this.gameBoard = gameBoard;
        this.pillBoard = new int[gameBoard.length][gameBoard[0].length];
        //gbc = new int[gameBoard.length][gameBoard[0].length];
        playTimer = new Timer(sleep,this);
        setTargetCanvas(canvas);
    }
    public void run() {
        
        bugCount = 7;//countBugs(gameBoard);
        nextPill = getNextPill();
        System.out.println("Bugs left: " + bugCount);
        try{
            Thread.sleep(sleep);
        }    
        catch(Exception e){}
        physicsTurn();
        while(bugCount>0&&gameBoard[1][3]==0&&gameBoard[1][4]==0){ //while another pill can fit in
            playerTurn(); 
            physicsTurn();        
        }
         System.out.print("Game over\n");
         if(canvas!=null)canvas.repaint();
    
    }

    //--------------Piece Movement Controls-----------------------------------------------    
    public boolean moveDown(){  //Checks underneith the pill, returns true if it was able move the pill down
        switch(pillBoard[pillI][pillJ]%6){
            case 2: 
                if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0){              //U, bring it down
                    pillBoard[pillI+1][pillJ] = pillBoard[pillI][pillJ];
                    pillBoard[pillI][pillJ] = pillBoard[pillI-1][pillJ];
                    pillBoard[pillI-1][pillJ] = 0;
                    pillI++;
                    playTimer.restart();
                    return true;
                }
                return false;
            case 5:
                if(pillI<gameBoard.length-1&&gameBoard[pillI+1][pillJ]==0&&gameBoard[pillI+1][pillJ+1]==0){   //(, bring it down
                    pillBoard[pillI+1][pillJ] = pillBoard[pillI][pillJ];
                    pillBoard[pillI+1][pillJ+1] = pillBoard[pillI][pillJ+1];
                    pillBoard[pillI][pillJ] = 0;
                    pillBoard[pillI][pillJ+1] = 0;
                    pillI++;
                    playTimer.restart();
                    return true;
                }
                return false; 
        }
        return false;      
    }
    public boolean moveLeft(){
        switch(pillBoard[pillI][pillJ]%6){ //Queries the shape of the piece
            case 2: 
                if(pillJ>0 && gameBoard[pillI][pillJ-1]==0 && gameBoard[pillI-1][pillJ-1]==0){     //U, bring it left
                    pillBoard[pillI][pillJ-1] = pillBoard[pillI][pillJ];
                    pillBoard[pillI-1][pillJ-1] = pillBoard[pillI-1][pillJ];
                    pillBoard[pillI][pillJ] = 0;
                    pillBoard[pillI-1][pillJ] = 0;
                    pillJ--;
                    return true;                    
                }
                return false;
            case 5:
                if(pillJ>0 && gameBoard[pillI][pillJ-1]==0){          //(, bring it left
                    pillBoard[pillI][pillJ-1] = pillBoard[pillI][pillJ];
                    pillBoard[pillI][pillJ] = pillBoard[pillI][pillJ+1];
                    pillBoard[pillI][pillJ+1] = 0;
                    pillJ--;
                    return true;
                }
                return false; 
        }
        return false;        
    }
    public boolean moveRight(){
        switch(pillBoard[pillI][pillJ]%6){
            case 2: 
                if(pillJ < gameBoard[0].length-1 && gameBoard[pillI][pillJ+1]==0 && gameBoard[pillI-1][pillJ+1]==0){  //U, bring it down
                    pillBoard[pillI][pillJ+1] = pillBoard[pillI][pillJ];
                    pillBoard[pillI-1][pillJ+1] = pillBoard[pillI-1][pillJ];
                    pillBoard[pillI][pillJ] = 0;
                    pillBoard[pillI-1][pillJ] = 0;
                    pillJ++;
                    return true;
                }
                return false;
            case 5:
                if(pillJ < gameBoard[0].length-2 && gameBoard[pillI][pillJ+2]==0){        //(, bring it over
                    pillBoard[pillI][pillJ+2] = pillBoard[pillI][pillJ+1];
                    pillBoard[pillI][pillJ+1] = pillBoard[pillI][pillJ];
                    pillBoard[pillI][pillJ] = 0;
                    pillJ++;
                    return true;
                }
                return false;
        }
        return false;        
    }

    public boolean rotate(boolean counterClockwise){ //This rotates the pill right (clockwise) unless counterClockwise==true, which will rotate it the other way.  Returns new coordinates
        //pb is the locally scoped version of pillBoard, gb for gameboard

        switch(pillBoard[pillI][pillJ]%6){
            case 2: 
                if(pillJ<gameBoard[0].length-1&&gameBoard[pillI][pillJ+1]==0){  //U, if space to right is open
                    pillBoard[pillI][pillJ+1] = pillBoard[pillI-1][pillJ]+1;
                    pillBoard[pillI][pillJ]   = pillBoard[pillI][pillJ]+3;
                    pillBoard[pillI-1][pillJ] = 0;
                }
                else if(pillJ>0&&gameBoard[pillI][pillJ-1]==0){                 //U, if right blocked but left open
                    pillBoard[pillI][pillJ-1] = pillBoard[pillI][pillJ]+3;
                    pillBoard[pillI][pillJ]   = pillBoard[pillI-1][pillJ]+1;
                    pillBoard[pillI-1][pillJ] = 0;
                    pillJ--;
                }
                else{
                    return false;
                }
                break;
                
            case 5:
                if(gameBoard[pillI-1][pillJ]==0){         // (     , if directly above is not blocked
                    pillBoard[pillI-1][pillJ  ] = pillBoard[pillI][pillJ  ]-2;
                    pillBoard[pillI  ][pillJ  ] = pillBoard[pillI][pillJ+1]-2;
                    pillBoard[pillI  ][pillJ+1] = 0;
                }
                else if(gameBoard[pillI-1][pillJ+1]==0){    // (, if above blocked but above-right is not
                    pillBoard[pillI-1][pillJ+1] = pillBoard[pillI][pillJ]-2;
                    pillBoard[pillI][pillJ+1]   = pillBoard[pillI][pillJ+1]-2;
                    pillBoard[pillI][pillJ]     = 0;
                    pillJ++;
                }
                else if(pillI<gameBoard.length-1 && gameBoard[pillI+1][pillJ]==0){ // ( bottom isn't blocked
                    pillBoard[pillI][pillJ]   = pillBoard[pillI][pillJ]-2;
                    pillBoard[pillI+1][pillJ] = pillBoard[pillI][pillJ+1]-2;
                    pillBoard[pillI][pillJ+1] = 0;
                    pillI++;
                }
                else if(pillI<gameBoard.length-1 && gameBoard[pillI+1][pillJ+1]==0){ // ( bottom right isn't blocked
                    pillBoard[pillI+1][pillJ+1] = pillBoard[pillI][pillJ+1]-2;
                    pillBoard[pillI][pillJ+1]   = pillBoard[pillI][pillJ]-2;
                    pillBoard[pillI][pillJ]     = 0;
                    pillI++;
                    pillJ++;
                }
                else{
                    return false;
                }
                break;
        }
        if(counterClockwise){ //we need to switch the colors
            int color1 = pillBoard[pillI][pillJ]/6;
            int color2;
            switch(pillBoard[pillI][pillJ]%6){
                case 2: color2 = pillBoard[pillI-1][pillJ]/6;
                        pillBoard[pillI][pillJ]   = color2*6 + 2;
                        pillBoard[pillI-1][pillJ] = color1*6 + 3;
                        break;
                case 5: color2 = pillBoard[pillI][pillJ+1]/6;
                        pillBoard[pillI][pillJ] = color2*6 + 5;
                        pillBoard[pillI][pillJ+1] = color1*6 + 4;
                        break;
            }
            
        }
        return true;
    }
    public boolean warpTo(int i, int j){
        if (gameBoard[i][j] !=0) {
            System.out.println("warpTo("+i+","+j+") location that's already filled with a " +gameBoard[i][j] + ", piece 1");
            System.exit(11);
        }
        if(gameBoard[getPillCoordinates()[2]][getPillCoordinates()[3]] !=0){
            System.out.println("warpTo("+i+","+j+") location that's already filled with a " +gameBoard[i][j] + ", piece 1");
            System.exit(11);
        }

        int temp1 = pillBoard[pillI][pillJ]; // moves 1st half of pill to temp
        int temp2 = pillBoard[getPillCoordinates()[2]][getPillCoordinates()[3]]; //2nd

        
        pillBoard[getPillCoordinates()[2]][getPillCoordinates()[3]] = 0;
        pillBoard[pillI][pillJ]=0;//zero the old spots
        
        pillI = i;
        pillJ = j;
        

        pillBoard[i][j] = temp1;
        pillBoard[getPillCoordinates()[2]][getPillCoordinates()[3]] = temp2;
        return true;
    }
    
    public static int[][] clone2DArray(int[][] array) {
        int rows=array.length ;
        //int rowIs=array[0].length ;

        //clone the 'shallow' structure of array
        int[][] newArray =(int[][]) array.clone();
        //clone the 'deep' structure of array
        for(int row=0;row<rows;row++){
            newArray[row]=(int[]) array[row].clone();
        }

        return newArray;
    }
    
    public static int[][][] clone3DArray(int[][][] array) {
        int rows=array.length ;
        //int rowIs=array[0].length ;

        //clone the 'shallow' structure of array
        int[][][] newArray =(int[][][]) array.clone();
        //clone the 'deep' structure of array
        for(int row=0;row<rows;row++){
            newArray[row]=(int[][]) array[row].clone();
            for(int j=0; j<array[0].length;j++){
                newArray[row][j] = (int[]) array[row][j].clone();
            }
        }

        return newArray;
    }    

    public static boolean[][] clone2DArray(boolean[][] array) {
        int rows=array.length ;
        //int rowIs=array[0].length ;

        //clone the 'shallow' structure of array
        boolean[][] newArray =(boolean[][]) array.clone();
        //clone the 'deep' structure of array
        for(int row=0;row<rows;row++){
            newArray[row]=(boolean[]) array[row].clone();
        }

        return newArray;
    }
    
    public int[] simulateMove(int pi, int pj, int rotations){
        // [3]Bugs killed, [4]Bugs matched, [5]Projection bug matches [6]unimportant pieces matched         PRIORITES(in decreasing order)
        // [7]Unimportant pieces mismatched,[8]Projection bugs mismatched, [9]bugs mismatched
        DMGame sim = new DMGame(clone2DArray(gameBoard));
        sim.setPillBoard(clone2DArray(pillBoard));
//        sim.setReachables(clone2DArray(reachables));
//        sim.setNeedsV(clone3DArray(needsV));
//        sim.setNeedsH(clone3DArray(needsH));
        sim.setPill(pill);
        sim.setPillI(pillI);
        sim.setPillJ(pillJ);
        sim.reachableCount = reachableCount;
                    if(pi==15&&pj==4&&turn==61){
                int d=0;
            }
         return sim.startSimulationNEW(pi,pj,rotations);
    }
    public int sumAbs(int[] a){
        int sum =0;
        for(int i=0;i<a.length;i++){
            sum += Math.abs(a[i]);
        }
        return sum;
    }
    public boolean sum(boolean[] a){
        boolean sum =false;
        for(int i=0;i<a.length;i++){
            if(a[i]){
                return true;
            }
        }
        return false;
    }
    public int[] startSimulation(int pi, int pj, int rotations){ //Only call this from outside of an instance
        long start = System.currentTimeMillis();
        //int sumReachables0 = determineReachables(gameBoard); reachables have already been determined
        determineNeeds(gameBoard);
        
        int kills = 0;
        int bugsKilled = 0;
        int[] results;

        
        for(int k=0; (rotations+4)%4 != k%4; k++){       //Rotate the pill
            rotate(false);         
        }       
        warpTo(pi,pj);                                     //Place the pill
        
        
//---------------------------------Determine # of matches (experimental----------------------------------      
        int pc1 = pillBoard[pi][pj]/6-1;
        int pc2 = 999;        
        int matches = 0;
        int mismatches = 0;
        
        //Direct bug or bugstack
        int bmv=0;  //bug matches vertical  
        int bmmv =0; //bug mismatches vertical
        int bmh=0;
        int bmmh;
        
        //Bug needs filled
        int bnfv=0; //bug needs filled
        int bnfh=0;
        
        //Needs filled
        int nfv = 0;
        int nfh = 0;        
        
        //Stack of bugs with mismatches pieces on top
        int bsmv = 0; //bug stack matches vertical (there's depth on the stack)
        int bsmmv = 0;
        
        //Projection
        int pm  = 0; //projection matches
        int pbm = 0; //projection bug matches
        int pbmm = 0;
        
        boolean killable; // is there enough space to kill the pill in the direction we're orienting it .  i need to explain this better
        
        
        
        if(needsProjectedV[pi][pj][pc1] >0 || needsH[pi][pj][pc1] >2){            //Examining piece1
            matches++;
            if(Math.abs(needsH[pi][pj][pc1]) > 0){
                
            }
        }
        else if(sumAbs(needsProjectedV[pi][pj]) != 0 && bugsPerColumn[pj]>0){
           mismatches++;
        }
        if(rotations%2==0){ //pill is horizontal
            pc2 = pillBoard[pi][pj+1]/6-1;
            if(needsProjectedV[pi][pj+1][pc2]>0 || Math.abs(needsH[pi][pj+1][pc2]) >2 || Math.abs(needsV[pi][pj+1][pc2]) >0){
                matches++;
            }
            else if(sumAbs(needsProjectedV[pi][pj+1]) != 0 && bugsPerColumn[pj+1]>0){
                mismatches++;
            }
            if(pc1 == pc2 && (Math.abs(needsH[pi][pj][pc2])+ Math.abs(needsH[pi][pj+1][pc2]) > 1)){// && (Math.abs(needsH[pi][pj][pc2])+Math.abs(needsH[pi][pj+1][pc2]))>1)){
                matches = 2;
                mismatches =0;
            }
        }
        else if(pi!=1){ //pill is vertical, and not chopped off by the top row

            pc2 = pillBoard[pi-1][pj]/6-1;
            if(pc1 == pc2){
                if((needsProjectedV[pi][pj][pc1] <3 && needsProjectedV[pi][pj][pc1] > 0)){ //if needsProj is b/n 1 & 3, we are getting usage from both halves
                    matches ++;
                }
                else if(Math.abs(needsV[pi-1][pj][pc1]) > 0 || Math.abs(needsH[pi-1][pj][pc1]) > 2 ){ //matching something above, or killing something to the side
                    matches ++;
                }
            }
            else{
                if(Math.abs(needsV[pi-1][pj][pc2]) >2 || Math.abs(needsH[pi-1][pj][pc2]) >2){ //if the top pill kills stuff, we have a match
                    matches ++;
                }
                else if((needsProjectedV[pi][pj][pc1] >2 || Math.abs(needsH[pi][pj][pc1]) >2) && (pi<gameBoard.length-1)){ //if the bottom pill breaks, we only have a match if the top pill falls on a matching pill
                    int k;
                    for(k=1; (pi+k <gameBoard.length-1) && (gameBoard[pi+k][pj]/6-1)==pc1;k++){/* no loop body */}
                    while(pi+k <gameBoard.length-1 && gameBoard[pi+k][pj]==0){
                        k++;
                    }
                        // we have arrived at a cell that is different color than the ones that will be killed
                    
                    if(gameBoard[pi+k][pj]/6-1 == pc2 || Math.abs(needsH[pi][pj][pc2])>2){//The piece we reach is the same color or kills a horz set
                        matches++;
                    }
                    else if(gameBoard[pi+k][pj] !=0){
                        mismatches ++;
                    }
                    
                }
                else if(bugsPerColumn[pj]>0){
                    mismatches ++;
                }
            }
            
        }
//-------------------------------------------------------------------------------------------------------------
        int sumNeeds0=0;                                         //Gather some stats
        int sumNeedsBugStack0 = 0;
        int sumBugStackDepths0 =0;
       
        int projectionMatches=0;
        for(int i=0; i< gameBoard.length; i++){
            for(int j=0; j< gameBoard[i].length; j++){
                sumBugStackDepths0 += this.bugStackDepthV[i][j];
                for(int k=0; k< needsV[i][j].length; k++){
                    sumNeeds0 += Math.abs(needsV[i][j][k]) + Math.abs(needsH[i][j][k]);
                    if(bugStackV[i][j][k]){
                        sumNeedsBugStack0 += Math.abs(needsV[i][j][k]);
                    }
                }
            }
        }      

        for(int i=0; i < pillBoard.length; i++){         //lay pillboard on gameboard + clear pillboard
            for(int j=0; j < pillBoard[0].length; j++){ //seems like you'd do this fast without the loop
                if(pillBoard[i][j] > 0){
                    gameBoard[i][j] = pillBoard[i][j];
                    pillBoard[i][j] = 0;
                }
            }
        }
        do{                                               //Let the physics take over
            while(settle(gameBoard)){}
            results = killBlocks(gameBoard);
            kills += results[0];
            bugsKilled += results[1];
        }
        while(results[0] > 0);
//        if(kills>0){
          
//            DrMario.print(gameBoard);
//        }
        int sumReachables = 0;//determineReachables(gameBoard);                   //recalculate needs and regather stats
        determineNeeds(gameBoard);
        int sumNeeds=0;                                                 
        int sumNeedsBugStack = 0;
        int sumBugStackDepths =0;
        int sumBugs =0;
        int gameStatus =0;  //-1 =lose, 1 = win
        
        for(int i=0; i< gameBoard.length; i++){
            for(int j=0; j< gameBoard[i].length; j++){
                if(gameBoard[i][j]>0 && gameBoard[i][j]%6==0){
                    sumBugs++;
                }
                sumBugStackDepths += this.bugStackDepthV[i][j];
                for(int k=0; k< needsV[i][j].length; k++){
                    sumNeeds += Math.abs(needsV[i][j][k]) + Math.abs(needsH[i][j][k]);
                    if(bugStackV[i][j][k] || bugStackDepthV[i][j] >0){
                        sumNeedsBugStack += Math.abs(needsV[i][j][k]);
                    }
                }
            }
        }
        if(sumBugs == 0){
            gameStatus = 1;
        }
        else if(gameBoard[1][3] + gameBoard[1][4] != 0){
            gameStatus = -1;
        }
//        long fin = System.currentTimeMillis() - start;
//        System.out.println("----------------Stats for move ("+pi+", " + pj+")---------------("+fin+"ms)");
//        System.out.println("Bug Kills: " + bugsKilled + ", Total kills: " + kills);
//        System.out.println("Bug Needs: " + sumNeedsBugStack0 + " --> " + sumNeedsBugStack);
//        System.out.println("Bug mismatches(v): " + sumBugStackDepths0 + " --> " + sumBugStackDepths);
//        System.out.println("Needs (total): " + sumNeeds0 + " --> " + sumNeeds);
//        System.out.println("Reachables: " + sumReachables0 + " --> " + sumReachables);
//        System.out.println("----------------------------------------------------------------");
//        JOptionPane.showMessageDialog(null, "", null, JOptionPane.INFORMATION_MESSAGE);
       // return new int[] {pi, pj, rotations, gameStatus, -mismatches,bugsKilled ,matches, -sumBugStackDepths,sumNeedsBugStack, kills, sumReachables }; //These seem to be important stats
        if(pi+needsV[pi][pj][pc1]<3 || pi+needsV[pi][pj][pc2]<3){
           // mismatches=10;
            //matches=-10;
        }
        return new int[] {pi, pj, rotations, gameStatus, -mismatches,matches,bugsKilled , -sumBugStackDepths,sumNeedsBugStack, kills, sumReachables,pi }; //These seem to be important stats
    }
    public int[] startSimulationNEW(int pi, int pj, int rotations){ //Only call this from outside of an instance
        long start = System.currentTimeMillis();
        //int sumReachables0 = determineReachables(gameBoard); reachables have already been determined
        determineNeeds(gameBoard);
        
        int kills = 0;
        int bugsKilled = 0;
        int[] results;

        
        for(int k=0; (rotations+4)%4 != k%4; k++){       //Rotate the pill
            rotate(false);         
        }       
        warpTo(pi,pj);                                     //Place the pill
        
        
//---------------------------------Determine # of matches (experimental----------------------------------      
        int pc1 = pillBoard[pi][pj]/6-1;
        int pc2 = 999;        
        int matchesV = 0;
        int matchesH = 0;
        int mismatchesV = 0;
        int mismatchesH = 0;
        int trash = 0;
        
        //Direct bug or bugstack
        int bmv=0;  //bug matches vertical  
        int bmmv =0; //bug mismatches vertical
        int bmh=0;
        int bmmh =0;
        
        //Bug needs filled
        int bnfv=0; //bug needs filled
        int bnfh=0;
        
        //Needs filled
        int nfv = 0;
        int nfh = 0;        
        
        //Stack of bugs with mismatches pieces on top
        int bsmv = 0; //bug stack matches vertical (there's depth on the stack)
        int bsmmv = 0;
        
        //Projection
        int pmv  = 0; //projection matches
        int pmh = 0;
        int pmhd =0;  //projection match horizontal distance
        int pmmv = 0;
        int pmmh = 0;
        int pbmv = 0; //projection bug matches vertical
        int pbsm = 0;  //bug stack w depth
        int pbmm = 0; //vertical is assumed here
        int pnf = 0;
        
        int killable; // is there enough space to kill the pill in the direction we're orienting it .  i need to explain this 
        
        boolean p1KilledV = false;  //Is pill piece 1 killed by vertical means
        boolean p1KilledH = false;
        
        boolean p2KilledV = false;
        boolean p2KilledH = false;       
        
        p1KilledV = Math.abs(needsV[pi  ][pj][pc1]) > 2;
        p1KilledH = Math.abs(needsH[pi  ][pj][pc1]) > 2;
        
        if(rotations%2==0){ //horizontal pill
            pc2 = pillBoard[pi][pj+1]/6-1;
            p1KilledH = p1KilledH || (pc1==pc2 && (Math.abs(needsH[pi][pj][pc1]) + Math.abs(needsH[pi][pj+1][pc1]) > 1));
            p2KilledV = Math.abs(needsV[pi][pj+1][pc2]) > 2;
            p2KilledH = Math.abs(needsH[pi][pj+1][pc2]) > 2 || (pc1 == pc2 && p1KilledH);           
        }
        else{   //vertical pill
            pc2 = pillBoard[pi-1][pj]/6-1;
            
            p2KilledH = Math.abs(needsH[pi-1][pj][pc2]) > 2;
            p1KilledV = p1KilledV || (pc1==pc2 && (Math.abs(needsV[pi][pj][pc1]) + Math.abs(needsH[pi-1][pj][pc1]) > 1));
            p2KilledV = Math.abs(needsV[pi-1][pj][pc2]) > 2 || (pc1==pc2 && p1KilledV);
            
            
        }
//--------------------------Examine p1 Vertical 
        if(this.turn==14&&pi==1&&pj==5&&rotations==0 ){
            int debug=0;
        }
        
        if(!(p1KilledH && !p1KilledV)){ //if p1 is killed horizontally but not vertically, we shouldn't award it as filling any vertical needs
            if(needsProjectedV[pi][pj][pc1] >0 || Math.abs(needsV[pi][pj][pc1])>0){      //We're above something that matches color, or directly under     
                pmv ++;
                if(bugStackProjectedV[pi][pj]|| (bugStackV[pi][pj][pc1] && Math.abs(needsV[pi][pj][pc1])>0)){     //It's a bug stack
                    pbmv ++;
                    pnf += needsProjectedV[pi][pj][pc1];
                    if(bugStackV[pi][pj][pc1]){             //We're directly touching it
                        matchesV++;
                        bmv ++;
                        bnfv += needsV[pi][pj][pc1];
                        nfv  += needsV[pi][pj][pc1];
                    }
                }
                else if(needsV[pi][pj][pc1] > 0){          //directly touching non bugstack (bugs could still be under though)
                    matchesV ++;
                    nfv += needsV[pi][pj][pc1];
                }
            }
            else if(sumAbs(needsProjectedV[pi][pj]) > 0){ //We're above something NOT of the same color

                pmmv ++;
                if(bugStackProjectedV[pi][pj]){              //It's a bug stack
                    pbmm ++;
                    if(sum(bugStackV[pi][pj])){             //We're directly touching it
                        bmmv ++;
                        mismatchesV++;
                    }
                }
                else if(sumAbs(needsV[pi][pj]) != 0){ //directly touching nonbugstack
                    mismatchesV++;
                }
            }
            else{                              //Not above anything, we'll call this trash
               trash++;
            }
        }
        
//-----------------------------pill is horizontal, examining p2 vertical down
        if(rotations %2 == 0){
            if(!(p2KilledH && !p2KilledV)){ //if p2 is killed horizontally but not vertically, we shouldn't award it as filling any vertical needs
                if(needsProjectedV[pi][pj+1][pc2] >0 || Math.abs(needsV[pi][pj+1][pc2])>0){      //We're above something that matches color, or directly under     
                    pmv ++;
                    if(bugStackProjectedV[pi][pj+1]|| (bugStackV[pi][pj+1][pc2] && Math.abs(needsV[pi][pj+1][pc2])>0)){     //It's a bug stack
                        pbmv ++;
                        pnf += needsProjectedV[pi][pj+1][pc2];
                        if(bugStackV[pi][pj+1][pc2]){             //We're directly touching it
                            bmv ++;
                            bnfv += needsV[pi][pj+1][pc2];
                            nfv  += needsV[pi][pj+1][pc2];
                        }
                    }
                    else if(needsV[pi][pj+1][pc2] > 0){          //directly touching non bugstack (bugs could still be under though)
                        matchesV ++;
                        nfv += needsV[pi][pj+1][pc2];
                    }
                }
                else if(sumAbs(needsProjectedV[pi][pj+1]) > 0){ //We're above something NOT of the same color

                    pmmv ++;
                    if(bugStackProjectedV[pi][pj+1]){              //It's a bug stack
                        pbmm ++;
                        if(sum(bugStackV[pi][pj+1])){             //We're directly touching it
                            bmmv++; 
                            mismatchesV++;
                        }
                    }
                    else if(sumAbs(needsV[pi][pj+1]) != 0){ //directly touching nonbugstack
                        mismatchesV++;
                    }
                }
                else{                              //Not above anything, we'll call this trash
                   trash++;
                }
            }
        }
                
                
                
//--------------------------pill is vertical, and not chopped off by the top row
        else if(pi!=1){   
            //double
            if(pc1 == pc2){
                if((needsV[pi][pj][pc1] <3 && needsV[pi][pj][pc1] > 0) || (needsV[pi-1][pj][pc1] <3 && needsV[pi-1][pj][pc1] > 0)|| (needsV[pi-1][pj][pc1] >0 && needsV[pi][pj][pc1] > 0)){ 
                    //if needs is b/n 1 & 3, we are getting usage from both halves, or, if the double piece joins 2 sets, its fully used
                    matchesV ++;
                    pmv++;
                                  //This is for getting the needs filled correct
                    nfv += Math.abs(needsV[pi-1][pj][pc1]) +1;
 
                    if(bugStackV[pi][pj][pc1] || bugStackV[pi-1][pj][pc1]){  //if top or bottom are a bug stack
                        pbmv ++;
                        bmv ++;
                        //This is for getting the needs filled correct
                        bnfv += needsV[pi-1][pj][pc1]+1;

                    }
                
                }
                else if(Math.abs(needsV[pi-1][pj][pc1]) > 0){ //matching something above
                    matchesV ++;
                    nfv += needsV[pi-1][pj][pc1];
                    if(bugStackV[pi-1][pj][pc1]){              //if that thing is a bugstack
                        bmv ++;
                        bnfv += needsV[pi-1][pj][pc1];                            
                    }                  
                }
                else if(p1KilledH &! (p2KilledV || p2KilledH)){ //bottom pill breaks due to horizontal need, does top half fall to a good spot?
                    if(needsV[pi][pj][pc2]>0){
                        matchesV++;
                        nfv += needsV[pi][pj][pc2];
                        if(bugStackV[pi][pj][pc2]){              //if that thing is a bugstack
                            bmv ++;
                            bnfv += needsV[pi][pj][pc2];
                        }
                        else if(sum(bugStackV[pi][pj])){
                            bmmv++;
                        }
                    }
                    else if(sumAbs(needsV[pi][pj]) > 0){
                        mismatchesV++;
                    }
                }

                //HORIZONTAL (pasted)
                if(needsProjectedH[pi-1][pj][pc2] >0 && (p2KilledH || (!(p1KilledV || p1KilledH) ))){
                //if we're in hor range of a match AND (  p2 is killedH OR  (it's NOT killed at all))
                    pmh ++;
                    pmhd = needsProjectedH[pi-1][pj][pc2];
                    if(Math.abs(needsH[pi-1][pj][pc2]) >0){ //directly touching matched color
                        matchesH ++;
                        if(bugStackH[pi-1][pj][pc2]){
                            bmh ++;
                            bnfh += needsH[pi-1][pj][pc2];
                        }

                    }

                }
                else if (sumAbs(needsProjectedH[pi-1][pj]) > 0){ // proj horizontal MISMATCH
                    pmmh ++;
                    if(Math.abs(needsH[pi-1][pj][pc2]) >0){ //directly touching mismatched color
                        mismatchesH++;
                         if(sum(bugStackH[pi-1][pj])){ // is it a bugstack?
                             bmmh ++;
                         }
                    }
                }
            }
            else{ //NOT double vertical pill, examining piece2
               
                //HORIZONTAL
                if(needsProjectedH[pi-1][pj][pc2] >0 && (p2KilledH || (!(p1KilledV || p1KilledH) ))){
                //if we're in hor range of a match AND (  p2 is killedH OR  (it's NOT killed at all))
                    pmh ++;
                    pmhd = needsProjectedH[pi-1][pj][pc2];
                    if(Math.abs(needsH[pi-1][pj][pc2]) >0){ //directly touching matched color
                        matchesH ++;
                        if(bugStackH[pi-1][pj][pc2]){
                            bmh ++;
                            bnfh += needsH[pi-1][pj][pc2];
                        }
                        
                    }
                    
                }
                else if (sumAbs(needsProjectedH[pi-1][pj]) > 0){ // proj horizontal MISMATCH
                    pmmh ++;
                    if(Math.abs(needsH[pi-1][pj][pc2]) >0){ //directly touching mismatched color
                        mismatchesH++;
                         if(sum(bugStackH[pi-1][pj])){ // is it a bugstack?
                             bmmh ++;
                         }
                    }
                }

                //VERTICAL
                if(p2KilledV || !(p1KilledV || p1KilledH || p2KilledH)){ //if p2 dies vertically, or remains
                    if(Math.abs(needsV[pi-1][pj][pc2]) >0){ //Match above
                        matchesV ++;
                        nfv += Math.abs(needsV[pi-1][pj][pc2]);
                        if(bugStackV[pi-1][pj][pc2] ){            //it's a bug match
                            bnfv += Math.abs(needsV[pi-1][pj][pc2]);
                            bmv++;
                        }
                    }
                    else if (sumAbs(needsV[pi-1][pj]) >0){  // MISMATCH ABOVE
                        mismatchesV ++;
                        if(sum(bugStackV[pi-1][pj]) ){
                            bmmv++;
                        }
                    }
                    else if (sumAbs(needsV[pi][pj]) >0){ //mismatch, since this piece mismatches the piece below it.
                        mismatchesV ++;
                        if(sum(bugStackV[pi][pj]) ){
                            bmmv++;
                        }
                    }
                }
                //VERT && HORIZONTAL
                else if((p1KilledV || p1KilledH) && !(p2KilledV || p2KilledH) && (pi<gameBoard.length-1)){
                    //if the bottom pill breaks & top does not, we only have a match if the top pill falls on a matching pill
                    int k;
                    for(k=1; (pi+k <gameBoard.length-1) && (gameBoard[pi+k][pj]/6-1)==pc1;k++){/* no loop body */}
                    while(pi+k <gameBoard.length-1 && gameBoard[pi+k][pj]==0){
                        k++;
                    }
                        // we have arrived at a cell that is different color than the ones that will be killed

                    //VERTICAL
                    if(gameBoard[pi+k][pj]/6-1 == pc2){//The piece we reach is the same color
                        pmv ++;
                        pnf += Math.abs(needsV[pi+k-1][pj][pc2]);
                        matchesV++;
                        if(bugStackV[pi+k-1][pj][pc2] ){
                            bnfv += Math.abs(needsV[pi+k-1][pj][pc2]);
                            bmv++;
                        }
                    }
                    else if(gameBoard[pi+k][pj] !=0){ //Piece we reached NOT same color
                        mismatchesV ++;
                        if(sum(bugStackV[pi+k-1][pj])){
                            bmmv++;
                        }
                    }

                    //HORIZONTAL
                    if(needsProjectedH[pi+k-1][pj][pc2] >0){
                        pmh++;
                        pmhd = needsProjectedH[pi+k-1][pj][pc2];
                        if(Math.abs(needsH[pi+k-1][pj][pc2])>0){
                            matchesH ++;
                            if(bugStackH[pi+k-1][pj][pc2] ){
                                bmh ++;
                                bnfh += needsH[pi+k-1][pj][pc2];
                            }
                        }
                    }
                }
//                else if(bugsPerColumn[pj]>0){
//                    mismatchesV ++;
//                }
            }
            
        }
//-------------------------------------------------------------------------------------------------------------
        int sumNeeds0=0;                                         //Gather some stats
        int sumNeedsBugStack0 = 0;
        int sumBugStackDepths0 =0;
       
        int projectionMatches=0;
        for(int i=0; i< gameBoard.length; i++){
            for(int j=0; j< gameBoard[i].length; j++){
                sumBugStackDepths0 += this.bugStackDepthV[i][j];
                for(int k=0; k< needsV[i][j].length; k++){
                    sumNeeds0 += Math.abs(needsV[i][j][k]) + Math.abs(needsH[i][j][k]);
                    if(bugStackV[i][j][k]){
                        sumNeedsBugStack0 += Math.abs(needsV[i][j][k]);
                    }
                }
            }
        }      

        for(int i=0; i < pillBoard.length; i++){         //lay pillboard on gameboard + clear pillboard
            for(int j=0; j < pillBoard[0].length; j++){ //seems like you'd do this fast without the loop
                if(pillBoard[i][j] > 0){
                    gameBoard[i][j] = pillBoard[i][j];
                    pillBoard[i][j] = 0;
                }
            }
        }
        do{                                               //Let the physics take over
            while(settle(gameBoard)){}
            results = killBlocks(gameBoard);
            kills += results[0];
            bugsKilled += results[1];
        }
        while(results[0] > 0);
//        if(kills>0){
          
//            DrMario.print(gameBoard);
//        }
        int sumReachables = 0;//determineReachables(gameBoard);                   //recalculate needs and regather stats
        determineNeeds(gameBoard);
        int sumNeeds=0;                                                 
        int sumNeedsBugStack = 0;
        int sumBugStackDepths =0;
        int sumBugs =0;
        int gameStatus =0;  //-1 =lose, 1 = win
        
        for(int i=0; i< gameBoard.length; i++){
            for(int j=0; j< gameBoard[i].length; j++){
                if(gameBoard[i][j]>0 && gameBoard[i][j]%6==0){
                    sumBugs++;
                }
                sumBugStackDepths += this.bugStackDepthV[i][j];
                for(int k=0; k< needsV[i][j].length; k++){
                    sumNeeds += Math.abs(needsV[i][j][k]) + Math.abs(needsH[i][j][k]);
                    if(bugStackV[i][j][k] || bugStackDepthV[i][j] >0){
                        sumNeedsBugStack += Math.abs(needsV[i][j][k]);
                    }
                }
            }
        }
        if(sumBugs == 0){
            gameStatus = 1;
        }
        else if(gameBoard[1][3] + gameBoard[1][4] != 0){
            gameStatus = -1;
        }
//        long fin = System.currentTimeMillis() - start;
//        System.out.println("----------------Stats for move ("+pi+", " + pj+")---------------("+fin+"ms)");
//        System.out.println("Bug Kills: " + bugsKilled + ", Total kills: " + kills);
//        System.out.println("Bug Needs: " + sumNeedsBugStack0 + " --> " + sumNeedsBugStack);
//        System.out.println("Bug mismatches(v): " + sumBugStackDepths0 + " --> " + sumBugStackDepths);
//        System.out.println("Needs (total): " + sumNeeds0 + " --> " + sumNeeds);
//        System.out.println("Reachables: " + sumReachables0 + " --> " + sumReachables);
//        System.out.println("----------------------------------------------------------------");
//        JOptionPane.showMessageDialog(null, "", null, JOptionPane.INFORMATION_MESSAGE);
       // return new int[] {pi, pj, rotations, gameStatus, -mismatches,bugsKilled ,matches, -sumBugStackDepths,sumNeedsBugStack, kills, sumReachables }; //These seem to be important stats
        if(pi+needsV[pi][pj][pc1]<3 || pi+needsV[pi][pj][pc2]<3){
           // mismatches=10;
            //matches=-10;
        }
       /* choiceText[ct++]  = new String

                ("pc1         " +     pc1               

        +"\npc2         " +     pc2               

        +"\nmatchesV    " +     matchesV          

        +"\nmatchesH    " +     matchesH          

        +"\nmismatchesV " +     mismatchesV       

        +"\nmismatchesH " +     mismatchesH       

        +"\ntrash       " +     trash   

        

        +"\nbmv         " +     bmv               

        +"\nbmmv        " +     bmmv              

        +"\nbmh         " +     bmh               

        +"\nbmmh        " +     bmmh              

        +"\nbnfv        " +     bnfv              

        +"\nbnfh        " +     bnfh              

        

        

        +"\nnfv         " +     nfv               

        +"\nnfh         " +     nfh               

                

        +"\nbsmv        " +     bsmv              

        +"\nbsmmv       " +     bsmmv             

               

        +"\npmv         " +     pmv               

        +"\npmh         " +     pmh               

        +"\npmhd        " +     pmhd              

        +"\npmmv        " +     pmmv              

        +"\npmmh        " +     pmmh              

        +"\npbmv        " +     pbmv              

        +"\npbsm        " +     pbsm              

        +"\npbmm        " +     pbmm              

        +"\npnf         " +     pnf               

        

        +"\nkillable;               +     killable");*/
        //return new int[] {pi, pj, rotations, gameStatus, -mismatchesV,matchesV,bugsKilled, -sumBugStackDepths,sumNeedsBugStack, kills, sumReachables,pi }; //These seem to be important stats
       // return new int[] {pi,pj,rotations,gameStatus, -pmmv,-pbmm,-bmmv,bugsKilled,pmv,pbmv,bmv };
        return new int[] {pi,pj,rotations,gameStatus, -pmmv + (-pbmm*2) + (-bmmv*3),bugsKilled,pmv+(pbmv*2)+(bmv*3) };

    }    
    //--------Game Flow----------------------------------------------------------------
    public void playerTurn(){
        turn++;
        playerTurn = true;
        pill = nextPill;
        nextPill = getNextPill();
        boolean gameOver = false;//gameBoard[1][3]!=0 || gameBoard[1][4] !=0;
        pillBoard[1][3] = pill[0];//placing the new pill onto the board
        pillBoard[1][4] = pill[1];
        pillI = 1;
        pillJ = 3;
        this.determineNeeds(gameBoard);
        this.determineReachables(gameBoard);
      //  DrMario.print(this.needsProjectedH;
        if(canvas!=null)canvas.repaint();
        //JOptionPane.showMessageDialog(null, "yo", null, JOptionPane.INFORMATION_MESSAGE);
        if(cpuPlayer){
            thisTurn = chooseLocation3(pill,gameBoard);
           
            System.out.println("----------------Turn "+turn+" ------------------------Pill: (" + pill[0] +"," + pill[1]+")-------------");
            //System.out.println("Pill: (" + pill[0] +"," + pill[1]+")");
           // System.out.println("Rw\\Co\\Rt\tSt\tMm\tMa\tBK\tBSD\tNBs\tKi\tSr");
            System.out.println("ijr\t\twin\t -pmmv\t-pbmm\t-bmmv\tbk\tpmv\tpbmv\tbmv");
      
            //System.out.print("("+thisTurn[0]+","+thisTurn[1]+","+thisTurn[2]+")\t\t");
            System.out.print(thisTurn[0]+","+thisTurn[1]+"\t\t");
            for(int i=3;i<thisTurn.length;i++){
                System.out.print(thisTurn[i] + "\t");
            }
            System.out.println();
            //DrMario.print(moveHere);
            //simulateMove(moveHere[0],moveHere[1],moveHere[2]);
            //simulateMove(moveHere[0],moveHere[1],moveHere[2]);
           // DrMario.print();
          //  DrMario.print(moveHere);
            //canvas.partialRepaint(gameID);
            

            //int[][] before = gameBoard.clone();

      /*      if(gameOver){
                System.exit(0);
            }*/
            if(cpuPlayer){
                if(thisTurn == null){
                    DrMario.print(gameBoard);
                }
                for(int i=0; (thisTurn[2]+4)%4 != i%4; i++){
                    rotate(false);
                    //canvas.repaint();
//                    try{
//                        Thread.sleep(10);
//                    }
//                    catch(Exception e){}           
                }
                warpTo(thisTurn[0],thisTurn[1]);
                if(canvas!=null)canvas.repaint();
                
                if(turn>0){
                    JOptionPane.showMessageDialog(null, "yo", null, JOptionPane.INFORMATION_MESSAGE);
                    this.sleep = 400;
                    
                }
                else{
                    this.sleep=0;
                }
            }
        }
       // JOptionPane.showMessageDialog(null, "yo", null, JOptionPane.INFORMATION_MESSAGE);
        playTimer.start();


//        System.out.print("Pill: ");
//        DrMario.print(pill);
//        int[][] after = gameBoard.clone();
//        int lastshape=0;
//        for(int i=0;i<after.length;i++){
//            lastshape=after[i][0];
//            for(int j=1; j< after[i].length; j++){
//                if(after[i][j]%6 != 4 && lastshape == 5){
//                    JOptionPane.showMessageDialog(null, DrMario.toString(before)+ "/n/n/n" + DrMario.toString(after), null, JOptionPane.INFORMATION_MESSAGE);
//                }
//                lastshape = after[i][j]%6;
//            }
//        }
        //JOptionPane.showMessageDialog(null, DrMario.toString(before)+ "/n/n/n" + DrMario.toString(after), null, JOptionPane.INFORMATION_MESSAGE);

//        canvas.repaintBoard(gameID);
        while(playerTurn){ //Wait until player's turn is done
            try{
                Thread.sleep(sleep);
            }
            catch(Exception e){}
        }
        for(int i=0; i < pillBoard.length; i++){ //lay pillboard on gameboard + clear pillboard
            for(int j=0; j < pillBoard[0].length; j++){
                if(pillBoard[i][j] > 0){
                    gameBoard[i][j] = pillBoard[i][j];
                    pillBoard[i][j] = 0;
                }
            }
        }
    } 

    public void physicsTurn(){
        boolean loopAgain = false;
        do{
            while(settle(gameBoard)){                            //Attempt to settle one step, redraw if successful
                if(canvas!=null)canvas.repaintBoard(gameID);
                try{Thread.sleep(sleep);} catch(Exception e){}
            }
            try{Thread.sleep(sleep);} catch(Exception e){} //delete this for norm operation
            loopAgain = killBlocks(gameBoard)[0] > 1;                   //Attempt to kill blocks, redraw if successful
            if(canvas!=null)canvas.repaintBoard(gameID);//move this 2 lines up for norm operation
            

            if(loopAgain){
                //System.out.println("Player " + (gameID+1) + ": " + countBugs(gameBoard) + " bugs left!");
                if(canvas!=null)canvas.repaintBoard(gameID);
                try{Thread.sleep(sleep);} catch(Exception e){}
            }
        }while(loopAgain);
    }

    //--------AI Functions-------------------------------------------------------------
    public int determineReachables(int[][] board){ //creates a map showing where a pill can be manuevered to from the drop point, returns # of reachable spots
        long start = System.currentTimeMillis();
        reachables = new boolean[board.length][board[0].length];
        reachables[1][3] = board[1][3] != 0;
        reachables[1][4] = board[1][4] != 0;
        resetReachChecked(board);
       
        int r =  mapReachables(1,3, board);
         //System.out.println("Map reachables took " + (System.currentTimeMillis()- start) + "ms");
         return r;

    }
    public void        resetReachChecked(int[][] board){
        reachChecked = new boolean[board.length][board[0].length];
        for(int i=0; i < board.length; i++){
            for(int j=0; j < board[i].length; j++){
                reachChecked[i][j] = board[i][j] != 0;
            }
        }
    }
    public int        mapReachables(int i, int j, int[][] board){ // call this with a known reachable location (
                                  //ie - where the pill is now) and it will map out true's
        try{                                // to all places you can get to
            
            if(!reachChecked[i][j]){
                reachChecked[i][j] = true;
                reachables[i][j] = board[i][j]==0;
                return mapReachables(i, j-1, board)
                     + mapReachables(i, j+1, board)
                     + mapReachables(i+1, j, board) + 1;
            }
            else{
                return 0;
            }
        }
        catch(Exception e){
            //e.printStackTrace();
            return 0;
        }
    }
    
    public boolean isSolidGround(int i, int j, boolean hPill, int[][] board){ //if a piece falls or is placed here, will it stick? horPill is if the piece is a horizontally-oriented pill    
        try{
            if((hPill && board[i][j+1] !=0) || (!hPill && board[i-1][j] !=0)){ //Makes sure piece can fit in the place
                return false;
            }
        }
        catch(Exception e){
            return false;

        }
        try{
            return (board[i][j]==0) && ((i == board.length-1) || (board[i+1][j] !=0) || (hPill && board[i+1][j+1] !=0));   //Makes sure the piece cannot fall after being placed
        }
        catch(Exception e){
            return false;
        }
            
    }
    
    public int[][]     determineNeeds(int[][] board){//creates a board to determine where pills are needed (for AI purposes)
        needsV = new int[board.length][board[0].length][3];
        needsH = new int[board.length][board[0].length][3];
        needsProjectedV = new int[board.length][board[0].length][3];
        needsProjectedH = new int[board.length][board[0].length][3];
        bugStackV = new boolean[board.length][board[0].length][3];  //indicates if a bug is in the stack - used it for top and bottom
        bugStackH = new boolean[board.length][board[0].length][3]; // same as above for horizontal scan, on left and right
        bugStackDepthV = new int[board.length][board[0].length];   //how many color changes are on this stack until the next bug down? i only did this for the tops on vertical so far
        boolean bugFound = false;   
        bugsPerColumn = new int[board[0].length];
        bugStackProjectedV = new boolean[board.length][board[0].length];
        

        for(int j=0; j < board[0].length; j++){ //Vertical scan
            for(int i=board.length-1 ; i>0; i--){//for each cell on the board
                if(board[i][j]!=0){                                              //find a bug/pill piece
                    
                    int k=0;
                    bugFound = board[i - (k)][j] % 6 == 0;                                           //used to know if we're in a stack of bugs (so we can prioritize bugstacks over lone pieces)
                    try{                                  
                        do{
                            if(!bugFound){                                       // Once we find a bug, bugStackV stays true
                                bugFound = board[i - (k)][j] % 6 == 0;
                            }
                            k++;
                        } while(board[i - (k)][j] / 6 == board[i][j] / 6);       //Traverse upwards until the piece is no longer the same color as the start piece
                        //canvas.repaint();
                        if(true || board[i-k][j] %6==0){                                   //If our destination cell is empty, write the count + bugStackV status
                            needsV[i-k][j][board[i][j]/6-1] += k;                       
                            bugStackV[i-k][j][board[i][j]/6-1] = bugFound;
                            if(board[i-k][j] ==0){
                                needsProjectedV[i-k][j][board[i][j]/6-1] += k;
                                bugStackProjectedV[i-k][j] = bugFound;
                            }
                        }
                        if((bugFound && board[i - (k)][j] % 6 > 0) && (i-k > 0)){ // The cell we reached is NOT blank, not a bug, but it IS a different color
                            int currentColor = board[i - (k)][j] / 6;
                            int h = k;
                            int colorChanges = 1; 
                            boolean anotherBugFound = false; // b/c of the conditional in the else
                            do{
                                h++;
                                anotherBugFound = board[i - (h)][j] % 6 == 0;
                                if(board[i - (h)][j] / 6 != currentColor){
                                    colorChanges ++;
                                    currentColor = board[i - (h)][j] / 6;
                                }
                            }while(i-h > 0 && !anotherBugFound && board[i - (h)][j] != 0); //while in bounds, no bug, no blank
                            if(board[i - (h)][j] == 0){ //If this is true, we place the # of color changes
                                bugStackDepthV[i-h][j] = colorChanges-1;
                            }                       
                        }
                        if(i<board.length-1){// && board[i+1][j] == 0){              //Place the count underneath the stack of pieces as well (if you can)
                            bugStackV[i+1][j][board[i][j]/6-1] = bugStackV[i+1][j][board[i][j]/6-1] || bugFound; 
                            if(needsV[i+1][j][board[i][j]/6-1] != 0){            //If there's already a need listed (perhaps the top of a previous stack), add the count to it (losing our ability to distinguish bn top and bottom of a stack)
                                needsV[i+1][j][board[i][j]/6-1] += k;
                            }
                            else{
                                needsV[i+1][j][board[i][j]/6-1] = -k;           // use the negative number to indicate this is the bottom of  a stack
                            }
                        }
                        i = i-k+1;                                              // Jump past the cells we just examined (k will always be > 0)
                    }
                    catch(Exception e){
                        System.out.println("OOB Vertical");
                    }
                }
                
            }
        }
        for(int i=0; i<board.length; i++){
            for(int j=0; j < board[0].length; j++){ //Horizontal scan
            //for each cell on the board
                if(board[i][j]!=0){  //find a bug/pill piece
                    if(i==1){
                        int z=32;
                    }
                    bugFound = false;
                    int k=0;
                    try{
                        do{
                            if(!bugFound){                                       // Once we find a bug, bugStackV stays true
                                bugFound = board[i][j+k] % 6 == 0;
                            }                            
                            k++;
                        } while(j+k < board[i].length && board[i][j+k] / 6 == board[i][j] / 6); //Keep moving right until the piece is no longer the same color
                        //canvas.repaint();
                        if(j+k < board[i].length && true){//board[i][j+k] %6 ==0){ //if the cell we reach is blank
                            needsH[i][j+k][board[i][j]/6-1] += k;
                            if(isSolidGround(i,j+k,false,board) && needsH[i][j+k][board[i][j]/6-1] > 2){
                                needsProjectedV[i][j+k][board[i][j]/6-1] += k;  // should we project from horizontal needs? tricky subject - i'll do it if a single piece will kill stuff
                            }  
                            bugStackH[i][j+k][board[i][j]/6-1] = bugFound;
                            if(canvas!=null)canvas.repaint();
                            if(board[i][j+k] ==0){
                                for(int h=0;((j+k+h < board[0].length) && board[i][j+k+h] ==0 ); h++){ 
                                    if(isSolidGround(i,j+k+h,false,board) || isSolidGround(i+1,j+k+h,false,board)){//vertical pills
                                        needsProjectedH[i][j+k+h][board[i][j]/6-1] = h+1; //this is laying down the distance from the horizontal pill
                                        //if(canvas!=null)canvas.repaint();
                                    }
                                    else if(isSolidGround(i,j+k+h,true,board)){
                                        needsProjectedH[i][j+k+h][board[i][j]/6-1] = h+1;
                                        h++;
                                        needsProjectedH[i][j+k+h][board[i][j]/6-1] = h+1;

                                    }
                                    else if(isSolidGround(i,j+k+h-1,true,board)&&(i==board.length-1 || board[i+1][j+k+h]!=0)){
                                        needsProjectedH[i][j+k+h][board[i][j]/6-1] = h+1;
                                    }
                                    else{
                                        break;
                                    }
                                }
                            }
                        }
                        if(j>0){// && board[i][j-1] %6 == 0){ //put the need to the left of the original piece
                            //if(i==3&&j==4)
                            if(board[i][j-1] ==0){
                                for(int h=1;((j-h >-1) && board[i][j-h] ==0 ); h++){ //go left
                                    boolean usedHor = false;
                                    boolean valid = false;
                                    if(isSolidGround(i,j-h,false,board)|| isSolidGround(i+1,j-h,false,board) || isSolidGround(i,j-h,true,board)){// || isSolidGround(i,j-h,true,board)  || isSolidGround(i,j-h-1,true,board)){                                  
                                            valid = true;                     
                                    }
                                    else if(isSolidGround(i,j-h-1,true,board)){
                                        usedHor = true;
                                        valid = true;
                                    }
                                    if(valid){
                                        if(needsProjectedH[i][j-h][board[i][j]/6-1] !=0){
                                            needsProjectedH[i][j-h][board[i][j]/6-1] = Math.min(h,needsProjectedH[i][j-h][board[i][j]/6-1] ); //if there's already an entry for distance, pick the shorter one 
                                            break;
                                        } //this is laying down the distance from the horizontal pill
                                        else{
                                            needsProjectedH[i][j-h][board[i][j]/6-1] = h;
                                            //if(canvas!=null)canvas.repaint();       
                                        }
                                        if(usedHor){
                                            h++;
                                            if(needsProjectedH[i][j-h][board[i][j]/6-1] !=0){
                                                needsProjectedH[i][j-h][board[i][j]/6-1] = Math.min(h,needsProjectedH[i][j-h][board[i][j]/6-1] ); //if there's already an entry for distance, pick the shorter one 
                                                break;
                                            } //this is laying down the distance from the horizontal pill
                                            else{
                                                needsProjectedH[i][j-h][board[i][j]/6-1] = h;
                                                //if(canvas!=null)canvas.repaint();       
                                            }
                                        }
                                    }
                                    else{
                                        break;
                                    }
                                }
                            }                            
                            bugStackH[i][j-1][board[i][j]/6-1] = bugStackH[i][j-1][board[i][j]/6-1] || bugFound;
                            if(isSolidGround(i,j-1,false,board) && needsH[i][j-1][board[i][j]/6-1] > 2){
                                needsProjectedV[i][j-1][board[i][j]/6-1] += k;  // should we project from horizontal needs? tricky subject - i'll do it if a single piece will kill stuff
                            }                             
                            if(needsH[i][j-1][board[i][j]/6-1] != 0){
                                needsH[i][j-1][board[i][j]/6-1] += k;
                            }
                            else{
                                needsH[i][j-1][board[i][j]/6-1] = -k; //do we really need to make the before/after distinction on horizontal scan?
                            }
                        }
                        j = j+k-1; //k will always be > 0

                    }
                    catch(Exception e){
                        e.printStackTrace();
                        System.out.println("OOB Horizontal, j=" +j+ ", k=" + k);
                    }
                }
            }
        }//work in progress

        for(int i=board.length-1 ; i > 0; i--){
            for(int j=0; j < board[i].length; j++){
                if(board[i][j]!=0 && board[i][j]%6==0){
                    bugsPerColumn[j] ++;
                }
                for(int k=0;k<3;k++){
                    if(i<board.length-1 && board[i][j] ==0 && board[i+1][j] ==0){
                        needsProjectedV[i][j][k] = needsProjectedV[i+1][j][k];
                        bugStackProjectedV[i][j] = bugStackProjectedV[i+1][j];
                    }
                }
            }
        }
        return null;
    }
    
    public int[][]     determineNeedsForEmptyCells(int[][] board){//creates a board to determine where pills are needed (for AI purposes)
        needsV = new int[board.length][board[0].length][3];
        needsH = new int[board.length][board[0].length][3];
        needsProjectedV = new int[board.length][board[0].length][3];
        bugStackV = new boolean[board.length][board[0].length][3];  //indicates if a bug is in the stack - used it for top and bottom
        bugStackH = new boolean[board.length][board[0].length][3]; // same as above for horizontal scan, on left and right
        bugStackDepthV = new int[board.length][board[0].length];   //how many color changes are on this stack until the next bug down? i only did this for the tops on vertical so far
        boolean bugFound = false;   
        bugsPerColumn = new int[board[0].length];
        

        for(int j=0; j < board[0].length; j++){ //Vertical scan
            for(int i=board.length-1 ; i>0; i--){//for each cell on the board
                if(board[i][j]!=0){                                              //find a bug/pill piece
                    
                    int k=0;
                    bugFound = board[i - (k)][j] % 6 == 0;                                           //used to know if we're in a stack of bugs (so we can prioritize bugstacks over lone pieces)
                    try{                                  
                        do{
                            if(!bugFound){                                       // Once we find a bug, bugStackV stays true
                                bugFound = board[i - (k)][j] % 6 == 0;
                            }
                            k++;
                        } while(board[i - (k)][j] / 6 == board[i][j] / 6);       //Traverse upwards until the piece is no longer the same color as the start piece
                        //canvas.repaint();
                        if(board[i-k][j] ==0){                                   //If our destination cell is empty, write the count + bugStackV status
                            needsV[i-k][j][board[i][j]/6-1] += k;
                            needsProjectedV[i-k][j][board[i][j]/6-1] += k;
                            bugStackV[i-k][j][board[i][j]/6-1] = bugFound;
                        }
                        else if(bugFound && board[i - (k)][j] % 6 > 0 && i-k > 0){ // The cell we reached is NOT blank, not a bug, but it IS a different color
                            int currentColor = board[i - (k)][j] / 6;
                            int h = k;
                            int colorChanges = 1; 
                            boolean anotherBugFound = false; // b/c of the conditional in the else
                            do{
                                h++;
                                anotherBugFound = board[i - (h)][j] % 6 == 0;
                                if(board[i - (h)][j] / 6 != currentColor){
                                    colorChanges ++;
                                    currentColor = board[i - (h)][j] / 6;
                                }
                            }while(i-h > 0 && !anotherBugFound && board[i - (h)][j] != 0); //while in bounds, no bug, no blank
                            if(board[i - (h)][j] == 0){ //If this is true, we place the # of color changes
                                bugStackDepthV[i-h][j] = colorChanges-1;
                            }                       
                        }
                        if(i<board.length-1 && board[i+1][j] == 0){              //Place the count underneath the stack of pieces as well (if you can)
                            bugStackV[i+1][j][board[i][j]/6-1] = bugStackV[i+1][j][board[i][j]/6-1] || bugFound; 
                            if(needsV[i+1][j][board[i][j]/6-1] != 0){            //If there's already a need listed (perhaps the top of a previous stack), add the count to it (losing our ability to distinguish bn top and bottom of a stack)
                                needsV[i+1][j][board[i][j]/6-1] += k;
                            }
                            else{
                                needsV[i+1][j][board[i][j]/6-1] = -k;           // use the negative number to indicate this is the bottom of  a stack
                            }
                        }
                        i = i-k+1;                                              // Jump past the cells we just examined (k will always be > 0)
                    }
                    catch(Exception e){
                        System.out.println("OOB Vertical");
                    }
                }
                
            }
        }
        for(int i=0; i<board.length; i++){
            for(int j=0; j < board[0].length; j++){ //Horizontal scan
            //for each cell on the board
                if(board[i][j]!=0){  //find a bug/pill piece
                    bugFound = false;
                    int k=0;
                    try{
                        do{
                            if(!bugFound){                                       // Once we find a bug, bugStackV stays true
                                bugFound = board[i][j+k] % 6 == 0;
                            }                            
                            k++;
                        } while(j+k < board[i].length && board[i][j+k] / 6 == board[i][j] / 6); //Keep moving right until the piece is no longer the same color
                        //canvas.repaint();
                        if(j+k < board[i].length && board[i][j+k] ==0){ //if the cell we reach is blank
                            needsH[i][j+k][board[i][j]/6-1] += k;
                            if(isSolidGround(i,j+k,false,board) && needsH[i][j+k][board[i][j]/6-1] > 2){
                                needsProjectedV[i][j+k][board[i][j]/6-1] += k;  // should we project from horizontal needs? tricky subject - i'll do it if a single piece will kill stuff
                            }  
                            bugStackH[i][j+k][board[i][j]/6-1] = bugFound;
                        }
                        if(j>0 && board[i][j-1] == 0){ //put the need to the left of the original piece
                            bugStackH[i][j-1][board[i][j]/6-1] = bugStackH[i][j-1][board[i][j]/6-1] || bugFound;
                            if(isSolidGround(i,j-1,false,board) && needsH[i][j-1][board[i][j]/6-1] > 2){
                                needsProjectedV[i][j-1][board[i][j]/6-1] += k;  // should we project from horizontal needs? tricky subject - i'll do it if a single piece will kill stuff
                            }                             
                            if(needsH[i][j-1][board[i][j]/6-1] != 0){
                                needsH[i][j-1][board[i][j]/6-1] += k;
                            }
                            else{
                                needsH[i][j-1][board[i][j]/6-1] = -k; //do we really need to make the before/after distinction on horizontal scan?
                            }
                        }
                        j = j+k-1; //k will always be > 0

                    }
                    catch(Exception e){
                        System.out.println("OOB Horizontal, j=" +j+ ", k=" + k);
                    }
                }
            }
        }//work in progress

        for(int i=board.length-1 ; i > 0; i--){
            for(int j=0; j < board[i].length; j++){
                if(board[i][j]!=0 && board[i][j]%6==0){
                    bugsPerColumn[j] ++;
                }
                for(int k=0;k<3;k++){
                    if(i<board.length-1 && board[i][j] ==0 && board[i+1][j] ==0){
                        needsProjectedV[i][j][k] = needsProjectedV[i+1][j][k];
                    }
                }
            }
        }
        return null;
    }    

    public int[]       chooseLocation(int[] pill, int[][] board){
        //Run the prereqs
        determineReachables(board);
        determineNeeds(board);

        int pillColor1 = pill[0]/6-1;
        int pillColor2 = pill[1]/6-1;
        int choiceCount = 0;  //how many choices we've found

        // [0]pillX, [1]pillY, [2]rotations, [3]matched colors, [4]mismatched colors
        int[][] choices = new int[100][5]; //1 set of coordinates, net amt of rotations required(clockwise), # matched colors, # mismatched

        for(int i=0; i < board.length; i++){
            for(int j=0; j < board[i].length; j++){ //for each cell on the board
                if(reachables[i][j]){ // can you get to it?
                    if(needsV[i][j][pillColor1] > 0){ //does it need this color
                        try {
                            if(board[i][j+1] == 0){
                                if (needsProjectedV[i][j + 1][pillColor2] > 0) {// check pill j+1 (to the right of the cell) for a projected need
                                    choices[choiceCount++] = new int[]{i,j,0,2,0};
                                }
                                else if(needsProjectedV[i][j + 1][0] + needsProjectedV[i][j + 1][1] + needsProjectedV[i][j + 1][2] == 0){
                                    //are we lining this piece up with a blank slot? (an empty column)
                                    choices[choiceCount++] = new int[]{i,j,0,1,0};
                                }
                                else if(choiceCount == 0){ //if we ain't got shit else
                                    choices[choiceCount++] = new int[]{i,j,0,1,1};
                                }
                            }
                        } catch (Exception e) {
                            //j+1 is out of bounds
                        }
                        try { //check pill j-1, left of target
                            if(board[i][j-1] == 0){
                                if (needsProjectedV[i][j - 1][pillColor2] > 0) { 
                                    choices[choiceCount++] = new int[]{i,j-1,2,2,0};
                                }
                                else if(needsProjectedV[i][j - 1][0] + needsProjectedV[i][j - 1][1] + needsProjectedV[i][j - 1][2] == 0){
                                    //are we lining this piece up with a blank slot?
                                    choices[choiceCount++] = new int[]{i,j-1,2,1,0};
                                }
                                else if(choiceCount == 0){
                                    choices[choiceCount++] = new int[]{i,j-1,2,1,1};
                                }
                            }
                        } catch (Exception e) {
                            //j-1 is out of bounds
                        }
                        try { //check pill i-1
                            if(board[i-1][j] == 0){//if the cell above is blank
                                if (needsProjectedV[i-1][j][pillColor2] > 0) { //isn't this always true?
                                    if(needsProjectedV[i][j][pillColor1] > 2 && pillColor1 == pillColor2){
                                        choices[choiceCount++] = new int[]{i,j,-1,1,0};
                                    }
                                    else if(needsProjectedV[i][j][pillColor1] < 2 && pillColor1 == pillColor2){
                                        choices[choiceCount++] = new int[]{i,j,-1,2,0};
                                    }
                                }
/*special case?*/               else if(needsProjectedV[i-1][j][0] + needsProjectedV[i-1][j][1] + needsProjectedV[i-1][j][2] == 0){
                                    //are we lining this piece up with a blank slot?
                                    choices[choiceCount++] = new int[]{i,j,-1,1,1};
                                }
                                else if(choiceCount == 0){
                                    choices[choiceCount++] = new int[]{i,j,-1,1,1};
                                }
                            }
                        } catch (Exception e) {
                            //i-1 is out of bounds, which, it should never be
                        }


                    } // NOW we check if pill2's color matches the current cell
                    else if(needsV[i][j][pillColor2] > 0){ //does it need this color
                        try {
                            if(board[i][j+1] == 0){
                                if (needsProjectedV[i][j + 1][pillColor1] > 0) {// check pill j+1 (to the right of the cell)
                                    choices[choiceCount++] = new int[]{i,j,2,2,0};
                                }
                                else if(needsProjectedV[i][j + 1][0] + needsProjectedV[i][j + 1][1] + needsProjectedV[i][j + 1][2] == 0){
                                    //are we lining this piece up with a blank slot?
                                    choices[choiceCount++] = new int[]{i,j,2,1,0};
                                }
                                else if(choiceCount == 0){ //if we ain't got shit else
                                    choices[choiceCount++] = new int[]{i,j,2,1,1};
                                }
                            }
                        } catch (Exception e) {
                            //j+1 is out of bounds
                        }
                        try { //check pill j-1, left of target
                            if(board[i][j-1] == 0){
                                if (needsProjectedV[i][j - 1][pillColor1] > 0) {
                                    choices[choiceCount++] = new int[]{i,j-1,0,2,0};
                                }
                                else if(needsProjectedV[i][j - 1][0] + needsProjectedV[i][j - 1][1] + needsProjectedV[i][j - 1][2] == 0){
                                    //are we lining this piece up with a blank slot?
                                    choices[choiceCount++] = new int[]{i,j-1,0,1,0};
                                }
                                else if(choiceCount == 0){
                                    choices[choiceCount++] = new int[]{i,j-1,0,1,1};
                                }
                            }
                        } catch (Exception e) {
                            //j-1 is out of bounds
                        }
                        try { //check pill i-1
                            if(board[i-1][j] == 0){
                                if (needsProjectedV[i-1][j][pillColor1] > 0) { 
                                    if(needsProjectedV[i][j][pillColor1] > 2 && pillColor1 == pillColor2){
                                        choices[choiceCount++] = new int[]{i,j,1,1,0};
                                    }
                                    else if(needsProjectedV[i][j][pillColor1] < 2 && pillColor1 == pillColor2){
                                        choices[choiceCount++] = new int[]{i,j,1,2,0};
                                    }
                                }
/*special case?*/               else if(needsProjectedV[i-1][j][0] + needsProjectedV[i-1][j][1] + needsProjectedV[i-1][j][2] == 0){
                                    //are we lining this piece up with a blank slot?
                                    choices[choiceCount++] = new int[]{i,j,1,1,0};
                                }
                                else if(choiceCount == 0){
                                    choices[choiceCount++] = new int[]{i,j,1,1,1};
                                }
                            }
                        } catch (Exception e) {
                            //i-1 is out of bounds, which, it should never be
                        }


                    }
                    else if(i==board.length-1 && j<board[0].length-1 && board[i][j+1] == 0){
                        choices[choiceCount++] = new int[]{i,j,0,0,0};
                    }
                }
            }
         }
        
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==2){
                return(choices[i]);
                //max = choices[i];
            }
        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==1 && choices[i][4]==0){
                return(choices[i]);
                //max = choices[i];
            }
        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==0 && choices[i][4]==0){
                return(choices[i]);
                //max = choices[i];
            }
        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==1 && choices[i][4]==1){
                return(choices[i]);
                //max = choices[i];
            }
        }
        return new int[] {1,3,0,0,0};
    }
    public int[]       chooseLocation3(int[] pill, int[][] board){ 
        long start = System.currentTimeMillis();
        //Run the prereqs
        reachableCount = determineReachables(board);
        int[][] choices = new int[reachableCount*4][];
      //  choiceText = new String[choices.length];
      //  ct=0;
        determineNeeds(board);
        //{pi, pj, rotations, bugsKilled, sumBugStackDepths, sumNeedsBugStack, kills, sumReachables };
        int choiceCount = 0;  //how many choices we've found
        
        
        for(int i=0; i < board.length; i++){
            for(int j=0; j < board[i].length; j++){//for each cell on the board
               if(reachables[i][j]){  //can you get to it?
                   if(j<board[i].length-1 && isSolidGround(i, j, true, board)){  //For horizontal pills
                       choices[choiceCount++] = simulateMove(i,j,0);
                       choices[choiceCount++] = simulateMove(i,j,2);
                   }
                   if(isSolidGround(i, j, false, board)){                       //For vertical pills
                       choices[choiceCount++] = simulateMove(i,j,1);
                       choices[choiceCount++] = simulateMove(i,j,3);
                   }                   
               }   
            }
        }
        //Now we should have many choices, and lots of them will be bad
        
        long fin = System.currentTimeMillis() - start;

        //System.out.println("Simulated " + choiceCount + " choices in " + fin + "ms") ;
        return quickChoicePicker(choices, choiceCount);
    }
    
    public int[] quickChoicePicker(int[][] choices, int choiceCount){ //picks the max value in the list for the 1st criteria, eliminates the rest, repeats for every criteria
        int x = choices[0].length;
        for(int k=3;k<x;k++){
            int max =-99999;                         //We'll use a data structure later, but quick choice for now
            for(int i=0;i<choiceCount; i++){
                if(choices[i]!=null && choices[i][k] > max){
                    max = choices[i][k];
                }
            }
            for(int i=0;i<choiceCount; i++){
                if(choices[i]!=null && choices[i][k] < max){
                    choices[i] = null;
                }
            }  
        }
        for(int i=0;i<choiceCount; i++){
            if(choices[i]!=null){
               // long fin = System.currentTimeMillis() - start;
               // System.out.println("Simulated " + choiceCount + " choices in " + fin + "ms") ;
                //System.out.println(choiceText[i]);
                return choices[i];
                //return new int[] {choices[i][0],choices[i][1],choices[i][2]};
            }
        } 
        return null;
    }
    
    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    
    public int[]       chooseLocation2(int[] pill, int[][] board){
        //Run the prereqs
        determineReachables(board);
        determineNeeds(board);

        int pillColor1 = pill[1]/6-1;    //Intentionally did the pill colors backwards so the k loop below will swap the colors to be correct
        int pillColor2 = pill[0]/6-1;
        int choiceCount = 0;  //how many choices we've found
       

        // [0]pillX, [1]pillY, [2]rotations,                                        INFO       
        // [3]Bugs killed, [4]Bugs matched, [5]Projection bug matches [6]unimportant pieces matched         PRIORITES(in decreasing order)
        // [7]Unimportant pieces mismatched,[8]Projection bugs mismatched, [9]bugs mismatched
        int[][] choices = new int[100][5]; //1 set of coordinates, net amt of rotations required(clockwise), # matched colors, # mismatched

        for(int i=0; i < board.length; i++){
            for(int j=0; j < board[i].length; j++){//for each cell on the board
               if(reachables[i][j]){               //that is reachable
                    for(int k=0;k<2;k++){          //for each half of the pill
                       
                        int temp = pillColor1;  //Swap colors on the pill
                        pillColor1 = pillColor2;
                        pillColor2 = temp;
                        
                        if(needsV[i][j][pillColor1] > 0 && (needsV[i][j][pillColor1] + i >3)){ //does it need this color, and can achieve 4-in-a-row before hitting the ceiling?
                            try {
                                if(board[i][j+1] == 0){
                                    if (needsProjectedV[i][j + 1][pillColor2] > 0) {// check pill j+1 (to the right of the cell) for a projected need
                                        choices[choiceCount++] = new int[]{i,j,2*k,2,0};
                                    }
                                    else if(needsProjectedV[i][j + 1][0] + needsProjectedV[i][j + 1][1] + needsProjectedV[i][j + 1][2] == 0){
                                        //are we lining this piece up with a blank slot? (an empty column)
                                        choices[choiceCount++] = new int[]{i,j,2*k,1,0};
                                    }
                                    else if(choiceCount == 0){ //if we ain't got shit else
                                        choices[choiceCount++] = new int[]{i,j,2*k,1,1};
                                    }
                                }
                            } catch (Exception e) {
                                //j+1 is out of bounds
                            }
                            try { //check pill j-1, left of target
                                if(board[i][j-1] == 0){
                                    if (needsProjectedV[i][j - 1][pillColor2] > 0) { 
                                        choices[choiceCount++] = new int[]{i,j-1,2-(2*k),2,0};
                                    }
                                    else if(needsProjectedV[i][j - 1][0] + needsProjectedV[i][j - 1][1] + needsProjectedV[i][j - 1][2] == 0){
                                        //are we lining this piece up with a blank slot?
                                        choices[choiceCount++] = new int[]{i,j-1,2-(2*k),1,0};
                                    }
                                    else if(choiceCount == 0){
                                        choices[choiceCount++] = new int[]{i,j-1,2-(2*k),1,1};
                                    }
                                }
                            } catch (Exception e) {
                                //j-1 is out of bounds
                            }
                            try { //check pill i-1
                                if(board[i-1][j] == 0){//if the cell above is blank
                                    if (needsProjectedV[i-1][j][pillColor2] > 0) { //isn't this always true?
                                        if(needsProjectedV[i][j][pillColor1] > 2 && pillColor1 == pillColor2){
                                            choices[choiceCount++] = new int[]{i,j,-1+(2*k),1,0};
                                        }
                                        else if(needsProjectedV[i][j][pillColor1] < 2 && pillColor1 == pillColor2){
                                            choices[choiceCount++] = new int[]{i,j,-1+(2*k),2,0};
                                        }
                                    }
    /*special case?*/               else if(needsProjectedV[i-1][j][0] + needsProjectedV[i-1][j][1] + needsProjectedV[i-1][j][2] == 0){
                                        //are we lining this piece up with a blank slot?
                                        choices[choiceCount++] = new int[]{i,j,-1+(2*k),1,1};
                                    }
                                    else if(choiceCount == 0){
                                        choices[choiceCount++] = new int[]{i,j,-1+(2*k),1,1};
                                    }
                                }
                            } catch (Exception e) {
                                //i-1 is out of bounds, which, it should never be
                            }


                        } 
                        else if(i==board.length-1 && j<board[0].length-1 && board[i][j+1] == 0){ //allows you to put garbage piece on the ground, WIP
                            choices[choiceCount++] = new int[]{i,j,0,0,0};
                        }
                    }
                }
            }
         }
        
//        for(int i=0;i<choiceCount; i++){
//            choices[i] = this.simulateMove(choices[i][0], choices[i][1], choices[i][2]);
//        }
//        if(true){
//            return quickChoicePicker(choices, choiceCount);
//        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==2){
                return(choices[i]);
                //max = choices[i];
            }
        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==1 && choices[i][4]==0){
                return(choices[i]);
                //max = choices[i];
            }
        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==0 && choices[i][4]==0){
                return(choices[i]);
                //max = choices[i];
            }
        }
        for(int i=0; i < choiceCount; i++){
            if (choices[i][3]==1 && choices[i][4]==1){
                return(choices[i]);
                //max = choices[i];
            }
        }
        return new int[] {1,3,0,0,0};
    }

    //--------Game Engine Functions-----------------------------------------------------
    public int[] getPillCoordinates(){
        int[] xy = new int[4];
        xy[0] = pillI;
        xy[1] = pillJ;
        //DrMario.print(xy);
        switch(pillBoard[pillI][pillJ]%6){
            case 2:
                xy[2] = pillI - 1;
                xy[3] = pillJ;
                break;
            case 5:
                xy[2] = pillI;
                xy[3] = pillJ + 1;
                break;
            default:
                System.out.println(pillBoard[pillI][pillJ]%6);
                System.exit(45);
        }

        return xy;

    }
    public static int[][] getNewBoard(int n){       //Generates new random board with n bugs, SEEMS to work
        int[][] b = new int[17][8];
        int[][] b2 = new int[][]{{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0,12, 0,12,12, 6, 6, 0},
{12, 6, 6,18, 0,12, 6,18},
{ 0, 0, 0, 0, 0,12, 0,12},
{ 0, 0, 6,18,18,18, 6,18},
{ 6, 6, 0,12, 6,12, 0,12},
{ 6, 0,18, 0,12, 6, 0,18},
{ 6, 0, 0, 0,18, 0, 0,12},
{ 0,18, 0, 0, 0, 6,18, 0},
{12, 0, 6,12, 0,12, 0,18},
{12, 0,12, 0, 6,12, 6, 0},
{ 6, 6, 6, 0, 0,12, 0, 0},
{ 6, 0, 0, 0,18,18, 6, 6},
{ 6, 0,12, 0, 0, 0, 0,18}};
        if(false) return b2;
        
        if(false)return new int[][] {{ 0, 0, 0, 0, 0, 0, 0, 0}, //temp debug
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{12,12,18, 0, 0, 6, 6, 0},
{ 0, 0,18,12, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0,18, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0,12, 0, 0},
{ 0, 0, 0, 0,12, 0,12, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 6, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 6, 0, 6},
{ 0, 0, 0, 0,12, 0, 6, 0},
{ 0, 0, 6, 0, 0, 0, 0,12},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 6, 0, 0, 0, 0,18, 0},
};
        
        
        
        if(true) return new int[][] {{ 0, 0, 0, 0, 0, 0, 0, 0},  //this is the one that loses
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{ 0, 0, 0, 0, 0, 0, 0, 0},
{12, 6,12, 6, 6, 6,12, 6},
{12, 6, 6,18,18,12, 6,18},
{18,12, 6,18,12,18,18,18},
{ 6,18,18,12,18, 6, 6,12},
{18, 0, 0, 6,12,18, 6,18},
{12,18,18,12, 6,12,12, 6},
{12,12,18, 6,12, 6,12, 6},
{ 6,18,12,18,18,12, 6,12},
{12, 6,18, 6,12,18,12, 6},
{18,12, 6,12, 0, 6,18, 6},
{ 6, 6,12,18, 6,12, 0,18},
{18, 6, 6,18, 6,12,18, 6},
{18, 6,12,18, 6, 6, 6,12},
};
        while(countBugs(b)<n){
        //for(int h=0; h<90 ; h++){
           int i = (int)(Math.ceil(Math.random()*13))+3;
           int j = (int)(Math.ceil(Math.random()*8))-1;
           b[i][j]=(int)((Math.ceil(Math.random()*3)))*6;
           killBlocks(b);
           //System.out.println(countBugs(b));
        }
        DrMario.print(b);
        if(true)return b; 
        
        

        if(true)return new int[][] {{ 0, 0, 0, 0, 0, 0, 0, 0},
                            { 0, 0, 0, 0, 0, 0, 0, 0},
                            {19, 0, 0, 0, 0, 0, 0, 0},
                            {19, 0, 0, 0, 0, 0, 0, 0},
                            {18, 0,23,16, 0, 0, 0,12},
                            { 6, 0, 0,12, 0, 0, 0, 6},
                            { 6, 0, 0, 0, 0, 0, 0, 0},
                            { 0,18, 0, 6, 0, 0, 0, 6},
                            { 6,12, 0,12,18,13,12,18},
                            { 0,12, 0, 0, 0,12, 6,18},
                            { 6,12,18, 6,18, 0,12, 0},
                            { 0,18,18, 6,12, 6,18, 0},
                            { 0, 0,12, 0, 6,12,12,18},
                            {12, 6, 0,18, 0,18,18, 6},
                            { 0, 0, 6,18,18, 6,12,12},
                            { 0, 6,18, 6,18, 6, 6, 6},
                            {12,18,12,18,12, 6, 6,18}};
        return null;
    }
    public static int[] killBlocks(int[][] b){ //Now includes other stats
        //How this algorithm works - it scans through the board, dropping a crumb (an int) on each element
        //in the array.  If an element is of different color than the previous, we start dropping a new type of
        //crumb(different int) and recall how many of the previous type of crumb we dropped.  If the number is
        // >3, we can add that crumbtype to a list of elements to "kill" (zero) later.  We don't kill them immediately
        //because 4 in a row may exist horizontally & vertically, and you don't want to get rid of your evidence
        
        int       crumbs = 0;               //Used to keep track of how many consecutive colors in a row
        int       crumbType = 0;            //Used to go back and kill stuff after finding 4+ in a row
        int       currentColor = b[0][0]/6; // 0 = blank, 1 = red, 2 =  yellow, 3 = blue

        int[][][] crumbBoard = new int[b.length][b[0].length][2];  //This is where the crumbs are dropped
        int[]     killList = new int[100];                       //List of crumbTypes to kill.  10 should be plenty
        int       killListIterator = 0;                         //For traversing the killList
 
        // [3]Bugs killed, [4]Bugs matched, [5]Projection bug matches [6]unimportant pieces matched         PRIORITES(in decreasing order)
        // [7]Unimportant pieces mismatched,[8]Projection bugs mismatched, [9]bugs mismatched
        //STATS
        int       kills = 0;                //Total kills (including bugs)
        int       bugsKilled = 0;           //How many kills were bugs



        //Scan 1: scans horizontally, left to right, top to bottom (like a book!)
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

        //Scan 2: scans vertically (like a uh... you decide)
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
        if(killList[0]!=0){ //if there exists any kills on the list (this just saves a lot of looping if there are no kills)
            for(int i=0; i<b.length; i++){
                 for(int j=0; j<b[0].length; j++){
                    for(int k=0; killList[k]!=0; k++){ 
                        if(killList[k]==crumbBoard[i][j][1] || killList[k]==crumbBoard[i][j][0]){ //If the crumbtype is on the kill list
                           
                            switch (b[i][j]%6) { //This switch statement handles the breaking of pills in half
                                case 0:  if(b[i][j]!=0) bugsKilled ++; break;
                                case 2:  b[i-1][j]-=2; break; // If you kill bottom of pill, make top half turn into ()
                                case 3:  b[i+1][j]--; break;  // If you kill top of pill, make bottom half turn into ()
                                case 4:  b[i][j-1]-=4;break;  // if you kill right half of pill, turn left into ()
                                case 5:  b[i][j+1]-=3;break;  // If you kill left half of pill, turn right into ()
                            }
                             
                            if(b[i][j]!=0){ //this keeps  kills from getting counted more than once for T-shaped kills
                                kills ++;
                            }
                            b[i][j] = 0; //The KILLING!!
                        }
                    }
                 }      
            }
         /*   bugCount = countBugs(b);
            System.out.println("Bugs left: " + bugCount);*/
        }
        return new int[] {kills, bugsKilled};
    }  
    public static int[] killBlocksOLD(int[][] b){
        //How this algorithm works - it scans through the board, dropping a crumb (an int) on each element
        //in the array.  If an element is of different color than the previous, we start dropping a new type of
        //crumb(different int) and recall how many of the previous type of crumb we dropped.  If the number is
        // >3, we can add that crumbtype to a list of elements to "kill" (zero) later.  We don't kill them immediately
        //because 4 in a row may exist horizontally & vertically, and you don't want to get rid of your evidence
        
        int       crumbs = 0;               //Used to keep track of how many consecutive colors in a row
        int       crumbType = 0;            //Used to go back and kill stuff after finding 4+ in a row
        int       currentColor = b[0][0]/6; // 0 = blank, 1 = red, 2 =  yellow, 3 = blue

        int[][][] crumbBoard = new int[b.length][b[0].length][2];  //This is where the crumbs are dropped
        int[]     killList = new int[100];                       //List of crumbTypes to kill.  10 should be plenty
        int       killListIterator = 0;                         //For traversing the killList
 
        int       kills = 0;                //Total kills (including bugs)
        int       bugsKilled = 0;           //How many kills were bugs


        //Scan 1: scans horizontally, left to right, top to bottom (like a book!)
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

        //Scan 2: scans vertically (like a uh... you decide)
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
        if(killList[0]!=0){ //if there exists any kills on the list (this just saves a lot of looping if there are no kills)
            for(int i=0; i<b.length; i++){
                 for(int j=0; j<b[0].length; j++){
                    for(int k=0; killList[k]!=0; k++){ 
                        if(killList[k]==crumbBoard[i][j][1] || killList[k]==crumbBoard[i][j][0]){ //If the crumbtype is on the kill list
                           
                            switch (b[i][j]%6) { //This switch statement handles the breaking of pills in half
                                case 0:  bugsKilled ++; break;
                                case 2:  b[i-1][j]-=2; break; // If you kill bottom of pill, make top half turn into ()
                                case 3:  b[i+1][j]--; break;  // If you kill top of pill, make bottom half turn into ()
                                case 4:  b[i][j-1]-=4;break;  // if you kill right half of pill, turn left into ()
                                case 5:  b[i][j+1]-=3;break;  // If you kill left half of pill, turn right into ()
                            }
                            b[i][j] = 0; //The KILLING!!
                            kills ++;
                        }
                    }
                }      
            }
         /*   bugCount = countBugs(b);
            System.out.println("Bugs left: " + bugCount);*/
        }
        return new int[] {kills, bugsKilled};
    }    
    public static boolean settle(int[][] b){// this lets everything fall one step, returns true if there was any movement
        boolean movement = false;           // if b is modified(aka, pieces do fall), this will turn true
        //boolean[][] fall = new boolean[b.length][b[0].length];
        
        for(int i=b.length-2; i>-1; i--){  //Starting from row above the bottom row, since bottom row can't fall
            for(int j=0; j<b[0].length; j++){  //Working from left to right                
                int type = b[i][j]%6;          //Gets the shape of the piece
                switch (type) {
                    case 1:                                            
                    case 2:  if((b[i+1][j]==0)){           //()& U will fall if under is empty
                                b[i+1][j] = b[i][j];
                                if(type==2){              // If type is U, go ahead and bring down the upper half too
                                    b[i][j]   = b[i-1][j];
                                    b[i-1][j] = 0;
                                }
                                else{
                                    b[i][j] = 0;
                                }
                                movement = true;
                             }
                             break;          
                    case 5:  if((b[i+1][j]==0)&&(b[i+1][j+1]==0)){     //(   , and we don't need a case 4, b/c this case
                                b[i+1][j]   = b[i][j];                 // will handle both (since it always sees the left
                                b[i+1][j+1] = b[i][j+1];             //side first
                                b[i][j]     = 0;
                                b[i][j+1]   = 0;
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
        if(predeterminedPills){
            return pills[turn];
        }
        int i = ((int)Math.ceil(Math.random()*3))*6+5;
        int j = ((int)Math.ceil(Math.random()*3))*6+4;
        return new int[] {i,j};
        
    }
    public static int countBugs(int[][] b){
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
    
    //--------Getters & Setters--------------------------------------------------------
    public int getBugCount() {
        return bugCount;
    }
    public int[][] getGameBoard() {
        return gameBoard;
    }
    public void setGameBoard(int[][] gameBoard) {
        this.gameBoard = gameBoard;
    }
    public void setTargetCanvas(MyCanvas mc){
        this.canvas = mc;
        if(canvas!=null) this.gameID = canvas.addGame(this);
    }  
    public int getPillI() {
        return pillI;
    }
    public int getPillJ() {
        return pillJ;
    }
    public int[][][] getNeedsV() {
        return needsV;
    }
    public void setNeeds(int[][][] needs) {
        this.needsV = needs;
    }
    public boolean[][] getReachables() {
        return reachables;
    }
    public void setReachables(boolean[][] reachables) {
        this.reachables = reachables;
    }
    public int[][] getPillBoard(){
        return pillBoard;
    }
    public void enableAI(){
        this.cpuPlayer = true;
    }
    public int[][][] getNeedsProjected() {
        return needsProjectedV;
    }
    public int[] getPill() {
        return pill;
    }
    public void setPill(int[] pill) {
        this.pill = pill;
    }
    public int[][] getBugStackDepthH() {
        return bugStackDepthH;
    }

    public void setBugStackDepthH(int[][] bugStackDepthH) {
        this.bugStackDepthH = bugStackDepthH;
    }

    public int[][] getBugStackDepthV() {
        return bugStackDepthV;
    }

    public void setBugStackDepthV(int[][] bugStackDepthV) {
        this.bugStackDepthV = bugStackDepthV;
    }

    public boolean[][][] getBugStackH() {
        return bugStackH;
    }

    public void setBugStackH(boolean[][][] bugStackH) {
        this.bugStackH = bugStackH;
    }

    public boolean[][][] getBugStackV() {
        return bugStackV;
    }

    public void setBugStackV(boolean[][][] bugStackV) {
        this.bugStackV = bugStackV;
    }

    public MyCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(MyCanvas canvas) {
        this.canvas = canvas;
    }

    public boolean isCpuPlayer() {
        return cpuPlayer;
    }

    public void setCpuPlayer(boolean cpuPlayer) {
        this.cpuPlayer = cpuPlayer;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int[][][] getNeedsH() {
        return needsH;
    }

    public void setNeedsH(int[][][] needsH) {
        this.needsH = needsH;
    }

    public void setNeedsV(int[][][] needsV) {
        this.needsV = needsV;
    }
    public void setPillI(int pillI) {
        this.pillI = pillI;
    }

    public void setPillJ(int pillJ) {
        this.pillJ = pillJ;
    }
    public void setPillBoard(int[][] pillBoard) {
        this.pillBoard = pillBoard;
    }    
    //--------Action Handling(Timer)--------------------------------------------------
    public void actionPerformed(ActionEvent e) { //the game timer trigers this
        if(moveDown()){
            if(canvas!=null) canvas.partialRepaint(this.gameID);
        }
        else{
            playTimer.stop();
            playerTurn = false;
        }
    }

    public static void main(String[] args){
        new DrMario();
    }
}
