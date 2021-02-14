package it.stebielli.numberserver;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class MockitoTest {
    protected static final int TIMEOUT = 100;

    @BeforeEach
    protected void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    protected void verifyNoAsyncInteraction(Object... mocks) throws InterruptedException {
        // the interaction is async
        Thread.sleep(TIMEOUT);
        verifyNoInteractions(mocks);
    }

    protected void verifyNoMoreAsyncInteraction(Object... mocks) throws InterruptedException {
        // the interaction is async
        Thread.sleep(TIMEOUT);
        verifyNoMoreInteractions(mocks);
    }
}
