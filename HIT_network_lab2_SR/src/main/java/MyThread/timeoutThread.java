package MyThread;

import entity.cache;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

public class timeoutThread extends Thread{
    private List<cache> buffer;
    DatagramSocket socket;
    int timeLimit;

    public timeoutThread(List<cache> buffer,DatagramSocket socket,int timeLimit){
        this.buffer=buffer;
        this.socket=socket;
        this.timeLimit=timeLimit;
    }
    @Override
    public void run(){
        while(true) {
            if (buffer.size() != 0) {
                for (cache ca : buffer) {
                    if (ca.isTimeout()) {
                        System.out.println("序列号为 " + ca.getSerNum() + "的数据分组超时");
                        try {
                            socket.send(ca.getPacket());
                            new myTimerThread(ca.getSerNum(), timeLimit, buffer).start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("重传完成");
                    }
                }
            }
        }
    }
}
