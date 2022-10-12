package MyThread;

import entity.BaseNum;
import entity.NextSec;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

public class TimeoutThread extends Thread{
    int[] nums;
    BaseNum baseNum;
    int timeLimit;
    NextSec nextSec;
    Map<Integer, DatagramPacket> map;
    DatagramSocket socket;
    public TimeoutThread(int[] nums,BaseNum baseNum,int timeLimit,Map<Integer,DatagramPacket>map,NextSec nextSec,DatagramSocket socket){
        this.nums=nums;
        this.baseNum=baseNum;
        this.timeLimit=timeLimit;
        this.map=map;
        this.nextSec=nextSec;
        this.socket=socket;
    }
    @Override
    public void run(){
        while(true){
            if (MaxOutSer(nums) == baseNum.baseNum) {
                System.out.println("time out事件发生，现在将缓存内容重传");
                new TimerThread(baseNum.baseNum, timeLimit, nums).start();//重新开始计时
                for (Map.Entry<Integer, DatagramPacket> entry : map.entrySet()) {
                    if (isWithin(entry.getKey(), baseNum.baseNum, nextSec.nextSec)) {
                        try {
                            socket.send(entry.getValue());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }//如果超时，则将所有未确认数据分组全部发出(time out事件)
                System.out.println("数据分组重传完毕");
            }
        }
    }
    public static int MaxOutSer(int[] nums){
        for(int i= nums.length-1;i>=0;i--){
            if(nums[i]==1)return i;
        }
        return -1;
    }
    public static boolean isWithin(int num,int left,int right){
        if(num>=left&&num<right)return true;
        return false;
    }
}
