package com.ballworld.thread;

import com.ballworld.mapEntity.CoverBlock;
import com.ballworld.view.GameView;

import static com.ballworld.util.Constant.*;
import static com.ballworld.view.GameView.*;

/**
 * Created by duocai at 22:55 on 2015/11/10.
 */
public class BallMoveThread extends Thread {
    GameView gameView;//引用gameView
    public Boolean flag = true;//线程标志位
    public static float t = 0.1f;//每次走的时间，认为规定的每次改变位置设为移动了该时间
    public float ballGX;
    public float ballGZ;//每次拷贝加速度

    public BallMoveThread(GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    public void run() {
        while (flag) {
            //清楚脚下覆盖方块，并判断是不是炸弹
            clearCoverBlock(ball.ballX,ball.ballZ);

            //确定加速度//拷贝加速度,作为此时加速度处理
            ballGX = ball.ballGX;
            ballGZ = ball.ballGZ;

            //判断是否撞墙
            knockWall(ball.ballX, ball.ballZ, ball.ballVX * t + ball.ballGX * t * t * 0.5, ball.ballVZ * t + ball.ballGZ * t * t * 0.5);

            //确定速度
            ball.ballVX += ballGX * t;
            ball.ballVZ += ballGZ * t;//最终速度

            //确定位置和旋转角
            if(ball.ballY>0)
                ball.ballY-=0.1f;
            if (ball.ballY<0)
                ball.ballY=0f;
            ball.ballX = ball.ballX + ball.ballVX * t + ballGX * t * t / 2;//V*T+1/2A*T*T
            ball.ballZ = ball.ballZ + ball.ballVZ * t + ballGZ * t * t / 2;//最终位置

            //旋转的角度
            ball.mAngleX += (float) Math.toDegrees(((ball.ballVZ * t + ballGZ * t * t / 2)) / ballR);
            ball.mAngleZ -= (float) Math.toDegrees((ball.ballVX * t + ballGX * t * t / 2) / ballR);
            //如果当前前进值小于调整值，则相应的转动方向角归零
            if (Math.abs((ball.ballVZ * t + ballGZ * t * t / 2)) < 0.005f) {
                ball.mAngleX = 0;
            }
            if (Math.abs(ball.ballVX * t + ballGX * t * t / 2) < 0.005f) {
                ball.mAngleZ = 0;
            }

            //速度衰减
            ball.ballVX *= V_TENUATION;
            ball.ballVZ *= V_TENUATION;//衰减

            //当速度小于某个调整值时,归0
            if (Math.abs(ball.ballVX) < 0.04) {
                ball.ballVX = 0;//速度归零
                ball.mAngleZ = 0;//将绕轴选择的值置为零
            }
            if (Math.abs(ball.ballVZ) < 0.04) {
                ball.ballVZ = 0;
                ball.mAngleX = 0;
            }

            //停顿后进入下一循环
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是将要撞到墙
     * @param ballX
     * @param ballZ
     * @param xForward
     * @param zForward
     * @return
     */
    public Boolean knockWall(float ballX, float ballZ, double xForward, double zForward) {
        Boolean flag = false;
        //将地图移到XZ都大于零的象限,以匹配数组
        ballX = map[0].length * UNIT_SIZE / 2 + ballX;
        ballZ = map.length * UNIT_SIZE / 2 + ballZ;
        
        if (zForward > 0) {//如果向Z轴正方向运动
            //循环，假如它一下穿过几个格子，那么从第一个格子开始判断
            for (int i = (int) ((ballZ + ballR) / UNIT_SIZE); i<map.length && i <= (int) ((ballZ + ballR + zForward) / UNIT_SIZE); i++) {
                //判断是否碰墙壁了,Z向正碰
                if (map[i][(int) (ballX / UNIT_SIZE)] == 1 && map[i - 1][(int) (ballX / UNIT_SIZE)] == 0) {
                    ball.ballVZ = -ball.ballVZ * VZ_TENUATION;//将速度置反，并调整

                    //如果速度调反后还是会穿墙，那么将加速度归零，并将球画在和墙壁紧挨着的地方
                    if ((ball.ballZ + ball.ballVZ * t + ballGZ * t * t / 2) >= (i * UNIT_SIZE - ballR - map.length * UNIT_SIZE / 2)) {
                        ball.ballZ = (i * UNIT_SIZE - ballR - map.length * UNIT_SIZE / 2);
                        ball.ballVZ = 0;
                        ballGZ = 0;
                    }

                    flag = true;//标志位置为true
                }
            }
        }

        //如果向X轴正方向走
        if (xForward > 0) {
            for (int i = (int) ((ballX + ballR) / UNIT_SIZE); i<map[0].length&&i <= (int) ((ballX + ballR + xForward) / UNIT_SIZE); i++) {//循环，假如它一下穿过几个格子，那么从第一个格子开始判断
                if (map[(int) (ballZ / UNIT_SIZE)][i] == 1 && map[(int) (ballZ / UNIT_SIZE)][i - 1] == 0) {//如果碰壁了
                    ball.ballVX = -ball.ballVX * VZ_TENUATION;//速度置反，并调整

                    //如果速度调反后还是会穿墙，那么将加速度归零，并将球画在和墙壁紧挨着的地方
                    if ((ball.ballX + ball.ballVX * t + ballGX * t * t / 2) >((i) * UNIT_SIZE - ballR - map[0].length * UNIT_SIZE / 2)) {
                        ball.ballX = (i) * UNIT_SIZE - ballR - map[0].length * UNIT_SIZE / 2;
                        ballGX = 0;//加速度和速度设置为零
                        ball.ballVX = 0;
                    }

                    flag = true;
                }
            }
        }

        //x轴负方向
        if (xForward < 0) {
            //循环判断是否碰壁
            for (int i = (int) ((ballX-ballR) / UNIT_SIZE); i >= (int) ((ballX - ballR + xForward) / UNIT_SIZE); i--) {
                if (map[(int) (ballZ / UNIT_SIZE)][i] == 1 && map[(int) (ballZ / UNIT_SIZE)][i + 1] == 0) {//如果碰壁
                    ball.ballVX = -ball.ballVX * VZ_TENUATION;//速度置反并调整，
                    //任然会撞墙
                    if ((ball.ballX + ball.ballVX * t + ballGX * t * t / 2) < ((1 + i) * UNIT_SIZE + ballR - map[0].length * UNIT_SIZE / 2)) {
                        ball.ballX = (1 + i) * UNIT_SIZE + ballR  - map[0].length * UNIT_SIZE / 2;
                        ballGX = 0;//加速度和速度设置为零
                        ball.ballVX = 0;
                    }

                    flag = true;
                }
            }
        }

        //向Z轴负方向上运动时
        if (zForward < 0) {
            //循环看是否碰壁了
            for (int i = (int) ((ballZ-ballR) / UNIT_SIZE); i >= (int) ((ballZ - ballR + zForward) / UNIT_SIZE); i--) {
                if (map[i][(int) (ballX / UNIT_SIZE)] == 1 && map[i + 1][(int) (ballX / UNIT_SIZE)] == 0) {
                    ball.ballVZ = -ball.ballVZ * VZ_TENUATION;//将速度置反，并调整

                    //看调整后的速度下，是否会穿墙
                    if ((ball.ballZ + ball.ballVZ * t + ballGZ * t * t / 2) <= ((1 + i) * UNIT_SIZE + ballR - map.length * UNIT_SIZE / 2)) {
                        ball.ballZ = (1 + i) * UNIT_SIZE + ballR - map.length * UNIT_SIZE / 2;
                        ball.ballVZ = 0;
                        ballGZ = 0;
                    }

                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 清除覆盖方块
     * @param ballX
     * @param ballZ
     */
    public void clearCoverBlock(float ballX,float ballZ) {
        //将地图移到XZ都大于零的象限,以匹配数组
        ballX = map[0].length * UNIT_SIZE / 2 + ballX;
        ballZ = map.length * UNIT_SIZE / 2 + ballZ;
        coverBlocks[(int)(ballZ/UNIT_SIZE)][(int)(ballX/UNIT_SIZE)]=0;
        if (mapBomb[(int)(ballZ/UNIT_SIZE)][(int)(ballX/UNIT_SIZE)]==1) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mapBomb[(int)(ballZ/UNIT_SIZE)][(int)(ballX/UNIT_SIZE)]=2;

            //声效
        }

        //到达目标
        if ((int)(ballZ/UNIT_SIZE)==3&&(int)(ballX/UNIT_SIZE)==4) {
            activity.hd.sendEmptyMessage(0);
        }
    }

}
