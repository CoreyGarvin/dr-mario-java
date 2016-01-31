import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MyCanvas extends Canvas// implements ComponentListener
 { //This is what the games are drawn on

//    public void componentHidden(ComponentEvent e) {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void componentMoved(ComponentEvent e) {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void componentResized(ComponentEvent e) {
//       
//       // System.out.println("Resized " + getWidth() + ", " + getHeight());
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void componentShown(ComponentEvent e) {
//        //throw new UnsupportedOperationException("Not supported yet.");
//    }
    private DMGame[] games;
    private int[][][] boards;
    private int[][][] refBoards;
    private int numGames=0;
    private int numBoards=0;
    private int maxGames=20;
    int brdThickness=1;
    int horSpacer = 3;
    int vertSpacer = 50;
    int tileSize = 24;
    int radius = tileSize/2;
    int iOffset;   //y direction
    int jOffset;
    int repaintCount = 0;

   // private int w;  //width, height.. not really used right now
   // private int h;

    public MyCanvas(){
        //this.addComponentListener(this);
        boards = new int[maxGames][][];
        refBoards = new int[maxGames][17][8];
        games = new DMGame[maxGames];
    }
    public MyCanvas(int[][] board){
        addBoard(board);
//        w = getWidth();
      //  h = getHeight();
    }
    
    public int addGame(DMGame game){
        if (numGames>maxGames-1){
            return -1;
        }
//        for(int i=0; i<refBoards[numGames].length; i++){
//            for(int j=0; j<refBoards[numGames][i].length; j++){
//                refBoards[numGames][i][j] = game.getGameBoard()[i][j];
//            }
//        }
       // refBoards[numGames] = 
        games[numGames] = game;
        addBoard(game.getGameBoard());
        return numGames++;
    }
    private void addBoard(int[][] board) {
        if (numBoards>maxGames-1){
            return;
        }
        boards[numBoards++] = board;
    }


    //@Override
/*    public void setSize(int h, int w){
        super.setSize(h,w);
        //w = getWidth();
        //h = getHeight();
    }*/
    
    public void repaintBoard(int b){ //Repaints just one game, or board
        if(true){repaint();return;}
        iOffset = getIOff(b);  //y direction
        jOffset = getJOff(b); //x direction
        System.out.print("Board " + b + " ");
        repaint(jOffset,iOffset+tileSize,tileSize*boards[b][0].length,tileSize*(boards[b].length-1));
    }
    public void partialRepaint(int b){  //Calls a repaint() for only the area immediately surrounding the pill (less work on CPU)

        if(true){repaint();return;}
       // System.out.print("Partial " + b + " ");
       // int jOffset = 10 + (b*tileSize*boards[b][0].length)+b;  //x direction 
        iOffset = getIOff(b);  //y direction
        jOffset = getJOff(b); //x direction
        //g.drawRect((games[b].getPillJ()-1)*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*3, tileSize*3);
        repaint((games[b].getPillJ()-1)*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*4, tileSize*3);
    }
   
    //public void paint(Graphics g){}
    private int getIOff(int b){  //y dir  returns the y coordinate of the upper left hand corner of where the #bth board should be drawn at
       
       // if(getWidth()==0){
         // / // System.out.println(getWidth() + " width");
          //  this.setSize(500, 500);
           // System.out.println(getWidth() + " width now");
      //  }
        //System.out.println(horSpacer + tileSize*boards[b][0].length+brdThickness*2);
        int row = b/(getWidth() / (horSpacer + tileSize*boards[b][0].length+brdThickness*2));
        return -tileSize + vertSpacer + brdThickness + (tileSize*boards[b].length+brdThickness*2+vertSpacer)*(row);
    }
    private int getJOff(int b){ //x direction
        int inRow = (getWidth() / (horSpacer + tileSize*boards[b][0].length+brdThickness*2));
        return horSpacer + brdThickness + (b*(tileSize*boards[b][0].length+brdThickness*2+horSpacer))%((inRow*(tileSize*boards[b][0].length+brdThickness*2+horSpacer)));
    }
    @Override
    public void paint(Graphics g){

        //if(true) return;
        long start = System.currentTimeMillis();
        if(boards[0]==null){
            return;
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.white);
        g.drawString(games[0].turn+"", 5, 25);
        
        for(int b=0;b<numBoards;b++){ //draw the nextpill
            if(b==0){
                switch (games[0].nextPill[0]/6) {
                    case 1: g.setColor(Color.RED);break;
                    case 2: g.setColor(Color.YELLOW);break;
                    case 3: g.setColor(Color.BLUE);break;
                }
                g.fillArc(25-tileSize/2, 25, radius * 2, radius * 2, -90, -180);
                g.fillRect(25, 25, tileSize/2, tileSize);
                switch (games[0].nextPill[1]/6) {
                    case 1: g.setColor(Color.RED);break;
                    case 2: g.setColor(Color.YELLOW);break;
                    case 3: g.setColor(Color.BLUE);break;
                }                
                g.fillArc(25+tileSize/2, 25, radius * 2, radius * 2, -90, 180);
                g.fillRect(25+tileSize/2, 25, tileSize/2,tileSize);
            }
            for(int i=1; i<boards[b].length; i++){ //i=1 b/c we don't draw that top row that's above the top...yea
                for(int j=0; j<boards[b][i].length; j++){
                   // if(boards[b][i][j]==refBoards[b][i][j]&&repaintCount>1){
                   //     continue;
                   // }
                    switch (boards[b][i][j]/6) {
                        case 0:
                            g.setColor(Color.BLACK);
                            switch (games[b].getPillBoard()[i][j]/6) {
                                case 0: 
                                    break;
                                case 1: g.setColor(Color.RED);break;
                                case 2: g.setColor(Color.YELLOW);break;
                                case 3: g.setColor(Color.BLUE);break; 
                            }
                            break;
                        case 1: g.setColor(Color.RED);break;
                        case 2: g.setColor(Color.YELLOW);break;
                        case 3: g.setColor(Color.BLUE);break;
                    }


                    iOffset = getIOff(b);  //y direction
                    jOffset = getJOff(b); //x direction

/*DEBUG*/          if(false&&b==1 && games[0].getReachables()!=null && games[0].getNeedsV() != null){ // lets the 2nd board be drawn as reachables from the first board //for debug
//                        g.setColor(Color.GRAY);
//                        if( games[0].getReachables()[i][j]){
//                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
//                        }
                            switch (boards[0][i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(Color.RED);break;
                            case 2: g.setColor(Color.YELLOW);break;
                            case 3: g.setColor(Color.BLUE);break;
                        }
                        switch (boards[0][i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        if(games[0].getNeedsV()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsV()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsV()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsV()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsV()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsV()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }

                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                        
                        g.setColor(Color.BLACK);
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                    }
/*DEBUG*/          if(false&&b==2 && games[0].getReachables()!=null && games[0].getNeedsH() != null){ // lets the 3d board be drawn as reachables from the first board //for debug
//                        g.setColor(Color.GRAY);
//                        if( games[0].getReachables()[i][j]){
//                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
//                        }
                        if(games[0].getNeedsH()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsH()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsH()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsH()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }
                        switch (boards[0][i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(Color.RED);break;
                            case 2: g.setColor(Color.YELLOW);break;
                            case 3: g.setColor(Color.BLUE);break;
                        }
                        switch (boards[0][i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                        
                        
                        g.setColor(Color.BLACK);
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                    }
/*DEBUG*/          if(false&&b==1){ // lets the 3rd board be drawn as needs from the first board //for debug
    //games[0].determineNeeds(games[0].gameBoard);
                    

                        g.setColor(Color.red);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][0] !=0){
                            if(games[0].bugStackV[i][j][0]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                        g.setColor(Color.yellow);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][1] != 0){
                            if(games[0].bugStackV[i][j][1]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }                            
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                        g.setColor(Color.blue);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][2] != 0){
                            if(games[0].bugStackV[i][j][2]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }                            
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                         if(games[0].getNeedsV()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsV()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsV()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsV()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsV()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsV()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }   
                        switch (boards[0][i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(Color.RED);break;
                            case 2: g.setColor(Color.YELLOW);break;
                            case 3: g.setColor(Color.BLUE);break;
                        }
                        switch (boards[0][i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                        g.setColor(Color.white);
                        if(games[0].bugStackDepthV!=null)g.drawString(games[0].bugStackDepthV[i][j]+"", j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset+tileSize/2);
                        
                    }
/*DEBUG*/          if(false&&b==2){ // lets the 3rd board be drawn as needs from the first board //for debug
                    
                        switch (boards[0][i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(Color.RED);break;
                            case 2: g.setColor(Color.YELLOW);break;
                            case 3: g.setColor(Color.BLUE);break;
                        }
                        switch (boards[0][i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                        g.setColor(Color.white);
                        if(games[0].bugStackDepthV!=null)g.drawString(games[0].bugStackDepthV[i][j]+"", j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset+tileSize/2);

                        g.setColor(Color.red);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][0] !=0){
                            if(games[0].bugStackV[i][j][0]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                        g.setColor(Color.yellow);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][1] != 0){
                            if(games[0].bugStackV[i][j][1]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }                            
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                        g.setColor(Color.blue);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][2] != 0){
                            if(games[0].bugStackV[i][j][2]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }                            
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                         if(games[0].getNeedsH()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsH()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsH()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsH()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }                   
                    }
/*DEBUG*/          if(false&&b==3){ // lets the 3rd board be drawn as needs from the first board //for debug
                    
                        switch (boards[0][i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(Color.RED);break;
                            case 2: g.setColor(Color.YELLOW);break;
                            case 3: g.setColor(Color.BLUE);break;
                        }
                        switch (boards[0][i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                        g.setColor(Color.white);
                        if(games[0].bugStackDepthV!=null)g.drawString(games[0].bugStackDepthV[i][j]+"", j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset+tileSize/2);

                        g.setColor(Color.red);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][0] !=0){
                            if(games[0].bugStackV[i][j][0]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                        g.setColor(Color.yellow);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][1] != 0){
                            if(games[0].bugStackV[i][j][1]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }                            
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                        g.setColor(Color.blue);
                        if(games[0].getNeedsV()!=null && games[0].getNeedsV()[i][j][2] != 0){
                            if(games[0].bugStackV[i][j][2]){
                                g.drawRect(j*tileSize+jOffset+5, i*tileSize+iOffset+5, tileSize-10, tileSize-10);
                            }                            
                            g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);
                            g.drawRect(j*tileSize+jOffset+1, i*tileSize+iOffset+1, tileSize-2, tileSize-2);
                        }
                         if(games[0].getNeedsH()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsH()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsH()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsH()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }                   
                    }
                    else if(false){
// lets the 4th board be drawn as simulation
                    
                     
                        
                    }
        
                    
                    else if(boards[b][i][j] != 0)
                    {
                        switch (boards[b][i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                    }
                    else if(games[b].getPillBoard()[i][j]!=0){
                         switch (games[b].getPillBoard()[i][j]%6) { //Draws the pill
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                    }
                    g.setColor(Color.lightGray); //draw border
                    
                    for(int p=1;p<=brdThickness;p++){
                        g.drawRect(jOffset-p,iOffset-p+tileSize, tileSize*boards[0][0].length+(p*2)-1, tileSize*16+(2*p)-1);
                        
                    }
                    //This shows the bounding box for partial repaint.  
                    g.drawRect((games[b].getPillJ())*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*3, tileSize*3-1);
                   // if(b==0){
                        //g.drawRect((games[b].getPillJ()-1)*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*3, tileSize*3);
                   // }

                 //   if(board[i][j]/6==2){g.setColor(Color.black);}
                    //else{g.setColor(Color.white);}
                 //   g.drawString(""+board[i][j]%6, j*tileSize+radius, i*tileSize+radius);
                    
                    if(b==1){
                        if(games[0].getNeedsV()==null){
                            games[0].determineNeeds(games[0].gameBoard);
                        }
                        int br=150;
                        switch (games[0].getGameBoard()[i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(new Color(br,30,30));break;
                            case 2: g.setColor(new Color(br,br,30));break;
                            case 3: g.setColor(new Color(30,30,br));break;
                        }
                        switch (games[0].gameBoard[i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);                         
                        
                         if(games[0].getNeedsV()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsV()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsV()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsV()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsV()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsV()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }                         
                    }
                    if(b==2){
                        if(games[0].getNeedsH()==null){
                            //games[0].determineNeeds(games[0].gameBoard);
                        }
                        int br=150;
                        switch (games[0].getGameBoard()[i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(new Color(br,30,30));break;
                            case 2: g.setColor(new Color(br,br,30));break;
                            case 3: g.setColor(new Color(30,30,br));break;
                        }
                        switch (games[0].gameBoard[i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);                         
                        
                         if(games[0].getNeedsH()[i][j][0] != 0){
                            g.setColor(Color.red);
                            g.fillRect(j*tileSize+jOffset+(tileSize/4), i*tileSize+iOffset, tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][0] + "",j*tileSize+jOffset+(tileSize/4), i*tileSize+(tileSize/2)+iOffset);
                        }
                        if(games[0].getNeedsH()[i][j][1] != 0){
                            g.setColor(Color.yellow);
                            g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.black);
                            g.drawString(games[0].getNeedsH()[i][j][1] + "",j*tileSize+jOffset, i*tileSize+iOffset+(tileSize));
                        }
                        if(games[0].getNeedsH()[i][j][2] !=0 ){
                            g.setColor(Color.blue);
                            g.fillRect(j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize/2), tileSize/2, tileSize/2);
                            g.setColor(Color.white);
                            g.drawString(games[0].getNeedsH()[i][j][2] + "", j*tileSize+jOffset+(tileSize/2), i*tileSize+iOffset+(tileSize));

                        }                         
                    }  
                    if(b==3){//games[0].determineNeeds(games[0].gameBoard);
                        if(games[0].getNeedsH()==null){
                            //games[0].determineNeeds(games[0].gameBoard);
                        }
                        int br=150;
                        switch (games[0].getGameBoard()[i][j]/6) {
                            case 0: g.setColor(Color.BLACK);break;
                            case 1: g.setColor(new Color(br,30,30));break;
                            case 2: g.setColor(new Color(br,br,30));break;
                            case 3: g.setColor(new Color(30,30,br));break;
                        }
                        switch (games[0].gameBoard[i][j]%6) { //Draws all pieces on board according to their shape
                            case 0: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);  break;
                            case 1: g.fillOval(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);break;
                            case 2: g.fillArc((j+0)*tileSize+jOffset, (i-1)*tileSize+iOffset+tileSize, radius * 2, radius * 2, 180, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize/2);break;
                            case 3: g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset+radius, tileSize, tileSize/2);
                                    g.fillArc((j+0)*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, 180, -180);break;
                            case 4: g.fillArc((j-1)*tileSize+jOffset+tileSize, i*tileSize+iOffset, radius * 2, radius * 2, -90, 180);
                                    g.fillRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize/2, tileSize);break;
                            case 5: g.fillArc(j*tileSize+jOffset, i*tileSize+iOffset, radius * 2, radius * 2, -90, -180);
                                    g.fillRect(j*tileSize+jOffset+tileSize/2, i*tileSize+iOffset, tileSize/2, tileSize);
                                    break;

                            //default: g.fillRect(j*40, i*40, 40, 40);
                        }
                        g.setColor(new Color(34,34,34)); //really dark gray grid
                        g.drawRect(j*tileSize+jOffset, i*tileSize+iOffset, tileSize, tileSize);    
                        g.setColor(Color.white);
                        if(games[0].bugStackV[i][j][0] || games[0].bugStackV[i][j][1] || games[0].bugStackV[i][j][2]){
                            
                           // g.drawString("V", j*tileSize+jOffset+ tileSize/2, i*tileSize+iOffset+ tileSize/2);
                        } 
                                
                        else if(games[0].bugStackDepthV[i][j]>0){
                             g.drawString(games[0].bugStackDepthV[i][j] + "", j*tileSize+jOffset+ tileSize/2, i*tileSize+iOffset+ tileSize/2);
                        
                            
                        }
                        for(int z=0;z<3;z++)
                        if(games[0].needsProjectedH[i][j][z]>0){
                            if(z==0) g.setColor(Color.red);
                            if(z==1) g.setColor(Color.yellow);
                            if(z==2) g.setColor(Color.blue);
                            g.drawString(games[0].needsProjectedH[i][j][z]+"", j*tileSize+jOffset+ tileSize/2, i*tileSize+iOffset+ tileSize/2);
                        }
                    }                     
                }
                
            }
            
        }
        repaintCount++;
        //System.out.println("Paint: " + (System.currentTimeMillis()-start) + "ms" );
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

}
