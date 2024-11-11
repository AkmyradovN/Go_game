


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import gogame.BoardSizeTest;
import gogame.GoStateTest;

@SelectClasses({
	BoardSizeTest.class,
    GoStateTest.class,
})
@Suite public class GoGameTestSuite {}
