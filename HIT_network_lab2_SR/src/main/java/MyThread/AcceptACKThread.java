package MyThread;

import entity.BaseNum;
import entity.cache;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Iterator;
import java.util.List;

public class AcceptACKThread extends Thread{
    private DatagramSocket socket;
    private BaseNum baseNum;
    private List<cache> buffer;

    public AcceptACKThread(DatagramSocket socket,BaseNum baseNum,List<cache> buffer){
        this.socket=socket;
        this.baseNum=baseNum;
        this.buffer=buffer;
    }
    @Override
    public void run(){
        while(true){
            DatagramPacket packet=new DatagramPacket(new byte[1024],1024);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String newData=new String(packet.getData());
            int ackNum=Integer.valueOf(newData.split(":")[0]);
            System.out.println("接收到序列号为 "+ackNum+"的ACK");
            for(cache ca:buffer){
                if(ca.getSerNum()==ackNum){
                    ca.ACKed();
                }
            }//缓存中对应的对象被ack了
            if(buffer.size()!=0) {
                for (int i = 0; i < buffer.size() - 1; i++) {
                    for (int j = 0; j < buffer.size() - 1 - i; j++) {
                        if (buffer.get(j).getSerNum() > buffer.get(j + 1).getSerNum()) {
                            cache a = buffer.get(j);
                            cache b = buffer.get(j + 1);
                            buffer.set(j + 1, a);
                            buffer.set(j, b);
                        }
                    }
                }//将缓存中的packet按序列号升序排列
                Iterator<cache> it = buffer.iterator();
                while (it.hasNext()) {
                    if (it.next().isACKed()) {
                        it.remove();
                        baseNum.baseNum++;
                    }
                    else break;
                }//清理缓存
                if(buffer.size()!=0) {
                    baseNum.baseNum = buffer.get(0).getSerNum();
                }
            }
        }
    }
}
