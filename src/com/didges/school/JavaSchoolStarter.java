package com.didges.school;

import java.util.*;

public class JavaSchoolStarter {
    private final RequestHandler handler = new RequestHandler();

    public JavaSchoolStarter() {}

    public List<Map<String,Object>> execute(String request) throws Exception {
        return  handler.processRequest(request);
    }
}
