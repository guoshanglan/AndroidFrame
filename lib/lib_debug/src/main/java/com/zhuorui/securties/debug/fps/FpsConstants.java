package com.zhuorui.securties.debug.fps;

/**
 * FpsConstants
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  13:34
 */
public class FpsConstants {

     public static final int FPS_INTERVAL_COST_DEFAULT = 16;
     public static final int FPS_MAX_DEFAULT = 60;
     public static final int FPS_MAX_COUNT_DEFAULT = 100000;
     public static final double FRAME_INTERVAL_NANOS = Math.pow(10,9)/ 60;
     public static final int NANOS_PER_MS = 1000000;
     public static final int MS_PER_SECOND = 1000;

     public static final int MS_CPU_SECOND = 1000;

     public static final int METHOD_TRACE_SKIP = 2;
     /**
      * 采样频率
      */
     public static final int METHOD_TRACE_SKIP_INTERVAL = 50;


}
