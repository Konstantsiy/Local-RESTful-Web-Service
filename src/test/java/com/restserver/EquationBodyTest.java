package com.restserver;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import static org.junit.jupiter.api.Assertions.*;

class EquationBodyTest {

    @BeforeTestClass
    public static void beforeTestClass() {
        System.out.println("Before EquationBodyTest.class");
    }

    @AfterTestClass
    public static void afterTestClass() {
        System.out.println("After EquationBodyTest.class");
    }

    @Test
    public void verification() {
        EquationBody equationBody = new EquationBody(2, 12, -10, 30);
        assertEquals(true, equationBody.verification());
        equationBody.setAllParams(30, 10, 0, 40);
        assertEquals(false, equationBody.verification());
    }
}