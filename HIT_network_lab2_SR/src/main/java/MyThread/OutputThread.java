package MyThread;

import entity.BaseNum;
import entity.NextSec;
import entity.cache;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class OutputThread extends Thread{
    private List<cache> buffer;
    private int timeLimit;
    private BaseNum baseNum;
    private NextSec nextSec;
    private int sizeOfWin;
    private int targetPort;
    private DatagramSocket socket;
    public OutputThread(List<cache> buffer,int timeLimit,BaseNum baseNum,NextSec nextSec,int sizeOfWin,int targetPort,DatagramSocket socket){
        this.buffer=buffer;
        this.timeLimit=timeLimit;
        this.baseNum=baseNum;
        this.nextSec=nextSec;
        this.sizeOfWin=sizeOfWin;
        this.targetPort=targetPort;
        this.socket=socket;
    }
    @Override
    public void run(){
        while(true){
            if(nextSec.nextSec>=baseNum.baseNum+sizeOfWin){
                System.out.println("现无可用的序列号,请稍等片刻");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else{
                int SerNum= nextSec.nextSec;
                nextSec.nextSec=(SerNum+1)%100;
                System.out.println("请输入你想传输的数据:");
                Scanner in=new Scanner(System.in);
                String data=in.nextLine();
                System.out.println("发送方窗口起始位置:"+baseNum.baseNum+",下一序列号:"+nextSec.nextSec);
                String content=SerNum+":packet:"+data;
                DatagramPacket packet;
                try {
                    packet=
                            new DatagramPacket(content.getBytes(),content.getBytes().length, InetAddress.getByName("192.168.124.1"),targetPort);
                    socket.send(packet);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                cache newCache=new cache(SerNum,packet);
                buffer.add(newCache);//将发送的数据分组缓存在本地
                new myTimerThread(SerNum,timeLimit,buffer).start();//每一个发送出去的分组都要进行计时
            }
        }
    }
}
