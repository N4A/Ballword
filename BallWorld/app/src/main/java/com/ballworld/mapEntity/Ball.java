package com.ballworld.mapEntity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import static com.ballworld.util.Constant.*;

/**
 * Created by duocai at 22:24 on 2015/11/8.
 */
public class Ball {
    private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    private FloatBuffer mTextureBuffer;//顶点纹理数据缓冲
    public float mAngleX;//沿x轴旋转角度
    public float mAngleY;//沿y轴旋转角度
    public float mAngleZ;//沿z轴旋转角度
    public static float ballX;//球的各个坐标
    public static float ballY;
    public static float ballZ;
    public static float ballGX=0f;//x方向上的加速度
    public static float ballGZ=0f;//z方向上的加速度

    public static int ballCsX;//初始格子，y为0
    public static int ballCsZ;
    public static int ballMbX;//目标格子
    public static int ballMbZ;

    public static float ballVX=0;//XZ方向上的速度
    public static float ballVZ=0;
    int vCount=0;//顶点数量

    /**
     * 构造函数，初始化小球
     * @param scale
     * @param angleSpan
     */
    public Ball(float scale,float angleSpan) {
        this.ballY=scale;//小球高度坐标为半径
        //获取切分整图的纹理数组
        float[] texCoorArray=generateTexCoor
                        (
                                (int)(360/angleSpan), //纹理图切分的列数
                                (int)(180/angleSpan)  //纹理图切分的行数
                        );
        int tc=0;//纹理数组计数器
        int ts=texCoorArray.length;//纹理数组长度

        ArrayList<Float> alVertix=new ArrayList<Float>();//存放顶点坐标的ArrayList
        ArrayList<Float> alTexture=new ArrayList<Float>();//存放纹理坐标的ArrayList

        for(float vAngle=90;vAngle>-90;vAngle=vAngle-angleSpan)//垂直方向angleSpan度一份
        {
            for(float hAngle=360;hAngle>0;hAngle=hAngle-angleSpan)//水平方向angleSpan度一份
            {
                //纵向横向各到一个角度后计算对应的此点在球面上的四边形顶点坐标
                //并构建两个组成四边形的三角形
                double xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
                float x1=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
                float z1=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
                float y1=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));

                xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
                float x2=(float)(xozLength*Math.cos(Math.toRadians(hAngle)));
                float z2=(float)(xozLength*Math.sin(Math.toRadians(hAngle)));
                float y2=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));

                xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle-angleSpan));
                float x3=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
                float z3=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
                float y3=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle-angleSpan)));

                xozLength=scale*UNIT_SIZE*Math.cos(Math.toRadians(vAngle));
                float x4=(float)(xozLength*Math.cos(Math.toRadians(hAngle-angleSpan)));
                float z4=(float)(xozLength*Math.sin(Math.toRadians(hAngle-angleSpan)));
                float y4=(float)(scale*UNIT_SIZE*Math.sin(Math.toRadians(vAngle)));

                //构建第一三角形
                alVertix.add(x1);alVertix.add(y1);alVertix.add(z1);
                alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
                alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
                //构建第二三角形
                alVertix.add(x4);alVertix.add(y4);alVertix.add(z4);
                alVertix.add(x2);alVertix.add(y2);alVertix.add(z2);
                alVertix.add(x3);alVertix.add(y3);alVertix.add(z3);

                //第一三角形3个顶点的6个纹理坐标
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                //第二三角形3个顶点的6个纹理坐标
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
                alTexture.add(texCoorArray[tc++%ts]);
            }
        }



        vCount=alVertix.size()/3;//顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        //将alVertix中的坐标值转存到一个int数组中
        float vertices[]=new float[vCount*3];
        for(int i=0;i<alVertix.size();i++)
        {
            vertices[i]=alVertix.get(i);
        }

        //创建绘制顶点数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置

        //创建顶点法向量数据缓冲
        ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mNormalBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mNormalBuffer.position(0);//设置缓冲区起始位置

        //创建纹理坐标缓冲
        float textureCoors[]=new float[alTexture.size()];//顶点纹理值数组
        for(int i=0;i<alTexture.size();i++)
        {
            textureCoors[i]=alTexture.get(i);
        }

        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoors.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer = tbb.asFloatBuffer();//转换为int型缓冲
        mTextureBuffer.put(textureCoors);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
    }

    public void drawSelf(GL10 gl,int texId)
    {
        gl.glTranslatef(ballX, ballY, ballZ);     //移动相应的位置
        gl.glRotatef(mAngleZ, 0, 0, 1);//沿Z轴旋转
        gl.glRotatef(mAngleX, 1, 0, 0);//沿X轴旋转
        gl.glRotatef(mAngleY, 0, 1, 0);//沿Y轴旋转

        //允许使用顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //为画笔指定顶点坐标数据
        gl.glVertexPointer
                (
                        3,				//每个顶点的坐标数量为3  xyz
                        GL10.GL_FLOAT,	//顶点坐标值的类型为 GL_FIXED
                        0, 				//连续顶点坐标数据之间的间隔
                        mVertexBuffer	//顶点坐标数据
                );


        //为画笔指定顶点法向量数据
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);

        //开启纹理
        //为画笔指定纹理ST坐标缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        //绑定当前纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);

        //绘制图形
        gl.glDrawArrays
                (
                        GL10.GL_TRIANGLES, 		//以三角形方式填充
                        0, 			 			//开始点编号
                        vCount					//顶点数量
                );
    }

    //自动切分纹理产生纹理数组的方法
    public float[] generateTexCoor(int bw,int bh)//传入切分的列数  ， 行数
    {
        float[] result=new float[bw*bh*6*2];
        float sizew=1.0f/bw;//列宽
        float sizeh=1.0f/bh;//行宽
        int c=0;
        for(int i=0;i<bh;i++)
        {
            for(int j=0;j<bw;j++)
            {
                //每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
                float s=j*sizew;
                float t=i*sizeh;

                result[c++]=s;
                result[c++]=t;

                result[c++]=s;
                result[c++]=t+sizeh;

                result[c++]=s+sizew;
                result[c++]=t;


                result[c++]=s+sizew;
                result[c++]=t;

                result[c++]=s;
                result[c++]=t+sizeh;

                result[c++]=s+sizew;
                result[c++]=t+sizeh;
            }
        }
        return result;
    }
}
