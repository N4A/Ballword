package com.ballworld.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.ballworld.util.RotateUtil;
import com.ballworld.view.GameView;
import com.ballworld.view.WelcomeView;
import static com.ballworld.util.Constant.*;
import static com.ballworld.view.GameView.*;

public class MainActivity extends Activity {
    //声明变量
    //view
    WelcomeView welcomeView;
    GameView gameView;

    //功能引用
    SensorManager mySensorManager;	//SensorManager对象引用，后注册手机方向传感器

    //    界面转换控制
    public Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://切换主菜单界面
                    goToMenuView();
                    break;
                case 1://切换城镇的界面
                    goToTownView();
                    break;
                case 2://切换到建造房屋的界面
                    goToBuildHouseView();
                    break;
                case 3://切换到建造武器的界面
                    goToMakeWeaponView();
                    break;
                case 4://切换到玩家信息界面
                    goToPlayerInformationView();
                    break;
                case 5://切换到游戏界面
                    goToGameView();
                    break;
                case 6://切换到关于游戏界面
                    goToAboutGameView();
                    break;
                case 7://切换到游戏帮助界面
                    goToGameHelpView();
                    break;
                case 8://切换到游戏设置界面
                    goToSettingView();
                    break;
                case 9://回到欢迎界面
                    goToWelcomeView();
                    break;
            }
        }
    };

    //监听传感器
    private SensorListener mySensorListener = new SensorListener(){
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(int sensor, float[] values) {
            if(sensor == SensorManager.SENSOR_ORIENTATION) {//判断是否为加速度传感器变化产生的数据

                //通过倾角算出X轴和Z轴方向的加速度
                int directionDotXY[]= RotateUtil.getDirectionDot(
                        new double[]{values[0], values[1], values[2]}
                );
                //改变小球加速度
                ball.ballGX=-directionDotXY[0]*3.2f;//得到X和Z方向上的加速度
                ball.ballGZ=directionDotXY[1]*3.2f;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉tittle
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        //强制为横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //初始化变量
        //获取屏幕长宽,变量在Constant类声明
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_WIDTH = dm.widthPixels;
        //其他变量
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);//获得SensorManager对象

        //进入欢迎界面
        goToWelcomeView();
    }

    /**
     * 进入欢迎界面
     */
    private void goToWelcomeView() {
        if (welcomeView == null)
            welcomeView = new WelcomeView(this);
        this.setContentView(welcomeView);
    }

    /**
     * 进入主菜单界面
     * 并为主菜单界面的控件添加listener
     */
    private void goToMenuView() {
        this.setContentView(R.layout.menu);

        //get button
        ImageButton storyMode = (ImageButton) this.findViewById(R.id.storyModeButton),
                    casualMode = (ImageButton) this.findViewById(R.id.casualModeButton),
                    gameSetting = (ImageButton) this.findViewById(R.id.gameSettingButton),
                    gameHelp = (ImageButton) this.findViewById(R.id.gameHelpButton);

        //set listener
        storyMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(1);//城镇界面
            }
        });
        casualMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(5);//游戏界面
            }
        });
        gameSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(8);//设置界面
            }
        });
        gameHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hd.sendEmptyMessage(7);//帮助界面
            }
        });
    }

    /**
     * 进入城镇界面
     * 初始化变量
     * add listener
     */
    private void goToTownView() {
        setContentView(R.layout.main_town);

        //add listener
    }

    /**
     * 进入建造房屋的界面
     */
    private void goToBuildHouseView() {
        setContentView(R.layout.build_house);
    }

    /**
     * 进入制造武器的界面
     */
    private void goToMakeWeaponView() {
        setContentView(R.layout.make_weapon);
    }

    /**
     * 进入玩家信息界面
     * 要实现穿戴装备功能
     */
    private void goToPlayerInformationView() {

    }

    /**
     * 进入游戏界面
     */
    private void goToGameView() {
        levelId=0;//模拟用
        gameView = new GameView(this);
        gameView.requestFocus();//获得焦点
        gameView.setFocusableInTouchMode(false);//可触控
        this.setContentView(gameView);
    }

    /**
     * 进入游戏信息界面
     * 放在后面实现
     */
    private void goToAboutGameView() {
        setContentView(R.layout.about_game);
    }

    /**
     * 进入游戏帮助界面
     * 添加至少一个返回button listener
     * 放在后面实现
     */
    private void goToGameHelpView() {
        setContentView(R.layout.game_help);
    }

    /**
     * 进入游戏设置界面
     * 初始化变量
     * set listener
     * 放在后面实现
     */
    private void goToSettingView() {
        setContentView(R.layout.setting);
    }

    @Override
    protected void onResume() //重写onResume方法
    {
        super.onResume();
        mySensorManager.registerListener
                (			//注册监听器
                        mySensorListener, 					//监听器对象
                        SensorManager.SENSOR_ORIENTATION,	//传感器类型,倾角
                        SensorManager.SENSOR_DELAY_UI		//传感器事件传递的频度
                );
    }

    @Override
    protected void onPause() //重写onPause方法
    {
        super.onPause();
        mySensorManager.unregisterListener(mySensorListener);	//取消注册监听器
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
