package com.ballworld.mapEntity;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import static com.ballworld.util.Constant.*;

/**
 * 表示地板的类
 */
public class Road {
	private FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;//顶点纹理数据缓冲
    private FloatBuffer mNormalBuffer;
    int vCount=0;//顶点数量
    int length;//地板横向length个单位
    int width;//地板纵向width个单位
    public Road(int length, int width)
    {
    	this.length=length;
    	this.width=width;
    	//顶点坐标数据的初始化================begin============================
        vCount=6;//每个地板块6个顶点,两个三角形
    	float []vertices=new float[]
    	{
    			-length*UNIT_SIZE/2,0,-width*UNIT_SIZE/2,
    			-length*UNIT_SIZE/2,0,width*UNIT_SIZE/2,
    			length*UNIT_SIZE/2,0,width*UNIT_SIZE/2,
    		
    			length*UNIT_SIZE/2,0,width*UNIT_SIZE/2,
    			length*UNIT_SIZE/2,0,-width*UNIT_SIZE/2,
    			-length*UNIT_SIZE/2,0,-width*UNIT_SIZE/2
    	};    	
    	ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为int型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置       
        float textures[]=new float[]
        {
        		0,0,
        		0,2,
        		2,2,
        		
        		2,2,
        		2,0,
        		0,0
        };
        
        ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length*4);
        tbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mTextureBuffer= tbb.asFloatBuffer();//转换为Float型缓冲
        mTextureBuffer.put(textures);//向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
//        顶点法向量数据的初始化================begin============================
        float normals[]=new float[vCount*3];//y轴方向
        for(int i=0;i<vCount;i++)
        {
        	normals[i*3]=0;
        	normals[i*3+1]=1;
        	normals[i*3+2]=0;
        }

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为int型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点着色数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
        
        
    }

    public void drawSelf(GL10 gl,int texId) {
        gl.glPushMatrix();
		//为画笔指定顶点坐标数据
        gl.glVertexPointer
        (
        		3,				//每个顶点的坐标数量为3  xyz 
        		GL10.GL_FLOAT,	//顶点坐标值的类型为 GL_Float
        		0, 				//连续顶点坐标数据之间的间隔
        		mVertexBuffer	//顶点坐标数据
        );      
        
        //为画笔指定纹理ST坐标缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        //绑定当前纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		
        gl.glNormalPointer(GL10.GL_FLOAT, 0, mNormalBuffer);
        //绘制图形
        gl.glDrawArrays
        (
        		GL10.GL_TRIANGLES, 		//以三角形方式填充
        		0, 			 			//开始点编号
        		vCount					//顶点的数量
        );

        gl.glPopMatrix();
    }
}
