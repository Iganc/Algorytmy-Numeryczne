package org.example;

public class wektor {
    public static double wx = Math.cos(Main.theta.doubleValue()) - 1;
    public static double wy = Math.sin(Main.theta.doubleValue());

    public wektor(double wx, double wy){
        this.wx = wx;
        this.wy = wy;

    }
    public double getwx(){
        return wx;
    }
    public double getwy(){
        return wy;
    }
    public void setwx(double wx){
        this.wx = wx;
    }
    public void setwy(double wy){
        this.wy = wy;
    }
    @Override
    public String toString(){
        return "wx: " + wx + " wy:" + wy;
    }
}
