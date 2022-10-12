package SR;

import MyThread.AcceptACKThread;
import MyThread.OutputThread;
import MyThread.timeoutThread;
import entity.BaseNum;
import entity.NextSec;
import entity.cache;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sender {
    private static int timeLimit=10*1000;//设置超时时间为10秒
    private static int maxSecNum=100;//最大序列号为100
    private static int sizeOfWin=10;//滑动窗口大小
    private static int base=0;//窗口起始位置
    private static int next=0;//下一个将要用的序列号
    private static int targetPort=8081;//目的进程端口号

    public static void main(String[] args) throws SocketException {
        BaseNum baseNum=new BaseNum(base);
        NextSec nextSec=new NextSec(next);
        List<cache> buffer=new ArrayList<>();//发送方缓存
        DatagramSocket senderSocket=new DatagramSocket(8080);
        System.out.println("发送端程序已启动");
        /******** 1:发送udp数据分组 ********/
        new OutputThread(buffer,timeLimit,baseNum,nextSec,sizeOfWin,targetPort,senderSocket).start();
        /******** 2:处理接收到的ACK ********/
        new AcceptACKThread(senderSocket,baseNum,buffer).start();
        /******** 3:检查发送过的数据分组是否有超时及相应处理 ********/
        new timeoutThread(buffer,senderSocket,timeLimit).start();
    }
}
