package MyThread;

import entity.cache;

import java.util.List;

public class myTimerThread extends Thread{
    private int SerNum;
    private int timeLimit;
    private List<cache> buffer;
    public myTimerThread(int SerNum,int timeLimit,List<cache> buffer){
        this.SerNum=SerNum;
        this.timeLimit=timeLimit;
        this.buffer=buffer;
    }
    @Override
    public void run(){
        for(int i=0;i<timeLimit/1000;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }//计时完成
        if(buffer.size()!=0) {
            for (cache ca : buffer) {
                if (ca.getSerNum() == SerNum && !ca.isACKed()) {
                    ca.timeout();//如果计时对应的数据依然在缓存中，并且他还没有被确认，那么他就超时了
                    //超时对应的处理在另一个线程
                }
            }
        }
    }
}
