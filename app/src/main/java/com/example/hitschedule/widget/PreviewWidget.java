package com.example.hitschedule.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.example.hitschedule.R;
import com.example.hitschedule.ui.MainActivity;
import com.example.hitschedule.view.WeekView;
import com.zhuangfei.timetable.model.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 桌面小组件，快速显示本日课程
 */
public class PreviewWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * 刷新小组件
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews containerViews = new RemoteViews(context.getPackageName(), R.layout.widget_preview);

        for (Schedule course : getTodayCourses()) {
            RemoteViews elementViews = new RemoteViews(context.getPackageName(), R.layout.widget_preview_element);
            elementViews.setTextViewText(R.id.appwidget_textview, course.getName());
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