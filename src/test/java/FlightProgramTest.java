import com.smart4aviation.task.FlightProgram;
import com.smart4aviation.task.QueryParser;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FlightProgramTest {

    @Test
    public void testCaseA_HoldsHistory() throws IOException {
        String inputData =
                "1 2\n"
                + "100\n"
                + "P 1 150 10\n"
                + "Q 1 1 20\n";

        InputStream is = new ByteArrayInputStream(inputData.getBytes());

        QueryParser queryParser = new QueryParser(is);
        FlightProgram flightProgram = new FlightProgram(queryParser);

        flightProgram.initializeFlights();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        flightProgram.solve();

        String actualOutput = out.toString().trim();
        assertEquals("2500", actualOutput);

    }

    @Test
    public void testInvalidParameters_ThrowsException() {
        String inputData = "0 -1\n";
        InputStream is = new ByteArrayInputStream(inputData.getBytes());
        QueryParser queryParser = new QueryParser(is);

        assertThrows(IllegalArgumentException.class, () -> {
            new FlightProgram(queryParser);
        });
    }

    @Test
    public void testNonChronologicalTime_PrintsError() throws IOException {
        String inputData = "1 2\n"
                + "100\n"
                + "P 1 200 10\n"
                + "Q 1 1 5\n";

        InputStream is = new ByteArrayInputStream(inputData.getBytes());
        QueryParser queryParser = new QueryParser(is);
        FlightProgram flightProgram = new FlightProgram(queryParser);

        flightProgram.initializeFlights();

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));

        flightProgram.solve();

        String actualError = err.toString().trim();
        assertEquals("Błąd: Podany czas nie jest ustawiony chronologicznie!", actualError);
    }

    @Test
    public void testNumberFormatError_PrintsError() throws IOException {
        String inputData = "1 1\n"
                + "100\n"
                + "Q 1 ABC 10\n";
        InputStream is = new ByteArrayInputStream(inputData.getBytes());
        QueryParser queryParser = new QueryParser(is);
        FlightProgram flightProgram = new FlightProgram(queryParser);

        flightProgram.initializeFlights();

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));

        flightProgram.solve();

        String actualError = err.toString().trim();
        assertEquals("Błąd formatu w linii, oczekiwano liczby.", actualError);
    }

    @Test
    public void testIncompleteCommand_PrintsError() throws IOException {
        String inputData = "1 1\n"
                + "100\n"
                + "P 1 200";
        InputStream is = new ByteArrayInputStream(inputData.getBytes());
        QueryParser queryParser = new QueryParser(is);
        FlightProgram flightProgram = new FlightProgram(queryParser);

        flightProgram.initializeFlights();

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));

        flightProgram.solve();

        String actualError = err.toString().trim();
        assertEquals("Niekompletna komenda.", actualError);
    }
}