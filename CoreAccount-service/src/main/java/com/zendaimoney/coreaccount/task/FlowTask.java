package com.zendaimoney.coreaccount.task;

import java.util.ArrayList;

import org.springside.modules.utils.SpringContextHolder;

import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.service.FlowService;

/**
 * Author: kimi
 * Date: 14-5-14
 * Time: 下午1:51
 */
public class FlowTask implements Runnable {
    private ArrayList<Flow> flowList;
    private static FlowService flowService;

    public FlowTask(ArrayList<Flow> flowList) {
        this.flowList = flowList;
        if (flowService == null) {
            flowService = SpringContextHolder.getBean(FlowService.class);
        }
    }

    @Override
    public void run() {
        flowService.insertFlows(flowList);
    }
}
