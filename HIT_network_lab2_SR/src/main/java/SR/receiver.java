package SR;

import MyThread.DeliverThread;
import entity.BaseNum;
import entity.cache;
import entity.cache2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class receiver {
    private static int maxSecNum=100;//最大序列号为100
    private static int sizeOfWin=10;//滑动窗口大小
    private static int base=0;//窗口起始位置
    private static int targetPort=8080;
    public static void main(String[] args) throws IOException {
        DatagramSocket receiver=new DatagramSocket(8081);
        List<cache2> buffer=new ArrayList<>();//接收方缓存
        BaseNum baseNum=new BaseNum(base);
        System.out.println("接收方程序已启动");
        while(true){
            System.out.println("接收方窗口起始位置:"+baseNum.baseNum);
            DatagramPacket packet=new DatagramPacket(new byte[1024],1024);
            receiver.receive(packet);
            String newData=new String(packet.getData());
            int SerNum=Integer.valueOf(newData.split(":")[0]);
            if(SerNum<baseNum.baseNum){
                System.out.println("接收到序列号为 "+SerNum+"的数据分组:"+newData);
                String ack=SerNum+":ACK";
                DatagramPacket ACK=
                        new DatagramPacket(ack.getBytes(),ack.getBytes().length, InetAddress.getByName("192.168.124.1"),targetPort);
                receiver.send(ACK);//如果收到的序列号小于baseNum，只发ACK,不缓存
            }else if(SerNum>=baseNum.baseNum&&SerNum<baseNum.baseNum+sizeOfWin){
                System.out.println("接收到序列号为 "+SerNum+"的数据分组:"+newData);
                String ack=SerNum+":ACK";
                DatagramPacket ACK=
                        new DatagramPacket(ack.getBytes(),ack.getBytes().length,InetAddress.getByName("192.168.124.1"),targetPort);
                receiver.send(ACK);
                buffer.add(new cache2(SerNum,packet));
            }else{
                System.out.println("序列号不在接受范围内");
            }
            for(int i=0;i<buffer.size()-1;i++){
                for(int j=0;j< buffer.size()-1-i;j++){
                    if(buffer.get(j).getSerNum()>buffer.get(j+1).getSerNum()){
                        cache2 a=buffer.get(j);
                        cache2 b=buffer.get(j+1);
                        buffer.set(j+1,a);
                        buffer.set(j,b);
                    }
                }
            }//将缓存中的packet按序列号升序排列
            new DeliverThread(buffer,baseNum).start();
        }
    }
}
