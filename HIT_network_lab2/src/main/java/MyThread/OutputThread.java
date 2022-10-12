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
import java.util.Map;
import java.util.Scanner;

public class OutputThread extends Thread{
    NextSec nextSec;
    BaseNum baseNum;
    int sizeOfWin;
    Map<Integer, DatagramPacket> map;
    DatagramSocket socket;
    int timeLimit;
    int[] nums;
    int targetPort;

    public OutputThread(NextSec nextSec,BaseNum baseNum,int sizeOfWin,Map<Integer,DatagramPacket> map,DatagramSocket socket,int timeLimit,int[] nums,int targetPort){
        this.nextSec=nextSec;
        this.baseNum=baseNum;
        this.sizeOfWin=sizeOfWin;
        this.map=map;
        this.socket=socket;
        this.timeLimit=timeLimit;
        this.nums=nums;
        this.targetPort=targetPort;
    }
    @Override
    public void run(){
        Scanner in=new Scanner(System.in);
        while (true){
            if (nextSec.nextSec >= baseNum.baseNum + sizeOfWin) {
                System.out.println("发送窗口已满，请等待片刻再发送数据");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else{
                int SerNum=nextSec.nextSec;
                nextSec.nextSec=(SerNum+1)%100;
                System.out.println("请输入你想发送的数据(或口令):");
                String data=in.nextLine();
                if(data.contains("post")){
                    File file=new File("D:\\JAVA\\HIT_network_lab2\\src\\main\\java\\file\\sender.txt");
                    try {
                        FileInputStream input=new FileInputStream(file);
                        byte[] bytes=new byte[(int)file.length()];
                        input.read(bytes);
                        String res=new String(bytes);
                        String content=SerNum+":packet:"+res;
                        DatagramPacket packet=
                                new DatagramPacket(content.getBytes(),content.getBytes().length,InetAddress.getByName("192.168.124.1"),targetPort);
                        socket.send(packet);
                        map.put(SerNum, packet);
                        if (SerNum == baseNum.baseNum) new TimerThread(SerNum, timeLimit, nums).start();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if(data.contains("get")){
                    try {
                        String content=SerNum+":packet:"+data;
                        DatagramPacket packet=new DatagramPacket(content.getBytes(),content.getBytes().length,InetAddress.getByName("192.168.124.1"),targetPort);
                        socket.send(packet);
                        map.put(SerNum,packet);
                        if (SerNum == baseNum.baseNum) new TimerThread(SerNum, timeLimit, nums).start();
                    } catch (UnknownHostException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    String content = SerNum + ":packet:" + data;
                    DatagramPacket packet;
                    try {
                        packet =
                                new DatagramPacket(content.getBytes(), content.getBytes().length, InetAddress.getByName("192.168.124.1"), targetPort);
                        socket.send(packet);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    map.put(SerNum, packet);
                    if (SerNum == baseNum.baseNum) new TimerThread(SerNum, timeLimit, nums).start();
                }
            }
        }
    }
}
