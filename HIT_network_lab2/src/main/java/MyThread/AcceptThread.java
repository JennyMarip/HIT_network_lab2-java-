package MyThread;

import entity.BaseNum;
import entity.NextSec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class AcceptThread extends Thread{
    DatagramSocket socket;
    static BaseNum baseNum;
    static NextSec nextSec;
    int timeLimit;
    int[] bytes;
    Map<Integer, DatagramPacket> map;
    int expect;
    int targetPort;
    public AcceptThread(DatagramSocket socket,BaseNum baseNum,NextSec nextSec,int timeLimit,int[] bytes,Map<Integer, DatagramPacket> map,int expect,int targetPort){
        this.socket=socket;
        this.baseNum=baseNum;
        this.nextSec=nextSec;
        this.timeLimit=timeLimit;
        this.bytes=bytes;
        this.map=map;
        this.expect=expect;
        this.targetPort=targetPort;
    }
    @Override
    public void run() {
        while(true) {
            DatagramPacket data = new DatagramPacket(new byte[1024], 1024);
            try {
                socket.receive(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String newData;
            int ACKSerNum = -1;
            newData = new String(data.getData());
            if (newData.contains("ACK")) {//如果收到的数据是ACK的话
                ACKSerNum = Integer.valueOf(newData.split(":")[0]);
                System.out.println("收到序列号为" + ACKSerNum + "的数据分组的ack");
                System.out.println("ACK内容为:");
                System.out.println(newData);
                baseNum.baseNum = ACKSerNum + 1;
                if (baseNum.baseNum == 100) {
                    clean(bytes);
                    baseNum.baseNum = 0;
                }
                if(NextSec.nextSec> baseNum.baseNum)new TimerThread(baseNum.baseNum, timeLimit, bytes).start();//如果收到ack且还有未确认的分组，重新计时
                Iterator<Map.Entry<Integer, DatagramPacket>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, DatagramPacket> entry = iterator.next();
                    if (!isWithin(entry.getKey(), baseNum.baseNum, NextSec.nextSec)) iterator.remove();
                }//删除已经确认到达的缓存
            } else if (newData.contains("packet")&&!newData.contains("get")) {//接收到的是一个分组
                if (Integer.valueOf(newData.split(":")[0]) == expect) {
                    System.out.println("接收到序列号为 " + expect + "的数据分组:"+newData);
                    String ack = expect + ":ACK";
                    try {
                        DatagramPacket ACK =
                                new DatagramPacket(ack.getBytes(), ack.getBytes().length, InetAddress.getByName("192.168.124.1"), targetPort);
                        socket.send(ACK);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    expect=(expect+1)%100;
                }
            } else if(newData.contains("get")){
                if (Integer.valueOf(newData.split(":")[0]) == expect) {
                    System.out.println("接收到客户端的get命令,序列号为 " +expect);
                    File file = new File("D:\\JAVA\\HIT_network_lab2\\src\\main\\java\\file\\receiver.txt");
                    try {
                        String ack=expect+":ACK:";
                        FileInputStream input = new FileInputStream(file);
                        byte[] bytes = new byte[(int) file.length()];
                        input.read(bytes);
                        String content = new String(bytes);
                        ack=ack+content;
                        DatagramPacket ACK=
                                new DatagramPacket(ack.getBytes(),ack.getBytes().length,InetAddress.getByName("192.168.124.1"),targetPort);
                        socket.send(ACK);
                        expect=(expect+1)%100;
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else{
                System.out.println("接收到文本数据为:");
                System.out.println(newData);
            }
        }
    }
    public static void clean(int[] nums){
        for(int i=0;i<nums.length;i++){
            nums[i]=0;
        }
    }
    public static boolean isWithin(int num,int left,int right){
        return num >= left && num < right;
    }
}
