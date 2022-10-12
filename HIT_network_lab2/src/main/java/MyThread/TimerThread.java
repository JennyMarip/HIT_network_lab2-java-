package MyThread;

public class TimerThread extends Thread{
    int SerNum;//被计时的数据包的序列号
    int timeLimit;//时限
    int[] outFlag;
    public TimerThread(int SerNum,int timeLimit,int[] outFlag){
        this.SerNum=SerNum;
        this.timeLimit=timeLimit;
        this.outFlag=outFlag;
    }
    @Override
    public void run(){
        for(int i=0;i<timeLimit/1000;i++){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        outFlag[SerNum]=1;
    }

}
