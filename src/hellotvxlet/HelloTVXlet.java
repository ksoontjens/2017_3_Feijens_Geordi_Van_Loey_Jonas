package hellotvxlet;


import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Timer;
import javax.tv.xlet.*;
import org.havi.ui.event.*;
import org.dvb.event.*;
import java.awt.event.*;
import org.havi.ui.*;



public class HelloTVXlet implements Xlet,UserEventListener, ObserverInterface, HActionListener{
    
    static HScene scene = null; //dit hoort bij de klasse niet het object
    static Subject publisher = null;
    brick brick = null;
    ball bal = null;
    brick ending = null;
    Rectangle[][] brickranden = new Rectangle[7][7];
    brick[][] enemybricks = new brick[7][7];
    private HTextButton resetbtn;
    Timer timer;
    boolean space = false;

   public static HScene getScene(){
    return scene;
    }
    public static Subject getPublisher(){
    return publisher;
    }
    public void destroyXlet(boolean unconditional) throws XletStateChangeException {
        
    }

    public void initXlet(XletContext ctx) throws XletStateChangeException {
        scene  = HSceneFactory.getInstance().getDefaultHScene();
        
         publisher = new Subject();
        timer = new Timer();
        timer.scheduleAtFixedRate(publisher,0,10); //elke 10ms
        
        brick= new brick(200,410,200,30, Color.GREEN);
        scene.add(brick);
        int width = 90;
        int height = 25;
               for(int i=0;i<7;i++){
                   for(int j=0;j<7;j++){
                       
                      

                       
                      brick bricksm = new brick(30+92*i,40+26*j,width,height, Color.YELLOW);
                      scene.add(bricksm);
                      brickranden[i][j] = bricksm.getRect();
                      enemybricks[i][j] = bricksm;
                      
                   }
               }
        bal =  new ball(80,400,20,20);
        scene.add(bal);
        scene.validate();
        scene.setVisible(true);
        publisher.register(this);
        
        
            }

    public void pauseXlet() {
        
    }

    public void startXlet() throws XletStateChangeException {
        //event manager aanvragen
        EventManager  mngr = EventManager.getInstance();
        UserEventRepository repo = new UserEventRepository("Keys");
        repo.addAllArrowKeys();
        repo.addKey(HRcEvent.VK_SPACE);
        mngr.addUserEventListener(this, repo);     
    }

    
    public void userEventReceived(UserEvent e) {
      if(e.getType() == KeyEvent.KEY_PRESSED){
      switch(e.getCode()){
           
            case HRcEvent.VK_SPACE:  
                if (space == false)
                {
                    publisher.register(bal);
                    space = true;
                }
               
              break;               
           case HRcEvent.VK_RIGHT: 
              brick.MoveRight();
              break; 
           case HRcEvent.VK_LEFT: 
              brick.MoveLeft();
              break;
            }
      
      }
    }
    public void CollideBalk(){
        Rectangle mainbrickrand = brick.getRect();
        Rectangle balrand = bal.getRect();
        if(balrand.y > 720)
        {
            timer.cancel();
            endGame();
        }
        if(balrand.intersects(mainbrickrand))
        {
            bal.setYDir(-1);
        }
        
        for(int i=0;i<7;i++){
            for(int j=0;j<7;j++){
                    if(balrand.intersects(brickranden[i][j])){

                        Point pointRight = new Point(balrand.x + balrand.width + 1, balrand.y);
                        Point pointLeft = new Point(balrand.x - 1, balrand.y);
                        Point pointTop = new Point(balrand.x, balrand.y - 1);
                        Point pointBottom = new Point(balrand.x, balrand.y+ balrand.height + 1);
                        if(!enemybricks[i][j].isDestroyed()){
                            if (brickranden[i][j].contains(pointTop)) {
                            bal.setYDir(1);
                                if(bal.getXDir() == 1){
                                    bal.setXDir(-1);
                                }
                                else{
                                    bal.setXDir(1);
                                }
                                      
                            } else if (enemybricks[i][j].contains(pointBottom)) {
                                bal.setYDir(-1);
                                if(bal.getXDir() == 1){
                                    bal.setXDir(-1);
                                }
                                else{
                                    bal.setXDir(1);
                                }
                            }
                            if (brickranden[i][j].contains(pointRight)) {
                            bal.setXDir(-1);
                                
                            } else if (brickranden[i][j].contains(pointLeft)) {
                            bal.setXDir(1);
                            }
                           
                        }
                        enemybricks[i][j].setDestroyed(true);
                        scene.remove(enemybricks[i][j]);
                        scene.repaint();
                    }
            }
        }
    }
    
    public void update(int tijd) {
       CollideBalk();
    }
    
     public void actionPerformed(ActionEvent arg0) {
        String action = arg0.getActionCommand();
        if(action.equals("quit")){
            System.exit(0);
           
           System.out.println("net pindakaas gegeten");          
        } 
    }
    public void endGame(){
        ending = new brick(110,200,500,200,Color.YELLOW);
       resetbtn = new HTextButton("QUIT");
        resetbtn.setLocation(150,300);
        resetbtn.setBackground(Color.BLUE);
        resetbtn.setBackgroundMode(HVisible.BACKGROUND_FILL);
        resetbtn.setSize(100,50);
        resetbtn.setActionCommand("quit");
        resetbtn.addHActionListener(this);
        scene.add(resetbtn);
        scene.add(ending);
        resetbtn.requestFocus();
        scene.validate();
        scene.setVisible(true);
        scene.repaint();
    }


   
 
   
}
