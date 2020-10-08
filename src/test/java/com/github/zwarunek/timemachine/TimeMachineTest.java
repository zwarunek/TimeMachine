package com.github.zwarunek.timemachine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeMachineTest {

    @Test
    void onEnable() {
        TimeMachine timeMachine = new TimeMachine();
        timeMachine.onEnable();
    }

    @Test
    void onDisable() {
        System.out.println("onDisable test ran");
    }

    @Test
    void restartServer() {
        System.out.println("restartServer test ran");
    }

    @Test
    void quickSort() {
        System.out.println("quickSort test ran");
    }
}
