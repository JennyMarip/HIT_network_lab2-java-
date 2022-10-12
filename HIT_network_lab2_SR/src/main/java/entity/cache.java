package entity;

import java.net.DatagramPacket;

public class cache {
    private DatagramPacket packet;
    private int SerNum;
    private boolean isTimeout=false;
    private boolean isACKed=false;
    public cache(int SerNum,DatagramPacket packet){
        this.SerNum=SerNum;
        this.packet=packet;
    }
    public boolean isTimeout(){
        return isTimeout;
    }
    public boolean isACKed(){
        return isACKed;
    }
    public int getSerNum(){
        return this.SerNum;
    }
    public DatagramPacket getPacket(){
        return this.packet;
    }
    public void timeout(){
        this.isTimeout=true;
    }
    public void ACKed(){
        isACKed=true;
    }
    public void setPacket(DatagramPacket packet){
        this.packet=packet;
    }
    public void setSerNum(int SerNum){
        this.SerNum=SerNum;
    }

}
