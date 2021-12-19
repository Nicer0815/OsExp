import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Random;
/**
 * 关于flag的约定 flag == x：
 * 1：红色， 不想进入状态
 * 2：黄色， 想进入但还没进入状态 伴随箭头，表示请求进入
 * 3：绿色， 此轮竞争已经进入过临界区
 * 4：粉色， 正在临界区中运行
 */
class Dekker_Resource {
    public boolean[] wantEnter= {false,false};
    public int dekker_turn;              //turn
    public JDialog d;
    public JTextArea text;
    public JPanel rightPanel;
    public JScrollPane scroll;
    public DynamicPanelinDockker dynamicPannel;   //两个点
    Dekker_Resource(JFrame f){
        Random ra =new Random();
        dekker_turn =( ra.nextInt(10)+1) % 2;

        d = new JDialog(f);
        d.setSize(600,400);
        d.setLocation(900,200);
        d.setLayout(new BorderLayout());

        text = new JTextArea();
        scroll = new JScrollPane(text);
        scroll.setSize(220,390);
        text.setSize(220,390);
        scroll.setAutoscrolls(true);
        scroll.setVisible(true);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        text.setEditable(false);
        text.setAutoscrolls(true);
        d.add(scroll,BorderLayout.CENTER);

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setSize(200,400);
        d.add(rightPanel,BorderLayout.EAST);

        JTextArea D_text = new JTextArea();
        D_text.setEditable(false);
        D_text.setFont(new Font("宋体", Font.BOLD,25));
        D_text.append("        dekker  算法 ");
        D_text.setBackground(new Color(240,240,240));
        rightPanel.add(D_text, BorderLayout.NORTH);

        dynamicPannel = new DynamicPanelinDockker(90,200,280,200);
        dynamicPannel.setSize(400,200);
        dynamicPannel.setLayout(null);
        rightPanel.add(dynamicPannel, BorderLayout.CENTER);
        d.setVisible(true);
    }

}

public class Dekker implements Runnable {
    private int id;
    private Dekker_Resource resource;
    public int x,y;
    Dekker(int id,Dekker_Resource resource){
        this.id = id;
        this.resource = resource;
        if(id == 0){ this.x = 20;this.y = 100;}
        else{this.x = 220;this.y = 100;}
    }
    @Override
    public void run() {
        while (true) {
            resource.wantEnter[id] = true;
            resource.dynamicPannel.setColor(id, 2);//黄色,想进入但还没进入
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (resource.wantEnter[1 - id] == true) {   //另一个进程也想进入
                //引入一个turn变量来控制在p0/p1 wantEnter都为true时谁将获得进入critical region的权限
                if (resource.dekker_turn == 1 - id) {   //turn 为另一个进程 则主动放弃
                    resource.text.append("turn为" + resource.dekker_turn + ",进程" + id + "放弃\n");
                    resource.text.setCaretPosition(resource.text.getText().length());
                    resource.wantEnter[id] = false;
                    resource.dynamicPannel.setColor(id, 1);//红色，暂时等待不想进入的状态
                    while (resource.dekker_turn == 1 - id) {
                        //一直等到turn改变，此时再次参与竞争
                        resource.text.append("turn为 " + resource.dekker_turn + "进程 " + id + "等待中\n");
                        resource.text.setCaretPosition(resource.text.getText().length());
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    resource.wantEnter[id] = true;  //终于轮到我了嘛？再来请求进入
                    resource.dynamicPannel.setColor(id, 2);
                }else{  //防止因为 死循环忙式等待 JVM挂起进程
                    resource.text.append("turn为" + resource.dekker_turn + ",进程" + id + "等对方放弃\n");
                    resource.text.setCaretPosition(resource.text.getText().length());

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Random ra = new Random();
            resource.dynamicPannel.setColor(id, 4);     //进程进入临界区
            resource.text.append("进程" + id + "正在访问临界区----\n");
            resource.text.setCaretPosition(resource.text.getText().length());

            try {
                Thread.sleep(2000 + ra.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            resource.dekker_turn = 1 - id;
            resource.text.append("进程" + id + "访问结束！\n");
            resource.text.setCaretPosition(resource.text.getText().length());
            resource.text.append("turn 更新为" + resource.dekker_turn + "\n");
            resource.text.setCaretPosition(resource.text.getText().length());
            resource.dynamicPannel.setColor(id, 3);
            resource.wantEnter[id] = false;

        }
    }
}

class DynamicPanelinDockker extends Panel{
    //flag = 1,2,3 分别代表 红 黄 绿
    int x0,y0,x1,y1,flag0,flag1;
    Image image;
    public DynamicPanelinDockker(int x0,int y0,int x1,int y1){
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
        setColor(0,1);
        setColor(1,1);
        image = new ImageIcon("image/dekker_v2.png").getImage();

    }
    public void setColor(int id,int flag){
        if(id == 0){ //
            this.flag0 = flag;
        }else{
            this.flag1 = flag;
        }
        repaint();
    }
    public void DrawColorandShape(Graphics g){
        if(flag0 != 4) {
            switch (flag0) {
                case 1:
                    g.setColor(Color.RED);
                    break;
                case 2:
                    g.setColor(Color.YELLOW);
                    break;
                case 3:
                    g.setColor(Color.GREEN);
            }
            g.drawOval(x0, y0, 50, 50);    //坐标 宽高
            g.fillOval(x0, y0, 50, 50);
        }
        if(flag1 != 4) {
            switch (flag1) {
                case 1:
                    g.setColor(Color.RED);
                    break;
                case 2:
                    g.setColor(Color.YELLOW);
                    break;
                case 3:
                    g.setColor(Color.GREEN);
            }
            g.drawOval(x1, y1, 50, 50);    //坐标 宽高
            g.fillOval(x1, y1, 50, 50);
        }
    }
    public void DrawArrow(int x,int y,int x_target,int y_target,Graphics g){
        g.setColor(Color.BLACK);
        g.drawLine(x,y,x_target,y_target);  //箭头的箭尾
        g.drawLine(x_target,y_target,x_target-5,y_target+5);//箭头
        g.drawLine(x_target,y_target,x_target+5,y_target+5);

    }
    public void DrawInThreadShape(int id,Graphics g){
        g.setColor(Color.GREEN);
        g.drawString("进程 "+id+" 正在运行ing",170,60+60);
        g.setColor(Color.PINK);
        g.drawOval(180,60,50,50);    //坐标 宽高
        g.fillOval(180,60,50,50);
    }
    public void paint(Graphics g){
        g.drawString("进程0",x0,y0+60);
        g.drawString("进程1",x1,y1+60);
        this.DrawColorandShape(g);
        g.drawImage(this.image,110,0,Color.GRAY,this);
        if(flag0  == 2){
            this.DrawArrow(x0+30,y0,x0+60,y0-70,g);
        }
        if(flag1 == 2){
            this.DrawArrow(x1+10,y1,x1-20,y1-70,g);
        }
        if(flag0 == 4){
            this.DrawInThreadShape(0,g);
        }
        if(flag1 == 4){
            this.DrawInThreadShape(1,g);
        }
    }
}
