package com.zendaimoney.coreaccount.timer;


import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.zendaimoney.coreaccount.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.service.FlowService;
import org.springframework.beans.factory.annotation.Value;

/**
 * Author: kimi Date: 14-5-15 Time: 下午3:00
 */
@Named
public class SycnFlows2DB {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${flowFile.path}")
    private String flowFilePath;
    @Inject
    private FlowService flowService;

    private String getFlowFilePath(){
        String path=flowFilePath.lastIndexOf('/') == -1 ? flowFilePath : flowFilePath.substring(0,flowFilePath.length());
        return path + Constants.FLOW_FILE_NAME;
    }

    @PostConstruct
    public void sync() {
        File dataFile;
        boolean eof = false;
        ObjectInputStream ois = null;
        if ((dataFile = new File(getFlowFilePath())).exists()) {
            ArrayList<Flow> flows;
            try {
                ois = new ObjectInputStream(
                        new BufferedInputStream(new FileInputStream(dataFile)));
                for (; ; ) {
                    flows = (ArrayList<Flow>) ois.readObject();
                    flowService.insertFlows(flows);
                }
            } catch (EOFException e) {
                eof = true;
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } finally {
                if (ois != null)
                    try {
                        ois.close();
                    } catch (IOException ignore) {
                    } finally {
                        if (eof)
                            dataFile.delete();
                    }
            }
        }
    }

}
