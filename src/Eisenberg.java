import javax.swing.*;
import java.awt.*;
import java.util.Random;

class Eisenberg_Resource{
    int flags[] = null;
    int NUM_THREADS;
    int IDLE = 0;//表示进程未进入临界区
    int WAITING = 1;//表示想进程发出了请求，进入临界区
    int ACTIVE = 2;//表示进程可能已经进入，也可能没有进入临界区
    int turn;

    public JDialog d = null;
    public JTextArea text;
    public JScrollPane scroll;
    public ProgressPanel progresspanel;
    Eisenberg_Resource(int n,JFrame f){
        flags = new int[n];
        for(int i=0;i<n;i++)
            flags[i] = IDLE; //初始化 每个进程都是IDLE属性的
        NUM_THREADS = n;
        Random random = new Random();
        turn = random.nextInt(n);  //////////turn一开始随机生成

        d = new JDialog(f);
        d.setSize(900,600); ///////////总界面
        d.setLocation(500,300);
        d.setLayout(null);
        text = new JTextArea();
        scroll = new JScrollPane(text);
        scroll.setBounds(0,0,300,600);
        text.setEditable(false);
        text.setAutoscrolls(true);
        text.setLineWrap(true);
        text.selectAll();

        JTextArea L_text = new JTextArea();
        L_text.setEditable(false);
        L_text.setFont(new Font("宋体", Font.BOLD,35));
        L_text.append("         Eisenberg算法   ");
        L_text.setBackground(new Color(238,238,238));
        L_text.setBounds(300,0,600,100);

        progresspanel= new ProgressPanel(NUM_THREADS);
        progresspanel.setBackground(Color.white);
        progresspanel.setBounds(300,100,600, 500);
        d.add(scroll);
        d.add(L_text);
        d.add(progresspanel);

        d.setVisible(true);

    }

}



public class Eisenberg implements Runnable {
    int id;
    Eisenberg_Resource resource;
    Eisenberg(int id,Eisenberg_Resource resource){
        this.id = id;
        this.resource = resource;
    }
    public void run() {
        int index;


        while(true){
            Random ra = new Random();
            int m = ra.nextInt(10);
            if(m>3) {
                do {
                    resource.flags[id] = resource.WAITING;
                    resource.progresspanel.setColor(id ,resource.WAITING);

                    index = resource.turn;
                    while (index != id) {
//                        resource.text.append("正在查找是否存在非空闲进程......\n");
                        if (resource.flags[index] != resource.IDLE)
                            index = resource.turn;
                        else {
                            index = (index + 1) % resource.NUM_THREADS;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    resource.flags[id] = resource.ACTIVE;


                    index = 0;
                    while (resource.flags[index] != resource.ACTIVE)
                        index = (index + 1) % resource.NUM_THREADS;
                    resource.text.append("进程"+(id+1)+"正在等待.....\n");
                    resource.text.setCaretPosition(resource.text.getText().length());


                } while (!(index == id && (resource.turn == id || resource.flags[resource.turn] == resource.IDLE)));
                resource.turn = id;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                resource.text.append("---进程" + (id + 1) + "正在访问临界区---\n");
                resource.text.setCaretPosition(resource.text.getText().length());
                resource.progresspanel.setColor(id ,resource.ACTIVE);

                try {
                    Thread.sleep(1500 + ra.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                index = (resource.turn + 1) % resource.NUM_THREADS;
                while (resource.flags[index] == resource.IDLE) {
                    index = (index + 1) % resource.NUM_THREADS;
                }
                resource.turn = index;

                resource.text.append("*****进程" + (id + 1) + "访问结束！*****\n");
                resource.text.setCaretPosition(resource.text.getText().length());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resource.flags[id] = resource.IDLE;
                resource.progresspanel.setColor(id ,resource.WAITING);

            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resource.flags[id]= resource.IDLE;
                resource.progresspanel.setColor(id,resource.IDLE);
                resource.text.append("*****进程" + (id + 1) + "从现在开始不想进入临界区\n");
                resource.text.setCaretPosition(resource.text.getText().length());
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }//////////////////run 函数结束
}
class ProgressPanel extends Panel{
    int NUM_THREAD;
    int change_color[] = null;

    ProgressPanel(int NUM_THREAD){
        this.NUM_THREAD = NUM_THREAD;
        this.change_color=new int[NUM_THREAD];
        for (int i = 0; i < NUM_THREAD; i++) {
            this.change_color[i]=0;

        }
    }


    public void setColor(int id,int change_id)
    {
        change_color[id]=change_id;
        repaint();
    }
    public int jisuan_x(int yuanxin_x,int r,int n,int id) {
        double jiaodu = java.lang.Math.toRadians(360.0 / n * (id - 1));
        double x1 = yuanxin_x + r * java.lang.Math.cos(jiaodu);
        return (int) x1;
    }////////获得坐标X
    public int jisuan_y(int yuanxin_y,int r,int n,int id){
        double jiaodu=java.lang.Math.toRadians(360.0/n*(id-1));
        double y1=yuanxin_y+r*java.lang.Math.sin(jiaodu);
        return (int) y1;

    }///////////////获得坐标Y
    public void paint(Graphics g) {
        //g.setColor(Color.WHITE);
        //g.drawOval(100,100,400,500);
        for(int i = 0;i < NUM_THREAD; i++){
            g.setColor(Color.BLACK);
            g.drawOval(jisuan_x(270,95, NUM_THREAD, i), jisuan_y(205,95, NUM_THREAD, i), 35, 35);
            String s = String.valueOf(i+1);
            g.drawString(s,jisuan_x(270,95, NUM_THREAD, i), jisuan_y(205,95, NUM_THREAD, i));
            if(change_color[i]==0){                                       //红不变
                g.setColor(Color.RED);
                g.fillOval(jisuan_x(270,95, NUM_THREAD, i), jisuan_y(205,95, NUM_THREAD, i), 35, 35);
                continue;
            }
            if(change_color[i]==1){
                g.setColor(Color.YELLOW);
                g.fillOval(jisuan_x(270,95, NUM_THREAD, i), jisuan_y(205,95, NUM_THREAD, i), 35, 35);
                continue;
            }

            if(change_color[i]==2){
                g.setColor(Color.GREEN);
                g.fillOval(jisuan_x(270,95, NUM_THREAD, i), jisuan_y(205,95, NUM_THREAD, i), 35, 35);
                continue;
            }
            if(change_color[i]==3){
                g.setColor(Color.PINK);
                g.fillOval(jisuan_x(270,95, NUM_THREAD, i), jisuan_y(205,95, NUM_THREAD, i), 35, 35);
                continue;
            }

        }

    }

}