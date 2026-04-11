package com.smart4aviation.task;

import java.io.IOException;

public class Main {
    static void main(String[] args) throws IOException {
        QueryParser parser = new QueryParser(System.in);
        FlightProgram program = new FlightProgram(parser);

        program.initializeFlights();
        program.solve();
    }
}
