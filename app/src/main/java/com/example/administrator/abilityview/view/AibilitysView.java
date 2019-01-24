package com.example.administrator.abilityview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

public class AibilitysView extends View {

    private Paint linePaint;//画线的笔
    private Paint textPaint;//画文字的笔
    private Object[] allAbility;

    private int n;    //边的数量或者能力的个数
    private float intervalCount;//间隔数量，把半径分为几段
    private float R; //最外圈的半径
    private float angle; //角度


    private int viewHight;//控件的高度
    private int viewWidth;//控件的宽度

    private ArrayList<ArrayList<PointF>> pointArrayList;//存储多边形顶点数组的数组
    private ArrayList<PointF> abilityPoints;//储存能力点的数组

    public AibilitysView(Context context) {
        this(context, null);
    }

    public AibilitysView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AibilitysView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initSize();

        initPoints();

        initPaint();
    }

    /**
     * 初始化点的位置
     */
    private void initPoints() {
        if (pointArrayList == null) {
            pointArrayList = new ArrayList<>();
        } else {
            pointArrayList.clear();
        }
        float x;
        float y;
        for (int i = 0; i < intervalCount; i++) {
            //创建一个存储点的数组
            ArrayList<PointF> points = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                float r = R * ((float) (intervalCount - i) / intervalCount);//每一圈的半径按比例减少
                //这里减去Math.PI /2 是为了让段变形逆时针旋转90度，所以后面的所有用到cos,sin的都要减
                x = (float) (r * Math.cos(j * angle - Math.PI / 2));
                y = (float) (r * Math.sin(j * angle - Math.PI / 2));
                points.add(new PointF(x, y));
            }
            pointArrayList.add(points);
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //画线的笔
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //设置线笔的宽度
        linePaint.setStrokeWidth(dp2px(getContext(), 1f));

        //画文字的笔
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);//设置文字居中
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(sp2pxF(getContext(), 14f));

    }

    /**
     * 初始化固定数据Size
     * 设置了是几边型；
     * 设置了最外圈的半径
     * 计算出每一边型的角度
     * 获取屏幕方向
     */
    private void initSize() {
        if (allAbility == null) {
            n = 10;//十条辺
        } else {
            n = allAbility.length;
        }
        R = dp2pxF(getContext(), 100);
        intervalCount = 4; //有四层
        angle = (float) ((2 * Math.PI) / n); //2π是一周，除以n是算出平均每一个的角度是多少


        /**
         *
         * 此方法默认获取的是整个手机的宽度和高度；无论如何调节控件或父控件的宽度都不可能实现，Size的改变
         *
         *
         //拿到屏幕的宽高
         int screenWidth = getResources().getDisplayMetrics().widthPixels;
         //控件设置为正方向
         viewHight = screenWidth;
         viewWidth = screenWidth;
         */
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        viewHight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHight);
//        //设置控件的最终视图大小
//        setMeasuredDimension(viewWidth, viewHight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize();
        initPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //把画布的原点移动到控件的中心点
        canvas.translate(viewWidth / 2, viewHight / 2);

        //绘制形状
        drawPolygon(canvas);

        //画出边框线
        drawOutLine(canvas);

        //画出能力线
        drawAbilityLine(canvas);

        //画出文字
        drawAbilityText(canvas);


    }

    private void drawAbilityText(Canvas canvas) {
        canvas.save();

        ArrayList<PointF> textPoints = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            float r = R + dp2pxF(getContext(), 15f);
            float x = (float) (r * Math.cos(i * angle - Math.PI / 2));
            float y = (float) (r * Math.sin(i * angle - Math.PI / 2));
            textPoints.add(new PointF(x, y));
        }
        //拿到字体测量器
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        for (int i = 0; i < n; i++) {
            float x = textPoints.get(i).x;
            //ascent:上坡度，是文字的基线到文字的最高处的距离
            //descent:下坡度,，文字的基线到文字的最低处的距离
            float y = textPoints.get(i).y - (metrics.ascent + metrics.descent) / 2;
            canvas.drawText(allAbility[i] + "", x, y, textPaint);
        }
        canvas.restore();
    }

    /**
     * 画出能力线
     *
     * @param canvas
     */
    private void drawAbilityLine(Canvas canvas) {

        if (allAbility == null) {
            return;
        }

        canvas.save();

        //先把能力点初始化出来
        abilityPoints = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            float r = R * (Float.valueOf("" + allAbility[i]) / 100.0f);  //能力值/100再乘以半径就是所占的比例
            float x = (float) (r * Math.cos(i * angle - Math.PI / 2));
            float y = (float) (r * Math.sin(i * angle - Math.PI / 2));
            abilityPoints.add(new PointF(x, y));
        }

        linePaint.setStrokeWidth(dp2px(getContext(), 2f));
        linePaint.setColor(Color.parseColor("#E96153"));
        linePaint.setStyle(Paint.Style.STROKE);  //设置空心的

        Path path = new Path();  //路径
        for (int i = 0; i < n; i++) {
            float x = abilityPoints.get(i).x;
            float y = abilityPoints.get(i).y;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        path.close();   //别忘了闭合

        canvas.drawPath(path, linePaint);

        canvas.restore();

    }

    /**
     * 绘制多边形的辺，轮廓线
     *
     * @param canvas
     */
    private void drawOutLine(Canvas canvas) {
        //遇上一个方法中的用意一样
        canvas.save();

        //设置画笔的颜色
        linePaint.setColor(Color.parseColor("#99DCC2"));
        //设置画笔的样式为空心
        linePaint.setStyle(Paint.Style.STROKE);

        //先画出最外面的多边形轮库
        Path path = new Path();
        for (int i = 0; i < n; i++) {
            //只需要第一组的点
            float x = pointArrayList.get(0).get(i).x;
            float y = pointArrayList.get(0).get(i).y;
            if (i == 0) {
                //如果是第一个点就把path的起点设置为这个点
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        //闭合路径
        path.close();
        canvas.drawPath(path, linePaint);

        //再画顶点到中心的线
        for (int i = 0; i < n; i++) {
            float x = pointArrayList.get(0).get(i).x;
            float y = pointArrayList.get(0).get(i).y;
            canvas.drawLine(0, 0, x, y, linePaint); //起点都是中心点
        }

        canvas.restore();

    }

    /**
     * 在画布上绘制画出的形状
     *
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        //保存画布当前状态（平移，缩放、旋转、裁剪）和canvas.restore()配合使用
        canvas.save();
        //填充且描边
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //设置路径
        Path path = new Path();
        //循环、一层一层的绘制
        for (int i = 0; i < intervalCount; i++) {
            //每一层的颜色更改
            switch (i) {
                case 0:
                    linePaint.setColor(Color.parseColor("#D4F0F3"));
                    break;
                case 1:
                    linePaint.setColor(Color.parseColor("#99DCE2"));
                    break;
                case 2:
                    linePaint.setColor(Color.parseColor("#56C1C7"));
                    break;
                case 3:
                    linePaint.setColor(Color.parseColor("#278891"));
                    break;
            }
            //每一层都有n个点
            for (int j = 0; j < n; j++) {
                float x = pointArrayList.get(i).get(j).x;
                float y = pointArrayList.get(i).get(j).y;
                if (j == 0) {
                    //如果是每层的第一个点，就把它设置为path的起始点
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            //关闭路径
            path.close();
            //在画布上画出路径
            canvas.drawPath(path, linePaint);
            //清除Path存储的路径
            path.reset();
        }
        canvas.restore();
    }

    /**
     * 传入元数据
     *
     * @param data
     */
    public void setData(Object[] data) {
        if (data == null) {
            return;
        }
        this.allAbility = data;

        //View本身调用迫使view重画
        invalidate();
    }

    /**
     * 下面都是工具类，dp单位转px单位
     */
    public static int dp2px(Context c, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context c, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }

    public static float dp2pxF(Context c, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static float sp2pxF(Context c, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, c.getResources().getDisplayMetrics());
    }
}
