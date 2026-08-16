package com.shubhamthorat.androidtechnicaldeepdive

/**
 * ANDROID TESTING MASTERY (0 to 100) - INTERVIEW EDITION
 *
 * Testing ensures your code works as expected and prevents regressions.
 * KEY CONCEPT: The Testing Pyramid (Unit Tests > Integration Tests > UI Tests).
 */

// =========================================================================================
// PART 1: UNIT TESTING (JUnit 4 & MockK)
// Unit tests are fast and test small pieces of logic in isolation.
// =========================================================================================

class Calculator {
    fun add(a: Int, b: Int): Int = a + b
}

/**
 * Example Unit Test for a simple class.
 * Interview Tip: Use descriptive test names like 'should_return_sum_when_adding_two_numbers'.
 */
/*
class CalculatorTest {
    private val calculator = Calculator()

    @Test
    fun `add should return correct sum`() {
        val result = calculator.add(2, 3)
        assertEquals(5, result) // Asserting the expected value
    }
}
*/

// =========================================================================================
// PART 2: MOCKING WITH MOCKK
// Mocking allows you to isolate the class under test by replacing dependencies.
// =========================================================================================

interface UserRepositoryInterface {
    suspend fun getUserName(id: Int): String
}

class UserViewModelTest(private val repository: UserRepositoryInterface) {
    suspend fun getGreeting(id: Int): String {
        val name = repository.getUserName(id)
        return "Hello, $name"
    }
}

/**
 * MockK Syntax:
 * - mockk<T>(): Creates a mock instance.
 * - every { ... } returns ...: Stubs a method call.
 * - coEvery { ... } returns ...: Stubs a suspend method call.
 * - verify { ... }: Ensures a method was called.
 */
/*
class UserViewModelUnitTest {
    private val repository = mockk<UserRepositoryInterface>()
    private val viewModel = UserViewModelTest(repository)

    @Test
    fun `getGreeting should return correct message`() = runTest {
        // 1. Stubbing (Arrange)
        coEvery { repository.getUserName(1) } returns "Shubham"

        // 2. Action (Act)
        val result = viewModel.getGreeting(1)

        // 3. Verification (Assert)
        assertEquals("Hello, Shubham", result)
        coVerify { repository.getUserName(1) } // Verify the interaction
    }
}
*/

// =========================================================================================
// PART 3: COROUTINE TESTING (runTest & TestDispatcher)
// Coroutines need special handling because they are asynchronous.
// =========================================================================================

/**
 * runTest: A coroutine builder for testing that skips delays.
 * StandardTestDispatcher: Gives you control over time and execution.
 * MainDispatcherRule: A common custom JUnit Rule to swap the Main dispatcher for tests.
 */
/*
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTest {
    @Test
    fun `test with delay`() = runTest {
        val startTime = currentTime
        delay(1000) // This delay is skipped in runTest!
        val endTime = currentTime
        assertEquals(1000, endTime - startTime)
    }
}
*/

// =========================================================================================
// PART 4: ROOM DATABASE TESTING (Integration Testing)
// Use an In-Memory database so tests don't affect actual app data.
// =========================================================================================

/*
@RunWith(AndroidJUnit4::class)
class RoomTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 1. Create In-Memory DB (Wiped when process ends)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `insert and read user`() = runTest {
        val user = User(name = "Test User", age = 25)
        dao.insertUser(user)
        val allUsers = dao.getAllUsers().first()
        assertEquals(user.name, allUsers[0].name)
    }
}
*/

// =========================================================================================
// PART 5: UI TESTING (Espresso & Compose)
// UI tests interact with the app like a real user.
// =========================================================================================

/**
 * Espresso (XML):
 * - onView(withText("Submit")).perform(click())
 * - onView(withId(R.id.name_field)).check(matches(withText("John")))
 *
 * Compose Test:
 * - composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
 * - composeTestRule.onNodeWithTag("login_button").performClick()
 */
/*
class MyUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `clicking button updates text`() {
        composeTestRule.setContent {
            MyScreen() // Your Composable
        }

        composeTestRule.onNodeWithText("Click Me").performClick()
        composeTestRule.onNodeWithText("Button Clicked!").assertIsDisplayed()
    }
}
*/

// =========================================================================================
// INTERVIEW TIPS:
// 1. The 70-20-10 Rule: 70% Unit tests, 20% Integration tests, 10% UI tests.
// 2. Why Mocking? To test a component in isolation without its real (and often complex) dependencies.
// 3. Fakes vs Mocks: Fakes have real (simplified) logic (e.g., In-Memory DB). Mocks are pre-programmed objects.
// 4. Test-Driven Development (TDD): Red (Fail) -> Green (Pass) -> Refactor.
// 5. Flakiness: UI tests are often "flaky" (fail randomly). Use IdlingResources or skip delays to fix.
// =========================================================================================
