package com.wenying.domain.task.service;

import com.wenying.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * 消息任务服务接口
 */
public interface ITaskService {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);//扫描到了就发送这个消息

    void updateTaskSendMessageCompleted(String userId, String messageId);//发送成功更新
    void updateTaskSendMessageFail(String userId, String messageId);//发送失败更新

}
