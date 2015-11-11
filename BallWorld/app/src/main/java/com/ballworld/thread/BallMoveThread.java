package com.ballworld.thread;

import com.ballworld.view.GameView;

import static com.ballworld.util.Constant.*;

/**
 * Created by duocai at 22:55 on 2015/11/10.
 */
public class BallMoveThread extends Thread {
    GameView gameView;//引用gameView
    public Boolean flag=true;//线程标志位
    public static float t=0.1f;//每次走的时间，认为规定的每次改变位置设为移动了该时间
    public static float ballRD;
    public float ballGX;
    public float ballGZ;//每次拷贝加速度


    int ballRow=0;//此格所在的行列
    int ballCol=0;

    public BallMoveThread(GameView gameView) {
        this.gameView = gameView;
        ballRD=ballR/2;
    }

    @Override
    public void run()
    {
        while(flag) {
            //确定加速度
            ballGX= GameView.ball.ballGX;//拷贝加速度
            ballGZ= GameView.ball.ballGZ;

            //确定速度
            GameView.ball.ballVX+=ballGX*t;
            GameView.ball.ballVZ+=ballGZ*t;//最终速度

            //确定位置和旋转角
            GameView.ball.ballX= GameView.ball.ballX+ GameView.ball.ballVX*t+ballGX*t*t/2;//VT+1/2A*T*T
            GameView.ball.ballZ= GameView.ball.ballZ+ GameView.ball.ballVZ*t+ballGZ*t*t/2;//最终位置
            GameView.ball.mAngleX+=(float)Math.toDegrees(((GameView.ball.ballVZ*t+ballGZ*t*t/2))/ballR);
            GameView.ball.mAngleZ-=(float)Math.toDegrees((GameView.ball.ballVX*t+ballGX*t*t/2)/ballR);//旋转的角度

            //如果当前前进值小于调整值，则相应的转动方向角归零
            if(Math.abs((GameView.ball.ballVZ*t+ballGZ*t*t/2))<0.005f) {
                GameView.ball.mAngleX=0;
            }
            if(Math.abs(GameView.ball.ballVX*t+ballGX*t*t/2)<0.005f) {
                GameView.ball.mAngleZ=0;
            }

            //速度衰减
            GameView.ball.ballVX*=V_TENUATION;
            GameView.ball.ballVZ*=V_TENUATION;//衰减

            //当速度小于某个调整值时,归0
            if(Math.abs(GameView.ball.ballVX)<0.04) {
                GameView.ball.ballVX=0;//速度归零
                GameView.ball.mAngleZ=0;//将绕轴选择的值置为零
            }
            if(Math.abs(GameView.ball.ballVZ)<0.04) {
                GameView.ball.ballVZ=0;
                GameView.ball.mAngleX=0;
            }

            //停顿后进入下一循环
            try {
                Thread.sleep(50);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
