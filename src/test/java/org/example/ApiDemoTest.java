package org.example;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class ApiDemoTest extends AndroidCustomDriver {

    @Test
    public void testAddition() {
        click();
        log.info("TEST PASSED !!!!");
    }


}
