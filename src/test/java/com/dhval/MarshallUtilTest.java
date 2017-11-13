package com.dhval;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class MarshallUtilTest {
    private static final Logger LOG = LoggerFactory.getLogger(MarshallUtilTest.class);

}
