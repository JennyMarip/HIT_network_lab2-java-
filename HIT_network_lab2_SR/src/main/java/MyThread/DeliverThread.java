package MyThread;

import entity.BaseNum;
import entity.cache2;

import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

public class DeliverThread extends Thread{
    private List<cache2> buffer;
    private BaseNum baseNum;
    public DeliverThread(List<cache2> buffer,BaseNum baseNum){
        this.buffer=buffer;
        this.baseNum=baseNum;
    }
    @Override
    public void run(){
        if(buffer.size()!=0&&buffer.get(0).getSerNum()==baseNum.baseNum) {
            Iterator<cache2> it = buffer.iterator();
            int index = baseNum.baseNum;
            while (it.hasNext()) {
                cache2 ca = it.next();
                if (ca.getSerNum() == baseNum.baseNum) {
                    it.remove();
                    baseNum.baseNum++;
                    index = baseNum.baseNum;
                } else if (ca.getSerNum() == index + 1) {
                    it.remove();
                    baseNum.baseNum++;
                    index++;
                } else break;
            }
        }//模拟向上交付数据
    }
}
