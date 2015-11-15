package com.ballworld.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ballworld.util.RotateUtil;
import com.ballworld.view.GameView;
import com.ballworld.view.WelcomeView;

import java.util.HashMap;

import static com.ballworld.util.Constant.SCREEN_HEIGHT;
import static com.ballworld.util.Constant.SCREEN_WIDTH;
import static com.ballworld.view.GameView.OnTouchListener;
import static com.ballworld.view.GameView.ballGX;
import static com.ballworld.view.GameView.ballGZ;

/**
 * Created by duocai at 20:24 on 2015/10/31.
 */
public class MainActivity extends Activity {
    //声明变量
    //view
    WelcomeView welcomeView;
    GameView gameView;
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
    //功能引用
    Vibrator myVibrator;//声明振动器
    boolean shakeflag=true;//是否震动
    SoundPool soundPool;//声音池
    HashMap<Integer, Integer> soundPoolMap; //记录声音池返回的资源id
    boolean backgroundSoundFlag = true;//是否播放背景音乐
    boolean knockWallSoundFlag = true;//撞壁音效
    SensorManager mySensorManager;    //SensorManager对象引用，后注册手机方向传感器
    //监听传感器
    private SensorListener mySensorListener = new SensorListener() {
        @Override
        public void onAccuracyChanged(int sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(int sensor, float[] values) {
            if (sensor == SensorManager.SENSOR_ORIENTATION) {//判断是否为加速度传感器变化产生的数据

                //通过倾角算出X轴和Z轴方向的加速度
                int directionDotXY[] = RotateUtil.getDirectionDot(
                        new double[]{values[0], values[1], values[2]}
                );
                //改变小球加速度
                ballGX = -directionDotXY[0] * 3.2f;//得到X和Z方向上的加速度
                ballGZ = directionDotXY[1] * 3.2f;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获得SensorManager对象
        myVibrator=(Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);//获得震动服务
        initSound();
        //进入欢迎界面
        goToWelcomeView();
    }

    /**
     * 初始化音乐
     */
    private void initSound(){
        //声音池
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPoolMap = new HashMap<Integer, Integer>();
        //撞墙音
        soundPoolMap.put(1, soundPool.load(this, R.raw.dong, 1));
        soundPoolMap.put(2,soundPool.load(this,R.raw.bomb,1));
    }

    /**
     * 播放声音
     * @param sound - 声音id
     * @param loop - loop —— 循环播放的次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次（例如，3为一共播放4次）.
     */
    public void playSound(int sound, int loop) {
        AudioManager mgr = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);//音量
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        switch (sound) {
            case 1:
                if (knockWallSoundFlag)//是否关闭撞壁音效
                    soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
                break;
            case 2:
                soundPool.play(soundPoolMap.get(sound), volume, volume, 1, loop, 1f);
                break;
        }

    }

    /**
     * 包装震动
     * @param mode - 震动模式，0：一次100ms短震动
     */
    public void shake(int mode) {
        if (shakeflag) {//判断用户是否关闭震动
            switch (mode) {
                case 0:
                    myVibrator.vibrate(100);
            }
        }
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
        final ImageView hospital = (ImageView) findViewById(R.id.hospital);
        final ImageView food = (ImageView) findViewById(R.id.food);
        final ImageView fabricate = (ImageView) findViewById(R.id.fabricate);
        final ImageView wood = (ImageView) findViewById(R.id.wood);
        final ImageView mine = (ImageView) findViewById(R.id.mine);
        final ImageView house = (ImageView) findViewById(R.id.house);
        final ImageView arraw1 = (ImageView) findViewById(R.id.arraw1);
        final ImageView arraw2 = (ImageView) findViewById(R.id.arraw2);
        hospital.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    hospital.setImageResource(R.drawable.hospitalpressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x-previousX;
                    float dy = y-previousY;
                    if ((dx>2||dy>2)&&previousX!=0&&previousY!=0)
                        click = false;
                    previousX=x;
                    previousY=y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    hospital.setImageResource(R.drawable.hospital);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });


        house.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    house.setImageResource(R.drawable.housepressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x-previousX;
                    float dy = y-previousY;
                    if ((dx>2||dy>2)&&previousX!=0&&previousY!=0)
                        click = false;
                    previousX=x;
                    previousY=y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    house.setImageResource(R.drawable.house);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });

        wood.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    wood.setImageResource(R.drawable.woodpressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x-previousX;
                    float dy = y-previousY;
                    if ((dx>2||dy>2)&&previousX!=0&&previousY!=0)
                        click = false;
                    previousX=x;
                    previousY=y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    wood.setImageResource(R.drawable.wood);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });

        food.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    food.setImageResource(R.drawable.foodpressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x - previousX;
                    float dy = y - previousY;
                    if ((dx > 2 || dy > 2) && previousX != 0 && previousY != 0)
                        click = false;
                    previousX = x;
                    previousY = y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    food.setImageResource(R.drawable.food);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });

        mine.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    mine.setImageResource(R.drawable.minepressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x - previousX;
                    float dy = y - previousY;
                    if ((dx > 2 || dy > 2) && previousX != 0 && previousY != 0)
                        click = false;
                    previousX = x;
                    previousY = y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    mine.setImageResource(R.drawable.mine);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });

        fabricate.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    fabricate.setImageResource(R.drawable.fabricatepressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x - previousX;
                    float dy = y - previousY;
                    if ((dx > 2 || dy > 2) && previousX != 0 && previousY != 0)
                        click = false;
                    previousX = x;
                    previousY = y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    fabricate.setImageResource(R.drawable.fabricate);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });

        arraw1.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    arraw1.setImageResource(R.drawable.arraw1pressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x - previousX;
                    float dy = y - previousY;
                    if ((dx > 2 || dy > 2) && previousX != 0 && previousY != 0)
                        click = false;
                    previousX = x;
                    previousY = y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    arraw1.setImageResource(R.drawable.arraw1);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });

        arraw2.setOnTouchListener(new OnTouchListener() {
            boolean click = true;
            float previousX = 0;
            float previousY = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                if (event.getAction() == event.ACTION_DOWN) {
                    arraw2.setImageResource(R.drawable.arraw2pressed);
                }
                if (event.getAction() == event.ACTION_MOVE) {
                    float dx = x - previousX;
                    float dy = y - previousY;
                    if ((dx > 2 || dy > 2) && previousX != 0 && previousY != 0)
                        click = false;
                    previousX = x;
                    previousY = y;
                }
                if (event.getAction() == event.ACTION_UP) {
                    arraw2.setImageResource(R.drawable.arraw2);
                    if (click)
                        hd.sendEmptyMessage(0);
                    click = true;
                }
                return false;
            }
        });
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
        int levelId = (int)(Math.random()*4);
        gameView = new GameView(this, levelId);//模拟第0（1）关
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
                (            //注册监听器
                        mySensorListener,                    //监听器对象
                        SensorManager.SENSOR_ORIENTATION,    //传感器类型,倾角
                        SensorManager.SENSOR_DELAY_UI        //传感器事件传递的频度
                );
    }

    @Override
    protected void onPause() //重写onPause方法
    {
        super.onPause();
        mySensorManager.unregisterListener(mySensorListener);    //取消注册监听器
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
