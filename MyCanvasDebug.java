import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class MyCanvasDebug extends Canvas { //This is what the games are drawn on
    private DMGame[] games;
    private int[][][] boards;
    private int[][][] refBoards;
    private int numGames=0;
    private int numBoards=0;
    private int maxGames=20;
    int brdThickness=1;
    int horSpacer = 3;
    int vertSpacer = 0;
    int tileSize = 10;
    int radius = tileSize/2;
    int iOffset;   //y direction
    int jOffset;
    int repaintCount = 0;

    private int w;  //width, height.. not really used right now
    private int h;

    public MyCanvasDebug(){
        boards = new int[maxGames][][];
        refBoards = new int[maxGames][17][8];
        games = new DMGame[maxGames];
    }
    public MyCanvasDebug(int[][] board){
        addBoard(board);
        w = getWidth();
        h = getHeight();
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


    @Override
    public void setSize(int h, int w){
        super.setSize(h,w);
        w = getWidth();
        h = getHeight();
    }
    
    public void repaintBoard(int b){
        
        //repaint();
        //refBoards[b] = boards[b].clone();
        //return;
        //int jOffset = 10 + (b*tileSize*boards[b][0].length)+b;  //x direction 
        iOffset = getIOff(b);  //y direction
        jOffset = getJOff(b); //x direction
        System.out.print("Board " + b + " ");
        repaint(jOffset,iOffset+tileSize,tileSize*boards[b][0].length,tileSize*(boards[b].length-1));
    }
    public void partialRepaint(int b){  //Calls a repaint() for only the area immediately surrounding the pill (less work on CPU)
        //repaint();return;
        System.out.print("Partial " + b + " ");
       // int jOffset = 10 + (b*tileSize*boards[b][0].length)+b;  //x direction 
        iOffset = getIOff(b);  //y direction
        jOffset = getJOff(b); //x direction
        //g.drawRect((games[b].getPillJ()-1)*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*3, tileSize*3);
        repaint((games[b].getPillJ()-1)*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*4, tileSize*3);        
    }
   
    //public void paint(Graphics g){}
    private int getIOff(int b){  //y dir
        int row = b/(getWidth() / (horSpacer + tileSize*boards[b][0].length+brdThickness*2));
        return -tileSize + vertSpacer + brdThickness + (tileSize*boards[b].length+brdThickness*2+vertSpacer)*(row);
    }
    private int getJOff(int b){ //x direction
        int inRow = (getWidth() / (horSpacer + tileSize*boards[b][0].length+brdThickness*2));
        return horSpacer + brdThickness + (b*(tileSize*boards[b][0].length+brdThickness*2+horSpacer))%((inRow*(tileSize*boards[b][0].length+brdThickness*2+horSpacer)));
    }
    public void paint(Graphics g){
        long start = System.currentTimeMillis();
        if(boards[0]==null){
            return;
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        for(int b=0;b<numBoards;b++){
            if(b==-1){
                print(boards[b]);
                System.out.println("--Now reference--");
                print(refBoards[b]);
                System.out.println("----------------------");
            }
            for(int i=1; i<boards[b].length; i++){ //i=1 b/c we don't draw that top row that's above the top...yea
                for(int j=0; j<boards[b][i].length; j++){
                   // if(boards[b][i][j]==refBoards[b][i][j]&&repaintCount>1){
                   //     continue;
                   // }
                    switch (boards[b][i][j]/6) {
                        case 0: g.setColor(Color.BLACK);break;
                        case 1: g.setColor(Color.RED);break;
                        case 2: g.setColor(Color.YELLOW);break;
                        case 3: g.setColor(Color.BLUE);break;
                    }


                    iOffset = getIOff(b);  //y direction
                    jOffset = getJOff(b); //x direction
                    
                    
                    if(boards[b][i][j]!=0){
                        switch (boards[b][i][j]%6) {
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
                    g.setColor(Color.GREEN); //draw border
                    
                    for(int p=1;p<=brdThickness;p++){
                        g.drawRect(jOffset-p,iOffset-p+tileSize, tileSize*boards[0][0].length+(p*2)-1, tileSize*16+(2*p)-1);
                        
                    }
                   // if(b==0){
                        //g.drawRect((games[b].getPillJ()-1)*tileSize+jOffset, (games[b].getPillI()-2)*tileSize+iOffset, tileSize*3, tileSize*3);
                   // }

                 //   if(board[i][j]/6==2){g.setColor(Color.black);}
                    //else{g.setColor(Color.white);}
                 //   g.drawString(""+board[i][j]%6, j*tileSize+radius, i*tileSize+radius);
                }
            }
        }
        repaintCount++;
        System.out.println("Paint: " + (System.currentTimeMillis()-start) + "ms" );
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
