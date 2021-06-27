package com.insistingon.binlogportal.binlogportalspringbootstartertest.job;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.insistingon.binlogportal.BinlogPortalException;
import com.insistingon.binlogportal.BinlogPortalStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static java.lang.System.exit;

@Slf4j
@Component
public class BinlogSync implements CommandLineRunner {
    @Resource
    BinlogPortalStarter binlogPortalStarter;

    public void run(String... args) throws Exception {
        new Thread(this::start).start();
        new Thread(this::check).start();
    }

    void start() {
        try {
            binlogPortalStarter.start();
        } catch (BinlogPortalException e) {
            log.error(e.getMessage(), e);
        }
    }

    void check() {
        while (true) {
            try {
                BinaryLogClient d1 = null;
                d1 = binlogPortalStarter.getClientByDbKey("d1");
                if (d1 != null && d1.isConnected()) {
                    System.out.println("connect");
                    d1.disconnect();
                    if (!d1.isConnected()) {
                        exit(1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }
}
