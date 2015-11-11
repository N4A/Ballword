package com.ballworld.util;

/**
 * Created by duocai at 19:25 on 2015/10/31.
 */
public class Constant {
    //屏幕长宽
    public static int SCREEN_HEIGHT;
    public static int SCREEN_WIDTH;

    public static final float WALL_HEIGHT=1.7f;//墙的高度
    public static final float UNIT_SIZE=1f;//地面每个格子的大小
    public static final float FLOOR_Y=0f;//地面的Y坐标
    public static final float ballR=1*UNIT_SIZE/0.8f;//球半径
    public static final float V_TENUATION=0.950f;//速度衰减系数
    public static final float VZ_TENUATION=0.655f;//碰壁时的速度衰减系数
    public static final float SD_TZZ=0.45f;	//速度最低限值
    public static final float TOUCH_SCALE_FACTOR=180.0f/480f;//角度缩放
    public static final float DISTANCE=45;//30f;//摄像机与目标点的距离
}
