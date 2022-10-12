package GBN;

import MyThread.AcceptThread;
import MyThread.OutputThread;
import MyThread.TimeoutThread;
import entity.BaseNum;
import entity.NextSec;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class receiver {
    private static int MaxSerNum=100;//序列号范围为[1,100]
    private static int base=0;//窗口起始位置为0
    private static int nextSecNum=0;//下一个将要使用的序列号为0
    private static int sizeOfWin=10;//设置滑动窗口大小
    private static int timeLimit=10*1000;//设置超时时间(10秒)
    private static int expectNum=0;//设置期望收到的序列号的数据分组
    public static void main(String[] args) throws IOException {
        int targetPort=8080;
        int expect = expectNum;
        BaseNum baseNum = new BaseNum(base);
        NextSec nextSec = new NextSec(nextSecNum);
        int[] outFlag = new int[100];//记录这一百个数据包哪个超时
        Map<Integer, DatagramPacket> buffer = new HashMap<>();//发送方缓存
        DatagramSocket senderSocket = new DatagramSocket(8081);
        System.out.println("接收方(服务器)程序启动");
        /******** 1:检查传过来的数据（ACK/数据分组） ********/
        new AcceptThread(senderSocket, baseNum, nextSec, timeLimit, outFlag, buffer, expect,targetPort).start();
        /******** 2:检查超时的最大序列号 ********/
        new TimeoutThread(outFlag, baseNum, timeLimit, buffer, nextSec, senderSocket).start();
        /******** 3:发送数据(数据分组) ********/
        new OutputThread(nextSec, baseNum, sizeOfWin, buffer, senderSocket, timeLimit, outFlag,targetPort).start();
    }
}
