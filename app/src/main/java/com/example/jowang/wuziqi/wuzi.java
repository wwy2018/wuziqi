package com.example.jowang.wuziqi;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jowang on 16/7/30.
 */
public class wuzi extends View {
    private int panelwidth;
    private float lineheight;
    private int MAX_LINE=10;
    private Paint paint=new Paint();
    private Bitmap white;
    private Bitmap black;
    private float ratio=3*1.0f/4;
    private boolean iswhite=true;
    private ArrayList<Point> whitearray=new ArrayList<>();
    private ArrayList<Point> blackarray=new ArrayList<>();
    private boolean gameover;
    private boolean whitewinner;
    private int MAXCOUNTLINE=5;

    public wuzi(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(0x88000000);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        white=BitmapFactory.decodeResource(getResources(),R.drawable.qi1);
        black=BitmapFactory.decodeResource(getResources(),R.drawable.qi2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthsize=MeasureSpec.getSize(widthMeasureSpec);
        int widthmode=MeasureSpec.getMode(widthMeasureSpec);
        int heightsize=MeasureSpec.getSize(heightMeasureSpec);
        int heightmode=MeasureSpec.getMode(heightMeasureSpec);
        int width=Math.min(widthsize,heightsize);
        if (widthmode==MeasureSpec.UNSPECIFIED){
            width=heightsize;
        }else if (heightmode==MeasureSpec.UNSPECIFIED){
            width=widthsize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        panelwidth=w;
        lineheight=panelwidth*1.0f/MAX_LINE;
        int whitewidth=(int)(lineheight*ratio);
        white=Bitmap.createScaledBitmap(white,whitewidth,whitewidth,false);
        black=Bitmap.createScaledBitmap(black,whitewidth,whitewidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameover) return false;
        int action=event.getAction();
        if (action==MotionEvent.ACTION_UP){
            int x=(int)event.getX();
            int y=(int)event.getY();
            Point p=getValidPoint(x,y);
            if (whitearray.contains(p)||blackarray.contains(p)){
                return false;
            }
            if (iswhite){
                whitearray.add(p);
            }else {
                blackarray.add(p);
            }
            invalidate();
            iswhite=!iswhite;

        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int)(x/lineheight),(int)(y/lineheight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGame();
    }

    private void checkGame() {
        boolean whitewin=checkFiveInLine(whitearray);
        boolean blackwin=checkFiveInLine(blackarray);
        if (whitewin||blackwin){
            gameover=true;
            whitewinner=whitewin;
            String text=whitewinner?"whitewin":"blackwin";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p:points){
            int x=p.x;
            int y=p.y;
            boolean win=checkHorizontal(x,y,points);
            if (win) return true;
            win=checkVertical(x,y,points);
            if (win) return true;
            win=left(x,y,points);
            if (win) return true;
            win=right(x,y,points);
            if (win) return true;
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count=1;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        return false;
    }
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count=1;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        return false;
    }
    private boolean left(int x, int y, List<Point> points) {
        int count=1;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        return false;
    }
    private boolean right(int x, int y, List<Point> points) {
        int count=1;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        for (int i=1;i<MAXCOUNTLINE;i++){
            if (points.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==MAXCOUNTLINE) return true;
        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i=0;i<whitearray.size();i++){
            Point whitepoint=whitearray.get(i);
            canvas.drawBitmap(white,(whitepoint.x+(1-ratio)/2)*lineheight,(whitepoint.y+(1-ratio)/2)*lineheight,null);
        }
        for (int i=0;i<blackarray.size();i++){
            Point blackpoint=blackarray.get(i);
            canvas.drawBitmap(black,(blackpoint.x+(1-ratio)/2)*lineheight,(blackpoint.y+(1-ratio)/2)*lineheight,null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w=panelwidth;
        float lineHeight=lineheight;
        for (int i=0;i<MAX_LINE;i++){
            int startX=(int)(lineHeight/2);
            int endX=(int)(w-lineHeight/2);
            int y=(int)((0.5+i)*lineHeight);
            canvas.drawLine(startX,y,endX,y,paint);//horizon line
            canvas.drawLine(y,startX,y,endX,paint);//vertical line
        }
    }
    private static final String INSTANCE="instance";
    private static final String INSTANCE_OVER="gameover";
    private static final String INSTANCE_WHITE="white";
    private static final String INSTANCE_BLACK="black";
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_OVER,gameover);
        bundle.putParcelableArrayList(INSTANCE_WHITE,whitearray);
        bundle.putParcelableArrayList(INSTANCE_BLACK,blackarray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle= (Bundle) state;
            gameover=bundle.getBoolean(INSTANCE_OVER);
            whitearray=bundle.getParcelableArrayList(INSTANCE_WHITE);
            blackarray=bundle.getParcelableArrayList(INSTANCE_BLACK);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
    public void restart(){
        whitearray.clear();
        blackarray.clear();
        gameover=false;
        whitewinner=false;
        invalidate();
    }
}
