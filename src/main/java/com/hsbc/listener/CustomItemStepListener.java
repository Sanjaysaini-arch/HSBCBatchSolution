package com.hsbc.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class CustomItemStepListener implements StepExecutionListener {
	 private static final Logger logger = LoggerFactory.getLogger("CustomItemStepListener");
		
    @Override
    public ExitStatus afterStep(StepExecution arg0) {
    	
        
        logger.info("ReadCount:"+arg0.getReadCount());
        logger.info("WriteCount:"+arg0.getWriteCount());
        logger.info("SkipCount:"+arg0.getSkipCount());
        logger.info("ProcessSkipCount:"+arg0.getProcessSkipCount());
        int commitCount=arg0.getCommitCount();

        arg0.getStartTime();
        arg0.getEndTime();
        return arg0.getExitStatus();

    }

    @Override
    public void beforeStep(StepExecution arg0) {

    }
}
