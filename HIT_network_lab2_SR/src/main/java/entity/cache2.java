package entity;

import java.net.DatagramPacket;

public class cache2 {
    private int SerNum;
    private DatagramPacket packet;
    public cache2(int SerNum,DatagramPacket packet){
        this.SerNum=SerNum;
        this.packet=packet;
    }
    public int getSerNum(){
        return this.SerNum;
    }
    public DatagramPacket packet(){
        return this.packet;
    }
}
