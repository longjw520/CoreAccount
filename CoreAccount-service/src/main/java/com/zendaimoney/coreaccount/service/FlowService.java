package com.zendaimoney.coreaccount.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import com.zendaimoney.coreaccount.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.dao.FlowDao;
import com.zendaimoney.coreaccount.entity.Flow;
import com.zendaimoney.coreaccount.util.CollectionUtil;

/**
 * Author: kimi
 * Date: 14-5-14
 * Time: 下午5:09
 */
@Named
public class FlowService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${flowFile.path}")
    private String flowFilePath;
    @Inject
    private FlowDao flowDao;


    private String getFlowFilePath(){
        String path = flowFilePath.lastIndexOf('/') == -1 ? flowFilePath : flowFilePath.substring(0,flowFilePath.length());
        return path + Constants.FLOW_FILE_NAME;
    }

    @Transactional
    public void insertFlows(ArrayList<Flow> flows) {
        try {
            flowDao.batchSave(flows);
        } catch (Exception ex) {
            File dataFile;
            ObjectOutputStream oos = null;
            ObjectInputStream ois = null;
            try {
                dataFile = new File(getFlowFilePath());
                if (dataFile.exists()) {
                    ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(dataFile)));
                    flows = CollectionUtil.addAll((ArrayList<Flow>) ois.readObject(), flows);
                    dataFile.delete();
                }
                oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)));
                oos.writeObject(flows);
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (oos != null) {
                        oos.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                }
            }

        }
    }

}
