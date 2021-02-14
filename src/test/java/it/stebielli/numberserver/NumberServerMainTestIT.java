package it.stebielli.numberserver;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class NumberServerMainTestIT {

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("numbers.log"));
    }

    @Test
    void stressTestSingleSocket() throws IOException {
        var main = new NumberServerMain();
        main.launch();

        Socket socket = new Socket("localhost", 4000);
        try (var print = new PrintStream(new BufferedOutputStream(socket.getOutputStream()))) {

            long start = System.currentTimeMillis();

            int count = 0;
            while (count < 2_000_000) {
                print.printf("%09d%n", count);
                count++;
            }
            print.flush();
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;

            Assertions.assertThat(timeElapsed).isLessThan(10_000);
            System.out.println(timeElapsed);
            print.println("quit");
        }

        var size = Files.readAllLines(Path.of("numbers.log")).size();
        Assertions.assertThat(size).isEqualTo(2_000_000);
        main.shutdown();
    }

}