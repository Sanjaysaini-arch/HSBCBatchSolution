package com.hsbc.listener;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;




 
/**
 * The Class FileReaderVarificationCustomSkiper.
 *
 * @author sanjay saini
 */
public class FileReaderVarificationCustomSkiper implements SkipPolicy {
     
    private static final Logger logger = LoggerFactory.getLogger("badRecordLogger");
    
  
    
 
    @Override
    public boolean shouldSkip(Throwable exception, int skipCount) throws SkipLimitExceededException {
        if (exception instanceof FileNotFoundException) {
            return false;
        }else if (exception instanceof FlatFileParseException && skipCount <= 5) {
        
        	
            FlatFileParseException ffpe = (FlatFileParseException) exception;
            StringBuilder errorMessage = new StringBuilder();
            //reportDto.setRecordId(ffpe.getLineNumber());
            //reportDto.setRecordProcessStratTime(LocalDateTime.now());
            errorMessage.append("ReadSkip: An error at " + ffpe.getLineNumber()
                    + " line of the file. Faulty record " + "input.\n");
            errorMessage.append(ffpe.getInput() + "\n");
            logger.info("{}", errorMessage.toString());
            
            //reportDto.setRecordProcessEndTime(LocalDateTime.now());
            //reportDto.setRecordProcessDuration(Duration.between(reportDto.getRecordProcessStratTime(), reportDto.getRecordProcessEndTime()).toMillis());
            //reportDtoRepository.save(reportDto);
            return true;
        }else if (exception instanceof org.springframework.batch.item.validator.ValidationException) {
            return true;
    } else {
            return false;
        }
    }
 
}
