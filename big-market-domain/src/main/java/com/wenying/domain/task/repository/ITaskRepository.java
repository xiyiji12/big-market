package com.wenying.domain.task.repository;

import com.wenying.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * 任务仓储接口
 */
public interface ITaskRepository {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
