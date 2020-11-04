package com.example.hitschedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.example.hitschedule.R;
import com.example.hitschedule.ui.MainActivity;
import com.zhuangfei.timetable.model.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 桌面小组件，快速显示本日课程
 */
public class PreviewWidget extends AppWidgetProvider {

    /**
     * refresh_button被点击的事件
     */
    private static final String REFRESH_BUTTON_CLICKED = "RefreshButtonClick";

    /**
     * 课程时间表
     */
    private static final String[] courseTimes = new String[]{
            "8:00", "9:45", "10:00", "11:45",
            "13:45", "15:30", "15:45", "17:30",
            "18:30", "20:15", "20:30", "22:15"
    };

    /**
     * 所有小组件的ID
     */
    private int[] widgetIds;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        widgetIds = appWidgetIds;

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_preview);
        ComponentName watchWidget = new ComponentName(context, PreviewWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.refresh_button, getPendingIntent(context));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (REFRESH_BUTTON_CLICKED.equals(intent.getAction())) {    // 手动更新widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            for (int appWidgetId : widgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }

    /**
     * 获取点击refresh_button的PendingIntent
     */
    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(PreviewWidget.REFRESH_BUTTON_CLICKED);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    /**
     * 刷新小组件
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // TODO 清空旧内容
        RemoteViews containerViews = new RemoteViews(context.getPackageName(), R.layout.widget_preview);

        for (Schedule course : getTodayCourses()) {
            RemoteViews elementViews = new RemoteViews(context.getPackageName(), R.layout.widget_preview_element);

            // 课程名
            elementViews.setTextViewText(R.id.course_textview, course.getName());

            // 课程时间
            String courseTimeSpan = courseTimes[course.getStart() - 1] + "-" + courseTimes[course.getStart() + course.getStep() - 2];
            elementViews.setTextViewText(R.id.course_time_textview, courseTimeSpan);

            containerViews.addView(R.id.widget_container, elementViews);
        }

        appWidgetManager.updateAppWidget(appWidgetId, containerViews);
    }

    /**
     * 获取今天的课程
     */
    private static List<Schedule> getTodayCourses() {
        MainActivity mainActivity = MainActivity.getInstance();
        List<Schedule> allCourses = mainActivity.getmWeekView().dataSource();
        List<Schedule> todayCourses = new ArrayList<>();
        int week = mainActivity.getcWeek();
        int dayOfWeek = getDayOfWeek();

        for (Schedule course : allCourses) {
            if (course.getWeekList().contains(week) && course.getDay() == dayOfWeek) {
                todayCourses.add(course);
            }
        }

        return todayCourses;
    }

    /**
     * 获取今天是一周的第几天（星期一是第一天）
     */
    private static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return 7;
        } else {
            return dayOfWeek - 1;
        }
    }
}