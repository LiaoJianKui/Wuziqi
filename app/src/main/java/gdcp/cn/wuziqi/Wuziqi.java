package gdcp.cn.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class Wuziqi extends View {
    private int mPaneWidth;
    private float mLineHeight;
    private int MAX_LINE=10;
    private int MAX_COUNT_IN_LINE=7;
    private Paint mPaint=new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeigh=3*1.0f/4;

    private boolean mIsWhite=true;//白棋先手，开始轮到白棋
    private ArrayList<Point> mWhiteArray=new ArrayList<>();
    private ArrayList<Point> mBlackArray=new ArrayList<>();

    private boolean isGameOver;
    private boolean isWhiteWinner;



    public Wuziqi(Context context) {
        super(context);
    }

    public Wuziqi(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPiece= BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);//获取宽的尺寸
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);//获取宽的模式

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);//获取高的尺寸
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);//获取高的模式
        //获取较小的长度作为棋盘的边长
        int width=Math.min(widthSize,heightSize);

        if(widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);//保存测量宽度和测量高度

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaneWidth=w;
        //棋盘行高
        mLineHeight=mPaneWidth*1.0f/MAX_LINE;
        //设置棋子的宽度
        int pieceWidth= (int) (mLineHeight*ratioPieceOfLineHeigh);
        //白棋子的大小
        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        //黑棋子的大小
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver){
            return false;
        }
        int action = event.getAction();
        if (action==MotionEvent.ACTION_UP){
            int x= (int) event.getX();
            int y= (int) event.getY();

            Point p=getValidPoint(x,y);
            if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }
            if(mIsWhite){
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite=!mIsWhite;

        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)( x/mLineHeight),(int)(y/mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawPiece(canvas);
        //检查游戏是否结束
        checkGameOver();

    }

    private void checkGameOver() {
        boolean whiteWin=checkFiveLine(mWhiteArray);
        boolean blackWin=checkFiveLine(mBlackArray);
        if(whiteWin||blackWin){
            isGameOver=true;
            isWhiteWinner=whiteWin;
            String text=isWhiteWinner?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }
    //检测五子棋是否连续五个连成一线
    private boolean checkFiveLine(List<Point> points) {
        for (Point p:points){
            int x=p.x;
            int y=p.y;
           boolean win= checkHorziontal(x,y,points);
            if (win){
                return true;
            }
            win= checkVertical(x,y,points);
            if (win){
                return true;
            }
            win= checkLeftDiagonal(x,y,points);
            if (win){
                return true;
            }
            win= checkRightDiagonal(x,y,points);
            if (win){
                return true;
            }

        }
        return false;
    }

    /**
     * 判断x,y位置的棋子，是否横向有相邻的五个一致
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorziontal(int x, int y, List<Point> points) {
        int count=1;
        //左边
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //右边
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count=1;
        //上边
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //下边
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count=1;
        //左斜上
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //左斜下
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count=1;
        //右斜上
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        //右斜下
        for (int i = 0; i <MAX_COUNT_IN_LINE ; i++) {
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE){
            return true;
        }
        return false;
    }

    private void drawPiece(Canvas canvas) {
        for (int i=0;i<mWhiteArray.size();i++){
            Point whitePoint=mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-ratioPieceOfLineHeigh)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeigh)/2)*mLineHeight,null);
        }
        for (int i=0;i<mBlackArray.size();i++){
            Point blackPoint=mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-ratioPieceOfLineHeigh)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeigh)/2)*mLineHeight,null);
        }

    }

    private void drawBoard(Canvas canvas) {
        int w=mPaneWidth;
        float lineHeight=mLineHeight;
        for (int i=0;i<MAX_LINE;i++){
            //绘制的起点
            int startX= (int) (lineHeight/2);
            //绘制的终点
            int endX= (int) (w-lineHeight/2);
            int y= (int) ((0.5+i)*lineHeight);
            //遍历绘制X轴方向的线
            canvas.drawLine(startX,y,endX,y,mPaint);
            //遍历绘制Y轴方向的线
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }

    private static final String INSTANCE="instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,isGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle){
            Bundle bundle= (Bundle) state;
            isGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public  void start(){
        mBlackArray.clear();
        mWhiteArray.clear();
        isGameOver=false;
        mIsWhite=false;
        invalidate();
    }
}
