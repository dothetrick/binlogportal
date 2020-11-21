package com.insistingon.binlogportal.binlogportalspringbootstartertest.job;

import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.BinlogPortalStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class BinlogSync implements CommandLineRunner {
    @Resource
    BinlogPortalStarter binlogPortalStarter;

    public void run(String... args) throws Exception {
        try {
            binlogPortalStarter.start();
        } catch (BinlogPortalException e) {
            log.error(e.getMessage(), e);
        }
    }
}
