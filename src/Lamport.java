import javax.swing.*;
import java.awt.*;
import java.util.Random;
/**
 * 关于flag的约定 flag == x：
 * 1：红色， 不想进入状态
 * 2：黄色， 想进入但还没进入状态 伴随箭头，表示请求进入
 * 3：绿色， 此轮竞争已经进入过临界区
 * 4：粉色， 正在临界区中运行
 * 5：蓝色， 正在取号
 */
class Lamport_Resource {

    public int number[];
    public int choosing[];
    public int N;
    public JDialog d;
    public JTextArea text;
    public JPanel rightPanel;
    public JScrollPane scroll;
    public DynamicPanelinLamport dynamicPannel;   //两个点
    Lamport_Resource(int n,JFrame f){
        Random ra =new Random();
        number = new int[n];
        choosing =new int[n];
        N = n;

        d = new JDialog(f);
        d.setSize(700,650);
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
        D_text.append("        Lamport  算法 ");
        D_text.setBackground(new Color(240,240,240));
        rightPanel.add(D_text, BorderLayout.NORTH);

        dynamicPannel = new DynamicPanelinLamport(N,number);
        dynamicPannel.setSize(400,200);
        dynamicPannel.setLayout(null);
        rightPanel.add(dynamicPannel, BorderLayout.CENTER);
        d.setVisible(true);
    }
    public int getNumber(int id){
        int num = 0;
        for(int i = 0;i<N;i++){
            if(num<this.number[i]){
                num = this.number[i];
            }
        }
        return this.number[id] = num + 1;
    }
    public boolean morePriority(int id,int theOtherId){
        return (number[id] != 0) &&
                ((number[id] < number[theOtherId])
                        ||(number[id] == number[theOtherId] && id < theOtherId));
    }
}

public class Lamport implements Runnable {
    public int id;
    private Lamport_Resource resource;
    Lamport(int id,Lamport_Resource resource){
        this.id = id;
        this.resource = resource;
    }
    @Override
    public void run() {
        Random ra = new Random();
        while (true) {
            resource.dynamicPannel.setColor(id,1);
            try {
                Thread.sleep(1000 + ra.nextInt(4000));     //红色的显示时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            resource.choosing[id] = 1;
            resource.dynamicPannel.setColor(id,5);
            try {
                Thread.sleep(1000 + ra.nextInt(1000));  //蓝色 取号过程的显示时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resource.getNumber(id);         //取号
            resource.text.append("进程"+id+" 取到号码：" + resource.number[id]+"\n");
            resource.text.setCaretPosition(resource.text.getText().length());
            resource.choosing[id] = 0;
            resource.dynamicPannel.setColor(id,2);  // 黄色 想进临界区 已经排完号
            try {
                Thread.sleep(1000 + ra.nextInt(2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i = 0;i<resource.N;i++){
                while (resource.choosing[i] != 0){  //当进程 i 正在取号，则等待其取完
                    resource.text.append("当进程 "+i+" 正在取号，"+"进程 "+id+" 则等待其取完\n");
                    resource.text.setCaretPosition(resource.text.getText().length());
                    try {
                        Thread.sleep(1000 + ra.nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                while(resource.morePriority(i,id)){ //i 进程更优先，等待
                    if(ra.nextInt(13)%12 == 0) {
                        resource.text.append("当进程 " + i + " 更优先，" + "进程 " + id + " 等待\n");
                    }
                    resource.text.setCaretPosition(resource.text.getText().length());
                    try {
                        Thread.sleep(100 + ra.nextInt(200));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //临界区
            resource.text.append("进程" + id + "正在访问临界区----\n");
            resource.text.setCaretPosition(resource.text.getText().length());
            resource.dynamicPannel.setColor(id,4);
            try {
                Thread.sleep(1000 + ra.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            resource.number[id] = 0;
            //非临界区
            resource.text.append("进程" + id + "访问结束！\n");
            resource.text.setCaretPosition(resource.text.getText().length());
            resource.dynamicPannel.setColor(id,3);  //绿色 访问结束

            try {
                Thread.sleep(ra.nextInt(12000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class DynamicPanelinLamport extends Panel{
    int x[],y[],flag[];
    int number[];
    int x0,y0,dis,len;
    int N;
    Image image;
    public DynamicPanelinLamport(int n,int nums[]){
        N = n;
        x = new int[n];
        y = new int[n];
        flag = new int[n];
        number = nums;
        x0 = 50; //相对原点
        y0 = 180;
        dis = 70;//相对距离
        len = 5;//一行个数
        for (int i = 0;i<n;i++){
            x[i] = x0 + (i % len) * dis;
            y[i] = y0 + i / len * dis;
            flag[i] = 1;
        }
        image = new ImageIcon("image/dekker_v2.png").getImage();
    }
    public void setColor(int id,int color){
        flag[id] = color;
        repaint();
    }
    public void DrawColorandShape(Graphics g){
        for(int i = 0;i<N;i++) {
            if (flag[i] != 4) {
                switch (flag[i]) {
                    case 1:
                        g.setColor(Color.RED);
                        break;
                    case 2:
                        g.setColor(Color.YELLOW);
                        break;
                    case 3:
                        g.setColor(Color.GREEN);
                        break;
                    case 5:
                        g.setColor(Color.BLUE);
                }
                g.drawOval(x[i], y[i], 50, 50);    //坐标 宽高
                g.fillOval(x[i], y[i], 50, 50);
                g.setColor(Color.BLACK);
                g.drawString("进程"+i,x[i]+10, y[i]+60);
                g.drawString(""+number[i],x[i]+20,y[i]+30);
            }else{
                this.DrawInThreadShape(i,g);
            }
        }
    }
    public void DrawInThreadShape(int id,Graphics g){
        g.setColor(Color.GREEN);
        g.drawString("进程 "+id+" 正在运行ing",170,60+60);
        g.setColor(Color.PINK);
        g.drawOval(180,60,50,50);    //坐标 宽高
        g.fillOval(180,60,50,50);
    }
    public void paint(Graphics g){
        g.drawImage(this.image,110,0,Color.GRAY,this);
        this.DrawColorandShape(g);

    }
}