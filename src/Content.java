import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Content extends JFrame {
    private Listener_Button decision;
    private Listener_Begin begin;
    private JTextField number_choice;
    private int NUM_THREADS;
    private int sign;
    private JFrame f = new JFrame("选择界面");
    private JButton ok = new JButton("启动");
    private JButton a1 = new JButton("Dekker");
    private JButton a2 = new JButton("Peterson");
    private JButton a3 = new JButton("Lamport");
    private JButton a4 = new JButton("Eisenberg/Mcgouire");

    public Content(){
        f.setSize(400,500);
        f.setLocation(400,150);

        f.setLayout(null);
        a1.setBounds(50,50,120,120);
        a2.setBounds(200,50,120,120);
        a3.setBounds(50,200,120,120);
        a4.setBounds(200,200,120,120);
        ok.setBounds(50,380,270,60);

        Panel mp = new Panel();
        mp.setBounds(50,360,270,1);
        mp.setBackground(Color.BLACK);

        decision = new Listener_Button();
        begin = new Listener_Begin();
        a1.addActionListener(decision);
        a2.addActionListener(decision);
        a3.addActionListener(decision);
        a4.addActionListener(decision);
        ok.addActionListener(begin);

        f.add(a1);
        f.add(a2);
        f.add(a3);
        f.add(a4);
        f.add(ok);
        f.add(mp);
        f.setVisible(true);

        JLabel ch1 = new JLabel("基于软件互斥算法的临界区进程互斥的模拟实现");
        ch1.setForeground(Color.BLACK);
        ch1.setBounds(50,10,400,20);
        f.add(ch1);

        number_choice = new JTextField();
        number_choice.setBounds(50,330,270,20);
        number_choice.setEditable(true);
        number_choice.setVisible(false);
        number_choice.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                number_choice.setText("");
            }

            @Override
            public void focusLost(FocusEvent focusEvent) {

            }
        });
        f.add(number_choice);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    class Listener_Button implements ActionListener{              //根据按钮，用sign来保存要调用哪个算法函数
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if(actionEvent.getSource().equals(a1)){
                sign = 1;
                number_choice.setEditable(false);
                number_choice.setVisible(false);

            }
            if(actionEvent.getSource().equals(a2)){
                sign = 2;
                number_choice.setEditable(false);
                number_choice.setVisible(false);

            }
            if(actionEvent.getSource().equals(a3)){
                sign = 3;
                number_choice.setEditable(true);
                number_choice.setVisible(true);
                number_choice.setText("请输入线程数：");
            }
            if(actionEvent.getSource().equals(a4)){
                sign = 4;
                number_choice.setEditable(true);
                number_choice.setVisible(true);
                number_choice.setText("请输入线程数：");
            }
        }
    }
    class Listener_Begin implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try{
                if(!number_choice.getText().toString().equals(""))
                    NUM_THREADS = Integer.valueOf(number_choice.getText().toString());
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null,"错误输入！");
                NUM_THREADS = 15;       //修复切换算法可能导致报错窗口的弹出
                return;
            }

            switch (sign){
                case 1:
                    Dekker();
                    break;
                case 2:
                    Peterson();
                    break;
                case 3:
                    Lamport();
                    break;
                case 4:
                    Eisenberg();
                    break;
            }
        }
    }

    private void Dekker(){
        Dekker_Resource resource = new Dekker_Resource(f);
        new Thread(new Dekker(0,resource)).start();
        new Thread(new Dekker(1,resource)).start();
    }
    private void Peterson(){
        Peterson_Resource resource = new Peterson_Resource(f);
        new Thread((new Peterson(0,resource))).start();
        new Thread((new Peterson(1,resource))).start();
    }
    private void Lamport(){
        Lamport_Resource resource = new Lamport_Resource(NUM_THREADS,f);
        for(int i=0;i<NUM_THREADS;i++){
            new Thread((new Lamport(i,resource))).start();
        }
    }
    private void Eisenberg(){
        Eisenberg_Resource resource = new Eisenberg_Resource(NUM_THREADS,f);
        for(int i=0;i<NUM_THREADS;i++){
            new Thread((new Eisenberg(i,resource))).start();
        }
    }
}


