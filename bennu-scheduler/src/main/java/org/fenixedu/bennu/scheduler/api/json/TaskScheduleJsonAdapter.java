package org.fenixedu.bennu.scheduler.api.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.domain.exceptions.InvalidJsonException;
import org.fenixedu.bennu.core.json.JsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.scheduler.domain.SchedulerSystem;
import org.fenixedu.bennu.scheduler.domain.TaskSchedule;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(TaskSchedule.class)
public class TaskScheduleJsonAdapter implements JsonAdapter<TaskSchedule> {

    private static final String TASK_TYPE = "type";
    private static final String TASK_SCHEDULE = "schedule";

    @Override
    public JsonElement view(TaskSchedule taskSchedule, JsonBuilder ctx) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", taskSchedule.getExternalId());
        obj.addProperty("type", taskSchedule.getTaskClassName());
        obj.addProperty("name", taskSchedule.getTask().getLocalizedName());
        obj.addProperty(TASK_SCHEDULE, taskSchedule.getSchedule());
        return obj;
    }

    @Override
    public TaskSchedule create(JsonElement el, JsonBuilder ctx) {
        final JsonObject jsonObject = el.getAsJsonObject();
        if (!jsonObject.has(TASK_TYPE) || !jsonObject.has(TASK_SCHEDULE)) {
            throw new InvalidJsonException(el, TASK_TYPE, TASK_SCHEDULE);
        }
        final String taskType = jsonObject.get(TASK_TYPE).getAsString();
        final String schedule = jsonObject.get(TASK_SCHEDULE).getAsString();
        return new TaskSchedule(taskType, schedule);
    }

    @Override
    public TaskSchedule update(JsonElement el, TaskSchedule taskSchedule, JsonBuilder ctx) {
        final JsonObject jsonObject = el.getAsJsonObject();
        String taskType = null;
        String schedule = null;
        if (jsonObject.has(TASK_TYPE)) {
            taskType = jsonObject.get(TASK_TYPE).getAsString();
        }

        if (jsonObject.has(TASK_SCHEDULE)) {
            schedule = jsonObject.get(TASK_SCHEDULE).getAsString();
        }
        updateTaskSchedule(taskSchedule, taskType, schedule);
        return taskSchedule;
    }

    private void updateTaskSchedule(TaskSchedule taskSchedule, final String taskType, final String schedule) {
        SchedulerSystem.unschedule(taskSchedule);
        if (schedule != null && !schedule.equals(taskSchedule.getSchedule())) {
            taskSchedule.setSchedule(schedule);
        }

        if (taskType != null && !taskType.equals(taskSchedule.getTaskClassName())) {
            taskSchedule.setTaskClassName(taskType);
        }
        SchedulerSystem.schedule(taskSchedule);
    }
}
